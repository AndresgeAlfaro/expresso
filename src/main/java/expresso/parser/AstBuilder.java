package expresso.parser;

import expresso.ast.*;
import expresso.ExprParser;
import expresso.ExprBaseVisitor;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

public class AstBuilder extends ExprBaseVisitor<Ast> {
    
    // ========== PROGRAMA ==========
    @Override
    public Ast visitProg(ExprParser.ProgContext ctx) {
        List<Ast> statements = new ArrayList<>();
        for (ExprParser.StatContext statCtx : ctx.stat()) {
            Ast stmt = visit(statCtx);
            if (stmt != null) {  // Ignora líneas en blanco
                statements.add(stmt);
            }
        }
        return new Program(statements);
    }
    
    // ========== STATEMENTS ==========
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
        return null;  // Ignora líneas en blanco
    }
    
    @Override
    public Ast visitLetStatement(ExprParser.LetStatementContext ctx) {
        String id = ctx.ID().getText();
        Ast expr = visit(ctx.expr());
        if (expr == null) {
            throw new RuntimeException("La expresión no puede ser nula en let statement");
        }
        return new LetStatement(id, expr);
    }
    
    @Override
    public Ast visitPrintStatement(ExprParser.PrintStatementContext ctx) {
        Ast expr = visit(ctx.expr());
        if (expr == null) {
            throw new RuntimeException("La expresión no puede ser nula en print statement");
        }
        return new PrintStatement(expr);
    }
    
    // ========== EXPRESIONES ==========
    @Override
    public Ast visitTernary(ExprParser.TernaryContext ctx) {
        Ast condition = visit(ctx.expr(0));
        Ast thenExpr = visit(ctx.expr(1));
        Ast elseExpr = visit(ctx.expr(2));
        return new TernaryAst(condition, thenExpr, elseExpr);
    }

    // ========== LAMBDA ==========
    @Override
    public Ast visitLambda(ExprParser.LambdaContext ctx) {
        List<String> params = visitParamList(ctx.paramList());
        Ast body = visit(ctx.expr());
        return new LambdaAst(params, body);
    }
    
    private List<String> visitParamList(ExprParser.ParamListContext ctx) {
        List<String> params = new ArrayList<>();
        
        if (ctx instanceof ExprParser.SingleParamContext) {
            ExprParser.SingleParamContext single = (ExprParser.SingleParamContext) ctx;
            params.add(single.ID().getText());
        } else if (ctx instanceof ExprParser.MultiParamContext) {
            ExprParser.MultiParamContext multi = (ExprParser.MultiParamContext) ctx;
            for (TerminalNode id : multi.ID()) {
                params.add(id.getText());
            }
        }
        
        return params;
    }
    
    @Override
    public Ast visitFuncCall(ExprParser.FuncCallContext ctx) {
        Ast function = visit(ctx.expr());
        List<Ast> arguments = processExprList(ctx.exprList());  
    
        if (function == null) {
            throw new RuntimeException("La función no puede ser nula en llamada a función");
        }
        if (arguments == null || arguments.isEmpty()) {
            throw new RuntimeException("La llamada a función necesita al menos un argumento");
        }
    
        return new FuncCall(function, arguments);
    }

    // Método auxiliar PRIVADO (no override)
    private List<Ast> processExprList(ExprParser.ExprListContext ctx) {
        List<Ast> exprs = new ArrayList<>();
        
        for (ExprParser.ExprContext exprCtx : ctx.expr()) {
            Ast expr = visit(exprCtx);
            
            if (expr == null) {
                throw new RuntimeException("Expresión nula encontrada en lista de argumentos");
            }
            
            exprs.add(expr);
        }
        
        return exprs;
    }

    @Override
    public Ast visitAddSub(ExprParser.AddSubContext ctx) {
        Ast left = visit(ctx.expr(0));
        Ast right = visit(ctx.expr(1));
        return new Binary(ctx.op.getText(), left, right);
    }
    
    @Override
    public Ast visitMulDiv(ExprParser.MulDivContext ctx) {
        Ast left = visit(ctx.expr(0));
        Ast right = visit(ctx.expr(1));
        return new Binary(ctx.op.getText(), left, right);
    }
    
    @Override
    public Ast visitPower(ExprParser.PowerContext ctx) {
        Ast left = visit(ctx.expr(0));
        Ast right = visit(ctx.expr(1));
        return new Binary("**", left, right);
    }
    
    @Override
    public Ast visitUnaryMinus(ExprParser.UnaryMinusContext ctx) {
        Ast expr = visit(ctx.expr());
        return new UnaryMinusAst(expr);
    }
    
    @Override
    public Ast visitAtomExpr(ExprParser.AtomExprContext ctx) {
        return visit(ctx.atom());  // Pass-through
    }
    
    // ========== ATOMS ==========
    @Override
    public Ast visitInt(ExprParser.IntContext ctx) {
        return new Literal(Integer.parseInt(ctx.INT().getText()));
    }
    
    @Override
    public Ast visitVarRef(ExprParser.VarRefContext ctx) {
        return new Variable(ctx.ID().getText());  // ¡ESTO FALTABA!
    }
    
    @Override
    public Ast visitParens(ExprParser.ParensContext ctx) {
        return visit(ctx.expr());  // Los paréntesis no generan nodos
    }
}