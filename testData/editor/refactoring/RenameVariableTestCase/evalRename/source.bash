. target.bash
eval '$<caret>a'
# if the string is missing then the rename doesn't work because the text $a is not found in the usages index/search
eval "$<caret>a"