# Examples

Here are configuration examples that you can use to adapt handWavey to your needs.

In most cases you can drop the files into your configuration directory.

## Users trying configurations found here

### Install

1. Backup your existing configuration.
2. Copy the `.yml` files into your configuration directory.
   On linux, you can do it with a command like this:
   ```
   cp *yml ~/.config/handWavey
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

* You may want to set `saveBackConfig` in `main.yml` to `false` while writing your layout. In practice, I found it easier to do my development in a separate directory, and simply copy them to the handWavey configuration directory to test it.

#### What to include in your configuration

* You should include all of the files that are relevant to your layout.
* Should not include:
    * `main.yml`
* The default values in the template are the ones that get created by default if they don't exist. If you don't want them, you need to set them to `""` so that they don't get recreated.
