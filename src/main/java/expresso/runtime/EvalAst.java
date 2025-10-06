
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

package expresso.runtime;

import java.util.Optional;

import expresso.ast.Ast;

public class EvalAst {

    // Evaluate an AST node with a new visitor
    public static int eval(Ast ast) {
        var evaluator = new EvalVisitor();
        return eval(ast, evaluator);
    }

    // Evaluate an AST node with an existing visitor
    public static int eval(Ast ast, EvalVisitor evaluator) {
        return Optional.ofNullable(ast.accept(evaluator))
                .filter(Integer.class::isInstance)
                .map(Integer.class::cast)
                .orElse(0);  // return 0 if not an Integer
    }
}
