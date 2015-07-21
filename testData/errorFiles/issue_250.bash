#!/bin/bash

dummyMethod()
{
    cat <<EOF
    Foobar this is part of the EOF
    '' This is a properly quoted line
    There's a single quote in this line
    From here on out, editor thinks the file is broken due to mismatch quotations.
EOF
}


variable='foobar'
variable2=""
dummyMethod