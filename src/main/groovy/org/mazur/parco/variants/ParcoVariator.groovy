package org.mazur.parco.variants;


import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.optimize.OptimizeExtender;
import org.mazur.parco.utils.PermutationsHelper;
import org.mazur.parco.optimize.ParcoOptimizer;
import org.mazur.parco.parser.ParcoLexer;

/**
 * @author Roman Mazur (mailto:mazur.roman@gmail.com)
 */
@SuppressWarnings("unchecked")
public class ParcoVariator extends ParcoOptimizer {
  
  private int getWeight(final int type) {
    switch (type) {
    case ParcoLexer.PLUS: 
    case ParcoLexer.MINUS: return 1
    case ParcoLexer.MOD:
    case ParcoLexer.DIV: return 8
    case ParcoLexer.MULT: return 6
    case ParcoLexer.POWER: return 9
    default: return 0
    }
  }
  
  public static CommonTree copy(final CommonTree tree) {
    LinkedList<CommonTree> queue = new LinkedList<CommonTree>()
    LinkedList<CommonTree> parents = new LinkedList<CommonTree>()
    queue.addLast(tree)
    parents.addLast(null)
    CommonTree result = null
    while (!queue.empty) {
      CommonTree parent = parents.remove()
      CommonTree current = queue.remove()
      CommonTree currentCopy = new CommonTree(current)
      if (!result) { result = currentCopy }
      currentCopy.parent = parent
      if (parent) { parent.addChild(currentCopy) }
      current.children.each { 
        queue.addLast it
        parents.addLast currentCopy
      }
    }
    return result
  }
  
  private void getWeights(final CommonTree root, Map result) {
    int res = getWeight(root.token.type)
    if (root.childCount > 2) {
      res *= root.childCount - 1
    }
    root.children.each() {
      if (!result.containsKey(it)) {
        getWeights(it, result)
      }
      res += result[it]
    }
    result[root] = res
  }
  
  public CommonTree optimizeFirst(final CommonTree tree) {
    optimizers = []
    OptimizeExtender.extendVariator(this)
    return super.optimize(tree)
  }

  public CommonTree optimize(final CommonTree tree) {
    initOptimizers()
    OptimizeExtender.extend(this)
    return super.optimize(tree)
  }
  
  private void sortChildren(final CommonTree root, final Map weights) {
    def children = root.children.sort() { a, b -> -(weights[a] <=> weights[b]) }
    children.eachWithIndex { CommonTree node, int index ->
      println node
      println weights[node]
      root.replaceChildren(index, index, node)
    }
  }
  
  private void permutateChildren(final CommonTree root, final Map weights, final List result) {
    if (!root.childCount) { return }
    sortChildren(root, weights)
    result.add optimize(copy(root))
    def zones = []
    int lastIndex = 0
    int lastWeight = weights[root.getChild(0)]
    int k = 1
    while (k < root.childCount) {
      int w = weights[root.getChild(k)]
      println w
      if (lastWeight != w) {
        zoneIndexes += new IntPair(i : lastIndex, j : k - 1)
        lastIndex = k
      }
      k++
    }
    int zonesCount = zoneIndexes.size()
    PermutationsHelper.generate(zones, handler)
  }
  
  public List<CommonTree> variants(final CommonTree tree) {
    CommonTree root = optimizeFirst(copy(tree))
    def weights = [:]
    getWeights(root, weights)
    def result = []
    permutateChildren(root, weights, result)
    return result
  }
  
}

class IntPair { int i, j }
