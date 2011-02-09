#!/bin/sh

function functionWithLocal() {
    local a_isLocal=1
}

abIsOk=1
aIsOk2=1

${a<caret>}

aIsNotOk=