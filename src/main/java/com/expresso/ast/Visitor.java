package com.expresso.ast;

public interface Visitor<T> {
    T visitBinary(Binary node);
    T visitLiteral(Literal node);
    T visitVariable(Variable node);
}
