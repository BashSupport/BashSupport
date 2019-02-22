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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class ArrayUseOfSimpleVarInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testSimpleAccess() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/simpleVar", new SimpleArrayUseInspection());
    }

    @Test
    public void testSimpleAccessDeclared() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/simpleVarDeclared", new SimpleArrayUseInspection());
    }

    @Test
    public void testSimpleAccessTypeset() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayVarTypeset", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayAccess() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayVar", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayAccessDeclared() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayVarDeclared", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayAccessLocal() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayVarLocal", new SimpleArrayUseInspection());
    }

    @Test
    public void testMapfileArray() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/mapfileArray", new SimpleArrayUseInspection());
    }

    @Test
    public void testReadarrayArray() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/readarrayArray", new SimpleArrayUseInspection());
    }

    @Test
    public void testArrayParam() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/arrayParam", new SimpleArrayUseInspection());
    }

    @Test
    public void testReadArrayParam() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/readArrayParam", new SimpleArrayUseInspection());
    }

    @Test
    public void testStringLength() throws Exception {
        doTest("arrayUseOfSimpleVarInspection/stringLength", new SimpleArrayUseInspection());
    }
}
