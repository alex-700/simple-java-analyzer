package ru.ifmo.ctddev.latyshev.analyzer;

public class SimpleSmellPrinter implements SmellPrinter {

    @Override
    public void print(SmellResult smellResult, String message) {
        if (smellResult.getFile() != null)
            System.out.println("File: " + smellResult.getFile());
        if (smellResult.getCoid() != null)
            System.out.println("Class: " + smellResult.getCoid().getNameAsString());
        if (smellResult.getMd() != null)
            System.out.println("Method: " + smellResult.getMd().getDeclarationAsString());
        if (smellResult.getNode() != null) {
            System.out.println("Range: " + smellResult.getNode().getRange().get());
            System.out.println("Code: " + smellResult.getNode());
        }
        System.out.println("Verdict: " + message);
    }
}
