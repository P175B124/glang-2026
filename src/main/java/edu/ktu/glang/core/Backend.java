package edu.ktu.glang.core;

import java.io.PrintStream;

/**
 * Runs an analyzed program: interpret it, or compile it and run the result.
 * Changing the backend does not change the analysis or the tests.
 */
public interface Backend {
    void run(AnalyzedProgram program, PrintStream out);
}
