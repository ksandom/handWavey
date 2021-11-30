package ultraMotion;

import config.Config;
import config.*;
import com.leapmotion.leap.*;
import handWavey.HandSummary;
import handWavey.UltraMotionManager;
import debug.Debug;
import java.util.Arrays;

public class UltraMotionInput extends Listener {
    private UltraMotionManager ultraMotionManager = null;
    private Debug debug;
    private HandSummary[] handSummaries = {null, null, null, null, null, null, null, null, null, null};
    private int lastHandCount = 0;
    private int maxHands = 2;
    private float openThreshold = 0;
    private float pi = (float)3.1415926536;
    
    private int fingerToUse = 2;
    private Bone.Type boneToUse = null;

    public UltraMotionInput () {
        this.boneToUse = Bone.Type.values()[Bone.Type.values().length-1];
        
        Group ultraMotionConfig = Config.singleton().getGroup("ultraMotion");
        this.openThreshold = Float.parseFloat(ultraMotionConfig.getItem("openThreshold").get());
        this.maxHands = Integer.parseInt(ultraMotionConfig.getItem("maxHands").get());
        
        int debugLevel = Integer.parseInt(ultraMotionConfig.getItem("debugLevel").get());
        this.debug = new Debug(debugLevel, "UltraMotionInput");
    }

    public void setMaxHands(int maxHands) {
        // TODO This assumption is simply because I haven't put time into how to dynamically add more entries into the handSummaries array. If that is solved, this limit can be removed. But for now, 10 hands seems like way more than is ever going to be used... Famous last words.
        if (maxHands > 10) {
            maxHands = 10;
        }

        this.maxHands = maxHands;
        emptyHands();
    }
    
    public void setOpenThreshold(float openThreshold) {
        this.openThreshold = openThreshold;
    }

    private void emptyHands() {
        // This just simplifies the code later down since we only need to check for one absense.
        for (int i = 0; i<this.maxHands; i++) {
            this.handSummaries[i] = null;
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

                    Float middleDistalBonePitch = hand.fingers().get(this.fingerToUse).bone(this.boneToUse).direction().pitch();
                    Float relativeFingerPitch = mangleAngle(middleDistalBonePitch) + handDirection.pitch();
                    Float fingerDifference = Math.abs(relativeFingerPitch);
                    Boolean handOpen = (fingerDifference > openThreshold);
                    
                    this.handSummaries[handNumber].setHandOpen(handOpen);

                } else {
                    this.handSummaries[handNumber].markInvalid();
                    this.debug.out(0, "Discarding hand " + String.valueOf(this.handSummaries[handNumber].getID()) + " != " + String.valueOf(hand.id()));
                }
            } else {
                // If we have more hands than we are configured to handle, let's just stop processing the extra hands.
                break;
            }
        }
        this.lastHandCount = handCount;
    }
    
    private float mangleAngle(float angle) {
        // Moves the center by 180 degrees. I didn't get rotation working reliably.
        // TODO Revisit whether getting rotation working reliably would be a better solution. There must be no jump when rotating past the boundary where an individual angle loops.
        
        int sign = (angle < 0)?-1:1;
        float invertedValue = this.pi - Math.abs(angle);
        
        return invertedValue * (float)sign;
    }
    
    private float fixCenter(float value) {
        return combineAngles(value, this.pi);
    }
    
    private float combineAngles(float value, float rotateAmount) {
        // TODO There must be a better way to do this, but it will do for now.
        
        float combined = value + rotateAmount + this.pi;
        combined = combined % (this.pi * 2);
        combined = combined - this.pi;
        
        return combined;
    }
}
