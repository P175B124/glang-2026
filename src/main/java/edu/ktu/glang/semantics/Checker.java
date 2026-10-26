package edu.ktu.glang.semantics;

import edu.ktu.glang.syntax.GLangBaseVisitor;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * One pass of semantic analysis: walks the whole tree and collects errors.
 * Each pass checks one thing and runs only if the previous passes found no errors.
 */
public abstract class Checker<T> extends GLangBaseVisitor<T> {

    private final List<String> errors = new ArrayList<>();

    public List<String> getErrors() { return errors; }

    protected void error(Token at, String msg) {
        errors.add("line " + at.getLine() + ":" + at.getCharPositionInLine() + " " + msg);
    }
}
