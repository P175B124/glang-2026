package edu.ktu.glang.visitors;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import edu.ktu.glang.syntax.GLangParser;

import java.io.PrintStream;

public class GLangVisitor extends GLangBaseVisitor<Object> {

    private final PrintStream out;

    public GLangVisitor(PrintStream out) {
        this.out = out;
    }
}
