package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Config;
import com.github.dlopuch.icosastar.IcosaFFT;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PImage;

import static processing.core.PConstants.RGB;

/**
 * Spectograph of frequencies over time
 */
public class FrequencySpectograph {
  private final PApplet p;
  private final FFT fft;

  private int[][] spectograph;
  private int currentRowI = 0;
  private int numMillisPerSample;
  private int lastSampleMillis;

  private PImage spectographImg;

  /** Approximate time period of the spectrograph (might get longer if framerate doesn't keep up with draw samples) */
  public int TIME_PERIOD_MS = 2000;

  /**
   * How high to render it.  Also determines how many samples are taken -- one px of height is one sample.
   * */
  public int HEIGHT_PX = 295;

  public FrequencySpectograph(PApplet p, FFT fft) {
    this.p = p;
    this.fft = fft;

    p.registerDraw(this);
  }

  public void init() {
    numMillisPerSample = TIME_PERIOD_MS / HEIGHT_PX;
    lastSampleMillis = 0;

    spectograph = new int[HEIGHT_PX][fft.specSize()];
    spectographImg = p.createImage(fft.specSize(), HEIGHT_PX, RGB);

    for (int r=0; r<HEIGHT_PX; r++) {
      for (int c=0; c<fft.specSize(); c++) {
        spectograph[r][c] = p.color(0f);
      }
    }
  }

  public void draw() {
    if (spectograph == null)
      throw new RuntimeException("Looks like you forgot to .init()!");

    if (p.millis() - lastSampleMillis > numMillisPerSample) {
      takeSample();
      renderSpectograph();

      lastSampleMillis = p.millis();
    }

    p.image(spectographImg, -Config.SIDE/2, Config.SIDE/2 - HEIGHT_PX);

  }

  private void takeSample() {
    currentRowI = (currentRowI + 1) % HEIGHT_PX;

    p.pushStyle();
    p.colorMode(RGB, 2f);

    for (int i=0; i<fft.specSize(); i++) {
      spectograph[currentRowI][i] = p.color(fft.getBand(i));
    }

    p.popStyle();
  }

  private void renderSpectograph() {
    spectographImg.loadPixels();

    int px = 0;
    for (int r=0; r < HEIGHT_PX; r++) {
      int realRowI = (currentRowI + r) % HEIGHT_PX;
      for (int c=0; c < fft.specSize(); c++) {
        spectographImg.pixels[px] = spectograph[realRowI][c];
        px++;
      }
    }

    spectographImg.updatePixels();
  }



}
