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

package com.shapesecurity.shift.js.codegen;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
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
import com.shapesecurity.shift.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.js.ast.expression.NewExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PostfixExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ThisExpression;
import com.shapesecurity.shift.js.ast.operators.BinaryOperator;
import com.shapesecurity.shift.js.ast.operators.Precedence;
import com.shapesecurity.shift.js.ast.property.DataProperty;
import com.shapesecurity.shift.js.ast.property.Getter;
import com.shapesecurity.shift.js.ast.property.PropertyName;
import com.shapesecurity.shift.js.ast.property.PropertyName.PropertyNameKind;
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
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.utils.Utils;
import com.shapesecurity.shift.js.visitor.Reducer;

public final class CodeGen implements Reducer<CodeRep> {
  public static final CodeGen COMPACT = new CodeGen(new CodeRepFactory());
  public static final CodeGen PRETTY = new CodeGen(new FormattedCodeRepFactory());

  private final CodeRepFactory factory;

  protected CodeGen(@Nonnull CodeRepFactory factory) {
    this.factory = factory;
  }

  @Nonnull
  public static String codeGen(@Nonnull Script script) {
    return codeGen(script, false);
  }

  @Nonnull
  public static String codeGen(@Nonnull Script script, boolean pretty) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    script.reduce(pretty ? PRETTY : COMPACT).emit(ts, false);
    return sb.toString();
  }

  @Nonnull
  public static String codeGen(@Nonnull Script script, @Nonnull FormattedCodeRepFactory instance) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    script.reduce(new CodeGen(instance)).emit(ts, false);
    return sb.toString();
  }

  @Nonnull
  private CodeRep seqVA(@Nonnull CodeRep... reps) {
    ArrayList<CodeRep> arrayList = new ArrayList<>();
    Collections.addAll(arrayList, reps);
    return factory.seq(List.from(arrayList));
  }

  @Nonnull
  private CodeRep parenToAvoidBeingDirective(@Nonnull Node element, @Nonnull CodeRep original) {
    if (element instanceof ExpressionStatement &&
        ((ExpressionStatement) element).expression instanceof LiteralStringExpression) {
      return seqVA(factory.semiOp(), original);
    }
    return original;
  }

  @Override
  @Nonnull
  public CodeRep reduceScript(@Nonnull Script node, @Nonnull List<Branch> path, @Nonnull CodeRep body) {
    return body;
  }

  @Override
  @Nonnull
  public CodeRep reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return factory.token(node.name);
  }

  @Override
  @Nonnull
  public CodeRep reduceIdentifierExpression(
      @Nonnull IdentifierExpression node, @Nonnull List<Branch> path, @Nonnull CodeRep name) {
    return name;
  }

  @Override
  @Nonnull
  public CodeRep reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path) {
    return factory.token("this");
  }

  @Override
  @Nonnull
  public CodeRep reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node, @Nonnull List<Branch> path) {
    return factory.token(Boolean.toString(node.value));
  }

  @Override
  @Nonnull
  public CodeRep reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull List<Branch> path) {
    return factory.token(Utils.escapeStringLiteral(node.value));
  }

  @Override
  @Nonnull
  public CodeRep reduceLiteralRegexExpression(@Nonnull LiteralRegExpExpression node, @Nonnull List<Branch> path) {
    return factory.token(node.value);
  }

  @Override
  @Nonnull
  public CodeRep reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node, @Nonnull List<Branch> path) {
    return factory.num(node.value);
  }

  @Override
  @Nonnull
  public CodeRep reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull List<Branch> path) {
    return factory.token("null");
  }

  @Override
  @Nonnull
  public CodeRep reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<CodeRep> id,
      @Nonnull List<CodeRep> params,
      @Nonnull CodeRep body) {
    final CodeRep argBody = seqVA(factory.paren(factory.commaSep(params)), factory.brace(body));
    CodeRep result = seqVA(factory.token("function"), id.maybe(argBody, state -> seqVA(state, argBody)));
    result.startsWithFunctionOrCurly = true;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep object,
      @Nonnull CodeRep property) {
    CodeRep result = seqVA(
        factory.expr(node.object, node.getPrecedence(), object), factory.token("."), property);
    result.startsWithFunctionOrCurly = object.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep object,
      @Nonnull CodeRep expression) {
    CodeRep result = seqVA(
        factory.expr(node.object, node.getPrecedence(), object), factory.bracket(
            expression));
    result.startsWithFunctionOrCurly = object.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceObjectExpression(
      @Nonnull ObjectExpression node, @Nonnull List<Branch> path, @Nonnull List<CodeRep> properties) {
    CodeRep result = factory.brace(factory.commaSep(properties));
    result.startsWithFunctionOrCurly = true;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceBinaryExpression(
      @Nonnull BinaryExpression node, @Nonnull List<Branch> path, @Nonnull CodeRep left, @Nonnull CodeRep right) {
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
  @Nonnull
  public CodeRep reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep binding,
      @Nonnull CodeRep expression) {
    CodeRep rightCode = expression;
    boolean rightContainsIn = expression.containsIn;
    if (node.expression.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
      rightCode = factory.paren(rightCode);
      rightContainsIn = false;
    }
    CodeRep result = seqVA(binding, factory.token(node.operator.getName()), rightCode);
    result.containsIn = rightContainsIn;
    result.startsWithFunctionOrCurly = binding.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceArrayExpression(
      @Nonnull ArrayExpression node, @Nonnull List<Branch> path, @Nonnull List<Maybe<CodeRep>> elements) {
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
  @Nonnull
  public CodeRep reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep callee,
      @Nonnull List<CodeRep> arguments) {
    callee = node.callee.getPrecedence() == Precedence.CALL ? factory.paren(callee) : factory.expr(
        node.callee, node.getPrecedence(), callee);
    return seqVA(
        factory.token("new"), callee, arguments.isEmpty() ? factory.empty() : factory.paren(
            factory.commaSep(arguments)));
  }

  @Override
  @Nonnull
  public CodeRep reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep callee,
      @Nonnull List<CodeRep> arguments) {
    CodeRep result = seqVA(
        factory.expr(node.callee, node.getPrecedence(), callee), factory.paren(
            factory.commaSep(arguments)));
    result.startsWithFunctionOrCurly = callee.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reducePostfixExpression(
      @Nonnull PostfixExpression node, @Nonnull List<Branch> path, @Nonnull CodeRep operand) {
    CodeRep result = seqVA(
        factory.expr(node.operand, node.getPrecedence(), operand), factory.token(node.operator.getName()));
    result.startsWithFunctionOrCurly = operand.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reducePrefixExpression(
      @Nonnull PrefixExpression node, @Nonnull List<Branch> path, @Nonnull CodeRep operand) {
    return seqVA(
        factory.token(node.operator.getName()), factory.expr(
            node.operand, node.getPrecedence(), operand));
  }

  @Override
  @Nonnull
  public CodeRep reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep test,
      @Nonnull CodeRep consequent,
      @Nonnull CodeRep alternate) {
    CodeRep result = seqVA(
        factory.expr(node.test, Precedence.LOGICAL_OR, test), factory.token("?"), factory.expr(
            node.consequent, Precedence.ASSIGNMENT, consequent), factory.token(":"), factory.expr(
            node.alternate, Precedence.ASSIGNMENT, alternate));
    result.containsIn = test.containsIn || alternate.containsIn;
    result.startsWithFunctionOrCurly = test.startsWithFunctionOrCurly;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep id,
      @Nonnull List<CodeRep> params,
      @Nonnull CodeRep body) {
    return seqVA(
        factory.token("function"), id, factory.paren(factory.commaSep(params)), factory.brace(body));
  }

  @Nonnull
  @Override
  public CodeRep reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path) {
    return seqVA(factory.token("\"use strict\""), factory.semiOp());
  }

  @Nonnull
  @Override
  public CodeRep reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path) {
    return seqVA(
        factory.token(
            "\"" + ("use strict".equals(node.getContents()) ? "use\\u0020strict" : node.getContents()) + '"'),
        factory.semiOp());
  }

  @Override
  @Nonnull
  public CodeRep reduceBlockStatement(
      @Nonnull BlockStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep block) {
    return block;
  }

  @Override
  @Nonnull
  public CodeRep reduceBreakStatement(
      @Nonnull BreakStatement node, @Nonnull List<Branch> path, @Nonnull Maybe<CodeRep> label) {
    return seqVA(factory.token("break"), label.orJust(factory.empty()), factory.semiOp());
  }

  @Nonnull
  @Override
  public CodeRep reduceCatchClause(
      @Nonnull CatchClause node, @Nonnull List<Branch> path, @Nonnull CodeRep param, @Nonnull CodeRep body) {
    return seqVA(factory.token("catch"), factory.paren(param), body);
  }

  @Override
  @Nonnull
  public CodeRep reduceContinueStatement(
      @Nonnull ContinueStatement node, @Nonnull List<Branch> path, @Nonnull Maybe<CodeRep> label) {
    return seqVA(factory.token("continue"), label.orJust(factory.empty()), factory.semiOp());
  }

  @Override
  @Nonnull
  public CodeRep reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path) {
    return seqVA(factory.token("debugger"), factory.semiOp());
  }

  @Override
  @Nonnull
  public CodeRep reduceDoWhileStatement(
      @Nonnull DoWhileStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep body, @Nonnull CodeRep test) {
    return seqVA(
        factory.token("do"), body, factory.token("while"), factory.paren(test), factory.semiOp());
  }

  @Override
  @Nonnull
  public CodeRep reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path) {
    return factory.semi();
  }

  @Override
  @Nonnull
  public CodeRep reduceExpressionStatement(
      @Nonnull ExpressionStatement expressionStatement, @Nonnull List<Branch> path, @Nonnull CodeRep expression) {
    return seqVA((expression.startsWithFunctionOrCurly ? factory.paren(expression) : expression), factory.semiOp());
  }

  @Nonnull
  @Override
  public CodeRep reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<CodeRep, CodeRep> left,
      @Nonnull CodeRep right,
      @Nonnull CodeRep body) {
    CodeRep result = seqVA(
        factory.token("for"), factory.paren(
            seqVA(
                factory.noIn(factory.testIn(Either.extract(left))), factory.token("in"), right)), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Nonnull
  @Override
  public CodeRep reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<CodeRep, CodeRep>> init,
      @Nonnull Maybe<CodeRep> test,
      @Nonnull Maybe<CodeRep> update,
      @Nonnull CodeRep body) {
    CodeRep result = seqVA(
        factory.token("for"), factory.paren(
            seqVA(
                init.maybe(factory.empty(), x -> factory.noIn(factory.testIn(Either.extract(x)))),
                factory.semi(),
                test.orJust(
                    factory.empty()),
                factory.semi(),
                update.orJust(factory.empty()))), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep test,
      @Nonnull CodeRep consequent,
      @Nonnull Maybe<CodeRep> alternate) {
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
  @Nonnull
  public CodeRep reduceLabeledStatement(
      @Nonnull LabeledStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep label, @Nonnull CodeRep body) {
    CodeRep result = seqVA(label, factory.token(":"), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceReturnStatement(
      @Nonnull ReturnStatement node, @Nonnull List<Branch> path, @Nonnull Maybe<CodeRep> argument) {
    return seqVA(
        factory.token("return"), seqVA(argument.orJust(factory.empty())), factory.semiOp());
  }

  @Nonnull
  @Override
  public CodeRep reduceSwitchCase(
      @Nonnull SwitchCase node, @Nonnull List<Branch> path, @Nonnull CodeRep test, @Nonnull List<CodeRep> consequent) {
    return seqVA(factory.token("case"), test, factory.token(":"), factory.seq(consequent));
  }

  @Nonnull
  @Override
  public CodeRep reduceSwitchDefault(
      @Nonnull SwitchDefault node, @Nonnull List<Branch> path, @Nonnull List<CodeRep> consequent) {
    return seqVA(factory.token("default"), factory.token(":"), factory.seq(consequent));
  }

  @Override
  @Nonnull
  public CodeRep reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep discriminant,
      @Nonnull List<CodeRep> cases) {
    return seqVA(
        factory.token("switch"), factory.paren(discriminant), factory.brace(
            factory.seq(cases)));
  }

  @Nonnull
  @Override
  public CodeRep reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep discriminant,
      @Nonnull List<CodeRep> cases,
      @Nonnull CodeRep defaultCase,
      @Nonnull List<CodeRep> postDefaultCases) {
    return seqVA(
        factory.token("switch"), factory.paren(discriminant), factory.brace(
            seqVA(
                factory.seq(cases), defaultCase, factory.seq(
                    postDefaultCases))));
  }

  @Override
  @Nonnull
  public CodeRep reduceThrowStatement(
      @Nonnull ThrowStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep argument) {
    return seqVA(factory.token("throw"), argument, factory.semiOp());
  }

  @Nonnull
  @Override
  public CodeRep reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep block,
      @Nonnull CodeRep catchClause) {
    return seqVA(factory.token("try"), block, catchClause);
  }

  @Nonnull
  @Override
  public CodeRep reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep block,
      @Nonnull Maybe<CodeRep> catchClause,
      @Nonnull CodeRep finalizer) {
    return seqVA(
        factory.token("try"), block, catchClause.orJust(factory.empty()), seqVA(
            factory.token("finally"), finalizer));
  }

  @Nonnull
  @Override
  public CodeRep reduceVariableDeclarationStatement(
      @Nonnull VariableDeclarationStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep declaration) {
    return seqVA(declaration, factory.semiOp());
  }

  @Nonnull
  @Override
  public CodeRep reduceVariableDeclaration(
      @Nonnull VariableDeclaration node, @Nonnull List<Branch> path, @Nonnull NonEmptyList<CodeRep> declarators) {
    return seqVA(factory.token(node.kind.name), factory.commaSep(declarators));
  }

  @Override
  @Nonnull
  public CodeRep reduceWhileStatement(
      @Nonnull WhileStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep test, @Nonnull CodeRep body) {
    CodeRep result = seqVA(factory.token("while"), factory.paren(test), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceWithStatement(
      @Nonnull WithStatement node, @Nonnull List<Branch> path, @Nonnull CodeRep object, @Nonnull CodeRep body) {
    CodeRep result = seqVA(factory.token("with"), factory.paren(object), body);
    result.endsWithMissingElse = body.endsWithMissingElse;
    return result;
  }

  @Override
  @Nonnull
  public CodeRep reduceDataProperty(
      @Nonnull DataProperty node, @Nonnull List<Branch> path, @Nonnull CodeRep key, @Nonnull CodeRep value) {
    return seqVA(key, factory.token(":"), value.containsGroup ? factory.paren(value) : value);
  }

  @Override
  @Nonnull
  public CodeRep reduceGetter(
      @Nonnull Getter node, @Nonnull List<Branch> path, @Nonnull CodeRep key, @Nonnull CodeRep body) {
    return seqVA(
        factory.token("get"), key, factory.paren(factory.empty()), factory.brace(
            body));
  }

  @Override
  @Nonnull
  public CodeRep reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull CodeRep key,
      @Nonnull CodeRep parameter,
      @Nonnull CodeRep body) {
    return (seqVA(factory.token("set"), key, factory.paren(parameter), factory.brace(body)));
  }

  @Nonnull
  @Override
  public CodeRep reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    if (node.kind == PropertyNameKind.Number || node.kind == PropertyNameKind.Identifier) {
      return (factory.token(node.value));
    }
    return factory.token(Utils.escapeStringLiteral(node.value));
  }

  @Override
  @Nonnull
  public CodeRep reduceFunctionBody(
      @Nonnull final FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<CodeRep> directives,
      @Nonnull final List<CodeRep> sourceElements) {
    CodeRep body;
    if (sourceElements.isEmpty()) {
      body = factory.empty();
    } else {
      NonEmptyList<CodeRep> seNel = ((NonEmptyList<CodeRep>) sourceElements);
      body = parenToAvoidBeingDirective(((NonEmptyList<Statement>) node.statements).head, seNel.head);
      body = seqVA(body, factory.seq(seNel.tail()));
    }
    return seqVA(factory.seq(directives), body);
  }

  @Override
  @Nonnull
  public CodeRep reduceVariableDeclarator(
      @Nonnull VariableDeclarator node, @Nonnull List<Branch> path, @Nonnull CodeRep id, @Nonnull Maybe<CodeRep> init) {
    CodeRep result = factory.init(
        id, init.map(
            state -> state.containsGroup ? factory.paren(state) : factory.testIn(state)));
    result.containsIn = init.maybe(false, state -> state.containsIn && !state.containsGroup);
    return result;
  }

  @Nonnull
  @Override
  public CodeRep reduceBlock(@Nonnull Block node, @Nonnull List<Branch> path, @Nonnull List<CodeRep> statements) {
    return factory.brace(factory.seq(statements));
  }
}
