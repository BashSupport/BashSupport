#!/bin/shell

echo "\*"

(if echo a; then echo ok; else echo b; fi;)
echo $(if echo a; then echo ok; else echo b; fi;)
$((1+4*78/3))
((a=3))
((i=$((1 + 9))))

function a() {
    verA=$(($(echo "$1" | sed 's/\([0-9]*\)\.\([0-9]*\)\.\([0-9]*\).*/\1 \* 10000 + \2 \* 100 + \3/')))
}

i=12
echo $i

$i

function b
 for f in a; do echo $f; done;

for ((;;)); do
 echo $i
done

i=1

[[ -z "" ]] || echo false && echo ok

for i in `echo a b c`; do echo $i; done;
for f in `echo`; do echo $f; done;

#domount devpts "" /dev/pts devpts -onoexec,nosuid,gid=$TTYGRP,mode=$TTYMODE

echo a << a
    o
    p
    e
    r
    a
 e
b

x=4 i=1 j=2
echo $i $j $x

"Hey there `echo end`"
i=1 echo hey there [nix da] $i
$i

jtmplUsage="# Usage:
#   jtmpl [ [ options ] classOrInterfaceName ] [ ... ]
#
# Options:
#   -i              Produce a java interface
#   -c              Produce a java class
#   -p package      Specify the package in which the
#                     new class or interface resides
#   -e base         Specify base class or interface to extend
#                     (comma-separated list; one only for classes)
#   -m interface    Specify implemented interfaces (comma-separated)
#   -I interface    Alternate option code for -m
#   -f              Allow overwrite of already exsisting output files
#   --help          Show this usage summary
#
# Environment:
#   JTMPL_INTF   Header file for interfaces
#   JTMPL_CLASS   Header file for classes"

echo $jtmplUsage

echo "$(echo "\\")" jdhulkjfdsoierkj "$(echo "\\")"
a b c d
"$("hey there")"