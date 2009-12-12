package org.mazur.parco.visualizer

import java.util.LinkedList;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.DOTTreeGenerator
import org.antlr.runtime.tree.TreeAdaptor
import org.antlr.stringtemplate.StringTemplate
import org.mazur.parco.loader.LoadStep;
import org.mazur.parco.optimize.ParcoOptimizer.NodeOptimizer;


public class DotGen extends DOTTreeGenerator {

  private Map<Object, Integer> nodeToNumberMap = [:]
  private int nodeNumber = 0

  public static StringTemplate _nodeST =
    new StringTemplate('$name$ [label=\"$text$\", shape=$shape$, color=$color$, fillcolor=$fillcolor$, fontcolor=$fontcolor$];\n');
  
  private LoadStep loadStep = null
  
  protected int getNodeNumber(Object t) {
    Integer nI = (Integer)nodeToNumberMap.get(t);
    if ( nI!=null ) {
      return nI.intValue();
    } else {
      nodeToNumberMap.put(t, new Integer(nodeNumber));
      nodeNumber++;
      return nodeNumber-1;
    }
  }

  public StringTemplate toDOT(final Tree tree, final LoadStep loadStep) {
    this.loadStep = loadStep
    return toDOT(tree)
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
  
  protected StringTemplate getNodeST(final TreeAdaptor adaptor, final Object t) {
    String text = adaptor.getText(t);
    StringTemplate nodeST = _nodeST.getInstanceOf();
    String uniqueName = "n"+getNodeNumber(t);
    nodeST.setAttribute("name", uniqueName);

    nodeST.setAttribute("text", fixString(text));
    
    String fillColor = 'white'
    String color = 'black'
    String fontColor = 'black'
    String shape = 'circle'
    
    if (((Tree)t).getChildCount() == 0) { shape = 'box'; fillColor = 'grey' }
    if (loadStep) {
      if (loadStep.getFinishedOperations().contains(t) && ((Tree)t).getChildCount() > 0) {
        fillColor = 'green'
      }
    }
      
    nodeST.setAttribute("color", color);
    nodeST.setAttribute("fillcolor", fillColor);
    nodeST.setAttribute("fontcolor", fontColor);
    nodeST.setAttribute("shape", shape);
    return nodeST;
  }
  
}
