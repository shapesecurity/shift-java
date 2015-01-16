/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shapesecurity.shift.parser;

import static com.shapesecurity.shift.parser.ErrorMessages.STRICT_RESERVED_WORD;
import static com.shapesecurity.shift.parser.ErrorMessages.UNEXPECTED_EOS;
import static com.shapesecurity.shift.parser.ErrorMessages.UNEXPECTED_IDENTIFIER;
import static com.shapesecurity.shift.parser.ErrorMessages.UNEXPECTED_NUMBER;
import static com.shapesecurity.shift.parser.ErrorMessages.UNEXPECTED_RESERVED_WORD;
import static com.shapesecurity.shift.parser.ErrorMessages.UNEXPECTED_STRING;
import static com.shapesecurity.shift.parser.ErrorMessages.UNEXPECTED_TOKEN;

import com.shapesecurity.shift.ast.SourceLocation;
import com.shapesecurity.shift.parser.token.EOFToken;
import com.shapesecurity.shift.parser.token.FalseLiteralToken;
import com.shapesecurity.shift.parser.token.IdentifierToken;
import com.shapesecurity.shift.parser.token.KeywordToken;
import com.shapesecurity.shift.parser.token.NullLiteralToken;
import com.shapesecurity.shift.parser.token.NumericLiteralToken;
import com.shapesecurity.shift.parser.token.PunctuatorToken;
import com.shapesecurity.shift.parser.token.RegularExpressionLiteralToken;
import com.shapesecurity.shift.parser.token.StringLiteralToken;
import com.shapesecurity.shift.parser.token.TrueLiteralToken;
import com.shapesecurity.shift.utils.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Tokenizer {
  static final HashSet<String> STRICT_MODE_RESERVED_WORD = new HashSet<>(Arrays.asList("implements",
      "interface", "package", "private", "protected", "public", "static", "yield", "let"));
  private static final TokenType[] ONE_CHAR_PUNCTUATOR =
      new TokenType[]{TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.NOT, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.MOD, TokenType.BIT_AND, TokenType.ILLEGAL,
                      TokenType.LPAREN, TokenType.RPAREN, TokenType.MUL, TokenType.ADD, TokenType.COMMA, TokenType.SUB,
                      TokenType.PERIOD, TokenType.DIV, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.COLON, TokenType.SEMICOLON, TokenType.LT,
                      TokenType.ASSIGN, TokenType.GT, TokenType.CONDITIONAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.LBRACK, TokenType.ILLEGAL, TokenType.RBRACK, TokenType.BIT_XOR, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.ILLEGAL,
                      TokenType.ILLEGAL, TokenType.ILLEGAL, TokenType.LBRACE, TokenType.BIT_OR, TokenType.RBRACE,
                      TokenType.BIT_NOT};
  private static final boolean[] PUNCTUATOR_START =
      new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, true, false, false, false, true, true, false, true, true, true,
                    true, true, true, false, false, false, false, false, false, false, false, false, false, false,
                    false, true, true, true, true, true, true, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, true, false, true, true, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, true, true, true, true, false};
  private static final boolean[] IDENTIFIER_START =
      new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, true, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false, false, true, true, true, true, true, true,
                    true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                    true, true, true, true, false, true, false, false, true, false, true, true, true, true, true, true,
                    true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                    true, true, true, true, false, false, false, false, false};
  @NotNull
  final String source;
  private final boolean directed;
  @NotNull
  protected Token lookahead;
  boolean hasLineTerminatorBeforeNext;
  boolean strict;
  boolean collectingToken;
  private int index, line, lineStart;
  private int lastIndex, lastLine, lastLineStart;
  private int startIndex, startLine, startLineStart;

  @Nullable
  private SourceRange lastWhitespace;
  @Nullable
  private Token prevToken;
  @Nullable
  private TokenType curlyCheckToken;
  @Nullable
  private TokenType funcCheckToken;
  @Nullable
  private TokenType parenCheckToken;

  private SourceLocation cachedSourceLocation;
  private int lastCachedSourceLocation = -1;

  public Tokenizer(@NotNull String source) throws JsError {
    this(false, source);
  }

  Tokenizer(boolean directed, @NotNull String source) throws JsError {
    this.directed = directed;
    this.source = source;
    this.lookahead = this.collectToken();
    this.hasLineTerminatorBeforeNext = false;
  }

  @NotNull
  public static ArrayList<Token> tokenize(@NotNull String source) throws JsError {
    Tokenizer tokenizer = new Tokenizer(source);
    ArrayList<Token> result = new ArrayList<>();
    while (tokenizer.lookahead.type != TokenType.EOS) {
      result.add(tokenizer.lex());
    }
    return result;
  }

  private static boolean cse2(@NotNull CharSequence id, char ch1, char ch2) {
    return id.charAt(1) == ch1 && id.charAt(2) == ch2;
  }

  private static boolean cse3(@NotNull CharSequence id, char ch1, char ch2, char ch3) {
    return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3;
  }

  private static boolean cse4(@NotNull CharSequence id, char ch1, char ch2, char ch3, char ch4) {
    return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4;
  }

  private static boolean cse5(@NotNull CharSequence id, char ch1, char ch2, char ch3, char ch4, char ch5) {
    return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4 && id.charAt(5)
        == ch5;
  }

  private static boolean cse6(@NotNull CharSequence id, char ch1, char ch2, char ch3, char ch4, char ch5, char ch6) {
    return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4 && id.charAt(5)
        == ch5
        && id.charAt(6) == ch6;
  }

  private static boolean cse7(
      @NotNull CharSequence id,
      char ch1,
      char ch2,
      char ch3,
      char ch4,
      char ch5,
      char ch6,
      char ch7) {
    return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4 && id.charAt(5)
        == ch5
        && id.charAt(6) == ch6 && id.charAt(7) == ch7;
  }

  // 7.6.1.1 Keywords
  @SuppressWarnings("ConfusingElseBranch")
  @NotNull
  private TokenType getKeyword(@NotNull CharSequence id) {
    // 'const' is specialized as Keyword in V8.
    // 'yield' and 'let' are for compatibility with SpiderMonkey and ES.next.
    // Some others are from future reserved words.

    if (id.length() == 1 || id.length() > 10) {
      return TokenType.ILLEGAL;
    }
    switch (id.length()) {
    case 2:
      switch (id.charAt(0)) {
      case 'i':
        switch (id.charAt(1)) {
        case 'f':
          return TokenType.IF;
        case 'n':
          return TokenType.IN;
        default:
          break;
        }
        break;
      case 'd':
        if (id.charAt(1) == 'o') {
          return TokenType.DO;
        }
        break;
      default:
        break;
      }
      break;
    case 3:
      switch (id.charAt(0)) {
      case 'v':
        if (cse2(id, 'a', 'r')) {
          return TokenType.VAR;
        }
        break;
      case 'f':
        if (cse2(id, 'o', 'r')) {
          return TokenType.FOR;
        }
        break;
      case 'n':
        if (cse2(id, 'e', 'w')) {
          return TokenType.NEW;
        }
        break;
      case 't':
        if (cse2(id, 'r', 'y')) {
          return TokenType.TRY;
        }
        break;
      case 'l':
        if (cse2(id, 'e', 't')) {
          return TokenType.LET;
        }
        break;
      default:
        break;
      }
      break;
    case 4:
      switch (id.charAt(0)) {
      case 't':
        if (cse3(id, 'h', 'i', 's')) {
          return TokenType.THIS;
        }
        break;
      case 'e':
        if (cse3(id, 'l', 's', 'e')) {
          return TokenType.ELSE;
        } else if (cse3(id, 'n', 'u', 'm')) {
          return TokenType.FUTURE_RESERVED_WORD;
        }
        break;
      case 'c':
        if (cse3(id, 'a', 's', 'e')) {
          return TokenType.CASE;
        }
        break;
      case 'v':
        if (cse3(id, 'o', 'i', 'd')) {
          return TokenType.VOID;
        }
        break;
      case 'w':
        if (cse3(id, 'i', 't', 'h')) {
          return TokenType.WITH;
        }
        break;
      default:
        break;
      }
      break;
    case 5:
      switch (id.charAt(0)) {
      case 'w': // WHILE
        if (cse4(id, 'h', 'i', 'l', 'e')) {
          return TokenType.WHILE;
        }
        break;
      case 'b': // BREAK
        if (cse4(id, 'r', 'e', 'a', 'k')) {
          return TokenType.BREAK;
        }
        break;
      case 'c': // CATCH
        if (cse4(id, 'a', 't', 'c', 'h')) {
          return TokenType.CATCH;
        } else if (cse4(id, 'o', 'n', 's', 't')) {
          return TokenType.CONST;
        } else if (cse4(id, 'l', 'a', 's', 's')) {
          return TokenType.FUTURE_RESERVED_WORD;
        }
        break;
      case 't': // THROW
        if (cse4(id, 'h', 'r', 'o', 'w')) {
          return TokenType.THROW;
        }
        break;
      case 'y': // YIELD
        if (cse4(id, 'i', 'e', 'l', 'd')) {
          return this.strict ? TokenType.FUTURE_STRICT_RESERVED_WORD : TokenType.ILLEGAL;
        }
        break;
      case 's': // SUPER
        if (cse4(id, 'u', 'p', 'e', 'r')) {
          return TokenType.FUTURE_RESERVED_WORD;
        }
        break;
      default:
        break;
      }
      break;
    case 6:
      switch (id.charAt(0)) {
      case 'r':
        if (cse5(id, 'e', 't', 'u', 'r', 'n')) {
          return TokenType.RETURN;
        }
        break;
      case 't':
        if (cse5(id, 'y', 'p', 'e', 'o', 'f')) {
          return TokenType.TYPEOF;
        }
        break;
      case 'd':
        if (cse5(id, 'e', 'l', 'e', 't', 'e')) {
          return TokenType.DELETE;
        }
        break;
      case 's':
        if (cse5(id, 'w', 'i', 't', 'c', 'h')) {
          return TokenType.SWITCH;
        } else if (this.strict && cse5(id, 't', 'a', 't', 'i', 'c')) {
          return TokenType.FUTURE_STRICT_RESERVED_WORD;
        }
        break;
      case 'e':
        if (cse5(id, 'x', 'p', 'o', 'r', 't')) {
          return TokenType.FUTURE_RESERVED_WORD;
        }
        break;
      case 'i':
        if (cse5(id, 'm', 'p', 'o', 'r', 't')) {
          return TokenType.FUTURE_RESERVED_WORD;
        }
        break;
      case 'p':
        if (this.strict && cse5(id, 'u', 'b', 'l', 'i', 'c')) {
          return TokenType.FUTURE_STRICT_RESERVED_WORD;
        }
        break;
      default:
        break;
      }
      break;
    case 7:
      switch (id.charAt(0)) {
      case 'd': // default
        if (cse6(id, 'e', 'f', 'a', 'u', 'l', 't')) {
          return TokenType.DEFAULT;
        }
        break;
      case 'f': // finally
        if (cse6(id, 'i', 'n', 'a', 'l', 'l', 'y')) {
          return TokenType.FINALLY;
        }
        break;
      case 'e': // extends
        if (cse6(id, 'x', 't', 'e', 'n', 'd', 's')) {
          return TokenType.FUTURE_RESERVED_WORD;
        }
        break;
      case 'p':
        if (this.strict) {
          String s = id.toString();
          if ("private".equals(s) || "package".equals(s)) {
            return TokenType.FUTURE_STRICT_RESERVED_WORD;
          }
        }
        break;
      default:
        break;
      }
      break;
    case 8:
      switch (id.charAt(0)) {
      case 'f':
        if (cse7(id, 'u', 'n', 'c', 't', 'i', 'o', 'n')) {
          return TokenType.FUNCTION;
        }
        break;
      case 'c':
        if (cse7(id, 'o', 'n', 't', 'i', 'n', 'u', 'e')) {
          return TokenType.CONTINUE;
        }
        break;
      case 'd':
        if (cse7(id, 'e', 'b', 'u', 'g', 'g', 'e', 'r')) {
          return TokenType.DEBUGGER;
        }
        break;
      default:
        break;
      }
      break;
    case 9:
      if (this.strict && (id.charAt(0) == 'p' || id.charAt(0) == 'i')) {
        String s = id.toString();
        if ("protected".equals(s) || "interface".equals(s)) {
          return TokenType.FUTURE_STRICT_RESERVED_WORD;
        }
      }
      break;
    case 10:
      String s = id.toString();
      if ("instanceof".equals(s)) {
        return TokenType.INSTANCEOF;
      } else if (this.strict && "implements".equals(s)) {
        return TokenType.FUTURE_STRICT_RESERVED_WORD;
      }
      break;
    default:
      break;
    }
    return TokenType.ILLEGAL;
  }

  @NotNull
  private JsError createILLEGAL() {
    return this.createError(ErrorMessages.UNEXPECTED_ILLEGAL_TOKEN);
  }

  JsError createUnexpected(@NotNull Token token) {
    switch (token.type.klass) {
    case Eof:
      return this.createError(UNEXPECTED_EOS);
    case NumericLiteral:
      return this.createError(UNEXPECTED_NUMBER);
    case StringLiteral:
      return this.createError(UNEXPECTED_STRING);
    case Ident:
      return this.createError(UNEXPECTED_IDENTIFIER);
    case Keyword:
      if ((token.type == TokenType.FUTURE_RESERVED_WORD)) {
        return this.createError(UNEXPECTED_RESERVED_WORD);
      }
      if ((token.type == TokenType.FUTURE_STRICT_RESERVED_WORD)) {
        return this.createError(STRICT_RESERVED_WORD);
      }
      return this.createError(UNEXPECTED_TOKEN, token.slice.getString());
    case Punctuator:
      return this.createError(UNEXPECTED_TOKEN, token.type.toString());
    default:
      break;
    }
    return this.createError(UNEXPECTED_TOKEN, token.getValueString());
  }

  @NotNull
  JsError createError(@NotNull String message, @NotNull Object... args) {
    String msg = String.format(message, args);
    if (this.collectingToken) {
      return new JsError(this.index, this.line + 1, this.index - this.lineStart, msg);
    } else {
      return new JsError(this.startIndex, this.startLine + 1, this.startIndex - this.startLineStart, msg);
    }
  }

  @NotNull
  JsError createErrorWithToken(@NotNull SourceLocation location, @NotNull String message, @NotNull Object... args) {
    String msg = String.format(message, args);
    return new JsError(location.offset, location.line + 1, location.column, msg);
  }

  @NotNull
  SourceLocation getLocation() {
    if (this.lastCachedSourceLocation != this.index) {
      this.cachedSourceLocation = new SourceLocation(this.startLine, this.startIndex - this.startLineStart, this.startIndex);
      this.lastCachedSourceLocation = this.index;
    }
    return this.cachedSourceLocation;
  }

  @NotNull
  private SourceRange getSlice(int start) {
    return new SourceRange(start, this.index, this.source);
  }

  @NotNull
  SourceRange getSliceBeforeLookahead(int start) {
    return new SourceRange(start, this.lastIndex, this.source);
  }

  private void skipSingleLineComment(int offset) {
    this.index += offset;
    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      this.index++;
      if (Utils.isLineTerminator(ch)) {
        this.hasLineTerminatorBeforeNext = true;
        if (ch == '\r' && this.index < this.source.length() && this.source.charAt(this.index) == '\n') {
          this.index++;
        }
        this.lineStart = this.index;
        this.line++;
        return;
      }
    }
  }

  private void skipMultiLineComment() throws JsError {
    this.index += 2;
    int length = this.source.length();
    int i = this.index;
    while (i < length) {
      char ch = this.source.charAt(i);
      if (ch < 0x80) {
        switch (ch) {
        case '*':
          // Block comment ends with '*/'.
          if (i + 1 < length && this.source.charAt(i + 1) == '/') {
            this.index = i + 2;
            return;
          }
          i++;
          break;
        case '\n':
          this.hasLineTerminatorBeforeNext = true;
          i++;
          this.lineStart = i;
          this.line++;
          break;
        case '\r':
          this.hasLineTerminatorBeforeNext = true;
          if (i < length - 1 && this.source.charAt(i + 1) == '\n') {
            i++;
          }
          i++;
          this.lineStart = i;
          this.line++;
          break;
        default:
          i++;
        }
      } else if (ch == 0x2028 || ch == 0x2029) {
        i++;
        this.lineStart = i;
        this.line++;
      } else {
        i++;
      }
    }
    this.index = i;
    throw this.createILLEGAL();
  }

  private void skipComment() throws JsError {
    boolean isLineStart = this.index == 0;
    int length = this.source.length();

    while (this.index < length) {
      char ch = this.source.charAt(this.index);
      if (Utils.isWhitespace(ch)) {
        this.index++;
      } else if (Utils.isLineTerminator(ch)) {
        this.hasLineTerminatorBeforeNext = true;
        this.index++;
        if (ch == '\r' && this.index < length && this.source.charAt(this.index) == '\n') {
          this.index++;
        }
        this.lineStart = this.index;
        this.line++;
        isLineStart = true;
      } else if (ch == '/') {
        if (this.index + 1 >= length) {
          break;
        }
        ch = this.source.charAt(this.index + 1);
        if (ch == '/') {
          this.skipSingleLineComment(2);
          isLineStart = true;
        } else if (ch == '*') {
          this.skipMultiLineComment();
        } else {
          break;
        }
      } else if (isLineStart && ch == '-') {
        if (this.index + 2 >= length) {
          break;
        }
        // U+003E is '>'
        if ((this.source.charAt(this.index + 1) == '-') && (this.source.charAt(this.index + 2) == '>')) {
          // '-->' is a single-line comment
          this.skipSingleLineComment(3);
        } else {
          break;
        }
      } else if (ch == '<') {
        if (this.index + 4 <= length && this.source.charAt(this.index + 1) == '!' && this.source.charAt(this.index + 2)
            == '-' && this.source.charAt(
            this.index + 3) == '-') {
          this.skipSingleLineComment(4);
        } else {
          break;
        }
      } else {
        break;
      }
    }
  }

  private int scanHexEscape4() {
    if (this.index + 4 > this.source.length()) {
      return -1;
    }
    int r1 = Utils.getHexValue(this.source.charAt(this.index));
    if (r1 == -1) {
      return -1;
    }
    int r2 = Utils.getHexValue(this.source.charAt(this.index + 1));
    if (r2 == -1) {
      return -1;
    }
    int r3 = Utils.getHexValue(this.source.charAt(this.index + 2));
    if (r3 == -1) {
      return -1;
    }
    int r4 = Utils.getHexValue(this.source.charAt(this.index + 3));
    if (r4 == -1) {
      return -1;
    }
    this.index += 4;
    return r1 << 12 | r2 << 8 | r3 << 4 | r4;
  }

  private int scanHexEscape2() {
    if (this.index + 2 > this.source.length()) {
      return -1;
    }
    int r1 = Utils.getHexValue(this.source.charAt(this.index));
    if (r1 == -1) {
      return -1;
    }
    int r2 = Utils.getHexValue(this.source.charAt(this.index + 1));
    if (r2 == -1) {
      return -1;
    }
    this.index += 2;
    return r1 << 4 | r2;
  }

  @NotNull
  private CharSequence getEscapedIdentifier() throws JsError {
    char ch = this.source.charAt(this.index);
    this.index++;
    if (this.index >= this.source.length()) {
      throw this.createILLEGAL();
    }

    StringBuilder id = new StringBuilder();

    if (ch == '\\') {
      if (this.source.charAt(this.index) != 'u') {
        throw this.createILLEGAL();
      }
      this.index++;
      if (this.index >= this.source.length()) {
        throw this.createILLEGAL();
      }
      int ich = this.scanHexEscape4();
      if (ich < 0 || ich == '\\' || !Utils.isIdentifierStart((char) ich)) {
        throw this.createILLEGAL();
      }
      ch = (char) ich;
    }
    id.append(ch);

    while (this.index < this.source.length()) {
      ch = this.source.charAt(this.index);
      if (!Utils.isIdentifierPart(ch) && ch != '\\') {
        break;
      }
      this.index++;
      if (ch == '\\') {
        if (this.index >= this.source.length()) {
          throw this.createILLEGAL();
        }
        if (this.source.charAt(this.index) != 'u') {
          throw this.createILLEGAL();
        }
        this.index++;
        if (this.index >= this.source.length()) {
          throw this.createILLEGAL();
        }
        int ich = this.scanHexEscape4();
        if (ich < 0 || ich == '\\' || !Utils.isIdentifierPart((char) ich)) {
          throw this.createILLEGAL();
        }
        ch = (char) ich;
      }
      id.append(ch);
    }

    return id;
  }

  @NotNull
  private CharSequence getIdentifier() throws JsError {
    int start = this.index;
    this.index++;
    int l = this.source.length();
    int i = this.index;
    while (i < l) {
      char ch = this.source.charAt(i);
      if (ch == '\\') {
        // Go back and try the hard one.
        this.index = start;
        return this.getEscapedIdentifier();
      } else if (Utils.isIdentifierPart(ch)) {
        i++;
      } else {
        break;
      }
    }
    this.index = i;
    return this.getSlice(start);
  }

  @NotNull
  private Token scanIdentifier() throws JsError {
    int start = this.index;

    // Backslash (U+005C) starts an escaped character.
    CharSequence id = this.source.charAt(this.index) == '\\' ? this.getEscapedIdentifier() : this.getIdentifier();

    // There is no keyword or literal with only one character.
    // Thus, it must be an identifier.
    SourceRange slice = this.getSlice(start);

    if ((id.length() == 1)) {
      return new IdentifierToken(slice);
    }

    TokenType subType = this.getKeyword(id);
    if (subType != TokenType.ILLEGAL) {
      return new KeywordToken(subType, slice);
    }

    if (id.length() == 4) {
      id = id.toString();
      if ("null".equals(id)) {
        return new NullLiteralToken(slice);
      } else if ("true".equals(id)) {
        return new TrueLiteralToken(slice);
      }
    }

    if (id.length() == 5 && "false".equals(id.toString())) {
      return new FalseLiteralToken(slice);
    }

    return new IdentifierToken(slice);
  }

  @NotNull
  private TokenType scanPunctuatorHelper() {
    char ch1 = this.source.charAt(this.index);

    switch (ch1) {
    // Check for most common single-character punctuators.
    case '.':
      return TokenType.PERIOD;
    case '(':
      return TokenType.LPAREN;
    case ')':
    case ';':
    case ',':
      return ONE_CHAR_PUNCTUATOR[ch1];
    case '{':
      return TokenType.LBRACE;
    case '}':
    case '[':
    case ']':
    case ':':
    case '?':
    case '~':
      return ONE_CHAR_PUNCTUATOR[ch1];
    default:
      // '=' (U+003D) marks an assignment or comparison operator.
      if (this.index + 1 < this.source.length() && this.source.charAt(this.index + 1) == '=') {
        switch (ch1) {
        case '=':
          if (this.index + 2 < this.source.length() && this.source.charAt(this.index + 2) == '=') {
            return TokenType.EQ_STRICT;
          }
          return TokenType.EQ;
        case '!':
          if (this.index + 2 < this.source.length() && this.source.charAt(this.index + 2) == '=') {
            return TokenType.NE_STRICT;
          }
          return TokenType.NE;
        case '|':
          return TokenType.ASSIGN_BIT_OR;
        case '+':
          return TokenType.ASSIGN_ADD;
        case '-':
          return TokenType.ASSIGN_SUB;
        case '*':
          return TokenType.ASSIGN_MUL;
        case '<':
          return TokenType.LTE;
        case '>':
          return TokenType.GTE;
        case '/':
          return TokenType.ASSIGN_DIV;
        case '%':
          return TokenType.ASSIGN_MOD;
        case '^':
          return TokenType.ASSIGN_BIT_XOR;
        case '&':
          return TokenType.ASSIGN_BIT_AND;
        default:
          break; //failed
        }
      }
    }

    if (this.index + 1 < this.source.length()) {
      char ch2 = this.source.charAt(this.index + 1);
      if (ch1 == ch2) {
        if (this.index + 2 < this.source.length()) {
          char ch3 = this.source.charAt(this.index + 2);
          if (ch1 == '>' && ch3 == '>') {
            // 4-character punctuator: >>>=
            if (this.index + 3 < this.source.length() && this.source.charAt(this.index + 3) == '=') {
              return TokenType.ASSIGN_SHR_UNSIGNED;
            }
            return TokenType.SHR_UNSIGNED;
          }

          if (ch1 == '<' && ch3 == '=') {
            return TokenType.ASSIGN_SHL;
          }

          if (ch1 == '>' && ch3 == '=') {
            return TokenType.ASSIGN_SHR;
          }
        }
        // Other 2-character punctuators: ++ -- << >> && ||
        switch (ch1) {
        case '+':
          return TokenType.INC;
        case '-':
          return TokenType.DEC;
        case '<':
          return TokenType.SHL;
        case '>':
          return TokenType.SHR;
        case '&':
          return TokenType.AND;
        case '|':
          return TokenType.OR;
        default:
          break; //failed
        }
      }
    }

    return ONE_CHAR_PUNCTUATOR[ch1];
  }

  // 7.7 Punctuators
  @NotNull
  private Token scanPunctuator() {
    int start = this.index;
    TokenType subType = this.scanPunctuatorHelper();
    this.index += subType.toString().length();
    return new PunctuatorToken(subType, this.getSlice(start));
  }

  @NotNull
  private Token scanHexLiteral(int start) throws JsError {
    BigInteger value = BigInteger.ZERO;
    int i = this.index;
    while (i < this.source.length()) {
      char ch = this.source.charAt(i);
      int hex = Utils.getHexValue(ch);
      if (hex == -1) {
        break;
      }
      value = value.shiftLeft(4);
      value = value.add(BigInteger.valueOf(hex));
      i++;
    }

    if (this.index == i) {
      throw this.createILLEGAL();
    }

    if (i < this.source.length() && Utils.isIdentifierStart(this.source.charAt(i))) {
      throw this.createILLEGAL();
    }

    this.index = i;

    return new NumericLiteralToken(this.getSlice(start), value.doubleValue());
  }

  @NotNull
  private Token scanOctalLiteral(int start) throws JsError {
    BigInteger value = BigInteger.ZERO;
    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      if (!('0' <= ch && ch <= '7')) {
        break;
      }
      this.index++;
      value = value.shiftLeft(3);
      value = value.add(BigInteger.valueOf(ch - '0'));
    }

    if (this.index < this.source.length() && (Utils.isIdentifierStart(this.source.charAt(this.index)) || Utils
        .isDecimalDigit(this.source.charAt(this.index)))) {
      throw this.createILLEGAL();
    }

    return new NumericLiteralToken(this.getSlice(start), value.doubleValue(), true);
  }

  @NotNull
  private Token scanNumericLiteral() throws JsError {
    @NotNull
    BigInteger value = BigInteger.ZERO;
    char ch = this.source.charAt(this.index);
    // assert(ch == '.' || '0' <= ch && ch <= '9')
    int start = this.index;

    if (ch == '0') {
      this.index++;
      if (this.index < this.source.length()) {
        ch = this.source.charAt(this.index);
        if (ch == 'x' || ch == 'X') {
          this.index++;
          return this.scanHexLiteral(start);
        } else if ('0' <= ch && ch <= '9') {
          return this.scanOctalLiteral(start);
        }
      } else {
        return new NumericLiteralToken(this.getSlice(start), 0);
      }
    } else if (ch != '.') {
      // Must be '1'..'9'
      ch = this.source.charAt(this.index);
      while ('0' <= ch && ch <= '9') {
        value = value.multiply(BigInteger.TEN);
        value = value.add(BigInteger.valueOf(ch - '0'));
        this.index++;
        if (this.index == this.source.length()) {
          return new NumericLiteralToken(this.getSlice(start), value.doubleValue());
        }
        ch = this.source.charAt(this.index);
      }
    }

    int e = 0;
    if (ch == '.') {
      this.index++;
      if (this.index == this.source.length()) {
        return new NumericLiteralToken(this.getSlice(start), value.doubleValue());
      }

      ch = this.source.charAt(this.index);
      while ('0' <= ch && ch <= '9') {
        e++;
        value = value.multiply(BigInteger.TEN);
        value = value.add(BigInteger.valueOf(ch - '0'));
        this.index++;
        if (this.index == this.source.length()) {
          return new NumericLiteralToken(this.getSlice(start), new BigDecimal(value, e).doubleValue());
        }
        ch = this.source.charAt(this.index);
      }
    }

    // EOF not reached here
    if (ch == 'e' || ch == 'E') {
      this.index++;
      if (this.index == this.source.length()) {
        throw this.createILLEGAL();
      }

      ch = this.source.charAt(this.index);
      boolean neg = false;
      if (ch == '+' || ch == '-') {
        neg = ch == '-';
        this.index++;
        if (this.index == this.source.length()) {
          throw this.createILLEGAL();
        }
        ch = this.source.charAt(this.index);
      }

      int f = 0;
      if ('0' <= ch && ch <= '9') {
        while ('0' <= ch && ch <= '9') {
          f *= 10;
          f += ch - '0';
          this.index++;
          if (this.index == this.source.length()) {
            break;
          }
          ch = this.source.charAt(this.index);
        }
      } else {
        throw this.createILLEGAL();
      }
      e += neg ? f : -f;
    }

    if (Utils.isIdentifierStart(ch)) {
      throw this.createILLEGAL();
    }

    return new NumericLiteralToken(this.getSlice(start), new BigDecimal(value, e).doubleValue());
  }

  // 7.8.4 String Literals
  @NotNull
  private Token scanStringLiteral() throws JsError {
    StringBuilder str = new StringBuilder();

    char quote = this.source.charAt(this.index);
    //	assert((quote == '\'' || quote == '"'),
    //		'String literal must starts with a quote')

    int start = this.index;
    this.index++;

    boolean octal = false;
    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      if (ch == quote) {
        this.index++;
        return new StringLiteralToken(this.getSlice(start), str.toString(), octal);
      } else if (ch == '\\') {
        this.index++;
        if (this.index == this.source.length()) {
          throw this.createILLEGAL();
        }
        ch = this.source.charAt(this.index);
        if (!Utils.isLineTerminator(ch)) {
          switch (ch) {
          case 'n':
            str.append('\n');
            this.index++;
            break;
          case 'r':
            str.append('\r');
            this.index++;
            break;
          case 't':
            str.append('\t');
            this.index++;
            break;
          case 'u':
          case 'x':
            int restore = this.index;
            int unescaped;
            this.index++;
            if (this.index >= this.source.length()) {
              throw this.createILLEGAL();
            }
            unescaped = ch == 'u' ? this.scanHexEscape4() : this.scanHexEscape2();
            if (unescaped >= 0) {
              str.append((char) unescaped);
            } else {
              this.index = restore;
              str.append(ch);
              this.index++;
            }
            break;
          case 'b':
            str.append('\b');
            this.index++;
            break;
          case 'f':
            str.append('\f');
            this.index++;
            break;
          case 'v':
            str.append('\u000B');
            this.index++;
            break;
          default:
            if ('0' <= ch && ch <= '7') {
              octal = true;
              int octLen = 1;
              // 3 digits are only allowed when string starts
              // with 0, 1, 2, 3
              if ('0' <= ch && ch <= '3') {
                octLen = 0;
              }
              int code = 0;
              while (octLen < 3 && '0' <= ch && ch <= '7') {
                code *= 8;
                octLen++;
                code += ch - '0';
                this.index++;
                if (this.index == this.source.length()) {
                  throw this.createILLEGAL();
                }
                ch = this.source.charAt(this.index);
              }
              str.append((char) code);
            } else {
              str.append(ch);
              this.index++;
            }
          }
        } else {
          this.hasLineTerminatorBeforeNext = true;
          this.index++;
          if (ch == '\r' && this.source.charAt(this.index) == '\n') {
            this.index++;
          }
          this.lineStart = this.index;
          this.line++;
        }
      } else if (Utils.isLineTerminator(ch)) {
        throw this.createILLEGAL();
      } else {
        str.append(ch);
        this.index++;
      }
    }

    throw this.createILLEGAL();
  }

  @NotNull
  protected Token rescanRegExp() throws JsError {
    // rollback to the beginning of the token.
    this.index = this.startIndex;
    this.line = this.startLine;
    this.lineStart = this.startLineStart;
    this.collectingToken = true;
    this.lookahead = this.scanRegExp();
    this.collectingToken = false;
    return this.lookahead;
  }

  @NotNull
  private Token scanRegExp() throws JsError {
    int start = this.index;

    StringBuilder str = new StringBuilder();
    str.append('/');
    this.index++;

    boolean terminated = false;
    boolean classMarker = false;
    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      if (ch == '\\') {
        str.append(ch);
        this.index++;
        ch = this.source.charAt(this.index);
        // ECMA-262 7.8.5
        if (Utils.isLineTerminator(ch)) {
          throw this.createError(ErrorMessages.UNTERMINATED_REG_EXP);
        }
        str.append(ch);
        this.index++;
      } else if (Utils.isLineTerminator(ch)) {
        throw this.createError(ErrorMessages.UNTERMINATED_REG_EXP);
      } else {
        if (classMarker) {
          if (ch == ']') {
            classMarker = false;
          }
        } else {
          if (ch == '/') {
            terminated = true;
            str.append(ch);
            this.index++;
            break;
          } else if (ch == '[') {
            classMarker = true;
          }
        }
        str.append(ch);
        this.index++;
      }
    }

    if (!terminated) {
      throw this.createError(ErrorMessages.UNTERMINATED_REG_EXP);
    }

    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      if (!Utils.isIdentifierPart(ch) && ch != '\\') {
        break;
      }
      this.index++;
      str.append(ch);
    }
    return new RegularExpressionLiteralToken(this.getSlice(start), str.toString());
  }

  private boolean guessRegexp() {
    // Using the following algorithm:
    // https://github.com/mozilla/sweet.js/wiki/design

    if (this.prevToken == null) {
      // Nothing before that: it cannot be a division.
      return true;
    }

    if (this.prevToken.type.klass == TokenClass.Keyword) {
      return true;
    }

    if (this.prevToken.type.klass == TokenClass.Punctuator) {
      if (this.prevToken.type == TokenType.RBRACK) {
        return false;
      }
      if (this.prevToken.type == TokenType.RPAREN) {
        if (this.parenCheckToken != null) {
          switch (this.parenCheckToken) {
          case IF:
          case WHILE:
          case FOR:
          case WITH:
            return true;
          default:
            break;
          }
        }
        return false;
      }
      if (this.prevToken.type == TokenType.RBRACE) {
        if (this.curlyCheckToken == null) {
          return true;
        }

        TokenType checkToken = this.curlyCheckToken;
        // Dividing a function by anything makes little sense,
        // but we have to check for that.
        if (checkToken == TokenType.RPAREN) {
          // Anonymous function.
          if (this.parenCheckToken == TokenType.FUNCTION || this.parenCheckToken == TokenType.IDENTIFIER) {
            checkToken = this.funcCheckToken;
          }
        }

        if (checkToken == null) {
          return true;
        }

        // checkToken determines whether the function is
        // a declaration or an expression.
        switch (checkToken) {
        case LPAREN:
        case LBRACE:
        case LBRACK:
        case IN:
        case TYPEOF:
        case INSTANCEOF:
        case NEW:
        case RETURN:
        case CASE:
        case DELETE:
        case THROW:
        case VOID:

          // assignment operators
        case ASSIGN:
        case ASSIGN_ADD:
        case ASSIGN_SUB:
        case ASSIGN_MUL:
        case ASSIGN_DIV:
        case ASSIGN_MOD:
        case ASSIGN_SHL:
        case ASSIGN_SHR:
        case ASSIGN_SHR_UNSIGNED:
        case ASSIGN_BIT_AND:
        case ASSIGN_BIT_OR:
        case ASSIGN_BIT_XOR:
        case COMMA:

          // binary/unary operators
        case ADD:
        case SUB:
        case MUL:
        case DIV:
        case MOD:
        case INC:
        case DEC:
        case SHL:
        case SHR:
        case SHR_UNSIGNED:
        case BIT_AND:
        case BIT_OR:
        case BIT_XOR:
        case NOT:
        case BIT_NOT:
        case AND:
        case OR:
        case CONDITIONAL:
        case COLON:
        case EQ_STRICT:
        case EQ:
        case GTE:
        case LTE:
        case LT:
        case GT:
        case NE:
        case NE_STRICT:
          return false;
        default:
          // It is a declaration.
          return true;
        }
      }
      return true;
    }
    return false;
  }

  @NotNull
  private Token advance() throws JsError {
    char ch = this.source.charAt(this.index);

    if (ch < 0x80) {
      if (PUNCTUATOR_START[ch]) {
        return this.scanPunctuator();
      }

      if (IDENTIFIER_START[ch]) {
        return this.scanIdentifier();
      }

      // Dot (.) U+002E can also start a floating-point number, hence the need
      // to check the next character.
      if (ch == '.') {
        if (this.index + 1 < this.source.length() && Utils.isDecimalDigit(this.source.charAt(this.index + 1))) {
          return this.scanNumericLiteral();
        }
        return this.scanPunctuator();
      }

      // String literal starts with single quote (U+0027) or double quote (U+0022).
      if (ch == '\'' || ch == '"') {
        return this.scanStringLiteral();
      }

      if (Utils.isDecimalDigit(ch)) {
        return this.scanNumericLiteral();
      }

      // Slash (/) U+002F can also start a regex.
      if (ch == '/') {
        if (!this.directed) {
          int index = this.index;
          if (this.guessRegexp()) {
            try {
              return this.scanRegExp();
            } catch (JsError ignored) {
              this.index = index;
            }
          }
        }
        return this.scanPunctuator();
      }
      throw this.createILLEGAL();
    } else {
      if (Utils.isIdentifierStart(ch)) {
        return this.scanIdentifier();
      }

      throw this.createILLEGAL();
    }
  }

  boolean eof() {
    return this.lookahead.type == TokenType.EOS;
  }

  @NotNull
  private Token collectToken() throws JsError {
    this.collectingToken = true;
    int start = this.index;

    this.lastIndex = this.index;
    this.lastLine = this.line;
    this.lastLineStart = this.lineStart;

    this.skipComment();

    this.startIndex = this.index;
    this.startLine = this.line;
    this.startLineStart = this.lineStart;

    this.lastWhitespace = this.getSlice(start);
    if (this.index >= this.source.length()) {
      return new EOFToken(this.getSlice(start));
    }

    Token token = this.advance();


    token.leadingWhitespace = this.lastWhitespace;
    this.collectingToken = false;
    return token;
  }

  @NotNull
  Token lex() throws JsError {
    if (this.prevToken != null && this.prevToken.type == TokenType.EOS) {
      return this.prevToken;
    }

    Token prevToken2 = this.prevToken;
    this.prevToken = this.lookahead;
    this.hasLineTerminatorBeforeNext = false;
    this.lookahead = this.collectToken();

    if (prevToken2 != null) {
      if (this.prevToken.type == TokenType.LPAREN) {
        this.parenCheckToken = prevToken2.type;
      } else if (this.prevToken.type == TokenType.FUNCTION) {
        this.funcCheckToken = prevToken2.type;
      } else if (this.prevToken.type == TokenType.LBRACE) {
        this.curlyCheckToken = prevToken2.type;
      }
    }
    return this.prevToken;
  }
}
