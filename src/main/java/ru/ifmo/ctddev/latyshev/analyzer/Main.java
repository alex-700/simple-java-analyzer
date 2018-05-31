package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Optional;

public class Main {

    static int cnt = 0;
    public static void analyze(Node node) {
        if (node instanceof IfStmt) {
            if (((IfStmt) node).hasElseBranch()) {
                if (((IfStmt) node).getThenStmt().equals(((IfStmt) node).getElseStmt().get())) {
                    System.out.println(
                            node.getRange()
                    );
                    System.out.println(node);
                }
            }
        }
        node.getChildNodes().forEach(Main::analyze);
    }

    public static void analyze(BlockStmt blockStmt) {
        blockStmt.stream().forEach(Main::analyze);
    }

    public static void main(String[] args) throws IOException {
        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\guava");
//        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\elasticsearch");
//        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\analyzer\\src\\test\\java");
//        var src = Paths.get("C:\\programming\\study\\10term\\verifiers\\spring-framework");
        Files.walkFileTree(src, new SimpleFileVisitor<>(){
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    var cu = JavaParser.parse(file);
                    cu.getTypes().stream()
                            .filter(x -> x instanceof ClassOrInterfaceDeclaration)
                            .map(NodeWithMembers::getMethods)
                            .flatMap(Collection::stream)
                            .map(MethodDeclaration::getBody)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(Main::analyze);

                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println(cnt);
    }

}