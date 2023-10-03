# Where does handWavey work?

To over-simplify:

handWavey works where ever you can use the LeapMotion controller with Java.

Now let's refine it:

* As of this writing, the latest Linux and MacOS support is in 2.3.1 of the SDK. So I've targeted 2.3.1 on all platforms.
    * The latest version is 5.6, so Linux and MacOS have been neglected for a long time.
    * There was word that updated Linux and MacOS support was coming. But nothing has eventuated, yet.
        * UPDATE: MacOS has just been updated. The download is not listed in the same place, so I'm not sure if it will still be compatible. Once Linux gets the update as well, I'll dive into updating it. So for now, [MacOS remains in the same state](https://github.com/ksandom/handWavey/issues/1#issuecomment-1744668679).
* Last year's release of MacOS broke compatibility with 2.3.1 of the SDK. There is [a hack to get it going](https://github.com/ksandom/handWavey/issues/1#issuecomment-1092271612) that **some** people have manged to get working.
* Wayland support is not available yet, but [is likely to happen](https://github.com/ksandom/handWavey/issues/14).
* X is known to work.
* Windows 10 is known to work.
* Missing platforms [can be made to work over VNC](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/vnc.md) using something like an old laptop running a supported platform to control the unsupported platform.

## Yes, so where does it work?

So let's bring all of that together:

| Platform | Experience | Support |
| --- | --- | --- |
| Linux - X | Excellent | Native |
| Linux - Wayland | Poor | Only [VNC](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/vnc.md), so far. Hopefully native soon. Note that it works well enough that you can run it locally, and connect to VNC locally without needing to expose VNC to the rest of the network. |
| Windows 10 | Excellent | Native |
| Windows 11 | _Unknown_ | _Unknown_ - Probably native. Definitely via [VNC](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/vnc.md). |
| MacOS | Poor | May be possible to get it working with [a hack](https://github.com/ksandom/handWavey/issues/1#issuecomment-1092271612). Otherwise via [VNC](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/vnc.md). |
