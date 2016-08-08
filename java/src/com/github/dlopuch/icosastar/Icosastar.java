package com.github.dlopuch.icosastar;

import com.github.dlopuch.icosastar.effects.BassBlinders;
import com.github.dlopuch.icosastar.effects.Sparkles;
import com.github.dlopuch.icosastar.widgets.FrequencySpectograph;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.Arrays;

public class Icosastar extends PApplet {
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.Icosastar" });
  }

  LEDMapping ledMapping;
  ColorDot colorDot;

  VertexPoppers vertexPoppers;
//  RainbowSpiral rainbowSpiral;
  IcosaFFT icosaFft = new IcosaFFT(this);
  FrequencySpectograph frequencySpectograph;
  FFTSpiral fftSpiral;

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

    pine = loadImage("data/pine1.jpg");
    fftColors = loadImage("data/fftColors.png");
    colorDot = new ColorDot(this);

    ledMapping = new LEDMapping();

    // Enable some implementations:
    // --------

    //new FrequencyHistogram(this, icosaFft);
    //this.frequencySpectograph = new FrequencySpectograph(this, icosaFft.fft);
//    this.frequencySpectograph = new FrequencySpectograph(this,
//        new FrequencySpectograph.OctaveFftSupplier(icosaFft.in, 60, 7)
//    );
//    this.frequencySpectograph.init();
//    this.frequencySpectograph.setWidthScale(3);

    //vertexPoppers = new VertexPoppers(this, ledMapping.verticies);
    //rainbowSpiral = new RainbowSpiral(this);
    new VertexFFT(this, colorDot, icosaFft.beat,
        Arrays.asList(ledMapping.ring2Vs), // bottom ring
        Arrays.asList(ledMapping.ring1Vs), // middle ring
        Arrays.asList(ledMapping.center)
    );
    fftSpiral = new FFTSpiral(this, colorDot.dot, icosaFft, fftColors);
    BassBlinders.onBottomRing(this, colorDot, icosaFft, ledMapping);
    new Sparkles(this, colorDot, icosaFft, ledMapping.innerSpokeLeds);



    // ---------

    // Keep Last!
    ledMapping.registerDraw(this);
  }

  public void mousePressed() {
    //h = (h + 10) % 100;
    //frequencySpectograph.USE_LOG_SCALE = !frequencySpectograph.USE_LOG_SCALE;
    //System.out.println("Spectograph using log scale? " + frequencySpectograph.USE_LOG_SCALE);
  }

  float imgHeight = SIDE;
  float y1 = imgHeight;
  float y2 = 0;

  float speed = 0.04f;


  public void draw() {
    background(0);

    icosaFft.forward();

    // Mouse pointer
    float hue = (millis() * -speed) % (imgHeight*2);
    colorDot.draw(mouseX, mouseY, hue, 100, 100, 200, 255);// + 200 * sin(t));


    // Pine tree background
    //rotate(PI/2);
    //image(pine, 0, -y1, SIDE, imgHeight);
    //image(pine, 0, -y2, SIDE, imgHeight);

    y1 = (y1 + speed) % (imgHeight * 2);
    y2 = (y2 + speed) % (imgHeight * 2);




    // Translate the origin point to the center of the screen
    // (for other drawers)
    translate(SIDE/2, SIDE/2);

  }
}
