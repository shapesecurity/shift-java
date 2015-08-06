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

package com.shapesecurity.shift.codegen;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.Reducer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnqualifiedFieldAccess")
public final class CodeGen implements Reducer<CodeRep> {
  public static final CodeGen COMPACT = new CodeGen(new CodeRepFactory());
  public static final CodeGen PRETTY = new CodeGen(new FormattedCodeRepFactory());
  private final CodeRepFactory factory;

  protected CodeGen(@NotNull CodeRepFactory factory) {
    this.factory = factory;
  }

  @NotNull
  public static String codeGen(@NotNull Script script) {
    return codeGen(script, false);
  }

  @NotNull
  public static String codeGen(@NotNull Module module) {
    return codeGen(module, false);
  }

  @NotNull
  public static String codeGen(@NotNull Script script, boolean pretty) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    Director.reduceScript(pretty ? PRETTY : COMPACT, script).emit(ts, false);
    return sb.toString();
  }

  @NotNull
  public static String codeGen(@NotNull Module module, boolean pretty) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    Director.reduceModule(pretty ? PRETTY : COMPACT, module).emit(ts, false);
    return sb.toString();
  }

  private char decodeUtf16(char lead, char trail) {
    return (char)((lead - 0xD800) * 0x400 + (trail - 0xDC00) + 0x10000);
  }

  private String escapeStringLiteral(String stringValue) {
    String result = "";
    int nSingle = 0;
    int nDouble = 0;
    for (int i = 0, l = stringValue.length(); i < l; ++i) {
      char ch = stringValue.charAt(i);
      if (ch == '\"') {
        ++nDouble;
      } else if (ch == '\'') {
        ++nSingle;
      }
    }
    char delim = nDouble > nSingle ? '\'' : '\"';
    result += delim;
    for (int i = 0; i < stringValue.length(); i++) {
      char ch = stringValue.charAt(i);
      if (ch == delim) {
        result += "\\" + delim;
      } else if (ch == '\b') {
        result += "\\b";
      } else if (ch == '\t') {
        result += "\\t";
      } else if (ch == '\n') {
        result += "\\n";
      } else if (ch == '\u000B') {
        result += "\\v";
      } else if (ch == '\u000C') {
        result += "\\f";
      } else if (ch == '\r') {
        result += "\\r";
      } else if (ch == '\\') {
        result += "\\\\";
      } else if (ch == '\u2028') {
        result += "\\u2028";
      } else if (ch == '\u2029') {
        result += "\\u2029";
      } else {
        result += ch;
      }
    }
    result += delim;
    return result;
  }

  @NotNull
  private CodeRep getAssignmentExpr(@NotNull Maybe<CodeRep> state) {
    if (state.isJust()) {
      return state.just().containsGroup ? factory.paren(state.just()) : state.just();
    } else {
      return factory.empty();
    }
  }

  private Precedence getBinaryPrecedence(BinaryOperator operator) {
    switch (operator) {
      case Sequence:
        return Precedence.SEQUENCE;
      case LogicalOr:
        return Precedence.LOGICAL_OR;
      case LogicalAnd:
        return Precedence.LOGICAL_AND;
      case BitwiseOr:
        return Precedence.BITWISE_OR;
      case BitwiseXor:
        return Precedence.BITWISE_XOR;
      case BitwiseAnd:
        return Precedence.BITWISE_AND;
      case Plus:
      case Minus:
        return Precedence.ADDITIVE;
      case Equal:
      case NotEqual:
      case StrictEqual:
      case StrictNotEqual:
        return Precedence.EQUALITY;
      case Mul:
      case Div:
      case Rem:
        return Precedence.MULTIPLICATIVE;
      case LessThan:
      case LessThanEqual:
      case GreaterThan:
      case GreaterThanEqual:
      case In:
      case Instanceof:
        return Precedence.RELATIONAL;
      case Left:
      case Right:
      case UnsignedRight:
        return Precedence.SHIFT;
      default: // SHOULD NOT BE HERE
        return Precedence.ASSIGNMENT;
    }
  }

  @NotNull
  private Precedence getPrecedence(Node node) {
    if (node instanceof ArrayExpression
      || node instanceof ClassExpression
      || node instanceof FunctionExpression
      || node instanceof IdentifierExpression
      || node instanceof LiteralBooleanExpression
      || node instanceof LiteralNullExpression
      || node instanceof LiteralNumericExpression
      || node instanceof LiteralInfinityExpression
      || node instanceof LiteralRegExpExpression
      || node instanceof LiteralStringExpression
      || node instanceof ObjectExpression
      || node instanceof ThisExpression) {
      return Precedence.PRIMARY;
    } else if (node instanceof AssignmentExpression
      || node instanceof CompoundAssignmentExpression
      || node instanceof YieldExpression
      || node instanceof YieldGeneratorExpression) {
      return Precedence.ASSIGNMENT;
    } else if (node instanceof ConditionalExpression) {
      return Precedence.CONDITIONAL;
    } else if (node instanceof ComputedMemberExpression) {
      ExpressionSuper object = ((ComputedMemberExpression) node)._object;
      if (object instanceof CallExpression
        || object instanceof ComputedMemberExpression
        || object instanceof StaticMemberExpression
        || object instanceof TemplateExpression) {
        return getPrecedence((Expression) object);
      } else {
        return Precedence.MEMBER;
      }
    } else if (node instanceof StaticMemberExpression) {
      ExpressionSuper object = ((StaticMemberExpression) node)._object;
      if (object instanceof CallExpression
        || object instanceof ComputedMemberExpression
        || object instanceof StaticMemberExpression
        || object instanceof TemplateExpression) {
        return getPrecedence((Expression) object);
      } else {
        return Precedence.MEMBER;
      }
    } else if (node instanceof TemplateExpression) {
      Maybe<Expression> maybeTag = ((TemplateExpression) node).tag;
      if (maybeTag.isNothing()) {
        return Precedence.MEMBER;
      }
      Expression tag = maybeTag.just();
      if (tag instanceof CallExpression
        || tag instanceof ComputedMemberExpression
        || tag instanceof StaticMemberExpression
        || tag instanceof TemplateExpression) {
        return getPrecedence(tag);
      } else {
        return Precedence.MEMBER;
      }
    } else if (node instanceof BinaryExpression) {
      return getBinaryPrecedence(((BinaryExpression) node).operator);
    } else if (node instanceof CallExpression) {
      return Precedence.CALL;
    } else if (node instanceof NewExpression) {
      return ((NewExpression) node).arguments.length == 0 ? Precedence.NEW : Precedence.MEMBER;
    } else if (node instanceof UpdateExpression) {
      return ((UpdateExpression) node).isPrefix ? Precedence.PREFIX : Precedence.POSTFIX;
    } else { // if (node instanceof UnaryExpression) {
      return Precedence.PREFIX;
    }
  }

  private boolean isIdentifierNameES6(String id) {
    char ch;
    char lowCh;

    if (id.length() == 0) {
      return false;
    }
    if (!Utils.isIdentifierStart(id.charAt(0))) {
      return false;
    }
    for (int i = 1, iz = id.length(); i < iz; ++i) {
      ch = id.charAt(i);
      if (0xD800 <= ch && ch <= 0xDBFF) {
        ++i;
        if (i >= iz) { return false; }
        lowCh = id.charAt(i);
        if (!(0xDC00 <= lowCh && lowCh <= 0xDFFF)) {
          return false;
        }
        ch = decodeUtf16(ch, lowCh);
      }
      if (!Utils.isIdentifierPart(ch)) {
        return false;
      }
    }
    return true;
  }

  private CodeRep p(Node node, Precedence precedence, CodeRep a) {
    return getPrecedence(node).ordinal() < precedence.ordinal() ? factory.paren(a) : a;
  }

  @NotNull
  private CodeRep parenToAvoidBeingDirective(@NotNull ImportDeclarationExportDeclarationStatement element, @NotNull CodeRep original) {
    if (element instanceof ExpressionStatement &&
      ((ExpressionStatement) element).expression instanceof LiteralStringExpression) {
      return seqVA(factory.paren(((CodeRep.Seq) original).children[0]), factory.semiOp());
    }
    return original;
  }

  @NotNull
  @Override
  public CodeRep reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<CodeRep>> elements, @NotNull Maybe<CodeRep> restElement) {
    CodeRep content;
    if (elements.length == 0) {
      content = restElement.maybe(factory.empty(), r -> seqVA(factory.token("..."), r));
    } else {
      content = factory.commaSep(elements.map(this::getAssignmentExpr));
      if (elements.length > 0 && elements.maybeLast().just().isNothing() && restElement.isNothing()) {
        content = seqVA(content, factory.token(","));
      }
      if (restElement.isJust()) {
        content = seqVA(content, factory.token(","), factory.token("..."), restElement.just());
      }
    }
    return factory.bracket(content);
  }

  @Override
  @NotNull
  public CodeRep reduceArrayExpression(@NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<CodeRep>> elements) {
    if (elements.isEmpty()) {
      return factory.bracket(factory.empty());
    }

    CodeRep content = factory.commaSep(elements.map(this::getAssignmentExpr));
    if (elements.length > 0 && elements.maybeLast().just().isNothing()) {
      content = seqVA(content, factory.token(","));
    }
    return factory.bracket(content);
  }

  @NotNull
  @Override
  public CodeRep reduceArrowExpression(@NotNull ArrowExpression node, @NotNull CodeRep params, @NotNull CodeRep body) {
    if (node.params.rest.isJust() || node.params.items.length != 1 || !(node.params.items.maybeHead().just() instanceof BindingIdentifier)) {
      params = factory.paren(params);
    }
    if (node.body instanceof FunctionBody) {
      body = factory.brace(body);
    } else if (body.startsWithCurly) {
      body = factory.paren(body);
    }
    return seqVA(params, factory.token("=>"), body);
  }

  @Override
  @NotNull
  public CodeRep reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull CodeRep binding, @NotNull CodeRep expression) {
    CodeRep leftCode = binding;
    CodeRep rightCode = expression;
    boolean containsIn = expression.containsIn;
    boolean startsWithCurly = binding.startsWithCurly;
    boolean startsWithLetSquareBracket = binding.startsWithLetSquareBracket;
    boolean startsWithFunctionOrClass = binding.startsWithFunctionOrClass;
    if (getPrecedence(node.expression).ordinal() < getPrecedence(node).ordinal()) {
      rightCode = factory.paren(rightCode);
      containsIn = false;
    }
    CodeRep toReturn = seqVA(leftCode, factory.token("="), rightCode);
    toReturn.containsIn = containsIn;
    toReturn.startsWithCurly = startsWithCurly;
    toReturn.startsWithLetSquareBracket = startsWithLetSquareBracket;
    toReturn.startsWithFunctionOrClass = startsWithFunctionOrClass;
    return toReturn;
  }

  @Override
  @NotNull
  public CodeRep reduceBinaryExpression(@NotNull BinaryExpression node, @NotNull CodeRep left, @NotNull CodeRep right) {
    CodeRep leftCode = left;
    boolean startsWithCurly = left.startsWithCurly;
    boolean startsWithLetSquareBracket = left.startsWithLetSquareBracket;
    boolean startsWithFunctionOrClass = left.startsWithFunctionOrClass;
    boolean leftContainsIn = left.containsIn;
    if (getPrecedence(node.left).ordinal() < getPrecedence(node).ordinal()) {
      leftCode = factory.paren(leftCode);
      startsWithCurly = false;
      startsWithLetSquareBracket = false;
      startsWithFunctionOrClass = false;
      leftContainsIn = false;
    }
    CodeRep rightCode = right;
    boolean rightContainsIn = right.containsIn;
    if (getPrecedence(node.right).ordinal() <= getPrecedence(node).ordinal()) {
      rightCode = factory.paren(rightCode);
      rightContainsIn = false;
    }
    CodeRep toReturn = seqVA(leftCode, factory.token(node.operator.getName()), rightCode);
    toReturn.containsIn = leftContainsIn || rightContainsIn || node.operator.equals(BinaryOperator.In);
    toReturn.containsGroup = node.operator.equals(BinaryOperator.Sequence);
    toReturn.startsWithCurly = startsWithCurly;
    toReturn.startsWithLetSquareBracket = startsWithLetSquareBracket;
    toReturn.startsWithFunctionOrClass = startsWithFunctionOrClass;
    return toReturn;
  }

  @NotNull
  @Override
  public CodeRep reduceBindingIdentifier(@NotNull BindingIdentifier node) {
    CodeRep a = factory.token(node.name);
    if (node.name.equals("let")) {
      a.startsWithLet = true;
    }
    return a;
  }

  @NotNull
  @Override
  public CodeRep reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull CodeRep binding, @NotNull Maybe<CodeRep> init) {
    return init.maybe(binding, i -> seqVA(binding, factory.token("="), i));
  }

  @NotNull
  @Override
  public CodeRep reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull CodeRep name, @NotNull CodeRep binding) {
    return seqVA(name, factory.token(":"), binding);
  }

  @NotNull
  @Override
  public CodeRep reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull CodeRep binding, @NotNull CodeRep init) {
    return seqVA(binding, factory.token("="), init);
  }

  @NotNull
  @Override
  public CodeRep reduceBlock(@NotNull Block node, @NotNull ImmutableList<CodeRep> statements) {
    return factory.brace(factory.seq(statements));
  }

  @Override
  @NotNull
  public CodeRep reduceBlockStatement(
      @NotNull BlockStatement node, @NotNull CodeRep block) {
    return block;
  }

  @NotNull
  @Override
  public CodeRep reduceBreakStatement(@NotNull BreakStatement node) {
    return seqVA(factory.token("break"), node.label.maybe(factory.empty(), factory::token), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceCallExpression(@NotNull CallExpression node, @NotNull CodeRep callee, @NotNull ImmutableList<CodeRep> arguments) {
    CodeRep result;
    if (node.callee instanceof Expression) {
      result = seqVA(p((Expression) node.callee, getPrecedence(node), callee), factory.paren(factory.commaSep(arguments)));
    } else {
      result = seqVA(callee, factory.paren(factory.commaSep(arguments)));
    }
    result.startsWithCurly = callee.startsWithCurly;
    result.startsWithLetSquareBracket = callee.startsWithLetSquareBracket;
    result.startsWithFunctionOrClass = callee.startsWithFunctionOrClass;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceCatchClause(@NotNull CatchClause node, @NotNull CodeRep binding, @NotNull CodeRep body) {
    return seqVA(factory.token("catch"), factory.paren(binding), body);
  }

  @NotNull
  @Override
  public CodeRep reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull CodeRep name, @NotNull Maybe<CodeRep> _super, @NotNull ImmutableList<CodeRep> elements) {
    CodeRep state = seqVA(factory.token("class"), name);
    if (_super.isJust()) {
      state = seqVA(state, factory.token("extends"), _super.just());
    }
    state = seqVA(state, factory.token("{"), factory.seq(elements), factory.token("}"));
    return state;
  }

  @NotNull
  @Override
  public CodeRep reduceClassElement(@NotNull ClassElement node, @NotNull CodeRep method) {
    if (!node.isStatic) {
      return method;
    }
    return seqVA(factory.token("static"), method);
  }

  @NotNull
  @Override
  public CodeRep reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<CodeRep> name, @NotNull Maybe<CodeRep> _super, @NotNull ImmutableList<CodeRep> elements) {
    CodeRep state = factory.token("class");
    if (name.isJust()) {
      state = seqVA(state, name.just());
    }
    if (_super.isJust()) {
      state = seqVA(state, factory.token("extends"), _super.just());
    }
    state = seqVA(state, factory.token("{"), factory.seq(elements), factory.token("}"));
    state.startsWithFunctionOrClass = true;
    return state;
  }

  @NotNull
  @Override
  public CodeRep reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull CodeRep binding, @NotNull CodeRep expression) {
    CodeRep rightCode = expression;
    boolean containsIn = expression.containsIn;
    boolean startsWithCurly = binding.startsWithCurly;
    boolean startsWithLetSquareBracket = binding.startsWithLetSquareBracket;
    boolean startsWithFunctionOrClass = binding.startsWithFunctionOrClass;
    if (getPrecedence(node.expression).ordinal() < getPrecedence(node).ordinal()) {
      rightCode = factory.paren(rightCode);
      containsIn = false;
    }
    CodeRep toReturn = seqVA(binding, factory.token(node.operator.getName()), rightCode);
    toReturn.containsIn = containsIn;
    toReturn.startsWithCurly = startsWithCurly;
    toReturn.startsWithLetSquareBracket = startsWithLetSquareBracket;
    toReturn.startsWithFunctionOrClass = startsWithFunctionOrClass;
    return toReturn;
  }

  @Override
  @NotNull
  public CodeRep reduceComputedMemberExpression(@NotNull ComputedMemberExpression node, @NotNull CodeRep expression, @NotNull CodeRep object) {
    boolean startsWithLetSquareBracket = object.startsWithLetSquareBracket || node._object instanceof IdentifierExpression && ((IdentifierExpression) node._object).name.equals("let");
    CodeRep result;
    if (node._object instanceof Expression) {
      result = seqVA(p((Expression) node._object, getPrecedence(node), object), factory.bracket(expression));
    } else {
      result = seqVA(object, factory.bracket(expression));
    }
    result.startsWithLetSquareBracket = startsWithLetSquareBracket;
    result.startsWithLet = object.startsWithLet;
    result.startsWithCurly = object.startsWithCurly;
    result.startsWithFunctionOrClass = object.startsWithFunctionOrClass;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull CodeRep expression) {
    return factory.bracket(expression);
  }

  @Override
  @NotNull
  public CodeRep reduceConditionalExpression(@NotNull ConditionalExpression node, @NotNull CodeRep test, @NotNull CodeRep consequent, @NotNull CodeRep alternate) {
    boolean containsIn = test.containsIn || alternate.containsIn;
    boolean startsWithCurly = test.startsWithCurly;
    boolean startsWithLetSquareBracket = test.startsWithLetSquareBracket;
    boolean startsWithFunctionOrClass = test.startsWithFunctionOrClass;

    CodeRep toReturn = seqVA(p(node.test, Precedence.LOGICAL_OR, test), factory.token("?"), p(node.consequent, Precedence.ASSIGNMENT, consequent), factory.token(":"), p(node.alternate, Precedence.ASSIGNMENT, alternate));
    toReturn.containsIn = containsIn;
    toReturn.startsWithCurly = startsWithCurly;
    toReturn.startsWithLetSquareBracket = startsWithLetSquareBracket;
    toReturn.startsWithFunctionOrClass = startsWithFunctionOrClass;
    return toReturn;
  }

  @NotNull
  @Override
  public CodeRep reduceContinueStatement(@NotNull ContinueStatement node) {
    return seqVA(factory.token("continue"), node.label.maybe(factory.empty(), factory::token), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceDataProperty(@NotNull DataProperty node, @NotNull CodeRep expression, @NotNull CodeRep name) {
    return seqVA(name, factory.token(":"), getAssignmentExpr(Maybe.just(expression)));
  }

  @Override
  @NotNull
  public CodeRep reduceDebuggerStatement(@NotNull DebuggerStatement node) {
    return seqVA(factory.token("debugger"), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceDirective(@NotNull Directive node) {
    String delim = node.rawValue.matches("^(?:[^\"]|.)*$") ? "\"" : "\'";
    return seqVA(factory.token(delim + node.rawValue + delim), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceDoWhileStatement(
      @NotNull DoWhileStatement node, @NotNull CodeRep test, @NotNull CodeRep body) {
    return seqVA(
        factory.token("do"), body, factory.token("while"), factory.paren(test), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceEmptyStatement(@NotNull EmptyStatement node) {
    return factory.semi();
  }

  @NotNull
  @Override
  public CodeRep reduceExport(@NotNull Export node, @NotNull CodeRep declaration) {
    if (node.declaration instanceof VariableDeclaration) {
        declaration = seqVA(declaration, factory.semiOp());
    }
    return seqVA(factory.token("export"), declaration);
  }

  @NotNull
  @Override
  public CodeRep reduceExportAllFrom(@NotNull ExportAllFrom node) {
    return seqVA(factory.token("export"), factory.token("*"), factory.token("from"), factory.token(escapeStringLiteral(node.moduleSpecifier)), factory.semiOp());

  }

  @NotNull
  @Override
  public CodeRep reduceExportDefault(@NotNull ExportDefault node, @NotNull CodeRep body) {
    body = body.startsWithFunctionOrClass ? factory.paren(body) : body;
    if (node.body instanceof Expression) {
        body = seqVA(body, factory.semiOp());
    }
    return seqVA(factory.token("export default"), body);
  }

  @NotNull
  @Override
  public CodeRep reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<CodeRep> namedExports) {
    return seqVA(
      factory.token("export"),
      factory.brace(factory.commaSep(namedExports)),
      node.moduleSpecifier.maybe(factory.empty(), m -> seqVA(factory.token("from"), factory.token(escapeStringLiteral(m)), factory.semiOp()))
    );
  }

  @NotNull
  @Override
  public CodeRep reduceExportSpecifier(@NotNull ExportSpecifier node) {
    if (node.name.isNothing()) {
      return factory.token(node.exportedName);
    }
    return seqVA(factory.token(node.name.just()), factory.token("as"), factory.token(node.exportedName));
  }

  @Override
  @NotNull
  public CodeRep reduceExpressionStatement(@NotNull ExpressionStatement expressionStatement, @NotNull CodeRep expression) {
    boolean needsParens = expression.startsWithCurly || expression.startsWithLetSquareBracket || expression.startsWithFunctionOrClass;
    return seqVA((needsParens ? factory.paren(expression) : expression), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceForInStatement(@NotNull ForInStatement node, @NotNull CodeRep left, @NotNull CodeRep right, @NotNull CodeRep body) {
    CodeRep leftP = left;
    if (node.left instanceof VariableDeclaration) {
      leftP = factory.noIn(factory.markContainsIn(left));
    } else if (node.left instanceof BindingIdentifier) {
      if (((BindingIdentifier) node.left).name.equals("let")) {
        leftP = factory.paren(left);
      }
    }
    CodeRep toReturn = seqVA(factory.token("for"), factory.paren(seqVA(leftP, factory.token("in"), right)), body);
    toReturn.endsWithMissingElse = body.endsWithMissingElse;
    return toReturn;
  }

  @NotNull
  @Override
  public CodeRep reduceForOfStatement(@NotNull ForOfStatement node, @NotNull CodeRep left, @NotNull CodeRep right, @NotNull CodeRep body) {
    left = node.left instanceof VariableDeclaration ? factory.noIn(factory.markContainsIn(left)) : left;
    CodeRep toReturn = seqVA(factory.token("for"), factory.paren(seqVA(left.startsWithLet ? factory.paren(left) : left, factory.token("of"), right)), body);
    toReturn.endsWithMissingElse = body.endsWithMissingElse;
    return toReturn;
  }

  @NotNull
  @Override
  public CodeRep reduceForStatement(
      @NotNull ForStatement node,
      @NotNull Maybe<CodeRep> init,
      @NotNull Maybe<CodeRep> test,
      @NotNull Maybe<CodeRep> update,
      @NotNull CodeRep body) {
    CodeRep result = seqVA(
        factory.token("for"),
        factory.paren(seqVA(
            init.maybe(factory.empty(), x -> factory.noIn(factory.testIn(x))),
            factory.token(";"),
            test.orJust(factory.empty()),
            factory.token(";"),
            update.orJust(factory.empty()))),
        body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<CodeRep> items, @NotNull Maybe<CodeRep> rest) {
    return factory.commaSep(rest.maybe(items, r -> items.append(ImmutableList.list(seqVA(factory.token("..."), r)))));
  }

  @Override
  @NotNull
  public CodeRep reduceFunctionBody(
      @NotNull final FunctionBody node,
      @NotNull final ImmutableList<CodeRep> directives,
      @NotNull final ImmutableList<CodeRep> statements) {
    CodeRep body;
    if (statements.isEmpty()) {
      body = factory.empty();
    } else {
      NonEmptyImmutableList<CodeRep> seNel = ((NonEmptyImmutableList<CodeRep>) statements);
      body = parenToAvoidBeingDirective(((NonEmptyImmutableList<Statement>) node.statements).head, seNel.head);
      body = seqVA(body, factory.seq(seNel.tail()));
    }
    return seqVA(factory.seq(directives), body);
  }

  @Override
  @NotNull
  public CodeRep reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull CodeRep name, @NotNull CodeRep params, @NotNull CodeRep body) {
    return seqVA(factory.token("function"), node.isGenerator ? factory.token("*") : factory.empty(), node.name.name.equals("*default*") ? factory.empty() : name, factory.paren(params), factory.brace(body));
  }

  @Override
  @NotNull
  public CodeRep reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<CodeRep> name, @NotNull CodeRep params, @NotNull CodeRep body) {
    CodeRep state = seqVA(factory.token("function"), node.isGenerator ? factory.token("*") : factory.empty(), name.isJust() ? name.just() : factory.empty(), factory.paren(params), factory.brace(body));
    state.startsWithFunctionOrClass = true;
    return state;
  }

  @Override
  @NotNull
  public CodeRep reduceGetter(
      @NotNull Getter node, @NotNull CodeRep body, @NotNull CodeRep name) {
    return seqVA(factory.token("get"), name, factory.paren(factory.empty()), factory.brace(body));
  }

  @NotNull
  @Override
  public CodeRep reduceIdentifierExpression(@NotNull IdentifierExpression node) {
    CodeRep a = factory.token(node.name);
    if (node.name.equals("let")) {
      a.startsWithLet = true;
    }
    return a;
  }

  @Override
  @NotNull
  public CodeRep reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull CodeRep test,
      @NotNull CodeRep consequent,
      @NotNull Maybe<CodeRep> alternate) {
    CodeRep consequentCode = consequent;
    if (alternate.isJust() && consequent.endsWithMissingElse) {
      consequentCode = factory.brace(consequentCode);
    }
    CodeRep result = seqVA(
        factory.token("if"), factory.paren(test), consequentCode, alternate.maybe(
            factory.empty(), s -> seqVA(
                factory.token("else"), s)));
    result.endsWithMissingElse = alternate.maybe(true, s -> s.endsWithMissingElse);
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceImport(@NotNull Import node, @NotNull Maybe<CodeRep> defaultBinding, @NotNull ImmutableList<CodeRep> namedImports) {
    List<CodeRep> bindings = new ArrayList<>();
    if (defaultBinding.isJust()) {
      bindings.add(defaultBinding.just());
    }
    if (namedImports.length > 0) {
      bindings.add(factory.brace(factory.commaSep(namedImports)));
    }
    if (bindings.size() == 0) {
      return seqVA(factory.token("import"), factory.token(escapeStringLiteral(node.moduleSpecifier)), factory.semiOp());
    }
    return seqVA(factory.token("import"), factory.commaSep(ImmutableList.from(bindings)), factory.token("from"), factory.token(escapeStringLiteral(node.moduleSpecifier)), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<CodeRep> defaultBinding, @NotNull CodeRep namespaceBinding) {
    return seqVA(
      factory.token("import"),
      defaultBinding.maybe(factory.empty(), b -> seqVA(b, factory.token(","))),
      factory.token("*"),
      factory.token("as"),
      namespaceBinding,
      factory.token("from"),
      factory.token(escapeStringLiteral(node.moduleSpecifier)),
      factory.semiOp()
    );
  }

  @NotNull
  @Override
  public CodeRep reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull CodeRep binding) {
    return node.name.maybe(binding, n -> seqVA(factory.token(n), factory.token("as"), binding));
  }

  @Override
  @NotNull
  public CodeRep reduceLabeledStatement(
      @NotNull LabeledStatement node, @NotNull CodeRep body) {
    CodeRep result = seqVA(factory.token(node.label), factory.token(":"), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node) {
    return factory.token(Boolean.toString(node.value));
  }

  @NotNull
  @Override
  public CodeRep reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
    return factory.token("2e308");
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
    return factory.token("null");
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
    return factory.num(node.value);
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
    return factory.token("/" + node.pattern + "/" + node.flags);
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
    return factory.token(Utils.escapeStringLiteral(node.value));
  }

  @NotNull
  @Override
  public CodeRep reduceMethod(@NotNull Method node, @NotNull CodeRep params, @NotNull CodeRep body, @NotNull CodeRep name) {
    return seqVA(node.isGenerator ? factory.token("*") : factory.empty(), name, factory.paren(params), factory.brace(body));
  }

  @NotNull
  @Override
  public CodeRep reduceModule(@NotNull Module node, @NotNull ImmutableList<CodeRep> directives, @NotNull ImmutableList<CodeRep> items) {
    CodeRep body;
    if (items.isEmpty()) {
      body = factory.empty();
    } else {
      NonEmptyImmutableList<CodeRep> seNel = ((NonEmptyImmutableList<CodeRep>) items);
      body = parenToAvoidBeingDirective(((NonEmptyImmutableList<ImportDeclarationExportDeclarationStatement>) node.items).head, seNel.head);
      body = seqVA(body, factory.seq(seNel.tail()));
    }
    return seqVA(factory.seq(directives), body);
  }

  @Override
  @NotNull
  public CodeRep reduceNewExpression(@NotNull NewExpression node, @NotNull CodeRep callee, @NotNull ImmutableList<CodeRep> arguments) {
    CodeRep calleeRep = getPrecedence(node.callee) == Precedence.CALL ? factory.paren(callee) : p(node.callee, getPrecedence(node), callee);
    return seqVA(factory.token("new"), calleeRep, arguments.length == 0 ? factory.empty() : factory.paren(factory.commaSep(arguments)));
  }

  @NotNull
  @Override
  public CodeRep reduceNewTargetExpression(@NotNull NewTargetExpression node) {
    return factory.token("new.target");
  }

  @NotNull
  @Override
  public CodeRep reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<CodeRep> properties) {
    CodeRep state = factory.brace(factory.commaSep(properties));
    state.startsWithCurly = true;
    return state;
  }

  @Override
  @NotNull
  public CodeRep reduceObjectExpression(@NotNull ObjectExpression node, @NotNull ImmutableList<CodeRep> properties) {
    CodeRep result = factory.brace(factory.commaSep(properties));
    result.startsWithCurly = true;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceReturnStatement(
      @NotNull ReturnStatement node, @NotNull Maybe<CodeRep> expression) {
    return seqVA(
        factory.token("return"), seqVA(expression.orJust(factory.empty())), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceScript(@NotNull Script node, @NotNull ImmutableList<CodeRep> directives, @NotNull ImmutableList<CodeRep> statements) {
    CodeRep body;
    if (statements.isEmpty()) {
      body = factory.empty();
    } else {
      NonEmptyImmutableList<CodeRep> seNel = ((NonEmptyImmutableList<CodeRep>) statements);
      body = parenToAvoidBeingDirective(((NonEmptyImmutableList<Statement>) node.statements).head, seNel.head);
      body = seqVA(body, factory.seq(seNel.tail()));
    }
    return seqVA(factory.seq(directives), body);
  }

  @Override
  @NotNull
  public CodeRep reduceSetter(
      @NotNull Setter node,
      @NotNull CodeRep parameter,
      @NotNull CodeRep body,
      @NotNull CodeRep name) {
    return (seqVA(factory.token("set"), name, factory.paren(parameter), factory.brace(body)));
  }

  @NotNull
  @Override
  public CodeRep reduceShorthandProperty(@NotNull ShorthandProperty node) {
    return factory.token(node.name);
  }

  @NotNull
  @Override
  public CodeRep reduceSpreadElement(@NotNull SpreadElement node, @NotNull CodeRep expression) {
    return seqVA(factory.token("..."), p(node.expression, Precedence.ASSIGNMENT, expression));
  }

  @Override
  @NotNull
  public CodeRep reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull CodeRep object) {
    CodeRep state;
    if (node._object instanceof Expression) {
      state = seqVA(p((Expression) node._object, getPrecedence(node), object), factory.token("."), factory.token(node.property));
    } else {
      // node._object is a Super
      state = seqVA(object, factory.token("."), factory.token(node.property));
    }
    state.startsWithLet = object.startsWithLet;
    state.startsWithCurly = object.startsWithCurly;
    state.startsWithLetSquareBracket = object.startsWithLetSquareBracket;
    state.startsWithFunctionOrClass = object.startsWithFunctionOrClass;
    return state;
  }

  @NotNull
  @Override
  public CodeRep reduceStaticPropertyName(@NotNull StaticPropertyName node) {
    if (isIdentifierNameES6(node.value) && !node.value.equals("Infinity")) {
      return factory.token(node.value);
    } else {
      try {
        double n = Double.parseDouble(node.value);
        return new CodeRep.NumberCodeRep(n);
      } catch (NumberFormatException ignored) {
        return factory.token(escapeStringLiteral(node.value));
      }
    }
  }

  @NotNull
  @Override
  public CodeRep reduceSuper(@NotNull Super node) {
    return factory.token("super");
  }

  @NotNull
  @Override
  public CodeRep reduceSwitchCase(
      @NotNull SwitchCase node, @NotNull CodeRep test, @NotNull ImmutableList<CodeRep> consequent) {
    return seqVA(factory.token("case"), test, factory.token(":"), factory.seq(consequent));
  }

  @NotNull
  @Override
  public CodeRep reduceSwitchDefault(
      @NotNull SwitchDefault node, @NotNull ImmutableList<CodeRep> consequent) {
    return seqVA(factory.token("default"), factory.token(":"), factory.seq(consequent));
  }

  @Override
  @NotNull
  public CodeRep reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull CodeRep discriminant,
      @NotNull ImmutableList<CodeRep> cases) {
    return seqVA(
        factory.token("switch"), factory.paren(discriminant), factory.brace(
        factory.seq(cases)));
  }

  @NotNull
  @Override
  public CodeRep reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull CodeRep discriminant,
      @NotNull ImmutableList<CodeRep> preDefaultCases,
      @NotNull CodeRep defaultCase,
      @NotNull ImmutableList<CodeRep> postDefaultCases) {
    return seqVA(
        factory.token("switch"), factory.paren(discriminant), factory.brace(
        seqVA(
          factory.seq(preDefaultCases), defaultCase, factory.seq(postDefaultCases
          ))));
  }

  @NotNull
  @Override
  public CodeRep reduceTemplateElement(@NotNull TemplateElement node) {
    return factory.token("`" + node.rawValue + "`");
  }

  @NotNull
  @Override
  public CodeRep reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<CodeRep> tag, @NotNull ImmutableList<CodeRep> elements) {
    CodeRep state = node.tag.isNothing() ? factory.empty() : p(node.tag.just(), getPrecedence(node), tag.just());
    state = seqVA(state, factory.token("`"));
    for (int i = 0, l = node.elements.length; i < l; ++i) {
      if (node.elements.index(i).just() instanceof TemplateElement) {
        String d = "";
        if (i > 0) {
          d += "}";
        }
        d += ((TemplateElement) node.elements.index(i).just()).rawValue;
        if (i < l - 1) {
          d += "${";
        }
        if (d.length() > 0) {
          state = seqVA(state, factory.token(d));
        }
      } else {
        state = seqVA(state, elements.index(i).just());
      }
    }
    state = seqVA(state, factory.token("`"));
    if (node.tag.isJust()) {
      state.startsWithCurly = tag.just().startsWithCurly;
      state.startsWithLetSquareBracket = tag.just().startsWithLetSquareBracket;
      state.startsWithFunctionOrClass = tag.just().startsWithFunctionOrClass;
    }
    return state;
  }

  @Override
  @NotNull
  public CodeRep reduceThisExpression(@NotNull ThisExpression node) {
    return factory.token("this");
  }

  @Override
  @NotNull
  public CodeRep reduceThrowStatement(
      @NotNull ThrowStatement node, @NotNull CodeRep expression) {
    return seqVA(factory.token("throw"), expression, factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull CodeRep block,
      @NotNull CodeRep catchClause) {
    return seqVA(factory.token("try"), block, catchClause);
  }

  @NotNull
  @Override
  public CodeRep reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull CodeRep block,
      @NotNull Maybe<CodeRep> catchClause,
      @NotNull CodeRep finalizer) {
    return seqVA(
        factory.token("try"), block, catchClause.orJust(factory.empty()), seqVA(
            factory.token("finally"), finalizer));
  }

  @NotNull
  @Override
  public CodeRep reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull CodeRep operand) {
    return seqVA(factory.token(node.operator.getName()), p(node.operand, getPrecedence(node), operand));
  }

  //@NotNull
  @Override
  public CodeRep reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull CodeRep operand) {
    if (node.isPrefix) {
      return seqVA(factory.token(node.operator.getName()), operand);
    } else {
      CodeRep toReturn;
      if (node.operand instanceof BindingIdentifier) {
        toReturn = seqVA(operand, factory.token(node.operator.getName()));
      } else {
        toReturn = seqVA(p((MemberExpression) node.operand, Precedence.NEW, operand), factory.token(node.operator.getName()));
      }
      toReturn.startsWithCurly = operand.startsWithCurly;
      toReturn.startsWithLetSquareBracket = operand.startsWithLetSquareBracket;
      toReturn.startsWithFunctionOrClass = operand.startsWithFunctionOrClass;
      return toReturn;
    }
  }

//----------------------------------------------------------------------------------------------------------------------

  @NotNull
  @Override
  public CodeRep reduceVariableDeclaration(
      @NotNull VariableDeclaration node, @NotNull ImmutableList<CodeRep> declarators) {
    return seqVA(factory.token(node.kind.name), factory.commaSep(declarators));
  }

  @NotNull
  @Override
  public CodeRep reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node, @NotNull CodeRep declaration) {
    return seqVA(declaration, factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceVariableDeclarator(
      @NotNull VariableDeclarator node, @NotNull CodeRep binding,
      @NotNull Maybe<CodeRep> init) {
    CodeRep result = factory.init(
        binding, init.map(
            state -> state.containsGroup ? factory.paren(state) : factory.testIn(state)));
    result.containsIn = init.maybe(false, state -> state.containsIn && !state.containsGroup);
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceWhileStatement(
      @NotNull WhileStatement node, @NotNull CodeRep test, @NotNull CodeRep body) {
    CodeRep result = seqVA(factory.token("while"), factory.paren(test), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceWithStatement(
      @NotNull WithStatement node, @NotNull CodeRep object, @NotNull CodeRep body) {
    CodeRep result = seqVA(factory.token("with"), factory.paren(object), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<CodeRep> expression) {
    if (node.expression.isNothing()) {
      return factory.token("yield");
    }
    return seqVA(factory.token("yield"), p(node.expression.just(), getPrecedence(node), expression.just()));
  }

  @NotNull
  @Override
  public CodeRep reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull CodeRep expression) {
    return seqVA(factory.token("yield"), factory.token("*"), p(node.expression, getPrecedence(node), expression));
  }

  @NotNull
  private CodeRep seqVA(@NotNull CodeRep... reps) {
    return factory.seq(reps);
  }

}












































