package expresso.runtime;

import expresso.runtime.EvalAst;
import expresso.ast.Ast;
import expresso.parser.ConvertToAST;

public class ParseEvaluate {
    
    public static int run(String input) {
        Ast ast = ConvertToAST.fromString(input);
        return EvalAst.eval(ast);
    }
    
    public static int runExpression(String input) {
        Ast ast = ConvertToAST.fromExpression(input);
        return EvalAst.eval(ast);
    }
}