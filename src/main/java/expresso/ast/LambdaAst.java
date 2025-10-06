
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

public class LambdaAst extends Ast {
    public final List<String> params;  // Changed from single String param to List
    public final Ast body;
    
    public LambdaAst(List<String> params, Ast body) {
        this.params = params;
        this.body = body;
    }
    
    // Convenience constructor for a single parameter (backward compatibility)
    public LambdaAst(String param, Ast body) {
        this.params = new ArrayList<>();
        this.params.add(param);
        this.body = body;
    }
    
    public int arity() {
        return params.size();
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLambda(this);
    }
}
