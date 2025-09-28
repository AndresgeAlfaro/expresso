package expresso.parser;


import expresso.ast.Ast;
import expresso.parser.AstBuilder;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import expresso.ExprLexer;
import expresso.ExprParser;

public class ConvertToAST {
    public static Ast fromString(String input) {
        CharStream cs = CharStreams.fromString(input);
        ExprLexer lexer = new ExprLexer(cs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);
        ParseTree tree = parser.expr();
        //Aqui tambien se trae AstBuilder pero no lo usa
        AstBuilder builder = new AstBuilder();
        return builder.visit(tree);
    }
}
