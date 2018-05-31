package ru.ifmo.ctddev.latyshev.analyzer;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\guava");
//        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\elasticsearch");
//        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\analyzer\\src\\test\\java");
//        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\spring-framework");
        SmellPrinter smellPrinter = new SimpleSmellPrinter();
        final List<Analyzer> analyzers = List.of(
                new EquivalentIfBranchesAnalyzer(smellPrinter)
        );
        Files.walkFileTree(src, new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    analyzers.forEach(analyzer -> analyzer.analyzeFile(file));
                }
                return super.visitFile(file, attrs);
            }
        });
    }

}