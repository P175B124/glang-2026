package edu.ktu.glang.visitors;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;

/**
 * Handles only if/else semantics and delegates expression evaluation
 * and statement execution back to the root interpreter.
 */
public class IfVisitor extends GLangBaseVisitor<Object> {
    private final GLangVisitor root; // we reuse root's symbol table, output, and helpers

    public IfVisitor(GLangVisitor root) {
        this.root = root;
    }

    @Override
    public Object visitIfStmt(GLangParser.IfStmtContext ctx) {
        boolean condition = (Boolean) root.visit(ctx.expr()); // NOTE - we assume condition is boolean
        if (condition) {
            root.visit(ctx.statement(0));                     // NOTE - statement 0
        } else if (ctx.ELSE() != null) {
            root.visit(ctx.statement(1));                     // NOTE - statement 1
        }
        return null;
    }
}
