#<shebang>!/bin/bash</shebang>

# This script does nothing in particular
# It somehow manages to include most of Bash's syntax elements :)

# Computes the number 42 using Bash
<keyword>function</keyword> <functionDef>compute42</functionDef>() {
    <internalCmd>echo</internalCmd> $((<number>2</number> * <number>3</number> * (<number>3</number> + <number>4</number>)))
}

# Computes the number 42 using a subshell command
<keyword>function</keyword> <functionDef>compute42Subshell</functionDef>() {
    <internalCmd>echo</internalCmd> "$(<subshellCmd>echo "2*3*(3+4)" | bc</subshellCmd>)"
}

# Subtract the second parameter from the first and returns the result
# It can only handle integers
<keyword>function</keyword> <functionDef>subtract</functionDef>() {
    <keyword>local</keyword> <varDef>a</varDef>=${<internalVar>1</internalVar>:?<string>"First param not set"</string>}
    <keyword>local</keyword> <varDef>b</varDef>=${<internalVar>2</internalVar>:?<string>"Second param not set"</string>}

    <internalCmd>echo</internalCmd> -n <string>"$((<var>a</var> - <var>b</var>))"</string>
}

<internalCmd>echo</internalCmd> <simpleString>'The current working directory is: '</simpleString><string>" ${PWD}"</string>

<internalCmd>echo</internalCmd> <string>"100 - 58 = $(<functionCall>subtract</functionCall> 100 58)"</string>

<varDef>fortyTwo</varDef>=$(<functionCall>compute42</functionCall>)
<internalCmd>echo</internalCmd> <string>"$<var>fortyTwo</var> is 42"</string>

<varDef>fortyTwo</varDef>=$(<functionCall>compute42Subshell</functionCall>)
<internalCmd>echo</internalCmd> <string>"${<var>fortyTwo</var>} is 42"</string>
