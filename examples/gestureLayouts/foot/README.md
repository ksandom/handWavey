# grabClick

**HIGHLY EXPERIMENTAL**

The foot layout is not currently fit for daily use. Some time investment will be needed to make it useable. If this will make a meaningful difference to your life (or someone you know/care for), please let me know, and we can work through addressing your needs together.

If you'd like to try it as it is, please pay close attention to all of this documentation to maximise your chances of success.

## What works, what doesn't

I have not put enough time into becoming good at this yet to determine what is my skill level, and what technically needs to improve. Please take the following with a grain of salt:

* Moving the pointer is _relatively_ easy to learn.
    * Note that acceleration works, so you can "flick" the cursor longer distances when needed, but have gentle movements to move the cursor finely.
* Clicking is doable, but time-consuming and frustrating.
    * Note that I've implemented a simple `click();` rather than `mouseDown();mouseUp();`. This is to reduce the learning curve/difficulty in making a reliable click vs an accidental drag. But it is very easy to change `gestureConfig.yml` if you'd like the separate events.
* Second foot gestures need some more effort to master.

## How to set it up

* Apply the configuration.
* Wear a black sock on each foot.
* Print out the hands and cut out the outline.
* Stick one hand to each foot so that it is as flat as possible.
    * I found double side tape/looping some sticky tape worked the best.

## How to get the best result

What I've learnt so far:

* Hang the sensor from the desk:
    * Facing down.
    * About 30cm above the floor. (Mine was 28.7cm.)
* Have a light absorbing mat below the visible area.
    * This helps get some separation between your foot, and the floor.
    * Note that just because it's black, doesn't mean that it absorbs the infrared light. Use the Leapmotion visualizer to see how well you are achieving this.
* Use the debug output to work out why things are working/not working.
* Use the Leapmotion visualizer to understand what you are doing well, and what you need to do better.
* Type of chair.
    * I found that being able to easily roll my chair backwards and forwards massively helped being able to precisely click.
* Be comfortable in your chair so that you can comfortably lift your legs and move them around.
* Other than when you intentionally remove your foot, try not to accidentally move it out of range.
    * Doing so will cause erratic behavior.
    * More tuning is needed to tune the bounds of the usable area. This is relaxed at the moment to give you the best chance of getting it going, but comes at the cost or reliability when you move your foot too far.

## Things to experiment with

* Hand sizes.
* Material that the hands are made from.
    * Does it work better with thicker material? (Thinking about it flopping around less in the air.)
    * Does it work better with more/less reflective material?
        * Retro reflector?
        * Matte?
        * Smooth?
    * Mannequin hand?
    * Silicone casting of a hand?
* Device height.
    * There is an optimum range where the sensor will pick up your foot, and give the most reliable/accurate data. I haven't explored this enough to give you good advice yet.
* Device distance from your chair. (How far you need to stretch our your foot to reach it.)
    * It should be easy and comfortable to get to the active area. You shouldn't have to stretch too far to click. But you also want to be able to move into the "NoMove" zone easily so that you can do the equivalent of taking your finger off the touch pad.
* Type of chair.
    * I found that being able to easily roll my chair backwards and forwards massively helped being able to precisely click.
    * Comfort suspending your legs.
* What gestures trigger what actions.
    * I've made some assumptions about what actions are the most useful. Perhaps your needs will be different.
    * You can tune them in `actionEvents.yml`.
    * If you come up with a layout that is easier. Please share it under a new name.

## The Gestures

* Cursor movement
    * X: Move your foot side to side.
    * Y: Move your foot up and down.
* Clicks
    * Click: Extend primary foot to the "Action" zone,
    * Right click: Add second foot and place in "Action" zone. Make a normal click with your primary foot.
    * Middle click: Add second foot and place in "Active" zone. Make a normal click with your primary foot.
    * Double click: _not implemented_
* Scroll: Add second foot and place in "Active" zone. Move primary up/down to scroll.

## Target audience

NOTE That this still needs a lot of work. Please read the note about it being **HIGHLY EXPERIMENTAL** at the top of this page.

* People who don't have good use of their hands.
* Gimmick/party trick.
