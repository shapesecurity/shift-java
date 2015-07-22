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

import com.shapesecurity.functional.data.Either;
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
  public static String codeGen(@NotNull Node script, boolean pretty) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    Director.reduce(pretty ? PRETTY : COMPACT, script).emit(ts, false);
    return sb.toString();
  }

  @NotNull
  public static String codeGen(@NotNull Script script, @NotNull FormattedCodeRepFactory instance) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    script.reduce(new CodeGen(instance)).emit(ts, false);
    return sb.toString();
  }

  @NotNull
  public static String codeGenNode(@NotNull Node node) {
    CodeRep codeRep = Director.reduce(COMPACT, node);
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    codeRep.emit(ts, false);
    return sb.toString();
  }

  @NotNull
  private CodeRep parenToAvoidBeingDirective(@NotNull Node element, @NotNull CodeRep original) {
    if (element instanceof ExpressionStatement &&
        ((ExpressionStatement) element).expression instanceof LiteralStringExpression) {
      return seqVA(factory.paren(((CodeRep.Seq) original).children[0]), factory.semiOp());
    }
    return original;
  }

  @NotNull
  @Override
  public CodeRep reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<CodeRep>> elements, @NotNull Maybe<CodeRep> restElement) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceArrayExpression(
      @NotNull ArrayExpression node, @NotNull ImmutableList<Maybe<CodeRep>> elements) {
    if (elements.isEmpty()) {
      return factory.bracket(factory.empty());
    }

    CodeRep empty = factory.empty();
    CodeRep[] reps = new CodeRep[elements.length];
    for (int i = 0; i < reps.length; i++) {
      NonEmptyImmutableList<Maybe<CodeRep>> nel = (NonEmptyImmutableList<Maybe<CodeRep>>) elements;
      CodeRep el = empty;
      if (nel.head.isJust()) {
        el = nel.head.just();
        if (el.containsGroup) {
          el = factory.paren(el);
        }
      }
      reps[i] = el;
      elements = nel.tail;
    }
    CodeRep content = factory.commaSep(reps);
    if (reps[reps.length - 1] == empty) {
      content = seqVA(content, factory.token(","));
    }
    return factory.bracket(content);
  }

  @NotNull
  @Override
  public CodeRep reduceArrowExpression(@NotNull ArrowExpression node, @NotNull CodeRep params, @NotNull CodeRep body) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull CodeRep binding,
      @NotNull CodeRep expression) {
    CodeRep rightCode = expression;
    boolean rightContainsIn = expression.containsIn;
    if (node.expression.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
      rightCode = factory.paren(rightCode);
      rightContainsIn = false;
    }
    CodeRep result = seqVA(
        factory.expr(node.binding, Precedence.NEW, binding),
        factory.token(node.operator.getName()),
        rightCode);
    result.containsIn = rightContainsIn;
    result.startsWithFunctionOrCurly = binding.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceBinaryExpression(
      @NotNull BinaryExpression node, @NotNull CodeRep left, @NotNull CodeRep right) {
    CodeRep leftCode = left;
    boolean leftStartsWithFunctionOrCurly = left.startsWithFunctionOrCurly;
    boolean leftContainsIn = left.containsIn;
    if (node.left.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
      leftCode = factory.paren(leftCode);
      leftStartsWithFunctionOrCurly = false;
      leftContainsIn = false;
    }
    CodeRep rightCode = right;
    boolean rightContainsIn = right.containsIn;
    if (node.right.getPrecedence().ordinal() <= node.getPrecedence().ordinal()) {
      rightCode = factory.paren(rightCode);
      rightContainsIn = false;
    }

    CodeRep result = seqVA(leftCode, factory.token(node.operator.getName()), rightCode);
    result.containsIn = leftContainsIn || rightContainsIn || node.operator == BinaryOperator.In;
    result.containsGroup = node.operator == BinaryOperator.Sequence;
    result.startsWithFunctionOrCurly = leftStartsWithFunctionOrCurly;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceBindingIdentifier(@NotNull BindingIdentifier node) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull CodeRep binding, @NotNull Maybe<CodeRep> init) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull CodeRep name, @NotNull CodeRep binding) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull CodeRep binding, @NotNull CodeRep init) {
    return null;
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
    return seqVA(factory.token("break"), node.label.orJust(factory.empty()), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull CodeRep callee,
      @NotNull ImmutableList<CodeRep> arguments) {
    CodeRep result = seqVA(
        factory.expr(node.callee, node.getPrecedence(), callee), factory.paren(
            factory.commaSep(arguments)));
    result.startsWithFunctionOrCurly = callee.startsWithFunctionOrCurly;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceCatchClause(
      @NotNull CatchClause node, @NotNull CodeRep binding, @NotNull CodeRep body) {
    return seqVA(factory.token("catch"), factory.paren(binding), body);
  }

  @NotNull
  @Override
  public CodeRep reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull CodeRep name, @NotNull Maybe<CodeRep> _super, @NotNull ImmutableList<CodeRep> elements) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceClassElement(@NotNull ClassElement node, @NotNull CodeRep method) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<CodeRep> name, @NotNull Maybe<CodeRep> _super, @NotNull ImmutableList<CodeRep> elements) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull CodeRep binding, @NotNull CodeRep expression) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull CodeRep object,
      @NotNull CodeRep expression) {
    CodeRep result = seqVA(
        factory.expr(node._object, node.getPrecedence(), object), factory.bracket(
            expression));
    result.startsWithFunctionOrCurly = object.startsWithFunctionOrCurly;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull CodeRep expression) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull CodeRep test,
      @NotNull CodeRep consequent,
      @NotNull CodeRep alternate) {
    CodeRep result = seqVA(
        factory.expr(node.test, Precedence.LOGICAL_OR, test), factory.token("?"), factory.expr(
            node.consequent, Precedence.ASSIGNMENT, consequent), factory.token(":"), factory.expr(
            node.alternate, Precedence.ASSIGNMENT, alternate));
    result.containsIn = test.containsIn || alternate.containsIn;
    result.startsWithFunctionOrCurly = test.startsWithFunctionOrCurly;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceContinueStatement(@NotNull ContinueStatement node) {
    return seqVA(factory.token("continue"), node.label.orJust(factory.empty()), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceDataProperty(
      @NotNull DataProperty node, @NotNull CodeRep name, @NotNull CodeRep value) {
    return seqVA(name, factory.token(":"), value.containsGroup ? factory.paren(value) : value);
  }

  @Override
  @NotNull
  public CodeRep reduceDebuggerStatement(@NotNull DebuggerStatement node) {
    return seqVA(factory.token("debugger"), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceDirective(@NotNull Directive node) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceDoWhileStatement(
      @NotNull DoWhileStatement node, @NotNull CodeRep body, @NotNull CodeRep test) {
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
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceExportAllFrom(@NotNull ExportAllFrom node) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceExportDefault(@NotNull ExportDefault node, @NotNull CodeRep body) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<CodeRep> namedExports) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceExportSpecifier(@NotNull ExportSpecifier node) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceExpressionStatement(
      @NotNull ExpressionStatement expressionStatement, @NotNull CodeRep expression) {
    return seqVA((expression.startsWithFunctionOrCurly ? factory.paren(expression) : expression), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull CodeRep left,
      @NotNull CodeRep right,
      @NotNull CodeRep body) {
    CodeRep result = seqVA(
        factory.token("for"),
        factory.paren(seqVA(factory.noIn(
                factory.testIn(Either.extract(left.mapRight((expr) -> factory.expr(node.left.right().just(),
                    Precedence.NEW,
                    expr))))),
            factory.token("in"), right)),
        body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceForOfStatement(@NotNull ForOfStatement node, @NotNull CodeRep left, @NotNull CodeRep right, @NotNull CodeRep body) {
    return null;
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
            init.maybe(factory.empty(), x -> factory.noIn(factory.testIn(Either.extract(x)))),
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
    return null;
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
  public CodeRep reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull CodeRep name,
      @NotNull CodeRep params,
      @NotNull CodeRep body) {
    return seqVA(
        factory.token("function"), name, factory.paren(factory.commaSep(params)), factory.brace(body));
  }

  @Override
  @NotNull
  public CodeRep reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull Maybe<CodeRep> name,
      @NotNull CodeRep parameters,
      @NotNull CodeRep body) {
    final CodeRep argBody = seqVA(factory.paren(factory.commaSep(parameters)), factory.brace(body));
    CodeRep result = seqVA(factory.token("function"), name.maybe(argBody, state -> seqVA(state, argBody)));
    result.startsWithFunctionOrCurly = true;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceGetter(
      @NotNull Getter node, @NotNull CodeRep name, @NotNull CodeRep body) {
    return seqVA(
      factory.token("get"), name, factory.paren(factory.empty()), factory.brace(
            body));
  }

  @NotNull
  @Override
  public CodeRep reduceIdentifierExpression(@NotNull IdentifierExpression node) {
    return factory.token(node.name);
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
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<CodeRep> defaultBinding, @NotNull CodeRep namespaceBinding) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull CodeRep binding) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceLabeledStatement(
      @NotNull LabeledStatement node, @NotNull CodeRep body) {
    CodeRep result = seqVA(node.label, factory.token(":"), body);
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
    return factory.token(node.value);
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralStringExpression(@NotNull LiteralStringExpression node) {
    return factory.token(Utils.escapeStringLiteral(node.value));
  }

  @NotNull
  @Override
  public CodeRep reduceMethod(@NotNull Method node, @NotNull CodeRep name, @NotNull CodeRep params, @NotNull CodeRep body) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceModule(@NotNull Module node, @NotNull ImmutableList<CodeRep> directives, @NotNull ImmutableList<CodeRep> items) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull CodeRep callee,
      @NotNull ImmutableList<CodeRep> arguments) {
    callee = node.callee.getPrecedence() == Precedence.CALL ? factory.paren(callee) : factory.expr(
        node.callee, node.getPrecedence(), callee);
    return seqVA(
        factory.token("new"), callee, arguments.isEmpty() ? factory.empty() : factory.paren(
            factory.commaSep(arguments)));
  }

  @NotNull
  @Override
  public CodeRep reduceNewTargetExpression(@NotNull NewTargetExpression node) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<CodeRep> properties) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceObjectExpression(
      @NotNull ObjectExpression node, @NotNull ImmutableList<CodeRep> properties) {
    CodeRep result = factory.brace(factory.commaSep(properties));
    result.startsWithFunctionOrCurly = true;
    return result;
  }

//  @Override
//  @NotNull
//  public CodeRep reducePostfixExpression(
//      @NotNull PostfixExpression node, @NotNull CodeRep operand) {
//    CodeRep result = seqVA(
//        factory.expr(node.operand, Precedence.NEW, operand),
//        factory.token(node.operator.getName()));
//    result.startsWithFunctionOrCurly = operand.startsWithFunctionOrCurly;
//    return result;
//  }
//
//  @Override
//  @NotNull
//  public CodeRep reducePrefixExpression(
//      @NotNull PrefixExpression node, @NotNull CodeRep operand) {
//    return seqVA(
//        factory.token(node.operator.getName()),
//        factory.expr(node.operand, node.getPrecedence(), operand));
//  }

  @NotNull
  @Override
  public CodeRep reducePropertyName(@NotNull PropertyName node) {
    if (node.kind == PropertyNameKind.Number) {
      if (node.value.equals("Infinity")) {
        return factory.token("2e308");
      } else {
        return factory.token(node.value);
      }
    } else if (node.kind == PropertyNameKind.Identifier) {
      return factory.token(node.value);
    }
    return factory.token(Utils.escapeStringLiteral(node.value));
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
    return body;
  }

  @Override
  @NotNull
  public CodeRep reduceSetter(
      @NotNull Setter node,
      @NotNull CodeRep name,
      @NotNull CodeRep parameter,
      @NotNull CodeRep body) {
    return (seqVA(factory.token("set"), name, factory.paren(parameter), factory.brace(body)));
  }

  @NotNull
  @Override
  public CodeRep reduceShorthandProperty(@NotNull ShorthandProperty node) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceSpreadElement(@NotNull SpreadElement node, @NotNull CodeRep expression) {
    return null;
  }

  @Override
  @NotNull
  public CodeRep reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull CodeRep object) {
    CodeRep result = seqVA(
        factory.expr(node._object, node.getPrecedence(), object), factory.token("."), node.property);
    result.startsWithFunctionOrCurly = object.startsWithFunctionOrCurly;
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceStaticPropertyName(@NotNull StaticPropertyName node) {
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceSuper(@NotNull Super node) {
    return null;
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
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<CodeRep> tag, @NotNull ImmutableList<CodeRep> elements) {
    return null;
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
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull CodeRep operand) {
    return null;
  }

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
    return null;
  }

  @NotNull
  @Override
  public CodeRep reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull CodeRep expression) {
    return null;
  }

  @NotNull
  private CodeRep seqVA(@NotNull CodeRep... reps) {
    return factory.seq(reps);
  }
}
