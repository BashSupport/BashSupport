#!/bin/sh

function help() {
    echo my help
}

function test() {
    echo my test
}

function kill {
    echo my kill

    function help {
        my inner help
    }
}

help
test
kill