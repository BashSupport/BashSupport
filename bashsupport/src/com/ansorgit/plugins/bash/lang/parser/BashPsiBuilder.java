/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiBuilder.java, Class: BashPsiBuilder
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

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.util.ForwardingMarker;
import com.ansorgit.plugins.bash.lang.parser.util.ForwardingPsiBuilder;
import com.ansorgit.plugins.bash.util.ReflectionUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Stack;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * The PsiBuilder which has been enhanced to be more helpful for Bash parsing.
 * It provides the possiblity to enable whitespace tokens on demand.
 * <p/>
 * <p/>
 * Date: 10.04.2009
 * Time: 00:35:17
 *
 * @author Joachim Ansorg
 */
public class BashPsiBuilder extends ForwardingPsiBuilder implements PsiBuilder {
    static final Logger log = Logger.getInstance("#bash.BashPsiBuilder");
    private static final short originalWhitespaceIndex = BashTokenTypes.WHITESPACE.getIndex();
    private final Stack<Boolean> errorsStatusStack = new Stack<Boolean>();
    private final BashTokenRemapper tokenRemapper;
    private final BashVersion bashVersion;
    private boolean whitespaceEnabled = false;
    private Project project;

    public BashPsiBuilder(Project project, PsiBuilder wrappedBuilder, BashVersion bashVersion) {
        super(wrappedBuilder);

        this.project = project;
        this.bashVersion = bashVersion;
        this.tokenRemapper = new BashTokenRemapper(this);
        setTokenTypeRemapper(tokenRemapper);
    }

    /**
     * A hack to let whitespace tokens be delivered by the builder on demand.
     * It changes the internal, unique index of the token so it's not filtered out any more.
     * <p/>
     * IntelliJ filters out the whitespace tokens.
     * <p/>
     * This method relies on the implementation of an token type. It changes the
     * internal index (which is used for equals, ...) to a value not available in other tokens.
     * This way we can trick the parser to deliver the whitespace tokens to use.
     * <p/>
     * In some areas Bash is whitespace sensitive, so we need to access these tokens in certain
     * places.
     *
     * @return True if the operation has been successful.
     */
    public boolean enableWhitespace() {
        //optimization to avoid as many reflective calls as possible
        if (!whitespaceEnabled) {
            whitespaceEnabled = true;

            return ReflectionUtil.setShort(BashTokenTypes.WHITESPACE, "myIndex", (short) -1);
        }

        return true;
    }

    /**
     * A hack to disable whitespace parsing.
     * If restores the original value of the whitespace token.
     * <p/>
     * This reverses the effect done by enableWhitespace .
     *
     * @return True if successful.
     */
    public boolean disableWhitespace() {
        //optimization to avoid as many reflective calls as possible
        if (whitespaceEnabled) {
            whitespaceEnabled = false;

            return ReflectionUtil.setShort(BashTokenTypes.WHITESPACE, "myIndex", originalWhitespaceIndex);
        }

        return true;
    }

    /**
     * Overloaded method to provide whitespac tokens on demand.
     *
     * @param withWhitespace If true whitespace tokens are not ignored. In this case the enableWhitespace hack is used to enable them.
     * @param remapping      If true the returned token is remapped
     * @return The token for the given conditions.
     */
    @Nullable
    public IElementType getTokenType(boolean withWhitespace, boolean remapping) {
        if (!remapping) {
            return getTokenType(withWhitespace);
        }

        return getRemappingTokenType(withWhitespace);
    }

    @Nullable
    public String getTokenText(boolean enableWhitespace) {
        if (enableWhitespace && rawLookup(0) == BashTokenTypes.WHITESPACE) {
            int startOffset = rawTokenTypeStart(0);
            if (startOffset == -1) {
                //first token
                startOffset = 0;
            }

            int length = rawTokenTypeStart(1) - startOffset;
            if (length == 1) {
                return "";
            }

            return StringUtils.repeat(" ", length);
        }

        return getTokenText();
    }

    @Nullable
    public IElementType getTokenType(boolean withWhitespace) {
        if (!withWhitespace) {
            return getTokenType();
        }

        return rawLookup(0);
    }

    @Nullable
    public IElementType getRemappingTokenType(boolean withWhitespace) {
        getParsingState().enterSimpleCommand();
        try {
            if (!withWhitespace) {
                return getTokenType();
            }

            return rawLookup(0);
        } finally {
            getParsingState().leaveSimpleCommand();
        }
    }

    public void advanceLexer(boolean useWhitespace) {
        if (useWhitespace) {
            enableWhitespace();
            try {
                advanceLexer();
            } finally {
                disableWhitespace();
            }
        } else {
            super.advanceLexer();
        }
    }

    /**
     * Eats all the following newline tokens.
     *
     * @return True if at least one newline has been read.
     */
    public boolean eatOptionalNewlines() {
        return eatOptionalNewlines(-1);
    }

    /**
     * Reads all line feed tokens of the stream until either there are no more newlines
     * or that maxNewLines is reached.
     *
     * @param maxNewlines The maximum amount of newlines which should be read.
     *                    A value of -1 means there's no limit in read line feed tokens.
     * @return True if at least one newline has been read.
     */
    public boolean eatOptionalNewlines(int maxNewlines) {
        return eatOptionalNewlines(maxNewlines, false);
    }

    public boolean eatOptionalNewlines(int maxNewlines, boolean withWhitespace) {
        if (maxNewlines < 0) {
            maxNewlines = Integer.MAX_VALUE;
        }

        boolean hasNewline;
        int readNewlines;

        if (withWhitespace) {
            //read whitespace tokens
            hasNewline = rawLookup(0) == BashTokenTypes.LINE_FEED;
            readNewlines = 0;
            while (rawLookup(0) == BashTokenTypes.LINE_FEED && readNewlines < maxNewlines) {
                advanceLexer();
                readNewlines++;
            }
        } else {
            //do not read whitespace tokens, step over it
            hasNewline = getTokenType() == BashTokenTypes.LINE_FEED;
            readNewlines = 0;
            while (getTokenType() == BashTokenTypes.LINE_FEED && readNewlines < maxNewlines) {
                advanceLexer();
                readNewlines++;
            }
        }

        return hasNewline;
    }

    public boolean isBash4() {
        return BashVersion.Bash_v4.equals(bashVersion);
    }

    private final BackquoteData backquoteData = new BackquoteData();
    private final HereDocData hereDocData = new HereDocData();
    private final ParsingStateData parsingStateData = new ParsingStateData();

    public void remapShebangToComment() {
        tokenRemapper.setMapShebangToComment(true);
    }

    public void enterHereDoc() {
        getParsingState().enterHereDoc();
    }

    public void leaveHereDoc() {
        getParsingState().leaveHereDoc();
    }

    /**
     * Returns the backquote data which holds the state of the backquote command parsing.
     *
     * @return The state for the backquote command parsing.
     */
    public BackquoteData getBackquoteData() {
        return backquoteData;
    }

    public HereDocData getHereDocData() {
        return hereDocData;
    }

    public ParsingStateData getParsingState() {
        return parsingStateData;
    }

    /**
     * BashPsiBuilder supports nested levels of error reporting. Error reporting can
     * be switched of on demand to make test functions possible which
     * call the regular parsing functions to check whether the upcoming stream of tokens is
     * valid.
     * Each call to this method has to be followed by a call to leaveLastErrorLevel() .
     *
     * @param status True if error reporting shoudl be switched on.
     *               False if no errors should be added to the resulting tree.
     */
    public void enterNewErrorLevel(boolean status) {
        errorsStatusStack.push(status);
    }

    /**
     * Removes the last error reporting state from the stack of saved states.
     */
    public void leaveLastErrorLevel() {
        if (!errorsStatusStack.isEmpty()) {
            errorsStatusStack.pop();
        }
    }

    /**
     * Overridden method which only reports errors if error reporting is switched on.
     *
     * @param message The message to report.
     */
    public void error(String message) {
        if (getErrorReportingStatus()) {
            getOriginalPsiBuilder().error(message);
        } else if (log.isDebugEnabled()) {
            log.debug("Supressed psi error: " + message);
        }
    }

    public Project getProject() {
        return project;
    }

    /**
     * Returns an enhanced marker which knows about the error reporting state.
     * The error only adds errors to the tree if error reporting is enabled.
     * The marker has the same state as the markers returned by the wrapped PsiBuilder.
     * It only intercepts the error message reporting if necessary.
     *
     * @return The new marker.
     */
    @Override
    public Marker mark() {
        return new BashPsiMarker(getOriginalPsiBuilder().mark());
    }

    /**
     * Returns the state of error reporting.
     *
     * @return True if errors should be reported. False if not.
     */
    boolean getErrorReportingStatus() {
        return errorsStatusStack.isEmpty() || errorsStatusStack.peek();
    }

    /**
     * An enhanced marker which takes care of error reporting.
     *
     * @author Joachim Ansorg, mail@ansorg-it.com
     */
    private final class BashPsiMarker extends ForwardingMarker implements Marker {
        protected BashPsiMarker(final Marker original) {
            super(original);
        }

        @Override
        public void doneBefore(IElementType type, Marker beforeCandidate) {
            //IntelliJ's API assumes that before is a StartMarker and not another implementation
            //thus we have to pass the original marker
            Marker before = beforeCandidate instanceof ForwardingMarker ? ((ForwardingMarker) beforeCandidate).getOriginal() : beforeCandidate;
            super.doneBefore(type, before);
        }

        @Override
        public void error(final String errorMessage) {
            if (BashPsiBuilder.this.getErrorReportingStatus()) {
                original.error(errorMessage);
            } else {
                drop();
                if (log.isDebugEnabled()) {
                    log.debug("Marker: suppressed error " + errorMessage);
                }
            }
        }
    }
}
