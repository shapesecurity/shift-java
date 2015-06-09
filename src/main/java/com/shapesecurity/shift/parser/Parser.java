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

import com.shapesecurity.functional.Thunk;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiFunction;

public class Parser extends Tokenizer {
  @NotNull
  private HashSet<String> labelSet = new HashSet<>();
  private boolean inIteration;
  private boolean inSwitch;
  private boolean inFunctionBody;
  private boolean module;
  private boolean allowIn = true;
  private boolean isBindingElement;
  private boolean isAssignmentTarget;
  private boolean firstExprError;
  private boolean inGeneratorParameter;
  private boolean allowYieldExpression;
  private boolean inParameter = false;

  private Parser(@NotNull String source) throws JsError {
    super(source);
  }

  @Nullable
  private static UnaryOperator lookupPrefixOperator(@NotNull TokenType type) {
    switch (type) {
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
      default:
        return null;
    }
  }

  @Nullable
  private static UpdateOperator lookupPostfixOperator(@NotNull TokenType type) {
    switch (type) {
      case INC:
        return UpdateOperator.Increment;
      case DEC:
        return UpdateOperator.Decrement;
      default:
        return null;
    }
  }


  /*
  @NotNull
  public static Script parseWithLocation(@NotNull String text) throws JsError {
    return new Parser(text) {
      @NotNull
      @Override
      protected <T extends Node> T markLocation(@NotNull SourceLocation startLocation, @NotNull T node) {
        int start = startLocation.offset;
        ((Located) node).setLoc(startLocation.withSourceRange(this.getSliceBeforeLookahead(start)));
        return node;
      }
    }.parseScript();
  }
  */


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

  private boolean match(@NotNull TokenType subType) {
    return this.lookahead.type == subType;
  }

  private void consumeSemicolon() throws JsError {
    // Catch the very common case first: immediately a semicolon (U+003B).
    if (this.hasLineTerminatorBeforeNext) {
      return;
    }
    if (this.eat(TokenType.SEMICOLON)) {
      return;
    }
    if (!this.eof() && !this.match(TokenType.RBRACE)) {
      throw this.createUnexpected(this.lookahead);
    }
  }

  @NotNull
  protected <T extends Node> T markLocation(@NotNull SourceLocation startLocation, @NotNull T node) {
    return node;
  }

  private boolean lookaheadLexicalDeclaration() throws JsError {
    if (this.match(TokenType.LET) || this.match(TokenType.CONST)) {
//      lexerState = this.saveLexerState();
      this.lex();
      if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.LET) || this.match(TokenType.LBRACE) || this.match(TokenType.LBRACK)) {
//        this.restoreLexerState(lexerState);
        return true;
      } else {
//        this.restoreLexerState(lexerState);
      }
    }
    return false;
  }

  @NotNull
  public static Script parseScript(@NotNull String text) throws JsError {
    return new Parser(text).parseScript();
  }

  @NotNull
  public static Module parseModule(@NotNull String text) throws JsError {
    return new Parser(text).parseModule();
  }

  @NotNull
  private Script parseScript() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Script node = this.parseBody(this::parseStatementListItem, Script::new);
    if (!this.match(TokenType.EOS)) {
      throw this.createUnexpected(this.lookahead);
    }
    return this.markLocation(startLocation, node);
  }


  @NotNull
  private Module parseModule() throws JsError {
    return this.markLocation(this.getLocation(), this.parseBody(this::parseModuleItem, Module::new));
  }

  private ImportDeclarationExportDeclarationStatement parseModuleItem() throws JsError {
    return this.parseStatementListItem();
  }

  @FunctionalInterface
  private static interface ExceptionalSupplier<A> {
    A get() throws JsError;
  }

  @NotNull
  private <A, B> B parseBody(@NotNull ExceptionalSupplier<A> parser, @NotNull BiFunction<ImmutableList<Directive>, ImmutableList<A>, B> constructor) throws JsError {
    ArrayList<Directive> directives = new ArrayList<>();
    ArrayList<A> statements = new ArrayList<>();
    boolean parsingDirectives = true;

    while (true) {
      if (this.eof() || this.match(TokenType.RBRACE)) {
        break;
      }

      Token token = this.lookahead;
      String text = token.slice.toString();
      boolean isStringLiteral = token.type == TokenType.STRING;
      SourceLocation directiveLocation = this.getLocation();
      A stmt = parser.get();

      if (parsingDirectives) {
        if (isStringLiteral && stmt.getClass() == ExpressionStatement.class && ((ExpressionStatement) stmt).expression.getClass() == LiteralStringExpression.class) {
          directives.add(this.markLocation(directiveLocation, new Directive(text.substring(1, -1))));
        } else {
          parsingDirectives = false;
          statements.add(stmt);
        }
      } else {
        statements.add(stmt);
      }
    }

    return constructor.apply(ImmutableList.from(directives), ImmutableList.from(statements));
  }

  private FunctionBody parseFunctionBody() throws JsError {
    SourceLocation startLocation = this.getLocation();
    boolean oldInFunctionBody = this.inFunctionBody;
    boolean oldModule = this.module;
    this.inFunctionBody = true;
    this.module = false;

    this.expect(TokenType.LBRACE);
    FunctionBody body = this.parseBody(this::parseStatementListItem, FunctionBody::new);
    this.expect(TokenType.RBRACE);

    this.inFunctionBody = oldInFunctionBody;
    this.module = oldModule;

    return this.markLocation(startLocation, body);
  }

  private Statement parseStatementListItem() throws JsError {
    if (this.eof()) {
      throw this.createUnexpected(this.lookahead);
    }
    //TODO: rest of the function
    switch (this.lookahead.type) {
      default:
        if (this.lookaheadLexicalDeclaration()) {
          SourceLocation startLocation = this.getLocation();
          return this.markLocation(startLocation, this.parseVariableDeclarationStatement());
        } else {
          return this.parseStatement();
        }
    }
  }

  private Statement parseVariableDeclarationStatement() throws JsError {
    VariableDeclaration declaration = this.parseVariableDeclaration(true);
    this.consumeSemicolon();
    return new VariableDeclarationStatement(declaration);
  }

  private VariableDeclaration parseVariableDeclaration(boolean bindingPatternsMustHaveInit) throws JsError {
    SourceLocation startLocation = this.getLocation();
    Token token = this.lex();
    VariableDeclarationKind kind = token.type == TokenType.VAR ? VariableDeclarationKind.Var :
        token.type == TokenType.CONST ? VariableDeclarationKind.Const : VariableDeclarationKind.Let;
    ImmutableList<VariableDeclarator> declarators = this.parseVariableDeclaratorList(bindingPatternsMustHaveInit);
    return this.markLocation(startLocation, new VariableDeclaration(kind, declarators));

  }

  private ImmutableList<VariableDeclarator> parseVariableDeclaratorList(boolean bindingPatternsMustHaveInit) throws JsError {
    ArrayList<VariableDeclarator> result = new ArrayList();
    do {
      result.add(this.parseVariableDeclarator(bindingPatternsMustHaveInit));
    } while (this.eat(TokenType.COMMA));
    return ImmutableList.from(result);
  }

  private VariableDeclarator parseVariableDeclarator(boolean bindingPatternsMustHaveInit) throws JsError {
    SourceLocation startLocation = this.getLocation();

    if (this.match(TokenType.LPAREN)) {
      throw this.createUnexpected(this.lookahead);
    }
    Binding binding = this.parseBindingTarget();
    if (bindingPatternsMustHaveInit && binding.getClass() != BindingIdentifier.class && !this.match(TokenType.ASSIGN)) {
      this.expect(TokenType.ASSIGN);
    }

    Maybe<Expression> init = Maybe.nothing();
    if (this.eat(TokenType.ASSIGN)) {
      init = Maybe.just(this.parseAssignmentExpression());
    }

    return this.markLocation(startLocation, new VariableDeclarator(binding, init));
  }

  private Binding parseBindingTarget() throws JsError {
    switch (this.lookahead.type) {
      case IDENTIFIER:
      case LET:
//      case YIELD:
        return this.parseBindingIdentifier();
//      case LBRACK:
//        return this.parseArrayBinding();
//      case LBRACE:
//        return this.parseObjectBinding(); // TODO implement this two functions
    }
    throw this.createUnexpected(this.lookahead);
  }

  private BindingIdentifier parseBindingIdentifier() throws JsError {
    SourceLocation startLocation = this.getLocation();
    return this.markLocation(startLocation, new BindingIdentifier(this.parseIdentifier()));
  }

  private String parseIdentifier() throws JsError {
    if (this.match(TokenType.IDENTIFIER)) {
      return this.lex().toString();
    } else {
      throw this.createUnexpected(this.lookahead);
    }
  }

  private Statement parseIfStatement() throws JsError {

    SourceLocation startLocation = this.getLocation();

    this.lex();

    this.expect(TokenType.LPAREN);

    Expression test = this.parseExpression();

    this.expect(TokenType.RPAREN);

    Statement consequent = this.parseIfStatementChild();

    Maybe<Statement> alternate = null;

    if (this.eat(TokenType.ELSE)) {
      alternate = Maybe.fromNullable(this.parseIfStatementChild());
    } else {
      alternate = Maybe.nothing();
    }

    return this.markLocation(startLocation, new IfStatement(test, consequent, alternate));
  }

  private Statement parseIfStatementChild() throws JsError {
    return this.match(TokenType.FUNCTION) ? this.parseFunction(false, false) : this.parseStatement();
  }

  // isExpr is false
  private Statement parseFunction(boolean inDefault, boolean allowGenerator) throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.lex();

    boolean isGenerator = allowGenerator && this.eat(TokenType.MUL);
    boolean previousGeneratorParameter = this.inGeneratorParameter;
    boolean previousYield = this.allowYieldExpression;
    BindingIdentifier name;
    if (!this.match(TokenType.LPAREN)) {
      name = this.parseBindingIdentifier();
    } else if (inDefault) {
      name = this.markLocation(startLocation, new BindingIdentifier("*default*"));
    } else {
      throw this.createUnexpected(this.lookahead);
    }
    this.inGeneratorParameter = isGenerator;
    this.allowYieldExpression = isGenerator;
    FormalParameters params = this.parseParams();
    this.inGeneratorParameter = previousGeneratorParameter;
    this.allowYieldExpression = isGenerator;
    FunctionBody body = this.parseFunctionBody();
    this.allowYieldExpression = previousYield;
    this.inGeneratorParameter = previousGeneratorParameter;
    return this.markLocation(startLocation, new FunctionDeclaration(name, isGenerator, params, body));
  }

  private Expression parseFunction(boolean allowGenerator) throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.lex();

    Maybe<BindingIdentifier> name = Maybe.nothing();
    boolean isGenerator = allowGenerator && this.eat(TokenType.MUL);
    boolean previousGeneratorParameter = this.inGeneratorParameter;
    boolean previousYield = this.allowYieldExpression;
    if (!this.match(TokenType.LPAREN)) {
      name = Maybe.just(this.parseBindingIdentifier());
    }
    this.inGeneratorParameter = isGenerator;
    this.allowYieldExpression = isGenerator;
    FormalParameters params = this.parseParams();
    this.inGeneratorParameter = previousGeneratorParameter;
    this.allowYieldExpression = isGenerator;
    FunctionBody body = this.parseFunctionBody();
    this.allowYieldExpression = previousYield;
    this.inGeneratorParameter = previousGeneratorParameter;
    return this.markLocation(startLocation, new FunctionExpression(name, isGenerator, params, body));
  }

  private FormalParameters parseParams() throws JsError {
    SourceLocation paramsLocation = this.getLocation();
    this.expect(TokenType.LPAREN);
    ArrayList<BindingBindingWithDefault> items = new ArrayList();
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
    return this.markLocation(paramsLocation, new FormalParameters(ImmutableList.from(items), Maybe.fromNullable(rest)));
  }

  private BindingBindingWithDefault parseParam() throws JsError {
    boolean previousInParameter = this.inParameter;
    this.inParameter = true;
    BindingBindingWithDefault param = this.parseBindingElement();
    this.inParameter = previousInParameter;
    return param;
  }

  private BindingBindingWithDefault parseBindingElement() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Binding binding = this.parseBindingTarget();
    if (this.eat(TokenType.ASSIGN)) {
      boolean previousInGeneratorParameter = this.inGeneratorParameter;
      boolean previousYieldExpression = this.allowYieldExpression;
      if (this.inGeneratorParameter) {
        this.allowYieldExpression = false;
      }
      this.inGeneratorParameter = false;
      Expression init = this.parseAssignmentExpression();
      binding = (Binding) this.markLocation(startLocation, new BindingWithDefault(binding, init));
      this.inGeneratorParameter = previousInGeneratorParameter;
      this.allowYieldExpression = previousYieldExpression;
    }
    return binding;
  }

  private Statement parseStatement() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Statement stmt = this.parseStatementHelper();
    return this.markLocation(startLocation, stmt);
  }

  private Statement parseStatementHelper() throws JsError {
    SourceLocation startLocation = this.getLocation();
    if (this.eof()) {
      throw this.createUnexpected(this.lookahead);
    }

    // TODO: rest of function
    switch (this.lookahead.type) {
      case SEMICOLON:
        return this.parseEmptyStatement();
      case LPAREN:
        return this.parseExpressionStatement();
      case LBRACE:
        return this.parseBlockStatement();
      case IF:
        return this.parseIfStatement();
      case WHILE:
        return this.parseWhileStatement();
      case VAR:
        return this.parseVariableDeclarationStatement();
      default: {
        if (this.lookaheadLexicalDeclaration()) {
          throw this.createUnexpected(this.lookahead);
        }
        Expression expr = this.parseExpression();
//        if (expr.getClass() == IdentifierExpression.class && this.eat(TokenType.COLON)) {
//          Statement labeledBody = this.match(TokenType.FUNCTION) ? this.parseFunction(false, false) : this.parseStatement();
//          return new LabeledStatement(expr.toString(), labeledBody);
//        } else {
//          this.consumeSemicolon();
//          return new ExpressionStatement(expr);
//        }
        this.consumeSemicolon();
        return new ExpressionStatement(expr);
      }
    }
  }

  private Statement parseEmptyStatement() throws JsError {
    this.lex();
    return new EmptyStatement();
  }

  private Statement parseWhileStatement() throws JsError {
    this.lex();
    this.expect(TokenType.LPAREN);
    Expression test = this.parseExpression();
    Statement body = this.getIteratorStatementEpilogue();
    return new WhileStatement(test, body);
  }

  private Statement getIteratorStatementEpilogue() throws JsError {
    this.expect(TokenType.RPAREN);
    Statement body = this.parseStatement();
    return body;
  }

  private Statement parseBlockStatement() throws JsError {
    return new BlockStatement(this.parseBlock());
  }

  private Block parseBlock() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.expect(TokenType.LBRACE);
    ArrayList<Statement> body = new ArrayList<>();
    while (!this.match(TokenType.RBRACE)) {
      body.add(parseStatementListItem());
    }
    this.expect(TokenType.RBRACE);
    return this.markLocation(startLocation, new Block(ImmutableList.from(body)));
  }

  private Statement parseExpressionStatement() throws JsError {
    Expression expr = this.parseExpression();
    this.consumeSemicolon();
    return new ExpressionStatement(expr);
  }

  private Expression parseExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Expression left = this.parseAssignmentExpression();
    if (!this.match(TokenType.COMMA)) {
      while (!this.eof()) {
        if (!this.match(TokenType.COMMA)) {
          break;
        }
        this.lex();
        Expression right = this.parseAssignmentExpression();
        left = this.markLocation(startLocation, new BinaryExpression(BinaryOperator.Sequence, left, right));
      }
    }
    return left;
  }

  // TODO: rest of expression hierarchy

  private Expression parseAssignmentExpression() throws JsError {
    return this.parseAssignmentExpressionOrBindingElement();
  }

  private Expression parseAssignmentExpressionOrBindingElement() throws JsError {
    // TODO: rest of function;
    SourceLocation startLocation = this.getLocation();
    Expression expr = this.parseConditionalExpression();
    return expr;
  }

  private Expression parseConditionalExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Expression test = this.parseBinaryExpression();
    // TODO: rest of function
    return test;
  }

  private Expression parseBinaryExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Expression left = this.parseUnaryExpression();

    BinaryOperator operator = this.lookupBinaryOperator(this.lookahead);
    if (operator == null) {
      return left;
    }

    this.isBindingElement = this.isAssignmentTarget = false;

    this.lex();
    ImmutableList<ExprStackItem> stack = ImmutableList.nil();
    stack = stack.cons(new ExprStackItem(startLocation, left, operator));
    startLocation = this.getLocation();
    Expression expr = this.parseUnaryExpression();

    operator = this.lookupBinaryOperator(this.lookahead);
    while (operator != null) {
      Precedence precedence = operator.getPrecedence();
      // Reduce: make a binary expression from the three topmost entries.
      while ((stack.isNotEmpty()) && (precedence.ordinal() <= ((NonEmptyImmutableList<ExprStackItem>) stack).head.precedence)) {
        ExprStackItem stackItem = ((NonEmptyImmutableList<ExprStackItem>) stack).head;
        BinaryOperator stackOperator = stackItem.operator;
        left = stackItem.left;
        stack = ((NonEmptyImmutableList<ExprStackItem>) stack).tail();
        startLocation = stackItem.startLocation;
        expr = this.markLocation(stackItem.startLocation, new BinaryExpression(stackOperator, left, expr));
      }

      // Shift.
      this.lex();
      stack = stack.cons(new ExprStackItem(startLocation, expr, operator));
      startLocation = this.getLocation();
      expr = this.parseUnaryExpression();

      operator = this.lookupBinaryOperator(this.lookahead);
    }

    // Final reduce to clean-up the stack.
    return stack.foldLeft(
        (expr1, stackItem) -> this.markLocation(
            stackItem.startLocation, new BinaryExpression(stackItem.operator, stackItem.left, expr1)), expr);
  }

  private Expression parseUnaryExpression() throws JsError {
    if (this.lookahead.type.klass != TokenClass.Punctuator && this.lookahead.type.klass != TokenClass.Keyword) {
      return this.parseUpdateExpression();
    }

    SourceLocation startLocation = this.getLocation();
    Token operatorToken = this.lookahead;
    if (!this.isPrefixOperator(operatorToken)) {
      return this.parseUpdateExpression();
    }

    this.lex();
    this.isBindingElement = this.isAssignmentTarget = false;
    Expression operand = this.parseUnaryExpression();

    Node node;
    UpdateOperator updateOperator = this.lookupUpdateOperator(operatorToken);
    if (updateOperator != null) {
      return createUpdateExpression(startLocation, operand, updateOperator);
    }
    UnaryOperator operator = this.lookupUnaryOperator(operatorToken);
    assert operator != null;
    return new UnaryExpression(operator, operand);
  }

  @NotNull
  private Expression parseUpdateExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Expression operand = this.parseLeftHandSideExpression(true);
    if (this.hasLineTerminatorBeforeNext) return operand;
    UpdateOperator operator = this.lookupUpdateOperator(this.lookahead);
    if (operator == null) {
      return operand;
    }
    this.lex();
    return createUpdateExpression(startLocation, operand, operator);
  }

  @NotNull
  private Expression createUpdateExpression(@NotNull SourceLocation startLocation, @NotNull Expression operand, @NotNull UpdateOperator operator) throws JsError {
    BindingIdentifierMemberExpression restrictedOperand;
    if (operand instanceof MemberExpression) {
      restrictedOperand = (MemberExpression) operand;
    } else if (operand instanceof IdentifierExpression) {
      String name = ((IdentifierExpression) operand).name;
      restrictedOperand = operand.loc.map(loc -> new BindingIdentifier(loc, name)).orJustLazy(Thunk.from(() -> new BindingIdentifier(name)));
    } else {
      throw this.createError("Cannot increment/decrement expression of type " + operand.getClass().getName());
    }
    return this.markLocation(startLocation, new UpdateExpression(false, operator, restrictedOperand));
  }


  private Expression parseNumericLiteral() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Token token = this.lex();
    return this.markLocation(startLocation, new LiteralNumericExpression(Double.parseDouble(token.toString())));
  }

  private Expression parseLeftHandSideExpression(boolean allowCall) throws JsError {
    SourceLocation startLocation = this.getLocation();
    boolean previousAllowIn = this.allowIn;
    this.allowIn = allowCall;

    Expression expr = null;
    Token token = this.lookahead;

    if (this.eat(TokenType.SUPER)) {
      this.isBindingElement = false;
      this.isAssignmentTarget = false;
      if (this.match(TokenType.LPAREN)) {
        if (allowCall) {
          expr = this.markLocation(startLocation, new CallExpression(expr, this.parseArgumentList()));
        }
      } else {
        throw this.createUnexpected(token);
      }
    } else {
      expr = this.parsePrimaryExpression();
      if (this.firstExprError) {
        return expr;
      }
    }

    while (true) {
      if (allowCall && this.match((TokenType.LPAREN))) {
        this.isBindingElement = this.isAssignmentTarget = false;
        expr = this.markLocation(startLocation, new CallExpression(expr, this.parseArgumentList()));
      } else {
        break;
      }
    }

    this.allowIn = previousAllowIn;
    return expr;
  }

  private ImmutableList parseArgumentList() throws JsError {
    this.lex();
    ImmutableList args = this.parseArguments();
    this.expect(TokenType.RPAREN);
    return args;
  }

  private ImmutableList parseArguments() throws JsError {
    ArrayList<Expression> result = new ArrayList();
//    while (true) {
//      if (this.match(TokenType.RPAREN) || this.eof()) {
//        return ImmutableList.from(result);
//      }
//      Expression arg;
//      if (this.match(TokenType.ELLIPSIS)) {
//        SourceLocation startLocation = this.getLocation();
//        arg = this.markLocation(startLocation, new SpreadElement(this.parseAssignmentExpression()))
//      }
//      else {
//        arg = this.parseAssignmentExpression();
//      }
//      result.add(arg);
//      if (!this.eat(TokenType.COMMA)) {
//        break;
//      }
//    }
    return ImmutableList.from(result);
  }

  private Expression parsePrimaryExpression() throws JsError {
    if (this.match(TokenType.LPAREN)) {
      return this.parseGroupExpression();
    }
    SourceLocation startLocation = this.getLocation();

    // TODO: all the other types
    switch (this.lookahead.type) {
      case IDENTIFIER:
//      case YIELD:
      case LET:
        return new IdentifierExpression(this.parseIdentifier());
      case TRUE_LITERAL:
        this.lex();
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.markLocation(startLocation, new LiteralBooleanExpression(true));
      case FALSE_LITERAL:
        this.lex();
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.markLocation(startLocation, new LiteralBooleanExpression(false));
      case NULL_LITERAL:
        this.lex();
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.markLocation(startLocation, new LiteralNullExpression());
      case FUNCTION:
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.markLocation(startLocation, this.parseFunction(true));
      case NUMBER:
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.parseNumericLiteral();
      case THIS:
        this.lex();
        this.isBindingElement = this.isAssignmentTarget = false;
        return new ThisExpression();
      default:
        throw this.createUnexpected(this.lookahead);
    }
  }

  private Expression parseGroupExpression() throws JsError {
    this.lex();
    Expression group = this.parseAssignmentExpressionOrBindingElement();
    this.expect(TokenType.RPAREN);
    return group;
  }

  @Nullable
  private BinaryOperator lookupBinaryOperator(@NotNull Token token) {
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
        return this.allowIn ? BinaryOperator.In : null;
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


  private boolean isUpdateOperator(Token token) {
    switch (token.type) {
      case INC:
      case DEC:
        return true;
    }
    return false;
  }

  private boolean isPrefixOperator(Token token) {
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
  private UnaryOperator lookupUnaryOperator(Token token) {
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
  private UpdateOperator lookupUpdateOperator(Token token) {
    switch (token.type) {
      case INC:
        return UpdateOperator.Increment;
      case DEC:
        return UpdateOperator.Decrement;
    }
    return null;
  }

  private static class ExprStackItem {
    final SourceLocation startLocation;
    @NotNull
    final Expression left;
    @NotNull
    final BinaryOperator operator;
    final int precedence;

    ExprStackItem(@NotNull SourceLocation startLocation, @NotNull Expression left, @NotNull BinaryOperator operator) {
      this.startLocation = startLocation;
      this.left = left;
      this.operator = operator;
      this.precedence = operator.getPrecedence().ordinal();
    }
  }


}
