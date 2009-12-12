package org.mazur.parco.visualizer

import org.antlr.runtime.tree.CommonTree;
import org.mazur.parco.loader.LoadStep;

import java.io.ByteArrayInputStream;

import javax.swing.ImageIcon;

import java.io.FileOutputStream

import javax.imageio.ImageIO;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import groovy.swing.SwingBuilder;

import java.awt.Image;
import java.io.IOException

/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
public class Vizualizer {

  private Vizualizer() {
  }
  
  public static Image getImage(final CommonTree tree) {
    DotGen gen = new DotGen()
    return getImage(gen.toDOT(tree).toString())
  }
  
  public static Image getImage(final CommonTree tree, final LoadStep loadStep) {
    DotGen gen = new DotGen()
    return getImage(gen.toDOT(tree, loadStep).toString())
  }

  public static Image getImage(final String source) {
    println source
    String[] args = new String[3]
    [
      "dot",
      "-Tjpg",
      "-Kdot",
    ].eachWithIndex { v, i -> args[i] = v }
    Process dotProcess = Runtime.getRuntime().exec(args)
    InputStream resultStream = dotProcess.inputStream
    byte[] imageBytes
    Thread reader = new CThread({
      byte[] buf = new byte[4096]
      ByteArrayOutputStream out = new ByteArrayOutputStream()
      int cnt = 0
      while (cnt >= 0) {
        cnt = resultStream.read(buf)
        if (cnt > 0) { out.write(buf, 0, cnt) }
      }
      imageBytes = out.toByteArray()
    })
    reader.start()
    dotProcess.outputStream.write(source.bytes)
    dotProcess.outputStream.close()
    
    println "Waiting for dot..."
    int res = dotProcess.waitFor();
    if (res) { System.err.println("Visualizer error!!! " + res); return }
    reader.join()
    println "Done"
    return ImageIO.read(new ByteArrayInputStream(imageBytes))
  }
  
  public static void run(final def source, final String title) {
    Image image = getImage(source)
    ImageIcon icon = new ImageIcon(image)
    SwingBuilder.build {
      (frame(title : title, pack : true) {
        label(icon : icon)
      }).visible = true
    }
  }
  
}

class CThread extends Thread {
  private def c
  public CThread(def c) {
    this.c = c
  }
  public void run() { c() }
}
