package ru.ifmo.ctddev.latyshev.analyzer;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration;

public class SynchronizationAnalyzer extends Analyzer {
  private JavaParserFacade parserFacade;
  
  public SynchronizationAnalyzer(SmellPrinter smellPrinter, JavaParserFacade parserFacade) {
      super(smellPrinter);
      this.parserFacade = parserFacade;
  }

  @Override
  void analyzeNode(Node node) {
    if (node instanceof SynchronizedStmt) {
        var stmt = (SynchronizedStmt) node;
        var exp = stmt.getExpression();
        var symbolReference = parserFacade.solve(stmt.getExpression());
        if (symbolReference.isSolved()) {
            var valueDeclaration = symbolReference.getCorrespondingDeclaration();
            if (valueDeclaration.isField()) {
                var field = valueDeclaration.asField();
                if (field instanceof JavaParserFieldDeclaration) {
                    var declaration = (JavaParserFieldDeclaration) field;
                    if (!declaration.getWrappedNode().isFinal()) {
                        smellPrinter.print(smellResult, "Synchronizing on a non-final variable " + valueDeclaration.getName());
                    }
                }
            } else {
                smellPrinter.print(smellResult, "Synchronizing on a local variable " + valueDeclaration.getName());
            }
        } else {
            System.err.println("Could not solve declaration in " + exp);
        }
    }
    super.analyzeNode(node);
  }
}
