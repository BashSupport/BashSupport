#!/usr/bin/env bash
test -f "$HOME/.jdk8" && . $HOME/.jdk8

function ideaBranch {
    if [[ "$1" == 191.* || "$1" == 2019.1.* ]]; then
        echo "191"
    elif [[ "$1" == 183.* || "$1" == 2018.3.* ]]; then
        echo "183"
    elif [[ "$1" == 182.* || "$1" == 2018.2.* ]]; then
        echo "182"
    elif [[ "$1" == 181.* || "$1" == 2018.1.* ]]; then
        echo "181"
    elif [[ "$1" == 173.* || "$1" == 2017.3.* ]]; then
        echo "173"
    elif [[ "$1" == 172.* || "$1" == 2017.2.* ]]; then
        echo "172"
    elif [[ "$1" == 171.* || "$1" == 2017.1.* ]]; then
        echo "171"
    fi
}

for v in "2019.1.3" "2018.3.6" "2018.2.3" "2018.1.6" "2017.3.5" "2017.2.6" "2017.1.6" "2016.3.8" "2016.2.5" "2016.1.4"; do
    branch="$(ideaBranch $v)"
    echo "## Building with version $v, IDEA branch $branch..."
    _JAVA_OPTIONS="" JAVA_OPTS="" gradle -Dbash.skipUrls="true" -PideaVersion="$v" -PideaBranch="$branch" clean build

    status=$?
    if [[ $status -ne 0 ]]; then
        echo "## Build for version $v failed. Exiting." >&2
        exit 1
    fi
done