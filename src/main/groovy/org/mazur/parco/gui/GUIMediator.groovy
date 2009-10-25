package org.mazur.parco.gui;


import org.mazur.parco.ParcoWorker;
import org.mazur.parco.variants.ParcoVariant;

import groovy.swing.SwingBuilder;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class GUIMediator {

  private ParcoWorker worker = new ParcoWorker()
  
  JList variantsList
  
  public void setVariantsList(JList variantsList) {
    this.variantsList = variantsList
    this.variantsList.model = new DefaultListModel()
    this.variantsList.cellRenderer = new Renderer()
  }
  
  public void newExpression(final String expr) {
    List<ParcoVariant> vList = worker.addVariant(expr);
    vList.each() {
      variantsList.model.addElement it
    }
    println variantsList.model.size()
  }
  
  public void showTree(final int index) {
    ParcoVariant v = (ParcoVariant)variantsList.model.getElementAt(index)
    SwingBuilder.build() {
      (frame(title : v.toString(), pack : true) {
        label(icon : new ImageIcon(v.image))
      }).visible = true
    }
  }
  
}

class Renderer extends JLabel implements ListCellRenderer {
  public Component getListCellRendererComponent(
      JList list,
      Object value,
      int index,
      boolean isSelected,
      boolean cellHasFocus) {
    if (!(value instanceof ParcoVariant)) {
      text = value.toString()
      return
    }
    ParcoVariant v = (ParcoVariant)value
    text = v.toString()
    if (isSelected) {
      background = list.selectionBackground
      foreground = list.selectionForeground
    } else {
      background = list.background
      foreground = list.foreground
    }
    return this
  }

}
