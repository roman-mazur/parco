package org.mazur.parco.gui;

import groovy.swing.SwingBuilder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import org.mazur.parco.loader.LoadStep;
import org.mazur.parco.visualizer.Vizualizer;
import java.awt.Image;

public class LoadFrame {
  /** Swing builder. */
  private SwingBuilder swing = new SwingBuilder()
  
  private JLabel imageLabel
  
  private List<Image> loadSteps
  
  private def stepAction = swing.action() {
    name : "Step",
    closure : {
      if (!loadSteps) { return }
      Image stepImage = loadSteps.remove(0)
      
    }
  }
  
  void show(final List<LoadStep> loadSteps, int n, final CommonTree root) {
    this.loadSteps = new ArrayList<Image>(loadSteps.size())
    for (LoadStep step in loadSteps) {
      this.loadSteps += Vizualizer.getImage(root)
    }
    (swing.frame(title : "Loader", pack : true) {
      borderLayout()
      panel(constraints : BorderLayout.NORTH) {
        vbox {
          label("Count of processors: $n")
          button(action : stepAction)
        }
      }
      panel(constraints : BorderLayout.CENTER) {
        imageLabel = label(icon : new ImageIcon(Vizualizer.getImage(root)))
      }
    }).visible = true
  }
  
}
