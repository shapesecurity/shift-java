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

import javax.annotation.Nonnull;

public enum TokenType {
    /* end of source indicator. */
    EOS(TokenClass.Eof, "EOS"),
    LPAREN(TokenClass.Punctuator, "("),
    RPAREN(TokenClass.Punctuator, ")"),
    LBRACK(TokenClass.Punctuator, "["),
    RBRACK(TokenClass.Punctuator, "]"),
    LBRACE(TokenClass.Punctuator, "{"),
    RBRACE(TokenClass.Punctuator, "}"),
    COLON(TokenClass.Punctuator, ":"),
    SEMICOLON(TokenClass.Punctuator, ";"),
    PERIOD(TokenClass.Punctuator, "."),
    ELLIPSIS(TokenClass.Punctuator, "..."),
    CONDITIONAL(TokenClass.Punctuator, "?"),
    INC(TokenClass.Punctuator, "++"),
    DEC(TokenClass.Punctuator, "--"),
    ASSIGN(TokenClass.Punctuator, "="),
    ASSIGN_BIT_OR(TokenClass.Punctuator, "|="),
    ASSIGN_BIT_XOR(TokenClass.Punctuator, "^="),
    ASSIGN_BIT_AND(TokenClass.Punctuator, "&="),
    ASSIGN_SHL(TokenClass.Punctuator, "<<="),
    ASSIGN_SHR(TokenClass.Punctuator, ">>="),
    ASSIGN_SHR_UNSIGNED(TokenClass.Punctuator, ">>>="),
    ASSIGN_ADD(TokenClass.Punctuator, "+="),
    ASSIGN_SUB(TokenClass.Punctuator, "-="),
    ASSIGN_MUL(TokenClass.Punctuator, "*="),
    ASSIGN_DIV(TokenClass.Punctuator, "/="),
    ASSIGN_MOD(TokenClass.Punctuator, "%="),
    ASSIGN_EXP(TokenClass.Punctuator, "**="),
    COMMA(TokenClass.Punctuator, ","),
    OR(TokenClass.Punctuator, "||"),
    AND(TokenClass.Punctuator, "&&"),
    BIT_OR(TokenClass.Punctuator, "|"),
    BIT_XOR(TokenClass.Punctuator, "^"),
    BIT_AND(TokenClass.Punctuator, "&"),
    SHL(TokenClass.Punctuator, "<<"),
    SHR(TokenClass.Punctuator, ">>"),
    SHR_UNSIGNED(TokenClass.Punctuator, ">>>"),
    ADD(TokenClass.Punctuator, "+"),
    SUB(TokenClass.Punctuator, "-"),
    MUL(TokenClass.Punctuator, "*"),
    DIV(TokenClass.Punctuator, "/"),
    MOD(TokenClass.Punctuator, "%"),
    EXP(TokenClass.Punctuator, "**"),
    EQ(TokenClass.Punctuator, "=="),
    NE(TokenClass.Punctuator, "!="),
    EQ_STRICT(TokenClass.Punctuator, "==="),
    NE_STRICT(TokenClass.Punctuator, "!=="),
    LT(TokenClass.Punctuator, "<"),
    GT(TokenClass.Punctuator, ">"),
    LTE(TokenClass.Punctuator, "<="),
    GTE(TokenClass.Punctuator, ">="),
    ARROW(TokenClass.Punctuator, "=>"),
    INSTANCEOF(TokenClass.Keyword, "instanceof"),
    IN(TokenClass.Keyword, "in"),
    NOT(TokenClass.Punctuator, "!"),
    BIT_NOT(TokenClass.Punctuator, "~"),
    DELETE(TokenClass.Keyword, "delete"),
    TYPEOF(TokenClass.Keyword, "typeof"),
    VOID(TokenClass.Keyword, "void"),
    BREAK(TokenClass.Keyword, "break"),
    CASE(TokenClass.Keyword, "case"),
    CATCH(TokenClass.Keyword, "catch"),
    CONTINUE(TokenClass.Keyword, "continue"),
    DEBUGGER(TokenClass.Keyword, "debugger"),
    DEFAULT(TokenClass.Keyword, "default"),
    DO(TokenClass.Keyword, "do"),
    ELSE(TokenClass.Keyword, "else"),
    FINALLY(TokenClass.Keyword, "finally"),
    FOR(TokenClass.Keyword, "for"),
    FUNCTION(TokenClass.Keyword, "function"),
    IF(TokenClass.Keyword, "if"),
    NEW(TokenClass.Keyword, "new"),
    RETURN(TokenClass.Keyword, "return"),
    SWITCH(TokenClass.Keyword, "switch"),
    THIS(TokenClass.Keyword, "this"),
    THROW(TokenClass.Keyword, "throw"),
    TRY(TokenClass.Keyword, "try"),
    VAR(TokenClass.Keyword, "var"),
    WHILE(TokenClass.Keyword, "while"),
    WITH(TokenClass.Keyword, "with"),
    SUPER(TokenClass.Keyword, "super"),
    NULL_LITERAL(TokenClass.NullLiteral, "null"),
    TRUE_LITERAL(TokenClass.BooleanLiteral, "true"),
    FALSE_LITERAL(TokenClass.BooleanLiteral, "false"),
    NUMBER(TokenClass.NumericLiteral, "number"),
    STRING(TokenClass.StringLiteral, "string"),
    REGEXP(TokenClass.RegularExpression, "regexp"),
    IDENTIFIER(TokenClass.Ident, "identifier"),
    FUTURE_RESERVED_WORD(TokenClass.Keyword, "future-reserved-word"),
    FUTURE_STRICT_RESERVED_WORD(TokenClass.Keyword, "future-strict-reserved-word"),
    CONST(TokenClass.Keyword, "const"),
    LET(TokenClass.Keyword, "let"),
    YIELD(TokenClass.Keyword, "yield"),
    EXTENDS(TokenClass.Keyword, "extends"),
    CLASS(TokenClass.Keyword, "class"),
    IMPORT(TokenClass.Keyword, "import"),
    EXPORT(TokenClass.Keyword, "export"),
    AWAIT(TokenClass.Keyword, "await"),
    ASYNC(TokenClass.Keyword, "async"),
    ILLEGAL(TokenClass.Illegal, "ILLEGAL"),
    TEMPLATE(TokenClass.TemplateElement, "template");
    @Nonnull
    public final TokenClass klass;
    @Nonnull
    public final String name;

    private TokenType(@Nonnull TokenClass klass, @Nonnull String name) {
        this.klass = klass;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
