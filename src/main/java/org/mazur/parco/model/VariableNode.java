package org.mazur.parco.model;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class VariableNode extends CommonTree {
  
  /** Name. */
  private String name;

  public VariableNode(final Token token) {
    super(token);
  }
  
  public VariableNode(final VariableNode node) {
    super(node);
    this.name = node.name;
  }
  
  @Override
  public Tree dupNode() {
    return new VariableNode(this);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }
  
  
  
}
