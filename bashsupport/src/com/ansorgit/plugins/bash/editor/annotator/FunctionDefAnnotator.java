/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FunctionDefAnnotator.java, Class: FunctionDefAnnotator
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.annotator;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Provides annotations methods for function definitions.
 * <p/>
 * User: jansorg
 * Date: Oct 31, 2009
 * Time: 8:10:06 PM
 */
public class FunctionDefAnnotator {
    private static final Logger log = Logger.getInstance("#FunctionDefAnnotator");

    public void annotate(BashFunctionDef element, AnnotationHolder holder) {
        log.debug("annotating function def");
        annotateIcon(element, holder);
    }

    private void annotateIcon(BashFunctionDef element, AnnotationHolder holder) {
        final Annotation annotation = holder.createInfoAnnotation(element, "");
        annotation.setGutterIconRenderer(new ElementIconGutterProvider(element));
    }
}
