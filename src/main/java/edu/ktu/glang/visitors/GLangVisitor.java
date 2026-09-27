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
    public Object visitIntExpr(GLangParser.IntExprContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    @Override
    public Object visitAddExpr(GLangParser.AddExprContext ctx) {
        int left = (Integer) visit(ctx.expr(0));  // NOTE - we assume both operands are numbers
        int right = (Integer) visit(ctx.expr(1));
        return switch (ctx.addOp().getText()) {
            case "+" -> left + right;
            case "-" -> left - right;
            default -> throw new IllegalStateException("Unknown operator: " + ctx.addOp().getText());
        };
    }
}
