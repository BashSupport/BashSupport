#!/usr/bin/env bash

A="A variable"

B="B variable include <fold text='A variable'>$A</fold>"

C="C variable include <fold text='B variable include A variable'>$B</fold>"



