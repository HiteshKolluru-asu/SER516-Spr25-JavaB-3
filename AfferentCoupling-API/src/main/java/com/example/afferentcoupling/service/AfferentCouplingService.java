package com.example.afferentcoupling.service;

import com.example.afferentcoupling.util.ZipExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AfferentCouplingService {
    private static final String CLASS_PATTERN = "package\\s+([\\w\\.]+);.*?class\\s+(\\w+)";
    private static final String IMPORT_PATTERN = "import\\s+([\\w\\.]+);";

    public Map<String, Integer> processZipFile(MultipartFile file) {
        try {
            Map<String, Set<String>> classDependencies = new HashMap<>();
            Map<String, Set<String>> afferentCoupling = new HashMap<>();
            List<String> javaFiles = ZipExtractor.extractJavaFiles(file);
            Set<String> projectClasses = new HashSet<>();

            //Extract class names
            for (String fileContent : javaFiles) {
                String className = extractClassName(fileContent);
                if (className != null) {
                    projectClasses.add(className);
                }
            }

            // Extracting dependencies to compute afferent coupling
            for (String fileContent : javaFiles) {
                processFile(fileContent, classDependencies, projectClasses);
            }

            computeAfferentCoupling(classDependencies, afferentCoupling);

            //converting to json format
            Map<String, Integer> result = new HashMap<>();
            for (Map.Entry<String, Set<String>> entry : afferentCoupling.entrySet()) {
                result.put(entry.getKey(), entry.getValue().size());
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + e.getMessage());
        }
    }

    private void processFile(String fileContent, Map<String, Set<String>> classDependencies, Set<String> projectClasses) {
        String className = extractClassName(fileContent);
        if (className != null) {
            Set<String> dependencies = extractDependencies(fileContent, projectClasses);
            classDependencies.put(className, dependencies);
        }
    }

    private String extractClassName(String content) {
        Pattern pattern = Pattern.compile(CLASS_PATTERN, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        return matcher.find() ? matcher.group(1) + "." + matcher.group(2) : null;
    }

    private Set<String> extractDependencies(String content, Set<String> projectClasses) {
        Set<String> dependencies = new HashSet<>();
        Pattern pattern = Pattern.compile(IMPORT_PATTERN);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String importedClass = matcher.group(1);
            if (projectClasses.contains(importedClass)) {
                dependencies.add(importedClass);
            }
        }

        return dependencies;
    }

    private void computeAfferentCoupling(Map<String, Set<String>> classDependencies, Map<String, Set<String>> afferentCoupling) {
        for (String className : classDependencies.keySet()) {
            afferentCoupling.putIfAbsent(className, new HashSet<>());
        }
        for (Map.Entry<String, Set<String>> entry : classDependencies.entrySet()) {
            String dependentClass = entry.getKey();
            for (String dependency : entry.getValue()) {
                afferentCoupling.putIfAbsent(dependency, new HashSet<>());
                afferentCoupling.get(dependency).add(dependentClass);
            }
        }
    }
}
