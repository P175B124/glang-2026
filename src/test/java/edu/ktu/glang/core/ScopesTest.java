package edu.ktu.glang.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScopesTest {
    @Test
    void execute_nestedBlocks_seeAllOuterVariables() {
        String src = """
            let a = 1;
            if (true) {
                let b = 2;
                if (true) {
                    let c = 3;
                    print(a + b + c);
                }
                print(a + b);
            }
            print(a);
            """;

        String expected = """
            6
            3
            1
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_variableFromBlock_isNotVisibleAfterIf() {
        String src = """
            if (true) {
                let y = 2;
            }
            print(y);
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 4:6 Variable 'y' is not declared."), e.getMessage());
    }

    @Test
    void execute_variableFromInnerBlock_isNotVisibleInOuterBlock() {
        String src = """
            if (true) {
                if (true) {
                    let c = 3;
                }
                print(c);
            }
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 5:10 Variable 'c' is not declared."), e.getMessage());
    }

    @Test
    void execute_variableFromThenBlock_isNotVisibleInElseBlock() {
        String src = """
            if (true) {
                let y = 1;
            } else {
                print(y);
            }
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 4:10 Variable 'y' is not declared."), e.getMessage());
    }

    @Test
    void execute_sameNameInSeparateBlocks_isAllowed() {
        // NOTE - first block is removed from the stack before the second one is created
        String src = """
            if (true) {
                let y = 1;
                print(y);
            } else {
                let y = 2;
                print(y);
            }
            if (true) {
                let y = true;
                print(y);
            }
            """;

        String expected = """
            1
            true
            """;

        String out = Runner.runToString(src);
        assertEquals(expected, out);
    }

    @Test
    void execute_redeclarationOfOuterVariableInBlock_isRejected() {
        // NOTE - like local variables in Java: no shadowing
        String src = """
            let x = 1;
            if (true) {
                let x = 2;
            }
            """;

        var e = assertThrows(IllegalArgumentException.class, () -> Runner.runToString(src));
        assertTrue(e.getMessage().contains("line 3:8 Variable 'x' is already declared."), e.getMessage());
    }
}
