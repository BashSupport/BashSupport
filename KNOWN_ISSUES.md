#Known issues of BashSupport

##Bash syntax
- trap: If the signal handler is more than a simple command than this is unsupported. For example: trap "export called=1; echo CALLED" RETURN
