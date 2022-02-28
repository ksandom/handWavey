# Bug

Tools for detecting bugs and making them easier for a user to either solve or report.

## ShouldComplete

Designed to be run in regularly used code. Ie when a previous iteration has failed to complete, the alarm will be raised when the current iteration is attempted to be started.

The current iteration will continue regardless of the previous iterations success, unless you choose to capture it and take alternative action.

### Example

```java
import bug.ShouldComplete;

public class SomethingSensitive {
    private ShouldComplete shouldComplete = new ShouldComplete("sensitive thing");
    
    // Simple every day example.
    public void doSomethingSensitive1(String input) {
        this.shouldComplete.start("A successful thing;");
        
        // TODO Do fragile stuff here
        
        this.shouldComplete.finish();
    }
    
    // Capture the failed operation for further use.
    public void doSomethingSensitive2(String input) {
        if (!this.shouldComplete.start("A successful thing;")) {
            String failedOperation = this.shouldComplete.getUnfinishedOperation();
        }
        
        // TODO Do fragile stuff here
        
        this.shouldComplete.finish();
    }
}
```
