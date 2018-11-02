#!/usr/bin/env bash
. $HOME/.jdk8

for v in 2017.2.6 2017.1.6 2016.3.8 2016.2.5 2016.1.4; do
#for v in 183.3283.2 2018.2.3 2018.1.6 2017.3.5 2017.2.6 2017.1.6 2016.3.8 2016.2.5 2016.1.4; do
    echo "## Building with version $v..."
    _JAVA_OPTIONS="" JAVA_OPTS="" gradle -Dbash.skipUrls="true" -PideaVersion="$v" -PideaBranch="" clean build

    status=$?
    if [ $status -ne 0 ]; then
        echo "## Build for version $v failed. Exiting." >&2
        exit -1
    fi
done