package org.mazur.parco.gui

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout as BL

import groovy.swing.SwingBuilder;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class MainFrame {

  /** Builder. */
  private SwingBuilder swing = new SwingBuilder()
  
  /** Frame instance. */
  private JFrame frame
  
  /** Expression field. */
  private JTextField exprField
  
  /** Main action. */
  private def goAction = swing.action() {
    name : "Go",
    shortDescription : "Start processing",
    closure : {
      println exprField.text
    }
  }
  
  /**
   * Displays the new frame.
   */
  void showFrame() {
    if (frame) { 
      frame.visible = true
      return
    }
    frame = swing.frame(title : "parco", pack : true) {
      borderLayout()
      panel(constraints : BL.NORTH) {
        borderLayout()
        label(text : "Enter an expression", constraints : BL.NORTH)
        panel() {
          borderLayout()
          exprField = textField(constraints : BL.CENTER, action : goAction)
          button(text : "Go", constraints : BL.EAST, action : goAction)
        }
      }
      panel(constraints : BL.CENTER) {
        borderLayout()
        label(text : "Variants", constraints : BL.NORTH)
        list(constraints : BL.CENTER)
      }
    }
    frame.visible = true
  }
  
}
