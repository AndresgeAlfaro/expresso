
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

package expresso.ast;

public interface Visitor<T> {
    T visitBinary(Binary node);
    T visitLiteral(Literal node);
    T visitVariable(Variable node);
    T visitUnaryMinus(UnaryMinusAst node);  
    T visitTernary(TernaryAst node);      
    T visitLambda(LambdaAst node);  
    T visitLetStat(LetStatement node);  
    T visitPrintStat(PrintStatement node);
    T visitFuncCall(FuncCall node);
    T visitProgram(Program node);
}