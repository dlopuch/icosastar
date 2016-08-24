package com.github.dlopuch.icosastar.lx.patterns;


import com.github.dlopuch.icosastar.lx.model.AbstractIcosaLXModel;
import com.github.dlopuch.icosastar.lx.patterns.perlin.GradientColorizer;
import com.github.dlopuch.icosastar.lx.patterns.perlin.LXPerlinNoiseExplorer;
import com.github.dlopuch.icosastar.lx.patterns.perlin.PerlinNoiseColorizer;
import com.github.dlopuch.icosastar.lx.patterns.perlin.RotatingHueColorizer;
import com.github.dlopuch.icosastar.lx.utils.AudioDetector;
import com.github.dlopuch.icosastar.lx.utils.GradientSupplier;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import ddf.minim.analysis.BeatDetect;
import heronarts.lx.LX;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerlinNoisePattern extends LXPattern {

  private BeatDetect beat;

  private LXPerlinNoiseExplorer hueNoise;

  public final LXParameter hueSpeed;
  public final LXParameter hueXForm;

  private LXPerlinNoiseExplorer brightnessBoostNoise;
  private float brightnessBoostT = 0;

  /**
   * Exponential dropoff of the brightness boost, per ms.
   * eg in 500ms, the brightness boost will be at Math.pow({bright decay}, 500) %
   */
  private BasicParameter brightnessBoostDecayPerMs = new BasicParameter("bb decay", 0.998, 0.990, 0.999);

  /** Selects a colorizer to use */
  public DiscreteParameter colorizerSelect;
  public BooleanParameter rotateColorizer = new BooleanParameter("rotate", true);

  private Map<String, PerlinNoiseColorizer> allColorizers;

  private SawLFO gradientAutoselect;

  private double timeSinceLastRotate = 0;

  public BasicParameter maxBrightness = new BasicParameter("maxBrightness", 70, 40, 100);
  public BasicParameter baseBrightness = new BasicParameter("baseBrightness", 0.30, 0.10, 1.00);

  public PerlinNoisePattern(LX lx, PApplet p, IcosaFFT fft) {
    super(lx);

    this.beat = fft.beat;

    addParameter(maxBrightness);
    addParameter(baseBrightness);


    // Make Hue Noise
    // -----------------
    List<PVector> leds = this.model.getPoints().stream()
        .map(pt -> new PVector(pt.x, pt.y, pt.z))
        .collect(Collectors.toList());

    this.hueNoise = new LXPerlinNoiseExplorer(p, this.model.getPoints(), "h ");
    addParameter(this.hueSpeed = hueNoise.noiseSpeed);
    addParameter(this.hueXForm = hueNoise.noiseXForm);


    // Make Noise field
    // -----------------
    this.brightnessBoostNoise = new LXPerlinNoiseExplorer(p, this.model.getPoints(), "bb ");
    addParameter(brightnessBoostNoise.noiseSpeed);
    addParameter(brightnessBoostNoise.noiseXForm);

    addParameter(brightnessBoostDecayPerMs);


    // Make colorizers
    // -----------------
    allColorizers = new HashMap<>();

    RotatingHueColorizer rotatingHueColorizer = new RotatingHueColorizer(hueNoise) {
      @Override
      public RotatingHueColorizer activate() {
        hueNoise.noiseSpeed.setValue(0.027);
        return this;
      }
    };
    allColorizers.put("rotatingHue", rotatingHueColorizer);
    addParameter(rotatingHueColorizer.huePeriodMs);

    // Colorizer: gradient
    GradientColorizer gradientColorizer = new GradientColorizer(hueNoise, new GradientSupplier(p)) {
        @Override
        public GradientColorizer activate() {
          hueNoise.noiseSpeed.setValue(0.027);
          return this;
        }
    };
    allColorizers.put("gradient", gradientColorizer);
    addParameter(gradientColorizer.gradientSupplier.gradientSelect);

    // Colorizer: patterns
    GradientColorizer patternColorizer = new GradientColorizer(hueNoise, new GradientSupplier(p, true)) {
      @Override
      public GradientColorizer activate() {
        hueNoise.noiseSpeed.setValue(0.006);
        return this;
      }
    };
    allColorizers.put("pattern", patternColorizer);
    addParameter(patternColorizer.gradientSupplier.gradientSelect);

    // Register all colorizers
    for (PerlinNoiseColorizer colorizer : allColorizers.values()) {
      for (LXModulator modulator : colorizer.getModulators()) {
        addModulator(modulator);
      }
    }

    colorizerSelect = new DiscreteParameter(
        "clrzr",
        allColorizers.keySet().toArray(new String[allColorizers.keySet().size()])
    );
    colorizerSelect.addListener(param -> allColorizers.get(colorizerSelect.getOption()).activate());
    // And do first activation:
    allColorizers.get(colorizerSelect.getOption()).activate();

    addParameter(colorizerSelect);
    addParameter(rotateColorizer);



    // Add an auto-rotate-through-gradients if in headless
//    if (!((AbstractIcosaLXModel) this.model).hasGui) {
//      useGradientSupplier.setValue(true);
//      gradientAutoselect = new SawLFO(
//          gradientSupplier.gradientSelect.getMinValue(),
//          gradientSupplier.gradientSelect.getMaxValue() + 1,
//          5000 * (gradientSupplier.gradientSelect.getMaxValue() + 1)
//      );
//      gradientSupplier.gradientSelect.addListener(param -> System.out.println("Using gradient #" + param.getValue()) );
//      addModulator(
//          gradientAutoselect
//      ).start();
//    }


    // initialize according to mapping
    ((AbstractIcosaLXModel) this.model).applyPresets(this);
    brightnessBoostNoise.noiseSpeed.setValue(2.0 * this.hueSpeed.getValue());
    brightnessBoostNoise.noiseXForm.setValue(0.5 * this.hueXForm.getValue());
  }

  public void run(double deltaMs) {
//    if (gradientAutoselect != null) {
//      gradientSupplier.gradientSelect.setValue(Math.floor(gradientAutoselect.getValue()));
//    }


    boolean isBrightnessBoost = beat.isKick();
    if (isBrightnessBoost) {
      brightnessBoostT = 1.0f;
    } else if (brightnessBoostT > 0.05) {
      brightnessBoostT *= Math.pow(brightnessBoostDecayPerMs.getValuef(), deltaMs);
    }

    PerlinNoiseColorizer colorizer = allColorizers.get(colorizerSelect.getOption());
    //float maxBrightness = ((AbstractIcosaLXModel)model).getMaxBrightness();
    float maxBrightness = this.maxBrightness.getValuef();

    for (LXPoint p : this.model.points) {
      int color = colorizer.getColor(p);

      float b = LXColor.b(color);

      if (AudioDetector.LINE_IN.isRunning()) {
        b = (maxBrightness * .30f) * b/100f +
            (brightnessBoostT > 0.05 ? brightnessBoostT * (maxBrightness * .70f) * brightnessBoostNoise.getNoise(p.index) : 0);
      } else {
        // If audio not working, just do random brightness mapping
        b = (maxBrightness * baseBrightness.getValuef()) * b / 100f +
            (maxBrightness * (1f - baseBrightness.getValuef())) * brightnessBoostNoise.getNoise(p.index);
      }

      color = LX.hsb(
          LXColor.h(color),
          LXColor.s(color),
          b
      );

      colors[p.index] = color;

    }

    // Rotate colorizers
    boolean doRotate = false;
    if (AudioDetector.LINE_IN.isRunning()) {
      doRotate = beat.isSnare() && beat.isKick() && rotateColorizer.getValueb();
    } else if (rotateColorizer.getValueb()) {
      // No audio?  Rotate probabilistically
      doRotate = Math.random() * 1000000 < timeSinceLastRotate;
    }
    if (doRotate) {
      colorizer = randomColorizer();
      hueNoise.randomizeDirection();
      colorizer.rotate();
      timeSinceLastRotate = 0;
    } else {
      timeSinceLastRotate += deltaMs;
    }

    hueNoise.step();
    //brightnessBoostNoise.step();
  }

  private PerlinNoiseColorizer randomColorizer() {
    double rand = Math.random();

    // Weighted random

    // hueRotate
    if (rand < 0.2) {
      colorizerSelect.setValue(0);

    // gradient:
    } else if (rand < 0.7) {
      colorizerSelect.setValue(1);

    // pattern
    } else {
      colorizerSelect.setValue(2);
    }

    return allColorizers.get(colorizerSelect.getOption());
  }

}
