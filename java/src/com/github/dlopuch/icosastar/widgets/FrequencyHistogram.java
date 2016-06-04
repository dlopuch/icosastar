package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Config;
import com.github.dlopuch.icosastar.IcosaFFT;
import processing.core.PApplet;

/**
 * Renders Frequency spectrum histogram
 */
public class FrequencyHistogram {
  private final PApplet p;
  private final IcosaFFT icosaFFT;

  public int SPECTRUM_BUCKET_WIDTH_PX = 1;
  public int SPECTRUM_HEIGHT = 20;


  public FrequencyHistogram(PApplet p, IcosaFFT icosaFFT) {
    this.p = p;
    this.icosaFFT = icosaFFT;

    p.registerDraw(this);
  }

  public void draw() {
    p.pushStyle();

    p.noStroke();
    p.fill(255);

    float[] fftSpectrum = icosaFFT.getFilter();
    for (int i=0; i<fftSpectrum.length; i++) {
      p.rect(
          // 0,0 is center, transform to SIDE/2
          -Config.SIDE/2 + i * SPECTRUM_BUCKET_WIDTH_PX,
          Config.SIDE/2,
          SPECTRUM_BUCKET_WIDTH_PX,
          -fftSpectrum[i] * SPECTRUM_HEIGHT
      );
    }

    p.popStyle();
  }
}
