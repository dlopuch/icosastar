package com.github.dlopuch.icosastar;


import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeMath.random;
import static processing.core.PConstants.HSB;

public class VertexPoppers {
  private static int POPPER_LIFE_MS = 300;
  private static int POPPER_SPAWN_MS = 200;

  private static float BASE_SIZE_PX = 10;
  private static float GROW_SIZE_PX = 270;

  PApplet p;

  List<IcosaVertex> verticies;
  List<VertexPopper> poppers;

  int lastPopperBirthMs = 0;

  VertexPoppers(PApplet parent, List<IcosaVertex> verticies) {
    this.p = parent;

    this.verticies = verticies;
    this.poppers = new ArrayList();

    parent.registerMethod("draw", this);
  }

  private void onPopperDie(VertexPopper popper) {
    this.poppers.remove(popper);
  }

  public void draw() {

    if (p.millis() - lastPopperBirthMs > POPPER_SPAWN_MS) {
      VertexPopper popper = new VertexPopper(
          this,
          this.verticies.get( (int) random(this.verticies.size()) )
      );
      lastPopperBirthMs = p.millis();
      poppers.add(popper);
    }

    try {
      // Occasionally fails with ConcurrentModificationException. dafuq?
      for (VertexPopper p : poppers) {
        p.draw();
      }
    } catch(Exception e) {
      // meh, just ignore
      //println(e, e.getStackTrace());
    }
  }

  int vId = 1;

  private class VertexPopper {
    int epoch = p.millis();
    IcosaVertex vertex;
    VertexPoppers parent;
    int id;
    float h;

    VertexPopper(VertexPoppers parent, IcosaVertex vertex) {
      this.vertex = vertex;
      this.parent = parent;
      this.id = vId++;
      this.h = 45 + p.random(20);
    }

    void draw() {
      float lifetimePct = (p.millis() - this.epoch) / POPPER_LIFE_MS;
      float size = BASE_SIZE_PX + GROW_SIZE_PX * lifetimePct;


      p.colorMode(HSB, 100);
      p.fill(this.h, 0, 120 * (1 - lifetimePct));
      p.ellipse(
          this.vertex.x,
          this.vertex.y,
          size, size
      ); //*/

      /*colorDot(
        this.vertex.x,
        this.vertex.y,

        // hsv:
        100,
        100,
        200,

        size,
        lifetimePct
      );// + 200 * sin(t)); */

      if (p.millis() - this.epoch > POPPER_LIFE_MS) {
        this.id *= -1;
        this.parent.onPopperDie(this);
      }

    }
  }
}
