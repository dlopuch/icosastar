package com.github.dlopuch.icosastar.effects;

import com.github.dlopuch.icosastar.ColorDot;
import com.github.dlopuch.icosastar.Drawable;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;
import java.util.stream.Collectors;

import static processing.core.PApplet.max;

/**
 * Makes a random vertex pop on a hihat hit.
 */
public class HihatSparkles implements Drawable {

  private PApplet p;
  private ColorDot colorDot;
  private BeatDetect beat;

  private List<PVector> sparkleSpots;
  private List<Float> sparkleSizes;

  // Configuration fields.  Customize as desired;
  public float minSizePx = 10;
  public float maxSizePx = 30;
  public float decayMult = 0.90f;
  public float offCutoff = 0.05f;

  public HihatSparkles(PApplet p, ColorDot colorDot, IcosaFFT fft, List<PVector> sparkleSpots) {
    this.p = p;
    this.colorDot = colorDot;
    this.beat = fft.beat;

    this.sparkleSpots = sparkleSpots;
    this.sparkleSizes = sparkleSpots.stream().map(ss -> 0f).collect(Collectors.toList());

    p.registerMethod("draw", this);
  }

  public void draw() {
    if (beat.isHat()) {
      int vI = (int)p.random(0, sparkleSpots.size());
      sparkleSizes.set(vI, 1.0f);
    }

    // Draw the vertices
    for (int i=0; i<sparkleSpots.size(); i++) {
      if (sparkleSizes.get(i) < offCutoff)
        continue;

      colorDot.draw(
          sparkleSpots.get(i).x, sparkleSpots.get(i).y,
          0, 0, 100f * sparkleSizes.get(i),
          maxSizePx, 0
      );
    }

    sparkleSizes = sparkleSizes.stream().map(size -> size > offCutoff ? size * decayMult : 0).collect(Collectors.toList());
  }
}
