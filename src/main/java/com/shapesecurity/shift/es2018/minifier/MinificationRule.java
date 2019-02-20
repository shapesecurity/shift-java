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
//package com.shapesecurity.shift.minifier;
//
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
//import com.shapesecurity.shift.visitor.DirtyState;
//import com.shapesecurity.shift.visitor.TransformerP;
//
//import javax.annotation.Nonnull;
//
//public class MinificationRule
//    implements TransformerP<DirtyState<Script>, DirtyState<FunctionBody>, DirtyState<ObjectProperty>, DirtyState<PropertyName>, DirtyState<Identifier>, DirtyState<Expression>, DirtyState<Directive>, DirtyState<Statement>, DirtyState<Block>, DirtyState<VariableDeclarator>, DirtyState<VariableDeclaration>, DirtyState<SwitchCase>, DirtyState<SwitchDefault>, DirtyState<CatchClause>> {
//  @Nonnull
//  @Override
//  public DirtyState<CatchClause> transform(@Nonnull CatchClause node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<VariableDeclarator> transform(@Nonnull VariableDeclarator node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Directive> transform(@Nonnull UnknownDirective node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Directive> transform(@Nonnull UseStrictDirective node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ArrayExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull BinaryExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull AssignmentExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull CallExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ComputedMemberExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ConditionalExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull FunctionExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull IdentifierExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralBooleanExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralNullExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralInfinityExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralNumericExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralRegExpExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull LiteralStringExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull NewExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ObjectExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull PostfixExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull PrefixExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull StaticMemberExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Expression> transform(@Nonnull ThisExpression node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Identifier> transform(@Nonnull Identifier node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<FunctionBody> transform(@Nonnull FunctionBody node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Script> transform(@Nonnull Script node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<PropertyName> transform(@Nonnull PropertyName node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> transform(@Nonnull Getter node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> transform(@Nonnull DataProperty node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<ObjectProperty> transform(@Nonnull Setter node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull BlockStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull BreakStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ContinueStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull DebuggerStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull DoWhileStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull EmptyStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ExpressionStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ForInStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ForStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull FunctionDeclaration node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull IfStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull LabeledStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ReturnStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull SwitchStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull SwitchStatementWithDefault node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull ThrowStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull TryCatchStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull TryFinallyStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull VariableDeclarationStatement node) {
//    return DirtyState.clean((Statement) node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Block> transform(@Nonnull Block node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<VariableDeclaration> transform(@Nonnull VariableDeclaration node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull WhileStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<Statement> transform(@Nonnull WithStatement node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchCase> transform(@Nonnull SwitchCase node) {
//    return DirtyState.clean(node);
//  }
//
//  @Nonnull
//  @Override
//  public DirtyState<SwitchDefault> transform(@Nonnull SwitchDefault node) {
//    return DirtyState.clean(node);
//  }
//}
