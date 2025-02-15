package com.example.efferent_coupling_api.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class JavaParserUtil {

    public static Map<String, Integer> computeEfferentCoupling(File repoDir) {
        Map<String, Set<String>> couplingMap = new HashMap<>();
        scanJavaFiles(repoDir, couplingMap);

        Map<String, Integer> efferentCouplingMetrics = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : couplingMap.entrySet()) {
            efferentCouplingMetrics.put(entry.getKey(), entry.getValue().size());
        }

        return efferentCouplingMetrics;
    }

    private static void scanJavaFiles(File dir, Map<String, Set<String>> couplingMap) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                scanJavaFiles(file, couplingMap);
            } else if (file.getName().endsWith(".java")) {
                analyzeJavaFile(file, couplingMap);
            }
        }
    }

    private static void analyzeJavaFile(File javaFile, Map<String, Set<String>> couplingMap) {
        try {
            CompilationUnit cu = new JavaParser().parse(javaFile).getResult().orElse(null);
            if (cu == null) return;

            String packageName = cu.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("default");
            couplingMap.putIfAbsent(packageName, new HashSet<>());

            for (ImportDeclaration importDecl : cu.getImports()) {
                String importedPackage = importDecl.getName().toString();

                if (!importedPackage.startsWith("java.") && !importedPackage.startsWith("javax.")) {
                    couplingMap.get(packageName).add(importedPackage);
                }
            }

            cu.findAll(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class).forEach(cls -> {
                cls.getAnnotations().forEach(annotation -> {
                    String annotationName = annotation.getNameAsString();
                    if (!annotationName.startsWith("java.") && !annotationName.startsWith("javax.")) {
                        couplingMap.get(packageName).add(annotationName);
                    }
                });
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
