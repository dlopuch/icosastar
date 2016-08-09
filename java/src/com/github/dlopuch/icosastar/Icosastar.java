package com.github.dlopuch.icosastar;

import com.github.dlopuch.icosastar.effects.*;
import com.github.dlopuch.icosastar.signal.IcosaFFT;
import com.github.dlopuch.icosastar.widgets.*;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Icosastar extends PApplet {
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.Icosastar" });
  }

  LEDMapping ledMapping;
  ColorDot colorDot;

  List<Drawable> widgets = new LinkedList<>();

//  RainbowSpiral rainbowSpiral;
  IcosaFFT icosaFft = new IcosaFFT(this);

  int SIDE = Config.SIDE;

  PImage pine;
  PImage fftColors;


  float colorOffset = 0;
  float colorOffsetRadiansPerBucket = radians(1.5f);

  public void settings() {
    size(SIDE, SIDE, P3D); //3D to force GPU blending
  }

  public void setup() {

    translate(SIDE/2, SIDE/2);

    fftColors = loadImage("data/fftColors.png");
    colorDot = new ColorDot(this);

    ledMapping = new LEDMapping();

    // Enable some implementations:
    // --------

    // WIDGET: Frequency Histogram: shows FFT power distribution
    //this.widgets.add(new FrequencyHistogram(this, icosaFft));

    // WIDGET: Spectograph
    FrequencySpectograph frequencySpectograph = new FrequencySpectograph(this,
        new FrequencySpectograph.OctaveFftSupplier(icosaFft.in, 60, 7)
    );
    frequencySpectograph.init();
    frequencySpectograph.setWidthScale(3);
    this.widgets.add(frequencySpectograph);

    // WIDGET: VertexFFT
    this.widgets.add( new VertexFFT(this, colorDot, icosaFft.beat,
        Arrays.asList(ledMapping.ring2Vs), // bottom ring
        Arrays.asList(ledMapping.ring1Vs), // middle ring
        Arrays.asList(ledMapping.center)
    ) );


    // WIDGET: FFTSpiral
    // Draws a rotating color cloud according to frequency spectrum
    this.widgets.add(new FFTSpiral(this, colorDot.dot, icosaFft, fftColors));

    // WIDGET: BassBlinders: Flash on kick hit, flash extra hard on kick+mids
    this.widgets.add(new BassBlinders(this, colorDot, icosaFft, ledMapping.ring2Vs));

    // WIDGET: HihatSparkles: flash verticies on a hihat hit
    this.widgets.add(new HihatSparkles(this, colorDot, icosaFft, ledMapping.innerSpokeLeds));


    // ---------

    // Keep Last!
    ledMapping.init(this);
  }

  public void mousePressed() {
    //h = (h + 10) % 100;
    //frequencySpectograph.USE_LOG_SCALE = !frequencySpectograph.USE_LOG_SCALE;
    //System.out.println("Spectograph using log scale? " + frequencySpectograph.USE_LOG_SCALE);
  }

  float imgHeight = SIDE;

  float speed = 0.04f;


  public void draw() {
    background(0);

    icosaFft.forward();


    // Mouse pointer
    float hue = (millis() * -speed) % (imgHeight*2);
    colorDot.draw(mouseX, mouseY, hue, 100, 100, 200, 255);// + 200 * sin(t));



    // Translate the origin point to the center of the screen
    // (for other drawers)
    translate(SIDE/2, SIDE/2);

    widgets.forEach(Drawable::draw);
    this.ledMapping.draw(); // MUST BE LAST TO DRAW (does pixel sampling to OPC, everything must be already drawn)

  }
}
