package edu.ktu.glang.visitors;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;

import java.io.PrintStream;

public class GLangVisitor extends GLangBaseVisitor<Object> {

    private final PrintStream out;

    public GLangVisitor(PrintStream out) {
        this.out = out;
    }

    @Override
    public Object visitProgram(GLangParser.ProgramContext ctx) {
        // visit every statement in order
        for (GLangParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

    @Override
    public Object visitStatement(GLangParser.StatementContext ctx) {
        // just delegate to child
        return visitChildren(ctx);
    }

    @Override
    public Object visitPrintStmt(GLangParser.PrintStmtContext ctx) {
        Object value = visit(ctx.expr());
        out.println(value);
        return null;
    }

    @Override
    public Object visitExpr(GLangParser.ExprContext ctx) {
        int left = Integer.parseInt(ctx.INT(0).getText());
        if (ctx.addOp() == null) {
            return left; // NOTE - just a number
        }
        int right = Integer.parseInt(ctx.INT(1).getText());
        return switch (ctx.addOp().getText()) {
            case "+" -> left + right;
            case "-" -> left - right;
            default -> throw new IllegalStateException("Unknown operator: " + ctx.addOp().getText());
        };
    }
}
