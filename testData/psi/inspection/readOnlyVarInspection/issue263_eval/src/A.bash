#!/usr/bin/env bash

x=1
. include1.bash

x=2
y=2

eval "echo $x && x=1"

function x() {
    local x
    x=1
}