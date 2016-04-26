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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiBuilder;
import org.junit.Assert;
import org.junit.Test;

public class RecursionGuardTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testLevel0() throws Exception {
        MockPsiBuilder mockPsiBuilder = new MockPsiBuilder();
        BashPsiBuilder builder = new BashPsiBuilder(getProject(), mockPsiBuilder, BashVersion.Bash_v3);
        Assert.assertFalse(mockPsiBuilder.hasErrors());

        RecursionGuard guard = RecursionGuard.initial(0);
        Assert.assertFalse("Expected failed first call for 0 max levels", guard.next(builder));
        Assert.assertTrue("Expected an error message in the psi builder", mockPsiBuilder.hasErrors());

        Assert.assertFalse("Expected failed next call for 0 max levels", guard.next(builder));
    }

    @Test
    public void testLevel1() throws Exception {
        MockPsiBuilder mockPsiBuilder = new MockPsiBuilder();
        BashPsiBuilder builder = new BashPsiBuilder(getProject(), mockPsiBuilder, BashVersion.Bash_v3);

        RecursionGuard guard = RecursionGuard.initial(1);
        Assert.assertTrue(guard.next(builder));
        Assert.assertFalse(mockPsiBuilder.hasErrors());

        Assert.assertFalse(guard.next(builder));
        Assert.assertTrue("Expected an error message in the psi builder", mockPsiBuilder.hasErrors());
    }
}