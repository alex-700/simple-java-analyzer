package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

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
        final JavaParserFacade parserFacade = JavaParserFacade.get(typeSolver);

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