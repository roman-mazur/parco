package org.mazur.parco.model;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class ConstantNode extends CommonTree {

  /** Value. */
  private Number value;

  public ConstantNode(final Token token) {
    super(token);
  }
  
  public ConstantNode(final ConstantNode node) {
    super(node);
    this.value = node.value;
  }

  @Override
  public Tree dupNode() {
    return new ConstantNode(this);
  }
  
  /**
   * @return the value
   */
  public Number getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(final Number value) {
    this.value = value;
  }
  
}
