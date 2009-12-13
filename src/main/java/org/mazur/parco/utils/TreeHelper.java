package org.mazur.parco.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.antlr.runtime.tree.Tree;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class TreeHelper {

  public static Map<Object, Integer> getNodeNumbers(final Tree tree) {
    LinkedList<Tree> stack = new LinkedList<Tree>();
    HashMap<Object, Integer> result = new HashMap<Object, Integer>();
    stack.push(tree);
    int counter = 0;
    while (!stack.isEmpty()) {
      Tree node = stack.pop();
      result.put(node, ++counter);
      for (int i = 0; i < node.getChildCount(); i++) {
        stack.add(node.getChild(i));
      }
    }
    return result;
  }
  
}
