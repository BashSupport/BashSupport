#!/bin/sh

f=

function x() {
    local f
    
    for y in 1 2 3; do
        # This variable must resolve to the global definition
        echo $<ref>f
    done
}

f=
