#!/usr/bin/env bash

function a() {
    X="def value"
}

function b() {
    "$X"
}