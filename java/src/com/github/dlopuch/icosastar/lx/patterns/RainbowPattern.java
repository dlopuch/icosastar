package com.github.dlopuch.icosastar.lx.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXLayer;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.pattern.LXPattern;

public class RainbowPattern extends LXPattern {

  private final BasicParameter huePeriod = new BasicParameter("hue period", 500, 20000);

  public RainbowPattern(LX lx) {
    super(lx);

    addLayer(new RainbowLayer(lx));
    addParameter(huePeriod);
    huePeriod.setValue(10000);
  }

  public void run(double deltaMx) {
    // no-op -- layers run automatically
  }

  private class RainbowLayer extends LXLayer {

    private final SinLFO color = new SinLFO(0, 360, huePeriod);
    private RainbowLayer(LX lx) {
      super(lx);
      addModulator(color).start();
    }

    public void run(double deltaMx) {
      for (LXPoint p : model.points) {
        colors[p.index] = LXColor.hsb(color.getValue(), 100, 50);
      }
    }
  }
}
