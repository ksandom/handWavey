# handWavey Training - Accuracy

While the previous lesson was about getting confident with handWavey; this lesson is perhaps the most important lesson, because it will help you be more relaxed by helping the tool be accurate.

This training is in the format of an [example configuration](https://github.com/ksandom/handWavey/blob/main/docs/user/exampleConfigurations.md) that you can apply. It intentionally does not perform any actions like mouse clicks, or key presses. This way you can experiment freely and get comfortable with it in a relaxed setting.

## Exercise - Posture vs accuracy

1. Start by placing your hand about 30cm above the sensor. The mouse cursor should be moving with your hand now.
1. Try twisting your hand slowly as far as you can. Things to notice:
    * What happens to the cursor? Did it stay where you expected?
1. Try doing the same thing with your hand closed like a fist. Things to notice:
    * What happens to the cursor? Did it stay where you expected?
1. Try step 1 and 2 with your hand in other postures and observe the same things.
1. Try these steps again with your hand in areas that are visible to the device. Things to notice:
    * Just above the sensor was probably best. How did the other areas compare?
1. Try these steps again with your hand in a relaxed, flat-ish position.

### Explaining posture vs accuracy

The leapmotion device detects your hand visually. Quite frankly, it's amazing at this. But a flat posture is still much easier for it to measure than anything else. So it will do the best job when you keep your hand as flat as possible when using it.

For comfort though, your hand should be relaxed, so I tried to configure all examples with this in mind. I recommend holding your hand in a relaxed, flat-ish position (open, but not paper-flat).

## Exercise - Fast hand movements

1. Start with your hand at rest beside you, or on your keyboard.
1. Without delay, move your hand across the usable area. Things to notice:
    * Did the mouse cursor move like it did in the earlier exercises?
    * Did it move accurately?
    * Is it the same every time?
1. Try steps 1 and 2 at different speeds.
1. Try steps 1 and 2, but giving the sensor a moment to find your hand before doing the fast movement. Things to notice:
    * How quickly can you move your hand before it becomes unreliable?
1. Try steps 1 an 2, but with your hand in different postures (Eg flat, on its side, upside down, slightly closed etc). Things to notice:
    * How quickly can the device find your hand in one posture vs another?
    * How does the accuracy change?
1. Introduce your hand, and then try step 2.
    1. Try moving your hand at different speeds.
    1. Try moving your hand slowly, then giving a small quick movement and back to slow.
    1. Things to notice:
        * How much distance does the mouse cursor move for a given hand movement at different speeds?

### Explaining fast hand movements

There are three parts to this:

1. Giving the device time to find your hand.
1. How quickly the device can track your hand once it's found it.
1. Mouse cursor acceleration.

The time to find your hand will vary depending on things like:

* How still is your hand when you first introduce it.
* Room lighting. (It's much harder for it to distinguish your hand from the rest of the room in a brightly lit room than a dark one.)
* How clean the device is.

How quickly you can move your hand and still have the device able to track it is influenced by similar things. Just understand that accuracy will fall if you move your hand too quickly.

Mouse cursor acceleration is a common trick used in various pointing devices to help you use a larger desktop area quickly, while still giving your accuracy. While you move your hand slowly, you'll get razor sharp precision. Yet, give your hand a quick flick, and the cursor will shoot across the desktop, ready for your next action.

With a little practice, you can get very accurate at this. If at any point it starts to get erratic; just slow down and try again. I have found that as I get more comfortable with it, I start to move my hand in ways that just aren't fair for the sensor to try to accurately measure.

## Exercise - Yo' face!

1. Rest your face on your hand.
1. Lean in close to the sensor. Things to notice:
    * When does the mouse cursor do something weird?

### Explaining Yo' face

Your face is lovely. However the device is very good at detecting hands, and it's going to do so regardless of whether it is currently hosting your beautiful face. This is still true when someone else is using the device.

This isn't a skill you need to master. But just be aware that this can influence your experience.
