#!/usr/bin/env bash

global=abc

function x() {
    function innerX {
        for x in y; do
            echo $x
        done
    }

    innerX

    function innerX2() {
        echo hi $x
        if test; then
            for x in z; do echo $x; done;
            echo $x $<ref>global
            echo while
            echo while there
        else
            echo done
        fi
    }
}

x