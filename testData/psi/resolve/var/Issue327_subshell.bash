a=1

cat <<EOF
echo \$a
echo \$(
  \$<ref>a
)
EOF