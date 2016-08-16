package com.github.dlopuch.icosastar.mappings;

import com.github.dlopuch.icosastar.ColorDot;
import com.github.dlopuch.icosastar.Drawable;
import com.github.dlopuch.icosastar.effects.*;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.github.dlopuch.icosastar.vendor.OPC;
import processing.core.PApplet;

/**
 * Base mapping that adds support for all known effects
 */
public abstract class LedMapping implements Drawable {

  protected PApplet p;
  protected OPC opc;

  public LedMapping(PApplet p, OPC opc) {
    this.p = p;
    this.opc = opc;
  }

  /**
   * Sometimes mappings draw support guides
   */
  public abstract void draw();

  // Effects implementations:

  public abstract VertexFFT makeVertexFFT();
  public abstract FFTSpiral makeFFTSpiral();
  public abstract BassBlinders makeBassBlinders();
  public abstract HihatSparkles makeHihatSparkles();
  public abstract RadialStream makeRadialStream();
  public abstract PerlinNoise makePerlinNoiseField();
}
