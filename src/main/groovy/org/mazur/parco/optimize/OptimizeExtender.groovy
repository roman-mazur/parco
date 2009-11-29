package org.mazur.parco.optimize

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.mazur.parco.parser.ParcoLexer;
import org.mazur.parco.variants.ParcoVariator;

import static org.mazur.parco.optimize.ParcoOptimizer.*

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class OptimizeExtender {
  
  private static constructCombineSimilar(final boolean strictToCommutation, final Closure affected) {
    return { CommonTree node ->
      if (!isOperation(node)) { return null }
      if (!node.parent) { return null }
      if (strictToCommutation && node.type != ParcoLexer.PLUS && node.type != ParcoLexer.MULT) {
        return null;
      }
      if (node.type == node.parent.type) {
        int index = node.childIndex
        if (!strictToCommutation && index) { return null }
        CommonTree list = new CommonTree(null)
        node.children.each { list.addChild it }
        node.parent.replaceChildren(index, index, list)
        if (affected) { affected() }
        return null
      }
    }
  }
  
  static void extendVariator(final ParcoVariator var, final Closure affected) {
    // combine similar nodes
    var.addOptimizer 0, constructCombineSimilar(true, affected)
  }
  
  static void extend(final ParcoOptimizer opt) {

    def popupMinus = {CommonTree node ->
      Tree result = null
      if (!isOperation(node)) { return null }
      if (!node.parent) { return null }
      if (!isOperation(node.parent)) {return null }
      for (def pair in [[ParcoLexer.MINUS, ParcoLexer.PLUS], [ParcoLexer.DIV, ParcoLexer.MULT]]) {
        if (node.type == pair[0] && node.parent.type == pair[1]) {
          Tree nodeParent = node.parent 
          Tree p = nodeParent
          while (p.parent?.type == pair[1]) { p = p.parent }
          int index = node.childIndex
          Tree lowChild = node.getChild(index)
          node.parent = p.parent
          if (p.parent) { node.parent.setChild p.childIndex, node }
          node.setChild index, p
          p.parent = node
          nodeParent.setChild index, lowChild
          result = node.parent ? null : node
          break
        }
      }
      return result
    }

    // pop minus and division
    opt.addOptimizer 1, popupMinus

    // combine similar nodes
    opt.addOptimizer 2, constructCombineSimilar(false, null)

    // inverse
    opt.addOptimizer 3, { CommonTree node ->
      if (node.type != ParcoLexer.MINUS && node.type != ParcoLexer.DIV) { return null }
      int type = node.type == ParcoLexer.MINUS ? ParcoLexer.PLUS : ParcoLexer.MULT
      String text = node.type == ParcoLexer.MINUS ? "+" : "*"
      Tree upTree = new CommonTree(new CommonToken(type, text))
      def toDelete = []
      node.children.eachWithIndex { CommonTree it, index ->
        if (it.type == type || (!index && isLeaf(it))) { return }
        toDelete += it.childIndex
        upTree.addChild(it)
      }
      if (upTree.children?.size() <= 1) { return null }
      int dec = 0
      upTree.children.eachWithIndex { it, i -> 
        node.deleteChild(toDelete[i] - dec)
        it.parent = upTree
        dec++
      }
      upTree.parent = node
      node.addChild upTree
      return null
    }

    // convert to binary
    opt.addOptimizer 4, { CommonTree node ->
      if (node.childCount <= 2) { return null }
      def newChildren = [new CommonTree(node), new CommonTree(node)]
      int minCount = node.childCount / 2
      if (node.childCount % 2) { minCount++ }
      int count = node.children.size()
      node.children.eachWithIndex { child, i ->
        int index = (int)(i / minCount)
        Tree p = newChildren[index]
        child.parent = p
        p.addChild child
      }
      while (count--) { node.deleteChild(0) }
      newChildren.each { CommonTree it ->
        if (it.childCount == 1) { it = it.getChild(0) }
        node.addChild it
        it.parent = node
      }
      return null
    }
  }

}
