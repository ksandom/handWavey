// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
Provides the VNC output option by implementing the Output interface.
*/

package mouseAndKeyboardOutput;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Image;
import java.util.*;

import config.*;
import debug.Debug;

import com.shinyhut.vernacular.utils.KeySyms;
import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;
import com.shinyhut.vernacular.client.rendering.ColorDepth;


public class VNCOutput implements Output {
    private Debug debug;
    
    private Pressables keys = new Pressables();
    
    private VernacularConfig config;
    private VernacularClient client;
    
    private String host;
    private int port;
    private String password;
    
    private int width = 0;
    private int height = 0;
    private Boolean connected = false;
    
    public VNCOutput(String outputDevice) {
        this.debug = Debug.getDebug("VNCOutput");
        
        this.keys.defineKeys();
        
        this.config = new VernacularConfig();
        this.client = new VernacularClient(config);
        
        Group deviceConfig = Config.singleton().getGroup("output").getGroup(outputDevice);
        this.host = deviceConfig.getItem("host").get();
        this.port = Integer.parseInt(deviceConfig.getItem("port").get());
        
        // TODO Find a better way of doing this. This VNC library does provide a mechanism for abstracting this. So it may be easy.
        this.password = deviceConfig.getItem("password").get();
        
        connect();
    }
    
    private void connect() {
        //config.setColorDepth(ColorDepth.BPP_24_TRUE);
        config.setColorDepth(ColorDepth.BPP_8_INDEXED);
        config.setErrorListener(Throwable::printStackTrace); // TODO Is this the best solution for this project?
        config.setPasswordSupplier(() -> this.password);
        
        config.setBellListener(v -> System.out.println("DING!")); // TODO Change this to debug.
        config.setRemoteClipboardListener(text -> this.debug.out(1, String.format("Received copied text.", text))); // TODO Change this to debug.
        
        config.setScreenUpdateListener(image -> {
            this.width = image.getWidth(null);
            this.height = image.getHeight(null);
            this.connected = true;
            this.debug.out(1, String.format("Received a %dx%d screen update", this.width, this.height));
        });
        
        client.start(this.host, this.port);
        
        this.debug.out(0, "Connected to " + this.host);
    }
    
    public void info() {
        System.out.println("VNC output.");
    }
    
    public Dimension getDesktopResolution() {
        while (this.connected == false) {
            sleep(100);
        }
        
        return new Dimension(this.width, this.height);
    }
    
    public void setPosition(int x, int y) {
        this.client.moveMouse(x, y);
    }
    
    public void click(String button) {
        mouseDown(button);
        mouseUp(button);
    }
    
    public void doubleClick(String button) {
        click(button);
        click(button);
    }
    
    public void mouseDown(String button) {
        this.client.updateMouseButton(getButtonID(button), true);
    }
    
    public void mouseUp(String button) {
        this.client.updateMouseButton(getButtonID(button), false);
    }
    
    private int getButtonID(String button) {
        int result = 0;
        
        switch (button) {
            case "left":
                result = 1;
                break;
            case "middle":
                result = 2;
                break;
            case "right":
                result = 3;
                break;
        }
        
        return result;
    }
    
    public void scroll(int amount) {
        int absoluteAmount = Math.abs(amount);
        
        for (int i = 0; i < absoluteAmount; i ++) {
            if (amount > 0) {
                this.client.scrollDown();
            } else {
                this.client.scrollUp();
            }
        }
    }
    
    
    private int getKeyID(String key) {
        return KeySyms.forKeyCode(this.keys.getPressableID(key)).get();
    }
    
    public void keyDown(String key) {
        this.client.updateKey(getKeyID(key), true);
    }
    
    public void keyUp(String key) {
        this.client.updateKey(getKeyID(key), false);
    }
    
    public Set<String> getKeysIKnow() {
        return this.keys.getPressablesIKnow();
    }
    
    
    public int testInt(String testName) {
        int result = 0;
        
//         switch (testName) {
//             case "posX":
//                 result = this.x;
//                 break;
//             case "posY":
//                 result = this.y;
//                 break;
//             case "clicked":
//                 result = (this.clicked)?1:0;
//                 break;
//             case "scroll":
//                 result = this.scroll;
//                 break;
//             case "lastKey":
//                 result = this.lastKey;
//                 break;
//             case "lastMouseButton":
//                 result = this.lastButton;
//                 break;
//         }
        
        return result;
    }
    
    private void sleep(int microseconds) {
        try {
            Thread.sleep(microseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
