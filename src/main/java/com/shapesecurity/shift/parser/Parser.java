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

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.Thunk;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.Class;
import com.shapesecurity.shift.ast.operators.*;
import com.shapesecurity.shift.parser.token.IdentifierToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.crypto.Data;
import javax.xml.transform.Source;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
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
  private boolean isAssignmentTarget = true;
  @Nullable
  private JsError firstExprError;
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
      TokenizerState tokenizerState = this.saveTokenizerState();
      this.lex();
      if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.LET) || this.match(TokenType.LBRACE) || this.match(TokenType.LBRACK)) {
        this.restoreTokenizerState(tokenizerState);
        return true;
      } else {
        this.restoreTokenizerState(tokenizerState);
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
        if (isStringLiteral && stmt instanceof ExpressionStatement && ((ExpressionStatement) stmt).expression instanceof LiteralStringExpression) {
          directives.add(this.markLocation(directiveLocation, new Directive(text.substring(1, text.length() - 1))));
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
    switch (this.lookahead.type) {
      case FUNCTION:
        return this.parseFunction(false, true);
      case CLASS:
        return this.parseClass(false);
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
    ArrayList<VariableDeclarator> result = new ArrayList<>();
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
    if (bindingPatternsMustHaveInit && !(binding instanceof BindingIdentifier) && !this.match(TokenType.ASSIGN)) {
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
      case YIELD:
        return this.parseBindingIdentifier();
      case LBRACK:
        return this.parseArrayBinding();
      case LBRACE:
        return this.parseObjectBinding();
    }
    throw this.createUnexpected(this.lookahead);
  }

  private Binding parseObjectBinding() throws JsError {
    SourceLocation startLocation = this.getLocation();

    this.expect(TokenType.LBRACE);

    ArrayList<BindingProperty> properties = new ArrayList<>();
    while (!this.match(TokenType.RBRACE)) {
      properties.add(this.parseBindingProperty());
      if (!this.match(TokenType.RBRACE)) {
        this.expect(TokenType.COMMA);
      }
    }

    this.expect(TokenType.RBRACE);

    return this.markLocation(startLocation, new ObjectBinding(ImmutableList.from(properties)));
  }

  private BindingProperty parseBindingProperty() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Token token = this.lookahead;

    Pair<PropertyName, Maybe<Binding>> fromParsePropertyName = this.parsePropertyName();
    PropertyName name = fromParsePropertyName.a;
    Maybe<Binding> binding = fromParsePropertyName.b;

    if ((token.type == TokenType.IDENTIFIER || token.type == TokenType.YIELD) && name instanceof StaticPropertyName) {
      if (!this.match(TokenType.COLON)) {
        Maybe<Expression> defaultValue = Maybe.nothing();
        if (this.eat(TokenType.ASSIGN)) {
          boolean previousAllowYieldExpression = this.allowYieldExpression;
          if (this.inGeneratorParameter) {
            this.allowYieldExpression = false;
          }
          Expression expr = this.parseAssignmentExpression();
          defaultValue = Maybe.just(expr);
          this.allowYieldExpression = previousAllowYieldExpression;
        }
        return this.markLocation(startLocation, new BindingPropertyIdentifier((BindingIdentifier) binding.just(), defaultValue));
      }
    }
    this.expect(TokenType.COLON);
    binding = Maybe.just((Binding) this.parseBindingElement());
    return this.markLocation(startLocation, new BindingPropertyProperty(name, binding.just()));

  }

  private Binding parseArrayBinding() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.expect(TokenType.LBRACK);
    ArrayList<Maybe<BindingBindingWithDefault>> elements = new ArrayList<>();
    Maybe<Binding> restElement = Maybe.nothing();

    while (true) {
      if (this.match(TokenType.RBRACK)) {
        break;
      }
      Maybe<BindingBindingWithDefault> el;

      if (this.eat(TokenType.COMMA)) {
        el = null;
      } else {
        if (this.eat(TokenType.ELLIPSIS)) {
          restElement = Maybe.just(this.parseBindingIdentifier());
          break;
        } else {
          el = Maybe.just(this.parseBindingElement());
        }
        if (!this.match(TokenType.RBRACK)) {
          this.expect(TokenType.COMMA);
        }
      }
      elements.add(el);
    }

    this.expect(TokenType.RBRACK);

    return this.markLocation(startLocation, new ArrayBinding(ImmutableList.from(elements), restElement));
  }

  private BindingIdentifier parseBindingIdentifier() throws JsError {
    SourceLocation startLocation = this.getLocation();
    return this.markLocation(startLocation, new BindingIdentifier(this.parseIdentifier()));
  }

  private String parseIdentifier() throws JsError {
    if (this.match(TokenType.IDENTIFIER) || !this.allowYieldExpression && this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
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
    BindingBindingWithDefault bbwd = binding;
    if (this.eat(TokenType.ASSIGN)) {
      boolean previousInGeneratorParameter = this.inGeneratorParameter;
      boolean previousYieldExpression = this.allowYieldExpression;
      if (this.inGeneratorParameter) {
        this.allowYieldExpression = false;
      }
      this.inGeneratorParameter = false;
      Expression init = this.parseAssignmentExpression();
      bbwd = this.markLocation(startLocation, new BindingWithDefault(binding, init));
      this.inGeneratorParameter = previousInGeneratorParameter;
      this.allowYieldExpression = previousYieldExpression;
    }
    return bbwd;
  }

  private boolean isValidSimpleAssignmentTarget(Node node) {
    if (node instanceof IdentifierExpression || node instanceof ComputedMemberExpression
        || node instanceof StaticMemberExpression) {
      return true;
    }
    return false;
  }

  private Statement parseStatement() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Statement stmt = this.isolateCoverGrammar(this::parseStatementHelper);
    return this.markLocation(startLocation, stmt);
  }

  private Statement parseStatementHelper() throws JsError {
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
        Expression expr = this.parseExpression();
        if (expr instanceof IdentifierExpression && this.eat(TokenType.COLON)) {
          Statement labeledBody = this.match(TokenType.FUNCTION) ? this.parseFunction(false, false) : this.parseStatement();
          return new LabeledStatement(((IdentifierExpression) expr).getName(), labeledBody);
        } else {
          this.consumeSemicolon();
          return new ExpressionStatement(expr);
        }
      }
    }
  }

  // TODO: finish this method
  private Statement parseForStatement() throws JsError {
    this.lex();
    this.expect(TokenType.LPAREN);
    Maybe<Expression> test = Maybe.nothing();
    Maybe<Expression> right = Maybe.nothing();
    if (this.eat(TokenType.SEMICOLON)) {
      if (!this.match(TokenType.SEMICOLON)) {
        test = Maybe.just(this.parseExpression());
      }
      this.expect(TokenType.SEMICOLON);
      if (!this.match(TokenType.RPAREN)) {
        right = Maybe.just(this.parseExpression());
      }
      return new ForStatement(Maybe.nothing(), test, right, this.getIteratorStatementEpilogue());
    } else {
      boolean startsWithLet = this.match(TokenType.LET);
      boolean isForDecl = this.lookaheadLexicalDeclaration();
      SourceLocation leftLocation = this.getLocation();
      if (this.match(TokenType.VAR) || isForDecl) {
        boolean previousAllowIn = this.allowIn;
        this.allowIn = false;
        VariableDeclarationExpression init = this.parseVariableDeclaration(false);
        this.allowIn = previousAllowIn;

        if (((VariableDeclaration) init).declarators.length == 1 && (this.match((TokenType.IN)) || this.matchContextualKeyword("of"))) {
          if (this.match(TokenType.IN)) {
            if (((VariableDeclaration) init).declarators.index(0).just().init != null) {
              throw this.createError(ErrorMessages.INVALID_VAR_INIT_FOR_IN);
            }
            this.lex();
            right = Maybe.just(this.parseExpression());
            Statement body = this.getIteratorStatementEpilogue();
            return new ForInStatement((VariableDeclarationBinding) init, right.just(), body);
          } else {
            if (((VariableDeclaration) init).declarators.index(0).just().init != null) {
              throw this.createError(ErrorMessages.INVALID_VAR_INIT_FOR_OF);
            }
            this.lex();
            right = Maybe.just(this.parseAssignmentExpression());
            Statement body = this.getIteratorStatementEpilogue();
            return new ForOfStatement((VariableDeclarationBinding) init, right.just(), body);
          }
        } else {
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.SEMICOLON)) {
            test = Maybe.just(this.parseExpression());
          }
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.RPAREN)) {
            right = Maybe.just(this.parseExpression());
          }
          return new ForStatement(Maybe.just(init), test, right, this.getIteratorStatementEpilogue());
        }
      } else {
        boolean previousAllowIn = this.allowIn;
        this.allowIn = false;
        Expression expr = this.parseAssignmentExpressionOrBindingElement();
        this.allowIn = previousAllowIn;

        if (this.isAssignmentTarget && expr instanceof AssignmentExpression && (this.match(TokenType.IN) || this.matchContextualKeyword("of"))) {
          if (startsWithLet && this.matchContextualKeyword("of")) {
            throw this.createError(ErrorMessages.INVALID_LHS_IN_FOR_OF);
          }
          if (this.match(TokenType.IN)) {
            this.lex();
            right = Maybe.just(this.parseExpression());
            return new ForInStatement(Parser.transformDestructuring(expr), right.just(), this.getIteratorStatementEpilogue());
          } else {
            this.lex();
            right = Maybe.just(this.parseExpression());
            return new ForOfStatement(Parser.transformDestructuring(expr), right.just(), this.getIteratorStatementEpilogue());
          }
        } else {
//          if (this.firstExprError) {
//            throw this.firstExprError;
//          }
          while (this.eat(TokenType.COMMA)) {
            Expression rhs = this.parseAssignmentExpression();
            expr = this.markLocation(leftLocation, new BinaryExpression(BinaryOperator.Sequence, expr, rhs));
          }
          if (this.match(TokenType.IN)) {
            throw this.createError(ErrorMessages.INVALID_LHS_IN_FOR_IN);
          }
          if (this.matchContextualKeyword("of")) {
            throw this.createError(ErrorMessages.INVALID_LHS_IN_FOR_OF);
          }
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.SEMICOLON)) {
            test = Maybe.just(this.parseExpression());
          }
          this.expect(TokenType.SEMICOLON);
          if (!this.match(TokenType.RPAREN)) {
            right = Maybe.just(this.parseExpression());
          }
          return new ForStatement(Maybe.just(expr), test, right, this.getIteratorStatementEpilogue());
        }
      }
    }
  }

  private static BindingProperty transformDestructuring(ObjectProperty objectProperty) throws JsError {
    if (objectProperty instanceof DataProperty) {
      DataProperty dataProperty = (DataProperty) objectProperty;
      return new BindingPropertyProperty(dataProperty.name, transformDestructuringWithDefault(dataProperty.expression));
    } else if (objectProperty instanceof ShorthandProperty) {
      ShorthandProperty shorthandProperty = (ShorthandProperty) objectProperty;
      return new BindingPropertyIdentifier(new BindingIdentifier(shorthandProperty.name), Maybe.nothing());
    }
    throw new JsError(0, 0, 0, "not reached");
  }

  // TODO: preserve location information in transformDestructuring() functions by implementing copyLocation()
  private static Binding transformDestructuring(Expression node) throws JsError {
    if (node instanceof ObjectExpression) {
      ObjectExpression objectExpression = (ObjectExpression) node;
      ImmutableList<BindingProperty> properties = ImmutableList.nil();
      for (ObjectProperty p : objectExpression.properties) {
        properties = properties.cons(Parser.transformDestructuring(p));
      }
      return new ObjectBinding(properties);
    } else if (node instanceof ArrayExpression) {
      ArrayExpression arrayExpression = (ArrayExpression) node;
      Maybe<SpreadElementExpression> last = Maybe.join(arrayExpression.elements.maybeLast());
      if (last.isJust()) {
        NonEmptyImmutableList<Maybe<SpreadElementExpression>> elements = (NonEmptyImmutableList<Maybe<SpreadElementExpression>>) arrayExpression.elements;
        ImmutableList<Maybe<BindingBindingWithDefault>> newElements = ImmutableList.list();
        if (last.just() instanceof SpreadElement) {
          SpreadElement spreadElement = (SpreadElement) last.just();
          for (Maybe<SpreadElementExpression> maybeBbwd : elements.init()) {
            if (maybeBbwd.isJust()) {
              newElements = newElements.cons(Maybe.just(Parser.transformDestructuringWithDefault((Expression) maybeBbwd.just())));
            }
          }
          return new ArrayBinding(newElements, Maybe.just(Parser.transformDestructuring(spreadElement.expression)));
        } else {
          for (Maybe<SpreadElementExpression> maybeBbwd : elements) {
            if (maybeBbwd.isJust()) {
              newElements = newElements.cons(Maybe.just(Parser.transformDestructuringWithDefault((Expression) maybeBbwd.just())));
            }
          }
          return new ArrayBinding(newElements, Maybe.nothing());
        }
      }
    } else if (node instanceof IdentifierExpression) {
      return new BindingIdentifier(((IdentifierExpression) node).name);
    } else if (node instanceof ComputedMemberExpression || node instanceof StaticMemberExpression) {
      return (Binding) node;
    }
    throw new JsError(0, 0, 0, "not reached");
  }

  private static BindingBindingWithDefault transformDestructuringWithDefault(Expression node) throws JsError {
    if (node instanceof AssignmentExpression) {
      AssignmentExpression assignmentExpression = (AssignmentExpression) node;
      return new BindingWithDefault(transformDestructuring(assignmentExpression.binding), assignmentExpression.expression);
    }
    return transformDestructuring(node);
  }

  private static Binding transformDestructuring(Binding b) {
    return b;
  }

  private boolean matchContextualKeyword(String keyword) {
    return this.lookahead.type == TokenType.IDENTIFIER && keyword.equals(this.lookahead.type.name);
  }

  private Statement parseSwitchStatement() throws JsError {
    this.lex();
    this.expect(TokenType.LPAREN);
    Expression discriminant = this.parseExpression();
    this.expect(TokenType.RPAREN);
    this.expect(TokenType.LBRACE);

    if (this.eat(TokenType.RBRACE)) {
      return new SwitchStatement(discriminant, ImmutableList.nil());
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

  private ImmutableList<SwitchCase> parseSwitchCases() throws JsError {
    ArrayList<SwitchCase> result = new ArrayList<>();
    while (!(this.eof() || this.match(TokenType.RBRACE) || this.match(TokenType.DEFAULT))) {
      result.add(this.parseSwitchCase());
    }
    return ImmutableList.from(result);
  }

  private SwitchCase parseSwitchCase() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.expect(TokenType.CASE);
    return markLocation(startLocation, new SwitchCase(this.parseExpression(), this.parseSwitchCaseBody()));
  }

  private ImmutableList<Statement> parseSwitchCaseBody() throws JsError {
    this.expect(TokenType.COLON);
    return this.parseStatementListInSwitchCaseBody();
  }

  private ImmutableList<Statement> parseStatementListInSwitchCaseBody() throws JsError {
    ArrayList<Statement> result = new ArrayList<>();
    while (!(this.eof() || this.match(TokenType.RBRACE) || this.match(TokenType.DEFAULT) || this.match(TokenType.CASE))) {
      result.add(this.parseStatementListItem());
    }
    return ImmutableList.from(result);
  }

  private SwitchDefault parseSwitchDefault() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.expect(TokenType.DEFAULT);
    return this.markLocation(startLocation, new SwitchDefault(this.parseSwitchCaseBody()));
  }

  private Statement parseDebuggerStatement() throws JsError {
    this.lex();
    this.consumeSemicolon();
    return new DebuggerStatement();
  }

  private Statement parseDoWhileStatement() throws JsError {
    this.lex();
    Statement body = this.parseStatement();
    this.expect(TokenType.WHILE);
    this.expect(TokenType.LPAREN);
    Expression test = this.parseExpression();
    this.expect(TokenType.RPAREN);
    this.eat(TokenType.SEMICOLON);
    return new DoWhileStatement(test, body);
  }

  private Statement parseContinueStatement() throws JsError {
    this.lex();

    if (this.eat(TokenType.SEMICOLON) || this.hasLineTerminatorBeforeNext) {
      return new ContinueStatement(Maybe.nothing());
    }

    Maybe<String> label = Maybe.nothing();
    if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
      label = Maybe.just(this.parseIdentifier());
    }

    this.consumeSemicolon();

    return new ContinueStatement(label);

  }

  private Statement parseBreakStatement() throws JsError {
    this.lex();
    if (this.eat(TokenType.SEMICOLON) || this.hasLineTerminatorBeforeNext) {
      return new BreakStatement(Maybe.nothing());
    }

    Maybe<String> label = Maybe.nothing();
    if (this.match(TokenType.IDENTIFIER) || this.match(TokenType.YIELD) || this.match(TokenType.LET)) {
      label = Maybe.just(this.parseIdentifier());
    }

    this.consumeSemicolon();

    return new BreakStatement(label);
  }

  private Statement parseTryStatement() throws JsError {
    this.lex();
    Block body = this.parseBlock();

    if (this.match(TokenType.CATCH)) {
      CatchClause catchClause = this.parseCatchClause();
      if (this.eat(TokenType.FINALLY)) {
        Block finalizer = this.parseBlock();
        return new TryFinallyStatement(body, Maybe.just(catchClause), finalizer);
      }
      return new TryCatchStatement(body, catchClause);
    }
    if (this.eat(TokenType.FINALLY)) {
      Block finalizer = this.parseBlock();
      return new TryFinallyStatement(body, Maybe.nothing(), finalizer);
    } else {
      throw this.createError(ErrorMessages.NO_CATCH_OR_FINALLY);
    }
  }

  private CatchClause parseCatchClause() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.lex();
    this.expect(TokenType.LPAREN);
    if (this.match(TokenType.RPAREN) || this.match(TokenType.LPAREN)) {
      throw this.createUnexpected(this.lookahead);
    }
    Binding binding = this.parseBindingTarget();
    this.expect(TokenType.RPAREN);
    Block body = this.parseBlock();

    return this.markLocation(startLocation, new CatchClause(binding, body));
  }

  private Statement parseThrowStatement() throws JsError {
    this.lex();
    if (this.hasLineTerminatorBeforeNext) {
      throw this.createErrorWithLocation(this.getLocation(), ErrorMessages.NEWLINE_AFTER_THROW);
    }
    Expression expression = this.parseExpression();
    this.consumeSemicolon();
    return new ThrowStatement(expression);

  }

  private Statement parseReturnStatement() throws JsError {
    if (!this.inFunctionBody) {
      throw this.createError(ErrorMessages.ILLEGAL_RETURN);
    }

    this.lex();

    if (this.hasLineTerminatorBeforeNext) {
      return new ReturnStatement(Maybe.nothing());
    }

    Maybe<Expression> expression = Maybe.nothing();

    if (!this.match(TokenType.SEMICOLON) && !this.match(TokenType.RBRACE) && !this.eof()) {
      expression = Maybe.just(this.parseExpression());
    }

    this.consumeSemicolon();
    return new ReturnStatement(expression);
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

  private Statement parseWithStatement() throws JsError {
    this.lex();
    this.expect(TokenType.LPAREN);
    Expression test = this.parseExpression();
    Statement body = this.getIteratorStatementEpilogue();
    return new WithStatement(test, body);
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
    if (this.match(TokenType.COMMA)) {
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

  private <T> T isolateCoverGrammar(ExceptionalSupplier<T> parser) throws JsError {
    boolean oldIsBindingElement = this.isBindingElement,
        oldIsAssignmentTarget = this.isAssignmentTarget;
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

  private Expression parseAssignmentExpression() throws JsError {
    return this.isolateCoverGrammar(this::parseAssignmentExpressionOrBindingElement);
  }

  private Expression parseAssignmentExpressionOrBindingElement() throws JsError {
    SourceLocation startLocation = this.getLocation();

    if (this.allowYieldExpression && !this.inGeneratorParameter && this.match(TokenType.YIELD)) {
      this.isBindingElement = this.isAssignmentTarget = false;
      return this.parseYieldExpression();
    }

    Expression expr = this.parseConditionalExpression();

    // TODO: arrow
//    if (!this.hasLineTerminatorBeforeNext && this.match(TokenType.ARROW)) {
//      this.isBindingElement = this.isAssignmentTarget = false;
//      this.firstExprError = null;
//      return this.parseArrowExpressionTail(expr, startLocation);
//    }

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
        isAssignmentOperator = true;
        break;
    }
    Binding binding;
    if (isAssignmentOperator) {
      if (!this.isAssignmentTarget || !isValidSimpleAssignmentTarget(expr)) {
        throw this.createError(ErrorMessages.INVALID_LHS_IN_ASSIGNMENT);
      }
      binding = transformDestructuring(expr);
    } else if (operator.type == TokenType.ASSIGN) {
      if (!this.isAssignmentTarget) {
        throw this.createError(ErrorMessages.INVALID_LHS_IN_ASSIGNMENT);
      }
      binding = transformDestructuring(expr);
    } else {
      return expr;
    }

    this.lex();
    boolean previousInGeneratorParameter = this.inGeneratorParameter;
    this.inGeneratorParameter = false;
    Expression rhs = this.parseAssignmentExpression();

    this.inGeneratorParameter = previousInGeneratorParameter;
//    this.firstExprError = null;
    if (operator.type == TokenType.ASSIGN) {
      return this.markLocation(startLocation, new AssignmentExpression(binding, rhs));
    } else {
      return this.markLocation(startLocation, new CompoundAssignmentExpression(this.lookupCompoundAssignmentOperator(operator), (BindingIdentifierMemberExpression) binding, rhs));
    }
  }

  private Expression parseYieldExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();

    this.lex();
    if (this.hasLineTerminatorBeforeNext) {
      return this.markLocation(startLocation, new YieldExpression(Maybe.nothing()));
    }
    boolean isGenerator = this.eat(TokenType.MUL);
    Maybe<Expression> expr = Maybe.nothing();
    if (isGenerator || this.lookaheadAssignmentExpression()) {
      expr = Maybe.just(this.parseAssignmentExpression());
    }
    if (isGenerator) {
      return this.markLocation(startLocation, new YieldGeneratorExpression(expr.just()));
    } else {
      return this.markLocation(startLocation, new YieldExpression(expr));
    }
  }

  private boolean lookaheadAssignmentExpression() {
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
      case THIS:
      case TRUE_LITERAL:
      case YIELD:
      case TEMPLATE:
        return true;
    }
    return false;

  }

  private Expression parseConditionalExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Expression test = this.parseBinaryExpression();
    if (this.firstExprError != null) {
      return test;
    }
    if (this.eat(TokenType.CONDITIONAL)) {
      this.isBindingElement = this.isAssignmentTarget = false;
      boolean previousAllowIn = this.allowIn;
      this.allowIn = true;
      Expression consequent = this.isolateCoverGrammar(this::parseAssignmentExpression);
      this.allowIn = previousAllowIn;
      this.expect(TokenType.COLON);
      Expression alternate = this.isolateCoverGrammar(this::parseAssignmentExpression);
      return this.markLocation(startLocation, new ConditionalExpression(test, consequent, alternate));
    }
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
    Expression expr = this.isolateCoverGrammar(this::parseUnaryExpression);
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
      expr = this.isolateCoverGrammar(this::parseUnaryExpression);

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
    Expression operand = this.isolateCoverGrammar(this::parseUnaryExpression);

    Node node;
    UpdateOperator updateOperator = this.lookupUpdateOperator(operatorToken);
    if (updateOperator != null) {
      return createUpdateExpression(startLocation, operand, updateOperator, true);
    }
    UnaryOperator operator = this.lookupUnaryOperator(operatorToken);
    assert operator != null;
    return new UnaryExpression(operator, operand);
  }

  @NotNull
  private Expression parseUpdateExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Expression operand = (Expression) this.parseLeftHandSideExpression(true);
    if (this.hasLineTerminatorBeforeNext) return operand;
    UpdateOperator operator = this.lookupUpdateOperator(this.lookahead);
    if (operator == null) {
      return operand;
    }
    this.lex();
    return createUpdateExpression(startLocation, operand, operator, false);
  }

  @NotNull
  private Expression createUpdateExpression(@NotNull SourceLocation startLocation, @NotNull Expression operand, @NotNull UpdateOperator operator, @NotNull boolean isPrefix) throws JsError {
    BindingIdentifierMemberExpression restrictedOperand;
    if (operand instanceof MemberExpression) {
      restrictedOperand = (MemberExpression) operand;
    } else if (operand instanceof IdentifierExpression) {
      String name = ((IdentifierExpression) operand).name;
      restrictedOperand = operand.loc.map(loc -> new BindingIdentifier(loc, name)).orJustLazy(Thunk.from(() -> new BindingIdentifier(name)));
    } else {
      throw this.createError("Cannot increment/decrement expression of type " + operand.getClass().getName());
    }
    return this.markLocation(startLocation, new UpdateExpression(isPrefix, operator, restrictedOperand));
  }


  private Expression parseNumericLiteral() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Token token = this.lex();
    return this.markLocation(startLocation, new LiteralNumericExpression(Double.parseDouble(token.toString())));
  }

  private ExpressionSuper parseLeftHandSideExpression(boolean allowCall) throws JsError {
    SourceLocation startLocation = this.getLocation();
    boolean previousAllowIn = this.allowIn;
    this.allowIn = allowCall;

    Maybe<ExpressionSuper> expr = Maybe.nothing();
    Token token = this.lookahead;

    if (this.eat(TokenType.SUPER)) {
      this.isBindingElement = this.isAssignmentTarget = false;
      expr = Maybe.just(this.markLocation(startLocation, new Super()));

      if (this.match(TokenType.LPAREN)) {
        if (allowCall) {
          expr = Maybe.just((Expression) this.markLocation(startLocation, new CallExpression(expr.just(), this.parseArgumentList())));
        }
      } else if (this.match(TokenType.LBRACK)) {
        expr = Maybe.just((Expression) this.markLocation(startLocation, new ComputedMemberExpression(this.parseComputedMember(), expr.just())));
        this.isAssignmentTarget = true;
      } else if (this.match(TokenType.PERIOD)) {
        expr = Maybe.just((Expression) this.markLocation(startLocation, new StaticMemberExpression(this.parseStaticMember(), expr.just())));
        this.isAssignmentTarget = true;
      } else {
        throw this.createUnexpected(token);
      }
    } else if (this.match(TokenType.NEW)) {
      this.isBindingElement = this.isAssignmentTarget = false;
      expr = Maybe.just(this.parseNewExpression());
    } else {
      expr = Maybe.just(this.parsePrimaryExpression());
      if (this.firstExprError != null) {
        return expr.just();
      }
    }

    while (true) {
      if (allowCall && this.match((TokenType.LPAREN))) {
        this.isBindingElement = this.isAssignmentTarget = false;
        expr = Maybe.just(this.markLocation(startLocation, new CallExpression(expr.just(), this.parseArgumentList())));
//      } else if (this.match(TokenType.TEMPLATE)) {
//        this.isBindingElement = this.isAssignmentTarget = false;
//        return this.markLocation(startLocation, new TemplateExpression(Maybe.just(expr), this.parseTemplateElements()));
      } else if (this.match(TokenType.LBRACK)) {
        this.isBindingElement = false;
        this.isAssignmentTarget = true;
        expr = Maybe.just(this.markLocation(startLocation, new ComputedMemberExpression(this.parseComputedMember(), expr.just())));
      } else if (this.match(TokenType.PERIOD)) {
        this.isBindingElement = false;
        this.isAssignmentTarget = true;
        expr = Maybe.just(this.markLocation(startLocation, new StaticMemberExpression(this.parseStaticMember(), expr.just())));
      } else {
        break;
      }
    }

    this.allowIn = previousAllowIn;
    return expr.just();
  }

  private String parseStaticMember() throws JsError {
    this.lex();
    if (!this.isIdentifierName(this.lookahead.type.klass)) {
      throw this.createUnexpected(this.lookahead);
    } else {
      return this.lex().toString();
    }
  }

  private Expression parseComputedMember() throws JsError {
    this.lex();
    Expression expr = this.parseExpression();
    this.expect(TokenType.RBRACK);
    return expr;
  }

//  private ImmutableList<ExpressionTemplateElement> parseTemplateElements() {
//    // TODO: implement this
//
//  }

  private Expression parseNewExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.lex();
    if (this.eat(TokenType.PERIOD)) {
      Token ident = this.expect(TokenType.IDENTIFIER);
      if ((!ident.toString().equals("target"))) {
        throw this.createUnexpected(ident);
      }
      return this.markLocation(startLocation, new NewTargetExpression());
    }
    ExpressionSuper callee = this.isolateCoverGrammar(() -> this.parseLeftHandSideExpression(false));
//    if (!(callee instanceof Expression)) {
//      createUnexpected(this.lookahead);
//    }
    return this.markLocation(startLocation, new NewExpression((Expression) callee, this.match(TokenType.LPAREN) ?
        this.parseArgumentList() : ImmutableList.nil()));
  }

  private ImmutableList parseArgumentList() throws JsError {
    this.lex();
    ImmutableList args = this.parseArguments();
    this.expect(TokenType.RPAREN);
    return args;
  }

  private ImmutableList parseArguments() throws JsError {
    ArrayList<SpreadElementExpression> result = new ArrayList();
    while (true) {
      if (this.match(TokenType.RPAREN) || this.eof()) {
        return ImmutableList.from(result);
      }
      SpreadElementExpression arg;
      if (this.match(TokenType.ELLIPSIS)) {
        SourceLocation startLocation = this.getLocation();
        arg = this.markLocation(startLocation, new SpreadElement(this.parseAssignmentExpression()));
      } else {
        arg = this.parseAssignmentExpression();
      }
      result.add(arg);
      if (!this.eat(TokenType.COMMA)) {
        break;
      }
    }
    return ImmutableList.from(result);
  }

  private Expression parsePrimaryExpression() throws JsError {
    if (this.match(TokenType.LPAREN)) {
      return this.parseGroupExpression();
    }
    SourceLocation startLocation = this.getLocation();

    switch (this.lookahead.type) {
      case IDENTIFIER:
      case YIELD:
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
      case STRING:
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.parseStringLiteral();
      case LBRACK:
        return this.parseArrayExpression();
      case THIS:
        this.lex();
        this.isBindingElement = this.isAssignmentTarget = false;
        return new ThisExpression();
      case LBRACE:
        return this.parseObjectExpression();
      case CLASS:
        this.isBindingElement = this.isAssignmentTarget = false;
        return this.parseClass();
      // TODO: template
      // TODO: regex
      default:
        throw this.createUnexpected(this.lookahead);
    }
  }

  private Expression parseStringLiteral() throws JsError {
    SourceLocation startLocation = this.getLocation();
    return this.markLocation(startLocation, new LiteralStringExpression(this.lex().toString()));
  }

  private Expression parseArrayExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.lex();

    ArrayList<Maybe<SpreadElementExpression>> exprs = new ArrayList<>();

    while (true) {
      if (this.match(TokenType.RBRACK)) {
        break;
      }
      if (this.eat(TokenType.COMMA)) {
        exprs.add(Maybe.nothing());
      } else {
        SourceLocation elementLocation = this.getLocation();
        SpreadElementExpression expr;
        if (this.eat(TokenType.ELLIPSIS)) {
          // Spread/Rest element
          expr = this.parseAssignmentExpressionOrBindingElement();
          if (!this.isAssignmentTarget && this.firstExprError != null) {
            throw this.firstExprError;
          }
          expr = this.markLocation(elementLocation, new SpreadElement((Expression) expr));
          if (!this.match(TokenType.RBRACK)) {
            this.isBindingElement = this.isAssignmentTarget = false;
          }
        } else {
          expr = this.parseAssignmentExpressionOrBindingElement();
          if (!this.isAssignmentTarget && this.firstExprError != null) {
            throw this.firstExprError;
          }
        }
        exprs.add(Maybe.just((Expression) expr));

        if (!this.match(TokenType.RBRACK)) {
          this.expect(TokenType.COMMA);
        }
      }
    }
    this.expect(TokenType.RBRACK);

    return this.markLocation(startLocation, new ArrayExpression(ImmutableList.from(exprs)));
  }

  private Expression parseObjectExpression() throws JsError {
    SourceLocation startLocation = this.getLocation();
    this.lex();
    ArrayList<ObjectProperty> properties = new ArrayList<>();
    while (!this.match(TokenType.RBRACE)) {
      ObjectProperty property = (ObjectProperty) this.parsePropertyDefinition();
      properties.add(property);
      if (!this.match(TokenType.RBRACE)) {
        this.expect(TokenType.COMMA);
      }
    }
    this.expect(TokenType.RBRACE);
    return this.markLocation(startLocation, new ObjectExpression(ImmutableList.from(properties)));
  }

  private Expression parseGroupExpression() throws JsError {
    Expression rest = null;
    Token start = this.expect(TokenType.LPAREN);
    if (this.eat(TokenType.RPAREN)) {
      // TODO arrow stuff
    } else if (this.eat(TokenType.ELLIPSIS)) {
      // TODO arrow stuff
    }

    SourceLocation startLocation = this.getLocation();
    Expression group = this.parseAssignmentExpressionOrBindingElement();

    while (this.eat(TokenType.COMMA)) {
      // TODO rest of function
      Expression expr = this.parseAssignmentExpressionOrBindingElement();
      group = new BinaryExpression(BinaryOperator.Sequence, group, expr);
    }

    this.expect(TokenType.RPAREN);
    return group;
  }

  private Node parsePropertyDefinition() throws JsError {
    SourceLocation startLocation = this.getLocation();
    Token token = this.lookahead;

    Pair<Node, String> fromParseMethodDefinition = this.parseMethodDefinition();
    Node methodOrKey = fromParseMethodDefinition.a;
    String kind = fromParseMethodDefinition.b;

    if (kind.equals("method")) {
      this.isBindingElement = this.isAssignmentTarget = false;
      return methodOrKey;
    } else if (kind.equals("identifier")) {
//      if (this.eat(TokenType.ASSIGN)) {
//        Expression init = this.isolateCoverGrammar(this::parseAssignmentExpression);
////        this.firstExprError = this.createErrorWithLocation(startLocation, ErrorMessages.ILLEGAL_PROPERTY);
//        return this.markLocation(startLocation, new BindingPropertyIdentifier(transformDestructuring(methodOrKey), init)); //TODO: transform destructuring for node input
//      } else
      if (!this.match(TokenType.COLON)) {
        if (token.type != TokenType.IDENTIFIER && token.type != TokenType.YIELD && token.type != TokenType.LET) {
          throw this.createUnexpected(token);
        }
        return this.markLocation(startLocation, new ShorthandProperty(((StaticPropertyName) methodOrKey).value));
      }
    }

    this.expect(TokenType.COLON);

    Expression expr = this.parseAssignmentExpressionOrBindingElement();
    return this.markLocation(startLocation, new DataProperty(expr, (PropertyName) methodOrKey));
  }

  private Pair<Node, String> parseMethodDefinition() throws JsError {
    Token token = this.lookahead;
    SourceLocation startLocation = this.getLocation();

    boolean isGenerator = this.eat(TokenType.MUL);

    Pair<PropertyName, Maybe<Binding>> fromParsePropertyName = this.parsePropertyName();
    PropertyName name = fromParsePropertyName.a;
//    Binding binding = fromParsePropertyName.b.just();

    if (!isGenerator && token.type == TokenType.IDENTIFIER) {
      String tokenName = token.toString();
      if (tokenName.length() == 3) {
        // Property Assignment: Getter and Setter.
        if (tokenName.equals("get") && this.lookaheadPropertyName()) {
          name = this.parsePropertyName().a;
          this.expect(TokenType.LPAREN);
          this.expect(TokenType.RPAREN);
          FunctionBody body = this.parseFunctionBody();
          return new Pair<>(this.markLocation(startLocation, new Getter(body, name)), "method");
        } else if (tokenName.equals("set") && this.lookaheadPropertyName()) {
          name = this.parsePropertyName().a;
          this.expect(TokenType.LPAREN);
          BindingBindingWithDefault param = this.parseBindingElement();
          this.expect(TokenType.RPAREN);
          boolean previousYield = this.allowYieldExpression;
          this.allowYieldExpression = false;
          FunctionBody body = this.parseFunctionBody();
          this.allowYieldExpression = previousYield;
          return new Pair<>(this.markLocation(startLocation, new Setter(param, body, name)), "method");
        }
      }
    }

    if (this.match(TokenType.LPAREN)) {
      boolean previousYield = this.allowYieldExpression;
      boolean previousInGeneratorParameter = this.inGeneratorParameter;
      this.inGeneratorParameter = isGenerator;
      this.allowYieldExpression = isGenerator;
      FormalParameters params = this.parseParams();
      this.inGeneratorParameter = previousInGeneratorParameter;
      this.allowYieldExpression = previousYield;
      this.allowYieldExpression = isGenerator;

      FunctionBody body = this.parseFunctionBody();
      this.allowYieldExpression = previousYield;

      return new Pair<>(this.markLocation(startLocation, new Method(isGenerator, params, body, name)), "method");
    }

    if (isGenerator && this.match(TokenType.COLON)) {
      throw this.createUnexpected(this.lookahead);
    }

    return new Pair<>(name, this.isIdentifierName(token.type.klass) ? "identifier" : "property");
  }

  private boolean lookaheadPropertyName() {
    switch (this.lookahead.type) {
      case NUMBER:
      case STRING:
      case LBRACK:
        return true;
      default:
        return this.isIdentifierName(this.lookahead.type.klass);
    }
  }

  private Pair<PropertyName, Maybe<Binding>> parsePropertyName() throws JsError {
    Token token = this.lookahead;
    SourceLocation startLocation = this.getLocation();

    if (this.eof()) {
      throw this.createUnexpected(token);
    }

    switch (token.type) {
      case STRING:
        String stringValue = ((LiteralStringExpression) this.parseStringLiteral()).value;
        return new Pair<>(this.markLocation(startLocation, new StaticPropertyName(stringValue)), Maybe.nothing());
      case NUMBER:
        Expression numLiteral = this.parseNumericLiteral();
        double numberValue = numLiteral instanceof LiteralInfinityExpression ? 1.0 : ((LiteralNumericExpression) numLiteral).value;
        int val = (int) numberValue;
        Integer number = new Integer(val);
        return new Pair<>(this.markLocation(startLocation, new StaticPropertyName((number.toString()))), Maybe.nothing());
      case LBRACK:
        boolean previousYield = this.allowYieldExpression;
        if (this.inGeneratorParameter) {
          this.allowYieldExpression = false;
        }
        this.lex();
        Expression expr = this.parseAssignmentExpression();
        this.expect(TokenType.RBRACK);
        this.allowYieldExpression = previousYield;
        return new Pair<>(this.markLocation(startLocation, new ComputedPropertyName(expr)), Maybe.nothing());
    }

    String name = this.parseIdentifierName();
    Maybe<Binding> maybeBinding = Maybe.just(new BindingIdentifier(name));
    return new Pair<>(this.markLocation(startLocation, new StaticPropertyName(name)), maybeBinding); // TODO mark location on binding part
  }

  private String parseIdentifierName() throws JsError {
    if (this.isIdentifierName(this.lookahead.type.klass)) {
      return this.lex().toString();
    } else {
      throw this.createUnexpected(this.lookahead);
    }
  }

  private boolean isIdentifierName(TokenClass klass) {
    if (klass.getName().equals("Identifier") || klass.getName().equals("Keyword")) {
      return true;
    }
    return false;
  }

  // class expression, isExpr = true
  private Expression parseClass() throws JsError {
    SourceLocation startLocation = this.getLocation();

    this.lex();
    Maybe<BindingIdentifier> name = Maybe.nothing();
    Maybe<Expression> heritage = Maybe.nothing();

    if (this.match(TokenType.IDENTIFIER)) {
      name = Maybe.just(this.parseBindingIdentifier());
    }

    boolean previousInGeneratorParameter = this.inGeneratorParameter;
    boolean previousParamYield = this.allowYieldExpression;
    this.inGeneratorParameter = false;
    this.allowYieldExpression = false;
    if (this.eat(TokenType.EXTENDS)) {
      heritage = Maybe.just((Expression) this.isolateCoverGrammar(() -> this.parseLeftHandSideExpression(true)));
    }

    this.expect(TokenType.LBRACE);
    ArrayList<ClassElement> elements = new ArrayList<>();
    while (!this.eat(TokenType.RBRACE)) {
      if (this.eat(TokenType.SEMICOLON)) {
        continue;
      }
      boolean isStatic = false;
      Pair<Node, String> fromParseMethodDefinition = this.parseMethodDefinition();
      Node methodOrKey = fromParseMethodDefinition.a;
      String kind = fromParseMethodDefinition.b;
      if (kind.equals("identifier") && ((StaticPropertyName) methodOrKey).value.equals("static")) {
        isStatic = true;
        fromParseMethodDefinition = this.parseMethodDefinition();
        methodOrKey = fromParseMethodDefinition.a;
        kind = fromParseMethodDefinition.b;
      }
      if (kind.equals("method")) {
        elements.add(new ClassElement(isStatic, (MethodDefinition) methodOrKey));
      } else {
        throw this.createError("Only methods are allowed in classes");
      }
    }
    this.allowYieldExpression = previousParamYield;
    this.inGeneratorParameter = previousInGeneratorParameter;
    return this.markLocation(startLocation, new ClassExpression(name, heritage, ImmutableList.from(elements)));
  }

  // class declaration, isExpr = false
  private Statement parseClass(boolean inDefault) throws JsError {
    SourceLocation startLocation = this.getLocation();

    this.lex();
    Maybe<BindingIdentifier> name = Maybe.nothing();
    Maybe<Expression> heritage = Maybe.nothing();

    if (this.match(TokenType.IDENTIFIER)) {
      name = Maybe.just(this.parseBindingIdentifier());
    } else {
      if (inDefault) {
        name = Maybe.just(this.markLocation(startLocation, new BindingIdentifier("*default*")));
      } else {
        throw this.createUnexpected(this.lookahead);
      }
    }

    boolean previousInGeneratorParameter = this.inGeneratorParameter;
    boolean previousParamYield = this.allowYieldExpression;

    if (this.eat(TokenType.EXTENDS)) {
      heritage = Maybe.just((Expression) this.isolateCoverGrammar(() -> this.parseLeftHandSideExpression(true)));
    }

    this.expect(TokenType.LBRACE);
    ArrayList<ClassElement> elements = new ArrayList<>();
    while (!this.eat(TokenType.RBRACE)) {
      if (this.eat(TokenType.SEMICOLON)) {
        continue;
      }
      boolean isStatic = false;
      Pair<Node, String> fromParseMethodDefinition = this.parseMethodDefinition();
      Node methodOrKey = fromParseMethodDefinition.a;
      String kind = fromParseMethodDefinition.b;
      if (kind.equals("identifier") && ((StaticPropertyName) methodOrKey).value.equals("static")) {
        isStatic = true;
        fromParseMethodDefinition = this.parseMethodDefinition();
        methodOrKey = fromParseMethodDefinition.a;
        kind = fromParseMethodDefinition.b;
      }
      if (kind.equals("method")) {
        elements.add(new ClassElement(isStatic, (MethodDefinition) methodOrKey));
      } else {
        throw this.createError("Only methods are allowed in classes");
      }
    }
    this.allowYieldExpression = previousParamYield;
    this.inGeneratorParameter = previousInGeneratorParameter;
    return this.markLocation(startLocation, new ClassDeclaration(name.just(), heritage, ImmutableList.from(elements)));
  }

  @Nullable
  private CompoundAssignmentOperator lookupCompoundAssignmentOperator(@NotNull Token token) {
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
      default:
        return null; // should not happen
    }
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
