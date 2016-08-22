package com.github.dlopuch.icosastar.signal;

import static processing.core.PApplet.log;
import static processing.core.PApplet.map;
import static processing.core.PApplet.max;

import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;

public class IcosaFFT {

  private static float DECAY = 0.97f;

  private Minim minim;
  private float[] fftFilter;

  private FFT fft;

  public final AudioInput in;
  public final BeatDetect beat;

  protected IcosaFFT(BeatDetect beat) {
    this.in = null;
    this.fft = null;
    this.beat = beat;
  }

  public IcosaFFT() {

    this.minim = new Minim(this);
    //minim.debugOn();

    // 44hz is 2x 22hz (nyquist on human hearing).
    // However, for music-reactive stuff, most stuff happens lower down.  Do only 3/4 of 44hz to get finer resolution
    // on the frequencies we care about
    this.in = minim.getLineIn(Minim.MONO, 1024, 44100);

    // TODO: Turned off to get some extra processing?  No effect  :(
    //this.fft = new FFT(in.bufferSize(), in.sampleRate());
    //this.fftFilter = new float[fft.specSize()];

    this.beat = new BeatDetect(in.bufferSize(), in.sampleRate());
    this.beat.setSensitivity(200);
  }

  // Move the FFT forward one cycle
  public void forward() {
    this.beat.detect(in.mix);

    if (this.fft != null) {
      this.fft.forward(in.mix);

      for (int i = 0; i < this.fftFilter.length; i++) {
        this.fftFilter[i] = max(this.fftFilter[i] * DECAY, log(1 + this.fft.getBand(i)));
      }
    }
  }

  public float[] getFilter() {
    if (this.fft == null) {
      throw new UnsupportedOperationException("Manual FFT turned off");
    }
    return this.fftFilter.clone();
  }

  public float[] getFilter(int numBuckets) {
    float[] filter = new float[numBuckets];
    for (int i=0; i<numBuckets; i++) {

      // Sample true FFT buckets into the numBuckets specified
      filter[i] = this.fftFilter[ (int)(map(i, 0, numBuckets, 0, this.fftFilter.length)) ];
    }

    return filter;
  }

}
