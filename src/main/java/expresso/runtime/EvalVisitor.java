
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

package expresso.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import expresso.ast.Binary;
import expresso.ast.FuncCall;
import expresso.ast.LambdaAst;
import expresso.ast.LetStatement;
import expresso.ast.Literal;
import expresso.ast.PrintStatement;
import expresso.ast.Program;
import expresso.ast.TernaryAst;
import expresso.ast.UnaryMinusAst;
import expresso.ast.Variable;
import expresso.ast.Visitor;

public class EvalVisitor implements Visitor<Integer> {

    private final Map<String, Integer> variables = new HashMap<>();

    // ==== PROGRAM ====
    @Override
    public Integer visitProgram(Program node) {
        return node.statements.stream()
                .map(stmt -> stmt.accept(this))
                .reduce(0, (a, b) -> b); // last statement result
    }

    // ==== STATEMENTS ====
    @Override
    public Integer visitLetStat(LetStatement node) {
        return Optional.of(node.expr)
                .filter(expr -> !(expr instanceof LambdaAst))
                .map(expr -> expr.accept(this))
                .map(value -> {
                    variables.put(node.id, value);
                    return value;
                })
                .orElseThrow(() -> new RuntimeException(
                        "Error: Lambdas cannot be evaluated. Use the transpiler instead."
                ));
    }

    @Override
    public Integer visitPrintStat(PrintStatement node) {
        var value = node.expr.accept(this);
        System.out.println(value);
        return value;
    }

    // ==== BINARY EXPRESSIONS ====
    @Override
    public Integer visitBinary(Binary node) {
        var left = node.left.accept(this);
        var right = node.right.accept(this);

        return switch (node.op) {
            case "+"  -> left + right;
            case "-"  -> left - right;
            case "*"  -> left * right;
            case "/"  -> {
                if (right == 0) throw new RuntimeException("Error: Division by zero");
                yield left / right;
            }
            case "**" -> (int) Math.pow(left, right);
            default   -> throw new RuntimeException("Unsupported operator: " + node.op);
        };
    }

    // ==== UNARY EXPRESSIONS ====
    @Override
    public Integer visitUnaryMinus(UnaryMinusAst node) {
        return -node.expr.accept(this);
    }

    // ==== TERNARY EXPRESSIONS ====
    @Override
    public Integer visitTernary(TernaryAst node) {
        var condition = node.condition.accept(this);
        return condition != 0
                ? node.expr1.accept(this)
                : node.expr2.accept(this);
    }

    // ==== LITERALS AND VARIABLES ====
    @Override
    public Integer visitLiteral(Literal node) {
        return node.value;
    }

    @Override
    public Integer visitVariable(Variable node) {
        return Optional.ofNullable(variables.get(node.name))
                .orElseThrow(() -> new RuntimeException("Undefined variable: " + node.name));
    }

    // ==== LAMBDAS AND FUNCTION CALLS (UNSUPPORTED) ====
    @Override
    public Integer visitLambda(LambdaAst node) {
        throw new RuntimeException(
                "Error: Lambdas cannot be evaluated in the interpreter.\n" +
                "Use the transpiler to generate Java code."
        );
    }

    @Override
    public Integer visitFuncCall(FuncCall node) {
        throw new RuntimeException(
                "Error: Function calls are not supported in the interpreter.\n" +
                "Use the transpiler to generate Java code."
        );
    }
}
