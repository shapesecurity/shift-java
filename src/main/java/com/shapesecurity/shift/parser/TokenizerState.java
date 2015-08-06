package com.shapesecurity.shift.parser;

import org.jetbrains.annotations.NotNull;

public class TokenizerState {
    @NotNull
    public final int index;
    public final int line;
    public final int lineStart;
    public final int startIndex;
    public final int startLine;
    public final int startLineStart;
    public final int lastIndex;
    @NotNull
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
            @NotNull Token lookahead,
            boolean hasLineTerminatorBeforeNext
    ) {
        this.index = index;
        this.line = line;
        this.lineStart = lineStart;
        this.startIndex = startIndex;
        this.startLine = startLine;
        this.startLineStart = startLineStart;
        this.lastIndex = lastIndex;
        this.lookahead = lookahead;
        this.hasLineTerminatorBeforeNext = hasLineTerminatorBeforeNext;
    }
}
