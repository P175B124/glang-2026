package edu.ktu.glang.visitors;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class GLangVisitor extends GLangBaseVisitor<Object> {

    private final PrintStream out;
    private final Map<String, Object> symbolTable = new HashMap<>();

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
    public Object visitLetDecl(GLangParser.LetDeclContext ctx) {
        String name = ctx.ID().getText();
        Object value = visit(ctx.expr());
        symbolTable.put(name, value); // NOTE - we allow re-declaring for now
        return null;
    }

    @Override
    public Object visitAssignment(GLangParser.AssignmentContext ctx) {
        String name = ctx.ID().getText();
        Object value = visit(ctx.expr());
        symbolTable.put(name, value); // NOTE - we assume the variable was declared
        return null;
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
    public Object visitBoolExpr(GLangParser.BoolExprContext ctx) {
        return Boolean.parseBoolean(ctx.BOOL().getText());
    }

    @Override
    public Object visitIdExpr(GLangParser.IdExprContext ctx) {
        String name = ctx.ID().getText();
        return symbolTable.get(name); // NOTE - we assume the variable was declared
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

    @Override
    public Object visitRelationExpr(GLangParser.RelationExprContext ctx) {
        Object left = visit(ctx.expr(0));
        Object right = visit(ctx.expr(1));
        return switch (ctx.relationOp().getText()) {
            case "==" -> left.equals(right);
            case "!=" -> !left.equals(right);
            case "<" -> (Integer) left < (Integer) right;  // NOTE - we assume both operands are numbers
            case ">" -> (Integer) left > (Integer) right;
            default -> throw new IllegalStateException("Unknown operator: " + ctx.relationOp().getText());
        };
    }
}
