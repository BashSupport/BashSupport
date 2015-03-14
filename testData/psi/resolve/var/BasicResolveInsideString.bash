# Unresolved variable s3 in line 2
function func() {
    echo dummy
}

a="v"$(func)
"${<ref>a}"$(func)
