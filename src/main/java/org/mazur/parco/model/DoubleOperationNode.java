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
public class DoubleOperationNode extends CommonTree {

  /** Operation. */
  private DoubleOperation op;
  
  /** Arguments. */
  private CommonTree leftArg, rightArg;

  public DoubleOperationNode(final Token token) {
    super(token);
  }
  
  public DoubleOperationNode(final DoubleOperationNode node) {
    super(node);
    this.op = node.op;
    this.leftArg = node.leftArg;
    this.rightArg = node.rightArg;
  }
  
  @Override
  public Tree dupNode() {
    return new DoubleOperationNode(this);
  }
  
  /**
   * @return the op
   */
  public DoubleOperation getOp() {
    return op;
  }

  /**
   * @return the leftArg
   */
  public CommonTree getLeftArg() {
    return leftArg;
  }

  /**
   * @return the rightArg
   */
  public CommonTree getRightArg() {
    return rightArg;
  }

  /**
   * @param op the op to set
   */
  public void setOp(final DoubleOperation op) {
    this.op = op;
  }

  /**
   * @param leftArg the leftArg to set
   */
  public void setLeftArg(final CommonTree leftArg) {
    this.leftArg = leftArg;
  }

  /**
   * @param rightArg the rightArg to set
   */
  public void setRightArg(final CommonTree rightArg) {
    this.rightArg = rightArg;
  }
  
}
