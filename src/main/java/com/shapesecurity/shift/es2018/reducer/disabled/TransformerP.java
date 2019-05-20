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
//
//import javax.annotation.Nonnull;
//
//public interface TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> {
//  @Nonnull
//  default StatementState transform(@Nonnull Statement node) {
//    switch (node.type()) {
//    case BlockStatement:
//      return this.transform((BlockStatement) node);
//    case BreakStatement:
//      return this.transform((BreakStatement) node);
//    case ContinueStatement:
//      return this.transform((ContinueStatement) node);
//    case DebuggerStatement:
//      return this.transform((DebuggerStatement) node);
//    case DoWhileStatement:
//      return this.transform((DoWhileStatement) node);
//    case EmptyStatement:
//      return this.transform((EmptyStatement) node);
//    case ExpressionStatement:
//      return this.transform((ExpressionStatement) node);
//    case ForInStatement:
//      return this.transform((ForInStatement) node);
//    case ForStatement:
//      return this.transform((ForStatement) node);
//    case IfStatement:
//      return this.transform((IfStatement) node);
//    case LabeledStatement:
//      return this.transform((LabeledStatement) node);
//    case ReturnStatement:
//      return this.transform((ReturnStatement) node);
//    case SwitchStatement:
//      return this.transform((SwitchStatement) node);
//    case SwitchStatementWithDefault:
//      return this.transform((SwitchStatementWithDefault) node);
//    case ThrowStatement:
//      return this.transform((ThrowStatement) node);
//    case TryCatchStatement:
//      return this.transform((TryCatchStatement) node);
//    case TryFinallyStatement:
//      return this.transform((TryFinallyStatement) node);
//    case VariableDeclarationStatement:
//      return this.transform((VariableDeclarationStatement) node);
//    case WhileStatement:
//      return this.transform((WhileStatement) node);
//    case WithStatement:
//      return this.transform((WithStatement) node);
//    case FunctionDeclaration:
//      return this.transform((FunctionDeclaration) node);
//    default:
//      throw new RuntimeException("not reached");
//    }
//  }
//
//  @Nonnull
//  default ExpressionState transform(@Nonnull Expression node) {
//    switch (node.type()) {
//    case LiteralBooleanExpression:
//      return this.transform((LiteralBooleanExpression) node);
//    case LiteralNullExpression:
//      return this.transform((LiteralNullExpression) node);
//    case LiteralInfinityExpression:
//      return this.transform((LiteralInfinityExpression) node);
//    case LiteralNumericExpression:
//      return this.transform((LiteralNumericExpression) node);
//    case LiteralRegExpExpression:
//      return this.transform((LiteralRegExpExpression) node);
//    case LiteralStringExpression:
//      return this.transform((LiteralStringExpression) node);
//    case ArrayExpression:
//      return this.transform((ArrayExpression) node);
//    case ObjectExpression:
//      return this.transform((ObjectExpression) node);
//    case AssignmentExpression:
//      return this.transform((AssignmentExpression) node);
//    case BinaryExpression:
//      return this.transform((BinaryExpression) node);
//    case CallExpression:
//      return this.transform((CallExpression) node);
//    case ComputedMemberExpression:
//      return this.transform((ComputedMemberExpression) node);
//    case ConditionalExpression:
//      return this.transform((ConditionalExpression) node);
//    case IdentifierExpression:
//      return this.transform((IdentifierExpression) node);
//    case NewExpression:
//      return this.transform((NewExpression) node);
//    case PostfixExpression:
//      return this.transform((PostfixExpression) node);
//    case PrefixExpression:
//      return this.transform((PrefixExpression) node);
//    case StaticMemberExpression:
//      return this.transform((StaticMemberExpression) node);
//    case ThisExpression:
//      return this.transform((ThisExpression) node);
//    case FunctionExpression:
//      return this.transform((FunctionExpression) node);
//    default:
//      throw new RuntimeException("not reached");
//    }
//  }
//
//  @Nonnull
//  default PropertyState transform(@Nonnull ObjectProperty node) {
//    if (node instanceof DataProperty) {
//      return this.transform((DataProperty) node);
//    } else if (node instanceof Getter) {
//      return this.transform((Getter) node);
//    } else {
//      return this.transform((Setter) node);
//    }
//  }
//
//  @Nonnull
//  default DirectiveState transform(@Nonnull Directive node) {
//    if (node instanceof UnknownDirective) {
//      return this.transform((UnknownDirective) node);
//    }
//    return this.transform((UseStrictDirective) node);
//  }
//
//  @Nonnull
//  CatchClauseState transform(@Nonnull CatchClause node);
//
//  @Nonnull
//  DeclaratorState transform(@Nonnull VariableDeclarator node);
//
//  @Nonnull
//  DirectiveState transform(@Nonnull UnknownDirective node);
//
//  @Nonnull
//  DirectiveState transform(@Nonnull UseStrictDirective node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull ArrayExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull BinaryExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull AssignmentExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull CallExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull ComputedMemberExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull ConditionalExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull FunctionExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull IdentifierExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull LiteralBooleanExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull LiteralNullExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull LiteralInfinityExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull LiteralNumericExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull LiteralRegExpExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull LiteralStringExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull NewExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull ObjectExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull PostfixExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull PrefixExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull StaticMemberExpression node);
//
//  @Nonnull
//  ExpressionState transform(@Nonnull ThisExpression node);
//
//  @Nonnull
//  IdentifierState transform(@Nonnull Identifier node);
//
//  @Nonnull
//  ProgramBodyState transform(@Nonnull FunctionBody node);
//
//  @Nonnull
//  ScriptState transform(@Nonnull Script node);
//
//  @Nonnull
//  PropertyNameState transform(@Nonnull PropertyName node);
//
//  @Nonnull
//  PropertyState transform(@Nonnull Getter node);
//
//  @Nonnull
//  PropertyState transform(@Nonnull DataProperty node);
//
//  @Nonnull
//  PropertyState transform(@Nonnull Setter node);
//
//  @Nonnull
//  StatementState transform(@Nonnull BlockStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull BreakStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull ContinueStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull DebuggerStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull DoWhileStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull EmptyStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull ExpressionStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull ForInStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull ForStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull FunctionDeclaration node);
//
//  @Nonnull
//  StatementState transform(@Nonnull IfStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull LabeledStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull ReturnStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull SwitchStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull SwitchStatementWithDefault node);
//
//  @Nonnull
//  StatementState transform(@Nonnull ThrowStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull TryCatchStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull TryFinallyStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull VariableDeclarationStatement node);
//
//  @Nonnull
//  BlockState transform(@Nonnull Block node);
//
//  @Nonnull
//  DeclarationState transform(@Nonnull VariableDeclaration node);
//
//  @Nonnull
//  StatementState transform(@Nonnull WhileStatement node);
//
//  @Nonnull
//  StatementState transform(@Nonnull WithStatement node);
//
//  @Nonnull
//  SwitchCaseState transform(@Nonnull SwitchCase node);
//
//  @Nonnull
//  SwitchDefaultState transform(@Nonnull SwitchDefault node);
//}
