#!/bin/sh

function a {
    echo
}

function b {
    echo "$(a)" || b
}

b
