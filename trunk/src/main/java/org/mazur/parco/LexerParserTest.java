package org.mazur.parco;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.mazur.parco.optimize.ParcoOptimizer;
import org.mazur.parco.parser.ParcoAnalyzer;
import org.mazur.parco.parser.ParsingException;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class LexerParserTest {

  /** User characters reader. */
  private static Reader reader = new InputStreamReader(System.in, Charset.forName("UTF-8"));
  
  /** Input builder. */
  private static StringBuilder inputBuilder = new StringBuilder();
  
  private static void parse(final InputStream input) throws IOException {
    ParcoAnalyzer pa = new ParcoAnalyzer(input, Charset.forName("UTF-8"));
    boolean correct = pa.parse();
    if (correct) {
      System.out.println("OK");
      System.out.println("-----------------");
      CommonTree tree = pa.getTree();
      System.out.println(tree.toStringTree());
      System.out.println("-----------------");
      ParcoOptimizer opt = new ParcoOptimizer();
      tree = opt.optimize(tree);
      System.out.println(tree.toStringTree());
      DOTTreeGenerator generator = new DOTTreeGenerator();
      String result = generator.toDOT(tree).toString();
      System.out.println(result);
    } else {
      System.out.println("ERROR");
      List<ParsingException> errors = pa.getErrors();
      for (ParsingException e : errors) {
        System.out.println(e.getMessage());
      }
    }
  }
  
  private static String readString() {
    int cnt = 0;
    char[] buffer = new char[512];
    do {
      try {
        cnt = reader.read(buffer);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (cnt > 0) { inputBuilder.append(buffer, 0, cnt); }
      int index = inputBuilder.indexOf("\n");
      if (index >= 0) { 
        String res = inputBuilder.substring(0, index - 1);
        inputBuilder.delete(0, index + 1);
        return res;
      }
    } while (true);
  }
  
  public static void main(final String[] args) throws IOException, RecognitionException {
    System.out.println("Hello");
    do {
      System.out.println("Enter expression please");
      String input = readString();
      if (input.length() == 0) { break; } 
      parse(new ByteArrayInputStream(input.getBytes()));
    } while (true);
    System.out.println("Bye");
  }
  
  public static void print(final CommonTree tree, final int indent) {
    if (tree == null) { return; }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) { sb.append("  "); }
    for (int i = 0; i < tree.getChildCount(); i++) {
      System.out.println(sb.toString() + tree.getChild(i).toString());
      print((CommonTree)tree.getChild(i), indent + 1);
    }
  }
  
}
