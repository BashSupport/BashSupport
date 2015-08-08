#!/usr/bin/env bash

trap -l

trap -p

trap -p SIGINT

function myTrapHandler() {
    echo hi
}

trap myTrapHandler SIGINT SIGKILL
trap myTrapHandler SIGINT SIGKILL 2>&1 <IN

trap myTrapHandler SIGINT SIGKILL && echo hi && trap -p
trap myTrapHandler SIGINT SIGKILL; echo hi && trap -p