/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: TriggerExceptionAction.java, Class: TriggerExceptionAction
 * Last modified: 2010-06-03 22:08
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
package nu.studer.idea.errortesting;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * This class allows to test the error reporter functionality by throwing a runtime exception when the action is invoked.
 *
 * @author <a href="mailto:intellij@studer.nu">Etienne Studer</a>, May 30, 2006
 */
public class TriggerExceptionAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        throw new RuntimeException("I'm an artificial exception!");
    }
}
