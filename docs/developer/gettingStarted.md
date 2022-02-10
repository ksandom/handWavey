# Getting started

Start here in [HandWaveyManager.java](https://github.com/ksandom/handWavey/blob/main/src/main/java/handWavey/HandWaveyManager.java):

```java
    // This is where everything gets glued together.
    public void sendHandSummaries(HandSummary[] handSummaries) {
```

## Summary

* `UltraMotionInput` sends `HandSummary`s to `HandWaveyManager` via `sendHandSummaries()`.
* `sendHandSummaries()`
    * Make sure that `HandsState` has the latest `HandSummary`s.
    * Tells `HandsState` to `figureStuffOut()` what state the hands are in and therefore what gestures are being performed and what events need to be triggered.
        * Tracks how the states have changed using `HandStateEvents`.
        * Gets a the lists of events from `HandStateEvents` so that they can be performed in the right order.
        * Triggers events in the right order so that exits happen before enters, for example.
        * Triggers combined events (both hands).
    * Based on which zone is active, interact with the cursor/scroll in the appropriate way.
* Events get executed by `MacroCore`.

TODO Write more about this.
