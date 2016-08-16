package com.github.dlopuch.icosastar.effects.utils;

import processing.core.PApplet;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Buffer that can count how many events happened within the last x milliseconds
 */
public class EventFrequencyCounter {
  private final long numMillisToTrack;

  // How big the buffer is allowed to get before we recycle
  private final int MAX_BUFFER_SIZE = 1024;

  private LinkedList<Long> events = new LinkedList<>();
  Long lastEvent;

  public EventFrequencyCounter() {
    this(null);
  }

  public EventFrequencyCounter(Long numMillisToTrack) {
    this.numMillisToTrack = numMillisToTrack == null ? 5000 : numMillisToTrack;
  }

  public void tick() {
    events.push(System.currentTimeMillis());
  }

  public float getFrequencyPerSec() {
    return getFrequencyPerSec(numMillisToTrack);
  }

  public float getFrequencyPerSec(long lastXMillis) {
    Long minTime = System.currentTimeMillis() - lastXMillis;

    // OPTIMIZATION: If not at max buffer, just count how many events are within range
    if (events.size() < MAX_BUFFER_SIZE) {
      int numEvents = 0;
      for (Long eventMs : events) {
        if (eventMs < minTime)
          break;

        numEvents++;
      }
      return (float)numEvents / (float)lastXMillis * 1000f;
    }

    // Otherwise, need to clear the buffer.  Create a new list with only the passing events.
    PApplet.println("clearing buffer up to " + minTime);
    LinkedList<Long> newEvents = new LinkedList<>();
    for (Long eventMs : events) {
      if (eventMs < minTime)
        break;

      newEvents.add(eventMs);
    }
    events = newEvents;

    return (float)events.size() / (float)lastXMillis * 1000f;
  }
}
