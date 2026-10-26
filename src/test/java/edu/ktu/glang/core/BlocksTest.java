package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlocksTest {
    @Test
    void execute_blockInThenBranch_runsAllStatements() {
        String src = """
            if (true) {
                print(1);
                print(2);
            }
            """;

        String expected = """
            1
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_blockInElseBranch_runsAllStatements() {
        String src = """
            let x = 1;
            if (x > 3) {
                print(1);
            } else {
                print(2);
                print(3);
            }
            """;

        String expected = """
            2
            3
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_emptyBlock_doesNothing() {
        String src = """
            if (true) { }
            print(1);
            """;

        String expected = """
            1
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_nestedBlocks_runInOrder() {
        String src = """
            let a = 1;
            if (true) {
                print(a);
                if (a == 1) {
                    print(2);
                }
                print(3);
            }
            """;

        String expected = """
            1
            2
            3
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_variableChangedInBlock_keepsNewValue() {
        String src = """
            let x = 1;
            if (true) {
                x = x + 1;
            }
            print(x);
            """;

        String expected = """
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_ifWithoutBraces_isSyntaxError() {
        // NOTE - braces are required: no dangling else, and the same block later for while
        String src = """
            if (true) print(1);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("Syntax errors"), e.getMessage());
    }
}
