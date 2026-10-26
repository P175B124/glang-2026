package edu.ktu.glang.compiler;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Walks the tree like an interpreter would, but instead of computing values
 * it emits JVM instructions that compute them when the program runs.
 * <p>
 * JVM is a stack machine: an expression leaves its value on the operand stack,
 * a statement leaves the stack empty.
 */
public class CompilerVisitor extends GLangBaseVisitor<Void> implements Opcodes {

    private final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    private MethodVisitor mv; // main method being written

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
    public Void visitPrintStmt(GLangParser.PrintStmtContext ctx) {
        // System.out.println(expr)
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        visit(ctx.expr());
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        return null;
    }

    // ---- Expressions ----

    @Override
    public Void visitIntExpr(GLangParser.IntExprContext ctx) {
        mv.visitLdcInsn(Integer.parseInt(ctx.INT().getText())); // NOTE - constant from the class constant pool
        return null;
    }
}
