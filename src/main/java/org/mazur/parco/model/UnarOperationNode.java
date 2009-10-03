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
public class UnarOperationNode extends CommonTree {

  /** Operation. */
  private UnarOperation op;
  
  /** Argument. */
  private CommonTree argument;

  public UnarOperationNode(final Token token) {
    super(token);
  }
  
  public UnarOperationNode(final UnarOperationNode node) {
    super(node);
    this.op = node.op;
    this.argument = node.argument;
  }
  
  @Override
  public Tree dupNode() {
    return new UnarOperationNode(this);
  }
  
  /**
   * @return the op
   */
  public UnarOperation getOp() {
    return op;
  }

  /**
   * @return the argument
   */
  public CommonTree getArgument() {
    return argument;
  }

  /**
   * @param op the op to set
   */
  public void setOp(final UnarOperation op) {
    this.op = op;
  }

  /**
   * @param argument the argument to set
   */
  public void setArgument(final CommonTree argument) {
    this.argument = argument;
  }
  
}
