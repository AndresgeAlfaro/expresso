package expresso.runtime;

import expresso.runtime.EvalAst;
import expresso.ast.Ast;
import expresso.parser.ConvertToAST;

public class ParseEvaluate {
    public static int run(String input) {
        Ast ast = ConvertToAST.fromString(input);
        //Aqui tambien dice que no se usa EvalAst por alguna razon
        return EvalAst.eval(ast);
    }
}
