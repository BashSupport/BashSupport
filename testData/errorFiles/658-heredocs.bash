cat << $'"'
content
"

cat << $'\''
content
'

cat << "'"
content
'

cat << a\"a
content
aAa

cat << $"\""
content
"

cat << "\""
content
"

cat << $"\""$"X"
content
"X
