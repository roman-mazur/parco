package org.mazur.parco.gui;

import groovy.swing.SwingBuilder;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JLabel;
import org.mazur.parco.loader.LoadStep;
import org.mazur.parco.loader.TreeLoader;
import org.mazur.parco.parser.ParcoLexer;
import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.variants.ParcoVariator;
import org.mazur.parco.visualizer.Vizualizer;
import java.awt.Image;

public class LoadFrame {
  /** Swing builder. */
  private SwingBuilder swing = new SwingBuilder()
  
  private JLabel imageLabel, timeLabel, stepLabel
  
  private ProcessorsTableModel tableModel
  
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
      tableModel.nextStep()
    }
  )
  
  void show(final List<LoadStep> loadSteps, int n, final CommonTree root) {
    this.loadStepImages = new ArrayList<Image>(loadSteps.size())
    this.loadSteps = loadSteps
    for (LoadStep step in loadSteps) {
      this.loadStepImages += Vizualizer.getImage(root, step)
    }
    
    tableModel = new ProcessorsTableModel(loadSteps, n)
    
    (swing.frame(title : "Loader", pack : true) {
      borderLayout()
      panel(constraints : BorderLayout.NORTH) {
          hbox() {
            vbox() {
              label("Count of processors: $n   ")
              hbox() { label("Time: "); timeLabel = label() }
              hbox() { label("Step: "); stepLabel = label() }
              button(action : stepAction)
            }
            vbox() {
              ParcoVariator pv = new ParcoVariator()
              int to = pv.getWeight(root)
              int tp = loadSteps[loadSteps.size() - 1].duration
              label("To: $to  ")
              label("Tp: $tp  ")
              label("Ka: ${to/tp}  ")
              label("Ke: ${to/tp/n}  ")
            }
            vbox() {
              label("  Operations:")
              label("  + " + TreeLoader.getWeight(ParcoLexer.PLUS))
              label("  - " + TreeLoader.getWeight(ParcoLexer.MINUS))
              label("  * " + TreeLoader.getWeight(ParcoLexer.MULT))
              label("  / " + TreeLoader.getWeight(ParcoLexer.DIV))
              label("  % " + TreeLoader.getWeight(ParcoLexer.MOD))
              label("  ** " + TreeLoader.getWeight(ParcoLexer.POWER))
            }
          }
      }
      splitPane(
        leftComponent : (panel(constraints : BorderLayout.CENTER) {
          borderLayout()
          scrollPane() {
            imageLabel = label(icon : new ImageIcon(Vizualizer.getImage(root)))
          }
        }),
        rightComponent : (panel(constraints : BorderLayout.EAST) {
          borderLayout()
          scrollPane() {
            widget(new JTable(tableModel))
          }
        })
      )
    }).visible = true
  }
  
}
