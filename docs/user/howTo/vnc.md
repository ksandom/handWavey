# VNC

If you are using an environment that is not yet supported by handWavey, you can use VNC from a working machine to control it. This document describes how to set it up.

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

You'll need to google got a VNC server that works with your OS/Window manager. But here are some suggestions:

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
