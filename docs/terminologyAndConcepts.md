# Terminology and concepts

## Hands

* Primary: The hand you introduced first.
* Secondary: The second hand you introduced.

Note that there is no left vs right in handWavey other than to correctly perform calculations. This is to keep it dynamically ambidextrous. Multiple people can take turns without having to reconfigure it in between.

## Gesture components

* Segment: This is a way of identifying how your hand is twisted (roll). The complete rotation is divided into a number of segments in `gestureConfig.yml`. Some of the segments will not be comfortable/possible to reach, and configuration should take this into account.
* Zone: This is a way of identifying how deeply your hand has moved into the field of view of the device. Moving through each zone gives a different behavior:
    * zones for the "touchPad" zoneMode:
        * noMove: This is the equivalent of lifting your finger off the touch pad. You can maintain a gesture that has triggered a mouse down so that you can perform a drag.
        * active: This is the equivalent of your finger being on the touch pad. Moving your hand will move the cursor.
        * action: While gestures are the normal way to trigger an action like clicking, pushing through to this zone can trigger another action. Doubleclick, which is hard to do with a large gesture, is a sensible choice for this zone.
    * zones for the "touchScreen" zoneMode:
        * none: The cursor will not move while your hand is in this zone, and you can maintain a gesture that has triggered a mouse down so that you can perform a drag, although the use for that is less obvious that noMove on the "touchPad" zoneMode. The purpose of this zone is:
            * To give the sensor time to stabilise before moving the cursor around.
            * To give you a rest from the stress while you're part way through a drag.
            * To give you a tool for being precise with clicks.
        * absolute: The cursor moves in a direct relationship to your hand.
        * relative: The cursor moves relative to where it was when you exited the absolute zone. This is to give you better precision, and reduce some of the stress.
        * action: While gestures are the normal way to trigger an action like clicking, pushing through to this zone can trigger another action. Doubleclick, which is hard to do with a large gesture, is a sensible choice for this zone.
    * virtual zones:
        * OOB: (Out Of Bounds) The hand is either:
            * Not in an area that the sensor can track.
            * Not in an area that the the application will track. This is to reduce error that comes from the edges of the visible area where the sensor is less reliable.
        * nonOOB: (Non Out Of Bounces) That hand is being tracked. This is useful for gestures that don't need the zone and shouldn't reset when the zone changes. Eg you want to hold the mouse button down while dragging in multiple stages.
* State:
    * Closed: Your hand is resembling a fist.
    * Open: Your hand is flat.
    * Absent: You hand is Out Of Bounds.

## zoneModes

These define how the pointer will move when you move your hand.

* touchPad: This behaves just like a touch pad on a laptop. If you pull back your hand towards you, it's like lifting your finger off the touch page. So you can move the cursor in precise stages. A quick flick will move it much further. This is the mode that almost everyone will want, and is the default.
* touchScreen: This was my first go and is mostly just staying around because it's interesting to play with. It takes an absolute position of your hand, and turns it into an absolute position on the display. This is frustrating, stressful, slow, imprecise etc. Feel free to play with it, but I discourage you from using it on a day-to-day basis.

## Event types
