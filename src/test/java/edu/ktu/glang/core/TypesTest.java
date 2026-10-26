package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypesTest {
    @Test
    void execute_numberPlusBoolean_isRejected() {
        String src = """
            print(1 + true);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:8 Operator '+' expects int operands, got int and bool."), e.getMessage());
    }

    @Test
    void execute_booleanMinusNumber_isRejected() {
        String src = """
            print(true - 1);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:11 Operator '-' expects int operands, got bool and int."), e.getMessage());
    }

    @Test
    void execute_chainedComparison_isRejected() {
        // NOTE - (1 < 2) < 3 compares bool with int
        String src = """
            print(1 < 2 < 3);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:12 Operator '<' expects int operands, got bool and int."), e.getMessage());
    }

    @Test
    void execute_equalityOfDifferentTypes_isRejected() {
        String src = """
            print(1 == true);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:8 Operator '==' expects operands of the same type, got int and bool."), e.getMessage());
    }

    @Test
    void execute_numberAsCondition_isRejected() {
        String src = """
            if (1) {
                print(1);
            }
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 1:4 Condition must be bool, got int."), e.getMessage());
    }

    @Test
    void execute_assignmentOfOtherType_isRejected() {
        String src = """
            let x = 1;
            x = true;
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 2:0 Cannot assign bool to variable 'x' of type int."), e.getMessage());
    }

    @Test
    void execute_variableTypeComesFromInitializer() {
        String src = """
            let big = 5 > 3;
            print(big + 1);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 2:10 Operator '+' expects int operands, got bool and int."), e.getMessage());
    }

    @Test
    void execute_undeclaredVariable_reportsOnlyOneError() {
        // NOTE - unknown type of 'y' must not cause a second, misleading error about '+'
        String src = """
            print(y + 1);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("Variable 'y' is not declared."), e.getMessage());
        assertFalse(e.getMessage().contains("Operator"), e.getMessage());
    }

    @Test
    void execute_correctlyTypedProgram_stillWorks() {
        String src = """
            let x = 2 + 3;
            let ok = x > 4;
            ok = x == 5;
            if (ok) {
                print(x);
            } else {
                print(0);
            }
            print(true == false);
            """;

        String expected = """
            5
            false
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
