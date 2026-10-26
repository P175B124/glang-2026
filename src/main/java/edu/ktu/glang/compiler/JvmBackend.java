package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Backend;

import java.io.PrintStream;

/** Backend that compiles to JVM bytecode, loads the class in memory and runs its main. */
public class JvmBackend implements Backend {

    @Override
    public void run(AnalyzedProgram program, PrintStream out) {
        throw new UnsupportedOperationException("TODO - step-10b");
    }
}
