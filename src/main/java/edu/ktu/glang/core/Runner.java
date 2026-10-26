package edu.ktu.glang.core;

import edu.ktu.glang.compiler.JvmBackend;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class Runner {

    private static final Backend BACKEND = new JvmBackend(); // NOTE - the only place that chooses the backend

    private Runner() {}

    /** Analyze + run from a CharStream (used by Main). */
    public static void run(CharStream input, PrintStream out) {
        BACKEND.run(Analyzer.analyze(input), out);
    }

    /** Analyze + run from a raw source string. */
    public static void run(String source, PrintStream out) {
        run(CharStreams.fromString(source), out);
    }

    /** Convenience for tests: returns captured stdout as a String. */
    public static String runToString(String source) {
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(printed, true, StandardCharsets.UTF_8);
        run(source, out);

        String output = printed.toString(StandardCharsets.UTF_8);
        return output.replace("\r\n", "\n"); // NOTE - println writes \r\n on Windows, \n on Linux / macOS
    }
}
