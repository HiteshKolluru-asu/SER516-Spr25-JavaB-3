package com.example.afferentcoupling.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtractor {
    public static List<String> extractJavaFiles(MultipartFile file) throws IOException {
        List<String> javaFiles = new ArrayList<>();

        // Convert MultipartFile to a temporary file
        File tempFile = File.createTempFile("upload", ".zip");
        file.transferTo(tempFile);  // Save MultipartFile to disk

        try (ZipFile zipFile = new ZipFile(tempFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println("File: " + entry.getName() + ", Compression Method: " + entry.getMethod());

                if (entry.getName().endsWith(".java")) {
                    InputStream is = zipFile.getInputStream(entry);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                    javaFiles.add(fileContent.toString());
                }
            }
        } finally {
            // Delete temporary file after processing
            tempFile.delete();
        }

        return javaFiles;
    }
}