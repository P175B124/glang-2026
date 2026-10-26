package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeclarationsTest {
    @Test
    void execute_undeclaredVariableUse_isRejected() {
        String src = """
            print(y);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:6 Variable 'y' is not declared."), e.getMessage());
    }

    @Test
    void execute_assignmentToUndeclaredVariable_isRejected() {
        String src = """
            y = 1;
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:0 Variable 'y' is not declared."), e.getMessage());
    }

    @Test
    void execute_redeclaration_isRejected() {
        String src = """
            let x = 1;
            let x = 2;
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 2:4 Variable 'x' is already declared."), e.getMessage());
    }

    @Test
    void execute_useBeforeDeclaration_isRejected() {
        String src = """
            print(x);
            let x = 1;
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:6 Variable 'x' is not declared."), e.getMessage());
    }

    @Test
    void execute_variableInOwnInitializer_isRejected() {
        String src = """
            let x = x + 1;
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:8 Variable 'x' is not declared."), e.getMessage());
    }

    @Test
    void execute_undeclaredVariableInBranchNeverExecuted_isRejected() {
        // NOTE - the program never reaches print(y), only analysis before execution finds it
        String src = """
            if (false) {
                print(y);
            }
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 2:10 Variable 'y' is not declared."), e.getMessage());
    }

    @Test
    void execute_programWithErrors_printsNothing() {
        String src = """
            print(1);
            print(y);
            """;

        var buf = new ByteArrayOutputStream();
        assertThrows(IllegalArgumentException.class, () -> Runner.run(src, new PrintStream(buf)));
        assertEquals("", buf.toString()); // NOTE - print(1) must not run either
    }

    @Test
    void execute_severalErrors_allReported() {
        String src = """
            print(a);
            b = 1;
            let c = 1;
            let c = 2;
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        String msg = e.getMessage();
        assertTrue(msg.contains("line 1:6 Variable 'a' is not declared."), msg);
        assertTrue(msg.contains("line 2:0 Variable 'b' is not declared."), msg);
        assertTrue(msg.contains("line 4:4 Variable 'c' is already declared."), msg);
    }

    @Test
    void execute_declaredVariables_stillWork() {
        String src = """
            let x = 1;
            let y = x + 1;
            x = y + 1;
            print(x);
            """;

        String expected = """
            3
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
