#!/bin/sh

function f() {
    f=
    f=
    # This variable must resolve to the global definition
    echo $<ref>f
}

f=