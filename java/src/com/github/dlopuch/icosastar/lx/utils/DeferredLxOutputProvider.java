package com.github.dlopuch.icosastar.lx.utils;

import heronarts.lx.output.LXOutput;

/**
 * Wiring/dependency hell kludge
 */
public abstract class DeferredLxOutputProvider {
  public abstract LXOutput getOutput();
}
