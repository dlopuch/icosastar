package com.github.dlopuch.icosastar;

import static processing.core.PApplet.ADD;
import static processing.core.PApplet.RGB;
import static processing.core.PApplet.radians;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class FFTSpiral {

  private PApplet p;

  PImage dot;
  IcosaFFT icosaFft;
  PImage fftColors;

  // Default parameters, change per instance if need be
  float minSize = 0.1f;
  float sizeScale = 0.3f;
  float radiansPerBucket = radians(1.5f);
  float spin = 0.001f;
  float opacity = 25f;


  FFTSpiral(PApplet parent, PImage dot, IcosaFFT fft, PImage paletteImage) {
    this.p = parent;
    parent.registerDraw(this);
    this.dot = dot;
    this.icosaFft = fft;
    this.fftColors = paletteImage;
  }

  public void draw() {
    float[] fftFilter = this.icosaFft.getFilter();
    p.colorMode(RGB);

    for (int i = 0; i < fftFilter.length; i += 3) {
      // tall palettes (sample height)
      /*color rgb = this.fftColors.get(
        fftColors.width/2,
        int(map(i, 0, fftFilter.length-1, 0, this.fftColors.height-1))
      );// */

      // wide palettes (sample width)
      int rgb = this.fftColors.get(
          (int)(p.map(i, 0, fftFilter.length-1, 0, this.fftColors.width-1)),
          fftColors.height/2
      );

      p.tint(rgb, fftFilter[i] * this.opacity);
      p.blendMode(ADD);

      // Size of the bucket's dot overlay
      float size = p.height * (this.minSize + this.sizeScale * fftFilter[i]);

      // Make a vector for the bucket
      PVector center = new PVector(p.width * (fftFilter[i] * 0.2f), 0);
      center.rotate((p.millis() * this.spin)  +  (i * this.radiansPerBucket));

      p.image(this.dot, center.x - size/2, center.y - size/2, size, size);
    }
  }
}