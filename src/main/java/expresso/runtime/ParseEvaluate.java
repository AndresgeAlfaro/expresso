
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