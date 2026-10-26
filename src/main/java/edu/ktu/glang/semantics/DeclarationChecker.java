package edu.ktu.glang.semantics;

import edu.ktu.glang.syntax.GLangParser;
import org.antlr.v4.runtime.Token;

/**
 * Pass 1: every variable is declared before use, and only once.
 * Walks the whole tree, both if blocks too.
 */
public class DeclarationChecker extends Checker<Void> {

    private final SymbolTable symbolTable = new SymbolTable(); // NOTE - stack of scopes

    @Override
    public Void visitLetDecl(GLangParser.LetDeclContext ctx) {
        visit(ctx.expr()); // NOTE - first the value, so 'let x = x + 1;' is an error
        String name = ctx.ID().getText();
        if (!symbolTable.declare(name)) {
            error(ctx.ID().getSymbol(), "Variable '" + name + "' is already declared.");
        }
        return null;
    }

    @Override
    public Void visitAssignment(GLangParser.AssignmentContext ctx) {
        checkDeclared(ctx.ID().getSymbol());
        visit(ctx.expr());
        return null;
    }

    @Override
    public Void visitIdExpr(GLangParser.IdExprContext ctx) {
        checkDeclared(ctx.ID().getSymbol());
        return null;
    }

    @Override
    public Void visitBlock(GLangParser.BlockContext ctx) {
        symbolTable.enterScope();  // push: new scope on top
        visitChildren(ctx);
        symbolTable.exitScope();   // pop: variables of this block disappear
        return null;
    }

    private void checkDeclared(Token id) {
        if (!symbolTable.isDeclared(id.getText())) {
            error(id, "Variable '" + id.getText() + "' is not declared.");
        }
    }
}
