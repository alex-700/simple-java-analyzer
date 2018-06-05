package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
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
            smellPrinter.print(smellResult, declaration + " is unused");
        }
    }

  @Override
    void analyzeMethod(MethodDeclaration md) {
        if (md.getBody().isPresent() && !md.getAnnotations().contains(new MarkerAnnotationExpr("Override"))) {
            declarations.addAll(md.getParameters());
            super.analyzeMethod(md);
            for (var declaration : declarations) {
                if (md.getParameters().contains(declaration)) {
                    smellPrinter.print(smellResult, declaration + " is unused");
                }
            }
            declarations.removeAll(md.getParameters());
        } else {
          super.analyzeMethod(md);
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
            }
        } else if (node instanceof ClassOrInterfaceDeclaration) {
            analyzeClassOrInterfaceDeclaration((ClassOrInterfaceDeclaration) node);
        }
    }
}
