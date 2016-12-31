for_loop() {
    function inner() {
        for f in 1 2 3 4; do
            echo "$1";
            shift;
        done;
    }
}

for_loop 1
inner 1