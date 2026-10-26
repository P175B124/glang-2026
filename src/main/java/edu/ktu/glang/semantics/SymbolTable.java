package edu.ktu.glang.semantics;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Declared variable names as a stack of scopes.
 * <p>
 * Entering a block pushes a new scope, leaving it pops the scope with all its variables.
 * Lookup goes from the top (innermost scope) down to the bottom (global scope).
 */
public class SymbolTable {

    // NOTE - ArrayDeque would be the modern choice; classic Stack keeps the idea simple
    private final Stack<Set<String>> scopes = new Stack<>();

    public SymbolTable() {
        enterScope(); // NOTE - global scope, never removed
    }

    public void enterScope() {
        scopes.push(new HashSet<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    /** Declare in the current (top) scope. Returns false if the name is already visible. */
    public boolean declare(String name) {
        if (isDeclared(name)) {
            return false; // NOTE - no shadowing, like local variables in Java
        }
        scopes.peek().add(name);
        return true;
    }

    public boolean isDeclared(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) { // NOTE - from top to bottom
            if (scopes.get(i).contains(name)) {
                return true;
            }
        }
        return false;
    }
}
