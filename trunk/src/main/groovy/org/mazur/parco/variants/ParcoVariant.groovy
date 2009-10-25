package org.mazur.parco.variants;

import org.antlr.runtime.tree.CommonTree;
import java.awt.Image;

public class ParcoVariant {

  CommonTree tree
  
  Image image;
  
  private String str
  
  private String getStr() {
    return ''
  }
  
  @Override
  String toString() {
    if (!str) { str = getStr() }
    return str
  }
  
}
