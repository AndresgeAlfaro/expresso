package expresso.runtime;

import expresso.ast.*;

import java.util.HashMap;
import java.util.Map;

public class EvalVisitor implements Visitor<Integer> {
    private Map<String, Integer> variables = new HashMap<>();
    
    // ========== PROGRAMA ==========
    @Override
    public Integer visitProgram(Program node) {
        Integer lastResult = 0;
        for (Ast statement : node.statements) {
            lastResult = statement.accept(this);
        }
        return lastResult;
    }
    
    // ========== STATEMENTS ==========
    @Override
    public Integer visitLetStat(LetStatement node) {
        // Si es una lambda, no la evaluamos, solo retornamos 0
        if (node.expr instanceof LambdaAst) {
            throw new RuntimeException(
                "Error: No se pueden evaluar lambdas en el intérprete. " +
                "Las lambdas solo funcionan al transpilar a Java."
            );
        }
        
        int value = node.expr.accept(this);
        variables.put(node.id, value);
        return value;
    }
    
    @Override
    public Integer visitPrintStat(PrintStatement node) {
        int value = node.expr.accept(this);
        System.out.println(value);
        return value;
    }
    
    // ========== EXPRESIONES BINARIAS ==========
    @Override
    public Integer visitBinary(Binary node) {
        int left = node.left.accept(this);
        int right = node.right.accept(this);
        
        switch (node.op) {
            case "+": 
                return left + right;
            case "-": 
                return left - right;
            case "*": 
                return left * right;
            case "/": 
                if (right == 0) {
                    throw new RuntimeException("Error: División por cero");
                }
                return left / right;
            case "**": 
                return (int) Math.pow(left, right);
            default: 
                throw new RuntimeException("Operador no soportado: " + node.op);
        }
    }
    
    // ========== EXPRESIONES UNARIAS ==========
    @Override
    public Integer visitUnaryMinus(UnaryMinusAst node) {
        int value = node.expr.accept(this);
        return -value;
    }
    
    // ========== EXPRESIONES TERNARIAS ==========
    @Override
    public Integer visitTernary(TernaryAst node) {
        int condition = node.condition.accept(this);
        // Evaluación perezosa: solo evalúa la rama necesaria
        if (condition != 0) {
            return node.expr1.accept(this);
        } else {
            return node.expr2.accept(this);
        }
    }
    
    // ========== LITERALES Y VARIABLES ==========
    @Override
    public Integer visitLiteral(Literal node) {
        return node.value;
    }
    
    @Override
    public Integer visitVariable(Variable node) {
        if (!variables.containsKey(node.name)) {
            throw new RuntimeException("Variable no definida: " + node.name);
        }
        return variables.get(node.name);
    }
    
    // ========== LAMBDA (NO SOPORTADO) ==========
    @Override
    public Integer visitLambda(LambdaAst node) {
        throw new RuntimeException(
            "Error: Las lambdas no pueden evaluarse en el intérprete.\n" +
            "Usa el transpilador para generar código Java con lambdas."
        );
    }
    
    @Override
    public Integer visitFuncCall(FuncCall node) {
        throw new RuntimeException(
            "Error: Las llamadas a función no están soportadas en el intérprete.\n" +
            "Usa el transpilador para generar código Java."
        );
    }

}
