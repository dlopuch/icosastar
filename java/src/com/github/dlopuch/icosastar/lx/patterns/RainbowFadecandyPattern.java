package com.github.dlopuch.icosastar.lx.patterns;

import com.github.dlopuch.icosastar.lx.model.CloudLXModel;
import com.github.dlopuch.icosastar.lx.model.CloudLXModelBuilder;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.pattern.LXPattern;


public class RainbowFadecandyPattern extends LXPattern {

  private final LXModulator color = new SawLFO(0, 360, 2000);

  private final LXModulator fadecandySelect = new SawLFO(0, CloudLXModelBuilder.NUM_FADECANDIES, 8000);
  private final LXModulator radialSelect = new SawLFO(0, CloudLXModelBuilder.RADIALS_PER_FADECANDY, 4000);

  public final DiscreteParameter fadecandyP = new DiscreteParameter("fc#", -1, -1, CloudLXModelBuilder.NUM_FADECANDIES);
  public final DiscreteParameter radialP = new DiscreteParameter("radial", -1, -1, CloudLXModelBuilder.RADIALS_PER_FADECANDY);

  private CloudLXModel model;

  public RainbowFadecandyPattern(LX lx) {
    super(lx);
    addModulator(color).start();
    addModulator(fadecandySelect).start();
    addModulator(radialSelect).start();

    addParameter(fadecandyP);
    addParameter(radialP);

    this.model = (CloudLXModel) lx.model;
  }

  public void run(double deltaMs) {
    for (LXPoint p : model.points) {
      colors[p.index] = LXColor.hsb(0, 0, 0);
    }

    int fc = fadecandyP.getValue() < 0 ? (int)Math.floor(fadecandySelect.getValue()) : (int)fadecandyP.getValue();
    int radial = radialP.getValue() < 0 ? (int)Math.floor(radialSelect.getValue()) : (int)radialP.getValue();

    for (LXPoint p : this.model.radialsByFadecandy.get(fc).get(radial).getPoints()) {
      colors[p.index] = LXColor.hsb(color.getValue(), 100, 50);
    }
  }
}
