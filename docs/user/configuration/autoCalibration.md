# Auto calibration

It used to be that calibration of the roll axis was done in the gestureLayout via `rotationOffset`. This worked reasonably well. But assumptions that worked well for one user were not necessarily the same for the next user. Heck, those assumptions didn't even hold up for one person from one moment to the next.

`rotationOffset` has therefore been removed entirely and has been replaced with:

* `recalibrateSegments();` - A macro command, typically called by the `special-newHandUnfreezeCursor` event.
* `autoTrim`. - Constant adjustment based on how far the hand is from the center of the current segment.

## recalibrateSegments

### What & how

`recalibrateSegments();` does the heavy lifting.

Every time you enter your hand into the view of the device, this command gets called once the hand has had a chance to stabilise. What ever the roll current is, an offset of that amount is taken into account for every calculation involving segments for the remainder of the time that the hand is visible.

### Why

This has the effect that handWavey is immediately calibrated to the newly introduced hand, regardless of whose it is. Users can just do what is comfortable, and it will work.

### Configuration

This is configured in the actionEvents.yml within the [gestureLayout](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createAGestureLayout.md).

You can disable this behaviour be removing the `recalibrateSegments();` from the actionEvents.yml file.

## autoTrim

### What & how

For every sample measuring your hand:

* The rotational distance from the center of the segment is captured.
* Based on the rotational distance, an offset (independant of `recalibrateSegments();`) is adjusted:
    * The rate of change is limited by `autoTrimMaxChangePerSecond` within handCleaner.yml.
        * Setting this to `0` disables this feature.
    * The total change is limited by `autoTrimMaxChange` within handCleaner.yml.

### Why

Ideally, intentional movements will go straight through, while unintentional movements will be transparently adjusted for.

### Configuration

Adjust `autoTrimMaxChangePerSecond` abd `autoTrimMaxChange` within handCleaner.yml.
