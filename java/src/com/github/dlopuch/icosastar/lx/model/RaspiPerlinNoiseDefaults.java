package com.github.dlopuch.icosastar.lx.model;

import com.github.dlopuch.icosastar.lx.patterns.PerlinNoisePattern;
import com.github.dlopuch.icosastar.lx.utils.AudioDetector;
import com.github.dlopuch.icosastar.lx.utils.RaspiGpio;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.function.Function;

/**
 * Applies perlin noise pattern listeners when raspi GPIO is present
 */
public class RaspiPerlinNoiseDefaults {
  static int wipeCount = 0;
  static double timeSinceLastWipeCountReset = System.currentTimeMillis();

  public static void applyPresetsIfRaspiGpio(PerlinNoisePattern perlinNoise) {
    if (!RaspiGpio.isActive()) {
      return;
    }

    // Brightnesses:
    // ----------------
    // baseBrightness is set depending on whether line-in audio is working.
    // If not working, up to .75
    // If working, up to .90
    RaspiGpio.DipSwitchListener defaultDipSwitchListener = (float dipValuef) -> {
      perlinNoise.maxBrightness.setValue(dipValuef == 0 ? 10 : 100);
      if (!AudioDetector.LINE_IN.isRunning()) {
        // baseBrightness at 85 and above seems to trip the battery breaker.
        // Keep it at .75 -- no real perceptable increase in brightness.
        perlinNoise.baseBrightnessPct.setValue(dipValuef * 0.75f);
      } else {
        // Push it if audio is working... yolo
        perlinNoise.baseBrightnessPct.setValue(dipValuef * 0.90f);
      }

      System.out.println("PerlinNoisePattern maxBrightness changed to:" + perlinNoise.maxBrightness.getValue());
      System.out.println("PerlinNoisePattern baseBrightnessPct changed to:" + perlinNoise.baseBrightnessPct.getValue());
    };
    perlinNoise.maxBrightness.setValue(100);
    RaspiGpio.addDipSwitchListener(defaultDipSwitchListener);


    // Pattern rotations:
    // ----------------
    // Black moment causes us to move to next pattern
    RaspiGpio.blackMoment.addListener(new GpioPinListenerDigital() {
      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if (event.getState().isHigh()) {
          perlinNoise.rotate();
        }
      }
    });

    // Sleep Toggle
    // -------------
    // Flipping this toggle switch sends perlin noise to sleep model (noise-reactive off, dim) or
    // awake (noise-reactive on, bright)
    class SleepTracker {
      // Gymnastics class to get around "variable used in lambda expression should be final or effectively final"
      public boolean isSleeping = true;
      public boolean isBright = false;
    }
    final SleepTracker sleep = new SleepTracker();

    Function setSleepBrightness = (Object whatever) -> {
      if (sleep.isBright) {
        defaultDipSwitchListener.onDipSwitchChange(RaspiGpio.getDipValuef());
      } else {
        perlinNoise.maxBrightness.setValue(38);
        perlinNoise.baseBrightnessPct.setValue(0.7);
      }
      return true;
    };

    Function<Boolean, Boolean> onSleepToggle = (Boolean toggleIsHigh) -> {
      if (toggleIsHigh) {
        System.out.println("Perlin sleep toggle activated. In-java muting");
        AudioDetector.mute = true; // turn off noise pulses
        setSleepBrightness.apply(null);
        sleep.isSleeping = true;
      } else {
        AudioDetector.mute = false;
        defaultDipSwitchListener.onDipSwitchChange(RaspiGpio.getDipValuef());
        sleep.isSleeping = false;
      }
      return true;
    };
    RaspiGpio.toggle.addListener(new GpioPinListenerDigital() {
      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        onSleepToggle.apply(event.getState().isHigh());
      }
    });
    onSleepToggle.apply(RaspiGpio.toggle.isHigh());


    // Yellow Moment:
    // -------------
    // When sleeping, toggles between bright sleep and dim sleep
    // ~~When not sleeping, toggles between auto-rotate and stay-on-same-pattern~~
    // TODO: Not sleeping currently triggers white wipes and sparkles.
    // Ten wipes activates the sparkle mode (10! 9! 8! ...).  A couple more turns sparkles off.  Count reset 30 sec
    // after last wipe.
    RaspiGpio.yellowMoment.addListener(new GpioPinListenerDigital() {
      @Override
      public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        if (event.getState().isHigh()) {
          if (sleep.isSleeping) {
            sleep.isBright = !sleep.isBright;
            setSleepBrightness.apply(null);
          } else {
            //perlinNoise.rotateColorizer.setValue(!perlinNoise.rotateColorizer.getValueb());

            perlinNoise.startRandomWipe();

            if (System.currentTimeMillis() - timeSinceLastWipeCountReset > 30000) {
              wipeCount = 1;
              timeSinceLastWipeCountReset = System.currentTimeMillis();
            } else {
              wipeCount++;
            }
            if (wipeCount >= 15) {
              perlinNoise.triggerSparklers(false);
            } else if (wipeCount > 10) {
              perlinNoise.triggerSparklers(true);
            } else {
              perlinNoise.triggerSparklers(false);
            }


          }
        }
      }
    });
  }
}
