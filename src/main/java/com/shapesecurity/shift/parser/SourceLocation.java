package com.shapesecurity.shift.parser;

import com.shapesecurity.functional.data.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

public class SourceLocation {
    @NotNull
    public final Integer line;
    @NotNull
    public final Integer column;
    @NotNull
    public final Integer offset;

    public SourceLocation(@NotNull Integer line, @NotNull Integer column, @NotNull Integer offset) {
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
