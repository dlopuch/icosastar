package com.github.dlopuch.icosastar.lx.model;


import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import heronarts.lx.LX;
import heronarts.lx.model.LXFixture;
import heronarts.lx.model.LXModel;
import processing.core.PApplet;

/**
 * Class that defines mapping-specific pattern defaults that patterns can register themselves against.
 */
public abstract class AbstractIcosaLXModel extends LXModel {
  public final boolean hasGui;

  public AbstractIcosaLXModel(LXFixture[] fixtures, boolean hasGui) {
    super(fixtures);
    this.hasGui = hasGui;
  }

  /**
   * Implementation hook for models to customize LX or LX engine
   * @param lx lx
   * @return This instance, for chaining
   */
  public AbstractIcosaLXModel initLx(LX lx) {
    // default is no-op
    return this;
  }

  /**
   * Implementation adds patterns appropriate for it and calls lx.goPattern() on first one.
   */
  public abstract void addPatternsAndGo(LX lx, PApplet p, IcosaFFT icosaFft);


  public abstract void applyPresets(PerlinNoisePattern perlinNoise);

  public float getMaxBrightness() {
    return 100;
  }
}
