#!/bin/sh

function a {
    echo a $1 1>&2 $2 ${3}
}

function x {
    echo $*
}

a a b c
x a
