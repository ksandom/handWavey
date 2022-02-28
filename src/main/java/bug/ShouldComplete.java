// (c) 2022 Kevin Sandom under the GPL v3. See LICENSE for details.

/*
An easy way to detect broken code that may be obscured by an overly generous catch somewhere.
*/

package bug;

import debug.*;

public class ShouldComplete {
    private Debug debug = null;
    
    private String title = "";
    
    private String context = "";
    private String lastAction = "";
    private String unfinishedOperation = "";
    private Boolean active = false;
    
    public ShouldComplete(String context) {
        this.debug = Debug.getDebug("bug.ShouldComplete");
        
        this.context = context;
        this.title = "----- Bug detected. Previous " + this.context + " did not complete. -----";
    }
    
    public Boolean start(String action) {
        Boolean result = true;
        if (this.active) {
            result = false;
            
            this.unfinishedOperation = this.lastAction;
            
            this.debug.out(0, this.title);
            this.debug.out(0, "    " + getUnfinishedOperation());
        }
        
        this.active = true;
        this.lastAction = action;
        
        return result;
    }
    
    public void finish() {
        this.active = false;
        this.lastAction = "";
        this.unfinishedOperation = "";
    }
    
    public String getUnfinishedOperation() {
        return this.unfinishedOperation;
    }
}
