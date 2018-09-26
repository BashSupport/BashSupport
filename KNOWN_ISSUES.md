# Known issues of BashSupport

## Bash syntax
- trap: If the signal handler is more than a simple command than this is unsupported. For example: `trap "export called=1; echo CALLED" RETURN`
- Escaped variables and expressions in eval parameters are not properly supported. You can enable its support in the BashSupport configuration settings, but use it at your own risk!
- Heredoc: Subshell or parameter expansions which span more than one line inside of a heredoc are currently unsupported 
- Line continuations are only supported in words and string content, they are unsupported in keywords, e.g. `fo\<linebreak>r`
- Concatenated strings to build variable names are not supported, e.g. as in `printf -v "my""Var" foo` to set `$myVar`.
  Only `printf -v a` , `printf -v "a"` and `printf -v 'a'` are supported variable definitions.

## Refactorings
- "Rename variable" doesn't work for variables in single-quoted strings, e.g. "eval '$a'"