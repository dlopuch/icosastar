package com.github.dlopuch.icosastar.lx.patterns;


import com.github.dlopuch.icosastar.effects.perlin_noise.PerlinNoiseExplorer;
import com.github.dlopuch.icosastar.lx.model.AbstractIcosaLXModel;
import com.github.dlopuch.icosastar.lx.utils.GradientSupplier;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import ddf.minim.analysis.BeatDetect;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;
import java.util.stream.Collectors;

public class PerlinNoisePattern extends LXPattern {

  private BeatDetect beat;

  private LXPerlinNoiseExplorer hueNoise;
  public final DiscreteParameter huePeriodMs = new DiscreteParameter("h period", 10000, 1000, 30000);
  private SawLFO hueOffset = new SawLFO(0, 360, huePeriodMs);

  public final LXParameter hueSpeed;
  public final LXParameter hueXForm;

  private LXPerlinNoiseExplorer brightnessBoostNoise;
  private float brightnessBoostT = 0;
  private BasicParameter brightnessBoostDecay = new BasicParameter("bright decay", 0.97, 0.999, 0.80);

  public final BooleanParameter useGradientSupplier = new BooleanParameter("use grad", false);
  public final GradientSupplier gradientSupplier;
  private SawLFO gradientAutoselect;

  public PerlinNoisePattern(LX lx, PApplet p, IcosaFFT fft) {
    super(lx);

    this.beat = fft.beat;

    addModulator(hueOffset).start();

    this.gradientSupplier = new GradientSupplier(p);

    List<PVector> leds = this.model.getPoints().stream()
        .map(pt -> new PVector(pt.x, pt.y, pt.z))
        .collect(Collectors.toList());

    this.hueNoise = new LXPerlinNoiseExplorer(p, this.model.getPoints(), "h ");
    addParameter(huePeriodMs);
    addParameter(this.hueSpeed = hueNoise.noiseSpeed);
    addParameter(this.hueXForm = hueNoise.noiseXForm);


    this.brightnessBoostNoise = new LXPerlinNoiseExplorer(p, this.model.getPoints(), "b");
    addParameter(brightnessBoostDecay);
    addParameter(brightnessBoostNoise.noiseSpeed);
    addParameter(brightnessBoostNoise.noiseXForm);

    // Gradient supplier
    addParameter(useGradientSupplier);
    addParameter(gradientSupplier.gradientSelect);

    // Add an auto-rotate-through-gradients if in headless
    if (!((AbstractIcosaLXModel) this.model).hasGui) {
      useGradientSupplier.setValue(true);
      gradientAutoselect = new SawLFO(
          gradientSupplier.gradientSelect.getMinValue(),
          gradientSupplier.gradientSelect.getMaxValue() + 1,
          5000 * (gradientSupplier.gradientSelect.getMaxValue() + 1)
      );
      gradientSupplier.gradientSelect.addListener(param -> System.out.println("Using gradient #" + param.getValue()) );
      addModulator(
          gradientAutoselect
      ).start();
    }


    // initialize according to mapping
    ((AbstractIcosaLXModel) this.model).applyPresets(this);
    brightnessBoostNoise.noiseSpeed.setValue(2.0 * this.hueSpeed.getValue());
    brightnessBoostNoise.noiseXForm.setValue(0.5 * this.hueXForm.getValue());
  }

  public void run(double deltaMs) {
    if (gradientAutoselect != null) {
      gradientSupplier.gradientSelect.setValue(Math.floor(gradientAutoselect.getValue()));
    }


    boolean isBrightnessBoost = beat.isKick();
    if (isBrightnessBoost) {
      brightnessBoostT = 1.0f;
    } else if (brightnessBoostT > 0.05) {
      brightnessBoostT *= brightnessBoostDecay.getValuef();
    }

    for (LXPoint p : this.model.points) {
      int color;
      if (!useGradientSupplier.getValueb()) {
        color = LX.hsb(
            (hueOffset.getValuef() + 360 * hueNoise.getNoise(p.index)) % 360,
            100,
            30 + (brightnessBoostT > 0.05 ? brightnessBoostT * 60 * brightnessBoostNoise.getNoise(p.index) : 0)
        );
      } else {
        color = gradientSupplier.getColor(hueNoise.getNoise(p.index));
        float b = LXColor.b(color);
        color = LX.hsb(
            LXColor.h(color),
            LXColor.s(color),
            30f * b/100f + (brightnessBoostT > 0.05 ? brightnessBoostT * 70 * brightnessBoostNoise.getNoise(p.index) : 0)
            //60f * b/100f + (brightnessBoostT > 0.05 ? brightnessBoostT * 40 * brightnessBoostNoise.getNoise(p.index) : 0)
        );
      }

      colors[p.index] = color;

    }

    if (beat.isSnare() && beat.isKick()) {
      double newHue = (hueOffset.getValue() + Math.random() * 360 - 180) % 360;
      hueOffset.setValue(newHue > 0 ? newHue : 360 + newHue);
    }

    hueNoise.step();
    //brightnessBoostNoise.step();
  }

}
