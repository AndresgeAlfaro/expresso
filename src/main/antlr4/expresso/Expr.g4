grammar Expr;

// Entry point
prog : stat* EOF ;

// Statements
stat : letStatement (NEWLINE | EOF)     # letStat
     | printStatement (NEWLINE | EOF)   # printStat
     | NEWLINE                          # blankLine
     ;

// 'let' and 'print' statements
letStatement : 'let' ID '=' expr ;
printStatement : 'print' '(' expr ')' ;

// Expressions
expr : expr '(' exprList ')'                   # FuncCall      
     | <assoc=right> expr '**' expr            # Power
     | '-' expr                                # UnaryMinus
     | expr op=('*'|'/') expr                  # MulDiv
     | expr op=('+'|'-') expr                  # AddSub
     | <assoc=right> expr '?' expr ':' expr    # Ternary       
     | <assoc=right> paramList '->' expr       # Lambda        
     | atom                                    # atomExpr
     ;

// Parameter list for lambdas
paramList : ID                           # SingleParam
          | '(' ID (',' ID)* ')'         # MultiParam
          ;

// Argument list for function calls
exprList : expr (',' expr)* ;

// Atomic expressions
atom : INT                              # int
     | ID                               # varRef
     | '(' expr ')'                     # parens
     ;

// ========== TOKENS ==========

// Identifiers
ID : [a-zA-Z_][a-zA-Z_0-9]* ;

// Integer numbers
INT : [0-9]+ ;

// Newline (BEFORE WS)
NEWLINE : '\r'? '\n' ;

// Comments
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;

// Whitespace (WITHOUT newlines)
WS : [ \t]+ -> skip ;
