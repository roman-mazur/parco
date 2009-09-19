package org.mazur.parco;
import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.mazur.parco.parser.ParcoLexer;
import org.mazur.parco.parser.ParcoParser;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class LexerParserTest {

  public static void main(final String[] args) throws IOException, RecognitionException {
    ParcoLexer lexer = new ParcoLexer(new ANTLRFileStream(args[0]));
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    ParcoParser parser = new ParcoParser(tokenStream);
    parser.setTreeAdaptor(new CommonTreeAdaptor() {
      @Override
      public Object create(final Token payload) {
        System.out.println("Create " + payload);
        return new CommonTree(payload);
      }
    });
    ParcoParser.expr_return result = parser.expr();
    CommonTree tree = (CommonTree)result.getTree();
    print(tree, 0);
    System.out.println("-----------------");
    System.out.println(tree.toStringTree());
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
