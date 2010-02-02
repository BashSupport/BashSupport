/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSyntaxHighlighter.java, Class: BashSyntaxHighlighter
 * Last modified: 2010-01-30
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
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines bash token highlighting and formatting.
 */
public class BashSyntaxHighlighter extends SyntaxHighlighterBase implements BashTokenTypes {
    private static final TokenSet lineCommentSet = TokenSet.create(COMMENT);
    private static final TokenSet shebangCommentSet = TokenSet.create(BashTokenTypes.SHEBANG);
    private static final TokenSet parenthesisSet = TokenSet.create(LEFT_PAREN, RIGHT_PAREN);
    private static final TokenSet curlySet = TokenSet.create(LEFT_CURLY, RIGHT_CURLY);
    private static final TokenSet bracketSet = TokenSet.create(LEFT_SQUARE, RIGHT_SQUARE);
    private static final TokenSet string2Set = TokenSet.create(BashTokenTypes.STRING2);
    private static final TokenSet numberSet = TokenSet.create(BashTokenTypes.NUMBER, INTEGER_LITERAL);
    private static final TokenSet internalCommandSet = TokenSet.create(BashTokenTypes.INTERNAL_COMMAND);
    private static final TokenSet varUseSet = TokenSet.create(BashTokenTypes.VARIABLE);
    private static final TokenSet badCharacterSet = TokenSet.create(BashTokenTypes.BAD_CHARACTER);

    @NonNls
    public static final String LINE_COMMENT_ID = "Line comment";
    @NonNls
    public static final String SHEBANG_ID = "Shebang (#!) comment";
    @NonNls
    public static final String KEYWORD_ID = "Keyword";
    @NonNls
    public static final String PAREN_ID = "Parenthesis";
    @NonNls
    public static final String BRACES_ID = "Braces";
    @NonNls
    public static final String BRACKETS_ID = "Brackets";
    @NonNls
    public static final String NUMBER_ID = "Number";
    @NonNls
    public static final String STRING_ID = "String \"...\"";
    @NonNls
    public static final String STRING2_ID = "String '...'";
    @NonNls
    public static final String REDIRECTION_ID = "Command redirection";
    @NonNls
    public static final String CONDITIONAL_ID = "Conditional operator";
    @NonNls
    public static final String BAD_CHARACTER_ID = "Bad character";
    @NonNls
    public static final String HERE_DOC_ID = "Here-document";
    @NonNls
    public static final String HERE_DOC_START_ID = "Here-document start marker";
    @NonNls
    public static final String HERE_DOC_END_ID = "Here-document end marker";
    @NonNls
    public static final String INTERNAL_COMMAND_ID = "Build-in Bash command";
    @NonNls
    public static final String EXTERNAL_COMMAND_ID = "External command";

    public static final String SUBSHELL_COMMAND_ID = "Subshell command";
    @NonNls
    public static final String BACKQUOTE_COMMAND_ID = "Backquote command `...`";
    @NonNls
    public static final String FUNCTION_CALL_ID = "Function call";
    @NonNls
    public static final String VAR_DEF_ID = "Variable declaration, e.g. a=1";
    @NonNls
    public static final String VAR_USE_ID = "Variable use $a";
    @NonNls
    public static final String VAR_USE_BUILTIN_ID = "Variable use of built-in ($PATH, ...)";
    @NonNls
    public static final String VAR_USE_COMPOSED_ID = "Variable use of composed variable like ${A}";

    //custom highlightings
    private static final TextAttributes SHEBANG_ATTRIB = SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes().clone();
    private static final TextAttributes REDIRECTION_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes CONDITIONAL_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes HERE_DOC_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes HERE_DOC_START_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes HERE_DOC_END_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes BACKQUOTE_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes INTERNAL_COMMAND_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes EXTERNAL_COMMAND_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes SUBSHELL_COMMAND_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes FUNCTION_CALL_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    private static final TextAttributes VAR_DEF_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    public static final TextAttributes VAR_USE_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    public static final TextAttributes VAR_USE_INTERNAL_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();
    public static final TextAttributes VAR_USE_COMPOSED_ATTRIB = HighlighterColors.TEXT.getDefaultAttributes().clone();

    private static final TextAttributes STRING_ATTRIB = SyntaxHighlighterColors.STRING.getDefaultAttributes().clone();
    private static final TextAttributes STRING2_ATTRIB = SyntaxHighlighterColors.STRING.getDefaultAttributes().clone();

    static {
        //register
        TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID, SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(SHEBANG_ID, SHEBANG_ATTRIB);
        TextAttributesKey.createTextAttributesKey(KEYWORD_ID, SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(PAREN_ID, SyntaxHighlighterColors.PARENTHS.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(BRACES_ID, SyntaxHighlighterColors.BRACES.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(BRACKETS_ID, SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(BRACKETS_ID, SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(NUMBER_ID, SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
        TextAttributesKey.createTextAttributesKey(STRING2_ID, STRING2_ATTRIB);
        TextAttributesKey.createTextAttributesKey(REDIRECTION_ID, REDIRECTION_ATTRIB);
        TextAttributesKey.createTextAttributesKey(CONDITIONAL_ID, CONDITIONAL_ATTRIB);
        TextAttributesKey.createTextAttributesKey(INTERNAL_COMMAND_ID, INTERNAL_COMMAND_ATTRIB);
        TextAttributesKey.createTextAttributesKey(VAR_USE_ID, VAR_USE_ATTRIB);
        TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID, HighlighterColors.BAD_CHARACTER.getDefaultAttributes());

        //psi highlighting
        TextAttributesKey.createTextAttributesKey(STRING_ID, STRING_ATTRIB);
        TextAttributesKey.createTextAttributesKey(HERE_DOC_ID, HERE_DOC_ATTRIB);
        TextAttributesKey.createTextAttributesKey(HERE_DOC_START_ID, HERE_DOC_START_ATTRIB);
        TextAttributesKey.createTextAttributesKey(HERE_DOC_END_ID, HERE_DOC_END_ATTRIB);
        TextAttributesKey.createTextAttributesKey(EXTERNAL_COMMAND_ID, EXTERNAL_COMMAND_ATTRIB);
        TextAttributesKey.createTextAttributesKey(BACKQUOTE_COMMAND_ID, BACKQUOTE_ATTRIB);
        TextAttributesKey.createTextAttributesKey(SUBSHELL_COMMAND_ID, SUBSHELL_COMMAND_ATTRIB);
        TextAttributesKey.createTextAttributesKey(FUNCTION_CALL_ID, FUNCTION_CALL_ATTRIB);
        TextAttributesKey.createTextAttributesKey(VAR_DEF_ID, VAR_DEF_ATTRIB);
        TextAttributesKey.createTextAttributesKey(VAR_USE_BUILTIN_ID, VAR_USE_INTERNAL_ATTRIB);
        TextAttributesKey.createTextAttributesKey(VAR_USE_COMPOSED_ID, VAR_USE_COMPOSED_ATTRIB);
    }

    public static final TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey(LINE_COMMENT_ID);
    public static final TextAttributesKey SHEBANG_COMMENT = TextAttributesKey.createTextAttributesKey(SHEBANG_ID);
    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(KEYWORD_ID);
    public static final TextAttributesKey PAREN = TextAttributesKey.createTextAttributesKey(PAREN_ID);
    public static final TextAttributesKey BRACE = TextAttributesKey.createTextAttributesKey(BRACES_ID);
    public static final TextAttributesKey BRACKET = TextAttributesKey.createTextAttributesKey(BRACKETS_ID);
    public static final TextAttributesKey STRING2 = TextAttributesKey.createTextAttributesKey(STRING2_ID);
    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(NUMBER_ID);
    public static final TextAttributesKey REDIRECTION = TextAttributesKey.createTextAttributesKey(REDIRECTION_ID);
    public static final TextAttributesKey CONDITIONAL = TextAttributesKey.createTextAttributesKey(CONDITIONAL_ID);
    public static final TextAttributesKey INTERNAL_COMMAND = TextAttributesKey.createTextAttributesKey(INTERNAL_COMMAND_ID);
    public static final TextAttributesKey VAR_USE = TextAttributesKey.createTextAttributesKey(VAR_USE_ID);
    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(BAD_CHARACTER_ID);

    //psi highlighting
    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(STRING_ID);
    public static final TextAttributesKey BACKQUOTE = TextAttributesKey.createTextAttributesKey(BACKQUOTE_COMMAND_ID);
    public static final TextAttributesKey HERE_DOC = TextAttributesKey.createTextAttributesKey(HERE_DOC_ID);
    public static final TextAttributesKey HERE_DOC_START = TextAttributesKey.createTextAttributesKey(HERE_DOC_START_ID);
    public static final TextAttributesKey HERE_DOC_END = TextAttributesKey.createTextAttributesKey(HERE_DOC_END_ID);
    public static final TextAttributesKey EXTERNAL_COMMAND = TextAttributesKey.createTextAttributesKey(EXTERNAL_COMMAND_ID);
    public static final TextAttributesKey SUBSHELL_COMMAND = TextAttributesKey.createTextAttributesKey(SUBSHELL_COMMAND_ID);
    public static final TextAttributesKey FUNCTION_CALL = TextAttributesKey.createTextAttributesKey(FUNCTION_CALL_ID);
    public static final TextAttributesKey VAR_DEF = TextAttributesKey.createTextAttributesKey(VAR_DEF_ID);
    public static final TextAttributesKey VAR_USE_BUILTIN = TextAttributesKey.createTextAttributesKey(VAR_USE_BUILTIN_ID);
    public static final TextAttributesKey VAR_USE_COMPOSED = TextAttributesKey.createTextAttributesKey(VAR_USE_COMPOSED_ID);

    //
    public static final TextAttributesKey NONE = HighlighterColors.TEXT;

    private static final Map<IElementType, TextAttributesKey> attributes;

    static {
        //setup default attribute formatting
        SHEBANG_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);

        VAR_USE_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);
        VAR_USE_ATTRIB.setForegroundColor(new java.awt.Color(0, 0, 204)); //dark blue

        VAR_USE_INTERNAL_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);
        VAR_USE_INTERNAL_ATTRIB.setForegroundColor(new java.awt.Color(0, 0, 204)); //dark blue

        VAR_USE_COMPOSED_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);
        VAR_USE_COMPOSED_ATTRIB.setForegroundColor(new java.awt.Color(0, 0, 204)); //dark blue

        VAR_DEF_ATTRIB.setForegroundColor(new Color(51, 51, 255)); //lighter blue

        HERE_DOC_ATTRIB.setBackgroundColor(new Color(204, 255, 204)); //light green background
        HERE_DOC_START_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);
        HERE_DOC_START_ATTRIB.setForegroundColor(new Color(0, 153, 0)); //dark green
        HERE_DOC_END_ATTRIB.setFontType(SimpleTextAttributes.STYLE_BOLD);
        HERE_DOC_END_ATTRIB.setForegroundColor(new Color(0, 153, 0)); // dark greem

        FUNCTION_CALL_ATTRIB.setFontType(SimpleTextAttributes.STYLE_ITALIC);

        EXTERNAL_COMMAND_ATTRIB.setFontType(SimpleTextAttributes.STYLE_ITALIC);

        BACKQUOTE_ATTRIB.setFontType(SimpleTextAttributes.STYLE_ITALIC);

        INTERNAL_COMMAND_ATTRIB.setFontType(SimpleTextAttributes.STYLE_ITALIC | SimpleTextAttributes.STYLE_BOLD);

        SUBSHELL_COMMAND_ATTRIB.setFontType(SimpleTextAttributes.STYLE_ITALIC);

        attributes = new HashMap<IElementType, TextAttributesKey>();
        fillMap(attributes, lineCommentSet, LINE_COMMENT);
        fillMap(attributes, shebangCommentSet, SHEBANG_COMMENT);
        fillMap(attributes, keywords, KEYWORD);
        fillMap(attributes, parenthesisSet, PAREN);
        fillMap(attributes, curlySet, BRACE);
        fillMap(attributes, bracketSet, BRACKET);
        fillMap(attributes, string2Set, STRING2);
        fillMap(attributes, numberSet, NUMBER);
        fillMap(attributes, redirectionSet, REDIRECTION);
        //fixme bash4 redirects?
        fillMap(attributes, conditionalOperators, CONDITIONAL);
        fillMap(attributes, internalCommandSet, INTERNAL_COMMAND);
        fillMap(attributes, varUseSet, VAR_USE);
        fillMap(attributes, badCharacterSet, BAD_CHARACTER);
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new BashLexer();
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(final IElementType tokenType) {
        return pack(attributes.get(tokenType));
    }
}


