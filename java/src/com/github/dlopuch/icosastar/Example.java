package com.github.dlopuch.icosastar;

import processing.core.PApplet;

/**
 * HEYDAN Notes on how to startup a processing sketch in IDE:
 *   - Add opengl libs as IDE jar libraries: gluegen-rt, gluegen-rt-natives-*, jogl-all, jogl-all-natives-*
 *   - Copy processing core.jar to a lib/ folder.  Needed because path determination of data folder (loadImage() etc)
 *     is set relative to that jar -- data/ directory will be sibling to lib/.  Apparently.
 *       - Then add lib/ as an IDE jar library.
 *   - Add a static public void main() with line: PApplet.main(new String[] { "com.github.dlopuch.icosastar.Example" });
 */
public class Example extends PApplet {
  static public void main(String args[]) {
    System.out.println("HEYDAN BOOTING UP.");
    PApplet.main(new String[] { "com.github.dlopuch.icosastar.Example" });
  }

  float x = 100;
  float y = 100;
  float angle1 = 0.0f;
  float segLength = 50;

  public void setup() {
    size(640, 360, P3D);
    strokeWeight(20.0f);
    stroke(255, 100);
  }

  public void draw() {
    background(0);

    float dx = mouseX - x;
    float dy = mouseY - y;
    angle1 = atan2(dy, dx);
    x = mouseX - (cos(angle1) * segLength);
    y = mouseY - (sin(angle1) * segLength);

    segment(x, y, angle1);
    ellipse(x, y, 20, 20);
  }

  void segment(float x, float y, float a) {
    pushMatrix();
    translate(x, y);
    rotate(a);
    line(0, 0, segLength, 0);
    popMatrix();
  }
}
