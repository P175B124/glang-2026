package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Analyzer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Compiles a GLang program into a JVM class {@value #CLASS_NAME} with a {@code main} method.
 */
public final class GLangCompiler {

    public static final String CLASS_NAME = "Program";

    private GLangCompiler() {}

    /** Generate bytecode of the class for an analyzed program. */
    public static byte[] compile(AnalyzedProgram program) {
        CompilerVisitor compiler = new CompilerVisitor(program.types());
        compiler.visit(program.tree());
        return compiler.toByteArray();
    }

    /** Analyze + generate bytecode. */
    public static byte[] compile(CharStream input) {
        return compile(Analyzer.analyze(input));
    }

    public static byte[] compile(String source) {
        return compile(CharStreams.fromString(source));
    }

    /** Write a runnable jar: java -jar program.jar */
    public static void writeJar(byte[] bytecode, Path jar) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, CLASS_NAME);

        try (OutputStream file = Files.newOutputStream(jar);
             JarOutputStream out = new JarOutputStream(file, manifest)) {
            out.putNextEntry(new JarEntry(CLASS_NAME + ".class"));
            out.write(bytecode);
            out.closeEntry();
        }
    }
}
