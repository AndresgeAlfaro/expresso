
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

public class Variable extends Ast {
    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitVariable(this);
    }
}
