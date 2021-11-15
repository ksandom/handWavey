package config;

import config.Config;

class Dirt { // Manages whether configuration has changed since a known state.
    private Boolean dirty = false;
    private Config config = null;
    private Boolean startup = true;
    
    public Dirt(Boolean startup) {
        this.startup = startup;
    }
    
    protected void makeDirty() {
        if (this.startup == false) {
            this.dirty = true;
            
            if (this.config != null) {
                this.config.makeDirty();
            }
        }
    }
    
    protected void makeClean() {
        this.dirty = true;
    }
    
    public Boolean isDirty() {
        return this.dirty;
    }
    
    public void setConfigManager(Config config) {
        this.config = config;
    }
    
    public void finishedStartup() {
        this.startup = false;
    }
}
