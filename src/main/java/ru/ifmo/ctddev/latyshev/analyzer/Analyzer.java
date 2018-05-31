package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;

import java.io.IOException;
import java.nio.file.Path;

public class Analyzer {
    protected SmellResult smellResult = new SmellResult();
    protected SmellPrinter smellPrinter;

    public Analyzer(SmellPrinter smellPrinter) {
        this.smellPrinter = smellPrinter;
    }

    void analyzeFile(Path file) {
        smellResult.setFile(file);
        try {
            var cu = JavaParser.parse(file);
            analyzeCompilationUnit(cu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void analyzeCompilationUnit(CompilationUnit cu) {
        cu.getTypes().stream()
                .filter(x -> x instanceof ClassOrInterfaceDeclaration)
                .map(x -> (ClassOrInterfaceDeclaration) x)
                .forEach(coid -> {
                    smellResult.setCoid(coid);
                    analyzeClassOrInterfaceDeclaration(coid);
                });
    }

    void analyzeClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration coid) {
        coid.getMethods().forEach(md -> {
            smellResult.setMd(md);
            analyzeMethod(md);
        });
    }

    void analyzeMethod(MethodDeclaration md) {
        md.getBody().ifPresent(this::analyzeBlockStmt);
    }

    void analyzeBlockStmt(BlockStmt bs) {
        bs.stream().forEach(node -> {
            smellResult.setNode(node);
            analyzeNode(node);
        });
    }

    void analyzeNode(Node node) {
    }
}
