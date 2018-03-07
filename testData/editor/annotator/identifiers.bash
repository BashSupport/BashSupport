#Valid
a=1

#Invalid
α=1
разработка=1
export разработка=1 α=1
export α

# must not be an error
readarray -td '' all_lua_files
readarray -d '' -t all_lua_files
readarray -t -d '' -O 1 all_lua_files