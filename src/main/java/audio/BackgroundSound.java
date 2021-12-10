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
