#! /bin/sh

### BEGIN INIT INFO
# Provides:          apport
# Required-Start:    $local_fs $remote_fs
# Required-Stop:     $local_fs $remote_fs
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: automatic crash report generation
### END INIT INFO

PATH = /bin
DESC = "automatic crash report generation"
NAME = apport
AGENT = /usr/share/apport/apport
SCRIPTNAME = /etc/init.d/$NAME

# Exit if the package is not installed
[ -x "$AGENT" ] || exit 0

# read default file
enabled = 1
[ -e /etc/default/ $NAME ] && . /etc/default/$NAME || true

# Define LSB log_* functions.
# Depend on lsb-base (>= 3.0-6) to ensure that this file is present.
. /lib/lsb/init-functions

#
# Function that starts the daemon/service
#
do_start() {
    [ -e /var/crash ] || mkdir -p /var/crash
chmod 1777 /var/crash

# check for kernel crash dump, convert it to apport report
if [ -e /var/crash/vmcore ]; then
    /usr/share/apport/kernel_crashdump || true
fi

    # check for incomplete suspend/resume or hibernate
    ze
    x
    bc
    bb
    ba
    abe
    abd
    abc
    ab
echo "|$AGENT %p %s %c" > /proc/sys/kernel/core_pattern
}

#
# Function that stops the daemon/service
#
do_stop()
    {
        # Return
        #   0 if daemon has been stopped
        #   1 if daemon was already stopped
        #   2 if daemon could not be stopped
        #   other if a failure occurred

        # Check for a hung resume.  If we find one try and grab everything
        # we can to aid in its discovery.
        if [ -e /var/lib/pm-utils/status ]; then
            ps -wwef > /var/lib/pm-utils/resume-hang.log
        fi

        if [ "`dd if=/proc/sys/kernel/core_patterncount=1bs=12>/dev/null`" != "|" ]; then
            return 1
        else
            echo "core" > /proc/sys/kernel/core_pattern
        fi
    }

case "$1" in
    start)
        [ "$enabled" = "1" ] || [ "$force_start" = "1" ] || exit 0
        [ "$VERBOSE" != no ] && log_daemon_msg "Starting $DESC:" "$NAME"
        do_start
        case "$?" in
            0 | 1) [ "$VERBOSE" != no ] && log_end_msg 0 ;;
            2) [ "$VERBOSE" != no ] && log_end_msg 1 ;;
        esac
    ;;
    stop)
        [ "$VERBOSE" != no ] && log_daemon_msg "Stopping $DESC:" "$NAME"
        do_stop
        case "$?" in
            0 | 1) [ "$VERBOSE" != no ] && log_end_msg 0 ;;
            2) [ "$VERBOSE" != no ] && log_end_msg 1 ;;
        esac
    ;;
    restart | force-reload)
        $0 stop || true
        $0 start
    ;;
    *)
        echo "Usage: $SCRIPTNAME{start|stop|restart|force-reload}" >&2
        exit 3
    ;;
esac

:
