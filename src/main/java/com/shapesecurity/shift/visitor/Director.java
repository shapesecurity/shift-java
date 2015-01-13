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
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
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
import com.shapesecurity.shift.path.IndexedBranch;
import com.shapesecurity.shift.path.StaticBranch;

import org.jetbrains.annotations.NotNull;

public final class Director {

  private Director() {
    // static only
  }

  @NotNull
  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  Maybe<ExpressionState> reduceOptionExpression(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Maybe<Expression> node,
      @NotNull List<Branch> path) {
    NonEmptyList<Branch> just_path = path.cons(StaticBranch.JUST);
    return node.map(n -> reduceExpression(reducer, n, just_path));
  }

  @NotNull
  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  Maybe<StatementState> reduceOptionStatement(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Maybe<Statement> node,
      @NotNull List<Branch> path) {
    NonEmptyList<Branch> just_path = path.cons(StaticBranch.JUST);
    return node.map(n -> reduceStatement(reducer, n, just_path));
  }

  @NotNull
  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  List<StatementState> reduceListStatement(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull List<Statement> list,
      @NotNull List<Branch> path) {
    return list.mapWithIndex((i, el) -> reduceStatement(reducer, el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  List<ExpressionState> reduceListExpression(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull List<Expression> list,
      @NotNull List<Branch> path) {
    return list.mapWithIndex((i, el) -> reduceExpression(reducer, el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  List<IdentifierState> reduceListIdentifier(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull List<Identifier> list,
      @NotNull List<Branch> path) {
    return list.mapWithIndex((i, el) -> reduceIdentifier(reducer, el, path.cons(IndexedBranch.from(i))));
  }

  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  List<DirectiveState> reducerListDirective(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull List<Directive> directives,
      @NotNull List<Branch> path) {
    return directives.mapWithIndex((i, el) -> reduceDirective(reducer, el, path.cons(IndexedBranch.from(i))));
  }

  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  List<PropertyState> reducerListProperty(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull List<ObjectProperty> properties,
      @NotNull List<Branch> path) {
    return properties.mapWithIndex((i, el) -> reduceObjectProperty(reducer, el, path.cons(IndexedBranch.from(i))));
  }

  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  Either<DeclarationState, ExpressionState> reduceEitherVariableDeclarationExpression(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Either<VariableDeclaration, Expression> node,
      @NotNull NonEmptyList<Branch> path) {
    return node.map(
        n -> reduceVariableDeclaration(reducer, n, path.cons(StaticBranch.LEFT)),
        n -> reduceExpression(reducer, n, path.cons(StaticBranch.RIGHT)));
  }

  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  List<SwitchCaseState> reduceListSwitchCase(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull List<SwitchCase> node,
      @NotNull NonEmptyList<Branch> path) {
    return node.mapWithIndex((i, el) -> reduceSwitchCase(reducer, el, path.cons(IndexedBranch.from(i))));
  }

  private static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  Maybe<IdentifierState> reducerMaybeIdentifier(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Maybe<Identifier> node,
      @NotNull NonEmptyList<Branch> path) {
    return node.map(n -> reduceIdentifier(reducer, n, path.cons(StaticBranch.JUST)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  ScriptState reduceScript(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Script node,
      @NotNull List<Branch> path) {
    return reducer.reduceScript(node, path, reduceFunctionBody(reducer, node.body, path.cons(StaticBranch.BODY)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  ProgramBodyState reduceFunctionBody(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull FunctionBody node,
      @NotNull List<Branch> path) {
    return reducer.reduceFunctionBody(
        node,
        path,
        reducerListDirective(reducer, node.directives, path.cons(StaticBranch.DIRECTIVES)),
        reduceListStatement(reducer, node.statements, path.cons(StaticBranch.STATEMENTS)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  PropertyState reduceObjectProperty(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull ObjectProperty node,
      @NotNull List<Branch> path) {
    PropertyNameState nameState = reducePropertyName(reducer, node.name, path.cons(StaticBranch.NAME));
    switch (node.type()) {
      case DataProperty: {
        DataProperty tNode = (DataProperty) node;
        return reducer.reduceDataProperty(
            tNode,
            path,
            nameState,
            reduceExpression(reducer, tNode.value, path.cons(StaticBranch.VALUE)));
      }
      case Getter: {
        Getter tNode = (Getter) node;
        return reducer.reduceGetter(
            tNode,
            path,
            nameState,
            reduceFunctionBody(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case Setter: {
        Setter tNode = (Setter) node;
        return reducer.reduceSetter(
            tNode,
            path,
            nameState,
            reduceIdentifier(reducer, tNode.parameter, path.cons(StaticBranch.PARAMETER)),
            reduceFunctionBody(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  PropertyNameState reducePropertyName(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull PropertyName node,
      @NotNull List<Branch> path) {
    return reducer.reducePropertyName(node, path);
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  IdentifierState reduceIdentifier(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Identifier node,
      @NotNull List<Branch> path) {
    return reducer.reduceIdentifier(node, path);
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  ExpressionState reduceExpression(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Expression node,
      @NotNull List<Branch> path) {
    switch (node.type()) {
      case FunctionExpression: {
        FunctionExpression tNode = (FunctionExpression) node;
        return reducer.reduceFunctionExpression(
            tNode,
            path,
            reducerMaybeIdentifier(reducer, tNode.name, path.cons(StaticBranch.NAME)),
            reduceListIdentifier(reducer, tNode.parameters, path.cons(StaticBranch.PARAMETERS)),
            reduceFunctionBody(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case LiteralBooleanExpression: {
        LiteralBooleanExpression tNode = (LiteralBooleanExpression) node;
        return reducer.reduceLiteralBooleanExpression(
            tNode,
            path);
      }
      case LiteralNullExpression: {
        LiteralNullExpression tNode = (LiteralNullExpression) node;
        return reducer.reduceLiteralNullExpression(
            tNode,
            path);
      }
      case LiteralNumericExpression: {
        LiteralNumericExpression tNode = (LiteralNumericExpression) node;
        return reducer.reduceLiteralNumericExpression(
            tNode,
            path);
      }
      case LiteralRegExpExpression: {
        LiteralRegExpExpression tNode = (LiteralRegExpExpression) node;
        return reducer.reduceLiteralRegExpExpression(
            tNode,
            path);
      }
      case LiteralStringExpression: {
        LiteralStringExpression tNode = (LiteralStringExpression) node;
        return reducer.reduceLiteralStringExpression(
            tNode,
            path);
      }
      case ArrayExpression: {
        ArrayExpression tNode = (ArrayExpression) node;
        return reducer.reduceArrayExpression(
            tNode,
            path,
            tNode.elements.mapWithIndex((i, el) ->
                reduceOptionExpression(
                    reducer,
                    el,
                    path.cons(StaticBranch.ELEMENTS).cons(IndexedBranch.from(i)))));
      }
      case AssignmentExpression: {
        AssignmentExpression tNode = (AssignmentExpression) node;
        return reducer.reduceAssignmentExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.binding, path.cons(StaticBranch.BINDING)),
            reduceExpression(reducer, tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case BinaryExpression: {
        BinaryExpression tNode = (BinaryExpression) node;
        return reducer.reduceBinaryExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.left, path.cons(StaticBranch.LEFT)),
            reduceExpression(reducer, tNode.right, path.cons(StaticBranch.RIGHT)));
      }
      case CallExpression: {
        CallExpression tNode = (CallExpression) node;
        return reducer.reduceCallExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.callee, path.cons(StaticBranch.CALLEE)),
            reduceListExpression(reducer, tNode.arguments, path.cons(StaticBranch.ARGUMENTS)));
      }
      case ComputedMemberExpression: {
        ComputedMemberExpression tNode = (ComputedMemberExpression) node;
        return reducer.reduceComputedMemberExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.object, path.cons(StaticBranch.OBJECT)),
            reduceExpression(reducer, tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case ConditionalExpression: {
        ConditionalExpression tNode = (ConditionalExpression) node;
        return reducer.reduceConditionalExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.test, path.cons(StaticBranch.TEST)),
            reduceExpression(reducer, tNode.consequent, path.cons(StaticBranch.CONSEQUENT)),
            reduceExpression(reducer, tNode.alternate, path.cons(StaticBranch.ALTERNATE)));
      }
      case IdentifierExpression: {
        IdentifierExpression tNode = (IdentifierExpression) node;
        return reducer.reduceIdentifierExpression(
            tNode,
            path,
            reduceIdentifier(reducer, tNode.identifier, path.cons(StaticBranch.IDENTIFIER)));
      }
      case NewExpression: {
        NewExpression tNode = (NewExpression) node;
        return reducer.reduceNewExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.callee, path.cons(StaticBranch.CALLEE)),
            reduceListExpression(reducer, tNode.arguments, path.cons(StaticBranch.ARGUMENTS)));
      }
      case PostfixExpression: {
        PostfixExpression tNode = (PostfixExpression) node;
        return reducer.reducePostfixExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.operand, path.cons(StaticBranch.OPERAND)));
      }
      case ObjectExpression: {
        ObjectExpression tNode = (ObjectExpression) node;
        return reducer.reduceObjectExpression(
            tNode,
            path,
            reducerListProperty(reducer, tNode.properties, path.cons(StaticBranch.PROPERTIES)));
      }
      case PrefixExpression: {
        PrefixExpression tNode = (PrefixExpression) node;
        return reducer.reducePrefixExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.operand, path.cons(StaticBranch.OPERAND)));
      }
      case StaticMemberExpression: {
        StaticMemberExpression tNode = (StaticMemberExpression) node;
        return reducer.reduceStaticMemberExpression(
            tNode,
            path,
            reduceExpression(reducer, tNode.object, path.cons(StaticBranch.OBJECT)),
            reduceIdentifier(reducer, tNode.property, path.cons(StaticBranch.PROPERTY)));
      }
      case ThisExpression: {
        ThisExpression tNode = (ThisExpression) node;
        return reducer.reduceThisExpression(tNode, path);
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  DirectiveState reduceDirective(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Directive node,
      @NotNull List<Branch> path) {
    if (node instanceof UseStrictDirective) {
      return reducer.reduceUseStrictDirective(((UseStrictDirective) node), path);
    } else {
      return reducer.reduceUnknownDirective(((UnknownDirective) node), path);
    }
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  StatementState reduceStatement(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Statement node,
      @NotNull List<Branch> path) {
    switch (node.type()) {
      case FunctionDeclaration: {
        FunctionDeclaration tNode = (FunctionDeclaration) node;
        return reducer.reduceFunctionDeclaration(
            tNode,
            path,
            reduceIdentifier(reducer, tNode.name, path.cons(StaticBranch.NAME)),
            reduceListIdentifier(reducer, tNode.parameters, path.cons(StaticBranch.PARAMETERS)),
            reduceFunctionBody(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case BlockStatement: {
        BlockStatement tNode = (BlockStatement) node;
        return reducer.reduceBlockStatement(
            tNode,
            path,
            reduceBlock(reducer, tNode.block, path.cons(StaticBranch.BLOCK)));
      }
      case BreakStatement: {
        BreakStatement tNode = (BreakStatement) node;
        return reducer.reduceBreakStatement(
            tNode,
            path,
            tNode.label.map(n -> reduceIdentifier(reducer, n, path.cons(StaticBranch.LABEL).cons(StaticBranch.JUST))));
      }
      case ContinueStatement: {
        ContinueStatement tNode = (ContinueStatement) node;
        return reducer.reduceContinueStatement(
            tNode,
            path,
            tNode.label.map(n -> reduceIdentifier(reducer, n, path.cons(StaticBranch.LABEL).cons(StaticBranch.JUST))));
      }
      case DebuggerStatement: {
        DebuggerStatement tNode = (DebuggerStatement) node;
        return reducer.reduceDebuggerStatement(tNode, path);
      }
      case DoWhileStatement: {
        DoWhileStatement tNode = (DoWhileStatement) node;
        return reducer.reduceDoWhileStatement(
            tNode,
            path,
            reduceStatement(reducer, tNode.body, path.cons(StaticBranch.BODY)),
            reduceExpression(reducer, tNode.test, path.cons(StaticBranch.TEST)));
      }
      case EmptyStatement: {
        EmptyStatement tNode = (EmptyStatement) node;
        return reducer.reduceEmptyStatement(tNode, path);
      }
      case ExpressionStatement: {
        ExpressionStatement tNode = (ExpressionStatement) node;
        return reducer.reduceExpressionStatement(
            tNode,
            path,
            reduceExpression(reducer, tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case ForInStatement: {
        ForInStatement tNode = (ForInStatement) node;
        NonEmptyList<Branch> inner_path = path.cons(StaticBranch.LEFT);
        Either<VariableDeclaration, Expression> left_node = tNode.left;
        return reducer.reduceForInStatement(
            tNode,
            path,
            reduceEitherVariableDeclarationExpression(reducer, left_node, inner_path),
            reduceExpression(reducer, tNode.right, path.cons(StaticBranch.RIGHT)),
            reduceStatement(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case ForStatement: {
        ForStatement tNode = (ForStatement) node;
        NonEmptyList<Branch> inner_path = path.cons(StaticBranch.INIT).cons(StaticBranch.JUST);
        return reducer.reduceForStatement(
            tNode,
            path,
            tNode.init.map(init -> reduceEitherVariableDeclarationExpression(reducer, init, inner_path)),
            reduceOptionExpression(reducer, tNode.test, path.cons(StaticBranch.TEST)),
            reduceOptionExpression(reducer, tNode.update, path.cons(StaticBranch.UPDATE)),
            reduceStatement(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case IfStatement: {
        IfStatement tNode = (IfStatement) node;
        return reducer.reduceIfStatement(
            tNode,
            path,
            reduceExpression(reducer, tNode.test, path.cons(StaticBranch.TEST)),
            reduceStatement(reducer, tNode.consequent, path.cons(StaticBranch.CONSEQUENT)),
            reduceOptionStatement(reducer, tNode.alternate, path.cons(StaticBranch.ALTERNATE)));
      }
      case LabeledStatement: {
        LabeledStatement tNode = (LabeledStatement) node;
        return reducer.reduceLabeledStatement(
            tNode,
            path,
            reduceIdentifier(reducer, tNode.label, path.cons(StaticBranch.LABEL)),
            reduceStatement(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case ReturnStatement: {
        ReturnStatement tNode = (ReturnStatement) node;
        return reducer.reduceReturnStatement(
            tNode,
            path,
            reduceOptionExpression(reducer, tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case SwitchStatement: {
        SwitchStatement tNode = (SwitchStatement) node;
        return reducer.reduceSwitchStatement(
            tNode,
            path,
            reduceExpression(reducer, tNode.discriminant, path.cons(StaticBranch.DISCRIMINANT)),
            reduceListSwitchCase(reducer, tNode.cases, path.cons(StaticBranch.CASES)));
      }
      case SwitchStatementWithDefault: {
        SwitchStatementWithDefault tNode = (SwitchStatementWithDefault) node;
        return reducer.reduceSwitchStatementWithDefault(
            tNode,
            path,
            reduceExpression(reducer, tNode.discriminant, path.cons(StaticBranch.DISCRIMINANT)),
            reduceListSwitchCase(reducer, tNode.preDefaultCases, path.cons(StaticBranch.PREDEFAULTCASES)),
            reduceSwitchDefault(reducer, tNode.defaultCase, path.cons(StaticBranch.DEFAULTCASE)),
            reduceListSwitchCase(reducer, tNode.postDefaultCases, path.cons(StaticBranch.POSTDEFAULTCASES)));
      }
      case ThrowStatement: {
        ThrowStatement tNode = (ThrowStatement) node;
        return reducer.reduceThrowStatement(
            tNode,
            path,
            reduceExpression(reducer, tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case TryCatchStatement: {
        TryCatchStatement tNode = (TryCatchStatement) node;
        return reducer.reduceTryCatchStatement(
            tNode,
            path,
            reduceBlock(reducer, tNode.body, path.cons(StaticBranch.BODY)),
            reduceCatchClause(reducer, tNode.catchClause, path.cons(StaticBranch.CATCHCLAUSE)));
      }
      case TryFinallyStatement: {
        TryFinallyStatement tNode = (TryFinallyStatement) node;
        return reducer.reduceTryFinallyStatement(
            tNode,
            path,
            reduceBlock(reducer, tNode.body, path.cons(StaticBranch.BODY)),
            tNode.catchClause.map(n ->
                reduceCatchClause(
                    reducer,
                    n,
                    path.cons(StaticBranch.CATCHCLAUSE).cons(StaticBranch.JUST))),
            reduceBlock(reducer, tNode.finalizer, path.cons(StaticBranch.FINALIZER)));
      }
      case VariableDeclarationStatement: {
        VariableDeclarationStatement tNode = (VariableDeclarationStatement) node;
        return reducer.reduceVariableDeclarationStatement(
            tNode,
            path,
            reduceVariableDeclaration(reducer, tNode.declaration, path.cons(StaticBranch.DECLARATION)));
      }
      case WhileStatement: {
        WhileStatement tNode = (WhileStatement) node;
        return reducer.reduceWhileStatement(
            tNode,
            path,
            reduceExpression(reducer, tNode.test, path.cons(StaticBranch.TEST)),
            reduceStatement(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      case WithStatement: {
        WithStatement tNode = (WithStatement) node;
        return reducer.reduceWithStatement(
            tNode,
            path,
            reduceExpression(reducer, tNode.object, path.cons(StaticBranch.OBJECT)),
            reduceStatement(reducer, tNode.body, path.cons(StaticBranch.BODY)));
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  BlockState reduceBlock(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull Block node,
      @NotNull List<Branch> path) {
    return reducer.reduceBlock(
        node,
        path,
        reduceListStatement(reducer, node.statements, path.cons(StaticBranch.STATEMENTS)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  DeclaratorState reduceVariableDeclarator(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path) {
    return reducer.reduceVariableDeclarator(
        node,
        path,
        reduceIdentifier(reducer, node.binding, path.cons(StaticBranch.BINDING)),
        reduceOptionExpression(reducer, node.init, path.cons(StaticBranch.INIT)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  DeclarationState reduceVariableDeclaration(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull VariableDeclaration node,
      @NotNull List<Branch> path) {
    return reducer.reduceVariableDeclaration(
        node,
        path,
        node.declarators.mapWithIndex(
            (i, el) ->
                reduceVariableDeclarator(
                    reducer,
                    el,
                    path.cons(StaticBranch.DECLARATORS).cons(IndexedBranch.from(i)))));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  SwitchCaseState reduceSwitchCase(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull SwitchCase node,
      @NotNull List<Branch> path) {
    return reducer.reduceSwitchCase(
        node,
        path,
        reduceExpression(reducer, node.test, path.cons(StaticBranch.TEST)),
        reduceListStatement(reducer, node.consequent, path.cons(StaticBranch.CONSEQUENT)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  SwitchDefaultState reduceSwitchDefault(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull SwitchDefault node,
      @NotNull List<Branch> path) {
    return reducer.reduceSwitchDefault(
        node,
        path,
        reduceListStatement(reducer, node.consequent, path.cons(StaticBranch.CONSEQUENT)));
  }

  @NotNull
  public static <ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState>
  CatchClauseState reduceCatchClause(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState, ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer,
      @NotNull CatchClause node,
      @NotNull List<Branch> path) {
    return reducer.reduceCatchClause(
        node,
        path,
        reduceIdentifier(reducer, node.binding, path.cons(StaticBranch.BINDING)),
        reduceBlock(reducer, node.body, path.cons(StaticBranch.BODY)));
  }
}
