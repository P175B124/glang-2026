package edu.ktu.glang.interpreter;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;

/**
 * Handles only if/else semantics and delegates expression evaluation
 * and statement execution back to the root interpreter.
 */
public class IfVisitor extends GLangBaseVisitor<Object> {
    private final InterpreterVisitor root; // we reuse root's symbol table, output, and helpers

    public IfVisitor(InterpreterVisitor root) {
        this.root = root;
    }

    @Override
    public Object visitIfStmt(GLangParser.IfStmtContext ctx) {
        boolean condition = (Boolean) root.visit(ctx.expr()); // NOTE - TypeChecker checked condition is bool
        if (condition) {
            root.visit(ctx.block(0));                         // NOTE - then block
        } else if (ctx.ELSE() != null) {
            root.visit(ctx.block(1));                         // NOTE - else block
        }
        return null;
    }
}
