#!/bin/bash
# Generate a table of custom- events that can be used in gestureLayouts, and insert it into the createAGestureLayout.md document.

srcFile="createADynamicGestureLayout.md"
backupFile="${srcFile}.backup"
customDefaultFile="../../../src/main/java/handWavey/HandWaveyConfig.java"

function prep
{
    cp -v "$srcFile" "$backupFile"
}

function cleanup
{
    rm -v "$backupFile"
}

function generateTable
{
    echo "| custom- event | Description |"
    echo "| --- | --- |"
    grep 'customGroup.getItem("' "$customDefaultFile" | sed 's/^.*"custom-/| custom-/g; s/".*\/\/ / | /g; s/$/ |/g'
}

function getHead
{
    grep -B10000 "BEGIN custom- table." "$backupFile"
}

function getFoot
{
    grep -A10000 "END custom- table." "$backupFile"
}

prep
getHead > "$srcFile"
generateTable >> "$srcFile"
getFoot >> "$srcFile"
cleanup
