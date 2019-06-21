#!/usr/bin/env bash
test -f "$HOME/.jdk8" && . $HOME/.jdk8

for v in "191.5701.16" "183.4139.22" "2018.2.3" "2018.1.6" "2017.3.5" "2017.2.6" "2017.1.6" "2016.3.8" "2016.2.5" "2016.1.4"; do
    echo "## Building with version $v..."
    _JAVA_OPTIONS="" JAVA_OPTS="" gradle -Dbash.skipUrls="true" -PideaVersion="$v" -PideaBranch="" clean build

    status=$?
    if [[ $status -ne 0 ]]; then
        echo "## Build for version $v failed. Exiting." >&2
        exit 1
    fi
done