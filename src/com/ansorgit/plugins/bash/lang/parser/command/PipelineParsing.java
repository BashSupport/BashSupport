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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * @author jansorg
 */
public class PipelineParsing implements ParsingTool {
    /*
    pipeline_command:
           pipeline
       |	BANG pipeline
       |	timespec pipeline
       |	timespec BANG pipeline
       |	BANG timespec pipeline
       |	timespec list_terminator
       ;

    pipeline:	command2 ('|' newline_list command2)*
       ;
    */
    private TimespecState parseOptionalTimespec(BashPsiBuilder builder) {
        boolean hasTimespec = builder.getTokenType() == TIME_KEYWORD;
        if (hasTimespec && !parseTimespecPart(builder)) {
            return TimespecState.Error;
        }

        return hasTimespec ? TimespecState.Ok : TimespecState.NotAvailable;
    }

    public OptionalParseResult parsePipelineCommand(BashPsiBuilder builder, boolean errorOnMissingCommand) {
        if (builder.eof() || Parsing.list.isListTerminator(builder.getTokenType())) {
            return OptionalParseResult.Invalid;
        }

        PsiBuilder.Marker pipelineCommandMarker = builder.mark();

        boolean hasBang = ParserUtil.isWord(builder, "!");
        if (hasBang) { //the bang is optional
            builder.advanceLexer();
        }

        TimespecState timespecState = parseOptionalTimespec(builder);
        if (timespecState.equals(TimespecState.Error)) {
            pipelineCommandMarker.drop();
            return OptionalParseResult.ParseError;
        }

        if (!hasBang && ParserUtil.isWord(builder, "!")) {
            builder.advanceLexer(); //read the bang token we found
            hasBang = true;

            if (timespecState.equals(TimespecState.NotAvailable)) {
                timespecState = parseOptionalTimespec(builder);
            }
        }

        if (Parsing.list.isListTerminator(builder.getTokenType())) {
            //an empty command is only allowed if it is a time command spec without arguments

            if (!hasBang && timespecState.equals(TimespecState.Ok)) {
                //an empty timespec is only valid if no bang token was used
                builder.advanceLexer();
                pipelineCommandMarker.drop();

                return OptionalParseResult.Ok;
            } else {
                if (errorOnMissingCommand) {
                    builder.error("Expected a command.");
                }
                pipelineCommandMarker.drop();
                builder.advanceLexer();

                return OptionalParseResult.ParseError;
            }
        }

        ParseState parseState = parsePipeline(builder);
        switch (parseState) {
            case OK_PIPELINE:
                pipelineCommandMarker.done(PIPELINE_COMMAND);
                return OptionalParseResult.Ok;
            case OK_NO_PIPELINE:
                pipelineCommandMarker.drop();
                return OptionalParseResult.Ok;
            case ERROR:
                pipelineCommandMarker.drop();
                if (errorOnMissingCommand) {
                    builder.error("Expected a command.");
                }
                return OptionalParseResult.ParseError;
            default:
                pipelineCommandMarker.drop();
                throw new IllegalStateException("Unexpected ParseState value" + parseState);
        }
    }

    /**
     * Parses a pipeline command. Marks if at least one pipeline is found. Does NOT mark
     * if just one command or an error has been found.
     *
     * @param builder The builder to use
     * @return True if no errors occured
     */
    private ParseState parsePipeline(BashPsiBuilder builder) {
        OptionalParseResult result = Parsing.command.parseIfValid(builder);
        if (!result.isParsedSuccessfully()) {
            return ParseState.ERROR;
        }

        boolean withPipeline = pipeTokens.contains(builder.getTokenType());
        if (!withPipeline) {
            return ParseState.OK_NO_PIPELINE;
        }

        while (result.isParsedSuccessfully() && pipeTokens.contains(builder.getTokenType())) {
            builder.advanceLexer(); //eat the pipe token
            builder.readOptionalNewlines();

            result = Parsing.command.parseIfValid(builder);
        }

        if (!result.isParsedSuccessfully()) {
            return ParseState.ERROR;
        }

        return ParseState.OK_PIPELINE;
    }

    boolean parseTimespecPart(BashPsiBuilder builder) {
        final PsiBuilder.Marker time = builder.mark();

        final IElementType timeToken = ParserUtil.getTokenAndAdvance(builder);
        if (timeToken != TIME_KEYWORD) {
            ParserUtil.error(time, "parser.time.expected.time");
            return false;
        }

        if (ParserUtil.isWordToken(builder.getTokenType()) && "-p".equals(builder.getTokenText())) {
            builder.advanceLexer();
        }

        time.done(TIME_COMMAND);
        return true;
    }

    private enum ParseState {
        OK_PIPELINE,
        OK_NO_PIPELINE,
        ERROR,
    }

    private enum TimespecState {
        Ok, NotAvailable, Error
    }

}
