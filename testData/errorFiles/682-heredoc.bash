#!/usr/bin/env bash
cat > file <<-EOF ||
content
EOF
echo "Failed"

cat > file <<-EOF ||
EOF
echo "Failed"

cat > file <<-EOF &
content
EOF

cat > file <<-EOF;
content
EOF