package ru.ifmo.ctddev.latyshev.analyzer;

public class SimpleSmellPrinter implements SmellPrinter {

    @Override
    public void print(SmellResult smellResult) {
        System.out.println("File: " + smellResult.getFile());
        System.out.println("Class: " + smellResult.getCoid().getNameAsString());
        System.out.println("Method: " + smellResult.getMd().getDeclarationAsString());
        System.out.println("Range: " + smellResult.getNode().getRange());
        System.out.println("Code: " + smellResult.getNode());
    }
}
