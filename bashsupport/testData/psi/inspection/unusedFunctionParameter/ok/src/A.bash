#!/bin/sh

function a {
    echo a $1 1>&2 $2 ${3}
}

function x {
    echo $*
}

function x2 {
    echo ${*}
}

function y {
    echo $@
}

function y2 {
    echo ${@}
}

a a b c
x a
x2 a
y a b c
y2 a b c
