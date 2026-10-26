grammar GLang;

program : statement* EOF ;

statement
    : letDecl ';'
    | assignment ';'
    | printStmt ';'
    | ifStmt                    // NOTE - no semicolon
    ;

letDecl    : LET ID '=' expr ;
assignment : ID '=' expr ;
printStmt  : PRINT '(' expr ')' ;
ifStmt     : IF '(' expr ')' block (ELSE block)? ; // NOTE - braces required

block      : '{' statement* '}' ;   // NOTE - later reused by while, for, functions

expr
    : INT                       #intExpr
    | BOOL                      #boolExpr
    | ID                        #idExpr
    | expr addOp expr           #addExpr      // NOTE - alternative order matters
    | expr relationOp expr      #relationExpr
    ;

addOp      : '+' | '-' ;
relationOp : '==' | '!=' | '<' | '>' ;

// ---- Lexer ----
LET   : 'let' ;
PRINT : 'print' ;
IF    : 'if' ;
ELSE  : 'else' ;
BOOL  : 'true' | 'false' ;

ID  : [a-zA-Z_][a-zA-Z_0-9]* ; // NOTE - must be after keywords
INT : [0-9]+ ; // NOTE - negative numbers are not supported

COMMENT : ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip ;
WS      : [ \t\r\n]+ -> skip ;
