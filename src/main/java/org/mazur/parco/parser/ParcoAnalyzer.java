package org.mazur.parco.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.mazur.parco.model.ConstantNode;
import org.mazur.parco.model.DoubleOperationNode;
import org.mazur.parco.model.VariableNode;

/**
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */
public class ParcoAnalyzer {

  /** Lexer. */
  private ParcoLexer lexer;
  /** Parser. */
  private ParcoParser parser;
  
  /** Result tree. */
  private CommonTree result;
  
  public ParcoAnalyzer(final InputStream input, final Charset charset) throws IOException {
    Reader reader = new InputStreamReader(input, charset);
    lexer = new Lexer(new ANTLRReaderStream(reader));
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    parser = new Parser(tokenStream);
//    parser.setTreeAdaptor(new CommonTreeAdaptor() {
//      @Override
//      public Object create(final Token payload) {
//        switch (payload.getType()) {
//        case ParcoLexer.CONST: return new ConstantNode(payload);
//        case ParcoLexer.IDENTIFIER: return new VariableNode(payload);
//        case ParcoLexer.DIV:
//        case ParcoLexer.MINUS:
//        case ParcoLexer.MOD:
//        case ParcoLexer.MULT:
//        case ParcoLexer.PLUS:
//        case ParcoLexer.POWER:
//          return new DoubleOperationNode(payload);
//        default: return null;
//        }
//      }
//    });
  }

  /**
   * Main method for parsing.
   */
  public boolean parse() {
    try {
      ParcoParser.prog_return ret = parser.prog();
      result = (CommonTree)ret.getTree();
      return parser.isCorrect();
    } catch (RecognitionException e) {
      System.err.println("FATAL: unhandled error");
      throw new RuntimeException(e);
    } catch (RuntimeException e) {
      return false;
    }
  }
  
  public CommonTree getTree() { return result; }
  
  public List<ParsingException> getErrors() { return parser.getExceptions(); }
  
  /**
   * Subclass to catch all errors.
   * @author Roman Mazur (mailto: mazur.roman@gmail.com)
   */
  private static class Parser extends ParcoParser {
    public Parser(final TokenStream input) {
      super(input);
    }
    public Parser(final TokenStream input, final RecognizerSharedState state) {
      super(input, state);
    }
    @Override
    protected Object recoverFromMismatchedToken(final IntStream input, final int ttype,
        final BitSet follow) throws RecognitionException {
      throw new MismatchedTokenException(ttype, input);
    }
    @Override
    public Object recoverFromMismatchedSet(final IntStream input,
        final RecognitionException e, final BitSet follow) throws RecognitionException {
      return new MismatchedTokenException(e.token.getType(), input);
    }
  }
  
  /**
   * @author Roman Mazur (mailto: mazur.roman@gmail.com)
   */
  private static class Lexer extends ParcoLexer {
    public Lexer(final CharStream input) {
      this(input, new RecognizerSharedState());
    }
    public Lexer(CharStream input, RecognizerSharedState state) {
      super(input,state);
    }
    @Override
    protected Object recoverFromMismatchedToken(IntStream input, int ttype,
        BitSet follow) throws RecognitionException {
      throw new MismatchedTokenException(ttype, input);
    }
  }
}
