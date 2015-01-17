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
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.Statement;
import com.shapesecurity.shift.ast.SwitchCase;
import com.shapesecurity.shift.ast.SwitchDefault;
import com.shapesecurity.shift.ast.VariableDeclaration;
import com.shapesecurity.shift.ast.VariableDeclarator;
import com.shapesecurity.shift.ast.directive.UnknownDirective;
import com.shapesecurity.shift.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.ast.expression.ArrayExpression;
import com.shapesecurity.shift.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.ast.expression.BinaryExpression;
import com.shapesecurity.shift.ast.expression.CallExpression;
import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.ast.expression.LiteralInfinityExpression;
import com.shapesecurity.shift.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.ast.expression.NewExpression;
import com.shapesecurity.shift.ast.expression.ObjectExpression;
import com.shapesecurity.shift.ast.expression.PostfixExpression;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.ast.expression.ThisExpression;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.Precedence;
import com.shapesecurity.shift.ast.property.DataProperty;
import com.shapesecurity.shift.ast.property.Getter;
import com.shapesecurity.shift.ast.property.PropertyName;
import com.shapesecurity.shift.ast.property.PropertyName.PropertyNameKind;
import com.shapesecurity.shift.ast.property.Setter;
import com.shapesecurity.shift.ast.statement.BlockStatement;
import com.shapesecurity.shift.ast.statement.BreakStatement;
import com.shapesecurity.shift.ast.statement.ContinueStatement;
import com.shapesecurity.shift.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.ast.statement.EmptyStatement;
import com.shapesecurity.shift.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.ast.statement.ForInStatement;
import com.shapesecurity.shift.ast.statement.ForStatement;
import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.ast.statement.IfStatement;
import com.shapesecurity.shift.ast.statement.LabeledStatement;
import com.shapesecurity.shift.ast.statement.ReturnStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.ast.statement.ThrowStatement;
import com.shapesecurity.shift.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.ast.statement.WhileStatement;
import com.shapesecurity.shift.ast.statement.WithStatement;
import com.shapesecurity.shift.path.Branch;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.Reducer;

import java.util.ArrayList;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

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
  public static String codeGenNode(@NotNull Node node) {
    CodeRep codeRep = Director.reduce(COMPACT, node, List.<Branch>nil());
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    codeRep.emit(ts, false);
    return sb.toString();
  }

  @NotNull
  public static String codeGen(@NotNull Node script, boolean pretty) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    Director.reduce(pretty ? PRETTY : COMPACT, script, List.<Branch>nil()).emit(ts, false);
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
  private CodeRep seqVA(@NotNull CodeRep... reps) {
    ArrayList<CodeRep> arrayList = new ArrayList<>();
    Collections.addAll(arrayList, reps);
    return factory.seq(List.from(arrayList));
  }

  @NotNull
  private CodeRep parenToAvoidBeingDirective(@NotNull Node element, @NotNull CodeRep original) {
    if (element instanceof ExpressionStatement &&
        ((ExpressionStatement) element).expression instanceof LiteralStringExpression) {
      return seqVA(factory.paren(((CodeRep.Seq) original).children.maybeHead().just()), factory.semiOp());
    }
    return original;
  }

  @Override
  @NotNull
  public CodeRep reduceScript(@NotNull Script node, @NotNull List<Branch> path, @NotNull CodeRep body) {
    return body;
  }

  @Override
  @NotNull
  public CodeRep reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    return factory.token(node.name);
  }

  @Override
  @NotNull
  public CodeRep reduceIdentifierExpression(
      @NotNull IdentifierExpression node, @NotNull List<Branch> path, @NotNull CodeRep identifier) {
    return identifier;
  }

  @Override
  @NotNull
  public CodeRep reduceThisExpression(@NotNull ThisExpression node, @NotNull List<Branch> path) {
    return factory.token("this");
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node, @NotNull List<Branch> path) {
    return factory.token(Boolean.toString(node.value));
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralStringExpression(@NotNull LiteralStringExpression node, @NotNull List<Branch> path) {
    return factory.token(Utils.escapeStringLiteral(node.value));
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node, @NotNull List<Branch> path) {
    return factory.token(node.value);
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node, @NotNull List<Branch> path) {
    return factory.num(node.value);
  }

  @NotNull
  @Override
  public CodeRep reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node, @NotNull List<Branch> path) {
    return factory.token("2e308");
  }

  @Override
  @NotNull
  public CodeRep reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull List<Branch> path) {
    return factory.token("null");
  }

  @Override
  @NotNull
  public CodeRep reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<CodeRep> name,
      @NotNull List<CodeRep> parameters,
      @NotNull CodeRep body) {
    final CodeRep argBody = seqVA(factory.paren(factory.commaSep(parameters)), factory.brace(body));
    CodeRep result = seqVA(factory.token("function"), name.maybe(argBody, state -> seqVA(state, argBody)));
    result.startsWithFunctionOrCurly = true;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull CodeRep object,
      @NotNull CodeRep property) {
    CodeRep result = seqVA(
        factory.expr(node.object, node.getPrecedence(), object), factory.token("."), property);
    result.startsWithFunctionOrCurly = object.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull CodeRep object,
      @NotNull CodeRep expression) {
    CodeRep result = seqVA(
        factory.expr(node.object, node.getPrecedence(), object), factory.bracket(
            expression));
    result.startsWithFunctionOrCurly = object.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceObjectExpression(
      @NotNull ObjectExpression node, @NotNull List<Branch> path, @NotNull List<CodeRep> properties) {
    CodeRep result = factory.brace(factory.commaSep(properties));
    result.startsWithFunctionOrCurly = true;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceBinaryExpression(
      @NotNull BinaryExpression node, @NotNull List<Branch> path, @NotNull CodeRep left, @NotNull CodeRep right) {
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

  @Override
  @NotNull
  public CodeRep reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
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
  public CodeRep reduceArrayExpression(
      @NotNull ArrayExpression node, @NotNull List<Branch> path, @NotNull List<Maybe<CodeRep>> elements) {
    if (elements.isEmpty()) {
      return factory.bracket(factory.empty());
    }

    CodeRep content = factory.commaSep(
        elements.map(
            states -> states.map(s -> s.containsGroup ? factory.paren(s) : s).orJust(
                factory.empty())));
    if (elements.maybeLast().just().isNothing()) {
      content = seqVA(content, factory.token(","));
    }
    return factory.bracket(content);
  }

  @Override
  @NotNull
  public CodeRep reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull List<Branch> path,
      @NotNull CodeRep callee,
      @NotNull List<CodeRep> arguments) {
    callee = node.callee.getPrecedence() == Precedence.CALL ? factory.paren(callee) : factory.expr(
        node.callee, node.getPrecedence(), callee);
    return seqVA(
        factory.token("new"), callee, arguments.isEmpty() ? factory.empty() : factory.paren(
            factory.commaSep(arguments)));
  }

  @Override
  @NotNull
  public CodeRep reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull CodeRep callee,
      @NotNull List<CodeRep> arguments) {
    CodeRep result = seqVA(
        factory.expr(node.callee, node.getPrecedence(), callee), factory.paren(
            factory.commaSep(arguments)));
    result.startsWithFunctionOrCurly = callee.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reducePostfixExpression(
      @NotNull PostfixExpression node, @NotNull List<Branch> path, @NotNull CodeRep operand) {
    CodeRep result = seqVA(
        factory.expr(node.operand, Precedence.NEW, operand),
        factory.token(node.operator.getName()));
    result.startsWithFunctionOrCurly = operand.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reducePrefixExpression(
      @NotNull PrefixExpression node, @NotNull List<Branch> path, @NotNull CodeRep operand) {
    return seqVA(
        factory.token(node.operator.getName()),
        factory.expr(node.operand, node.getPrecedence(), operand));
  }

  @Override
  @NotNull
  public CodeRep reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull List<Branch> path,
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

  @Override
  @NotNull
  public CodeRep reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull CodeRep name,
      @NotNull List<CodeRep> params,
      @NotNull CodeRep body) {
    return seqVA(
        factory.token("function"), name, factory.paren(factory.commaSep(params)), factory.brace(body));
  }

  @NotNull
  @Override
  public CodeRep reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull List<Branch> path) {
    return seqVA(factory.token("\"use strict\""), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull List<Branch> path) {
    return seqVA(
        factory.token(
            "\"" + ("use strict".equals(node.getContents()) ? "use\\u0020strict" : node.getContents()) + '"'),
        factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceBlockStatement(
      @NotNull BlockStatement node, @NotNull List<Branch> path, @NotNull CodeRep block) {
    return block;
  }

  @Override
  @NotNull
  public CodeRep reduceBreakStatement(
      @NotNull BreakStatement node, @NotNull List<Branch> path, @NotNull Maybe<CodeRep> label) {
    return seqVA(factory.token("break"), label.orJust(factory.empty()), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceCatchClause(
      @NotNull CatchClause node, @NotNull List<Branch> path, @NotNull CodeRep binding, @NotNull CodeRep body) {
    return seqVA(factory.token("catch"), factory.paren(binding), body);
  }

  @Override
  @NotNull
  public CodeRep reduceContinueStatement(
      @NotNull ContinueStatement node, @NotNull List<Branch> path, @NotNull Maybe<CodeRep> label) {
    return seqVA(factory.token("continue"), label.orJust(factory.empty()), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull List<Branch> path) {
    return seqVA(factory.token("debugger"), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceDoWhileStatement(
      @NotNull DoWhileStatement node, @NotNull List<Branch> path, @NotNull CodeRep body, @NotNull CodeRep test) {
    return seqVA(
        factory.token("do"), body, factory.token("while"), factory.paren(test), factory.semiOp());
  }

  @Override
  @NotNull
  public CodeRep reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull List<Branch> path) {
    return factory.semi();
  }

  @Override
  @NotNull
  public CodeRep reduceExpressionStatement(
      @NotNull ExpressionStatement expressionStatement, @NotNull List<Branch> path, @NotNull CodeRep expression) {
    return seqVA((expression.startsWithFunctionOrCurly ? factory.paren(expression) : expression), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<CodeRep, CodeRep> left,
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
  public CodeRep reduceForStatement(
      @NotNull ForStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<CodeRep, CodeRep>> init,
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

  @Override
  @NotNull
  public CodeRep reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull List<Branch> path,
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

  @Override
  @NotNull
  public CodeRep reduceLabeledStatement(
      @NotNull LabeledStatement node, @NotNull List<Branch> path, @NotNull CodeRep label, @NotNull CodeRep body) {
    CodeRep result = seqVA(label, factory.token(":"), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceReturnStatement(
      @NotNull ReturnStatement node, @NotNull List<Branch> path, @NotNull Maybe<CodeRep> expression) {
    return seqVA(
        factory.token("return"), seqVA(expression.orJust(factory.empty())), factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceSwitchCase(
      @NotNull SwitchCase node, @NotNull List<Branch> path, @NotNull CodeRep test, @NotNull List<CodeRep> consequent) {
    return seqVA(factory.token("case"), test, factory.token(":"), factory.seq(consequent));
  }

  @NotNull
  @Override
  public CodeRep reduceSwitchDefault(
      @NotNull SwitchDefault node, @NotNull List<Branch> path, @NotNull List<CodeRep> consequent) {
    return seqVA(factory.token("default"), factory.token(":"), factory.seq(consequent));
  }

  @Override
  @NotNull
  public CodeRep reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull List<Branch> path,
      @NotNull CodeRep discriminant,
      @NotNull List<CodeRep> cases) {
    return seqVA(
        factory.token("switch"), factory.paren(discriminant), factory.brace(
            factory.seq(cases)));
  }

  @NotNull
  @Override
  public CodeRep reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull CodeRep discriminant,
      @NotNull List<CodeRep> preDefaultCases,
      @NotNull CodeRep defaultCase,
      @NotNull List<CodeRep> postDefaultCases) {
    return seqVA(
        factory.token("switch"), factory.paren(discriminant), factory.brace(
            seqVA(
                factory.seq(preDefaultCases), defaultCase, factory.seq(
                    postDefaultCases))));
  }

  @Override
  @NotNull
  public CodeRep reduceThrowStatement(
      @NotNull ThrowStatement node, @NotNull List<Branch> path, @NotNull CodeRep expression) {
    return seqVA(factory.token("throw"), expression, factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull List<Branch> path,
      @NotNull CodeRep block,
      @NotNull CodeRep catchClause) {
    return seqVA(factory.token("try"), block, catchClause);
  }

  @NotNull
  @Override
  public CodeRep reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull List<Branch> path,
      @NotNull CodeRep block,
      @NotNull Maybe<CodeRep> catchClause,
      @NotNull CodeRep finalizer) {
    return seqVA(
        factory.token("try"), block, catchClause.orJust(factory.empty()), seqVA(
            factory.token("finally"), finalizer));
  }

  @NotNull
  @Override
  public CodeRep reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node, @NotNull List<Branch> path, @NotNull CodeRep declaration) {
    return seqVA(declaration, factory.semiOp());
  }

  @NotNull
  @Override
  public CodeRep reduceVariableDeclaration(
      @NotNull VariableDeclaration node, @NotNull List<Branch> path, @NotNull NonEmptyList<CodeRep> declarators) {
    return seqVA(factory.token(node.kind.name), factory.commaSep(declarators));
  }

  @Override
  @NotNull
  public CodeRep reduceWhileStatement(
      @NotNull WhileStatement node, @NotNull List<Branch> path, @NotNull CodeRep test, @NotNull CodeRep body) {
    CodeRep result = seqVA(factory.token("while"), factory.paren(test), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceWithStatement(
      @NotNull WithStatement node, @NotNull List<Branch> path, @NotNull CodeRep object, @NotNull CodeRep body) {
    CodeRep result = seqVA(factory.token("with"), factory.paren(object), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @NotNull
  public CodeRep reduceDataProperty(
      @NotNull DataProperty node, @NotNull List<Branch> path, @NotNull CodeRep name, @NotNull CodeRep value) {
    return seqVA(name, factory.token(":"), value.containsGroup ? factory.paren(value) : value);
  }

  @Override
  @NotNull
  public CodeRep reduceGetter(
      @NotNull Getter node, @NotNull List<Branch> path, @NotNull CodeRep name, @NotNull CodeRep body) {
    return seqVA(
        factory.token("get"), name, factory.paren(factory.empty()), factory.brace(
            body));
  }

  @Override
  @NotNull
  public CodeRep reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull CodeRep name,
      @NotNull CodeRep parameter,
      @NotNull CodeRep body) {
    return (seqVA(factory.token("set"), name, factory.paren(parameter), factory.brace(body)));
  }

  @NotNull
  @Override
  public CodeRep reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path) {
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
  public CodeRep reduceFunctionBody(
      @NotNull final FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<CodeRep> directives,
      @NotNull final List<CodeRep> statements) {
    CodeRep body;
    if (statements.isEmpty()) {
      body = factory.empty();
    } else {
      NonEmptyList<CodeRep> seNel = ((NonEmptyList<CodeRep>) statements);
      body = parenToAvoidBeingDirective(((NonEmptyList<Statement>) node.statements).head, seNel.head);
      body = seqVA(body, factory.seq(seNel.tail()));
    }
    return seqVA(factory.seq(directives), body);
  }

  @Override
  @NotNull
  public CodeRep reduceVariableDeclarator(
      @NotNull VariableDeclarator node, @NotNull List<Branch> path, @NotNull CodeRep binding,
      @NotNull Maybe<CodeRep> init) {
    CodeRep result = factory.init(
        binding, init.map(
            state -> state.containsGroup ? factory.paren(state) : factory.testIn(state)));
    result.containsIn = init.maybe(false, state -> state.containsIn && !state.containsGroup);
    return result;
  }

  @NotNull
  @Override
  public CodeRep reduceBlock(@NotNull Block node, @NotNull List<Branch> path, @NotNull List<CodeRep> statements) {
    return factory.brace(factory.seq(statements));
  }
}
