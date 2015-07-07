#!/usr/bin/env bash

# See http://wiki.bash-hackers.org/syntax/ccmd/case?s[]=case

printf '%s ' 'Which fruit do you like most?'
read -${BASH_VERSION+e}r fruit

case $fruit in
    apple)
        echo 'Mmmmh... I like those!'
        ;;
    banana)
        echo 'Hm, a bit awry, no?'
        ;;
    orange|tangerine)
        echo $'Eeeks! I don\'t like those!\nGo away!'
        exit 1
        ;;
    *)
        echo "Unknown fruit - sure it isn't toxic?"
esac


printf '%s ' 'Which fruit do you like most?'
read -${BASH_VERSION+e}r fruit

case $fruit in
    apple)
        echo 'Mmmmh... I like those!'
        ;;
    banana)
        echo 'Hm, a bit awry, no?'
        ;;
    orange|tangerine)
        echo $'Eeeks! I don\'t like those!\nGo away!'
        exit 1
        ;;
    *)
        echo "Unknown fruit - sure it isn't toxic?"
esac;




# Set radeon power management
function clk {
	typeset base=/sys/class/drm/card0/device
	[[ -r ${base}/hwmon/hwmon0/temp1_input && -r ${base}/power_profile ]] || return 1

	case $1 in
		low|high|default)
			printf '%s\n' "temp: $(<${base}/hwmon/hwmon0/temp1_input)C" "old profile: $(<${base}/power_profile)"
			echo "$1" >${base}/power_profile
			echo "new profile: $(<${base}/power_profile)"
			;;
		*)
			echo "Usage: $FUNCNAME [ low | high | default ]"
			printf '%s\n' "temp: $(<${base}/hwmon/hwmon0/temp1_input)C" "current profile: $(<${base}/power_profile)"
	esac
}
