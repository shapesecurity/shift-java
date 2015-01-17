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


package com.shapesecurity.shift.ast.types;

import org.jetbrains.annotations.NotNull;

public enum Type implements GenType {
  Node {
    @Override
    public boolean isAssignableFrom(@NotNull GenType type) {
      return true;
    }
  },

  FunctionBody,
  Getter,
  Setter,
  DataProperty,
  PropertyName,
  FunctionExpression,
  ObjectExpression,
  LiteralBooleanExpression,
  LiteralNullExpression,
  LiteralInfinityExpression,
  LiteralNumericExpression,
  LiteralRegExpExpression,
  LiteralStringExpression,
  ArrayExpression,
  AssignmentExpression,
  BinaryExpression,
  CallExpression,
  ComputedMemberExpression,
  ConditionalExpression,
  IdentifierExpression,
  NewExpression,
  PostfixExpression,
  PrefixExpression,
  StaticMemberExpression,
  ThisExpression,
  FunctionDeclaration,
  BlockStatement,
  BreakStatement,
  ContinueStatement,
  DebuggerStatement,
  DoWhileStatement,
  EmptyStatement,
  ExpressionStatement,
  ForInStatement,
  ForStatement,
  IfStatement,
  LabeledStatement,
  ReturnStatement,
  SwitchStatement,
  SwitchStatementWithDefault,
  ThrowStatement,
  TryCatchStatement,
  TryFinallyStatement,
  VariableDeclarationStatement,
  WhileStatement,
  WithStatement,
  UnknownDirective,
  UseStrictDirective,
  Block,
  CatchClause,
  Identifier,
  Script,
  SwitchCase,
  SwitchDefault,
  VariableDeclaration,
  VariableDeclarator,

  List,
  NonEmptyList,
  Maybe,
  Either,

  // Sink types
  Expression {
    @Override
    public boolean isAssignableFrom(@NotNull GenType type) {
      switch (type.rawType()) {
      case Expression:
      case FunctionExpression:
      case ObjectExpression:
      case LiteralBooleanExpression:
      case LiteralNullExpression:
      case LiteralInfinityExpression:
      case LiteralNumericExpression:
      case LiteralRegExpExpression:
      case LiteralStringExpression:
      case ArrayExpression:
      case AssignmentExpression:
      case BinaryExpression:
      case CallExpression:
      case ComputedMemberExpression:
      case ConditionalExpression:
      case IdentifierExpression:
      case NewExpression:
      case PostfixExpression:
      case PrefixExpression:
      case StaticMemberExpression:
      case ThisExpression:
        return true;
      }
      return false;
    }
  },
  Statement {
    @Override
    public boolean isAssignableFrom(@NotNull GenType type) {
      switch (type.rawType()) {
      case Statement:
      case FunctionDeclaration:
      case BlockStatement:
      case BreakStatement:
      case ContinueStatement:
      case DebuggerStatement:
      case DoWhileStatement:
      case EmptyStatement:
      case ExpressionStatement:
      case ForInStatement:
      case ForStatement:
      case IfStatement:
      case LabeledStatement:
      case ReturnStatement:
      case SwitchStatement:
      case SwitchStatementWithDefault:
      case ThrowStatement:
      case TryCatchStatement:
      case TryFinallyStatement:
      case VariableDeclarationStatement:
      case WhileStatement:
      case WithStatement:
        return true;
      }
      return false;
    }
  },
  Directive {
    @Override
    public boolean isAssignableFrom(@NotNull GenType type) {
      switch (type.rawType()) {
      case Directive:
      case UnknownDirective:
      case UseStrictDirective:
        return true;
      }
      return false;
    }
  },
  AccessorProperty {
    @Override
    public boolean isAssignableFrom(@NotNull GenType type) {
      switch (type.rawType()) {
      case AccessorProperty:
      case Getter:
      case Setter:
        return true;
      }
      return false;
    }
  },
  ObjectProperty {
    @Override
    public boolean isAssignableFrom(@NotNull GenType type) {
      switch (type.rawType()) {
      case ObjectProperty:
      case AccessorProperty:
      case DataProperty:
      case Getter:
      case Setter:
        return true;
      }
      return false;
    }
  };

  @NotNull
  public Type rawType() {
    return this;
  }

  @Override
  public boolean isAssignableFrom(@NotNull GenType type) {
    return type == this;
  }
}
