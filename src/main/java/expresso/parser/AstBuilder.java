
/*
 Proyecto: EIF400-II-2025 Expresso (Sprint Mediano)
 Curso: Paradigmas de Programación - UNA
 Grupo: 01-1pm
 Autores: Andres Alfaro Ramirez 
          Rafael Blanco Badilla 
          Maquerly Nuñez Morales 
          Randy Nuñez Vargas
 */

package expresso.parser;

import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.tree.TerminalNode;

import expresso.ExprBaseVisitor;
import expresso.ExprParser;
import expresso.ast.Ast;
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

public class AstBuilder extends ExprBaseVisitor<Ast> {

    // ==== PROGRAM ====
    @Override
    public Ast visitProg(ExprParser.ProgContext ctx) {
        var statements = ctx.stat().stream()
                .map(this::visit)
                .filter(stmt -> stmt != null)
                .toList();
        return new Program(statements);
    }

    // ==== STATEMENTS ====
    @Override
    public Ast visitLetStat(ExprParser.LetStatContext ctx) {
        return visit(ctx.letStatement());
    }

    @Override
    public Ast visitPrintStat(ExprParser.PrintStatContext ctx) {
        return visit(ctx.printStatement());
    }

    @Override
    public Ast visitBlankLine(ExprParser.BlankLineContext ctx) {
        return null; // skip blanks
    }

    @Override
    public Ast visitLetStatement(ExprParser.LetStatementContext ctx) {
        return Optional.ofNullable(visit(ctx.expr()))
                .map(expr -> new LetStatement(ctx.ID().getText(), expr))
                .orElseThrow(() -> new RuntimeException("Expression cannot be null in let statement"));
    }

    @Override
    public Ast visitPrintStatement(ExprParser.PrintStatementContext ctx) {
        return Optional.ofNullable(visit(ctx.expr()))
                .map(PrintStatement::new)
                .orElseThrow(() -> new RuntimeException("Expression cannot be null in print statement"));
    }

    // ==== EXPRESSIONS ====
    @Override
    public Ast visitTernary(ExprParser.TernaryContext ctx) {
        return new TernaryAst(
                visit(ctx.expr(0)),
                visit(ctx.expr(1)),
                visit(ctx.expr(2))
        );
    }

    // ==== LAMBDA ====
    @Override
    public Ast visitLambda(ExprParser.LambdaContext ctx) {
        return new LambdaAst(visitParamList(ctx.paramList()), visit(ctx.expr()));
    }

    private List<String> visitParamList(ExprParser.ParamListContext ctx) {
    return Optional.ofNullable(ctx)
            .map(c -> {
                if (c instanceof ExprParser.SingleParamContext single) {
                    return List.of(single.ID().getText());
                } else if (c instanceof ExprParser.MultiParamContext multi) {
                    return multi.ID().stream()
                            .map(TerminalNode::getText)
                            .toList();
                } else {
                    return List.<String>of();
                }
            })
            .orElse(List.of());
}


    // ==== FUNCTION CALL ====
    @Override
    public Ast visitFuncCall(ExprParser.FuncCallContext ctx) {
        var func = Optional.ofNullable(visit(ctx.expr()))
                .orElseThrow(() -> new RuntimeException("Function cannot be null in call"));
        var args = processExprList(ctx.exprList());
        if (args.isEmpty()) {
            throw new RuntimeException("Function call needs at least one argument");
        }
        return new FuncCall(func, args);
    }

    private List<Ast> processExprList(ExprParser.ExprListContext ctx) {
        return ctx.expr().stream()
                .map(this::visit)
                .peek(expr -> Optional.ofNullable(expr)
                        .orElseThrow(() -> new RuntimeException("Null expression in argument list")))
                .toList();
    }

    // ==== OPERATORS ====
    @Override
    public Ast visitAddSub(ExprParser.AddSubContext ctx) {
        return new Binary(ctx.op.getText(), visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public Ast visitMulDiv(ExprParser.MulDivContext ctx) {
        return new Binary(ctx.op.getText(), visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public Ast visitPower(ExprParser.PowerContext ctx) {
        return new Binary("**", visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    @Override
    public Ast visitUnaryMinus(ExprParser.UnaryMinusContext ctx) {
        return new UnaryMinusAst(visit(ctx.expr()));
    }

    @Override
    public Ast visitAtomExpr(ExprParser.AtomExprContext ctx) {
        return visit(ctx.atom());
    }

    // ==== ATOMS ====
    @Override
    public Ast visitInt(ExprParser.IntContext ctx) {
        return new Literal(Integer.parseInt(ctx.INT().getText()));
    }

    @Override
    public Ast visitVarRef(ExprParser.VarRefContext ctx) {
        return new Variable(ctx.ID().getText());
    }

    @Override
    public Ast visitParens(ExprParser.ParensContext ctx) {
        return visit(ctx.expr());
    }
}
