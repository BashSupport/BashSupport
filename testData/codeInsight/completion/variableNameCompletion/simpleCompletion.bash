#!/bin/sh

function functionWithLocal() {
    local a_isLocal=1
}

abIsOk1=1
abIsOk2=1

$a<caret>

aIsNotOk=