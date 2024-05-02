# Move before action

* Problem: The LeapMotion controller is quite noisy when the hand is first introduced, which causes unwanted actions. So we don't allow any action to be taken for a period of time after the hand is introduced to allow it to stabilise. But this timing is really hard to get right.
* Assumptions:
    * We don't normally intend to perform an action before having first moved the mouse cursor.
    * The LeapMotion controller has usually had plenty of time to stabilise by the time we have the mouse cursor where we want it.
* Solution: Keep actions locked until the cursor is relatively stationary.
* Consequences:
    * If you want to be able to perform actions without moving the cursor beforehand, they won't work. For this reason, the setting is optional.

## General

There is a small wait between the hand arriving, and actions being allowed.

* Configure in: [yourConfigurationDirectory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md)/handCleaner.yml
* Setting name: `minHandAge`

## Taps

* Configure in: [yourConfigurationDirectory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md)/tap.yml
* Setting name: `moveBeforeTaps`
* Default: `true`
