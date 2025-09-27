package com.expresso;

import com.expresso.ast.Ast;
import com.expresso.ast.Binary;
import com.expresso.ast.Literal;

public class AstBuilder extends ExprBaseVisitor<Ast> {

    @Override
    public Ast visitInt(ExprParser.IntContext ctx) {
        return new Literal(Integer.parseInt(ctx.INT().getText()));
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
    public Ast visitParens(ExprParser.ParensContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Ast visitUnaryMinus(ExprParser.UnaryMinusContext ctx) {
        Ast value = visit(ctx.expr());
        return new Binary("*", new Literal(-1), value);
    }
}
