package expresso.ast;

import java.util.List;

public class Program extends Ast {
    public final List<Ast> statements;
    
    public Program(List<Ast> statements) {
        this.statements = statements;
    }
    
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitProgram(this);
    }
}