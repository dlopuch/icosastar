package com.github.dlopuch.icosastar.lx.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXLayer;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.LinearEnvelope;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.pattern.LXPattern;

public class RainbowSpreadPattern extends LXPattern {

  private final BasicParameter huePeriod = new BasicParameter("hue speed", 500, 20000);
  private final BasicParameter heightToHueRange;

  public RainbowSpreadPattern(LX lx) {
    super(lx);

    addLayer(new RainbowLayer(lx));
    addParameter(huePeriod);
    huePeriod.setValue(10000);

    heightToHueRange = new BasicParameter("height", 360, 360 * 2);
    addParameter(heightToHueRange);
  }

  public void run(double deltaMx) {
    // no-op -- layers run automatically
  }

  private class RainbowLayer extends LXLayer {

    private final LXModulator color = new SawLFO(0, 360, huePeriod);
    private RainbowLayer(LX lx) {
      super(lx);
      addModulator(color).start();
    }

    public void run(double deltaMx) {
      for (LXPoint p : model.points) {
        colors[p.index] = LXColor.hsb((p.z / model.zMax * heightToHueRange.getValue() + color.getValue()) % 360, 100, 50);
      }
    }
  }
}
