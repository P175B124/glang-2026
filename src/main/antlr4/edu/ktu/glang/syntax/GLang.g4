grammar GLang;

program : statement* EOF ;

statement
    : letDecl ';'
    | assignment ';'
    | printStmt ';'
    ;

letDecl    : LET ID '=' expr ;
assignment : ID '=' expr ;
printStmt  : PRINT '(' expr ')' ;

expr
    : INT                       #intExpr
    | ID                        #idExpr
    | expr addOp expr           #addExpr
    ;

addOp : '+' | '-' ;

// ---- Lexer ----
LET   : 'let' ;
PRINT : 'print' ;

ID  : [a-zA-Z_][a-zA-Z_0-9]* ; // NOTE - must be after keywords
INT : [0-9]+ ; // NOTE - negative numbers are not supported

COMMENT : ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip ;
WS      : [ \t\r\n]+ -> skip ;
