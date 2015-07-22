/**
 * ****************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ListParsing.java, Class: ListParsing
 * Last modified: 2011-04-30 16:33
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.ansorgit.plugins.bash.util.NullMarker;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Date: 25.03.2009
 * Time: 11:28:38
 *
 * @author Joachim Ansorg
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
        return token == LINE_FEED || token == SEMI || token == null; //fixme null right for eof?
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
   list:		newline_list list0 ;

   compound_list:
           list
       |   newline_list list1
       ;

   list0:  	list1 '\n' newline_list
       |	list1 '&' newline_list
       |	list1 ';' newline_list
        ;
    */

    public boolean parseCompoundList(BashPsiBuilder builder, boolean optionalTerminator, boolean markAsFoldable) {
        PsiBuilder.Marker optionalMarker = markAsFoldable ? builder.mark() : NullMarker.get();

        builder.eatOptionalNewlines(1);
        builder.eatOptionalNewlines();

        //this is the list0 parsing here
        if (!parseList1(builder, false, true, RecursionGuard.initial())) {
            optionalMarker.drop();

            return false;
        }

        //now either a \n, & or ;
        final IElementType token = builder.getTokenType();

        //in contrast to the grammar we assume that compound_list is terminated
        if (token == SEMI || token == LINE_FEED || token == AMP) {
            optionalMarker.done(BLOCK_ELEMENT);

            builder.advanceLexer();
            builder.eatOptionalNewlines();

            return true;
        }

        optionalMarker.done(BLOCK_ELEMENT);

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
    //fixme refactor this to include markers, take care of recursive calls

    boolean parseList1(BashPsiBuilder builder, boolean simpleMode, boolean markComposedCommand, RecursionGuard recursionGuard) {
        if (!recursionGuard.next(builder)) {
            return false;
        }

        if (!Parsing.pipeline.isPipelineCommand(builder)) {
            builder.error("Expected a command");
            return false;
        }

        //used only to mark composed commands which combine several commands, not for single commands or a command list
        PsiBuilder.Marker composedMarker = markComposedCommand ? builder.mark() : NullMarker.get();

        if (!Parsing.pipeline.parsePipelineCommand(builder)) {
            composedMarker.drop();
            return false;
        }

        if (builder.eof()) {
            composedMarker.drop();
            return true;
        }

        boolean result = true;

        final IElementType token = builder.getTokenType();
        if (token == AND_AND || token == OR_OR) {
            builder.advanceLexer();
            builder.eatOptionalNewlines();
            result = parseList1(builder, simpleMode, false, recursionGuard); //with errors

            composedMarker.done(COMPOSED_COMMAND);
        } else if (token == AMP || token == LINE_FEED || token == SEMI) {
            boolean hasHeredoc = parseOptionalHeredoc(builder);

            if (builder.getTokenType() == LINE_FEED && simpleMode) {
                composedMarker.drop();
                return true;
            }

            final PsiBuilder.Marker start = builder.mark();
            builder.advanceLexer();
            builder.eatOptionalNewlines();

            if (!Parsing.pipeline.isPipelineCommand(builder)) {
                //not followed by a command, return true
                //the AMP is taken by parseCompoundList
                start.rollbackTo();

                if (hasHeredoc) {
                    composedMarker.done(COMPOSED_COMMAND);
                } else {
                    composedMarker.drop();
                }

                return true;
            }

            start.drop();

            if (hasHeredoc) {
                composedMarker.done(COMPOSED_COMMAND);
            } else {
                composedMarker.drop();
            }

            result = parseList1(builder, simpleMode, false, recursionGuard);
        } else {
            composedMarker.drop();

            //this can happen if we have a valid command start, e.g. ">1" of the (invalid) sequence ">1 ((1))".
            //">1" is valid and was successfully parsed, now the current token is (( now
            //in this case we have to fail because the token is not expected here
            if (token != null && simpleMode) {
                ParserUtil.errorToken(builder, "parser.unexpected.token");
                return false;
            }
        }

        return result;
    }

    /**
     * Parses an optional heredoc starting at the current position.
     * Problem is that a heredoc might start with a variable. But that variable might also be part of the
     * next command. The lexer state is not available here so we need to look ahead at a limited amount
     * of following tokens.
     *
     * @param builder
     * @return True if a heredoc was parsed, false if no heredoc was found
     */
    private boolean parseOptionalHeredoc(BashPsiBuilder builder) {
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
                    } else if (Parsing.var.isValid(builder)) {
                        if (!Parsing.var.parse(builder)) {
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

                if (builder.getTokenType() == HEREDOC_MARKER_END) {
                    ParserUtil.markTokenAndAdvance(builder, HEREDOC_END_ELEMENT);
                    builder.getParsingState().popHeredocMarker();
                } else {
                    if (builder.getParsingState().expectsHeredocMarker()) {
                        builder.error("Unexpected token");
                    }

                    break;
                }
            } while (builder.getParsingState().expectsHeredocMarker());

            // return true;
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
        if (!parseList1(builder, true, true, RecursionGuard.initial())) {
            return false;
        }

        //optional & or ; at the end
        final IElementType tokenType = builder.getTokenType();
        if (tokenType != null && (tokenType == AMP || tokenType == SEMI)) {
            builder.advanceLexer();
        }

        return true;
    }
}
