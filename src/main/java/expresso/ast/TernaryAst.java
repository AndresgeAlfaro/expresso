
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

public class TernaryAst extends Ast {
    public final Ast condition;
    public final Ast expr1;
    public final Ast expr2;

    public TernaryAst(Ast condition, Ast expr1, Ast expr2) {
        this.condition = condition;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitTernary(this);
    }
}

