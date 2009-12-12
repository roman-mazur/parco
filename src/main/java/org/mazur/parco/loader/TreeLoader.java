package org.mazur.parco.loader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;

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
    while (!operations.isEmpty()) {
      List<CommonTree> toLoad = selectOperations();
      System.out.println("Selected: " + toLoad);
      finished.addAll(toLoad);
      operations.removeAll(toLoad);
      LoadStep step = new LoadStep();
      step.setFinishedOperations(new ArrayList<CommonTree>(finished));
      result.add(step);
    }
    return result;
  }
  
}
