package com.expresso.ast;

public class Variable extends Ast {
    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitVariable(this);
    }
}
