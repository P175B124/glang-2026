package edu.ktu.glang.core;

import edu.ktu.glang.syntax.GLangLexer;
import edu.ktu.glang.syntax.GLangParser;
import edu.ktu.glang.syntax.SyntaxErrorCollector;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

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

        return new AnalyzedProgram(tree);
    }
}
