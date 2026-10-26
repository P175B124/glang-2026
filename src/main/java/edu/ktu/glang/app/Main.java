package edu.ktu.glang.app;

import edu.ktu.glang.core.Runner;
import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: glang <file.glang>");
            System.exit(1);
        }

        try {
            // Read source file and execute using Runner
            Runner.run(CharStreams.fromFileName(args[0]), System.out);
        } catch (IllegalArgumentException e) {
            // NOTE - errors in the GLang program: message only, no Java stack trace
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Cannot read file: " + e.getMessage());
            System.exit(2);
        }
    }
}
