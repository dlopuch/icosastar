package com.github.dlopuch.icosastar.signal;

import processing.core.PApplet;

/**
 * A Mock FFT wrapper that does no FFT processing.
 * Used to measure processing impact of doing FFT's using minim.
 */
public class MockIcosaFFT extends IcosaFFT {

  private PApplet p;
  private float[] fftFilter;

  public MockIcosaFFT(PApplet parent) {
    super(new MockBeatDetect(parent));
    this.p = parent;
    this.fftFilter = new float[1024];
  }

  @Override
  public void forward() {
    int len = this.fftFilter.length;
    int millis = p.millis();
    for (int i=0; i < len; i++) {
      this.fftFilter[i] = (float)(millis % len > i - 10 && millis % len < i + 10 ? 1.0 : 0.0);
    }
  }

  @Override
  public float[] getFilter() {
    return this.fftFilter.clone();
  }
}
