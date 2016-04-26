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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.LimitedPool;
import com.intellij.util.containers.Stack;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * The PsiBuilder which has been enhanced to be more helpful for Bash parsing.
 * It provides the possiblity to enable whitespace tokens on demand.
 * <br>
 * <br>
 * @author jansorg
 */
public final class BashPsiBuilder extends PsiBuilderAdapter implements PsiBuilder {
    static final Logger log = Logger.getInstance("#bash.BashPsiBuilder");

    private final Stack<Boolean> errorsStatusStack = new Stack<Boolean>();
    private final BashTokenRemapper tokenRemapper;
    private final BashVersion bashVersion;

    //reuse markers in the pool, the PsiBuilder allocates a lot of marker. Markers can be cleaned fairly simply so we will reuse them
    //the original PsiBUilderImpl does it in a similair way
    private final LimitedPool<BashPsiMarker> markerPool = new LimitedPool<BashPsiMarker>(750, new LimitedPool.ObjectFactory<BashPsiMarker>() {
        @Override
        public BashPsiMarker create() {
            return new BashPsiMarker();
        }

        @Override
        public void cleanup(final BashPsiMarker startMarker) {
            startMarker.clean();
        }
    });
    private final BackquoteData backquoteData = new BackquoteData();
    private final ParsingStateData parsingStateData = new ParsingStateData();
    private final Project project;

    public BashPsiBuilder(Project project, PsiBuilder wrappedBuilder, BashVersion bashVersion) {
        super(wrappedBuilder);

        this.project = project;
        this.bashVersion = bashVersion;
        this.tokenRemapper = new BashTokenRemapper(this);
        setTokenTypeRemapper(tokenRemapper);
    }

    /**
     * @param enableWhitespace
     * @return
     */
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
    public final IElementType getTokenType(boolean withWhitespace) {
        return withWhitespace ? rawLookup(0) : getTokenType();
    }

    @Nullable
    public IElementType getRemappingTokenType() {
        try {
            parsingStateData.enterSimpleCommand();

            return getTokenType();
        } finally {
            parsingStateData.leaveSimpleCommand();
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

    public void remapShebangToComment() {
        tokenRemapper.enableShebangToCommentMapping();
    }

    /**
     * Returns the backquote data which holds the state of the backquote command parsing.
     *
     * @return The state for the backquote command parsing.
     */
    public BackquoteData getBackquoteData() {
        return backquoteData;
    }

    public final ParsingStateData getParsingState() {
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
        if (isErrorReportingEnabled()) {
            myDelegate.error(message);
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
        BashPsiMarker marker = markerPool.alloc();
        marker.psiBuilder = this;
        marker.original = myDelegate.mark();

        return marker;
    }

    /**
     * Returns the state of error reporting.
     *
     * @return True if errors should be reported. False if not.
     */
    boolean isErrorReportingEnabled() {
        return errorsStatusStack.isEmpty() || errorsStatusStack.peek();
    }


    private void recycle(BashPsiMarker marker) {
        markerPool.recycle(marker);
    }

    /**
     * An enhanced marker which takes care of error reporting.
     *
     * @author jansorg, mail@ansorg-it.com
     */
    private static final class BashPsiMarker implements Marker {
        BashPsiBuilder psiBuilder;
        Marker original;

        public BashPsiMarker() {
        }

        @Override
        public void doneBefore(IElementType type, Marker beforeCandidate) {
            //IntelliJ's API assumes that before is a StartMarker and not another implementation
            //thus we have to pass the original marker
            Marker before = beforeCandidate instanceof BashPsiMarker ? ((BashPsiMarker) beforeCandidate).original : beforeCandidate;

            original.doneBefore(type, before);
            psiBuilder.recycle(this);
        }

        @Override
        public void error(final String errorMessage) {
            if (psiBuilder.isErrorReportingEnabled()) {
                original.error(errorMessage);
            } else {
                drop();

                if (log.isDebugEnabled()) {
                    log.debug("Marker: suppressed error " + errorMessage);
                }
            }
        }

        @Override
        public void drop() {
            original.drop();

            psiBuilder.recycle(this);
        }


        @Override
        public void done(IElementType type) {
            original.done(type);

            psiBuilder.recycle(this);
        }

        @Override
        public void doneBefore(IElementType type, Marker before, String errorMessage) {
            original.doneBefore(type, before, errorMessage);

            psiBuilder.recycle(this);
        }

        @Override
        public void errorBefore(String message, Marker marker) {
            original.errorBefore(message, marker);

            psiBuilder.recycle(this);
        }

        @Override
        public void rollbackTo() {
            original.rollbackTo();

            psiBuilder.recycle(this);
        }

        public void clean() {
            this.original = null;
            this.psiBuilder = null;
        }

        public void collapse(IElementType iElementType) {
            original.collapse(iElementType);

            psiBuilder.recycle(this);
        }

        public Marker precede() {
            return original.precede();
        }

        public void setCustomEdgeTokenBinders(@Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder, @Nullable WhitespacesAndCommentsBinder whitespacesAndCommentsBinder1) {
            original.setCustomEdgeTokenBinders(whitespacesAndCommentsBinder, whitespacesAndCommentsBinder1);
        }
    }
}
