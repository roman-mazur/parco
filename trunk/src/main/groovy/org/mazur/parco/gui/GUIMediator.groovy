package org.mazur.parco.gui;

import javax.swing.JTable;

import org.mazur.parco.ParcoWorker;
import org.mazur.parco.variants.ParcoVariant;
import java.awt.event.MouseEvent;

import org.mazur.parco.variants.ParcoVariator;


import org.mazur.parco.loader.LoadStep;
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
    List<ParcoVariant> vList = worker.addCommutativeVariant(expr)
    vList += worker.addDistributiveVariant(expr)
    println "------------${expr}"
    vList.each() {
      variantsList.model.addElement it
      println it
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
  
  public void model(final String expr, final int n, final boolean sync) {
    List<ParcoVariant> vList = worker.addDistributiveVariant(expr)
    List<ParcoVariant> aList = worker.addCommutativeVariant(expr)
    aList.remove 0
    vList.addAll aList
    
    int toBase
    String[] columns = ['Expr', 'To', 'Tp', 'Ka', 'Ke', 'Ka2', 'Ke2', 'Variator'].toArray()
    Object[][] data = new Object[vList.size()][]
    vList.eachWithIndex() { ParcoVariant v, int index ->
      TreeLoader loader = new TreeLoader(v.tree, n, sync)
      loader.initialize()
      def loadSteps = loader.load()
      ParcoVariator pv = new ParcoVariator()
      int to = pv.getWeight(v.tree)
      int tp = loadSteps[-1].duration
      if (index == 0) { toBase = to }
      double ka = toBase / tp
      double ke = ka / n
      double ka2 = to / tp
      double ke2 = ka2 / n
     
      data[index] = [v.toString(), to, tp, ka, ke, ka2, ke2, v.variator].toArray()
    }
    
    SwingBuilder.build {
      frame(title : 'Results', pack : true, visible : true) {
        borderLayout()
        def tbl = new JTable(data, columns)
        tbl.addMouseListener new MouseClosure({ MouseEvent event ->
          if (event.isShiftDown()) {
            int index = tbl.getRowSorter().convertRowIndexToModel(tbl.getSelectedRow())
            String e = tbl.getModel().getValueAt(index, 0)
            ParcoVariant v = vList.find { it.toString() == e }
            if (!v.image) { v.image = Vizualizer.getImage(v.tree) }
            SwingBuilder.build() {
              (frame(title : v.toString(), pack : true) {
                label(icon : new ImageIcon(v.image))
              }).visible = true
            }
          }
        })
        tbl.setAutoCreateRowSorter true
        scrollPane() {
          widget(tbl)
        }
      }
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
