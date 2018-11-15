#!/usr/bin/env bash

HOST=`hostname`
NSNAM="nsnam-www.coe-hosted.gatech.edu"

HOST="bla-bla-bla"
if [ $nsnam -eq 1 ]; <fold text='then...fi'>then
    HOST=<fold text='nsnam-www.coe-hosted.gatech.edu'>$NSNAM</fold>
    say "-n forcing HOST = <fold text='$NSNAM'>$HOST</fold>"
fi</fold>

A="value of A"
if true; <fold text='then...fi'>then
    A="conditional true of A"
else
    A="conditional false of A"
fi</fold>
echo "hello $A, how are you?"

daily=0
while getopts :pndth option ; <fold text='do...done'>do
    case <fold text='option'>$option</fold> in
	(d)  daily=1  ;;
	esac
done</fold>

if [ $daily -eq 1 ] ; <fold text='then...fi'>then
    say "Hi!"
fi</fold>
