# An expansion of an array to all its elements should trigger a warning, but not a "simple use of array variable"
declare -a a=(1 2 3)
echo ${a[@]}

# not a warning
echo "${a[@]}"
echo "${#a[@]}"