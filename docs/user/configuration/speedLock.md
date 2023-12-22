# Speed locking the gestures

While the hand is moving, the data from the LeapMotion is super-unreliable. This leads to very unreliable gestures while moving the hands, eg incorrect clicks, scrolls, or letting go when you didn't intend to.

Fortunately, it's extremely unusual that we need to change click/scroll states while moving the cursor. So we can simply lock those states while the hand is moving.

## Concepts

* We have a `stationarySpeed`, which is the speed that the hand is considered to be slow enough to be stationary.
* When the speed goes above `stationarySpeed`: the `special-primaryMoving` event is triggered.
    * Calls `lockGestures`.
* When the speed goes below `stationarySpeed`: the `special-primaryStationary` event is triggered.
    * Calls `unlockGestures`.

## Configuration

### stationarySpeed

`stationarySpeed` is found in [handCleaner.yml](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).

A round guide to settings values:

| Value | Effect |
| --- | --- |
| -1 | Disabled. |
| 0 | Don't do this. The speedLock will always be engaged. |
| 5 | The hand needs to be fairly still to trigger gestures. This was my original default. |
| 10 | A little more relaxed. |
| 15 | A little more relaxed, still. The current default. |
| 20 | Very relaxed. I found that this wasn't enough protection for my usage. |

I found 15 to be a nice balance between protecting from false inputs vs having to do precise movements.

TODO I'm still forming my opinion about what is a good threshold, so will likely update the default, and this documentation when I have a stronger opinion. So it's worth checking back here to see how things evolve.

### The events

| Event name | When |
| --- | --- |
| special-primaryMoving | Triggered when the primary hand is moving faster than `stationarySpeed`. |
| special-primaryStationary | Triggered when the primary hand is moving slower than `stationarySpeed`. |
| special-secondaryMoving | Triggered when the secondary hand is moving faster than `stationarySpeed`. |
| special-secondaryStationary | Triggered when the secondary hand is moving slower than `stationarySpeed`. |

These can be defined in the `actionEvents.yml` and the `audioEvents.yml` [configuration](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).

Setting them to nothing (`""`) will also effectively disable the functionality. But I recommend instead setting `stationarySpeed` to `-1`.
