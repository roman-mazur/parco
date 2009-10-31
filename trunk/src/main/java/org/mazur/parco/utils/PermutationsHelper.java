package org.mazur.parco.utils;

import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

/**
 * This class helps to generate permutations of elements.
 */
public class PermutationsHelper<T> {
  /** Handler. */
  private PermutationHandler<T> handler;
  /** Bitmap to identify used elements. */
  private BitSet bitmap;
  /** Elements to permutate. */
  private ArrayList<T> elements;
  /** Current permutation. */
  private ArrayList<T> permutation;
  
  /** Hidden constructor. */
  private PermutationsHelper() { }
  
  /**
   * Does a change in the permutation.
   * @param iteration iteration index (recursion depth)
   */
  private void change(final int iteration) {
    if (iteration == permutation.size()) {
      handler.handle(permutation);
      return;
    }
    for (int i = bitmap.nextSetBit(0); i >= 0; i = bitmap.nextSetBit(i + 1)) {
      permutation.set(iteration, elements.get(i));
      bitmap.clear(i);
      change(iteration + 1);
      bitmap.set(i);
    };
  }
  
  /**
   * Generates permutations for the elements collection and calls a handler for the each new permutation.
   * @param <T> elements type
   * @param elements elements collection
   * @param handler permutation handler
   */
  public static <T> void generate(final Collection<T> elements, 
      final PermutationHandler<T> handler) {
    PermutationsHelper<T> helper = new PermutationsHelper<T>();
    helper.elements = new ArrayList<T>(elements);
    helper.permutation = new ArrayList<T>(elements);
    helper.handler = handler;
    helper.bitmap = new BitSet(elements.size());
    helper.bitmap.set(0, elements.size());
    helper.change(0);
  }
  
  public static <T> void generate(final Collection<T> elements, 
      final Closure closure) {
    generate(elements, new PermutationHandler<T>() {
      @Override
      public void handle(final ArrayList<T> permutation) {
        closure.call(permutation);
      }
    });
  }
    
  /**
   * Permutation handler interface.
   * @param <T> elements type
   */
  public static interface PermutationHandler<T> {
    void handle(final ArrayList<T> permutation);
  }
  
  /**
   * Starts the test.
   * @param args arguments are ignored
   */
  public static void main(final String[] args) {
    PermutationsHelper.generate(
        Arrays.asList("a", "b", "c", "d"), 
        new PermutationHandler<String>() {
          @Override
          public void handle(final ArrayList<String> permutation) {
            System.out.println(permutation);
          }
        }
    );
  }
  
}
