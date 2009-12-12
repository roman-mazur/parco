package org.mazur.parco.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.parser.ParcoLexer;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class TreeLoader {

  /** The tree root. */
  private CommonTree root;
  
  /** Count of processors. */
  private int processorsCount;
  
  /** List of operations to load. */
  private List<CommonTree> operations;
  
  private HashSet<CommonTree> finished;
  
  public TreeLoader(final CommonTree root, final int processorsCount) {
    this.root = root; 
    this.processorsCount = processorsCount;
  }
  
  public static int getWeight(final int type) {
    switch (type) {
    case ParcoLexer.PLUS: 
    case ParcoLexer.MINUS: return 2;
    case ParcoLexer.MOD:
    case ParcoLexer.DIV: return 8;
    case ParcoLexer.MULT: return 5;
    case ParcoLexer.POWER: return 9;
    default: return 0;
    }
  }
  
  @SuppressWarnings("unchecked")
  public void initialize() {
    finished = new HashSet<CommonTree>();
    List<CommonTree> operations = new LinkedList<CommonTree>();
    LinkedList<CommonTree> stack = new LinkedList<CommonTree>();
    stack.push(root);
    while (!stack.isEmpty()) {
      CommonTree currentNode = stack.pop();
      if (currentNode.getChildCount() > 0) {
        operations.add(currentNode);
        for (CommonTree child : (List<CommonTree>)currentNode.getChildren()) {
          stack.push(child);
        }
      } else {
        finished.add(currentNode);
      }
    }
    this.operations = new ArrayList<CommonTree>(operations);
    System.out.println("Operations to load: " + this.operations.size());
  }
  
  private List<CommonTree> selectOperations() {
    List<CommonTree> result = new ArrayList<CommonTree>(processorsCount);
    for (CommonTree node : operations) {
      if (node.getChildCount() == 0 || finished.containsAll(node.getChildren())) {
        result.add(node);
        if (result.size() >= processorsCount) { break; }
      }
    }
    return result;
  }
  
  public List<LoadStep> load() {
    List<LoadStep> result = new LinkedList<LoadStep>();
    int lastDuration = 0;
    while (!operations.isEmpty()) {
      List<CommonTree> toLoad = selectOperations();
      int maxDuration = 0;
      for (CommonTree op : toLoad) {
        int w = getWeight(op.getType());
        if (w > maxDuration) { maxDuration = w; }
      }
      maxDuration += lastDuration;
      System.out.println("Selected: " + toLoad + " duration: " + maxDuration);
      finished.addAll(toLoad);
      operations.removeAll(toLoad);
      LoadStep step = new LoadStep();
      step.setFinishedOperations(new ArrayList<CommonTree>(finished));
      step.setDuration(maxDuration);
      result.add(step);
      lastDuration = maxDuration;
    }
    return result;
  }
  
}
