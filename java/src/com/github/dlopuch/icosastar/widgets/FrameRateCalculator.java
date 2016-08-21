package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Drawable;
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

  private AudioBuffer audio;
  private float maxAudioLevel;

  public FrameRateCalculator(PApplet p) {
    this(p, 1000, null);
  }

  public FrameRateCalculator(PApplet p, int printFrequencyMs, AudioBuffer audio) {
    this.p = p;
    this.printFrequencyMs = printFrequencyMs;
    this.audio = audio;
    lastPrint = p.millis();
  }

  public void draw() {
    frameCount++;

    if (audio != null) {
      maxAudioLevel = Math.max(maxAudioLevel, audio.level());
    }

    int now = p.millis();
    if (now - lastPrint > printFrequencyMs) {
      PApplet.println("Frame rate: " + ((float)frameCount / ( (float)(now - lastPrint)/1000.0)) + " fps");
      lastPrint = now;
      frameCount = 0;

      PApplet.println("Max audio level:" + maxAudioLevel);
      maxAudioLevel = 0;
    }
  }
}
