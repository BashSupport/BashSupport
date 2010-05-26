About:
------
This is a custom language plugin for IntelliJ IDEA version 9.x. It is compatible with
the Community and the Ultimate editions of IntelliJ. It also is working with RubyMine 2.0.1 or later.
It is supposed to work with the other available editions (PyCharm, WebStorm, PHPStorm).

This plugin supports the Bash command shell, which is widely available in Linux and UNIX systems.
There is no support yet for Windows Cygwin editions of Bash.


Further information:
--------------------
    o Author: Joachim Ansorg, mail@ansorg-it.com
    o Documentation URL: http://www.ansorg-it.com/en/products_bashsupport.html
    o Source code: http://www.ansorg-it.com/en/products_bashsupport.html

License:
--------
   This work is licensed under the terms of the Apache 2.0 license.
   Read the file LICENSE.txt for further details.

   Please see NOTICE for further details about the license and included code of other developers.

Developer information:
----------------------
 The Bash lexer
 ~~~~~~~~~~~~~~
   The lexer is defined as a JFlex lexer, the definition is in the file bash.flex
   in the package com.ansorgit.plugins.bash.lang.lexer.
   The actual lexing has to track several states, which makes the lexer definition quite
   difficult.
   There are lexer unit tests defined in the package com.ansorgit.plugins.bash.lang.lexer
   in the source directory "test". 

 The Bash Parser
 ~~~~~~~~~~~~~~
   The parser is defined in com.ansorgit.plugins.bash.lang.parser and its subpackages.
   The entry point is the class "BashParser". The actual parsing is split into several smaller pieces.
   A piece usually implements the interface ParsingFunction.

   Parsing Bash mostly is whitespace insensitive in most pars, but at some places the whitespace needs to be
   read by the parser, i.e. Bash is partly whitespace-sensitive. To achieve this a hack has been implemented
   (for further details see BashPsiBuilder.enableWhitespace).

   Also, in Bash keywords are context-sensitive. The word if is a keyword if it's the first token
   on a line. If passed as parameter it's a normal word token. BashTokenRemapper remaps keywords to
   normal word tokens, depending on the context.

   String parsing is quite adventureous in Bash. Strings can contain subshells which can contain
   strings (with even more nesting). So "$(echo "$a")" is a string nested inside of another string.
   The class "StringParsingState" is used by the lexer to track the state of string nesting.

 The Bash PSI
 ~~~~~~~~~~~~
   The Bash PSI definition is in com.ansorgit.plugins.bash.lang.psi . The entry point is the class
   BashPsiCreator, which is used to map a AST node to a newly created PSI node.

 Other extensions
 ~~~~~~~~~~~~~~~~
   A good starting point is META-INF/plugins.xml. The referenced classes are the entry point for most
   of the functionality offered by the plugin.
