package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class UnusedVariableAnalyzer extends Analyzer {
    private JavaParserFacade parserFacade;
    private Set<Parameter> declarations = new HashSet<>();
  
    public UnusedVariableAnalyzer(SmellPrinter smellPrinter, JavaParserFacade parserFacade) {
        super(smellPrinter);
        this.parserFacade = parserFacade;
    }

    @Override
    void analyzeFile(Path file) {
        super.analyzeFile(file);
        for (var declaration : declarations) {
            smellResult.setNode(declaration);
            smellPrinter.print(smellResult, declaration.getNameAsString() + " is unused");
        }
    }

  @Override
    void analyzeMethod(MethodDeclaration md) {
        if (md.getBody().isPresent()
                && !md.getAnnotationByName("Override").isPresent()) {
            if (md.getBody().isPresent() && md.getBody().get().getStatements().stream().findFirst().isPresent()) {
                Node node = md.getBody().get().getStatements().stream().findFirst().get();
                if (node instanceof ThrowStmt) return;
            }
            declarations.addAll(md.getParameters());
            super.analyzeMethod(md);
            for (var declaration : declarations) {
                if (md.getParameters().contains(declaration)) {
                    var suppressWarningOpt = declaration.getAnnotationByName("SuppressWarnings");
                    if (suppressWarningOpt.isPresent()) {
                        var suppressWarning = suppressWarningOpt.get();
                        if (suppressWarning.toString().contains("unused")) {
                            continue;
                        }
                    }
                    smellResult.setNode(declaration);
                    smellPrinter.print(smellResult, declaration.getNameAsString() + " is unused");
                }
            }
            declarations.removeAll(md.getParameters());
        }
    }

    @Override
    void analyzeNode(Node node) {
        if (node instanceof NameExpr) {
            var nameExpr = (NameExpr) node;
            try {
                var nameResolved = parserFacade.solve(nameExpr);
                if (nameResolved.isSolved()) {
                    var declaration = nameResolved.getCorrespondingDeclaration();
                    if (declaration instanceof JavaParserParameterDeclaration) {
                        declarations.remove(((JavaParserParameterDeclaration) declaration).getWrappedNode());
                    }
                }
            } catch (Exception e) {
//                System.err.println(this.getClass().getSimpleName() + " : Could not solve declaration in " + nameExpr);
            }
        } else if (node instanceof ClassOrInterfaceDeclaration) {
            analyzeClassOrInterfaceDeclaration((ClassOrInterfaceDeclaration) node);
        }
    }
}
