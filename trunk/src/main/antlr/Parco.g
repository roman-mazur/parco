grammar Parco;

options {
  output = AST;
}

tokens {
  PLUS = '+';
  MINUS = '-';
  MULT = '*';
  DIV = '/';
  MOD = '%';
  LB = '(';
  RB = ')';
  UNDERSCORE = '_';
  IS = '=';
}

@header {
package org.mazur.parco.parser;
}
@lexer::header {
package org.mazur.parco.parser;
}


// ---- Parser ----
expr  : term ((PLUS term) | (MINUS term))*
  ;
term  : unar (  power
    | (MULT unar)
    | (DIV unar)
    | (MOD unar)
    )*
  ;
unar  : (PLUS unar)
  | (MINUS unar)
  | power
  ;
power : factor ((POWER unar))?
  ;
factor  : CONST
  | IDENTIFIER
  | LB expr RB        
  ;

// ---- Lexer ----
POWER : MULT MULT
  ;

WS  : (' ' | '\t' | '\f' | '\r' | '\n')+ { $channel = HIDDEN; }
  ;

IDENTIFIER
  : (LETTER | UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)*
  ;
CONST : (DIGIT)+
  ;

fragment LETTER
  : ('A'..'Z') | ('a'..'z')
  ;
fragment DIGIT
  : '0'..'9'
  ;
