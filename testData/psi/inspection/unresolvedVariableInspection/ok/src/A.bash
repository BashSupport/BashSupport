#!/bin/bash

export a=1
echo $a

imageformat_png=(png)
imageformat_jpg=(jpg jpeg)

declare -p ${!imageformat_*}
echo
declare -p ${!imageformat_@}