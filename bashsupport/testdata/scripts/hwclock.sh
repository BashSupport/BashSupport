#!/bin/sh
# hwclock.sh	Set and adjust the CMOS clock, according to the UTC
#		setting in /etc/default/rcS (see also rcS(5)).
#
# Version:	@(#)hwclock.sh  2.00  14-Dec-1998  miquels@cistron.nl
#
# Patches:
#		2000-01-30 Henrique M. Holschuh <hmh@rcm.org.br>
#		 - Minor cosmetic changes in an attempt to help new
#		   users notice something IS changing their clocks
#		   during startup/shutdown.
#		 - Added comments to alert users of hwclock issues
#		   and discourage tampering without proper doc reading.

# WARNING:	Please read /usr/share/doc/util-linux/README.Debian.hwclock
#		before changing this file. You risk serious clock
#		misbehaviour otherwise.

### BEGIN INIT INFO
# Provides:          hwclock
# Required-Start:    mountdevsubfs
# Required-Stop:     $local_fs
# Default-Start:     S
# Default-Stop:      0 6
### END INIT INFO

FIRST=no	# debian/rules sets this to 'yes' when creating hwclockfirst.sh

# Set this to any options you might need to give to hwclock, such
# as machine hardware clock type for Alphas.
HWCLOCKPARS=

hwclocksh()
{
    [ ! -x /sbin/hwclock ] && return 0
    . /etc/default/rcS

    . /lib/lsb/init-functions
    verbose_log_action_msg() { [ "$VERBOSE" = no ] || log_action_msg "$@"; }

    [ "$GMT" = "-u" ] && UTC="yes"
    case "$UTC" in
       no|"")	GMT="--localtime"
		UTC=""
		if [ "X$FIRST" = "Xyes" ] && [ ! -r /etc/localtime ]; then
		    if [ -z "$TZ" ]; then
			log_action_msg "System clock was not updated at this time"
			return 1
		    fi
		fi
		;;
       yes)	GMT="--utc"
		UTC="--utc"
		;;
       *)	log_action_msg "Unknown UTC setting: \"$UTC\""; return 1 ;;
    esac

    case "$BADYEAR" in
       no|"")	BADYEAR="" ;;
       yes)	BADYEAR="--badyear" ;;
       *)	log_action_msg "unknown BADYEAR setting: \"$BADYEAR\""; return 1 ;;
    esac

    case "$1" in
	start)
	    if [ -w /etc ] && [ ! -f /etc/adjtime ] && [ ! -e /etc/adjtime ]; then
		echo "0.0 0 0.0" > /etc/adjtime
	    fi

	    if [ ! -w /etc/adjtime ]; then
		NOADJ="--noadjfile"
	    else
	    	NOADJ=""
	    fi

	    if [ "$FIRST" != yes ]; then
		# Uncomment the hwclock --adjust line below if you want
		# hwclock to try to correct systematic drift errors in the
		# Hardware Clock.
		#
		# WARNING: If you uncomment this option, you must either make
		# sure *nothing* changes the Hardware Clock other than
		# hwclock --systohc, or you must delete /etc/adjtime
		# every time someone else modifies the Hardware Clock.
		#
		# Common "vilains" are: ntp, MS Windows, the BIOS Setup
		# program.
		#
		# WARNING: You must remember to invalidate (delete)
		# /etc/adjtime if you ever need to set the system clock
		# to a very different value and hwclock --adjust is being
		# used.
		#
		# Please read /usr/share/doc/util-linux/README.Debian.hwclock
		# before enabling hwclock --adjust.

		#/sbin/hwclock --adjust $GMT $BADYEAR
		:
	    fi

	    if [ "$HWCLOCKACCESS" != no ]; then
		log_action_msg "Setting the system clock"

		# Copies Hardware Clock time to System Clock using the correct
		# timezone for hardware clocks in local time, and sets kernel
		# timezone. DO NOT REMOVE.
		if /sbin/hwclock --hctosys $GMT $HWCLOCKPARS $BADYEAR $NOADJ; then
		    #	Announce the local time.
		    verbose_log_action_msg "System Clock set to: `date $UTC`"
		else
		    log_warning_msg "Unable to set System Clock to: `date $UTC`"
		fi
	    else
		verbose_log_action_msg "Not setting System Clock"
	    fi
	    ;;
	stop|restart|reload|force-reload)
	    #
	    # Updates the Hardware Clock with the System Clock time.
	    # This will *override* any changes made to the Hardware Clock.
	    #
	    # WARNING: If you disable this, any changes to the system
	    #          clock will not be carried across reboots.
	    #
	    if [ "$HWCLOCKACCESS" != no ]; then
		log_action_msg "Saving the system clock"
		if [ "$GMT" = "-u" ]; then
		    GMT="--utc"
		fi
		if /sbin/hwclock --systohc $GMT $HWCLOCKPARS $BADYEAR; then
		    verbose_log_action_msg "Hardware Clock updated to `date`"
		fi
	    else
		verbose_log_action_msg "Not saving System Clock"
	    fi
	    ;;
	show)
	    if [ "$HWCLOCKACCESS" != no ]; then
		/sbin/hwclock --show $GMT $HWCLOCKPARS $BADYEAR
	    fi
	    ;;
	*)
	    log_success_msg "Usage: hwclock.sh {start|stop|reload|force-reload|show}"
	    log_success_msg "       start sets kernel (system) clock from hardware (RTC) clock"
	    log_success_msg "       stop and reload set hardware (RTC) clock from kernel (system) clock"
	    return 1
	    ;;
    esac
}

hwclocksh "$@"
