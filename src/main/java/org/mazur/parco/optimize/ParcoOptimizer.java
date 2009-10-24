package org.mazur.parco.optimize;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.mazur.parco.parser.ParcoLexer;
import org.mazur.parco.visualizer.DotGen;
import org.mazur.parco.visualizer.Vizualizer;


/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class ParcoOptimizer {

  /** Flag to display the step. */
  private boolean displayStep = false;
  
  /** First pass optimizers. */
  private List<NodeOptimizer> firstPassOptimizers = Arrays.asList(new NodeOptimizer[] {
      new UnarOptimizer()  
  });
  
  @SuppressWarnings("unchecked")
  private List<List> optimizers = new LinkedList<List>(Arrays.asList(new List[] {
      firstPassOptimizers
  }));

  public void setDisplayStep(final boolean displayStep) {
    this.displayStep = displayStep;
  }
  
  public static boolean isOperation(final Tree node) {
    int t = node.getType();
    return t == ParcoLexer.DIV || t == ParcoLexer.MINUS 
        || t == ParcoLexer.MULT || t == ParcoLexer.POWER
        || t == ParcoLexer.PLUS;
  }
  
  public static boolean isLeaf(final Tree node) {
    int t = node.getType();
    return t == ParcoLexer.CONST || t == ParcoLexer.IDENTIFIER;
  }
  
  @SuppressWarnings("unchecked")
  public List<List> getOptimizers() { return optimizers; }
  
  @SuppressWarnings("unchecked")
  public void addOptimizer(final int passIndex, final Closure c) {
    if (optimizers.size() < passIndex) { throw new IllegalArgumentException("Bad pass index " + passIndex); }
    if (optimizers.size() == passIndex) {
      optimizers.add(new LinkedList<NodeOptimizer>());
    }
    optimizers.get(passIndex).add(new ClosureOptimizer(c));
  }
  
  public CommonTree optimize(final CommonTree tree) {
    return walk(tree);
  }
  
  private CommonTree walk(final CommonTree tree) {
    CommonTree result = tree;
    int index = 0;
    for (List<NodeOptimizer> passOptimizers : optimizers) {
      LinkedList<CommonTree> stack = new LinkedList<CommonTree>();
      stack.push(result);
      while (!stack.isEmpty()) {
        CommonTree current = stack.pop();
        for (NodeOptimizer no : passOptimizers) { 
          CommonTree or = no.process(current);
          if (or != null) { result = or; }
        }
        for (int i = 0; i < current.getChildCount(); i++) {
          CommonTree child = (CommonTree) current.getChild(i);
          stack.push(child);
        }
      }
      System.out.println("------->>>--------");
      System.out.println(result.toStringTree());
      if (displayStep) {
        DotGen gen = new DotGen();
        Vizualizer.run(gen.toDOT(result).toString(), "Step " + (++index));
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
          newChild.setParent(null);
          return newChild;
        }
      } else {
        Tree left = child.getChild(0), right = child.getChild(1);
        child.replaceChildren(0, 0, right);
        child.replaceChildren(1, 1, left);
        if (parent == null) {
          child.setParent(null);
          return child;
        } else {
          parent.replaceChildren(index, index, child);
          return null;
        }
      }
    }
  }
  
  private class ClosureOptimizer implements NodeOptimizer {
    /** Closure. */
    private Closure c;
    
    public ClosureOptimizer(final Closure c) { this.c = c; }
    
    @Override
    public CommonTree process(final CommonTree node) {
      return (CommonTree)c.call(node);
    }
  }
  
}
