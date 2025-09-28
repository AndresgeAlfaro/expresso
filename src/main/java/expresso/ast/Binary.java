package expresso.ast;

public class Binary extends Ast {
    public final String op;
    public final Ast left;
    public final Ast right;

    public Binary(String op, Ast left, Ast right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitBinary(this);
    }
}
