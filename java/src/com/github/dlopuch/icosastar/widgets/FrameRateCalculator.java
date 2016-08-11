package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Drawable;
import processing.core.PApplet;

/**
 * console-prints the framerate
 */
public class FrameRateCalculator implements Drawable {

  private final PApplet p;
  private final int printFrequencyMs;

  private int lastPrint;
  private int frameCount = 0;

  public FrameRateCalculator(PApplet p) {
    this(p, 1000);
  }

  public FrameRateCalculator(PApplet p, int printFrequencyMs) {
    this.p = p;
    this.printFrequencyMs = printFrequencyMs;
    lastPrint = p.millis();
  }

  public void draw() {
    frameCount++;

    int now = p.millis();
    if (now - lastPrint > printFrequencyMs) {
      PApplet.println("Frame rate: " + ((float)frameCount / ( (float)(now - lastPrint)/1000.0)) + " fps");
      lastPrint = now;
      frameCount = 0;
    }
  }
}
