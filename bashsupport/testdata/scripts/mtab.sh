#! /bin/sh
### BEGIN INIT INFO
# Provides:          mtab
# Required-Start:    checkroot
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Update mtab file.
# Description:       Update the mount program's mtab file after
#                    all local filesystems have been mounted.
### END INIT INFO

#
# The main purpose of this script is to update the mtab file to reflect
# the fact that virtual filesystems were mounted early on, before mtab
# was writable.
#

PATH=/lib/init:/sbin:/bin
. /lib/init/vars.sh

TTYGRP=5
TTYMODE=620
[ -f /etc/default/devpts ] && . /etc/default/devpts

TMPFS_SIZE=
[ -f /etc/default/tmpfs ] && . /etc/default/tmpfs

KERNEL="$(uname -s)"

. /lib/lsb/init-functions
. /lib/init/mount-functions.sh

# $1 - fstype
# $2 - mount point
# $3 - mount name/device
# $4 - mount options
domtab ()
{
	# Directory present?
	if [ ! -d $2 ]
	then
		return
	fi

	# Not mounted?
	if ! mountpoint -q $2 < /dev/null
	then
		return
	fi

	if [ -n "$3" ]
	then
		NAME="$3"
	else
		NAME="$1"
	fi

	# Already recorded?
	if ! grep -E -sq "^([^ ]+) +$2 +" /etc/mtab < /dev/null
	then
		mount -f -t $1 $OPTS $4 $NAME $2 < /dev/null
	fi
}

do_start () {
	DO_MTAB=""
	MTAB_PATH="$(readlink -f /etc/mtab || :)"
	case "$MTAB_PATH" in
	  /proc/*)
		# Assume that /proc/ is not writable
		;;
	  /*)
		# Only update mtab if it is known to be writable
		# Note that the touch program is in /usr/bin
		#if ! touch "$MTAB_PATH" >/dev/null 2>&1
		#then
		#	return
		#fi
		;;
	  "")
		[ -L /etc/mtab ] && MTAB_PATH="$(readlink /etc/mtab)"
		if [ "$MTAB_PATH" ]
		then
			log_failure_msg "Cannot initialize ${MTAB_PATH}."
		else
			log_failure_msg "Cannot initialize /etc/mtab."
		fi
		;;
	  *)
		log_failure_msg "Illegal mtab location '${MTAB_PATH}'."
		;;
	esac

	#
	# Initialize mtab file if necessary
	#
	if [ ! -f /etc/mtab ]
	then
		:> /etc/mtab
		chmod 644 /etc/mtab
	fi
	if selinux_enabled && which restorecon >/dev/null 2>&1 && [ -r /etc/mtab ]
	then
		restorecon /etc/mtab
	fi

	# S02mountkernfs.sh
	RW_OPT=
	[ "${RW_SIZE:=$TMPFS_SIZE}" ] && RW_OPT=",size=$RW_SIZE"
	domtab tmpfs /lib/init/rw tmpfs -omode=0755,nosuid$RW_OPT

	domtab proc /proc "proc" -onodev,noexec,nosuid
	if grep -E -qs "sysfs\$" /proc/filesystems
	then
		domtab sysfs /sys sysfs -onodev,noexec,nosuid
	fi
	if [ yes = "$RAMRUN" ] ; then
		RUN_OPT=
		[ "${RUN_SIZE:=$TMPFS_SIZE}" ] && RUN_OPT=",size=$RUN_SIZE"
		domtab tmpfs /var/run "varrun" -omode=0755,nosuid$RUN_OPT
	fi
	if [ yes = "$RAMLOCK" ] ; then
		LOCK_OPT=
		[ "${LOCK_SIZE:=$TMPFS_SIZE}" ] && LOCK_OPT=",size=$LOCK_SIZE"
		domtab tmpfs /var/lock "varlock" -omode=1777,nodev,noexec,nosuid$LOCK_OPT
	fi
	if [ -d /proc/bus/usb ]
	then
		domtab usbfs /proc/bus/usb "procbususb"
	fi

	# S03udev
	domtab tmpfs /dev "udev" -omode=0755

	# S04mountdevsubfs
	SHM_OPT=
	[ "${SHM_SIZE:=$TMPFS_SIZE}" ] && SHM_OPT=",size=$SHM_SIZE"
	domtab tmpfs /dev/shm tmpfs -onosuid,nodev$SHM_OPT
	domtab devpts /dev/pts "devpts" -onoexec,nosuid,gid=$TTYGRP,mode=$TTYMODE

	# Add everything else in /proc/mounts into /etc/mtab, with
	# special exceptions.
	exec 9<&0 0</proc/mounts
	while read FDEV FDIR FTYPE FOPTS REST
	do
		case "$FDIR" in
			/lib/modules/*/volatile)
				FDEV="lrm"
				;;
			/dev/.static/dev)
				# Not really useful to show in 'df',
				# and it isn't accessible for non-root
				# users.
				continue
				;;
		esac
		domtab "$FTYPE" "$FDIR" "$FDEV" "-o$FOPTS"
	done
	exec 0<&9 9<&-
}

case "$1" in
  start|"")
	do_start
	;;
  restart|reload|force-reload)
	echo "Error: argument '$1' not supported" >&2
	exit 3
	;;
  stop)
	# No-op
	;;
  *)
	echo "Usage: mountall-mtab.sh [start|stop]" >&2
	exit 3
	;;
esac

:
