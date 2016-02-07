
public class FFTSpiral {
  
  PImage dot;
  IcosaFFT icosaFft;
  PImage fftColors;
  
  // Default parameters, change per instance if need be
  float minSize = 0.1;
  float sizeScale = 0.3;
  float radiansPerBucket = radians(1.5);
  float spin = 0.001;
  float opacity = 25;
  
  
  FFTSpiral(PApplet parent, PImage dot, IcosaFFT fft, PImage paletteImage) {
    parent.registerDraw(this);
    this.dot = dot;
    this.icosaFft = fft;
    this.fftColors = paletteImage;
  }
  
  void draw() {
    float[] fftFilter = this.icosaFft.getFilter();
    colorMode(RGB);
    
    for (int i = 0; i < fftFilter.length; i += 3) {
      // tall palettes (sample height)   
      /*color rgb = this.fftColors.get(
        fftColors.width/2,
        int(map(i, 0, fftFilter.length-1, 0, this.fftColors.height-1))
      );// */
      
      // wide palettes (sample width)
      color rgb = this.fftColors.get(
        int(map(i, 0, fftFilter.length-1, 0, this.fftColors.width-1)), 
        fftColors.height/2
      ); 
      
      tint(rgb, fftFilter[i] * this.opacity);
      blendMode(ADD);
   
      // Size of the bucket's dot overlay
      float size = height * (this.minSize + this.sizeScale * fftFilter[i]);
      
      // Make a vector for the bucket
      PVector center = new PVector(width * (fftFilter[i] * 0.2), 0);
      center.rotate((millis() * this.spin)  +  (i * this.radiansPerBucket));
   
      image(this.dot, center.x - size/2, center.y - size/2, size, size);
    }
  }
}
