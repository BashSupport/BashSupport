#!/bin/bash

function a {
    echo a
}

function ab {
    function c{
        echo c
    }

    echo ab
}

a
ab
c