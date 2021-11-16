package ultraMotion;

import java.io.IOException;
import com.leapmotion.leap.*;
import handWavey.UltraMotionManager;

public class UltraMotionInput extends Listener {
    private UltraMotionManager ultraMotionManager = null;
    
    public UltraMotionInput () {
    }
    
    public void setUltraMotionManager(UltraMotionManager ultraMotionManager) {
        this.ultraMotionManager = ultraMotionManager;
    }
    
    public void onInit(Controller controller) {
        System.out.println("UltraMotionInput: Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("UltraMotionInput: Connected");
    }

    public void onDisconnect(Controller controller) {
        System.out.println("UltraMotionInput: Disconnected");
        
        if (this.ultraMotionManager != null) {
            this.ultraMotionManager.exit();
        }
    }

    public void onExit(Controller controller) {
        System.out.println("UltraMotionInput: Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        System.out.println("UltraMotionInput: Frame id: " + frame.id()
                         + ", timestamp: " + frame.timestamp()
                         + ", hands: " + frame.hands().count());

        //Get hands
        for(Hand hand : frame.hands()) {
            String handType = hand.isLeft() ? "Left hand" : "Right hand";
            System.out.println("  " + handType + ", id: " + hand.id()
                             + ", palm position: " + hand.palmPosition());

            // Get the hand's normal vector and direction
            Vector normal = hand.palmNormal();
            Vector direction = hand.direction();

            // Calculate the hand's pitch, roll, and yaw angles
            System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
                             + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
                             + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");
        }
    }
}
