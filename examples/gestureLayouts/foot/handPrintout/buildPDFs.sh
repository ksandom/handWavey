#!/bin/bash -ex
# Build the PDFs from the jpegs.

convert withoutStrap-L.jpg withoutStrap-R.jpg -quality 100 withoutStrap.pdf
convert withStrap-L.jpg withStrap-R.jpg -quality 100 withStrap.pdf
