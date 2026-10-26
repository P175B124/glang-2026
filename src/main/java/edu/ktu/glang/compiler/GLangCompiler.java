package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;

/**
 * Compiles a GLang program into a JVM class {@value #CLASS_NAME} with a {@code main} method.
 */
public final class GLangCompiler {

    public static final String CLASS_NAME = "Program";

    private GLangCompiler() {}

    /** Generate bytecode of the class for an analyzed program. */
    public static byte[] compile(AnalyzedProgram program) {
        CompilerVisitor compiler = new CompilerVisitor();
        compiler.visit(program.tree());
        return compiler.toByteArray();
    }
}
