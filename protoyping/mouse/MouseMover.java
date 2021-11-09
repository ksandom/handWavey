import java.io.IOException;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;

class MouseX {
  public void shootingStar() {
    System.out.println("Shooting star.");
  
    for (int x=400; x<6000; x+=10) {
      Point p = new Point(x, 200);
      moveMouse(p);
      
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  public void upperLeftish() {
    System.out.println("Top left-ish.");
    
    Point p = new Point(100, 100);
    moveMouse(p);
  }
  
  public void moveMouse(Point p) {
    // Lifted from https://stackoverflow.com/questions/2941324/how-do-i-set-the-position-of-the-mouse-in-java
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();

    // Search the devices for the one that draws the specified point.
    for (GraphicsDevice device: gs) { 
      GraphicsConfiguration[] configurations = device.getConfigurations();
      for (GraphicsConfiguration config: configurations) {
        Rectangle bounds = config.getBounds();
        if(bounds.contains(p)) {
          // Set point to screen coordinates.
          Point b = bounds.getLocation(); 
          Point s = new Point(p.x - b.x, p.y - b.y);

          try {
            Robot r = new Robot(device);
            r.mouseMove(s.x, s.y);
          } catch (AWTException e) {
            e.printStackTrace();
          }

          return;
        }
      }
    }
    // Couldn't move to the point, it may be off screen.
    return;
  }
}

class MouseMover {
  public static void main(String[] args) {
    MouseX mouseX = new MouseX();
    
    mouseX.upperLeftish();
    mouseX.shootingStar();
    
  }
}
