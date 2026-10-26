package edu.ktu.glang.compiler;

import edu.ktu.glang.core.AnalyzedProgram;
import edu.ktu.glang.core.Backend;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

/** Backend that compiles to JVM bytecode, loads the class in memory and runs its main. */
public class JvmBackend implements Backend {

    @Override
    public void run(AnalyzedProgram program, PrintStream out) {
        byte[] bytecode = GLangCompiler.compile(program);
        Class<?> compiled = new ByteClassLoader().define(GLangCompiler.CLASS_NAME, bytecode);
        execute(compiled, out);
    }

    private static synchronized void execute(Class<?> compiled, PrintStream out) {
        PrintStream original = System.out;
        System.setOut(out); // NOTE - compiled code prints to System.out
        try {
            compiled.getMethod("main", String[].class).invoke(null, (Object) new String[0]);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Program failed: " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot run compiled program", e);
        } finally {
            out.flush();
            System.setOut(original);
        }
    }

    private static final class ByteClassLoader extends ClassLoader {
        Class<?> define(String name, byte[] bytecode) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }
}
