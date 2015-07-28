cat - << _EOF
You can't use an unmatched single-quote this way
_EOF

echo a

cat >> /tmp/test.txt << !
Some text here
Some more text...
!

cat << EOF << EOF2
    a
EOF
    b
EOF2

abc=1
cat << EOF
    a $abc b
    $abc
EOF

echo $(cat <<EOF
hi there
EOF
)

# The marker may be escaped to disable variable evaluation
echo $(cat <<\EOF
hi there
EOF
)
