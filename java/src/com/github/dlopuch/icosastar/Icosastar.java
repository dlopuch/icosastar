package com.github.dlopuch.icosastar;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.Arrays;

public class Icosastar extends PApplet {
  static public void main(String args[]) {
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.Icosastar" });
  }

  LEDMapping ledMapping;
  VertexPoppers vertexPoppers;
  ColorDot colorDot;
//  RainbowSpiral rainbowSpiral;
  IcosaFFT icosaFft = new IcosaFFT(this);
  FFTSpiral fftSpiral;

  int SIDE = Config.SIDE;

  PImage pine;
  PImage fftColors;


  float colorOffset = 0;
  float colorOffsetRadiansPerBucket = radians(1.5f);


  public void setup() {
    size(SIDE, SIDE, P3D); //3D to force GPU blending
    translate(SIDE/2, SIDE/2);

    pine = loadImage("pine1.jpg");
    fftColors = loadImage("fftColors.png");
    colorDot = new ColorDot(this);

    ledMapping = new LEDMapping();

    // Enable some implementations:
    // --------
    //vertexPoppers = new VertexPoppers(this, ledMapping.verticies);
    //rainbowSpiral = new RainbowSpiral(this);
    new VertexFFT(this, colorDot, icosaFft.beat,
        Arrays.asList(ledMapping.ring2Vs), // bottom ring
        Arrays.asList(ledMapping.ring1Vs), // middle ring
        Arrays.asList(ledMapping.center)
    );
    fftSpiral = new FFTSpiral(this, colorDot.dot, icosaFft, fftColors);
    // ---------

    // Keep Last!
    ledMapping.registerDraw(this);
  }

  public void mousePressed() {
    //h = (h + 10) % 100;
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

    this.noStroke();
    this.fill(255);
    int SPECTRUM_BUCKET_WIDTH_PX = 1;
    int SPECTRUM_HEIGHT = 20;
    float[] fftSpectrum = icosaFft.getFilter();
    for (int i=0; i<fftSpectrum.length; i++) {
      this.rect(
          i * SPECTRUM_BUCKET_WIDTH_PX,
          SIDE,
          SPECTRUM_BUCKET_WIDTH_PX,
          -fftSpectrum[i] * SPECTRUM_HEIGHT
      );
    }


    // Translate the origin point to the center of the screen
    // (for other drawers)
    translate(SIDE/2, SIDE/2);

  }
}
