// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
An easy way to manage large/complex configuration.
*/

package config;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/*
TODOs
* #Choose best path.
* Load default.
* Overwrite using file if it exists.
  * Else write out default config to file.
*/


public class Config extends config.Group {
    protected String fileName = "";
    protected Boolean fileExists = false;
    private static String singletonFileName = "";
    private static Config singletonConfig = null;
    
    public Config(String fileName) {
        this.makeClean();
        this.fileName = getFullPath(fileName);
        this.fileExists = new File(this.fileName).exists();
        
        String fileState = (this.fileExists)?"exists":"new";
        
        System.out.println("Load config from: " + this.fileName + " (" + fileState + ")");
    }

    public static void setSingletonFilename(String fileName) {
        Config.singletonFileName = fileName;
    }

    public static Config singleton() {
        if (Config.singletonConfig == null) {
            Config.singletonConfig = new Config(Config.singletonFileName);
        }

        return Config.singletonConfig;
    }
    
    protected String getBestPath() {
        List<String> possiblePaths = new ArrayList<String>();
        
        possiblePaths.add("/.config/");
        
        String home = System.getProperty("user.home");
        
        for (String possiblePath: possiblePaths) {
            String pathToTry = home + possiblePath;
            
            if (new File(pathToTry).exists()) {
                return pathToTry;
            }
        }
        
        return null;
    }
    
    protected String getFullPath(String fileName) {
        return getBestPath() + fileName;
    }
}
