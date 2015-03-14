#!/bin/sh

function myFunctionOneIsOk {
echo One
}

function myFunctionTwoIsOk {
    echo Two

    function myFunctionTwoOneIsOk {
        echo inner function
    }
}

$myFunction<caret>

function myFunctionThreeIsNotOk {
    echo not ok
}

function functionFourNotOk {
    echo not ok
}