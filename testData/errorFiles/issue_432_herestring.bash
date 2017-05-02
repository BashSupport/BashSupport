grep "nor" <<<$var >/dev/null && echo "Found" || echo "Not found"

wc -w <<<$(netstat -i | cut -d" " -f1 | egrep -v "^Kernel|Iface|lo")

mysql <<<'CREATE DATABASE dev' || exit