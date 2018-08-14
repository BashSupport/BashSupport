#!/usr/bin/env bash

HOST=`hostname`
NSNAM="nsnam-www.coe-hosted.gatech.edu"

HOST="bla-bla-bla"
if [ $nsnam -eq 1 ]; then
    HOST=<fold text='nsnam-www.coe-hosted.gatech.edu'>$NSNAM</fold>
    say "-n forcing HOST = <fold text='nsnam-www.coe-hosted.gatech.edu'>$HOST</fold>"
fi
