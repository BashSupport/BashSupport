#!/usr/bin/env bash

x=1
#y="abc"
. include1.bash

x=2
y=2

function x() {
    local x
    x=1
}