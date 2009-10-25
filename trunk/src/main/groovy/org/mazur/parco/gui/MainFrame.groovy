package org.mazur.parco.gui

import javax.swing.WindowConstants;

import groovy.swing.SwingBuilder;

import javax.swing.JList;
import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import java.awt.BorderLayout as BL;

import java.awt.Color;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class MainFrame {

  GUIMediator mediator
  
  /** Builder. */
  private SwingBuilder swing = new SwingBuilder()
  
  /** Frame instance. */
  private JFrame frame
  
  /** Expression field. */
  private JTextField exprField
  
  /** List. */
  JList variantsList
  
  /** Main action. */
  private def goAction = swing.action(
    name : "Go",
    shortDescription : "Start processing",
    closure : {
      mediator.newExpression(exprField.text)
    }
  )
  
  public void setVariantsList(final JList list) {
    variantsList = list
    mediator.variantsList = list
  }
  
  /**
   * Displays the new frame.
   */
  void showFrame() {
    if (frame) { 
      frame.visible = true
      return
    }
    frame = swing.frame(title : "parco", pack : true, defaultCloseOperation : WindowConstants.EXIT_ON_CLOSE) {
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
        scrollPane(constraints : BL.CENTER, size : [700, 500]) {
          variantsList = list(selectionBackground : Color.GREEN, selectionForeground : Color.RED)
          variantsList.addMouseListener new MouseClosure({ MouseEvent event ->
            if (event.clickCount == 2) {
              mediator.showTree(variantsList.locationToIndex(event.point))
            }
          })
        }
      }
    }
    frame.visible = true
  }
  
}

class MouseClosure extends MouseAdapter {
  private def c
  public MouseClosure(def c) { this.c = c }
  @Override
  public void mouseClicked(final MouseEvent e) { c(e) }
}
