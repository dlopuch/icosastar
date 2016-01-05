float MIN_SIZE_KICK_PX = 30;
float MAX_SIZE_KICK_PX = 150;

float MIN_SIZE_SNARE_PX = 30;
float MAX_SIZE_SNARE_PX = 150;

float MIN_SIZE_HIHAT_PX = 30;
float MAX_SIZE_HIHAT_PX = 150;

float GROW_MULT = 2.0;
float DECAY_MULT = 0.96;

float DECAY_HIHAT_MULT = 0.90;

public class VertexFFT {
  List<IcosaVertex> kicks;
  List<IcosaVertex> snares;
  List<IcosaVertex> hihats;
  
  float rKick  = MIN_SIZE_KICK_PX;
  float rSnare = MIN_SIZE_SNARE_PX;
  float rHihat = MIN_SIZE_HIHAT_PX;
  
  BeatDetect beat;
  
  VertexFFT(
    PApplet parent, 
    BeatDetect beat, 
    List<IcosaVertex> kicks, 
    List<IcosaVertex> snares,
    List<IcosaVertex> hihats
  ) {
    this.kicks = new ArrayList(kicks);
    this.snares = new ArrayList(snares);
    this.hihats = new ArrayList(hihats);
    
    this.beat = beat;
    
    parent.registerDraw(this);
  }
  
  void draw() {
    // Kick drum
    // --------------
    if (beat.isKick()) {
      this.rKick = MAX_SIZE_KICK_PX; // min(rKick * GROW_MULT, MAX_SIZE_KICK_PX);
    }
    
    for (IcosaVertex v : this.kicks) {
      colorDot(
        v.x, v.y, 
        0, 0, 100, 
        this.rKick, 100
      );
    }
    
    // Snare drum
    // --------------
    if (beat.isSnare()) {
      this.rSnare = MAX_SIZE_SNARE_PX; // min(rKick * GROW_MULT, MAX_SIZE_KICK_PX);
    }
    
    for (IcosaVertex v : this.snares) {
      colorDot(
        v.x, v.y, 
        0, 0, 100, 
        this.rSnare, 100
      );
    }
    
    // Hihat drum
    // --------------
    if (beat.isHat()) {
      this.rHihat = min(rHihat * GROW_MULT, MAX_SIZE_HIHAT_PX);
    }
    
    for (IcosaVertex v : this.hihats) {
      colorDot(
        v.x, v.y, 
        0, 0, 100, 
        this.rHihat, 100
      );
    }
    
    // Decays
    // ---------
    this.rKick  = max(MIN_SIZE_KICK_PX , this.rKick  * DECAY_MULT);
    this.rSnare = max(MIN_SIZE_SNARE_PX, this.rSnare * DECAY_MULT);
    this.rHihat = max(DECAY_HIHAT_MULT, this.rHihat * DECAY_MULT);
  } 
}
