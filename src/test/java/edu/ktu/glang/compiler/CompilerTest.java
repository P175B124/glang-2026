package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Analyzer;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
}
