// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
An easy way to manage large/complex configuration.
*/

package config;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import debug.*;

public class Config extends config.Group {
    protected String directoryName = "";
    protected Boolean fileExists = false;
    private String primaryFileName = "main.yml";
    private HashMap groupsToSaveSeparately = new HashMap<String, Boolean>();
    private Boolean missmatchingSeparates = false;

    private static String singletonFileName = "";
    private static Config singletonConfig = null;

    private Debug debug = new Debug(0, "Config");

    public Config(String fileName) {
        this.directoryName = getFullPath(fileName);
        this.fileExists = new File(this.directoryName).exists();

        String fileState = (this.fileExists)?"exists":"new";

        System.out.println("Load config from: " + this.directoryName + " (" + fileState + ")");
    }

    public synchronized static void setSingletonFilename(String fileName) {
        Config.singletonFileName = fileName;
    }

    public synchronized static Config singleton() {
        if (Config.singletonConfig == null) {
            Config.singletonConfig = new Config(Config.singletonFileName);
        }

        return Config.singletonConfig;
    }

    public void addGroupToSeparate(String groupName) {
        if (groupName.equals("")) return;
        this.groupsToSaveSeparately.put(groupName, true);
    }

    public void load() {
        load(true);
    }

    public void load(Boolean loadEverything) {
        Persistence persistence = new Persistence();

        String file = this.directoryName + this.primaryFileName;
        persistence.load(file, (Group)this);

        // Load the sub-groups.
        if (loadEverything) {
            for (Object rawGroupName : this.groupsToSaveSeparately.keySet()) {
                String groupName = (String) rawGroupName;
                if (!separateGroupExists(groupName)) continue;
                Group group = getGroup(groupName);
                file = this.directoryName + groupName + ".yml";
                persistence.load(file, group);
            }
        }

        // Reload the debug class now that we have configuration for it.
        this.debug = Debug.getDebug("Config");
    }

    public void save() {
        Persistence persistence = new Persistence();

        String file = this.directoryName + this.primaryFileName;
        persistence.save(file, (Group)this, this.groupsToSaveSeparately);

        for (Object rawGroupName : this.groupsToSaveSeparately.keySet()) {
            String groupName = (String) rawGroupName;
            if (!separateGroupExists(groupName)) continue;
            Group group = getGroup(groupName);
            file = this.directoryName + groupName + ".yml";
            persistence.save(file, group);
        }
    }

    private Boolean separateGroupExists(String groupName) {
        if (!this.groups.containsKey(groupName)) {
            this.debug.out(0, "groupsToSaveSeparately contains \"" + groupName + "\" which does not appear to exist. This is a programmer error (missmatch between addGroupToSeparate() and existing groups that have been defined), and may mean that you are not getting the result that you expect.");

            this.missmatchingSeparates = true;
            return false;
        } else {
            return true;
        }
    }

    public Boolean testSeparates() {
        for (Object rawGroupName : this.groupsToSaveSeparately.keySet()) {
            String groupName = (String) rawGroupName;
            separateGroupExists(groupName);
        }
        return separatesMissmatch();
    }

    public Boolean separatesMissmatch() {
        return this.missmatchingSeparates;
    }

    protected String getBestPath() {
        List<String> possiblePaths = new ArrayList<String>();
        String slash = File.separator;

        possiblePaths.add(slash + ".config/" + slash);
        possiblePaths.add(slash);

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
        String path = getBestPath();
        String directoryPath = path + fileName;

        assertDirectory(directoryPath);

        return directoryPath + File.separator;
    }

    private void assertDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                this.debug.out(0, "Unable to create directory \"" + path + "\".");
            }
        }
    }
}
