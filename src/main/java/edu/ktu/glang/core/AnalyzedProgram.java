package edu.ktu.glang.core;

import edu.ktu.glang.syntax.GLangParser;

/** A program that passed the analysis: a backend gets only correct programs. */
public record AnalyzedProgram(GLangParser.ProgramContext tree) {}
