package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
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
        doTestHighlighting();
    }

    @Test
    public void testBuiltinVariable() {
        doTestHighlighting();
    }

    @Test
    public void testBuiltinCommand() {
        doTestHighlighting();
    }

    @Test
    public void testLexerHighlighting() {
        // highlight func declaration first, lest we get an "Extra fragment highlighted" error.
        EditorColorsManager manager = EditorColorsManager.getInstance();
        EditorColorsScheme scheme = (EditorColorsScheme) manager.getGlobalScheme().clone();
        manager.addColorsScheme(scheme);
        EditorColorsManager.getInstance().setGlobalScheme(scheme);

        scheme.setAttributes(BashSyntaxHighlighter.VAR_USE,
                new TextAttributes(JBColor.RED, JBColor.BLUE, JBColor.GRAY, EffectType.BOXED, Font.BOLD));

        doTestHighlighting();
    }

    protected void doTestHighlighting() {
        myFixture.testHighlighting(true, true, true, getTestDataPath() + "/" + getTestName(true) + ".bash");
    }
}