package com.expresso;

import com.expresso.ast.*;

public class EvalVisitor implements Visitor<Integer> {
    @Override
    public Integer visitBinary(Binary node) {
        int left = node.left.accept(this);
        int right = node.right.accept(this);
        switch (node.op) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/": return left / right;
            default: throw new RuntimeException("Operador no soportado: " + node.op);
        }
    }

    @Override
    public Integer visitLiteral(Literal node) {
        return node.value;
    }

    @Override
    public Integer visitVariable(Variable node) {
        throw new RuntimeException("Variables no implementadas todavía: " + node.name);
    }
}
