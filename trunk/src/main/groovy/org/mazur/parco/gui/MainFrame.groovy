package org.mazur.parco.gui

import javax.swing.WindowConstants;

import groovy.swing.SwingBuilder;

import javax.swing.JList;
import javax.swing.JCheckBox;
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
  
  /** Heights filter checkbox. */
  private JCheckBox heightsFilterCB
  
  /** List. */
  JList variantsList
  
  /** Count of processors. */
  private JTextField processorsField
  
  private JCheckBox syncBox
  
  /** Main action. */
  private def goAction = swing.action(
    name : "Go",
    shortDescription : "Start processing",
    closure : {
      mediator.newExpression(exprField.text)
    }
  )
  
  /** Heights filter action. */
  private def heigtsFilterAction = swing.action(
    name : "Hights filter",
    shortDescription : "Enable/disable heights filter",
    closure : {
      mediator.heightsFilter = heightsFilterCB.selected
    }
  )
  
  private def clearAction = swing.action(
    name : "Clean variants",
    shortDescription : "Remove all variants",
    closure : {
      mediator.clearVariants()
    }
  )
  
  private def loadAction = swing.action(
    name : "Load variant",
    shortDescription : "Load the selected variant",
    closure : {
      mediator.loadVariant(variantsList.selectedIndex, Integer.parseInt(processorsField.text), syncBox.selected)
    }
  )
  
  private def modelAction = swing.action(
    name : "Model",
    shortDescription : "Run all variants",
    closure : {
      mediator.model(exprField.text, Integer.parseInt(processorsField.text), syncBox.selected)
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
        panel(constraints : BL.CENTER) {
          borderLayout(constraints : BL.CENTER)
          exprField = textField(constraints : BL.CENTER, action : goAction, text : "a*b+a*c+d*b+c*d + e1*t + e1*r + e2*t+e2*r") //"a+b+b*d*a+e5/(e2*t+e3*t+e3+t)+(e4*e1+e5*e1)*(e7+e8*e9)")
          panel(constraints : BL.EAST) {
            button(action : goAction)
            button(action : clearAction)
            button(action : modelAction)
          }
        }
        heightsFilterCB = checkBox(action : heigtsFilterAction, constraints : BL.SOUTH)
      }
      panel(constraints : BL.CENTER) {
        borderLayout()
        label(text : "Variants", constraints : BL.NORTH)
        scrollPane(constraints : BL.CENTER, size : [700, 500]) {
          variantsList = list(selectionBackground : Color.GREEN, selectionForeground : Color.RED, foreground : Color.GRAY)
          variantsList.addMouseListener new MouseClosure({ MouseEvent event ->
            if (event.clickCount == 2) {
              mediator.showTree(variantsList.locationToIndex(event.point))
            }
          })
        }
      }
      panel(constraints : BL.SOUTH) {
        borderLayout()
        label(text : "Number of processors", constraints : BL.WEST)
        processorsField = textField(text : "3", action : loadAction, constraints : BL.CENTER)
        hbox(constraints : BL.EAST) {
          syncBox = checkBox("Synchronous")
          button(action : loadAction)
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
