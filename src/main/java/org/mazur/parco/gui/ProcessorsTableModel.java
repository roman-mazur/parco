package org.mazur.parco.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.mazur.parco.loader.LoadStep;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class ProcessorsTableModel extends DefaultTableModel {

  private static final long serialVersionUID = 8557834894176671975L;

  private ArrayList<LoadStep> loadSteps;
  private int pCount;
  private int step = 0;
  
  public ProcessorsTableModel(final Collection<LoadStep> loadSteps, final int processorsCount) {
    this.loadSteps = new ArrayList<LoadStep>(loadSteps);
    this.pCount = processorsCount;
  }
  
  public void nextStep() {
    step++;
    fireTableDataChanged();
  }

  @Override
  public int getColumnCount() { return pCount + 1; }
  
  @Override
  public String getColumnName(final int column) { return column == 0 ? "N" : "P" + column; }
  
  @Override
  public int getRowCount() { 
    return loadSteps != null 
      ? step > 0 ? loadSteps.get(step - 1).getDuration() : 0
      : 0; }
  
  @Override
  public Object getValueAt(final int row, final int column) {
    if (column == 0) { return row + 1; }
    int p = column - 1; // processor
    int counter = 0, time = 0;
    LoadStep currentLoadStep = null;
    String number = null;
    for (LoadStep lStep : loadSteps) {
      List<Integer> info = lStep.getProcessorsInfo().get(p);
      if (info == null) { continue; }
      time += info.get(0);
      counter = lStep.getDuration();
      if (counter > row || time > row) { number = String.valueOf(info.get(1)); currentLoadStep = lStep; break; }
    }
    if (currentLoadStep != null && number != null) {
      int d = row - currentLoadStep.getStartTime();
      if (currentLoadStep.getProcessorsInfo().get(p).get(0) < d) { number = ""; }
      if (d < 0) { number = ""; }
    }
    if (number == null) { number = ""; }
    return number;
  }
  
}
