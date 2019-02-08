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

package com.ansorgit.plugins.bash.lang.psiTree;

import com.ansorgit.plugins.bash.lang.AbstractBashPsiTreeTest;
import org.junit.Test;

import java.io.IOException;

public class ArithmeticExprTest extends AbstractBashPsiTreeTest {
    @Override
    protected String getBasePath() {
        return "psiTree/arithmetic";
    }

    @Test
    public void testShiftLeft() throws Exception {
        assertPsiTree("$((1024 << 1))", "shiftLeft.txt");
    }

    @Test
    public void testShiftLeftAssignment() throws Exception {
        assertPsiTree("$((a <<= 1))", "shiftLeftAssignment.txt");
    }

    @Test
    public void testShiftRight() throws IOException {
        assertPsiTree("$((1024 >> 1))", "shiftRight.txt");
    }

    @Test
    public void testShiftRightAssignment() throws IOException {
        assertPsiTree("$((a >>= 1))", "shiftRightAssignment.txt");
    }
}