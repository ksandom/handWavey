# reducedMobility

Here are some gestureLayouts for if you have trouble with fine-control of your hands.

They give you fine control of the mouse while using large, easy to make, movements of your arms. See the README.md in each directory for pros and cons, and specifics for how to operate them.

## The gist of the layouts

* modifyWithSecondaryZone - No drag.
* modifyWithSecondaryZoneWithDrag - No middle click.
* modifyWithSecondaryZoneWithDragAndMiddleClick - Both drag, and middle click work. (Harder to master.)

## Uninstalling these layouts

These layouts do two things that are incompatible with most other layouts, and will cause you problems if you don't reset these settings afterwards:

### What is changed

* gestureConfig.yml:
    * primaryHand/rotationSegments: 0
    * secondaryHand/rotationSegments: 0
* ultraMotion.yml:
    * openThreshold: 3

The zones are also moved slightly more tightly together, which may not feel natural for other gestureLayouts:

* zones.yml:
    * touchPad/action/threshold: 80

### Resetting the changes

The easiest way to reset to defaults is to delete these files from your [configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md). But you could edit the files manually if you prefer.

### Doing the uninstall

1. [Reset the changes](#resetting-the-changes) that are not used by other gestureLayouts.
1. Apply your preferred [gestureLayout](https://github.com/ksandom/handWavey/tree/main/examples/gestureLayouts).
1. Restart handWavey.
