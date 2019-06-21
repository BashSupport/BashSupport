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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.ide.todo.TodoIndexPatternProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.IndexPatternOccurrence;
import com.intellij.psi.search.searches.IndexPatternSearch;
import com.intellij.util.Query;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * @author jansorg
 */
public class BashFixmeHighlighterTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testComment() {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "# fixme: a\n#  b");
        Query<IndexPatternOccurrence> result = IndexPatternSearch.search(file, TodoIndexPatternProvider.getInstance(), true);
        Collection<IndexPatternOccurrence> matches = result.findAll();
        Assert.assertEquals(1, matches.size());

        IndexPatternOccurrence first = matches.iterator().next();
        Assert.assertEquals("A multiline comment must be detected as a single occurence with one additional text range", 1, first.getAdditionalTextRanges().size());
    }
}