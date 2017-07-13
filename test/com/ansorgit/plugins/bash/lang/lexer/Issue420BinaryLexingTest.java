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

package com.ansorgit.plugins.bash.lang.lexer;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.BashVersion;
import org.junit.Test;

/**
 * @author jansorg
 */
public class Issue420BinaryLexingTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testBinaryData() throws Exception {
        String data = BashTestUtils.loadTestCaseFile(this, "420-binaryData.bash");

        _BashLexer lexer = new _BashLexer(BashVersion.Bash_v4, null);
        lexer.reset(data, 0, data.length(), 0);

        //must not cause a "lexer could not match" exception
        while (lexer.advance() != null) {
            lexer.yytext();
        }
    }

    @Override
    protected String getBasePath() {
        return "lexer";
    }
}
