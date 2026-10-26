package edu.ktu.glang.compiler;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;

/**
 * Walks the tree like an interpreter would, but instead of computing values
 * it emits LLVM IR (text) that computes them when the program runs.
 * <p>
 * Every expression returns the operand that holds its value:
 * a constant ({@code 42}) or a register ({@code %t3}).
 */
public class CompilerVisitor extends GLangBaseVisitor<String> {

    private final StringBuilder main = new StringBuilder(); // instructions of main

    /** The whole LLVM module: constants, declarations and main. */
    public String toIr() {
        return """
                @.int = private constant [4 x i8] c"%d\\0A\\00"

                declare i32 @printf(ptr, ...)

                define i32 @main() {
                entry:
                """ + main + """
                  ret i32 0
                }
                """;
    }

    // ---- Statements ----

    @Override
    public String visitProgram(GLangParser.ProgramContext ctx) {
        for (GLangParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

    @Override
    public String visitPrintStmt(GLangParser.PrintStmtContext ctx) {
        String value = visit(ctx.expr());
        emit("call i32 (ptr, ...) @printf(ptr @.int, i32 " + value + ")"); // printf("%d\n", value)
        return null;
    }

    // ---- Expressions ----

    @Override
    public String visitIntExpr(GLangParser.IntExprContext ctx) {
        return ctx.INT().getText(); // NOTE - a constant is an operand itself, no instruction needed
    }

    // ---- Helpers ----

    private void emit(String instruction) {
        main.append("  ").append(instruction).append('\n');
    }
}
