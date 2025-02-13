package com.example.afferentcoupling.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipExtractor {

    public static Set<String> extractJavaFiles(MultipartFile file) throws IOException {
        Set<String> javaFiles = new HashSet<>();
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".java")) {
                    javaFiles.add(formatClassName(entry.getName()));
                }
            }
        }
        return javaFiles;
    }

    public static String readJavaFile(MultipartFile file, String targetClass) throws IOException {
        StringBuilder content = new StringBuilder();
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String className = formatClassName(entry.getName());
                if (className.equals(targetClass)) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(zis))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                    }
                    break;
                }
            }
        }
        return content.toString();
    }

    private static String formatClassName(String filePath) {
        filePath = filePath.replace("/", ".").replace(".java", "");
        int index = filePath.indexOf("com.example");
        return (index != -1) ? filePath.substring(index) : filePath;
    }
}
