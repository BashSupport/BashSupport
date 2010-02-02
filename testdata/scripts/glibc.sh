#! /bin/sh -e
#
### BEGIN INIT INFO
# Provides:          glibc
# Required-Start:
# Required-Stop:
# Default-Start:     S
# Default-Stop:
### END INIT INFO
#
# 
# This script detects deprecated kernel versions incompatible with
# the current version of the glibc

# glibc kernel version check: KERNEL_VERSION_CHECK
linux_compare_versions () {
    verA=$(($(echo "$1" | sed 's/\([0-9]*\)\.\([0-9]*\)\.\([0-9]*\).*/\1 \* 10000 + \2 \* 100 + \3/')))
#    verB=$(($(echo "$3" | sed 's/\([0-9]*\)\.\([0-9]*\)\.\([0-9]*\).*/\1 \* 10000 + \2 \* 100 + \3/')))

    test $verA $2 $verB
}

kfreebsd_compare_versions () {
    verA=$(($(echo "$1" | sed 's/\([0-9]*\)\.\([0-9]*\).*/\1 \* 100 + \2/')))
    verB=$(($(echo "$3" | sed 's/\([0-9]*\)\.\([0-9]*\).*/\1 \* 100 + \2/')))

    test $verA -$2 $verB
}

kernel26_help() {
    echo ""
    echo "The installation of a 2.6 kernel _could_ ask you to install a new libc"
    echo "first, this is NOT a bug, and should *NOT* be reported. In that case,"
    echo "please add etch sources to your /etc/apt/sources.list and run:"
    echo "  apt-get install -t etch linux-image-2.6"
    echo "Then reboot into this new kernel, and proceed with your upgrade"
}

exit_check () {
    sleep 5
    exit 1
}

    system=`uname -s`
    if [ "$system" = "Linux" ]
    then
        # Test to make sure z < 255, in x.y.z-n form of kernel version
        # Also make sure we don't trip on x.y.zFOO-n form
        #kernel_rev=$(uname -r | tr -- - . | cut -d. -f3 | tr -d '[:alpha:]')
        kernel_rev=$(uname -r | sed 's/\([0-9]*\.[0-9]*\.\)\([0-9]*\)\(.*\)/\2/')
        if [ "$kernel_rev" -ge 255 ]
        then
            echo "WARNING: Your kernel version indicates a revision number"
            echo "of 255 or greater.  Glibc has a number of built in"
            echo "assumptions that this revision number is less than 255."
            echo "If you\'ve built your own kernel, please make sure that any"
            echo "custom version numbers are appended to the upstream"
            echo "kernel number with a dash or some other delimiter."

            exit_check
        fi

        # sanity checking for the appropriate kernel on each architecture.
        realarch=`uname -m`
        kernel_ver=`uname -r`

        # convert "armv4l" and similar to just "arm", and "mips64" and similar
        # to just "mips"
        case $realarch in
          arm*) realarch="arm";;
          mips*) realarch="mips";;
        esac


        # From glibc 2.3.5-7 real-i386 is dropped.
        if [ "$realarch" = i386 ]
        then
            echo "WARNING: This machine has real i386 class processor."
            echo "Debian etch and later does not support such old hardware"
            echo "any longer."
            echo "The reason is that \"bswap\" instruction is not supported"
            echo "on i386 class processors, and some core libraries have"
            echo "such instruction.  You\'ll see illegal instruction error"
            echo "when you upgrade your Debian system."
            exit_check
        fi

        # arm boxes require __ARM_NR_set_tls in the kernel to function properly.
        if [ "$realarch" = arm ]
        then
            if linux_compare_versions "$kernel_ver" lt 2.6.12
            then
                echo WARNING: This version of glibc requires that you be running
                echo kernel version 2.6.12 or later.  Earlier kernels contained
                echo "bugs that may render the system unusable if a modern version"
                echo of glibc is installed.
                kernel26_help
                exit_check
            fi	
        fi

        # Alpha and HPPA boxes require latest fixes in the kernel to function properly.
        if [ "$realarch" = parisc -o "$realarch" = alpha ]
        then
            if linux_compare_versions "$kernel_ver" lt 2.6.9
            then
                echo "WARNING: This version of glibc requires that you be running"
                echo "kernel version 2.6.9 or later.  Earlier kernels contained"
                echo "bugs that may render the system unusable if a modern version"
                echo of glibc is installed.
                kernel26_help
                exit_check
            fi
        fi

        # sh4 boxes require kernel version 2.6.11 minimum
        if [ "$realarch" = sh4 ]
        then
            if linux_compare_versions "$kernel_ver" lt 2.6.11
            then
                echo WARNING: This version of glibc requires that you be running
                echo kernel version 2.6.11 or later.  Earlier kernels contained
                echo "bugs that may render the system unusable if a modern version"
                echo of glibc is installed.
                kernel26_help
                exit_check
            fi	
        fi

        # The GNU libc requires 2.6 kernel (except on m68k) because we drop to
        # support linuxthreads
        if [ "$realarch" != m68k ]
        then
            if linux_compare_versions "$kernel_ver" lt 2.6.8
            then
                echo WARNING: POSIX threads library NPTL requires kernel version
                echo 2.6.8 or later.  If you use a kernel 2.4, please upgrade it
                echo before installing glibc.
                kernel26_help
                exit_check
            fi
        fi

        # The GNU libc is now built with --with-kernel= >= 2.4.1 on m68k
        if [ "$realarch" = m68k ]
        then
            if linux_compare_versions "$kernel_ver" lt 2.4.1
            then
                echo WARNING: This version of glibc requires that you be running
                echo kernel version 2.4.1 or later.  Earlier kernels contained
                echo "bugs that may render the system unusable if a modern version"
                echo of glibc is installed.
                kernel26_help
                exit_check
            fi
        fi

        # From glibc 2.6-3 SPARC V8 support is dropped.
        if [ "$realarch" = sparc ]
        then
            # The process could be run using linux32, check for /proc.
            if [ -f /proc/cpuinfo ]
            then
               case "$(sed '/^type/!d;s/^type.*: //g' /proc/cpuinfo)" in
                   sun4u)
                      # UltraSPARC CPU
                      ;;
                   sun4v)
                      # Niagara CPU
                      ;;
                   *)
                      echo "WARNING: This machine has a SPARC V8 or earlier class processor."
                      echo "Debian lenny and later does not support such old hardware"
                      echo "any longer."
                      exit_check
                      ;;
               esac
            fi
        fi
    elif [ $system = "GNU/kFreeBSD" ] ; then
        kernel_ver=`uname -r`
        if kfreebsd_compare_versions "$kernel_ver" lt 6.0
        then
            echo WARNING: This version of glibc uses UMTX_OP_WAIT and UMTX_OP_WAKE
	    echo "syscalls that are not present in the current running kernel. They"
	    echo "have been added in kFreeBSD 6.0.  Your system should still work,"
	    echo but it is recommended to upgrade to a more recent version.
        fi
    fi

: exit 0
