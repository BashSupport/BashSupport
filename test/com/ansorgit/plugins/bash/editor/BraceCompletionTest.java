package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import org.junit.Assert;
import org.junit.Test;

public class BraceCompletionTest extends BashCodeInsightFixtureTestCase {
    @Test
    public void testIssue89() throws Exception {
        //tests brace completion of a function defined above another function
        configurePsiAtCaret("issue89.bash");

        doTyping();
    }

    @Test
    public void testIssue89Formatting() throws Exception {
        //tests brace completion of a function defined above another function
        configurePsiAtCaret("issue89.bash");

        BashProjectSettings projectSettings = BashProjectSettings.storedSettings(getProject());
        boolean formatterEnabled = projectSettings.isFormatterEnabled();
        try {
            projectSettings.setFormatterEnabled(true);
            doTyping();
        } finally {
            projectSettings.setFormatterEnabled(formatterEnabled);
        }
    }

    private void doTyping() {
        myFixture.type("\n"); //trigger the brace completion

        String expectedText = "function above() {\n" +
                "\n" +
                "\n" + //two \n seem to be inserted by InteeliJ's default behaviour
                "}\n" +
                "function below() {\n" +
                "    echo\n" +
                "}";

        Assert.assertEquals("Brace completion must insert a newline after the completed brace", expectedText, myFixture.getFile().getText());
    }

    @Override
    protected String getBasePath() {
        return "/editor/braceCompletion/functionBrace/";
    }

}
