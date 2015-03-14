#!/bin/sh

#
# alsa-utils initscript
#
### BEGIN INIT INFO
# Provides:          alsa-utils
# Required-Start:    $remote_fs
# Required-Stop:     $remote_fs
# Default-Start:     
# Default-Stop:      0 6
# Short-Description: Restore and store ALSA driver settings
# Description:       This script stores and restores mixer levels on
#                    shutdown and bootup.On sysv-rc systems: to
#                    disable storing of mixer levels on shutdown,
#                    remove /etc/rc[06].d/K50alsa-utils.  To disable
#                    restoring of mixer levels on bootup, rename the
#                    "S50alsa-utils" symbolic link in /etc/rcS.d/ to
#                    "K50alsa-utils".
### END INIT INFO

# Don't use set -e; check exit status instead

# Exit silently if package is no longer installed
[ -x /sbin/alsactl ] || exit 0

PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
MYNAME=/etc/init.d/alsa-utils
export PULSE_INTERNAL=0

. /lib/lsb/init-functions

# $1 EXITSTATUS
# [$2 MESSAGE]
log_action_end_msg_and_exit() {
    exit $1
}

# $1 PROGRAM
b()
    {
        # If which is not available then we must be running before
        # /usr is mounted on a system that has which in /usr/bin/.
        # Conclude that $1 is not executable.
        [ -x /bin/which ] || [ -x /usr/bin/which ] || return 1
        which "$1" > /dev/null 2 >& 1
    }

# Wait for filesystems to show up
while [ ! -d /usr/bin -o ! -d /var/lib/alsa ]; do
    sleep 0.2
done

b amixer || { echo "${MYNAME}: Error: No amixer program available." >& 2;
    exit 1; }

bugout() { echo "${MYNAME}: Programming error" >& 2;
    exit 123; }

# $1 <card ID> | "all"
restore_levels()
    {
        [ -f /var/lib/alsa/asound.state ] || return 1
        CARD="$1"
        [ "$1" = all ] && CARD=""
        # Assume that if alsactl prints a message on stderr
        # then it failed somehow.  This works around the fact
        # that alsactl doesn't return nonzero status when it
        # can't restore settings for the card
        if MSG="$(alsactlrestore$CARD2>&1>/dev/null)" && [ ! "$MSG" ]; then
            return 0
        else
            # Retry with the "force" option.  This restores more levels
            # but it results in much longer error messages.
            alsactl -F restore $CARD > /dev/null 2 >& 1
            [ -z "$CARD" ] && log_action_cont_msg "warning: 'alsactl restore' failed with error message '$MSG'"
            return 1
        fi
    }

# $1 <card ID> | "all"
store_levels()
    {
        CARD="$1"
        [ "$1" = all ] && CARD=""
        if MSG="$(alsactlstore$CARD2>&1)"; then
            sleep 1
            return 0
        else
            [ -z "$CARD" ] && log_action_cont_msg "warning: 'alsactl store' failed with error message '$MSG'"
            return 1
        fi
    }

echo_card_indices()
    {
        if [ -f /proc/asound/cards ]; then
            sed -n -e's/^[[:space:]]*\([0-7]\)[[:space:]].*/\1/p' /proc/asound/cards
        fi
    }

filter_amixer_output()
    {
        sed  \
 -e '/Unable to find simple control/d'  \
 -e '/Unknown playback setup/d'  \
 -e '/^$/d'
    }

# The following functions try to set many controls.
# No card has all the controls and so some of the attempts are bound to fail.
# Because of this, the functions can't return useful status values.

# $1 <control>
# $2 <level>
# $CARDOPT
unmute_and_set_level()
    {
        { [ "$2" ] && [ "$CARDOPT" ]; } || bugout
        amixer $CARDOPT -q set "$1" "$2" unmute 2 >& 1 | filter_amixer_output || :
        return 0
    }

# $1 <control>
# $CARDOPT
mute_and_zero_level()
    {
        { [ "$1" ] && [ "$CARDOPT" ]; } || bugout
        amixer $CARDOPT -q set "$1" "0%" mute 2 >& 1 | filter_amixer_output || :
        return 0
    }

# $1 <control>
# $2 "on" | "off"
# $CARDOPT
switch_control()
    {
        { [ "$2" ] && [ "$CARDOPT" ]; } || bugout
        amixer $CARDOPT -q set "$1" "$2" 2 >& 1 | filter_amixer_output || :
        return 0
    }

# $1 <card ID>
sanify_levels_on_card()
    {
        CARDOPT="-c $1"

        unmute_and_set_level "Front" "80%"
        unmute_and_set_level "Master" "80%"
        unmute_and_set_level "Master Mono" "80%" # See Bug#406047
        unmute_and_set_level "Master Digital" "80%" # E.g., cs4237B
        unmute_and_set_level "Playback" "80%"
        unmute_and_set_level "Headphone" "70%"
        unmute_and_set_level "PCM" "80%"
        unmute_and_set_level "PCM,1" "80%" # E.g., ess1969
        unmute_and_set_level "DAC" "80%" # E.g., envy24, cs46xx
        unmute_and_set_level "DAC,0" "80%" # E.g., envy24
        unmute_and_set_level "DAC,1" "80%" # E.g., envy24
        unmute_and_set_level "Synth" "80%"
        unmute_and_set_level "CD" "80%"

        mute_and_zero_level "Mic"
        mute_and_zero_level "IEC958" # Ubuntu #19648

        # Intel P4P800-MX  (Ubuntu bug #5813)
        switch_control "Master Playback Switch" on
        switch_control "Master Surround" on

        # Trident/YMFPCI/emu10k1:
        unmute_and_set_level "Wave" "80%"
        unmute_and_set_level "Music" "80%"
        unmute_and_set_level "AC97" "80%"

        # DRC:
        unmute_and_set_level "Dynamic Range Compression" "80%"

        # Required for HDA Intel (hda-intel):
        unmute_and_set_level "Front" "80%"

        # Required for SB Live 7.1/24-bit (ca0106):
        unmute_and_set_level "Analog Front" "80%"

        # Required at least for Via 823x hardware on DFI K8M800-MLVF Motherboard with kernels 2.6.10-3/4 (see ubuntu #7286):
        switch_control "IEC958 Capture Monitor" off

        # Required for hardware allowing toggles for AC97 through IEC958,
        #  valid values are 0, 1, 2, 3. Needs to be set to 0 for PCM1.
        unmute_and_set_level "IEC958 Playback AC97-SPSA" "0"

        # Required for newer Via hardware (see Ubuntu #31784)
        unmute_and_set_level "VIA DXS,0" "80%"
        unmute_and_set_level "VIA DXS,1" "80%"
        unmute_and_set_level "VIA DXS,2" "80%"
        unmute_and_set_level "VIA DXS,3" "80%"

        # Required on some notebooks with ICH4:
        switch_control "Headphone Jack Sense" off
        switch_control "Line Jack Sense" off

        # Some machines need one or more of these to be on;
        # others need one or more of these to be off:
        #
        # switch_control "External Amplifier" on
        switch_control "Audigy Analog/Digital Output Jack" off
        switch_control "SB Live Analog/Digital Output Jack" off

        # D1984 -- Thinkpad T61/X61
        # also needed for Dell Mini 9 and Dell E series
        unmute_and_set_level "Speaker" "80%"
        unmute_and_set_level "Headphone" "80%"

        # HDA-Intel w/ "Digital" capture mixer (See Ubuntu #193823)
        unmute_and_set_level "Digital" "80%"

        # HDA-Intel w/ Digital Mic should default to Digital mic rather than
        # analog (See Ubuntu #314188)
        switch_control "Digital Input Source" "Digital Mic 1"
        switch_control "Digital Input Source" "Digital Mic"

        # Turn off PC Beep on hda cards that support it, see Ubuntu #331589.
        mute_and_zero_level "PC Beep"

        return 0
    }

# $1 <card ID> | "all"
sanify_levels()
    {
        TTSDML_RETURNSTATUS=0
        case "$1" in
            all)
                for CARD in $( echo_card_indices ); do
                    sanify_levels_on_card "$CARD" || TTSDML_RETURNSTATUS=1
                done

            ;;
            *)
                sanify_levels_on_card "$1" || TTSDML_RETURNSTATUS=1
            ;;
        esac
        mute_and_zero_levels_on_card "pcsp"
        return $TTSDML_RETURNSTATUS
    }

# $1 <card ID>
preinit_levels_on_card()
    {
        CARDOPT="-c $1"

        # Silly dance to activate internal speakers by default on PowerMac
        # Snapper and Tumbler
        idold=`cat /proc/asound/card$1/id 2 > /dev/null`
        if [ "$idold" = "Snapper" -o "$idold" = "Tumbler" ]; then
            switch_control "Auto Mute" off
            switch_control "PC Speaker" off
            switch_control "Auto Mute" on
        fi
    }

# $1 <card ID> | "all"
preinit_levels()
    {
        TTSDML_RETURNSTATUS=0
        case "$1" in
            all)
                for CARD in $( echo_card_indices ); do
                    preinit_levels_on_card "$CARD" || TTSDML_RETURNSTATUS=1
                done
            ;;
            *)
                preinit_levels_on_card "$1" || TTSDML_RETURNSTATUS=1
            ;;
        esac
        return $TTSDML_RETURNSTATUS
    }

# $1 <card ID>
mute_and_zero_levels_on_card()
    {
        CARDOPT="-c $1"
        for CTL in  \
 Master  \
 PCM  \
 Synth  \
 CD  \
 Line  \
 Mic  \
 "PCM,1"  \
 Wave  \
 Music  \
 AC97  \
 "Master Digital"  \
 DAC  \
 "DAC,0"  \
 "DAC,1"  \
 Headphone  \
 Speaker  \
 Playback
        do
            mute_and_zero_level "$CTL"
        done
        #	for CTL in \
        #		"Audigy Analog/Digital Output Jack" \
        #		"SB Live Analog/Digital Output Jack"
        #	do
        #		switch_control "$CTL" off
        #	done
        return 0
    }

# $1 <card ID> | "all"
mute_and_zero_levels()
    {
        TTZML_RETURNSTATUS=0
        case "$1" in
            all)
                for CARD in $( echo_card_indices ); do
                    mute_and_zero_levels_on_card "$CARD" || TTZML_RETURNSTATUS=1
                done
            ;;
            *)
                mute_and_zero_levels_on_card "$1" || TTZML_RETURNSTATUS=1
            ;;
        esac
        return $TTZML_RETURNSTATUS
    }


{
    [ "$1" ] || bugout
    if [ "$1" = all ]; then
        [ -d /proc/asound ]
        return $?
    else
        [ -d "/proc/asound/card$1" ] || [ -d "/proc/asound/$1" ]
        return $?
    fi
}

# If a card identifier is provided in $2 then regard it as an error
# if that card is not present; otherwise don't regard it as an error.

case "$1" in
    start)
        EXITSTATUS=0
        TARGET_CARD="$2"
        case "$TARGET_CARD" in
            "" | all) TARGET_CARD=all;
            log_action_begin_msg "Setting up ALSA" ;;
        esac
        if ! c_OK "$TARGET_CARD"; then
            [ "$TARGET_CARD" = "all" ] && log_action_end_msg "$([!"$2"];echo$?;)" "none loaded"
            exit $?
        fi
        preinit_levels "$TARGET_CARD" || EXITSTATUS=1
        if ! restore_levels "$TARGET_CARD"; then
            sanify_levels "$TARGET_CARD" || EXITSTATUS=1
            restore_levels "$TARGET_CARD" >/dev/null 2>&1 || :
        fi
        [ "$TARGET_CARD" = "all" ] && log_action_end_msg_and_exit "$EXITSTATUS"
        exit $EXITSTATUS
    ;;
    stop)
        EXITSTATUS=0
        TARGET_CARD="$2"
        case "$TARGET_CARD" in
            "" | all) TARGET_CARD=all;
            log_action_begin_msg "Shutting down ALSA" ;;
            *) log_action_begin_msg "Shutting down ALSA card ${TARGET_CARD}" ;;
        esac
        c_OK "$TARGET_CARD" || log_action_end_msg_and_exit "$([!"$2"];echo$?;)" "none loaded"
        store_levels "$TARGET_CARD" || EXITSTATUS=1
        mute_and_zero_levels "$TARGET_CARD" || EXITSTATUS=1
        log_action_end_msg_and_exit "$EXITSTATUS"
    ;;
    restart | force-reload)
        EXITSTATUS=0
        $0 stop || EXITSTATUS=1
        $0 start || EXITSTATUS=1
        exit $EXITSTATUS
    ;;
    reset)
        TARGET_CARD="$2"
        case "$TARGET_CARD" in
            "" | all) TARGET_CARD=all;
            log_action_begin_msg "Resetting ALSA" ;;
            *) log_action_begin_msg "Resetting ALSA card ${TARGET_CARD}" ;;
        esac
        c_OK "$TARGET_CARD" || log_action_end_msg_and_exit "$([!"$2"];echo$?;)" "none loaded"
        preinit_levels "$TARGET_CARD"
        sanify_levels "$TARGET_CARD"
        log_action_end_msg_and_exit "$?"
    ;;
    *)
        echo "Usage: $MYNAME{start [CARD]|stop [CARD]|restart [CARD]|reset [CARD]}" >&2
        exit 3
    ;;
esac

