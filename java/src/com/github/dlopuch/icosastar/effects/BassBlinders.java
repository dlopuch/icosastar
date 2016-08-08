package com.github.dlopuch.icosastar.effects;

import com.github.dlopuch.icosastar.ColorDot;
import com.github.dlopuch.icosastar.IcosaFFT;
import com.github.dlopuch.icosastar.IcosaVertex;
import com.github.dlopuch.icosastar.LEDMapping;
import ddf.minim.analysis.BeatDetect;
import processing.core.PApplet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static processing.core.PApplet.max;

/**
 * Makes a set of verticies flash on bass hits.  When bass and mids kick at the same time, make it extra blinding.
 */
public class BassBlinders {

  private PApplet p;
  private ColorDot colorDot;
  private BeatDetect beat;

  private List<IcosaVertex> vertices;

  /** Sizes of each vertex */
  private List<Float> vSize;


  // Configuration fields.  Customize as desired;
  public float minSizePx = 30;
  public float maxSizePx = 150;
  public float decayMult = 0.96f;


  public static BassBlinders onBottomRing(PApplet p, ColorDot colorDot, IcosaFFT fft, LEDMapping leds) {
    return new BassBlinders(p, colorDot, fft, leds.ring2Vs);
  }

  public BassBlinders(PApplet p, ColorDot colorDot, IcosaFFT fft, IcosaVertex[]vertices) {
    this.p = p;
    this.colorDot = colorDot;
    this.vertices = Arrays.asList(vertices);
    this.beat = fft.beat;

    this.vSize = this.vertices.stream().map(v -> this.minSizePx).collect(Collectors.toList());

    p.registerMethod("draw", this);
  }

  public void draw() {
    if (beat.isKick() && beat.isSnare()) {
      vSize = vSize.stream().map(v -> this.maxSizePx * 2).collect(Collectors.toList());

    } else if (beat.isKick()) {
      int vI = (int)p.random(0, vertices.size());
      vSize.set(vI, max(this.maxSizePx, vSize.get(vI)));
    }


    // Now draw the verticies
    for (int i=0; i< this.vertices.size(); i++) { //IcosaVertex v : this.kicks) {
      colorDot.draw(
          vertices.get(i).getX(), vertices.get(i).getY(),
          0, 0, 100,
          vSize.get(i), 100
      );
    }


    // Decay
    vSize = vSize.stream().map(size -> max(minSizePx, size * decayMult)).collect(Collectors.toList());
  }
}
