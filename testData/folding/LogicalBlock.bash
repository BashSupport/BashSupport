#!/usr/bin/env bash

if [ ${#pathes[@]} -eq 0 ]; <fold text='then...fi'>then
    >&2 echo "xxx"
    exit 1
elif [ ${#pathes[@]} -eq 1 ]; <fold text='then...fi'>then
	echo "${pathes[0]}"
	exit 0
else
	>&2 echo "yyyyy"
	>&2 printf '%s\n' "${pathes[@]}"
	exit 1
fi</fold></fold>


function lookup_nearby()
<fold text='{...}'>{
	PARENT=".."
	for (( i = 0; <fold text=''>i</fold> < 5; <fold text=''>i</fold>++ )); <fold text='do...done'>do
		while IFS=  read -r -d $'\0'; do
		pathes+=("$REPLY")
		done < <(find <fold text='..'>$PARENT</fold> -maxdepth 3 -type d -name "xxx" -print0)
		PARENT+="/.."
		purge_pathes
		if [ ${#pathes[@]} -ne 0 ]; then
			break
		fi
	done</fold>
}</fold>
