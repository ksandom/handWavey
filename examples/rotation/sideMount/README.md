# side mount rotation

**Experimental/proof of concept. Not ready for mainstream usage.**

This configuration changes the mappings so that the device can be mounted at 90 degrees to one side of the user, eg on a wall.

The advantage of this is that it frees up your desk, and is potentially less biased to one hand or other. (range might limit the reality of this.)

The disadvantage of this is that the device is far less reliable when it is looking at the side of your hand rather than the palm. You will get jittery movement, and false triggers of mouse buttons. Proceed with caution.

## Assumptions and getting the best out of it

* This has only been tested by mounting the device on the right hand side. If you want to mount it on the left, you might need to invert one or two axies in axisOrientation.yml.
* You will get more reliable results if your hand remains slightly below (or above) the horizontal plane coming out of the device. This is because it can see slightly more of the hand, giving it more visual clues about the state of things.

## Mappings

This configuration maps vertical movement of the hand to vertical movement of the cursor, and horizontal (y) movement of the hand to horizontal movement of the cursor.

The remaining depth movement of the hand (how far away it is away from you) maps to zones.
