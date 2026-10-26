package edu.ktu.glang.compiler;

import edu.ktu.glang.semantics.Type;
import edu.ktu.glang.semantics.TypeChecker;
import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

/**
 * Walks the tree like an interpreter would, but instead of computing values
 * it emits JVM instructions that compute them when the program runs.
 * <p>
 * JVM is a stack machine: an expression leaves its value on the operand stack,
 * a statement leaves the stack empty.
 */
public class CompilerVisitor extends GLangBaseVisitor<Void> implements Opcodes {

    private final TypeChecker types;
    private final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    private MethodVisitor mv; // main method being written

    // NOTE - variable name -> local variable slot; slot 0 is String[] args of main
    private final Map<String, Integer> slots = new HashMap<>();

    public CompilerVisitor(TypeChecker types) {
        this.types = types;
    }

    public byte[] toByteArray() { return classWriter.toByteArray(); }

    // ---- Statements ----

    @Override
    public Void visitProgram(GLangParser.ProgramContext ctx) {
        // public class Program { public static void main(String[] args) { ... } }
        classWriter.visit(V21, ACC_PUBLIC | ACC_SUPER, GLangCompiler.CLASS_NAME, null, "java/lang/Object", null);
        mv = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        for (GLangParser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0); // NOTE - stack size and locals computed by ASM
        mv.visitEnd();
        classWriter.visitEnd();
        return null;
    }

    @Override
    public Void visitLetDecl(GLangParser.LetDeclContext ctx) {
        // NOTE - same name in separate blocks reuses its slot; int and bool are both int on JVM
        int slot = slots.computeIfAbsent(ctx.ID().getText(), name -> slots.size() + 1);
        visit(ctx.expr());             // value -> stack
        mv.visitVarInsn(ISTORE, slot); // stack -> local variable
        return null;
    }

    @Override
    public Void visitAssignment(GLangParser.AssignmentContext ctx) {
        visit(ctx.expr());
        mv.visitVarInsn(ISTORE, slots.get(ctx.ID().getText()));
        return null;
    }

    @Override
    public Void visitPrintStmt(GLangParser.PrintStmtContext ctx) {
        // System.out.println(expr)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        visit(ctx.expr());
        String descriptor = types.typeOf(ctx.expr()) == Type.BOOL ? "(Z)V" : "(I)V"; // NOTE - types decide
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false);
        return null;
    }

    @Override
    public Void visitIfStmt(GLangParser.IfStmtContext ctx) {
        Label elseBlock = new Label();
        Label end = new Label();

        visit(ctx.expr());                    // condition -> stack (1 or 0)
        mv.visitJumpInsn(IFEQ, elseBlock);    // 0 (false) -> skip the then block
        visit(ctx.block(0));
        if (ctx.ELSE() != null) {
            mv.visitJumpInsn(GOTO, end);      // then block done -> skip the else block
            mv.visitLabel(elseBlock);
            visit(ctx.block(1));
        } else {
            mv.visitLabel(elseBlock);
        }
        mv.visitLabel(end);
        return null;
    }

    // ---- Expressions ----

    @Override
    public Void visitIntExpr(GLangParser.IntExprContext ctx) {
        pushInt(Integer.parseInt(ctx.INT().getText()));
        return null;
    }

    @Override
    public Void visitBoolExpr(GLangParser.BoolExprContext ctx) {
        mv.visitInsn(ctx.BOOL().getText().equals("true") ? ICONST_1 : ICONST_0);
        return null;
    }

    @Override
    public Void visitIdExpr(GLangParser.IdExprContext ctx) {
        mv.visitVarInsn(ILOAD, slots.get(ctx.ID().getText()));
        return null;
    }

    @Override
    public Void visitAddExpr(GLangParser.AddExprContext ctx) {
        visit(ctx.expr(0)); // left -> stack
        visit(ctx.expr(1)); // right -> stack
        switch (ctx.addOp().getText()) { // pops both, pushes result
            case "+" -> mv.visitInsn(IADD);
            case "-" -> mv.visitInsn(ISUB);
            default -> throw new IllegalStateException("Unknown operator: " + ctx.addOp().getText());
        }
        return null;
    }

    @Override
    public Void visitRelationExpr(GLangParser.RelationExprContext ctx) {
        visit(ctx.expr(0));
        visit(ctx.expr(1));
        int jumpIfTrue = switch (ctx.relationOp().getText()) { // NOTE - bool is int too, so == works for both
            case "==" -> IF_ICMPEQ;
            case "!=" -> IF_ICMPNE;
            case "<" -> IF_ICMPLT;
            case ">" -> IF_ICMPGT;
            default -> throw new IllegalStateException("Unknown operator: " + ctx.relationOp().getText());
        };

        // JVM has no "compare and push boolean": jump, then push 1 or 0
        Label isTrue = new Label();
        Label end = new Label();
        mv.visitJumpInsn(jumpIfTrue, isTrue); // pops both operands
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, end);
        mv.visitLabel(isTrue);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(end);
        return null;
    }

    // ---- Helpers ----

    /** Shortest JVM instruction for a constant. */
    private void pushInt(int value) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            mv.visitLdcInsn(value); // NOTE - from the class constant pool
        }
    }
}
