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
//import com.shapesecurity.functional.F;
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
//import com.shapesecurity.shift.ast.expression.BinaryExpression;
//import com.shapesecurity.shift.ast.expression.CallExpression;
//import com.shapesecurity.shift.ast.expression.ComputedMemberExpression;
//import com.shapesecurity.shift.ast.expression.ConditionalExpression;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.LiteralBooleanExpression;
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
//import com.shapesecurity.shift.visitor.Director;
//import javax.annotation.Nonnull;
//
//public class FixPointTransformer extends LazyCloner {
//  @Nonnull
//  private final TransformerP<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ? extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>>
//      t;
//
//  public FixPointTransformer(
//      @Nonnull TransformerP<? extends DirtyState<Script>, ? extends DirtyState<FunctionBody>, ? extends
//          DirtyState<ObjectProperty>, ? extends DirtyState<PropertyName>, ? extends DirtyState<Identifier>, ? extends
//          DirtyState<Expression>, ? extends DirtyState<Directive>, ? extends DirtyState<Statement>, ? extends
//          DirtyState<Block>, ? extends DirtyState<VariableDeclarator>, ? extends DirtyState<VariableDeclaration>, ?
//          extends DirtyState<SwitchCase>, ? extends DirtyState<SwitchDefault>, ? extends DirtyState<CatchClause>> t) {
//    super();
//    this.t = t;
//  }
//
//  private F<Expression, DirtyState<Expression>> bindExp(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceExpression(this, node1, path).setDirty());
//  }
//
//  private F<Identifier, DirtyState<Identifier>> bindIdent(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceIdentifier(this, node1, path).setDirty());
//  }
//
//  private F<Statement, DirtyState<Statement>> bindStmt(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceStatement(this, node1, path).setDirty());
//  }
//
//  private F<Block, DirtyState<Block>> bindBlock(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceBlock(this, node1, path).setDirty());
//  }
//
//  private F<VariableDeclaration, DirtyState<VariableDeclaration>> bindVarDeclStmt(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceVariableDeclaration(this, node1, path).setDirty());
//  }
//
//  private F<SwitchCase, DirtyState<SwitchCase>> bindCase(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceSwitchCase(this, node1, path).setDirty());
//  }
//
//  private F<SwitchDefault, DirtyState<SwitchDefault>> bindDefault(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceSwitchDefault(this, node1, path).setDirty());
//  }
//
//  private F<Directive, DirtyState<Directive>> bindDirective(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceDirective(this, node1, path).setDirty());
//  }
//
//  private F<PropertyName, DirtyState<PropertyName>> bindPropName(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reducePropertyName(this, node1, path).setDirty());
//  }
//
//  private F<ObjectProperty, DirtyState<ObjectProperty>> bindProp(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceObjectProperty(this, node1, path).setDirty());
//  }
//
//  private F<Script, DirtyState<Script>> bindProgram(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceScript(this, node1, path).setDirty());
//  }
//
//  private F<FunctionBody, DirtyState<FunctionBody>> bindProgramBody(@Nonnull final ImmutableList<Branch> path) {
//    return node -> this.t.transform(node).onDirty(node1 -> Director.reduceFunctionBody(this, node1, path).setDirty());
//  }
//
//  @Nonnull
//  public Script transform(@Nonnull Script script, @Nonnull ImmutableList<Branch> path) {
//    return Director.reduceScript(this, script, path).node;
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Script> reduceScript(
//      @Nonnull Script node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<FunctionBody> body) {
//    return super.reduceScript(node, path, body).bind(this.bindProgram(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Identifier> reduceIdentifier(@Nonnull Identifier node, @Nonnull ImmutableList<Branch> path) {
//    return super.reduceIdentifier(node, path).bind(this.bindIdent(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceIdentifierExpression(
//      @Nonnull IdentifierExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Identifier> identifier) {
//    return super.reduceIdentifierExpression(node, path, identifier).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceThisExpression(@Nonnull ThisExpression node, @Nonnull ImmutableList<Branch> path) {
//    return super.reduceThisExpression(node, path).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceLiteralBooleanExpression(
//      @Nonnull LiteralBooleanExpression node,
//      @Nonnull ImmutableList<Branch> path) {
//    return super.reduceLiteralBooleanExpression(node, path).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceLiteralStringExpression(
//      @Nonnull LiteralStringExpression node,
//      @Nonnull ImmutableList<Branch> path) {
//    return super.reduceLiteralStringExpression(node, path).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceLiteralRegExpExpression(
//      @Nonnull LiteralRegExpExpression node,
//      @Nonnull ImmutableList<Branch> path) {
//    return super.reduceLiteralRegExpExpression(node, path).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceLiteralNumericExpression(
//      @Nonnull LiteralNumericExpression node,
//      @Nonnull ImmutableList<Branch> path) {
//    return super.reduceLiteralNumericExpression(node, path).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceLiteralNullExpression(
//      @Nonnull LiteralNullExpression node,
//      @Nonnull ImmutableList<Branch> path) {
//    return super.reduceLiteralNullExpression(node, path).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceFunctionExpression(
//      @Nonnull FunctionExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<DirtyState<Identifier>> name,
//      @Nonnull ImmutableList<DirtyState<Identifier>> parameters,
//      @Nonnull DirtyState<FunctionBody> body) {
//    return super.reduceFunctionExpression(node, path, name, parameters, body).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceStaticMemberExpression(
//      @Nonnull StaticMemberExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> object,
//      @Nonnull DirtyState<Identifier> property) {
//    return super.reduceStaticMemberExpression(node, path, object, property).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceComputedMemberExpression(
//      @Nonnull ComputedMemberExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> object,
//      @Nonnull DirtyState<Expression> expression) {
//    return super.reduceComputedMemberExpression(node, path, object, expression).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceObjectExpression(
//      @Nonnull ObjectExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<DirtyState<ObjectProperty>> properties) {
//    return super.reduceObjectExpression(node, path, properties).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceBinaryExpression(
//      @Nonnull BinaryExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> left,
//      @Nonnull DirtyState<Expression> right) {
//    return super.reduceBinaryExpression(node, path, left, right).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceArrayExpression(
//      @Nonnull ArrayExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<Maybe<DirtyState<Expression>>> elements) {
//    return super.reduceArrayExpression(node, path, elements).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceNewExpression(
//      @Nonnull NewExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> callee,
//      @Nonnull ImmutableList<DirtyState<Expression>> arguments) {
//    return super.reduceNewExpression(node, path, callee, arguments).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceCallExpression(
//      @Nonnull CallExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> callee,
//      @Nonnull ImmutableList<DirtyState<Expression>> arguments) {
//    return super.reduceCallExpression(node, path, callee, arguments).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reducePostfixExpression(
//      @Nonnull PostfixExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> operand) {
//    return super.reducePostfixExpression(node, path, operand).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reducePrefixExpression(
//      @Nonnull PrefixExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> operand) {
//    return super.reducePrefixExpression(node, path, operand).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> reduceConditionalExpression(
//      @Nonnull ConditionalExpression node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> test,
//      @Nonnull DirtyState<Expression> consequent,
//      @Nonnull DirtyState<Expression> alternate) {
//    return super.reduceConditionalExpression(node, path, test, consequent, alternate).bind(this.bindExp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceFunctionDeclaration(
//      @Nonnull FunctionDeclaration node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Identifier> name,
//      @Nonnull ImmutableList<DirtyState<Identifier>> params,
//      @Nonnull DirtyState<FunctionBody> body) {
//    return super.reduceFunctionDeclaration(node, path, name, params, body).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Directive> reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull ImmutableList<Branch> path) {
//    return super.reduceUseStrictDirective(node, path).bind(this.bindDirective(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Directive> reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull ImmutableList<Branch> path) {
//    return super.reduceUnknownDirective(node, path).bind(this.bindDirective(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceBlockStatement(
//      @Nonnull BlockStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Block> block) {
//    return super.reduceBlockStatement(node, path, block).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceBreakStatement(
//      @Nonnull BreakStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<DirtyState<Identifier>> label) {
//    return super.reduceBreakStatement(node, path, label).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<CatchClause> reduceCatchClause(
//      @Nonnull CatchClause node,
//      @Nonnull final ImmutableList<Branch> path,
//      @Nonnull DirtyState<Identifier> binding,
//      @Nonnull DirtyState<Block> body) {
//    return super.reduceCatchClause(node, path, binding, body).bind(
//        node1 -> this.t.transform(node1).onDirty(
//            node2 -> Director.reduceCatchClause(this, node2, path).setDirty()));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceContinueStatement(
//      @Nonnull ContinueStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<DirtyState<Identifier>> label) {
//    return super.reduceContinueStatement(node, path, label).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull ImmutableList<Branch> path) {
//    return super.reduceDebuggerStatement(node, path).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceDoWhileStatement(
//      @Nonnull DoWhileStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Statement> body,
//      @Nonnull DirtyState<Expression> test) {
//    return super.reduceDoWhileStatement(node, path, body, test).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull ImmutableList<Branch> path) {
//    return super.reduceEmptyStatement(node, path).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceExpressionStatement(
//      @Nonnull ExpressionStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> expression) {
//    return super.reduceExpressionStatement(node, path, expression).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceForInStatement(
//      @Nonnull ForInStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Either<DirtyState<VariableDeclaration>, DirtyState<Expression>> left,
//      @Nonnull DirtyState<Expression> right,
//      @Nonnull DirtyState<Statement> body) {
//    return super.reduceForInStatement(node, path, left, right, body).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceForStatement(
//      @Nonnull ForStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<Either<DirtyState<VariableDeclaration>, DirtyState<Expression>>> init,
//      @Nonnull Maybe<DirtyState<Expression>> test,
//      @Nonnull Maybe<DirtyState<Expression>> update,
//      @Nonnull DirtyState<Statement> body) {
//    return super.reduceForStatement(node, path, init, test, update, body).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceIfStatement(
//      @Nonnull IfStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> test,
//      @Nonnull DirtyState<Statement> consequent,
//      @Nonnull Maybe<DirtyState<Statement>> alternate) {
//    return super.reduceIfStatement(node, path, test, consequent, alternate).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceLabeledStatement(
//      @Nonnull LabeledStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Identifier> label,
//      @Nonnull DirtyState<Statement> body) {
//    return super.reduceLabeledStatement(node, path, label, body).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceReturnStatement(
//      @Nonnull ReturnStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull Maybe<DirtyState<Expression>> expression) {
//    return super.reduceReturnStatement(node, path, expression).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchCase> reduceSwitchCase(
//      @Nonnull SwitchCase node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> test,
//      @Nonnull ImmutableList<DirtyState<Statement>> consequent) {
//    return super.reduceSwitchCase(node, path, test, consequent).bind(this.bindCase(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchDefault> reduceSwitchDefault(
//      @Nonnull SwitchDefault node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<DirtyState<Statement>> consequent) {
//    return super.reduceSwitchDefault(node, path, consequent).bind(this.bindDefault(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceSwitchStatement(
//      @Nonnull SwitchStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> discriminant,
//      @Nonnull ImmutableList<DirtyState<SwitchCase>> cases) {
//    return super.reduceSwitchStatement(node, path, discriminant, cases).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceSwitchStatementWithDefault(
//      @Nonnull SwitchStatementWithDefault node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> discriminant,
//      @Nonnull ImmutableList<DirtyState<SwitchCase>> preDefaultCases,
//      @Nonnull DirtyState<SwitchDefault> defaultCase,
//      @Nonnull ImmutableList<DirtyState<SwitchCase>> postDefaultCases) {
//    return super.reduceSwitchStatementWithDefault(node, path, discriminant, preDefaultCases, defaultCase, postDefaultCases).bind(
//        this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceThrowStatement(
//      @Nonnull ThrowStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> expression) {
//    return super.reduceThrowStatement(node, path, expression).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceTryCatchStatement(
//      @Nonnull TryCatchStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Block> block,
//      @Nonnull DirtyState<CatchClause> catchClause) {
//    return super.reduceTryCatchStatement(node, path, block, catchClause).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceTryFinallyStatement(
//      @Nonnull TryFinallyStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Block> block,
//      @Nonnull Maybe<DirtyState<CatchClause>> catchClause,
//      @Nonnull DirtyState<Block> finalizer) {
//    return super.reduceTryFinallyStatement(node, path, block, catchClause, finalizer).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceVariableDeclarationStatement(
//      @Nonnull VariableDeclarationStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<VariableDeclaration> declaration) {
//    return super.reduceVariableDeclarationStatement(node, path, declaration).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<VariableDeclaration> reduceVariableDeclaration(
//      @Nonnull VariableDeclaration node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull NonEmptyImmutableList<DirtyState<VariableDeclarator>> declarators) {
//    return super.reduceVariableDeclaration(node, path, declarators).bind(this.bindVarDeclStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceWhileStatement(
//      @Nonnull WhileStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> test,
//      @Nonnull DirtyState<Statement> body) {
//    return super.reduceWhileStatement(node, path, test, body).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> reduceWithStatement(
//      @Nonnull WithStatement node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<Expression> object,
//      @Nonnull DirtyState<Statement> body) {
//    return super.reduceWithStatement(node, path, object, body).bind(this.bindStmt(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Block> reduceBlock(
//      @Nonnull Block node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<DirtyState<Statement>> statements) {
//    return super.reduceBlock(node, path, statements).bind(this.bindBlock(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> reduceDataProperty(
//      @Nonnull DataProperty node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<PropertyName> name,
//      @Nonnull DirtyState<Expression> value) {
//    return super.reduceDataProperty(node, path, name, value).bind(this.bindProp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> reduceGetter(
//      @Nonnull Getter node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<PropertyName> name,
//      @Nonnull DirtyState<FunctionBody> body) {
//    return super.reduceGetter(node, path, name, body).bind(this.bindProp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> reduceSetter(
//      @Nonnull Setter node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull DirtyState<PropertyName> name,
//      @Nonnull DirtyState<Identifier> parameter,
//      @Nonnull DirtyState<FunctionBody> body) {
//    return super.reduceSetter(node, path, name, parameter, body).bind(this.bindProp(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<PropertyName> reducePropertyName(@Nonnull PropertyName node, @Nonnull ImmutableList<Branch> path) {
//    return super.reducePropertyName(node, path).bind(this.bindPropName(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<FunctionBody> reduceFunctionBody(
//      @Nonnull FunctionBody node,
//      @Nonnull ImmutableList<Branch> path,
//      @Nonnull ImmutableList<DirtyState<Directive>> directives,
//      @Nonnull ImmutableList<DirtyState<Statement>> statements) {
//    return super.reduceFunctionBody(node, path, directives, statements).bind(this.bindProgramBody(path));
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<VariableDeclarator> reduceVariableDeclarator(
//      @Nonnull VariableDeclarator node,
//      @Nonnull final ImmutableList<Branch> path,
//      @Nonnull DirtyState<Identifier> binding,
//      @Nonnull Maybe<DirtyState<Expression>> init) {
//    return super.reduceVariableDeclarator(node, path, binding, init).bind(
//        node1 -> this.t.transform(node1).onDirty(
//            variableDeclarator -> Director.reduceVariableDeclarator(this, variableDeclarator, path).setDirty()));
//  }
//}
