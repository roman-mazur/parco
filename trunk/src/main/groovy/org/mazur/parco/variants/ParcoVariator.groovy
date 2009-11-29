package org.mazur.parco.variants;


import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.visualizer.Vizualizer;
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
    case ParcoLexer.MINUS: return 2
    case ParcoLexer.MOD:
    case ParcoLexer.DIV: return 8
    case ParcoLexer.MULT: return 5
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
    boolean affected = true
    OptimizeExtender.extendVariator(this) { affected |= true }
    CommonTree result = tree
    while (affected) {
      affected = false
      result = super.optimize(result) 
    }
    return result
  }

  public CommonTree optimize(final CommonTree tree) {
    initOptimizers()
    OptimizeExtender.extend(this)
    return super.optimize(tree)
  }
  
  private void sortChildren(final CommonTree root, final Map weights) {
    def children = root.children.sort() { a, b -> -(weights[a] <=> weights[b]) }
    children.eachWithIndex { CommonTree node, int index ->
      root.replaceChildren(index, index, node)
    }
  }
  
  private void permutateChildren(final CommonTree root, final CommonTree current, final Map weights, final List result) {
    if (!current.childCount) {
      return 
    }
    if (current.type != ParcoLexer.PLUS && current.type != ParcoLexer.MULT) {
      current.children.each() { permutateChildren(root, it, weights, result) }
      return
    }
    
    boolean haveOperation = current.children.inject(false) { allOperands, child -> 
      return allOperands || isOperation(child)
    }
    if (!haveOperation) { return }
    
    sortChildren(current, weights)
    
    def childrenLog = new StringBuilder()
    childrenLog << "("
    current.children.each() { childrenLog << it << " " }
    childrenLog << ")"
    println "Permutate for $current $childrenLog $current.childCount"
    
    def zones = []
    int lastIndex = 0
    int lastWeight = weights[current.getChild(0)]
    print "$lastWeight "                         
    int k = 1
    while (k < current.childCount) {
      int w = weights[current.getChild(k)]
      print "$w " 
      if (lastWeight != w) {
        IntPair ip = new IntPair(i : lastIndex, j : k - 1)
        zones += ip 
        lastIndex = k
      }
      k++
    }
    zones += new IntPair(i : lastIndex, j : k - 1)
    println "Zones: $zones"

    def childrenCopy = new ArrayList(current.children)
    def lastZones = null
    PermutationsHelper.generate(zones) { zonesPermutations ->
      println "New  permutations: $zonesPermutations"
      if (lastZones != null) { // check if operands only were exchanged 
        def changed = new ArrayList(4)
        for (int i in 0..<zonesPermutations.size()) {
          if (!zonesPermutations[i].equal(lastZones[i])) { changed += zonesPermutations[i] }
        }
        println "Changed: $changed"
        boolean trivial = !(changed.inject(false) { nt, z -> nt || !z.trivial || isOperation(childrenCopy[z.i]) })
        if (trivial) { 
          println "Trivial"
          lastZones = new ArrayList(zonesPermutations)
          return 
        }
      }
      int c = 0
      for (IntPair z in zonesPermutations) {
        for (int index in z.i..z.j) {
          current.replaceChildren(c, c, childrenCopy[index])
          c++
        }
      }
      result.add optimize(copy(root))
      current.children.each() { permutateChildren(root, it, weights, result) }
      lastZones = new ArrayList(zonesPermutations)
    }
    
  }
  
  public List<CommonTree> variants(final CommonTree tree) {
    CommonTree root = optimizeFirst(copy(tree))
    def weights = [:]
    getWeights(root, weights)
    def result = []
    permutateChildren(root, root, weights, result)
    return result
  }
  
}

class IntPair { 
  int i, j
  boolean isTrivial() { return i == j }
  String toString() { return "pair($i, $j)" }
  boolean equal(final IntPair o) {
    return (this.i == o.i) && (this.j == o.j)
  }
}
