package expresso.ast;
import java.util.List;
import java.util.ArrayList;

public class LambdaAst extends Ast {
    public final List<String> params;  // Cambiado de String param a List
    public final Ast body;
    
    public LambdaAst(List<String> params, Ast body) {
        this.params = params;
        this.body = body;
    }
    
    // Constructor de conveniencia para un solo parámetro (backward compatibility)
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