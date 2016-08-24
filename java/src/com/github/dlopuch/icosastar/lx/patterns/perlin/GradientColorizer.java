package com.github.dlopuch.icosastar.lx.patterns.perlin;

import com.github.dlopuch.icosastar.lx.utils.GradientSupplier;
import heronarts.lx.model.LXPoint;

/**
 * Colorizes perlin points based off a palette of gradients from a GradientSupplier
 */
public class GradientColorizer extends PerlinNoiseColorizer {
  public final GradientSupplier gradientSupplier;

  public GradientColorizer(LXPerlinNoiseExplorer noiseSource, GradientSupplier gradientSupplier) {
    super(noiseSource);
    this.gradientSupplier = gradientSupplier;
  }

  @Override
  public GradientColorizer rotate() {
    gradientSupplier.setRandomGradient();
    return this;
  }

  @Override
  public int getColor(LXPoint point) {
    return gradientSupplier.getColor(noiseSource.getNoise(point.index));
  }
}
