/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ParsingStateData.java, Class: ParsingStateData
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

package com.ansorgit.plugins.bash.lang.parser;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data container to track the advanced parsing state.
 * It can track whether the parser currently is in a heredoc or a simple command.
 * <p/>
 * User: jansorg
 * Date: Jan 29, 2010
 * Time: 7:12:36 PM
 */
final class ParsingStateData {
    private AtomicInteger inSimpleCommand = new AtomicInteger(0);
    private AtomicInteger inHereDoc = new AtomicInteger(0);

    public void enterSimpleCommand() {
        inSimpleCommand.incrementAndGet();
    }

    public void leaveSimpleCommand() {
        inSimpleCommand.decrementAndGet();
    }

    public boolean isInSimpleCommand() {
        return inSimpleCommand.get() > 0;
    }

    public void enterHereDoc() {
        inHereDoc.incrementAndGet();
    }

    public void leaveHereDoc() {
        inHereDoc.decrementAndGet();
    }

    public boolean isInHereDoc() {
        return inHereDoc.get() > 0;
    }
}
