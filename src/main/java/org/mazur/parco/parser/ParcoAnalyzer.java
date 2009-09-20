package org.mazur.parco.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

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
    lexer = new ParcoLexer(new ANTLRReaderStream(reader));
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
    parser = new Parser(tokenStream);
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
  }
}
