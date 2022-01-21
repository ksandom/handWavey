// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Play a .WAV file in a new thread so that it does not block the current thread.
*/

package audio;

public class BackgroundSound extends Thread {
    String fileName;
    
    public BackgroundSound(String fileName) {
        this.fileName = fileName;
    }
    
    public void run() {
        SimplePlayer player = new SimplePlayer();
        player.playSound(this.fileName);
    }
    
    public static void play(String fileName) {
        BackgroundSound sound = new BackgroundSound(fileName);
        sound.start();
    }
}
