package edu.ktu.glang.core;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Known problems of the current GLang implementation.
 * <p>
 * Every test describes behaviour that does NOT work yet. Remove {@code @Disabled},
 * make the test pass, and keep all other tests green.
 * <p>
 * A program that must be "rejected" should fail before it runs:
 * {@link Runner} throws {@link IllegalArgumentException}, as it already does for syntax errors.
 */
public class KnownProblemsTest {

    // ---- Arithmetic ----

    @Disabled("Žinoma problema: daugyba ir operacijų prioritetas")
    @Test
    void execute_multiplication_bindsTighterThanAddition() {
        String src = """
            print(1 + 2 * 3);
            """;

        String expected = """
            7
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
    @Disabled("Žinoma problema: dalyba ir operacijų prioritetas")
    @Test
    void execute_division_bindsTighterThanSubtraction() {
        String src = """
            print(8 - 6 / 2);
            """;

        String expected = """
            5
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
    @Disabled("Žinoma problema: neigiami skaičiai")
    @Test
    void execute_negativeNumbers_printsResult() {
        String src = """
            print(-5);
            print(3 - -2);
            """;

        String expected = """
            -5
            5
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
    @Disabled("Žinoma problema: skliaustai")
    @Test
    void execute_parentheses_changeOrder() {
        String src = """
            print(10 - (3 - 2));
            """;

        String expected = """
            9
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    // ---- Variables (semantic analysis - T08) ----

    @Disabled("Žinoma problema: nedeklaruotas kintamasis (T08)")
    @Test
    void execute_undeclaredVariableUse_isRejected() {
        String src = """
            print(y);
            """;

        assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
    }
    @Disabled("Žinoma problema: priskyrimas nedeklaruotam kintamajam (T08)")
    @Test
    void execute_assignmentToUndeclaredVariable_isRejected() {
        String src = """
            y = 1;
            """;

        assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
    }
    @Disabled("Žinoma problema: pakartotinis deklaravimas (T08)")
    @Test
    void execute_redeclaration_isRejected() {
        String src = """
            let x = 1;
            let x = 2;
            """;

        assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
    }
}
