import java.io.IOException;

import org.eclipse.swt.widgets.Display;

// import java.awt.AWTException;
// import java.awt.Robot;
//import java.awt.Point;
// import java.awt.GraphicsEnvironment;
// import java.awt.GraphicsDevice;
// import java.awt.GraphicsConfiguration;
// import java.awt.Point;
// import java.awt.DisplayMode;
// import java.awt.Point;
// import java.awt.Toolkit;
// import java.awt.event.InputEvent;
// import java.awt.event.KeyEvent;

import org.eclipse.swt.graphics.Point;

// For more experimentation.
import org.eclipse.swt.graphics.*;


class MouseX {
  //private Display display = Display.getDefault();
  private Display display = new Display();

  public MouseX() {


//     try {
//       this.robot = new Robot();
//     } catch (AWTException e) {
//       e.printStackTrace();
//     }
  }

  public Rectangle getDesktopResolution() {
    int w = 0;
    int h = 0;
    
    // TODO Check that this behaves correctly with multiple displays.
    Rectangle screenBounds = this.display.getBounds();
    w = screenBounds.x;
    h = screenBounds.y;

    return new Rectangle(0, 0, w, h);
  }
  
  public void info() {
    System.out.println("Info.");
    
    Point pointerLocation = display.getCursorLocation();
    System.out.println("Pointer location: " + pointerLocation.toString());

    Rectangle desktopResolution = getDesktopResolution();
    System.out.println("Desktop resolution: " + desktopResolution.width + " " + desktopResolution.height);
  }
  
//   public void click(int button) {
//     try {
//       this.robot.mousePress(button);
//       this.robot.mouseRelease(button);
//     } catch (IllegalArgumentException e) {
//       e.printStackTrace();
//     }
//   }
  
  public void sleep(int microseconds) {
    try {
      Thread.sleep(microseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  /* ********************************************************************* */
  
//   public void rightClickAndCancel() {
//     System.out.println("rightClickAndCancel.");
//
//     this.robot.mouseMove(650, 1500);
//     click(InputEvent.BUTTON3_MASK);
//     sleep(1000);
//     this.robot.mouseMove(600, 1500);
//     click(InputEvent.BUTTON1_MASK);
//   }
//
//   public void scroll() {
//     System.out.println("scroll.");
//
//     int interval = 600;
//
//     this.robot.mouseMove(800, 2000);
//
//     this.robot.mouseWheel(1);
//     sleep(interval);
//     this.robot.mouseWheel(1);
//     sleep(interval);
//     this.robot.mouseWheel(1);
//     sleep(interval);
//     this.robot.mouseWheel(1);
//     sleep(interval);
//     this.robot.mouseWheel(-1);
//     sleep(interval);
//     this.robot.mouseWheel(-300);
//   }
//
//   public void ctrlZoom() {
//     System.out.println("ctrlZoom.");
//
//     int interval = 600;
//
//     this.robot.mouseMove(800, 1800);
//
//     try {
//       this.robot.keyPress(KeyEvent.VK_CONTROL);
//     } catch (IllegalArgumentException e) {
//       e.printStackTrace();
//     }
//
//     this.robot.mouseWheel(1);
//     sleep(interval);
//     this.robot.mouseWheel(1);
//     sleep(interval);
//     this.robot.mouseWheel(-2);
//
//     try {
//       this.robot.keyRelease(KeyEvent.VK_CONTROL);
//     } catch (IllegalArgumentException e) {
//       e.printStackTrace();
//     }
//   }
//
//   public void shootingStar() {
//     System.out.println("Shooting star.");
//
//     for (int x=400; x<6000; x+=20) {
//       this.robot.mouseMove(x, 200);
//       sleep(10);
//     }
//   }
//
//   public void fallingStar() {
//     System.out.println("Falling star.");
//
//     for (int y=400; y<4000; y+=20) {
//       this.robot.mouseMove(200, y);
//       sleep(10);
//     }
//   }
//
//   public void upperLeftish() {
//     System.out.println("Top left-ish.");
//
//     this.robot.mouseMove(300, 300);
//   }
//
//   public void basicSet() {
//     System.out.println("Basic set.");
//
//     this.robot.mouseMove(6000, 200);
//   }
  
  public void center() {
    System.out.println("Center.");
    
    Rectangle desktopResolution = getDesktopResolution();
    int x = Math.round(desktopResolution.width/2);
    int y = Math.round(desktopResolution.height/2);

    Point centerPoint = new Point(x, y);
    Point arb = new Point(10, 10);
    System.out.println("Center: " + centerPoint.toString());
    
    //this.robot.mouseMove(centerPoint.x, centerPoint.y);
    this.display.setCursorLocation(arb);

  }
}

class MouseMover {
  public static void main(String[] args) {
    MouseX mouseX = new MouseX();
    
    mouseX.info();
//     mouseX.rightClickAndCancel();
//     mouseX.scroll();
//     mouseX.ctrlZoom();
//     mouseX.shootingStar();
//     mouseX.fallingStar();
//     mouseX.upperLeftish();
    mouseX.center();
//     mouseX.basicSet();
  }
}
