/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: CaseParsingFunction.java, Class: CaseParsingFunction
 * Last modified: 2010-02-09
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Date: 02.05.2009
 * Time: 11:20:22
 *
 * @author Joachim Ansorg
 */
public class CaseParsingFunction extends DefaultParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.CaseCommandParsingFunction");

    private static enum CaseParseResult {
        Faulty, SingleElement, ElementWithEndMarker
    }

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == CASE_KEYWORD;
    }

    /**
     * Parse the case command.
     *
     * @param builder
     * @return
     */
    public boolean parse(BashPsiBuilder builder) {
        /*
       case_command:
               CASE WORD newline_list IN newline_list ESAC
           |	CASE WORD newline_list IN case_clause_sequence newline_list ESAC
           |	CASE WORD newline_list IN case_clause ESAC
           ;

       case_clause:
               pattern_list
           |	case_clause_sequence pattern_list
           ;

       pattern_list:	newline_list pattern ')' compound_list
           |	newline_list pattern ')' newline_list
           |	newline_list '(' pattern ')' compound_list
           |	newline_list '(' pattern ')' newline_list
           ;

       case_clause_sequence:  pattern_list SEMI_SEMI
           |	case_clause_sequence pattern_list SEMI_SEMI
           ;


        Bash 4.0 changes:
        ee. The new `;&' case statement action list terminator causes execution to
            continue with the action associated with the next pattern in the
            statement rather than terminating the command.

        ff. The new `;;&' case statement action list terminator causes the shell to
            test the next set of patterns after completing execution of the current
            action, rather than terminating the command.
        */
        log.assertTrue(isValid(builder));
        final PsiBuilder.Marker caseCommand = builder.mark();

        builder.advanceLexer();//after the "case"

        if (!Parsing.word.parseWord(builder)) {
            ParserUtil.error(caseCommand, "parser.unexpected.token");
            return false;
        }

        //after the word token
        builder.eatOptionalNewlines();

        final IElementType inToken = ParserUtil.getTokenAndAdvance(builder);
        if (inToken != IN_KEYWORD) {
            ParserUtil.error(caseCommand, "parser.unexpected.token");
            return false;
        }

        builder.eatOptionalNewlines();
        if (builder.getTokenType() == ESAC_KEYWORD) {
            builder.advanceLexer();
            caseCommand.done(CASE_COMMAND);

            return true;
        }

        //now parse the case_clause_sequence
        CaseParseResult hasPattern = parsePatternList(builder);
        if (hasPattern == CaseParseResult.Faulty) {
            log.debug("Could not find first case pattern");
            ParserUtil.error(caseCommand, "parser.unexpected.token");
            return false;
        }


        while (hasPattern != CaseParseResult.Faulty) {
            //another pattern may follow
            if (hasPattern == CaseParseResult.ElementWithEndMarker) {
                builder.eatOptionalNewlines();
                if (builder.getTokenType() == ESAC_KEYWORD) {
                    break;
                }

                hasPattern = parsePatternList(builder);
            } else { //no more patterns possible
                break;
            }
        }

        //now check for ESAC

        final IElementType endToken = ParserUtil.getTokenAndAdvance(builder);
        if (endToken != ESAC_KEYWORD) {
            ParserUtil.error(caseCommand, "parser.unexpected.token");
            return false;
        }

        caseCommand.done(CASE_COMMAND);
        return true;
    }

    /**
     * Parses a pattern list.
     */
    CaseParseResult parsePatternList(BashPsiBuilder builder) {
        /*
        pattern_list:
                newline_list pattern ')' compound_list
           |	newline_list pattern ')' newline_list
           |	newline_list '(' pattern ')' compound_list
           |	newline_list '(' pattern ')' newline_list
           ;
         */

        builder.eatOptionalNewlines();

        final PsiBuilder.Marker casePattern = builder.mark();

        if (builder.getTokenType() == LEFT_PAREN) {
            builder.advanceLexer();
        }

        //parse case pattern
        if (!readCasePattern(builder)) {
            ParserUtil.error(casePattern, "parser.unexpected.token");
            return CaseParseResult.Faulty;
        }

        //parse closing bracket
        final IElementType closingBracket = ParserUtil.getTokenAndAdvance(builder);
        if (closingBracket != RIGHT_PAREN) {
            ParserUtil.error(casePattern, "parser.unexpected.token");
            return CaseParseResult.Faulty;
        }

        //parse compoundlist or newline list
        builder.eatOptionalNewlines();

        //hack to check if we have some commands in the pattern
        if (builder.getTokenType() != CASE_END && builder.getTokenType() != ESAC_KEYWORD) {
            boolean parsed = Parsing.list.parseCompoundList(builder, true, false, true);
            if (!parsed) {
                ParserUtil.error(casePattern, "parser.unexpected.token");
                return CaseParseResult.Faulty;
            }
        }

        boolean hasEndMarker = builder.getTokenType() == CASE_END;
        if (hasEndMarker) {
            builder.advanceLexer();

            IElementType nextToken = builder.getTokenType(true);
            if (builder.isBash4() && nextToken == AMP) {
                builder.advanceLexer(true);
            }
        }

        casePattern.done(CASE_PATTERN_LIST_ELEMENT);
        return hasEndMarker ? CaseParseResult.ElementWithEndMarker : CaseParseResult.SingleElement;
    }

    boolean readCasePattern(BashPsiBuilder builder) {
        /*
        pattern:
                WORD
        	|	pattern '|' WORD
	        ;
         */

        final PsiBuilder.Marker pattern = builder.mark();

        final boolean wordParsed = Parsing.word.parseWord(builder);
        if (!wordParsed) {
            pattern.drop();
            return false;
        }

        //fixme mark this
        while (builder.getTokenType() == PIPE) {
            builder.advanceLexer();

            final boolean myWordParsed = Parsing.word.parseWord(builder);
            if (!myWordParsed) {
                pattern.drop();
                return false;
            }
        }

        pattern.done(CASE_PATTERN_ELEMENT);
        return true;
    }
}
