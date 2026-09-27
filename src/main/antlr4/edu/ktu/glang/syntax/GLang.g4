grammar GLang;

program : statement* EOF ;

statement
    : printStmt ';'
    ;

printStmt : PRINT '(' expr ')' ;

expr
    : INT                       #intExpr
    | expr addOp expr           #addExpr
    ;

addOp : '+' | '-' ;

// ---- Lexer ----
PRINT : 'print' ;

INT : [0-9]+ ; // NOTE - negative numbers are not supported

COMMENT : ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip ;
WS      : [ \t\r\n]+ -> skip ;
