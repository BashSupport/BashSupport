package com.ansorgit.plugins.bash.codeInsight.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import org.junit.Assert;

/**
 * User: jansorg
 * Date: 09.02.11
 * Time: 20:59
 */
public class DynamicPathCompletionTest extends AbstractCompletionTest {
    @Override
    protected String getTestDir() {
        return "dynamicPathCompletion";
    }

    public void testSimpleCompletion() throws Throwable {
        configure();
        checkItems("./SimpleCompletion.bash");
    }

    public void testNoCompletion() throws Throwable {
        configure();
        checkItems(NO_COMPLETIONS);
    }

    public void testHomeVarCompletion() throws Throwable {
        configure();
        Assert.assertTrue("No completions for $HOME", myItems.length >= 1);

        for (LookupElement item : myItems) {
            String lookup = item.getLookupString();
            Assert.assertTrue("item does not begin with $HOME/ : " + lookup, lookup.startsWith("$HOME/"));
            Assert.assertFalse("item must not begin with $HOME// : " + lookup, lookup.startsWith("$HOME//"));
        }
    }

    public void testTildeCompletion() throws Throwable {
        configure();
        Assert.assertTrue("No completions for $HOME", myItems.length >= 1);

        for (LookupElement item : myItems) {
            String lookup = item.getLookupString();
            Assert.assertTrue("item does not begin with ~/ : " + lookup, lookup.startsWith("~/"));
            Assert.assertFalse("item must not begin with ~// : " + lookup, lookup.startsWith("~//"));
        }
    }
}
