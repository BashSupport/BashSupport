shopt -s extglob
mv -v !(src|*.sh) ${dir}

if ! mv -v !(src|*.sh) ${dir}; then
  echo "moved files"
fi

echo !(a)
ls -la !(a|b|c|z*)

echo @(a|lib)
ls -la @(a|lib|a*|lib*)