#! /bin/sh
### BEGIN INIT INFO
# Provides:          checkfs
# Required-Start:    checkroot
# Required-Stop:
# Should-Start:      mtab cryptdisks
# Default-Start:     S
# Default-Stop:
# Short-Description: Check all filesystems.
### END INIT INFO

# Include /usr/bin in path to find on_ac_power if /usr/ is on the root
# partition.
PATH=/sbin:/bin:/usr/bin
FSCK_LOGFILE=/var/log/fsck/checkfs
[ "$FSCKFIX" ] || FSCKFIX=no
. /lib/init/vars.sh

. /lib/lsb/init-functions
. /lib/init/splash-functions-base
. /lib/init/usplash-fsck-functions.sh

do_start () {
	# See if we're on AC Power.  If not, we're not gonna run our
	# check.  If on_ac_power (in /usr/) is unavailable, behave as
	# before and check all file systems needing it.
	if which on_ac_power >/dev/null 2>&1
	then
		on_ac_power >/dev/null 2>&1
		if [ $? -eq 1 ]
		then
			[ "$VERBOSE" = no ] || log_success_msg "Running on battery power, so skipping file system check."
			BAT=yes
		fi
	fi

	#
	# Check the rest of the file systems.
	#
	if [ ! -f /fastboot ] && [ ! "$BAT" ] && [ "$FSCKTYPES" != "none" ]
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
			spinner=""
			;;
		esac
		[ "$(uname -m)" = s390 ] && spinner=""  # This should go away
		FSCKTYPES_OPT=""
		[ "$FSCKTYPES" ] && FSCKTYPES_OPT="-t $FSCKTYPES"
		handle_failed_fsck() {
			log_failure_msg "File system check failed. A log is being saved in ${FSCK_LOGFILE} if that location is writable. Please repair the file system manually."
			 log_warning_msg "A maintenance shell will now be started. CONTROL-D will terminate this shell and resume system boot."
			# Start a single user shell on the console
			if ! sulogin $CONSOLE
			then
				log_failure_msg "Attempt to start maintenance shell failed. Continuing with system boot in 5 seconds."
				sleep 5
			fi
		}
		if [ "$VERBOSE" = no ]
		then
			log_action_begin_msg "Checking file systems"
                        if pidof usplash; then
                            PROGRESS_FILE=`mktemp` || exit 1
                            set -m
                            logsave -s $FSCK_LOGFILE fsck -C3 -R -A $fix $force $FSCKTYPES_OPT >/dev/console 2>&1 3>$PROGRESS_FILE &
                            set +m
                            usplash_progress "$PROGRESS_FILE"
                            rm -f $PROGRESS_FILE
                        else
                            logsave -s $FSCK_LOGFILE fsck $spinner -R -A $fix $force $FSCKTYPES_OPT
                            FSCKCODE=$?
                        fi

			if [ "$FSCKCODE" -gt 1 ]
			then
				log_action_end_msg 1 "code $FSCKCODE"
				handle_failed_fsck
			else
				log_action_end_msg 0
			fi
		else
			if [ "$FSCKTYPES" ]
			then
				log_action_msg "Will now check all file systems of types $FSCKTYPES"
			else
				log_action_msg "Will now check all file systems"
			fi
			logsave -s $FSCK_LOGFILE fsck $spinner -V -R -A $fix $force $FSCKTYPES_OPT
			FSCKCODE=$?
			if [ "$FSCKCODE" -gt 1 ]
			then
				handle_failed_fsck
			else
				log_success_msg "Done checking file systems. A log is being saved in ${FSCK_LOGFILE} if that location is writable."
			fi
		fi
	fi
	rm -f /fastboot /forcefsck
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
	echo "Usage: checkfs.sh [start|stop]" >&2
	exit 3
	;;
esac

:
