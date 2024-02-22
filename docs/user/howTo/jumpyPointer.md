# Jumpy cursor movement

If the mouse cursor is jumping around in weird ways, this is the document to solve that.

## Sudden large jumps while moving slowly

### The exact problem

Cursor movement is mostly consistent. Then occasionally (a few times a minute), the cursor jumps by a large amount, and then continues moving like it did before as if nothing weird had happened.

### Why

Each frame that comes in is marked with a timestamp for when it came in. Sometimes a frame is delayed, causing it to have a timestamp that is very close to the previous frame. This causes a divide by 0/(or very small number) when calculating the speed.

### Solution

Use the `minFrameGapSeconds` setting in dataCleaning.yml in the [config directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).

`minFrameGapSeconds` defines the minimum gap between frames in seconds. When the gap is smaller than this number, a debug message will be produced like

```
Debug 1 (Motion): Skipping frame that is only 0.001 seconds old.
```

and the frame will not contribute to the cursor moving.

* If this number is too high, then too many frames will get skipped, and the cursor won't move as much as desired.
* If this number is too low, then pointer acceleration can be erratic when frame timings get erratic (Common during high CPU load.)

## Cursor barely moves regardless of input

### The exact problem

The cursor occasionally moves, but most of the time it doesn't, despite what the hand is doing.

You will see lots of these in the debug output:

```
Debug 1 (Motion): Skipping frame that is only 0.001 seconds old.
```

### Why

The value is set too high.

### Solution

Make the value lower. You'll get a feel for what the value should be by looking at the some of these messages:

```
Debug 1 (Motion): Skipping frame that is only 0.001 seconds old.
```

You want the value to be lower than most of the messages. But if it's too low, it won't catch the frames that are too close together.

Have a play with the number until you get it right.

## Pointer acceleration does not behave as your want it to

### The exact problem

Movement is relatively consistent/smooth. It's just not quite as you want it to be.

### Why

It's probably not configured to your liking.

### Solution

Take a look at the [sensitivity](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/sensitivity.md) documentation.

