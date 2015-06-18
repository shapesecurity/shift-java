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

import com.shapesecurity.functional.Pair;
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
import java.util.function.Function;
import java.util.regex.Pattern;

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
          false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
          false, false, false, false, true, false, false, false, true, true, false, true, true, true, true, true, true,
          false, true, false, false, false, false, false, false, false, false, false, false, true, true, true, true,
          true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
          false, false, false, false, false, false, false, false, false, false, false, false, false, true, false, true,
          true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
          false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true,
          true, false};
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
  private static final boolean[] IDENTIFIER_PART =
      new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false,
          false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
          false, false, false, false, false, false, false, true, false, false, false, false, false, false, false, false,
          false, false, false, true, true, true, true, true, true, true, true, true, true, false, false, false, false,
          false, false, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
          true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, true, false,
          true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
          true, true, true, true, true, true, true, true, false, false, false, false, false};

  @NotNull
  final String source;
  @NotNull
  protected Token lookahead;
  protected boolean hasLineTerminatorBeforeNext;
  protected boolean strict;
  private int index, line, lineStart;
  private int lastIndex;
  private int startIndex, startLine, startLineStart;

  private SourceLocation cachedSourceLocation;
  private int lastCachedSourceLocation = -1;

  public Tokenizer(@NotNull String source) throws JsError {
    this.source = source;
    this.lookahead = this.collectToken();
    this.hasLineTerminatorBeforeNext = false;
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
      return TokenType.IDENTIFIER;
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
              return TokenType.CLASS;
            }
            break;
          case 't': // THROW
            if (cse4(id, 'h', 'r', 'o', 'w')) {
              return TokenType.THROW;
            }
            break;
          case 'y': // YIELD
            if (cse4(id, 'i', 'e', 'l', 'd')) {
              return TokenType.YIELD;
            }
            break;
          case 's': // SUPER
            if (cse4(id, 'u', 'p', 'e', 'r')) {
              return TokenType.SUPER;
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
              return TokenType.EXPORT;
            }
            break;
          case 'i':
            if (cse5(id, 'm', 'p', 'o', 'r', 't')) {
              return TokenType.IMPORT;
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
              return TokenType.EXTENDS;
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
    return TokenType.IDENTIFIER;
  }

  @NotNull
  private JsError createILLEGAL() {
    this.startIndex = this.index;
    this.startLine = this.line;
    this.startLineStart = this.lineStart;
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
    return new JsError(this.startIndex, this.startLine + 1, this.startIndex - this.startLineStart, msg);
  }

  JsError createErrorWithLocation(@NotNull SourceLocation location, @NotNull String message, @NotNull Object... args) {
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
    boolean isLineStart = false;
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
            isLineStart = true;
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
        isLineStart = true;
        this.hasLineTerminatorBeforeNext = true;
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

//  private RegularExpressionLiteralToken scanRegExp() {
//
//  }

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
  private CharSequence getIdentifier() throws JsError {
    int start = this.index;
    int l = this.source.length();
    int i = this.index;
    Function<Integer, Boolean> check = Utils::isIdentifierStart;
    while (i < l) {
      char ch = this.source.charAt(i);
      int code = (int) ch;
      if (ch == '\\' || 0xD800 <= code && code <= 0xDBFF) {
        // Go back and try the hard one.
        this.index = start;
        return this.getEscapedIdentifier();
      }
      if (!check.apply(code)) {
        this.index = i;
        return this.source.subSequence(start, i);
      }
      ++i;
      check = Utils::isIdentifierPart;
    }
    this.index = i;
    return this.source.subSequence(start, i);
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
      return new IdentifierToken(slice, id);
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

    return new IdentifierToken(slice, id);
  }

  @NotNull
  private TokenType scanPunctuatorHelper() {
    char ch1 = this.source.charAt(this.index);

    switch (ch1) {
      // Check for most common single-character punctuators.
      case '.':
        char ch2 = this.source.charAt(this.index + 1);
        if (ch2 != '.') return TokenType.PERIOD;
        char ch3 = this.source.charAt(this.index + 2);
        if (ch3 != '.') return TokenType.PERIOD;
        return TokenType.ELLIPSIS;
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
  public Token advance() throws JsError {
    char ch = this.source.charAt(this.index);

    if (ch < 0x80) {
      if (PUNCTUATOR_START[ch]) {
        return this.scanPunctuator();
      }

      if (IDENTIFIER_START[ch] || ch == '\\') {
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

      throw this.createILLEGAL();
    } else {
      if (Utils.isIdentifierStart(ch) || 0xD800 <= ch && ch <= 0xDBFF) {
        return this.scanIdentifier();
      }

      throw this.createILLEGAL();
    }
  }

//  @NotNull
//  private Token scanTemplateElement() {
//    SourceLocation startLocation = this.getLocation();
//    int start = this.index;
//    while (this.index < this.source.length()) {
//      char ch = this.source.charAt(this.index);
//      switch (ch) {
//        case 0x60:  // `
//          this.index++;
//          return new ;
//          return { type: TokenType.TEMPLATE, tail: true, slice: this.getSlice(start, startLocation) };
//        case 0x24:  // $
//          if (this.source.charAt(this.index + 1) === 0x7B) {  // {
//            this.index += 2;
//            return { type: TokenType.TEMPLATE, tail: false, slice: this.getSlice(start, startLocation) };
//          }
//          this.index++;
//          break;
//        case 0x5C:  // \\
//        {
//          let octal = this.scanStringEscape("", false)[1];
//          if (octal) {
//            throw this.createILLEGAL();
//          }
//          break;
//        }
//        default:
//          this.index++;
//      }
//    }
//  }

  @NotNull
  private Token scanStringLiteral() throws JsError {
    String str = "";
    char quote = this.source.charAt(this.index);
    SourceLocation startLocation = this.getLocation();
    int start = this.index;
    this.index++;

    boolean octal = false;
    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      if (ch == quote) {
        this.index++;
        return new StringLiteralToken(this.getSlice(start), str, octal);
      } else if (ch == '\\') {
        Pair<String, Boolean> info = this.scanStringEscape(str, octal);
        str = info.a;
        octal = info.b;
      } else if (Utils.isLineTerminator(ch)) {
        throw this.createILLEGAL();
      } else {
        str += ch;
        this.index++;
      }
    }

    throw this.createILLEGAL();
  }

  private Pair<String, Boolean> scanStringEscape(String str, boolean octal) throws JsError {
    this.index++;
    if (this.index == this.source.length()) {
      throw this.createILLEGAL();
    }
    char ch = this.source.charAt(this.index);
    if (!Utils.isLineTerminator(ch)) {
      switch (ch) {
        case 'n':
          str += "\n";
          this.index++;
          break;
        case 'r':
          str += "\r";
          this.index++;
          break;
        case 't':
          str += "\t";
          this.index++;
          break;
        case 'u':
        case 'x':
          int unescaped;
          this.index++;
          if (this.index >= this.source.length()) {
            throw this.createILLEGAL();
          }
          unescaped = ch == 'u' ? this.scanUnicode() : this.scanHexEscape2();
          if (unescaped < 0) {
            throw this.createILLEGAL();
          }
          str += fromCodePoint(unescaped);
          break;
        case 'b':
          str += "\b";
          this.index++;
          break;
        case 'f':
          str += "\f";
          this.index++;
          break;
        case 'v':
          str += "\u000B";
          this.index++;
          break;
        default:
          if ('0' <= ch && ch <= '7') {
            int octLen = 1;
            // 3 digits are only allowed when string starts
            // with 0, 1, 2, 3
            if ('0' <= ch && ch <= '3') {
              octLen = 0;
            }
            int code = 0;
            while (octLen < 3 && '0' <= ch && ch <= '7') {
              if (octLen > 0 || ch != '0') {
                octal = true;
              }
              code *= 8;
              octLen++;
              code += ch - '0';
              this.index++;
              if (this.index == this.source.length()) {
                throw this.createILLEGAL();
              }
              ch = this.source.charAt(this.index);
            }
            str += fromCodePoint(code);
          } else if (ch == '8' || ch == '9') {
            throw this.createILLEGAL();
          } else {
            str += ch;
            this.index++;
          }
      }
    } else {
      this.index++;
      if (ch == '\r' && this.source.charAt(this.index) == '\n') {
        this.index++;
      }
      this.lineStart = this.index;
      this.line++;
    }
    return new Pair<String, Boolean>(str, octal);
  }

  @NotNull
  private Token scanNumericLiteral() throws JsError {
    char ch = this.source.charAt(this.index);
    SourceLocation startLocation = this.getLocation();
    int start = this.index;

    if (ch == '0') {
      this.index++;
      if (this.index < this.source.length()) {
        ch = this.source.charAt(this.index);
        if (ch == 'x' || ch == 'X') {
          this.index++;
          return this.scanHexLiteral(start);
        } else if (ch == 'b' || ch == 'B') {
//          return this.scan TODO: scan binary literal
        } else if (ch == 'o' || ch == 'O') {
          return this.scanOctalLiteral(start);
        } else if ('0' <= ch && ch <= '9') {
          // TODO: scan legacy octal literal
        }
      } else {
        SourceRange slice = this.getSlice(start);
        return new NumericLiteralToken(slice, Double.parseDouble(slice.toString()));
      }
    } else if (ch != '.') {
      ch = this.source.charAt(this.index);
      while ('0' <= ch && ch <= '9') {
        this.index++;
        if (this.index == this.source.length()) {
          SourceRange slice = this.getSlice(start);
          return new NumericLiteralToken(slice, Double.parseDouble(slice.toString()));
        }
        ch = this.source.charAt(this.index);
      }
    }

    if (ch == '.') {
      this.index++;
      if (this.index == this.source.length()) {
        SourceRange slice = this.getSlice(start);
        return new NumericLiteralToken(slice, Double.parseDouble(slice.toString()));
      }
    }

    int e = 0;
    ch = this.source.charAt(this.index);
    while ('0' <= ch && ch <= '9') {
      e++;
      this.index++;
      if (this.index == this.source.length()) {
        SourceRange slice = this.getSlice(start);
        return new NumericLiteralToken(slice, Double.parseDouble(slice.toString()));
      }
      ch = this.source.charAt(this.index);
    }

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
          f += +ch;
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

    SourceRange slice = this.getSlice(start);
    return new NumericLiteralToken(slice, Double.parseDouble(slice.toString()));
  }


  protected boolean eof() {
    return this.lookahead.type == TokenType.EOS;
  }

  @NotNull
  private Token collectToken() throws JsError {
    this.hasLineTerminatorBeforeNext = false;
    int start = this.index;

    this.lastIndex = this.index;

    this.skipComment();

    this.startIndex = this.index;
    this.startLine = this.line;
    this.startLineStart = this.lineStart;

    SourceRange lastWhitespace = this.getSlice(start);
    if (this.index >= this.source.length()) {
      return new EOFToken(this.getSlice(start));
    }

    Token token = this.advance();


    token.leadingWhitespace = lastWhitespace;
    return token;
  }

  @NotNull
  protected Token lex() throws JsError {
    if (this.lookahead.type == TokenType.EOS) {
      return this.lookahead;
    }

    Token prevToken = this.lookahead;

    this.lookahead = this.collectToken();

    return prevToken;
  }

  private static String fromCodePoint(int cp) {
    if (cp <= 0xFFFF) {
      return Character.toString((char) cp);
    }
    return String.valueOf(Character.toChars(cp));
  }

  private static int decodeUtf16(int lead, int trail) {
    return (lead - 0xD800) * 0x400 + (trail - 0xDC00) + 0x10000;
  }
  
  @NotNull
  public CharSequence getEscapedIdentifier() throws JsError {
    StringBuilder id = new StringBuilder();
    Function<Integer, Boolean> check = Utils::isIdentifierStart;

    while (this.index < this.source.length()) {
      char ch = this.source.charAt(this.index);
      int code = (int) ch;
      String s = "";
      int start = this.index;
      ++this.index;
      if (ch == '\\') {
        if (this.index >= this.source.length()) {
          throw this.createILLEGAL();
        }
        if (this.source.charAt(this.index) != 'u') {
          throw this.createILLEGAL();
        }
        ++this.index;
        code = this.scanUnicode();
        s = fromCodePoint(code);
      } else if (0xD800 <= code && code <= 0xDBFF) {
        if (this.index >= this.source.length()) {
          throw this.createILLEGAL();
        }
        int lowSurrogateCode = (int) this.source.charAt(this.index);
        ++this.index;
        if (!(0xDC00 <= lowSurrogateCode && lowSurrogateCode <= 0xDFFF)) {
          throw this.createILLEGAL();
        }
        code = decodeUtf16(code, lowSurrogateCode);
        s = fromCodePoint(code);
      } else {
        s = "" + ch;
      }
      if (!check.apply(code)) {
        if (id.length() < 1) {
          throw this.createILLEGAL();
        }
        this.index = start;
        return id;
      }
      check = Utils::isIdentifierPart;
      id.append(s);
    }
    return id;
  }

  private int scanUnicode() throws JsError {
    if (this.source.charAt(this.index) == '{') {
      // \ u { HexDigits }
      int i = this.index + 1;
      int hexDigits = 0;
      char ch = '\0';
      while (i < this.source.length()) {
        ch = this.source.charAt(i);
        int hex;
        try {
          hex = Integer.valueOf(Character.toString(ch), 16);
        } catch (NumberFormatException e) {
          break;
        }
        hexDigits = (hexDigits << 4) | hex;
        if (hexDigits > 0x10FFFF) {
          throw this.createILLEGAL();
        }
        i++;
      }
      if (ch != '}') {
        throw this.createILLEGAL();
      }
      this.index = i + 1;
      return hexDigits;
    } else {
      // \ u Hex4Digits
      if (this.index + 4 > this.source.length()) {
        return -1;
      }
      try {
        int x = Integer.valueOf(new String(new char[]{
            this.source.charAt(this.index),
            this.source.charAt(this.index + 1),
            this.source.charAt(this.index + 2),
            this.source.charAt(this.index + 3)
        }), 16);
        this.index += 4;
        return x;
      } catch (NumberFormatException e) {
        throw this.createILLEGAL();
      }
    }
  }

  public TokenizerState saveTokenizerState() {
    return new TokenizerState(
        this.index,
        this.line,
        this.lineStart,
        this.startIndex,
        this.startLine,
        this.startLineStart,
        this.lastIndex,
        this.lookahead,
        this.hasLineTerminatorBeforeNext
    );
  }

  public void restoreTokenizerState(TokenizerState s) {
    this.index = s.index;
    this.line = s.line;
    this.lineStart = s.lineStart;
    this.startIndex = s.startIndex;
    this.startLine = s.startLine;
    this.startLineStart = s.startLineStart;
    this.lastIndex = s.lastIndex;
    this.lookahead = s.lookahead;
    this.hasLineTerminatorBeforeNext = s.hasLineTerminatorBeforeNext;
  }
}
