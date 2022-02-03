#!/bin/bash
# Prepare config files in the current directory for release.

function expectFile
{
    local fileName="$1"
    if [ ! -e "$fileName" ]; then
        echo "FAIL: Could not find \"$fileName\" in the current directory. Your gestureLayout is not complete." >&2
        exit 1
    else
        echo "PASS: Found \"$fileName\"."
    fi
}

# Make sure we have the files that should exist in a gestureLayout.
expectFile "actionEvents.yml"
expectFile "audioEvents.yml"
expectFile "README.md"

# Where are the util scripts stored?
utilDirectory="$(dirname "$0")"

# Clean up any config.
for configFile in "actionEvents.yml" "audioEvents.yml"; do
    $utilDirectory/stripNonEssentialConfig.sh "$configFile"
done

# Does the README.md have any remaining TODOs?
if [ -f README.md ]; then
    todoCount="$(grep 'TODO' README.md | wc -l)"
    if [ "$todoCount" -eq 0 ]; then
        echo "PASS: There are $todoCount TODOs remaining in the README.md file."
    else
        echo "FAIL: There are $todoCount TODO(s) remaining in the README.md file."
        exit 1
    fi
fi
