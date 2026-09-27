package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VariablesTest {
    @Test
    void execute_letThenPrint_printsValue() {
        String src = """
            let x = 42;
            print(x);
            """;

        String expected = """
            42
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_letWithExpression_printsValue() {
        String src = """
            let x = 1 + 2;
            print(x);
            """;

        String expected = """
            3
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_variableInExpression_printsResult() {
        String src = """
            let x = 1 + 2;
            print(x + 10);
            """;

        String expected = """
            13
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_reassignment_printsNewValue() {
        String src = """
            let x = 3;
            x = x - 1;
            print(x);
            """;

        String expected = """
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_variableFromVariable_printsValue() {
        String src = """
            let a = 5;
            let b = a + a;
            print(b);
            """;

        String expected = """
            10
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
