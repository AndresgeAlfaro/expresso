package expresso.ast;

public class Literal extends Ast {
    public final int value;

    public Literal(int value) {
        this.value = value;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
