#!/bin/sh
x=1

local x=2

function abc() {
    echo $x
}

local y=1
y=2

function xyz() {
    echo $y
}