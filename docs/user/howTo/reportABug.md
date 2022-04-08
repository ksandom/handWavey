# How to report a bug

This howTo is split into two sections:

* [I heard the coocoo clock noise](#i-heard-the-coocoo-clock-noise).
* [I'm reporting another type of bug](#im-reporting-another-type-of-bug).

## I heard the coocoo clock noise

Great! This means that handWavey detected the bug, and has told you where it went wrong.

It's usually a configuration issue, but could be a bug in handWavey itself. Regardless, have a look at the terminal output, and look for lines that look something like this:

```
Debug 0 (bug.ShouldComplete): ----- Bug detected. Previous exampleContext did not complete. -----
Debug 0 (bug.ShouldComplete):     aBrokenCall();
```

This should give you enough information to fix the problem if you're playing with the configuration. You may want to [adjust the debugging levels](https://github.com/ksandom/handWavey/blob/main/docs/debugging.md) to better see what's going on.

There will likely be multiple lines, like the two above, that are close together; and getting more and more specific. If you decide to file a bug report, please include all of those lines that are close together in the bug report.

[Report a coocoo clock noise](https://github.com/ksandom/handWavey/issues/new).

## I'm reporting another type of bug

This document will refine over time. But for now, this is a starting point:

* Have a look at the terminal output to see if there are any clues that match up with the bug you are reporting. Eg You tried to do a left click, but it did a right click instead, then output like this would be very helpful
    ```
    Debug 1 (HandsState): Current state: pActive5Open, sOOB0Absent
    Debug 1 (MacroLine): Used button: right
    Debug 1 (HandsState): Current state: pActive0Open, sOOB0Absent
    ```
* If you'd like to fix the bug yourself, the [debugging](https://github.com/ksandom/handWavey/blob/main/docs/debugging.md) documentation may help you.

[Report another type of bug](https://github.com/ksandom/handWavey/issues/new).
