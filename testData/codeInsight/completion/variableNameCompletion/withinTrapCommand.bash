#!/bin/sh

outer=123
trap "inner=1; echo $<caret>" RETURN