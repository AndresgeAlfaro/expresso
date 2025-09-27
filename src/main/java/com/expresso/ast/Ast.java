package com.expresso.ast;

public abstract class Ast {
    public abstract <T> T accept(Visitor<T> visitor);
}
