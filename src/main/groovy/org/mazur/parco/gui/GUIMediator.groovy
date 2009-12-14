package org.mazur.parco.gui;


import org.mazur.parco.ParcoWorker;
import org.mazur.parco.variants.ParcoVariant;

import org.mazur.parco.loader.TreeLoader;
import org.mazur.parco.visualizer.Vizualizer;

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
  
  public void setHeightsFilter(boolean value) { worker.heightsFilter = value }
  
  public void setVariantsList(JList variantsList) {
    this.variantsList = variantsList
    this.variantsList.model = new DefaultListModel()
    this.variantsList.cellRenderer = new Renderer()
  }
  
  public void newExpression(final String expr) {
    List<ParcoVariant> vList = worker.addDistributiveVariant(expr)
    vList.each() {
      variantsList.model.addElement it
    }
    println variantsList.model.size()
  }
  
  public void showTree(final int index) {
    ParcoVariant v = (ParcoVariant)variantsList.model.getElementAt(index)
    if (!v.image) {
      v.image = Vizualizer.getImage(v.tree)
    }
    SwingBuilder.build() {
      (frame(title : v.toString(), pack : true) {
        label(icon : new ImageIcon(v.image))
      }).visible = true
    }
  }
  
  public void clearVariants() {
    variantsList.model.removeAllElements()
  }
  
  public void loadVariant(final int index, final int n, final boolean sync) {
    ParcoVariant v = (ParcoVariant)variantsList.model.getElementAt(index)
    TreeLoader loader = new TreeLoader(v.tree, n, sync)
    loader.initialize()
    LoadFrame frame = new LoadFrame()
    def loadSteps = loader.load()
    frame.show(loadSteps, n, v.tree)
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