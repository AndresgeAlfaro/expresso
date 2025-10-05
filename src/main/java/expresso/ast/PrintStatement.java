package expresso.ast;

public class PrintStatement extends Ast {
    public final Ast expr;  // Expresión que será impresa

    public PrintStatement(Ast expr) {
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPrintStat(this);
    }
}
