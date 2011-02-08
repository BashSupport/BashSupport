package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * User: jansorg
 * Date: 08.02.11
 * Time: 18:40
 */
public class VariableNameCompletionProviderTest extends AbstractCompletionTest {
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/editor/codecompletion/variableNameCompletion/";
    }

    @Test
    public void testSimpleCompletion() throws Exception {
        List<LookupElement> completions = findCompletions(CompletionType.BASIC);
        Assert.assertEquals("Completion for variable not found", 2, completions.size());

        asssertLookupStringContains(completions, "IsOk");
    }

    @Test
    public void testDollarCompletion() throws Exception {
        List<LookupElement> completions = findCompletions(CompletionType.BASIC);
        Assert.assertEquals("Completions do not match: " + completions.toString(), 2, completions.size());

        asssertLookupStringContains(completions, "IsOk");
    }

    public void testIncludedVariables() throws Exception {
        List<LookupElement> completions = findCompletions(CompletionType.BASIC, "include.bash");
        Assert.assertEquals("Expected four completions, found: " + completions, 4, completions.size());

        asssertLookupStringContains(completions, "IsOk");
    }

    protected void asssertLookupStringContains(List<LookupElement> completions, String part) {
        for (LookupElement completion : completions) {
            String lookupString = completion.getLookupString();
            Assert.assertTrue("Completion '" + lookupString + "' does not contain " + part, lookupString.contains(part));
        }
    }
}
