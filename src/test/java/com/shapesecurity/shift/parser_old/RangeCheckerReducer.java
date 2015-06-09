/*
 * Copyright 2014 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"));
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

package com.shapesecurity.shift.parser_old;

import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.ast.*;
import org.jetbrains.annotations.NotNull;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RangeCheckerReducer extends MonoidalReducer<RangeCheckerReducer.RangeChecker> {
  protected RangeCheckerReducer() {
    super(RangeChecker.MONOID);
  }

  static class RangeChecker {
    public final int begin, end;

    RangeChecker(int begin, int end) {
      this.begin = begin;
      this.end = end;
    }

    public final static Monoid<RangeChecker> MONOID = new Monoid<RangeChecker>() {
      @NotNull
      @Override
      public RangeChecker identity() {
        return new RangeChecker(Integer.MAX_VALUE, Integer.MIN_VALUE);
      }

      @NotNull
      @Override
      public RangeChecker append(RangeChecker a, RangeChecker b) {
        assertTrue(a.end <= b.begin);
        return new RangeChecker(a.begin, b.end);
      }
    };

    private static RangeChecker from(Node node) {
      SourceLocation loc = node.getLoc();
      assertNotNull(loc);
      assertNotNull(loc.source);
      return new RangeChecker(loc.offset, loc.offset + loc.source.length());
    }
  }

  RangeChecker accept(Node node, RangeChecker ch) {
    RangeChecker result = RangeChecker.from(node);
    assertTrue(result.begin <= ch.begin);
    assertTrue(result.end >= ch.end);
    return result;
  }

  @NotNull
  @Override
  public RangeChecker reduceArrayExpression(@NotNull ArrayExpression node,
                                            @NotNull ImmutableList<Branch> path,
                                            @NotNull ImmutableList<Maybe<RangeChecker>> elements) {
    return accept(node, super.reduceArrayExpression(node, path, elements));
  }

  @NotNull
  @Override
  public RangeChecker reduceAssignmentExpression(@NotNull AssignmentExpression node,
                                                 @NotNull ImmutableList<Branch> path,
                                                 @NotNull RangeChecker binding,
                                                 @NotNull RangeChecker expression) {
    return accept(node, super.reduceAssignmentExpression(node, path, binding, expression));
  }

  @NotNull
  @Override
  public RangeChecker reduceBinaryExpression(@NotNull BinaryExpression node,
                                             @NotNull ImmutableList<Branch> path,
                                             @NotNull RangeChecker left,
                                             @NotNull RangeChecker right) {
    return accept(node, super.reduceBinaryExpression(node, path, left, right));
  }

  @NotNull
  @Override
  public RangeChecker reduceBlock(@NotNull Block node, @NotNull ImmutableList<Branch> path,
                                  @NotNull ImmutableList<RangeChecker> statements) {
    return accept(node, super.reduceBlock(node, path, statements));
  }

  @NotNull
  @Override
  public RangeChecker reduceBlockStatement(@NotNull BlockStatement node, @NotNull ImmutableList<Branch> path,
                                           @NotNull RangeChecker block) {
    return accept(node, super.reduceBlockStatement(node, path, block));
  }

  @NotNull
  @Override
  public RangeChecker reduceBreakStatement(@NotNull BreakStatement node, @NotNull ImmutableList<Branch> path,
                                           @NotNull Maybe<RangeChecker> label) {
    return accept(node, super.reduceBreakStatement(node, path, label));
  }

  @NotNull
  @Override
  public RangeChecker reduceCallExpression(@NotNull CallExpression node, @NotNull ImmutableList<Branch> path,
                                           @NotNull RangeChecker callee,
                                           @NotNull ImmutableList<RangeChecker> arguments) {
    return accept(node, super.reduceCallExpression(node, path, callee, arguments));
  }

  @NotNull
  @Override
  public RangeChecker reduceCatchClause(@NotNull CatchClause node, @NotNull ImmutableList<Branch> path,
                                        @NotNull RangeChecker binding,
                                        @NotNull RangeChecker body) {
    return accept(node, super.reduceCatchClause(node, path, binding, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceComputedMemberExpression(@NotNull ComputedMemberExpression node,
                                                     @NotNull ImmutableList<Branch> path,
                                                     @NotNull RangeChecker object,
                                                     @NotNull RangeChecker expression) {
    return accept(node, super.reduceComputedMemberExpression(node, path, object, expression));
  }

  @NotNull
  @Override
  public RangeChecker reduceConditionalExpression(@NotNull ConditionalExpression node,
                                                  @NotNull ImmutableList<Branch> path,
                                                  @NotNull RangeChecker test,
                                                  @NotNull RangeChecker consequent,
                                                  @NotNull RangeChecker alternate) {
    return accept(node, super.reduceConditionalExpression(node, path, test, consequent, alternate));
  }

  @NotNull
  @Override
  public RangeChecker reduceContinueStatement(@NotNull ContinueStatement node,
                                              @NotNull ImmutableList<Branch> path,
                                              @NotNull Maybe<RangeChecker> label) {
    return accept(node, super.reduceContinueStatement(node, path, label));
  }

  @NotNull
  @Override
  public RangeChecker reduceDataProperty(@NotNull DataProperty node, @NotNull ImmutableList<Branch> path,
                                         @NotNull RangeChecker name,
                                         @NotNull RangeChecker value) {
    return accept(node, super.reduceDataProperty(node, path, name, value));
  }

  @NotNull
  @Override
  public RangeChecker reduceDebuggerStatement(@NotNull DebuggerStatement node,
                                              @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceDebuggerStatement(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceDoWhileStatement(@NotNull DoWhileStatement node,
                                             @NotNull ImmutableList<Branch> path,
                                             @NotNull RangeChecker body,
                                             @NotNull RangeChecker test) {
    return accept(node, super.reduceDoWhileStatement(node, path, body, test));
  }

  @NotNull
  @Override
  public RangeChecker reduceEmptyStatement(@NotNull EmptyStatement node,
                                           @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceEmptyStatement(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceExpressionStatement(@NotNull ExpressionStatement node,
                                                @NotNull ImmutableList<Branch> path,
                                                @NotNull RangeChecker expression) {
    return accept(node, super.reduceExpressionStatement(node, path, expression));
  }

  @NotNull
  @Override
  public RangeChecker reduceForInStatement(@NotNull ForInStatement node, @NotNull ImmutableList<Branch> path,
                                           @NotNull Either<RangeChecker, RangeChecker> left,
                                           @NotNull RangeChecker right,
                                           @NotNull RangeChecker body) {
    return accept(node, super.reduceForInStatement(node, path, left, right, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceForStatement(@NotNull ForStatement node, @NotNull ImmutableList<Branch> path,
                                         @NotNull Maybe<Either<RangeChecker, RangeChecker>> init,
                                         @NotNull Maybe<RangeChecker> test,
                                         @NotNull Maybe<RangeChecker> update,
                                         @NotNull RangeChecker body) {
    return accept(node, super.reduceForStatement(node, path, init, test, update, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<Branch> path,
                                         @NotNull ImmutableList<RangeChecker> directives,
                                         @NotNull ImmutableList<RangeChecker> statements) {
    return accept(node, super.reduceFunctionBody(node, path, directives, statements));
  }

  @NotNull
  @Override
  public RangeChecker reduceFunctionDeclaration(@NotNull FunctionDeclaration node,
                                                @NotNull ImmutableList<Branch> path,
                                                @NotNull RangeChecker name,
                                                @NotNull ImmutableList<RangeChecker> params,
                                                @NotNull RangeChecker body) {
    return accept(node, super.reduceFunctionDeclaration(node, path, name, params, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceFunctionExpression(@NotNull FunctionExpression node,
                                               @NotNull ImmutableList<Branch> path,
                                               @NotNull Maybe<RangeChecker> name,
                                               @NotNull ImmutableList<RangeChecker> parameters,
                                               @NotNull RangeChecker body) {
    return accept(node, super.reduceFunctionExpression(node, path, name, parameters, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceGetter(@NotNull Getter node, @NotNull ImmutableList<Branch> path,
                                   @NotNull RangeChecker name,
                                   @NotNull RangeChecker body) {
    return accept(node, super.reduceGetter(node, path, name, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceIdentifier(@NotNull Identifier node, @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceIdentifier(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceIdentifierExpression(@NotNull IdentifierExpression node,
                                                 @NotNull ImmutableList<Branch> path,
                                                 @NotNull RangeChecker identifier) {
    return accept(node, super.reduceIdentifierExpression(node, path, identifier));
  }

  @NotNull
  @Override
  public RangeChecker reduceIfStatement(@NotNull IfStatement node, @NotNull ImmutableList<Branch> path,
                                        @NotNull RangeChecker test,
                                        @NotNull RangeChecker consequent,
                                        @NotNull Maybe<RangeChecker> alternate) {
    return accept(node, super.reduceIfStatement(node, path, test, consequent, alternate));
  }

  @NotNull
  @Override
  public RangeChecker reduceLabeledStatement(@NotNull LabeledStatement node,
                                             @NotNull ImmutableList<Branch> path,
                                             @NotNull RangeChecker label,
                                             @NotNull RangeChecker body) {
    return accept(node, super.reduceLabeledStatement(node, path, label, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node,
                                                     @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceLiteralBooleanExpression(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceLiteralNullExpression(@NotNull LiteralNullExpression node,
                                                  @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceLiteralNullExpression(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node,
                                                     @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceLiteralNumericExpression(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node,
                                                    @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceLiteralRegExpExpression(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceLiteralStringExpression(@NotNull LiteralStringExpression node,
                                                    @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceLiteralStringExpression(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceNewExpression(@NotNull NewExpression node, @NotNull ImmutableList<Branch> path,
                                          @NotNull RangeChecker callee,
                                          @NotNull ImmutableList<RangeChecker> arguments) {
    return accept(node, super.reduceNewExpression(node, path, callee, arguments));
  }

  @NotNull
  @Override
  public RangeChecker reduceObjectExpression(@NotNull ObjectExpression node,
                                             @NotNull ImmutableList<Branch> path,
                                             @NotNull ImmutableList<RangeChecker> properties) {
    return accept(node, super.reduceObjectExpression(node, path, properties));
  }

  @NotNull
  @Override
  public RangeChecker reducePostfixExpression(@NotNull PostfixExpression node,
                                              @NotNull ImmutableList<Branch> path,
                                              @NotNull RangeChecker operand) {
    return accept(node, super.reducePostfixExpression(node, path, operand));
  }

  @NotNull
  @Override
  public RangeChecker reducePrefixExpression(@NotNull PrefixExpression node,
                                             @NotNull ImmutableList<Branch> path,
                                             @NotNull RangeChecker operand) {
    return accept(node, super.reducePrefixExpression(node, path, operand));
  }

  @NotNull
  @Override
  public RangeChecker reducePropertyName(@NotNull PropertyName node, @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reducePropertyName(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceReturnStatement(@NotNull ReturnStatement node,
                                            @NotNull ImmutableList<Branch> path,
                                            @NotNull Maybe<RangeChecker> expression) {
    return accept(node, super.reduceReturnStatement(node, path, expression));
  }

  @NotNull
  @Override
  public RangeChecker reduceScript(@NotNull Script node, @NotNull ImmutableList<Branch> path,
                                   @NotNull RangeChecker body) {
    return accept(node, super.reduceScript(node, path, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceSetter(@NotNull Setter node, @NotNull ImmutableList<Branch> path,
                                   @NotNull RangeChecker name,
                                   @NotNull RangeChecker parameter,
                                   @NotNull RangeChecker body) {
    return accept(node, super.reduceSetter(node, path, name, parameter, body));
  }

  @NotNull
  @Override
  public RangeChecker reduceStaticMemberExpression(@NotNull StaticMemberExpression node,
                                                   @NotNull ImmutableList<Branch> path,
                                                   @NotNull RangeChecker object,
                                                   @NotNull RangeChecker property) {
    return accept(node, super.reduceStaticMemberExpression(node, path, object, property));
  }


  @NotNull
  @Override
  public RangeChecker reduceSwitchCase(@NotNull SwitchCase node, @NotNull ImmutableList<Branch> path,
                                       @NotNull RangeChecker test,
                                       @NotNull ImmutableList<RangeChecker> consequent) {
    return accept(node, super.reduceSwitchCase(node, path, test, consequent));
  }

  @NotNull
  @Override
  public RangeChecker reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull ImmutableList<Branch> path,
                                          @NotNull ImmutableList<RangeChecker> consequent) {
    return accept(node, super.reduceSwitchDefault(node, path, consequent));
  }

  @NotNull
  @Override
  public RangeChecker reduceSwitchStatement(@NotNull SwitchStatement node,
                                            @NotNull ImmutableList<Branch> path,
                                            @NotNull RangeChecker discriminant,
                                            @NotNull ImmutableList<RangeChecker> cases) {
    return accept(node, super.reduceSwitchStatement(node, path, discriminant, cases));
  }

  @NotNull
  @Override
  public RangeChecker reduceSwitchStatementWithDefault(@NotNull SwitchStatementWithDefault node,
                                                       @NotNull ImmutableList<Branch> path,
                                                       @NotNull RangeChecker discriminant,
                                                       @NotNull ImmutableList<RangeChecker> preDefaultCases,
                                                       @NotNull RangeChecker defaultCase,
                                                       @NotNull ImmutableList<RangeChecker> postDefaultCases) {
    return accept(node, super.reduceSwitchStatementWithDefault(node, path, discriminant, preDefaultCases, defaultCase,
        postDefaultCases));
  }

  @NotNull
  @Override
  public RangeChecker reduceThisExpression(@NotNull ThisExpression node,
                                           @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceThisExpression(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceThrowStatement(@NotNull ThrowStatement node, @NotNull ImmutableList<Branch> path,
                                           @NotNull RangeChecker expression) {
    return accept(node, super.reduceThrowStatement(node, path, expression));
  }

  @NotNull
  @Override
  public RangeChecker reduceTryCatchStatement(@NotNull TryCatchStatement node,
                                              @NotNull ImmutableList<Branch> path,
                                              @NotNull RangeChecker block,
                                              @NotNull RangeChecker catchClause) {
    return accept(node, super.reduceTryCatchStatement(node, path, block, catchClause));
  }

  @NotNull
  @Override
  public RangeChecker reduceTryFinallyStatement(@NotNull TryFinallyStatement node,
                                                @NotNull ImmutableList<Branch> path,
                                                @NotNull RangeChecker block,
                                                @NotNull Maybe<RangeChecker> catchClause,
                                                @NotNull RangeChecker finalizer) {
    return accept(node, super.reduceTryFinallyStatement(node, path, block, catchClause, finalizer));
  }

  @NotNull
  @Override
  public RangeChecker reduceUnknownDirective(@NotNull UnknownDirective node,
                                             @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceUnknownDirective(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceUseStrictDirective(@NotNull UseStrictDirective node,
                                               @NotNull ImmutableList<Branch> path) {
    return accept(node, super.reduceUseStrictDirective(node, path));
  }

  @NotNull
  @Override
  public RangeChecker reduceVariableDeclaration(@NotNull VariableDeclaration node,
                                                @NotNull ImmutableList<Branch> path,
                                                @NotNull NonEmptyImmutableList<RangeChecker> declarators) {
    return accept(node, super.reduceVariableDeclaration(node, path, declarators));
  }

  @NotNull
  @Override
  public RangeChecker reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node,
                                                         @NotNull ImmutableList<Branch> path,
                                                         @NotNull RangeChecker declaration) {
    return accept(node, super.reduceVariableDeclarationStatement(node, path, declaration));
  }

  @NotNull
  @Override
  public RangeChecker reduceVariableDeclarator(@NotNull VariableDeclarator node,
                                               @NotNull ImmutableList<Branch> path,
                                               @NotNull RangeChecker binding,
                                               @NotNull Maybe<RangeChecker> init) {
    return accept(node, super.reduceVariableDeclarator(node, path, binding, init));
  }
}
