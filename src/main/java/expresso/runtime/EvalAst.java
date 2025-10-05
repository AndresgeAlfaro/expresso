package expresso.runtime;

import expresso.runtime.EvalVisitor;
import expresso.ast.Ast;

public class EvalAst {
    
    public static int eval(Ast ast) {
        EvalVisitor evaluator = new EvalVisitor();
        Object result = ast.accept(evaluator);
        
        if (result instanceof Integer) {
            return (Integer) result;
        }
        
        // Si el resultado no es Integer (ej: una lambda), retornar 0
        return 0;
    }
    
    public static int eval(Ast ast, EvalVisitor evaluator) {
        Object result = ast.accept(evaluator);
        
        if (result instanceof Integer) {
            return (Integer) result;
        }
        
        return 0;
    }
}