#!/usr/bin/env bash

function base() {
    local num=5
    if ((8#$num > 8#3)); then
        :
    fi

    a=1
    $(( a=12#${a} ))
    $(( a=12#1$a ))

    $(( a=12#C ))
}
