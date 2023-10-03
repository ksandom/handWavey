# VNC

If you are using an environment that is not yet supported by handWavey, you can use VNC from a working machine to control it. This document describes how to set it up.

## When you would use this

### On a system that only has difficulty controlling the mouse and keyboard

Thinking Wayland.

You can run handWavey on the same machine, and only use VNC as the mechanism to talk to the display. This will give you the best results possible.

### On a system that is not compatible with the LeapMotion controller and SDK 2.3.1

Thinking MacOS.

You'll need a another computer that can use the LeapMotion controller. handWavey can then talk via VNC directly (no need for you to have a VNC client visible).

This setup will be vulnerable to any congestion on your network, and the quality of your connection. It works well on Wifi, but it is likely to work a lot better over Ethernet.

## The experience of using VNC

It works, and it's usable.

As of this writing, there are a couple of issues that affect the fun of the experience:

| What | How bad? | The future |
| --- | --- | --- |
| VNC latency | It's slight, but noticeable. | This is expected, and will probably never change. |
| Control frame rate | Noticeable. | _Might_ be possible to improve. The current implementation gets many captures of the display per second that we simply don't need. If that can be stopped, this would reduce the network usage, and leave more capacity for sending more regular inputs. |

## Security considerations

### Problems

* VNC can be encrypted, but generally is not by default. Currently handWavey can only connect via a username/password using a default set up. [Pull-requests to improve this are welcome](https://github.com/ksandom/handWavey/issues/23).
* Additionally output.yml stores your VNC password in clear-text. Ie anyone who can read the file, can see your password. [Pull-requests to improve this are welcome](https://github.com/ksandom/handWavey/issues/24).

### Mitigations

When configuring your set up, you need to think about

* When configuring your firewall: Who/what needs to connect to the VNC server? If it's only running on your local machine, then you only need to allow your local machine to connect to it.
* When choosing your VNC password: It should not be the same as any other password. Ie If someone is able to read output.yml, they should not now know your password to anything else.

## Setting up your VNC server

Goal: Have a VNC server running on the computer that you want to control.

It must be:

* Accessible by the machine running handWavey.
* Able to be authenticated to using a username and password.

You'll need to google for a VNC server that works with your OS/Window manager. But here are some suggestions:

* Linux
  * X: x11vnc
  * Wayland
    * KDE: krfb
    * Gnome: TODO PRs welcome.
* Windows: TODO PRs welcome.
* MacOS: TODO PRs welcome.

## Configuring handWavey as a VNC client

This is output.yml stored in [your configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md):

```yaml
groups:
  VNC:
    groups: {}
    items:
      password:
        description: 'WARNING: This is currently stored in plain text. Please take
          that into account when assessing the security of your setup. It''s worth
          having the VNC server only listening where it''s needed, and in addition
          having the firewall configured to block it externally.'
        value: ''
      port:
        description: When you start the VNC server, you'll get either a port number
          (eg 5901), or a desktop number (eg :1). If it's a desktop number, it should
          be added to 5900 to get the number that you want here. Eg :1 + 5900 = 5901.
        value: '5900'
      host:
        description: The hostname to connect to.
        value: 127.0.0.1
items:
  device:
    description: '[AWT, VNC, Null]: Which method to use to control the mouse and keyboard.
      Default is AWTOutput, which will be the best setting in most situations. VNC
      gives you a method of controlling a separate computer, and needs to be configured
      in the config group. NullOutput is there purely for testing.'
    value: AWT
```

You need to fill in the `value: ''` entry for the `host`, `password`, and `port`. Your VNC server should tell you these values. If you don't know the `host` value, and you are running handWavey on the same machine that you want to control, then `127.0.0.1` is very likely fine.

## Optimisations

It's worth messing with the [sensitivity](https://github.com/ksandom/handWavey/tree/main/examples/sensitivity) to get the behaviour that you want. The default experience will almost certainly not be what you want.
