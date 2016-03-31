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

package com.ansorgit.plugins.bash.editor.codecompletion;

/**
 * The global order of completion suggestions
 * <br>
 * The defined order is the reverse of what is displayed in the autocomletion popup.
 * <br>
 * @author jansorg
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
