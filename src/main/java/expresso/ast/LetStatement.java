package expresso.ast;

public class LetStatement extends Ast {
    public final String id;  // Nombre de la variable
    public final Ast expr;   // Expresión a asignar a la variable

    public LetStatement(String id, Ast expr) {
        this.id = id;
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLetStat(this);
    }
}
