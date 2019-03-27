#!/usr/bin/env bash

<info textAttributesKey="BASH.VAR_DEF">var</info>=<info textAttributesKey="BASH.STRING">"there"</info>
<info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> $(<info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> hello world <info>"<info textAttributesKey="BASH.VAR_USE">$var</info>"</info>)

(
    # comments inside of subshells must be properly highlighted
    # $var is highlighted by the syntax highlighter, not by the annotator
    <info textAttributesKey="BASH.INTERNAL_COMMAND">echo</info> hi $var
)