#!/bin/bash

set -e
A="a
b"
echo "A '${A}'"
# replace newline with space
A="${A//
/ }"  # mistakenly flagged as an error
echo "A '${A}'"