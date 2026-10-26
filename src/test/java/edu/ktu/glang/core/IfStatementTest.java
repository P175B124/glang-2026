package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IfStatementTest {
    @Test
    void execute_ifTrue_runsThenBranch() {
        String src = """
            if (true) {
                print(1);
            }
            """;

        String expected = """
            1
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_ifFalseWithoutElse_printsNothing() {
        String src = """
            if (false) {
                print(1);
            }
            print(2);
            """;

        String expected = """
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_ifFalseWithElse_runsElseBranch() {
        String src = """
            if (false) {
                print(1);
            } else {
                print(0);
            }
            """;

        String expected = """
            0
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_conditionWithVariable_runsMatchingBranch() {
        String src = """
            let x = 5;
            if (x > 3) {
                print(1);
            } else {
                print(0);
            }
            """;

        String expected = """
            1
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_nestedIf_runsInnerElse() {
        String src = """
            if (true) {
                if (false) {
                    print(1);
                } else {
                    print(2);
                }
            }
            """;

        String expected = """
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
