#!/usr/bin/env bash

foo1() (
    :
)

foo2() if true; then
    :
fi

foo3() [[ -n $1 ]]