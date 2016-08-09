package com.github.dlopuch.icosastar;

import java.util.ArrayList;
import java.util.List;

public class IcosaVertex {
  public final float x;
  public final float y;
  public final float[] xy;

  List<IcosaVertex> adjacents = new ArrayList();

  public IcosaVertex(float[] xy) {
    this.x = xy[0];
    this.y = xy[1];
    this.xy = xy;
  }

  public void addAdjacent(IcosaVertex adjacent) {
    this.adjacents.add(adjacent);
    adjacent.adjacents.add(this);
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }
}
