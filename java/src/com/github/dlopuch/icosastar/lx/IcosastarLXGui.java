package com.github.dlopuch.icosastar.lx;

import com.github.dlopuch.icosastar.Config;
import com.github.dlopuch.icosastar.lx.model.AbstractIcosaLXModel;
import com.github.dlopuch.icosastar.lx.model.CloudLXModelBuilder;
import com.github.dlopuch.icosastar.lx.model.IcosastarLXModel;
import com.github.dlopuch.icosastar.lx.model.IcosastarLXModelBuilder;
import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowFadecandyPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.github.dlopuch.icosastar.widgets.FrameRateCalculator;
import heronarts.lx.model.LXPoint;
import heronarts.lx.output.FadecandyOutput;
import heronarts.lx.output.LXOutput;
import heronarts.lx.pattern.LXPattern;
import heronarts.p3lx.P3LX;
import heronarts.p3lx.ui.UI3dContext;
import heronarts.p3lx.ui.component.UIPointCloud;
import heronarts.p3lx.ui.control.UIChannelControl;
import processing.core.PApplet;

/**
 *
 */
public class IcosastarLXGui extends PApplet {
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.lx.IcosastarLXGui" });
  }

  private P3LX lx;
  private AbstractIcosaLXModel model;

  private IcosaFFT icosaFft = new IcosaFFT();
  private LXOutput fcOutput;

  private FrameRateCalculator frc = new FrameRateCalculator(this, 3000, icosaFft.in.mix);

  public void settings() {
    size(Config.SIDE, Config.SIDE, P3D); //3D to force GPU blending
  }

  public void setup() {
    //model = IcosastarLXModelBuilder.makeModel();
    model = CloudLXModelBuilder.makeModel();

    lx = new P3LX(this, model);

    lx.setPatterns(new LXPattern[] {
        new PerlinNoisePattern(lx, this, icosaFft),
        new RainbowPattern(lx),
        new RainbowSpreadPattern(lx),
        new RainbowFadecandyPattern(lx)
    });

    fcOutput = new FadecandyOutput(lx, "localhost", 7890);
    lx.addOutput(fcOutput);

    lx.ui.addLayer(
        new UI3dContext(lx.ui)
        .setCenter(model.cx, model.cy, model.cz)
        .setRadius(130)
        .addComponent(new UIPointCloud(lx, model).setPointSize(5))
    );

    lx.ui.addLayer(new UIChannelControl(lx.ui, lx, 8, 4, 4));

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

      public void run () {

        System.out.println("Shutting down: turning all off...");

        fcOutput.mode.setValue(LXOutput.MODE_OFF);
        fcOutput.send(null);
        for (int i=0; i<100000; i++)
          Thread.yield();
      }
    }
    ));
  }

  public void draw() {
    // Wipe the frame...
    background(0x292929);
    // ...and everything else is handled by P3LX!

    frc.draw();
    icosaFft.forward();
  }
}
