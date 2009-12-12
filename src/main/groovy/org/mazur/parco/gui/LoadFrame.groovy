package org.mazur.parco.gui;

import groovy.swing.SwingBuilder;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.mazur.parco.loader.LoadStep;
import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.visualizer.Vizualizer;
import java.awt.Image;

public class LoadFrame {
  /** Swing builder. */
  private SwingBuilder swing = new SwingBuilder()
  
  private JLabel imageLabel, timeLabel, stepLabel
  
  private List<Image> loadStepImages
  private List<LoadStep> loadSteps
  
  private int stepCounter = 0
  
  private def stepAction = swing.action(
    name : "Step",
    closure : {
      if (!loadSteps) { return }
      stepCounter++
      Image stepImage = loadStepImages.remove(0)
      LoadStep step = loadSteps.remove(0)
      println "Next step"
      imageLabel.icon = new ImageIcon(stepImage)
      timeLabel.text = "$step.duration"
      stepLabel.text = "$stepCounter"
    }
  )
  
  void show(final List<LoadStep> loadSteps, int n, final CommonTree root) {
    this.loadStepImages = new ArrayList<Image>(loadSteps.size())
    this.loadSteps = loadSteps
    for (LoadStep step in loadSteps) {
      this.loadStepImages += Vizualizer.getImage(root, step)
    }
    (swing.frame(title : "Loader", pack : true) {
      borderLayout()
      panel(constraints : BorderLayout.NORTH) {
        vbox {
          label("Count of processors: $n")
          hbox() { label("Time: "); timeLabel = label() }
          hbox() { label("Step: "); stepLabel = label() }
          button(action : stepAction)
        }
      }
      panel(constraints : BorderLayout.CENTER) {
        imageLabel = label(icon : new ImageIcon(Vizualizer.getImage(root)))
      }
    }).visible = true
  }
  
}
