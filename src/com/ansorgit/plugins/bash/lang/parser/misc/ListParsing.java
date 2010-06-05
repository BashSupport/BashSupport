/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ListParsing.java, Class: ListParsing
 * Last modified: 2010-06-05
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Date: 25.03.2009
 * Time: 11:28:38
 *
 * @author Joachim Ansorg
 */
public class ListParsing implements ParsingTool {
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
     * Returns whether the builder is at a position which starts a compound list.
     *
     * @param builder            The psi builder to use.
     * @param optionalTerminator
     * @return True if a compound list start here.
     */
    public boolean isCompoundList(BashPsiBuilder builder, final boolean optionalTerminator) {
        final PsiBuilder.Marker start = builder.mark();
        try {
            builder.enterNewErrorLevel(false);
            return parseCompoundList(builder, optionalTerminator, false);
        } finally {
            start.rollbackTo();
            builder.leaveLastErrorLevel();
        }
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
        PsiBuilder.Marker optionalMarker = markAsFoldable ? builder.mark() : null;

        builder.eatOptionalNewlines(1);
        builder.eatOptionalNewlines();

        //this is the list0 parsing here
        if (!parseList1(builder, false, true)) {
            if (optionalMarker != null) {
                optionalMarker.drop();
            }

            return false;
        }

        //now either a \n, & or ;
        final IElementType token = builder.getTokenType();

        //in contrast to the grammar we assume that compound_list is terminated
        if (token == SEMI || token == LINE_FEED || token == AMP) {
            if (optionalMarker != null) {
                optionalMarker.done(BLOCK_ELEMENT);
            }

            builder.advanceLexer();
            builder.eatOptionalNewlines();

            return true;
        }

        if (optionalMarker != null) {
            optionalMarker.done(BLOCK_ELEMENT);
        }

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

    public boolean parseList1(BashPsiBuilder builder, boolean simpleMode, boolean markComposedCommand) {
        //used only to mark composed commands which combine several commands, not for single commands
        //or a command list
        PsiBuilder.Marker composedMarker = builder.mark();

        if (!Parsing.pipeline.parsePipelineCommand(builder)) {
            composedMarker.drop();
            builder.error("Expected a command.");
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
            result = parseList1(builder, simpleMode, false); //with errors

            if (markComposedCommand) {
                composedMarker.done(COMPOSED_COMMAND);
            } else {
                composedMarker.drop();
            }
        } else if (token == AMP || token == LINE_FEED || token == SEMI) {
            IElementType current = token;
            if (current == LINE_FEED) {
                //parse here documents at this place. They follow a statement which opened one.
                // Several here-docs can be combined
                if (Parsing.hereDoc.parseOptionalHereDocs(builder)) {
                    current = builder.getTokenType();
                }
            }

            if (current == LINE_FEED && simpleMode) {
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
                composedMarker.drop();
                return true;
            }

            start.drop();
            composedMarker.drop();
            result = parseList1(builder, simpleMode, false);
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
     * A simple list is like a list1
     *
     * @param builder
     * @return
     */
    public boolean parseSimpleList(BashPsiBuilder builder) {
        /*
        simple_list:
                simple_list1
        |       simple_list1 '&'
        |       simple_list1 ';'
        ;
         */

        if (!parseSimpleList1(builder)) {
            return false;
        }

        //optional & or ; at the end
        final IElementType tokenType = builder.getTokenType();
        if (tokenType != null && (tokenType == AMP || tokenType == SEMI)) {
            builder.advanceLexer();
        }

        return true;
    }

    /*
        simple_list1:
            simple_list1 AND_AND newline_list simple_list1
        |   simple_list1 OR_OR newline_list simple_list1
        |   simple_list1 '&' simple_list1
        |   simple_list1 ';' simple_list1
        |   pipeline_command
        ;
     */

    boolean parseSimpleList1(BashPsiBuilder builder) {
        return parseList1(builder, true, true);
    }
}
