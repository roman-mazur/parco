package org.mazur.parco.parser;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/**
 * Parser exception.
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class ParsingException extends Exception {

  /** serialVersionUID. */
  private static final long serialVersionUID = 3882294804420821423L;

  /** Line. */
  private int line;
  
  /** Position. */
  private int pos;
  
  /** Token. */
  private Token token;
  
  /** Character. */
  private char character;

  /** Message. */
  private String message;
  
  public ParsingException(final RecognitionException e) {
    this.line = e.line;
    this.pos = e.charPositionInLine;
    this.token = e.token;
    this.character = (char)e.c;
    message = "ERROR. Line " + line + ". Position " + pos + ".";
    if (token != null) { message += " Bad token: " + token.getText(); }
  }
  
  @Override
  public String getMessage() { return message; }
  
  /**
   * @return the line
   */
  public int getLine() {
    return line;
  }

  /**
   * @return the pos
   */
  public int getPos() {
    return pos;
  }

  /**
   * @return the token
   */
  public Token getToken() {
    return token;
  }

  /**
   * @return the character
   */
  public char getCharacter() {
    return character;
  }

}
