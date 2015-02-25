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

package com.shapesecurity.shift.visitor;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.Directive;
import com.shapesecurity.shift.ast.Expression;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
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
import com.shapesecurity.shift.ast.property.DataProperty;
import com.shapesecurity.shift.ast.property.Getter;
import com.shapesecurity.shift.ast.property.ObjectProperty;
import com.shapesecurity.shift.ast.property.PropertyName;
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

import org.jetbrains.annotations.NotNull;

public class CloneReducer
    implements ReducerP<Script, FunctionBody, ObjectProperty, PropertyName, Identifier, Expression, Directive, Statement, Block, VariableDeclarator, VariableDeclaration, SwitchCase, SwitchDefault, CatchClause> {
  public static final CloneReducer INSTANCE = new CloneReducer();

  protected CloneReducer() {
  }

  @NotNull
  @Override
  public Script reduceScript(@NotNull Script node, @NotNull ImmutableList<Branch> path, @NotNull FunctionBody body) {
    return new Script(body);
  }

  @NotNull
  @Override
  public Identifier reduceIdentifier(@NotNull Identifier node, @NotNull ImmutableList<Branch> path) {
    return new Identifier(node.name);
  }

  @NotNull
  @Override
  public Expression reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Identifier identifier) {
    return new IdentifierExpression(identifier);
  }

  @NotNull
  @Override
  public Expression reduceThisExpression(@NotNull ThisExpression node, @NotNull ImmutableList<Branch> path) {
    return new ThisExpression();
  }

  @NotNull
  @Override
  public Expression reduceLiteralBooleanExpression(@NotNull LiteralBooleanExpression node, @NotNull ImmutableList<Branch> path) {
    return new LiteralBooleanExpression(node.value);
  }

  @NotNull
  @Override
  public Expression reduceLiteralStringExpression(@NotNull LiteralStringExpression node, @NotNull ImmutableList<Branch> path) {
    return new LiteralStringExpression(node.value);
  }

  @NotNull
  @Override
  public Expression reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node, @NotNull ImmutableList<Branch> path) {
    return new LiteralRegExpExpression(node.value);
  }

  @NotNull
  @Override
  public Expression reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node, @NotNull ImmutableList<Branch> path) {
    return new LiteralNumericExpression(node.value);
  }

  @NotNull
  @Override
  public Expression reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node,
                                                    @NotNull ImmutableList<Branch> path) {
    return new LiteralInfinityExpression();
  }

  @NotNull
  @Override
  public Expression reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull ImmutableList<Branch> path) {
    return new LiteralNullExpression();
  }

  @NotNull
  @Override
  public Expression reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<Identifier> name,
      @NotNull ImmutableList<Identifier> parameters,
      @NotNull FunctionBody body) {
    return new FunctionExpression(name, parameters, body);
  }

  @NotNull
  @Override
  public Expression reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression object,
      @NotNull Identifier property) {
    return new StaticMemberExpression(object, property);
  }

  @NotNull
  @Override
  public Expression reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression object,
      @NotNull Expression expression) {
    return new ComputedMemberExpression(object, expression);
  }

  @NotNull
  @Override
  public Expression reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<ObjectProperty> properties) {
    return new ObjectExpression(properties);
  }

  @NotNull
  @Override
  public Expression reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression left,
      @NotNull Expression right) {
    return new BinaryExpression(node.operator, left, right);
  }

  @NotNull
  @Override
  public Expression reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression binding,
      @NotNull Expression expression) {
    return new AssignmentExpression(node.operator, binding, expression);
  }

  @NotNull
  @Override
  public Expression reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<Maybe<Expression>> elements) {
    return new ArrayExpression(elements);
  }

  @NotNull
  @Override
  public Expression reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression callee,
      @NotNull ImmutableList<Expression> arguments) {
    return new NewExpression(callee, arguments);
  }

  @NotNull
  @Override
  public Expression reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression callee,
      @NotNull ImmutableList<Expression> arguments) {
    return new CallExpression(callee, arguments);
  }

  @NotNull
  @Override
  public Expression reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression operand) {
    return new PostfixExpression(node.operator, operand);
  }

  @NotNull
  @Override
  public Expression reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression operand) {
    return new PrefixExpression(node.operator, operand);
  }

  @NotNull
  @Override
  public Expression reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression test,
      @NotNull Expression consequent,
      @NotNull Expression alternate) {
    return new ConditionalExpression(test, consequent, alternate);
  }

  @NotNull
  @Override
  public Statement reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Identifier name,
      @NotNull ImmutableList<Identifier> params,
      @NotNull FunctionBody body) {
    return new FunctionDeclaration(name, params, body);
  }

  @NotNull
  @Override
  public Directive reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull ImmutableList<Branch> path) {
    return new UseStrictDirective();
  }

  @NotNull
  @Override
  public Directive reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull ImmutableList<Branch> path) {
    return new UnknownDirective(node.value);
  }

  @NotNull
  @Override
  public Statement reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Block block) {
    return new BlockStatement(block);
  }

  @NotNull
  @Override
  public Statement reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<Identifier> label) {
    return new BreakStatement(label);
  }

  @NotNull
  @Override
  public CatchClause reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Identifier binding,
      @NotNull Block body) {
    return new CatchClause(binding, body);
  }

  @NotNull
  @Override
  public Statement reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<Identifier> label) {
    return new ContinueStatement(label);
  }

  @NotNull
  @Override
  public Statement reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull ImmutableList<Branch> path) {
    return new DebuggerStatement();
  }

  @NotNull
  @Override
  public Statement reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Statement body,
      @NotNull Expression test) {
    return new DoWhileStatement(body, test);
  }

  @NotNull
  @Override
  public Statement reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull ImmutableList<Branch> path) {
    return new EmptyStatement();
  }

  @NotNull
  @Override
  public Statement reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression expression) {
    return new ExpressionStatement(expression);
  }

  @NotNull
  @Override
  public Statement reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Either<VariableDeclaration, Expression> left,
      @NotNull Expression right,
      @NotNull Statement body) {
    return new ForInStatement(left, right, body);
  }

  @NotNull
  @Override
  public Statement reduceForStatement(
      @NotNull ForStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<Either<VariableDeclaration, Expression>> init,
      @NotNull Maybe<Expression> test,
      @NotNull Maybe<Expression> update,
      @NotNull Statement body) {
    return new ForStatement(init, test, update, body);
  }

  @NotNull
  @Override
  public Statement reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression test,
      @NotNull Statement consequent,
      @NotNull Maybe<Statement> alternate) {
    return new IfStatement(test, consequent, alternate);
  }

  @NotNull
  @Override
  public Statement reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Identifier label,
      @NotNull Statement body) {
    return new LabeledStatement(label, body);
  }

  @NotNull
  @Override
  public Statement reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Maybe<Expression> expression) {
    return new ReturnStatement(expression);
  }

  @NotNull
  @Override
  public SwitchCase reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression test,
      @NotNull ImmutableList<Statement> consequent) {
    return new SwitchCase(test, consequent);
  }

  @NotNull
  @Override
  public SwitchDefault reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<Statement> consequent) {
    return new SwitchDefault(consequent);
  }

  @NotNull
  @Override
  public Statement reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression discriminant,
      @NotNull ImmutableList<SwitchCase> cases) {
    return new SwitchStatement(discriminant, cases);
  }

  @NotNull
  @Override
  public Statement reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression discriminant,
      @NotNull ImmutableList<SwitchCase> preDefaultCases,
      @NotNull SwitchDefault defaultCase,
      @NotNull ImmutableList<SwitchCase> postDefaultCases) {
    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
  }

  @NotNull
  @Override
  public Statement reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression expression) {
    return new ThrowStatement(expression);
  }

  @NotNull
  @Override
  public Statement reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Block block,
      @NotNull CatchClause catchClause) {
    return new TryCatchStatement(block, catchClause);
  }

  @NotNull
  @Override
  public Statement reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Block block,
      @NotNull Maybe<CatchClause> catchClause,
      @NotNull Block finalizer) {
    return new TryFinallyStatement(block, catchClause, finalizer);
  }

  @NotNull
  @Override
  public Statement reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull VariableDeclaration declaration) {
    return new VariableDeclarationStatement(declaration);
  }

  @NotNull
  @Override
  public VariableDeclaration reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull ImmutableList<Branch> path,
      @NotNull NonEmptyImmutableList<VariableDeclarator> declarators) {
    return new VariableDeclaration(node.kind, declarators);
  }

  @NotNull
  @Override
  public Statement reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression test,
      @NotNull Statement body) {
    return new WhileStatement(test, body);
  }

  @NotNull
  @Override
  public Statement reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Expression object,
      @NotNull Statement body) {
    return new WithStatement(object, body);
  }

  @NotNull
  @Override
  public ObjectProperty reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull ImmutableList<Branch> path,
      @NotNull PropertyName name,
      @NotNull Expression value) {
    return new DataProperty(name, value);
  }

  @NotNull
  @Override
  public ObjectProperty reduceGetter(
      @NotNull Getter node,
      @NotNull ImmutableList<Branch> path,
      @NotNull PropertyName name,
      @NotNull FunctionBody body) {
    return new Getter(name, body);
  }

  @NotNull
  @Override
  public ObjectProperty reduceSetter(
      @NotNull Setter node,
      @NotNull ImmutableList<Branch> path,
      @NotNull PropertyName name,
      @NotNull Identifier parameter,
      @NotNull FunctionBody body) {
    return new Setter(name, parameter, body);
  }

  @NotNull
  @Override
  public PropertyName reducePropertyName(@NotNull PropertyName node, @NotNull ImmutableList<Branch> path) {
    return new PropertyName(node);
  }

  @NotNull
  @Override
  public FunctionBody reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull ImmutableList<Branch> path,
      @NotNull ImmutableList<Directive> directives,
      @NotNull ImmutableList<Statement> statements) {
    return new FunctionBody(directives, statements);
  }

  @NotNull
  @Override
  public VariableDeclarator reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull ImmutableList<Branch> path,
      @NotNull Identifier binding,
      @NotNull Maybe<Expression> init) {
    return new VariableDeclarator(binding, init);
  }

  @NotNull
  @Override
  public Block reduceBlock(@NotNull Block node, @NotNull ImmutableList<Branch> path, @NotNull ImmutableList<Statement> statements) {
    return new Block(statements);
  }
}
