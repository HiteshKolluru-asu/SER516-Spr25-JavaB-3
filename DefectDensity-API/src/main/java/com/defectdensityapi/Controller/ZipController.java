package com.defectdensityapi.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@RestController
@CrossOrigin(origins = "*") 
@RequestMapping("/api/code-analysis")
public class ZipController {

    private static final Logger logger = LoggerFactory.getLogger(ZipController.class);

    // Refined regex patterns for defect detection:

    // Matches empty catch blocks, e.g., catch (Exception e) { }
    private static final Pattern PATTERN_EMPTY_CATCH = Pattern.compile("catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}");
    
    // Matches method calls where null is passed as an argument, e.g., object.method(null)
    private static final Pattern PATTERN_NULL_METHOD_CALL = Pattern.compile("\\b\\w+\\s*\\.\\s*\\w+\\s*\\(\\s*null\\s*\\)");
    
    // Matches calls to System.out.println, often left in for debugging
    private static final Pattern PATTERN_PRINTLN = Pattern.compile("System\\.out\\.println");
    
    // Matches TODO or FIXME comments indicating unfinished code
    private static final Pattern PATTERN_TODO = Pattern.compile("\\b(?:TODO|FIXME)\\b");

    // Array of refined defect patterns.
    private static final Pattern[] DEFECT_PATTERNS = new Pattern[]{
            PATTERN_EMPTY_CATCH,
            PATTERN_NULL_METHOD_CALL,
            PATTERN_PRINTLN,
            PATTERN_TODO
    };

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeCode(@RequestParam("file") MultipartFile zipFile) {
        File tempFile = null;
        try {
            // Save the uploaded file to a temporary file.
            tempFile = File.createTempFile("uploaded", ".zip");
            zipFile.transferTo(tempFile);

            // Validate that the file is a proper ZIP.
            validateZipFile(tempFile);

            // Analyze the ZIP file for Java code.
            Map<String, Object> analysis = analyzeJavaCode(tempFile);
            return ResponseEntity.ok(analysis);
        } catch (IOException e) {
            logger.error("Error processing ZIP file: {}", e.getMessage(), e);
            return buildErrorResponse("Error processing ZIP file: " + e.getMessage());
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (!tempFile.delete()) {
                    logger.warn("Temporary file {} could not be deleted.", tempFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Validates the ZIP file by attempting to open it as a ZipFile.
     *
     * @param file The file to validate.
     * @throws IOException if the file is not a valid ZIP.
     */
    private void validateZipFile(File file) throws IOException {
        try (ZipFile zip = new ZipFile(file)) {
            // Successfully opened as ZIP; do nothing.
        } catch (ZipException e) {
            logger.error("Invalid ZIP file provided.", e);
            throw new IOException("Invalid ZIP file provided", e);
        }
    }

    /**
     * Processes the ZIP file and analyzes all Java files contained within using a line-by-line approach.
     *
     * @param zipFile The ZIP file to analyze.
     * @return A map containing analysis results.
     * @throws IOException if an I/O error occurs.
     */
    private Map<String, Object> analyzeJavaCode(File zipFile) throws IOException {
        Map<String, Object> results = new HashMap<>();
        int totalLines = 0;
        int totalDefects = 0;

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                logger.debug("Found entry: {}", entry.getName());
                if (entry.isDirectory() || shouldSkipEntry(entry.getName())) {
                    logger.info("Skipping directory or unwanted entry: {}", entry.getName());
                    zis.closeEntry();
                    continue;
                }
                if (entry.getName().endsWith(".java")) {
                    logger.info("Processing file: {}", entry.getName());
                    try {
                        Map<String, Integer> fileAnalysis = analyzeEntry(zis);
                        totalLines += fileAnalysis.get("lines");
                        totalDefects += fileAnalysis.get("defects");
                    } catch (Exception e) {
                        logger.error("Error processing entry {}: {}", entry.getName(), e.getMessage());
                    }
                }
                zis.closeEntry();
            }
        }

        double defectDensity = totalLines > 0 ? (double) totalDefects * 1000 / totalLines : 0;
        results.put("totalLinesOfCode", totalLines);
        results.put("totalDefects", totalDefects);
        results.put("defectDensity", String.format("%.2f", defectDensity));

        logger.info("Analysis complete: {} lines, {} defects, density: {}",
                totalLines, totalDefects, results.get("defectDensity"));
        return results;
    }

    /**
     * Determines whether a ZIP entry should be skipped.
     * Adjust filters as needed to ignore test, docs, build, or target directories.
     *
     * @param entryName The name/path of the ZIP entry.
     * @return true if the entry should be skipped.
     */
    private boolean shouldSkipEntry(String entryName) {
        String lower = entryName.toLowerCase();
        return lower.contains("/test/") || lower.contains("/docs/") ||
               lower.contains("/build/") || lower.contains("/target/");
    }

    /**
     * Analyzes a single Java file entry using a line-by-line approach.
     * This method uses a BufferedReader on the current entry's stream.
     *
     * @param zis The ZipInputStream positioned at the current entry.
     * @return A map with keys "lines" and "defects".
     * @throws IOException if an I/O error occurs.
     */
    private Map<String, Integer> analyzeEntry(ZipInputStream zis) throws IOException {
        int lines = 0;
        int defects = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
        String line;
        while ((line = reader.readLine()) != null) {
            if (isValidCodeLine(line)) {
                lines++;
            }
            defects += countDefectsInLine(line);
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("lines", lines);
        result.put("defects", defects);
        return result;
    }

    /**
     * Checks if a line of code is valid (non-empty and not a comment).
     *
     * @param line The line to check.
     * @return true if it is a valid code line.
     */
    private boolean isValidCodeLine(String line) {
        String trimmed = line.trim();
        return !trimmed.isEmpty() &&
               !trimmed.startsWith("//") &&
               !trimmed.startsWith("/*") &&
               !trimmed.startsWith("*");
    }

    /**
     * Applies all refined defect regex patterns to a single line of code.
     *
     * @param line The code line.
     * @return The number of defect occurrences in the line.
     */
    private int countDefectsInLine(String line) {
        int defects = 0;
        for (Pattern pattern : DEFECT_PATTERNS) {
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                defects++;
            }
        }
        return defects;
    }

    /**
     * Builds an error response to return in case of failure.
     *
     * @param message The error message.
     * @return A ResponseEntity with a bad request status and error details.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.badRequest().body(error);
    }
}
