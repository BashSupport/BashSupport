#!/usr/bin/env bash

function a() <fold text='{...}'>{
    X="def value"
}</fold>

function b() <fold text='{...}'>{
    "$X"
}</fold>