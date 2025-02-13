package com.example.afferentcoupling.service;

import com.example.afferentcoupling.util.ZipExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class AfferentCouplingService {

    public Map<String, Integer> processZipFile(MultipartFile file) {
        try {
            Set<String> javaFiles = ZipExtractor.extractJavaFiles(file);
            Map<String, Set<String>> dependencyGraph = buildDependencyGraph(file, javaFiles);

            // Calculating afferent coupling using dependency graph
            Map<String, Integer> afferentCoupling = new HashMap<>();
            for (String className : javaFiles) {
                afferentCoupling.put(className, dependencyGraph.getOrDefault(className, new HashSet<>()).size());
            }

            return afferentCoupling;
        } catch (IOException e) {
            throw new RuntimeException("Error processing file: " + e.getMessage());
        }
    }


    private Map<String, Set<String>> buildDependencyGraph(MultipartFile file, Set<String> javaFiles) throws IOException {
        Map<String, Set<String>> dependencyGraph = new HashMap<>();

        // Initialising every class with empty set of dependencies
        for (String className : javaFiles) {
            dependencyGraph.put(className, new HashSet<>());
        }

        for (String className : javaFiles) {
            String fileContent = ZipExtractor.readJavaFile(file, className);
            Set<String> dependencies = extractDependencies(fileContent, javaFiles);

            //Addition of dependency
            for (String dependency : dependencies) {
                dependencyGraph.computeIfAbsent(dependency, k -> new HashSet<>()).add(className);
            }
        }
        return dependencyGraph;
    }


    private Set<String> extractDependencies(String fileContent, Set<String> javaFiles) {
        Set<String> dependencies = new HashSet<>();
        try (Scanner scanner = new Scanner(fileContent)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (line.startsWith("import ")) {
                    String importedClass = line.replace("import ", "").replace(";", "").trim();
                    for (String className : javaFiles) {
                        if (importedClass.endsWith(className)) {
                            dependencies.add(className);
                        }
                    }
                }

                for (String className : javaFiles) {
                    if (line.contains(" " + className.substring(className.lastIndexOf(".") + 1) + " ")) {
                        dependencies.add(className);
                    }
                }
            }
        }
        return dependencies;
    }
}
