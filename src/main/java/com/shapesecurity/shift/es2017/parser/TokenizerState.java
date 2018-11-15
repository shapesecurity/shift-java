package com.shapesecurity.shift.es2017.parser;

import javax.annotation.Nonnull;

public class TokenizerState {
    @Nonnull
    public final int index;
    public final int line;
    public final int lineStart;
    public final int startIndex;
    public final int startLine;
    public final int startLineStart;
    public final int lastLine;
    public final int lastLineStart;
    public final int lastIndex;
    @Nonnull
    public final Token lookahead;
    public final boolean hasLineTerminatorBeforeNext;

    public TokenizerState(
            int index,
            int line,
            int lineStart,
            int startIndex,
            int startLine,
            int startLineStart,
            int lastIndex,
            int lastLine,
            int lastLineStart,
            @Nonnull Token lookahead,
            boolean hasLineTerminatorBeforeNext
    ) {
        this.index = index;
        this.line = line;
        this.lineStart = lineStart;
        this.startIndex = startIndex;
        this.startLine = startLine;
        this.startLineStart = startLineStart;
        this.lastIndex = lastIndex;
        this.lastLine = lastLine;
        this.lastLineStart = lastLineStart;
        this.lookahead = lookahead;
        this.hasLineTerminatorBeforeNext = hasLineTerminatorBeforeNext;
    }
}
