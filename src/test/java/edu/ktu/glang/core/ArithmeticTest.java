package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArithmeticTest {
    @Test
    void execute_addition_printsSum() {
        String src = """
            print(2 + 3);
            """;

        String expected = """
            5
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_subtraction_printsDifference() {
        String src = """
            print(5 - 3);
            """;

        String expected = """
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_singleNumber_stillWorks() {
        String src = """
            print(7);
            """;

        String expected = """
            7
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
