#!/bin/bash
function foo() {
    function _trapper() { echo trapper; }
    trap _trapper RETURN
}
foo