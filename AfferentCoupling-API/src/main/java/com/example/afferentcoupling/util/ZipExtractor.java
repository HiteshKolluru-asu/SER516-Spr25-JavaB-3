package com.example.afferentcoupling.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {
    public static List<String> extractJavaFiles(MultipartFile file) throws IOException {
        List<String> javaFiles = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".java")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                    javaFiles.add(fileContent.toString());
                }
            }
        }
        return javaFiles;
    }
}