///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.visitor.disabled;
//
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.functional.data.NonEmptyImmutableList;
//import Block;
//import CatchClause;
//import Directive;
//import Expression;
//import FunctionBody;
//import com.shapesecurity.shift.ast.Identifier;
//import Script;
//import Statement;
//import SwitchCase;
//import SwitchDefault;
//import VariableDeclaration;
//import VariableDeclarator;
//import com.shapesecurity.shift.ast.directive.UnknownDirective;
//import com.shapesecurity.shift.ast.directive.UseStrictDirective;
//import com.shapesecurity.shift.ast.expression.ArrayExpression;
//import com.shapesecurity.shift.ast.expression.AssignmentExpression;
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import com.shapesecurity.shift.ast.expression.CallExpression;
//import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
//import com.shapesecurity.shift.ast.expression.ConditionalExpression;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
//import com.shapesecurity.shift.ast.expression.LiteralInfinityExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNullExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
//import com.shapesecurity.shift.ast.expression.LiteralRegExpExpression;
//import com.shapesecurity.shift.ast.expression.LiteralStringExpression;
//import com.shapesecurity.shift.ast.expression.NewExpression;
//import com.shapesecurity.shift.ast.expression.ObjectExpression;
//import com.shapesecurity.shift.ast.expression.PostfixExpression;
//import com.shapesecurity.shift.ast.expression.PrefixExpression;
//import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
//import com.shapesecurity.shift.ast.expression.ThisExpression;
//import com.shapesecurity.shift.ast.property.DataProperty;
//import com.shapesecurity.shift.ast.property.Getter;
//import com.shapesecurity.shift.ast.property.ObjectProperty;
//import com.shapesecurity.shift.ast.property.PropertyName;
//import com.shapesecurity.shift.ast.property.Setter;
//import com.shapesecurity.shift.ast.statement.BlockStatement;
//import com.shapesecurity.shift.ast.statement.BreakStatement;
//import com.shapesecurity.shift.ast.statement.ContinueStatement;
//import com.shapesecurity.shift.ast.statement.DebuggerStatement;
//import com.shapesecurity.shift.ast.statement.DoWhileStatement;
//import com.shapesecurity.shift.ast.statement.EmptyStatement;
//import com.shapesecurity.shift.ast.statement.ExpressionStatement;
//import com.shapesecurity.shift.ast.statement.ForInStatement;
//import com.shapesecurity.shift.ast.statement.ForStatement;
//import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
//import com.shapesecurity.shift.ast.statement.IfStatement;
//import com.shapesecurity.shift.ast.statement.LabeledStatement;
//import com.shapesecurity.shift.ast.statement.ReturnStatement;
//import com.shapesecurity.shift.ast.statement.SwitchStatement;
//import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
//import com.shapesecurity.shift.ast.statement.ThrowStatement;
//import com.shapesecurity.shift.ast.statement.TryCatchStatement;
//import com.shapesecurity.shift.ast.statement.TryFinallyStatement;
//import com.shapesecurity.shift.ast.statement.VariableDeclarationStatement;
//import com.shapesecurity.shift.ast.statement.WhileStatement;
//import com.shapesecurity.shift.ast.statement.WithStatement;
//import Branch;
//
//import javax.annotation.Nonnull;
//
//public class CloneReducer
//    implements ReducerP<Script, FunctionBody, ObjectProperty, PropertyName, Identifier, Expression, Directive, Statement, Block, VariableDeclarator, VariableDeclaration, SwitchCase, SwitchDefault, CatchClause> {
//  public static final CloneReducer INSTANCE = new CloneReducer();
//
//  protected CloneReducer() {
//  }
//
//  @Nonnull
//  @Override
//  public Script reduceScript(@Nonnull Script node, @Nonnull ImmutableList<Branch> path, @Nonnull FunctionBody body) {
//    return new Script(body);
//  }
//
//  @Nonnull
//  @Override
//  public Identifier reduceIdentifier(@Nonnull Identifier node, @Nonnull ImmutableList<Branch> path) {
//    return new Identifier(node.name);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceIdentifierExpression(
//      @Nonnull IdentifierExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Identifier identifier) {
//    return new IdentifierExpression(identifier);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceThisExpression(@Nonnull ThisExpression node, @Nonnull ImmutableList<Branch> path) {
//    return new ThisExpression();
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node, @Nonnull ImmutableList<Branch> path) {
//    return new LiteralBooleanExpression(node.value);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull ImmutableList<Branch> path) {
//    return new LiteralStringExpression(node.value);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node, @Nonnull ImmutableList<Branch> path) {
//    return new LiteralRegExpExpression(node.value);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node, @Nonnull ImmutableList<Branch> path) {
//    return new LiteralNumericExpression(node.value);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node,
//                                                    @Nonnull ImmutableList<Branch> path) {
//    return new LiteralInfinityExpression();
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull ImmutableList<Branch> path) {
//    return new LiteralNullExpression();
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceFunctionExpression(
//      @Nonnull FunctionExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Identifier> name,
//      @Nonnull ImmutableList<Identifier> parameters,
//      @Nonnull FunctionBody body) {
//    return new FunctionExpression(name, parameters, body);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceStaticMemberExpression(
//      @Nonnull StaticMemberExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression object,
//      @Nonnull Identifier property) {
//    return new StaticMemberExpression(object, property);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceComputedMemberExpression(
//      @Nonnull ComputedMemberExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression object,
//      @Nonnull Expression expression) {
//    return new ComputedMemberExpression(object, expression);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceObjectExpression(
//      @Nonnull ObjectExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<ObjectProperty> properties) {
//    return new ObjectExpression(properties);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceBinaryExpression(
//      @Nonnull BinaryExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression left,
//      @Nonnull Expression right) {
//    return new BinaryExpression(node.operator, left, right);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceAssignmentExpression(
//      @Nonnull AssignmentExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression binding,
//      @Nonnull Expression expression) {
//    return new AssignmentExpression(node.operator, binding, expression);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceArrayExpression(
//      @Nonnull ArrayExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Maybe<Expression>> elements) {
//    return new ArrayExpression(elements);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceNewExpression(
//      @Nonnull NewExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression callee,
//      @Nonnull ImmutableList<Expression> arguments) {
//    return new NewExpression(callee, arguments);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceCallExpression(
//      @Nonnull CallExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression callee,
//      @Nonnull ImmutableList<Expression> arguments) {
//    return new CallExpression(callee, arguments);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reducePostfixExpression(
//      @Nonnull PostfixExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression operand) {
//    return new PostfixExpression(node.operator, operand);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reducePrefixExpression(
//      @Nonnull PrefixExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression operand) {
//    return new PrefixExpression(node.operator, operand);
//  }
//
//  @Nonnull
//  @Override
//  public Expression reduceConditionalExpression(
//      @Nonnull ConditionalExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression test,
//      @Nonnull Expression consequent,
//      @Nonnull Expression alternate) {
//    return new ConditionalExpression(test, consequent, alternate);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceFunctionDeclaration(
//      @Nonnull FunctionDeclaration node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Identifier name,
//      @Nonnull ImmutableList<Identifier> params,
//      @Nonnull FunctionBody body) {
//    return new FunctionDeclaration(name, params, body);
//  }
//
//  @Nonnull
//  @Override
//  public Directive reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull ImmutableList<Branch> path) {
//    return new UseStrictDirective();
//  }
//
//  @Nonnull
//  @Override
//  public Directive reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull ImmutableList<Branch> path) {
//    return new UnknownDirective(node.value);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceBlockStatement(
//      @Nonnull BlockStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Block block) {
//    return new BlockStatement(block);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceBreakStatement(
//      @Nonnull BreakStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Identifier> label) {
//    return new BreakStatement(label);
//  }
//
//  @Nonnull
//  @Override
//  public CatchClause reduceCatchClause(
//      @Nonnull CatchClause node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Identifier binding,
//      @Nonnull Block body) {
//    return new CatchClause(binding, body);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceContinueStatement(
//      @Nonnull ContinueStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Identifier> label) {
//    return new ContinueStatement(label);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull ImmutableList<Branch> path) {
//    return new DebuggerStatement();
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceDoWhileStatement(
//      @Nonnull DoWhileStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Statement body,
//      @Nonnull Expression test) {
//    return new DoWhileStatement(body, test);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull ImmutableList<Branch> path) {
//    return new EmptyStatement();
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceExpressionStatement(
//      @Nonnull ExpressionStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression expression) {
//    return new ExpressionStatement(expression);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceForInStatement(
//      @Nonnull ForInStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Either<VariableDeclaration, Expression> left,
//      @Nonnull Expression right,
//      @Nonnull Statement body) {
//    return new ForInStatement(left, right, body);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceForStatement(
//      @Nonnull ForStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Either<VariableDeclaration, Expression>> init,
//      @Nonnull Maybe<Expression> test,
//      @Nonnull Maybe<Expression> update,
//      @Nonnull Statement body) {
//    return new ForStatement(init, test, update, body);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceIfStatement(
//      @Nonnull IfStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression test,
//      @Nonnull Statement consequent,
//      @Nonnull Maybe<Statement> alternate) {
//    return new IfStatement(test, consequent, alternate);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceLabeledStatement(
//      @Nonnull LabeledStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Identifier label,
//      @Nonnull Statement body) {
//    return new LabeledStatement(label, body);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceReturnStatement(
//      @Nonnull ReturnStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Expression> expression) {
//    return new ReturnStatement(expression);
//  }
//
//  @Nonnull
//  @Override
//  public SwitchCase reduceSwitchCase(
//      @Nonnull SwitchCase node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression test,
//      @Nonnull ImmutableList<Statement> consequent) {
//    return new SwitchCase(test, consequent);
//  }
//
//  @Nonnull
//  @Override
//  public SwitchDefault reduceSwitchDefault(
//      @Nonnull SwitchDefault node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Statement> consequent) {
//    return new SwitchDefault(consequent);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceSwitchStatement(
//      @Nonnull SwitchStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression discriminant,
//      @Nonnull ImmutableList<SwitchCase> cases) {
//    return new SwitchStatement(discriminant, cases);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceSwitchStatementWithDefault(
//      @Nonnull SwitchStatementWithDefault node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression discriminant,
//      @Nonnull ImmutableList<SwitchCase> preDefaultCases,
//      @Nonnull SwitchDefault defaultCase,
//      @Nonnull ImmutableList<SwitchCase> postDefaultCases) {
//    return new SwitchStatementWithDefault(discriminant, preDefaultCases, defaultCase, postDefaultCases);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceThrowStatement(
//      @Nonnull ThrowStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression expression) {
//    return new ThrowStatement(expression);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceTryCatchStatement(
//      @Nonnull TryCatchStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Block block,
//      @Nonnull CatchClause catchClause) {
//    return new TryCatchStatement(block, catchClause);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceTryFinallyStatement(
//      @Nonnull TryFinallyStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Block block,
//      @Nonnull Maybe<CatchClause> catchClause,
//      @Nonnull Block finalizer) {
//    return new TryFinallyStatement(block, catchClause, finalizer);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceVariableDeclarationStatement(
//      @Nonnull VariableDeclarationStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull VariableDeclaration declaration) {
//    return new VariableDeclarationStatement(declaration);
//  }
//
//  @Nonnull
//  @Override
//  public VariableDeclaration reduceVariableDeclaration(
//      @Nonnull VariableDeclaration node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull NonEmptyImmutableList<VariableDeclarator> declarators) {
//    return new VariableDeclaration(node.kind, declarators);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceWhileStatement(
//      @Nonnull WhileStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression test,
//      @Nonnull Statement body) {
//    return new WhileStatement(test, body);
//  }
//
//  @Nonnull
//  @Override
//  public Statement reduceWithStatement(
//      @Nonnull WithStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Expression object,
//      @Nonnull Statement body) {
//    return new WithStatement(object, body);
//  }
//
//  @Nonnull
//  @Override
//  public ObjectProperty reduceDataProperty(
//      @Nonnull DataProperty node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull PropertyName name,
//      @Nonnull Expression value) {
//    return new DataProperty(name, value);
//  }
//
//  @Nonnull
//  @Override
//  public ObjectProperty reduceGetter(
//      @Nonnull Getter node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull PropertyName name,
//      @Nonnull FunctionBody body) {
//    return new Getter(name, body);
//  }
//
//  @Nonnull
//  @Override
//  public ObjectProperty reduceSetter(
//      @Nonnull Setter node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull PropertyName name,
//      @Nonnull Identifier parameter,
//      @Nonnull FunctionBody body) {
//    return new Setter(name, parameter, body);
//  }
//
//  @Nonnull
//  @Override
//  public PropertyName reducePropertyName(@Nonnull PropertyName node, @Nonnull ImmutableList<Branch> path) {
//    return new PropertyName(node);
//  }
//
//  @Nonnull
//  @Override
//  public FunctionBody reduceFunctionBody(
//      @Nonnull FunctionBody node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Directive> directives,
//      @Nonnull ImmutableList<Statement> statements) {
//    return new FunctionBody(directives, statements);
//  }
//
//  @Nonnull
//  @Override
//  public VariableDeclarator reduceVariableDeclarator(
//      @Nonnull VariableDeclarator node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Identifier binding,
//      @Nonnull Maybe<Expression> init) {
//    return new VariableDeclarator(binding, init);
//  }
//
//  @Nonnull
//  @Override
//  public Block reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<Branch> path, @Nonnull ImmutableList<Statement> statements) {
//    return new Block(statements);
//  }
//}
