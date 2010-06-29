#!/bin/sh
### BEGIN INIT INFO
# Provides:          console-screen.kbd
# Required-Start:    $local_fs $remote_fs
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Description: Set console screen modes and fonts
# Short-Description:    Prepare console
### END INIT INFO

# This is the boot script for the `kbd' package.
# It loads parameters from /etc/kbd/config and maybe loads
# default font and map.
# (c) 1997 Yann Dirson

# If setupcon is present, then we've been superseded by console-setup.
if type setupcon >/dev/null 2>&1; then
    exit 0
fi

PKG=kbd
if [ -r /etc/$PKG/config ]; then
    . /etc/$PKG/config
fi

if [ -d /etc/$PKG/config.d ]; then
    for i in `run-parts --list /etc/$PKG/config.d `; do
       . $i
    done
fi

# do some magic with the variables for compatibility with the config
# file of console-tools
for vc in '' `set | grep "^.*_vc[0-9][0-9]*="  | sed 's/^.*\(_vc[0-9][0-9]*\)=.*/\1/'`
do
    eval [ "a" ]
    eval [ \"\${SCREEN_FONT$vc}\" ] && eval CONSOLE_FONT$vc=\${CONSOLE_FONT$vc:-\${SCREEN_FONT$vc}}
    eval [ \"\${SCREEN_FONT_MAP$vc}\" ] && eval FONT_MAP$vc=\${FONT_MAP$vc:-\${SCREEN_FONT_MAP$vc}}
    eval [ \"\${APP_CHARSET_MAP$vc}\" ] && eval CONSOLE_MAP$vc=\${CONSOLE_MAP$vc:-\${APP_CHARSET_MAP$vc}}
done

. /lib/lsb/init-functions

PATH=/sbin:/bin:/usr/sbin:/usr/bin
SETFONT_OPT="-v"

if which setupcon >/dev/null
then
    HAVE_SETUPCON=yes
fi

# set DEVICE_PREFIX depending on devfs/udev
if [ -d /dev/vc ]; then
    DEVICE_PREFIX="/dev/vc/"
else
    DEVICE_PREFIX="/dev/tty"
fi

# determine the system charmap
CHARMAP=`LANG=$LANG LC_ALL=$LC_ALL LC_CTYPE=$LC_CTYPE locale charmap 2>/dev/null`
if [ "$CHARMAP" = "UTF-8" -a -z "$CONSOLE_MAP" ]
then
    UNICODE_MODE=yes
fi

reset_vga_palette ()
{
    if [ -f /proc/fb ]; then
        # They have a framebuffer device.
         That means we have work to do...
        echo -n "]R"
    fi
}

unicode_start_stop ()
{
    vc=$1
    if [ -f /etc/environment ] || [ -f /etc/default/locale ]
    then
        for var in LANG LC_CTYPE LC_ALL
        do
#            value=$(egrep "^[^#]*${var}=" /etc/environment /etc/default/locale 2>/dev/null | tail -n1 | cut -d= -f2)
            eval $var=$value
        done
    fi
    if [ -n "$UNICODE_MODE" -a -z "`eval echo \$CONSOLE_MAP_vc$vc`" ]; then
        action=unicode_start
    else
        action=unicode_stop
    fi
    if [ "${CONSOLE_FONT}" ]; then
        $action "${CONSOLE_FONT}" < ${DEVICE_PREFIX}$vc > ${DEVICE_PREFIX}$vc 2> /dev/null || true
    else
        $action < ${DEVICE_PREFIX}$vc > ${DEVICE_PREFIX}$vc 2> /dev/null || true
    fi
}

setup ()
{
    # be sure the main program is installed
    which setfont >/dev/null || return

    VT="no"
    # If we can't access the console, quit
    CONSOLE_TYPE=`fgconsole 2>/dev/null` || return

    if [ ! $CONSOLE_TYPE = "serial" ]; then
        readlink /proc/self/fd/0 | grep -q -e /dev/vc -e '/dev/tty[^p]' -e /dev/console
        if [ $? -eq 0 ]; then
            VT="yes"
            reset_vga_palette
        fi
    fi

    [ $VT = "no" ] && return

    # start vcstime
    if [ "${DO_VCSTIME}" = "yes" ] && which vcstime >/dev/null; then
        # Different device name for 2.6 kernels and devfs
        if [ `uname -r | cut -f 2 -d .` = 6 ] && [ -e /dev/.devfsd ]; then
            VCSTIME_OPT="-2 /dev/vcsa0"
        else
            VCSTIME_OPT=""
        fi
        [ "$VERBOSE" != "no" ] && log_action_begin_msg "Starting clock on text console"
        vcstime ${VCSTIME_OPT} &
        [ "$VERBOSE" != "no" ] && log_action_end_msg 0
    fi

    LIST_CONSOLES=`sed -e '/^ *#/d' /etc/inittab | grep -e '\<tty[0-9]*\>' | awk -F: '{printf "%s ", $1}'`

    # Global default font+map
    if [ -z "${HAVE_SETUPCON}" -a "${CONSOLE_FONT}" ]; then
        [ "$VERBOSE" != "no" ] && log_action_begin_msg "Setting up general console font"
        sfm="${FONT_MAP}" && [ "$sfm" ] && sfm="-u $sfm"
        acm="${CONSOLE_MAP}" && [ "$acm" ] && acm="-m $acm"
    
        # Set for the first 6 VCs (as they are allocated in /etc/inittab)
        for vc in $LIST_CONSOLES
        do
            if ! ( unicode_start_stop $vc \
                   && setfont -C ${DEVICE_PREFIX}$vc ${SETFONT_OPT} $sfm ${CONSOLE_FONT} $acm )
            then
                [ "$VERBOSE" != "no" ] && log_action_end_msg 1
                break
            fi
        done
        [ "$VERBOSE" != "no" ] && log_action_end_msg 0
    fi

    # Default to Unicode mode for new VTs?
    if [ -f /sys/module/vt/parameters/default_utf8 ]; then
        if [ -n "$UNICODE_MODE" ]; then
            echo 1
        else
            echo 0
        fi > /sys/module/vt/parameters/default_utf8
    fi

    # Per-VC font+sfm
    PERVC_FONTS="`set | grep "^CONSOLE_FONT_vc[0-9]*="  | tr -d \' `"
    if [ -z "${HAVE_SETUPCON}" -a "${PERVC_FONTS}" ]; then
        [ "$VERBOSE" != "no" ] && log_action_begin_msg "Setting up per-VC fonts"
        for font in ${PERVC_FONTS}
        do
            # extract VC and FONTNAME info from variable setting
            vc=`echo $font | cut -b16- | cut -d= -f1`
            eval font=\$CONSOLE_FONT_vc$vc
            # eventually find an associated SFM
            eval sfm=\${FONT_MAP_vc${vc}}
            [ "$sfm" ] && sfm="-u $sfm"
            if ! ( unicode_start_stop $vc \
                   && setfont -C ${DEVICE_PREFIX}$vc ${SETFONT_OPT} $sfm $font )
            then
                [ "$VERBOSE" != "no" ] && log_action_end_msg 1
                break
            fi
        done
        [ "$VERBOSE" != "no" ] && log_action_end_msg 0
    fi


    # Per-VC ACMs
    PERVC_ACMS="`set | grep "^CONSOLE_MAP_vc[0-9]*="  | tr -d \' `"
    if [ -z "${HAVE_SETUPCON}" -a "${PERVC_ACMS}" ]; then
        [ "$VERBOSE" != "no" ] && log_action_begin_msg "Setting up per-VC ACM's"
        for acm in ${PERVC_ACMS}
          do
          # extract VC and ACM_FONTNAME info from variable setting
          vc=`echo $acm | cut -b15- | cut -d= -f1`
          eval acm=\$CONSOLE_MAP_vc$vc
          if ! setfont -C "${DEVICE_PREFIX}$vc" ${SETFONT_OPT} -m "$acm"; then
              [ "$VERBOSE" != "no" ] && log_action_end_msg 1
              break
          fi
        done
        [ "$VERBOSE" != "no" ] && log_action_end_msg 0
    fi
    

    # screensaver stuff
    setterm_args=""
    if [ "$BLANK_TIME" ]; then
        setterm_args="$setterm_args -blank $BLANK_TIME"
    fi
    if [ "$BLANK_DPMS" ]; then
        setterm_args="$setterm_args -powersave $BLANK_DPMS"
    fi
    if [ "$POWERDOWN_TIME" ]; then
        setterm_args="$setterm_args -powerdown $POWERDOWN_TIME"
    fi
    if [ "$setterm_args" ]; then
        setterm $setterm_args 
    fi

    # Keyboard rate and delay
    KBDRATE_ARGS=""
    if [ -n "$KEYBOARD_RATE" ]; then
        KBDRATE_ARGS="-r $KEYBOARD_RATE"
    fi
    if [ -n "$KEYBOARD_DELAY" ]; then
        KBDRATE_ARGS="$KBDRATE_ARGS -d $KEYBOARD_DELAY"
    fi
    if [ -n "$KBDRATE_ARGS" ]; then
        [ "$VERBOSE" != "no" ] && log_action_begin_msg "Setting keyboard rate and delay"
        kbdrate -s $KBDRATE_ARGS
        [ "$VERBOSE" != "no" ] && log_action_end_msg 0
    fi

    # Inform gpm if present, of potential changes.
    if [ -f /var/run/gpm.pid ]; then
        kill -WINCH `cat /var/run/gpm.pid` 2> /dev/null
    fi

    # Allow user to remap keys on the console
    if [ -z "${HAVE_SETUPCON}" -a -r /etc/$PKG/remap ]; then
        dumpkeys < ${DEVICE_PREFIX}1 | sed -f /etc/$PKG/remap | loadkeys --quiet
    fi

    # Set LEDS here
    if [ -n "$LEDS" ]; then
        for i in $LIST_CONSOLES
        do
            setleds -D $LEDS < $DEVICE_PREFIX$i
        done
    fi
}

case "$1" in
    start|reload|restart|force-reload)
        if [ -n "$HAVE_SETUPCON" ]
        then
                log_action_msg "Setting console screen modes"
        else
                log_action_msg "Setting console screen modes and fonts"
        fi
        setup
        ;;
    stop)
        ;;
    *)
        setup
        ;;
esac

:

