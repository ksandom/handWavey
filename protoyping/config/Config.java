// import java.nio.file.Files;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
// import java.io.IOException;

/*
TODOs
* #Choose best path.
* Load default.
* Overwrite using file if it exists.
  * Else write out default config to file.
*/


class ConfigManager {
  String fileName = "";
  Boolean fileExists = false;
  
  public ConfigManager(String fileName) {
    this.fileName = getFullPath(fileName);
    this.fileExists = new File(this.fileName).exists();
    
    String fileState = (this.fileExists)?"exists":"new";
    
    System.out.println("Load config from: " + this.fileName + " (" + fileState + ")");
  }
  
  private String getBestPath() {
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
  
  private String getFullPath(String fileName) {
    return getBestPath()+fileName;
  }
}


class Config {
  public static void main(String[] args) {
    System.out.println("Config.");
    
    ConfigManager config = new ConfigManager("testExample.yaml");
  }
  
  public static void sleep(int microseconds) {
    try {
      Thread.sleep(microseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
