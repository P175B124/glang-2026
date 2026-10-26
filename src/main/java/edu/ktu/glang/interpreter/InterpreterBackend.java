package edu.ktu.glang.interpreter;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Backend;

import java.io.PrintStream;

/** Backend that walks the tree and executes it directly. */
public class InterpreterBackend implements Backend {
    @Override
    public void run(AnalyzedProgram program, PrintStream out) {
        new InterpreterVisitor(out).visit(program.tree());
    }
}
