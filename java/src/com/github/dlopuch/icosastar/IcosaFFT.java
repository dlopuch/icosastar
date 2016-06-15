package com.github.dlopuch.icosastar;

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

  private PApplet p;
  private Minim minim;
  private float[] fftFilter;

  public final AudioInput in;
  public final FFT fft;
  public final BeatDetect beat;

  IcosaFFT(PApplet parent) {
    this.p = parent;

    this.minim = new Minim(this);
    //minim.debugOn();

    // 44hz is 2x 22hz (nyquist on human hearing).
    // However, for music-reactive stuff, most stuff happens lower down.  Do only 3/4 of 44hz to get finer resolution
    // on the frequencies we care about
    this.in = minim.getLineIn(Minim.MONO, 1024, 44100);

    this.fft = new FFT(in.bufferSize(), in.sampleRate());
    this.fftFilter = new float[fft.specSize()];

    this.beat = new BeatDetect(in.bufferSize(), in.sampleRate());
    this.beat.setSensitivity(200);
  }

  // Move the FFT forward one cycle
  void forward() {
    this.fft.forward(in.mix);
    this.beat.detect(in.mix);

    for (int i = 0; i < this.fftFilter.length; i++) {
      this.fftFilter[i] = max(this.fftFilter[i] * DECAY, log(1 + this.fft.getBand(i)));
    }
  }

  public float[] getFilter() {
    return this.fftFilter.clone();
  }

  float[] getFilter(int numBuckets) {
    float[] filter = new float[numBuckets];
    for (int i=0; i<numBuckets; i++) {

      // Sample true FFT buckets into the numBuckets specified
      filter[i] = this.fftFilter[ (int)(map(i, 0, numBuckets, 0, this.fftFilter.length)) ];
    }

    return filter;
  }

}
