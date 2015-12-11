function _trap() {
    echo trapped

    function inner() {
        function inner2() {
            echo hi
        }
    }
}

_trap
inner
<ref>inner2