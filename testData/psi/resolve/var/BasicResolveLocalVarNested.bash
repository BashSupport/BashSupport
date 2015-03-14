function testFunction() {
    local a=1
    
    function inner {
        echo $<ref>a
    }
}