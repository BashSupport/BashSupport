#!/usr/bin/env bash

A="A variable"

B="B variable includes <fold text='A variable'>$A</fold>"

C="C variable includes <fold text='B variable includes $A'>$B</fold>"



