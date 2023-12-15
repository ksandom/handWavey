// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Play a .WAV file in a new thread so that it does not block the current thread.
*/

package audio;

import bug.ShouldComplete;

public class BackgroundSound extends Thread {
    private String fileName;
    private static ShouldComplete shouldCompleteSound = null;

    public BackgroundSound(String fileName) {
        this.fileName = fileName;
    }

    public void run() {
        SimplePlayer player = new SimplePlayer();
        player.playSound(this.fileName);
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value="LI_LAZY_INIT_STATIC",
        justification="There might be a better way of doing this. But it really doesn't matter if this isn't 100% reliable. The purpose of shouldComplete is to help catch something not finishing. If it fails the first time, it'll catch it the second time.")

    public static void play(String fileName) {
        if (BackgroundSound.shouldCompleteSound == null) {
            BackgroundSound.shouldCompleteSound = new ShouldComplete("BackgroundSound/play");
        }

        BackgroundSound.shouldCompleteSound.start(fileName);

        BackgroundSound sound = new BackgroundSound(fileName);
        sound.start();

        BackgroundSound.shouldCompleteSound.finish();
    }
}
