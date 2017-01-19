package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.data.HashCodeBuilder;
import com.shapesecurity.functional.data.Maybe;
import org.jetbrains.annotations.NotNull;

public class SourceSpan {
    @NotNull
    public final Maybe<String> source;
    @NotNull
    public final SourceLocation start;
    @NotNull
    public final SourceLocation end;

    public SourceSpan(@NotNull Maybe<String> source, @NotNull SourceLocation start, @NotNull SourceLocation end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SourceSpan && this.source.equals(((SourceSpan) object).source) && this.start.equals(
                ((SourceSpan) object).start) && this.end.equals(((SourceSpan) object).end);
    }

    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "SourceSpan");
        code = HashCodeBuilder.put(code, this.source);
        code = HashCodeBuilder.put(code, this.start);
        code = HashCodeBuilder.put(code, this.end);
        return code;
    }
}
