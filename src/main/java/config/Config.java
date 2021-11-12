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


class Config {
  protected String fileName = "";
  protected Boolean fileExists = false;
  
  public Config(String fileName) {
    this.fileName = getFullPath(fileName);
    this.fileExists = new File(this.fileName).exists();
    
    String fileState = (this.fileExists)?"exists":"new";
    
    System.out.println("Load config from: " + this.fileName + " (" + fileState + ")");
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
