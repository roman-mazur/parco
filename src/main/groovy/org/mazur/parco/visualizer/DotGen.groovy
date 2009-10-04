package org.mazur.parco.visualizer

import java.util.LinkedList;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.DOTTreeGenerator
import org.antlr.runtime.tree.TreeAdaptor
import org.antlr.stringtemplate.StringTemplate
import org.mazur.parco.optimize.ParcoOptimizer.NodeOptimizer;


public class DotGen extends DOTTreeGenerator {

  private Map<Object, Integer> nodeToNumberMap = [:]
  private int nodeNumber = 0
                                                  
  protected int getNodeNumber(Object t) {
    Integer nI = (Integer)nodeToNumberMap.get(t);
    if ( nI!=null ) {
      return nI.intValue();
    }
    else {
      nodeToNumberMap.put(t, new Integer(nodeNumber));
      nodeNumber++;
      return nodeNumber-1;
    }
  }

  protected void toDOTDefineNodes(Object tree, TreeAdaptor adaptor, StringTemplate treeST) {
    if (tree == null) { return }

    LinkedList stack = new LinkedList()
    stack.push(tree)
    while (!stack.empty) {
      Object current = stack.pop()
      StringTemplate parentNodeST = getNodeST(adaptor, current);
      treeST.setAttribute("nodes", parentNodeST);
      int n = adaptor.getChildCount(current)
      for (int i = 0; i < n; i++) {
        Object child = adaptor.getChild(current, i);
        stack.push(child);
      }
    }
  }
  
}
