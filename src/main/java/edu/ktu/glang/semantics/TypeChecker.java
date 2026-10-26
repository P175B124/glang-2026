package edu.ktu.glang.semantics;

import edu.ktu.glang.syntax.GLangParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Pass 2: operands and conditions have the right types.
 * <p>
 * Walks the tree like the interpreter or the compiler, but every expression returns its {@link Type}
 * instead of its value. Runs only after {@link DeclarationChecker} found no errors,
 * so every variable here is declared exactly once.
 */
public class TypeChecker extends Checker<Type> {

    // NOTE - one map is enough: DeclarationChecker checked scopes, so a name is used only where it is visible
    private final Map<String, Type> types = new HashMap<>(); // variable name -> type
    private final ParseTreeProperty<Type> exprTypes = new ParseTreeProperty<>(); // NOTE - type of every expr node

    /** Type of an expression found during the check - a backend may need it. */
    public Type typeOf(GLangParser.ExprContext expr) { return exprTypes.get(expr); }

    @Override
    public Type visit(ParseTree tree) {
        Type type = super.visit(tree);
        if (tree instanceof GLangParser.ExprContext expr) {
            exprTypes.put(expr, type); // NOTE - remember, so a backend does not compute it again
        }
        return type;
    }

    // ---- Statements ----

    @Override
    public Type visitLetDecl(GLangParser.LetDeclContext ctx) {
        types.put(ctx.ID().getText(), visit(ctx.expr())); // NOTE - type comes from the initial value
        return null;
    }

    @Override
    public Type visitAssignment(GLangParser.AssignmentContext ctx) {
        Type declared = types.get(ctx.ID().getText());
        Type value = visit(ctx.expr());
        if (declared != value) {
            error(ctx.getStart(), "Cannot assign " + value + " to variable '" + ctx.ID().getText()
                    + "' of type " + declared + ".");
        }
        return null;
    }

    @Override
    public Type visitPrintStmt(GLangParser.PrintStmtContext ctx) {
        visit(ctx.expr()); // NOTE - any type can be printed, but a backend may need to know which
        return null;
    }

    @Override
    public Type visitIfStmt(GLangParser.IfStmtContext ctx) {
        Type condition = visit(ctx.expr());
        if (condition != Type.BOOL) {
            error(ctx.expr().getStart(), "Condition must be bool, got " + condition + ".");
        }
        for (GLangParser.BlockContext block : ctx.block()) {
            visit(block); // NOTE - both blocks, even if one will never run
        }
        return null;
    }

    // ---- Expressions ----

    @Override
    public Type visitIntExpr(GLangParser.IntExprContext ctx) {
        return Type.INT;
    }

    @Override
    public Type visitBoolExpr(GLangParser.BoolExprContext ctx) {
        return Type.BOOL;
    }

    @Override
    public Type visitIdExpr(GLangParser.IdExprContext ctx) {
        return types.get(ctx.ID().getText());
    }

    @Override
    public Type visitAddExpr(GLangParser.AddExprContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        requireInts(ctx.addOp(), left, right);
        return Type.INT; // NOTE - even after an error, so one mistake gives one message
    }

    @Override
    public Type visitRelationExpr(GLangParser.RelationExprContext ctx) {
        Type left = visit(ctx.expr(0));
        Type right = visit(ctx.expr(1));
        switch (ctx.relationOp().getText()) {
            case "==", "!=" -> requireSameType(ctx.relationOp(), left, right);
            case "<", ">" -> requireInts(ctx.relationOp(), left, right);
            default -> throw new IllegalStateException("Unknown operator: " + ctx.relationOp().getText());
        }
        return Type.BOOL;
    }

    // ---- Helpers ----

    private void requireSameType(ParserRuleContext op, Type left, Type right) {
        if (left != right) {
            error(op.getStart(), "Operator '" + op.getText() + "' expects operands of the same type, got "
                    + left + " and " + right + ".");
        }
    }

    private void requireInts(ParserRuleContext op, Type left, Type right) {
        if (left != Type.INT || right != Type.INT) {
            error(op.getStart(), "Operator '" + op.getText() + "' expects int operands, got "
                    + left + " and " + right + ".");
        }
    }
}
