#! /bin/sh
### BEGIN INIT INFO
# Provides:          checkroot
# Required-Start:    mountdevsubfs hostname
# Required-Stop:     
# Should-Start:      keymap hwclockfirst hdparm bootlogd
# Should-stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Check to root file system.
### END INIT INFO

# Include /usr/bin in path to find on_ac_power if /usr/ is on the root
# partition.
PATH=/lib/init:/sbin:/bin:/usr/bin
FSCK_LOGFILE=/var/log/fsck/checkroot
[ "$FSCKFIX" ] || FSCKFIX=no
[ "$SULOGIN" ] || SULOGIN=no
. /lib/init/vars.sh

. /lib/lsb/init-functions
. /lib/init/mount-functions.sh
. /lib/init/splash-functions-base
. /lib/init/usplash-fsck-functions.sh

do_start () {
	#
	# Set SULOGIN in /etc/default/rcS to yes if you want a sulogin to
	# be spawned from this script *before anything else* with a timeout,
	# like sysv does.
	#
	[ "$SULOGIN" = yes ] && sulogin -t 30 $CONSOLE

	KERNEL="$(uname -s)"
	MACHINE="$(uname -m)"

	#
	# Read /etc/fstab, looking for:
	# 1) The root filesystem, resolving LABEL=*|UUID=* entries to the
	# 	device node,
	# 2) Swap that is on a md device or a file that may be on a md 
	# 	device,
	# 3) The mount parameters for a devfs filesystem.
	#

	exec 9<&0 </etc/fstab

	fstabroot=/dev/root
	rootdev=none
	roottype=none
	rootopts=defaults
	rootmode=rw
	rootcheck=no
	swap_on_lv=no
	swap_on_file=no
	devfs=

	while read DEV MTPT FSTYPE OPTS DUMP PASS JUNK
	do
		case "$DEV" in
		  ""|\#*)
			continue;
			;;
		  /dev/mapper/*)
			[ "$FSTYPE" = "swap" ] && swap_on_lv=yes
			;;
		  /dev/*)
			;;
		  LABEL=*|UUID=*)
			if [ "$MTPT" = "/" ] && which findfs >/dev/null 2>&1
			then
				DEV="$(findfs "$DEV")"
			fi
			;;
		  /*)
			[ "$FSTYPE" = "swap" ] && swap_on_file=yes
			;;
		  *)
			# Devfs definition ?
			if [ "$FSTYPE" = "devfs" ] && [ "$MTPT" = "/dev" ] && mountpoint -q /dev
			then
				devfs="-t $FSTYPE $DEV $MTPT"
			fi
			;;
		esac
		[ "$MTPT" != "/" ] && continue
		rootdev="$DEV"
		fstabroot="$DEV"
		rootopts="$OPTS"
		roottype="$FSTYPE"
		( [ "$PASS" != 0 ] && [ "$PASS" != "" ]   ) && rootcheck=yes
		( [ "$FSTYPE" = "nfs" ] || [ "$FSTYPE" = "nfs4" ] ) && rootcheck=no
		case "$OPTS" in
		  ro|ro,*|*,ro|*,ro,*)
			rootmode=ro
			;;
		esac
	done

	exec 0<&9 9<&-

	#
	# Activate the swap device(s) in /etc/fstab. This needs to be done
	# before fsck, since fsck can be quite memory-hungry.
	#
	ENABLE_SWAP=no
	case "$KERNEL" in
	  Linux)
	  	if [ "$NOSWAP" = yes ]
		then
			[ "$VERBOSE" = no ] || log_warning_msg "Not activating swap as requested via bootoption noswap."
			ENABLE_SWAP=no
		else
			if [ "$swap_on_lv" = yes ]
			then
				[ "$VERBOSE" = no ] || log_warning_msg "Not activating swap on logical volume."
			elif [ "$swap_on_file" = yes ]
			then
				[ "$VERBOSE" = no ] || log_warning_msg "Not activating swap on swapfile."
			else
				ENABLE_SWAP=yes
			fi
		fi
		;;
	  *)
		ENABLE_SWAP=yes
		;;
	esac
	if [ "$ENABLE_SWAP" = yes ]
	then
		if [ "$VERBOSE" = no ]
		then
			log_action_begin_msg "Activating swap"
			swapon -a -e >/dev/null 2>&1
			log_action_end_msg $?
		else
			log_daemon_msg "Activating swap"
			swapon -a -v
			log_end_msg $?
		fi
	fi

	#
	# Does the root device in /etc/fstab match with the actual device ?
	# If not we try to use the /dev/root alias device, and if that
	# fails we create a temporary node in /lib/init/rw.
	#
	if [ "$rootcheck" = yes ]
	then
		ddev="$(mountpoint -qx $rootdev)"
		rdev="$(mountpoint -d /)"
		if [ "$ddev" != "$rdev" ] && [ "$ddev" != "4:0" ]
		then
			if [ "$(mountpoint -qx /dev/root)" = "4:0" ]
			then
				rootdev=/dev/root
			else
				if \
					rm -f /lib/init/rw/rootdev \
					&& mknod -m 600 /lib/init/rw/rootdev b ${rdev%:*} ${rdev#*:} \
					&& [ -e /lib/init/rw/rootdev ]
				then
					rootdev=/lib/init/rw/rootdev
				else
					rootfatal=yes
				fi
			fi
		fi
	fi

	#
	# Bother, said Pooh.
	#
	if [ "$rootfatal" = yes ]
	then
		log_failure_msg "The device node $rootdev for the root filesystem is missing or incorrect or there is no entry for the root filesystem listed in /etc/fstab. The system is also unable to create a temporary node in /lib/init/rw. This means you have to fix the problem manually."
		log_warning_msg "A maintenance shell will now be started. CONTROL-D will terminate this shell and restart the system."
		# Start a single user shell on the console
		if ! sulogin $CONSOLE
		then
			log_failure_msg "Attempt to start maintenance shell failed. Will restart in 5 seconds."
			sleep 5
		fi
		[ "$VERBOSE" = no ] || log_action_msg "Will now restart"
		reboot -f
	fi

	# See if we're on AC Power.  If not, we're not gonna run our
	# check.  If on_ac_power (in /usr/) is unavailable, behave as
	# before and check all file systems needing it.
	if which on_ac_power >/dev/null 2>&1 && [ "$rootcheck" = yes ]
	then
		if [ -d /proc/acpi ]; then
			modprobe ac >/dev/null 2>&1
		fi
		on_ac_power >/dev/null 2>&1
		if [ "$?" -eq 1 ]
		then
			log_warning_msg "On battery power, so skipping file system check."
			rootcheck=no
		fi
	fi

	#
	# See if we want to check the root file system.
	#
	FSCKCODE=0
	if [ -f /fastboot ]
	then
		[ "$rootcheck" = yes ] && log_warning_msg "Fast boot enabled, so skipping file system check."
		rootcheck=no
	fi

	if [ "$rootcheck" = yes ]
	then
		#
		# Ensure that root is quiescent and read-only before fsck'ing.
		#
		# mount -n -o remount,ro / would be the correct syntax but
		# mount can get confused when there is a "bind" mount defined
		# in fstab that bind-mounts "/" somewhere else.
		#
		# So we use mount -n -o remount,ro $rootdev / but that can
		# fail on older kernels on sparc64/alpha architectures due
		# to a bug in sys_mount().
		#
		# As a compromise we try both.
		#
		if ! mount    -n -o remount,ro              $rootdev /              \
			&& ! mount -n -o remount,ro -t dummytype $rootdev /  2>/dev/null \
			&& ! mount -n -o remount,ro                       /  2>/dev/null
		then
			log_failure_msg "Cannot check root file system because it is not mounted read-only."
			rootcheck=no
		fi
	fi

	#
	# The actual checking is done here.
	#
	if [ "$rootcheck" = yes ]
	then
		if [ -f /forcefsck ]
		then
			force="-f"
		else
			force=""
		fi

		if [ "$FSCKFIX" = yes ]
		then
			fix="-y"
		else
			fix="-a"
		fi

		spinner="-C"
		case "$TERM" in
		  dumb|network|unknown|"")
			spinner="" ;;
		esac
		# This Linux/s390 special case should go away.
		if [ "${KERNEL}:${MACHINE}" = Linux:s390 ]
		then
			spinner=""
		fi
		
		if [ "$VERBOSE" = no ]
		then
			log_action_begin_msg "Checking root file system"
                        if [ "$roottype" = "ext2" -o "$roottype" = "ext3" ] && pidof usplash; then
                            PROGRESS_FILE=`mktemp -p /var/run` || PROGRESS_FILE=/var/run/checkroot_fsck
                            set -m
                            logsave -s $FSCK_LOGFILE fsck -C3 $force $fix -t $roottype $rootdev >/dev/console 2>&1 3>$PROGRESS_FILE &
                            set +m
                            usplash_progress "$PROGRESS_FILE"
                            rm -f $PROGRESS_FILE
                        else
                            logsave -s $FSCK_LOGFILE fsck $spinner $force $fix -t $roottype $rootdev
                            FSCKCODE=$?
                        fi
			if [ "$FSCKCODE" = 0 ]
			then
				log_action_end_msg 0
			else
				log_action_end_msg 1 "code $FSCKCODE"
			fi
		else
			log_daemon_msg "Will now check root file system"
			logsave -s $FSCK_LOGFILE fsck $spinner $force $fix -V -t $roottype $rootdev
			FSCKCODE=$?
			log_end_msg $FSCKCODE
		fi
	fi

	#
	# If there was a failure, drop into single-user mode.
	#
	# NOTE: "failure" is defined as exiting with a return code of
	# 4 or larger. A return code of 1 indicates that file system
	# errors were corrected but that the boot may proceed. A return
	# code of 2 or 3 indicates that the system should immediately reboot.
	#
	if [ "$FSCKCODE" -gt 3 ]
	then
		# Surprise! Re-directing from a HERE document (as in "cat << EOF")
		# does not work because the root is currently read-only.
		log_failure_msg "An automatic file system check (fsck) of the root filesystem failed. A manual fsck must be performed, then the system restarted. The fsck should be performed in maintenance mode with the root filesystem mounted in read-only mode."
		log_warning_msg "The root filesystem is currently mounted in read-only mode. A maintenance shell will now be started. After performing system maintenance, press CONTROL-D to terminate the maintenance shell and restart the system."
		# Start a single user shell on the console
		if ! sulogin $CONSOLE
		then
			log_failure_msg "Attempt to start maintenance shell failed. Will restart in 5 seconds."
			sleep 5
		fi
		[ "$VERBOSE" = no ] || log_action_msg "Will now restart"
		reboot -f
	elif [ "$FSCKCODE" -gt 1 ]
	then
		log_failure_msg "The file system check corrected errors on the root partition but requested that the system be restarted."
		log_warning_msg "The system will be restarted in 5 seconds."
		sleep 5
		[ "$VERBOSE" = no ] || log_action_msg "Will now restart"
		reboot -f
	fi

	#
	# Remount root to final mode (rw or ro).
	#
	# See the comments above at the previous "mount -o remount"
	# for an explanation why we try this twice.
	#
	if ! mount -n -o remount,$rootopts,$rootmode $fstabroot / 2>/dev/null
	then
		mount -n -o remount,$rootopts,$rootmode /
	fi

	#
	# We only create/modify /etc/mtab if the location where it is
	# stored is writable. If /etc/mtab is a symlink into /proc/
	# then it is not writable.
	#
	INIT_MTAB_FILE=no
	MTAB_PATH="$(readlink -f /etc/mtab || :)"
	case "$MTAB_PATH" in
	  /proc/*)
		;;
	  /*)
		if touch "$MTAB_PATH" >/dev/null 2>&1
		then
			:> "$MTAB_PATH"
			rm -f ${MTAB_PATH}~
			INIT_MTAB_FILE=yes
		fi
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

	if [ "$INIT_MTAB_FILE" = yes ]
	then
		[ "$roottype" != none ] &&
			mount -f -o $rootopts -t $roottype $fstabroot /
		[ "$devfs" ] && mount -f $devfs
	fi

	#
	# Remove /lib/init/rw/rootdev if we created it.
	#
	rm -f /lib/init/rw/rootdev
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
	echo "Usage: checkroot.sh [start|stop]" >&2
	exit 3
	;;
esac

:
