package expresso.runtime;

import expresso.runtime.EvalVisitor;
import expresso.ast.Ast;

public class EvalAst {
    public static int eval(Ast ast) {
        //VS code me dice que no se usa este evalVisitor, revisar
        EvalVisitor visitor = new EvalVisitor();
        return ast.accept(visitor);
    }
}
