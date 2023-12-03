// Sourced from: https://stackoverflow.com/questions/2416935/how-to-play-wav-files-with-java

/*
Play a .WAV file.
*/

package audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class SimplePlayer {

        private final int BUFFER_SIZE = 128000;
        private File soundFile;
        private AudioInputStream audioStream;
        private AudioFormat audioFormat;
        private SourceDataLine sourceLine;

        @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value="DLS_DEAD_LOCAL_STORE",
            justification="nBytesWritten is not needed. Yet the absense of it causes several syntax errors.")

        /**
         * @param filename the name of the file that is going to be played
         */
        public void playSound(String filename){

            String strFilename = filename;

            try {
                soundFile = new File(strFilename);
            } catch (Exception e) {
                e.printStackTrace();
                return ;
            }

            try {
                audioStream = AudioSystem.getAudioInputStream(soundFile);
            } catch (Exception e){
                e.printStackTrace();
                return ;
            }

            audioFormat = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            try {
                sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                sourceLine.open(audioFormat);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
