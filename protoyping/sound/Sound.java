import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class MakeSound {

    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;

    /**
     * @param filename the name of the file that is going to be played
     */
    public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
}

class SoundPlayer {
  public SoundPlayer() {
    System.out.println("SoundPlayer.");
  }

  public void sleep(int microseconds) {
    try {
      Thread.sleep(microseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
}

class BackgroundSound extends Thread {
  String fileName = "";
  
  public BackgroundSound(String fileName) {
    this.fileName = fileName;
  }
  
  public void run() {
    MakeSound sound = new MakeSound();
    System.out.println("Thread: Going to play " + this.fileName);
    sound.playSound(this.fileName);
  }
}

class Sound {
  public static void main(String[] args) {
        System.out.println("Sound.");
        
        for (int i=0; i<3; i++) {
          BackgroundSound sound = new BackgroundSound("../../audio/clips/metalDing01.wav");
          sound.start();
          System.out.println("Returned from triggering sound.");
          Sound.sleep(300);
        }
        
        Sound.sleep(1000);
        System.out.println("Exiting.");
  }
  
  public static void sleep(int microseconds) {
    try {
      Thread.sleep(microseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
