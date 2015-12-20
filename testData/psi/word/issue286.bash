#!/usr/bin/env bash

myvar123=myvalue
index=123
eval value=\$myvar${index}
echo ${<caret>value}