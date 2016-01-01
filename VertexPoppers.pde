int POPPER_LIFE_MS = 300;
int POPPER_SPAWN_MS = 200;

float BASE_SIZE_PX = 10;
float GROW_SIZE_PX = 270;

public class VertexPoppers {
  List<IcosaVertex> verticies;
  List<VertexPopper> poppers;
  
  float lastPopperBirthMs = 0;
  
  VertexPoppers(PApplet parent, List<IcosaVertex> verticies) {
    this.verticies = verticies;
    this.poppers = new ArrayList();
    
    parent.registerDraw(this);
  }
  
  private void onPopperDie(VertexPopper popper) {
    this.poppers.remove(popper);
  }
  
  void draw() {
    
    if (millis() - lastPopperBirthMs > POPPER_SPAWN_MS) {
      VertexPopper p = new VertexPopper(
        this, 
        this.verticies.get( (int) random(this.verticies.size()) )
      );
      lastPopperBirthMs = millis();
      poppers.add(p);
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
    float epoch = millis();
    IcosaVertex vertex;
    VertexPoppers parent;
    int id;
    float h;
    
    VertexPopper(VertexPoppers parent, IcosaVertex vertex) {
      this.vertex = vertex;
      this.parent = parent;
      this.id = vId++;
      this.h = 45 + random(20);
    }
    
    void draw() {
      float lifetimePct = (millis() - this.epoch) / POPPER_LIFE_MS;
      float size = BASE_SIZE_PX + GROW_SIZE_PX * lifetimePct;
      
      
      colorMode(HSB, 100);
      fill(this.h, 0, 120 * (1 - lifetimePct));
      ellipse(
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
      
      if (millis() - this.epoch > POPPER_LIFE_MS) {
        this.id *= -1;
        this.parent.onPopperDie(this);
      }
      
    }
  }
}
