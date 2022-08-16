#!/bin/bash
# Strip out non essential entries from a config file.
# This is used to help you focus when deriving a new gestureLayout from and old one. It also makes sure that what you set gets loaded in the way that you expect.
# The entries that are stripped out are everything that is not "value".
# 
# Syntax:
#   $0 "fileName.yml"

function showHelp
{
    echo >&2
    grep -B1000 '^function showHelp$' "$0" | grep '^# ' | sed 's/^# //g;s#\$0#'"$0"'#g' >&2
    exit 1
}

if [ "$1" == "" ]; then
    echo "You need to specify a filename." >&2
    showHelp
elif [ ! -e "$1" ]; then
    echo "The file \"$1\" doesn't appear to exist." >&2
    showHelp
fi

fileName="$1"
temporaryFileName="$fileName.cleaned"

grep '\(:$\|^ *\(value: \|groups\)\)' "$fileName" > "$temporaryFileName"
diff "$temporaryFileName" "$fileName"
mv "$temporaryFileName" "$fileName"
