package edu.ktu.glang.app;

import edu.ktu.glang.core.Runner;
import org.antlr.v4.runtime.CharStreams;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("ARG is missing.");
            System.exit(1);
        }

        try {
            // Read source file and execute using Runner
            Runner.run(CharStreams.fromFileName(args[0]), System.out);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
