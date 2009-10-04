package org.mazur.parco

import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import org.mazur.parco.parser.ParcoLexer
import org.mazur.parco.parser.ParcoAnalyzer
import org.mazur.parco.optimize.ParcoOptimizer
import org.mazur.parco.optimize.OptimizeExtender
import org.mazur.parco.visualizer.Vizualizer
import org.mazur.parco.visualizer.DotGen
import org.antlr.runtime.tree.CommonTree
import org.antlr.runtime.tree.DOTTreeGenerator

/** User characters reader. */
Reader reader = new InputStreamReader(System.in, Charset.forName("UTF-8"));

/** Input builder. */
StringBuilder inputBuilder = new StringBuilder();

/** DOT format generator. */
DOTTreeGenerator dotGenerator = new DotGen();

/** Optimizer. */
ParcoOptimizer opt = new ParcoOptimizer();
OptimizeExtender.extend opt

def readString = {
  int cnt = 0;
  char[] buffer = new char[512];
  while (true) {
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
  }
}

def parse = { InputStream input ->
  ParcoAnalyzer pa = new ParcoAnalyzer(input, Charset.forName("UTF-8"))
  boolean correct = pa.parse()
  if (correct) {
    println "OK"
    println "-----------------"
    CommonTree tree = pa.getTree();
    println "Generating images..."
    Vizualizer.run(dotGenerator.toDOT(tree).toString(), "Source")
    println tree.toStringTree()
    tree = opt.optimize(tree);
    println "Done"
  } else {
    System.out.println("ERROR");
    List errors = pa.getErrors();
    errors.each { println it.message }
  }
}

println "Hello!"
while (true) {
  println "Enter expression please"
  String input = readString()
  if (!input.length()) { break }
  parse(new ByteArrayInputStream(input.bytes))
}
println "Bye"