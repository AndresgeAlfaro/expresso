grammar Expr;

// Punto de entrada
prog : stat* EOF ;

// Sentencias
stat : letStatement (NEWLINE | EOF)     # letStat
     | printStatement (NEWLINE | EOF)   # printStat
     | NEWLINE                          # blankLine
     ;

// Sentencias 'let' y 'print'
letStatement : 'let' ID '=' expr ;
printStatement : 'print' '(' expr ')' ;

// Expresiones
expr : expr '(' exprList ')'                   # FuncCall      
     | <assoc=right> expr '**' expr            # Power
     | '-' expr                                # UnaryMinus
     | expr op=('*'|'/') expr                  # MulDiv
     | expr op=('+'|'-') expr                  # AddSub
     | <assoc=right> expr '?' expr ':' expr    # Ternary       
     | <assoc=right> paramList '->' expr       # Lambda        
     | atom                                    # atomExpr
     ;

// Lista de parámetros para lambdas
paramList : ID                           # SingleParam
          | '(' ID (',' ID)* ')'         # MultiParam
          ;

// Lista de argumentos para llamadas a función
exprList : expr (',' expr)* ;

// Expresiones atómicas
atom : INT                              # int
     | ID                               # varRef
     | '(' expr ')'                     # parens
     ;

// ========== TOKENS ==========

// Identificadores
ID : [a-zA-Z_][a-zA-Z_0-9]* ;

// Números enteros
INT : [0-9]+ ;

// Salto de línea (ANTES de WS)
NEWLINE : '\r'? '\n' ;

// Comentarios
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' -> skip ;

// Espacios en blanco (SIN saltos de línea)
WS : [ \t]+ -> skip ;