package com.github.dlopuch.icosastar;

import com.github.dlopuch.icosastar.mappings.CloudMapping;
import com.github.dlopuch.icosastar.mappings.IcosastarMapping;
import com.github.dlopuch.icosastar.mappings.LedMapping;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.github.dlopuch.icosastar.vendor.OPC;
import com.github.dlopuch.icosastar.widgets.*;
import heronarts.lx.pattern.LXPattern;
import processing.core.PApplet;

import java.util.LinkedList;
import java.util.List;

public class Icosastar extends PApplet {
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.Icosastar" });
  }

  private OPC opc;
  private ColorDot colorDot;

  private LedMapping ledMapping;

  private List<Drawable> widgets = new LinkedList<>();
  private List<Drawable> effects = new LinkedList<>();

  private IcosaFFT icosaFft = new IcosaFFT();
  //private IcosaFFT icosaFft = new MockIcosaFFT(this);


  public void settings() {
    size(Config.SIDE, Config.SIDE, P3D); //3D to force GPU blending
  }

  public void setup() {

    translate(Config.SIDE/2, Config.SIDE/2);


    colorDot = new ColorDot(this);

    this.opc = new OPC(this, "127.0.0.1", 7890);

    //this.ledMapping = new IcosastarMapping(this, opc, colorDot, icosaFft);
    this.ledMapping = new CloudMapping(this, opc, colorDot, icosaFft);

    // Enable some implementations:
    // --------

    // TOOL: Frame Rate Display
    //this.widgets.add(new FrameRateCalculator(this, 3000));

    // WIDGET: Frequency Histogram: shows FFT power distribution
    //this.widgets.add(new FrequencyHistogram(this, icosaFft));

    // WIDGET: Spectograph
    FrequencySpectograph frequencySpectograph = new FrequencySpectograph(this,
        new FrequencySpectograph.OctaveFftSupplier(icosaFft.in, 60, 7)
    );
    frequencySpectograph.init();
    frequencySpectograph.setWidthScale(3);
    this.widgets.add(frequencySpectograph);

    // WIDGET: Event Frequency Counter
    this.widgets.add(new BeatFrequencyIndicator(this, icosaFft, 5000l));


    // EFFECTS
    // -------------------

    // EFFECT: Perlin noise field
    this.effects.add(this.ledMapping.makePerlinNoiseField());

    // EFFECT: VertexFFT: adds color pops for lows, mids, and hi's
//    this.effects.add(this.ledMapping.makeVertexFFT());

    // EFFECT: FFTSpiral: Draws a rotating color cloud according to frequency spectrum
//    this.effects.add(this.ledMapping.makeFFTSpiral());

    // EFFECT: BassBlinders: Flash on kick hit, flash extra hard on kick+mids
    this.effects.add(this.ledMapping.makeBassBlinders());

    // EFFECT: HihatSparkles: flash verticies on a hihat hit
    this.effects.add(this.ledMapping.makeHihatSparkles());

    // EFFECT: Bass hits send out white bursts along the radials
    this.effects.add(this.ledMapping.makeRadialStream());

    // Experiments / effects that work less well:
    // ---------------
    // EXPERIMENT: mouse-controlled FluidDynamics simulator.  Works kinda meh.
    //this.effects.add(new FluidDynamics(this));

  }

  public void mousePressed() {
    //h = (h + 10) % 100;
    //frequencySpectograph.USE_LOG_SCALE = !frequencySpectograph.USE_LOG_SCALE;
    //System.out.println("Spectograph using log scale? " + frequencySpectograph.USE_LOG_SCALE);
  }

  private float imgHeight = Config.SIDE;

  private float speed = 0.04f;


  public void draw() {
    background(0);

    icosaFft.forward();


    // Mouse pointer
    float hue = (millis() * -speed) % (imgHeight*2);
    colorDot.draw(mouseX, mouseY, hue, 100, 100, 200, 255);// + 200 * sin(t));


    // Translate the origin point to the center of the screen
    // (for other drawers)
    translate(Config.SIDE/2, Config.SIDE/2);

    effects.forEach(Drawable::draw);
    this.ledMapping.draw();
    this.opc.draw(); // MUST BE LAST TO DRAW (does pixel sampling to OPC, everything must be already drawn)

    widgets.forEach(Drawable::draw);
  }
}
