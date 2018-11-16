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

package com.shapesecurity.shift.es2017.parser;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.shift.es2017.parser.token.EOFToken;
import com.shapesecurity.shift.es2017.parser.token.IdentifierToken;
import com.shapesecurity.shift.es2017.parser.token.KeywordToken;
import com.shapesecurity.shift.es2017.parser.token.NumericLiteralToken;
import com.shapesecurity.shift.es2017.parser.token.PunctuatorToken;
import com.shapesecurity.shift.es2017.parser.token.RegularExpressionLiteralToken;
import com.shapesecurity.shift.es2017.parser.token.StringLiteralToken;
import com.shapesecurity.shift.es2017.parser.token.TemplateToken;
import com.shapesecurity.shift.es2017.utils.Utils;

import javax.annotation.Nonnull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.function.Function;

public class Tokenizer {
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

    @Nonnull
    final String source;
    @Nonnull
    public Token lookahead;
    protected boolean hasLineTerminatorBeforeNext;
    protected boolean strict;
    protected final boolean moduleIsTheGoalSymbol;
    protected int index, line, lineStart;
    protected int lastLine, lastLineStart, lastIndex;
    protected int startIndex, startLine, startLineStart;
    private SourceLocation cachedSourceLocation;
    private SourceLocation cachedSourceEndLocation;
    private int lastCachedSourceLocation = -1;
    private int lastCachedSourceEndLocation = -1;

    public Tokenizer(@Nonnull String source, boolean isModule) throws JsError {
        this.moduleIsTheGoalSymbol = isModule;
        this.source = source;
        this.lookahead = this.collectToken();
        this.hasLineTerminatorBeforeNext = false;
    }

    private static boolean cse2(@Nonnull CharSequence id, char ch1, char ch2) {
        return id.charAt(1) == ch1 && id.charAt(2) == ch2;
    }

    private static boolean cse3(@Nonnull CharSequence id, char ch1, char ch2, char ch3) {
        return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3;
    }

    private static boolean cse4(@Nonnull CharSequence id, char ch1, char ch2, char ch3, char ch4) {
        return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4;
    }

    private static boolean cse5(@Nonnull CharSequence id, char ch1, char ch2, char ch3, char ch4, char ch5) {
        return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4 && id.charAt(5)
                == ch5;
    }

    private static boolean cse6(@Nonnull CharSequence id, char ch1, char ch2, char ch3, char ch4, char ch5, char ch6) {
        return id.charAt(1) == ch1 && id.charAt(2) == ch2 && id.charAt(3) == ch3 && id.charAt(4) == ch4 && id.charAt(5)
                == ch5
                && id.charAt(6) == ch6;
    }

    private static boolean cse7(
            @Nonnull CharSequence id,
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
    @Nonnull
    private TokenType getKeyword(@Nonnull CharSequence id) {
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
                        } else if (cse3(id, 'r', 'u', 'e')) {
                            return TokenType.TRUE_LITERAL;
                        }
                        break;
                    case 'n':
                        if (cse3(id, 'u', 'l', 'l')) {
                            return TokenType.NULL_LITERAL;
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
                    case 'a':
                        if (cse4(id, 'w', 'a', 'i', 't')) {
                            return TokenType.AWAIT;
                        }
                        if (cse4(id, 's', 'y', 'n', 'c')) {
                            return TokenType.ASYNC;
                        }
                        break;
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
                    case 'f':
                        if (cse4(id, 'a', 'l', 's', 'e')) {
                            return TokenType.FALSE_LITERAL;
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

    @Nonnull
    protected JsError createILLEGAL() {
        this.startIndex = this.index;
        this.startLine = this.line;
        this.startLineStart = this.lineStart;
        return this.index < this.source.length()
                ? this.createError(ErrorMessages.UNEXPECTED_ILLEGAL_TOKEN, Utils.escapeStringLiteral(Character.toString(this.source.charAt(this.index)), '\"', true))
                : this.createError(ErrorMessages.UNEXPECTED_EOS);
    }

    @Nonnull
    protected JsError createUnexpected(@Nonnull Token token) {
        switch (token.type.klass) {
            case Eof:
                return this.createError(ErrorMessages.UNEXPECTED_EOS);
            case Ident:
                return this.createError(ErrorMessages.UNEXPECTED_IDENTIFIER);
            case Keyword:
                if ((token.type == TokenType.FUTURE_RESERVED_WORD)) {
                    return this.createError(ErrorMessages.UNEXPECTED_RESERVED_WORD);
                }
                if ((token.type == TokenType.FUTURE_STRICT_RESERVED_WORD)) {
                    return this.createError(ErrorMessages.STRICT_RESERVED_WORD);
                }
                return this.createError(ErrorMessages.UNEXPECTED_TOKEN, token.slice.getString());
            case NumericLiteral:
                return this.createError(ErrorMessages.UNEXPECTED_NUMBER);
            case TemplateElement:
                return this.createError(ErrorMessages.UNEXPECTED_TEMPLATE);
            case Punctuator:
                return this.createError(ErrorMessages.UNEXPECTED_TOKEN, token.type.toString());
            case StringLiteral:
                return this.createError(ErrorMessages.UNEXPECTED_STRING);
            default:
                break;
        }
        return this.createError(ErrorMessages.UNEXPECTED_TOKEN, token.getValueString());
    }

    @Nonnull
    protected JsError createError(@Nonnull String message, @Nonnull Object... args) {
        ArrayList<String> escapedArgs = new ArrayList<>();
        for (Object arg : args) {
            escapedArgs.add(arg.toString());
        }
        String msg = String.format(message, escapedArgs.toArray());
        return new JsError(this.startIndex, this.startLine + 1, this.startIndex - this.startLineStart, msg);
    }

    @Nonnull
    protected JsError createErrorWithLocation(@Nonnull SourceLocation location, @Nonnull String message, @Nonnull Object... args) {
        String msg = String.format(message, args);
        return new JsError(location.offset, location.line, location.column, msg);
    }

    @Nonnull
    SourceLocation getLocation() {
        if (this.lastCachedSourceLocation != this.index) {
            this.cachedSourceLocation = new SourceLocation(this.startLine + 1, this.startIndex - this.startLineStart, this.startIndex);
            this.lastCachedSourceLocation = this.index;
        }
        return this.cachedSourceLocation;
    }

    @Nonnull
    SourceLocation getLastTokenEndLocation() {
        if (this.lastCachedSourceEndLocation != this.lastIndex) {
            this.cachedSourceEndLocation = new SourceLocation(this.lastLine + 1, this.lastIndex - this.lastLineStart, this.lastIndex);
            this.lastCachedSourceEndLocation = this.lastIndex;
        }
        return this.cachedSourceEndLocation;
    }

    @Nonnull
    private SourceRange getSlice(int start) {
        return new SourceRange(start, this.index, this.source);
    }

    protected void skipSingleLineComment(int offset) {
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

    protected boolean skipMultiLineComment() throws JsError {
        // returns true iff this contains a linebreak
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
                            return isLineStart;
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
                        isLineStart = true;
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
                    boolean isMultilineWithTerminator = this.skipMultiLineComment();
                    isLineStart = isMultilineWithTerminator || isLineStart;
                } else {
                    break;
                }
            } else if (!this.moduleIsTheGoalSymbol && isLineStart && ch == '-') {
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
            } else if (
                    ch == '<' && !this.moduleIsTheGoalSymbol && this.index + 4 <= length &&
                            this.source.charAt(this.index + 1) == '!' &&
                            this.source.charAt(this.index + 2) == '-' &&
                            this.source.charAt(this.index + 3) == '-'
                    ) {
                this.skipSingleLineComment(4);
            } else {
                break;
            }
        }
    }

    @Nonnull
    protected RegularExpressionLiteralToken scanRegExp(String str) throws JsError {
        int start = this.index;
        boolean terminated = false;
        boolean classMarker = false;
        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            if (ch == '\\') {
                str += ch;
                this.index++;
                ch = this.source.charAt(this.index);
                if (Utils.isLineTerminator(ch)) {
                    throw this.createError(ErrorMessages.UNTERMINATED_REGEXP);
                }
                str += ch;
                this.index++;
            } else if (Utils.isLineTerminator(ch)) {
                throw this.createError(ErrorMessages.UNTERMINATED_REGEXP);
            } else {
                if (classMarker) {
                    if (ch == ']') {
                        classMarker = false;
                    }
                } else {
                    if (ch == '/') {
                        terminated = true;
                        str += ch;
                        this.index++;
                        break;
                    } else if (ch == '[') {
                        classMarker = true;
                    }
                }
                str += ch;
                this.index++;
            }
        }

        if (!terminated) {
            throw this.createError(ErrorMessages.UNTERMINATED_REGEXP);
        }

        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            if (ch == '\\') {
                throw this.createError(ErrorMessages.INVALID_REGEXP_FLAGS);
            }
            if (!Utils.isIdentifierPart(ch)) {
                break;
            }
            this.index++;
            str += ch;
        }
        return new RegularExpressionLiteralToken(this.getSlice(start), str);
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

    @Nonnull
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

    @Nonnull
    private Token scanIdentifier() throws JsError {
        int start = this.index;

        // Backslash (U+005C) starts an escaped character.
        CharSequence id = this.source.charAt(this.index) == '\\' ? this.getEscapedIdentifier() : this.getIdentifier();

        SourceRange slice = this.getSlice(start);

        TokenType subType = this.getKeyword(id);
        if (subType == TokenType.IDENTIFIER) {
            return new IdentifierToken(slice, id);
        } else {
            return new KeywordToken(subType, slice, id);
        }
    }

    @Nonnull
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

                    if (ch1 == '*' && ch3 == '=') {
                        return TokenType.ASSIGN_EXP;
                    }

                    if (ch1 == '<' && ch3 == '=') {
                        return TokenType.ASSIGN_SHL;
                    }

                    if (ch1 == '>' && ch3 == '=') {
                        return TokenType.ASSIGN_SHR;
                    }
                }
                // Other 2-character punctuators: ++ -- << >> && || **
                switch (ch1) {
                    case '*':
                        return TokenType.EXP;
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
            } else if (ch1 == '=' && ch2 == '>') {
                return TokenType.ARROW;
            }
        }

        return ONE_CHAR_PUNCTUATOR[ch1];
    }

    // 7.7 Punctuators
    @Nonnull
    private Token scanPunctuator() {
        int start = this.index;
        TokenType subType = this.scanPunctuatorHelper();
        this.index += subType.toString().length();
        return new PunctuatorToken(subType, this.getSlice(start));
    }

    @Nonnull
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

    @Nonnull
    private Token scanOctalLiteral(int start) throws JsError {
        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            if ('0' <= ch && ch <= '7') {
                this.index++;
            } else if (Utils.isIdentifierPart(ch)) {
                throw this.createILLEGAL();
            } else {
                break;
            }
        }

        if (this.index - start == 2) {
            throw this.createILLEGAL();
        }

        return new NumericLiteralToken(this.getSlice(start), Integer.parseInt(this.getSlice(start).getString().toString().substring(2), 8));
    }

    @Nonnull
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

            if (ch == 0x60) {
                return this.scanTemplateElement();
            }

            throw this.createILLEGAL();
        } else {
            if (Utils.isIdentifierStart(ch) || 0xD800 <= ch && ch <= 0xDBFF) {
                return this.scanIdentifier();
            }

            throw this.createILLEGAL();
        }
    }

    @Nonnull
    protected Token scanTemplateElement() throws JsError {
        int start = this.index;
        this.index++;
        int length = this.source.length();
        while (this.index < length) {
            char ch = this.source.charAt(this.index);
            switch (ch) {
                case '`':
                    this.index++;
                    return new TemplateToken(this.getSlice(start), true);
                case '$':
                    if (this.source.charAt(this.index + 1) == 0x7B) {  // {
                        this.index += 2;
                        return new TemplateToken(this.getSlice(start), false);
                    }
                    this.index++;
                    break;
                case '\\':
                {
                    String octal = this.scanStringEscape(null).right();
                    if (octal != null) {
                        throw this.createILLEGAL();
                    }
                    break;
                }
                case '\r':
                {
                    this.line++;
                    this.index++;
                    if (this.index < length && this.source.charAt(this.index) == '\n') {
                        this.index++;
                    }
                    this.lineStart = this.index;
                    break;
                }
                case '\n':
                case '\u2028':
                case '\u2029':
                {
                    this.line++;
                    this.index++;
                    this.lineStart = this.index;
                    break;
                }
                default:
                    this.index++;
            }
        }
        throw this.createILLEGAL();
    }

    @Nonnull
    private Token scanStringLiteral() throws JsError {
        StringBuilder str = new StringBuilder();
        char quote = this.source.charAt(this.index);
        int start = this.index;
        this.index++;

        String octal = null;
        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            if (ch == quote) {
                index++;
                return new StringLiteralToken(this.getSlice(start), str.toString(), octal);
            } else if (ch == '\\') {
                Pair<String, String> info = this.scanStringEscape(octal);
                str.append(info.left());
                octal = info.right();
            } else if (Utils.isLineTerminator(ch)) {
                throw this.createILLEGAL();
            } else {
                str.append(ch);
                this.index++;
            }
        }

        throw this.createILLEGAL();
    }

    @Nonnull
    private Pair<String, String> scanStringEscape(String octal) throws JsError {
        String cooked;
        this.index++;
        if (this.index == this.source.length()) {
            throw this.createILLEGAL();
        }
        char ch = this.source.charAt(this.index);
        if (!Utils.isLineTerminator(ch)) {
            switch (ch) {
                case 'n':
                    cooked = "\n";
                    this.index++;
                    break;
                case 'r':
                    cooked = "\r";
                    this.index++;
                    break;
                case 't':
                    cooked = "\t";
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
                    cooked = fromCodePoint(unescaped);
                    break;
                case 'b':
                    cooked = "\b";
                    this.index++;
                    break;
                case 'f':
                    cooked = "\f";
                    this.index++;
                    break;
                case 'v':
                    cooked = "\u000B";
                    this.index++;
                    break;
                default:
                    if ('0' <= ch && ch <= '7') {
                        int octalStart = this.index;
                        int octLen = 1;
                        // 3 digits are only allowed when string starts
                        // with 0, 1, 2, 3
                        if ('0' <= ch && ch <= '3') {
                            octLen = 0;
                        }
                        int code = 0;
                        while (octLen < 3 && '0' <= ch && ch <= '7') {
                            this.index++;
                            if (octLen > 0 || ch != '0') {
                                octal = this.source.substring(octalStart, this.index);
                            }
                            code *= 8;
                            code += ch - '0';
                            octLen++;
                            if (this.index == this.source.length()) {
                                throw this.createILLEGAL();
                            }
                            ch = this.source.charAt(this.index);
                        }
                        cooked = fromCodePoint(code);
                    } else if (ch == '8' || ch == '9') {
                        throw this.createILLEGAL();
                    } else {
                        cooked = Character.toString(ch);
                        this.index++;
                    }
            }
        } else {
            cooked = "";
            this.index++;
            if (ch == '\r' && this.source.charAt(this.index) == '\n') {
                this.index++;
            }
            this.lineStart = this.index;
            this.line++;
        }
        return new Pair<>(cooked, octal);
    }

    @Nonnull
    private Token scanNumericLiteral() throws JsError {
        char ch = this.source.charAt(this.index);
        int start = this.index;

        if (ch == '0') {
            this.index++;
            if (this.index < this.source.length()) {
                ch = this.source.charAt(this.index);
                if (ch == 'x' || ch == 'X') {
                    this.index++;
                    return this.scanHexLiteral(start);
                } else if (ch == 'o' || ch == 'O') {
                    this.index++;
                    return this.scanOctalLiteral(start);
                } else if (ch == 'b' || ch == 'B') {
                    this.index++;
                    return this.scanBinaryLiteral(start);
                } else if ('0' <= ch && ch <= '9') {
                    return this.scanLegacyOctalLiteral(start);
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

        this.eatDecimalLiteralSuffix();

        if (this.index != this.source.length() && Utils.isIdentifierStart(this.source.charAt(index))) {
            throw this.createILLEGAL();
        }

        SourceRange slice = this.getSlice(start);
        return new NumericLiteralToken(slice, Double.parseDouble(slice.toString()));
    }

    @Nonnull
    private void eatDecimalLiteralSuffix() throws JsError {
        if (this.index == this.source.length()) {
            return;
        }
        char ch = this.source.charAt(this.index);
        if (ch == '.') {
            this.index++;
            if (this.index == this.source.length()) {
                return;
            }
        }

        ch = this.source.charAt(this.index);
        while ('0' <= ch && ch <= '9') {
            this.index++;
            if (this.index == this.source.length()) {
                return;
            }
            ch = this.source.charAt(this.index);
        }
        if (ch == 'e' || ch == 'E') {
            this.index++;
            if (this.index == this.source.length()) {
                throw this.createILLEGAL();
            }
            ch = this.source.charAt(this.index);
            if (ch == '+' || ch == '-') {
                this.index++;
                if (this.index == this.source.length()) {
                    throw this.createILLEGAL();
                }
                ch = this.source.charAt(this.index);
            }
            if ('0' <= ch && ch <= '9') {
                while ('0' <= ch && ch <= '9') {
                    this.index++;
                    if (this.index == this.source.length()) {
                        break;
                    }
                    ch = this.source.charAt(this.index);
                }
            } else {
                throw this.createILLEGAL();
            }
        }
    }

    private NumericLiteralToken scanLegacyOctalLiteral(int start) throws JsError {
        boolean isOctal = true;

        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            if ('0' <= ch && ch <= '7') {
                this.index++;
            } else if (ch == '8' || ch == '9') {
                isOctal = false;
                this.index++;
            } else if (Utils.isIdentifierPart(ch)) {
                throw this.createILLEGAL();
            } else {
                break;
            }
        }

        SourceRange slice = this.getSlice(start);
        if (!isOctal) {
            this.eatDecimalLiteralSuffix();
            return new NumericLiteralToken(slice, Integer.parseInt(slice.getString().toString()), true, true);
        }

        return new NumericLiteralToken(slice, Integer.parseInt(slice.getString().toString().substring(1), 8), true, false);
    }

    private NumericLiteralToken scanBinaryLiteral(int start) throws JsError {
        int offset = this.index - start;

        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            if (ch != '0' && ch != '1') {
                break;
            }
            this.index++;
        }

        if (this.index - start <= offset) {
            throw this.createILLEGAL();
        }

        if (this.index < this.source.length() && (Utils.isIdentifierStart(this.source.charAt(this.index))
                || Utils.isDecimalDigit(this.source.charAt(this.index)))) {
            throw this.createILLEGAL();
        }

        return new NumericLiteralToken(this.getSlice(start), Integer.parseInt(this.getSlice(start).getString().toString().substring(offset), 2));
    }

    protected boolean eof() {
        return this.lookahead.type == TokenType.EOS;
    }

    @Nonnull
    public Token collectToken() throws JsError {
        this.hasLineTerminatorBeforeNext = false;
        int start = this.index;

        this.lastIndex = this.index;
        this.lastLine = this.line;
        this.lastLineStart = this.lineStart;

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

    @Nonnull
    public Token lex() throws JsError {
        if (this.lookahead.type == TokenType.EOS) {
            return this.lookahead;
        }

        Token prevToken = this.lookahead;

        this.lookahead = this.collectToken();

        return prevToken;
    }

    @Nonnull
    public static String fromCodePoint(int cp) {
        if (cp <= 0xFFFF) {
            return Character.toString((char) cp);
        }
        return new String(new int[]{cp}, 0, 1);
    }

    private static int decodeUtf16(int lead, int trail) {
        return (lead - 0xD800) * 0x400 + (trail - 0xDC00) + 0x10000;
    }

    @Nonnull
    public CharSequence getEscapedIdentifier() throws JsError {
        StringBuilder id = new StringBuilder();
        Function<Integer, Boolean> check = Utils::isIdentifierStart;

        while (this.index < this.source.length()) {
            char ch = this.source.charAt(this.index);
            int code = (int) ch;
            String s;
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
        if (this.index == this.source.length()) {
            throw this.createILLEGAL();
        }
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
            if (i == this.index + 1) {
                ++this.index; // This is so that the error is 'Unexpected "}"' instead of 'Unexpected "{"'.
                throw this.createILLEGAL();
            }
            this.index = i + 1;
            return hexDigits;
        } else {
            // \ u Hex4Digits
            if (this.index + 4 > this.source.length()) {
                throw this.createILLEGAL();
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

    @Nonnull
    public TokenizerState saveTokenizerState() {
        return new TokenizerState(
                this.index,
                this.line,
                this.lineStart,
                this.startIndex,
                this.startLine,
                this.startLineStart,
                this.lastIndex,
                this.lastLine,
                this.lastLineStart,
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
        this.lastLine = s.lastLine;
        this.lastLineStart = s.lastLineStart;
        this.lookahead = s.lookahead;
        this.hasLineTerminatorBeforeNext = s.hasLineTerminatorBeforeNext;
    }
}
