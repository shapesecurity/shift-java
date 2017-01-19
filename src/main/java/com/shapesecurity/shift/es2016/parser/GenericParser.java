/**
 * Copyright 2014 Shape Security, Inc. <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.shapesecurity.shift.es2016.parser;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2016.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es2016.ast.operators.Precedence;
import com.shapesecurity.shift.es2016.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2016.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es2016.parser.token.NumericLiteralToken;
import com.shapesecurity.shift.es2016.parser.token.RegularExpressionLiteralToken;
import com.shapesecurity.shift.es2016.parser.token.StringLiteralToken;
import com.shapesecurity.shift.es2016.parser.token.TemplateToken;
import com.shapesecurity.shift.es2016.utils.D2A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiFunction;

public abstract class GenericParser<AdditionalStateT> extends Tokenizer {

    protected boolean inFunctionBody = false;
    protected boolean module;
    protected boolean strict;
    protected boolean allowIn = true;
    protected boolean isBindingElement = false;
    protected boolean isAssignmentTarget = true;
    @Nullable
    protected JsError firstExprError = null;
    protected boolean allowYieldExpression = false;
    protected boolean inParameter = false;

    protected GenericParser(@NotNull String source, boolean isModule) throws JsError {
        super(source, isModule);
        this.module = this.strict = isModule;
    }

    boolean eat(@NotNull TokenType subType) throws JsError {
        if (this.lookahead.type != subType) {
            return false;
        }
        this.lex();
        return true;
    }

    @NotNull
    Token expect(@NotNull TokenType subType) throws JsError {
        if (this.lookahead.type != subType) {
            throw this.createUnexpected(this.lookahead);
        }
        return this.lex();
    }

    protected boolean match(@NotNull TokenType subType) {
        return this.lookahead.type == subType;
    }

    protected void consumeSemicolon() throws JsError {
        // Catch the very common case first: immediately a semicolon (U+003B).
        if (this.eat(TokenType.SEMICOLON)) {
            return;
        }
        if (this.hasLineTerminatorBeforeNext) {
            return;
        }
        if (!this.eof() && !this.match(TokenType.RBRACE)) {
            throw this.createUnexpected(this.lookahead);
        }
    }

    @NotNull
    protected abstract <T extends Node> T finishNode(@NotNull AdditionalStateT startState, @NotNull T node);

    @NotNull
    protected abstract AdditionalStateT startNode();

    @NotNull
    protected abstract <T extends Node> T copyNode(@NotNull Node src, @NotNull T dest);

    protected boolean lookaheadLexicalDeclaration() throws JsError {
        if (this.match(TokenType.LET) || this.match(TokenType.CONST)) {
            TokenizerState tokenizerState = this.saveTokenizerState();
            this.lex();
            if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.LET) || this.match(TokenType.LBRACE) || this.match(TokenType.LBRACK) || this.match(TokenType.YIELD)) {
                this.restoreTokenizerState(tokenizerState);
                return true;
            } else {
                this.restoreTokenizerState(tokenizerState);
            }
        }
        return false;
    }

    @NotNull
    protected ImportDeclarationExportDeclarationStatement parseModuleItem() throws JsError {
        switch (this.lookahead.type) {
            case IMPORT:
                return this.parseImportDeclaration();
            case EXPORT:
                return this.parseExportDeclaration();
            default:
                return this.parseStatementListItem();
        }
    }

    @FunctionalInterface
    protected interface ExceptionalSupplier<A> {
        A get() throws JsError;
    }

    @NotNull
    protected <A, B extends Node> B  parseTopLevel(@NotNull ExceptionalSupplier<A> parser, @NotNull BiFunction<ImmutableList<Directive>, ImmutableList<A>, B> constructor) throws JsError {
        B node = this.parseBody(parser, constructor);
        if (!this.match(TokenType.EOS)) {
            throw this.createUnexpected(this.lookahead);
        }
        return node;
    }

    @NotNull
    protected Script parseScript() throws JsError {
        return this.parseTopLevel(this::parseStatementListItem, Script::new);
    }

    @NotNull
    protected Module parseModule() throws JsError {
        return this.parseTopLevel(this::parseModuleItem, Module::new);
    }

    @NotNull
    protected <A, B extends Node> B parseBody(@NotNull ExceptionalSupplier<A> parser, @NotNull BiFunction<ImmutableList<Directive>, ImmutableList<A>, B> constructor) throws JsError {
        AdditionalStateT startState = this.startNode();
        ArrayList<Directive> directives = new ArrayList<>();
        ArrayList<A> statements = new ArrayList<>();
        boolean parsingDirectives = true;
        JsError directiveOctal = null;

        while (true) {
            if (this.eof() || this.match(TokenType.RBRACE)) {
                break;
            }

            Token token = this.lookahead;
            String text = token.slice.toString();
            boolean isStringLiteral = token.type == TokenType.STRING;
            AdditionalStateT directiveLocation = this.startNode();
            A stmt = parser.get();

            if (parsingDirectives) {
                if (isStringLiteral && stmt instanceof ExpressionStatement && ((ExpressionStatement) stmt).expression instanceof LiteralStringExpression) {
                    if (directiveOctal == null && ((StringLiteralToken) token).octal != null) {
                        directiveOctal = createErrorWithLocation(this.getLocation(), ErrorMessages.INVALID_STRICT_OCTAL + ((StringLiteralToken) token).octal);
                    }
                    String rawValue = text.substring(1, text.length() - 1);
                    if (rawValue.equals("use strict")) {
                        this.strict = true;
                    }
                    directives.add(this.finishNode(directiveLocation, new Directive(rawValue)));
                } else {
                    parsingDirectives = false;
                    if (directiveOctal != null && this.strict) {
                        throw directiveOctal;
                    }
                    statements.add(stmt);
                }
            } else {
                statements.add(stmt);
            }
        }
        if (directiveOctal != null && this.strict) {
            throw directiveOctal;
        }

        B node = constructor.apply(ImmutableList.from(directives), ImmutableList.from(statements));
        return this.finishNode(startState, node);
    }

    @NotNull
    protected FunctionBody parseFunctionBody() throws JsError {
        boolean oldInFunctionBody = this.inFunctionBody;
        boolean oldModule = this.module;
        boolean oldStrict = this.strict;
        this.inFunctionBody = true;
        this.module = false;

        this.expect(TokenType.LBRACE);
        FunctionBody body = this.parseBody(this::parseStatementListItem, FunctionBody::new);
        this.expect(TokenType.RBRACE);

        this.inFunctionBody = oldInFunctionBody;
        this.module = oldModule;
        this.strict = oldStrict;

        return body;
    }

    @NotNull
    protected Statement parseStatementListItem() throws JsError {
        if (this.eof()) {
            throw this.createUnexpected(this.lookahead);
        }
        switch (this.lookahead.type) {
            case FUNCTION:
                return this.parseFunctionDeclaration(false, true);
            case CLASS:
                return this.parseClass(false);
            default:
                if (this.lookaheadLexicalDeclaration()) {
                    AdditionalStateT startState = this.startNode();
                    return this.finishNode(startState, this.parseVariableDeclarationStatement());
                } else {
                    return this.parseStatement();
                }
        }
    }

    @NotNull
    protected Statement parseVariableDeclarationStatement() throws JsError {
        VariableDeclaration declaration = this.parseVariableDeclaration(true);
        this.consumeSemicolon();
        return new VariableDeclarationStatement(declaration);
    }

    @NotNull
    protected VariableDeclaration parseVariableDeclaration(boolean bindingPatternsMustHaveInit) throws JsError {
        AdditionalStateT startState = this.startNode();
        Token token = this.lex();
        VariableDeclarationKind kind = token.type == TokenType.VAR ? VariableDeclarationKind.Var :
                token.type == TokenType.CONST ? VariableDeclarationKind.Const : VariableDeclarationKind.Let;
        ImmutableList<VariableDeclarator> declarators = this.parseVariableDeclaratorList(bindingPatternsMustHaveInit);
        return this.finishNode(startState, new VariableDeclaration(kind, declarators));

    }

    @NotNull
    protected ImmutableList<VariableDeclarator> parseVariableDeclaratorList(boolean bindingPatternsMustHaveInit) throws JsError {
        ArrayList<VariableDeclarator> result = new ArrayList<>();
        do {
            result.add(this.parseVariableDeclarator(bindingPatternsMustHaveInit));
        } while (this.eat(TokenType.COMMA));
        return ImmutableList.from(result);
    }

    @NotNull
    protected VariableDeclarator parseVariableDeclarator(boolean bindingPatternsMustHaveInit) throws JsError {
        AdditionalStateT startState = this.startNode();
        if (this.match(TokenType.LPAREN)) {
            throw this.createUnexpected(this.lookahead);
        }
        Binding binding = this.parseBindingTarget();
        if (bindingPatternsMustHaveInit && !(binding instanceof BindingIdentifier) && !this.match(TokenType.ASSIGN)) {
            this.expect(TokenType.ASSIGN);
        }
        Maybe<Expression> init = Maybe.empty();
        if (this.eat(TokenType.ASSIGN)) {
            init = this.parseAssignmentExpression().left();
        }
        return this.finishNode(startState, new VariableDeclarator(binding, init));
    }

    @NotNull
    protected Binding parseBindingTarget() throws JsError {
        switch (this.lookahead.type) {
            case IDENTIFIER:
            case LET:
            case YIELD:
                return this.parseBindingIdentifier();
            case LBRACK:
                return this.parseArrayBinding();
            case LBRACE:
                return this.parseObjectBinding();
        }
        throw this.createUnexpected(this.lookahead);
    }

    @NotNull
    protected Binding parseObjectBinding() throws JsError {
        AdditionalStateT startState = this.startNode();

        this.expect(TokenType.LBRACE);

        ArrayList<BindingProperty> properties = new ArrayList<>();
        while (!this.match(TokenType.RBRACE)) {
            properties.add(this.parseBindingProperty());
            if (!this.match(TokenType.RBRACE)) {
                this.expect(TokenType.COMMA);
            }
        }

        this.expect(TokenType.RBRACE);

        return this.finishNode(startState, new ObjectBinding(ImmutableList.from(properties)));
    }

    @NotNull
    protected BindingProperty parseBindingProperty() throws JsError {
        AdditionalStateT startState = this.startNode();
        Token token = this.lookahead;

        Pair<PropertyName, Maybe<Binding>> fromParsePropertyName = this.parsePropertyName();
        PropertyName name = fromParsePropertyName.left();
        Maybe<Binding> binding = fromParsePropertyName.right();

        if ((token.type == TokenType.IDENTIFIER || token.type == TokenType.LET || token.type == TokenType.YIELD) && name instanceof StaticPropertyName) {
            if (!this.match(TokenType.COLON)) {
                Maybe<Expression> defaultValue = Maybe.empty();
                if (this.eat(TokenType.ASSIGN)) {
                    boolean previousAllowYieldExpression = this.allowYieldExpression;
                    Either<Expression, AssignmentTarget> expr = this.parseAssignmentExpression();
                    defaultValue = expr.left();
                    this.allowYieldExpression = previousAllowYieldExpression;
                } else if (token.type == TokenType.YIELD && this.allowYieldExpression) {
                    throw this.createUnexpected(token);
                }
                return this.finishNode(startState, new BindingPropertyIdentifier((BindingIdentifier) binding.fromJust(), defaultValue));
            }
        }
        this.expect(TokenType.COLON);
        BindingBindingWithDefault fromParseBindingElement = this.parseBindingElement();
        return this.finishNode(startState, new BindingPropertyProperty(name, fromParseBindingElement));
    }

    @NotNull
    protected Binding parseArrayBinding() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.expect(TokenType.LBRACK);
        ArrayList<Maybe<BindingBindingWithDefault>> elements = new ArrayList<>();
        Maybe<Binding> restElement = Maybe.empty();
        while (true) {
            if (this.match(TokenType.RBRACK)) {
                break;
            }
            Maybe<BindingBindingWithDefault> el;

            if (this.eat(TokenType.COMMA)) {
                el = Maybe.empty();
            } else {
                if (this.eat(TokenType.ELLIPSIS)) {
                    restElement = Maybe.of(this.parseBindingTarget());
                    break;
                } else {
                    el = Maybe.of(this.parseBindingElement());
                }
                if (!this.match(TokenType.RBRACK)) {
                    this.expect(TokenType.COMMA);
                }
            }
            elements.add(el);
        }
        this.expect(TokenType.RBRACK);
        return this.finishNode(startState, new ArrayBinding(ImmutableList.from(elements), restElement));
    }

    @NotNull
    protected BindingIdentifier parseBindingIdentifier() throws JsError {
        AdditionalStateT startState = this.startNode();
        return this.finishNode(startState, new BindingIdentifier(this.parseIdentifier()));
    }

    @NotNull
    protected String parseIdentifier() throws JsError {
        if (this.match(TokenType.IDENTIFIER) || !this.allowYieldExpression && this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
            return this.lex().toString();
        } else {
            throw this.createUnexpected(this.lookahead);
        }
    }

    @NotNull
    protected Expression parseArrowExpressionTail(ArrayList<BindingBindingWithDefault> params, Maybe<Binding> rest, AdditionalStateT startState) throws JsError {
        if (this.hasLineTerminatorBeforeNext) {
            throw this.createError(ErrorMessages.NEWLINE_AFTER_ARROW_PARAMS);
        }
        this.expect(TokenType.ARROW);
        this.isBindingElement = this.isAssignmentTarget = false;
        this.firstExprError = null;

        FormalParameters paramsNode = this.finishNode(startState, new FormalParameters(ImmutableList.from(params).map(this::bindingToParameter), rest));

        boolean previousYield = this.allowYieldExpression;
        this.allowYieldExpression = false;
        FunctionBodyExpression
            body = this.match(TokenType.LBRACE) ? this.parseFunctionBody() : this.parseAssignmentExpression().left().fromJust();
        this.allowYieldExpression = previousYield;
        return this.finishNode(startState, new ArrowExpression(paramsNode, body));
    }

    @NotNull
    protected Statement parseIfStatement() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();
        this.expect(TokenType.LPAREN);
        Expression test = this.parseExpression().left().fromJust();
        this.expect(TokenType.RPAREN);
        Statement consequent = this.parseIfStatementChild();
        Maybe<Statement> alternate;
        if (this.eat(TokenType.ELSE)) {
            alternate = Maybe.of(this.parseIfStatementChild());
        } else {
            alternate = Maybe.empty();
        }
        return this.finishNode(startState, new IfStatement(test, consequent, alternate));
    }

    @NotNull
    protected Statement parseIfStatementChild() throws JsError {
        return this.match(TokenType.FUNCTION) ? this.parseFunctionDeclaration(false, false) : this.parseStatement();
    }

    @NotNull
    protected Statement parseFunctionDeclaration(boolean inDefault, boolean allowGenerator) throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();

        boolean isGenerator = allowGenerator && this.eat(TokenType.MUL);
        boolean previousYield = this.allowYieldExpression;
        BindingIdentifier name;
        if (!this.match(TokenType.LPAREN)) {
            name = this.parseBindingIdentifier();
        } else if (inDefault) {
            name = this.finishNode(startState, new BindingIdentifier("*default*"));
        } else {
            throw this.createUnexpected(this.lookahead);
        }
        this.allowYieldExpression = isGenerator;
        FormalParameters params = this.parseParams();
        this.allowYieldExpression = isGenerator;
        FunctionBody body = this.parseFunctionBody();
        this.allowYieldExpression = previousYield;
        return this.finishNode(startState, new FunctionDeclaration(isGenerator, name, params, body));
    }

    @NotNull
    protected Expression parseFunctionExpression(boolean allowGenerator) throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();

        Maybe<BindingIdentifier> name = Maybe.empty();
        boolean isGenerator = allowGenerator && this.eat(TokenType.MUL);
        boolean previousYield = this.allowYieldExpression;
        this.allowYieldExpression = isGenerator;
        if (!this.match(TokenType.LPAREN)) {
            name = Maybe.of(this.parseBindingIdentifier());
        }
        FormalParameters params = this.parseParams();
        this.allowYieldExpression = isGenerator;
        FunctionBody body = this.parseFunctionBody();
        this.allowYieldExpression = previousYield;
        return this.finishNode(startState, new FunctionExpression(isGenerator, name, params, body));
    }

    @NotNull
    protected FormalParameters parseParams() throws JsError {
        AdditionalStateT paramsLocation = this.startNode();
        this.expect(TokenType.LPAREN);
        ArrayList<BindingBindingWithDefault> items = new ArrayList<>();
        BindingIdentifier rest = null;
        if (!this.match(TokenType.RPAREN)) {
            while (!this.eof()) {
                if (this.eat(TokenType.ELLIPSIS)) {
                    rest = parseBindingIdentifier();
                    break;
                }
                items.add(this.parseParam());
                if (this.match(TokenType.RPAREN)) {
                    break;
                }
                this.expect(TokenType.COMMA);
            }
        }
        this.expect(TokenType.RPAREN);
        return this.finishNode(paramsLocation, new FormalParameters(ImmutableList.from(items).map(this::bindingToParameter), Maybe.fromNullable(rest)));
    }

    @NotNull
    protected BindingBindingWithDefault parseParam() throws JsError {
        boolean previousInParameter = this.inParameter;
        this.inParameter = true;
        BindingBindingWithDefault param = this.parseBindingElement();
        this.inParameter = previousInParameter;
        return param;
    }

    @NotNull
    protected BindingBindingWithDefault parseBindingElement() throws JsError {
        AdditionalStateT startState = this.startNode();
        Binding binding = this.parseBindingTarget();
        BindingBindingWithDefault bbwd = binding;
        if (this.eat(TokenType.ASSIGN)) {
            boolean previousYieldExpression = this.allowYieldExpression;
            Either<Expression, AssignmentTarget> init = this.parseAssignmentExpression();
            bbwd = this.finishNode(startState, new BindingWithDefault(binding, init.left().fromJust()));
            this.allowYieldExpression = previousYieldExpression;
        }
        return bbwd;
    }

    public static boolean isValidSimpleAssignmentTarget(Node node) {
        return (node instanceof IdentifierExpression || node instanceof ComputedMemberExpression || node instanceof StaticMemberExpression);
    }

    @NotNull
    protected Statement parseStatement() throws JsError {
        AdditionalStateT startState = this.startNode();
        Statement stmt = this.isolateCoverGrammar(this::parseStatementHelper);
        return this.finishNode(startState, stmt);
    }

    @NotNull
    protected Statement parseStatementHelper() throws JsError {
        if (this.eof()) {
            throw this.createUnexpected(this.lookahead);
        }

        switch (this.lookahead.type) {
            case SEMICOLON:
                return this.parseEmptyStatement();
            case BREAK:
                return this.parseBreakStatement();
            case CONTINUE:
                return this.parseContinueStatement();
            case DEBUGGER:
                return this.parseDebuggerStatement();
            case DO:
                return this.parseDoWhileStatement();
            case LPAREN:
                return this.parseExpressionStatement();
            case LBRACE:
                return this.parseBlockStatement();
            case IF:
                return this.parseIfStatement();
            case FOR:
                return this.parseForStatement();
            case RETURN:
                return this.parseReturnStatement();
            case SWITCH:
                return this.parseSwitchStatement();
            case THROW:
                return this.parseThrowStatement();
            case WHILE:
                return this.parseWhileStatement();
            case WITH:
                return this.parseWithStatement();
            case TRY:
                return this.parseTryStatement();
            case VAR:
                return this.parseVariableDeclarationStatement();
            case FUNCTION:
            case CLASS:
                throw this.createUnexpected(this.lookahead);
            default: {
                if (this.lookaheadLexicalDeclaration()) {
                    throw this.createUnexpected(this.lookahead);
                }
                Expression expr = this.parseExpression().left().fromJust();
                if (expr instanceof IdentifierExpression && this.eat(TokenType.COLON)) {
                    Statement labeledBody = this.match(TokenType.FUNCTION) ? this.parseFunctionDeclaration(false, false) : this.parseStatement();
                    return new LabeledStatement(((IdentifierExpression) expr).name, labeledBody);
                } else {
                    this.consumeSemicolon();
                    return new ExpressionStatement(expr);
                }
            }
        }
    }

    @NotNull
    protected Statement parseForStatement() throws JsError {
        this.lex();
        this.expect(TokenType.LPAREN);
        Maybe<Expression> test = Maybe.empty();
        Maybe<Expression> right = Maybe.empty();
        if (this.eat(TokenType.SEMICOLON)) {
            if (!this.match(TokenType.SEMICOLON)) {
                test = this.parseExpression().left();
            }
            this.expect(TokenType.SEMICOLON);
            if (!this.match(TokenType.RPAREN)) {
                right = this.parseExpression().left();
            }
            return new ForStatement(Maybe.empty(), test, right, this.getIteratorStatementEpilogue());
        } else {
            boolean startsWithLet = this.match(TokenType.LET);
            boolean isForDecl = this.lookaheadLexicalDeclaration();
            AdditionalStateT leftLocation = this.startNode();
            if (this.match(TokenType.VAR) || isForDecl) {
                boolean previousAllowIn = this.allowIn;
                this.allowIn = false;
                VariableDeclarationExpression init = this.parseVariableDeclaration(false);
                this.allowIn = previousAllowIn;
                if (((VariableDeclaration) init).declarators.length == 1 && (this.match((TokenType.IN)) || this.matchContextualKeyword("of"))) {
                    if (this.match(TokenType.IN)) {
                        if (!(((VariableDeclaration) init).declarators.index(0).fromJust().init).equals(Maybe.empty())) {
                            throw this.createError(ErrorMessages.INVALID_VAR_INIT_FOR_IN);
                        }
                        this.lex();
                        right = this.parseExpression().left();
                        Statement body = this.getIteratorStatementEpilogue();
                        return new ForInStatement((VariableDeclarationAssignmentTarget) init, right.fromJust(), body);
                    } else {
                        if (!(((VariableDeclaration) init).declarators.index(0).fromJust().init).equals(Maybe.empty())) {
                            throw this.createError(ErrorMessages.INVALID_VAR_INIT_FOR_OF);
                        }
                        this.lex();
                        right = this.parseAssignmentExpression().left();
                        Statement body = this.getIteratorStatementEpilogue();
                        return new ForOfStatement((VariableDeclarationAssignmentTarget) init, right.fromJust(), body);
                    }
                } else {
                    this.expect(TokenType.SEMICOLON);
                    if (!this.match(TokenType.SEMICOLON)) {
                        test = this.parseExpression().left();
                    }
                    this.expect(TokenType.SEMICOLON);
                    if (!this.match(TokenType.RPAREN)) {
                        right = this.parseExpression().left();
                    }
                    return new ForStatement(Maybe.of(init), test, right, this.getIteratorStatementEpilogue());
                }
            } else {
                boolean previousAllowIn = this.allowIn;
                this.allowIn = false;
                Either<Expression, AssignmentTarget> fromParseAssignmentOrTarget = this.parseAssignmentExpressionOrTarget();
                this.allowIn = previousAllowIn;
                if (this.isAssignmentTarget && fromParseAssignmentOrTarget.left().map(expr -> !(expr instanceof AssignmentExpression)).orJust(true) && (this.match(TokenType.IN) || this.matchContextualKeyword("of"))) {
                    if (fromParseAssignmentOrTarget.isRight()) {
                        this.firstExprError = null;
                    }
                    AssignmentTarget target; // = fromParseAssignmentOrTarget.either(this::transformDestructuring, x -> x);
                    if (fromParseAssignmentOrTarget.isLeft()) {
                        target = transformDestructuring(fromParseAssignmentOrTarget.left().fromJust());
                    } else {
                        target = fromParseAssignmentOrTarget.right().fromJust();
                    }
                    if (startsWithLet && this.matchContextualKeyword("of")) {
                        throw this.createError(ErrorMessages.INVALID_LHS_IN_FOR_OF);
                    }
                    if (this.match(TokenType.IN)) {
                        this.lex();
                        right = this.parseExpression().left();
                        return new ForInStatement(target, right.fromJust(), this.getIteratorStatementEpilogue());
                    } else {
                        this.lex();
                        right = this.parseExpression().left();
                        return new ForOfStatement(target, right.fromJust(), this.getIteratorStatementEpilogue());
                    }
                } else {
                    Expression expr;
                    if (fromParseAssignmentOrTarget.isLeft()) {
                        expr = fromParseAssignmentOrTarget.left().fromJust();
                    } else {
                        throw this.createError(ErrorMessages.ILLEGAL_PROPERTY);
                    }
                    if (this.firstExprError != null) {
                        throw this.firstExprError;
                    }
                    while (this.eat(TokenType.COMMA)) {
                        Expression rhs = this.parseAssignmentExpression().left().fromJust();
                        expr = this.finishNode(leftLocation, new BinaryExpression(expr, BinaryOperator.Sequence, rhs));
                    }
                    if (this.match(TokenType.IN)) {
                        throw this.createError(ErrorMessages.INVALID_LHS_IN_FOR_IN);
                    }
                    if (this.matchContextualKeyword("of")) {
                        throw this.createError(ErrorMessages.INVALID_LHS_IN_FOR_OF);
                    }
                    this.expect(TokenType.SEMICOLON);
                    if (!this.match(TokenType.SEMICOLON)) {
                        test = this.parseExpression().left();
                    }
                    this.expect(TokenType.SEMICOLON);
                    if (!this.match(TokenType.RPAREN)) {
                        right = this.parseExpression().left();
                    }
                    return new ForStatement(Maybe.of(expr), test, right, this.getIteratorStatementEpilogue());
                }
            }
        }
    }

    @NotNull
    protected Parameter bindingToParameter(@NotNull BindingBindingWithDefault binding) {
        if (binding instanceof Binding) {
            return (Binding) binding;
        } else {
            return (BindingWithDefault) binding;
        }
    }

    @NotNull
    protected AssignmentTargetProperty transformDestructuring(ObjectProperty objectProperty) throws JsError {
        if (this.firstExprError != null) {
            throw this.firstExprError;
        }
        if (objectProperty instanceof DataProperty) {
            DataProperty dataProperty = (DataProperty) objectProperty;
            return this.copyNode(dataProperty, new AssignmentTargetPropertyProperty(dataProperty.name, this.transformDestructuringWithDefault(dataProperty.expression)));
        } else if (objectProperty instanceof ShorthandProperty) {
            ShorthandProperty shorthandProperty = (ShorthandProperty) objectProperty;
            return this.copyNode(shorthandProperty, new AssignmentTargetPropertyIdentifier(this.copyNode(shorthandProperty.name, new AssignmentTargetIdentifier(shorthandProperty.name.name)), Maybe.empty()));
        }
        throw this.createError(ErrorMessages.INVALID_LHS_IN_ASSIGNMENT);
    }

    @NotNull
    protected AssignmentTarget transformDestructuring(Expression node) throws JsError {
        if (node instanceof ObjectExpression) {
            ObjectExpression objectExpression = (ObjectExpression) node;
            ArrayList<AssignmentTargetProperty> properties = new ArrayList<>();
            for (ObjectProperty p : objectExpression.properties) {
                properties.add(this.transformDestructuring(p));
            }
            return this.copyNode(node, new ObjectAssignmentTarget(ImmutableList.from(properties)));
        } else if (node instanceof ArrayExpression) {
            ArrayExpression arrayExpression = (ArrayExpression) node;
            Maybe<SpreadElementExpression> last = Maybe.join(arrayExpression.elements.maybeLast());
            ImmutableList<Maybe<SpreadElementExpression>> elements = arrayExpression.elements;
            ArrayList<Maybe<AssignmentTargetAssignmentTargetWithDefault>> newElements = new ArrayList<>();
            if (last.isJust() && last.fromJust() instanceof SpreadElement) {
                SpreadElement spreadElement = (SpreadElement) last.fromJust();
                for (Maybe<SpreadElementExpression> maybeBbwd : ((NonEmptyImmutableList<Maybe<SpreadElementExpression>>) elements).init()) {
                    if (maybeBbwd.isJust()) {
                        newElements.add(Maybe.of(this.transformDestructuringWithDefault((Expression) maybeBbwd.fromJust())));
                    } else {
                        newElements.add(Maybe.empty());
                    }
                }
                return this.copyNode(node, new ArrayAssignmentTarget(ImmutableList.from(newElements), Maybe.of(this.transformDestructuring(spreadElement.expression))));
            } else {
                for (Maybe<SpreadElementExpression> maybeBbwd : elements) {
                    if (maybeBbwd.isJust()) {
                        newElements.add(Maybe.of(this.transformDestructuringWithDefault((Expression) maybeBbwd.fromJust())));
                    } else {
                        newElements.add(Maybe.empty());
                    }
                }
                return this.copyNode(node, new ArrayAssignmentTarget(ImmutableList.from(newElements), Maybe.empty()));
            }

        } else if (node instanceof IdentifierExpression) {
            return this.copyNode(node, new AssignmentTargetIdentifier(((IdentifierExpression) node).name));
        } else if (node instanceof ComputedMemberExpression) {
            ComputedMemberExpression expr = (ComputedMemberExpression) node;
            return this.copyNode(node, new ComputedMemberAssignmentTarget(expr.object, expr.expression));
        } else if (node instanceof StaticMemberExpression) {
            StaticMemberExpression expr = (StaticMemberExpression) node;
            return this.copyNode(node, new StaticMemberAssignmentTarget(expr.object, expr.property));
        }
        throw this.createError(ErrorMessages.INVALID_LHS_IN_ASSIGNMENT);
    }

    @NotNull
    protected AssignmentTargetIdentifier transformDestructuring(StaticPropertyName property) {
        return this.copyNode(property, new AssignmentTargetIdentifier(property.value));
    }

    @NotNull
    protected AssignmentTargetAssignmentTargetWithDefault transformDestructuringWithDefault(Expression node) throws JsError {
        if (node instanceof AssignmentExpression) {
            AssignmentExpression assignmentExpression = (AssignmentExpression) node;
            return this.copyNode(node, new AssignmentTargetWithDefault(assignmentExpression.binding, assignmentExpression.expression));
        }
        return this.transformDestructuring(node);
    }

    protected boolean matchContextualKeyword(String keyword) {
        return this.lookahead.type == TokenType.IDENTIFIER && keyword.equals(this.lookahead.toString());
    }

    @NotNull
    protected Statement parseSwitchStatement() throws JsError {
        this.lex();
        this.expect(TokenType.LPAREN);
        Expression discriminant = this.parseExpression().left().fromJust();
        this.expect(TokenType.RPAREN);
        this.expect(TokenType.LBRACE);

        if (this.eat(TokenType.RBRACE)) {
            return new SwitchStatement(discriminant, ImmutableList.empty());
        }

        ImmutableList<SwitchCase> cases = this.parseSwitchCases();
        if (this.match(TokenType.DEFAULT)) {
            SwitchDefault defaultCase = this.parseSwitchDefault();
            ImmutableList<SwitchCase> postDefaultCases = this.parseSwitchCases();
            if (this.match(TokenType.DEFAULT)) {
                throw this.createError(ErrorMessages.MULTIPLE_DEFAULTS_IN_SWITCH);
            }
            this.expect(TokenType.RBRACE);
            return new SwitchStatementWithDefault(discriminant, cases, defaultCase, postDefaultCases);
        } else {
            this.expect(TokenType.RBRACE);
            return new SwitchStatement(discriminant, cases);
        }
    }

    @NotNull
    protected ImmutableList<SwitchCase> parseSwitchCases() throws JsError {
        ArrayList<SwitchCase> result = new ArrayList<>();
        while (!(this.eof() || this.match(TokenType.RBRACE) || this.match(TokenType.DEFAULT))) {
            result.add(this.parseSwitchCase());
        }
        return ImmutableList.from(result);
    }

    @NotNull
    protected SwitchCase parseSwitchCase() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.expect(TokenType.CASE);
        return finishNode(startState, new SwitchCase(this.parseExpression().left().fromJust(),
                this.parseSwitchCaseBody()));
    }

    @NotNull
    protected ImmutableList<Statement> parseSwitchCaseBody() throws JsError {
        this.expect(TokenType.COLON);
        return this.parseStatementListInSwitchCaseBody();
    }

    @NotNull
    protected ImmutableList<Statement> parseStatementListInSwitchCaseBody() throws JsError {
        ArrayList<Statement> result = new ArrayList<>();
        while (!(this.eof() || this.match(TokenType.RBRACE) || this.match(TokenType.DEFAULT) || this.match(TokenType.CASE))) {
            result.add(this.parseStatementListItem());
        }
        return ImmutableList.from(result);
    }

    @NotNull
    protected SwitchDefault parseSwitchDefault() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.expect(TokenType.DEFAULT);
        return this.finishNode(startState, new SwitchDefault(this.parseSwitchCaseBody()));
    }

    @NotNull
    protected Statement parseDebuggerStatement() throws JsError {
        this.lex();
        this.consumeSemicolon();
        return new DebuggerStatement();
    }

    @NotNull
    protected Statement parseDoWhileStatement() throws JsError {
        this.lex();
        Statement body = this.parseStatement();
        this.expect(TokenType.WHILE);
        this.expect(TokenType.LPAREN);
        Expression test = this.parseExpression().left().fromJust();
        this.expect(TokenType.RPAREN);
        this.eat(TokenType.SEMICOLON);
        return new DoWhileStatement(body, test);
    }

    @NotNull
    protected Statement parseContinueStatement() throws JsError {
        this.lex();
        if (this.eat(TokenType.SEMICOLON) || this.hasLineTerminatorBeforeNext) {
            return new ContinueStatement(Maybe.empty());
        }
        Maybe<String> label = Maybe.empty();
        if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
            label = Maybe.of(this.parseIdentifier());
        }
        this.consumeSemicolon();
        return new ContinueStatement(label);
    }

    @NotNull
    protected Statement parseBreakStatement() throws JsError {
        this.lex();
        if (this.eat(TokenType.SEMICOLON) || this.hasLineTerminatorBeforeNext) {
            return new BreakStatement(Maybe.empty());
        }
        Maybe<String> label = Maybe.empty();
        if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
            label = Maybe.of(this.parseIdentifier());
        }
        this.consumeSemicolon();
        return new BreakStatement(label);
    }

    @NotNull
    protected Statement parseTryStatement() throws JsError {
        this.lex();
        Block body = this.parseBlock();

        if (this.match(TokenType.CATCH)) {
            CatchClause catchClause = this.parseCatchClause();
            if (this.eat(TokenType.FINALLY)) {
                Block finalizer = this.parseBlock();
                return new TryFinallyStatement(body, Maybe.of(catchClause), finalizer);
            }
            return new TryCatchStatement(body, catchClause);
        }
        if (this.eat(TokenType.FINALLY)) {
            Block finalizer = this.parseBlock();
            return new TryFinallyStatement(body, Maybe.empty(), finalizer);
        } else {
            throw this.createError(ErrorMessages.NO_CATCH_OR_FINALLY);
        }
    }

    @NotNull
    protected CatchClause parseCatchClause() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();
        this.expect(TokenType.LPAREN);
        if (this.match(TokenType.RPAREN) || this.match(TokenType.LPAREN)) {
            throw this.createUnexpected(this.lookahead);
        }
        Binding binding = this.parseBindingTarget();
        this.expect(TokenType.RPAREN);
        Block body = this.parseBlock();

        return this.finishNode(startState, new CatchClause(binding, body));
    }

    @NotNull
    protected Statement parseThrowStatement() throws JsError {
        this.lex();
        if (this.hasLineTerminatorBeforeNext) {
            throw this.createErrorWithLocation(this.getLocation(), ErrorMessages.NEWLINE_AFTER_THROW);
        }
        Expression expression = this.parseExpression().left().fromJust();
        this.consumeSemicolon();
        return new ThrowStatement(expression);

    }

    @NotNull
    protected Statement parseReturnStatement() throws JsError {
        if (!this.inFunctionBody) {
            throw this.createError(ErrorMessages.ILLEGAL_RETURN);
        }

        this.lex();

        if (this.eat(TokenType.SEMICOLON) || this.hasLineTerminatorBeforeNext) {
            return new ReturnStatement(Maybe.empty());
        }

        Maybe<Expression> expression = Maybe.empty();

        if (!this.match(TokenType.RBRACE) && !this.eof()) {
            expression = this.parseExpression().left();
        }

        this.consumeSemicolon();
        return new ReturnStatement(expression);
    }

    @NotNull
    protected Statement parseEmptyStatement() throws JsError {
        this.lex();
        return new EmptyStatement();
    }

    @NotNull
    protected Statement parseWhileStatement() throws JsError {
        this.lex();
        this.expect(TokenType.LPAREN);
        Expression test = this.parseExpression().left().fromJust();
        Statement body = this.getIteratorStatementEpilogue();
        return new WhileStatement(test, body);
    }

    @NotNull
    protected Statement parseWithStatement() throws JsError {
        this.lex();
        this.expect(TokenType.LPAREN);
        Expression test = this.parseExpression().left().fromJust();
        Statement body = this.getIteratorStatementEpilogue();
        return new WithStatement(test, body);
    }

    @NotNull
    protected Statement getIteratorStatementEpilogue() throws JsError {
        this.expect(TokenType.RPAREN);
        return this.parseStatement();
    }

    @NotNull
    protected Statement parseBlockStatement() throws JsError {
        return new BlockStatement(this.parseBlock());
    }

    @NotNull
    protected Block parseBlock() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.expect(TokenType.LBRACE);
        ArrayList<Statement> body = new ArrayList<>();
        while (!this.match(TokenType.RBRACE)) {
            body.add(parseStatementListItem());
        }
        this.expect(TokenType.RBRACE);
        return this.finishNode(startState, new Block(ImmutableList.from(body)));
    }

    @NotNull
    protected Statement parseExpressionStatement() throws JsError {
        Expression expr = this.parseExpression().left().fromJust();
        this.consumeSemicolon();
        return new ExpressionStatement(expr);
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        Either<Expression, AssignmentTarget> left = this.parseAssignmentExpression();
        if (this.match(TokenType.COMMA)) {
            while (!this.eof()) {
                if (!this.match(TokenType.COMMA)) {
                    break;
                }
                this.lex();
                Expression right = this.parseAssignmentExpression().left().fromJust();
                left = Either.left(this.finishNode(startState, new BinaryExpression(left.left().fromJust(), BinaryOperator.Sequence, right)));
            }
        }
        return left;
    }

    @NotNull
    protected <T> T isolateCoverGrammar(ExceptionalSupplier<T> parser) throws JsError {
        boolean oldIsBindingElement = this.isBindingElement;
        boolean oldIsAssignmentTarget = this.isAssignmentTarget;
        JsError oldFirstExprError = this.firstExprError;
        T result;
        this.isBindingElement = this.isAssignmentTarget = true;
        this.firstExprError = null;
        result = parser.get();
        if (this.firstExprError != null) {
            throw this.firstExprError;
        }
        this.isBindingElement = oldIsBindingElement;
        this.isAssignmentTarget = oldIsAssignmentTarget;
        this.firstExprError = oldFirstExprError;
        return result;
    }

    @NotNull
    protected <T> T inheritCoverGrammar(ExceptionalSupplier<T> parser) throws JsError {
        boolean oldIsBindingElement = this.isBindingElement;
        boolean oldIsAssignmentTarget = this.isAssignmentTarget;
        JsError oldFirstExprError = this.firstExprError;
        T result;
        this.isBindingElement = this.isAssignmentTarget = true;
        this.firstExprError = null;
        result = parser.get();
        this.isBindingElement = this.isBindingElement && oldIsBindingElement;
        this.isAssignmentTarget = this.isAssignmentTarget && oldIsAssignmentTarget;
        this.firstExprError = oldFirstExprError != null ? oldFirstExprError : this.firstExprError;
        return result;
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseAssignmentExpression() throws JsError {
        return this.isolateCoverGrammar(this::parseAssignmentExpressionOrTarget);
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseAssignmentExpressionOrTarget() throws JsError {
        AdditionalStateT startState = this.startNode();

        if (this.allowYieldExpression && this.match(TokenType.YIELD)) {
            this.isBindingElement = this.isAssignmentTarget = false;
            return Either.left(this.parseYieldExpression());
        }

        Either<Expression, AssignmentTarget> expr;
        if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
            expr = this.parseConditionalExpression();
            if (!this.hasLineTerminatorBeforeNext && this.match(TokenType.ARROW)) {
                this.isBindingElement = this.isAssignmentTarget = false;
                this.firstExprError = null;
                ArrayList<BindingBindingWithDefault> params = new ArrayList<>();
                params.add(this.targetToBinding(this.transformDestructuring(expr.left().fromJust())));
                return Either.left(this.parseArrowExpressionTail(params, Maybe.empty(), startState));
            }
        } else {
            expr = this.parseConditionalExpression();
        }

        boolean isAssignmentOperator = false;
        Token operator = this.lookahead;
        switch (operator.type) {
            case ASSIGN_BIT_OR:
            case ASSIGN_BIT_XOR:
            case ASSIGN_BIT_AND:
            case ASSIGN_SHL:
            case ASSIGN_SHR:
            case ASSIGN_SHR_UNSIGNED:
            case ASSIGN_ADD:
            case ASSIGN_SUB:
            case ASSIGN_MUL:
            case ASSIGN_DIV:
            case ASSIGN_MOD:
            case ASSIGN_EXP:
                isAssignmentOperator = true;
                break;
        }
        AssignmentTarget assignmentTarget;
        if (isAssignmentOperator) {
            if (expr.isRight() || !this.isAssignmentTarget || !isValidSimpleAssignmentTarget(expr.left().fromJust())) {
                throw this.createError(ErrorMessages.INVALID_LHS_IN_ASSIGNMENT);
            }
            assignmentTarget = this.transformDestructuring(expr.left().fromJust());
        } else if (operator.type == TokenType.ASSIGN) {
            if (!this.isAssignmentTarget) {
                throw this.createError(ErrorMessages.INVALID_LHS_IN_ASSIGNMENT);
            }
            if (expr.isLeft()) {
                assignmentTarget = this.transformDestructuring(expr.left().fromJust());
            } else {
                assignmentTarget = expr.right().fromJust();
            }
        } else {
            return expr;
        }

        this.lex();
        Either<Expression, AssignmentTarget> rhs = this.parseAssignmentExpression();
        if (rhs.isRight()) {
            throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
        }

        this.firstExprError = null;
        if (operator.type == TokenType.ASSIGN) {
            return Either.left(this.finishNode(startState, new AssignmentExpression(assignmentTarget, rhs.left().fromJust())));
        } else {
            CompoundAssignmentOperator compoundAssignmentOperator = lookupCompoundAssignmentOperator(operator);
            if (compoundAssignmentOperator != null) {
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, new CompoundAssignmentExpression((SimpleAssignmentTarget) assignmentTarget, compoundAssignmentOperator, rhs.left().fromJust())));
            } else {
                throw this.createError("should not be here", 0, 0, 0);
            }
        }
    }

    @NotNull
    protected Expression parseYieldExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();

        if (this.hasLineTerminatorBeforeNext) {
            return this.finishNode(startState, new YieldExpression(Maybe.empty()));
        }
        boolean isGenerator = this.eat(TokenType.MUL);
        Maybe<Expression> expr = Maybe.empty();
        if (isGenerator || this.lookaheadAssignmentExpression()) {
            expr = this.parseAssignmentExpression().left();
        }
        if (isGenerator) {
            return this.finishNode(startState, new YieldGeneratorExpression(expr.fromJust()));
        } else {
            return this.finishNode(startState, new YieldExpression(expr));
        }
    }

    protected boolean lookaheadAssignmentExpression() {
        switch (this.lookahead.type) {
            case ADD:
            case ASSIGN_DIV:
            case CLASS:
            case DEC:
            case DIV:
            case FALSE_LITERAL:
            case FUNCTION:
            case IDENTIFIER:
            case INC:
            case LET:
            case LBRACE:
            case LBRACK:
            case LPAREN:
            case NEW:
            case NOT:
            case NULL_LITERAL:
            case NUMBER:
            case STRING:
            case SUB:
            case SUPER:
            case THIS:
            case TRUE_LITERAL:
            case YIELD:
            case TEMPLATE:
                return true;
        }
        return false;

    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseConditionalExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        Either<Expression, AssignmentTarget> test = this.parseBinaryExpression();
        if (this.firstExprError != null) {
            return test;
        }
        if (this.eat(TokenType.CONDITIONAL)) {
            if (test.isLeft()) {
                this.isBindingElement = this.isAssignmentTarget = false;
                boolean previousAllowIn = this.allowIn;
                this.allowIn = true;
                Expression consequent = this.isolateCoverGrammar(this::parseAssignmentExpression).left().fromJust();
                this.allowIn = previousAllowIn;
                this.expect(TokenType.COLON);
                Expression alternate = this.isolateCoverGrammar(this::parseAssignmentExpression).left().fromJust();
                return Either.left(this.finishNode(startState, new ConditionalExpression(test.left().fromJust(), consequent, alternate)));
            } else {
                throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
            }
        }
        return test;
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseBinaryExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        Either<Expression, AssignmentTarget> left = this.parseExponentiationExpression();

        if (this.firstExprError != null) {
            return left;
        }

        BinaryOperator operator = lookupBinaryOperator(this.lookahead, this.allowIn);
        if (operator == null) {
            return left;
        }

        this.isBindingElement = this.isAssignmentTarget = false;

        if (left.isLeft()) {
            this.lex();
            ImmutableList<ExprStackItem<AdditionalStateT>> stack = ImmutableList.empty();
            stack = stack.cons(new ExprStackItem<>(startState, left.left().fromJust(), operator));
            startState = this.startNode();
            Either<Expression, AssignmentTarget> expr = this.isolateCoverGrammar(this::parseExponentiationExpression);
            operator = lookupBinaryOperator(this.lookahead, this.allowIn);
            while (operator != null) {
                Precedence precedence = operator.getPrecedence();
                // Reduce: make a binary expression from the three topmost entries.
                while ((stack.isNotEmpty()) && (precedence.ordinal() <= ((NonEmptyImmutableList<ExprStackItem<AdditionalStateT>>) stack).head.precedence)) {
                    ExprStackItem<AdditionalStateT> stackItem = ((NonEmptyImmutableList<ExprStackItem<AdditionalStateT>>) stack).head;
                    BinaryOperator stackOperator = stackItem.operator;
                    left = Either.left(stackItem.left);
                    stack = ((NonEmptyImmutableList<ExprStackItem<AdditionalStateT>>) stack).tail();
                    startState = stackItem.startState;
                    expr = Either.left(this.finishNode(stackItem.startState, new BinaryExpression(left.left().fromJust(), stackOperator, expr.left().fromJust())));
                }

                // Shift.
                this.lex();
                stack = stack.cons(new ExprStackItem<>(startState, expr.left().fromJust(), operator));
                startState = this.startNode();
                expr = this.isolateCoverGrammar(this::parseExponentiationExpression);

                operator = lookupBinaryOperator(this.lookahead, this.allowIn);
            }

            // Final reduce to clean-up the stack.
            return Either.left(stack.foldLeft(
                    (expr1, stackItem) -> this.finishNode(
                            stackItem.startState, new BinaryExpression(stackItem.left, stackItem.operator, expr1)), expr.left().fromJust()));
        } else {
            throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
        }
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseExponentiationExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        Either<Expression, AssignmentTarget> left = this.parseUnaryExpression();

        if (this.lookahead.type != TokenType.EXP) {
            return left;
        }
        this.lex();

        Expression right = this.isolateCoverGrammar(this::parseExponentiationExpression).left().fromJust();
        return Either.left(this.finishNode(startState, new BinaryExpression(left.left().fromJust(), BinaryOperator.Exp, right)));
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseUnaryExpression() throws JsError {
        if (this.lookahead.type.klass != TokenClass.Punctuator && this.lookahead.type.klass != TokenClass.Keyword) {
            return this.parseUpdateExpression();
        }

        AdditionalStateT startState = this.startNode();
        Token operatorToken = this.lookahead;
        if (!isPrefixOperator(operatorToken)) {
            return this.parseUpdateExpression();
        }

        this.lex();
        this.isBindingElement = this.isAssignmentTarget = false;
        Either<Expression, AssignmentTarget> operand = this.isolateCoverGrammar(this::parseUnaryExpression);

        if (operand.isLeft()) {
            UpdateOperator updateOperator = lookupUpdateOperator(operatorToken);
            if (updateOperator != null) {
                return Either.left(createUpdateExpression(startState, operand.left().fromJust(), updateOperator, true));
            }
            UnaryOperator operator = lookupUnaryOperator(operatorToken);
            assert operator != null;
            return Either.left(this.finishNode(startState, new UnaryExpression(operator, operand.left().fromJust())));
        } else {
            throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
        }
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseUpdateExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        Either<Expression, AssignmentTarget> operand = this.parseLeftHandSideExpression(true).mapLeft(x -> (Expression) x);
        if (this.firstExprError != null || this.hasLineTerminatorBeforeNext) {
            return operand;
        }
        UpdateOperator operator = lookupUpdateOperator(this.lookahead);
        if (operator == null) {
            return operand;
        }
        this.lex();
        if (operand.isLeft()) {
            this.isBindingElement = this.isAssignmentTarget = false;
            return Either.left(createUpdateExpression(startState, operand.left().fromJust(), operator, false));
        } else {
            throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
        }
    }

    @NotNull
    protected Expression createUpdateExpression(@NotNull AdditionalStateT startState, @NotNull Expression operand, @NotNull UpdateOperator operator, boolean isPrefix) throws JsError {
        SimpleAssignmentTarget restrictedOperand;
        if (operand instanceof MemberExpression || operand instanceof IdentifierExpression) {
            restrictedOperand = (SimpleAssignmentTarget) transformDestructuring(operand);
        } else {
            throw this.createError("Increment/decrement target must be an identifier or member expression");
        }
        return this.finishNode(startState, new UpdateExpression(isPrefix, operator, restrictedOperand));
    }

    @NotNull
    protected Expression parseNumericLiteral() throws JsError {
        AdditionalStateT startState = this.startNode();
        SourceLocation startLocation = this.getLocation();

        Token token = this.lex();
        assert token instanceof NumericLiteralToken;
        if (((NumericLiteralToken) token).octal && this.strict) {
            if (((NumericLiteralToken) token).noctal) {
                throw this.createErrorWithLocation(startLocation, "Unexpected noctal integer literal");
            } else {
                throw this.createErrorWithLocation(startLocation, "Unexpected legacy octal integer literal");
            }
        }
        if (Double.isInfinite(((NumericLiteralToken) token).value)) {
            return this.finishNode(startState, new LiteralInfinityExpression());
        } else {
            return this.finishNode(startState, new LiteralNumericExpression(((NumericLiteralToken) token).value));
        }
    }

    @NotNull
    protected Either<ExpressionSuper, AssignmentTarget> parseLeftHandSideExpression(boolean allowCall) throws JsError {
        AdditionalStateT startState = this.startNode();
        boolean previousAllowIn = this.allowIn;
        this.allowIn = allowCall;

        Either<ExpressionSuper, AssignmentTarget> expr;
        Token token = this.lookahead;

        if (this.eat(TokenType.SUPER)) {
            this.isBindingElement = this.isAssignmentTarget = false;
            expr = Either.left(this.finishNode(startState, new Super()));

            if (this.match(TokenType.LPAREN)) {
                if (allowCall) {
                    expr = Either.left(this.finishNode(startState, new CallExpression(expr.left().fromJust(), this.parseArgumentList())));
                }
            } else if (this.match(TokenType.LBRACK)) {
                expr = Either.left(this.finishNode(startState, new ComputedMemberExpression(expr.left().fromJust(), this.parseComputedMember())));
                this.isAssignmentTarget = true;
            } else if (this.match(TokenType.PERIOD)) {
                expr = Either.left(this.finishNode(startState, new StaticMemberExpression(expr.left().fromJust(), this.parseStaticMember())));
                this.isAssignmentTarget = true;
            } else {
                throw this.createUnexpected(token);
            }
        } else if (this.match(TokenType.NEW)) {
            this.isBindingElement = this.isAssignmentTarget = false;
            expr = Either.left(this.parseNewExpression());
        } else {
            expr = this.parsePrimaryExpression().mapLeft(x -> (ExpressionSuper) x);
            if (this.firstExprError != null) {
                return expr;
            }
        }

        while (true) {
            if (allowCall && this.match((TokenType.LPAREN))) {
                this.isBindingElement = this.isAssignmentTarget = false;
                expr = Either.left(this.finishNode(startState, new CallExpression(expr.left().fromJust(), this.parseArgumentList())));
            } else if (this.match(TokenType.TEMPLATE)) {
                this.isBindingElement = this.isAssignmentTarget = false;
                expr = Either.left(this.finishNode(startState, new TemplateExpression(Maybe.of((Expression) expr.left().fromJust()), this.parseTemplateElements())));
            } else if (this.match(TokenType.LBRACK)) {
                this.isBindingElement = false;
                this.isAssignmentTarget = true;
                expr = Either.left(this.finishNode(startState, new ComputedMemberExpression(expr.left().fromJust(), this.parseComputedMember())));
            } else if (this.match(TokenType.PERIOD)) {
                this.isBindingElement = false;
                this.isAssignmentTarget = true;
                expr = Either.left(this.finishNode(startState, new StaticMemberExpression(expr.left().fromJust(), this.parseStaticMember())));
            } else {
                break;
            }
        }

        this.allowIn = previousAllowIn;
        if (expr.isLeft()) { // todo why is this not just return expr?
            return Either.left(expr.left().fromJust());
        } else {
            return Either.right(expr.right().fromJust());
        }
    }

    @NotNull
    protected String parseStaticMember() throws JsError {
        this.lex();
        if (!isIdentifierName(this.lookahead.type.klass)) {
            throw this.createUnexpected(this.lookahead);
        } else {
            return this.lex().toString();
        }
    }

    @NotNull
    protected Expression parseComputedMember() throws JsError {
        this.lex();
        Expression expr = this.parseExpression().left().fromJust();
        this.expect(TokenType.RBRACK);
        return expr;
    }

    @NotNull
    protected ImmutableList<ExpressionTemplateElement> parseTemplateElements() throws JsError {
        AdditionalStateT startState = this.startNode();
        Token token = this.lookahead;
        String nonTemplatePart;
        ArrayList<ExpressionTemplateElement> result = new ArrayList<>();
        if (((TemplateToken) token).tail) {
            this.lex();
            nonTemplatePart = token.slice.subSequence(1, token.slice.length() - 1).toString();
            result.add(this.finishNode(startState, new TemplateElement(nonTemplatePart)));
            return ImmutableList.from(result);
        }
        token = this.lex();
        nonTemplatePart = token.slice.subSequence(1, token.slice.length() - 2).toString();
        result.add(this.finishNode(startState, new TemplateElement(nonTemplatePart)));
        while (true) {
            result.add(this.parseExpression().left().fromJust());
            if (!this.match(TokenType.RBRACE)) {
                throw this.createILLEGAL();
            }
            this.index = this.startIndex;
            this.line = this.startLine;
            this.lineStart = this.startLineStart;
            this.lookahead = this.scanTemplateElement();
            startState = this.startNode();
            token = this.lex();
            if (((TemplateToken) token).tail) {
                nonTemplatePart = token.slice.subSequence(1, token.slice.length() - 1).toString();
                result.add(this.finishNode(startState, new TemplateElement(nonTemplatePart)));
                return ImmutableList.from(result);
            } else {
                nonTemplatePart = token.slice.subSequence(1, token.slice.length() - 2).toString();
                result.add(this.finishNode(startState, new TemplateElement(nonTemplatePart)));
            }
        }
    }

    @NotNull
    protected Expression parseNewExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();
        if (this.eat(TokenType.PERIOD)) {
            Token ident = this.expect(TokenType.IDENTIFIER);
            if ((!ident.toString().equals("target"))) {
                throw this.createUnexpected(ident);
            }
            return this.finishNode(startState, new NewTargetExpression());
        }
        Either<ExpressionSuper, AssignmentTarget> fromParseLeftHandSideExpression = this.isolateCoverGrammar(
                () -> this.parseLeftHandSideExpression(false));
        if (fromParseLeftHandSideExpression.isLeft()) {
            ExpressionSuper callee = fromParseLeftHandSideExpression.left().fromJust();
            if (!(callee instanceof Expression)) {
                throw this.createUnexpected(this.lookahead);
            }
            return this.finishNode(startState, new NewExpression((Expression) callee, this.match(TokenType.LPAREN) ? this.parseArgumentList() : ImmutableList.empty()));
        } else {
            throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
        }
    }

    @NotNull
    protected ImmutableList<SpreadElementExpression> parseArgumentList() throws JsError {
        this.lex();
        ImmutableList<SpreadElementExpression> args = this.parseArguments();
        this.expect(TokenType.RPAREN);
        return args;
    }

    @NotNull
    protected ImmutableList<SpreadElementExpression> parseArguments() throws JsError {
        ArrayList<SpreadElementExpression> result = new ArrayList<>();
        while (true) {
            if (this.match(TokenType.RPAREN) || this.eof()) {
                return ImmutableList.from(result);
            }
            SpreadElementExpression arg;
            if (this.eat(TokenType.ELLIPSIS)) {
                AdditionalStateT startState = this.startNode();
                arg = this.finishNode(startState, new SpreadElement(this.parseAssignmentExpression().left().fromJust()));
            } else {
                arg = this.parseAssignmentExpression().left().fromJust();
            }
            result.add(arg);
            if (!this.eat(TokenType.COMMA)) {
                break;
            }
        }
        return ImmutableList.from(result);
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parsePrimaryExpression() throws JsError {
        if (this.match(TokenType.LPAREN)) {
            return this.parseGroupExpression();
        }
        AdditionalStateT startState = this.startNode();

        switch (this.lookahead.type) {
            case YIELD:
                if (this.allowYieldExpression) {
                    throw this.createUnexpected(this.lookahead);
                }
                // falls through
            case LET:
            case IDENTIFIER:
                return Either.left(this.finishNode(startState, new IdentifierExpression(this.lex().toString())));
            case TRUE_LITERAL:
                this.lex();
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, new LiteralBooleanExpression(true)));
            case FALSE_LITERAL:
                this.lex();
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, new LiteralBooleanExpression(false)));
            case NULL_LITERAL:
                this.lex();
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, new LiteralNullExpression()));
            case FUNCTION:
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, this.parseFunctionExpression(true)));
            case NUMBER:
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.parseNumericLiteral());
            case STRING:
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.parseStringLiteral());
            case LBRACK:
                return this.parseArrayExpression();
            case THIS:
                this.lex();
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, new ThisExpression()));
            case LBRACE:
                return this.parseObjectExpression();
            case CLASS:
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.parseClass());
            case TEMPLATE:
                this.isBindingElement = this.isAssignmentTarget = false;
                return Either.left(this.finishNode(startState, new TemplateExpression(Maybe.empty(), this.parseTemplateElements())));
            case DIV:
            case ASSIGN_DIV:
                this.isBindingElement = this.isAssignmentTarget = false;
                this.lookahead = this.scanRegExp(this.match(TokenType.DIV) ? "/" : "/=");
                Token token = this.lex();
                int lastSlash = ((RegularExpressionLiteralToken) token).getValueString().lastIndexOf("/");
                String pattern = ((RegularExpressionLiteralToken) token).getValueString().substring(1, lastSlash);
                String flags = ((RegularExpressionLiteralToken) token).getValueString().substring(lastSlash + 1);
                boolean gFlag = false, iFlag = false, mFlag = false, uFlag = false, yFlag = false;
                for (char c : flags.toCharArray()) {
                    switch (c) {
                        // duplicate or invalid flags are Early Syntax Errors per 12.2.8.1, but we can't represent them, so they are an early grammar error.
                        case 'g':
                            if (gFlag) {
                                throw this.createErrorWithLocation(this.getLocation(), "Duplicate regular expression flag 'g'");
                            }
                            gFlag = true;
                            break;
                        case 'i':
                            if (iFlag) {
                                throw this.createErrorWithLocation(this.getLocation(), "Duplicate regular expression flag 'i'");
                            }
                            iFlag = true;
                            break;
                        case 'm':
                            if (mFlag) {
                                throw this.createErrorWithLocation(this.getLocation(), "Duplicate regular expression flag 'm'");
                            }
                            mFlag = true;
                            break;
                        case 'u':
                            if (uFlag) {
                                throw this.createErrorWithLocation(this.getLocation(), "Duplicate regular expression flag 'u'");
                            }
                            uFlag = true;
                            break;
                        case 'y':
                            if (yFlag) {
                                throw this.createErrorWithLocation(this.getLocation(), "Duplicate regular expression flag 'y'");
                            }
                            yFlag = true;
                            break;
                        default:
                            throw this.createErrorWithLocation(this.getLocation(), "Invalid regular expression flags");
                    }
                }
                return Either.left(this.finishNode(startState, new LiteralRegExpExpression(pattern, gFlag, iFlag, mFlag, yFlag, uFlag)));
            default:
                throw this.createUnexpected(this.lookahead);
        }
    }

    @NotNull
    protected Expression parseStringLiteral() throws JsError {
        AdditionalStateT startState = this.startNode();
        SourceLocation startLocation = this.getLocation();
        Token token = this.lex();
        assert token instanceof StringLiteralToken;
        if (((StringLiteralToken) token).octal != null && this.strict) {
            throw this.createErrorWithLocation(startLocation, "Unexpected legacy octal escape sequence: \\" + ((StringLiteralToken) token).octal);
        }
        return this.finishNode(startState, new LiteralStringExpression(token.getValueString().toString()));
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseArrayExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();

        ArrayList<Maybe<SpreadElementExpression>> exprs = new ArrayList<>();
        ArrayList<Maybe<AssignmentTargetAssignmentTargetWithDefault>> bindings = new ArrayList<>();
        Maybe<AssignmentTarget> rest = Maybe.empty();
        boolean allExpressionsSoFar = true;

        while (true) {
            if (this.match(TokenType.RBRACK)) {
                break;
            }
            if (this.eat(TokenType.COMMA)) {
                exprs.add(Maybe.empty());
            } else {
                AdditionalStateT elementLocation = this.startNode();
                if (this.eat(TokenType.ELLIPSIS)) {
                    Either<SpreadElementExpression, AssignmentTarget> expr = this.parseAssignmentExpressionOrTarget().mapLeft(x -> (SpreadElementExpression) x); //TODO inherit cover grammar
                    if (expr.isLeft()) {
                        exprs.add(Maybe.of(this.finishNode(elementLocation, new SpreadElement((Expression) expr.left().fromJust()))));
                    } else {
                        allExpressionsSoFar = false;
                        for (Maybe<SpreadElementExpression> e : exprs) {
                            if (e.isNothing()) {
                                bindings.add(Maybe.empty());
                            } else {
                                SpreadElementExpression r = e.fromJust();
                                if (r instanceof SpreadElement) {
                                    throw this.createError(ErrorMessages.INVALID_REST);
                                }
                                bindings.add(Maybe.of(transformDestructuring((Expression) r)));
                            }
                        }
                        rest = expr.right();
                        break;
                    }
                    if (!this.isAssignmentTarget && this.firstExprError != null) {
                        throw this.firstExprError;
                    }
                    if (!this.match(TokenType.RBRACK)) {
                        this.isBindingElement = this.isAssignmentTarget = false;
                    }
                } else {
                    Either<Expression, AssignmentTarget> expr = this.parseAssignmentExpressionOrTarget(); //TODO inherit cover grammar
                    if (allExpressionsSoFar) {
                        if (expr.isLeft()) {
                            exprs.add(expr.left().map(x -> (SpreadElementExpression) x));
                        } else {
                            allExpressionsSoFar = false;
                            for (Maybe<SpreadElementExpression> e : exprs) {
                                if (e.isNothing()) {
                                    bindings.add(Maybe.empty());
                                } else {
                                    SpreadElementExpression r = e.fromJust();
                                    if (r instanceof SpreadElement) {
                                        rest = Maybe.of(this.transformDestructuring(((SpreadElement) r).expression));
                                        break;
                                    } else {
                                        bindings.add(Maybe.of(this.transformDestructuring((Expression) r)));
                                    }
                                }
                            }
                            bindings.add(expr.right().map(x -> (AssignmentTargetAssignmentTargetWithDefault) x));
                        }
                    } else {
                        if (expr.isLeft()) {
                            bindings.add(Maybe.of(this.transformDestructuring(expr.left().fromJust())));
                        } else {
                            bindings.add(expr.right().map(x -> (AssignmentTargetAssignmentTargetWithDefault) x));
                        }
                    }

                    if (!this.isAssignmentTarget && this.firstExprError != null) {
                        throw this.firstExprError;
                    }
                }

                if (!this.match(TokenType.RBRACK)) {
                    this.expect(TokenType.COMMA);
                }
            }
        }

        this.expect(TokenType.RBRACK);
        if (allExpressionsSoFar) {
            return Either.left(this.finishNode(startState, new ArrayExpression(ImmutableList.from(exprs))));
        } else {
            return Either.right(this.finishNode(startState, new ArrayAssignmentTarget(ImmutableList.from(bindings), rest)));
        }
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseObjectExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        this.lex();

        ArrayList<ObjectProperty> objectProperties = new ArrayList<>();
        ArrayList<AssignmentTargetProperty> bindingProperties = new ArrayList<>();

        boolean allExpressionsSoFar = true;

        while (!this.match(TokenType.RBRACE)) {
            Either<ObjectProperty, AssignmentTargetProperty> fromParsePropertyDefinition = this.parsePropertyDefinition();
            if (allExpressionsSoFar) {
                if (fromParsePropertyDefinition.isLeft()) {
                    objectProperties.add(fromParsePropertyDefinition.left().fromJust());
                } else {
                    allExpressionsSoFar = false;
                    for (ObjectProperty objectProperty : objectProperties) {
                        bindingProperties.add(this.transformDestructuring(objectProperty));
                    }
                    bindingProperties.add(fromParsePropertyDefinition.right().fromJust());
                }
            } else {
                if (fromParsePropertyDefinition.isLeft()) {
                    bindingProperties.add(this.transformDestructuring(fromParsePropertyDefinition.left().fromJust()));
                } else {
                    bindingProperties.add(fromParsePropertyDefinition.right().fromJust());
                }
            }
            if (!this.match(TokenType.RBRACE)) {
                this.expect(TokenType.COMMA);
            }
        }
        this.expect(TokenType.RBRACE);
        if (allExpressionsSoFar) {
            return Either.left(this.finishNode(startState, new ObjectExpression(ImmutableList.from(objectProperties))));
        } else {
            return Either.right(this.finishNode(startState, new ObjectAssignmentTarget(ImmutableList.from(bindingProperties))));
        }
    }

    @NotNull // todo move this
    protected BindingWithDefault targetToBindingWithDefault(@NotNull AssignmentTargetWithDefault target) throws JsError {
        return this.copyNode(target, new BindingWithDefault(targetToBinding(target.binding), target.init));
    }

    @NotNull
    protected Binding targetToBinding(@NotNull AssignmentTarget target) throws JsError {
        if (target instanceof AssignmentTargetIdentifier) {
            return this.copyNode(target, new BindingIdentifier(((AssignmentTargetIdentifier) target).name));
        } else if (target instanceof MemberAssignmentTarget) { // TODO correct location information for this error (ugh)
            throw this.createError(this.match(TokenType.ASSIGN) ? ErrorMessages.INVALID_LHS_IN_ASSIGNMENT : ErrorMessages.ILLEGAL_ARROW_FUNCTION_PARAMS); // TODO correct error message
        } else if (target instanceof ArrayAssignmentTarget) {
            ArrayAssignmentTarget aat = (ArrayAssignmentTarget) target;
            // can't do this as a lambda because lambdas can't throw...
            // return new ArrayBinding(aat.elements.map(x -> x.map(this::targetToBindingPossiblyWithDefault)), aat.rest.map(this::targetToBinding));
            ArrayList<Maybe<BindingBindingWithDefault>> elements = new ArrayList<>();
            for (Maybe<AssignmentTargetAssignmentTargetWithDefault> elementOrElison : aat.elements) {
                if (elementOrElison.isJust()) {
                    elements.add(Maybe.of(this.targetToBindingPossiblyWithDefault(elementOrElison.fromJust())));
                } else {
                    elements.add(Maybe.empty());
                }
            }
            Maybe<Binding> rest;
            if (aat.rest.isJust()) {
                rest = Maybe.of(this.targetToBinding(aat.rest.fromJust()));
            } else {
                rest = Maybe.empty();
            }
            return this.copyNode(target, new ArrayBinding(ImmutableList.from(elements), rest));
        } else {
            ObjectAssignmentTarget oat = (ObjectAssignmentTarget) target;
            ArrayList<BindingProperty> properties = new ArrayList<>();
            for (AssignmentTargetProperty prop : oat.properties) {
                if (prop instanceof AssignmentTargetPropertyIdentifier) {
                    AssignmentTargetPropertyIdentifier atpi = (AssignmentTargetPropertyIdentifier) prop;
                    properties.add(this.copyNode(atpi, new BindingPropertyIdentifier(this.copyNode(atpi.binding, new BindingIdentifier(atpi.binding.name)), atpi.init)));
                } else {
                    AssignmentTargetPropertyProperty atpp = (AssignmentTargetPropertyProperty) prop;
                    properties.add(this.copyNode(atpp, new BindingPropertyProperty(atpp.name, this.targetToBindingPossiblyWithDefault(atpp.binding))));
                }
            }
            return this.copyNode(target, new ObjectBinding(ImmutableList.from(properties)));
        }
    }

    @NotNull
    protected BindingBindingWithDefault targetToBindingPossiblyWithDefault(@NotNull AssignmentTargetAssignmentTargetWithDefault target) throws JsError {
        if (target instanceof AssignmentTargetWithDefault) {
            return targetToBindingWithDefault((AssignmentTargetWithDefault) target);
        } else {
            return targetToBinding((AssignmentTarget) target);
        }
    }

    @NotNull
    protected Either<Expression, AssignmentTarget> parseGroupExpression() throws JsError {
        AdditionalStateT startState = this.startNode();
        SourceLocation startLocation = this.getLocation();

        this.expect(TokenType.LPAREN);
        if (this.eat(TokenType.RPAREN)) {
            this.isBindingElement = this.isAssignmentTarget = false;
            return Either.left(this.parseArrowExpressionTail(new ArrayList<>(), Maybe.empty(), startState));
        } else if (this.eat(TokenType.ELLIPSIS)) {
            Maybe<Binding> rest = Maybe.of(this.parseBindingTarget());
            this.expect(TokenType.RPAREN);
            this.isBindingElement = this.isAssignmentTarget = false;
            return Either.left(this.parseArrowExpressionTail(new ArrayList<>(), rest, startState));
        }

        Either<Expression, AssignmentTarget> group = this.inheritCoverGrammar(this::parseAssignmentExpressionOrTarget);

        ArrayList<BindingBindingWithDefault> params = new ArrayList<>();
        if (this.isBindingElement) {
            if (group.isLeft()) {
                params.add(this.targetToBindingPossiblyWithDefault(this.transformDestructuringWithDefault(group.left().fromJust())));
            } else {
                params.add(this.targetToBinding(group.right().fromJust()));
            }
        }

        boolean mustBeArrowParameterList = false;

        while (this.eat(TokenType.COMMA)) {
            this.isAssignmentTarget = false;
            if (this.match(TokenType.ELLIPSIS)) {
                if (!this.isBindingElement) {
                    throw this.createUnexpected(this.lookahead);
                }
                this.lex();
                Maybe<Binding> rest = Maybe.of(this.parseBindingTarget());
                this.expect(TokenType.RPAREN);
                return Either.left(this.parseArrowExpressionTail(params, rest, startState));
            }

            if (mustBeArrowParameterList) {
                // Can be only binding elements.
                BindingBindingWithDefault binding = this.parseBindingElement();
                params.add(binding);
            } else {
                // Can be either binding element or assignment target.
                Either<Expression, AssignmentTarget> expr = this.inheritCoverGrammar(this::parseAssignmentExpressionOrTarget);
                if (this.isBindingElement) {
                    if (expr.isLeft()) {
                        params.add(this.targetToBindingPossiblyWithDefault(this.transformDestructuringWithDefault(expr.left().fromJust())));
                    } else {
                        params.add(this.targetToBinding(expr.right().fromJust()));
                    }
                }
                if (this.firstExprError == null) {
                    group = Either.left(this.finishNode(startState, new BinaryExpression(group.left().fromJust(), BinaryOperator.Sequence, expr.left().fromJust())));
                } else {
                    mustBeArrowParameterList = true;
                }
            }
        }

        this.expect(TokenType.RPAREN);

        if (!this.hasLineTerminatorBeforeNext && this.match(TokenType.ARROW) || mustBeArrowParameterList) {
            if (!this.isBindingElement) {
                throw this.createErrorWithLocation(startLocation, this.match(TokenType.ASSIGN) ? ErrorMessages.INVALID_LHS_IN_ASSIGNMENT : ErrorMessages.ILLEGAL_ARROW_FUNCTION_PARAMS);
            }
            this.isBindingElement = false;
            return Either.left(this.parseArrowExpressionTail(params, Maybe.empty(), startState));
        } else {
            // Ensure assignment pattern:
            this.isBindingElement = false;
            if (!isValidSimpleAssignmentTarget(group.either(x -> x, x -> x))) {
                this.isAssignmentTarget = false;
            }
            return group;
        }
    }

    @NotNull
    protected Either<ObjectProperty, AssignmentTargetProperty> parsePropertyDefinition() throws JsError {
        AdditionalStateT startState = this.startNode();
        SourceLocation startLocation = this.getLocation();
        Token token = this.lookahead;

        Either<PropertyName, MethodDefinition> keyOrMethod = this.parseMethodDefinition();

        if (keyOrMethod.isRight()) {
            this.isBindingElement = this.isAssignmentTarget = false;
            return Either.left(keyOrMethod.right().fromJust());
        } else if (keyOrMethod.isLeft()) {
            PropertyName propName = keyOrMethod.left().fromJust();
            if (propName instanceof StaticPropertyName) {
                StaticPropertyName staticPropertyName = (StaticPropertyName) propName;
                if (this.eat(TokenType.ASSIGN)) {
                    Expression init = this.isolateCoverGrammar(this::parseAssignmentExpression).left().fromJust();
                    this.firstExprError = this.createErrorWithLocation(startLocation, ErrorMessages.ILLEGAL_PROPERTY);
                    AssignmentTargetPropertyIdentifier toReturn = new AssignmentTargetPropertyIdentifier(this.transformDestructuring(staticPropertyName), Maybe.of(init));
                    return Either.right(this.finishNode(startState, toReturn));
                }
                if (!this.match(TokenType.COLON)) {
                    if (token.type != TokenType.IDENTIFIER && token.type != TokenType.YIELD && token.type != TokenType.LET) {
                        throw this.createUnexpected(token);
                    }
                    ShorthandProperty toReturn = new ShorthandProperty(this.finishNode(startState, new IdentifierExpression(staticPropertyName.value)));
                    return Either.left(this.finishNode(startState, toReturn));
                }
            }
        }

        this.expect(TokenType.COLON);

        PropertyName name = keyOrMethod.left().fromJust();
        Either<Expression, AssignmentTarget> val = this.parseAssignmentExpressionOrTarget();

        return val.map(
                expr -> this.finishNode(startState, new DataProperty(name, expr)),
                binding -> this.finishNode(startState, new AssignmentTargetPropertyProperty(name, binding))
        );
    }

    @NotNull
    protected Either<PropertyName, MethodDefinition> parseMethodDefinition() throws JsError {
        Token token = this.lookahead;
        AdditionalStateT startState = this.startNode();

        boolean isGenerator = this.eat(TokenType.MUL);

        Pair<PropertyName, Maybe<Binding>> fromParsePropertyName = this.parsePropertyName();
        PropertyName name = fromParsePropertyName.left();

        if (!isGenerator && token.type == TokenType.IDENTIFIER) {
            String tokenName = token.toString();
            if (tokenName.length() == 3) {
                // Property Assignment: Getter and Setter.
                if (tokenName.equals("get") && this.lookaheadPropertyName()) {
                    name = this.parsePropertyName().left();
                    this.expect(TokenType.LPAREN);
                    this.expect(TokenType.RPAREN);
                    FunctionBody body = this.parseFunctionBody();
                    return Either.right(this.finishNode(startState, new Getter(name, body)));
                } else if (tokenName.equals("set") && this.lookaheadPropertyName()) {
                    name = this.parsePropertyName().left();
                    this.expect(TokenType.LPAREN);
                    BindingBindingWithDefault param = this.parseBindingElement();
                    this.expect(TokenType.RPAREN);
                    boolean previousYield = this.allowYieldExpression;
                    this.allowYieldExpression = false;
                    FunctionBody body = this.parseFunctionBody();
                    this.allowYieldExpression = previousYield;
                    return Either.right(this.finishNode(startState, new Setter(name, bindingToParameter(param), body)));
                }
            }
        }

        if (this.match(TokenType.LPAREN)) {
            boolean previousYield = this.allowYieldExpression;
            this.allowYieldExpression = isGenerator;
            FormalParameters params = this.parseParams();
            this.allowYieldExpression = isGenerator;

            FunctionBody body = this.parseFunctionBody();
            this.allowYieldExpression = previousYield;

            return Either.right(this.finishNode(startState, new Method(isGenerator, name, params, body)));
        }

        if (isGenerator && this.match(TokenType.COLON)) {
            throw this.createUnexpected(this.lookahead);
        }

        return Either.left(name);
    }

    protected boolean lookaheadPropertyName() {
        switch (this.lookahead.type) {
            case NUMBER:
            case STRING:
            case LBRACK:
                return true;
            default:
                return isIdentifierName(this.lookahead.type.klass);
        }
    }

    @NotNull
    protected Pair<PropertyName, Maybe<Binding>> parsePropertyName() throws JsError {
        Token token = this.lookahead;
        AdditionalStateT startState = this.startNode();

        if (this.eof()) {
            throw this.createUnexpected(token);
        }

        switch (token.type) {
            case STRING:
                String stringValue = ((LiteralStringExpression) this.parseStringLiteral()).value;
                return new Pair<>(this.finishNode(startState, new StaticPropertyName(stringValue)), Maybe.empty());
            case NUMBER:
                Expression numLiteral = this.parseNumericLiteral();
                if (numLiteral instanceof LiteralInfinityExpression) {
                    return new Pair<>(this.finishNode(startState, new StaticPropertyName("Infinity")), Maybe.empty());
                } else {
                    double value = ((LiteralNumericExpression) numLiteral).value;

                    return new Pair<>(this.finishNode(startState, new StaticPropertyName(D2A.d2a(value))), Maybe.empty());
                }
            case LBRACK:
                boolean previousYield = this.allowYieldExpression;
                this.lex();
                Expression expr = this.parseAssignmentExpression().left().fromJust();
                this.expect(TokenType.RBRACK);
                this.allowYieldExpression = previousYield;
                return new Pair<>(this.finishNode(startState, new ComputedPropertyName(expr)), Maybe.empty());
        }

        AdditionalStateT bindingIdentifierStart = this.startNode();
        String name = this.parseIdentifierName();
        Maybe<Binding> maybeBinding = Maybe.of(this.finishNode(bindingIdentifierStart, new BindingIdentifier(name)));
        return new Pair<>(this.finishNode(startState, new StaticPropertyName(name)), maybeBinding);
    }

    @NotNull
    protected String parseIdentifierName() throws JsError {
        if (isIdentifierName(this.lookahead.type.klass)) {
            return this.lex().toString();
        } else {
            throw this.createUnexpected(this.lookahead);
        }
    }

    public static boolean isIdentifierName(TokenClass klass) {
        return (klass.getName().equals("Identifier") || klass.getName().equals("Keyword") || klass.getName().equals("Boolean") || klass.getName().equals("Null") || klass.getName().equals("Yield"));
    }

    @NotNull
    protected Expression parseClass() throws JsError {
        AdditionalStateT startState = this.startNode();

        this.lex();
        Maybe<BindingIdentifier> name = Maybe.empty();
        Maybe<Expression> heritage = Maybe.empty();

        if (this.match(TokenType.IDENTIFIER)) {
            name = Maybe.of(this.parseBindingIdentifier());
        }

        boolean previousParamYield = this.allowYieldExpression;
        this.allowYieldExpression = false;
        if (this.eat(TokenType.EXTENDS)) {
            Either<ExpressionSuper, AssignmentTarget> fromParseLeftHandSideExpression = this.isolateCoverGrammar(() -> this.parseLeftHandSideExpression(true));
            if (fromParseLeftHandSideExpression.isLeft()) {
                heritage = Maybe.of((Expression) fromParseLeftHandSideExpression.left().fromJust());
            } else {
                throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
            }
        }

        this.expect(TokenType.LBRACE);
        ArrayList<ClassElement> elements = new ArrayList<>();
        while (!this.eat(TokenType.RBRACE)) {
            if (this.eat(TokenType.SEMICOLON)) {
                continue;
            }
            boolean isStatic = false;
            AdditionalStateT classElementStart = this.startNode();
            Either<PropertyName, MethodDefinition> methodOrKey = this.parseMethodDefinition();
            if (methodOrKey.isLeft() && methodOrKey.left().fromJust() instanceof StaticPropertyName && ((StaticPropertyName) methodOrKey.left().fromJust()).value.equals("static")) {
                isStatic = true;
                methodOrKey = this.parseMethodDefinition();
            }
            if (methodOrKey.isRight()) {
                elements.add(this.finishNode(classElementStart, new ClassElement(isStatic, methodOrKey.right().fromJust())));
            } else {
                throw this.createError("Only methods are allowed in classes");
            }
        }
        this.allowYieldExpression = previousParamYield;
        return this.finishNode(startState, new ClassExpression(name, heritage, ImmutableList.from(elements)));
    }

    @NotNull
    protected ClassDeclaration parseClass(boolean inDefault) throws JsError {
        AdditionalStateT startState = this.startNode();

        this.lex();
        Maybe<BindingIdentifier> name;
        Maybe<Expression> heritage = Maybe.empty();

        if (this.match(TokenType.IDENTIFIER)) {
            name = Maybe.of(this.parseBindingIdentifier());
        } else {
            if (inDefault) {
                name = Maybe.of(this.finishNode(startState, new BindingIdentifier("*default*")));
            } else {
                throw this.createUnexpected(this.lookahead);
            }
        }

        boolean previousParamYield = this.allowYieldExpression;

        if (this.eat(TokenType.EXTENDS)) {
            Either<ExpressionSuper, AssignmentTarget> fromParseLeftHandSideExpression = this.isolateCoverGrammar(() -> this.parseLeftHandSideExpression(true));
            if (fromParseLeftHandSideExpression.isLeft()) {
                heritage = Maybe.of((Expression) fromParseLeftHandSideExpression.left().fromJust());
            } else {
                throw this.createError(ErrorMessages.UNEXPECTED_OBJECT_BINDING);
            }
        }

        this.expect(TokenType.LBRACE);
        ArrayList<ClassElement> elements = new ArrayList<>();
        while (!this.eat(TokenType.RBRACE)) {
            if (this.eat(TokenType.SEMICOLON)) {
                continue;
            }
            boolean isStatic = false;
            AdditionalStateT classElementStart = this.startNode();
            Either<PropertyName, MethodDefinition> methodOrKey = this.parseMethodDefinition();
            if (methodOrKey.isLeft() && ((StaticPropertyName) methodOrKey.left().fromJust()).value.equals("static")) {
                isStatic = true;
                methodOrKey = this.parseMethodDefinition();
            }
            if (methodOrKey.isRight()) {
                elements.add(this.finishNode(classElementStart, new ClassElement(isStatic, methodOrKey.right().fromJust())));
            } else {
                throw this.createError("Only methods are allowed in classes");
            }
        }
        this.allowYieldExpression = previousParamYield;
        return this.finishNode(startState, new ClassDeclaration(name.fromJust(), heritage, ImmutableList.from(elements)));
    }

    @NotNull
    protected ImportDeclaration parseImportDeclaration() throws JsError {
        AdditionalStateT startState = this.startNode();
        Maybe<BindingIdentifier> defaultBinding = Maybe.empty();
        String moduleSpecifier;

        this.expect(TokenType.IMPORT);

        switch (this.lookahead.type) {
            case STRING:
                moduleSpecifier = this.lex().getValueString().toString();
                this.consumeSemicolon();
                return this.finishNode(startState, new Import(defaultBinding, ImmutableList.empty(), moduleSpecifier));
            case IDENTIFIER:
            case YIELD:
            case LET:
                defaultBinding = Maybe.of(this.parseBindingIdentifier());
                if (!this.eat(TokenType.COMMA)) {
                    Import decl = new Import(defaultBinding, ImmutableList.empty(), this.parseFromClause());
                    this.consumeSemicolon();
                    return this.finishNode(startState, decl);
                }
                break;
        }
        if (this.match(TokenType.MUL)) {
            ImportNamespace decl = new ImportNamespace(defaultBinding, this.parseNameSpaceBinding(), this.parseFromClause());
            this.consumeSemicolon();
            return this.finishNode(startState, decl);
        } else if (this.match(TokenType.LBRACE)) {
            Import decl = this.finishNode(startState, new Import(defaultBinding, this.parseNamedImports(), this.parseFromClause()));
            this.consumeSemicolon();
            return this.finishNode(startState, decl);
        } else {
            throw this.createUnexpected(this.lookahead);
        }
    }

    @NotNull
    protected ImmutableList<ImportSpecifier> parseNamedImports() throws JsError {
        ArrayList<ImportSpecifier> result = new ArrayList<>();
        this.expect(TokenType.LBRACE);
        while (!this.eat(TokenType.RBRACE)) {
            result.add(this.parseImportSpecifier());
            if (!this.eat(TokenType.COMMA)) {
                this.expect(TokenType.RBRACE);
                break;
            }
        }
        return ImmutableList.from(result);
    }

    @NotNull
    protected ImportSpecifier parseImportSpecifier() throws JsError {
        AdditionalStateT startState = this.startNode();
        Maybe<String> name = Maybe.empty();

        if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
            name = Maybe.of(this.parseIdentifier());
            if (this.eatContextualKeyword("as") == null) {
                return this.finishNode(startState, new ImportSpecifier(Maybe.empty(), this.finishNode(startState, new BindingIdentifier(name.fromJust()))));
            }
        } else if (isIdentifierName(this.lookahead.type.klass)) {
            name = Maybe.of(this.parseIdentifierName());
            this.expectContextualKeyword("as");
        }

        return this.finishNode(startState, new ImportSpecifier(name, this.parseBindingIdentifier()));
    }

    @Nullable
    protected Token eatContextualKeyword(String keyword) throws JsError {
        if (this.lookahead.type == TokenType.IDENTIFIER && this.lookahead.toString().equals(keyword)) {
            return this.lex();
        } else {
            return null;
        }
    }

    @NotNull
    protected BindingIdentifier parseNameSpaceBinding() throws JsError {
        this.expect(TokenType.MUL);
        this.expectContextualKeyword("as");
        return this.parseBindingIdentifier();
    }

    @NotNull
    protected String parseFromClause() throws JsError {
        this.expectContextualKeyword("from");
        String value = this.expect(TokenType.STRING).getValueString().toString();
        return value;
    }

    @NotNull
    protected Token expectContextualKeyword(String keyword) throws JsError {
        if (this.lookahead.type == TokenType.IDENTIFIER && this.lookahead.toString().equals(keyword)) {
            return this.lex();
        } else {
            throw this.createUnexpected(this.lookahead);
        }
    }

    @NotNull
    protected ExportDeclaration parseExportDeclaration() throws JsError {
        AdditionalStateT startState = this.startNode();
        ExportDeclaration decl;
        this.expect(TokenType.EXPORT);
        switch (this.lookahead.type) {
            case MUL:
                this.lex();
                decl = new ExportAllFrom(this.parseFromClause());
                this.consumeSemicolon();
                break;
            case LBRACE:
                Pair<ImmutableList<ExportFromSpecifier>, ImmutableList<ExportLocalSpecifier>> namedExports = this.parseExportClause();
                if (this.matchContextualKeyword("from")) {
                    decl = new ExportFrom(namedExports.left(), this.parseFromClause());
                } else {
                    decl = new ExportLocals(namedExports.right());
                }
                this.consumeSemicolon();
                break;
            case CLASS:
                decl = new Export(this.parseClass(false));
                break;
            case FUNCTION:
                decl = new Export((FunctionDeclaration) this.parseFunctionDeclaration(false, true));
                break;
            case DEFAULT:
                this.lex();
                switch (this.lookahead.type) {
                    case FUNCTION:
                        decl = new ExportDefault((FunctionDeclaration) this.parseFunctionDeclaration(true, true));
                        break;
                    case CLASS:
                        decl = new ExportDefault(this.parseClass(true));
                        break;
                    default:
                        decl = new ExportDefault(this.parseAssignmentExpression().left().fromJust());
                        this.consumeSemicolon();
                        break;
                }
                break;
            case VAR:
            case LET:
            case CONST:
                decl = new Export(this.parseVariableDeclaration(true));
                this.consumeSemicolon();
                break;
            default:
                throw this.createUnexpected(this.lookahead);
        }
        return this.finishNode(startState, decl);
    }

    @NotNull
    protected Pair<ImmutableList<ExportFromSpecifier>, ImmutableList<ExportLocalSpecifier>> parseExportClause() throws JsError {
        this.expect(TokenType.LBRACE);
        ArrayList<ExportFromSpecifier> exportFromSpecifiers = new ArrayList<>();
        ArrayList<ExportLocalSpecifier> exportLocalSpecifiers = new ArrayList<>();
        while (!this.eat(TokenType.RBRACE)) {
            Pair<ExportFromSpecifier, ExportLocalSpecifier> exportSpecifiers = this.parseExportSpecifier();
            exportFromSpecifiers.add(exportSpecifiers.left());
            exportLocalSpecifiers.add(exportSpecifiers.right());
            if (!this.eat(TokenType.COMMA)) {
                this.expect(TokenType.RBRACE);
                break;
            }
        }
        return new Pair<>(ImmutableList.from(exportFromSpecifiers), ImmutableList.from(exportLocalSpecifiers));
    }

    @NotNull
    protected Pair<ExportFromSpecifier, ExportLocalSpecifier> parseExportSpecifier() throws JsError {
        AdditionalStateT startState = this.startNode();
        String name = this.parseIdentifierName();
        IdentifierExpression identifierExpression = this.finishNode(startState, new IdentifierExpression(name));
        ExportFromSpecifier exportFromSpecifier;
        ExportLocalSpecifier exportLocalSpecifier;
        if (this.eatContextualKeyword("as") != null) {
            String exportedName = this.parseIdentifierName();
            exportFromSpecifier = this.finishNode(startState, new ExportFromSpecifier(name, Maybe.of(exportedName)));
            exportLocalSpecifier = this.finishNode(startState, new ExportLocalSpecifier(identifierExpression, Maybe.of(exportedName)));
        } else {
            exportFromSpecifier = this.finishNode(startState, new ExportFromSpecifier(name, Maybe.empty()));
            exportLocalSpecifier = this.finishNode(startState, new ExportLocalSpecifier(identifierExpression, Maybe.empty()));
        }
        return new Pair<>(exportFromSpecifier, exportLocalSpecifier);
    }

    @Nullable
    public static CompoundAssignmentOperator lookupCompoundAssignmentOperator(@NotNull Token token) {
        switch (token.type) {
            case ASSIGN_BIT_OR:
                return CompoundAssignmentOperator.AssignBitOr;
            case ASSIGN_BIT_XOR:
                return CompoundAssignmentOperator.AssignBitXor;
            case ASSIGN_BIT_AND:
                return CompoundAssignmentOperator.AssignBitAnd;
            case ASSIGN_SHL:
                return CompoundAssignmentOperator.AssignLeftShift;
            case ASSIGN_SHR:
                return CompoundAssignmentOperator.AssignRightShift;
            case ASSIGN_SHR_UNSIGNED:
                return CompoundAssignmentOperator.AssignUnsignedRightShift;
            case ASSIGN_ADD:
                return CompoundAssignmentOperator.AssignPlus;
            case ASSIGN_SUB:
                return CompoundAssignmentOperator.AssignMinus;
            case ASSIGN_MUL:
                return CompoundAssignmentOperator.AssignMul;
            case ASSIGN_DIV:
                return CompoundAssignmentOperator.AssignDiv;
            case ASSIGN_MOD:
                return CompoundAssignmentOperator.AssignRem;
            case ASSIGN_EXP:
                return CompoundAssignmentOperator.AssignExp;
            default:
                return null; // should not happen
        }
    }

    @Nullable
    public static BinaryOperator lookupBinaryOperator(@NotNull Token token, boolean allowIn) {
        switch (token.type) {
            case OR:
                return BinaryOperator.LogicalOr;
            case AND:
                return BinaryOperator.LogicalAnd;
            case BIT_OR:
                return BinaryOperator.BitwiseOr;
            case BIT_XOR:
                return BinaryOperator.BitwiseXor;
            case BIT_AND:
                return BinaryOperator.BitwiseAnd;
            case EQ:
                return BinaryOperator.Equal;
            case NE:
                return BinaryOperator.NotEqual;
            case EQ_STRICT:
                return BinaryOperator.StrictEqual;
            case NE_STRICT:
                return BinaryOperator.StrictNotEqual;
            case LT:
                return BinaryOperator.LessThan;
            case GT:
                return BinaryOperator.GreaterThan;
            case LTE:
                return BinaryOperator.LessThanEqual;
            case GTE:
                return BinaryOperator.GreaterThanEqual;
            case INSTANCEOF:
                return BinaryOperator.Instanceof;
            case IN:
                return allowIn ? BinaryOperator.In : null;
            case SHL:
                return BinaryOperator.Left;
            case SHR:
                return BinaryOperator.Right;
            case SHR_UNSIGNED:
                return BinaryOperator.UnsignedRight;
            case ADD:
                return BinaryOperator.Plus;
            case SUB:
                return BinaryOperator.Minus;
            case MUL:
                return BinaryOperator.Mul;
            case DIV:
                return BinaryOperator.Div;
            case MOD:
                return BinaryOperator.Rem;
        }
        return null;
    }

    public static boolean isPrefixOperator(Token token) {
        switch (token.type) {
            case INC:
            case DEC:
            case ADD:
            case SUB:
            case BIT_NOT:
            case NOT:
            case DELETE:
            case VOID:
            case TYPEOF:
                return true;
        }
        return false;
    }

    @Nullable
    public static UnaryOperator lookupUnaryOperator(Token token) {
        switch (token.type) {
            case ADD:
                return UnaryOperator.Plus;
            case SUB:
                return UnaryOperator.Minus;
            case BIT_NOT:
                return UnaryOperator.BitNot;
            case NOT:
                return UnaryOperator.LogicalNot;
            case DELETE:
                return UnaryOperator.Delete;
            case VOID:
                return UnaryOperator.Void;
            case TYPEOF:
                return UnaryOperator.Typeof;
        }
        return null;
    }

    @Nullable
    public static UpdateOperator lookupUpdateOperator(Token token) {
        switch (token.type) {
            case INC:
                return UpdateOperator.Increment;
            case DEC:
                return UpdateOperator.Decrement;
        }
        return null;
    }

    protected static class ExprStackItem<T> {
        final T startState;
        @NotNull
        final Expression left;
        @NotNull
        final BinaryOperator operator;
        final int precedence;

        ExprStackItem(@NotNull T startState, @NotNull Expression left, @NotNull BinaryOperator operator) {
            this.startState = startState;
            this.left = left;
            this.operator = operator;
            this.precedence = operator.getPrecedence().ordinal();
        }
    }
}
