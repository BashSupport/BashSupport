#
# Append or modify entry to hosts file
#
function appendEntry {
local HOSTNAME=`echo "$1" | grep -o ' [a-zA-Z0-9]*'`

 if [[ $HOSTNAME =~ [a-zA-Z0-9] ]]; then
     echo "Updating host $HOSTNAME in hosts file to: $1"
     cp $HOSTS_FILE $TMP_HOSTS
     # remove previous entries with this hostname
     # then append new entry
     # and remove all blank lines
     sed -e "/$HOSTNAME/ d" -e '$ a\'"$1" -e '/^$/ d' $TMP_HOSTS > $HOSTS_FILE
 fi
}
