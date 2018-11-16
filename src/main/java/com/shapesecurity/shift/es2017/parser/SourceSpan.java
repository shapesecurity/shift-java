package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.data.HashCodeBuilder;
import com.shapesecurity.functional.data.Maybe;
import javax.annotation.Nonnull;

public class SourceSpan {
    @Nonnull
    public final Maybe<String> source;
    @Nonnull
    public final SourceLocation start;
    @Nonnull
    public final SourceLocation end;

    public SourceSpan(@Nonnull Maybe<String> source, @Nonnull SourceLocation start, @Nonnull SourceLocation end) {
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
