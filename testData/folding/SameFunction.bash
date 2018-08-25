#!/usr/bin/env bash

function a() {
    X="def value"

    test <fold text='def value'>$X</fold>
}

function b() {
    if test; then
        Y="def value"
        test <fold text='def value'>$Y</fold>
    fi
}

function reportDebug {
    typeset msg
    msg="Debug: ${FUNCNAME[1]}(): $*"
    reportSession "<fold text='Debug: ${FUNCNAME[1]}(): $*'>$msg</fold>"
}
