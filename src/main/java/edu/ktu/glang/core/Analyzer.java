package edu.ktu.glang.core;

import edu.ktu.glang.semantics.Checker;
import edu.ktu.glang.semantics.DeclarationChecker;
import edu.ktu.glang.semantics.TypeChecker;
import edu.ktu.glang.syntax.GLangLexer;
import edu.ktu.glang.syntax.GLangParser;
import edu.ktu.glang.syntax.SyntaxErrorCollector;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * Analysis part: everything before the program is run.
 * Throws if the program is not correct.
 */
public final class Analyzer {

    private Analyzer() {}

    public static AnalyzedProgram analyze(CharStream input) {
        GLangLexer lexer = new GLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GLangParser parser = new GLangParser(tokens);

        // Collect lexer and parser errors instead of printing to stderr
        SyntaxErrorCollector err = new SyntaxErrorCollector();
        lexer.removeErrorListeners();
        lexer.addErrorListener(err);
        parser.removeErrorListeners();
        parser.addErrorListener(err);

        GLangParser.ProgramContext tree = parser.program();

        List<String> errors = err.getErrors();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Syntax errors:\n" + String.join("\n", errors));
        }

        // Semantic analysis: nothing is run if there are errors
        check(new DeclarationChecker(), tree);
        TypeChecker types = new TypeChecker();
        check(types, tree);

        return new AnalyzedProgram(tree, types);
    }

    /** Run one semantic pass; the next pass runs only if this one found no errors. */
    private static void check(Checker<?> checker, ParseTree tree) {
        checker.visit(tree);
        if (!checker.getErrors().isEmpty()) {
            throw new IllegalArgumentException("Semantic errors:\n" + String.join("\n", checker.getErrors()));
        }
    }
}
