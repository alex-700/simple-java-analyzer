package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

public class ImportAnalyzer extends Analyzer {

    public ImportAnalyzer(SmellPrinter smellPrinter) {
        super(smellPrinter);
    }

    @Override
    void analyzeCompilationUnit(CompilationUnit cu) {
        int count = cu.getImports().size();
        for (int i = 0; i < count; i++) {
            for (int j = i + 1; j < count; j++) {
                ImportDeclaration id1 = cu.getImport(i);
                ImportDeclaration id2 = cu.getImport(j);
                if (id1.equals(id2)) {
                    smellPrinter.print(smellResult,
                            String.format("Same import %s in %s and %s", id1.getNameAsString(), id1.getRange().get(), id2.getRange().get()));
                }
            }
        }
    }
}
