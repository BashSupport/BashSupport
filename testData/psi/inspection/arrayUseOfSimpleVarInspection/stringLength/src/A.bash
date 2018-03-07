#no errors:
a=abcd
echo ${#a}

export myArray=(a ab abc)
echo ${#myArray}
echo ${#myArray[0]}
echo ${#myArray[1]}
echo ${#myArray[2]}

#errors:
a=abcd
echo ${#a[0]}
echo ${#a[*]}
echo ${#a[@]}
