package com.github.dlopuch.icosastar.lx;

import com.github.dlopuch.icosastar.lx.model.AbstractIcosaLXModel;
import com.github.dlopuch.icosastar.lx.model.ModelSupplier;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.github.dlopuch.icosastar.widgets.FrameRateCalculator;
import heronarts.lx.LX;
import heronarts.lx.output.FadecandyOutput;
import heronarts.lx.output.LXOutput;
import processing.core.PApplet;

/**
 * Headless (no gui) version of icosastar.
 *
 * (Technically has a PApplet gui, but rendered without any contents to minimize any rendering time).
 */
public class IcosastarLX extends PApplet {

  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.lx.IcosastarLX" });
  }

  private LX lx;
  private AbstractIcosaLXModel model;

  private IcosaFFT icosaFft = new IcosaFFT();
  LXOutput fcOutput;

  private FrameRateCalculator frc = new FrameRateCalculator(this, 3000, icosaFft.in.mix);

  public void settings() {
    size(1, 1, P2D);
  }

  public void setup() {
    PApplet.println("Starting 'headless' icosastar...");

    model = ModelSupplier.getModel(false);

    lx = new LX(model);

    model.addPatternsAndGo(lx, this, icosaFft);

    fcOutput = new FadecandyOutput(lx, "localhost", 7890);
    lx.addOutput(fcOutput);

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

    // LX engine run code from P3LX
    if (lx.engine.isThreaded()) {
      // NOTE: because we don't hold a lock, it is *possible* that the
      // engine stops being in threading mode just between these lines,
      // triggered by some action on the engine thread itself. It's okay
      // if this happens, worst side effect is the UI getting the last frame
      // from the copy buffer.
//      this.engine.copyBuffer(this.colors = this.buffer);
//      if (this.flags.showFramerate) {
//        frameRateStr = "Engine: " + this.engine.frameRate() + " "
//            + "Render: " + this.applet.frameRate;
//      }
    } else {
      // If the engine is not threaded, then we run it ourselves, and
      // we can just use its color buffer, as there is no thread contention.
      // We don't need to worry about lock contention because we are
      // currently on the only thread that *could* start the engine.
      lx.engine.run();
//      this.colors = this.engine.renderBuffer();
//      if (this.flags.showFramerate) {
//        frameRateStr = "Framerate: " + this.applet.frameRate;
//      }
    }


    frc.draw();
    icosaFft.forward();
  }
}
