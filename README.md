# handWavey

A more intuitive/usable desktop control using leapmotion/ultramotion.

You can [see it in action](https://youtu.be/kCbar8w3Pws) and learn [how to get the most out of handWavey](https://github.com/ksandom/handWavey/blob/main/docs/user/gettingStarted.md).

If you use it, I'd love to [hear from you](https://github.com/ksandom/handWavey/issues/4).

## Requirements

### Hardware

* [LeapMotion controller](https://www.ultraleap.com/product/leap-motion-controller/#pricingandlicensing).
   * [Getting the device set up well](https://support.leapmotion.com/hc/en-us/articles/360004322638-Taking-care-of-your-Leap-Motion-Controller).

### Softare

* [LeapMotion/UltraLeap SDK 2.3.1](https://github.com/ksandom/installUltraleap).
* Java. One of:
    * [OpenJDK](https://openjdk.java.net/install/).
    * Or [Oracle's Java](https://www.oracle.com/java/technologies/downloads/).

## Run

### Linux

1. Satisfy the [requirements](#Requirements).
1. Copy libraries from the LeapMotion SDK to lib.
1. Test that you have everything installed correctly:
   `./gradlew test`
1. Run it:
   `./gradlew run`

### Windows

1. Satisfy the [requirements](#Requirements).
1. `copy build.gradle.windows build.gradle`
1. Copy libraries from the LeapMotion SDK to lib.
1. Test that you have everything installed correctly:
   `gradlew.bat test`
1. Run it:
   `gradlew.bat run`

### MacOS

This is theoretically possible, but has not been tested. **There are [known challenges](https://github.com/ksandom/handWavey/issues/1#issuecomment-1092271612)**.

1. Satisfy the [requirements](#Requirements).
1. Copy libraries from the LeapMotion SDK to lib.
1. Test that you have everything installed correctly:
   `./gradlew test`
1. Run it:
   `./gradlew run`

## Install

* [Linux](https://github.com/ksandom/handWavey/blob/main/docs/user/install/installOnLinux.md).
* [Windows](https://github.com/ksandom/handWavey/blob/main/docs/user/install/installOnWindows.md).
* [MacOS](https://github.com/ksandom/handWavey/blob/main/docs/user/install/installOnMacOS.md). (This is theoretically possible, but has not been tested. **There are [known challenges](https://github.com/ksandom/handWavey/issues/1#issuecomment-1092271612)**.)

## Safety

I believe this to be safe to use long term. But I have no medical or ergonomic background to back that up.

Please use your intelligence and judgement when using this.

If you notice any changes in discomfort, pain, stiffness, or you have any doubts whatsoever; please stop using it immediately, and seek medical advice. Only resume using it after you have medical advice saying that it's ok for you to resume.

Nothing in this repo, or any related material should be understood to be medical advice.

By using this software, you are taking responsibility for staying healthy.

## Contributing

* [Pull requests](https://github.com/ksandom/handWavey/pulls) welcome.
* [Report bugs](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/reportABug.md).
* Support me on [Patreon](https://www.patreon.com/randomksandom). - There will be better ways of doing this soon.
