package com.expresso;

import com.expresso.ast.*;

public class ParseEvaluate {
    public static int run(String input) {
        Ast ast = ConvertToAST.fromString(input);
        return EvalAst.eval(ast);
    }
}
