/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarAnnotator.java, Class: BashVarAnnotator
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

import com.ansorgit.plugins.bash.editor.highlighting.BashSyntaxHighlighter;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.diagnostic.Logger;

/**
 * Annotates a Bash variable usage element. It marks the Var psi elements as buildtin or composed variables,
 * according to the type.
 *
 * @author Joachim Ansorg
 */
class BashVarAnnotator {
    private static final Logger log = Logger.getInstance("#BashVarAnnotator");

    public static void annotateVar(BashVar bashVar, AnnotationHolder annotationHolder) {
        log.debug("Annotating variable");
        if (bashVar.isBuiltinVar()) {
            //highlighting for built-in variables
            Annotation annotation = annotationHolder.createInfoAnnotation(bashVar, null);
            annotation.setTextAttributes(BashSyntaxHighlighter.VAR_USE_BUILTIN);
        } else if (bashVar.isParameterExpansion()) {
            //highlighting for composed variables
            Annotation annotation = annotationHolder.createInfoAnnotation(bashVar, null);
            annotation.setTextAttributes(BashSyntaxHighlighter.VAR_USE_COMPOSED);
        }
    }
}