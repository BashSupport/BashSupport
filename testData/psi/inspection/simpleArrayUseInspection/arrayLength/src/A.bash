declare -a a=(1 2 3)
${#a[@]}

for ((j=0; j < ${#a[@]}; j++)) do
    echo "working..."
done