package com.expresso;

import com.expresso.ast.*;

public class EvalAst {
    public static int eval(Ast ast) {
        EvalVisitor visitor = new EvalVisitor();
        return ast.accept(visitor);
    }
}
