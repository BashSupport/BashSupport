#! /bin/sh
#
# rc
#
# Starts/stops services on runlevel changes.
#
# Optimization: A start script is not run when the service was already
# configured to run in the previous runlevel.  A stop script is not run
# when the the service was already configured not to run in the previous
# runlevel.
#
# Authors:
# 	Miquel van Smoorenburg <miquels@cistron.nl>
# 	Bruce Perens <Bruce@Pixar.com>

PATH=/sbin:/usr/sbin:/bin:/usr/bin
export PATH

# Un-comment the following for debugging.
# debug=echo

# Specify method used to enable concurrent init.d scripts.
# Valid options are 'none', 'shell' and 'startpar'.  To enable the
# concurrent boot option, the init.d script order must allow for
# concurrency.  This is not the case with the default boot sequence in
# Debian as of 2008-01-20.  Before enabling concurrency, one need to
# check the sequence values of all boot scripts, and make sure only
# scripts that can be started in parallel have the same sequence
# number, and that a scripts dependencies have a earlier sequence
# number. See the insserv package for a away to reorder the boot
# automatically to allow this.
CONCURRENCY=none

# Make sure the name survive changing the argument list
scriptname="$0"

umask 022

on_exit() {
    echo "error: '$scriptname' exited outside the expected code flow."
}
trap on_exit EXIT # Enable emergency handler

# Ignore CTRL-C only in this shell, so we can interrupt subprocesses.
trap ":" INT QUIT TSTP

# Set onlcr to avoid staircase effect.
stty onlcr 0>&1

# Functions for splash progress bars
if [ -e /lib/init/splash-functions-base ] ; then
    . /lib/init/splash-functions-base
else
    # Quiet down script if old initscripts version without /lib/init/splash-functions-base is used.
    splash_progress() { return 1; }
fi

# Now find out what the current and what the previous runlevel are.

runlevel=$RUNLEVEL
# Get first argument. Set new runlevel to this argument.
[ "$1" != "" ] && runlevel=$1
if [ "$runlevel" = "" ]
then
	echo "Usage: $scriptname <runlevel>" >&2
	exit 1
fi
previous=$PREVLEVEL
[ "$previous" = "" ] && previous=N

export runlevel previous

. /etc/default/rcS
export VERBOSE

if [ -f /lib/lsb/init-functions ] ; then
    . /lib/lsb/init-functions
else
    log_action_msg() { echo $@; }
fi

#
# Stub to do progress bar ticks (for splash programs) on startup
#
startup_progress() {
    # Avoid divide by zero if anyone moved xdm/kdm/gdm first in a runlevel.
    if [ 0 -eq "$num_steps" ] ; then return; fi

    step=$(($step + $step_change))
    progress=$(($step * $progress_size / $num_steps + $first_step))
    $debug splash_progress "$progress" || true
}

sh=sh
# Debian Policy §9.3.1 requires .sh scripts in runlevel S to be
# sourced However, some important packages currently contain .sh
# scripts that do "exit" at some point, thus killing this process and
# the boot.  Bad!  See also bug #339955.
#[ S = "$runlevel" ] && sh=.

#
# Check if we are able to use make like booting.  It require the
# insserv package to be enabled.
#
if [ startpar = "$CONCURRENCY" ] ; then
    test -s /etc/init.d/.depend.boot  || CONCURRENCY="none"
    test -s /etc/init.d/.depend.start || CONCURRENCY="none"
    test -s /etc/init.d/.depend.stop  || CONCURRENCY="none"
    startpar -v      > /dev/null 2>&1 || CONCURRENCY="none"

    # startpar do not work properly at the start of rcS.d/.  Avoid it.
    # See #457896 for details.

    if [ S = "$runlevel" ] ; then
	CONCURRENCY=none
    fi
fi

#
# Start script or program.
#
case "$CONCURRENCY" in
  shell)
  	log_action_msg "Using shell-style concurrent boot in runlevel $runlevel"
	startup() {
		action=$1
		shift
		scripts="$@"
		backgrounded=0
		for script in $scripts ; do
			case "$script" in
			  *.sh)
				if [ "." = "$sh" ] ; then
					RC_SAVE_PATH="$PATH"
					set -- "$action"
					$debug . "$script"
					PATH="$RC_SAVE_PATH"
					startup_progress
				else
					$debug $sh "$script" $action
					startup_progress
				fi
				;;
			  *)
				$debug "$script" $action &
				startup_progress
				backgrounded=1
				;;
			esac
		done
		[ 1 = "$backgrounded" ] && wait
	}
	;;
  startpar)
  	log_action_msg "Using startpar-style concurrent boot in runlevel $runlevel"
	startup() {
		action=$1
		shift
		scripts="$@"
		# Make sure .sh scripts are sourced in runlevel S
		if [ "." = "$sh" ] ; then
			newscripts=
			for script in $scripts ; do
				case "$script" in
				  *.sh)
					RC_SAVE_PATH="$PATH"
					set -- "$action"
					$debug . "$script"
					PATH="$RC_SAVE_PATH"
					startup_progress
					;;
				  *)
					newscripts="$newscripts $script"
					step=$(($step + $step_change))
					;;
				esac
			done
			scripts="$newscripts"
		else
			# Update progress bar counter and jump to the new position
			for script in $scripts ; do
				step=$(($step + $step_change))
			done
		fi

		# startpar is not able to handle time jumps.  So the
		# hwclock.sh scripts should not be executed from
		# within startpar.  The .sh hack above make this
		# problem irrelevant. [pere 2005-09-10]
		[ -n "$scripts" ] && $debug startpar -a $action $scripts

		# Jump back one step to compencate for stepping one
		# time too many in the for loop, and to keep the same
		# location as the startup_progress call in the *.sh
		# case.
		step=$(($step - $step_change))
		startup_progress
	}
	;;
  none|*)
	startup() {
		action=$1
		shift
		scripts="$@"
		for script in $scripts ; do
			case "$script" in
			  *.sh)
				if [ "." = "$sh" ] ; then
					RC_SAVE_PATH="$PATH"
					set "$action"
					$debug . "$script"
					PATH="$RC_SAVE_PATH"
					startup_progress
				else
					$debug $sh "$script" $action
					startup_progress
				fi
				;;
			  *)
				$debug "$script" $action
				startup_progress
				;;
			esac
		done
	}
	;;
esac

# Is there an rc directory for this new runlevel?
if [ -d /etc/rc$runlevel.d ]
then
	# Find out where in the progress bar the initramfs got to.
	PROGRESS_STATE=0
	if [ -f /dev/.initramfs/progress_state ]; then
	    . /dev/.initramfs/progress_state
	fi

	# Split the remaining portion of the progress bar into thirds
	progress_size=$(((100 - $PROGRESS_STATE) / 3))

	case "$runlevel" in
		0|6)
			ACTION=stop
			# Count down from 0 to -100 and use the entire bar
			first_step=0
			progress_size=100
			step_change=-1
			;;
	        S)
		        ACTION=start
			# Begin where the initramfs left off and use 2/3
			# of the remaining space
			first_step=$PROGRESS_STATE
			progress_size=$(($progress_size * 2))
			step_change=1
			;;
		*)
			ACTION=start
			# Begin where rcS left off and use the final 1/3 of
			# the space (by leaving progress_size unchanged)
			first_step=$(($progress_size * 2 + $PROGRESS_STATE))
			step_change=1
			;;
	esac

	# Count the number of scripts we need to run
	# (for progress bars)
	num_steps=0
	for s in /etc/rc$runlevel.d/[SK]*; do
	    case "${s##/etc/rc$runlevel.d/S??}" in
	     gdm|xdm|kdm|ltsp-client|ltsp-client-core|reboot|halt)
		break
		;;
	    esac
	    num_steps=$(($num_steps + 1))
	done
	step=0

	# First, run the KILL scripts.
	if [ "$previous" != N ]
	then
		# Run all scripts with the same level in parallel
		CURLEVEL=""
		for s in /etc/rc$runlevel.d/K*
		do
			# Extract order value from symlink
			level=${s#/etc/rc$runlevel.d/K}
			level=${level[a-zA-Z]*}
			if [ "$level" = "$CURLEVEL" ]
			then
				continue
			fi
			CURLEVEL=$level
			SCRIPTS=""
			for i in /etc/rc$runlevel.d/K$level*
			do
				# Check if the script is there.
				[ ! -f $i ] && continue

				#
				# Find stop script in previous runlevel but
				# no start script there.
				#
				suffix=${i#/etc/rc$runlevel.d/K[0-9][0-9]}
				previous_stop=/etc/rc$previous.d/K[0-9][0-9]$suffix
				previous_start=/etc/rc$previous.d/S[0-9][0-9]$suffix
				#
				# If there is a stop script in the previous level
				# and _no_ start script there, we don't
				# have to re-stop the service.
				#
				[ -f $previous_stop ] && [ ! -f $previous_start ] && continue

				# Stop the service.
				SCRIPTS="$SCRIPTS $i"
			done
			startup stop $SCRIPTS
		done
	fi

	# Now run the START scripts for this runlevel.
	# Run all scripts with the same level in parallel
	CURLEVEL=""
	for s in /etc/rc$runlevel.d/S*
	do
		# Extract order value from symlink
		level=${s#/etc/rc$runlevel.d/S}
		level=${level%%[a-zA-Z]*}
		if [ "$level" = "$CURLEVEL" ]
		then
			continue
		fi
		CURLEVEL=$level
		SCRIPTS=""
		for i in /etc/rc$runlevel.d/S$level*
		do
			[ ! -f $i ] && continue

			if [ "$previous" != N ]
			then
				#
				# Find start script in previous runlevel and
				# stop script in this runlevel.
				#
				suffix=${i#/etc/rc$runlevel.d/S[0-9][0-9]}
				stop=/etc/rc$runlevel.d/K[0-9][0-9]$suffix
				previous_start=/etc/rc$previous.d/S[0-9][0-9]$suffix
				#
				# If there is a start script in the previous level
				# and _no_ stop script in this level, we don't
				# have to re-start the service.
				#
				if [ start = "$ACTION" ] ; then
				    [ -f $previous_start ] && [ ! -f $stop ] && continue
				else
				    # Workaround for the special
				    # handling of runlevels 0 and 6.
				    previous_stop=/etc/rc$previous.d/K[0-9][0-9]$suffix
				    #
				    # If there is a stop script in the previous level
				    # and _no_ start script there, we don't
				    # have to re-stop the service.
				    #
				    [ -f $previous_stop ] && [ ! -f $previous_start ] && continue
				fi

			fi
			SCRIPTS="$SCRIPTS $i"
		done
		startup $ACTION $SCRIPTS
	done
fi

if [ S = "$runlevel" ]
then
	#
	# For compatibility, run the files in /etc/rc.boot too.
	#
	[ -d /etc/rc.boot ] && run-parts /etc/rc.boot
fi

trap - EXIT # Disable emergency handler

exit 0

