package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class BashSyntaxHighlighterTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected boolean isWriteActionRequired() {
        return false;
    }

    @Override
    protected String getBasePath() {
        return "editor/highlighting/syntaxHighlighter";
    }

    @Test
    public void testSimpleVariable() {
        doHighlightingTest();
    }

    @Test
    public void testBuiltinVariable() {
        doHighlightingTest();
    }

    @Test
    public void testBuiltinCommand() {
        doHighlightingTest();
    }

    @Test
    public void testLexerHighlighting() {
        //keywords
        doLexerHighlightingTest("for", BashTokenTypes.FOR_KEYWORD);
        doLexerHighlightingTest("while", BashTokenTypes.WHILE_KEYWORD);
        doLexerHighlightingTest("!", BashTokenTypes.BANG_TOKEN);
        doLexerHighlightingTest("then", BashTokenTypes.THEN_KEYWORD);
        doLexerHighlightingTest("$", BashTokenTypes.DOLLAR);
        doLexerHighlightingTest("done", BashTokenTypes.DONE_KEYWORD);

        //internal commands
        doLexerHighlightingTest("trap", BashTokenTypes.TRAP_KEYWORD);
        doLexerHighlightingTest("let", BashTokenTypes.LET_KEYWORD);

        doLexerHighlightingTest("$VAR", BashTokenTypes.VARIABLE);

        doLexerHighlightingTest("#X", BashTokenTypes.COMMENT);

        doLexerHighlightingTest("#!/bin/bash", BashTokenTypes.SHEBANG);

        doLexerHighlightingTest("'string'", BashTokenTypes.STRING2);

        doLexerHighlightingTest("(test)", BashTokenTypes.LEFT_PAREN);
        doLexerHighlightingTest("(test)", BashTokenTypes.RIGHT_PAREN);

        doLexerHighlightingTest("${x}", BashTokenTypes.LEFT_CURLY);
        doLexerHighlightingTest("${x}", BashTokenTypes.RIGHT_CURLY);

        doLexerHighlightingTest("a > x", BashTokenTypes.GREATER_THAN);
        doLexerHighlightingTest("a < x", BashTokenTypes.LESS_THAN);
        doLexerHighlightingTest("a &> x", BashTokenTypes.REDIRECT_AMP_GREATER);
        doLexerHighlightingTest("a >> x", BashTokenTypes.SHIFT_RIGHT);

        doLexerHighlightingTest("a && b", BashTokenTypes.AND_AND);
        doLexerHighlightingTest("a || b", BashTokenTypes.OR_OR);
    }

    protected void doLexerHighlightingTest(String fileContent, IElementType targetElementType) {
        BashSyntaxHighlighter syntaxHighlighter = new BashSyntaxHighlighter();
        TextAttributesKey[] keys = syntaxHighlighter.getTokenHighlights(targetElementType);
        Assert.assertEquals("Expected one key", 1, keys.length);

        TextAttributesKey attributesKey = keys[0];
        Assert.assertNotNull(attributesKey);

        EditorColorsManager manager = EditorColorsManager.getInstance();
        EditorColorsScheme scheme = (EditorColorsScheme) manager.getGlobalScheme().clone();
        manager.addColorsScheme(scheme);
        EditorColorsManager.getInstance().setGlobalScheme(scheme);

        TextAttributes targetAttributes = new TextAttributes(JBColor.RED, JBColor.BLUE, JBColor.GRAY, EffectType.BOXED, Font.BOLD);
        scheme.setAttributes(attributesKey, targetAttributes);

        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, fileContent);

        TextAttributes actualAttributes = null;
        HighlighterIterator iterator = ((EditorImpl) myFixture.getEditor()).getHighlighter().createIterator(0);
        while (!iterator.atEnd()) {
            if (iterator.getTokenType() == targetElementType) {
                actualAttributes = iterator.getTextAttributes();
                break;
            }

            iterator.advance();
        }

        Assert.assertEquals("Expected text attributes for " + attributesKey, targetAttributes, actualAttributes);
    }

    /**
     * Checks the file testName.bash for proper markup. Does not test lexer highlighting.
     */
    protected void doHighlightingTest() {
        myFixture.testHighlighting(true, true, true, getTestDataPath() + "/" + getTestName(true) + ".bash");
    }
}