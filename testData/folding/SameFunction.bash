#!/usr/bin/env bash

function a() <fold text='{...}'>{
    X="def value"

    test <fold text='def value'>$X</fold>
}</fold>

function b() <fold text='{...}'>{
    if test; <fold text='then...fi'>then
        Y="def value"
        test <fold text='def value'>$Y</fold>
    fi</fold>
}</fold>

function reportDebug <fold text='{...}'>{
    typeset msg
    msg="Debug: ${FUNCNAME[1]}(): $*"
    reportSession "<fold text='Debug: ${FUNCNAME[1]}(): $*'>$msg</fold>"
}</fold>
