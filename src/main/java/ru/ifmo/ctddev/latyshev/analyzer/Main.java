package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        ArgumentParser parser = ArgumentParsers.newFor("simple-java-analyzer").build()
                .defaultHelp(true)
                .description("Do static analyzes of given java project");
        parser.addArgument("folder")
                .required(true)
                .help("Project folder to analyze.");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        final Path src = Paths.get(ns.getString("folder"));
        final TypeSolver typeSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(src)
        );

        Map<String, Path> dep2path = new HashMap<>();
        Files.walkFileTree(src, new SimpleFileVisitor<>(){
            boolean inTarget = false;
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.endsWith("target")) {
                    inTarget = true;
                }
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (inTarget) {
                    String dep = file.getName(file.getNameCount() - 1).toString();
                    dep2path.put(dep, file);
                }
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (dir.endsWith("target")) {
                    inTarget = false;
                }
                return super.postVisitDirectory(dir, exc);
            }
        });
        for (var path : dep2path.values()) {
            ((CombinedTypeSolver) typeSolver).add(new JarTypeSolver(path));
        }

        final JavaParserFacade parserFacade = JavaParserFacade.get(typeSolver);

        SmellPrinter smellPrinter = new SimpleSmellPrinter();
        final List<Analyzer> analyzers = List.of(
            new ImportAnalyzer(smellPrinter),
            new EquivalentIfBranchesAnalyzer(smellPrinter),
            new SynchronizationAnalyzer(smellPrinter, parserFacade),
            new UnusedVariableAnalyzer(smellPrinter, parserFacade)
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