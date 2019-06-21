#!/usr/bin/env bash

<info textAttributesKey="BASH.EXTERNAL_COMMAND">cat</info> - << <info textAttributesKey="BASH.HERE_DOC_START">EOF</info>
<info textAttributesKey="BASH.HERE_DOC">    CONTENT
</info><info textAttributesKey="BASH.HERE_DOC_END">EOF</info>

<info textAttributesKey="BASH.VAR_DEF">var</info>="hello world"
<info textAttributesKey="BASH.EXTERNAL_COMMAND">cat</info> - << <info textAttributesKey="BASH.HERE_DOC_START">EOF</info>
<info textAttributesKey="BASH.HERE_DOC">    CONTENT
    CONTENT </info>$var<info textAttributesKey="BASH.HERE_DOC"> CONTENT
</info><info textAttributesKey="BASH.HERE_DOC_END">EOF</info>