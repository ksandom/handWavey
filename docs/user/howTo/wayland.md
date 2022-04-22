# How to use on Wayland

Wayland does not include the functionality required for handWavey to work natively. This has caused fragmentation and duplication of effort. That's a honey pot of heated opinions, which this document will not cover. But it does mean that native Wayland support will take longer.

This document covers where handWavey currently is on the way to working natively with Wayland, and what to do about it in the mean time.

## Native status

* There is [a ticket for adding native support](https://github.com/ksandom/handWavey/issues/14).

## Short-term work-around

1. Set up a VNC server on your Wayland desktop. - You'll need to google this for your specific desktop since under Wayland it is tied to the Window Manager instead of Wayland.
1. [Set up handWavey as a VNC client](https://github.com/ksandom/handWavey/blob/main/docs/user/howTo/vnc.md).
