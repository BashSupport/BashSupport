[![Build Status](https://travis-ci.org/jansorg/BashSupport.svg?branch=idea-162.x)](https://travis-ci.org/jansorg/BashSupport)

**2017 project status: The BashSupport project will only receive fixes for major problems**

*BashSupport is fully developed in my spare time since 2009. I'm unable to provide full support for BashSupport and will concentrate on fixes for major problems.*

*Please let me know if you are interested in helping with the development. I will accept pull requests.*

# About
BashSupport adds Bash language support to IntelliJ based products.
It supports IntelliJ Ultimate and Community editions, PHPStorm, WebStorm, RubyMine, PyCharm and others.

## License
BashSupport is licensed under the terms of the Apache 2.0 license.
Read the file [LICENSE.txt](LICENSE.txt) for further details.

Please see NOTICE for further details about the license and included code of other developers.

## Documentation
For now, refer to the partially outdated [Documentation](http://www.ansorg-it.com/en/products_bashsupport.html).

## Changelog
See the [Changelog](Changelog.md) for a list of the last changes in BashSupport.

# Developer information
## Building
The command line build uses Gradle. The build definition is in `build.gradle`, it uses the ant file `build.xml` to re-generate the Bash lexer.

Build it and execute the unit tests by running:
```bash
./gradlew clean build
```

## Contributing
You need to setup IntelliJ at first to work on BashSupport.
BashSupport is developed in multiple branches, one for each major build version of the IntelliJ platform. For example idea-162.x is used
to develop for all platform products based on builds 162.x, i.e. IntelliJ Community/Ultimate 2016.2.x, PyCharm 2016.2 etc.

Development on Mac OS X and Linux has been tested, developing and building on Windows should work.

Currently all builds starting with 162.x are supported, idea-162.x is the main branch at the moment.

The usual way for contributors to work is this:
- checkout the main branch, i.e. `idea-162.x`
- Fork a new branch
- Do your changes and commit into the branch
- Create a pull request
- Before a new release all new changes and PRs will be merged into a single branch (forked from the main branch) to simplify the merging into the idea-* branches
- This new branch is then merge into all supported idea-* and the new release builds will be packaged

### Setup your IDE
These are the steps to setup IntelliJ to start with BashSupport development:
- Download IntelliJ Community 162.x (corresponding to the main branch), [Download page](https://www.jetbrains.com/idea/download/previous.html)
- Unpack/install it on your system
- Setup a new `IntelliJ Platform SDK` in your module settings, `File > Project structure ... > SDK > Add new SDK`
- Call it `IntelliJ IDEA Community Edition IC-162.x`. This SDK name is referenced by the project SDK configuration.
- Add the plugin `IntelliLang` to that SDK by clicking the add button on the `Classpath` tab and adding the files `plugins/IntelliLang/lib/*.jar` the the SDK
- Close the dialog and make sure that `Build > Rebuild Project` works without errors

### Contribution guidelines

I will accept pull requests. If possible add a unit test for your change. Test cases are quite important here to make sure that BashSupport
works on all the different platforms.

There is no fixed code style, but please follow the style you see in the source files, if possible.

If you need help to get started or to find out how a certain bug or feature could be done, please open a new issue and outline
what you would like to achieve and what the difficulties are.

## Documentation of plugin development
A highly recommended document is the [Plugin SDK documentation](http://www.jetbrains.org/intellij/sdk/docs/).

## Bash lexer
The lexer is defined as a JFlex lexer, the definition is in the file bash.flex in the package com.ansorgit.plugins.bash.lang.lexer.
The actual lexing has to track several states, which makes the lexer definition quite difficult. There are lexer unit tests defined in the package com.ansorgit.plugins.bash.lang.lexer in the source directory "test". 

## Bash Parser
The parser is defined in com.ansorgit.plugins.bash.lang.parser and its subpackages. The entry point is the class "BashParser". The actual parsing is split into several smaller pieces. A piece usually implements the interface ParsingFunction.

Parsing Bash mostly is whitespace insensitive in most parts, but at some places the whitespace needs to be read by the parser, i.e. Bash is partly whitespace-sensitive. To achieve this a hack has been implemented (for further details see BashPsiBuilder.enableWhitespace).

Also, in Bash keywords are context-sensitive. The word if is a keyword if it's the first token on a line. If passed as parameter it's a normal word token. BashTokenRemapper remaps keywords to normal word tokens, depending on the context.

String parsing is quite adventureous in Bash. Strings can contain subshells which can contain strings (with even more nesting). So "$(echo "$a")" is a string nested inside of another string. The class "StringParsingState" is used by the lexer to track the state of string nesting.

## Bash PSI
The Bash PSI definition is in com.ansorgit.plugins.bash.lang.psi . The entry point is the class BashPsiCreator, which is used to map a AST node to a newly created PSI node.

# Tools
I'm using Yourkit to locate and fix performance issues of BashSupport. YourKit, LLC kindly provided a
free open-source license of the [YourKit Java Profiler](https://www.yourkit.com/java/profiler/index.jsp).

![YourKit Java Profiler Logo](yklogo.png "YourKit Java Profiler Logo")
