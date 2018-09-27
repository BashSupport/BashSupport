#!/bin/sh

# existing, file is created by test case
. "~/bashsupport.bash"
. ~/bashsupport.bash
. "$HOME/bashsupport.bash"
. $HOME/bashsupport.bash

# missing files
. ~/bashsupport_missing.bash
. "~/bashsupport_missing.bash"
. $HOME/bashsupport_missing.bash
. "$HOME/bashsupport_missing.bash"

# no errors
. '~/bashsupport.bash'
. '$HOME/bashsupport.bash'