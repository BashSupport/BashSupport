eval "$1=\$(printf '%s\n%s' \"\$$1\" \"\$2\")"

x=1
eval 'echo $x; x=$((x+1))'; echo ${x} && echo $x