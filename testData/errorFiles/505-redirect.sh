#!/usr/bin/env bash
foo () {
    if ! grep $1 <<< ${2} > /dev/null; then echo Boom; fi
}