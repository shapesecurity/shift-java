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
//import com.shapesecurity.shift.ast.Block;
//import com.shapesecurity.shift.ast.CatchClause;
//import com.shapesecurity.shift.ast.Directive;
//import com.shapesecurity.shift.ast.Expression;
//import com.shapesecurity.shift.ast.FunctionBody;
//import com.shapesecurity.shift.ast.Identifier;
//import com.shapesecurity.shift.ast.Script;
//import com.shapesecurity.shift.ast.Statement;
//import com.shapesecurity.shift.ast.SwitchCase;
//import com.shapesecurity.shift.ast.SwitchDefault;
//import com.shapesecurity.shift.ast.VariableDeclaration;
//import com.shapesecurity.shift.ast.VariableDeclarator;
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
//import org.jetbrains.annotations.NotNull;
//
//public interface TransformerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> {
//  @NotNull
//  default StatementState transform(@NotNull Statement node) {
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
//  @NotNull
//  default ExpressionState transform(@NotNull Expression node) {
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
//  @NotNull
//  default PropertyState transform(@NotNull ObjectProperty node) {
//    if (node instanceof DataProperty) {
//      return this.transform((DataProperty) node);
//    } else if (node instanceof Getter) {
//      return this.transform((Getter) node);
//    } else {
//      return this.transform((Setter) node);
//    }
//  }
//
//  @NotNull
//  default DirectiveState transform(@NotNull Directive node) {
//    if (node instanceof UnknownDirective) {
//      return this.transform((UnknownDirective) node);
//    }
//    return this.transform((UseStrictDirective) node);
//  }
//
//  @NotNull
//  CatchClauseState transform(@NotNull CatchClause node);
//
//  @NotNull
//  DeclaratorState transform(@NotNull VariableDeclarator node);
//
//  @NotNull
//  DirectiveState transform(@NotNull UnknownDirective node);
//
//  @NotNull
//  DirectiveState transform(@NotNull UseStrictDirective node);
//
//  @NotNull
//  ExpressionState transform(@NotNull ArrayExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull BinaryExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull AssignmentExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull CallExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull ComputedMemberExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull ConditionalExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull FunctionExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull IdentifierExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull LiteralBooleanExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull LiteralNullExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull LiteralInfinityExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull LiteralNumericExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull LiteralRegExpExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull LiteralStringExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull NewExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull ObjectExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull PostfixExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull PrefixExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull StaticMemberExpression node);
//
//  @NotNull
//  ExpressionState transform(@NotNull ThisExpression node);
//
//  @NotNull
//  IdentifierState transform(@NotNull Identifier node);
//
//  @NotNull
//  ProgramBodyState transform(@NotNull FunctionBody node);
//
//  @NotNull
//  ScriptState transform(@NotNull Script node);
//
//  @NotNull
//  PropertyNameState transform(@NotNull PropertyName node);
//
//  @NotNull
//  PropertyState transform(@NotNull Getter node);
//
//  @NotNull
//  PropertyState transform(@NotNull DataProperty node);
//
//  @NotNull
//  PropertyState transform(@NotNull Setter node);
//
//  @NotNull
//  StatementState transform(@NotNull BlockStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull BreakStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull ContinueStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull DebuggerStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull DoWhileStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull EmptyStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull ExpressionStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull ForInStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull ForStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull FunctionDeclaration node);
//
//  @NotNull
//  StatementState transform(@NotNull IfStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull LabeledStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull ReturnStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull SwitchStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull SwitchStatementWithDefault node);
//
//  @NotNull
//  StatementState transform(@NotNull ThrowStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull TryCatchStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull TryFinallyStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull VariableDeclarationStatement node);
//
//  @NotNull
//  BlockState transform(@NotNull Block node);
//
//  @NotNull
//  DeclarationState transform(@NotNull VariableDeclaration node);
//
//  @NotNull
//  StatementState transform(@NotNull WhileStatement node);
//
//  @NotNull
//  StatementState transform(@NotNull WithStatement node);
//
//  @NotNull
//  SwitchCaseState transform(@NotNull SwitchCase node);
//
//  @NotNull
//  SwitchDefaultState transform(@NotNull SwitchDefault node);
//}
