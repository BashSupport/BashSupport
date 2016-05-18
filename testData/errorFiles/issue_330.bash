#!/usr/bin/env bash

# Joins an array with newlines into a string.
# Usage: $ array_to_lines my_array | sort
array_to_lines() {
  local varname="$1"
  eval "printf '%s\n' \"\${${varname}[@]}\""
}

# Executes a command and puts each line into an array
# Usage: $ lines_to_array dirty_files \
#             git diff-files --name-only
lines_to_array() {
  local varname="$1"
  shift
  #eval "${varname}=()"
  #while IFS= read -r -d $'\n'; do
  #  eval "${varname}+=(\"$REPLY\")"
  #done < <("$@")
}
