# grabMove

Move a window (in Linux) by grabbing it.

Right now, this is only a proof-of-concept, and not intended for every-day use.

## Using it

* Hold your hand as flat as possible without tensing up.

## The Gestures

* Clicks
    * Click: _Not implemented yet._
    * Right click: Tilt your hand by 90 degrees, and then closing your hand. (Segment 1)
    * Middle click: Turn your hand up-side down and closing your hand. ((Segment 2))
    * Double click: Touch the action zone (push you hand all the way through the visible area until you hear a different dong.)
* Scroll by turning your hand upside down and then moving your hand vertically.
* Move a window: Grab and move your hand. When you're done, you can let go.
* Keys
    * CTRL: Introduce a second second and keep it flat.
    * ALT: Introduce a second second and rotate it by 90 degrees. (Segment 1)
    * Shift: Introduce a second second and turn it upside down. (Segment 2)

## Considerations

Watch the posture of your hand. It should be relatively flat and relaxed. If it's not, find out why and fix it (eg habbit? Is something difficult). If you have feedback for how to make this easier, please create an [issue on github](https://github.com/ksandom/handWavey/issues).

As at 2022-08-03, this is the default gestureLayout for handWavey. Simply deleting these files in your config directory should be enough to get this configuration. But it is a nice reference for creating new layouts.

## Target audience

Proof-of-concept.
