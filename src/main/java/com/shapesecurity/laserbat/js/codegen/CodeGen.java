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

package com.shapesecurity.laserbat.js.codegen;

import com.shapesecurity.laserbat.functional.F;
import com.shapesecurity.laserbat.functional.data.Either;
import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.functional.data.NonEmptyList;
import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.CatchClause;
import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.FunctionBody;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Node;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.SwitchCase;
import com.shapesecurity.laserbat.js.ast.SwitchDefault;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration;
import com.shapesecurity.laserbat.js.ast.VariableDeclarator;
import com.shapesecurity.laserbat.js.ast.directive.UnknownDirective;
import com.shapesecurity.laserbat.js.ast.directive.UseStrictDirective;
import com.shapesecurity.laserbat.js.ast.expression.ArrayExpression;
import com.shapesecurity.laserbat.js.ast.expression.AssignmentExpression;
import com.shapesecurity.laserbat.js.ast.expression.BinaryExpression;
import com.shapesecurity.laserbat.js.ast.expression.CallExpression;
import com.shapesecurity.laserbat.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ConditionalExpression;
import com.shapesecurity.laserbat.js.ast.expression.FunctionExpression;
import com.shapesecurity.laserbat.js.ast.expression.IdentifierExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.laserbat.js.ast.expression.NewExpression;
import com.shapesecurity.laserbat.js.ast.expression.ObjectExpression;
import com.shapesecurity.laserbat.js.ast.expression.PostfixExpression;
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ThisExpression;
import com.shapesecurity.laserbat.js.ast.operators.BinaryOperator;
import com.shapesecurity.laserbat.js.ast.operators.Precedence;
import com.shapesecurity.laserbat.js.ast.operators.Relational;
import com.shapesecurity.laserbat.js.ast.property.DataProperty;
import com.shapesecurity.laserbat.js.ast.property.Getter;
import com.shapesecurity.laserbat.js.ast.property.PropertyName;
import com.shapesecurity.laserbat.js.ast.property.PropertyName.ObjectPropertyNameKind;
import com.shapesecurity.laserbat.js.ast.property.Setter;
import com.shapesecurity.laserbat.js.ast.statement.BlockStatement;
import com.shapesecurity.laserbat.js.ast.statement.BreakStatement;
import com.shapesecurity.laserbat.js.ast.statement.ContinueStatement;
import com.shapesecurity.laserbat.js.ast.statement.DebuggerStatement;
import com.shapesecurity.laserbat.js.ast.statement.DoWhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.EmptyStatement;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForInStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForStatement;
import com.shapesecurity.laserbat.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.laserbat.js.ast.statement.IfStatement;
import com.shapesecurity.laserbat.js.ast.statement.LabeledStatement;
import com.shapesecurity.laserbat.js.ast.statement.ReturnStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.laserbat.js.ast.statement.ThrowStatement;
import com.shapesecurity.laserbat.js.ast.statement.TryCatchStatement;
import com.shapesecurity.laserbat.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.laserbat.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.laserbat.js.ast.statement.WhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.WithStatement;
import com.shapesecurity.laserbat.js.codegen.CodeRep.Empty;
import com.shapesecurity.laserbat.js.codegen.CodeRep.Semi;
import com.shapesecurity.laserbat.js.path.Branch;
import com.shapesecurity.laserbat.js.utils.Utils;
import com.shapesecurity.laserbat.js.visitor.Reducer;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;

public class CodeGen implements Reducer<CodeGen.State> {
  public static final CodeGen INSTANCE = new CodeGen();
  public static final Empty EMPTY = new Empty();
  private static final F<State, CodeRep> GET_CODE = state -> state.code;
  private static final F<State, CodeRep> GET_ASSIGNMENT_EXPR = state -> state.containsGroup ? paren(state.code) :
                                                                        state.code;

  protected CodeGen() {
  }

  public static String codeGen(@Nonnull Script script) {
    StringBuilder sb = new StringBuilder();
    TokenStream ts = new TokenStream(sb);
    script.reduce(INSTANCE).code.emit(ts, false);
    return sb.toString();
  }

  @Nonnull
  private static CodeRep p(@Nonnull Expression node, @Nonnull Precedence precedence, @Nonnull CodeRep a) {
    return node.getPrecedence().ordinal() < precedence.ordinal() ? paren(a) : a;
  }

  @Nonnull
  private static CodeRep p(@Nonnull Expression node, @Nonnull Precedence precedence, @Nonnull State a) {
    return p(node, precedence, a.code);
  }

  @Nonnull
  private static CodeRep t(@Nonnull String token) {
    return new CodeRep.Token(token);
  }

  @Nonnull
  private static CodeRep paren(@Nonnull CodeRep rep) {
    return new CodeRep.Paren(rep);
  }

  @Nonnull
  private static CodeRep bracket(@Nonnull CodeRep rep) {
    return new CodeRep.Bracket(rep);
  }

  @Nonnull
  private static CodeRep noIn(@Nonnull CodeRep rep) {
    return new CodeRep.NoIn(rep);
  }

  @Nonnull
  private static CodeRep testIn(@Nonnull State state) {
    return state.containsIn ? new CodeRep.ContainsIn(state.code) : state.code;
  }

  @Nonnull
  private static CodeRep seq(@Nonnull final CodeRep... reps) {
    ArrayList<CodeRep> arrayList = new ArrayList<>();
    Collections.addAll(arrayList, reps);
    return new CodeRep.Seq(List.from(arrayList));
  }

  @Nonnull
  private static CodeRep seq(@Nonnull List<CodeRep> reps) {
    return new CodeRep.Seq(reps);
  }

  @Nonnull
  protected static CodeRep semi() {
    return new Semi();
  }

  @SuppressWarnings("SameReturnValue")
  @Nonnull
  private static CodeRep empty() {
    return EMPTY;
  }

  @Nonnull
  private static CodeRep commaSep(@Nonnull List<CodeRep> pieces) {
    return new CodeRep.CommaSep(pieces);
  }

  @Nonnull
  protected CodeRep brace(@Nonnull CodeRep rep) {
    return new CodeRep.Brace(rep);
  }

  @Nonnull
  protected CodeRep semiOp() {
    return new CodeRep.SemiOp();
  }

  private CodeRep parenToAvoidBeingDirective(@Nonnull Node element, @Nonnull CodeRep original) {
    if (element instanceof ExpressionStatement
        && ((ExpressionStatement) element).expression instanceof LiteralStringExpression) {
      return seq(semiOp(), original);
    }
    return original;
  }

  @Override
  @Nonnull
  public State reduceScript(@Nonnull Script node, @Nonnull List<Branch> path, @Nonnull State body) {
    return body;
  }

  @Override
  @Nonnull
  public State reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return new State(t(node.name));
  }

  @Override
  @Nonnull
  public State reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State name) {
    return name;
  }

  @Override
  @Nonnull
  public State reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path) {
    return new State(t("this"));
  }

  @Override
  @Nonnull
  public State reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node, @Nonnull List<Branch> path) {
    return new State(t(Boolean.toString(node.value)));
  }

  @Override
  @Nonnull
  public State reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull List<Branch> path) {
    return new State(t(Utils.escapeStringLiteral(node.value)));
  }

  @Override
  @Nonnull
  public State reduceLiteralRegexExpression(@Nonnull LiteralRegExpExpression node, @Nonnull List<Branch> path) {
    return new State(t(node.value));
  }

  @Override
  @Nonnull
  public State reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node, @Nonnull List<Branch> path) {
    return new State(new CodeRep.Number(node.value));
  }

  @Override
  @Nonnull
  public State reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull List<Branch> path) {
    return new State(t("null"));
  }

  @Override
  @Nonnull
  public State reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> id,
      @Nonnull List<State> params,
      @Nonnull State body) {
    final CodeRep argBody = seq(paren(commaSep(params.map(GET_CODE))), brace(body.code));
    return new State(seq(t("function"), id.maybe(argBody, state -> seq(state.code, argBody))), false, false, true,
        false);
  }

  @Override
  @Nonnull
  public State reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State property) {
    return new State(seq(p(node.object, node.getPrecedence(), object), t("."), property.code), false, false,
        object.startsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State expression) {
    return new State(seq(p(node.object, node.getPrecedence(), object), bracket(expression.code)), false, false,
        object.startsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reduceObjectExpression(
      @Nonnull ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<State> properties) {
    return new State(brace(commaSep(properties.map(GET_CODE))), false, false, true, false);
  }

  @Override
  @Nonnull
  public State reduceBinaryExpression(
      @Nonnull BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State left,
      @Nonnull State right) {
    CodeRep leftCode = left.code;
    boolean leftStartsWithFunctionOrCurly = left.startsWithFunctionOrCurly;
    boolean leftContainsIn = left.containsIn;
    if (node.left.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
      leftCode = paren(leftCode);
      leftStartsWithFunctionOrCurly = false;
      leftContainsIn = false;
    }
    CodeRep rightCode = right.code;
    boolean rightContainsIn = right.containsIn;
    if (node.right.getPrecedence().ordinal() <= node.getPrecedence().ordinal()) {
      rightCode = paren(rightCode);
      rightContainsIn = false;
    }
    return new State(seq(leftCode, t(node.operator.getName()), rightCode),
        leftContainsIn || rightContainsIn || node.operator == Relational.In, node.operator == BinaryOperator.SEQUENCE,
        leftStartsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State binding,
      @Nonnull State expression) {
    CodeRep rightCode = expression.code;
    boolean rightContainsIn = expression.containsIn;
    if (node.expression.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
      rightCode = paren(rightCode);
      rightContainsIn = false;
    }
    return new State(seq(binding.code, t(node.operator.getName()), rightCode), rightContainsIn, false,
        binding.startsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reduceArrayExpression(
      @Nonnull ArrayExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<Maybe<State>> elements) {
    if (elements.isEmpty()) {
      return new State(bracket(empty()));
    }

    CodeRep content = commaSep(elements.map(states -> states.maybe(empty(), GET_ASSIGNMENT_EXPR)));
    if (elements.maybeLast().just().isNothing()) {
      content = seq(content, t(","));
    }
    return new State(bracket(content));
  }

  @Override
  @Nonnull
  public State reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State callee,
      @Nonnull List<State> arguments) {
    return new State(seq(t("new"), node.callee.getPrecedence() == Precedence.CALL ? paren(callee.code) : p(node.callee,
        node.getPrecedence(), callee), arguments.isEmpty() ? empty() : paren(commaSep(arguments.map(GET_CODE)))));
  }

  @Override
  @Nonnull
  public State reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State callee,
      @Nonnull List<State> arguments) {
    return new State(seq(p(node.callee, node.getPrecedence(), callee), paren(commaSep(arguments.map(GET_CODE)))), false,
        false, callee.startsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reducePostfixExpression(
      @Nonnull PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State operand) {
    return new State(seq(p(node.operand, node.getPrecedence(), operand), t(node.operator.getName())), false, false,
        operand.startsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State operand) {
    return new State(seq(t(node.operator.getName()), p(node.operand, node.getPrecedence(), operand)));
  }

  @Override
  @Nonnull
  public State reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull State consequent,
      @Nonnull State alternate) {
    return new State(seq(p(node.test, Precedence.LOGICAL_OR, test), t("?"), p(node.consequent, Precedence.ASSIGNMENT,
        consequent), t(":"), p(node.alternate, Precedence.ASSIGNMENT, alternate)),
        test.containsIn || alternate.containsIn, false, test.startsWithFunctionOrCurly, false);
  }

  @Override
  @Nonnull
  public State reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull State id,
      @Nonnull List<State> params,
      @Nonnull State body) {
    return new State(seq(t("function"), id.code, paren(commaSep(params.map(GET_CODE))), brace(body.code)));
  }

  @Nonnull
  @Override
  public State reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path) {
    return new State(seq(t("\"use strict\""), semiOp()));
  }

  @Nonnull
  @Override
  public State reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path) {
    return new State(seq(t("\"" + ("use strict".equals(node.getContents()) ? "use\\u0020strict" : node.getContents()) +
        '"'), semiOp()));
  }

  @Override
  @Nonnull
  public State reduceBlockStatement(@Nonnull BlockStatement node, @Nonnull List<Branch> path, @Nonnull State block) {
    return block;
  }

  @Override
  @Nonnull
  public State reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> label) {
    return new State(seq(t("break"), label.maybe(empty(), GET_CODE), semiOp()));
  }

  @Nonnull
  @Override
  public State reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull State param,
      @Nonnull State body) {
    return new State(seq(t("catch"), paren(param.code), body.code));
  }

  @Override
  @Nonnull
  public State reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> label) {
    return new State(seq(t("continue"), label.maybe(empty(), GET_CODE), semiOp()));
  }

  @Override
  @Nonnull
  public State reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path) {
    return new State(seq(t("debugger"), semiOp()));
  }

  @Override
  @Nonnull
  public State reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State body,
      @Nonnull State test) {
    return new State(seq(t("do"), body.code, t("while"), paren(test.code), semiOp()));
  }

  @Override
  @Nonnull
  public State reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path) {
    return new State(new Semi());
  }

  @Override
  @Nonnull
  public State reduceExpressionStatement(
      @Nonnull ExpressionStatement expressionStatement,
      @Nonnull List<Branch> path,
      @Nonnull State expression) {
    return new State(seq((expression.startsWithFunctionOrCurly ? paren(expression.code) : expression.code), semiOp()));
  }

  @Nonnull
  @Override
  public State reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<State, State> left,
      @Nonnull State right,
      @Nonnull State body) {
    return new State(seq(t("for"), paren(seq(noIn(testIn(Either.extract(left))), t("in"), right.code)), body.code),
        false, false, false, body.endsWithMissingElse);
  }

  @Nonnull
  @Override
  public State reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<State, State>> init,
      @Nonnull Maybe<State> test,
      @Nonnull Maybe<State> update,
      @Nonnull State body) {
    return new State(seq(t("for"), paren(seq(init.maybe(empty(), x -> noIn(testIn(x.either(y -> y, y -> y)))), semi(),
        test.maybe(empty(), GET_CODE), semi(), update.maybe(empty(), GET_CODE))), body.code), false, false, false,
        body.endsWithMissingElse);
  }

  @Override
  @Nonnull
  public State reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull State consequent,
      @Nonnull Maybe<State> alternate) {
    CodeRep consequentCode = consequent.code;
    if (alternate.isJust() && consequent.endsWithMissingElse) {
      consequentCode = brace(consequentCode);
    }
    return new State(seq(t("if"), paren(test.code), consequentCode, alternate.maybe(empty(), s -> seq(t("else"),
        s.code))), false, false, false, alternate.maybe(true, state -> state.endsWithMissingElse));
  }

  @Override
  @Nonnull
  public State reduceLabeledStatement(
      @Nonnull LabeledStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State label,
      @Nonnull State body) {
    return new State(seq(label.code, t(":"), body.code), false, false, false, body.endsWithMissingElse);
  }

  @Override
  @Nonnull
  public State reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<State> argument) {
    return new State(seq(t("return"), seq(argument.maybe(empty(), GET_CODE)), semiOp()));
  }

  @Nonnull
  @Override
  public State reduceSwitchCase(
      @Nonnull SwitchCase node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull List<State> consequent) {
    return new State(seq(t("case"), test.code, t(":"), seq(consequent.map(GET_CODE))));
  }

  @Nonnull
  @Override
  public State reduceSwitchDefault(
      @Nonnull SwitchDefault node,
      @Nonnull List<Branch> path,
      @Nonnull List<State> consequent) {
    return new State(seq(t("default"), t(":"), seq(consequent.map(GET_CODE))));
  }

  @Override
  @Nonnull
  public State reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State discriminant,
      @Nonnull List<State> cases) {
    return new State(seq(t("switch"), paren(discriminant.code), brace(seq(cases.map(GET_CODE)))));
  }

  @Nonnull
  @Override
  public State reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull State discriminant,
      @Nonnull List<State> cases,
      @Nonnull State defaultCase,
      @Nonnull List<State> postDefaultCases) {
    return new State(seq(t("switch"), paren(discriminant.code), brace(seq(seq(cases.map(GET_CODE)), defaultCase.code,
        seq(postDefaultCases.map(GET_CODE))))));
  }

  @Override
  @Nonnull
  public State reduceThrowStatement(@Nonnull ThrowStatement node, @Nonnull List<Branch> path, @Nonnull State argument) {
    return new State(seq(t("throw"), argument.code, semiOp()));
  }

  @Nonnull
  @Override
  public State reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State block,
      @Nonnull State catchClause) {
    return new State(seq(t("try"), block.code, catchClause.code));
  }

  @Nonnull
  @Override
  public State reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State block,
      @Nonnull Maybe<State> catchClause,
      @Nonnull State finalizer) {
    return new State(seq(t("try"), block.code, catchClause.maybe(empty(), GET_CODE), seq(t("finally"),
        finalizer.code)));
  }

  @Nonnull
  @Override
  public State reduceVariableDeclarationStatement(
      @Nonnull VariableDeclarationStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State declaration) {
    return new State(seq(declaration.code, semiOp()));
  }

  @Nonnull
  @Override
  public State reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<State> declarators) {
    return new State(seq(t(node.kind.name), commaSep(declarators.map(GET_CODE))));
  }

  @Override
  @Nonnull
  public State reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State test,
      @Nonnull State body) {
    return new State(seq(t("while"), paren(test.code), body.code), false, false, false, body.endsWithMissingElse);
  }

  @Override
  @Nonnull
  public State reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull State object,
      @Nonnull State body) {
    return new State(seq(t("with"), paren(object.code), body.code), false, false, false, body.endsWithMissingElse);
  }

  @Override
  @Nonnull
  public State reduceDataProperty(
      @Nonnull DataProperty node,
      @Nonnull List<Branch> path,
      @Nonnull State key,
      @Nonnull State value) {
    return new State(seq(key.code, t(":"), GET_ASSIGNMENT_EXPR.apply(value)));
  }

  @Override
  @Nonnull
  public State reduceGetter(@Nonnull Getter node, @Nonnull List<Branch> path, @Nonnull State key, @Nonnull State body) {
    return new State(seq(t("get"), key.code, paren(empty()), brace(body.code)));
  }

  @Override
  @Nonnull
  public State reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull State key,
      @Nonnull State parameter,
      @Nonnull State body) {
    return new State(seq(t("set"), key.code, paren(parameter.code), brace(body.code)));
  }

  @Nonnull
  @Override
  public State reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    if (node.kind == ObjectPropertyNameKind.Number || node.kind == ObjectPropertyNameKind.Identifier) {
      return new State(t(node.value));
    }
    return new State(t(Utils.escapeStringLiteral(node.value)));
  }

  @Override
  @Nonnull
  public State reduceFunctionBody(
      @Nonnull final FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<State> directives,
      @Nonnull final List<State> sourceElements) {
    return new State(seq(seq(directives.map(GET_CODE)), sourceElements.decons((head, tail) -> seq(
        parenToAvoidBeingDirective(node.statements.maybeHead().just(), head.code), seq(tail.map(GET_CODE)))).orJust(
        empty())));
  }

  @Override
  @Nonnull
  public State reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull State id,
      @Nonnull Maybe<State> init) {
    return new State(new CodeRep.Init(id.code, init.map(state -> {
      CodeRep exp = testIn(state);
      if (state.containsGroup) {
        exp = paren(exp);
      }
      return exp;
    })), init.maybe(false, state -> state.containsIn && !state.containsGroup), false, false, false);
  }

  @Nonnull
  @Override
  public State reduceBlock(@Nonnull Block node, @Nonnull List<Branch> path, @Nonnull List<State> statements) {
    return new State(brace(seq(statements.map(GET_CODE))));
  }

  public static class State {
    public final boolean containsIn;
    public final boolean containsGroup;
    public final boolean startsWithFunctionOrCurly;
    public final boolean endsWithMissingElse;
    @Nonnull
    public final CodeRep code;

    public State(
        @Nonnull CodeRep code,
        boolean containsIn,
        boolean containsGroup,
        boolean startsWithFunctionOrCurly,
        boolean endsWithMissingElse) {
      this.code = code;
      this.containsIn = containsIn;
      this.containsGroup = containsGroup;
      this.startsWithFunctionOrCurly = startsWithFunctionOrCurly;
      this.endsWithMissingElse = endsWithMissingElse;
    }

    public State(@Nonnull CodeRep code) {
      this(code, false, false, false, false);
    }
  }
}
