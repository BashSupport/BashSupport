package com.ansorgit.plugins.bash.codeInsight.completion;

import com.intellij.codeInsight.lookup.LookupElement;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class DynamicPathCompletionTest extends AbstractCompletionTest {
    public DynamicPathCompletionTest() {
        super("/codeInsight/completion/dynamicPathCompletion");
    }

    @Test
    public void testSimpleCompletion() throws Throwable {
        configureByTestName();

        checkItems("./simpleCompletion.bash");
    }

    @Test
    public void testNoCompletion() throws Throwable {
        configureByTestName();

        checkItems(NO_COMPLETIONS);
    }

    @Test
    public void testHomeVarCompletion() throws Throwable {
        configureByTestName();

        complete();

        Assert.assertNotNull("Expected completion, but got " + myItems, myItems);
        Assert.assertTrue("No completions for $HOME", myItems.length >= 1);

        for (LookupElement item : myItems) {
            String lookup = item.getLookupString();
            Assert.assertTrue("item does not begin with $HOME/ : " + lookup, lookup.startsWith("$HOME/"));
            Assert.assertFalse("item must not begin with $HOME// : " + lookup, lookup.startsWith("$HOME//"));
        }
    }

    @Test
    public void testHomeVarHiddenCompletion() throws Throwable {
        configureByTestName();

        complete();

        Assert.assertNotNull(myItems);
        Assert.assertTrue("No completions for $HOME", myItems.length >= 1);

        for (LookupElement item : myItems) {
            String lookup = item.getLookupString();
            Assert.assertFalse("item must not contain ./. : " + lookup, lookup.contains("./."));
        }
    }

    @Test
    public void testTildeCompletion() throws Throwable {
        configureByTestName();

        complete();

        Assert.assertNotNull(myItems);
        Assert.assertTrue("No completions for $HOME", myItems.length >= 1);

        for (LookupElement item : myItems) {
            String lookup = item.getLookupString();
            Assert.assertTrue("item does not begin with ~/ : " + lookup, lookup.startsWith("~/"));
            Assert.assertFalse("item must not begin with ~// : " + lookup, lookup.startsWith("~//"));
        }
    }

    @Test
    public void testNoFunctionCompletion() throws Exception {
        configureByTestName();

        complete();

        Assert.assertNotNull(myItems);
        Assert.assertTrue("No completions found", myItems.length > 0);

        for (LookupElement item : myItems) {
            String lookupString = item.getLookupString();
            Assert.assertFalse("Completion of path must not offer function name: " + lookupString, lookupString.contains("myFunction"));
        }
    }
}
