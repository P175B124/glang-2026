package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Analyzer;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Compiler-specific tests: programs are run with JvmBackend directly. */
public class CompilerTest {
    @Test
    void compile_print_printsNumber() {
        String src = """
            print(1);
            """;

        String expected = """
            1
            """;

        assertEquals(expected, runCompiled(src));
    }

    @Test
    void compile_writeJar_runsWithJava(@TempDir Path dir) throws Exception {
        String src = """
            print(42);
            """;

        String expected = """
            42
            """;

        assertEquals(expected, runJar(src, dir));
    }

    // ---- Helpers ----

    /** Run the program with JvmBackend (compile, load in memory, run main) and return what it printed. */
    private static String runCompiled(String src) {
        AnalyzedProgram program = Analyzer.analyze(CharStreams.fromString(src));

        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(printed, true, StandardCharsets.UTF_8);
        new JvmBackend().run(program, out);

        String output = printed.toString(StandardCharsets.UTF_8);
        return output.replace("\r\n", "\n"); // NOTE - println writes \r\n on Windows, \n on Linux / macOS
    }

    /** Compile to a jar, run it with a separate java process and return what it printed. */
    private static String runJar(String src, Path dir) throws Exception {
        byte[] bytecode = GLangCompiler.compile(src);
        Path jar = dir.resolve("program.jar");
        GLangCompiler.writeJar(bytecode, jar);

        String java = ProcessHandle.current().info().command().orElse("java"); // NOTE - same java as the tests
        Process process = new ProcessBuilder(java, "-jar", jar.toString()).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        int exitCode = process.waitFor();
        assertEquals(0, exitCode, output);
        return output.replace("\r\n", "\n"); // NOTE - println writes \r\n on Windows, \n on Linux / macOS
    }
}
