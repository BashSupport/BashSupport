#!/usr/bin/env bash

# The backquote characters are lexer highlights and not handled by the annotator
# $var is highlighted by the syntax highlighter, not by the annotator
`<info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> hello $var`