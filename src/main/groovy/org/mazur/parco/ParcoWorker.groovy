package org.mazur.parco;

import org.antlr.runtime.tree.CommonTree;

import org.antlr.runtime.tree.Tree;

import java.nio.charset.Charset;
import java.util.LinkedList;

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
  
  private List<ParcoVariant> variants = new LinkedList<ParcoVariant>()
  
  public ParcoWorker() {
    optimizer = new ParcoOptimizer()
    OptimizeExtender.extend(optimizer)
    variator = new ParcoVariator()
  }
  
  List<ParcoVariant> addVariant(final String expr) { return addVariant(expr, true) }

  private ParcoVariant variant(CommonTree tree) {
    DotGen dotGen = new DotGen()
    String dotStr = dotGen.toDOT(tree).toString()
    return new ParcoVariant(tree : tree, image : Vizualizer.getImage(dotStr))
  }
  
  List<ParcoVariant> addVariant(final String expr, final boolean modify) {
    InputStream input = new ByteArrayInputStream(expr.bytes)
    ParcoAnalyzer analyzer = new ParcoAnalyzer(input, Charset.forName("UTF-8"))
    if (!analyzer.parse()) {
      def errors = "" << "Errors:\n"
      analyzer.errors.each() { errors << it << "\n" }
      throw new RuntimeException(errors.toString());
    }
    CommonTree tree = analyzer.getTree()
    CommonTree original = ParcoVariator.copy(tree)
    tree = optimizer.optimize(tree)
    LinkedList<ParcoVariant> result = new LinkedList<ParcoVariant>()
    result += variant(tree)
    if (!modify) { return result }
    variator.variants(original).each() { CommonTree vt ->
      result += variant(vt)
      println "new by modify"
    }
    return result
  }
  
  public List<ParcoVariant> getVariants() { return variants }
  
}
