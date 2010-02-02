#! /bin/sh
### BEGIN INIT INFO
# Provides:          mountkernfs
# Required-Start:
# Required-Stop:
# Should-Start:      glibc
# Default-Start:     S
# Default-Stop:
# Short-Description: Mount kernel virtual file systems.
# Description:       Mount initial set of virtual filesystems the kernel
#                    provides and that are required by everything.
### END INIT INFO

PATH=/lib/init:/sbin:/bin
. /lib/init/vars.sh

. /lib/lsb/init-functions
. /lib/init/mount-functions.sh

[ -f /etc/default/tmpfs ] && . /etc/default/tmpfs

do_start () {
	#
	# Get some writable area available before the root is checked
	# and remounted.
	#
	RW_OPT=
	[ "${RW_SIZE:=$TMPFS_SIZE}" ] && RW_OPT=",size=$RW_SIZE"
	domount tmpfs "" /lib/init/rw tmpfs -omode=0755,nosuid$RW_OPT
	touch /lib/init/rw/.ramfs

	# Make pidfile omit directory for sendsigs
	mkdir /lib/init/rw/sendsigs.omit.d/

	#
	# Mount proc filesystem on /proc
	#
	domount proc "" /proc proc -onodev,noexec,nosuid

	#
	# Mount sysfs on /sys
	#
	# Only mount sysfs if it is supported (kernel >= 2.6)
	if grep -E -qs "sysfs\$" /proc/filesystems
	then
		domount sysfs "" /sys sysfs -onodev,noexec,nosuid
	fi

	# Mount /var/run and /var/lock as tmpfs if enabled
	if [ yes = "$RAMRUN" ] ; then
		RUN_OPT=
		[ "${RUN_SIZE:=$TMPFS_SIZE}" ] && RUN_OPT=",size=$RUN_SIZE"
		domount tmpfs "" /var/run varrun -omode=0755,nosuid$RUN_OPT
		touch /var/run/.ramfs
	fi
	if [ yes = "$RAMLOCK" ] ; then
		LOCK_OPT=
		[ "${LOCK_SIZE:=$TMPFS_SIZE}" ] && LOCK_OPT=",size=$LOCK_SIZE"
		domount tmpfs "" /var/lock varlock -omode=1777,nodev,noexec,nosuid$LOCK_OPT
		touch /var/lock/.ramfs
	fi

	# Mount spufs, if Cell Broadband processor is detected
	if [ -d /spu ] && grep -qs '^cpu.*Cell' /proc/cpuinfo; then
	        domount spufs "" /spu spufs -ogid=spu
	fi

	# Propagate files from the initramfs to our new /var/run.
	for file in /dev/.initramfs/varrun/*; do
		[ -e "$file" ] || continue
		cp -a "$file" "/var/run/${x#/dev/.initramfs/varrun/}"
	done
}

case "$1" in
  "")
	echo "Warning: mountkernfs should be called with the 'start' argument." >&2
	do_start
	;;
  start)
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
	echo "Usage: mountkernfs [start|stop]" >&2
	exit 3
	;;
esac
