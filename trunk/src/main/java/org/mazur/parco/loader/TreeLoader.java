package org.mazur.parco.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.parser.ParcoLexer;
import org.mazur.parco.utils.TreeHelper;

/**
 * 
 * @version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class TreeLoader {

  /** The tree root. */
  private CommonTree root;
  
  /** Count of processors. */
  private int processorsCount;
  
  /** List of operations to load. */
  private List<CommonTree> operations;
  
  private HashSet<CommonTree> finished;

  private Map<Object, Integer> numbers;
  
  /** Synchronous mode. */
  private boolean synchronous = true;
  
  public TreeLoader(final CommonTree root, final int processorsCount, final boolean synchronous) {
    this.root = root; 
    this.processorsCount = processorsCount;
    this.synchronous = synchronous;
    numbers = TreeHelper.getNodeNumbers(root);
  }
  
  public static int getWeight(final int type) {
    switch (type) {
    case ParcoLexer.PLUS: return 1;
    case ParcoLexer.MINUS: return 2;
    case ParcoLexer.MOD:
    case ParcoLexer.DIV:
    case ParcoLexer.MULT: return 5;
    case ParcoLexer.POWER: return 8;
    default: return 0;
    }
  }
  
  @SuppressWarnings("unchecked")
  public void initialize() {
    finished = new HashSet<CommonTree>();
    List<CommonTree> operations = new LinkedList<CommonTree>();
    LinkedList<CommonTree> stack = new LinkedList<CommonTree>();
    stack.push(root);
    while (!stack.isEmpty()) {
      CommonTree currentNode = stack.pop();
      if (currentNode.getChildCount() > 0) {
        operations.add(currentNode);
        for (CommonTree child : (List<CommonTree>)currentNode.getChildren()) {
          stack.push(child);
        }
      } else {
        finished.add(currentNode);
      }
    }
    this.operations = new ArrayList<CommonTree>(operations);
    System.out.println("Operations to load: " + this.operations.size());
  }
  
  private List<CommonTree> selectOperationsSynchronous(final int processorsCount) {
    TreeMap<Integer, List<CommonTree>> operationsMap = new TreeMap<Integer, List<CommonTree>>();
    for (CommonTree node : operations) {
      if (node.getChildCount() == 0 || finished.containsAll(node.getChildren())) {
        int type = node.getType();
        List<CommonTree> list = operationsMap.get(type);
        if (list == null) {
          list = new LinkedList<CommonTree>();
          operationsMap.put(type, list);
        }
        list.add(node);
      }
    }
    ArrayList<List<CommonTree>> operations = new ArrayList<List<CommonTree>>(operationsMap.values());
    Collections.sort(operations, new Comparator<List<CommonTree>>() {
      @Override
      public int compare(List<CommonTree> o1, List<CommonTree> o2) {
        return o2.size() - o1.size(); // descending
      }
    });
    List<CommonTree> result = new ArrayList<CommonTree>(processorsCount);
    while (result.size() < processorsCount && !operations.isEmpty()) {
      result.addAll(operations.remove(0));
    }
    if (result.size() > processorsCount) { result = result.subList(0, processorsCount); }
    return result;
  }
  
  private List<LoadStep> loadSync() {
    List<LoadStep> result = new LinkedList<LoadStep>();
    int lastDuration = 0;
    while (!operations.isEmpty()) {
      List<CommonTree> toLoad = selectOperationsSynchronous(processorsCount);
      int maxDuration = 0;
      Map<Integer, List<Integer>> pInfo = new HashMap<Integer, List<Integer>>();
      int p = 0;
      for (CommonTree op : toLoad) {
        int w = getWeight(op.getType());
        pInfo.put(p, Arrays.asList(w, numbers.get(op)));
        if (w > maxDuration) { maxDuration = w; }
        p++;
      }
      maxDuration += lastDuration;
      System.out.println("Selected: " + toLoad + " duration: " + maxDuration);
      finished.addAll(toLoad);
      operations.removeAll(toLoad);
      LoadStep step = new LoadStep();
      step.setFinishedOperations(new ArrayList<CommonTree>(finished));
      step.setStartTime(lastDuration);
      step.setDuration(maxDuration);
      step.setProcessorsInfo(pInfo);
      result.add(step);
      lastDuration = maxDuration;
    }
    return result;
  }
  
  private List<LoadStep> loadAsync() {
    List<LoadStep> result = new LinkedList<LoadStep>();
    int[] busyProcessors = new int[processorsCount];
    CommonTree[] tasks = new CommonTree[processorsCount];
    Arrays.fill(busyProcessors, 0);
    TreeSet<Integer> timeTicks = new TreeSet<Integer>();
    timeTicks.add(0);
    HashSet<CommonTree> loaded = new HashSet<CommonTree>();
    while (!operations.isEmpty()) {
      int time = timeTicks.pollFirst();
      int freeProcessors = 0;
      for (int i = 0; i < tasks.length; i++) {
        if (busyProcessors[i] == time && tasks[i] != null) { finished.add(tasks[i]); }
        if (busyProcessors[i] <= time) { freeProcessors++; }
      }
      if (freeProcessors == 0) { continue; }
      List<CommonTree> toLoad = selectOperationsSynchronous(freeProcessors);
      if (toLoad.isEmpty()) { continue; }
      int pIndex = 0;
      int minDuration = 100;
      Map<Integer, List<Integer>> pInfo = new HashMap<Integer, List<Integer>>();
      for (CommonTree op : toLoad) {
        int w = getWeight(op.getType());
        if (w < minDuration) { minDuration = w; }
        for (int i = pIndex; i < busyProcessors.length; i++) {
          if (busyProcessors[i] <= time) { pIndex = i; break; }
        }
        pInfo.put(pIndex, Arrays.asList(w, numbers.get(op)));
        busyProcessors[pIndex] = time + w;
        tasks[pIndex] = op;
        timeTicks.add(time + w);
        pIndex++;
      }
      
      operations.removeAll(toLoad);

      loaded.addAll(toLoad);
      LoadStep step = new LoadStep();
      step.setFinishedOperations(new HashSet<CommonTree>(loaded));
      step.setStartTime(time);
      time += minDuration;
      step.setDuration(time);
      step.setProcessorsInfo(pInfo);
      result.add(step);
    }
    return result;
  }
  
  public List<LoadStep> load() {
    return synchronous ? loadSync() : loadAsync();
  }
  
}
