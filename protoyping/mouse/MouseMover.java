import java.io.IOException;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Point;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.DisplayMode;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

class MouseX {
  private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
  private GraphicsDevice[] gs = this.ge.getScreenDevices();
  private Robot robot = null;
  
  public MouseX() {
    try {
      this.robot = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
  }

  public Dimension getDesktopResolution() {
    int x = 0;
    int y = 0;
    int w = 0;
    int h = 0;
    
    for (GraphicsDevice device: this.gs) {
      GraphicsConfiguration[] configuration = device.getConfigurations();
      Rectangle screenBounds = configuration[0].getBounds();
      
      if (screenBounds.x > x) {
        x = screenBounds.x;
        w = x + screenBounds.width;
      }
      
      if (screenBounds.y > y) {
        y = screenBounds.y;
        h = y + screenBounds.height;
      }
      
      if (w == 0) {
        w = screenBounds.width;
      }
      
      if (h == 0) {
        h = screenBounds.height;
      }
    }
    
    return new Dimension(w, h);
  }
  
  public void info() {
    System.out.println("Info.");
    
    Dimension desktopResolution = getDesktopResolution();
    System.out.println("Desktop resolution: " + desktopResolution.width + " " + desktopResolution.height);
    
    Rectangle rectangle = this.ge.getMaximumWindowBounds();
    System.out.println("Primary display: " + rectangle.toString());
    
    Point centerPoint =  this.ge.getCenterPoint();
    System.out.println("  Center: " + centerPoint.toString());
    
    System.out.println("");
    
    GraphicsDevice[] gs = this.ge.getScreenDevices();
    int length = gs.length;
    String len = String.valueOf(length);
    System.out.println("Number of devices: " + len);
    
    for (int i = 0; i < gs.length; i ++) {
      String id = gs[i].getIDstring();
      System.out.println("  ID: " + id);
      
      DisplayMode dm = gs[i].getDisplayMode();
      System.out.println("    Mode via DisplayMode: " + String.valueOf(dm.getWidth()) + " " + String.valueOf(dm.getHeight()));
      
      GraphicsConfiguration[] gc = gs[i].getConfigurations();
      Rectangle gcBounds = gc[0].getBounds();
      System.out.println("    Rectangle(0): " + gcBounds.toString());
      
      System.out.println("");
    }
  }
  
  public void click(int button) {
    try {
      this.robot.mousePress(button);
      this.robot.mouseRelease(button);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }
  
  public void sleep(int microseconds) {
    try {
      Thread.sleep(microseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  /* ********************************************************************* */
  
  public void rightClickAndCancel() {
    System.out.println("rightClickAndCancel.");
    
    this.robot.mouseMove(650, 1500);
    click(InputEvent.BUTTON3_MASK);
    sleep(1000);
    this.robot.mouseMove(600, 1500);
    click(InputEvent.BUTTON1_MASK);
  }
  
  public void scroll() {
    System.out.println("scroll.");
    
    int interval = 600;
    
    this.robot.mouseMove(800, 2000);
    
    this.robot.mouseWheel(1);
    sleep(interval);
    this.robot.mouseWheel(1);
    sleep(interval);
    this.robot.mouseWheel(1);
    sleep(interval);
    this.robot.mouseWheel(1);
    sleep(interval);
    this.robot.mouseWheel(-1);
    sleep(interval);
    this.robot.mouseWheel(-300);
  }
  
  public void ctrlZoom() {
    System.out.println("ctrlZoom.");
    
    int interval = 600;
    
    this.robot.mouseMove(800, 1800);
    
    try {
      this.robot.keyPress(KeyEvent.VK_CONTROL);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
    
    this.robot.mouseWheel(1);
    sleep(interval);
    this.robot.mouseWheel(1);
    sleep(interval);
    this.robot.mouseWheel(-2);
    
    try {
      this.robot.keyRelease(KeyEvent.VK_CONTROL);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    }
  }
  
  public void shootingStar() {
    System.out.println("Shooting star.");
  
    for (int x=400; x<6000; x+=20) {
      this.robot.mouseMove(x, 200);
      sleep(10);
    }
  }
  
  public void fallingStar() {
    System.out.println("Falling star.");
  
    for (int y=400; y<4000; y+=20) {
      this.robot.mouseMove(200, y);
      sleep(10);
    }
  }
  
  public void upperLeftish() {
    System.out.println("Top left-ish.");
    
    this.robot.mouseMove(300, 300);
  }
  
  public void basicSet() {
    System.out.println("Basic set.");
    
    this.robot.mouseMove(6000, 200);
  }
  
  public void center() {
    System.out.println("Center.");
    
    Point centerPoint =  this.ge.getCenterPoint();
    System.out.println("Center: " + centerPoint.toString());
    
    this.robot.mouseMove(centerPoint.x, centerPoint.y);
  }
}

class MouseMover {
  public static void main(String[] args) {
    MouseX mouseX = new MouseX();
    
    mouseX.info();
//     mouseX.rightClickAndCancel();
//     mouseX.scroll();
    mouseX.ctrlZoom();
//     mouseX.shootingStar();
//     mouseX.fallingStar();
//     mouseX.upperLeftish();
//     mouseX.center();
//     mouseX.basicSet();
  }
}
