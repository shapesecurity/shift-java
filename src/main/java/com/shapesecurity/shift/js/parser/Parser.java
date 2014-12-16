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

package com.shapesecurity.shift.js.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.shapesecurity.shift.js.parser.ErrorMessages.ACCESSOR_DATA_PROPERTY;
import static com.shapesecurity.shift.js.parser.ErrorMessages.ACCESSOR_GET_SET;
import static com.shapesecurity.shift.js.parser.ErrorMessages.ILLEGAL_BREAK;
import static com.shapesecurity.shift.js.parser.ErrorMessages.ILLEGAL_CONTINUE;
import static com.shapesecurity.shift.js.parser.ErrorMessages.ILLEGAL_RETURN;
import static com.shapesecurity.shift.js.parser.ErrorMessages.INVALID_LHS_IN_ASSIGNMENT;
import static com.shapesecurity.shift.js.parser.ErrorMessages.INVALID_LHS_IN_FOR_IN;
import static com.shapesecurity.shift.js.parser.ErrorMessages.INVALID_PROPERTY_NAME;
import static com.shapesecurity.shift.js.parser.ErrorMessages.LABEL_REDECLARATION;
import static com.shapesecurity.shift.js.parser.ErrorMessages.MULTIPLE_DEFAULTS_IN_SWITCH;
import static com.shapesecurity.shift.js.parser.ErrorMessages.NEWLINE_AFTER_THROW;
import static com.shapesecurity.shift.js.parser.ErrorMessages.NO_CATCH_OR_FINALLY;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_CATCH_VARIABLE;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_DELETE;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_DUPLICATE_PROPERTY;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_FUNCTION_NAME;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_LHS_ASSIGNMENT;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_LHS_POSTFIX;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_LHS_PREFIX;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_MODE_WITH;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_OCTAL_LITERAL;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_PARAM_DUPE;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_PARAM_NAME;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_RESERVED_WORD;
import static com.shapesecurity.shift.js.parser.ErrorMessages.STRICT_VAR_NAME;
import static com.shapesecurity.shift.js.parser.ErrorMessages.UNEXPECTED_TOKEN;
import static com.shapesecurity.shift.js.parser.ErrorMessages.UNKNOWN_LABEL;

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Directive;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.ast.VariableDeclaration.VariableDeclarationKind;
import com.shapesecurity.shift.js.ast.VariableDeclarator;
import com.shapesecurity.shift.js.ast.directive.UnknownDirective;
import com.shapesecurity.shift.js.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.js.ast.expression.ArrayExpression;
import com.shapesecurity.shift.js.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.js.ast.expression.BinaryExpression;
import com.shapesecurity.shift.js.ast.expression.CallExpression;
import com.shapesecurity.shift.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.js.ast.expression.FunctionExpression;
import com.shapesecurity.shift.js.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.js.ast.expression.LeftHandSideExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.js.ast.expression.MemberExpression;
import com.shapesecurity.shift.js.ast.expression.NewExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PostfixExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ThisExpression;
import com.shapesecurity.shift.js.ast.operators.Assignment;
import com.shapesecurity.shift.js.ast.operators.BinaryOperator;
import com.shapesecurity.shift.js.ast.operators.PostfixOperator;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.ast.operators.PrefixOperator;
import com.shapesecurity.shift.js.ast.property.DataProperty;
import com.shapesecurity.shift.js.ast.property.Getter;
import com.shapesecurity.shift.js.ast.property.ObjectProperty;
import com.shapesecurity.shift.js.ast.property.ObjectProperty.ObjectPropertyKind;
import com.shapesecurity.shift.js.ast.property.PropertyName;
import com.shapesecurity.shift.js.ast.property.Setter;
import com.shapesecurity.shift.js.ast.statement.BlockStatement;
import com.shapesecurity.shift.js.ast.statement.BreakStatement;
import com.shapesecurity.shift.js.ast.statement.ContinueStatement;
import com.shapesecurity.shift.js.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.js.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.js.ast.statement.EmptyStatement;
import com.shapesecurity.shift.js.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.js.ast.statement.ForInStatement;
import com.shapesecurity.shift.js.ast.statement.ForStatement;
import com.shapesecurity.shift.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.js.ast.statement.IfStatement;
import com.shapesecurity.shift.js.ast.statement.LabeledStatement;
import com.shapesecurity.shift.js.ast.statement.ReturnStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.js.ast.statement.ThrowStatement;
import com.shapesecurity.shift.js.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.js.ast.statement.WhileStatement;
import com.shapesecurity.shift.js.ast.statement.WithStatement;
import com.shapesecurity.shift.js.parser.token.IdentifierLikeToken;
import com.shapesecurity.shift.js.parser.token.IdentifierToken;
import com.shapesecurity.shift.js.parser.token.NumericLiteralToken;
import com.shapesecurity.shift.js.parser.token.StringLiteralToken;
import com.shapesecurity.shift.js.utils.Utils;

public class Parser extends Tokenizer {
  @Nonnull
  private HashSet<String> labelSet = new HashSet<>();
  private boolean inIteration;
  private boolean inSwitch;
  private boolean inFunctionBody;
  private boolean allowIn = true;

  public Parser(@Nonnull String source) throws JsError {
    super(true, source);
  }

  @Nullable
  private static PrefixOperator lookupPrefixOperator(@Nonnull TokenType type) {
    switch (type) {
    case INC:
      return PrefixOperator.Increment;
    case DEC:
      return PrefixOperator.Decrement;
    case ADD:
      return PrefixOperator.Plus;
    case SUB:
      return PrefixOperator.Minus;
    case BIT_NOT:
      return PrefixOperator.BitNot;
    case NOT:
      return PrefixOperator.LogicalNot;
    case DELETE:
      return PrefixOperator.Delete;
    case VOID:
      return PrefixOperator.Void;
    case TYPEOF:
      return PrefixOperator.Typeof;
    default:
      return null;
    }
  }

  @Nullable
  private static PostfixOperator lookupPostfixOperator(@Nonnull TokenType type) {
    switch (type) {
    case INC:
      return PostfixOperator.Increment;
    case DEC:
      return PostfixOperator.Decrement;
    default:
      return null;
    }
  }

  private static boolean isLeftHandSide(@Nonnull Expression expr) {
    return expr instanceof MemberExpression || expr instanceof IdentifierExpression;
  }

  @Nonnull
  public static Script parse(@Nonnull String text) throws JsError {
    return new Parser(text).parse();
  }

  @Nonnull
  protected Token expect(@Nonnull TokenType subType) throws JsError {
    if (this.lookahead.type != subType) {
      throw this.createUnexpected(this.lookahead);
    }
    return this.lex();
  }

  protected boolean match(@Nonnull TokenType subType) {
    return this.lookahead.type == subType;
  }

  protected void consumeSemicolon() throws JsError {
    // Catch the very common case first: immediately a semicolon (U+003B).
    if (this.index < this.source.length() && this.source.charAt(this.index) == ';') {
      this.lex();
      return;
    }

    this.index = this.lookahead.slice.start;
    if (this.hasLineTerminatorBeforeNext) {
      return;
    }

    if (this.match(TokenType.SEMICOLON)) {
      this.lex();
      return;
    }

    if (!this.eof() && !this.match(TokenType.RBRACE)) {
      throw this.createUnexpected(this.lookahead);
    }
  }

  // this is a no-op, reserved for future use
  @SuppressWarnings({"UnusedParameters", "MethodMayBeStatic"})
  private <T> T markLocation(T node, int startTokenIndex, int endTokenIndex) {
    return node;
  }

  private <T> T markLocation(T node, int startTokenIndex) {
    return markLocation(node, startTokenIndex, this.tokenIndex);
  }

  @Nonnull
  private /* statements */ List<Directive> parseDirective(
      @Nonnull Statement[] sourceElements,
      @Nullable Token firstRestricted) throws JsError {

    if (this.lookahead.type != TokenType.STRING) {
      return List.nil();
    }

    Token token = this.lookahead;
    Statement stmt = this.parseSourceElement();
    if (stmt instanceof ExpressionStatement) {
      Expression expr = ((ExpressionStatement) stmt).expression;
      if (expr instanceof LiteralStringExpression) {
        CharSequence value = ((LiteralStringExpression) expr).raw;
        String directive = token.slice.toString();
        if ("\"use strict\"".equals(directive) || "'use strict'".equals(directive)) {
          this.strict = true;
          if (firstRestricted != null) {
            throw this.createError(firstRestricted, STRICT_OCTAL_LITERAL);
          }
          return List.cons(new UseStrictDirective(), parseDirective(sourceElements, null));
        } else {
          if (firstRestricted == null && token.octal) {
            firstRestricted = token;
          }
          CharSequence content = value.subSequence(1, value.length() - 1);
          return List.cons(new UnknownDirective(content), parseDirective(sourceElements, firstRestricted));
        }
      }
    }
    sourceElements[0] = stmt;
    return List.nil();
  }

  @Nonnull
  public Script parse() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.strict = false;

    FunctionBody body = this.parseProgramBody();

    return this.markLocation(new Script(body), startTokenIndex);
  }

  @Nonnull
  private FunctionBody parseProgramBody() throws JsError {
    int startTokenIndex = this.tokenIndex;

    Statement[] firstStatement = new Statement[1];
    List<Directive> directives = this.parseDirective(firstStatement, null);

    List<Statement> statements = parseSourceElements();
    if (firstStatement[0] != null) {
      statements = List.cons(firstStatement[0], statements);
    }

    return this.markLocation(new FunctionBody(directives, statements), startTokenIndex);
  }

  @Nonnull
  private FunctionBody parseFunctionBody() throws JsError {
    boolean previousStrict = this.strict;
    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.LBRACE);

    Statement[] firstStatement = new Statement[1];
    List<Directive> directives = this.parseDirective(firstStatement, null);
    HashSet<String> oldLabelSet = this.labelSet;
    boolean oldInIteration = this.inIteration;
    boolean oldInSwitch = this.inSwitch;
    boolean oldInFunctionBody = this.inFunctionBody;

    this.labelSet = new HashSet<>();
    this.inIteration = false;
    this.inSwitch = false;
    this.inFunctionBody = true;

    List<Statement> statements = parseSourceElementsInFunctionBody();
    if (firstStatement[0] != null) {
      statements = List.cons(firstStatement[0], statements);
    }

    this.expect(TokenType.RBRACE);
    FunctionBody body = this.markLocation(new FunctionBody(directives, statements), startTokenIndex);

    this.labelSet = oldLabelSet;
    this.inIteration = oldInIteration;
    this.inSwitch = oldInSwitch;
    this.inFunctionBody = oldInFunctionBody;
    this.strict = previousStrict;
    return body;
  }

  @Nonnull
  private List<Statement> parseSourceElements() throws JsError {
    if (this.eof()) {
      return List.nil();
    }
    return List.cons(this.parseSourceElement(), parseSourceElements());
  }

  @Nonnull
  private List<Statement> parseSourceElementsInFunctionBody() throws JsError {
    if (this.eof() || this.match(TokenType.RBRACE)) {
      return List.nil();
    }
    return List.cons(this.parseSourceElement(), parseSourceElementsInFunctionBody());
  }

  @Nonnull
  private Statement parseSourceElement() throws JsError {
    if (this.lookahead.type.klass == TokenClass.Keyword) {
      switch (this.lookahead.type) {
      case CONST:
        return this.parseConstLetDeclaration(VariableDeclarationKind.Const);
      case LET:
        return this.parseConstLetDeclaration(VariableDeclarationKind.Let);
      case FUNCTION:
        return this.parseFunctionDeclaration();
      default:
        return this.parseStatement();
      }
    }

    return this.parseStatement();
  }

  @Nonnull
  private VariableDeclarationStatement parseConstLetDeclaration(@Nonnull VariableDeclarationKind kind) throws JsError {
    int startTokenIndex = this.tokenIndex;

    switch (kind) {
    case Const:
      this.expect(TokenType.CONST);
      break;
    case Let:
      this.expect(TokenType.LET);
      break;
    default:
      break;
    }

    NonEmptyList<VariableDeclarator> declarations = this.parseVariableDeclaratorList(kind);
    this.consumeSemicolon();

    return this.markLocation(new VariableDeclarationStatement(new VariableDeclaration(kind, declarations)),
        startTokenIndex);
  }

  @Nonnull
  private VariableDeclarationStatement parseVariableDeclarationStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.VAR);
    NonEmptyList<VariableDeclarator> declarators = this.parseVariableDeclaratorList(VariableDeclarationKind.Var);
    this.consumeSemicolon();

    return this.markLocation(new VariableDeclarationStatement(new VariableDeclaration(VariableDeclarationKind.Var,
        declarators)), startTokenIndex);
  }

  @Nonnull
  private VariableDeclaration parseForVariableDeclaration() throws JsError {
    int startTokenIndex = this.tokenIndex;
    Token token = this.lex();

    // Preceded by this.match(TokenSubType.VAR) || this.match(TokenSubType.LET);
    VariableDeclarationKind kind =
        token.type == TokenType.VAR ? VariableDeclarationKind.Var : VariableDeclarationKind.Let;
    NonEmptyList<VariableDeclarator> declarators = this.parseVariableDeclaratorList(kind);
    return this.markLocation(new VariableDeclaration(kind, declarators), startTokenIndex);
  }

  @Nonnull
  ParamsInfo parseParams(@Nullable Token fr) throws JsError {
    ParamsInfo info = new ParamsInfo();
    info.firstRestricted = fr;
    this.expect(TokenType.LPAREN);

    if (!this.match(TokenType.RPAREN)) {
      HashSet<String> paramSet = new HashSet<>();

      while (!this.eof()) {
        Token token = this.lookahead;
        Identifier param = this.parseVariableIdentifier();
        String key = param.name;
        if (this.strict) {
          if (token instanceof IdentifierLikeToken && Utils.isRestrictedWord(param.name)) {
            info.stricted = token;
            info.message = STRICT_PARAM_NAME;
          }
          if (paramSet.contains(key)) {
            info.stricted = token;
            info.message = STRICT_PARAM_DUPE;
          }
        } else if (info.firstRestricted == null) {
          if (token instanceof IdentifierLikeToken && Utils.isRestrictedWord(param.name)) {
            info.firstRestricted = token;
            info.message = STRICT_PARAM_NAME;
          } else if (STRICT_MODE_RESERVED_WORD.contains(key)) {
            info.firstRestricted = token;
            info.message = STRICT_RESERVED_WORD;
          } else if (paramSet.contains(key)) {
            info.firstRestricted = token;
            info.message = STRICT_PARAM_DUPE;
          }
        }
        info.params.add(param);
        paramSet.add(key);
        if (this.match(TokenType.RPAREN)) {
          break;
        }
        this.expect(TokenType.COMMA);
      }
    }

    this.expect(TokenType.RPAREN);
    return info;
  }

  @Nonnull
  private FunctionDeclaration parseFunctionDeclaration() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.FUNCTION);

    Token token = this.lookahead;
    Identifier id = this.parseVariableIdentifier();
    Token firstRestricted = null;
    String message = null;
    if (this.strict) {
      if (token instanceof IdentifierLikeToken && Utils.isRestrictedWord(id.name)) {
        throw this.createError(token, STRICT_FUNCTION_NAME);
      }
    } else {
      if (token instanceof IdentifierLikeToken && Utils.isRestrictedWord(id.name)) {
        firstRestricted = token;
        message = STRICT_FUNCTION_NAME;
      } else if (STRICT_MODE_RESERVED_WORD.contains(id.name)) {
        firstRestricted = token;
        message = STRICT_RESERVED_WORD;
      }
    }

    ParamsInfo info = this.parseParams(firstRestricted);
    if (info.message != null) {
      message = info.message;
      firstRestricted = info.firstRestricted;
    }

    boolean previousStrict = this.strict;
    FunctionBody body = this.parseFunctionBody();
    if ((this.strict || body.isStrict()) && firstRestricted != null && message != null) {
      throw this.createError(message, firstRestricted);
    }
    if ((this.strict || body.isStrict()) && info.stricted != null && message != null) {
      throw this.createError(message, info.stricted);
    }
    this.strict = previousStrict;

    return this.markLocation(new FunctionDeclaration(id, List.from(info.params), body), startTokenIndex);
  }

  @Nonnull
  private Statement parseStatement() throws JsError {
    if (this.eof()) {
      throw this.createUnexpected(this.lookahead);
    }

    switch (this.lookahead.type) {
    case SEMICOLON:
      return this.parseEmptyStatement();
    case LBRACE:
      return this.parseBlockStatement();
    case LPAREN:
      return this.parseExpressionStatement();
    case BREAK:
      return this.parseBreakStatement();
    case CONTINUE:
      return this.parseContinueStatement();
    case DEBUGGER:
      return this.parseDebuggerStatement();
    case DO:
      return this.parseDoWhileStatement();
    case FOR:
      return this.parseForStatement();
    case FUNCTION:
      return this.parseFunctionDeclaration();
    case IF:
      return this.parseIfStatement();
    case RETURN:
      return this.parseReturnStatement();
    case SWITCH:
      return this.parseSwitchStatement();
    case THROW:
      return this.parseThrowStatement();
    case TRY:
      return this.parseTryStatement();
    case VAR:
      return this.parseVariableDeclarationStatement();
    case WHILE:
      return this.parseWhileStatement();
    case WITH:
      return this.parseWithStatement();
    default:
      int startTokenIndex = this.tokenIndex;
      Expression expr = this.parseExpression();

      // 12.12 Labelled Statements;
      if (expr instanceof IdentifierExpression && this.match(TokenType.COLON)) {
        this.lex();
        IdentifierExpression ident = (IdentifierExpression) expr;
        String key = ident.identifier.name;
        if (this.labelSet.contains(key)) {
          throw this.createError(LABEL_REDECLARATION, ident.identifier.name);
        }

        this.labelSet.add(key);
        Statement labeledBody = this.parseStatement();
        this.labelSet.remove(key);
        return this.markLocation(new LabeledStatement(ident.identifier, labeledBody), startTokenIndex);
      } else {
        this.consumeSemicolon();
        return this.markLocation(new ExpressionStatement(expr), startTokenIndex);
      }
    }
  }

  private BlockStatement parseBlockStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    return this.markLocation(new BlockStatement(this.parseBlock()), startTokenIndex);
  }

  // guaranteed to parse at least one declarator
  @Nonnull
  NonEmptyList<VariableDeclarator> parseVariableDeclaratorList(VariableDeclarationKind kind) throws JsError {
    VariableDeclarator variableDeclarator = this.parseVariableDeclarator(kind);
    if (!this.match(TokenType.COMMA)) {
      return List.list(variableDeclarator);
    }
    this.lex();
    if (this.eof()) {
      return List.list(variableDeclarator);
    }
    return List.cons(variableDeclarator, parseVariableDeclaratorList(kind));
  }

  @Nonnull
  private Identifier parseVariableIdentifier() throws JsError {
    int startTokenIndex = this.tokenIndex;

    Token token = this.lex();
    if (!(token instanceof IdentifierToken)) {
      throw this.createUnexpected(token);
    }

    return this.markLocation(new Identifier(String.valueOf(token.getValueString())), startTokenIndex);
  }

  @Nonnull
  private EmptyStatement parseEmptyStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.SEMICOLON);
    return this.markLocation(new EmptyStatement(), startTokenIndex);
  }

  @Nonnull
  private Block parseBlock() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.LBRACE);

    List<Statement> body = this.parseStatementList();

    this.expect(TokenType.RBRACE);

    return this.markLocation(new Block(body), startTokenIndex);
  }

  @Nonnull
  private ExpressionStatement parseExpressionStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    Expression expr = this.parseExpression();
    this.consumeSemicolon();
    return this.markLocation(new ExpressionStatement(expr), startTokenIndex);
  }

  @Nonnull
  private BreakStatement parseBreakStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    Token token = this.lookahead;
    this.expect(TokenType.BREAK);

    // Catch the very common case first: immediately a semicolon (U+003B).
    if (this.lookahead.type == TokenType.SEMICOLON) {
      this.lex();

      if (!(this.inIteration || this.inSwitch)) {
        throw this.createError(token, ILLEGAL_BREAK);
      }

      return this.markLocation(new BreakStatement(Maybe.<Identifier>nothing()), startTokenIndex);
    }

    if (this.hasLineTerminatorBeforeNext) {
      if (!(this.inIteration || this.inSwitch)) {
        throw this.createError(token, ILLEGAL_BREAK);
      }

      return this.markLocation(new BreakStatement(Maybe.<Identifier>nothing()), startTokenIndex);
    }

    Identifier label = null;
    if (this.lookahead.type == TokenType.IDENTIFIER) {
      label = this.parseVariableIdentifier();

      String key = label.name;
      if (!this.labelSet.contains(key)) {
        throw this.createError(UNKNOWN_LABEL, label.name);
      }
    }

    this.consumeSemicolon();

    if (label == null && !(this.inIteration || this.inSwitch)) {
      throw this.createError(token, ILLEGAL_BREAK);
    }

    return this.markLocation(new BreakStatement(Maybe.fromNullable(label)), startTokenIndex);
  }

  @Nonnull
  private ContinueStatement parseContinueStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    Token token = this.lookahead;
    this.expect(TokenType.CONTINUE);

    // Catch the very common case first: immediately a semicolon (U+003B).
    if (this.lookahead.type == TokenType.SEMICOLON) {
      this.lex();
      if (!this.inIteration) {
        throw this.createError(token, ILLEGAL_CONTINUE);
      }

      return this.markLocation(new ContinueStatement(Maybe.<Identifier>nothing()), startTokenIndex);
    }

    if (this.hasLineTerminatorBeforeNext) {
      if (!this.inIteration) {
        throw this.createError(token, ILLEGAL_CONTINUE);
      }

      return this.markLocation(new ContinueStatement(Maybe.<Identifier>nothing()), startTokenIndex);
    }

    Identifier label = null;
    if (this.lookahead.type == TokenType.IDENTIFIER) {
      label = this.parseVariableIdentifier();

      String key = label.name;
      if (!this.labelSet.contains(key)) {
        throw this.createError(UNKNOWN_LABEL, label.name);
      }
    }

    this.consumeSemicolon();
    if (!this.inIteration) {
      throw this.createError(token, ILLEGAL_CONTINUE);
    }

    return this.markLocation(new ContinueStatement(Maybe.fromNullable(label)), startTokenIndex);
  }

  @Nonnull
  private DebuggerStatement parseDebuggerStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.DEBUGGER);
    this.consumeSemicolon();
    return this.markLocation(new DebuggerStatement(), startTokenIndex);
  }

  @Nonnull
  private DoWhileStatement parseDoWhileStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.DO);
    boolean oldInIteration = this.inIteration;
    this.inIteration = true;

    Statement body = this.parseStatement();
    this.inIteration = oldInIteration;

    this.expect(TokenType.WHILE);
    this.expect(TokenType.LPAREN);
    Expression test = this.parseExpression();
    this.expect(TokenType.RPAREN);
    if (this.match(TokenType.SEMICOLON)) {
      this.lex();
    }

    return this.markLocation(new DoWhileStatement(body, test), startTokenIndex);
  }

  @Nonnull
  private Statement parseForStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.FOR);
    this.expect(TokenType.LPAREN);
    Expression test = null;
    Expression right = null;
    if (this.match(TokenType.SEMICOLON)) {
      this.lex();
      if (!this.match(TokenType.SEMICOLON)) {
        test = this.parseExpression();
      }
      this.expect(TokenType.SEMICOLON);
      if (!this.match(TokenType.RPAREN)) {
        right = this.parseExpression();
      }
      return this.markLocation(new ForStatement(Maybe.nothing(), Maybe.fromNullable(test), Maybe.fromNullable(right),
          this.getIteratorStatementEpilogue()), startTokenIndex);
    } else {
      if (this.match(TokenType.VAR) || this.match(TokenType.LET)) {
        boolean previousAllowIn = this.allowIn;
        this.allowIn = false;
        VariableDeclaration initDecl = this.parseForVariableDeclaration();
        this.allowIn = previousAllowIn;

        if (initDecl.declarators.tail().isEmpty() && this.match(TokenType.IN)) {
          this.lex();
          right = this.parseExpression();
          return this.markLocation(new ForInStatement(initDecl, right, this.getIteratorStatementEpilogue()),
              startTokenIndex);
        } else {
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.SEMICOLON)) {
            test = this.parseExpression();
          }
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.RPAREN)) {
            right = this.parseExpression();
          }
          return this.markLocation(new ForStatement(initDecl, Maybe.fromNullable(test), Maybe.fromNullable(right),
              this.getIteratorStatementEpilogue()), startTokenIndex);
        }
      } else {
        boolean previousAllowIn = this.allowIn;
        this.allowIn = false;
        Expression init = this.parseExpression();
        this.allowIn = previousAllowIn;

        if (this.match(TokenType.IN)) {
          // LeftHandSideExpression;
          if (!(init instanceof LeftHandSideExpression)) {
            throw this.createError(INVALID_LHS_IN_FOR_IN);
          }

          this.lex();
          right = this.parseExpression();
          return this.markLocation(new ForInStatement(init, right, this.getIteratorStatementEpilogue()),
              startTokenIndex);
        } else {
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.SEMICOLON)) {
            test = this.parseExpression();
          }
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.RPAREN)) {
            right = this.parseExpression();
          }
          return this.markLocation(new ForStatement(Maybe.fromNullable(init).map(Either::right), Maybe.fromNullable(
              test), Maybe.fromNullable(right), this.getIteratorStatementEpilogue()), startTokenIndex);
        }
      }
    }
  }

  private Statement getIteratorStatementEpilogue() throws JsError {
    this.expect(TokenType.RPAREN);
    boolean oldInIteration = this.inIteration;
    this.inIteration = true;
    Statement body = this.parseStatement();
    this.inIteration = oldInIteration;
    return body;
  }

  @Nonnull
  private IfStatement parseIfStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.IF);
    this.expect(TokenType.LPAREN);
    Expression test = this.parseExpression();

    this.expect(TokenType.RPAREN);
    Statement consequent = this.parseStatement();
    Maybe<Statement> alternate;
    if (this.match(TokenType.ELSE)) {
      this.lex();
      alternate = Maybe.fromNullable(this.parseStatement());
    } else {
      alternate = Maybe.nothing();
    }

    return this.markLocation(new IfStatement(test, consequent, alternate), startTokenIndex);
  }

  @Nonnull
  private ReturnStatement parseReturnStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    Maybe<Expression> argument = Maybe.nothing();

    this.expect(TokenType.RETURN);
    if (!this.inFunctionBody) {
      throw this.createError(ILLEGAL_RETURN);
    }

    if (this.hasLineTerminatorBeforeNext) {
      return this.markLocation(new ReturnStatement(Maybe.<Expression>nothing()), startTokenIndex);
    }

    if (!this.match(TokenType.SEMICOLON)) {
      if (!this.match(TokenType.RBRACE) && !this.eof()) {
        argument = Maybe.fromNullable(this.parseExpression());
      }
    }

    this.consumeSemicolon();
    return this.markLocation(new ReturnStatement(argument), startTokenIndex);
  }

  @Nonnull
  private WithStatement parseWithStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    if (this.strict) {
      throw this.createError(STRICT_MODE_WITH);
    }

    this.expect(TokenType.WITH);
    this.expect(TokenType.LPAREN);
    Expression object = this.parseExpression();
    this.expect(TokenType.RPAREN);
    Statement body = this.parseStatement();

    return this.markLocation(new WithStatement(object, body), startTokenIndex);
  }

  @Nonnull
  private Statement parseSwitchStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.SWITCH);
    this.expect(TokenType.LPAREN);

    Expression discriminant = this.parseExpression();
    this.expect(TokenType.RPAREN);
    this.expect(TokenType.LBRACE);

    if (this.match(TokenType.RBRACE)) {
      this.lex();
      return this.markLocation(new SwitchStatement(discriminant, List.<SwitchCase>nil()), startTokenIndex);
    }
    boolean oldInSwitch = this.inSwitch;
    this.inSwitch = true;

    List<SwitchCase> cases = parseSwitchCases();

    if (this.match(TokenType.DEFAULT)) {
      SwitchDefault switchDefault = this.parseSwitchDefault();
      List<SwitchCase> postDefaultCases = parseSwitchCases();
      if (this.match(TokenType.DEFAULT)) {
        throw this.createError(MULTIPLE_DEFAULTS_IN_SWITCH);
      }
      this.inSwitch = oldInSwitch;
      this.expect(TokenType.RBRACE);
      return this.markLocation(new SwitchStatementWithDefault(discriminant, cases, switchDefault, postDefaultCases),
          startTokenIndex);
    } else {
      this.inSwitch = oldInSwitch;
      this.expect(TokenType.RBRACE);
      return this.markLocation(new SwitchStatement(discriminant, cases), startTokenIndex);
    }
  }

  private List<SwitchCase> parseSwitchCases() throws JsError {
    if (this.eof() || this.match(TokenType.RBRACE) || this.match(TokenType.DEFAULT)) {
      return List.nil();
    }
    return List.cons(this.parseSwitchCase(), parseSwitchCases());
  }

  @Nonnull
  private ThrowStatement parseThrowStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;

    Token token = this.expect(TokenType.THROW);

    if (this.hasLineTerminatorBeforeNext) {
      throw this.createError(token, NEWLINE_AFTER_THROW);
    }

    Expression argument = this.parseExpression();

    this.consumeSemicolon();

    return this.markLocation(new ThrowStatement(argument), startTokenIndex);
  }

  @Nonnull
  private Statement parseTryStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.TRY);
    Block block = this.parseBlock();

    if (this.match(TokenType.CATCH)) {
      CatchClause handler = this.parseCatchClause();
      if (this.match(TokenType.FINALLY)) {
        this.lex();
        Block finalizer = this.parseBlock();
        return this.markLocation(new TryFinallyStatement(block, Maybe.just(handler), finalizer), startTokenIndex);
      }
      return this.markLocation(new TryCatchStatement(block, handler), startTokenIndex);
    }

    if (this.match(TokenType.FINALLY)) {
      this.lex();
      Block finalizer = this.parseBlock();
      return this.markLocation(new TryFinallyStatement(block, Maybe.<CatchClause>nothing(), finalizer),
          startTokenIndex);
    } else {
      throw this.createError(NO_CATCH_OR_FINALLY);
    }
  }

  @Nonnull
  private WhileStatement parseWhileStatement() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.WHILE);
    this.expect(TokenType.LPAREN);
    return this.markLocation(new WhileStatement(this.parseExpression(), this.getIteratorStatementEpilogue()),
        startTokenIndex);
  }

  private Expression parseExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;

    Expression expr = this.parseAssignmentExpression();

    if (this.match(TokenType.COMMA)) {
      while (!this.eof()) {
        if (!this.match(TokenType.COMMA)) {
          break;
        }
        this.lex();
        expr = this.markLocation(new BinaryExpression(BinaryOperator.Sequence, expr, this.parseAssignmentExpression()),
            startTokenIndex);
      }
    }

    return expr;
  }

  @Nonnull
  private VariableDeclarator parseVariableDeclarator(VariableDeclarationKind kind) throws JsError {
    int startTokenIndex = this.tokenIndex;

    Identifier id = this.parseVariableIdentifier();

    // 12.2.1;
    if (this.strict && Utils.isRestrictedWord(id.name)) {
      throw this.createError(STRICT_VAR_NAME);
    }

    Maybe<Expression> init = Maybe.nothing();
    if (kind == VariableDeclarationKind.Const) {
      this.expect(TokenType.ASSIGN);
      init = Maybe.just(this.parseAssignmentExpression());
    } else if (this.match(TokenType.ASSIGN)) {
      this.lex();
      init = Maybe.just(this.parseAssignmentExpression());
    }
    return this.markLocation(new VariableDeclarator(id, init), startTokenIndex);
  }

  @Nonnull
  // ECMAScript 5 does not allow FunctionDeclarations in block statements, but no
  // implementations comply to this restriction.
  private List<Statement> parseStatementList() throws JsError {
    if (this.eof()) {
      return List.nil();
    }

    if (this.match(TokenType.RBRACE)) {
      return List.nil();
    }
    return List.cons(this.parseSourceElement(), parseStatementList());
  }

  @Nonnull
  private SwitchCase parseSwitchCase() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.CASE);
    return this.markLocation(new SwitchCase(this.parseExpression(), this.parseSwitchCaseBody()), startTokenIndex);
  }

  @Nonnull
  private SwitchDefault parseSwitchDefault() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.DEFAULT);
    return this.markLocation(new SwitchDefault(this.parseSwitchCaseBody()), startTokenIndex);
  }

  private List<Statement> parseSwitchCaseBody() throws JsError {
    this.expect(TokenType.COLON);
    return parseStatementListInSwitchCaseBody();
  }

  private List<Statement> parseStatementListInSwitchCaseBody() throws JsError {
    if (this.eof() || this.match(TokenType.RBRACE) || this.match(TokenType.DEFAULT) || this.match(TokenType.CASE)) {
      return List.nil();
    }
    return List.cons(this.parseSourceElement(), parseStatementListInSwitchCaseBody());
  }

  @Nonnull
  private CatchClause parseCatchClause() throws JsError {
    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.CATCH);
    this.expect(TokenType.LPAREN);
    if (this.match(TokenType.RPAREN)) {
      throw this.createUnexpected(this.lookahead);
    }

    Identifier param = this.parseVariableIdentifier();

    // 12.14.1;
    if (this.strict && Utils.isRestrictedWord(param.name)) {
      throw this.createError(STRICT_CATCH_VARIABLE);
    }

    this.expect(TokenType.RPAREN);

    Block body = this.parseBlock();

    return this.markLocation(new CatchClause(param, body), startTokenIndex);
  }

  @Nonnull
  private Expression parseAssignmentExpression() throws JsError {
    Token token = this.lookahead;
    int startTokenIndex = this.tokenIndex;

    Expression node = this.parseConditionalExpression();

    Assignment operator = null;
    switch (this.lookahead.type) {
    case ASSIGN:
      operator = Assignment.Assign;
      break;
    case ASSIGN_BIT_OR:
      operator = Assignment.AssignBitOr;
      break;
    case ASSIGN_BIT_XOR:
      operator = Assignment.AssignBitXor;
      break;
    case ASSIGN_BIT_AND:
      operator = Assignment.AssignBitAnd;
      break;
    case ASSIGN_SHL:
      operator = Assignment.AssignLeftShift;
      break;
    case ASSIGN_SHR:
      operator = Assignment.AssignRightShift;
      break;
    case ASSIGN_SHR_UNSIGNED:
      operator = Assignment.AssignUnsignedRightShift;
      break;
    case ASSIGN_ADD:
      operator = Assignment.AssignPlus;
      break;
    case ASSIGN_SUB:
      operator = Assignment.AssignMinus;
      break;
    case ASSIGN_MUL:
      operator = Assignment.AssignMul;
      break;
    case ASSIGN_DIV:
      operator = Assignment.AssignDiv;
      break;
    case ASSIGN_MOD:
      operator = Assignment.AssignRem;
      break;
    default:
      break;
    }

    if (operator != null) {
      // To be permissive.
      // if (!isLeftHandSide(node)) {
      //     throw this.createError(INVALID_LHS_IN_ASSIGNMENT);
      // }

      // 11.13.1;
      if (node instanceof IdentifierExpression) {
        IdentifierExpression ident = (IdentifierExpression) node;
        if (this.strict && Utils.isRestrictedWord(ident.identifier.name)) {
          throw this.createError(token, STRICT_LHS_ASSIGNMENT);
        }
      }

      this.lex();
      Expression right = this.parseAssignmentExpression();
      return this.markLocation(new AssignmentExpression(operator, node, right), startTokenIndex);
    }
    return node;
  }

  @Nonnull
  private Expression parseConditionalExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;
    Expression expr = this.parseBinaryExpression();
    if (this.match(TokenType.CONDITIONAL)) {
      this.lex();
      boolean previousAllowIn = this.allowIn;
      this.allowIn = true;
      Expression consequent = this.parseAssignmentExpression();
      this.allowIn = previousAllowIn;
      this.expect(TokenType.COLON);
      Expression alternate = this.parseAssignmentExpression();
      return this.markLocation(new ConditionalExpression(expr, consequent, alternate), startTokenIndex);
    }

    return expr;
  }

  @Nullable
  private BinaryOperator lookupBinaryOperator(@Nonnull TokenType type) {
    switch (type) {
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
    default:
      return null;
    }
  }

  @Nonnull
  private Expression parseBinaryExpression() throws JsError {
    Expression left = this.parseUnaryExpression();
    Token token = this.lookahead;
    BinaryOperator operator = this.lookupBinaryOperator(token.type);
    if (operator == null) {
      return left;
    }

    this.lex();

    List<ExprStackItem> stack = List.nil();
    stack = stack.cons(new ExprStackItem(this.tokenIndex, left, operator));
    Expression expr = this.parseUnaryExpression();

    operator = this.lookupBinaryOperator(this.lookahead.type);
    while (operator != null) {
      Precedence precedence = operator.getPrecedence();
      // Reduce: make a binary expression from the three topmost entries.
      while ((stack.isNotEmpty()) && (precedence.ordinal() <= ((NonEmptyList<ExprStackItem>) stack).head.precedence)) {
        ExprStackItem stackItem = ((NonEmptyList<ExprStackItem>) stack).head;
        BinaryOperator stackOperator = stackItem.operator;
        left = stackItem.left;
        stack = ((NonEmptyList<ExprStackItem>) stack).tail();
        expr = this.markLocation(new BinaryExpression(stackOperator, left, expr), stackItem.startIndex,
            this.tokenIndex);
      }

      // Shift.
      this.lex();
      stack = stack.cons(new ExprStackItem(this.tokenIndex, expr, operator));
      expr = this.parseUnaryExpression();

      operator = this.lookupBinaryOperator(this.lookahead.type);
    }

    // Final reduce to clean-up the stack.
    return stack.foldLeft((expr1, stackItem) -> markLocation(new BinaryExpression(stackItem.operator, stackItem.left,
        expr1), stackItem.startIndex, this.tokenIndex), expr);
  }

  @Nonnull
  private Expression parseUnaryExpression() throws JsError {
    if (this.lookahead.type.klass != TokenClass.Punctuator && this.lookahead.type.klass != TokenClass.Keyword) {
      return this.parsePostfixExpression();
    }
    int startTokenIndex = this.tokenIndex;
    PrefixOperator operator = lookupPrefixOperator(this.lookahead.type);
    if (operator == null) {
      return this.parsePostfixExpression();
    }
    this.lex();
    Expression expr = this.parseUnaryExpression();
    switch (operator) {
    case Increment:
    case Decrement:
      // 11.4.4, 11.4.5;
      if (expr instanceof IdentifierExpression) {
        IdentifierExpression ident = (IdentifierExpression) expr;
        if (this.strict && Utils.isRestrictedWord(ident.identifier.name)) {
          throw this.createError(STRICT_LHS_PREFIX);
        }
      }

      if (!isLeftHandSide(expr)) {
        throw this.createError(INVALID_LHS_IN_ASSIGNMENT);
      }
      break;
    case Delete:
      if (expr instanceof IdentifierExpression && this.strict) {
        throw this.createError(STRICT_DELETE);
      }
      break;
    default:
      break;
    }

    return this.markLocation(new PrefixExpression(operator, expr), startTokenIndex);
  }

  @Nonnull
  private Expression parsePostfixExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;

    Expression expr = this.parseLeftHandSideExpressionAllowCall();

    if (this.hasLineTerminatorBeforeNext) {
      return expr;
    }

    PostfixOperator operator = lookupPostfixOperator(this.lookahead.type);
    if (operator == null) {
      return expr;
    }
    this.lex();
    // 11.3.1, 11.3.2;
    if (expr instanceof IdentifierExpression) {
      IdentifierExpression ident = (IdentifierExpression) expr;
      if (this.strict && Utils.isRestrictedWord(ident.identifier.name)) {
        throw this.createError(STRICT_LHS_POSTFIX);
      }
    }
    if (!isLeftHandSide(expr)) {
      throw this.createError(INVALID_LHS_IN_ASSIGNMENT);
    }
    return this.markLocation(new PostfixExpression(operator, expr), startTokenIndex);
  }

  @Nonnull
  private Expression parseLeftHandSideExpressionAllowCall() throws JsError {
    int startTokenIndex = this.tokenIndex;
    boolean previousAllowIn = this.allowIn;
    this.allowIn = true;
    Expression expr = this.match(TokenType.NEW) ? this.parseNewExpression() : this.parsePrimaryExpression();

    while (true) {
      if (this.match(TokenType.LPAREN)) {
        expr = this.markLocation(new CallExpression(expr, this.parseArgumentList()), startTokenIndex);
      } else if (this.match(TokenType.LBRACK)) {
        expr = this.markLocation(new ComputedMemberExpression(expr, this.parseComputedMember()), startTokenIndex);
      } else if (this.match(TokenType.PERIOD)) {
        expr = this.markLocation(new StaticMemberExpression(expr, this.parseNonComputedMember()), startTokenIndex);
      } else {
        break;
      }
    }

    this.allowIn = previousAllowIn;

    return expr;
  }

  @Nonnull
  private Expression parseLeftHandSideExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;

    assert this.allowIn;

    Expression expr = this.match(TokenType.NEW) ? this.parseNewExpression() : this.parsePrimaryExpression();

    while (this.match(TokenType.PERIOD) || this.match(TokenType.LBRACK)) {
      expr = this.match(TokenType.LBRACK) ? this.markLocation(new ComputedMemberExpression(expr,
          this.parseComputedMember()), startTokenIndex) : this.markLocation(new StaticMemberExpression(expr,
          this.parseNonComputedMember()), startTokenIndex);
    }

    return expr;
  }

  @Nonnull
  private Identifier parseNonComputedMember() throws JsError {
    this.expect(TokenType.PERIOD);
    return this.parseNonComputedProperty();
  }

  private Expression parseComputedMember() throws JsError {
    this.expect(TokenType.LBRACK);
    Expression expr = this.parseExpression();
    this.expect(TokenType.RBRACK);
    return expr;
  }

  @Nonnull
  private Expression parseNewExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;
    this.expect(TokenType.NEW);
    Expression callee = this.parseLeftHandSideExpression();
    return this.markLocation(new NewExpression(callee, this.match(TokenType.LPAREN) ? this.parseArgumentList() :
                                                       List.<Expression>nil()), startTokenIndex);
  }

  @Nonnull
  private Expression parsePrimaryExpression() throws JsError {
    if (this.match(TokenType.LPAREN)) {
      return this.parseGroupExpression();
    }

    int startTokenIndex = this.tokenIndex;

    switch (this.lookahead.type.klass) {
    case Ident:
      return this.markLocation(new IdentifierExpression(this.parseIdentifier()), startTokenIndex);
    case StringLiteral:
      return this.parseStringLiteral();
    case NumericLiteral:
      return this.parseNumericLiteral();
    case Keyword: {
      if (this.match(TokenType.THIS)) {
        this.lex();
        return this.markLocation(new ThisExpression(), startTokenIndex);
      }
      if (this.match(TokenType.FUNCTION)) {
        return this.parseFunctionExpression();
      }
      break;
    }
    case BooleanLiteral: {
      Token token = this.lex();
      return this.markLocation(new LiteralBooleanExpression(token.type == TokenType.TRUE_LITERAL), startTokenIndex);
    }
    case NullLiteral: {
      this.lex();
      return this.markLocation(new LiteralNullExpression(), startTokenIndex);
    }
    case RegularExpression: {
      Token token1 = this.lex();
      return this.markLocation(new LiteralRegExpExpression(String.valueOf(token1.getValueString())), startTokenIndex);
    }
    default:
      if (this.match(TokenType.LBRACK)) {
        return this.parseArrayExpression();
      } else if (this.match(TokenType.LBRACE)) {
        return this.parseObjectExpression();
      } else if (this.match(TokenType.DIV) || this.match(TokenType.ASSIGN_DIV)) {
        this.skipComment();
        this.lookahead = this.scanRegExp();
        Token token = this.lex();
        return this.markLocation(new LiteralRegExpExpression(String.valueOf(token.getValueString())), startTokenIndex);
      }
    }

    throw this.createUnexpected(this.lex());
  }

  @Nonnull
  private LiteralNumericExpression parseNumericLiteral() throws JsError {
    int startTokenIndex = this.tokenIndex;
    if (this.strict && this.lookahead.octal) {
      throw this.createError(this.lookahead, STRICT_OCTAL_LITERAL);
    }
    Token token2 = this.lex();
    return this.markLocation(new LiteralNumericExpression(((NumericLiteralToken) token2).value), startTokenIndex);
  }

  @Nonnull
  private LiteralStringExpression parseStringLiteral() throws JsError {
    int startTokenIndex = this.tokenIndex;
    if (this.strict && this.lookahead.octal) {
      throw this.createError(this.lookahead, STRICT_OCTAL_LITERAL);
    }
    Token token2 = this.lex();
    return this.markLocation(new LiteralStringExpression(String.valueOf(token2.getValueString()), token2.slice),
        startTokenIndex);
  }

  @Nonnull
  private Identifier parseIdentifier() throws JsError {
    int startTokenIndex = this.tokenIndex;
    return this.markLocation(new Identifier(String.valueOf(this.lex().getValueString())), startTokenIndex);
  }

  @Nonnull
  private List<Expression> parseArgumentList() throws JsError {
    this.expect(TokenType.LPAREN);
    List<Expression> args = parseArguments();
    this.expect(TokenType.RPAREN);
    return args;
  }

  @Nonnull
  private List<Expression> parseArguments() throws JsError {
    if (this.match(TokenType.RPAREN) || this.eof()) {
      return List.nil();
    }
    Expression arg = this.parseAssignmentExpression();
    if (this.match(TokenType.COMMA)) {
      this.expect(TokenType.COMMA);
      return List.cons(arg, parseArguments());
    }
    return List.list(arg);
  }

  // 11.2 Left-Hand-Side Expressions;

  @Nonnull
  private Identifier parseNonComputedProperty() throws JsError {
    int startTokenIndex = this.tokenIndex;

    Token token = this.lex();

    if (!(token instanceof IdentifierLikeToken)) {
      throw this.createUnexpected(token);
    } else {
      return this.markLocation(new Identifier(String.valueOf(token.getValueString())), startTokenIndex);
    }
  }

  @Nonnull
  private Expression parseGroupExpression() throws JsError {
    this.expect(TokenType.LPAREN);
    Expression expr = this.parseExpression();
    this.expect(TokenType.RPAREN);
    return expr;
  }

  @Nonnull
  private FunctionExpression parseFunctionExpression() throws JsError {

    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.FUNCTION);

    Identifier id = null;
    String message = null;
    Token firstRestricted = null;
    if (!this.match(TokenType.LPAREN)) {
      Token token = this.lookahead;
      id = this.parseVariableIdentifier();
      if (token instanceof IdentifierLikeToken) {
        if (this.strict) {
          if (Utils.isRestrictedWord(id.name)) {
            throw this.createError(token, STRICT_FUNCTION_NAME);
          }
        } else {
          if (Utils.isRestrictedWord(id.name)) {
            firstRestricted = token;
            message = STRICT_FUNCTION_NAME;
          } else if (Utils.isStrictModeReservedWordES5(id.name)) {
            firstRestricted = token;
            message = STRICT_RESERVED_WORD;
          }
        }
      }
    }
    ParamsInfo info = this.parseParams(firstRestricted);

    if (info.message != null) {
      message = info.message;
    }

    boolean previousStrict = this.strict;
    FunctionBody body = this.parseFunctionBody();
    if (message != null) {
      if ((this.strict || body.isStrict()) && info.firstRestricted != null) {
        throw this.createError(info.firstRestricted, message);
      }
      if ((this.strict || body.isStrict()) && info.stricted != null) {
        throw this.createError(info.stricted, message);
      }
    }
    this.strict = previousStrict;
    return this.markLocation(new FunctionExpression(Maybe.fromNullable(id), List.from(info.params), body),
        startTokenIndex);
  }

  @Nonnull
  private ArrayExpression parseArrayExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.LBRACK);

    List<Maybe<Expression>> elements = parseArrayExpressionElements();

    this.expect(TokenType.RBRACK);

    return this.markLocation(new ArrayExpression(elements), startTokenIndex);
  }

  @Nonnull
  private List<Maybe<Expression>> parseArrayExpressionElements() throws JsError {
    if (this.match(TokenType.RBRACK)) {
      return List.nil();
    }

    Maybe<Expression> el;

    if (this.match(TokenType.COMMA)) {
      this.lex();
      el = Maybe.nothing();
    } else {
      el = Maybe.just(this.parseAssignmentExpression());
      if (!this.match(TokenType.RBRACK)) {
        this.expect(TokenType.COMMA);
      }
    }
    return List.cons(el, parseArrayExpressionElements());
  }

  @Nonnull
  private ObjectExpression parseObjectExpression() throws JsError {
    int startTokenIndex = this.tokenIndex;

    this.expect(TokenType.LBRACE);

    HashMap<String, ObjectPropertyCombination> propertyMap = new HashMap<>();
    List<ObjectProperty> properties = this.parseObjectExpressionItems(propertyMap);

    this.expect(TokenType.RBRACE);

    return this.markLocation(new ObjectExpression(properties), startTokenIndex);
  }

  @Nonnull
  private List<ObjectProperty> parseObjectExpressionItems(@Nonnull HashMap<String, ObjectPropertyCombination> propertyMap)
      throws JsError {
    if (this.match(TokenType.RBRACE)) {
      return List.nil();
    }

    ObjectProperty property = this.parseObjectProperty();
    ObjectPropertyKind kind = property.getKind();
    @Nonnull
    final String key = property.name.value;
    final ObjectPropertyCombination maybeValue = propertyMap.get(key);
    @Nonnull
    final ObjectPropertyCombination value = maybeValue == null ? ObjectPropertyCombination.NIL : maybeValue;

    if (propertyMap.containsKey(key)) {
      if (value.hasInit) {
        if (this.strict && kind == ObjectPropertyKind.InitProperty) {
          throw this.createError(STRICT_DUPLICATE_PROPERTY);
        } else if (kind != ObjectPropertyKind.InitProperty) {
          throw this.createError(ACCESSOR_DATA_PROPERTY);
        }
      } else {
        if (kind == ObjectPropertyKind.InitProperty) {
          throw this.createError(ACCESSOR_DATA_PROPERTY);
        } else if (value.hasGetter && kind == ObjectPropertyKind.GetterProperty
            || value.hasSetter && kind == ObjectPropertyKind.SetterProperty) {
          throw this.createError(ACCESSOR_GET_SET);
        }
      }
    }
    switch (kind) {
    case InitProperty:
      propertyMap.put(key, value.withInit());
      break;
    case GetterProperty:
      propertyMap.put(key, value.withGetter());
      break;
    case SetterProperty:
      propertyMap.put(key, value.withSetter());
      break;
    }

    if (!this.match(TokenType.RBRACE)) {
      this.expect(TokenType.COMMA);
    }

    return List.cons(property, parseObjectExpressionItems(propertyMap));
  }

  @Nonnull
  private PropertyName parseObjectPropertyKey() throws JsError {
    Token token = this.lookahead;

    // Note: This function is called only from parseObjectProperty(), where;
    // Eof and Punctuator tokens are already filtered out.

    if (token instanceof StringLiteralToken) {
      return new PropertyName(this.parseStringLiteral());
    }
    if (token instanceof NumericLiteralToken) {
      return new PropertyName(this.parseNumericLiteral());
    }
    if (token instanceof IdentifierLikeToken) {
      return new PropertyName(this.parseIdentifier());
    }

    throw this.createError(INVALID_PROPERTY_NAME);
  }

  @Nonnull
  private /* prop */ ObjectProperty parseObjectProperty() throws JsError {
    Token token = this.lookahead;
    int startTokenIndex = this.tokenIndex;

    if (token.type == TokenType.IDENTIFIER) {
      PropertyName key = this.parseObjectPropertyKey();
      String name = token.toString();
      if (name.length() == 3) {
        // Property Assignment: Getter and Setter.
        if ("get".equals(name) && !this.match(TokenType.COLON)) {
          key = this.parseObjectPropertyKey();
          this.expect(TokenType.LPAREN);
          this.expect(TokenType.RPAREN);
          FunctionBody body = this.parseFunctionBody();
          return this.markLocation(new Getter(key, body), startTokenIndex);
        } else if ("set".equals(name) && !this.match(TokenType.COLON)) {
          key = this.parseObjectPropertyKey();
          this.expect(TokenType.LPAREN);
          token = this.lookahead;
          if (token.type != TokenType.IDENTIFIER) {
            this.expect(TokenType.RPAREN);
            throw this.createError(token, UNEXPECTED_TOKEN, token.type.toString());
          } else {
            Identifier param = this.parseVariableIdentifier();
            this.expect(TokenType.RPAREN);
            FunctionBody body = this.parseFunctionBody();
            if ((this.strict || body.isStrict()) && Utils.isRestrictedWord(param.name)) {
              throw this.createError(STRICT_PARAM_NAME);
            }
            return this.markLocation(new Setter(key, param, body), startTokenIndex);
          }
        }
      }

      this.expect(TokenType.COLON);
      Expression value = this.parseAssignmentExpression();
      return this.markLocation(new DataProperty(key, value), startTokenIndex);
    }
    if (this.eof() || token.type.klass == TokenClass.Punctuator) {
      throw this.createUnexpected(token);
    } else {
      PropertyName key = this.parseObjectPropertyKey();
      this.expect(TokenType.COLON);
      Expression value = this.parseAssignmentExpression();
      return this.markLocation(new DataProperty(key, value), startTokenIndex);
    }
  }

  private static class ObjectPropertyCombination {
    public static final ObjectPropertyCombination NIL = new ObjectPropertyCombination(false, false, false);
    public final boolean hasInit;
    public final boolean hasGetter;
    public final boolean hasSetter;

    private ObjectPropertyCombination(boolean hasInit, boolean hasGetter, boolean hasSetter) {
      this.hasInit = hasInit;
      this.hasGetter = hasGetter;
      this.hasSetter = hasSetter;
    }

    public ObjectPropertyCombination withInit() {
      return new ObjectPropertyCombination(true, this.hasGetter, this.hasSetter);
    }

    public ObjectPropertyCombination withGetter() {
      return new ObjectPropertyCombination(this.hasInit, true, this.hasSetter);
    }

    public ObjectPropertyCombination withSetter() {
      return new ObjectPropertyCombination(this.hasInit, this.hasGetter, true);
    }
  }

  private static class ParamsInfo {
    @Nonnull
    final ArrayList<Identifier> params = new ArrayList<>();
    @Nullable
    Token stricted;
    @Nullable
    Token firstRestricted;
    @Nullable
    String message;
  }

  private static class ExprStackItem {
    final int startIndex;
    @Nonnull
    final Expression left;
    @Nonnull
    final BinaryOperator operator;
    final int precedence;

    ExprStackItem(int startIndex, @Nonnull Expression left, @Nonnull BinaryOperator operator) {
      this.startIndex = startIndex;
      this.left = left;
      this.operator = operator;
      this.precedence = operator.getPrecedence().ordinal();
    }
  }
}
