package org.mazur.parco.optimize;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.mazur.parco.parser.ParcoLexer;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class ParcoOptimizer {

  private NodeOptimizer[] _optList = {
    new UnarOptimizer()  
  };
  
  /** Optimizers. */
  private List<NodeOptimizer> optimizers = Arrays.asList(_optList);
  
  public CommonTree optimize(final CommonTree tree) {
    return walk(tree);
  }
  
  private CommonTree walk(final CommonTree tree) {
    CommonTree result = tree;
    LinkedList<CommonTree> stack = new LinkedList<CommonTree>();
    stack.push(tree);
    while (!stack.isEmpty()) {
      CommonTree current = stack.pop();
      for (NodeOptimizer no : optimizers) { 
        CommonTree or = no.process(current);
        if (or != null) { result = or; }
      }
      for (int i = 0; i < current.getChildCount(); i++) {
        CommonTree child = (CommonTree) current.getChild(i);
        stack.push(child);
      }
    }
    return result;
  }
  
  private interface NodeOptimizer {
    CommonTree process(final CommonTree node);
  }
  
  private class UnarOptimizer implements NodeOptimizer {
    @Override
    public CommonTree process(final CommonTree node) {
      if (node.getType() != ParcoLexer.MINUS || node.getChildCount() != 1) { return null; }
      CommonTree child = (CommonTree)node.getChild(0);
      if (child.getType() != ParcoLexer.MINUS) { return null; }
      int index = node.childIndex;
      Tree parent = node.getParent();
      if (child.getChildCount() == 1) {
        CommonTree newChild = (CommonTree)child.getChild(0);
        if (parent != null) {
          parent.replaceChildren(index, index, newChild);
          return null;
        } else {
          return newChild;
        }
      } else {
        Tree left = child.getChild(0), right = child.getChild(1);
        child.replaceChildren(0, 0, right);
        child.replaceChildren(1, 1, left);
        if (parent == null) {
          return child;
        } else {
          parent.replaceChildren(index, index, child);
          return null;
        }
      }
    }
  }
  
  private class AddOptimizer implements NodeOptimizer {
    @Override
    public CommonTree process(CommonTree node) {
      CommonTree parent = (CommonTree) node.getParent();
      
      return null;
    }
  }
  
}
