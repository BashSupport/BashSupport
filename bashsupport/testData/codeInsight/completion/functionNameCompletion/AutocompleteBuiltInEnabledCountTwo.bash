#!/bin/sh

function disown123() {
    echo 123
}

disown<caret>

function disown123DefinedLater() {
    echo 123
}