// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Gets the UltraMotionInput up and running, which does all of the hard work.

This is used for receiving data from the UltraMotion/LeapMotion device for tracking hand positions.
*/

package handWavey;

import ultraMotion.*;
import com.leapmotion.leap.*;
import com.leapmotion.leap.Controller.PolicyFlag;
import handWavey.HandWaveyManager;

public final class UltraMotionManager {
    private UltraMotionInput ultraMotionInput = new UltraMotionInput();
    private Controller controller = new Controller();
    private HandWaveyManager handWaveyManager;

    private Boolean active = false;

    public UltraMotionManager (HandWaveyManager hwm) {
        this.handWaveyManager = hwm;

        // Make sure that we can run in the background on windows.
        if (!this.controller.isPolicySet(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES)) {
            this.controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
        }

        this.controller.addListener(ultraMotionInput);

        this.active = true;

        this.ultraMotionInput.setUltraMotionManager(this);
    }

    public HandWaveyManager getHandWaveyManager() {
        return this.handWaveyManager;
    }

    public void keepAlive() {
        this.active = true;

        while (this.active) {
            sleep(500);
        }

        cleanup();
    }

    public void keepAliveUntilBreak() {
        while (true) {
            sleep(500);
            // TODO I don't think that the cleanup() is being called at the moment. I'm not sure what the consequences of that are, but as a general rule, we should run cleanup when exiting. It would be great to determine if it's happening, and fix it if it needs to be fixed.
        }
    }

    public void exit() {
        this.active = false;
    }

    public void cleanup () {
        this.controller.removeListener(this.ultraMotionInput);
    }

    public void sleep(int microseconds) {
        try {
            Thread.sleep(microseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Cleanup");
            cleanup();
        }
    }
}
