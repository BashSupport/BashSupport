#!/usr/bin/env bash

set_var() {
    eval "export $(echo -n $1)=x$1"
}