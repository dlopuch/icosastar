package com.github.dlopuch.icosastar.lx;

import com.github.dlopuch.icosastar.Config;
import com.github.dlopuch.icosastar.lx.model.*;
import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowFadecandyPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowPattern;
import com.github.dlopuch.icosastar.lx.patterns.RainbowSpreadPattern;
import com.github.dlopuch.icosastar.lx.utils.AudioDetector;
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
  private static boolean isVerbose = false;
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.lx.IcosastarLXGui" });

    for (String s: args) {
      if (s.equalsIgnoreCase("-v")) {
        isVerbose = true;
      }
    }
  }

  private P3LX lx;
  private AbstractIcosaLXModel model;

  private IcosaFFT icosaFft = new IcosaFFT();
  private LXOutput fcOutput;

  private FrameRateCalculator frc;

  private float lastDrawMs = 0;

  public void settings() {
    size(Config.SIDE, Config.SIDE, P3D); //3D to force GPU blending
  }

  public void setup() {
    model = ModelSupplier.getModel(true);

    frc = new FrameRateCalculator(this, 3000, isVerbose);
    AudioDetector.init(icosaFft.in.mix);

    lx = new P3LX(this, model);

    model.initLx(lx);

    model.addPatternsAndGo(lx, this, icosaFft);

    fcOutput = new FadecandyOutput(lx, "localhost", 7890);
    lx.addOutput(fcOutput);

    lx.ui.addLayer(
        new UI3dContext(lx.ui)
        .setCenter(model.cx, model.cy, model.cz)
        .setRadius(model.xMax - model.xMin)
        .addComponent(new UIPointCloud(lx, model).setPointSize(5))
    );

    lx.ui.addLayer(new UIChannelControl(lx.ui, lx, 16, 4, 4));

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

    AudioDetector.LINE_IN.tick(this.millis() - lastDrawMs, isVerbose);
    lastDrawMs = this.millis();
  }
}
