package expresso.ast;

public class UnaryMinusAst extends Ast {
    public final Ast expr;

    public UnaryMinusAst(Ast expr) {
        this.expr = expr;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitUnaryMinus(this);
    }
}
