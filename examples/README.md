# Examples

Here are configuration examples that you can use to adapt handWavey to your needs.

In most cases you can drop the files into your [configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).

## Users trying configurations found here

### Install

1. Backup your existing configuration.
2. Copy the `.yml` files into your [configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md).
   On linux (and probably macOS), you can do it with a command like this:
   ```
   cp *yml ~/.config/handWavey
   ```
   On windows, you can do it like this:
   ```
   copy *.yml %USERPROFILE%\handWavey
   ```
3. Restart handWavey.

### Reset back to before

1. Get the configurations back to the desired state. Two ways that you can do this are:
    * Restore your backup. OR
    * Delete any file you want to reset. It will be re-generated when handWavey is restarted.
2. Restart handWavey.

## People creating new configurations

### Considerations

#### Tools to assist you.

* You may want to set `saveBackConfig` in `main.yml` to `false` while writing your layout. In practice, I found it easier to do my development in a separate directory, and simply copy them to the handWavey [configuration directory](https://github.com/ksandom/handWavey/blob/main/docs/user/configuration/whereIsMyConfigurationDirectory.md) to test it.

#### What to include in your configuration

* You should include all of the files that are relevant to your layout.
* Should not include:
    * `main.yml`
* The default values in the template are the ones that get created by default if they don't exist. If you don't want them, you need to set them to `""` so that they don't get recreated.

#### Trimming out stuff that you don't need

On restart after copying in the files, handWavey will re-create everything that it needs, but is missing. So you can safely trim out stuff. The catch is that if defaults get changed in a newer version, then assumptions that you make might break in the newer versions.

The best-practise around this is likely to evolve. Please check back here to see what the current stance is.

Things you can trim:

* Entire configuration items. Eg you can trim out `rotationSegments` in either hand in `gestureConfig.yml`.
* Item parts. Eg:
    * `description` - Is helpful for users looking at examples to understand why a value is being set the way it is. But it can also take a lot of space, which might make it harder to comprehend what the whole config is doing. Eg a gesture layout has a _lot_ of events to configure, and having descriptions makes the file large and hard to manage.
    * `zzzEmpty` - This is here purely to get the YAML library to format the Item in a readable way.
    * `defaultValue` - This is used to help handWavey decide what to do with the value if handWavey gets updated and the default value has changed. Eg had the value been changed from default on the version that this config was originally written for? If so, we probably want to keep the change. Otherwise it should update it to the new default.
    * `oldValue` - This is intended for future functionality. This is implemented, but has no practical use case right now. You can safely ignore it.
    * `value` - I can't imagine why you'd want to omit this, but not the others. But you can.
