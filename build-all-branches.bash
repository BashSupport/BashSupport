#!/usr/bin/env bash
test -f "$HOME/.jdk8" && . $HOME/.jdk8

for v in "192.5728.12-EAP-SNAPSHOT"; do
    echo "## Building with version $v..."
    _JAVA_OPTIONS="" JAVA_OPTS="" gradle -Dbash.skipUrls="true" -PideaVersion="$v" -PideaBranch="192" clean build

    status=$?
    if [[ $status -ne 0 ]]; then
        echo "## Build for version $v failed. Exiting." >&2
        exit 1
    fi
done