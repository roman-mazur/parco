package org.mazur.parco.model;

import java.util.Comparator;

import org.antlr.runtime.tree.Tree;

/**
 * Trees comparator.
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class TreesComparator implements Comparator<Tree> {

  @Override
  public int compare(final Tree o1, final Tree o2) {
    int dif = o1.getType() - o2.getType();  
    if (dif != 0) { return dif; }
    dif = o1.toString().compareTo(o2.toString());
    if (dif != 0) { return dif; }
    dif = o1.getChildCount() - o2.getChildCount();
    if (dif != 0) { return dif; }
    if (o1.getChildCount() == 0) { return 0; }
    for (int i = 0; i < o1.getChildCount(); i++) {
      dif = compare(o1.getChild(i), o2.getChild(i));
      if (dif != 0) { return dif; }
    }
    return 0;
  }

}
