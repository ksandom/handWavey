#!/bin/bash
# A very, very basic translation of YML to HandWaveyConfig.java, or similar.
# Syntax:
# $0 [fileIn.yml [destinationVariableToUse]]
#
# fileIn.yml: Defaults to "macros.yml"
# destinationVariableToUse: Defaults to "macrosGroup"
#
# This assumes that the entries are always in the form:
#   key:
#     description: A description of the key.
#     value: The value to be saved.
#
# IE The value is always the last part of the entry. If this is not true, you will not get the desired results.

fileIn="${1:-macros.yml}"
destinationVariableToUse="${2:-macrosGroup}"

echo "fileIn=$fileIn  destinationVariableToUse=$destinationVariableToUse" >&2

if [ ! -e "$fileIn" ]; then
    echo "Can not find \"$fileIn\"" >&2
    exit 1
fi

function outputEntry
{
    local key="$1"
    local description="$2"
    local value="$3"

    echo "
        $destinationVariableToUse.newItem(
            \"$key\",
            \"$value\",
            \"$description\");"
}


key=''
description=''
value=''
while read -r; do
    line="$REPLY" # Solely for readability.
    trimmed="$(echo "$line" | sed 's/^ *//g')"

    if [ "${line:1:1}" == ' ' ] && [ "${line:2:1}" != ' ' ]; then
        # The line is indented by exactly 2 spaces.
        key="$(echo "$trimmed" | sed 's/: *$//g')"
        #echo "Key: $trimmed"
    elif [ "${line:3:1}" == ' ' ] && [ "${line:4:1}" != ' ' ]; then
        leftSide="$(echo "$trimmed" | cut -d: -f1)"
        rightSide="$(echo "$trimmed" | cut -d: -f2 | sed 's/^ *//g;s/ *$//g;s/"/\\"/g')"

        if [ "$rightSide" == '\"\"' ]; then
            rightSide=''
        fi

        # echo "  $leftSide = $rightSide"
        if [ "$leftSide" == 'description' ]; then
            description="$rightSide"
        elif [ "$leftSide" == 'value' ]; then
            value="$rightSide"
            outputEntry "$key" "$description" "$value"
        else
            echo "Unknown key \"$leftSide\"" >&2
        fi
    else
        echo "Discarding line \"$line\"." >&2
    fi
done < "$fileIn"

