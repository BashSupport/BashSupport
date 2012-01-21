#!/bin/sh

[[ a =~ abc ]]

classes=("" "")

if [[ $- =~ i ]]; then
    exit $doExit
fi

eval "(( $1 ))"

function usage()
{
cat <<- EOF
bashdoc generates HTML documentation from bash scripts.

Usage: $(basename $0) [OPTIONS] [--] script [script ...]

Options:
  -p, --project project   Name of the project
  -o, --output directory  Specifies the directory you want the resulting html to go into
  -c, --nocss             Do not write default CSS file.
  -e, --exclusive tag     Only output if the block has this tag
  -q, --quiet             Quiet the output
  -h, --help              Display this help and exit
  -V, --version           Output version information and exit
  --                      No more arguments, only scripts
  script                  The script you want documented

Examples:
  bashdoc.sh -p bashdoc -o docs/ bashdoc.sh              Generate documentation for this program.
  bashdoc.sh -p appname -o docs/ -e Type=API someapp.sh  Generate documentation for someapp.sh,
                                                         exclude items that do not include the tag
                                                         @Type API
EOF
}
