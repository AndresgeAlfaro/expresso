
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

package expresso.transpiler;

import expresso.ast.*;
import java.util.*;
import java.util.stream.Collectors;

public class JavaGenerator implements Visitor<String> {

    private static final String INDENT = "    ";
    private int indentLevel = 0;
    private final Set<String> lambdaVars = new HashSet<>();
    private final Set<String> declaredVars = new HashSet<>();
    private Map<String, String> paramRenames = new HashMap<>();
    private boolean insideLambda = false;
    private String className = "Main";

    // ==== PUBLIC API ====
    public static String generate(Ast ast, String className) {
        var gen = new JavaGenerator();
        gen.className = toValidClassName(className);
        return (ast instanceof Program prog)
                ? gen.generateProgram(prog)
                : gen.wrapExpression(ast.accept(gen));
    }

    public static String generate(Ast ast) {
        return generate(ast, "Main");
    }

    // ==== PROGRAM ====
    private String generateProgram(Program prog) {
        prog.statements.forEach(this::detectLambdas);
        prog.statements.forEach(this::trackVariables);
        boolean usesPower = checkForPowerOperator(prog);

        var sb = new StringBuilder();

        // Conditional imports
        if (!lambdaVars.isEmpty()) {
            sb.append("import java.util.function.*;\n");
            if (usesPower) sb.append("import static java.lang.Math.pow;\n");
            sb.append("\n");
        } else if (usesPower) {
            sb.append("import static java.lang.Math.pow;\n\n");
        }

        sb.append("public class ").append(className).append(" {\n")
          .append("    public static void main(String... args) {\n");

        indentLevel = 2;
        prog.statements.forEach(stmt ->
                sb.append(indent()).append(stmt.accept(this)).append(";\n")
        );

        sb.append("    }\n}\n");
        return sb.toString();
    }

    private String wrapExpression(String expr) {
        return """
               public class %s {
                   public static void main(String... args) {
                       System.out.println(%s);
                   }
               }
               """.formatted(className, expr);
    }

    // ==== DETECTION ====
    private void detectLambdas(Ast node) {
        Optional.ofNullable(node)
                .filter(LetStatement.class::isInstance)
                .map(LetStatement.class::cast)
                .map(let -> let.expr)
                .filter(LambdaAst.class::isInstance)
                .ifPresent(expr -> lambdaVars.add(((LetStatement) node).id));
    }

    private void trackVariables(Ast node) {
        Optional.ofNullable(node)
                .filter(LetStatement.class::isInstance)
                .map(LetStatement.class::cast)
                .ifPresent(let -> declaredVars.add(let.id));
    }

    private String generateUniqueName(String original, Set<String> used) {
        var base = original + "$";
        return java.util.stream.IntStream.iterate(1, i -> i + 1)
                .mapToObj(i -> base + i)
                .filter(candidate -> !used.contains(candidate))
                .findFirst()
                .orElse(base);
    }

    // ==== POWER OPERATOR DETECTION ====
    private boolean checkForPowerOperator(Program prog) {
        return prog.statements.stream().anyMatch(this::containsPowerOperator);
    }

    private boolean containsPowerOperator(Ast node) {
        return switch (node) {
            case null -> false;
            case Binary b when b.op.equals("**")
                    || containsPowerOperator(b.left)
                    || containsPowerOperator(b.right) -> true;
            case LetStatement l when containsPowerOperator(l.expr) -> true;
            case PrintStatement p when containsPowerOperator(p.expr) -> true;
            case UnaryMinusAst u when containsPowerOperator(u.expr) -> true;
            case TernaryAst t when containsPowerOperator(t.condition)
                    || containsPowerOperator(t.expr1)
                    || containsPowerOperator(t.expr2) -> true;
            case LambdaAst l when containsPowerOperator(l.body) -> true;
            case FuncCall f when containsPowerOperator(f.function)
                    || f.arguments.stream().anyMatch(this::containsPowerOperator) -> true;
            default -> false;
        };
    }

    // ==== CLASS NAME VALIDATION ====
    private static String toValidClassName(String name) {
        if (name == null || name.isEmpty()) return "Main";
        name = name.replaceAll("\\.(expresso|java)$", "");
        name = name.replaceAll("[^a-zA-Z0-9_]", "");
        if (name.isEmpty()) return "Main";
        if (Character.isDigit(name.charAt(0))) name = "Class" + name;
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return isJavaKeyword(name) ? name + "Class" : name;
    }

    private static boolean isJavaKeyword(String name) {
        return Set.of(
                "abstract","assert","boolean","break","byte","case","catch","char","class","const","continue",
                "default","do","double","else","enum","extends","final","finally","float","for","goto","if",
                "implements","import","instanceof","int","interface","long","native","new","package","private",
                "protected","public","return","short","static","strictfp","super","switch","synchronized","this",
                "throw","throws","transient","try","void","volatile","while"
        ).contains(name.toLowerCase());
    }

    // ==== VISITORS ====
    @Override
    public String visitProgram(Program node) {
        return generateProgram(node);
    }

    @Override
    public String visitLetStat(LetStatement node) {
        var expr = node.expr.accept(this);
        var type = switch (node.expr) {
            case LambdaAst l when l.params.size() == 1 -> "UnaryOperator<Integer>";
            case LambdaAst l when l.params.size() == 2 -> "BinaryOperator<Integer>";
            case LambdaAst l ->
                    throw new RuntimeException("Lambdas with " + l.params.size() + " parameters not supported (max 2)");
            default -> "int";
        };
        return type + " " + node.id + " = " + expr;
    }

    @Override
    public String visitPrintStat(PrintStatement node) {
        return "System.out.println(" + node.expr.accept(this) + ")";
    }

    @Override
    public String visitBinary(Binary node) {
        var left = Optional.ofNullable(node.left)
                .orElseThrow(() -> new RuntimeException("Left operand is null for operator " + node.op))
                .accept(this);
        var right = Optional.ofNullable(node.right)
                .orElseThrow(() -> new RuntimeException("Right operand is null for operator " + node.op))
                .accept(this);

        return node.op.equals("**")
                ? "(int)pow(" + left + ", " + right + ")"
                : "(" + left + " " + node.op + " " + right + ")";
    }

    @Override
    public String visitUnaryMinus(UnaryMinusAst node) {
        var val = node.expr.accept(this);
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
        return insideLambda && paramRenames.containsKey(node.name)
                ? paramRenames.get(node.name)
                : node.name;
    }

    @Override
    public String visitLambda(LambdaAst node) {
        var wasInside = insideLambda;
        var oldRenames = new HashMap<>(paramRenames);
        insideLambda = true;

        var finalParams = new ArrayList<String>();
        var allUsed = new HashSet<>(declaredVars);
        allUsed.addAll(lambdaVars);

        node.params.forEach(param -> {
            var newName = declaredVars.contains(param)
                    ? generateUniqueName(param, allUsed)
                    : param;
            paramRenames.put(param, newName);
            finalParams.add(newName);
            allUsed.add(newName);
        });

        var body = node.body.accept(this);
        insideLambda = wasInside;
        paramRenames = oldRenames;

        return "(" + String.join(", ", finalParams) + ") -> " + body;
    }

    @Override
    public String visitFuncCall(FuncCall node) {
        var func = node.function.accept(this);
        return switch (node.arguments.size()) {
            case 1 -> func + ".apply(" + node.arguments.get(0).accept(this) + ")";
            case 2 -> func + ".apply(" + node.arguments.get(0).accept(this)
                    + ", " + node.arguments.get(1).accept(this) + ")";
            default ->
                    throw new RuntimeException("Function calls with "
                            + node.arguments.size() + " arguments not supported (max 2)");
        };
    }

    // ==== UTIL ====
    private String indent() {
        return INDENT.repeat(indentLevel);
    }
}
