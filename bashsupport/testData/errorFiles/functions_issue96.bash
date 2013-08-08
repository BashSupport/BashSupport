#!/bin/sh
#!/bin/bash
#
# Copyright © 2008-2013 RAAF Technology bv
#
# This file is part of Session.
#
# Session is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Session is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Session. If not, see <http://www.gnu.org/licenses/>.


# Function declarations have the following form:
#
# # foo(<parm1> [<parm2>])                      # Required and optional parameters
# # Prints.                                     # (Side-)effects, e.g., "Sets variable flurb"
# #
# # Does a trick to x because of z.             # Description of foo's behavior
# #
# function foo {
#     reportDebugFuncEntry "$*" "<var1> <var2>" # Names of global variables read, separated by spaces
# }
#


# report(<message_text>...)
# Prints.
#
# Print <message_text>s with standard framing text.
#
function report { printf "$*\n" ; }
function reportSession { report "Session: $*" ; }
function reportInfo { reportSession "Info: $*" ; }
function reportWarning { reportSession "Warning: $*" >&2 ; }
function reportError { reportSession "Error: ${FUNCNAME[1]}(): $*" >&2 ; }
function reportDebug {
    [ ! "$debug" -o  "$debug" = 0 ] && return
    typeset msg
    msg="Debug: ${FUNCNAME[1]}(): $*"
    reportSession "$msg" >&2
    [ ! "$logfile" ] || printf "%s\n" "$msg" >> "$logfile"
}

# reportDebugFuncEntry(<argument_name_vector> <environment_variable_name_vector> <additional_message>)
# Prints, Writes to log file.
#
# Report a debugging message containing:
#     the argument string supplied to that function
#     selected environment variable names separated by spaces
#     and an additional message.
# E.g.,
#     function foo { bar=BARVAL baz=BAZVAL reportDebugFuncEntry "$*" "bar baz" "have a nice day" ; }
#     foo FOO1 FOO2
#     => Session: Debug: Entering foo(FOO1 FOO2) with environment bar=BARVAL baz=BAZVAL ; have a nice day.
#
function reportDebugFuncEntry {
    [ ! "$debug" -o  "$debug" = 0 ] && return

    typeset funcname="${FUNCNAME[1]}"
    typeset args="$1"
    typeset varname
    typeset varval
    typeset envstr=""
    typeset msg

    typeset IFS=" "
    for varname in $2 ; do
       varval="$(eval printf '%s\\n' "\$$varname")"
       envstr="${envstr:+$envstr, }${varname}=${varval}"
    done

    msg="Entering $funcname($args)${envstr:+ with environment $envstr${3:+; }}$3"
    reportSession "Debug: $msg" >&2
    [ ! "$logfile" ] || printf "%s\n" "$msg" >> "$logfile"

    return
}

# isLoopback(<domain_name_or_address>)
# Returns truth value.
#
# Return 0 if address is loopback domain name or address; non-zero otherwise.
#
function isLoopback {
    reportDebugFuncEntry "$*"

    # Handles only IPv4 at present
    [[ "$1" =~ ^localhost\. ]] || [[ "$1" =~ ^127\. ]]
}

# isLocal(<name>)
# Returns truth value.
#
# Return 0 if name matches non-null short local hostname, 1 otherwise.
#
function isLocal {
    reportDebugFuncEntry "$*"

    [ "$hostname" ] || return 1
    [[ "$1" =~ "$hostname" ]]
}

# toLocalWindowsPath(<path>)
# Prints.
#
# Convert path to Windows-style but with forward slashes.
# Inspired by Cygwin's cygpath.
#
# Should only be used to generate path (string) arguments for local Windows commands.
#
function toLocalWindowsPath {
    reportDebugFuncEntry "$*"

    typeset input="$1"
    typeset output
    typeset path
    typeset file

    if [ ! "$input" ]; then
        reportError "No input given"
        return 1
    fi

    if [[ "$input" =~ : ]]; then
        # Windows path was passed (ie. contains colon). Only forward slashes.
        reportDebug "Detected colon, only forwarding slashes"
        output="$(printf "$input\n" | sed 's|\\|/|g')"
    elif [ -d "$input" ]; then
        # Path exists locally and is a directory, enter, read out and forward slashes.
        reportDebug "Path exists locally and is a directory. Entering, read out and forwarding slashes"
        output="$(cd "$input" ; cmd.exe /c cd | sed 's|\\|/|g')"
    elif [ -f "$input" -o "$(dirname "$input")" != "/" ]; then
        # Path exists locally and is a file, enter parent, read out and forward slashes.
        reportDebug "Path exists locally and is a file. Entering, read out and forwarding slashes"
        path="$(cd "$(dirname "$input")" ; cmd.exe /c cd | sed 's|\\|/|g')"
        file="$(basename "$input")"
        output="$path/$file"
    else
        # File does not exist locally, and contains no colon. Prepend C: and forward slashes.
        reportDebug "File does not exist locally and contains no colon. Prepending C: and forwarding slashes"
        output="$(printf "C:$input\n" | sed 's|\\|/|g' )"
    fi

    reportDebug "Incoming: $input"
    reportDebug "Outgoing: $output"
    report "$output"
    return 0
}


# toRemoteWindowsPath(<path>)
# Prints.
#
# Convert path to Windows-style but with forward slashes.
#
function toRemoteWindowsPath {
    reportDebugFuncEntry "$*"

    typeset input="$1"

    if [ ! "$input" ]; then
        reportError "No input given"
        return 1
    fi

    if [[ "$input" =~ : ]]; then
        output="$(printf "$input\n" | sed 's|\\|/|g')"
    else
        output="$(printf "C:$input\n" | sed 's|\\|/|g' )"
    fi

    reportDebug "Incoming: $input"
    reportDebug "Outgoing: $output"
    report "$output"
    return 0
}

# viaScript(<command>)
# Writes a shell or batch script, executes and cleans up.
#
# Handle execution of <command> via a platform's native scripting method.
#
function viaScript {
    reportDebugFuncEntry "$*"

    typeset command="$*"
    typeset retval
    typeset nametmp="${name}.$$"

    [ "$nametmp" ] || nametmp=local

    if [ "$platform" = "linux" -o "$platform" = "macosx" ]; then
        reportDebug "Writing UNIX shell script"
        printf "#!/bin/sh\n" > "$usrcfd/tmp/session.tell.$nametmp.sh"
        printf '%s\n '"$command" | sed 's/^[[:space:]]*//' 2> /dev/null >> "$usrcfd/tmp/session.tell.$nametmp.sh"

        reportDebug "Executing $usrcfd/tmp/session.tell.$nametmp.sh"
        sh "$usrcfd/tmp/session.tell.$nametmp.sh"
        retval="$?"

        if [ "$debug" ]; then
            reportDebug "Not removing $usrcfd/tmp/session.tell.$nametmp.sh"
        else
            rm "$usrcfd/tmp/session.tell.$nametmp.sh"
        fi
    elif [ "$platform" = "windows" ]; then
        reportDebug "Writing DOS batch script"
        printf "@echo off\n" > "$usrcfd/tmp/session.tell.$nametmp.bat.unix"
        printf '%s\n' "$command" | sed 's/^[[:space:]]*//' 2> /dev/null >> "$usrcfd/tmp/session.tell.$nametmp.bat.unix"

        reportDebug "Correcting for DOS style line endings"
        sed 's/$/\r/' "$usrcfd/tmp/session.tell.$nametmp.bat.unix" > "$usrcfd/tmp/session.tell.$nametmp.bat"
        rm "$usrcfd/tmp/session.tell.$nametmp.bat.unix"

        reportDebug "Executing $usrcfd/tmp/session.tell.$nametmp.bat"
        cd "$usrcfd/tmp" ; cmd.exe /c session.tell.$nametmp.bat ; cd - > /dev/null
        retval="$?"

        if [ "$debug" ]; then
            reportDebug "Not removing $usrcfd/tmp/session.tell.$nametmp.bat"
        else
            rm "$usrcfd/tmp/session.tell.$nametmp.bat"
        fi
    else
        reportError "Unknown platform specified: $platform"
        return 1
    fi

    return $retval
}

# localTellCommandWriter(<command>)
# Prints.
#
# Print <command> for immediate local execution.
#
function localTellCommandWriter {
    reportDebugFuncEntry "$*"

    typeset command="$*"

    printf '%s\n' "$command"
}

# psexecTellCommandWriter(<addr> <user> <pass> <command>)
# Prints.
#
# Print <command> for execution over smb using psexec.
#
function psexecTellCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset user="$2"
    typeset pass="$3"
    typeset command="$4"

    # Older version of psexec?
    if [ "$user" ]; then user="-u \"$user\"" ; fi
    if [ "$pass" ]; then pass="-p \"$pass\"" ; fi
    printf "psexec \\\\$addr -h $user $pass cmd.exe /c \"$command\" 2>nul\n"
}

# winexeTellCommandWriter(<addr> <user> <pass> <command>)
# Prints.
#
# Print <command> for execution over smb using winexe.
#
function winexeTellCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset user="$2"
    typeset pass="$3"
    typeset command="$4"

    if [ "$pass" ]; then pass="%$pass" ; fi
    printf "winexe --debug-stderr -U '${user}${pass}' //$addr 'cmd.exe /c \"$command\"' 2>/dev/null\n"
}

# plinkTellCommandWriter(<addr> <user> <command>)
# Prints.
#
# Print <command> for execution over ssh using plink.
#
function plinkTellCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset user="$2"
    typeset command="$3"

    printf "plink -x $sshopts -l \"$user\" $addr \"$command\"\n"
}

# sshTellCommandWriter(<host> <user> <command>)
# Prints.
#
# Print <command> for execution using ssh on <host> as <user>.
#
function sshTellCommandWriter {
    reportDebugFuncEntry "$*"

    typeset host="$1"
    typeset user="$2"
    typeset command="$3"

    printf "ssh $sshopts -l '$user' '$host' '$command'\n"
}

# localSendCommandWriter(<source> <target>)
# Prints.
#
# Print a command for sending data using the appropriate local copy command.
#
function localSendCommandWriter {
    reportDebugFuncEntry "$*"

    typeset source
    typeset target

    if [ "$platform" = "linux" -o "$platform" = "macosx" ]; then
        source="$1"
        target="$2"
        printf "cp -Rpd \"$source\" \"$target\" >/dev/null 2>&1 </dev/null\n"
    elif [ "$platform" = "windows" ]; then
        source="$(toLocalWindowsPath "$1")"
        target="$(toLocalWindowsPath "$2")"
        printf "robocopy /e \"$source\" \"$target\" >nul 2>&1\n"
    else
        reportError "Unknown platform specified: $platform"
        return 1
    fi

    return 0
}

# robocopySendCommandWriter(<addr> <user> <pass> <source> <target>)
# Prints.
#
# Print a command for sending data using robocopy remotely.
#
function robocopySendCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset user="$2"
    typeset pass="$3"
    typeset source="$(toLocalWindowsPath "$4")"
    typeset target="$(toLocalWindowsPath "$5")"
    typeset driveLetter

    driveLetter="$(printf "$target\n" | cut -d ':' -f 1)"
    reportDebug "Extracted drive letter $driveLetter from $target"

    target="$(printf "$target\n" | sed "s|$driveLetter:|//${addr}/${driveLetter}\$|" | sed 's|/|\\|g')"
    reportDebug  "Target is now $target"

    if [ "$pass" ]; then pass="\"$pass\"" ; fi
    printf %s "
        net use \\\\$addr\\$driveLetter\$ /user:\"${user}\" ${pass} 2>nul
        robocopy /e \"$source\" \"$target\" >nul 2>&1
        net use \\\\$addr\\$driveLetter\$ /delete >nul 2>&1
    " | sed 's/^[[:space:]]*//'
}

# smbclientSendCommandWriter(<addr> <user> <pass> <source> <target>)
# Prints.
#
# Print a command for sending data using smbclient remotely.
#
function smbclientSendCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset user="$2"
    typeset pass="$3"
    typeset source="$4"
    typeset target="$(toRemoteWindowsPath "$5")"
    typeset share
    typeset smbcommand

    share="$(printf "$target\n" | sed "s|:|$|" | cut -d/ -f 1 | sed 's|"||g')"
    target="$(printf "$target\n" | sed 's|/|\\\\|g' | cut -d: -f2- | sed 's|"||g')"
    smbcommand="mkdir \"$target\";cd \"$target\";lcd \"$source\";prompt off;recurse on;mput *;quit"

    if [ "$pass" ]; then pass="%$pass" ; fi
    printf "smbclient //$addr/$share -U \"${user}${pass}\" -c '$smbcommand' 2>/dev/null\n"
}

# pscpSendCommandWriter(<addr> <uopts> <source> <target>)
# Prints.
#
# Print a command for sending data using pscp remotely.
#
function pscpSendCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset uopts="$2"
    typeset source="$(toLocalWindowsPath "$3")"
    typeset target="$4"

    printf "pscp $sshopts -scp -p -q -r -l \"$uopts\" \"$source\" $addr:\"$target\"\n"
}

# scpSendCommandWriter(<addr> <uopts> <source> <target>)
# Prints.
#
# Print a command for sending data using scp remotely.
#
function scpSendCommandWriter {
    reportDebugFuncEntry "$*"

    typeset addr="$1"
    typeset uopts="$2"
    typeset source="$3"
    typeset target="$4"
    typeset sshuser

    sshuser="$(printf "$uopts\n" | sed 's|\ |\\ |g'| sed 's|\\|\\\\|g')"
    printf "scp -q $sshopts -r \"$source\" $sshuser@$addr:\"$target\"\n"
}

# toolFinder()
# Writes $usrcfd/cfg/tools.required and $usrcfd/cfg/tools.found.
#
# Detect required and optional tools.
#
function toolFinder {
    reportDebugFuncEntry "$*" "usrcfd color"

    typeset tools_terminal
    typeset tools_desktop
    typeset tools_browser
    typeset tools_access_result
    typeset tooltypes
    typeset tooltype
    typeset values
    typeset tool

    # Make sure color is off if requested.
    if [ "$color" = 0 ]; then unset color; fi

    # First write out tools.required.
    if [ "$platform" = "linux" -o "$platform" = "macosx" ]; then
        printf "tools_session='awk,cut,grep,host,lsof,nmap,sed,tr,ps'\n" > "$usrcfd/cfg/tools.required"
        printf "tools_execute='ssh,winexe'\n" >> "$usrcfd/cfg/tools.required"
        printf "tools_agent='ssh-agent,ssh-add'\n" >> "$usrcfd/cfg/tools.required"
        printf "tools_send='cp,scp,smbclient'\n" >> "$usrcfd/cfg/tools.required"
    elif [ "$platform" = "windows" ]; then
        printf "tools_session='awk,cut,grep,nmap,nslookup,sed,tr,tasklist'\n" > "$usrcfd/cfg/tools.required"
        printf "tools_execute='plink,psexec'\n" >> "$usrcfd/cfg/tools.required"
        printf "tools_agent='pageant'\n" >> "$usrcfd/cfg/tools.required"
        printf "tools_send='pscp,robocopy'\n" >> "$usrcfd/cfg/tools.required"
    else
        printf "# unidentified platform specified: $platform\n" > "$usrcfd/cfg/tools.required"
        printf "# toolFinder says you need to define tools_ variables!\n" >> "$usrcfd/cfg/tools.required"
    fi
    if [ "$terminal" = "putty" ]; then
        tools_terminal="putty"
    elif [ "$terminal" = "apple" ]; then
        PATH="${PATH}:/Applications/Utilities/Terminal.app/Contents/MacOS"
        tools_terminal="osascript,Terminal"
    elif [ "$terminal" = "gnome" ]; then
        tools_terminal="gnome-terminal,pgrep,xdotool"
    elif [ "$terminal" = "screen" ]; then
        tools_terminal="screen"
    fi

    if [ "$desktop" = "mstsc" ]; then
        tools_desktop="cryptrdp5,mstsc"
    elif [ "$desktop" = "amsrdc" ]; then
        PATH="${PATH}:/Applications/Remote Desktop Connection.app/Contents/MacOS"
        tools_desktop="Remote Desktop Connection"
    elif [ "$desktop" = "rdesktop" ]; then
        tools_desktop="rdesktop"
    fi

    if [ "$browser" = "windows" ]; then
        tools_browser="explorer"
    elif [ "$browser" = "apple" ]; then
        tools_browser="open"
    elif [ "$browser" = "gnome" ]; then
        tools_browser="open"
    fi

    for tools_access_type in tools_terminal tools_desktop tools_browser; do
        typeset current="$(eval printf '%s\\n' "\$$tools_access_type")"
        if [ "$current" ]; then
            tools_access_result="$current,$tools_access_result"
        fi
    done
    printf "tools_access='$tools_access_result'\n" | sed "s|,'$|'|" >> "$usrcfd/cfg/tools.required"

    # Clean up old tools.found first.
    rm -f "$usrcfd/cfg/tools.found"

    # Read tooltypes from generated tools.required.
    tooltypes="$(cat "$usrcfd/cfg/tools.required" | cut -d "=" -f 1)"

    # Loop over all tooltypes and for each tooltype over its values.
    ORIG_IFS="$IFS"
    typeset IFS="$ORIG_IFS"
    for tooltype in $tooltypes ; do
        values="$(cat "$usrcfd/cfg/tools.required" | grep $tooltype | cut -d '=' -f 2 | sed "s|'||g")"

        printf "$(printf "$tooltype\n" | cut -d "_" -f 2): "
        printf "${tooltype}_found='" >> "$usrcfd/cfg/tools.found"

        IFS=","
        for tool in $values ; do
            if [ ! "$color" ]; then
                unset color_red color_green color_yellow color_blue color_end
            fi

            if [ "$(basename "$(which $tool 2>/dev/null)")" ]; then
                printf "${color_green}${tool}${color_end} "
                printf "$tool," >> "$usrcfd/cfg/tools.found"
            else
                printf "(${color_red}${tool}${color_end}) "
            fi
        done
        printf "\n"
        printf "'\n" >> "$usrcfd/cfg/tools.found"
    done

    printf "\n"

    # Remove trailing commas from tools.found.
    sed -e "s|,'$|'|" "$usrcfd/cfg/tools.found" > "$usrcfd/tmp/tools.found" 2>/dev/null
    mv "$usrcfd/tmp/tools.found" "$usrcfd/cfg/tools.found"
}

# handleSshPrivateKeys()
# Detects private keys, handles automatic agent starting when needed.
#
# Check for private keys and be smart about agent handling.
#
function handleSshPrivateKeys {
    reportDebugFuncEntry "$*"

    # Set private key option when private key found.
    if [ "$platform" = "linux" -o "$platform" = "macosx" ]; then
        # Look for OpenSSH style public/private keypair.
        if [ -e "$HOME/.ssh/id_dsa" ]; then
            sshkey="$HOME/.ssh/id_dsa"
            sshpub="$HOME/.ssh/id_dsa.pub"
            sshopts="-i $sshkey"
        elif [ -e "$HOME/.ssh/id_rsa" ]; then
            sshkey="$HOME/.ssh/id_rsa"
            sshpub="$HOME/.ssh/id_rsa.pub"
            sshopts="-i $sshkey"
        fi

        # Load private key into ssh-agent if agent handling is enabled.
        if [ "$sshkey" -a "$agent" ]; then
            sshagentfile="$usrcfd/tmp/session.ssh-agent.out"
            sshagentproc="$(ps x | grep ssh-agent | grep -v grep)"
            if [ "$SSH_AUTH_SOCK" ]; then
                reportDebug "Reusing previously set environment variables for running ssh-agent"
                printf "SSH_AUTH_SOCK=$SSH_AUTH_SOCK; export SSH_AUTH_SOCK;\n"  > "$sshagentfile"
                printf "SSH_AGENT_PID=$SSH_AGENT_PID; export SSH_AGENT_PID;\n" >> "$sshagentfile"
                source "$sshagentfile"
                unset sshopts
            elif [ "$sshagentproc" ]; then
                reportDebug "Environment values not set but agent is running, inspecting agent"
                reportDebug "I'm using $privy lsof to do this"
                sshagentlsof="$($privy lsof /tmp/ssh-*/agent.* 2> /dev/null | grep ssh-agent | tail -n1)"
                SSH_AUTH_SOCK="$(printf "$sshagentlsof\n" | awk '{print $9}')"
                SSH_AGENT_PID="$(printf "$sshagentlsof\n" | awk '{print $2}')"
                printf "SSH_AUTH_SOCK=$SSH_AUTH_SOCK; export SSH_AUTH_SOCK;\n"  > "$sshagentfile"
                printf "SSH_AGENT_PID=$SSH_AGENT_PID; export SSH_AGENT_PID;\n" >> "$sshagentfile"
                source "$sshagentfile"
                unset sshopts
            elif [ ! "$sshagentproc" ]; then
                reportInfo "You have a private key; starting new ssh-agent"
                ssh-agent | grep -v "^echo " > "$sshagentfile"
                chmod 600 "$sshagentfile"
                source "$sshagentfile"
                unset sshopts
                ssh-add
            else
                reportError "Unexpected exit"
                exit 1
            fi
        fi

        # Disable strict host and reverse mapping checks if not already set.
        if [ -e "$HOME/.ssh/config" ]; then
            if [[ ! "$(cat "$HOME/.ssh/config")" =~ "StrictHostKeyChecking" ]]; then
                printf "StrictHostKeyChecking no\n" >> "$HOME/.ssh/config"
            fi
            if [[ ! "$(cat "$HOME/.ssh/config")" =~ "GSSAPIAuthentication" ]]; then
                printf "GSSAPIAuthentication no\n" >> "$HOME/.ssh/config"
            fi
        else
            mkdir -p "$HOME/.ssh"
            printf "GSSAPIAuthentication no\n"   > "$HOME/.ssh/config"
            printf "StrictHostKeyChecking no\n" >> "$HOME/.ssh/config"
        fi

    elif [ "$platform" = "windows" ]; then
        # Look for PuTTY style public/private keypair.
        if [ -e "$HOME/.ssh/id_dsa.ppk" ]; then
            sshkey="$(toLocalWindowsPath "$HOME/.ssh/id_dsa.ppk")"
            sshpub="$(toLocalWindowsPath "$HOME/.ssh/id_dsa.pub")"
            sshopts="-i \"$sshkey\""
        elif [ -e "$HOME/.ssh/id_rsa.ppk" ]; then
            sshkey="$(toLocalWindowsPath "$HOME/.ssh/id_rsa.ppk")"
            sshpub="$(toLocalWindowsPath "$HOME/.ssh/id_rsa.pub")"
            sshopts="-i \"$sshkey\""
        fi

        # Load private key into pageant if agent handling is enabled.
        if [ "$sshkey" -a "$agent" ]; then
            running="$($pslist | grep -i pageant | grep -v grep)"
            if [ ! "$running" ]; then
                reportInfo "You have a private key; loading into ssh-agent"
                typeset command="start /b pageant \"$sshkey\""
                viaScript "$(localTellCommandWriter "$command")" &
                sleep 10
            fi
        fi
    fi
}

# handleQuotedRegExpBehaviour()
# Sets shell options.
#
# Check quoted regexp behaviour and take appropriate action.
#
function handleQuotedRegExpBehaviour {
    reportDebugFuncEntry "$*"

    # http://dougbarton.us/Bash/Bash-FAQ.html, see E15
    # http://ftp.gnu.org/gnu/bash/bash-3.2-patches/bash32-010, regarding quoted regexp matching
    # http://ftp.gnu.org/gnu/bash/bash-3.2-patches/bash32-039, regarding compat31 shopt

    typeset major="${BASH_VERSINFO[0]}"
    typeset minor="${BASH_VERSINFO[1]}"
    typeset patch="${BASH_VERSINFO[2]}"

    if [ "$major" -lt 3 ]; then # Major is lower than 3
        reportError "Bash major version lower than 3, exiting"
        exit 1
    elif [ "$major" = 3 ]; then # Major is 3
        if [ "$minor" -lt 2 ]; then # Version 3.0 and 3.1 will work
            reportDebug "Bash version is old but will work with quoted regexp matching"
        elif [ "$minor" -eq 2 ]; then  # Handle 3.2 idiosyncracies
            if [ 0 -le "$patch" -a "$patch" -le 9 ]; then # Version 3.2.0 to 3.2.9 is broken
                reportError "Bash version has known unfixed issues with quoted regexp matching"
                exit 1
            elif [ "$patch" -lt 39 ]; then # Version 3.2.10 to 3.2.38 might work
                reportDebug "Bash version is old but may work with quoted regexp matching"
            else # Version 3.2.39 and higher has compat31
                reportDebug "Setting shell option compat31 to handle quoted rexexp matching"
                shopt -s compat31
            fi
        elif [ "$minor" -gt 2 ]; then # Currently non-existing version of bash 3.3 or higher but not 4
            reportDebug "Setting shell option compat31 to handle quoted rexexp matching"
            shopt -s compat31
        fi
    else # 4 and higher.
        reportDebug "Setting shell option compat31 to handle quoted rexexp matching"
        shopt -s compat31
    fi
}

# sshSendKey(<username>)
# Sends a local public key to another machine.
#
# This function takes a username which is supposed to exist on a remote
# system and first sends the key to the remote system and then executes
# the commandlist routine below to install the key for the specified user.
#
function sshSendKey {
    reportDebugFuncEntry "$*" "sshpub addr"

    typeset source="$sshpub"
    typeset target="/tmp/pubkey"
    typeset user="$1"
    typeset retval
    typeset commandlist

    [ "$addr" ] || { reportError "\$addr is empty; aborting" ; return 1 ; }
    [ "$sshpub" ] || { reportError "\$sshpub is empty; aborting" ; return 1 ; }

    reportInfo "Attempting to send public key $sshpub"

    viaScript "$(${sshsend}SendCommandWriter "$addr" "$user" "$source" "$target")"
    retval="$?"
    if [ "$retval" != 0 ]; then
        reportError "Failed to send $sshpub with return code: $retval"
        return 1
    fi

    reportInfo "Key sent successfully; will now attempt to install it"

    # Keep commandlist as one line, plink will go crazy if you do not.
    commandlist='[ "$HOME" ] || exit 1 ; mkdir -p $HOME/.ssh ; touch $HOME/.ssh/authorized_keys ; cat $HOME/.ssh/authorized_keys /tmp/pubkey | sort | uniq > /tmp/authorized_keys ; mv /tmp/authorized_keys $HOME/.ssh/authorized_keys ; rm /tmp/pubkey ; chmod 755 $HOME ; chmod 755 $HOME/.ssh ; chmod 600 $HOME/.ssh/authorized_keys'
    viaScript "$(${sshtell}TellCommandWriter "$addr" "$user" "$commandlist")"
    retval="$?"
    if [ "$retval" != 0 ]; then
        reportError "Installation attempt failed with return code: $retval"
        return 1
    fi

    reportInfo "Key installed"
    return 0
}

# winStoreCreds(<type> <name>)
# Writes pwd files.
#
# Store credentials for services that talk smb or rdp.
#
function winStoreCreds {
    reportDebugFuncEntry "$*"

    typeset store_type="$1"
    typeset store_name="$2"
    typeset store_pass1="unset1"
    typeset store_pass2="unset2"

    while [ "$store_pass1" != "$store_pass2" ]; do
        printf "Please supply password for $store_type $store_name: "
        stty -echo
        read store_pass1
        stty echo
        printf "\n"

        printf "Retype password for confirmation: "
        stty -echo
        read store_pass2
        stty echo
        printf "\n"

        if [ "$store_pass1" != "$store_pass2" ]; then
            reportError "Passwords do not match"
            return 1
        fi
    done

    mkdir -p "$usrcfd/sys/$name"
    if [ "$user" = "$admin" ]; then
        printf "$store_pass2\n" > "$usrcfd/sys/$name/user.pwd"
        printf "$store_pass2\n" > "$usrcfd/sys/$name/admin.pwd"
    else
        printf "$store_pass2\n" > "$usrcfd/sys/$name/$store_type.pwd"
    fi

    return 0
}

# osglobals(<osmt>)
# Sets osstop, osreboot and oslisten.
#
# Set generic global variables for commands that interact
# with the given operating system.
#
function osGlobals {
    reportDebugFuncEntry "$*"

    # Defaults
    osstop="/sbin/shutdown -h now"
    osreboot="reboot"
    oslisten="netstat -na|grep \"LISTEN\"|grep -w -e \"0\.0\.0\.0:$port\" -e \":::$port\" -e \"$addr:$port\" "

    # Override defaults given above for certain osses.
    case "$1" in
      aix5|aix6|aix7|hpux1123|hpux1131)
        osstop="/sbin/shutdown -hy 0"
        ;;
      dfbsd2|fbsd7|fbsd8)
        osstop="/sbin/shutdown -p now"
        ;;
      nbsd4|nbsd5|obsd4|obsd5)
        osstop="/sbin/shutdown -h -p now"
        ;;
      sol10|sol11)
        osstop="/usr/sbin/poweroff"
        ;;
      windows-like|win2k3|win2k8|wins8|winxp|win7|win8)
        osstop="shutdown -s -t 01"
        osreboot="shutdown -r -t 01"
        oslisten="netstat -na|findstr \"LISTEN\"|findstr \"\<0\.0\.0\.0:$port\> \<$addr:$port\>\" "
        ;;
    esac
}

# capsFirst(<arg>...)
# Prints.
#
# Echo all args with first one capitalized if it starts with a letter.
#
function capsFirst {
    reportDebugFuncEntry "$*"

    typeset input="$*"
    typeset upr

    case "$input" in
      a*) upr=A ;;  b*) upr=B  ;; c*) upr=C ;; d*) upr=D ;;
      e*) upr=E ;;  f*) upr=F  ;; g*) upr=G ;; h*) upr=H ;;
      i*) upr=I ;;  j*) upr=J  ;; k*) upr=K ;; l*) upr=L ;;
      m*) upr=M ;;  n*) upr=N  ;; o*) upr=O ;; p*) upr=P ;;
      q*) upr=Q ;;  r*) upr=R  ;; s*) upr=S ;; t*) upr=T ;;
      u*) upr=U ;;  v*) upr=V  ;; w*) upr=W ;; x*) upr=X ;;
      y*) upr=Y ;;  z*) upr=Z  ;;
       *) printf "$input\n" ; return ; ;;
    esac
    printf "${upr}${input#?}\n"
}

# rndGen(<count> <split_char>)
# Prints.
#
# Print <count> random 2-digit hex numerals separated by <split_char>.
#
function rndGen {
    reportDebugFuncEntry "$*"

    typeset count=1
    typeset split
    typeset part
    typeset length

    if [ "$1" ]; then
        count="$1"
    fi
    if [ "$2" ]; then
        split="$2"
    fi

    while [ "$count" -gt 0 ]; do
        part="$(printf '%02X' $RANDOM | cut -c2-3)"
        length=${#part}

        if [ "$length" -lt 2 ]; then
            part="$part$part"
        fi

        printf "$part"

        if [ "$count" != 1 ]; then
            printf "$split"
        fi

        let count-=1
    done

    printf "\n"
}

# macGen(<type>)
# Sets macaddr. Writes $usrcfd/sys/$name/generated.mac.
#
# Generate a MAC address for adapter of the give type.
#
function macGen {
    reportDebugFuncEntry "$*"

    typeset vendor_vmw="00:50:56"
    typeset vendor_xen="00:16:3E"
    typeset vendor_kvm="54:52:00"

    typeset first
    typeset newfirst
    typeset genmac
    typeset venmac
    typeset indec
    typeset check

    case "$1" in
      global)
        genmac="$(rndGen 6 :)"
        first="$(printf "$genmac\n" | cut -d : -f 1)"
        indec="$(printf "%d" 0x$first)"
        check="$(( $indec % 2 ))"
        if [ "$check" != 0 ]; then
            newfirst="$(printf "%x" $(( $indec + 1)))"
            genmac="$(printf "$genmac\n" | sed "s|^$first|$newfirst|")"
        fi
        macaddr="$genmac"
        ;;
      vmw|vmf|esx)
        venmac="$vendor_vmw"
        genmac="$(rndGen 3 :)"
        macaddr="$venmac:$genmac"
        ;;
      xen)
        venmac="$vendor_xen"
        genmac="$(rndGen 3 :)"
        macaddr="$venmac:$genmac"
        ;;
      kvm)
        venmac="$vendor_kvm"
        genmac="$(rndGen 3 :)"
        macaddr="$venmac:$genmac"
        ;;
      *)
        reportError "No macGen method specified; expected any of global|vmw|vmf|esx|xen|kvm"
        return 1
        ;;
    esac

    mkdir -p "$usrcfd/sys/$name"
    printf "$macaddr\n" > "$usrcfd/sys/$name/generated.mac"

    return 0
}

# printPortState(<address> <port>)
# Prints.
#
# Print the state ("open"/"closed"/"none") of a given port and address
# with as little response time as possible.
#
function printPortState {
    reportDebugFuncEntry "$*" "timeout"

    typeset addr
    typeset port
    typeset command
    typeset match

    if [ "$1" ]; then
        addr="$1"
    else
        addr="none"
    fi
    if [ "$2" ]; then
        port="$2"
    else
        port="none"
    fi

    if [ "$addr" = "none" -o "$port" = "none" ]; then
        reportDebug "IP address or port was none, will return none"
        printf "none\n"
        return
    fi

    if isLoopback "$addr" || isLocal "$name" ; then
        reportDebug "IP address or name was considered local, will return open"
        printf "open\n"
        return
    fi

    if [ "$resilient" ]; then
        reportDebug "Enabled resilient mode"
        command="nmap -n -T1 -PN -sT -p $port $addr"
        match=" open "
    else
        if [ "$scantype" = "connect" ]; then
            reportDebug "Scantype was set to connect"
            command="nmap -n -T5 --host-timeout $timeout -PN -sT -p $port $addr"
            match=" open "
        else
            command="nmap -n -T5 --host-timeout $timeout -PN -p $port $addr"
            match=" open "
        fi
    fi

    reportDebug "Command is: $privy $command"
    $privy $command 2>/dev/null | grep -q "$match" >/dev/null
    if [ "$?" = 0 ]; then
        reportDebug "Port $port on $addr is open"
        printf "open\n"
    else
        reportDebug "Port $port on $addr is closed"
        printf "closed\n"
    fi
}

function printPingState {
    reportDebugFuncEntry "$*"

    typeset addr
    typeset command
    typeset match

    if [ "$1" ]; then
        addr="$1"
    else
        addr="none"
    fi

    if [ "$addr" = "none" ]; then
        reportDebug "IP address was none, will return none"
        printf "none\n"
        return
    fi

    if isLoopback "$addr" || isLocal "$name" ; then
        reportDebug "IP address or name was considered local, will return open"
        printf "open\n"
        return
    fi

    command="nmap -sP $addr"
    match="Host is up"

    reportDebug "Command is: $privy $command"
    $privy $command 2>/dev/null | grep -q "$match" >/dev/null
    if [ "$?" = 0 ]; then
        reportDebug "$addr is reachable"
        printf "reachable\n"
    else
        reportDebug "$addr is unreachable"
        printf "unreachable\n"
    fi
}

# printState(<name> <returnFormat>)
# Prints.
#
# Print a long or short version of the state of a given machine, service or group thereof.
#
function printState {
    reportDebugFuncEntry "$*" "state acstate exstate vmstate svstate svrunning svlistening host type osmt acmt exmt svmt vrmt addr port user admin members"

    typeset name="$1"
    typeset returnFormat="$2"
    typeset sysopts
    typeset usropts

    if [ ! "$returnFormat" ]; then
        returnFormat="short"
    fi

    case "$returnFormat" in
      long)
        printf "# main config:\n"
        printf "type='$type'\n"
        printf "name='$name'\n"

        if [ "$type" = "host" -o "$type" = "guest" ]; then
            printf "osmt='$osmt'\n"
        elif [ "$type" = "service" ]; then
            printf "svmt='$svmt'\n"
            printf "port='$port'\n"
        fi

        if [ "$type" != "group" ]; then
            printf "acmt='$acmt'\n"
            printf "exmt='$exmt'\n"
            printf "user='$user'\n"
            printf "admin='$admin'\n"
            printf "addr='$addr'\n"
            printf "vrmt='$vrmt'\n"
            printf "host='$host'\n"
        fi
        printf "\n"

        sysopts="$syscfd/sys/$name/options.conf"
        usropts="$usrcfd/sys/$name/options.conf"

        if [ -e "$usropts" ]; then
            printf "# extra options (set locally):\n"
            cat "$usropts" | sed '/^$/d' | grep -v "^#"
            printf "\n"
        elif [ -e "$sysopts" -a ! -e "$usropts" ]; then
            printf "# extra options (set globally):\n"
            cat "$sysopts" | sed '/^$/d' | grep -v "^#"
            printf "\n"
        fi

        sysopts="$syscfd/sys/$host/options.conf"
        usropts="$usrcfd/sys/$host/options.conf"

        if [ -e "$usropts" ]; then
            printf "# inherited options (from host, set locally):\n"
            cat "$usropts" | sed '/^$/d' | grep -v "^#"
            printf "\n"
        elif [ -e "$sysopts" -a ! -e "$usropts" ]; then
            printf "# inherited options (from host, set globally):\n"
            cat "$sysopts" | sed '/^$/d' | grep -v "^#"
            printf "\n"
        fi

        if [ "$type" = "host" -o "$type" = "guest" ]; then
            printf "# inherited os options:\n"
            printf "osstop='$osstop'\n"
            printf "osreboot='$osreboot'\n"
            printf "oslisten='$oslisten'\n"
            printf "\n"
        fi

        if [ "$type" != "group" ]; then
            printf "# current state:\n"
            printf "acstate='$acstate'\n"
            printf "exstate='$exstate'\n"
            if [ "$type" = "service" ]; then
                printf "svrunning='$svrunning'\n"
                printf "svlistening='$svlistening'\n"
                printf "svstate='$svstate'\n"
            fi
            if [ "$type" = "guest" ]; then
                printf "vmstate='$vmstate'\n"
            fi
            printf "state='$state'\n"
            printf "\n"
        fi

        printf "# relations:\n"
        if [ "$type" != "group" ]; then
            printf "groups="
            typeset groups="$(unset debug ; verbose=1 ; listHelper group | grep "$name" | cut -d " " -f 2 | cut -d "(" -f 1 | tr '\n' ',' | sed "s|,$||" 2> /dev/null)"
            if [ "$groups" ]; then printf "'$groups'\n"; else printf "'none'\n"; fi
        fi

        if [ "$type" = "host" -o "$type" = "guest" ]; then
            printf "services="
            typeset services="$(unset debug ; verbose=1 ; listHelper service | grep "$name" | cut -d " " -f 2 | cut -d "(" -f 1 | tr '\n' ',' | sed  "s|,$||" 2> /dev/null)"
            if [ "$services" ]; then printf "'$services'\n"; else printf "'none'\n"; fi
        fi

        if [ "$type" = "host" -a "$vrmt" != "none" ]; then
            printf "guests="
            typeset guests="$(unset debug ; verbose=1 ; listHelper guest | grep "$name" | cut -d " " -f 2 | cut -d "(" -f 1 | tr '\n' ',' | sed  "s|,$||" 2> /dev/null)"
            if [ "$guests" ]; then printf "'$guests'\n"; else printf "'none'\n"; fi
        fi

        if [ "$type" = "group" ]; then
            printf "members="
            typeset members="$(tokenReader printVals "$name" "members")"
            if [ "$members" ]; then printf "'$members'\n"; else printf "'none'\n"; fi
        fi
        ;;
      short)
        if [ ! "$color" ]; then
            unset color_red color_green color_yellow color_blue color_end
        fi
        case "$state" in
          on|booting|created|destroyed|restarting|starting|stopping)
            report "$name: ${color_green}${state}${color_end}"
            return 0
            ;;
          off)
            report "$name: ${color_blue}${state}${color_end}"
            return 4
            ;;
          busy)
            report "$name: ${color_yellow}${state}${color_end}"
            return 2
            ;;
          *)
            report "$name: ${color_red}${state}${color_end}"
            return 1
        esac
        ;;
    esac
}

# addConf(<entry>)
# Prints.
#
# Add an <entry> to session.conf.
#
function addConf {
    reportDebugFuncEntry "$*" "type name"

    typeset entryFound
    typeset addConfEntry

    entry="$*"
    addConfEntry="$entry"
    type="${entry%%[[:space:]]*}"

    # Check for name collision
    entryFound="$(grep -v '^#' "$usrcff" | grep " $name(")"
    if [ "$entryFound" ]; then
        reportError "An entry with name $name is already present in $usrcff"
        return 1
    fi

    # Check for addr collision, excluding services.
    if [ "$type" = "host" -o "$type" = "guest" ]; then
        entryFound="$(grep -v '^#' "$usrcff" | grep -v '^service' | grep -v '^group' | grep "$addr")"
        if [ "$entryFound" ]; then
            reportError "An entry with addr $addr is already present in $usrcff"
            return 1
        fi
    fi

    # Add new entry.
    printf "$addConfEntry\n" >> "$usrcff"
    reportInfo "Successfully added entry $*"
    return 0
}

# delConf(<name>)
# Deletes.
#
# Remove the named configuration entry from session.conf.
#
function delConf {
    reportDebugFuncEntry "$*" "usrcff"

    typeset name="$1"
    typeset entryFound="$(entryReader "$name" "$usrcff")"
    typeset deletion

    if [ ! "$entryFound" ]; then
        reportError "No entry with name $name is present in $usrcff"
        return 1
    fi

    # Delete the entry.
    deletion="$(printf "$entryFound\n" | sed 's|\\|\\\\|g')"
    cat "$usrcff" | sed -e "/$deletion/d" > "$usrcfd/tmp/session.conf.tmp"
    mv -f "$usrcfd/tmp/session.conf.tmp" "$usrcff"
    reportInfo "Removed $entryFound"
    return 0
}

# checkNameOrAddress(<nameOrAddress>)
# Returns truth value.
#
# Check the syntax of a given host name or IP address.
# Return 0 if it's OK, 1 if it's not OK.
#
function checkNameOrAddress {
    reportDebugFuncEntry "$*"

    typeset input="$1"
    typeset maxLength
    typeset length
    typeset seq

    if [ "${input//[[:digit:].]/}" ]; then
        # It's not a numeral (possibly with some dots)
        # Treat it as an (attempted) host name

        if [ "${input//[[:word:]-.]/}" ]; then
            reportError "Name '$input' contains invalid characters"
            return 1
        fi

        maxLength=64
        length=${#input}
        if [ "$length" -gt "$maxLength" ]; then
            if [ "$name" = "$input" ]; then
                reportError "Name '$input' is too long: it is $length characters long but the maximum is $maxLength"
            elif [ "$addr" = "$input" ]; then
                reportError "Address '$input' is too long: it is $length characters long but the maximum is $maxLength"
            else
                reportError "String '$input' is too long: it is $length characters long but the maximum is $maxLength"
            fi
            return 1
        fi

        return 0
    fi

    # It's a numeral, possibly with some dots
    # Treat it as an (attempted) IP address

    typeset IFS="." ; set -- $input
    if [ "$#" != 4 ]; then
        reportError "Invalid IP address '$input' specified for '$name'"
        return 1
    fi
    for seq do
        case "$seq" in
          ""|*[!0-9]*)
            reportError "Invalid IP address '$input' specified for '$name'"
            return 1
            ;;
        esac
        if [ "$seq" -gt 255 ]; then
            reportError "Invalid IP address '$input' specified for '$name'"
            return 1
        fi
    done

    return 0
}

# listHelper(<name>)
# Prints.
#
# name => check for existence (return name if true)
# name --verbose => check for existence (return entry if true)
# Can list all sorts of session related entities.
#
function listHelper {
    reportDebugFuncEntry "$*"

    name="$1"

    case "$name" in
      group|groups|guest|guests|host|hosts|service|services|all)
        match="$(printf "$name\n" | sed 's|s$||')"
        if [ "$verbose" ]; then
            if [ "$name" = "all" ]; then
                grep -ve "^$" -ve "^#" "$config"
            else
                grep "^$match" "$config" | sed '/^$/d'
            fi
        else
            if  [ "$name" = "all" ]; then
                grep -ve "^$" -ve "^#" "$config" | cut -d '(' -f 1 | awk '{print $2}'
            else
                grep "^$match" "$config" | sed "s|^$match ||g" | cut -d '(' -f 1
            fi
        fi
        ;;
      mode|modes)
        if [ "$default" ]; then
            printf "$defaultmode\n"
        else
        for item in $known_modes ; do
            printf "$item\n" $(if [ "$item" = "$defaultmode" ]; then printf "(default)\n" ; fi)
        done
        fi
        ;;
      osmt)
        for item in $known_osmts ; do
            printf "$item\n"
        done
        ;;
      acmt)
        for item in $known_acmts ; do
            printf "$item\n"
        done
        ;;
      exmt)
        for item in $known_exmts ; do
            printf "$item\n"
        done
        ;;
      svmt)
        for item in $known_svmts ; do
            printf "$item\n"
        done
        ;;
      vrmt)
        for item in $known_vrmts ; do
            printf "$item\n"
        done
        ;;
      crmt)
        for item in $known_crmts ; do
            printf "$item\n"
        done
        ;;
      *)
        if [ "$verbose" ]; then
            entryReader "$name" "$config"
        else
            tokenReader printVals "$name" "name"
        fi
        ;;
    esac
}

# discoveryHelper(<range>)
# Prints
#
# Discover hosts on a network and addConf them.
#
function discoveryHelper {
    reportDebugFuncEntry "$*" "name userDblBacksl"

    typeset range="$1"

    typeset addr
    typeset type
    typeset name
    typeset osmt
    typeset acmt
    typeset exmt
    typeset user
    typeset admin
    typeset file

    if [ ! "$range" ]; then
        reportError "I was called without range being set"
        return 1
    fi

    if [ "$platform" = "linux" -o "$platform" = "macosx" ]; then
        file="$usrcfd/tmp/session.discover.out"
    elif [ "$platform" = "windows" ]; then
        file="$(toLocalWindowsPath "$usrcfd/tmp/session.discover.out")"
    fi

    $privy nmap -n -T5 -PE -oG "$file" -sP $range >/dev/null 2>&1 </dev/null
    reportDebug "Called nmap as: $privy nmap -n -T5 -PE -oG \"$file\" -sP $range"
    for addr in $($privy cat "$usrcfd/tmp/session.discover.out" | grep Up | cut -d " " -f 2 | sed -e 's/^[[:space:]]*//') ; do
        type="host"

        if [ "$platform" = "linux" -o "$platform" = "macosx" ]; then
            name="$(host $addr 2>/dev/null | grep "domain name pointer" | cut -d " " -f 5  | cut -d "." -f 1 | head -n 1 | sed -e 's/^[[:space:]]*//' | tr A-Z a-z)"
            reportDebug "Got name from DNS for $addr: $name"
        elif [ "$platform" = "windows" ]; then
            name="$(nslookup $addr 2>/dev/null | grep Name | cut -d ":" -f 2 | cut -d "." -f 1 | sed -e 's/^[[:space:]]*//' | tr A-Z a-z)"
            reportDebug "Got name from DNS for $addr: $name"
        fi

        if [ ! "$name" ]; then
            name="unknown$(printf "$addr\n" | awk 'BEGIN {FS="."}{print "-" $3 "-" $4}')"
            reportDebug "Got no name from DNS for $addr, created name: $name"
        fi

        osmt="embedded-like"
        acmt="none"
        exmt="none"
        user="unknown"
        admin="unknown"
        if [ "$(printPortState "$addr" 22)" = "open" ]; then
            osmt="unix-like"
            acmt="ssh"
            exmt="ssh"
            user="$userDblBacksl"
            admin="root"
        elif [ "$(printPortState "$addr" 23)" = "open" ]; then
            osmt="embedded-like"
            acmt="tel"
            exmt="none"
            user="unknown"
            admin="unknown"
        elif [ "$(printPortState "$addr" 445)" = "open" ]; then
            osmt="windows-like"
            acmt="rdp"
            exmt="smb"
            user="$userDblBacksl"
            admin="administrator"
        elif [ "$(printPortState "$addr" 443)" = "open" ]; then
            osmt="embedded-like"
            acmt="http"
            exmt="none"
            user="$userDblBacksl"
            admin="root"
        elif [ "$(printPortState "$addr" 80)" = "open" ]; then
            osmt="embedded-like"
            acmt="http"
            exmt="none"
            user="$userDblBacksl"
            admin="root"
        fi
        addConf "$type $name($osmt,$acmt,$exmt,$user,$admin,$addr,none)"
    done

    if [ "$debug" ]; then
        reportDebug "Not removing $usrcfd/tmp/session.discover.out"
    else
        $privy rm "$usrcfd/tmp/session.discover.out"
    fi

    return 0
}

# cutParentheses(<input>)
# Prints.
#
# Return the part of <input> between parentheses.
#
function cutParentheses {
    reportDebugFuncEntry "$*"

    typeset input="$*"
    typeset mangle="$input"; mangle="${mangle#*\(}"; mangle="${mangle%%\)*}"
    typeset output="$mangle"
    printf "$output\n"
}

# entryReader(<name> <file>)
# Prints.
#
# Print the (last) entry for <name> in <file>.
#
function entryReader {
    reportDebugFuncEntry "$*"

    typeset name="$1"
    typeset file="$2"
    typeset entry=$(grep -e "^[[:space:]]*[[:alnum:]]\+[[:space:]]\+$name(" "$file" | sed -e "s/^[[:space:]]*//;s/[[:space:]]*$//" | tail -n 1)

    if [ "$entry" ]; then
        printf "$entry\n"
    elif [ "$platform" != "windows" ]; then
        reportDebug "Entry returned empty, see if file was opened and retry when closed + available"
        typeset fileOpen="$(lsof "$file")"
        while [ "$fileOpen" ]; do
            fileOpen="$(lsof "$file")"
            reportInfo "Waiting to open $file"
        done

        entry=$(grep -e "^[[:space:]]*[[:alnum:]]\+[[:space:]]\+$name(" "$file" | sed -e "s/^[[:space:]]*//;s/[[:space:]]*$//" | tail -n 1)

        if  [ "$entry" ]; then
            printf "$entry\n"
         else
             reportError "Entry with name $name not found in $file"
             return 1
        fi
     else
         reportError "Entry with name $name not found in $file"
         return 1
    fi
}

# tokenReader(<function> <name> <keys>)
# Prints, sets variables.
#
# Look up the values for the given comma-separated <keys>
# in either the given <entry> if the <name> matches, otherwise
# in the <config> file under that <name> and either print out
# the values separated by spaces (if <function> is "printVals"),
# or set global variables named like the keys to the looked-up
# values (if <function> is "setVars").
#
function tokenReader {
    reportDebugFuncEntry "$*" "config entry"

    [ "$3" ] || { reportError "Not enough arguments passed" ; return 1 ; }

    typeset function="$1"
    typeset name="$2"
    typeset keys="$3"

    typeset enttype
    typeset key
    typeset -a values
    typeset -i indx
    typeset varname
    typeset position
    typeset value
    typeset output

    # Reuse a previously set entry if it is about the same entry.
    case "$entry" in
      *" ${name}("*)
        reportDebug "Using previously set entry for $name"
        ;;
      *)
        reportDebug "Reading entry for $name"
        entry="$(entryReader "$name" "$config")"
        ;;
    esac

    # Check if the neccessary parameters are set.
    [ "$function" ] || { reportDebug "Function not specified"    ; return 1 ; }
    [ "$name" ]     || { reportDebug "Name not passed"           ; return 1 ; }
    [ "$keys" ]     || { reportDebug "Keys not passed for $name" ; return 1 ; }
    [ "$entry" ]    || { reportDebug "Entry not set for $name"   ; return 1 ; }

    # Read type from entry
    enttype="${entry%%[[:space:]]*}"
    members="$enttype,$name,$(cutParentheses "$entry")"

    let indx=0
    typeset IFS=","
    for key in $keys ; do
        # Try to read the positional index number from defined syntax.
        varname="syntax_${enttype}_${key}"
        position="$(eval printf '%s\\n' "\$$varname")"

        reportDebug "Looking up $key for $name at position $position"
        if [ "$key" != "members" ]; then
            # Not looking for group members, use position.
            value="$(printf "$members\n" | cut -d ',' -f "$position")"
        else
            # Looking for members, which is a csv list in itself.
            value="$(cutParentheses "$entry")"
        fi

        (( "$position" )) || { reportError "Position $position for $varname unset" ; return 1 ; }

        if [ "$function" = "printVals" ]; then
            values[indx]="$value"
            let indx+=1
        elif [ "$function" = "setVars" ]; then
            export "$key"="$value"
        else
            reportError "Invalid function passed: $function"
            return 1
        fi
    done

    typeset IFS=" "
    if [ "$function" = "printVals" ]; then
        output="${values[*]}"
        reportDebug "Output: $output"
        printf "$output\n"
    fi

    return 0
}

# tokenValidator(<keys>)
# Returns truth value.
#
# Checks if tokens contain valid values.
# Returns 0 if valid, 1 if invalid.
#
function tokenValidator {
    reportDebugFuncEntry "$*"

    typeset keys="$1"
    typeset varname
    typeset value
    typeset list

    # Check if the neccessary parameters are set.
    [ "$keys" ]     || { reportDebug "Keys not passed" ; return 1 ; }

    typeset IFS=","
    for key in $keys ; do
        varname="known_${key}s"
        value="$(eval printf '%s\\n' "\$$key")"
        list="$(eval printf '%s\\n' "\$$varname")"

        if [ -z "$value" ]; then
            reportError "Empty value for $key"
            return 1
        fi

        case "$key" in
          name|host)
            if [ "$key" = "name" -a "$value" = "none" ]; then
                reportError "You cannot use none as a name"
                return 1
            fi
            ;;
          addr)
            reportDebug "Verifying $key for $name with value $value"
            if [ "$value" != "none" ]; then
                checkNameOrAddress "$value" || return 1
            fi
            ;;
          user|admin)
            reportDebug "Verifying $key for $name with value $value"
            ;;
          type|mode|osmt|acmt|exmt|svmt|vrmt)
            reportDebug "Verifying $key for $name with value $value"
            if [[ ! "$list" =~ "$value" ]]; then
                reportError "Unknown $key method $value specified for $name"
                return 1
            fi
            ;;
          port)
            reportDebug "Verifying $key for $name with value $value"
            ;;
          members)
            reportDebug "Verifying $key for $name with value $value"
            ;;
          *)
            reportError "Unknown key $key passed"
            return 1
        esac
    done

    return 0
}

# parseParameters(<parameters>)
# Sets variables.
#
# Parse command-line parameters <parameters> for options.
#
# For each command-line option "--foo" or "--foo=bar" set variable
# foo ether to "1" or, respectively, to "bar".
#
# Optionally supports a $mandatories and a $optionals list, which allows
# validity and scope checking.
#
# Example: mandatories="foo bar" ; optionals="baz" ; parseParameters "$@"
# (As shown in the example, $@ must be enclosed in double quotation marks.)
#
# Warning: arguments starting with '--' will be interpreted as options,
# so do not send to this function non-option arguments which could
# start with '--'
#
# Option values may be arbitrary strings.
# Option names may not contain spaces.
#
function parseParameters {
    reportDebugFuncEntry "$*" "mandatories optionals silence"

    typeset -a variables
    typeset -a values
    typeset -i indx
    typeset -i numVariables
    typeset found
    typeset mandatoriesMissing
    typeset optionalsOmitted
    typeset illegalsPresent
    typeset parameter
    typeset variable
    typeset mandatory
    typeset optional
    typeset value

    # First omit any non-option parameters
    while [ "$1" ]; do
        case "$1" in
          --*)
            # Found the first well formed option.
            break
            ;;
          *)
            # Not a well formed option.  Next!
            reportWarning "Unrecognized parameter $1"
            shift
            ;;
        esac
    done

    # From options extract variables;
    # i.e., strings between initial "--" and either first "=" or eol
    let indx=0
    typeset IFS=" "
    for parameter in "$@" ; do
        parameterWithoutHyphens="${parameter#--}"
        if [ "$parameterWithoutHyphens" = "$parameter" ]; then
            # Not a well-formed option
            reportWarning "Unrecognized parameter $parameter"
            continue
        fi
        case "$parameterWithoutHyphens" in
          *=*)
            variable="${parameterWithoutHyphens%%=*}"
            value="${parameterWithoutHyphens#$variable=}"
            ;;
          *)
            variable="$parameterWithoutHyphens"
            value=1  # Default value
            ;;
        esac
        variables[indx]="$variable"
        values[indx]="$value"
        let indx+=1
    done
    let numVariables=indx
    reportDebug "Variables=${variables[*]}, Values=${values[*]}"

    # Check that mandatories are present
    mandatoriesMissing=""
    for mandatory in $mandatories ; do
        found=""  # (False)
        for variable in "${variables[@]}" ; do
            if [ "$variable" = "$mandatory" ]; then
                found="y"
                break
            fi
        done
        [ "$found" ] || mandatoriesMissing="${mandatoriesMissing:+$mandatoriesMissing }--$mandatory"
    done

    if [ ! "$silence" ]; then
        optionalsOmitted=""
        # Check that optionals are present
        for optional in $optionals ; do
            found=""  # (False)
            for variable in "${variables[@]}" ; do
                if [ "$variable" = "$optional" ]; then
                    found="y"
                    break
                fi
            done
            [ "$found" ] || optionalsOmitted="${optionalsOmitted:+$optionalsOmitted }--$optional"
        done
    fi

    # Set variables (if legal)
    if [ "$mandatories" -o "$optionals" ]; then
        acceptAllVariables=""  # (False)
        legalVariables=" $mandatories $optionals " # With initial, intermediate and final space!
    else
        # No mandatories or optionals specified
        # Accept any variable (extremely unsafe)
        acceptAllVariables="y"
    fi
    let indx=0
    while [[ indx -lt numVariables ]]; do
        variable="${variables[indx]}"
        if [ "$acceptAllVariables" ] || [[ "$legalVariables" =~ " $variable " ]]; then
            value="${values[indx]}"
            reportDebug "Setting variable $variable to value $value"
            export "$variable"="$value"
        else
            illegalsPresent="${illegalsPresent:+$illegalsPresent }--$variable"
        fi
        let indx+=1
    done

    if [ "$illegalsPresent" ]; then
        reportError "Illegal parameter(s): $illegalsPresent"
    fi

    if [ "$mandatoriesMissing" ]; then
        reportError "Mandatory parameter(s) missing: $mandatoriesMissing"
    fi

    if [ ! "$silence" ] && [ "$optionalsOmitted" ]; then
        reportInfo "Optional parameter(s) omitted: $optionalsOmitted"
    fi

    if [ "$illegalsPresent" ] || [ "$mandatoriesMissing" ]; then
        exit 1
    fi

    # Unset extra parameters to this function
    unset mandatories
    unset optionals
    unset silence
}

# parseEntry(<name>)
# Sets name entry type.
#
# Call appropriate parse*(<name>).
#
function parseEntry {
    reportDebugFuncEntry "$*" "config"

    name="$1" ; [ "$name" ] || { reportError "No name passed" ; return 1 ; }

    entry="$(entryReader "$name" "$config")"
    type="${entry%%[[:space:]]*}"
    tokenValidator type || return 1

    parse$(capsFirst "$type") "$name"

    return $?
}

# parseGroup(<name>)
# Sets variables.
#
# Read group entry <name> from <config>.
#
function parseGroup {
    reportDebugFuncEntry "$*"

    typeset opts

    name="$1" ; [ "$name" ] || { reportError "No name passed" ; return 1 ; }

    # Source group extra options from global settings.
    opts="$syscfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then
        . "$opts"
    fi

    # Source group extra options from local settings (might override global options).
    opts="$usrcfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then
        . "$opts"
    fi

    tokenReader setVars "$name" "members" || return 1

    return 0
}

# parseHost(<name>)
# Sets variables.
#
# Read host entry <name> from <config>.
#
function parseHost {
    reportDebugFuncEntry "$*"

    typeset opts

    name="$1" ; [ "$name" ] || { reportError "No name passed" ; return 1 ; }

    # Read all key values and set them.
    tokenReader setVars "$name" "osmt,acmt,exmt,user,admin,addr,vrmt" || return 1
    port="none" ; host="none" ; svmt="none"

    # Validate all key values.
    tokenValidator "type,name,osmt,acmt,exmt,user,admin,addr,vrmt,port,host,svmt" || return 1

    # If an entry is about the local system, rewrite $privesc and $privy with correct admin user.
    if isLoopback "$addr" || isLocal "$name" ; then
        reportDebug "Rewriting privesc to $privesc"
        privesc="$(printf "$privesc\n" | sed -e "s|root|$admin|g" -e "s|administrator|$admin|g")"
        [ "$privy" ] && privy="$privesc"
    fi

    # Initialize os specific globals.
    osGlobals "$osmt" || return 1

    # Check for global password files and set variables.
    if [ -e "$syscfd/sys/$name/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$syscfd/sys/$name/user.pwd")"
    fi
    if [ -e "$syscfd/sys/$name/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$syscfd/sys/$name/admin.pwd")"
    fi

    # Check for local password files and set variables (might override global options).
    if [ -e "$usrcfd/sys/$name/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$usrcfd/sys/$name/user.pwd")"
    fi
    if [ -e "$usrcfd/sys/$name/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$usrcfd/sys/$name/admin.pwd")"
    fi

    # Source host extra options from global settings.
    opts="$syscfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then
        . "$opts"
    fi

    # Source host extra options from local settings (might override global options).
    opts="$usrcfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then
        . "$opts"
    fi

    return 0
}

# parseGuest(<name>)
# Sets variables.
#
# Read guest entry <name> from <config>.
#
function parseGuest {
    reportDebugFuncEntry "$*"

    typeset opts

    name="$1" ; [ "$name" ] || { reportError "No name passed" ; return 1 ; }

    # Read all key values and set them.
    tokenReader setVars "$name" "osmt,acmt,exmt,user,admin,addr,host" || return 1
    tokenReader setVars "$host" "vrmt" || return 1
    port="none" ; svmt="none"

    # Validate all key values.
    tokenValidator "type,name,osmt,acmt,exmt,user,admin,addr,host,vrmt,port,svmt" || return 1

    # If an entry is about the local system, rewrite $privesc and $privy with correct admin user.
    if isLoopback "$addr" || isLocal "$name" ; then
        reportDebug "Rewriting privesc to $privesc"
        privesc="$(printf "$privesc\n" | sed -e "s|root|$admin|g" -e "s|administrator|$admin|g")"
        [ "$privy" ] && privy="$privesc"
    fi

    # Initialize os specific globals
    osGlobals "$osmt" || return 1

    # Check for global password files and set variables.
    if [ -e "$syscfd/sys/$name/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$syscfd/sys/$name/user.pwd")"
    fi
    if [ -e "$syscfd/sys/$name/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$syscfd/sys/$name/admin.pwd")"
    fi

    # Check for local password files and set variables (might override global options).
    if [ -e "$usrcfd/sys/$name/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$usrcfd/sys/$name/user.pwd")"
    fi
    if [ -e "$usrcfd/sys/$name/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$usrcfd/sys/$name/admin.pwd")"
    fi

    # Source both parent host and guest extra options from global settings.
    opts="$syscfd/sys/$host/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi
    opts="$syscfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi

    # Source both parent host and guest extra options from local settings.
    opts="$usrcfd/sys/$host/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi
    opts="$usrcfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi

    return 0
}

# parseService(<name>)
# Sets variables.
#
# Read service entry <name> from <config>.
#
function parseService {
    reportDebugFuncEntry "$*"

    typeset opts
    typeset value
    typeset htype

    name="$1" ; [ "$name" ] || { reportError "No name passed" ; return 1 ; }

    # Read all key values and set them.
    tokenReader setVars "$name" "svmt,acmt,exmt,user,addr,port,host" || return 1
    osmt="none" ; admin="none" ; vrmt="none"

    # Inherit parent host values. The vrmt value is only inherited if parent type is host.
    htype="$(tokenReader printVals "$host" type)"
    for key in osmt vrmt acmt exmt user admin addr; do
        value="$(eval printf '%s\\n' "\$$key")"
        if [ "$htype" = "host" -a "$key" = "vrmt" ]; then
            tokenReader setVars "$host" "$key" || return 1
        elif [ "$value" = "none" -a "$key" != "vrmt" ]; then
            tokenReader setVars "$host" "$key" || return 1
        fi
    done

    # Validate all key values.
    tokenValidator "type,name,svmt,acmt,exmt,user,addr,port,host,osmt,admin,vrmt" || return 1

    # If an entry is about the local system, rewrite $privesc and $privy with correct service user.
    if isLoopback "$addr" || isLocal "$name" ; then
        reportDebug "Rewriting privesc to $privesc"
        privesc="$(printf "$privesc\n" | sed -e "s|root|$user|g" -e "s|administrator|$user|g")"
        [ "$privy" ] && privy="$privesc"
    fi

    # Check for global password files and set variables.
    if [ -e "$syscfd/sys/$host/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$syscfd/sys/$host/user.pwd")"
    fi
    if [ -e "$syscfd/sys/$name/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$syscfd/sys/$name/user.pwd")"
    fi
    if [ -e "$syscfd/sys/$host/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$syscfd/sys/$host/admin.pwd")"
    fi
    if [ -e "$syscfd/sys/$name/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$syscfd/sys/$name/admin.pwd")"
    fi

    # Check for local password files and set variables (might override global options).
    if [ -e "$usrcfd/sys/$host/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$usrcfd/sys/$host/user.pwd")"
    fi
    if [ -e "$usrcfd/sys/$name/user.pwd" ]; then
        upwd="$(tr -d '\r' < "$usrcfd/sys/$name/user.pwd")"
    fi
    if [ -e "$usrcfd/sys/$host/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$usrcfd/sys/$host/admin.pwd")"
    fi
    if [ -e "$usrcfd/sys/$name/admin.pwd" ]; then
        apwd="$(tr -d '\r' < "$usrcfd/sys/$name/admin.pwd")"
    fi

    # Source both parent host and service extra options from global settings.
    opts="$syscfd/sys/$host/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi
    opts="$syscfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi

    # Source both parent host and service extra options from local settings.
    opts="$usrcfd/sys/$host/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi
    opts="$usrcfd/sys/$name/options.conf"
    if [ -e "$opts" ]; then . "$opts" ; fi

    return 0
}

# checkState()
# Sets variables.
#
# Check the state of a host, guest or service.
# Also, initialize state variables and variables in options.conf.
#
function checkState {
    reportDebugFuncEntry "$*" "type acmt exmt"

    acstate="unset"
    exstate="unset"
    svstate="unset"
    vmstate="unset"
    state="unset"

    # Determine if we're sane.
    if [ ! "$type" ]; then
        reportError "Variable type not set"
        return 1
    elif [ "$type" = "group" ]; then
        reportError "Passed invalid type $type"
        return 1
    fi

    # Invoke state handlers to set acstate and exstate.
    ${acmt}AccessHandler state
    if [ "$acmt" = "$exmt" ]; then
        exstate="$acstate"
    else
        ${exmt}ExecHandler state
    fi

    # Determine final state for host entities.
    if [ "$type" = "host" ]; then
        if [ "$acstate" = "active" -a "$exstate" = "active" ]; then
            state="on"
        elif [ "$acstate" = "active" -a "$exstate" = "none" ]; then
            state="on"
        elif [ "$acstate" = "none" -a "$exstate" = "active" ]; then
            state="on"
        elif [ "$acstate" = "inactive" -a "$exstate" = "inactive" ]; then
            state="$(printPingState "$addr")"
            if [ "$state" = "reachable" ]; then state="busy"; fi;
        elif [ "$acstate" = "inactive" -a "$exstate" = "none" ]; then
            state="$(printPingState "$addr")"
            if [ "$state" = "reachable" ]; then state="busy"; fi;
        elif [ "$acstate" = "none" -a "$exstate" = "inactive" ]; then
            state="$(printPingState "$addr")"
            if [ "$state" = "reachable" ]; then state="busy"; fi;
        elif [ "$acstate" = "none" -a "$exstate" = "none" ]; then
            state="$(printPingState "$addr")"
            if [ "$state" = "reachable" ]; then state="on"; fi;
        else
            state="busy"
        fi

    # Determine final state for guest entities.
    elif [ "$type" = "guest" ]; then
        if [ "$acstate" = "active" -a "$exstate" = "active" ]; then
            state="on"
        elif [ "$acstate" = "active" -a "$exstate" = "none" ]; then
            state="on"
        elif [ "$acstate" = "none" -a "$exstate" = "active" ]; then
            state="on"
        else
            typeset parentstate="$(parseEntry "$host" ; checkState ; printf "$state\n")"
            if [ "$parentstate" = "on" ]; then
                ${vrmt}VirtHandler state
                if [ "$vmstate" = "active" ]; then
                    state="busy"
                elif [ "$vmstate" = "inactive" ] ;then
                    state="off"
                else
                    state="$vmstate"
                fi
            else
                state="$parentstate"
            fi
        fi

    # Determine final state for service entities.
    elif [ "$type" = "service" ]; then
        ${svmt}ServiceHandler state
        if [ "$svstate" = "active" ]; then
            state="on"
        elif [ "$svrunning" = "active" -a "$port" = "none" ]; then
            state="on"
        elif [ "$svrunning" = "inactive" -a "$port" = "none" ]; then
            state="off"
        elif [ "$svrunning" = "active" -a "$svlistening" = "active" ]; then
            state="on"
        elif [ "$svrunning" = "inactive" -a "$svlistening" = "inactive" ]; then
            state="off"
        elif [ "$svstate" = "unknown" ]; then
            state="unknown"
        else
            state="busy"
        fi
    fi
}

# startEntity()
# Calls specialized function.
#
# Start a system by means of the appropriate VirtHandler.
#
function startEntity {
    reportDebugFuncEntry "$*" "execute type"

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set to 1 in $usropts or $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$type" = "host" ]; then
        noneVirtHandler start
    elif [ "$type" = "guest" ]; then
        ${vrmt}VirtHandler start
    elif [ "$type" = "service" ]; then
        ${svmt}ServiceHandler start
    else
        reportError "Unknown type $type passed"
        return 1
    fi

    return 0
}

# stopEntity()
# Calls specialized function.
#
# Stop a system by means of the appropriate VirtHandler.
#
function stopEntity {
    reportDebugFuncEntry "$*" "execute type"

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set execute to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$type" = "host" ]; then
        noneVirtHandler stop
    elif [ "$type" = "guest" ]; then
        ${vrmt}VirtHandler stop
    elif [ "$type" = "service" ]; then
        ${svmt}ServiceHandler stop
    else
        reportError "Unknown type $type passed"
        return 1
    fi

    return 0
}

# restartEntity()
# Calls specialized function.
#
# Restart a system by means of the appropriate VirtHandler
#
function restartEntity {
    reportDebugFuncEntry "$*" "execute type"

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set execute to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$type" = "host" ]; then
        noneVirtHandler restart
    elif [ "$type" = "guest" ]; then
        ${vrmt}VirtHandler restart
    elif [ "$type" = "service" ]; then
        ${svmt}ServiceHandler restart
    else
        reportError "Unknown type $type passed"
        return 1
    fi

    return 0
}

# createEntity()
# Calls specialized function.
#
# Create a system by means of the appropriate VirtHandler.
#
function createEntity {
    reportDebugFuncEntry "$*" "execute send type"

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set execute to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$send" != "true" ]; then
        reportError "Required send functions disabled; set send to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_send; executables detected: $tools_send_found"
        return 1
    fi

    if [ "$type" = "host" ]; then
        noneVirtHandler create
    elif [ "$type" = "guest" ]; then
        ${vrmt}VirtHandler create
    else
        reportError "Unknown type $type passed"
        return 1
    fi

    return 0
}

# destroyEntity()
# Calls specialized function.
#
# Destroy a system by means of the appropriate VirtHandler.
#
function destroyEntity {
    reportDebugFuncEntry "$*" "execute destroy type"

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set execute to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$destroy" != "true" ]; then
        reportError "Required destroy functions disabled; set destroy to 1 in $usropts or in $sysopts to enable"
        return 1
    fi

    if [ "$type" = "host" ]; then
        noneVirtHandler destroy
    elif [ "$type" = "guest" ]; then
        ${vrmt}VirtHandler destroy
    else
        reportError "Unknown type $type passed"
        return 1
    fi

    return 0
}

# accessEntity()
# Calls specialized function, sets xsasuser xsasupwd.
#
# Access a system by means of the appropriate AccessHandler.
#
function accessEntity {
    reportDebugFuncEntry "$*" "acmt access xsastype user upwd admin apwd"

    if [ "$access" != "true" ]; then
        reportError "Required access functions disabled; set access to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_access; executables detected: $tools_access_found"
        return 1
    fi

    if [ "$xsastype" = "service" -o "$xsastype" = "user" ]; then
        xsasuser="$user"
        xsasupwd="$upwd"
    elif [ "$xsastype" = "admin" ]; then
        xsasuser="$admin"
        xsasapwd="$apwd"
    fi

    if [ "$type" ]; then
        ${acmt}AccessHandler access
        return $?
    else
        reportError "Unknown type $type passed"
        return 1
    fi
}

# runAs()
# Calls specialized function.
#
# Send a command to a system by means of the appropriate ExecHandler.
#
function runAs {
    reportDebugFuncEntry "$*" "execute exmt runastype"

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set execute to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$type" ]; then
        ${exmt}ExecHandler "runas${runastype}"
        return $?
    else
        reportError "Unknown type $type passed"
        return 1
    fi
}

# sendAs()
# Calls specialized function.
#
# Send a file or directory to a system by means of the appropriate ExecHandler.
#
function sendAs {
    reportDebugFuncEntry "$*" "send exmt sendastype"

    if [ "$send" != "true" ]; then
        reportError "Required send functions disabled; set send to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_send; executables detected: $tools_send_found"
        return 1
    fi

    if [ "$type" ]; then
        ${exmt}ExecHandler "sendas${sendastype}"
        return $?
    else
        reportError "Unknown type $type passed"
        return 1
    fi
}

# credAs()
# Calls specialized function.
#
# Call one or more credential managers, dependent on the relevant
# acmt and exmt settings.
#
function credAs {
    reportDebugFuncEntry "$*" "execute type exmt acmt addr"

    typeset list
    typeset crmt
    typeset retval

    if [ "$execute" != "true" ]; then
        reportError "Required execute functions disabled; set execute to 1 in $usropts or in $sysopts or make sure your have the required executables; executables required: $tools_execute; executables detected: $tools_execute_found"
        return 1
    fi

    if [ "$exmt" = "none" -a "$type" = "service" ]; then
        exmt="$(parseEntry "$host" ; printf "$exmt\n")"
        reportInfo "Inherited execute method ($exmt) from parent $host"
    fi

    if [ "$acmt" = "none" -a "$type" = "service" ]; then
        acmt="$(parseEntry "$host" ; printf "$acmt\n")"
        reportInfo "Inherited access method ($acmt) from parent $host"
    fi

    if [ "$addr" = "none" -a "$type" = "service" ]; then
        addr="$(parseEntry "$host" ; printf "$addr\n")"
        reportInfo "Inherited address ($addr) from parent $host"
    fi

    if [ "$exmt" = "none" -a "$acmt" = "none" ]; then
        reportError "Both execute method and access method are \"none\"; cannot send or store credentials without one of these"
        return 1
    elif [ "$exmt" = "ssh" -a "$acmt" = "rdp" ]; then
        list="ssh win"
    elif [ "$exmt" = "smb" -a "$acmt" = "ssh" ]; then
        list="ssh win"
    elif [ "$exmt" = "ssh" -o "$acmt" = "ssh" ]; then
        list="ssh"
    elif [ "$exmt" = "smb" -o "$acmt" = "rdp" ]; then
        list="win"
    fi

    retval=0
    for crmt in $list ; do
        ${crmt}CredHandler "as${type}"
        [ "$?" = 0 ] || retval=1
    done

    return $retval
}

# noneVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle non-vm-host related commands.
#
function noneVirtHandler {
    reportDebugFuncEntry "$*" "state destroy"

    case "$1" in
      state)
        vmstate="none"
        ;;
      start)
        reportError "Not implemented: I don't know how to start a physical system"
        return 1
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) >/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) >/dev/null
            state="restarting"
        fi
        ;;
      create)
        reportError "Not implemented: I don't know how to create a physical system"
        return 1
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            reportError "Not implemented: I don't know how to destroy a physical system"
            return 1
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# kvmVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle KVM virtual machine related commands.
#
function kvmVirtHandler {
    reportDebugFuncEntry "$*" "name host state vrmt memsize destroy"

    typeset result
    typeset template
    typeset answer
    typeset -i memsizeInKiB=memsize*1024

    case "$1" in
      state)
        result="$(command="virsh domstate \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin)"
        case "$result" in
          *running*)
            vmstate="active"
            ;;
          *"shut off"*)
            vmstate="inactive"
            ;;
          *)
            vmstate="non-existing"
            ;;
        esac
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command="virsh start \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="booting"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        # Check for global template, override with local if exists.
        if [ -e "$syscfd/tpl/$vrmt.stf" ]; then
            template="$syscfd/tpl/$vrmt.stf"
        fi
        if [ -e "$usrcfd/tpl/$vrmt.stf" ]; then
            template="$usrcfd/tpl/$vrmt.stf"
        fi

        # Create virtual machine configuration.
        if [ -e "$template" ]; then
            mkdir -p "$usrcfd/sys/$name"
            macGen "$vrmt"
            cat "$template" \
                | sed "s|GUEST_NAME|$name|g" \
                | sed "s|GUEST_DESC|$desc|g" \
                | sed "s|GUEST_OS|$guestos|g" \
                | sed "s|GUEST_NUMVCPU|$numvcpu|g" \
                | sed "s|GUEST_MEMSIZE|$memsizeInKiB|g" \
                | sed "s|GUEST_MACADDR|$macaddr|g" \
                > "$usrcfd/sys/$name/$name.xml"
            mkdir "$usrcfd/tmp/session.create.$name"
            cp "$usrcfd/sys/$name/$name.xml" "$usrcfd/tmp/session.create.$name"
            (source="$usrcfd/tmp/session.create.$name/$name.xml" target="$vmdata/qemu" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler sendasadmin) 2>/dev/null
            rm -f "$usrcfd/tmp/session.create.$name/$name.xml"
            rmdir "$usrcfd/tmp/session.create.$name"
            (command="\"$vmhome/qemu-img\" create -f qcow2 \"$vmdata/images/$name.img\" ${dsksize}G" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            (command="\"$vmhome/virsh\" define \"$vmdata/qemu/$name.xml\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="created"
        else
            reportError "Config template for $vrmt style guest not found: need either $usrcfd/tpl/$vrmt.stf or $syscfd/tpl/$vrmt.stf"
            return 1
        fi
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            if [ "$state" = "off" ]; then
                printf "You are about to delete and destroy $name. Are you sure? (y|n): "
                read answer
                if [ "$answer" = "y" -o "$answer" = "Y" ]; then
                    (command="\"$vmhome/virsh\" undefine \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
                    (command="rm -f \"$vmdata/qemu/$name.xml\" \"$vmdata/qemu/$name.log*\" \"$vmdata/images/$name.img\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
                    state="destroyed"
                else
                    state="saved"
                fi
            fi
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# hpvmVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle HPVM virtual machine related commands.
#
function hpvmVirtHandler {
    reportDebugFuncEntry "$*" "name host state destroy"

    typeset result

    case "$1" in
      state)
        result="$(command="/opt/hpvm/bin/hpvmstatus -P \"$name\" | grep \"^$name\" | awk '{print \$4}'" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin)"
        case "$result" in
          *On*)
            vmstate="active"
            ;;
          *Off*)
            vmstate="inactive"
            ;;
          *)
            vmstate="non-existing"
            ;;
        esac
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command="/opt/hpvm/bin/hpvmstart -P \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="booting"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        reportError "Haven't implemented $1 yet"
        return 1
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            reportError "Haven't implemented $1 yet"
            return 1
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# xenVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle XEN virtual machine related commands.
#
function xenVirtHandler {
    reportDebugFuncEntry "$*" "name host state destroy"

    typeset result

    case "$1" in
      state)
        # FIXME: Parse the fifth (state) column to determine the state
        result="$(command="xm list \"$name\" | grep \"^$name\" | awk '{print \$1}'" ; parseEntry "$host" ; checkState ;${exmt}ExecHandler runasadmin)"
        if [ "$result" ]; then
            vmstate="active"
        else
            vmstate="non-existing"
        fi
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command="xm create \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="booting"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        reportError "Haven't implemented $1 yet"
        return 1
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            reportError "Haven't implemented $1 yet"
            return 1
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# vboxVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle VirtualBox virtual machine related commands.
#
function vboxVirtHandler {
    reportDebugFuncEntry "$*" "name host state destroy"

    typeset result

    case "$1" in
      state)
        result="$(command="\"$vmhome/VboxManage\" list runningvms" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser)"
        case "$result" in
          *"$name"*)
            vmstate="active"
            ;;
          *)
            (command="\"$vmhome/VboxManage\" showvminfo \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
            if [ "$?" = 0 ]; then
                vmstate="inactive"
            else
                vmstate="non-existing"
            fi
            ;;
        esac
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command="\"$vmhome/VboxManage\" startvm \"$name\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
            state="booting"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        reportError "Haven't implemented $1 yet"
        return 1
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            reportError "Haven't implemented $1 yet"
            return 1
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# vmwVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle VMware Workstation virtual machine related commands.
#
function vmwVirtHandler {
    reportDebugFuncEntry "$*" "host name state vrmt memsize destroy"

    typeset result
    typeset template
    typeset answer

    case "$1" in
      state)
        result="$(command="\"$vmhome/vmrun\" list" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser)"
        case "$result" in
          *"$name"*)
            vmstate="active"
            ;;
          *)
            result="$(command="\"$vmhome/vmrun\" listSnapshots \"$vmdata/$name/$name.vmx\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser)"
            case "$result" in
              *"Error: Cannot open VM"*)
                vmstate="non-existing"
                ;;
              *)
                vmstate="inactive"
                ;;
            esac
            ;;
        esac
        ;;
      start)
        if [ "$state" = "off" ]; then
            result=$(command="\"$vmhome/vmrun\" start \"$vmdata/$name/$name.vmx\" nogui" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser)
            case "$result" in
              *"Error: The file is already in use"*)
                reportError "Failed to start $name because it is in-use. Try closing $name in your gui"
                state="aborted"
                ;;
              *)
                state="booting"
                ;;
            esac
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        # Check for global template, override with local if exists.
        if [ -e "$syscfd/tpl/$vrmt.stf" ]; then
            template="$syscfd/tpl/$vrmt.stf"
        fi
        if [ -e "$usrcfd/tpl/$vrmt.stf" ]; then
            template="$usrcfd/tpl/$vrmt.stf"
        fi

        # Create virtual machine configuration
        if [ -e "$template" ]; then
            mkdir -p "$usrcfd/sys/$name"
            macGen "$vrmt"
            cat "$template" \
                | sed "s|GUEST_NAME|$name|g" \
                | sed "s|GUEST_DESC|$desc|g" \
                | sed "s|GUEST_OS|$guestos|g" \
                | sed "s|GUEST_NUMVCPU|$numvcpu|g" \
                | sed "s|GUEST_MEMSIZE|$memsize|g" \
                | sed "s|GUEST_MACADDR|$macaddr|g" \
                > "$usrcfd/sys/$name/$name.vmx"
            mkdir "$usrcfd/tmp/session.create.$name"
            cp "$usrcfd/sys/$name/$name.vmx" "$usrcfd/tmp/session.create.$name"
            (source="$usrcfd/tmp/session.create.$name" target="$vmdata/$name" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler sendasuser) 2>/dev/null
            rm -f "$usrcfd/tmp/session.create.$name/$name.vmx"
            rmdir "$usrcfd/tmp/session.create.$name"
            (command="\"$vmhome/vmware-vdiskmanager\" -c -s ${dsksize}GB -a ide -t 0 \"$vmdata/$name/$name.vmdk\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
            state="created"
        else
            reportError "Config template for $vrmt style guest not found: need either $usrcfd/tpl/$vrmt.stf or $syscfd/tpl/$vrmt.stf"
            return 1
        fi
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            if [ "$state" = "off" ]; then
                printf "You are about to delete and destroy $name. Are you sure? (y|n): "
                read answer
                if [ "$answer" = "y" -o "$answer" = "Y" ]; then
                    (command="\"$vmhome/vmrun\" deleteVM \"$vmdata/$name/$name.vmx\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
                    state="destroyed"
                else
                    state="saved"
                fi
            fi
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# vmfVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle VMware Fusion virtual machine related commands.
#
function vmfVirtHandler {
    reportDebugFuncEntry "$*" "name host state vrmt memsize destroy"

    typeset result
    typeset template
    typeset answer

    case "$1" in
      state)
        result="$(command="\"$vmhome/vmrun\" list" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser)"
        case "$result" in
          *"$name"*)
            vmstate="active"
            ;;
          *)
            result="$(command="\"$vmhome/vmrun\" listSnapshots \"$vmdata/$name.vmwarevm/$name.vmx\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser)"
            case "$result" in
              *"Error: Cannot open VM"*)
                vmstate="inactive"
                ;;
              *)
                vmstate="non-existing"
                ;;
            esac
            ;;
        esac
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command="\"$vmhome/vmrun\" start \"$vmdata/$name.vmwarevm/$name.vmx\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
            state="booting"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        # Check for global template, override with local if exists.
        if [ -e "$syscfd/tpl/$vrmt.stf" ]; then
            template="$syscfd/tpl/$vrmt.stf"
        fi
        if [ -e "$usrcfd/tpl/$vrmt.stf" ]; then
            template="$usrcfd/tpl/$vrmt.stf"
        fi

        # Create virtual machine configuration
        if [ -e "$template" ]; then
            mkdir -p "$usrcfd/sys/$name"
            macGen "$vrmt"
            cat "$template" \
                | sed "s|GUEST_NAME|$name|g" \
                | sed "s|GUEST_DESC|$desc|g" \
                | sed "s|GUEST_OS|$guestos|g" \
                | sed "s|GUEST_NUMVCPU|$numvcpu|g" \
                | sed "s|GUEST_MEMSIZE|$memsize|g" \
                | sed "s|GUEST_MACADDR|$macaddr|g" \
                > "$usrcfd/sys/$name/$name.vmx"
            mkdir "$usrcfd/tmp/session.create.$name"
            cp "$usrcfd/sys/$name/$name.vmx" "$usrcfd/tmp/session.create.$name"
            (source="$usrcfd/tmp/session.create.$name" target="$vmdata/$name" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler sendasuser) 2>/dev/null
            rm -f "$usrcfd/tmp/session.create.$name/$name.vmx"
            rmdir "$usrcfd/tmp/session.create.$name"
            (command="\"$vmhome/vmware-vdiskmanager\" -c -s ${dsksize}GB -a ide -t 0 \"$vmdata/$name.vmwarevm/$name.vmdk\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
            state="created"
        else
            reportError "Config template for $vrmt style guest not found: need either $usrcfd/tpl/$vrmt.stf or $syscfd/tpl/$vrmt.stf"
            return 1
        fi
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            if [ "$state" = "off" ]; then
                printf "You are about to delete and destroy $name. Are you sure? (y|n): "
                read answer
                if [ "$answer" = "y" -o "$answer" = "Y" ]; then
                    (command="\"$vmhome/vmrun\" deleteVM \"$vmdata/$name.vmwarevm/$name.vmx\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasuser) 2>/dev/null
                    state="destroyed"
                else
                    state="saved"
                fi
            fi
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# esxVirtHandler(state|start|stop|restart|create|destroy)
# Sets vmstate, runs commands.
#
# Handle VMware ESX(i) virtual machine related commands.
#
function esxVirtHandler {
    reportDebugFuncEntry "$*" "name host state vrmt memsize destroy"

    typeset result
    typeset template
    typeset answer

    case "$1" in
      state)
        result="$(command='vim-cmd vmsvc/power.getstate $(vim-cmd vmsvc/getallvms | grep '"$name"' | cut -f 1) | grep Powered' ; parseEntry "$host" ; checkState ;  ${exmt}ExecHandler runasadmin)"
        case "$result" in
          *"Powered on"*)
            vmstate="active"
            ;;
          *"Powered off"*)
            vmstate="inactive"
            ;;
          *)
            vmstate="non-existing"
            ;;
        esac
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command='vim-cmd vmsvc/power.on $(vim-cmd vmsvc/getallvms | grep '"$name"' | cut -f 1)' ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="booting"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$osstop" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="stopping"
        fi
        ;;
      restart)
        if [ "$state" = "on" ]; then
            (command="$osreboot" ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="restarting"
        fi
        ;;
      create)
        # Check for global template, override with local if exists.
        if [ -e "$syscfd/tpl/$vrmt.stf" ]; then
            template="$syscfd/tpl/$vrmt.stf"
        fi
        if [ -e "$usrcfd/tpl/$vrmt.stf" ]; then
            template="$usrcfd/tpl/$vrmt.stf"
        fi

        # Create virtual machine configuration
        if [ -e "$template" ]; then
            mkdir -p "$usrcfd/sys/$name"
            macGen "$vrmt"
            cat "$template" \
                | sed "s|GUEST_NAME|$name|g" \
                | sed "s|GUEST_DESC|$desc|g" \
                | sed "s|GUEST_OS|$guestos|g" \
                | sed "s|GUEST_NUMVCPU|$numvcpu|g" \
                | sed "s|GUEST_MEMSIZE|$memsize|g" \
                | sed "s|GUEST_MACADDR|$macaddr|g" \
                > "$usrcfd/sys/$name/$name.vmx"
            mkdir "$usrcfd/tmp/session.create.$name"
            cp "$usrcfd/sys/$name/$name.vmx" "$usrcfd/tmp/session.create.$name"
            (source="$usrcfd/tmp/session.create.$name" target="$vmdata/$name" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler sendasadmin) 2>/dev/null
            rm -f "$usrcfd/tmp/session.create.$name/$name.vmx"
            rmdir "$usrcfd/tmp/session.create.$name"
            (command="/sbin/vmkfstools -c ${dsksize}G \"$vmdata/$name/$name.vmdk\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            (command="vim-cmd solo/registervm \"$vmdata/$name/$name.vmx\"" ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
            state="created"
        else
            reportError "Config template for $vrmt style guest not found: need either $usrcfd/tpl/$vrmt.stf or $syscfd/tpl/$vrmt.stf"
            return 1
        fi
        ;;
      destroy)
        if [ "$destroy" = "true" ]; then
            if [ "$state" = "off" ]; then
                printf "You are about to delete and destroy $name. Are you sure? (y|n): "
                read answer
                if [ "$answer" = "y" -o "$answer" = "Y" ]; then
                    (command='vim-cmd vmsvc/destroy $(vim-cmd vmsvc/getallvms | grep '"$name"' | cut -f 1)' ; parseEntry "$host" ; checkState ; ${exmt}ExecHandler runasadmin) 2>/dev/null
                    state="destroyed"
                else
                    state="saved"
                fi
            fi
        else
            reportError "Destroy is disabled"
            return 1
        fi
        ;;
    esac

    return 0
}

# noneServiceHandler(state|start|stop|restart)
# Sets svstate, runs commands.
#
# Handle services that are of type none.
#
function noneServiceHandler {
    reportDebugFuncEntry "$*"

    case "$1" in
      state)
        svstate="none"
        svrunning="none"
        svlistening="none"
        ;;
      start|stop|restart)
        reportError "No service method defined for this service"
        return 1
        ;;
    esac

    return 0
}

# scriptedServiceHandler(state|start|stop|restart)
# Sets svstate, svrunning, svlistening, runs commands.
#
# Handle regular services with svstatus, svstart and svstop.
#
function scriptedServiceHandler {
    reportDebugFuncEntry "$*" "name addr port state svstatus oslisten"

    typeset result
    typeset retval

    osGlobals
    if [ ! "$svstatus" -a ! "$oslisten" ]; then
        reportError "Neither svstatus nor oslisten is set for $name"
        return 1
    fi

    case "$1" in
      state)
        # Set initial svstate.
        svstate="unset"
        svrunning="unset"
        svlistening="unset"

        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "open" ]; then
            reportDebug "Service is listening on $addr:$port"
            svstate="active"
        else
            reportDebug "Service is not directly reachable, determine svrunning by sending svstatus command to $host"
            result="$(command="$svstatus" ; ${exmt}ExecHandler runasservice)"
            retval="$?" # from ExecHandler command
            if [ "$retval" != 0 ]; then
                reportDebug "Got return $retval from ${exmt}ExecHandler (on $name)"
                svstate="unknown"
                svrunning="unknown"
            fi
            if [ ! "$result" ]; then
                reportDebug "Service got no result from svstatus command, svrunning is inactive: $result"
                svstate="inactive"
                svrunning="inactive"
            elif [ "$result" ]; then
                reportDebug "Service got result from svstatus command, svrunning is active"
                svstate="active"
                svrunning="active"
            fi

            # And svlistening, if we have addr and port.
            if [ "$addr" != "none" -a "$port" != "none" ]; then
                reportDebug "Service is not directly reachable, determine svlistening by sending oslisten command to $host"
                result="$(command="$oslisten" ; ${exmt}ExecHandler runasservice)"
                retval="$?" # from ExecHandler command
                if [ "$retval" != 0 ]; then
                    reportDebug "Got return $retval from ${exmt}ExecHandler (on $name)"
                    svstate="unknown"
                    svlistening="unknown"
                fi
                if [ ! "$result" ]; then
                    reportDebug "Service got no result from oslisten command, svlistening is inactive"
                    svstate="inactive"
                    svlistening="inactive"
                elif [ "$result" ]; then
                    reportDebug "Service got result from oslisten command, svlistening is active"
                    svstate="active"
                    svlistening="active"
                fi
            fi
        fi
        ;;
      start)
        if [ "$state" = "off" ]; then
            (command="$svstart" ; ${exmt}ExecHandler runasservice) 2>/dev/null
            state="starting"
        else
            reportInfo "State of service on $name was not off but $state"
        fi
        ;;
      stop)
        if [ "$state" = "on" ]; then
            (command="$svstop" ; ${exmt}ExecHandler runasservice) 2>/dev/null
            state="stopping"
        else
            reportInfo "State of service on $name was not on but $state"
        fi
        ;;
      restart)
        scriptedServiceHandler state
        scriptedServiceHandler stop
        scriptedServiceHandler state
        scriptedServiceHandler start
        ;;
    esac

    return 0
}

# waitForDialog(<triggerProcess> <dialogProcess>)
# Waits.
#
# Waits for a dialog to close.
#
function waitForDialog {
    reportDebugFuncEntry "$*"

    typeset triggerProcess="$1"
    typeset dialogProcess="$2"
    typeset promptActive
    typeset detected

    [ "$triggerProcess" ] || { reportError "No trigger process specified" ; return 1 ; }

    if [ "$dialogProcess" ]; then
        detected="$($pslist | grep "$triggerProcess" | grep -v grep)"
        if [ "$detected" ]; then
            reportDebug "Detected $triggerProcess"
            sleep 1
            promptActive="$($pslist | grep "$dialogProcess" | grep -v grep)"
            while [ "$promptActive" ]; do
                promptActive="$($pslist | grep "$dialogProcess" | grep -v grep)"
                reportDebug "Waiting for $dialogProcess to close"
                sleep 1
            done
        fi
    else
        reportDebug "No dialog process specified to wait for, simply waiting for 10 seconds"
        sleep 10
    fi
}

# puttyTerminalHandler(<protocol>)
# Writes command string, runs command.
#
# Handle PuTTY terminal parameterization.
#
function puttyTerminalHandler {
    reportDebugFuncEntry "$*" "name addr sshopts xsasuser"

    typeset protocol="$1"
    typeset command
    typeset wrapped

    if [ "$protocol" = "ssh" ]; then
        command="putty $sshopts $xsasuser@$addr"
    elif [ "$protocol" = "tel" ]; then
        command="putty -telnet $addr"
    else
        reportError "Invalid protocol type passed: $protocol; valid types are: tel ssh"
        return 1
    fi

    if [ "$titling" ]; then
        wrapped="\
        reg delete \"HKEY_CURRENT_USER\\Software\\SimonTatham\\PuTTY\\Sessions\\Default%%20Settings\" /v \"WinTitle\" /f > nul 2>&1
        reg add \"HKEY_CURRENT_USER\\Software\\SimonTatham\\PuTTY\\Sessions\\Default%%20Settings\" /t REG_SZ /v \"WinTitle\" /d \"$(capsFirst "$name")\" > nul 2>&1
        start /b $command
        echo wscript.sleep 1000 > \"%TEMP%\\wait.vbs\"
        wscript.exe \"%TEMP%\\wait.vbs\"
        del \"%TEMP%\\wait.vbs\"
        reg delete \"HKEY_CURRENT_USER\\Software\\SimonTatham\\PuTTY\\Sessions\\Default%%20Settings\" /v \"WinTitle\" /f > nul 2>&1
        reg add \"HKEY_CURRENT_USER\\Software\\SimonTatham\\PuTTY\\Sessions\\Default%%20Settings\" /t REG_SZ /v \"WinTitle\" > nul 2>&1
        "
    else
        wrapped="start /b $command"
    fi

    viaScript "$(localTellCommandWriter "$wrapped")"

    return 0
}

# appleTerminalHandler(<protocol>)
# Runs command.
#
# Handle Apple Terminal parameterization.
#
function appleTerminalHandler {
    reportDebugFuncEntry "$*" "name addr sshopts xsasuser"

    typeset protocol="$1"
    typeset command
    typeset title="$(capsFirst "$name")"

    if [ "$protocol" = "ssh" ]; then
        command="ssh $sshopts -Y $xsasuser@$addr"
    elif [ "$protocol" = "tel" ]; then
        command="telnet $addr"
    else
        reportError "Invalid protocol type passed: $protocol; valid types are: tel ssh"
        return 1
    fi

    # Handle tab creation and run connection command.
    printf "activate application \"Terminal\"\n" > "$usrcfd/tmp/session.access.$name.scpt"
    printf "tell application \"Terminal\"\n" >> "$usrcfd/tmp/session.access.$name.scpt"
    printf "tell application \"System Events\" to tell process \"Terminal\" to keystroke \"t\" using command down\n" >> "$usrcfd/tmp/session.access.$name.scpt"
    printf "do script \"$command\" in last tab of front window\n" >> "$usrcfd/tmp/session.access.$name.scpt"
    printf "end tell\n" >> "$usrcfd/tmp/session.access.$name.scpt"
    osascript "$usrcfd/tmp/session.access.$name.scpt"
    rm "$usrcfd/tmp/session.access.$name.scpt"

    # If Keychain is running, check to see if a prompt has started, wait for it to close.
    waitForDialog "securityd" "SecurityAgent"

    # Set title (only if sshkey is set and agent enabled, due to non-interactivity requirement).
    if [ "$titling" -a "$sshkey" -a "$agent" ]; then
        printf 'activate application "Terminal"'\n > "$usrcfd/tmp/session.title.$name.scpt"
        printf 'tell application "System Events" to tell process "Terminal" to keystroke "PS1=\"[\\u@\\h \\W]\\$ \" ; PROMPT_COMMAND="'\n >> "$usrcfd/tmp/session.title.$name.scpt"
        printf "tell application \"System Events\" to tell process \"Terminal\" to keystroke quoted form of \"echo -ne \\\"\\\\033]0;$title\\\\007\\\"\"\n" >> "$usrcfd/tmp/session.title.$name.scpt"
        printf 'tell application "System Events" to tell process "Terminal" to keystroke return'\n >> "$usrcfd/tmp/session.title.$name.scpt"
        osascript "$usrcfd/tmp/session.title.$name.scpt"
        rm "$usrcfd/tmp/session.title.$name.scpt"
    fi

    # Return to first tab.
    osascript -e 'tell application "System Events"' -e 'keystroke "}" using command down' -e 'end tell'
}

# gnomeTerminalHandler(<protocol>)
# Runs command.
#
# Handle Gnome Terminal parameterization.
#
function gnomeTerminalHandler {
    reportDebugFuncEntry "$*" "name addr sshopts xsasuser"

    typeset protocol="$1"
    typeset title="$(capsFirst "$name")"
    typeset windowId
    typeset keyringDetected
    typeset keyringPromptActive
    typeset command


    if [ "$protocol" = "ssh" ]; then
        command="ssh $sshopts -A -Y $xsasuser@$addr"
    elif [ "$protocol" = "tel" ]; then
        command="telnet $addr"
    else
        reportError "Invalid protocol type passed: $protocol; valid types are: tel ssh"
        return 1
    fi

    pgrep -u "$userDblBacksl" gnome-terminal | grep -qv "$$"
    if [ "$?" = 0 ]; then
        # Handle tab creation and run connection command.
        windowId="$(xdotool search --class "gnome-terminal" | tail -1)"
        xdotool windowfocus "$windowId" 2>/dev/null
        xdotool key ctrl+shift+t
        sleep 0.2
        xdotool type --clearmodifiers --delay=10 "$command"
        xdotool key Return

        # If gnome-keyring-daemon is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "gnome-keyring-daemon" "gnome-keyring-prompt"

        # Set title (only if sshkey is set and agent enabled, due to non-interactivity requirement).
        if [ "$titling" -a "$sshkey" -a "$agent" ]; then
            xdotool type --clearmodifiers --delay=10 "PS1=\"[\\u@\\h \\W]\\$ \" ; PROMPT_COMMAND='echo -ne \"\\033]0;\"$title\"\\007\"'"
            xdotool key Return
        fi

        # Return to first tab.
        xdotool key ctrl+Next
    else
        gnome-terminal --command="$command" &
    fi

    return 0
}

# screenTerminalHandler(<protocol>)
# Runs command.
#
# Handle Screen Terminal parameterization.
#
function screenTerminalHandler {
    reportDebugFuncEntry "$*" "name addr sshopts xsasuser userDblBacksl TERM"

    typeset protocol="$1"
    typeset keyringDetected
    typeset keyringPromptActive
    typeset command

    if [ "$protocol" = "ssh" ]; then
        command="ssh $sshopts -Y $xsasuser@$addr"
    elif [ "$protocol" = "tel" ]; then
        command="telnet $addr"
    else
        reportError "Invalid protocol type passed: $protocol; valid types are: tel ssh"
        return 1
    fi

    typeset sesuser="$userDblBacksl"
    typeset seshost="$hostname"
    typeset sesname="session.$seshost.$sesuser"
    typeset sesruns="$(screen -ls | grep "$sesname")"

    if [ ! "$sesruns" ]; then
        reportDebug "Setting up initial screen in background"
        screen -dmS "$sesname" -t "$seshost" &
        sleep 1
        reportDebug "Creating window for $seshost (window 0)"
        screen -r -S "$sesname" -X hardstatus alwayslastline
        screen -r -S "$sesname" -X hardstatus string '%n:%t%-=%d/%m/%Y %c%{-}'
    fi

    if [ "$TERM" = "screen" ]; then
        reportDebug "Creating window for $name from within screen"
        # Warning: command has to be unquoted.
        screen -r -X screen -t "$name" $command
    else
        reportDebug "Creating window for $name from outside of screen"
        # Warning: command has to be unquoted.
        screen -r -S "$sesname" -X screen -t "$name" $command
    fi

    if [ "$platform" = "linux" ]; then
        # If gnome-keyring-daemon is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "gnome-keyring-daemon" "gnome-keyring-prompt"
    elif [ "$platform" = "macosx" ]; then
        # If Keychain is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "securityd" "SecurityAgent"
    elif [ "$platform" = "windows" ]; then
        reportDebug "No need to wait for dialog on Windows"
    else
        reportError "Unknown platform specified: $platform"
        return 1
    fi
}

# windowsBrowserHandler(<url>)
# Runs command.
#
# Handles Windows default browser parameterization.
#
function windowsBrowserHandler {
    reportDebugFuncEntry "$*"

    typeset url="$1"
    explorer "$url" &
}

# appleBrowserHandler(<url>)
# Runs command.
#
# Handles Apple default browser parameterization.
#
function macosBrowserHandler {
    reportDebugFuncEntry "$*"

    typeset url="$1"
    open "$url" &
}

# gnomeBrowserHandler(<url>)
# Runs command.
#
# Handles Gnome default browser parameterization.
#
function gnomeBrowserHandler {
    reportDebugFuncEntry "$*"

    typeset url="$1"
    open "$url" &
}

# rdpFileWriter()
# Sets rdpfile, writes to rdpfile.
#
# Creates an RDP file which can be parsed by mstsc.exe.
#
function rdpFileWriter {
    rdpfile="$usrcfd/sys/$name/$name.rdp"
    reportDebugFuncEntry "$*" "addr xsasuser name" "writing $rdpfile"

    mkdir -p "$usrcfd/sys/$name"
    printf "screen mode id:i:2\n" > "$rdpfile"
    printf "desktopwidth:i:1024\n" >> "$rdpfile"
    printf "desktopheight:i:768\n" >> "$rdpfile"
    printf "session bpp:i:24\n" >> "$rdpfile"
    printf "winposstr:s:0,1,32,68,800,572\n" >> "$rdpfile"
    printf "full address:s:$addr\n" >> "$rdpfile"
    printf "compression:i:1\n" >> "$rdpfile"
    printf "keyboardhook:i:2\n" >> "$rdpfile"
    printf "audiomode:i:2\n" >> "$rdpfile"
    printf "redirectdrives:i:0\n" >> "$rdpfile"
    printf "redirectprinters:i:0\n" >> "$rdpfile"
    printf "redirectcomports:i:0\n" >> "$rdpfile"
    printf "redirectsmartcards:i:1\n" >> "$rdpfile"
    printf "displayconnectionbar:i:1\n" >> "$rdpfile"
    printf "autoreconnection enabled:i:1\n" >> "$rdpfile"
    printf "authentication level:i:0\n" >> "$rdpfile"
    printf "username:s:$xsasuser\n" >> "$rdpfile"
    printf "domain:s:$name\n" >> "$rdpfile"
    printf "alternate shell:s:\n" >> "$rdpfile"
    printf "shell working directory:s:\n" >> "$rdpfile"
    printf "disable wallpaper:i:1\n" >> "$rdpfile"
    printf "disable full window drag:i:0\n" >> "$rdpfile"
    printf "disable menu anims:i:0\n" >> "$rdpfile"
    printf "disable themes:i:0\n" >> "$rdpfile"
    printf "disable cursor setting:i:0\n" >> "$rdpfile"
    printf "bitmapcachepersistenable:i:1\n" >> "$rdpfile"
    if [[ "$tools_access_found" =~ "cryptrdp5" ]]; then
        if [ "$xsastype" = "user" -a "$xsasupwd" ]; then
            typeset rdphash="$(cryptrdp5 "$xsasupwd")"
            printf "password 51:b:$rdphash\n" >> "$rdpfile"
        elif [ "$xsastype" = "admin" -a "$xsasapwd" ]; then
            typeset rdphash="$(cryptrdp5 "$xsasapwd")"
            printf "password 51:b:$rdphash\n" >> "$rdpfile"
        fi
    fi
    chmod 600 "$rdpfile"
}

# mstscDesktopHandler()
# Runs command.
#
# Calls rdpFileWriter and executes mstsc.
#
function mstscDesktopHandler {
    reportDebugFuncEntry "$*"

    rdpFileWriter
    mstsc "$(toLocalWindowsPath "$rdpfile")" 2>/dev/null &
}

# amsrdcDesktopHandler()
# Runs command.
#
# Calls rdpFileWriter and executes Microsoft Remote Desktop Connection.
#
function amsrdcDesktopHandler {
    reportDebugFuncEntry "$*"

    rdpFileWriter
    /Applications/Remote\ Desktop\ Connection.app/Contents/MacOS/Remote\ Desktop\ Connection "$rdpfile" 2>/dev/null &
}

# rdesktopDesktopHandler()
# Runs command.
#
# Calls rdpFileWriter and executes rdesktop.
#
function rdesktopDesktopHandler {
    reportDebugFuncEntry "$*"

    rdpFileWriter
    if [ "$titling" ]; then typeset title="-T $(capsFirst "$name")"; fi
    printf "$xsasupwd\n" | rdesktop -0 -g 1024x768 -b -B $title -u "$xsasuser" -p - -N -a 16 -z -x l -r disk:home="$HOME" "$addr" 2>/dev/null &
}

# noneAccessHandler(state|access)
# Sets acstate, runs commands.
#
# Either determine the state of, or access system without any known
# access method.
#
function noneAccessHandler {
    reportDebugFuncEntry "$*"

    case "$1" in
      state)
        acstate="none"
        ;;
      access)
        reportError "No access method defined for this system"
        return 1
        ;;
    esac

    return 0
}

# httpAccessHandler(state|access)
# Sets acstate, runs commands.
#
# Either determine the state of, or access system using the http
# protocol and the default web browser.
#
function httpAccessHandler {
    reportDebugFuncEntry "$*" "name addr port type acstate"

    typeset result
    typeset proto

    if [ "$type" = "host" -o "$type" = "guest" ]; then
        port=80
        proto="http"
        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "closed" ]; then
            port=443
            proto="https"
            result="$(printPortState "$addr" "$port")"
        fi
    elif [ "$type" = "service" ]; then
        proto="http"
        result="$(printPortState "$addr" "$port")"
    fi

    case "$1" in
      state)
        if [ "$result" = "open" ]; then
            acstate="active"
        elif [ "$result" = "closed" ]; then
            acstate="inactive"
        else
            acstate="$result"
        fi
        ;;
      access)
        if [ "$acstate" = "active" -a "$browser" != "none" ]; then
            ${browser}BrowserHandler "$proto://$addr:$port"
        elif [ "$browser" = "none" ]; then
            reportError "Browser handler was set to \"none\" in options.conf."
            return 1
        else
            reportError "Failed to access $name on $addr over $acmt (acstate=$acstate)"
            return 1
        fi
        ;;
    esac

    return 0
}

# telAccessHandler(state|access)
# Sets acstate, runs commands.
#
# Either determine the state of, or access system using the telnet protocol
# and the terminal handler specified in options.conf.
#
function telAccessHandler {
    reportDebugFuncEntry "$*" "name addr acstate terminal"

    typeset result
    typeset port=23

    case "$1" in
      state)
        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "open" ]; then
            acstate="active"
        elif [ "$result" = "closed" ]; then
            acstate="inactive"
        else
            acstate="$result"
        fi
        ;;
      access)
        if [ "$acstate" = "active" -a "$terminal" != "none" ]; then
            ${terminal}TerminalHandler tel
        elif [ "$terminal" = "none" ]; then
            reportError "Terminal handler was set to \"none\" in options.conf."
            return 1
        else
            reportError "Failed to access $name on $addr over $acmt (acstate=$acstate)"
            return 1
        fi
        ;;
    esac

    return 0
}

# sshAccessHandler(state|access)
# Sets acstate, runs commands.
#
# Either determine the state of, or access system using the ssh protocol
# and the terminal handler specified in options.conf.
#
function sshAccessHandler {
    reportDebugFuncEntry "$*" "name addr acstate terminal"

    typeset result
    typeset port=22

    case "$1" in
      state)
        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "open" ]; then
            acstate="active"
        elif [ "$result" = "closed" ]; then
            acstate="inactive"
        else
            acstate="$result"
        fi
        ;;
      access)
        if [ "$acstate" = "active" ]; then
            ${terminal}TerminalHandler ssh
         elif [ "$terminal" = "none" ]; then
            reportError "Terminal handler was set to \"none\" in options.conf."
            return 1
        else
            reportError "Failed to access $name on $addr over $acmt (acstate=$acstate)"
            return 1
        fi
        ;;
    esac

    return 0
}

# rdpAccessHandler(state|access)
# Sets acstate, runs commands.
#
# Either determine the state of, or access system using the rdp protocol
# and the desktop handler specified in options.conf.
#
function rdpAccessHandler {
    reportDebugFuncEntry "$*" "name addr acstate desktop"

    typeset result
    typeset port=3389

    case "$1" in
      state)
        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "open" ]; then
            acstate="active"
        elif [ "$result" = "closed" ]; then
            acstate="inactive"
        else
            acstate="$result"
        fi
        ;;
      access)
        if [ "$acstate" = "active" -a "$desktop" != "none" ] && { ! isLoopback "$addr" || ! isLocal "$name" ; } ; then
            ${desktop}DesktopHandler
        elif [ "$desktop" = "none" ]; then
            reportError "Desktop handler was set to \"none\" in options.conf."
            return 1
        else
            reportError "Failed to access $name on $addr over $acmt (acstate=$acstate)"
            return 1
        fi
        ;;
    esac

    return 0
}

# noneExecHandler(state|runasuser|runasadmin|runasservice|sendasuser|sendasadmin|sendasservice)
# Sets exstate, runs commands.
#
# Handle execute commands for systems without any known execute method.
#
function noneExecHandler {
    reportDebugFuncEntry "$*"

    case "$1" in
      state)
        exstate="none"
        ;;
      runasuser|runasadmin|runasservice|sendasuser|sendasadmin|sendasservice)
        reportError "No execute method defined for this system"
        return 1
        ;;
    esac

    return 0
}

# smbExecHandler(state|runasuser|runasadmin|runasservice|sendasuser|sendasadmin|sendasservice)
# Sets exstate, runs commands.
#
# Handle executing commands on a remote system using the smb/cifs protocol.
#
function smbExecHandler {
    reportDebugFuncEntry "$*"

    typeset result
    typeset port=445

    case "$1" in
      state)
        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "open" ]; then
            exstate="active"
        elif [ "$result" = "closed" ]; then
            exstate="inactive"
        else
            exstate="$result"
        fi
        ;;
      runasuser)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to execute user-level command locally (on $name)"
            viaScript "$(localTellCommandWriter "$command")"
            return $?
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to execute user-level command remotely (on $name)"
            viaScript "$(${smbtell}TellCommandWriter "$addr" "$user" "$upwd" "$command")"
            return $?
        else
            reportError "Failed to execute user-level command (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      runasadmin)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to execute admin-level command locally (on $name)"
            viaScript "$(localTellCommandWriter "$privesc $command")"
            return $?
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to execute admin-level command remotely (on $name)"
            viaScript "$(${smbtell}TellCommandWriter "$addr" "$admin" "$apwd" "$command")"
            return $?
        else
            reportError "Failed to execute admin-level command (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      runasservice)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" = "service" ]; } ; then
            reportDebug "Attempting to execute service-level command locally (on $host)"
            viaScript "$(localTellCommandWriter "$privesc $command")"
            return $?
        elif [ "$exstate" = "active" -a "$type" = "service" ]; then
            reportDebug "Attempting to execute service-level command remotely (on $host)"
            tokenReader setVars "$host" "addr" || return 1
            viaScript "$(${smbtell}TellCommandWriter "$addr" "$user" "$upwd" "$command")"
            return $?
        else
            reportError "Failed to execute service-level command (on $host) (exstate=$exstate)"
            return 1
        fi
        ;;
      sendasuser)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to send data locally at user-level (on $name)"
            viaScript "$(localSendCommandWriter "$source" "$target")"
            return $?
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to send data remotely at user-level (on $name)"
            viaScript "$(${smbsend}SendCommandWriter "$addr" "$user" "$upwd" "$source" "$target")"
            return $?
        else
            reportError "Failed to send data at user-level (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      sendasadmin)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to send data locally at admin-level (on $name)"
            reportError "Sending of typeset files with elevated privileges not implemented yet"
            return 1
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to send data remotely at admin-level (on $name)"
            viaScript "$(${smbsend}SendCommandWriter "$addr" "$admin" "$apwd" "$source" "$target")"
            return $?
        else
            reportError "Failed to send data at admin-level (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      sendasservice)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" = "service" ]; } ; then
            reportDebug "Attempting to send data locally at service-level (on $host)"
            reportError "Sending of typeset files with changed privileges not implemented yet"
            return 1
        elif [ "$exstate" = "active" -a "$type" = "service" ]; then
            reportDebug "Attempting to send data remotely at service-level (on $host)"
            tokenReader setVars "$host" "addr" || return 1
            viaScript "$(${smbsend}SendCommandWriter "$addr" "$user" "$upwd" "$source" "$target")"
            return $?
        else
            reportError "Failed to send data at service-level (on $host) (exstate=$exstate)"
            return 1
        fi
        ;;
    esac

    if [ "$platform" = "linux" ]; then
        # If gnome-keyring-daemon is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "gnome-keyring-daemon" "gnome-keyring-prompt"
    elif [ "$platform" = "macosx" ]; then
        # If Keychain is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "securityd" "SecurityAgent"
    elif [ "$platform" = "windows" ]; then
        reportDebug "No need to wait for dialog on Windows"
    else
        reportError "Unknown platform specified: $platform"
        return 1
    fi

    return 0
}

# sshExecHandler(state|runasuser|runasadmin|runasservice|sendasuser|sendasadmin|sendasservice)
# Sets exstate, runs commands.
#
# Handle executing commands using the ssh protocol.
#
function sshExecHandler {
    reportDebugFuncEntry "$*" "addr name type host exstate user admin command"

    typeset result
    typeset port=22

    case "$1" in
      state)
        result="$(printPortState "$addr" "$port")"
        if [ "$result" = "open" ]; then
            exstate="active"
        elif [ "$result" = "closed" ]; then
            exstate="inactive"
        else
            exstate="$result"
        fi
        ;;
      runasuser)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to execute user-level command locally (on $name)"
            viaScript "$(localTellCommandWriter "$command")"
            return $?
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to execute user-level command remotely (on $name)"
            viaScript "$(${sshtell}TellCommandWriter "$addr" "$user" "$command")"
            return $?
        else
            reportError "Failed to execute user-level command (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      runasadmin)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to execute admin-level command locally (on $name)"
            viaScript "$(localTellCommandWriter "$privesc $command")"
            return $?
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to execute admin-level command remotely (on $name)"
            viaScript "$(${sshtell}TellCommandWriter "$addr" "$admin" "$command")"
            return $?
        else
            reportError "Failed to execute admin-level command (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      runasservice)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" = "service" ]; } ; then
            reportDebug "Attempting to execute service-level command locally (on $host)"
            viaScript "$(localTellCommandWriter "$privesc $command")"
            return $?
        elif [ "$exstate" = "active" -a "$type" = "service" ]; then
            reportDebug "Attempting to execute service-level command remotely (on $host)"
            tokenReader setVars "$host" "addr" || return 1
            viaScript "$(${sshtell}TellCommandWriter "$addr" "$user" "$command")"
            return $?
        else
            reportError "Failed to execute service-level command (on $host) (exstate=$exstate)"
            return 1
        fi
        ;;
      sendasuser)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to send data locally at user-level (on $name)"
            viaScript "$(localSendCommandWriter "$source" "$target")"
            return $?
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to send data remotely at user-level (on $name)"
            viaScript "$(${sshsend}SendCommandWriter "$addr" "$user" "$source" "$target")"
            return $?
        else
            reportError "Failed to send data at user-level (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      sendasadmin)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" != "service" ]; } ; then
            reportDebug "Attempting to send data locally at admin-level (on $name)"
            reportError "Sending of typeset files with elevated privileges not implemented yet"
            return 1
        elif [ "$exstate" = "active" -a "$type" != "service" ]; then
            reportDebug "Attempting to send data remotely at admin-level (on $name)"
            viaScript "$(${sshsend}SendCommandWriter "$addr" "$admin" "$source" "$target")"
            return $?
        else
            reportError "Failed to send data at admin-level (on $name) (exstate=$exstate)"
            return 1
        fi
        ;;
      sendasservice)
        if isLoopback "$addr" || { isLocal "$name" && [ "$type" = "service" ]; } ; then
            reportDebug "Attempting to send data locally at service-level (on $host)"
            reportError "Sending of typeset files with changed privileges not implemented yet"
            return 1
        elif [ "$exstate" = "active" -a "$type" = "service" ]; then
            reportDebug "Attempting to send data remotely at service-level (on $host)"
            tokenReader setVars "$host" "addr" || return 1
            viaScript "$(${sshsend}SendCommandWriter "$addr" "$user" "$source" "$target")"
            return $?
        else
            reportError "Failed to send data at service-level (on $host) (exstate=$exstate)"
            return 1
        fi
        ;;
    esac

    if [ "$platform" = "linux" ]; then
        # If gnome-keyring-daemon is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "gnome-keyring-daemon" "gnome-keyring-prompt"
    elif [ "$platform" = "macosx" ]; then
        # If Keychain is running, check to see if a prompt has started, wait for it to close.
        waitForDialog "securityd" "SecurityAgent"
    elif [ "$platform" = "windows" ]; then
        reportDebug "No need to wait for dialog on Windows"
    else
        reportError "Unknown platform specified: $platform"
        return 1
    fi

    return 0
}

# sshCredHandler(ashost|asguest|asservice)
# Runs commands.
#
# Handle calling sshSendKey with correct username.
#
function sshCredHandler {
    reportDebugFuncEntry "$*"

    case "$1" in
      ashost|asguest)
        if [ "$user" != "none" ]; then
            reportInfo "Sending ssh key for defined user ($user) to $name"
            sshSendKey "$user"
        fi
        if [ "$admin" != "none" -a "$admin" != "$user" ]; then
            reportInfo "Sending ssh key for defined admin ($admin) to $name"
            sshSendKey "$admin"
        fi
        ;;
      asservice)
        if [ "$user" != "none" ]; then
            reportInfo "Sending ssh key for defined service user ($user) to $host"
            sshSendKey "$user"
        fi
        ;;
    esac
}

# winCredHandler(ashost|asguest|asservice)
# Runs commands.
#
# Handle calling winStoreCreds with correct username.
#
function winCredHandler {
    reportDebugFuncEntry "$*"

    case "$1" in
      ashost|asguest)
        if [ "$user" != "none" ]; then
            reportInfo "Storing credentials for defined user ($user) on $name"
            winStoreCreds user "$user"
        fi
        if [ "$admin" != "none" -a "$admin" != "$user" ]; then
            reportInfo "Storing credentials for defined admin ($admin) on $name"
            winStoreCreds admin "$admin"
        fi
        ;;
      asservice)
        if [ "$user" != "none" ]; then
            reportInfo "Storing credentials for defined service user ($user) on $host"
            winStoreCreds user "$user"
        fi
        ;;
    esac
}


# doFunction(<function>)
# Calls functions, returns exit code.
#
# Call <function>
#
function doFunction {
    typeset runnable="$1"
    typeset retval
    shift
    typeset IFS=" "
    $runnable "$@" || { retval="$?" ; reportDebug "Function $runnable returned status $retval" ; return $retval ; }
    return 0
}

# dynamicHandler(<name>)
# Calls functions, sets variables.
#
# Checks if a given name is dynamic, creates a group object if so. Passes along if not.
#
function dynamicHandler {
    reportDebugFuncEntry "$*"

    typeset entity="$1"
    typeset dynamicGroups="all,host,hosts,guest,guests,service,services,group,groups"

    # Sed statements in the member variables remove any whitespace around commas, then
    # continue to remove a leading comma, and then finish with removal of trailing comma.

    if [[ "$entity" =~ "," ]]; then
        reportDebug "Dynamic group: comma detected on entity specification ($entity), initializing"
        type="group"
        name="comma-detected-dynamic-group"
        members=$(echo $entity | sed -e 's/\s*,\s*/,/g' -e "s|^,||g" -e "s|,$||g")
    elif [[ "$dynamicGroups" =~ "$entity" ]]; then
        reportDebug "Dynamic group: entity matched dynamic group name ($entity), initializing"
        type="group"
        name="$entity"
        members=$(for item in $(listHelper $name); do printf "$item", ; done | sed -e "s|^,||g" -e "s|,$||g")
    else
        reportDebug "Not a dynamic group: $entity, passing to parseEntry"
        parseEntry "$entity"
    fi

    # If parent is set, return a dynamic group with the parent host(s) as members.
    if [ "$parent" = "true" ]; then
        reportDebug "Dynamic group: parent(s) requested, creating dynamic group of parents"
        parentHandler "$entity"
    fi
}

function parentHandler {
    reportDebugFuncEntry "$*"

    # Avoid entering the loop twice (called from dynamicHandler).
    unset parent

    typeset entity="$1"
    typeset parents
    typeset memberType

    if [ "$type" = "group" ]; then
        # We're talking about a group, need to discover members and make a unique list of parents.

        typeset IFS=","
        for member in $members; do
            memberType=$(tokenReader printVals "$member" "type")
            if [ "$memberType" = "group" ]; then
                # Tell the user we do not support nested groups for parent lookup.
                reportError "Type of group member \"$member\" is \"group\"."
                reportError "Group recursion for parents not supported yet."
            elif [ "$memberType" = "host" ]; then
                # Tell the user we encountered a host which by definition has no parent.
                reportError "Type of group member \"$member\" is \"host\" so has no parent."
            elif [ "$memberType" = "guest" -o "$memberType" = "service" ]; then
                # We encountered a guest or service which has a parent.
                parent=$(tokenReader printVals "$member" "host")
            fi

            if [[ ! "$parents" =~ "$parent" ]]; then
                # Uniquely prepend the parent to the parents list.
                parents="$parent,$parents"
            fi
        done

        # The parents list becomes the new members list, set a name for this dynamic group.
        name="parent-dynamic-group"
        members=$(echo "$parents" | sed -e "s|^,||g" -e "s|,$||g")
        if [ -z "$members" ]; then
            reportError "No parents found for \"$entity\"."
            return 1
        fi

    elif [ "$type" = "host" ]; then
        # We're talking about a host, which has no parent. Error.
        reportError "Type of  \"$entity\" is \"host\" so has no parent."
    elif [ "$type" = "guest" -o "$type" = "service" ]; then
        # We're talking about a guest or a service, lookup the parent.
        type="group"
        name="parent-dynamic-group"
        members=$(tokenReader printVals "$entity" "host")
    else
        # Something weird happened, tell us about it.
        reportError "Type not set or unknown: \"$type\""
        reportError "This indicates we were called outside of dynamicHandler or parseEntry."
        return 1
    fi
}

# mapEntity(<entity> <comma_separated_list_of_functions>)
# Calls functions.
#
# Execute the members of the <comma_separated_list_of_functions> sequentially passing <entity> to each one.
#
function mapEntity {
    reportDebugFuncEntry "$*" "mode"

    typeset entity="$1"
    typeset functions="$2"
    typeset targetState
    typeset currentState
    typeset -i retval

    # Set the targetState (for using stateful mode).
    if [[ "$functions" =~ "stop" ]]; then targetState="off" ; else targetState="on" ; fi
    reportDebug "Target state is: $targetState"

    # Execute all requested functions.
    typeset IFS=","
    for runnable in $functions ; do
        reportDebug "Running function: $runnable"
        doFunction "$runnable" "$entity"
        retval="$?"
    done

    # If mode is stateful, wait for targetState to be reached.
    if [ "$mode" = "stateful" ]; then
        reportDebug "Mode is stateful, initializing current state as: unknown"
        currentState="unknown"
        while [ "$currentState" != "$targetState" ]; do
            currentState="$(IFS=" " ; checkState ; printf "$state\n")"
            if [ "$currentState" != "$targetState" ]; then
                report "$entity: waiting to go $targetState (currently $currentState)"
                sleep 1
            else
                state="$currentState"
                printState "$entity"
                retval="$?"
            fi
        done
    fi

    return "$retval"
}


# mapGroup(<members> <comma_separated_list_of_functions>)
# Calls functions.
#
# Iterate over members and pass back to mapEntryPoint.
#
function mapGroup {
    reportDebugFuncEntry "$*" "mode"

    typeset members="$1"
    typeset functions="$2"
    typeset targetState
    typeset index
    typeset item
    typeset groupType
    typeset groupName
    typeset groupList
    typeset -i retval

    # Save group's type and name.
    groupType="$type"
    groupName="$name"

    # If stop funtions are in the function list, reverse order.
    # Set the targetState (for using stateful mode), reverse order.
    typeset IFS=","
    if [[ "$functions" =~ "stop" ]]; then
        targetState="off"
        groupList=""
        for member in $members ; do
            groupList="$member${groupList:+,$groupList}"
        done
        reportDebug "Function list contains stop functions, reversing group list."
    else
        targetState="on"
        groupList="$members"
    fi
    reportDebug "Group list is: $groupList"
    reportDebug "Target state is: $targetState"

    # Return value is the bitwise OR of all function return values
    retval=0

    let index=1
    IFS=","
    for item in $groupList ; do
        reportDebug "Recursing $item to mapEntryPoint"
        mapEntryPoint "$item" "$functions"
        let retval=$((retval|$?))
        let index+=1
    done

    # Restore group's type and name (which might have been overwritten by previous mapEntryPoint).
    type="$groupType"
    name="$groupName"

    return "$retval"
}

# mapEntryPoint(<entity> <space_separated_list_of_functions>)
# Calls functions.
#
# Execute the members of the <comma_separated_list_of_functions> sequentially passing <entity> to each one.
# If an entity is a group, pass to mapGroup, if it's a host/guest/service pass to mapEntity
#
function mapEntryPoint {
    reportDebugFuncEntry "$*"

    typeset entity="$1"
    typeset functions="$2"
    typeset -i retval

    # See if entityOrigin was set, set only when mapEntryPoint runs for the first time.
    if [ ! "$entityOrigin" ]; then entityOrigin="$entity"; fi

    # Reading in the entity entry is done via dynamicHandler. Here entity globals are set!
    dynamicHandler "$entity"

    # If a group, call mapGroup (which will recurse back to us). Call mapEntity otherwise.
    if [ "$type" = "group" ]; then
        reportDebug "Received a group, passing on to mapGroup"
        mapGroup "$members" "$functions"
        retval="$?"
    else
        reportDebug "Received an entity , passing to mapEntity"
        mapEntity "$entity" "$functions"
        retval="$?"
    fi

    # Do certain access related things when mapEntryPoint loop exits.
    if [[ "$functions" =~ "accessEntity" ]]; then
        reportDebug "Function list contains access functions, calling terminal exit hooks."
        # Reconnect to a previously generated screen session.
        if [ "$terminal" = "screen" -a "$TERM" != "screen" -a "$entity" = "$entityOrigin" ]; then
            reportDebug "Terminal exit hook for screen: resuming created screen session"
            sleep 1
            screen -r
        # Set the title of the local (first) window.
        elif [ "$terminal" = "gnome" -a "$titling" -a "$sshkey" -a "$agent" ]; then
            reportDebug "Terminal exit hook for gnome: titling typeset terminal screen"
            typeset title="$(capsFirst "$hostname")"
            sleep 1
            xdotool key alt+1
            xdotool type --clearmodifiers --delay=10 "PS1=\"[\\u@\\h \\W]\\$ \" ; PROMPT_COMMAND='echo -ne \"\\033]0;\"$title\"\\007\"'"
            xdotool key Return
        # Set the title of the local (first) window.
        elif [ "$terminal" = "apple" -a "$titling" -a "$sshkey" -a "$agent" ]; then
            reportDebug "Terminal exit hook for apple: titling typeset terminal screen"
            typeset title="$(capsFirst "$hostname")"
            printf 'activate application "Terminal"'\n > "$usrcfd/tmp/session.title.$hostname.scpt"
            printf 'tell application "System Events" to tell process "Terminal" to keystroke "PS1=\"[\\u@\\h \\W]\\$ \" ; PROMPT_COMMAND="'\n >> "$usrcfd/tmp/session.title.$hostname.scpt"
            printf "tell application \"System Events\" to tell process \"Terminal\" to keystroke quoted form of \"echo -ne \\\"\\\\033]0;$title\\\\007\\\"\"\n" >> "$usrcfd/tmp/session.title.$hostname.scpt"
            printf 'tell application "System Events" to tell process "Terminal" to keystroke return'\n >> "$usrcfd/tmp/session.title.$hostname.scpt"
            osascript "$usrcfd/tmp/session.title.$hostname.scpt"
            rm "$usrcfd/tmp/session.title.$hostname.scpt"
        fi
    fi

    return "$retval"
}

# printUsageText()
# Prints.
#
# Prints the standard help message for session.
#
function printUsageText {
    reportDebugFuncEntry "$*"

    printf "%s\n" "\
    Usage: $0 command {group, guest, host or special argument}

    Commands:
    addconf     - adds a host, guest, service or group to session.conf.
    modconf     - modify an existing host, guest, service or group in session.conf.
    delconf     - removes a host, guest, service or group from session.conf.
    discover    - scan an ip or an ip subnet.
    check       - (state, status) checks state of a host, guest, service or group.
    detail      - shows all known details for an entry, pass --nocheck optionally.
    start       - attempts to start a host, guest, service or group.
    stop        - attempts to stop a host, guest, service or group.
    restart     - attempts to restart a host, guest, service or group.
    create      - attempts to create a non-existing guest system.
    destroy     - attempts to destroy an existing guest system.
    access      - access a host, guest, service or group using various methods.
    tell        - send a command to the host, guest, service or group.
    send        - send a directory to the host, guest, service or group.
    key         - send public key or store credentials for remote admin and user.
    list        - list hosts, guests, services, groups, osmt, acmt, exmt or vrmts
    reinit      - reinitializes session required and detected tools.
    version     - show session version.

    Argument to control group execution mode (for state, start, stop, etc):
    --mode      - serial (default), stateful or parallel (experimental).

    Argument to set resilient mode for (extremely) bad or slow networks:
    --resilient - make checks for state assume the worst. Warning: slow.

    Argument to make the detail command not check the current state:
    --nocheck   - (nostate, nostatus) just return all detail info directly.

    Arguments for addconf, modconf and delconf:
    --type      - the type of the added entry (host, guest, service or group).
    --name      - the name of the entry (can be used for renaming).
    --osmt      - the operating system for the host or guest system.
    --acmt      - the access method to be used.
    --exmt      - the execute method to be used.
    --svmt      - (services only) the service method to be used.
    --addr      - the ip address for the host, guest or service.
    --port      - (services only) the port on which a service listens.
    --user      - the regular user account for the host, guest or service.
    --admin     - the administrative account for the host or guest.
    --vrmt      - (optional, hosts only) the virtualization method supported.
    --vmhome    - (optional, hosts only) where the host stores vm executables.
    --vmdata    - (optional, hosts only) where the host stores virtual machines.
    --svstatus  - (optional, scripted services only) status command for a service.
    --svstart   - (optional, scripted services only)  start command for a service.
    --svstop    - (optional, scripted services only)   stop command for a service.
    --host      - (guests and services only) the parent host system.
    --members   - (groups only) a comma-separated list of hosts/guests/services.

    Arguments for privilege specification in access, tell and send:
    --user      - run the command with user credentials (default).
    --admin     - run the command with admin credentials.
    --service   - run the command with service credentials.

    Argument to talk to parent of guest or service  in access, tell and send:
    --parent    - run the command on the parent of the guest or service.

    Arguments for create and destroy:
    --desc      - (optional) annotation (--desc=\"My description.\").
    --numvcpu   - the virtual CPU count for guest system (--numvcpu=2).
    --memsize   - the virtual memory size in MB for guest system (--memsize=512).
    --dsksize   - the disk size in GB for guest system (--dsksize=4).
    --guestos   - the operating system for guest system (--guestos=other-

  Arguments for send:
    --source    - the source directory on the local system to send from.
    --target    - the target directory on the remote system to send to.

    Argument to control debug mode (can be passed to all commands):
    --debug     - pass this to enable debug mode. no value required.

    Argument to give verbose output (currently for list command only):
    --verbose   - make output show entire entry instead of name only.

    Special parameters for list:
    all         - (list and check only) show or state all.
    mode        - (list) show all modes. --default for default only.
    host        - (list) show all hosts.
    guest       - (list) show all guests.
    group       - (list) show all groups.
    service     - (list) show all services.
    acmt        - (list) show all supported access methods.
    exmt        - (list) show all supported execute methods.
    svmt        - (list) show all supported service methods.
    vrmt        - (list) show all supported virtualization methods.
    osmt        - (list) show all supported operating environments.
    crmt        - (list) show all supported credential methods.

    Notes:
    Arguments starting with a double dash sign (--) can either be without a value
    (like --nocheck) or with a value (like --mode=stateful). Session will tell you
    when arguments are mandatory or optional. Use the special paramenters for the
    list command to find out which values can be passed to certain arguments.
    " | sed 's/^[[:space:]]*//'
}

# printVersion()
# Prints.
#
# Prints the version of session.
#
function printVersion {
    reportDebugFuncEntry "$*"
    echo "RELEASE.STRING.VERSION
    RELEASE.STRING.COPYRIGHT
    RELEASE.STRING.RELDATE
    RELEASE.STRING.BUILT
    RELEASE.STRING.LICENSE" | sed 's/^[ \t]*//;s/[ \t]*$//'
}
