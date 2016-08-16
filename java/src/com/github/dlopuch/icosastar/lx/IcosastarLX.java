package com.github.dlopuch.icosastar.lx;

import com.github.dlopuch.icosastar.Config;
import com.github.dlopuch.icosastar.lx.model.IcosastarLXModel;
import com.github.dlopuch.icosastar.lx.model.IcosastarLXModelBuilder;
import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.widgets.FrameRateCalculator;
import heronarts.lx.output.FadecandyOutput;
import heronarts.lx.pattern.LXPattern;
import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UI3dContext;
import heronarts.p3lx.ui.component.UIPointCloud;
import heronarts.p3lx.ui.control.UIChannelControl;
import processing.core.PApplet;

/**
 *
 */
public class IcosastarLX extends PApplet {
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.lx.IcosastarLX" });
  }

  private P3LX lx;
  private IcosastarLXModel model;

  private FrameRateCalculator frc = new FrameRateCalculator(this, 3000);

  public void settings() {
    size(Config.SIDE, Config.SIDE, P3D); //3D to force GPU blending
  }

  public void setup() {
    model = IcosastarLXModelBuilder.makeModel();
    lx = new P3LX(this, model);

    lx.setPatterns(new LXPattern[] {
        new PerlinNoisePattern(lx, this),
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx),
    });

    lx.addOutput(new FadecandyOutput(lx, "localhost", 7890));

    lx.ui.addLayer(
        new UI3dContext(lx.ui)
        .setCenter(model.cx, model.cy, model.cz)
        .setRadius(130)
        .addComponent(new UIPointCloud(lx, model).setPointSize(5))
    );

    lx.ui.addLayer(new UIChannelControl(lx.ui, lx, 8, 4, 4));
  }

  public void draw() {
    // Wipe the frame...
    background(0x292929);
    // ...and everything else is handled by P3LX!

    frc.draw();
  }
}
