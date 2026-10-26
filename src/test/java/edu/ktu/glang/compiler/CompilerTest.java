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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Compiler-specific tests. All other tests (PrintStatementTest, IfStatementTest, ...)
 * also test the compiler: Runner compiles the program and runs the bytecode.
 */
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
    void compile_numbersOfDifferentSize_printsAll() throws Exception {
        // NOTE - JVM has different instructions: iconst (-1..5), bipush (byte), sipush (short), ldc (other)
        String src = """
            print(0);
            print(5);
            print(100);
            print(1000);
            print(100000);
            """;

        String expected = """
            0
            5
            100
            1000
            100000
            """;

        assertEquals(expected, runCompiled(src));
    }

    @Test
    void compile_booleans_printsTrueFalse() throws Exception {
        // NOTE - JVM has no boolean on the stack: true/false are int 1/0, the type decides println(Z)
        String src = """
            let ok = true;
            print(ok);
            print(false);
            """;

        String expected = """
            true
            false
            """;

        assertEquals(expected, runCompiled(src));
    }

    @Test
    void compile_comparisons_printBooleans() throws Exception {
        // NOTE - a comparison is a jump (IF_ICMPxx) that pushes 1 or 0
        String src = """
            print(1 < 2);
            print(1 > 2);
            print(2 == 2);
            print(2 != 2);
            print(true == true);
            """;

        String expected = """
            true
            false
            true
            false
            true
            """;

        assertEquals(expected, runCompiled(src));
    }

    @Test
    void compile_ifElse_jumpsToTheRightBlock() throws Exception {
        // NOTE - if is a jump (IFEQ) over the then block, else block ends with GOTO
        String src = """
            let x = 5;
            if (x > 3) {
                print(1);
            } else {
                print(0);
            }
            if (x < 3) {
                print(2);
            }
            print(3);
            """;

        String expected = """
            1
            3
            """;

        assertEquals(expected, runCompiled(src));
    }

    @Test
    void compile_semanticError_isRejected() {
        String src = """
            print(1 + true);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> GLangCompiler.compile(src));
        assertTrue(e.getMessage().contains("Operator '+' expects int operands"), e.getMessage());
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
