#!/bin/sh

function f() {
    f=
    f=

    for f in 1 2; do
        # This variable must resolve to the local definition
        echo $<ref>f
    done
}
