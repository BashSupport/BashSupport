for_loop() {
    for f in 1 2 3 4; do
        echo "$1";
        shift;
    done;
}

while_loop() {
    while [ $# -gt 0 ]; do
        echo "$1";
        shift;
    done;
}

until_loop() {
    until [ $# -gt 0 ]; do
        echo "$1";
        shift;
    done;
}

for_loop 1 2 3
while_loop 1 2 3
until_loop 1 2 3