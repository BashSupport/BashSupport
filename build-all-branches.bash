#!/usr/bin/env bash
. $HOME/.jdk8

for v in 182.2574.2 181.2784.17 173.2290.1 172.1909.2 171.1834.9 163.3094.26 162.74.16 145.184.1; do
    echo "## Building with version $v..."
    _JAVA_OPTIONS="" JAVA_OPTS="" gradle -Dbash.skipUrls="true" -PideaVersion="$v" clean build

    status=$?
    if [ $status -ne 0 ]; then
        echo "## Build for version $v failed. Exiting." >&2
        exit -1
    fi
done