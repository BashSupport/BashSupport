#<shebang>!/bin/bash</shebang>

# This script does nothing in particular
# It somehow manages to include most of Bash's syntax elements :)

# Computes the number 42 using Bash
<keyword>function</keyword> <functionDef>compute42</functionDef>() {
    <internalCmd>echo</internalCmd> $((<number>2</number> * <number>3</number> * (<number>3</number> + <number>4</number>)))
}

# Computes the number 42 using a subshell command
<keyword>function</keyword> <functionDef>compute42Subshell</functionDef>() {
    <internalCmd>echo</internalCmd> "$(<internalCmd>echo</internalCmd> <string>"2*3*(3+4)"</string> | <externalCmd>bc</externalCmd>)"
}

# Subtract the second parameter from the first and outputs the result
# It can only handle integers
<keyword>function</keyword> <functionDef>subtract</functionDef>() {
    <keyword>local</keyword> <varDef>a</varDef>=${<internalVar>1</internalVar>:?<string>"First param not set"</string>}
    <keyword>local</keyword> <varDef>b</varDef>=${<internalVar>2</internalVar>:?<string>"Second param not set"</string>}

    <internalCmd>echo</internalCmd> -n <string>"$((<varUse>a</varUse> - <varUse>b</varUse>))"</string>
}

<internalCmd>echo</internalCmd> 'The current working directory is: '<string>" ${PWD}"</string>

<internalCmd>echo</internalCmd> <string>"100 - 58 = $(<functionCall>subtract</functionCall> 100 58)"</string>

<varDef>fortyTwo</varDef>=$(<functionCall>compute42</functionCall>)
<internalCmd>echo</internalCmd> <string>"$<varUse>fortyTwo</varUse> is 42"</string>

<varDef>fortyTwo</varDef>=$(<functionCall>compute42Subshell</functionCall>)
<internalCmd>echo</internalCmd> <string>"${<composedVar>fortyTwo</composedVar>} is 42"</string>

<internalCmd>echo</internalCmd> "6 * 7 is $<varUse>fortyTwo</varUse>" <redirect> > log.txt</redirect> <redirect>2>&1</redirect>

<internalCmd>echo</internalCmd> <backquote>`echo This is an echo`</backquote>

<varDef>empty</varDef>=""
[ -z "$<varUse>empty</varUse>" ]  && This variable is empty!

<externalCmd>cat</externalCmd> -  <<dummy><</dummy> <heredocStart>EOF</heredocStart>
    <heredoc>Dear Mr. X,
    this is a message to you.

    With kind regards,
    Mr. Y</heredoc>
<heredocEnd>EOF</heredocEnd>