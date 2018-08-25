#!/usr/bin/env bash

HOST=`hostname`
NSNAM="nsnam-www.coe-hosted.gatech.edu"

HOST="bla-bla-bla"
if [ $nsnam -eq 1 ]; then
    HOST=<fold text='nsnam-www.coe-hosted.gatech.edu'>$NSNAM</fold>
    say "-n forcing HOST = <fold text='$NSNAM'>$HOST</fold>"
fi

A="value of A"
if true; then
    A="conditional true of A"
else
    A="conditional false of A"
fi
echo "hello $A, how are you?"

daily=0
while getopts :pndth option ; do
    case <fold text='option'>$option</fold> in
	(d)  daily=1  ;;
	esac
done

if [ $daily -eq 1 ] ; then
    say "Hi!"
fi
