package org.mazur.parco.model;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public enum UnarOperation {

  /** Minus. */
  MINUS("-");
  
  /** Symbol. */
  private String symbol;
  
  private UnarOperation(final String symbol) { this.symbol = symbol; }
  
  public String getSymbol() { return symbol; }
  
}
