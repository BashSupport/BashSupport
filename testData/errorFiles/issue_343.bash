#!/bin/bash

# rpmdev-newspec -- generate new rpm .spec file from template
#
# Copyright (c) Warren Togami <warren@togami.com>,
#               Ville Skyttä <scop@fedoraproject.org>
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

SPECDIR="/etc/rpmdevtools"
DEFTYPE="minimal"
DEFSPEC="newpackage.spec"

usage() {
    ret=${1:-0}
    cat <<EOF <<EOF2
Usage: rpmdev-newspec [option]... [appname]

Options:
  -o FILE  Output the specfile to FILE.  "-" means stdout.  The default is
           "<appname>.spec", or "$DEFSPEC" if appname is not given.
  -t TYPE  Force use of the TYPE spec template.  The default is guessed
           from <appname>, falling back to "$DEFTYPE" if the guesswork
           does not result in a more specific one or if <appname> is not
           given.  See $SPECDIR/spectemplate-*.spec for available types.
  -h       Show this usage message
EOF
${a}
EOF2
    exit $ret
}

appname=
specfile=
spectype=

while [ -n "$1" ] ; do
    case "$1" in
        -t|--type)
            shift
            spectype="$1"
            ;;
        -o|--output)
            shift
            specfile="$1"
            ;;
        -h|--help)
            usage 0
            ;;
        *.spec)
            [ -z "$specfile" ] && specfile="$1"
            [ -z "$appname"  ] && appname="$(basename $1 .spec)"
            ;;
        *)
            appname="$1"
            [ -z "$specfile" ] && specfile="$appname.spec"
            ;;
    esac
    shift
done

specfilter=
if [ -z "$spectype" ] ; then
    case "$appname" in
        perl-*)
            spectype=perl
            cpandist="${appname##perl-}"
            specfilter="; s/^%setup.*/%setup -q -n $cpandist-%{version}/ \
               ; s|^\\(URL:\\s*\\).*|\1http://search.cpan.org/dist/$cpandist/|"
            ;;
        php-pear-*)
            spectype=php-pear
            pearname="$(echo ${appname##php-pear-} | tr - _)"
            specfilter="; s/\\bFoo_Bar\\b/$pearname/"
            ;;
        [Pp]y*)
            spectype=python
            ;;
        ruby-*)
            spectype=ruby
            ;;
        lib*|*-lib|*-libs)
            spectype=lib
            ;;
        *)
            spectype=$DEFTYPE
            ;;
    esac
fi

tempspec="$SPECDIR/spectemplate-$spectype.spec"

if [ ! -f "$tempspec" ] ; then
    echo "Template \"$tempspec\" not found, exiting."
    exit 1
fi

[ -z "$specfile" ] && specfile="$DEFSPEC"
if [ -f "$specfile" ] ; then
    echo "Output file \"$specfile\" already exists, exiting."
    exit 2
elif [ "$specfile" = "-" ] ; then
    specfile=/dev/stdout
fi

cat "$tempspec" | sed -e "s/^\\(Name:\\s*\\)/\\1$appname/ $specfilter" \
  > "$specfile"

if [ "$specfile" != "/dev/stdout" ] ; then
    echo "Skeleton specfile ($spectype) has been created to \"$specfile\"."
fi
