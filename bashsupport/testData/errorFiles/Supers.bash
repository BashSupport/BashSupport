#!/bin/bash --norc

declare targetListFile="collection-html"
declare -a targetFiles=()
declare dSep=/
declare pSep= .
declare usd='$'
declare usd='\$'
declare given=classes-given
declare linked=classes-linked
declare linkedTarget=classes-targets
declare classes=()
declare targets=()
declare derivations=()

echo $targets

targetClasses=($(egrep-v$'^[ \t]*#' "$@"))

a=()

classes=("${targetClases[@]}")


unique() {
    echo "$@" | tr ' ' '\n' | sort -u
}

docName() {
    echo "$1"  \
 | sed -r  \
 -e 's;\.([a-z]);/\1;g'  \
 -e 's;\.([A-Z]);/\1;'  \
 -e 's;.*;&.html;'
}


className() {
    echo "$1"  \
 | sed -r  \
 -e 's;(\.\./)+scala/;scala/;'  \
 -e 's;/;.;g'  \
 -e 's;\.html;;g'
}


classExtends() {
    target = "$1"
    sed -r -n  \
 -e $'/<code> extends <\/code>/ s/<a/\\n/gp'  \
 -e $'/<code> extends <\/code>/ q' "$(docName"$target")"  \
 | sed -r -n  \
 -e 's/\#[A-Za-z_0-9]+"/"/'  \
 -e 's/.*href="([^"]+)".*/\1/'  \
 -e 's;(\.\./)+scala/;scala/;'  \
 -e 's;/;.;g'  \
 -e 's;\.html;;gp'  \
 | grep -Fxv "$target"  \
 | sort -u
}


targets=($(unique "${targetClasses[@]}"))

while [ ${#targets@} -gt 0 ]; do
    newBases=()

for targetClass in "${targets[@]}"; do
    bases=($(classExtends "$targetClass"))

for baseClass in "${bases[@]}"; do
    classes=("${classes[@]}" "$baseClass")
#	echo "${targetClass//./_} -> ${baseClass//./_}"
done

    derivations=("${derivations[@]}" " ${targetClass//./} -> ${baseClass//./_}")

newBases=("${newBases[@]}" "${bases[@]}")
done

targets=($(unique "${newBases[@]}"))

done


classes=($(unique "${classes[@]}"))

#${.}

#	(
# Emit header
echo -e "digraph Derivations {\n"

(# Generate list of given classes
for class in "${classes[@]}"; do
    echo "${class//./_} [shape=box label=\"$class\"]"
done)  \
 | sed  \
 -e 's;mutable\.;m.;g'  \
 -e 's;immutable\.;s.c.i.;g'  \
 -e 's;jcl\.;s.c.j.;g'  \
 -e 's;collection\.;s.c;g'  \
 -e 's;;s.;g'

echo


# Generate list of linked classes
for derivation in "${derivations[@]}"; do
    echo "$derivation"
done


# Emit trailer
echo -e "\n}"
#	)