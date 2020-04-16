#### 2020-04-16:
 - [#792](https://github.com/BashSupport/BashSupport/issues/792): Added NO_SPACING formatting for variable concatenation using += operator (contributed by schilli91)
 
#### 2019-11-20:
 - Fixes for 2019.3
 - Version 1.7.14

#### 2019-11-06:
 - Fix a potential memory leak in an inspection
 
#### 2019-09-13:
 - Support 2019.3 EAP
 - Version 1.7.13

#### 2019-08-09:
 - [#771](https://github.com/BashSupport/BashSupport/issues/771): Line continuations not properly highlighted and parsed

#### 2019-07-19:
 - Version 1.7.12

#### 2019-07-15:
 - [#696](https://github.com/BashSupport/BashSupport/issues/696): Highlight comments in backtick commands
 - [#735](https://github.com/BashSupport/BashSupport/issues/735): Variables which were first declared in a loop were not properly resolved, renamed, and highlighted
 - [#753](https://github.com/BashSupport/BashSupport/issues/753): Fix NPE in BashRunConfiguration.suggestedName()
 - [#758](https://github.com/BashSupport/BashSupport/issues/758): Completions often showing "Invalid" as label
 - [#761](https://github.com/BashSupport/BashSupport/issues/761): IOException while scanning path
 - [#764](https://github.com/BashSupport/BashSupport/issues/764): Variables declared in included files were not always properly resolved.
 - Fix exception about IndexPatternBuilder incorrect Bash comment ranges

#### 2019-06-21:
 - [#682](https://github.com/BashSupport/BashSupport/issues/682): Parse heredocs in pipeline commands
 - [#662](https://github.com/BashSupport/BashSupport/issues/662): Enter handler to insert a line continuation in Bash files
 - [#658](https://github.com/BashSupport/BashSupport/issues/658): Fix broken highlighting of heredocs.
 - [#656](https://github.com/BashSupport/BashSupport/issues/656): Fix multiline highlighting of todo/fixme comments

#### 2019-05-28:
 - Version 1.7.8
 - Version 1.7.9

#### 2019-05-04:
 - Add internal documentation for ulimit command (contributed by dzieciolowski)

#### 2019-04-24:
  - [#458](https://github.com/BashSupport/BashSupport/issues/458): Support shopt -s extglob syntax

#### 2019-03-27:
  - [#687](https://github.com/BashSupport/BashSupport/issues/687): compound command () incorrectly highlighted. Removed highlighting of subshells, highlighting of contained elements must not be overridden. For example, comments inside of subshells must still be displayed as comments.
  - Removed override of highlighting inside of backticks `echo hello $world`. Now elements inside of backticks are highlighted as in subshell and group commands. Previously multi-line backticks weren't properly shown, for example.

#### 2019-03-29:
  - [#694](https://github.com/BashSupport/BashSupport/issues/694): Fix concurrent modification exception
  - Version 1.7.7

#### 2019-03-28:
 - [#486](https://github.com/BashSupport/BashSupport/issues/486): Highlighting of composed variables within strings. Not all of the configured highlighting was applied inside of strings. Now string highlighting does not override the highlighting of contained elements. The Kotlin plugin is handling this in the same way.

#### 2019-03-23:
  - We're not bundling Kotlin anymore due to several incompatilities with the version bundled with the SDK
  - Version 1.7.6

#### 2019-02-18:
 - [#655](https://github.com/BashSupport/BashSupport/issues/655): Improve performance of parsing and reference resolving. Disable the folding of variable values by default as this is too slow for large files. You can enable this in the settings if you want to keep this behavior.

#### 2019-02-15:
 - [#677](https://github.com/BashSupport/BashSupport/issues/677): Exception in idea.log of the latest CLion EAP

#### 2019-02-08:
 - [#657](https://github.com/BashSupport/BashSupport/issues/657): Exception when the arithmetic operator >> was used
 - [#671](https://github.com/BashSupport/BashSupport/issues/671): Ignore/log exception while scanning $PATH, e.g. while a system update is active.

#### 2019-02-07:
 - [PR#668](https://github.com/BashSupport/BashSupport/pull/668): Support `readarray` as alias of `mapfile` (contributed by niknetniko)

#### 2019-01-29:
 - [#664](https://github.com/BashSupport/BashSupport/issues/664): fix 'replace with double brackets' quick fix (contributed by bjansen)

#### 2018-12-15:
 - [#645](https://github.com/BashSupport/BashSupport/issues/645): fix NPE in keyword handling
 - [#634](https://github.com/BashSupport/BashSupport/issues/634): Handle invalid paths in include file inspection
 - [#636](https://github.com/BashSupport/BashSupport/issues/636): handle parse errors in interpreter paths in run configurations

#### 2018-11-13:
 - [#588](https://github.com/BashSupport/BashSupport/issues/588): Code folding for function's body (contributed by nosovae-dev)

#### 2018-11-07:
 - [#627](https://github.com/BashSupport/BashSupport/issues/627): Fix exception when folding builder was called when indexing
 - [#624](https://github.com/BashSupport/BashSupport/issues/624): Don't enable the project interpreter by default to keep run configurations created by 1.6.13 and earlier working. BashSupport tries to keep run configs modified by 1.7.0-1.7.2 working.
 - [#625](https://github.com/BashSupport/BashSupport/issues/625): java.nio.file.AccessDeniedException at startup
 - Don't show files in command completion which don't match *.exe or *.bat
 - Version 1.7.3

#### 2018-11-03:
 - Handle errors parsing elements of $PATH
 - Fix handling of interpreter path when creating new run configurations
 - Version 1.7.1
 - [#622](https://github.com/BashSupport/BashSupport/issues/622): Fix wrong size of script icons
 - Version 1.7.2

#### 2018-11-02:
 - [#611](https://github.com/BashSupport/BashSupport/issues/611): Work around "class BashIcons not found" exception which is (probably) caused by a bug in IntelliJ
 - [#478](https://github.com/BashSupport/BashSupport/issues/478): New setting for project wide interpreter path
 - [#487](https://github.com/BashSupport/BashSupport/issues/487): Use native path for shell script and working directory for new Bash run configurations
 - Version 1.7.0

#### 2018-10-25:
 - [PR#614](https://github.com/BashSupport/BashSupport/pull/614): Extension and refactoring of live templates (contributed by t0r0X)

#### 2018-10-25:
 - [#521](https://github.com/BashSupport/BashSupport/issues/521): control+j does not offer for loop solutions (contributed by t0r0X)

#### 2018-09-27:
 - [#572](https://github.com/BashSupport/BashSupport/issues/572): False-positive unresolved variables using names matching prefix parameter expansion (contributed by nosovae-dev)

#### 2018-09-27:
 - [#599](https://github.com/BashSupport/BashSupport/issues/599): Values passed to "export -f" were not resolved to the function definition

#### 2018-09-27:
 - [#526](https://github.com/BashSupport/BashSupport/issues/526): Documentation wasn't shown when the caret was positioned at the end of a command

#### 2018-09-10:
 - [#400](https://github.com/BashSupport/BashSupport/issues/400) Heredoc after command & is not parsed (contributed by nosovae-dev)

#### 2018-09-08:
 - [#477](https://github.com/BashSupport/BashSupport/issues/477) Support for .bashrc (or custom file extensions)
 - [#568](https://github.com/BashSupport/BashSupport/issues/568) variable incorrectly flagged as read-only (contributed by nosovae-dev)

#### 2018-09-05:
 - [#318](https://github.com/BashSupport/BashSupport/issues/318): Codefolding support (contributed by nosovae-dev)
 - [#546](https://github.com/BashSupport/BashSupport/issues/546): Color schemes: "Inherit values from" has no effect when checked (contributed by nosovae-dev)
 - [#519](https://github.com/BashSupport/BashSupport/issues/519): local -a arrays not recognized (contributed by nosovae-dev)
 - variables declared by local -r are now treated as read-only (contributed by nosovae-dev)

#### 2018-09-04:
 - [#518](https://github.com/BashSupport/BashSupport/issues/518): printf -v "${var}" flagged as Error

#### 2018-08-30:
 - [#540](https://github.com/BashSupport/BashSupport/issues/540): Tilde (~) not resolved correctly in file paths

#### 2018-08-29:
 - [#522](https://github.com/BashSupport/BashSupport/issues/522) Not a valid identifier in shell script with Bash functions with dash (contributed by nosovae-dev)

#### 2018-08-27:
 - Add action to create a report on the currently detected file type

#### 2018-08-25:
 - fix to improve handling of local variables in functions
 - [#566](https://github.com/BashSupport/BashSupport/issues/566): Fix slow startup of the IDE when BashSupport is installed
 - Enhancement: show path next to path command completion items

#### 2018-08-14:
 - new icons for plugin (contributed by nosovae-dev)

#### 2018-08-09:
 - Feature: Add folding for variables (contributed by nosovae-dev)

#### 2018-08-09:
 - Add kotlin language for development plugin (contributed by nosovae-dev)

#### 2018-07-25:
 - [#558](https://github.com/BashSupport/BashSupport/issues/558): Enable execute button of Bash run configuration even when IntelliJ is indexing
 - [#551](https://github.com/BashSupport/BashSupport/issues/551): Add default value TERM=xterm-256color when running a Bash script if there's not value defined in the run configuration
 - [#545](https://github.com/BashSupport/BashSupport/issues/545): Don't parse "in" in "read in" as keyword, but as variable name
 - [#482](https://github.com/BashSupport/BashSupport/issues/482): Make new Bash files executable for the current user by default

#### 2018-07-24:
 - Feature: Add inspection to check, whether function name is in lower camel case (contribued by dheid)
 - Feature: Add inspection and quickfix to detect double brackets (contribued by dheid)

#### 2018-03-07:
 - [#515](https://github.com/BashSupport/BashSupport/issues/515): Not a valid identifier in shell script (dynamic variable names with export command)
 - Release 1.6.13

#### 2018-03-06:
 - [#328](https://github.com/BashSupport/BashSupport/issues/328): Using ${#var} produces incorrect warning "Array use of non-array variable"
 - [#517](https://github.com/BashSupport/BashSupport/issues/517): Incorrect warning "Array use of non-array variable"

#### 2018-02-02:
 - [#505](https://github.com/BashSupport/BashSupport/issues/505): Fixed "Lexer could not match input" error for redirect after here string
 - [#508](https://github.com/BashSupport/BashSupport/issues/508): Argument values of the mapfile/readarray built-in were parsed as variable names
 - [#511](https://github.com/BashSupport/BashSupport/issues/511): Pull request by contributor vn971.
 - [#512](https://github.com/BashSupport/BashSupport/issues/512): Arguments ${10} and up flagged as errors when using curly brackets

#### 2017-09-02:
 - [#457](https://github.com/BashSupport/BashSupport/issues/457): Unresolved Variable Warning on Linebreak
 - [#460](https://github.com/BashSupport/BashSupport/issues/460): Parse commands separated by |&
 - [#462](https://github.com/BashSupport/BashSupport/issues/462): Unexpected Token w/ declare and some other constructs
 - [#465](https://github.com/BashSupport/BashSupport/issues/465): BashShebang NPE
 - [#467](https://github.com/BashSupport/BashSupport/issues/467): Deadlock in BashSupport
 - [#468](https://github.com/BashSupport/BashSupport/issues/468): Functions assumed to have curly brace body
 - [#469](https://github.com/BashSupport/BashSupport/issues/469): Support variables defined by `printf -v`
 - [#473](https://github.com/BashSupport/BashSupport/issues/473): Support heredocs where the end marker is followed by a backtick
 - [#474](https://github.com/BashSupport/BashSupport/issues/474): Support semicolon after command which starts a heredoc
 - Release 1.6.12

#### 2017-08-29:
 - [#459](https://github.com/BashSupport/BashSupport/issues/459): Fixed formatting of redirects with process substitutions

#### 2017-07-27:
 - [#394](https://github.com/BashSupport/BashSupport/issues/394): Renaming $1 shows 'Renaming unknown type $1' in the dialog
 - [#401](https://github.com/BashSupport/BashSupport/issues/401): Parameter expansion parsing error
 - [#404](https://github.com/BashSupport/BashSupport/issues/404): Using array variable without quotes generates a warning "Simple use of array variable"
 - [#411](https://github.com/BashSupport/BashSupport/issues/411): NPE in OSUtil.findBestExecutable
 - [#412](https://github.com/BashSupport/BashSupport/issues/412): Syntax Errors on valid code involving regular expression
 - [#433](https://github.com/BashSupport/BashSupport/issues/433): BashCommandManipulator: Cannot modify a read-only file
 - [#444](https://github.com/BashSupport/BashSupport/issues/444): Formatter removes space after empty string variable assignment
 - [#452](https://github.com/BashSupport/BashSupport/issues/452): Concurrent modification in BashVarDefImpl
 - [#453](https://github.com/BashSupport/BashSupport/issues/453): A weird light blue background is seen over my code area
 - [#454](https://github.com/BashSupport/BashSupport/issues/454): Using "mapfile" results in "Array use of non-array variables"
 - [#456](https://github.com/BashSupport/BashSupport/issues/456): NPE in BashVarDefImpl
 - Release 1.6.12

#### 2017-07-13:
 - [#451](https://github.com/BashSupport/BashSupport/issues/451): Fix stub exceptions triggered by BashSupport while looking for include files
  - [#450](https://github.com/BashSupport/BashSupport/issues/450) If a script has an exit call on toplevel we assume that all following lines contain non-bash/binary data. The content after the exit command will be highlighed as binary data. Before an exception was thrown and IDEA had to be restarted.
 - [#449](https://github.com/BashSupport/BashSupport/issues/449): Don't warn on variable identifiers declared in an eval block

#### 2017-06-17:
 - Removed "Build" step from default Bash run configuration

#### 2017-05-02:
 - [#432](https://github.com/BashSupport/BashSupport/issues/432): Unexpected token on correct line with here-string
 - [#427](https://github.com/BashSupport/BashSupport/issues/427): Wrong autoformatting for wrapped heredoc
 - Released 1.6.9

#### 2017-04-29:
 - [#418](https://github.com/BashSupport/BashSupport/issues/418): Fix to parsed structure of variable assignment lists

#### 2017-04-26:
 - [#426](https://github.com/BashSupport/BashSupport/issues/426): Fixed parsing of arithmetic operators "^", "^^", "," ",,"
 - [#426](https://github.com/BashSupport/BashSupport/issues/426): (Very) basic support to parse patterns in arithmetic expressions
 - [#424](https://github.com/BashSupport/BashSupport/issues/424): Error displaying the folding elements in the HERE-document when there are variables in the text
 - [#431](https://github.com/BashSupport/BashSupport/issues/431): The arithmetic bitwise assignments |= &= and ^= are now correctly parsed.
 - Released 1.6.8

#### 2017-04-11:
 - [#420](https://github.com/BashSupport/BashSupport/issues/420): Assignment chains in arithmetic expressions have to static replacement values (an exception was thrown for $((a=1,b=2,c=3)), for example)
 - [#419](https://github.com/BashSupport/BashSupport/issues/419): Parsing error with double-pipe in subshell contained in list parenthesis

#### 2017-04-02:
 - [#413](https://github.com/BashSupport/BashSupport/issues/413): Fix variable and function resolving in files which are outside of a module content root
 - [#409](https://github.com/BashSupport/BashSupport/issues/409): Jump to function def does not work in language injected string literals
 - [#408](https://github.com/BashSupport/BashSupport/issues/408): All variables in injected language chunks are flagged as unresolved

#### 2017-03-30:
 - [#358](https://github.com/BashSupport/BashSupport/issues/358): Remove the maximum nesting level while parsing comamnds.
 - Release 1.6.6 (eap)

#### 2017-01-07:
 - [#398](https://github.com/BashSupport/BashSupport/issues/398): Error updating LexerEditorHighlighter, resulted in many error while working in a Bash script.
 - [#399](https://github.com/BashSupport/BashSupport/issues/399): Potential fix for java.lang.NoClassDefFoundError: com/ansorgit/plugins/bash/util/BashIcons
 - Release 1.6.5

#### 2017-01-03:
 - [#395](https://github.com/BashSupport/BashSupport/issues/395): Hotfix to make BashSupport work again with scripts containing errors
 - [#396](https://github.com/BashSupport/BashSupport/issues/396): Check for working directory only if the module is valid
 - [#397](https://github.com/BashSupport/BashSupport/issues/397): Tokens after a here string <<< start were not always detected properly

#### 2016-12-31:
 - Prevent to pass empty elements to highlighting annotations
 - Fix a division by zero exception
 - Disable inline renaming if the context and the actual definition are in different files.
 - [#245](https://github.com/BashSupport/BashSupport/issues/245): No possibly incorrect warnings about unused parameters if shift is used in the function inside of a loop.
 - Release 1.6.1
 - Improved parsing to be more error resistant, functions and commands which contain faulty code will now be recognized more often
 - Handle line continuations in case pattern lists
 - Increase index version to handle stub index vs. psi exceptions
 - Release 1.6.2

#### 2016-12-30:
 - [#391](https://github.com/BashSupport/BashSupport/issues/391): New inspection to highlighting unresolved, but globally registered variables to have a separate highlighting level and inspection description.
 - [#388](https://github.com/BashSupport/BashSupport/issues/388): Reformatting heredocs changes content
 - [#361](https://github.com/BashSupport/BashSupport/issues/361): Added support for the mapfile and readarray Bash builtin comamnds
 - [#357](https://github.com/BashSupport/BashSupport/issues/357): Added a potential fix for the problem
 - [#350](https://github.com/BashSupport/BashSupport/issues/350): Redirection in eval statement is flagged as error
 - [#392](https://github.com/BashSupport/BashSupport/issues/392): Formatted changes code in eval strings and redirect tokens
 - [#393](https://github.com/BashSupport/BashSupport/issues/393): Update JFlex to the latest version
 - [#349](https://github.com/BashSupport/BashSupport/issues/349): Support unicode characters in the script, warn about invalid identifiers which included illegal characters

#### 2016-12-29:
 - [#351](https://github.com/BashSupport/BashSupport/issues/351): Added debug message for further diagnosis
 - Added support for the ## parameter expansion operator
 - [#367](https://github.com/BashSupport/BashSupport/issues/367): Fixed parsing of [[ $(< $1) ]] expression
 - [#367](https://github.com/BashSupport/BashSupport/issues/367): Fixed parsing of here-strings, e.g. "tr [:lower:] [:upper:] <<< [abc]"
 - [#383](https://github.com/BashSupport/BashSupport/issues/383): Handle division by zero in arithmetic expressions and show a warning in the code
 - [#368](https://github.com/BashSupport/BashSupport/issues/368): Added debugging messages for arithmetic expressions
 - Release 1.6.0

#### 2016-12-28:
 - [#377](https://github.com/BashSupport/BashSupport/issues/377): NPE in UnregisterGlobalVariableQuickfix
 - [#379](https://github.com/BashSupport/BashSupport/issues/379): Undo "Register Global Variable" doesn't work
 - [#388](https://github.com/BashSupport/BashSupport/issues/388): References for files are now searched in the module content scope and not in the module scope.
 - [#389](https://github.com/BashSupport/BashSupport/issues/389): Files with line continuations were not parsed properly and may break the whole project
 - Start of 1.6.x version

#### 2016-05-26:
 - [#310](https://github.com/BashSupport/BashSupport/issues/310): The parsed now accepts more deeply nested command lists
 - [#329](https://github.com/BashSupport/BashSupport/issues/329): "Unexpected Token" Error For Associative Array
 - [#333](https://github.com/BashSupport/BashSupport/issues/333): Marked support for escapes in evaluated code as experimental. A proper fix needs major work on the lexer framework and is currently not fully supported. There is a new experimental setting in the BashSupport settings dialog. Use it at your own risk.
 - [#343](https://github.com/BashSupport/BashSupport/issues/343): Exception "isExpectingEvaluatingHeredoc called on an empty marker stack "
 - Parsing for arithmetic expression in evaluating heredocs

#### 2016-05-22:
 - [#341](https://github.com/BashSupport/BashSupport/issues/341): Exception "The inString stack should not be empty"
 - [#342](https://github.com/BashSupport/BashSupport/issues/342): Exception "Index out of range: -1" at beginning of file
 - Released 1.5.7

#### 2016-05-18:
 - [#339](https://github.com/BashSupport/BashSupport/issues/339): Update failed for AnAction with ID=ExternalJavaDoc (ClassCastException)
 - [#340](https://github.com/BashSupport/BashSupport/issues/340): isExpectingEvaluatingHeredoc called on an empty marker stack

#### 2016-05-18:
 - Released 1.5.6

#### 2016-05-17:
 - [#331](https://github.com/BashSupport/BashSupport/issues/331): NPE in HereDoc parsing
 - [#332](https://github.com/BashSupport/BashSupport/issues/332): NPE in enter processor
 - [#332](https://github.com/BashSupport/BashSupport/issues/332): ClassCastExceptions in ManpageDocSourc
 - [#334](https://github.com/BashSupport/BashSupport/issues/334): Potential fix for the lexing state errors

#### 2016-05-16:
 - [#330](https://github.com/BashSupport/BashSupport/issues/330): Variable declarations in eval code now accepts variables on the left side of an assignment

#### 2016-04-22:
 - [#320](https://github.com/BashSupport/BashSupport/issues/320): Array use in arithmetic expressions wasn't working as expected

#### 2016-04-16:
 - [#321](https://github.com/BashSupport/BashSupport/issues/321): Handle leading tabs before heredoc end markers in heredocs started with the marker <<-

#### 2016-04-19:
 - [#324](https://github.com/BashSupport/BashSupport/issues/324) Error reporting component is broken
 - Switched the exception error reporting to use https.

#### 2016-04-22:
 - [#327](https://github.com/BashSupport/BashSupport/issues/327): Heredoc parser doesn't recognize \$( or \${ construction

#### 2016-04-24:
 - [#89](https://github.com/BashSupport/BashSupport/issues/89): Function expands incorrectly when defined above another function

#### 2016-03-31:
 - [#310](https://github.com/BashSupport/BashSupport/issues/310): Workaround for deeply nested structures

#### 2016-03-12:
 - Removed Bash facet, it wasn't used any more. Also, facets are unavailable on the minor platform products.
 - Added more test cases for improved test coverage
 - Configured copyright plugin

#### 2016-02-23:
 - [#125](https://github.com/BashSupport/BashSupport/issues/125): Array element gives "Unexpected token"

#### 2016-02-23:
 - Release 1.5.5

#### 2016-02-22:
 - No keyword and live-template autocompletion in command arguments
 - Performance fixes for less latency in the editor
 - Variables and functions can now be resolved in scratch files again
 - [#306](https://github.com/BashSupport/BashSupport/issues/306): Index access during dumb mode
 - [#306](https://github.com/BashSupport/BashSupport/issues/306): Could not initialize class nu.studer.idea.errorreporting.PluginErrorReportSubmitterBundle

#### 2016-02-20:
 - [#313](https://github.com/BashSupport/BashSupport/issues/313): More gentle parsing for empty command lists for: for, while, until, if, select.

#### 2016-02-20:
 - [#312](https://github.com/BashSupport/BashSupport/issues/312): Newlines are not accepted in parameter expansions

#### 2016-02-20:
 - [#297](https://github.com/BashSupport/BashSupport/issues/297): File includes with directories or / in the path reference are not working as expected

#### 2016-02-01:
 - [#300](https://github.com/BashSupport/BashSupport/issues/300): Variables are not recognized in case

#### 2016-02-01:
 - [#303](https://github.com/BashSupport/BashSupport/issues/303): Multiline lists not recognised

#### 2016-02-01:
 - [#308](https://github.com/BashSupport/BashSupport/issues/308): Grouping in test commands not properly parsed

#### 2016-01-06:
 - [#299](https://github.com/BashSupport/BashSupport/issues/299): ClassNotFound exception in PHPStorm

#### 2015-12-20
 - [#286](https://github.com/BashSupport/BashSupport/issues/286): Eval parsing of code not inside a string

#### 2015-12-15
 - [#263](https://github.com/BashSupport/BashSupport/issues/263): Warning for modifications of variables re-defined as readonly

#### 2015-12-11
 - Up to 35x faster file highlighting
 - Improved eval and trap parsing
 - Composed commands (if,while,do,case,...) may be the body of a function, i.e. without curly brackets

#### 2015-11-04
 - [#290](https://github.com/BashSupport/BashSupport/issues/290): Inspections have no descriptions
 - [#289](https://github.com/BashSupport/BashSupport/issues/289): Quickdoc lookup not working for keywords
 - [#288](https://github.com/BashSupport/BashSupport/issues/288): Documentation lookup not working for functions
 - [#283](https://github.com/BashSupport/BashSupport/issues/283): Highlighting of built-in commands: added missing highlighting of "source", "trap", "let", "coproc" and "mapfile"
 - Released 1.5.2

#### 2015-10-19
 - [#282](https://github.com/BashSupport/BashSupport/issues/282): Improved file type guessing for files without an extension

#### 2015-10-19
 - [#280](https://github.com/BashSupport/BashSupport/issues/280): Unnecessary error if there is no module available for a run configuration

#### 2015-10-06
 - Released 1.5.1

#### 2015-10-05
 - [#274](https://github.com/BashSupport/BashSupport/issues/274): Spellchecking support in single-quotes strings, double-quotes strings and heredoc content

#### 2015-09-16
 - [#273](https://github.com/BashSupport/BashSupport/issues/273): An exception was thrown if the Bash settings were opened by the welcome screen

#### 2015-08-19
 - [#270](https://github.com/BashSupport/BashSupport/issues/270): Incorrect parsing and 'Unresolved variable' report for heredocs
 - [#270](https://github.com/BashSupport/BashSupport/issues/270): Escape handling in heredoc content

#### 2015-08-14
 - [#90](https://github.com/BashSupport/BashSupport/issues/90): Language injection into unevaluated heredoc content, single and double quoted strings

#### 2015-08-13
 - [#206](https://github.com/BashSupport/BashSupport/issues/206): NPE in ProgramParametersConfigurator
 - [#266](https://github.com/BashSupport/BashSupport/issues/266): Valid parameter expansion ${#} marked as an error
 - [#271](https://github.com/BashSupport/BashSupport/issues/271): Empty subshell rejected as invalid
 - [#270](https://github.com/BashSupport/BashSupport/issues/270): Incorrect parsing and 'Unresolved variable' report for heredocs
 - [#228](https://github.com/BashSupport/BashSupport/issues/228) Support for the trap command.

#### 2015-08-11
 - [#265](https://github.com/BashSupport/BashSupport/issues/265): Digit $ is invalid with base 10

#### 2015-07-18
 - [#237](https://github.com/BashSupport/BashSupport/issues/237): BashSupport not saving globals entered into "Registered global variables" settings
 - Heredoc parsing improvement: The processing is more robust, more correct and should be faster, too.

#### 2015-07-12
 - [#246](https://github.com/BashSupport/BashSupport/issues/246): Minor Single Quote Escaping Issue

#### 2015-07-07
 - [#201](https://github.com/BashSupport/BashSupport/issues/201): Cannot parse logical not in arithmetic expressions
 - [#238](https://github.com/BashSupport/BashSupport/issues/238): Replace with evaluated expansion" works incorrectly

#### 2015-09-16
 - [#273](https://github.com/BashSupport/BashSupport/issues/273): An exception was thrown if the Bash settings were opened by the welcome screen

#### 2015-08-19
 - [#270](https://github.com/BashSupport/BashSupport/issues/270): Incorrect parsing and 'Unresolved variable' report for heredocs
 - [#270](https://github.com/BashSupport/BashSupport/issues/270): Escape handling in heredoc content

#### 2015-07-06
 - Bash code passed to eval in single quotes ('') is now parsed, too

#### 2015-07-04
 - [#243](https://github.com/BashSupport/BashSupport/issues/243): Single line case statement parse error

#### 2015-07-02
 - Files usages in strings are now processed during a file rename if the option is enabled.

#### 2015-06-21
 - Renaming files in non-source directories

#### 2015-06-18
 - References to other Bash script files will now be renamed if the filename is changed

#### 2015-06-14
 - Fixed renaming of filenames in unevaluated strings, e.g. 'file.bash'
 - New Bash files are now created from a template. If you want to customize the default script you can modify the template "Bash Script" in the IntelliJ settings.
 - [#223](https://github.com/BashSupport/BashSupport/issues/223): Update failed for AnAction with ID=Bash.NewBashScript: Already disposed

#### 2015-06-11
 - Fixed renaming of Bash files, IntelliJ had a feature for all files to turn off search for references. This is now disabled for Bash files. References are always changed if a Bash file is renamed.

#### 2015-06-10
 - Fixed Bash file renaming, file references to Bash files are changed now

#### 2015-06-05
 - No keyword completions while typing in a Bash comment
 - No live template completions while typing in a Bash comment
 - Bash version 4 support is now enabled by default

#### 2015-06-02
 - Moved README.txt to README.md

#### 2015-03-04
 - Version 1.4.0
 - [#193](https://github.com/BashSupport/BashSupport/issues/193): Variables in arithmetic base expressions were not supported
 - [#194](https://github.com/BashSupport/BashSupport/issues/194): $(( 8[#9](https://github.com/BashSupport/BashSupport/issues/9) )) threw an exception about an invalid base 8 for the value 9. A warning is now displayed in the editor instead.

#### 2015-03-03
 - Fixed the "Run before" steps of a Bash run configuration
 - The template Bash run configuration has no "Run before" step now

#### 2015-03-02
 - No keyword autocompletion in a variable context
 - Fixed parsing of a file containg a $ as last character
 - Fixed autocomplete in eval and trap commands

#### 2015-03-01
 - Parser performance improved (pool for internal marker objects added)
 - The special /usr/bin/env command is now supported by the shebang inspection

#### 2015-02-28
 - Added live template context for Bash files
 - Added basic Bash live templates: if, ife, ifee, while, until, case, cap, cap2, cap3
 - Possible fix for a stack overflow in the parser (not reproducible here, though)
 - [#190](https://github.com/BashSupport/BashSupport/issues/190): Fixed UOE error in the "register as global variable" quickfix
 - [#179](https://github.com/BashSupport/BashSupport/issues/179): Fixed NPE in FileInclusionManager
 - [#152](https://github.com/BashSupport/BashSupport/issues/152): Guard to prevent endless recursions
 - Parser performance improvement

#### 2015-02-27
 - [#192](https://github.com/BashSupport/BashSupport/issues/192): Fixed NPE in highlighting of arithmetic expressions
 - [#170](https://github.com/BashSupport/BashSupport/issues/170): Keywords like "fi", "if", "while", "done" were not offered in the list of autocompletion suggestions
 - Fixed autocompletion at the end of a string, e.g. "$a<caret>". The string end marker was replaced if tab was pressed.
 - Fixed autocompletion with open strings, e.g. $a<caret>\necho "x". The command in the next line was removed if tab was pressed.
 - Fixed autocompletion in curly brackets, e.g. ${<caret>}. The right curly bracket was removed if tab was pressed.
 - Fixed missing autocompletion of global variables and built-in commands inside of parameter expansions

#### 2015-02-13
 - [#184](https://github.com/BashSupport/BashSupport/issues/184): Single quoted associative array keys parsing error
 - [#186](https://github.com/BashSupport/BashSupport/issues/186): Erroneous warning "Simple use of array variable" when iterating through array

#### 2015-02-08
 - NPE fix for FileInclusionManager

#### 2015-02-04
 - Version 1.3.4
 - Small performance and memory improvement in the Bash file lexer
 - Fixed case expressions followed by a backtick character
 - Fixed "Local variable definition on global level" inspection
 - Escape characters were not accepted in case pattern

#### 2015-01-28
 - Fixed NullPointerException in AddShebangInspection.getBatchSuppressActions
 - Refactored inspections
 - Improved inspection testcases

#### 2015-01-24
 - Fixed exception "Assertion failed: leaveModal() should be invoked in event-dispatch thread"

#### 2015-01-16
 - Fixed time command exceptions. If the time command was used the parse tree was broken up to now. AssertionErrors in WalkingState.next were thrown.

#### 2015-01-14
 - Version 1.3.3
 - Fixed run configurations produced for non-bash files
 - Fixed IndexNotReadyException

#### 2015-01-11
 - Version 1.3.2
 - Fixed UOE which occurred if a new plain text file without extension was created
 - Fixed the mesage displayed on startup

#### 2015-01-10
 - Version 1.3.1
 - Version 1.3.0

#### 2015-01-07
 - Added notification message at project init to inform about the wedding gift campaign. It will not appear after the wedding date.

#### 2015-01-06
 - Fixed NPE BashPathCommandCompletion which occurred at startup of the latest IntelliJ IDEA EAP

#### 2014-12-05
 - Enabled and fixed the custom error reporting dialog

#### 2014-12-04
 - Creating new Bash run configurations now picks up the shell path and shell options of the file's shebang line

#### 2014-11-30
 - Disabled debug button for Bash run configuration

#### 2014-11-27
 - Major work on the run configuration: Ansi colors supported, reuse of default components, bugs fixed

#### 2014-11-25
 - [#166](https://github.com/BashSupport/BashSupport/issues/166): Support for ToDo-Strings in Bash comments

#### 2014-11-13
 - Fixed QuickFix implementations to not reference PSI element, reduces memory consumption
 - The inspection "Add missing shebang..." can now be suppressed. A comment will be added to the file to suppress it.

#### 2014-11-12
 - Fixed NPE which occurred with unclosed subshell expressions
 - https://code.google.com/p/bashsupport/issues/detail?id=156 Added characters +\#_ to the filename characters

#### 2014-11-07
 - Released 1.2.1

#### 2014-09-12
 - Fixed autocompletion tests
 - Fixed invalid warnings about unused paramters in ${*} and ${@}
 - Support for the getopts command
 - Support for process substitution redirects, e.g. <(command)
 - [Issue 145]: Functions defined in include files are now properly found if included on global level
 - Version 1.1 released

#### 2014-05-29
 - Fixed parsing of optional array variables used in the read command
 - Added missing implementation of the time command PSI element
 - Fixed autocompletion of built-in variables
 - Array type of a variable is inferred by typeset -a now, too
 - Added support to detect illegal use of readonly variables declared by typeset -r or declare -r
 - 1.1beta24

 - New settings: Variables which are defined in function can be treated as global variables. Default is strict checking.
 - Fixed variable resolving in blocks. If multiple definitions are present then some were ignored.
 - Fixed variable resolving in composed commands, added testcases

#### 2014-04-10
 - Fixed & simplified file type checking
 - Fixed exception to fix ISE with isDirectory calls

#### 2014-04-09
 - Possible fix for SOE in IDEA 13.1.1
 - Small improvement to file type checking for files without extensions

#### 2013-12-07
 - Bug [#116](https://github.com/BashSupport/BashSupport/issues/116): Fixed value expansions with a string prefix. "a"{1,2} is now properly expanded to 'a1 a2'.

#### 2013-12-02
 - 1.1beta20 for IDEA 13

#### 2013-08-08
 - 1.1beta17 for IDEA 12 and IDEA 13

#### 2013-06-15
 - Parsing performance improved, especially for large scripts
 - Support for the typeset command
 - Initial support for the trap command
 - Initial support for the eval command

#### 2013-06-12
 - "Replace with ${var}" is not suggested inside of arithmetic expressions any more

#### 2013-06-01
 - Improved color scheme definitions, looks better with Darcula
 - Improved performance (variable resolving)
 - Assignments which non-static variable names are not suggested with autocomplete (e.g. "$a"=a)
 - Better looking with themes now
 - Fixed highlighting of redirects
 - 1.1beta17

#### 2013-05-26
 - Moved from svn to git

#### 2013-05-09
 - 1.1beta16

#### 2013-05-09
 - Fixed highlighting of keywords used as plain text / word (the unwanted highlighting is removed).
 - Fixed shebang command inspection, settings a properly saved now
 - Added quickfix "Register as valid shebang command" for the shebang command inspection
 - Changed default highlighting level of "Unresolved variable" inspection from error to warning
 - "Simple variable use" is not added to variables which are wrapped by a string
 - "Simple array variable use" is not added to variables which are wrapped by a string

#### 2013-05-05
 - $@ is now taken as function parameter use, i.e. a a call of a function using $@ does not lead to unused parameter warnings

#### 2013-04-30
 - Improved parsing of let commands (still incomplete)
 - Improved parsing of variables named like internal bash keywords
 - Improved performance for large script files (implemented caching references for variables, variable definitions and function definitions)

#### 2013-04-06
 - Escape chars were used in single quoted strings, but Bash does not allow escaped characters there
 - Fixed parsing of string as case pattern
 - Fixed parsing of ;;& in Bash 3 parsing mode

#### 2013-02-09
 - Compatibility with 128.x
 - 1.1beta15

#### 2013-02-03
 - Autocompletion of commands using the user's $PATH environment. Autocompletion shows on second completion invocation.
 - Setting to disable autocompletion of commands using $PATH
 - 1.1beta14

#### 2013-01-30
 - Changed Bash file template to use /bin/bash instead of /bin/sh

#### 2013-01-25
 - Rework of the syntax highlighting configuration
 - Highlighting settings should be properly saved now
 - Added highlighting for function name in a function definition

#### 2013-01-24
 - 1.1beta13

#### 2013-01-23
 - Compatibility with 121 for PyCharm 2.6 and others
 - Fixed icons to look better with Darcula

#### 2012-12-19
 - Fixed flashing gutter bar icons
 - 1.1beta12

#### 2012-12-10
 - Removed code which is not needed for IntelliJ 12 any more
 - 1.1beta11

#### 2012-11-22
 - Improved parsing of paramter expansion (i.e. the parsing of the / operator)

#### 2012-11-21
 - Fixed testcases to work with IntelliJ IDEA 123.4
 - Added parsing of history expansions
 - Fixed parsing of single ! tokens

#### 2012-11-18
 - Fix to allow umlaut characters in command names and arguments.

#### 2012-09-26
 - Compatibility with IDEA 12.0
 - 1.1beta9

#### 2012-03-23
 - Compatibility with IDEA 11.1
 - 1.1beta8

#### 2012-02-16
 - Fixed the possible cause of the unbalanced markers
 - 1.1beta7

#### 2012-02-09
 - 1.1beta6

#### 2012-02-02
 - Possible fix for exception "StringIndexOutOfBoundsException"

#### 2012-01-26
 - Less inspection warnings

#### 2012-01-25
 - Hopefully fixed "marker not closed" exception
 - Better error messages for empty loop bodies

#### 2012-01-21
 - Lexer fixes
 - Parser fixes
 - Fixed exception in BashSearchScopes.moduleSearchScope
 - Improved here-doc parsing
 - Fixed parsing of string and subexpressions in arithmetic expressions
 - Fixed parsing of [ ] conditional expression
 - Enabled debug mode for beta releases
 - 1.1beta5

#### 2012-01-18
 - Parser fix for "unset todo_list[$todo_id]"

#### 2012-01-14
 - Parser fixes for nested strings
 - Improved support for substitution operators in parameter expansion, e.g. ${var:-value if var is null}
 - The bash library bashinator now parses without syntax errors

#### 2012-01-12
 - Parsing performance improved
 - Indexing of variable definitions, function definitions, script includes, reverse script includes and file names

#### 2012-01-07
 - Fixed parsing of ${@} and ${?}
 - Fixed unevaluated strings in a subshell
 - Error message for postfix / prefix operators on variable values in arithmetic expressions, e.g. $(( ${x}++ ))
 - Version 1.1beta3

#### 2012-01-06
 - Basic support for regular expressions in conditional command, e.g. [[ a =~ e* ]]
 - Fixed negation in conditional expressions, e.g. [ ! -f "test.txt" ]

#### 2012-01-05
 - Redirects after include command are supported now

#### 2011-11-29
 - Version 1.1beta2

#### 2011-11-12
 - Parser fixes

#### 2011-11-10
 - Fixed parsing of the semicolon in "case ... esac;"
 - Fixed hidden files in absolute path completion
 - Version 1.1beta1

#### 2011-11-09
 - Fixed parsing of conditional commands [[ x ]] and test expressions [ x ]
 - Compatibility with IntelliJ 11 EAP

#### 2011-08-12
 - Fixed Exception in file path completion provider

#### 2011-05-19
 - Version 1.0

#### 2011-05-17
 - Fixed compatibility with IntelliJ IDEA 10.5
 - The Bash REPL now uses Enter instead of Ctrl+Enter

#### 2011-05-03
 - Removed Java options tab from Bash run configuration settings

#### 2011-04-11
 - Bug: NPE occurred when editing files which are not part of the current project
 - Version 0.9.23

#### 2011-04-01
 - Restored documentation lookup for internal and external commands
 - Version 0.9.22

#### 2011-03-31
 - [#47](https://github.com/BashSupport/BashSupport/issues/47): Lexer fixes to parse ${_a}
 - [#46](https://github.com/BashSupport/BashSupport/issues/46): Default value with parentheses not parsed correctly

#### 2011-03-29
 - Find usages enabled on function definitions

#### 2011-03-27
 - Improved performance of parsing files which contain include files

#### 2011-03-26
 - Inspection to detect array use of simple variables
 - Inspection to detect simple use of array variables
 - It is possible now to defined functions which have the same name as built-in Bash commands
 - Inspection to mark functions which override built-in Bash commands

#### 2011-03-22
 - (Almost) full support for arrays
 - Version 0.9.21

#### 2011-03-12
 - Improved array support

#### 2011-03-08
 - Fixed unused function inspection in files which reference elements in files which include the file

#### 2011-02-19
 - Fixed detection of deep recursive file include loops
 - Version 0.9.20

#### 2011-02-18
 - Ctrl+B on file references (e.g. in include command) is supported now
 - Issue [#35](https://github.com/BashSupport/BashSupport/issues/35): Math calculations using square brackets not supported

#### 2011-02-15
  - Improved autocompletion

#### 2011-02-12
 - Documentation lookup for variable definitions, the comment before the definition is displayed, if available
 - Fixed documentation lookup for external command
 - Fixed some invalid autocompletion suggestions
 - Version 0.9.19

#### 2011-02-07
 - Variable completion from included files
 - Improved autocompletion
 - Autocompletion inside of ${}
 - Globals and built-ins are now offered after second completion call, if enabled

#### 2011-02-06
 - Support for include files and contained elements (find references, rename, go to defintion, ...)

#### 2011-01-28
 - Issue [#32](https://github.com/BashSupport/BashSupport/issues/32): Length-function not supported: Expected a command

#### 2011-01-26
 - Issue [#34](https://github.com/BashSupport/BashSupport/issues/34): Unresolved variable in string definition
 - Issue [#35](https://github.com/BashSupport/BashSupport/issues/35): Math calculations using square brackets not supported
 - Issue [#36](https://github.com/BashSupport/BashSupport/issues/36): Incorrect inspection of unused parameter when using $* inside function

#### 2010-12-29
 - More inspection test cases
 - Bugfixes
 - Version 0.9.18

#### 2010-12-28
 - New inspection to convert a simple variable usage $a to the equivalent parameter expansion ${a}
 - New inspection to highlight unused parameter values for function calls
 - New inspection to highlight unused function definitions
 - Bugfix: A duplicate function definition on the global level is highlighted again
 - Changed file icon for path completions

#### 2010-12-23
 - Fixed exception which occured while editing the shebang line of a script
 - Inline renaming of variables inside of parameter expansion elements

#### 2010-12-16
 - Fixed parsing of HereDocs with single quote markers, i.e. started with <<'EOF'
 - Fixed parsing of variables in evaluating heredocs

#### 2010-12-10
 - No inserted whitespace after certain rename refactorings
 - Version 0.9.17

#### 2010-12-09
 - Find usages now is able to group by Read/Write access

#### 2010-12-03
 - Improved parsing of parameter substitutions, e.g. for ${a:=b} assignments and ${!a} indirect references
 - Formatter fixes

#### 2010-11-25
 - Version 0.9.16-ideaX

#### 2010-11-23
 - Compilation fixes to work with 98.402

#### 2010-11-02
 - Lexer fix for `for f in ...`

#### 2010-10-05
 - Removed use of class DefaultRefactoring provider, restored compatibility with latest IDEA X EAPs
 - Version 0.9.15-ideaX

#### 2010-08-12
 - Fixed redirection parsing
 - Version 0.9.14-maia

#### 2010-07-28
 - Fixed parsing of single $ characters, e.g. "$a$"
 - Improved parsing of for loops
 - Fixed resolving of global variables

#### 2010-07-23
 - Fixed exception which occured while adding a REPL console

#### 2010-07-22
 - Lexer improvements

#### 2010-07-21
 - Fixed new Bash script action if the extension .bash was used

#### 2010-07-20
 - Many improvements to arithmetic expression parsing

#### 2010-07-17
 - Fixed evaluation of advanced arithmetic expressions
 - Improvements to the lexer
 - More test cases

#### 2010-07-13
 - Finally fixed the variable resolving issues (hopefully)

#### 2010-07-12
 - Improved variable resolving if definitions on different levels are present

#### 2010-07-10
 - Implemented parsing of process substitution elements

#### 2010-07-08
 - Fixed variable resolving, once again

#### 2010-07-05
 - Version 0.9.13-maia

#### 2010-07-01
 - Fixed method not found error in EvaluateExpansionInspection

#### 2010-06-30
 - Refactoring to use the official API's psi walkThrough method
 - More PSI test cases for command variants, variable variants, heredoc marker resolving

#### 2010-06-29
 - Reworked and improved variable resolving

#### 2010-06-16
 - Fixed exception "Directory index is not initialized yet ..."

#### 2010-06-15
 - PSI resolve test cases

#### 2010-06-12
 - Code cleanup
 - Removed dead code

#### 2010-06-09
 - Fixed scope of psi elements, previously a variable rename went through the whole module
 - Improved support for local variable references
 - New inspection to detect local variable definition on global level
 - Quickfix to remove the local keyword from a global variable definition

#### 2010-06-06
 - Version 0.9.12-maia

#### 2010-06-05
 - Fixed parsing of for loops with optional command end after a command group
 - Fixed static evaluation quickfix to offer result for "$(((1+2)))"
 - Performance improvements, especially for offline code analysis
 - Cached oftenly performed calculation in arithmetic evaluation

#### 2010-06-03
 - Integrated custom exception reporting tool by Etienne Studer

#### 2010-05-29
 - "Add shebang" quickfix is not displayed for a script in the Bash REPL
 - Shortcut "Ctrl+Enter" for the repl console run action

#### 2010-05-27
 - Slightly Improved error reporting
 - Parser improvements for function definitions
 - Properly resetting PSI element caches now
 - Improved parsing of composed arithmetic number literals (e.g. 123$a)

#### 2010-05-26
 - Hex, octal and custom-base literals in arithmetic expressions

#### 2010-05-13
 - Variable name completion in evaluating heredocs
 - Some code cleanup and refactoring
 - Improved some inspection/quickfix messages

#### 2010-05-12
 - Smaller performance improvements
 - Improved bash console, still experimental

#### 2010-05-11
 - Improved evaluation of static arithmetic expressions
 - Improved parser (correctly parses "a=1 ((1))" now)
 - Less error markers in faulty case commands

#### 2010-05-10
 - Fixed error marker handling, no large red blocks any more
 - Inspection to detect invalid filedescriptors (only the range &0 to &9 is valid)

#### 2010-05-08
  - Support for the % operator in arithmetic expressions
  - Added a few more man pages
  - The quick documentation lookup (Ctrl+Q) now tries to read the current system's info page
    for an external command, if it is available (needs the commands "info" in the path,
    optionally "txt2html" to turn the plain text into html).

#### 2010-04-28
  - 0.9.11-maia

#### 2010-04-26
  - Fixed Java5 compilation
  - Fixed unsupported escape codes

#### 2010-04-24
  - Some performance tuning

  - 0.9.10-maia
  - Added "&-" to the lexer
  - Improved parsing and marking of redirect lists
  - Improved parsing of conditional commands

#### 2010-04-23
  - Reworked redirect parsing
  - Fixed ternary operator in arithmetic expressions

#### 2010-04-22
  - Improved redirect parsing
  - Fixed redirect marker error

#### 2010-04-21
  - New inspection to detect changes to shell read-only variables, $EUID for example
  - Fixed variable renaming of ${a} variables

#### 2010-04-20
  - Support for the "readonly" command
  - New inspection to detect changes to read-only variables
  - Possible fix for an exception caused by appending newlines at the end of a file

#### 2010-04-19
  - Added missing textedit box to manage custom shebang lines

#### 2010-04-16
  - Fixed exception at startup ("Directory index is not initialized yet for Project")
  - Fixed keywords as unquoted parameters to commands
  - Fixed some unit tests
  - Fixed errorneous whitespace inserted before a renamed variable
  - New inspection which detects integer division with a remainder

#### 2010-04-14
  - Fixed incorrectly inserted whitespace after renaming a variable
  - "Show documentation" fixed for internal commands in a subshell element

#### 2010-03-30
  - Version 0.9.9-maia
  - Fixed icon size
  - Added setting to turn off the formatter (defualt is formatter turned off)

#### 2010-03-28
 - Version 0.9.8-maia
 - Fixed shebang command completion
 - New file icon added, remved old file icon which was taken from the KDE project

#### 2010-03-25
 - Update to Google collections 1.0

#### 2010-03-24
 - Updated documentation and copyright information

#### 2010-03-18
 - Improved parsing for shebang lines

#### 2010-03-15
 - Icons for path and variable completions

#### 2010-03-13
 - Fixes "export a" variables

#### 2010-03-10
 - Heredoc parsing fixes
 - Heredoc folding fixes

#### 2010-03-09
 - Improved highlighting for heredocs, variables and other tokens are not highlighted any more

#### 2010-03-02
 - AddShebangQuickfix is now run in a separate write action

#### 2010-03-01
 - A process started withing a run script action is terminated when the stop button is pressed

#### 2010-02-28
 - The facet configuration tree is expanded by default now

#### 2010-02-19
 - Fixed ${!name}
 - Fixed echo \> a
 - Fixed nested evaluations inside of a parameter expansion block

#### 2010-02-18
 - Version 0.9.7-maia
 - Variable rename does not insert whitespace any more

#### 2010-02-17
 - Finished the facet configuration, if you want to handle files without extension as Bash scripts
   you have to add a Bash facet and configure your preferences
 - Removed global settings, these are project settings now

#### 2010-02-16
 - More work on the facet configuration

#### 2010-02-10
 - Fixed "export a" like variables
 - Fixed parsing of commands which only consist of an expansion

#### 2010-02-09
 - Fixed test cases
 - Fixed rename of heredoc markers
 - Fixed a marker error

#### 2010-02-08
 - Exported variables resolve again
 - Code completion exception fix
 - Renaming variables to invalid names fixed

#### 2010-02-07
 - Finished arithmetic expression parsing
 - New inspection and quickfix to replace a static expression with its result

#### 2010-02-06
 - Parsing of arithmetic expressions
 - Support for variables in arithmetic expressions, e.g. in arithmetic for loops

#### 2010-01-30
 - Heredoc markers support Go to definition, rename and Search usages now
 - Heredoc end markers parsing improved
 - Formatting fixes for heredocs
 - Highlight option for subshell commands

#### 2010-01-29
 - Color configuration for heredoc end marker
 - Color configuration for heredoc start marker
 - Improved parsing of heredocs
 - Default colors for heredoc elements
 - Improved color configuration dialog
 - Better default colors
 - Configuration option to enable "Guess file by content" for files without extension

#### 2010-01-28
 - Support for the local keyword
 - Support for local var resolving
 - Fixed duplicate var name suggeestions in completion popup

#### 2010-01-27
 - Updated JFlex version
 - Fixed parsing of invalid calls of the export command
 - Updated documentation
 - Small improvement to parameter expansion parsing

#### 2010-01-26
 - Fixed heredoc parsing

#### 2010-01-25
 - Improved highlighting of unresolved variables
 - Highlighting of variables inside of strings
 - New inspection to unregister global variables

#### 2010-01-24
 - Hack to improve support for script files without extension, might fail on some editions of IntelliJ / WebIDE / RubyMine
 - You can turn on debug mode by setting the environment variable bashsupport.debug to "true", useful for debugging

#### 2010-01-21
 - Support for simple variable substitutions, e.g. ${A}
 - exported variables work again

#### 2010-01-18
 - Fixed || and && operators in conditional commands

#### 2010-01-10
 - Variables in substitution blocks are marked now as variable references

#### 2009-12-28
 - Source code released, licensed under the terms of the Apache License 2.0

#### 2009-12-04
 - Version 0.9.6-maia

#### 2009-12-03
 - Code completion in file paths, works for absolute paths, relative in project and for ~/ and $HOME/
 - Fixed pipeline command marking, now with proper Ctrl+W selection
 - Fixed composed command marking, now with proper Ctrl+W selection

#### 2009-12-02
 - Improved variable parsing, especially for variable declaring commands
 - Fixed lexer to properly parse expressions like ""$((1))""
 - Bash v4 support for new case end markers ;& and ;;&
 - Bash v4 support: Support the enhanced syntax of brace expansions, e.g. (001..010..3)

#### 2009-12-01
 - Fixed expansion parsing for certain cases
 - Initial Bash v4 support, there's now a project wide setting to enable v4 support

#### 2009-11-26
  - Improved variable definition command parsing,
    things like ">out a=1 b=notSeen export b=1" are now properly parsed.

#### 2009-11-17
  - Fixed the "Unusual shebang" inspection

#### 2009-11-15
  - Improved the expansion parsing (supports a prefix now)

#### 2009-11-14
  - Use only the api provided by RubyMine as well
  - New inspection to replace an expansion with the evaluated result
  - Fixed invalid shebang replacement inspection highlighting

#### 2009-11-10
  - Ctrl+Q on function definitions and calls now shows the comment block right
    before the definition of the requested function

#### 2009-11-07
  - Version 0.9.5-maia
  - Backquote formatting
  - Variable substitution formatting

#### 2009-11-06
  - Formatting fixes
  - code like "$ (a)" is now properly marked as invalid

#### 2009-11-05
  - Regression: Commands defining variables did not work anymore
  - Improved lexing of whitespace sensitive tokens (e.g. "[[ a    ]]")
  - Basic formatter support, still needs work
  - Unit tests for the formatting

#### 2009-11-02
  - Expressions like "[ $(uname -a) = 'SunOs' ]" are properly parsed now
  - Inspection to check for missing file in inclusion (". missingFile")
  - Inspection to detect recursive file inclusions (". a.txt" in file a.sh)

#### 2009-11-01
  - Fixed some false positives of the "Duplicate function definition" inspection.
  - Compatibility with the other IntelliJ editions.

#### 2009-10-31
  - Run configuration for Bash scripts. You can now run the scripts from within IntelliJ
  - Added hyperlinks to run-configuration output
  - Inspection to detect double function definitions
  - New highlighting option for built-in variables
  - Updated default colors for variables and built-in variables

#### 2009-10-30
  - Version 0.9.4-maia
  - Documentation lookup working now for the read command

#### 2009-10-29
  - Added GUI setting to manage global variables
  - Registered global variables are shown in autocompletion (project  level configuration)
  - Added quickfix to register an unresolved variable as global variable (removes the error marker)
  - Added quickfix to unregister an unresolved variable as global variable (add the error marker back in)

#### 2009-08-19
  - Slightly improved support for internal command's options. Fixes issues with read
    command options.

#### 2009-08-07
  - Variables declared inside of for, select and while statements are now available on the outside
  - Smaller performance improvements (var resolving, caching)

  - Version 0.9.3

#### 2009-08-06
  - Variables inside of conditional commands are resolved now
  - Redirects after variable declarations using read, declare and export work now

#### 2009-08-04
  - Added documentation for Bash's break command

#### 2009-08-01
  - Variable definitions are now found in function definitions, as well.
  - Variable definitions contained in case commands are now found, too
  - || and && are now correctly parsed inside of subshell expressions contained in strings

#### 2009-07-28
  - A valid shebang line may now have newlines in front
  - Shebang highlighting works again
  - Fixed parsing of line continuations
  - Default formatting of "Shebang comment" is now comment formatting + bold font style

#### 2009-07-25
  - Version 0.9.2

#### 2009-07-24
  - Support for shebang-like comments which are not the first line of a file
  - Support for (simple) variable declarations inside of arithmetic expressions

#### 2009-07-15
  - Fixed lexing of "\\!"
  - Fixed "assert" exception during string parsing

#### 2009-06-27
  - Version 0.9.1

#### 2009-06-20
  - Fixed parsing of "$( ( echo a ) )", whitespace and parenths are now parsed

#### 2009-06-06
  - $0, $1 etc. werden erkannt

#### 2009-05-31
  - "Fix unusual shebang lines" doesn't suggest alternatives for valid commands any more
  - FixBacktick command inspection has now default level "warn"
  - Highlighting of Strings works again

#### 2009-05-30
  - Variables and $-syntax elements like subshells inside of strings are parsed

#### 2009-05-27
  - Version 0.9
  - Removed usage of String.isEmpty() to make it work with Java 5 again
  - Autocompletion for function names
  - Fixed verbose logging
  - Option in settings to turn on/off autocompletion for builtin variables, disabled by default
  - Option in settings to turn on/off autocompletion for builtin commands, enabled by default

#### 2009-05-26
  - Autocompletion for variables, shows variables which have been defined previously
  - Undeclared variables are marked as errors, names of built-in variables are checked first

#### 2009-05-23
  - Support for variable defs by a "read" command

#### 2009-05-21
  - Version 0.8.7
  - Shift+F1 now works for external commands, opens online man page in the browser
  - Inspection "fix shebang" is now configurable. Needs some more usability work.
  - New inspection to convert backtick commands into subshell command
  - New inspection to convert subshell commands into backtick command, disabled by default
  - New inspection "Convert into String", disabled by default
  - Fixed Ctrl+Q for arithmetic for loops
  - Fixed failing inspection init
  - Removed verbose logging of file loading
  - Refactored out "Add shebang" inspection

#### 2009-05-15
  - Converted old inspections into configurable inspections
  - Fixed wrap body quickfix

  - Version 0.8.6

#### 2009-05-13
  - Support for variable references like ${a}
  - Fixed verbose info messages

#### 2009-05-12
  - Fix for rename refactoring of a=1
  - Support for a+=a
  - Support for files without .sh/.bash extensions

  - Version 0.8.5
  - Replaced ArrayDeque by LinkedList for Java 5 compatibility
  - Fixed assignment parsing marker problem

#### 2009-05-06
  - Added find usages provider (Alt+F7), works for functions and variables
  - Ctrl+Q for keywords if, while, for, if, case, select, time

#### 2009-05-05
  - Fixed command text range. Ctrl+Q now only works on the command element and shows the right
    element name.
  - Fixed Ctrl+Q for "." and ":"

#### 2009-05-03
  - Version 0.8
  - Added documentation for internal bash commands
  - Added documentation for most common external commands (awk, sed, gcc, ...)
  - Fixed Ctrl+Q for internal bash commands

#### 2009-05-02
  - Fixed lexing of "=" in expansions
  - Fixed lexing of "\#a\n", it's now command followed by a line feed
  - Fixed lexing \?
  - Fixed lexing of "[ a  ]"
  - Fixed lexing of "a$a" (now it's a word and a variable)
  - Fixed parsing of "case $a ..."
  - Shebang quickfix supports leading and trailing spaces in the command
  - Internal refactoring, moving towards "chain of responsiblities" pattern

  - Version 0.7.1 with the runner fix
  - Removed program runner from config

#### 2009-05-01
  - Version 0.7
  - Support assignments of the "export" command
  - Added "Inline rename"
  - Fixed lookup of variable definition (first occurence is used)

#### 2009-04-31
  - Fixed assignments like "i=1 j=2"
  - Fixed "go to definition" for assignments, jumps now to the first assignment, if there's any

#### 2009-04-30
  - Fixed "Create new Bash file" action
  - Added "Fix shebang" quickfix
  - Added "Fix function body curly brackets" quickfix

#### 2009-04-26
  - Versio 0.6.5
  - Fixed some more parsing

#### 2009-04-21
  - Improved parsing (combined words, command groups, parameter expansion, here docs)
  - All sample scripts are parsing now, all tests are passing

#### 2009-04-18
  - Support for nested backquotes in conditional expressions, e.g. [ -z `test -z a` ]
  - Fixed issues with whitespace, e.g. "a= a" is now an empy assignment and a command

#### 2009-04-17
  - Version 0.6
  - Improved parsing of strings with embedded substrings (in subshell commands)
  - Improved parsing of ${} constructs

#### 2009-04-16
  - Fixed "Go to declaration" so local assignments are ignored, e.g. "a=1 echo $a"
  - "Rename..." refactoring for functions
  - "Rename..." refactoring for variables
  - Minor improvement for code folding

#### 2009-04-15
  - Added basic support for "declare" variable declarations
  - Support for multiline strings

  - Version 0.5
  - Added "Go to declaration" for variables
  - Added "Highlight usages" for variables
  - Variable declarations of for loops are used
  - Variable declarations of for select loops are used
  - Variable declaration highlighting
  - Variable usage highlighting

#### 2009-04-14
  - Fixed parsing of \* character

#### 2009-04-13
  - Fixed parsing of nested commands inside of a case statement
  - Fixed parsing of arithmetic for loops
  - Fixed parsing of $*
  - Fixed parsing of tokens inside of assignment lists, e.g. a=(a=b)

  - Version 0.4
  - Support for $"a" und $'a'
  - Fix: Parses supports now array assignments and assignment lists
  - Parsing von a=(1,2,"3")

#### 2009-04-12
  - Structure view for bash files, shows functions
  - Highlighting for internal bash commands
  - Highlighting for external commands
  - Go to definition works for function names, now
  - Show quick definition works for functions now
  - Beginnings of Ctrl+Q (Show documentation) support

#### 2009-04-12
  - Version 0.3

#### 2009-04-11
  - Support for proper backquote parsing
  - Basic parsing for here docs
  - Fixed parsing error for function definitions
  - Custom highlighting for here documents
  - Custom highlighting for backquotes
  - Custom highlighting for String2 '...'
  - Statements like "echo a=b" are properly parsed now
  - Statements like "echo [123]" are properly parsed now
  - Shell commands with redirection list are now parsed properly
  - Better case pattern matching

#### 2009-03-31
  - Version 0.2

#### 2009-03-29
  - Improved parser for things like $((i=$(echo 12)))

#### 2009-03-27
  - Syntax highlighting color configuration page
  - Code folding support for case patter+command
  - Version 0.1
