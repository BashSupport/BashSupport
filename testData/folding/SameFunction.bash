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
