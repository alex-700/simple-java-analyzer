package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Main {
    private static Path src = Paths.get("src/test/java");
    private static TypeSolver typeSolver = new CombinedTypeSolver(
        new ReflectionTypeSolver(),
        new JavaParserTypeSolver(src)
    );
    private static JavaParserFacade parserFacade = JavaParserFacade.get(typeSolver);

    public static void main(String[] args) throws IOException {
        SmellPrinter smellPrinter = new SimpleSmellPrinter();
        final List<Analyzer> analyzers = List.of(
            new ImportAnalyzer(smellPrinter),
            new EquivalentIfBranchesAnalyzer(smellPrinter),
            new SynchronizationAnalyzer(smellPrinter, parserFacade)
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