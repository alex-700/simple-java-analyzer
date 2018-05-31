package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.IfStmt;

public class EquivalentIfBranchesAnalyzer extends Analyzer {

    public EquivalentIfBranchesAnalyzer(SmellPrinter smellPrinter) {
        super(smellPrinter);
    }

    @Override
    void analyzeNode(Node node) {
        if (node instanceof IfStmt) {
            if (((IfStmt) node).hasElseBranch()) {
                if (((IfStmt) node).getThenStmt().equals(((IfStmt) node).getElseStmt().get())) {
                    smellPrinter.print(smellResult);
                }
            }
        }
        super.analyzeNode(node);
    }
}
