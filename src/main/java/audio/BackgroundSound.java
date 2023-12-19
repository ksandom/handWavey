// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Play a .WAV file in a new thread so that it does not block the current thread.
*/

package audio;

import bug.ShouldComplete;
import debug.Debug;
import config.*;
import handWavey.HandWaveyEvent;
import java.io.File;


public class BackgroundSound extends Thread {
    private String fileName;
    private static ShouldComplete shouldCompleteSound = null;
    private static int concurrentCount = 0;
    private static int maxCount = 8;
    private static Boolean maxExceeded = false;
    private static Debug debug = Debug.getDebug("BackgroundSound");
    private static Boolean configured = false;
    private static String bugPath = "";

    public BackgroundSound(String fileName) {
        this.fileName = fileName;
    }

    public void run() {
        SimplePlayer player = new SimplePlayer();
        player.playSound(this.fileName);
    }

    public static void play(String fileName) {
        if (!BackgroundSound.begin()) {
            BackgroundSound.end();
            if (maxExceeded == false) {
                // Only play the bug noise once per incident.
                BackgroundSound.playBugNotification();
                maxExceeded = true;
            }

            return ;
        }

        BackgroundSound.playFile(fileName);
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="LI_LAZY_INIT_STATIC",
        justification="There might be a better way of doing this. But it really doesn't matter if this isn't 100% reliable. The purpose of shouldComplete is to help catch something not finishing. If it fails the first time, it'll catch it the second time.")

    private static void playFile(String fileName) {
        if (BackgroundSound.shouldCompleteSound == null) {
            BackgroundSound.shouldCompleteSound = new ShouldComplete("BackgroundSound/play");
        }

        BackgroundSound.shouldCompleteSound.start(fileName);

        BackgroundSound sound = new BackgroundSound(fileName);
        sound.start();

        BackgroundSound.shouldCompleteSound.finish();
    }

    private static void playBugNotification() {
        BackgroundSound.playFile(BackgroundSound.bugPath);
    }

    public static Boolean begin() {
        Boolean result = true;
        BackgroundSound.concurrentCount++;

        BackgroundSound.setup();

        if (BackgroundSound.concurrentCount > BackgroundSound.maxCount) {
            BackgroundSound.debug.out(1, "Reached maxCount. Not going to play any more sounds until this subsides.");
            result = false;
        }

        return result;
    }

    public static void end() {
        BackgroundSound.concurrentCount--;

        if (BackgroundSound.concurrentCount == 0) {
            maxExceeded = false;
        }
    }

    private static void setup() {
        // Immediately return if it's already configured.
        if (BackgroundSound.configured) {
            return ;
        }

        // Set up how the limit for how many sounds can play at once.
        BackgroundSound.maxCount = Integer.parseInt(Config.singleton().getGroup("audioConfig").getItem("maxCount").get());

        // Figure out the file to play.
        String audioPath = Config.singleton().getGroup("audioConfig").getItem("pathToAudio").get() + File.separator;
        String bugFile = Config.singleton().getGroup("audioEvents").getItem("bug").get();

        BackgroundSound.bugPath = audioPath + bugFile;

        // Make sure we don't do this again for this session.
        BackgroundSound.configured = true;
    }
}
