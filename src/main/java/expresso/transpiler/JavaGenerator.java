package expresso.transpiler;

import expresso.ast.*;

public class JavaGenerator implements Visitor<String> {

    // Método estático de entrada
    public static String generate(Ast ast) {
        JavaGenerator gen = new JavaGenerator();
        String expr = ast.accept(gen);

        return """
            public class Main {
                public static void main(String[] args) {
                    System.out.println(%s);
                }
            }
            """.formatted(expr);
    }

    @Override
    public String visitBinary(Binary node) {
        String left = node.left.accept(this);
        String right = node.right.accept(this);
        return "(" + left + " " + node.op + " " + right + ")";
    }

    @Override
    public String visitLiteral(Literal node) {
        return Integer.toString(node.value);
    }

    @Override
    public String visitVariable(Variable node) {
        // Por ahora, no hay variables implementadas en Expresso
        throw new UnsupportedOperationException("Variables no implementadas: " + node.name);
    }
}