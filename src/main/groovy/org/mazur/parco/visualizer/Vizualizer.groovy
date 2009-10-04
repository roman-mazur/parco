package org.mazur.parco.visualizer

import javax.swing.ImageIcon;

import java.io.FileOutputStream

import javax.imageio.ImageIO;
import groovy.swing.SwingBuilder;

import java.awt.Image;
import java.io.IOException

/**
 * Version: $Id$
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 */
class Vizualizer {

  private Vizualizer() {
  }
  
  public static void run(final String source, final String title) {
    FileOutputStream tempSourceOut = new FileOutputStream("temp-source.txt")
    tempSourceOut << source.bytes
    tempSourceOut.close()
    String[] args = new String[5]
    [
      "dot",
      "-Tjpg",
      "-o\"temp-result.jpg\"",
      "-Kdot",
      "temp-source.txt"
    ].eachWithIndex { v, i -> args[i] = v }
    Process dotProcess = Runtime.getRuntime().exec(args)
    int res = dotProcess.waitFor();
    if (res) { System.err.println("Visualizer error!!! " + res); return }
    Image image = ImageIO.read(new File("temp-result.jpg"))
    ImageIcon icon = new ImageIcon(image)
    SwingBuilder.build {
      (frame(title : title, pack : true) {
        label(icon : icon)
      }).visible = true
    }
  }
  
}
