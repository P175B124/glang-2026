package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Analyzer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

/**
 * Compiles a GLang program into an LLVM IR module (text) with a {@code main} function.
 */
public final class GLangCompiler {

    private GLangCompiler() {}

    /** Generate LLVM IR for an analyzed program. */
    public static String compile(AnalyzedProgram program) {
        CompilerVisitor compiler = new CompilerVisitor();
        compiler.visit(program.tree());
        return compiler.toIr();
    }

    /** Analyze + generate LLVM IR. */
    public static String compile(CharStream input) {
        return compile(Analyzer.analyze(input));
    }

    public static String compile(String source) {
        return compile(CharStreams.fromString(source));
    }
}
