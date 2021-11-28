package ultraMotion;

import java.io.IOException;
import com.leapmotion.leap.*;
// import com.leapmotion.leap.Finger.*;
// import com.leapmotion.leap.Bone.Type.*;
import handWavey.HandSummary;
import handWavey.UltraMotionManager;
import debug.Debug;
import java.util.Arrays;

public class UltraMotionInput extends Listener {
    private UltraMotionManager ultraMotionManager = null;
    private Debug debug = new Debug(2, "UltraMotionInput");
    private HandSummary[] handSummaries = {null, null, null, null, null, null, null, null, null, null};
    private int lastHandCount = 0;
    private int maxHands = 2;

    public UltraMotionInput () {
        this.maxHands = maxHands;
    }

    public void setMaxHands(int maxHands) {
        // TODO This assumption is simply because I haven't put time into how to dynamically add more entries into the handSummaries array. If that is solved, this limit can be removed. But for now, 10 hands seems like way more than is ever going to be used... Famous last words.
        if (maxHands > 10) {
            maxHands = 10;
        }

        this.maxHands = maxHands;
        emptyHands();
    }

    private void emptyHands() {
        // This just simplifies the code later down since we only need to check for one absense.
        for (int i=0; i<this.maxHands; i++) {
            this.handSummaries[i]=null;
        }
    }
    
    public void setUltraMotionManager(UltraMotionManager ultraMotionManager) {
        this.ultraMotionManager = ultraMotionManager;
    }
    
    public void onInit(Controller controller) {
        this.debug.out(1, "UltraMotionInput: Initialized");
    }

    public void onConnect(Controller controller) {
        this.debug.out(1, "UltraMotionInput: Connected");
    }

    public void onDisconnect(Controller controller) {
        this.debug.out(1, "UltraMotionInput: Disconnected");
        
        if (this.ultraMotionManager != null) {
            this.ultraMotionManager.exit();
        }
    }

    public void onExit(Controller controller) {
        this.debug.out(1, "UltraMotionInput: Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();
        this.debug.out(2, "UltraMotionInput: Frame id: " + frame.id()
                         + ", timestamp: " + frame.timestamp()
                         + ", hands: " + frame.hands().count());

        int handCount = frame.hands().count();
        int handNumber = 0;

        // No hands? Let's clean our state.
        if (handCount == 0) {
            emptyHands();
        }

        //Get hands
        for(Hand hand : frame.hands()) {
            if (handNumber < this.maxHands) {
                if (this.lastHandCount > handCount) {
                    // If we aren't tracking this hand yet. Let's do so.
                    if (this.handSummaries[handNumber] == null) {
                        this.handSummaries[handNumber] = new HandSummary(hand.id());
                    }

                    if (this.handSummaries[handNumber].getID() == hand.id()) {
                        Vector handPosition = hand.palmPosition();
                        this.handSummaries[handNumber].setHandPosition(
                            handPosition.getX(),
                            handPosition.getY(),
                            handPosition.getZ()
                            );

                        Vector handNormal = hand.palmNormal();
                        Vector handDirection = hand.direction();
                        this.handSummaries[handNumber].setHandAngles(
                            handNormal.roll(),
                            handDirection.pitch(),
                            handDirection.yaw()
                            );

                        Vector armDirection = hand.arm().direction();
                        this.handSummaries[handNumber].setArmAngles(
                            armDirection.roll(),
                            armDirection.pitch(),
                            armDirection.yaw()
                            );

                        // TODO figure out if the hand is open or closed. this probably needs to be compared to the palm position to make sense.
//                         Vector middleDistalBone = hand.finger(TYPE_MIDDLE).bone(TYPE_DISTAL).direction();

                    } else {
                        this.handSummaries[handNumber].markInvalid();
                    }
                }

                String handType = hand.isLeft() ? "Left hand" : "Right hand";
                this.debug.out(2, "  " + handType + ", id: " + hand.id()
                                 + ", palm position: " + hand.palmPosition() + " "
                                 + hand.palmPosition().getClass().getName());

                // Get the hand's normal vector and direction
                Vector normal = hand.palmNormal();
                Vector direction = hand.direction();

                // Calculate the hand's pitch, roll, and yaw angles
    //             this.debug.out(2, "  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
    //                              + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
    //                              + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees");
                handNumber++;
            } else {
                // TODO break the loop?
            }
        }
        this.lastHandCount = handCount;
    }
}
