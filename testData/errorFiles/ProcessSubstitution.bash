#!/bin/sh
ADD=($(comm -13 <(printf '%s\n' "${BEFORE[@]}" | LC_ALL=C sort) <(printf '%s\n' "${AFTER[@]}"  | LC_ALL=C sort)))
DEL=($(comm -13 <(printf '%s\n' "${AFTER[@]}"  | LC_ALL=C sort) <(printf '%s\n' "${BEFORE[@]}" | LC_ALL=C sort)))
