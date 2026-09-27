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

    // ---- Types (semantic analysis - T08) ----

    @Disabled("Žinoma problema: tipų tikrinimas (T08)")
    @Test
    void execute_numberPlusBoolean_isRejected() {
        String src = """
            print(1 + true);
            """;

        assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
    }
    @Disabled("Žinoma problema: tipų tikrinimas (T08)")
    @Test
    void execute_chainedComparison_isRejected() {
        String src = """
            print(1 < 2 < 3);
            """;

        assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
    }

    // ---- Booleans and strings ----

    @Disabled("Žinoma problema: loginės operacijos")
    @Test
    void execute_logicalOperators_printsResult() {
        String src = """
            print(true && false);
            print(true || false);
            print(!true);
            """;

        String expected = """
            false
            true
            false
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
    @Disabled("Žinoma problema: <= ir >=")
    @Test
    void execute_lessOrEqualAndGreaterOrEqual_printsResult() {
        String src = """
            print(2 <= 2);
            print(1 >= 2);
            """;

        String expected = """
            true
            false
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
    @Disabled("Žinoma problema: eilutės (string) tipas")
    @Test
    void execute_stringLiteral_printsText() {
        String src = """
            print("labas");
            """;

        String expected = """
            labas
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    // ---- Control flow ----

    @Disabled("Žinoma problema: tipų tikrinimas (T08)")
    @Test
    void execute_numberAsCondition_isRejected() {
        String src = """
            if (1) print(1);
            """;

        assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
    }
    @Disabled("Žinoma problema: blokai { }")
    @Test
    void execute_blockInIf_runsAllStatements() {
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
    @Disabled("Žinoma problema: while ciklas")
    @Test
    void execute_whileLoop_repeatsBody() {
        String src = """
            let i = 0;
            while (i < 3) {
                print(i);
                i = i + 1;
            }
            """;

        String expected = """
            0
            1
            2
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }
}
