# Tap

While motion of the mouse cursor uses the X (horizontal) and Y (vetical) axes, the tap functionality uses a stabbing motion on the Z (away from you) axis.

The motivation for this is to

* reduce how often you're doing fine repetitive movements to perform gestures.
* make it easier for new users to get comfortable with handWavey.

## Using it

1. Relax your hand.
    * You don't need to have it in any specific position to trigger a tap. (You will still need to adhere to any other gestures defined in your gestureLayout.)
    * I imagine pushing a basket ball away.
1. Once you have the cursor where you want it, move your hand away from you.
    * Experiment with how fast you need to move your hand.
    * Your hand movement will naturally move the cursor. handWavey automatically uses a position from a few milliseconds earlier. This is configured by `rewindCursorTime` in `click.yml`.

## Configuration

### Tuning

* tap.yml
    * `tapSpeed` - How fast your hand needs to move to trigger the tap event.
        * Setting this too low leads to unwanted tap events.
        * Setting this too high makes it very hard to trigger a tap event.
    * `samplesToWaitNegative` - How many retraction samples to wait until you can trigger another tap event after the last successful one completed.
        * Setting this too small leads to unwanted tap events.
        * Setting this too large restricts your ability to tap multiple things in a short space of time.
    * `samplesToWaitPositive` - After `samplesToWaitNegative` has succeeded, How many samples going deeper to wait until you can trigger another tap event after the last successful one completed.
        * Setting this too small leads to unwanted tap events.
        * Setting this too large restricts your ability to tap multiple things in a short space of time.
* click.yaml
    * `rewindCursorTime` - Use the cursor position from this much time before the tap event was triggered.
        * Setting this too low will make the effective click location move with the noise created during the tap.
        * Setting this too high will make the effective click location before you got the cursor to where you wanted to click.

These can be defined in the `tap.yml` `click.yml` files in [the configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).

There are [configuration examples for tap](https://github.com/ksandom/handWavey/tree/main/examples/tap).

### Events

You can customise what happens with tap events via the actionEvents.yml via the [gestureLayout](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/createAGestureLayout.md).

The event naming will evolve as the tap functionality matures, so it's worth checking back here. But right now, how's how the events work:

`tap-p0Open`

* `tap-` - Always the same. This is a tap event.
* `p` - `p`rimary or `s`econdary hand.
* `0` - Segment number.
* `Open` - `Open`, `Closed`, `Absent`. Absent won't happen under the current design, but might in the future.

You might have noticed that both the primary, and secondary hands can do taps.
