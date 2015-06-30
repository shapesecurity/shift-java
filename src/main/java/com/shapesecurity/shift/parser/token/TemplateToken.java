package com.shapesecurity.shift.parser.token;

import com.shapesecurity.shift.parser.SourceRange;
import com.shapesecurity.shift.parser.Token;
import com.shapesecurity.shift.parser.TokenType;
import org.jetbrains.annotations.NotNull;

public final class TemplateToken extends Token {

  public final boolean tail;

  public TemplateToken(@NotNull SourceRange slice, boolean tail) {
    super(TokenType.TEMPLATE, slice, false);
    this.tail = tail;
  }

  @NotNull
  @Override
  public CharSequence getValueString() {
    return this.slice;
  }
}
