#!/bin/bash
# Clean everything that should not be part of the repo

while read in; do
  echo "Cleaning $in."
  rm -f $in
done < <(cat ../.gitignore | grep -v '^\(#\|$\)')

