#! /bin/sh
### BEGIN INIT INFO
# Provides:          mountnfs
# Required-Start:    $local_fs
# Required-Stop:
# Should-Start:      $network $portmap nfs-common  udev-mtab
# Default-Start:     S
# Default-Stop:
# Short-Description: Wait for network file systems to be mounted
# Description:       Network file systems are mounted by
#                    /etc/network/if-up.d/mountnfs in the background
#                    when interfaces are brought up; this script waits
#                    for them to be mounted before carrying on.
### END INIT INFO

. /lib/init/vars.sh
. /lib/lsb/init-functions

do_wait_async_mount() {
	[ -f /etc/fstab ] || return
	#
	# Read through fstab line by line. If it is NFS, set the flag
	# for mounting NFS file systems. If any NFS partition is found
	# then wait around for it.
	#

	exec 9<&0 </etc/fstab

	waitnfs=
	while read DEV MTPT FSTYPE OPTS REST
	do
		case "$DEV" in
		  ""|\#*)
			continue
			;;
		esac
		case "$OPTS" in
		  noauto|*,noauto|noauto,*|*,noauto,*)
			continue
			;;
		esac
		case "$FSTYPE" in
		  nfs|nfs4|smbfs|cifs|coda|ncp|ncpfs|ocfs2|gfs)
			;;
		  *)
			continue
			;;
		esac
		case "$MTPT" in
		  /usr/local|/usr/local/*)
			;;
		  /usr|/usr/*)
			waitnfs="$waitnfs $MTPT"
			;;
		  /var|/var/*)
			waitnfs="$waitnfs $MTPT"
			;;
		esac
	done

	exec 0<&9 9<&-

	# Wait for each path, the timeout is for all of them as that's
	# really the maximum time we have to wait anyway
	TIMEOUT=900
	for mountpt in $waitnfs; do
		log_action_begin_msg "Waiting for $mountpt"

		while ! mountpoint -q $mountpt; do
			sleep 0.1

			TIMEOUT=$(( $TIMEOUT - 1 ))
			if [ $TIMEOUT -le 0 ]; then
				log_action_end_msg 1
				break
			fi
		done

		if [ $TIMEOUT -gt 0 ]; then
			log_action_end_msg 0
		fi
	done
}

case "$1" in
    start)
        # Using 'no !=' instead of 'yes =' to make sure async nfs
        # mounting is the default even without a value in
        # /etc/default/rcS
        if [ no != "$ASYNCMOUNTNFS" ] ; then
                do_wait_async_mount
        else
                FROMINITD=yes /etc/network/if-up.d/mountnfs
        fi
        ;;
    restart|reload|force-reload)
        echo "Error: argument '$1' not supported" >&2
        exit 3
        ;;
    stop)
        ;;
    *)
        echo "Usage: $0 start|stop" >&2
        exit 3
        ;;
esac

: exit 0
