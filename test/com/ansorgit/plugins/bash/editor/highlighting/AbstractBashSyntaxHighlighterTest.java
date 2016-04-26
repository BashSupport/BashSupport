package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
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

import java.awt.*;
import java.io.File;

public abstract class AbstractBashSyntaxHighlighterTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected boolean isWriteActionRequired() {
        return false;
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
    protected long doHighlightingTest() {
        return doHighlightingTest(getTestName(true) + ".bash");
    }

    /**
     * Checks the file testName.bash for proper markup. Does not test lexer highlighting.
     * @param filename
     */
    protected long doHighlightingTest(String filename) {
        return myFixture.testHighlighting(true, true, true, getTestDataPath() + File.separator + filename);
    }
}
