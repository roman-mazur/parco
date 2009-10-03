package org.mazur.parco.model;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public enum DoubleOperation {

  /** Addition. */
  ADD("+"),
  /** Subtraction. */
  SUB("-"), 
  /** Multiplication. */
  MUL("*"),
  /** Division. */
  DIV("/"),
  /** Modular. */
  MOD("%"),
  /** Power. */
  POWER("**");
  
  /** Symbol. */
  private String symbol;
 
  private DoubleOperation(final String symbol) { this.symbol = symbol; };
  
  public String getSymbol() { return symbol; }
  
}
