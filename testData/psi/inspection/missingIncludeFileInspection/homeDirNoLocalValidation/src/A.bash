#!/bin/sh

# not validated
. "~/bashsupport.bash"
. ~/bashsupport.bash
. "$HOME/bashsupport.bash"
. $HOME/bashsupport.bash

# missing files, but not validated -> no error
. ~/bashsupport_missing.bash
. "~/bashsupport_missing.bash"
. $HOME/bashsupport_missing.bash
. "$HOME/bashsupport_missing.bash"

# no errors
. '~/bashsupport.bash'
. '$HOME/bashsupport.bash'