package org.mazur.parco;

import org.antlr.runtime.tree.CommonTree;

import org.antlr.runtime.tree.Tree;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import java.io.ByteArrayInputStream;

import org.mazur.parco.optimize.OptimizeExtender;
import org.mazur.parco.parser.ParcoAnalyzer;
import java.io.InputStream;

import org.mazur.parco.optimize.ParcoOptimizer;
import org.mazur.parco.variants.ParcoVariator;
import org.mazur.parco.variants.ParcoVariant;
import org.mazur.parco.visualizer.DotGen;
import org.mazur.parco.visualizer.Vizualizer;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class ParcoWorker {

  /** Optimizer. */
  private ParcoOptimizer optimizer
  
  /** Variator. */
  private ParcoVariator variator
  
  private HashSet<String> variantsSet = new HashSet<String>()
  private HashSet<Integer> heightsSet = new HashSet<Integer>()
  
  boolean heightsFilter = false
  
  public ParcoWorker() {
    optimizer = new ParcoOptimizer()
    OptimizeExtender.extend(optimizer)
    variator = new ParcoVariator()
  }
  
  List<ParcoVariant> addVariant(final String expr) { return addVariant(expr, true) }

  private int height(final CommonTree tree) {
    if (!tree.childCount) { return 1 }
    int h = 0
    tree.children.each() {
      int c = height(it)
      if (h < c) { h = c }
    }
    return h + 1
  }
  
  private ParcoVariant variant(CommonTree tree) {
    ParcoVariant result = new ParcoVariant(tree : tree)
    String key = result.toString()
    if (variantsSet.contains(key)) { return null }
    int h = height(tree)
    println "Height: $h"
    if (heightsFilter && heightsSet.contains(h)) { return null }
    heightsSet += h
    variantsSet += key
    //result.image = Vizualizer.getImage(tree)
    return result 
  }
  
  List<ParcoVariant> addVariant(final String expr, final boolean modify) {
    variantsSet.clear()
    heightsSet.clear()
    InputStream input = new ByteArrayInputStream(expr.bytes)
    ParcoAnalyzer analyzer = new ParcoAnalyzer(input, Charset.forName("UTF-8"))
    if (!analyzer.parse()) {
      def errors = "" << "Errors:\n"
      analyzer.errors.each() { errors << it << "\n" }
      throw new RuntimeException(errors.toString());
    }
    LinkedList<ParcoVariant> result = new LinkedList<ParcoVariant>()
    CommonTree tree = analyzer.getTree()
    tree = optimizer.optimize(tree)
    CommonTree treeCopy = ParcoVariator.copy(tree)
    result += variant(tree)
    if (!modify) { return result }
    variator.variants(treeCopy).each() { CommonTree vt ->
      //def t = optimizer.optimize(vt)
      ParcoVariant v = variant(vt)
      if (v != null) { result += v }
    }
    return result
  }
  
  public List<ParcoVariant> getVariants() { return variants }
  
}
