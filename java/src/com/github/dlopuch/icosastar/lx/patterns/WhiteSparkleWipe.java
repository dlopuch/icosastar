package com.github.dlopuch.icosastar.lx.patterns;

import com.github.dlopuch.icosastar.lx.patterns.effects.WhiteWipe;
import heronarts.lx.LX;
import heronarts.lx.model.LXModel;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.BasicParameter;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;

import java.util.function.Function;

/**
 * White wipes
 */
public class WhiteSparkleWipe extends LXPattern {


  private BooleanParameter triggerWave = new BooleanParameter("doWave", false);

  private WhiteWipe wiper;
  private WhiteWipe[] allWipes;

  public WhiteSparkleWipe(LX lx, PApplet p) {
    super(lx);

    allWipes = new WhiteWipe[] {
        new WhiteWipe(lx, this, m -> m.yMin, m -> m.yMax, pt -> pt.y),
        new WhiteWipe(lx, this, m -> m.yMax, m -> m.yMin, pt -> pt.y),

        new WhiteWipe(lx, this, m -> m.xMin, m -> m.xMax, pt -> pt.x),
        new WhiteWipe(lx, this, m -> m.xMax, m -> m.xMin, pt -> pt.x),

        new WhiteWipe(lx, this, m -> m.zMin, m -> m.zMax, pt -> pt.z),
        new WhiteWipe(lx, this, m -> m.zMax, m -> m.zMin, pt -> pt.z)
    };


    addParameter(triggerWave);
    triggerWave.addListener(param -> {
      if (param.getValue() > 0) {
        this.startRandomWipe();
        System.out.println("STARTING WAVE: " + param.getValue());
      }
    });
  }

  private void startRandomWipe() {
    wiper = allWipes[ (int) (Math.random() * allWipes.length) ];
    wiper.start();
  }

  @Override
  protected void run(double deltaMs) {

    for (LXPoint p : this.model.points) {
      colors[p.index] = LX.hsb(0, 0, 0);
    }

    for (WhiteWipe w : allWipes) {
      w.run(deltaMs);
    }
  }
}
