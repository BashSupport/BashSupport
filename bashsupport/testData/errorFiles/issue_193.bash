#!/usr/bin/env bash

function base() {
    local num=5
    if ((8#$num > 8#3)); then
        :
    fi
}
