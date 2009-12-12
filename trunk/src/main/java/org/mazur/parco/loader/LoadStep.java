package org.mazur.parco.loader;

import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class LoadStep {

  private Collection<CommonTree> finishedOperations;

  /**
   * @return the finishedOperations
   */
  public Collection<CommonTree> getFinishedOperations() {
    return finishedOperations;
  }

  /**
   * @param finishedOperations the finishedOperations to set
   */
  public void setFinishedOperations(Collection<CommonTree> finishedOperations) {
    this.finishedOperations = finishedOperations;
  }
  
}
