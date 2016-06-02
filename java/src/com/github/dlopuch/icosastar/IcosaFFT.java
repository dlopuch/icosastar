package com.github.dlopuch.icosastar;

import ddf.minim.analysis.*;
import ddf.minim.*;
import processing.core.PApplet;

import java.io.InputStream;


public class IcosaFFT {

  private static float DECAY = 0.97f;

  private PApplet p;
  private Minim minim;
  private AudioInput in;
  private FFT fft;
  private float[] fftFilter;

  public BeatDetect beat;

  IcosaFFT(PApplet parent) {
    this.p = parent;

    this.minim = new Minim(this);

    // Small buffer size!
    this.in = minim.getLineIn();

    this.fft = new FFT(in.bufferSize(), in.sampleRate());
    this.fftFilter = new float[fft.specSize()];

    this.beat = new BeatDetect(this.in.bufferSize(), 44100);
    this.beat.setSensitivity(200);
  }

  // Move the FFT forward one cycle
  void forward() {
    this.fft.forward(in.mix);
    this.beat.detect(in.mix);

    for (int i = 0; i < this.fftFilter.length; i++) {
      this.fftFilter[i] = p.max(this.fftFilter[i] * DECAY, p.log(1 + this.fft.getBand(i)));
    }
  }

  float[] getFilter() {
    return this.fftFilter.clone();
  }

  float[] getFilter(int numBuckets) {
    float[] filter = new float[numBuckets];
    for (int i=0; i<numBuckets; i++) {

      // Sample true FFT buckets into the numBuckets specified
      filter[i] = this.fftFilter[ (int)(p.map(i, 0, numBuckets, 0, this.fftFilter.length)) ];
    }

    return filter;
  }

}
