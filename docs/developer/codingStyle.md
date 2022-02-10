# Coding style

* Indentation: 4 spaces. Always, exactly.
* Line length:
    * Do not statically wrap text under any circumstances. It is the job of the text editor to do this and handle the indentation correctly so that the reader can attain the line length that they can read efficiently by adjusting the window size vs the font size/zoom.
    * If a line feels like it's getting long, consider splitting up into separate actions.
    * Occasionally, it will really make sense to split a function call into multiple lines to make it easier to read. Eg [HandWaveyConfig](https://github.com/ksandom/handWavey/blob/main/src/main/java/handWavey/HandWaveyConfig.java#L52) where there's typically a long description with each entry.
