/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.annotator;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.google.common.collect.Lists;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.TextAttributes;
import org.junit.Assert;

import java.util.List;

/**
 * @author jansorg
 */
public class BashAnnotatorHighlightingTest extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected boolean isWriteActionRequired() {
        return false;
    }

    // #658
    public void testHeredocHighlighting() {
        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "<<XX<caret>\n" +
                "XX\n" +
                "# comment after");

        List<TextAttributes> initialComments = collectComments();
        Assert.assertEquals(1, initialComments.size());

        // remove the start EOF marker and verify highlighting again
        myFixture.type("\b\b");
        myFixture.type("XX");

        List<TextAttributes> comments = collectComments();
        Assert.assertEquals("The trailing comment must be properly highlighted", initialComments, comments);
    }

    private List<TextAttributes> collectComments() {
        List<TextAttributes> result = Lists.newArrayList();

        HighlighterIterator iterator = ((EditorEx) myFixture.getEditor()).getHighlighter().createIterator(0);
        while (!iterator.atEnd()) {
            if (iterator.getTokenType() == BashTokenTypes.COMMENT) {
                result.add(iterator.getTextAttributes());
            }
            iterator.advance();
        }
        return result;
    }
}
