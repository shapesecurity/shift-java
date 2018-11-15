package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.data.HashCodeBuilder;
import javax.annotation.Nonnull;

public class SourceLocation {
    @Nonnull
    public final Integer line;
    @Nonnull
    public final Integer column;
    @Nonnull
    public final Integer offset;

    public SourceLocation(@Nonnull Integer line, @Nonnull Integer column, @Nonnull Integer offset) {
        super();
        this.line = line;
        this.column = column;
        this.offset = offset;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SourceLocation && this.line.equals(((SourceLocation) object).line) &&
                this.column.equals(((SourceLocation) object).column) && this.offset.equals(
                ((SourceLocation) object).offset);
    }

    @Override
    public int hashCode() {
        int code = HashCodeBuilder.put(0, "SourceLocation");
        code = HashCodeBuilder.put(code, this.line);
        code = HashCodeBuilder.put(code, this.column);
        code = HashCodeBuilder.put(code, this.offset);
        return code;
    }
}
