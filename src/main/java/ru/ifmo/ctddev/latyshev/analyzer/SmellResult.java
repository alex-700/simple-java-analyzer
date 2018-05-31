package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.nio.file.Path;

public class SmellResult {
    private Path file;
    private ClassOrInterfaceDeclaration coid;
    private MethodDeclaration md;
    private Node node;

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public ClassOrInterfaceDeclaration getCoid() {
        return coid;
    }

    public void setCoid(ClassOrInterfaceDeclaration coid) {
        this.coid = coid;
    }

    public MethodDeclaration getMd() {
        return md;
    }

    public void setMd(MethodDeclaration md) {
        this.md = md;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
