package com.github.dlopuch.icosastar;

import java.util.ArrayList;
import java.util.List;

public class IcosaVertex {
  float x;
  float y;
  float[] xy;

  List<IcosaVertex> adjacents = new ArrayList();

  IcosaVertex(float[] xy) {
    this.x = xy[0];
    this.y = xy[1];
    this.xy = xy;
  }

  public void addAdjacent(IcosaVertex adjacent) {
    this.adjacents.add(adjacent);
    adjacent.adjacents.add(this);
  }
}
