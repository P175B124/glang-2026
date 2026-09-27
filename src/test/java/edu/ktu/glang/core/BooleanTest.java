package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanTest {
    @Test
    void execute_booleanLiteral_printsValue() {
        String src = """
            print(true);
            print(false);
            """;

        String expected = """
            true
            false
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_comparison_printsBoolean() {
        String src = """
            print(5 > 3);
            print(5 < 3);
            """;

        String expected = """
            true
            false
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_equality_printsBoolean() {
        String src = """
            print(2 == 2);
            print(2 != 2);
            """;

        String expected = """
            true
            false
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_arithmeticInsideComparison_evaluatesArithmeticFirst() {
        String src = """
            print(1 + 2 == 3);
            print(5 - 1 > 3);
            """;

        String expected = """
            true
            true
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_booleanVariable_printsValue() {
        String src = """
            let big = 5 > 3;
            print(big);
            """;

        String expected = """
            true
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
