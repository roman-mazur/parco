package org.mazur.parco.variants;

import java.awt.Image;

import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.parser.ParcoLexer;

public class ParcoVariant {

  CommonTree tree
  
  Image image
  
  String str
  
  String variator
  
  private String getString(final CommonTree tree) {
    if (!tree) { return '' }
    if (tree.token.type == ParcoLexer.IDENTIFIER || tree.token.type == ParcoLexer.CONST) {
      return tree.toString()
    }
    StringBuilder sb = new StringBuilder()
    int cIndex = 0
    if (tree.childCount == 2) {
      sb << getString(tree.getChild(0))
      cIndex = 1
    }
    sb << ' ' << tree.toString() << ' ' << getString(tree.getChild(cIndex))
    return '(' + sb.toString() + ')'
  }
  
  @Override
  String toString() {
    if (!str) { str = getString(tree) }
    return str
  }
  
}
