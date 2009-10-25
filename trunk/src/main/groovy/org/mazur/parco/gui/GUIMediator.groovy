package org.mazur.parco.gui;

import org.mazur.parco.ParcoWorker;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class GUIMediator {

  private ParcoWorker worker = new ParcoWorker()
  
  public void newExpression(final String expr) {
    worker.addVariant(expr);
  }
  
}
