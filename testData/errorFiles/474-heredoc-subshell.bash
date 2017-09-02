#!/usr/bin/env bash
echo start

$(
cat << EOF1
echo heredoc 1
EOF1
)

`
cat << EOF1
echo heredoc 2
EOF1
`

`cat << EOF3
echo heredoc 3
EOF3`

echo done

cat <<EOF4
EOF4