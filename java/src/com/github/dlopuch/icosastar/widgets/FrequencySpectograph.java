package com.github.dlopuch.icosastar.widgets;

import com.github.dlopuch.icosastar.Config;
import ddf.minim.AudioInput;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PImage;

import static processing.core.PConstants.RGB;

/**
 * Spectograph of frequencies over time
 */
public class FrequencySpectograph {
  private final PApplet p;
  private final FftSupplier fft;

  private int[][] spectograph;
  private int currentRowI = 0;
  private int numMillisPerSample;
  private int lastSampleMillis;

  private PImage spectographImg;

  /** Approximate time period of the spectrograph (might get longer if framerate doesn't keep up with draw samples) */
  public int TIME_PERIOD_MS = 1000;

  /**
   * How high to render it.  Also determines how many samples are taken -- one px of height is one sample.
   * */
  public int HEIGHT_PX = 295;

  // ff.getBand(i) seems to go up to about 230ish if you tap the mic.
  // Under normal speaking volume, seems to go 30ish.
  // Point is, it seems to be logarithmic
  // We're going to take log of the signal, so max color range should be Math.log() of the expected max.
  // Math.log(200) = 5.2
  // Math.log(50) = 3.9

  /**
   * Threshold level of FFT band before spectograph color maxes out at white.
   * Normal speaking volume seems to be around 30ish.
   * Max seems to be in low to mid 200's.
   */
  public double WHITE_THRESHOLD = 30;

  /**
   * The mic values seem to scale logarithmically -- eg speaking levels seems to be 30, max volume seems to be
   * mid 200's).
   *
   * When set to true, spectograph uses a log transform for the color scale.
   * eg when true, if WHITE_THRESHOLD set to 128, 50% grey is ~11.3 (b/c Math.log(11.3) ~= Math.log(128)/2 ).
   * When false, 50% grey would be 64.  False effectively skews coloring towards loud noises.
   */
  public boolean USE_LOG_SCALE = true;

  /**
   * Scale factor for width. 1 = 1px per band, 2 = 2px per band, etc.  Use getter/setter to change
   */
  private int widthScale = 1;

  /**
   * Spectrograph input that renders a full FFT spectrum
   */
  public static class SpectrumFftSupplier extends FftSupplier {
    private FFT fft;

    public SpectrumFftSupplier(FFT fft) {
      this.fft = fft;
    }

    public void forward() {
      // no-op: assuming supplied FFT is forward'ing outside of here
    }

    public int getNumBands() {
      return fft.specSize();
    }

    public float getBand(int i) {
      return fft.getBand(i);
    }
  }

  /**
   * Spectrograph input that creates an FFT grouping bands into octaves (see minim's FourierTransform)
   */
  public static class OctaveFftSupplier extends FftSupplier {
    private FFT fft;
    private AudioInput in;

    public OctaveFftSupplier(AudioInput in) {
      this(in, 60, 3); // minBandwidth & bandsPerOctave settings used in minim's BeatDetect
    }

    public OctaveFftSupplier(AudioInput in, int minBandwidth, int bandsPerOctave) {
      this.in = in;
      this.fft = new FFT(in.bufferSize(), in.sampleRate());
      fft.logAverages(minBandwidth, bandsPerOctave);
    }

    public void forward() {
      fft.forward(in.mix);
    }

    public int getNumBands() {
      return fft.avgSize();
    }

    public float getBand(int i) {
      return fft.getAvg(i);
    }
  }

  private static abstract class FftSupplier {
    public abstract void forward();
    public abstract int getNumBands();
    public abstract float getBand(int i);
  }

  public FrequencySpectograph(PApplet p, FftSupplier fft) {
    this.p = p;
    this.fft = fft;

    p.registerMethod("draw", this);
  }

  public void init() {
    numMillisPerSample = TIME_PERIOD_MS / HEIGHT_PX;
    lastSampleMillis = 0;

    spectograph = new int[HEIGHT_PX][fft.getNumBands()];
    initSpectographImg();

    for (int r=0; r<HEIGHT_PX; r++) {
      for (int c=0; c<fft.getNumBands(); c++) {
        spectograph[r][c] = p.color(0f);
      }
    }
  }

  public void draw() {
    if (spectograph == null)
      throw new RuntimeException("Looks like you forgot to .init()!");

    fft.forward();

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

    // log1p to avoid negatives < 1.0
    p.colorMode(RGB, (float)(USE_LOG_SCALE ? Math.log1p(WHITE_THRESHOLD) : WHITE_THRESHOLD));

    //float maxL = 0;

    for (int i=0; i<fft.getNumBands(); i++) {
      spectograph[currentRowI][i] = p.color(
          (float)( USE_LOG_SCALE ? Math.log1p(fft.getBand(i)) : fft.getBand(i) ));
      //maxL = Math.max(maxL, fft.getBand(i));
    }

    //System.out.println("max level:" + maxL);

    p.popStyle();
  }

  private void renderSpectograph() {
    spectographImg.loadPixels();

    int px = 0;
    for (int r=0; r < HEIGHT_PX; r++) {
      int realRowI = (currentRowI + r) % HEIGHT_PX;
      for (int c=0; c < fft.getNumBands(); c++) {

        for (int i=0; i<widthScale; i++) {
          spectographImg.pixels[px] = spectograph[realRowI][c];
          px++;
        }
      }
    }

    spectographImg.updatePixels();
  }

  public int getWidthScale() {
    return widthScale;
  }

  public void setWidthScale(int widthScale) {
    this.widthScale = widthScale;
    initSpectographImg();
  }

  private void initSpectographImg() {
    spectographImg = p.createImage(fft.getNumBands() * widthScale, HEIGHT_PX, RGB);
  }



}
