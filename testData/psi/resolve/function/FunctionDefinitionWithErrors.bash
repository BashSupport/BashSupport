function a() {
    echo ${=1}

    function inner {
        echo ${=1}
    }
}

<ref>inner