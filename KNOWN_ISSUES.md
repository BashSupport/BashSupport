#Known issues of BashSupport

##Bash syntax
- trap: If the signal handler is more than a simple command than this is unsupported. For example: trap "export called=1; echo CALLED" RETURN
- Escaped variables and expressions in eval parameters are not properly supported. You can enable its support in the BashSupport configuration settings, but use it at your own risk!

##Refactorings
- "Rename variable" doesn't work for variables in single-quoted strings, e.g. "eval '$a'"
