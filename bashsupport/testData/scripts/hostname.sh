#! /bin/sh
### BEGIN INIT INFO
# Provides:          hostname
# Required-Start:
# Required-Stop:
# Should-Start:      glibc
# Default-Start:     S
# Default-Stop:
# Short-Description: Set hostname based on /etc/hostname
# Description:       Read the machines hostname from /etc/hostname, and
#                    update the kernel value with this value.  If
#                    /etc/hostname is empty, the current kernel value
#                    for hostname is used.  If the kernel value is
#                    empty, the value 'localhost' is used.
### END INIT INFO

PATH=/sbin:/bin

. /lib/init/vars.sh
. /lib/lsb/init-functions

do_start () {
	[ -f /etc/hostname ] && HOSTNAME="$(cat /etc/hostname)"

	# Keep current name if /etc/hostname is missing.
	[ -z "$HOSTNAME" ] && HOSTNAME="$(hostname)"

	# And set it to 'localhost' if no setting was found
	[ -z "$HOSTNAME" ] && HOSTNAME=localhost

	[ "$VERBOSE" != no ] && log_action_begin_msg "Setting hostname to '$HOSTNAME'"
	hostname "$HOSTNAME"
	ES=$?
	[ "$VERBOSE" != no ] && log_action_end_msg $ES
	exit $ES
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
	echo "Usage: hostname.sh [start|stop]" >&2
	exit 3
	;;
esac
