# pathAppend:	Add a segment to a path at end
# pathPush:		Add a segment to a path at beginning
# pathPop:		Remove first segment of a path
# pathTrim:		Remove last segment of a path
# pathTop:		Print top element of stack to standard out
# pathRemove:	Remove a segment from a path by content
# pathPrint:	Print a path to standard out, one segment per line
# pathFind:		Run find rooted in all the directories of a path (variable)
# pathIter:		Execute a command for each segment of a path (variable)

# Note: path variable content or path separators containing or consisting of a vertical bar
#		will break the removal functions. If vertical bar occurs in your environment, replace
#		the syntactic use of vertical bar in all the sed commands found in those function with
#		a character not found in your setting.

# Variable side-effects:
#		As written, the following variables will be altered by executing these functions.
#		If this is a problem, assign them new names that don't interfere with other uses.
#
#			$PS
#			$pathVar
#			$pathAddition
#			$pathRemoval
#			$pathVal


# An alias, could also be pathAppend, if that's preferable
pathAdd()		{ pathPush "$@"; }
	
pathAppend() {
	if [ $# -lt 2 -o $# -gt 3 ]; then
		echo "pathAppend: Usage: pathAppend <PATH_VAR_NAME> <pathAddition> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 3 ]; then
		PS="$3"
	else
		PS=":"
	fi

	pathVar="$1"
	pathAddition="$2"

	eval "pathVal=\$$pathVar"

	case "$pathVal" in
	  $pathAddition \
	| $pathAddition$PS* \
	| *$PS$pathAddition \
	| *$PS$pathAddition$PS*)
		;;

	'')
		eval "$pathVar=$pathAddition"
		;;

	*)
		eval "$pathVar=\$$pathVar$PS$pathAddition"
		;;
	esac
}


pathPush() {
	if [ $# -lt 2 -o $# -gt 3 ]; then
		echo "pathAppend: Usage: pathPush <PATH_VAR_NAME> <pathAddition> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 3 ]; then
		PS="$3"
	else
		PS=":"
	fi

	pathVar="$1"
	pathAddition="$2"

	eval "pathVal=\$$pathVar"

	case "$pathVal" in
	  $pathAddition \
	| $pathAddition$PS* \
	| *$PS$pathAddition \
	| *$PS$pathAddition$PS*)
		;;

	'')
		eval "$pathVar=$pathAddition"
		;;

	*)
		eval "$pathVar=$pathAddition$PS\$$pathVar"
		;;
	esac
}


pathPop() {
	if [ $# -lt 1 -o $# -gt 2 ]; then
		echo "pathPop: Usage: pathPop <PATH_VAR_NAME> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 2 ]; then
		PS="$2"
	else
		PS=":"
	fi

	pathVar="$1"

	eval "pathVal=\$$pathVar"

	case "$pathVal" in
	$PS*)
		eval "$pathVar=$(echo $pathVal |sed -e '1 s|$PS\(.*\)|\1|')"
		;;

	*$PS*)
		eval "$pathVar=$(echo $pathVal |sed -e "1 s|[^$PS][^$PS]*$PS\(.*\)|\1|")"
		;;

	'' | *)
		eval "$pathVar="
		;;
	esac
}


pathTop() {
	if [ $# -lt 1 -o $# -gt 2 ]; then
		echo "pathTop: Usage: pathPop <PATH_VAR_NAME> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 2 ]; then
		PS="$2"
	else
		PS=":"
	fi

	pathVar="$1"

	eval "pathVal=\$$pathVar"

	case "$pathVal" in
	*$PS*)
		echo "$(echo $pathVal |sed -e "1 s|\([^$PS][^$PS]*\)$PS.*|\1|")"
		;;

	$PS* | '')
		echo ""
		;;

	*)
		echo "$pathVal"
		;;
	esac
}


pathTrim() {
	if [ $# -lt 1 -o $# -gt 2 ]; then
		echo "pathTrim: Usage: pathTrim <PATH_VAR_NAME> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 2 ]; then
		PS="$2"
	else
		PS=":"
	fi

	pathVar="$1"

	eval "pathVal=\$$pathVar"

	case "$pathVal" in
	*$PS)
		eval "$pathVar=$(echo $pathVal |sed -e "1 s|\(.*\)$PS|\1|")"
		;;

	*$PS*)
		eval "$pathVar=$(echo $pathVal |sed -e "1 s|\(.*\)$PS[^$PS][^$PS]*|\1|")"
		;;

	'' | *)
		eval "$pathVar="
		;;
	esac
}


pathRemove() {
	if [ $# -lt 2 -o $# -gt 3 ]; then
		echo "pathRemove: Usage: pathRemove <PATH_VAR_NAME> <pathRemoval> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 3 ]; then
		PS="$3"
	else
		PS=":"
	fi

	pathVar="$1"
	pathRemoval="$2"

	eval "pathVal=\$$pathVar"

	case "$pathVal" in
	"$pathRemoval")
		eval "$pathVar="
		;;

	*"$PS$pathRemoval$PS"*)
		eval "$pathVar=$(echo $pathVal |sed -e "1 s|\(.*\)$PS$pathRemoval$PS\(.*\)|\1$PS\2|")"
		;;

	"$pathRemoval$PS"*)
		eval "$pathVar=$(echo $pathVal |sed -e "1 s|$pathRemoval$PS\(.*\)|\1|")"
		;;

	*"$PS$pathRemoval")
		eval "$pathVar=$(echo $pathVal |sed -e "1 s|\(.*\)$PS$pathRemoval|\1|")"
		;;

	'' | *)
		;;
	esac
}


pathRemoveAll() {
	if [ $# -lt 2 -o $# -gt 3 ]; then
		echo "pathRemove: Usage: pathRemove <PATH_VAR_NAME> <pathRemoval> [separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 3 ]; then
		PS="$3"
	else
		PS=":"
	fi

	pathVar="$1"
	pathRemoval="$2"

	while true; do
		eval "pathVal=\$$pathVar"

		case "$pathVal" in
		"$pathRemoval")
			eval "$pathVar="
			break;
			;;

		*"$PS$pathRemoval$PS"*)
			eval "$pathVar=$(echo $pathVal |sed -e "1 s|\(.*\)$PS$pathRemoval$PS\(.*\)|\1$PS\2|")"
			;;

		"$pathRemoval$PS"*)
			eval "$pathVar=$(echo $pathVal |sed -e "1 s|$pathRemoval$PS\(.*\)|\1|")"
			;;

		*"$PS$pathRemoval")
			eval "$pathVar=$(echo $pathVal |sed -e "1 s|\(.*\)$PS$pathRemoval|\1|")"
			;;

		'' | *)
			break;
			;;
		esac
	done
}


pathPrint() {
	if [ $# -lt 1 -o $# -gt 2 ]; then
		echo "pathPrint: Usage: pathPrint <PATH_VAR_NAME>[separator]" >&2
		echo "  Do not include the '\$' in PATH_VAR_NAME" >&2
		return 1
	fi

	if [ $# -eq 2 ]; then
		PS="$2"
	else
		PS=":"
	fi

	pathVar="$1"
	eval "pathVal=\$$pathVar"

	echo "$pathVal" |tr ':' $'\n'
}


# Split a PATH-like value or variable and do a find in the resulting directories
pathFind() {
	if [ $# -lt 1 ]; then
		(echo "pathFind: Usage: pathFind <PATH> [find-arg] [...]"
		 echo "  <PATH> can be a path value or variable name (w/o a \$)") >&2
		return 1
	fi

	thePATH="$1"
	shift

	case "$thePATH" in
	*:*)
		# There's a colon -- assume it's a literal path list
		;;

	*[^a-zA-Z_0-9]*)
		# The name is not a valid environment variable name -- use it as it is
		;;

	*)
		# The name is a valid environment variable name -- defererence it
		eval "thePATH=\$$thePATH"
		;;
	esac

	find "${thePATH//:/ }" "$@";
}


# Generic iteration over the segments of a PATH-like variable (colon-separated segments)
pathIter() {
	if [ $# != 2 ]; then
		(echo "pathIter: Usage: pathIter <PATH> <command>"
		 echo "  <PATH> can be a path value or variable name (w/o a \$)") >&2
		return 1
	fi

	thePATH="$1"

	case "$thePATH" in
	*:*)
		# There's a colon -- assume it's a literal path list
		;;

	*[^a-zA-Z_0-9]*)
		# The name is not a valid environment variable name -- use it as it is
		;;

	*)
		# The name is a valid environment variable name -- defererence it
		eval "thePATH=\$$thePATH"
		;;
	esac

	echo "$2" >.pathIterCommand

	for pathSeg in ${thePATH//:/ }; do
		. ./.pathIterCommand
	done

	rm .pathIterCommand
}
