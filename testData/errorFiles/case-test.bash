#!/bin/sh

case "a b" in
    a\ b) # definitely import
        build_old_libs=$(case $build_libtool_libs in yes) echo no;; *) echo yes;; esac)
        build_old_libs=`case $build_libtool_libs in yes) echo no;; *) echo yes;; esac`
    ;;
esac
