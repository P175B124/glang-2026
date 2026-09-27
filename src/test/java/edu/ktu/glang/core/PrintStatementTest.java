package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrintStatementTest {
    @Test
    void execute_singlePrintStatement_printsNumber() {
        String src = """
            print(42);
            """;

        String expected = """
            42
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_multiplePrintStatementsWithComments_printsNumbers() {
        String src = """
            //print one
            print(1);
            /* print two */
            print(2);
            print(3); //print three
            """;

        String expected = """
            1
            2
            3
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
