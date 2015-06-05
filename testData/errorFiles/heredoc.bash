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