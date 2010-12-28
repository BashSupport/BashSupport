#!/bin/sh

function a {
    echo a $1 1>&2 $2 ${3}
}

a a b c
