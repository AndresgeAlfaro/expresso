package com.expresso;

import com.expresso.ast.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class ConvertToAST {
    public static Ast fromString(String input) {
        CharStream cs = CharStreams.fromString(input);
        ExprLexer lexer = new ExprLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ParseTree tree = parser.expr();
        AstBuilder builder = new AstBuilder();
        return builder.visit(tree);
    }
}
