
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

import java.util.ArrayList;
import java.util.List;

public class FuncCall extends Ast {
    public final Ast function;
    public final List<Ast> arguments;  // Changed from single Ast argument to List
    
    public FuncCall(Ast function, List<Ast> arguments) {
        this.function = function;
        this.arguments = arguments;
    }
    
    // Convenience constructor for a single argument (backward compatibility)
    public FuncCall(Ast function, Ast argument) {
        this.function = function;
        this.arguments = new ArrayList<>();
        this.arguments.add(argument);
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitFuncCall(this);
    }
}
