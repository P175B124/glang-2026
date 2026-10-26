package edu.ktu.glang.app;

import edu.ktu.glang.compiler.GLangCompiler;
import edu.ktu.glang.core.Runner;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final String USAGE = """
            Usage:
              glang <file.glang>                 run the program
              glang -c <file.glang> [<out.ll>]   compile to LLVM IR, then: lli <out.ll>""";

    public static void main(String[] args) {
        try {
            if (args.length == 1 && !args[0].startsWith("-")) {
                // Read source file and run it using Runner
                Runner.run(CharStreams.fromFileName(args[0]), System.out);
            } else if ((args.length == 2 || args.length == 3) && args[0].equals("-c")) {
                compile(args);
            } else {
                System.err.println(USAGE);
                System.exit(1);
            }
        } catch (IllegalArgumentException e) {
            // NOTE - errors in the GLang program: message only, no Java stack trace
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Cannot read or write file: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void compile(String[] args) throws IOException {
        Path source = Path.of(args[1]);
        Path irFile = args.length == 3
                ? Path.of(args[2])
                : Path.of(source.getFileName().toString().replaceFirst("\\.glang$", "") + ".ll");

        String ir = GLangCompiler.compile(CharStreams.fromPath(source));
        Files.writeString(irFile, ir);
        System.out.println("Compiled " + source + " -> " + irFile);
    }
}
