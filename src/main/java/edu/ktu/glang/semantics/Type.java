package edu.ktu.glang.semantics;

public enum Type {
    INT, BOOL;

    @Override
    public String toString() { return name().toLowerCase(); }
}
