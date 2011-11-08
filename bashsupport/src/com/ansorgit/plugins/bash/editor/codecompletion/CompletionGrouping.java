/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: CompletionGrouping.java, Class: CompletionGrouping
 * Last modified: 2011-02-08 19:08
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

package com.ansorgit.plugins.bash.editor.codecompletion;

/**
 * The global order of completion suggestions
 * <p/>
 * The defined order is the reverse of what is displayed in the autocomletion popup.
 * <p/>
 * User: jansorg
 * Date: 07.02.11
 * Time: 20:31
 */
enum CompletionGrouping {
    BuiltInVar,
    GlobalVar,
    NormalVar,

    GlobalCommand,
    Function,

    AbsoluteFilePath,
    RelativeFilePath
}
