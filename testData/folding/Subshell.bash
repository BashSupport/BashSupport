#!/usr/bin/env bash

function a() <fold text='(...)'>(
    :
)</fold>

function b() <fold text='(...)'>(
    echo $(echo hi)
)</fold>

<fold text='(...)'>(
    echo hi
)</fold> >/dev/null
