# The test for this file has the strict variable scope checking enabled

function a {
    echo $<ref>myVar
}

function b {
    myVar=123
}



