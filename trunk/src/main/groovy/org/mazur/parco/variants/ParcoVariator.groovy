package org.mazur.parco.variants;

import static org.mazur.parco.loader.TreeLoader.getWeight;
import org.mazur.parco.model.TreesComparator;

import java.util.Collections;

import org.antlr.runtime.CommonToken;

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
  
  public int getWeight(final CommonTree root) {
    Map res = [:]
    getWeights(root, res)
    return res[root]
  }
  
  public CommonTree optimizeFirst(final CommonTree tree) {
    optimizers = []
    boolean affected = true
    OptimizeExtender.extendVariator(this) { affected |= true }
    CommonTree result = tree
    while (affected) {
      affected = false
      result = super.optimize(result)
      println result.toStringTree()
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
  
  // ==========================================================================
  
  private CommonTree newDivider(final CommonTree node) {
    CommonTree result = new CommonTree(new CommonToken(ParcoLexer.DIV, "/"))
    result.addChild copy(node)
    return result
  }
  
  private Map<CommonTree, List<CommonTree>> getDistribMap(final CommonTree root) {
    if (root.type != ParcoLexer.PLUS && root.type != ParcoLexer.MINUS) { return [:] }
    if (root.getChildCount() == 0) { return [:] }
    
    def result = new TreeMap(new TreesComparator())
    def inc = { CommonTree multiplier, CommonTree e ->
      def entry = result[multiplier]
      boolean needToPut = !entry
      if (needToPut) { entry = [] }
      entry.add e
      if (needToPut) { result[multiplier] = entry }
    }
    
    for (CommonTree adder in root.getChildren()) {
      if (adder.getChildCount() == 0) {
        inc(adder, adder)
      } else {
        if (adder.type != ParcoLexer.MULT && adder.type != ParcoLexer.DIV) { continue }
        adder.getChildren().each() {
          if (it.childIndex && adder.type == ParcoLexer.DIV) {
            inc(newDivider(it), it)
          } else {
            inc(it, it) 
          }
        }
      }
    }
    return result
  }
  
  private List<CommonTree> separate(final CommonTree root, final List<CommonTree> entries) {
    List<CommonTree> result = []
    for (CommonTree adder in root.children) {
      if (!adder.childCount) {
        if (!entries.contains(adder)) { result.add adder }
        continue
      }
      if (!(entries.any() {
        adder.children.contains(it)
      })) { result.add adder }
    }
    return result
  }
  
  private CommonTree popupMultiplier(CommonTree tree, CommonTree m, final List<CommonTree> entries, List<CommonTree> sep) {
    CommonTree root = copy(tree)
    
    boolean divider = m.type == ParcoLexer.DIV && m.childCount == 1
    String text = divider ? "/" : "*"
    int type = divider ? ParcoLexer.DIV : ParcoLexer.MULT
    CommonTree newNode = new CommonTree(new CommonToken(type, text))
    println "new node: $newNode"
    int counter = 0
    // modify root
    for (CommonTree entry : entries) {
      if (entry.parent == tree) { // set '1'
        if (divider) { continue }
        CommonTree one = new CommonTree(new CommonToken(ParcoLexer.CONST, "1"))
        root.replaceChildren(entry.childIndex, entry.childIndex, one)
        counter++
      } else if (entry.parent.type == ParcoLexer.MULT) { // multiplication
        if (divider) { continue }
        CommonTree node = root.getChild(entry.parent.childIndex) 
        node.deleteChild(entry.childIndex)
        if (node.getChildCount() == 1) {
          root.replaceChildren(node.childIndex, node.childIndex, node.getChild(0))
        }
      } else { // division
        CommonTree node = root.getChild(entry.parent.childIndex) 
        if (!divider) {
          CommonTree one = new CommonTree(new CommonToken(ParcoLexer.CONST, "1"))
          node.replaceChildren(0, 0, one)
        } else {
          root.replaceChildren(node.childIndex, node.childIndex, node.getChild(0))
        }
      }
    }
    
    sep = sep.sort() { a, b -> a.childIndex <=> b.childIndex }
    def sepGroup = []
    int dec = 0
    for (CommonTree toRemove in sep) {
      int index = toRemove.childIndex - dec
      sepGroup += root.getChild(index)
      root.deleteChild index
      dec++
    }
    println "sep: $sepGroup"
    
    newNode.parent = tree.parent
    println "Setting parent ${tree.parent}"
    newNode.childIndex = tree.childIndex
    def addRoot = {
      newNode.addChild root
      root.parent = newNode
    }
    def addM = {
      def node = copy(m)
      newNode.addChild node
      node.parent = newNode
    }
    if (!divider) {
      addM(); addRoot()
    } else {
      m = m.getChild(0)
      addRoot(); addM()
    }
    
    if (!sepGroup.empty) {
      CommonTree node = new CommonTree(new CommonToken(ParcoLexer.PLUS, "+"))
      node.parent = newNode.parent
      node.childIndex = newNode.childIndex
      node.addChild(newNode)
      sepGroup.each() { node.addChild(it); it.parent = node }
      newNode = node
    }
    
    return newNode
  }
  
  private void popupAll(final CommonTree root, int index, def setResults) {
    println ">>>> $index"
    def results = []
    def distribMap = getDistribMap(root)
    println "root: $root, map: $distribMap"
    distribMap.each() {
      if (it.value.size() <= 1) { return }
      if (it.key.toString() == "1") { return }
      //println ">>>> popup for $root < $root.parent: $it.key, $it.value"
      def node = popupMultiplier(root, it.key, it.value, separate(root, it.value))
      //println ">>>> ${new ParcoVariant(tree : root).toString()}"
      //println ">>>> ${node.toStringTree()}"
      popupAll(node, index + 1) {
        if (it.empty) {
          //println ">>>> add result"
          results.add node
        } else {
          results += it
        }
      }
    }
    println ">>>>$index exit: $results"
    setResults(results)
  }
  
  private void fillVariants(final CommonTree root, CommonTree treeRoot, final def setResult) {
    println "---------------------------------"
    def result = []
    popupAll(root, 0) { rootResults ->
      for (CommonTree newRoot in (rootResults ? rootResults : [root])) {
        if (newRoot.parent) {
          if (newRoot.type == newRoot.parent.type && newRoot.type != ParcoLexer.DIV) {
            newRoot.parent.deleteChild(newRoot.childIndex)
            newRoot.children.each() { newRoot.parent.addChild it; it.parent = newRoot.parent }
          } else {
            newRoot.parent.replaceChildren(newRoot.childIndex, newRoot.childIndex, newRoot)
          }
          //println "#### have parent-> replace: ${newRoot.parent.toStringTree()}"
        } else {
          treeRoot = newRoot
          //println "#### new root: ${treeRoot.toStringTree()}"
        }
        def childrenResults = []
        if (newRoot.children) {
          for (CommonTree child in new ArrayList(newRoot.children)) {
            fillVariants(child, treeRoot) { childrenResults += it }
          }
        }
        if (!childrenResults) {
          result.add copy(treeRoot)
        } else {
          result += childrenResults
        }
      }
    }
    println "=========== $result"
    setResult(result)
  }
  
  public List<CommonTree> variantsDistributive(final CommonTree tree) {
    CommonTree root = optimizeFirst(copy(tree))
    def result = null
    fillVariants(root, root) {
      result = it.collect { optimize(it) }
    }
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
