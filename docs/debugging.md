# Debugging

You can get a lot of information from the terminal output. By default, it should give enough information for a user to make sensible decisions when making/debugging a gestureLayout, without being utterly flooded with information. But there's so much more to see if you need it.

## Tuning the output

* Edit debug.yml in your configuration directory.
* Change the `value` of the desired Item/class [up or down](#debug-levels).

## What information is coming from which classes

```
Debug 1 (HandsState): Current state: pNoMove0Open, sOOB0Absent
```

In the above example:

* Item/class: HandsState
* Debug level: 1 - So it would have been displayed as long as the debug level was 1 or higher.

## Debug levels

Making the number larger will give you more information. Making it smaller will give you less.

* 0 - Silent, unless urgent.
* 1 - A little information. A good starting point.
* 2 - More
* 3 - More
* 4 - More
* etc
