package expresso.ast;
import java.util.List;
import java.util.ArrayList;

public class FuncCall extends Ast {
    public final Ast function;
    public final List<Ast> arguments;  // Cambiado de Ast argument a List
    
    public FuncCall(Ast function, List<Ast> arguments) {
        this.function = function;
        this.arguments = arguments;
    }
    
    // Constructor de conveniencia para un solo argumento (backward compatibility)
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