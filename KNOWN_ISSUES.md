#Known issues of BashSupport

##Bash syntax
- trap: If the signal handler is more than a simple command than this is unsupported. For example: trap "export called=1; echo CALLED" RETURN

##Refactorings
- "Rename variable" doesn't work for variables in single-quoted strings, e.g. "eval '$a'"
