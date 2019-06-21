#!/usr/bin/env bash

<info textAttributesKey="BASH.VAR_DEF">var</info>="there"
<info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> $(<info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> hello world "$var")

(
    # comments inside of subshells must be properly highlighted
    # $var is highlighted by the syntax highlighter, not by the annotator
    <info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> hi $var
)