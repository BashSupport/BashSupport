readarray -t FILES < <(find . -type f -iname "*.go"|grep -v '\/vendor\/')
readarray -t DIRS < <(go list ./... | grep -v '\/vendor\/')

if [ ${#FILES[@]} -eq 0 ]; then
  errcho "No Go files found."
  exit 255
fi

if [ ${#DIRS[@]} -eq 0 ]; then
  errcho "No Go dirs found."
  exit 255
fi