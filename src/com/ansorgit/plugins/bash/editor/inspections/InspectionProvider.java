/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: InspectionProvider.java, Class: InspectionProvider
 * Last modified: 2010-05-09
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections;

import com.ansorgit.plugins.bash.editor.inspections.inspections.*;
import com.intellij.codeInspection.InspectionToolProvider;

/**
 * Provides the list of available Bash inspections. If you add a new inspection implementation
 * don't forget to add it here.
 * <p/>
 * Date: 15.05.2009
 * Time: 14:38:12
 *
 * @author Joachim Ansorg
 */
public class InspectionProvider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[]{
                FixShebangInspection.class,
                FunctionDefInspection.class,
                AddShebangInspection.class,
                WrapWordInStringInspection.class,
                ConvertBackquoteInspection.class,
                ConvertSubshellInspection.class,
                DuplicateFunctionDefInspection.class,
                MissingIncludeFileInspection.class,
                RecursiveIncludeFileInspection.class,
                EvaluateExpansionInspection.class,
                UnresolvedVariableInspection.class,
                UnregisterGlobalVarInspection.class,
                EvaluateStaticArithExprInspection.class,
                FloatArithmeticInspection.class,
                ReadonlyVariableInspection.class,
                InternalVariableInspection.class,
                UnknownFiledescriptorInspection.class
                //UnusedFunctionDefInspection.class
        };
    }
}
