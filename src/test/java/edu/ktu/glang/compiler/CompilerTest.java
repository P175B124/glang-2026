package edu.ktu.glang.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Compiler tests: the generated LLVM IR is compared with the expected text. */
public class CompilerTest {
    @Test
    void compile_print_generatesModule() {
        String src = """
            print(1);
            """;

        String expected = """
            @.int = private constant [4 x i8] c"%d\\0A\\00"

            declare i32 @printf(ptr, ...)

            define i32 @main() {
            entry:
              call i32 (ptr, ...) @printf(ptr @.int, i32 1)
              ret i32 0
            }
            """;

        assertEquals(expected, GLangCompiler.compile(src));
    }
}
