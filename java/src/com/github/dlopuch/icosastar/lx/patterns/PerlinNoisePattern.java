package com.github.dlopuch.icosastar.lx.patterns;


import com.github.dlopuch.icosastar.effects.perlin_noise.PerlinNoiseExplorer;
import com.github.dlopuch.icosastar.lx.model.AbstractIcosaLXModel;
import heronarts.lx.LX;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;
import java.util.stream.Collectors;

public class PerlinNoisePattern extends LXPattern {

  private LXPerlinNoiseExplorer hueNoise;
  public final DiscreteParameter huePeriodMs = new DiscreteParameter("h period", 10000, 1000, 30000);
  private SawLFO hueOffset = new SawLFO(0, 360, huePeriodMs);

  public final LXParameter hueSpeed;
  public final LXParameter hueXForm;

  private PerlinNoiseExplorer brightnessBoostNoise;
  private float brightnessBoostT = 0;
  private BasicParameter brightnessBoostDecay = new BasicParameter("bright decay", 0.97, 0.999, 0.80);

  public PerlinNoisePattern(LX lx, PApplet p) {
    super(lx);

    addModulator(hueOffset).start();


    addParameter(brightnessBoostDecay);

    List<PVector> leds = this.model.getPoints().stream()
        .map(pt -> new PVector(pt.x, pt.y, pt.z))
        .collect(Collectors.toList());

    this.hueNoise = new LXPerlinNoiseExplorer(p, this.model.getPoints(), "h ");
    addParameter(huePeriodMs);
    addParameter(this.hueSpeed = hueNoise.noiseSpeed);
    addParameter(this.hueXForm = hueNoise.noiseXForm);


//    this.brightnessBoostNoise = new PerlinNoiseExplorer(p, leds).setSpeed(hueNoiseSpeed.getValuef() * 2f);
//    this.hueNoiseSpeed.addListener(hueSpeedParam -> brightnessBoostNoise.setSpeed(hueSpeedParam.getValuef() * 2f));

    // initialize according to mapping
    ((AbstractIcosaLXModel) this.model).applyPresets(this);
  }

  public void run(double deltaMx) {
    for (LXPoint p : this.model.points) {
      colors[p.index] = LX.hsb(
          (hueOffset.getValuef() + 360 * hueNoise.getNoise(p.index)) % 360,
          100,
          70
      );
    }

    hueNoise.step();
    //brightnessBoostNoise.step();
  }

}
