#!/bin/bash
# Generate a table of default macros that can be used in gestureLayouts, and insert it into the createAGestureLayout.md document.

srcFile="docs/user/reference/macroCommands.md"
backupFile="${srcFile}.backup"
defaultMacrosSrc="src/main/java/handWavey/HandWaveyConfig.java"

function prep
{
    cp -v "$srcFile" "$backupFile"
}

function cleanup
{
    rm -v "$backupFile"
}

function macrosToLines
{
    macroName=""
    value=""
    description=""
    position=0
    while read line; do
        if [ "$line" == '--' ]; then
            macroName=""
            value=""
            description=""
            position=0
        else
            case $position in
                0)
                    macroName="$(echo "$line" | sed 's/^ *"//g;s/",$//g')"
                ;;
                1)
                    value="$(echo "$line" | sed 's/^ *"//g;s/",$//g')"
                ;;
                2)
                    description="$(echo "$line" | sed 's/^ *"//g;s/");$//g')"
                    tableLine
                ;;
            esac

            let position=position+1
        fi
    done < <(grep -A3 'macrosGroup\.newItem' "$defaultMacrosSrc" | grep -v 'macrosGroup\.newItem')
}

function tableHeader
{
    #echo "| custom- event | Description | What it does by default |"
    #echo "| --- | --- | --- |"
    echo "| custom- event | Description |"
    echo "| --- | --- |"
}

function tableLine
{
    #echo "| \`$macroName\` | $description | \`$value\` |"
    echo "| \`$macroName\` | $description |"
}

function generateTable
{
    tableHeader
    macrosToLines
}

function getHead
{
    grep -B10000 "BEGIN macro table." "$backupFile"
}

function getFoot
{
    grep -A10000 "END macro table." "$backupFile"
}

function updateGit
{
    git add "$srcFile"
    git commit -m "Re-generate macro table."
}


prep
getHead > "$srcFile"
generateTable >> "$srcFile"
getFoot >> "$srcFile"
cleanup

updateGit
