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
  DOT = '.';
}

@header {
  package org.mazur.parco.parser;
  import java.util.List;
  import java.util.LinkedList;
  import org.mazur.parco.parser.ParsingException;
}
@lexer::header {
  package org.mazur.parco.parser;
}

@members {
  private boolean correct = true;
  private List<ParsingException> exceptions = new LinkedList<ParsingException>();

  public boolean isCorrect() { return correct; }
  public List<ParsingException> getExceptions() { return exceptions; }
}

@rulecatch {
  catch (RecognitionException e) {
    correct = false;
    exceptions.add(new ParsingException(e));
  }
}

// ---- Parser ----
prog  :	expr^ EOF!
  ;
expr  : term ((PLUS | MINUS)^ term)*
  ;
term  : unar (  
      (MULT^ unar)
    | (DIV^ unar)
    | (MOD^ unar)
    )*
  ;
unar  : (PLUS! unar)
  | (MINUS^ unar)
  | power
  ;
power : factor (POWER^ unar)?
  ;
factor  : CONST
  | IDENTIFIER
  | LB expr RB        -> ^(expr)
  ;

// ---- Lexer ----
POWER : MULT MULT
  ;

WS  : (' ' | '\t' | '\f' | '\r' | '\n')+ { $channel = HIDDEN; }
  ;

IDENTIFIER
  : (LETTER | UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)*
  ;
CONST : (DIGIT)+ (DOT (DIGIT)+)?
  ;

fragment LETTER
  : ('A'..'Z') | ('a'..'z')
  ;
fragment DIGIT
  : '0'..'9'
  ;
