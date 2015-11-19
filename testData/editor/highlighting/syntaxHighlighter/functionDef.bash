#!/usr/bin/env bash

function <info textAttributesKey="BASH.FUNCTION_DEF_NAME">first</info>() {
    <info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> Hello
}

function <info textAttributesKey="BASH.FUNCTION_DEF_NAME">myName</info> {
    <info textAttributesKey="BASH.FUNCTION_CALL">first</info>
    <info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> world
}

<info textAttributesKey="BASH.FUNCTION_CALL">myName</info>