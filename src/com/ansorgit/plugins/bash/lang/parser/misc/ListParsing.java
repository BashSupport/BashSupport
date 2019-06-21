/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.ansorgit.plugins.bash.util.NullMarker;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * @author jansorg
 */
public final class ListParsing implements ParsingTool {
    /* Grammar:
        simple_list_terminator:	'\n' //or eof
            ;

        newline_list	:  '\n*	;
    */

    /*
        list_terminator :
        	'\n'
        | 	';'  //eof
        ;
     */

    public boolean isListTerminator(IElementType token) {
        return token == LINE_FEED || token == SEMI || token == null;
    }

    public boolean isSimpleListTerminator(IElementType token) {
        return token == LINE_FEED || token == null;
    }


    /**
     * Parses a compound list with required command list terminator.
     *
     * @param builder
     * @param markAsFoldable
     * @return
     */
    public boolean parseCompoundList(BashPsiBuilder builder, boolean markAsFoldable) {
        return parseCompoundList(builder, false, markAsFoldable);
    }

    /*
   list0:  	list1 '\n' newline_list
       |	list1 '&' newline_list
       |	list1 ';' newline_list
        ;

   list:		newline_list list0 ;

   compound_list:
           list
       |   newline_list list1
       ;
    */

    public boolean parseCompoundList(BashPsiBuilder builder, boolean optionalTerminator, boolean markAsFoldable) {
        PsiBuilder.Marker optionalMarker = markAsFoldable ? builder.mark() : NullMarker.get();

        //builder.readOptionalNewlines(1);
        builder.readOptionalNewlines();

        //this is the list0 parsing here
        if (!parseList1(builder, false, true)) {
            optionalMarker.drop();

            return false;
        }

        //now either a \n, & or ;
        final IElementType token = builder.getTokenType();

        //in contrast to the grammar we assume that compound_list is terminated
        if (token == SEMI || token == LINE_FEED || token == AMP) {
            optionalMarker.done(LOGICAL_BLOCK_ELEMENT);

            builder.advanceLexer();
            builder.readOptionalNewlines();

            return true;
        }

        optionalMarker.done(LOGICAL_BLOCK_ELEMENT);

        return builder.eof() || optionalTerminator;
    }

    public boolean parseList(BashPsiBuilder builder) {
        return parseCompoundList(builder, false, false);
    }

    /*
    list1:	list1 AND_AND newline_list list1
        |	list1 OR_OR newline_list list1
        |	list1 '&' newline_list list1
        |	list1 ';' newline_list list1
        |	list1 '\n' newline_list list1
        |	pipeline_command
        ;
     */
    boolean parseList1(BashPsiBuilder builder, boolean simpleMode, boolean markComposedCommand) {
        //used only to mark composed commands which combine several commands, not for single commands or a command list
        PsiBuilder.Marker startMarker = markComposedCommand ? builder.mark() : NullMarker.get();

        boolean success = parseList1Element(builder, true);
        boolean markCommand = success;

        while (success) {
            IElementType next = builder.getTokenType();

            if (next == AND_AND || next == OR_OR) {
                builder.advanceLexer();
                parseOptionalHeredocContent(builder);
                builder.readOptionalNewlines();

                success = parseList1Element(builder, true);
                markCommand = success;
            } else if (next == SEMI || next == LINE_FEED || next == AMP) {
                if (builder.getParsingState().expectsHeredocMarker() && next != LINE_FEED) {
                    builder.advanceLexer();
                }

                boolean hasHeredoc = parseOptionalHeredocContent(builder);

                //the next token after the heredoc, not the variable "next" !
                if (builder.getTokenType() == LINE_FEED && simpleMode) {
                    markCommand = hasHeredoc;
                    success = true;
                    break;
                } else {
                    if (hasHeredoc && builder.getTokenType() != LINE_FEED && !builder.eof()) {
                        //the heredoc end marker might be followed by a backtick, for example
                        //we must return to the outer parsing function without taking those tokens
                        markCommand = true;
                        break;
                    }

                    PsiBuilder.Marker start = builder.mark();

                    builder.advanceLexer();
                    builder.readOptionalNewlines();

                    success = parseList1Element(builder, false);
                    if (success) {
                        start.drop();
                    } else {
                        start.rollbackTo();
                        success = true;
                        markCommand = hasHeredoc;
                        break;
                    }
                }
            } else {
                markCommand = false;

                //this can happen if we have a valid command start, e.g. ">1" of the (invalid) sequence ">1 ((1))".
                //">1" is valid and was successfully parsed, now the current token is (( now
                //in this case we have to fail because the token is not expected here
                if (next != null && simpleMode) {
                    ParserUtil.errorToken(builder, "parser.unexpected.token");
                    success = false;
                }
                break;
            }
        }

        if (markCommand) {
            startMarker.done(COMPOSED_COMMAND);
        } else {
            startMarker.drop();
        }

        return success;
    }

    private boolean parseList1Element(BashPsiBuilder builder, boolean errorOnMissingCommand) {
        OptionalParseResult result = Parsing.pipeline.parsePipelineCommand(builder, errorOnMissingCommand);
        if (!result.isValid()) {
            if (errorOnMissingCommand) {
                builder.error("Expected a command");
            }

            return false;
        }

        return result.isParsedSuccessfully();
    }

    /**
     * Parses an optional heredoc starting at the current position.
     * Problem is that a heredoc might start with a variable. But that variable might also be part of the
     * next command. The lexer state is not available here so we need to look ahead at a limited amount
     * of following tokens.
     *
     * @param builder Bash psi builder
     * @return True if a heredoc was parsed, false if no heredoc was found
     */
    private boolean parseOptionalHeredocContent(BashPsiBuilder builder) {
        if (builder.getTokenType() == LINE_FEED && builder.getParsingState().expectsHeredocMarker()) {
            int startOffset = builder.getCurrentOffset();

            //eat the newline
            builder.advanceLexer();

            // Parse here documents at this place. They follow a statement which opened one.
            // Several here-docs can be combined and will be all parsed by this loop
            do {
                //the simple case is a heredoc element followed by a heredoc-end marker
                while (true) {
                    if (builder.getTokenType() == LINE_FEED) {
                        builder.advanceLexer();
                    } else if (builder.getTokenType() == HEREDOC_CONTENT) {
                        ParserUtil.markTokenAndAdvance(builder, HEREDOC_CONTENT_ELEMENT);
                    } else {
                        OptionalParseResult varResult = Parsing.var.parseIfValid(builder);
                        if (varResult.isValid()) {
                            if (!varResult.isParsedSuccessfully()) {
                                break;
                            }
                        } else if (Parsing.shellCommand.subshellParser.isValid(builder)) {
                            if (!Parsing.shellCommand.subshellParser.parse(builder)) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }

                if (builder.getTokenType() == HEREDOC_MARKER_END) {
                    ParserUtil.markTokenAndAdvance(builder, HEREDOC_END_ELEMENT);
                    builder.getParsingState().popHeredocMarker();
                } else if (builder.getTokenType() == HEREDOC_MARKER_IGNORING_TABS_END) {
                    ParserUtil.markTokenAndAdvance(builder, HEREDOC_END_IGNORING_TABS_ELEMENT);
                    builder.getParsingState().popHeredocMarker();
                } else {
                    if (builder.getParsingState().expectsHeredocMarker()) {
                        builder.error("Unexpected token");
                    }

                    break;
                }
            } while (builder.getParsingState().expectsHeredocMarker());

            return builder.getCurrentOffset() - startOffset > 0;
        }

        return false;
    }

    /**
     * A simple list is like a list1
     *
     * @param builder The BashPsiBuilder
     * @return true if the parsing had no errors
     */
    public boolean parseSimpleList(BashPsiBuilder builder) {
        /*
        simple_list:
                simple_list1
        |       simple_list1 '&'
        |       simple_list1 ';'
        ;
        */

        /*
        simple_list1:
            simple_list1 AND_AND newline_list simple_list1
        |   simple_list1 OR_OR newline_list simple_list1
        |   simple_list1 '&' simple_list1
        |   simple_list1 ';' simple_list1
        |   pipeline_command
        ;
        */

        // this is the simpleList1 parsing
        if (!parseList1(builder, true, true)) {
            return false;
        }

        //optional & or ; at the end
        final IElementType tokenType = builder.getTokenType();
        if (tokenType == AMP || tokenType == SEMI) {
            builder.advanceLexer();
        }

        return true;
    }
}
