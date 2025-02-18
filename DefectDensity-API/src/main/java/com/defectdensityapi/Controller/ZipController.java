package com.defectdensityapi.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.*;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@RestController
@CrossOrigin(origins = "*") 
@RequestMapping("/api/code-analysis")
public class ZipController {

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeCode(
            @RequestParam("file") MultipartFile zipFile) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("uploaded", ".zip");
            zipFile.transferTo(tempFile);
            Map<String, Object> analysis = analyzeJavaCode(tempFile);
            return ResponseEntity.ok(analysis);
        } catch (IOException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private Map<String, Object> analyzeJavaCode(File zipFile) throws IOException {
        Map<String, Object> results = new HashMap<>();
        int totalLines = 0;
        int totalDefects = 0;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".java")) {
                    String content = readContent(zis);
                    Map<String, Integer> fileAnalysis = analyzeFile(content);
                    totalLines += fileAnalysis.get("lines");
                    totalDefects += fileAnalysis.get("defects");
                }
            }
        }

        double defectDensity = totalLines > 0 ? 
            (double) totalDefects * 1000 / totalLines : 0;

        results.put("totalLinesOfCode", totalLines);
        results.put("totalDefects", totalDefects);
        results.put("defectDensity", String.format("%.2f", defectDensity));
        
        return results;
    }

    private String readContent(ZipInputStream zis) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        return content.toString();
    }

    private Map<String, Integer> analyzeFile(String content) {
        Map<String, Integer> analysis = new HashMap<>();
        int lines = countLines(content);
        int defects = findPotentialDefects(content);

        analysis.put("lines", lines);
        analysis.put("defects", defects);
        return analysis;
    }

    private int countLines(String content) {
        String[] lines = content.split("\n");
        int count = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty() && 
                !line.trim().startsWith("//") && 
                !line.trim().startsWith("/*") && 
                !line.trim().startsWith("*")) {
                count++;
            }
        }
        return count;
    }

    private int findPotentialDefects(String content) {
        int defects = 0;

        Pattern[] defectPatterns = {
            Pattern.compile("\\b\\w+\\s*\\.\\s*\\w+\\s*\\(\\s*null\\s*\\)"),
            Pattern.compile("catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}"),
            Pattern.compile("\\b(?!-?[0-1]\\b)\\b-?\\d+\\b"),
            Pattern.compile("System\\.out\\.println"),
            Pattern.compile("\\([A-Za-z]+\\)\\s*\\w+"),
            Pattern.compile("\"[/\\\\][^\"]+\""),
            Pattern.compile("\\breturn\\b"),
            Pattern.compile("\\{([^{}]|\\{[^{}]*\\})*\\}")
        };

        for (Pattern pattern : defectPatterns) {
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                defects++;
            }
        }

        return defects;
    }
}
