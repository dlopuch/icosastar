package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Drawable;
import com.github.dlopuch.icosastar.lx.utils.RaspiGpio;
import ddf.minim.AudioBuffer;
import processing.core.PApplet;

/**
 * console-prints the framerate
 */
public class FrameRateCalculator implements Drawable {

  private final PApplet p;
  private final int printFrequencyMs;

  private int lastPrint;
  private int frameCount = 0;

  private boolean isVerbose = false;

  public FrameRateCalculator(PApplet p) {
    this(p, 1000, false);
  }

  public FrameRateCalculator(PApplet p, int printFrequencyMs, boolean isVerbose) {
    this.p = p;
    this.printFrequencyMs = printFrequencyMs;
    lastPrint = p.millis();
    this.isVerbose = isVerbose;
  }

  public void draw() {
    frameCount++;

    int now = p.millis();
    if (now - lastPrint > printFrequencyMs) {
      if (isVerbose) {
        PApplet.println("Frame rate: " + ((float) frameCount / ((float) (now - lastPrint) / 1000.0)) + " fps");

        if (RaspiGpio.isActive()) {
          System.out.println("GPIO: toggle:" + RaspiGpio.isToggle() +
              " blackM:" + RaspiGpio.isBlackMoment() +
              " yellowM:" + RaspiGpio.isYellowMoment() +
              " resetM:" + RaspiGpio.isResetMoment() +
              " DIP: " + RaspiGpio.getDipValue()
          );
        }
      }

      lastPrint = now;
      frameCount = 0;
    }
  }
}
