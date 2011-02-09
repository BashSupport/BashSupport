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

#No command completions for the dollar!
$<caret>

function myFunctionThreeIsNotOk {
    echo not ok
}

function functionFourNotOk {
    echo not ok
}