#!/bin/sh

case "a b" in
    a\ b) # definitely import
        echo yes
    ;;
esac
