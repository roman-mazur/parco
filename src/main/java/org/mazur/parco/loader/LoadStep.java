package org.mazur.parco.loader;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class LoadStep {

  private Collection<CommonTree> finishedOperations;

  private Map<Integer, List<Integer>> processorsInfo;
  
  /** Duration. */
  private int duration, startTime;
  
  /**
   * @return the startTime
   */
  public int getStartTime() {
    return startTime;
  }

  /**
   * @param startTime the startTime to set
   */
  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }

  /**
   * @return the processorsInfo
   */
  public Map<Integer, List<Integer>> getProcessorsInfo() {
    return processorsInfo;
  }

  /**
   * @param processorsInfo the processorsInfo to set
   */
  public void setProcessorsInfo(final Map<Integer, List<Integer>> processorsInfo) {
    this.processorsInfo = processorsInfo;
  }

  /**
   * @return the duration
   */
  public int getDuration() {
    return duration;
  }

  /**
   * @param duration the duration to set
   */
  public void setDuration(int duration) {
    this.duration = duration;
  }

  /**
   * @return the finishedOperations
   */
  public Collection<CommonTree> getFinishedOperations() {
    return finishedOperations;
  }

  /**
   * @param finishedOperations the finishedOperations to set
   */
  public void setFinishedOperations(Collection<CommonTree> finishedOperations) {
    this.finishedOperations = finishedOperations;
  }
  
}
