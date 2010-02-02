#! /bin/sh
### BEGIN INIT INFO
# Provides:          mountall-bootclean
# Required-Start:    mountall
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: bootclean after mountall.
# Description:       Clean temporary filesystems after
#                    all local filesystems have been mounted.
### END INIT INFO

case "$1" in
  "start")
	# Clean /tmp, /var/lock, /var/run
	. /lib/init/bootclean.sh
	;;
  restart|reload|force-reload)
	echo "Error: argument '$1' not supported" >&2
	exit 3
	;;
  stop)
	# No-op
	;;
  *)
	echo "Usage: mountall-bootclean.sh [start|stop]" >&2
	exit 3
	;;
esac

:
