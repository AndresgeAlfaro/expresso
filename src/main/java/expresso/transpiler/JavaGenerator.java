package expresso.transpiler;

import expresso.ast.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class JavaGenerator implements Visitor<String> {
    
    private static final String INDENT = "    ";
    private int indentLevel = 0;
    private Set<String> lambdaVars = new HashSet<>();
    private Set<String> declaredVars = new HashSet<>();
    private Map<String, String> paramRenames = new HashMap<>(); // NUEVO: mapeo de renombres
    private boolean insideLambda = false;
    private String className = "Main";
    
    // ==================== API PÚBLICA ====================
    
    public static String generate(Ast ast, String className) {
        JavaGenerator gen = new JavaGenerator();
        gen.className = toValidClassName(className);
        return (ast instanceof Program) 
            ? gen.generateProgram((Program) ast)
            : gen.wrapExpression(ast.accept(gen));
    }
    
    public static String generate(Ast ast) {
        return generate(ast, "Main");
    }
    
    // ==================== GENERACIÓN ====================
    
    private String generateProgram(Program prog) {
        // Primera pasada: detectar lambdas y variables declaradas
        prog.statements.forEach(this::detectLambdas);
        prog.statements.forEach(this::trackVariables);
        
        boolean usesPower = checkForPowerOperator(prog);
        
        StringBuilder sb = new StringBuilder();
        
        // Imports condicionales
        if (!lambdaVars.isEmpty()) {
            sb.append("import java.util.function.*;\n");
            if (usesPower) {
                sb.append("import static java.lang.Math.pow;\n");
            }
            sb.append("\n");
        } else if (usesPower) {
            sb.append("import static java.lang.Math.pow;\n\n");
        }
        
        // Clase y método main
        sb.append("public class ").append(className).append(" {\n");
        sb.append("    public static void main(String... args) {\n");
        
        // Statements
        indentLevel = 2;
        for (Ast stmt : prog.statements) {
            sb.append(indent()).append(stmt.accept(this));
            sb.append(";\n");
        }
        
        sb.append("    }\n}\n");
        return sb.toString();
    }
    
    private String wrapExpression(String expr) {
        return "public class " + className + " {\n" +
               "    public static void main(String... args) {\n" +
               "        System.out.println(" + expr + ");\n" +
               "    }\n}\n";
    }
    
    // ==================== DETECCIÓN ====================
    
    private void detectLambdas(Ast node) {
        if (node instanceof LetStatement) {
            LetStatement let = (LetStatement) node;
            if (let.expr instanceof LambdaAst) {
                lambdaVars.add(let.id);
            }
        }
    }
    
    private void trackVariables(Ast node) {
        if (node instanceof LetStatement) {
            declaredVars.add(((LetStatement) node).id);
        }
    }
    
    /**
     * NUEVO: Genera un nombre único para un parámetro que causa shadowing
     */
    private String generateUniqueName(String original, Set<String> usedNames) {
        String candidate = original + "$";
        int counter = 1;
        
        while (usedNames.contains(candidate)) {
            candidate = original + "$" + counter;
            counter++;
        }
        
        return candidate;
    }
    
    // ==================== DETECCIÓN DE OPERADOR ** ====================
    
    private boolean checkForPowerOperator(Program prog) {
        for (Ast stmt : prog.statements) {
            if (containsPowerOperator(stmt)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsPowerOperator(Ast node) {
        if (node == null) return false;
        
        if (node instanceof Binary) {
            Binary bin = (Binary) node;
            if (bin.op.equals("**")) return true;
            return containsPowerOperator(bin.left) || containsPowerOperator(bin.right);
        }
        
        if (node instanceof LetStatement) {
            return containsPowerOperator(((LetStatement) node).expr);
        }
        
        if (node instanceof PrintStatement) {
            return containsPowerOperator(((PrintStatement) node).expr);
        }
        
        if (node instanceof UnaryMinusAst) {
            return containsPowerOperator(((UnaryMinusAst) node).expr);
        }
        
        if (node instanceof TernaryAst) {
            TernaryAst tern = (TernaryAst) node;
            return containsPowerOperator(tern.condition) 
                || containsPowerOperator(tern.expr1) 
                || containsPowerOperator(tern.expr2);
        }
        
        if (node instanceof LambdaAst) {
            return containsPowerOperator(((LambdaAst) node).body);
        }
        
        if (node instanceof FuncCall) {
            FuncCall call = (FuncCall) node;
            if (containsPowerOperator(call.function)) return true;
            for (Ast arg : call.arguments) {
                if (containsPowerOperator(arg)) return true;
            }
        }
        
        return false;
    }
    
    // ==================== VALIDACIÓN DE NOMBRE ====================
    
    private static String toValidClassName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "Main";
        }
        
        if (fileName.endsWith(".expresso")) {
            fileName = fileName.substring(0, fileName.length() - 9);
        } else if (fileName.endsWith(".java")) {
            fileName = fileName.substring(0, fileName.length() - 5);
        }
        
        fileName = fileName.replaceAll("[^a-zA-Z0-9_]", "");
        
        if (fileName.isEmpty()) {
            return "Main";
        }
        
        if (Character.isDigit(fileName.charAt(0))) {
            fileName = "Class" + fileName;
        }
        
        fileName = Character.toUpperCase(fileName.charAt(0)) + fileName.substring(1);
        
        if (isJavaKeyword(fileName)) {
            fileName = fileName + "Class";
        }
        
        return fileName;
    }
    
    private static boolean isJavaKeyword(String name) {
        String[] keywords = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "class", "const", "continue", "default", "do", "double",
            "else", "enum", "extends", "final", "finally", "float", "for",
            "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private",
            "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
        };
        
        String lowerName = name.toLowerCase();
        for (String keyword : keywords) {
            if (keyword.equals(lowerName)) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== VISITORS ====================
    
    @Override
    public String visitProgram(Program node) {
        return generateProgram(node);
    }
    
    @Override
    public String visitLetStat(LetStatement node) {
        String expr = node.expr.accept(this);
        String type;
        
        if (node.expr instanceof LambdaAst) {
            LambdaAst lambda = (LambdaAst) node.expr;
            int arity = lambda.params.size();
            
            if (arity == 1) {
                type = "UnaryOperator<Integer>";
            } else if (arity == 2) {
                type = "BinaryOperator<Integer>";
            } else {
                throw new RuntimeException("Lambdas con " + arity + " parámetros no están soportadas (máximo 2)");
            }
        } else {
            type = "int";
        }
        
        return type + " " + node.id + " = " + expr;
    }
    
    @Override
    public String visitPrintStat(PrintStatement node) {
        return "System.out.println(" + node.expr.accept(this) + ")";
    }
    
    @Override
    public String visitBinary(Binary node) {
        if (node.left == null) {
            throw new RuntimeException("ERROR: operando izquierdo es null en Binary con operador: " + node.op);
        }
        if (node.right == null) {
            throw new RuntimeException("ERROR: operando derecho es null en Binary con operador: " + node.op);
        }
        
        String left = node.left.accept(this);
        String right = node.right.accept(this);
        
        if (node.op.equals("**")) {
            String powExpr = "(int)pow(" + left + ", " + right + ")";
            return insideLambda ? powExpr : "(int) " + powExpr;
        }
        
        return "(" + left + " " + node.op + " " + right + ")";
    }
    
    @Override
    public String visitUnaryMinus(UnaryMinusAst node) {
        String val = node.expr.accept(this);
        return (node.expr instanceof Binary || node.expr instanceof TernaryAst)
            ? "-(" + val + ")"
            : "-" + val;
    }
    
    @Override
    public String visitTernary(TernaryAst node) {
        return "(" + node.condition.accept(this) + " != 0 ? " 
            + node.expr1.accept(this) + " : " + node.expr2.accept(this) + ")";
    }
    
    @Override
    public String visitLiteral(Literal node) {
        return String.valueOf(node.value);
    }
    
    @Override
    public String visitVariable(Variable node) {
        // NUEVO: Si la variable fue renombrada dentro de una lambda, usar el nuevo nombre
        if (insideLambda && paramRenames.containsKey(node.name)) {
            return paramRenames.get(node.name);
        }
        return node.name;
    }
    
    @Override
    public String visitLambda(LambdaAst node) {
        boolean wasInside = insideLambda;
        Map<String, String> oldRenames = new HashMap<>(paramRenames);
        
        insideLambda = true;
        
        // NUEVO: Detectar y renombrar parámetros que causan shadowing
        List<String> finalParams = new ArrayList<>();
        Set<String> allUsedNames = new HashSet<>(declaredVars);
        allUsedNames.addAll(lambdaVars);
        
        for (String param : node.params) {
            if (declaredVars.contains(param)) {
                // Hay conflicto, generar nombre único
                String newName = generateUniqueName(param, allUsedNames);
                paramRenames.put(param, newName);
                finalParams.add(newName);
                allUsedNames.add(newName);
            } else {
                finalParams.add(param);
                allUsedNames.add(param);
            }
        }
        
        String body = node.body.accept(this);
        
        // Restaurar estado
        insideLambda = wasInside;
        paramRenames = oldRenames;
        
        String params = String.join(", ", finalParams);
        return "(" + params + ") -> " + body;
    }
    
    @Override
    public String visitFuncCall(FuncCall node) {
        String function = node.function.accept(this);
        int arity = node.arguments.size();
        
        if (arity == 1) {
            return function + ".apply(" + node.arguments.get(0).accept(this) + ")";
        } else if (arity == 2) {
            return function + ".apply(" + 
                   node.arguments.get(0).accept(this) + ", " + 
                   node.arguments.get(1).accept(this) + ")";
        } else {
            throw new RuntimeException("Llamadas a función con " + arity + " argumentos no están soportadas (máximo 2)");
        }
    }
    
    // ==================== UTILIDADES ====================
    
    private String indent() {
        return INDENT.repeat(indentLevel);
    }
}