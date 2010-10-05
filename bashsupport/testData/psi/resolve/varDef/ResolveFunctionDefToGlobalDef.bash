#!/bin/sh

function f() {
    f=

    for f in 1; do 
        # This variable definition must resolve to the global definition
        <ref>f=
    done
}

f=