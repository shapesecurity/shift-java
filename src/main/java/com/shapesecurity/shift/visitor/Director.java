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

import com.shapesecurity.functional.F2;
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

public final class Director<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
    ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState,
    SwitchDefaultState, CatchClauseState> {

  @NotNull
  private final ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
      ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
      SwitchCaseState, SwitchDefaultState, CatchClauseState>
      reducer;

  public Director(
      @NotNull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer) {
    this.reducer = reducer;
  }

  @NotNull
  private <T, U> F2<List<T>, List<Branch>, List<U>> l(@NotNull F2<T, List<Branch>, U> bf) {
    return (list, path) -> list.mapWithIndex(
        (i, el) -> bf.apply(el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  private <T, U> F2<NonEmptyList<T>, List<Branch>, NonEmptyList<U>> nel(@NotNull F2<T, List<Branch>, U> bf) {
    return (list, path) -> list.mapWithIndex(
        (i, el) -> bf.apply(el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  private <T, U> F2<Maybe<T>, List<Branch>, Maybe<U>> op(@NotNull F2<T, List<Branch>, U> f) {
    return (node, path) -> node.map(n -> f.apply(n, path.cons(StaticBranch.JUST)));
  }

  @NotNull
  private <A, B, X, Y> F2<Either<A, B>, List<Branch>, Either<X, Y>> e(
      @NotNull F2<A, List<Branch>, X> f1, @NotNull F2<B, List<Branch>, Y> f2) {
    return (e, p) -> e.map(n -> with(n, p.cons(StaticBranch.LEFT), f1), n -> with(n, p.cons(StaticBranch.RIGHT), f2));
  }

  @NotNull
  private <T, U> U with(T node, List<Branch> path, @NotNull F2<T, List<Branch>, U> f) {
    return f.apply(node, path);
  }

  // Equivalent to op(this::reduceExpression).apply, simply an optimization.
  @NotNull
  private Maybe<ExpressionState> reduceOptionExpression(@NotNull Maybe<Expression> node, @NotNull List<Branch> path) {
    return node.map(n -> reduceExpression(n, path.cons(StaticBranch.JUST)));
  }

  // Equivalent to op(this::reduceStatement).apply, simply an optimization.
  @NotNull
  private Maybe<StatementState> reduceOptionStatement(@NotNull Maybe<Statement> node, @NotNull List<Branch> path) {
    return node.map(n -> reduceStatement(n, path.cons(StaticBranch.JUST)));
  }

  @NotNull
  // Equivalent to l(this::reduceStatement).apply, simply an optimization.
  private List<StatementState> reduceListStatement(@NotNull List<Statement> list, @NotNull List<Branch> path) {
    return list.mapWithIndex((i, el) -> reduceStatement(el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  // Equivalent to l(this::reduceExpression).apply, simply an optimization.
  private List<ExpressionState> reduceListExpression(@NotNull List<Expression> list, @NotNull List<Branch> path) {
    return list.mapWithIndex((i, el) -> reduceExpression(el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  // Equivalent to l(this::reduceIdentifier).apply, simply an optimization.
  private List<IdentifierState> reduceListIdentifier(@NotNull List<Identifier> list, @NotNull List<Branch> path) {
    return list.mapWithIndex((i, el) -> reduceIdentifier(el, path.cons(IndexedBranch.from(i))));
  }

  @NotNull
  public ScriptState reduceScript(@NotNull Script node, @NotNull List<Branch> path) {
    return reducer.reduceScript(node, path, reduceFunctionBody(node.body, path.cons(StaticBranch.BODY)));
  }

  @NotNull
  public ProgramBodyState reduceFunctionBody(@NotNull FunctionBody node, @NotNull List<Branch> path) {
    return reducer.reduceFunctionBody(
        node,
        path,
        l(this::reduceDirective).apply(node.directives, path.cons(StaticBranch.DIRECTIVES)),
        reduceListStatement(node.statements, path.cons(StaticBranch.STATEMENTS)));
  }

  @NotNull
  public PropertyState reduceObjectProperty(@NotNull ObjectProperty node, @NotNull List<Branch> path) {
    PropertyNameState nameState = reducePropertyName(node.name, path.cons(StaticBranch.NAME));
    switch (node.type()) {
      case DataProperty: {
        DataProperty tNode = (DataProperty) node;
        return reducer.reduceDataProperty(
            tNode,
            path,
            nameState,
            reduceExpression(tNode.value, path.cons(StaticBranch.VALUE)));
      }
      case Getter: {
        Getter tNode = (Getter) node;
        return reducer.reduceGetter(
            tNode,
            path,
            nameState,
            reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case Setter: {
        Setter tNode = (Setter) node;
        return reducer.reduceSetter(
            tNode,
            path,
            nameState,
            reduceIdentifier(tNode.parameter, path.cons(StaticBranch.PARAMETER)),
            reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @NotNull
  public PropertyNameState reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path) {
    return reducer.reducePropertyName(node, path);
  }

  @NotNull
  public IdentifierState reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    return reducer.reduceIdentifier(node, path);
  }

  @NotNull
  public ExpressionState reduceExpression(@NotNull Expression node, @NotNull List<Branch> path) {
    switch (node.type()) {
      case FunctionExpression: {
        FunctionExpression tNode = (FunctionExpression) node;
        return reducer.reduceFunctionExpression(
            tNode,
            path,
            op(this::reduceIdentifier).apply(tNode.name, path.cons(StaticBranch.NAME)),
            reduceListIdentifier(tNode.parameters, path.cons(StaticBranch.PARAMETERS)),
            reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
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
            l(this::reduceOptionExpression).apply(tNode.elements, path.cons(StaticBranch.ELEMENTS)));
      }
      case AssignmentExpression: {
        AssignmentExpression tNode = (AssignmentExpression) node;
        return reducer.reduceAssignmentExpression(
            tNode,
            path,
            reduceExpression(tNode.binding, path.cons(StaticBranch.BINDING)),
            reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case BinaryExpression: {
        BinaryExpression tNode = (BinaryExpression) node;
        return reducer.reduceBinaryExpression(
            tNode,
            path,
            reduceExpression(tNode.left, path.cons(StaticBranch.LEFT)),
            reduceExpression(tNode.right, path.cons(StaticBranch.RIGHT)));
      }
      case CallExpression: {
        CallExpression tNode = (CallExpression) node;
        return reducer.reduceCallExpression(
            tNode,
            path,
            reduceExpression(tNode.callee, path.cons(StaticBranch.CALLEE)),
            reduceListExpression(tNode.arguments, path.cons(StaticBranch.ARGUMENTS)));
      }
      case ComputedMemberExpression: {
        ComputedMemberExpression tNode = (ComputedMemberExpression) node;
        return reducer.reduceComputedMemberExpression(
            tNode,
            path,
            reduceExpression(tNode.object, path.cons(StaticBranch.OBJECT)),
            reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case ConditionalExpression: {
        ConditionalExpression tNode = (ConditionalExpression) node;
        return reducer.reduceConditionalExpression(
            tNode,
            path,
            reduceExpression(tNode.test, path.cons(StaticBranch.TEST)),
            reduceExpression(tNode.consequent, path.cons(StaticBranch.CONSEQUENT)),
            reduceExpression(tNode.alternate, path.cons(StaticBranch.ALTERNATE)));
      }
      case IdentifierExpression: {
        IdentifierExpression tNode = (IdentifierExpression) node;
        return reducer.reduceIdentifierExpression(
            tNode,
            path,
            reduceIdentifier(tNode.identifier, path.cons(StaticBranch.IDENTIFIER)));
      }
      case NewExpression: {
        NewExpression tNode = (NewExpression) node;
        return reducer.reduceNewExpression(
            tNode,
            path,
            reduceExpression(tNode.callee, path.cons(StaticBranch.CALLEE)),
            reduceListExpression(tNode.arguments, path.cons(StaticBranch.ARGUMENTS)));
      }
      case PostfixExpression: {
        PostfixExpression tNode = (PostfixExpression) node;
        return reducer.reducePostfixExpression(
            tNode,
            path,
            reduceExpression(tNode.operand, path.cons(StaticBranch.OPERAND)));
      }
      case ObjectExpression: {
        ObjectExpression tNode = (ObjectExpression) node;
        return reducer.reduceObjectExpression(
            tNode,
            path,
            l(this::reduceObjectProperty).apply(tNode.properties, path.cons(StaticBranch.PROPERTIES)));
      }
      case PrefixExpression: {
        PrefixExpression tNode = (PrefixExpression) node;
        return reducer.reducePrefixExpression(
            tNode,
            path,
            reduceExpression(tNode.operand, path.cons(StaticBranch.OPERAND)));
      }
      case StaticMemberExpression: {
        StaticMemberExpression tNode = (StaticMemberExpression) node;
        return reducer.reduceStaticMemberExpression(
            tNode,
            path,
            reduceExpression(tNode.object, path.cons(StaticBranch.OBJECT)),
            reduceIdentifier(tNode.property, path.cons(StaticBranch.PROPERTY)));
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
  public DirectiveState reduceDirective(@NotNull Directive node, @NotNull List<Branch> path) {
    if (node instanceof UseStrictDirective) {
      return reducer.reduceUseStrictDirective(((UseStrictDirective) node), path);
    } else {
      return reducer.reduceUnknownDirective(((UnknownDirective) node), path);
    }
  }

  @NotNull
  public StatementState reduceStatement(@NotNull Statement node, @NotNull List<Branch> path) {
    switch (node.type()) {
      case FunctionDeclaration: {
        FunctionDeclaration tNode = (FunctionDeclaration) node;
        return reducer.reduceFunctionDeclaration(
            tNode, path,
            reduceIdentifier(tNode.name, path.cons(StaticBranch.NAME)),
            reduceListIdentifier(tNode.parameters, path.cons(StaticBranch.PARAMETERS)),
            reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case BlockStatement: {
        BlockStatement tNode = (BlockStatement) node;
        return reducer.reduceBlockStatement(
            tNode,
            path,
            reduceBlock(tNode.block, path.cons(StaticBranch.BLOCK)));
      }
      case BreakStatement: {
        BreakStatement tNode = (BreakStatement) node;
        return reducer.reduceBreakStatement(
            tNode,
            path,
            op(this::reduceIdentifier).apply(tNode.label, path.cons(StaticBranch.LABEL)));
      }
      case ContinueStatement: {
        ContinueStatement tNode = (ContinueStatement) node;
        return reducer.reduceContinueStatement(
            tNode, path,
            op(this::reduceIdentifier).apply(tNode.label, path.cons(StaticBranch.LABEL)));
      }
      case DebuggerStatement: {
        DebuggerStatement tNode = (DebuggerStatement) node;
        return reducer.reduceDebuggerStatement(tNode, path);
      }
      case DoWhileStatement: {
        DoWhileStatement tNode = (DoWhileStatement) node;
        return reducer.reduceDoWhileStatement(
            tNode, path,
            reduceStatement(tNode.body, path.cons(StaticBranch.BODY)),
            reduceExpression(tNode.test, path.cons(StaticBranch.TEST)));
      }
      case EmptyStatement: {
        EmptyStatement tNode = (EmptyStatement) node;
        return reducer.reduceEmptyStatement(tNode, path);
      }
      case ExpressionStatement: {
        ExpressionStatement tNode = (ExpressionStatement) node;
        return reducer.reduceExpressionStatement(
            tNode, path,
            reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case ForInStatement: {
        ForInStatement tNode = (ForInStatement) node;
        return reducer.reduceForInStatement(
            tNode, path,
            e(this::reduceVariableDeclaration, this::reduceExpression).apply(tNode.left, path.cons(StaticBranch.LEFT)),
            reduceExpression(tNode.right, path.cons(StaticBranch.RIGHT)),
            reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case ForStatement: {
        ForStatement tNode = (ForStatement) node;
        return reducer.reduceForStatement(
            tNode, path,
            op(e(this::reduceVariableDeclaration, this::reduceExpression))
                .apply(tNode.init, path.cons(StaticBranch.INIT)),
            reduceOptionExpression(tNode.test, path.cons(StaticBranch.TEST)),
            reduceOptionExpression(tNode.update, path.cons(StaticBranch.UPDATE)),
            reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case IfStatement: {
        IfStatement tNode = (IfStatement) node;
        return reducer.reduceIfStatement(
            tNode, path,
            reduceExpression(tNode.test, path.cons(StaticBranch.TEST)),
            reduceStatement(tNode.consequent, path.cons(StaticBranch.CONSEQUENT)),
            reduceOptionStatement(tNode.alternate, path.cons(StaticBranch.ALTERNATE)));
      }
      case LabeledStatement: {
        LabeledStatement tNode = (LabeledStatement) node;
        return reducer.reduceLabeledStatement(
            tNode, path,
            reduceIdentifier(tNode.label, path.cons(StaticBranch.LABEL)),
            reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case ReturnStatement: {
        ReturnStatement tNode = (ReturnStatement) node;
        return reducer.reduceReturnStatement(
            tNode, path,
            reduceOptionExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case SwitchStatement: {
        SwitchStatement tNode = (SwitchStatement) node;
        return reducer.reduceSwitchStatement(
            tNode, path,
            reduceExpression(tNode.discriminant, path.cons(StaticBranch.DISCRIMINANT)),
            l(this::reduceSwitchCase).apply(tNode.cases, path.cons(StaticBranch.CASES)));
      }
      case SwitchStatementWithDefault: {
        SwitchStatementWithDefault tNode = (SwitchStatementWithDefault) node;
        return reducer.reduceSwitchStatementWithDefault(
            tNode, path,
            reduceExpression(tNode.discriminant, path.cons(StaticBranch.DISCRIMINANT)),
            l(this::reduceSwitchCase).apply(tNode.preDefaultCases, path.cons(StaticBranch.PREDEFAULTCASES)),
            reduceSwitchDefault(tNode.defaultCase, path.cons(StaticBranch.DEFAULTCASE)),
            l(this::reduceSwitchCase).apply(tNode.postDefaultCases, path.cons(StaticBranch.POSTDEFAULTCASES)));
      }
      case ThrowStatement: {
        ThrowStatement tNode = (ThrowStatement) node;
        return reducer.reduceThrowStatement(
            tNode, path,
            reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case TryCatchStatement: {
        TryCatchStatement tNode = (TryCatchStatement) node;
        return reducer.reduceTryCatchStatement(
            tNode, path,
            reduceBlock(tNode.body, path.cons(StaticBranch.BODY)),
            reduceCatchClause(tNode.catchClause, path.cons(StaticBranch.CATCHCLAUSE)));
      }
      case TryFinallyStatement: {
        TryFinallyStatement tNode = (TryFinallyStatement) node;
        return reducer.reduceTryFinallyStatement(
            tNode, path,
            reduceBlock(tNode.body, path.cons(StaticBranch.BODY)),
            op(this::reduceCatchClause).apply(tNode.catchClause, path.cons(StaticBranch.CATCHCLAUSE)),
            reduceBlock(tNode.finalizer, path.cons(StaticBranch.FINALIZER)));
      }
      case VariableDeclarationStatement: {
        VariableDeclarationStatement tNode = (VariableDeclarationStatement) node;
        return reducer.reduceVariableDeclarationStatement(
            tNode, path,
            reduceVariableDeclaration(tNode.declaration, path.cons(StaticBranch.DECLARATION)));
      }
      case WhileStatement: {
        WhileStatement tNode = (WhileStatement) node;
        return reducer.reduceWhileStatement(
            tNode, path,
            reduceExpression(tNode.test, path.cons(StaticBranch.TEST)),
            reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case WithStatement: {
        WithStatement tNode = (WithStatement) node;
        return reducer.reduceWithStatement(
            tNode, path,
            reduceExpression(tNode.object, path.cons(StaticBranch.OBJECT)),
            reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @NotNull
  public BlockState reduceBlock(@NotNull Block node, @NotNull List<Branch> path) {
    return reducer.reduceBlock(
        node, path, reduceListStatement(node.statements, path.cons(StaticBranch.STATEMENTS)));
  }

  @NotNull
  public DeclaratorState reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull List<Branch> path) {
    return reducer.reduceVariableDeclarator(
        node,
        path,
        reduceIdentifier(node.binding, path.cons(StaticBranch.BINDING)),
        reduceOptionExpression(node.init, path.cons(StaticBranch.INIT)));
  }

  @NotNull
  public DeclarationState reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull List<Branch> path) {
    return reducer.reduceVariableDeclaration(
        node,
        path,
        nel(this::reduceVariableDeclarator).apply(node.declarators, path.cons(StaticBranch.DECLARATORS)));
  }

  @NotNull
  public SwitchCaseState reduceSwitchCase(@NotNull SwitchCase node, @NotNull List<Branch> path) {
    return reducer.reduceSwitchCase(
        node,
        path,
        reduceExpression(node.test, path.cons(StaticBranch.TEST)),
        reduceListStatement(node.consequent, path.cons(StaticBranch.CONSEQUENT)));
  }

  @NotNull
  public SwitchDefaultState reduceSwitchDefault(@NotNull SwitchDefault node, @NotNull List<Branch> path) {
    return reducer.reduceSwitchDefault(
        node,
        path,
        reduceListStatement(node.consequent, path.cons(StaticBranch.CONSEQUENT)));
  }

  @NotNull
  public CatchClauseState reduceCatchClause(@NotNull CatchClause node, @NotNull List<Branch> path) {
    return reducer.reduceCatchClause(
        node,
        path,
        reduceIdentifier(node.binding, path.cons(StaticBranch.BINDING)),
        reduceBlock(node.body, path.cons(StaticBranch.BODY)));
  }
}
