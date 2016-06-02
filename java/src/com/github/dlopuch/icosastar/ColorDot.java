package com.github.dlopuch.icosastar;

import processing.core.PApplet;
import processing.core.PImage;

import static processing.core.PConstants.*;

/**
 * Draws a copy of the "dot" image with a color tint.
 */
public class ColorDot {

  private PApplet p;
  PImage dot;

  public ColorDot(PApplet parent) {
    this.p = parent;
    this.dot = p.loadImage("dot.png");
  }

  public void draw(float x, float y, float hue, float saturation, float brightness, float size, float transparency) {
    p.blendMode(ADD);
    p.colorMode(HSB, 100);
    p.tint(hue, saturation, brightness);

    p.image(dot, x - size/2, y - size/2, size, size);

    p.noTint();
    p.colorMode(RGB, 255);
    p.blendMode(NORMAL);
  }
}
