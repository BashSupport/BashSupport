/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSyntaxHighlighter.java, Class: BashSyntaxHighlighter
 * Last modified: 2013-01-25
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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.lang.lexer.BashLexer;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.google.common.collect.Maps;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Map;

import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.conditionalOperators;
import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.redirectionSet;

/**
 * Defines bash token highlighting and formatting.
 */
public class BashSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final TokenSet parenthesisSet = TokenSet.create(BashTokenTypes.LEFT_PAREN, BashTokenTypes.RIGHT_PAREN);
    private static final TokenSet bracesSet = TokenSet.create(BashTokenTypes.LEFT_CURLY, BashTokenTypes.RIGHT_CURLY);
    private static final TokenSet bracketSet = TokenSet.create(BashTokenTypes.LEFT_SQUARE, BashTokenTypes.RIGHT_SQUARE);
    private static final TokenSet numberSet = TokenSet.create(BashTokenTypes.NUMBER, BashTokenTypes.INTEGER_LITERAL);
    private static final TokenSet badCharacterSet = TokenSet.create(BashTokenTypes.BAD_CHARACTER);

    //custom highlightings
    private static final TextAttributes REDIRECTION_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes CONDITIONAL_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();

    public static final TextAttributes HERE_DOC_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes HERE_DOC_START_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes HERE_DOC_END_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();

    //    private static final TextAttributes BACKQUOTE_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes INTERNAL_COMMAND_ATTR = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes EXTERNAL_COMMAND_ATTR = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes SUBSHELL_COMMAND_ATTR = HighlighterColors.TEXT.getDefaultAttributes().clone();

    private static final TextAttributes FUNCTION_DEF_NAME_ATTR = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes FUNCTION_CALL_ATTR = CodeInsightColors.METHOD_CALL_ATTRIBUTES.getDefaultAttributes().clone();

    private static final TextAttributes VAR_DEF_ATTR = CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES.getDefaultAttributes().clone();

    public static final TextAttributes VAR_USE_ATTR = CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES.getDefaultAttributes().clone();
    public static final TextAttributes VAR_USE_INTERNAL_ATTR = CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES.getDefaultAttributes().clone();
    public static final TextAttributes VAR_USE_COMPOSED_ATTR = CodeInsightColors.LOCAL_VARIABLE_ATTRIBUTES.getDefaultAttributes().clone();

    //keys
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("BASH.KEYWORD", SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());

    public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey("BASH.LINE_COMMENT", SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());

    public static final TextAttributes SHEBANG_ATTR = SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes().clone();
    public static final TextAttributesKey SHEBANG_COMMENT = TextAttributesKey.createTextAttributesKey("BASH.SHEBANG", SHEBANG_ATTR);

    public static final TextAttributesKey PAREN = TextAttributesKey.createTextAttributesKey("BASH.PAREN", SyntaxHighlighterColors.PARENTHS.getDefaultAttributes());
    public static final TextAttributesKey BRACE = TextAttributesKey.createTextAttributesKey("BASH.BRACE", SyntaxHighlighterColors.BRACES.getDefaultAttributes());
    public static final TextAttributesKey BRACKET = TextAttributesKey.createTextAttributesKey("BASH.BRACKET", SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());

    public static final TextAttributesKey STRING2 = TextAttributesKey.createTextAttributesKey("BASH.STRING2", SyntaxHighlighterColors.STRING.getDefaultAttributes());
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("BASH.NUMBER", SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
    public static final TextAttributesKey REDIRECTION = TextAttributesKey.createTextAttributesKey("BASH.REDIRECTION", REDIRECTION_ATTRIB);
    public static final TextAttributesKey CONDITIONAL = TextAttributesKey.createTextAttributesKey("BASH.CONDITIONAL", CONDITIONAL_ATTRIB);

    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("BASH.BAD_CHARACTER", HighlighterColors.BAD_CHARACTER.getDefaultAttributes());

    //psi highlighting
    public static final TextAttributesKey TEXT = HighlighterColors.TEXT;

    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("BASH.STRING", SyntaxHighlighterColors.STRING.getDefaultAttributes());
    public static final TextAttributesKey BACKQUOTE = TextAttributesKey.createTextAttributesKey("BASH.BACKQUOTE");

    public static final TextAttributesKey HERE_DOC = TextAttributesKey.createTextAttributesKey("BASH.HERE_DOC", HERE_DOC_ATTRIB);
    public static final TextAttributesKey HERE_DOC_START = TextAttributesKey.createTextAttributesKey("BASH.HERE_DOC_START", HERE_DOC_START_ATTRIB);
    public static final TextAttributesKey HERE_DOC_END = TextAttributesKey.createTextAttributesKey("BASH.HERE_DOC_END", HERE_DOC_END_ATTRIB);

    public static final TextAttributesKey INTERNAL_COMMAND = TextAttributesKey.createTextAttributesKey("BASH.INTERNAL_COMMAND", INTERNAL_COMMAND_ATTR);
    public static final TextAttributesKey EXTERNAL_COMMAND = TextAttributesKey.createTextAttributesKey("BASH.EXTERNAL_COMMAND", EXTERNAL_COMMAND_ATTR);
    public static final TextAttributesKey SUBSHELL_COMMAND = TextAttributesKey.createTextAttributesKey("BASH.SUBSHELL_COMMAND", SUBSHELL_COMMAND_ATTR);

    public static final TextAttributesKey FUNCTION_DEF_NAME = TextAttributesKey.createTextAttributesKey("BASH.FUNCTION_DEF_NAME", FUNCTION_DEF_NAME_ATTR);
    public static final TextAttributesKey FUNCTION_CALL = TextAttributesKey.createTextAttributesKey("BASH.FUNCTION_CALL", CodeInsightColors.METHOD_CALL_ATTRIBUTES.getDefaultAttributes());

    public static final TextAttributesKey VAR_DEF = TextAttributesKey.createTextAttributesKey("BASH.VAR_DEF", VAR_DEF_ATTR);

    public static final TextAttributesKey VAR_USE = TextAttributesKey.createTextAttributesKey("BASH.VAR_USE", VAR_USE_ATTR);
    public static final TextAttributesKey VAR_USE_BUILTIN = TextAttributesKey.createTextAttributesKey("BASH.VAR_USE_BUILTIN", VAR_USE_INTERNAL_ATTR);
    public static final TextAttributesKey VAR_USE_COMPOSED = TextAttributesKey.createTextAttributesKey("BASH.VAR_USE_COMPOSED", VAR_USE_COMPOSED_ATTR);

    public static final TextAttributesKey NONE = HighlighterColors.TEXT;

    private static final Map<IElementType, TextAttributesKey> attributes1 = Maps.newHashMap();
    private static final Map<IElementType, TextAttributesKey> attributes2 = Maps.newHashMap();

    public static final TokenSet lineCommentSet = TokenSet.create(BashTokenTypes.COMMENT);

    public static final TokenSet shebangSet = TokenSet.create(BashTokenTypes.SHEBANG);

    static {
        //setup default attribute formatting
        SHEBANG_ATTR.setFontType(SimpleTextAttributes.STYLE_BOLD);

        HERE_DOC_ATTRIB.setBackgroundColor(new Color(204, 255, 204)); //light green background
        HERE_DOC_START_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);
        HERE_DOC_END_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);

        INTERNAL_COMMAND_ATTR.setFontType(SimpleTextAttributes.STYLE_ITALIC);
        FUNCTION_CALL_ATTR.setFontType(SimpleTextAttributes.STYLE_ITALIC | SimpleTextAttributes.STYLE_BOLD);

        fillMap(attributes1, BashTokenTypes.keywords, KEYWORD);

        fillMap(attributes1, lineCommentSet, LINE_COMMENT);

        fillMap(attributes1, shebangSet, SHEBANG_COMMENT);

        fillMap(attributes1, STRING2, BashTokenTypes.STRING2);

        fillMap(attributes1, VAR_USE, BashTokenTypes.VARIABLE);

        fillMap(attributes1, parenthesisSet, PAREN);
        fillMap(attributes1, bracesSet, BRACE);
        fillMap(attributes1, bracketSet, BRACKET);
        fillMap(attributes1, numberSet, NUMBER);

        fillMap(attributes1, redirectionSet, REDIRECTION);
        fillMap(attributes1, conditionalOperators, CONDITIONAL);


        fillMap(attributes1, badCharacterSet, BAD_CHARACTER);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new BashLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(final IElementType tokenType) {
        return pack(attributes1.get(tokenType), attributes2.get(tokenType));
    }
}


