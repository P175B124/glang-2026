package edu.ktu.glang.core;

import edu.ktu.glang.semantics.TypeChecker;
import edu.ktu.glang.syntax.GLangParser;

/**
 * A program that passed the analysis: a backend gets only correct programs.
 * The tree is annotated with the type of every expression.
 */
public record AnalyzedProgram(GLangParser.ProgramContext tree, TypeChecker types) {}
