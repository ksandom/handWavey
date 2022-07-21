# Experimental wayland support

My impression from conversations I've read is that wayland support is  already working in Java via SWT. This doesn't appear to be the case, but I'd love to be wrong. Input welcome.

Failure looks like this:

```
$ ./crun.convenience
Info.
Pointer location: Point {0, 0}
Desktop resolution: 0 0
Center.
Center: Point {0, 0}
```

Success should

* Move the mouse cursor to the expected location. At the moment, it's 10, 10:
    ```
      Point centerPoint = new Point(x, y);
    Point arb = new Point(10, 10);
    System.out.println("Center: " + centerPoint.toString());

    //this.robot.mouseMove(centerPoint.x, centerPoint.y);
    this.display.setCursorLocation(arb);
    ```
* Show the pointer location as where you last moved the mouse. (We need to see the mouse cursorm, so touch input is effectively useless.)
* Show the desktop resolution (as configured spanning all relevant displays).
    * It's important to test that this is correct for multiple displays.
* Show the correctly calculated cernter point.

## Do it

### Get the prerequisites

I had to satisfy some dependencies. Here are the problems that I had to solve:

#### libswt-gtk missing from class path

I'm sure there's a better way of solving this (probably by setting the class path.) But I just symlinked what I needed into `~/.swt/lib/linux/x86_64` like so:

```
ksandom@localhost:~/.swt/lib/linux/x86_64> ls -l
total 28
lrwxrwxrwx 1 ksandom users 108 Jul 21 14:58 libswt-atk-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-atk-gtk-4932r18.so
lrwxrwxrwx 1 ksandom users 108 Jul 21 14:58 libswt-awt-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-awt-gtk-4932r18.so
lrwxrwxrwx 1 ksandom users 110 Jul 21 14:58 libswt-cairo-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-cairo-gtk-4932r18.so
lrwxrwxrwx 1 ksandom users 108 Jul 21 14:58 libswt-glx-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-glx-gtk-4932r18.so
lrwxrwxrwx 1 ksandom users 104 Jul 21 14:57 libswt-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-gtk-4932r18.so
lrwxrwxrwx 1 ksandom users 108 Jul 21 14:58 libswt-pi3-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-pi3-gtk-4932r18.so
lrwxrwxrwx 1 ksandom users 111 Jul 21 14:58 libswt-webkit-gtk-4932r18.so -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/libswt-webkit-gtk-4932r18.so
```

#### No org.eclipse.swt in class path

Again, this would be done in a better way for the final implementation, but in the mean time I just symlinked it like this:

```
simple$ ls -l org
lrwxrwxrwx 1 ksandom users 86 Jul 21 14:24 org -> /usr/lib64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_3.114.0.v20200407-0844/org
```

### Build it

```
./compile.convenience
```

### Run it

```
./run.convenience
```

### Build & run it

```
./crun.convenience
```
