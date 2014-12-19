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


package com.shapesecurity.shift.js.visitor;

import javax.annotation.Nonnull;

import com.shapesecurity.shift.functional.F2;
import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.Directive;
import com.shapesecurity.shift.js.ast.Expression;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.Statement;
import com.shapesecurity.shift.js.ast.SwitchCase;
import com.shapesecurity.shift.js.ast.SwitchDefault;
import com.shapesecurity.shift.js.ast.VariableDeclaration;
import com.shapesecurity.shift.js.ast.VariableDeclarator;
import com.shapesecurity.shift.js.ast.directive.UnknownDirective;
import com.shapesecurity.shift.js.ast.directive.UseStrictDirective;
import com.shapesecurity.shift.js.ast.expression.ArrayExpression;
import com.shapesecurity.shift.js.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.js.ast.expression.BinaryExpression;
import com.shapesecurity.shift.js.ast.expression.CallExpression;
import com.shapesecurity.shift.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ConditionalExpression;
import com.shapesecurity.shift.js.ast.expression.FunctionExpression;
import com.shapesecurity.shift.js.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.shift.js.ast.expression.NewExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PostfixExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.shift.js.ast.expression.ThisExpression;
import com.shapesecurity.shift.js.ast.property.DataProperty;
import com.shapesecurity.shift.js.ast.property.Getter;
import com.shapesecurity.shift.js.ast.property.ObjectProperty;
import com.shapesecurity.shift.js.ast.property.PropertyName;
import com.shapesecurity.shift.js.ast.property.Setter;
import com.shapesecurity.shift.js.ast.statement.BlockStatement;
import com.shapesecurity.shift.js.ast.statement.BreakStatement;
import com.shapesecurity.shift.js.ast.statement.ContinueStatement;
import com.shapesecurity.shift.js.ast.statement.DebuggerStatement;
import com.shapesecurity.shift.js.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.js.ast.statement.EmptyStatement;
import com.shapesecurity.shift.js.ast.statement.ExpressionStatement;
import com.shapesecurity.shift.js.ast.statement.ForInStatement;
import com.shapesecurity.shift.js.ast.statement.ForStatement;
import com.shapesecurity.shift.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.js.ast.statement.IfStatement;
import com.shapesecurity.shift.js.ast.statement.LabeledStatement;
import com.shapesecurity.shift.js.ast.statement.ReturnStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.js.ast.statement.ThrowStatement;
import com.shapesecurity.shift.js.ast.statement.TryCatchStatement;
import com.shapesecurity.shift.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.shift.js.ast.statement.VariableDeclarationStatement;
import com.shapesecurity.shift.js.ast.statement.WhileStatement;
import com.shapesecurity.shift.js.ast.statement.WithStatement;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.path.IndexedBranch;
import com.shapesecurity.shift.js.path.StaticBranch;

public final class Director<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
    ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState, SwitchCaseState,
    SwitchDefaultState, CatchClauseState> {

  @Nonnull
  private final ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
      ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
      SwitchCaseState, SwitchDefaultState, CatchClauseState>
      reducer;

  public Director(
      @Nonnull ReducerP<ScriptState, ProgramBodyState, PropertyState, PropertyNameState, IdentifierState,
          ExpressionState, DirectiveState, StatementState, BlockState, DeclaratorState, DeclarationState,
          SwitchCaseState, SwitchDefaultState, CatchClauseState> reducer) {
    this.reducer = reducer;
  }

  @Nonnull
  private <T, U> F2<List<T>, List<Branch>, List<U>> l(@Nonnull F2<T, List<Branch>, U> bf) {
    return (list, path) -> list.mapWithIndex(
        (i, el) -> bf.apply(el, path.cons(IndexedBranch.from(i))));
  }

  @Nonnull
  private <T, U> F2<NonEmptyList<T>, List<Branch>, NonEmptyList<U>> nel(@Nonnull F2<T, List<Branch>, U> bf) {
    return (list, path) -> list.mapWithIndex(
        (i, el) -> bf.apply(el, path.cons(IndexedBranch.from(i))));
  }

  @Nonnull
  private <T, U> F2<Maybe<T>, List<Branch>, Maybe<U>> op(@Nonnull F2<T, List<Branch>, U> f) {
    return (node, path) -> node.map(n -> f.apply(n, path.cons(StaticBranch.JUST)));
  }

  @Nonnull
  private <A, B, X, Y> F2<Either<A, B>, List<Branch>, Either<X, Y>> e(
      @Nonnull F2<A, List<Branch>, X> f1, @Nonnull F2<B, List<Branch>, Y> f2) {
    return (e, p) -> e.map(n -> with(n, p.cons(StaticBranch.LEFT), f1), n -> with(n, p.cons(StaticBranch.RIGHT), f2));
  }

  @Nonnull
  private <T, U> U with(T node, List<Branch> path, @Nonnull F2<T, List<Branch>, U> f) {
    return f.apply(node, path);
  }

  @Nonnull
  private Maybe<ExpressionState> reduceOptionExpression(@Nonnull Maybe<Expression> node, @Nonnull List<Branch> path) {
    return node.map(n -> reduceExpression(n, path.cons(StaticBranch.JUST)));
  }

  @Nonnull
  private Maybe<StatementState> reduceOptionStatement(@Nonnull Maybe<Statement> node, @Nonnull List<Branch> path) {
    return node.map(n -> reduceStatement(n, path.cons(StaticBranch.JUST)));
  }

  @Nonnull
  private List<StatementState> reduceOptionStatement(@Nonnull List<Statement> list, @Nonnull List<Branch> path) {
    return list.mapWithIndex((i, el) -> this.reduceStatement(el, path.cons(IndexedBranch.from(i))));
  }

  @Nonnull
  public ScriptState reduceScript(@Nonnull Script node, @Nonnull List<Branch> path) {
    return reducer.reduceScript(node, path, this.reduceFunctionBody(node.body, path.cons(StaticBranch.BODY)));
  }

  @Nonnull
  public ProgramBodyState reduceFunctionBody(@Nonnull FunctionBody node, @Nonnull List<Branch> path) {
    return reducer.reduceFunctionBody(
        node,
        path,
        l(this::reduceDirective).apply(node.directives, path.cons(StaticBranch.DIRECTIVES)),
        this.reduceOptionStatement(node.statements, path.cons(StaticBranch.STATEMENTS)));
  }

  @Nonnull
  public PropertyState reduceObjectProperty(@Nonnull ObjectProperty node, @Nonnull List<Branch> path) {
    PropertyNameState nameState = this.reducePropertyName(node.name, path.cons(StaticBranch.NAME));
    switch (node.type()) {
      case DataProperty: {
        DataProperty tNode = (DataProperty) node;
        return reducer.reduceDataProperty(
            tNode,
            path,
            nameState,
            this.reduceExpression(tNode.value, path.cons(StaticBranch.VALUE)));
      }
      case Getter: {
        Getter tNode = (Getter) node;
        return reducer.reduceGetter(
            tNode,
            path,
            nameState,
            this.reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case Setter: {
        Setter tNode = (Setter) node;
        return reducer.reduceSetter(
            tNode,
            path,
            nameState,
            this.reduceIdentifier(tNode.parameter, path.cons(StaticBranch.PARAMETER)),
            this.reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @Nonnull
  public PropertyNameState reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    return reducer.reducePropertyName(node, path);
  }

  @Nonnull
  public IdentifierState reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return reducer.reduceIdentifier(node, path);
  }

  @Nonnull
  public ExpressionState reduceExpression(@Nonnull Expression node, @Nonnull List<Branch> path) {
    switch (node.type()) {
      case FunctionExpression: {
        FunctionExpression tNode = (FunctionExpression) node;
        return reducer.reduceFunctionExpression(
            tNode,
            path,
            op(this::reduceIdentifier).apply(tNode.name, path.cons(StaticBranch.NAME)),
            l(this::reduceIdentifier).apply(tNode.parameters, path.cons(StaticBranch.PARAMETERS)),
            this.reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
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
            this.reduceExpression(tNode.binding, path.cons(StaticBranch.BINDING)),
            this.reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case BinaryExpression: {
        BinaryExpression tNode = (BinaryExpression) node;
        return reducer.reduceBinaryExpression(
            tNode,
            path,
            this.reduceExpression(tNode.left, path.cons(StaticBranch.LEFT)),
            this.reduceExpression(tNode.right, path.cons(StaticBranch.RIGHT)));
      }
      case CallExpression: {
        CallExpression tNode = (CallExpression) node;
        return reducer.reduceCallExpression(
            tNode,
            path,
            this.reduceExpression(tNode.callee, path.cons(StaticBranch.CALLEE)),
            l(this::reduceExpression).apply(tNode.arguments, path.cons(StaticBranch.ARGUMENTS)));
      }
      case ComputedMemberExpression: {
        ComputedMemberExpression tNode = (ComputedMemberExpression) node;
        return reducer.reduceComputedMemberExpression(
            tNode,
            path,
            this.reduceExpression(tNode.object, path.cons(StaticBranch.OBJECT)),
            this.reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case ConditionalExpression: {
        ConditionalExpression tNode = (ConditionalExpression) node;
        return reducer.reduceConditionalExpression(
            tNode,
            path,
            this.reduceExpression(tNode.test, path.cons(StaticBranch.TEST)),
            this.reduceExpression(tNode.consequent, path.cons(StaticBranch.CONSEQUENT)),
            this.reduceExpression(tNode.alternate, path.cons(StaticBranch.ALTERNATE)));
      }
      case IdentifierExpression: {
        IdentifierExpression tNode = (IdentifierExpression) node;
        return reducer.reduceIdentifierExpression(
            tNode,
            path,
            this.reduceIdentifier(tNode.identifier, path.cons(StaticBranch.IDENTIFIER)));
      }
      case NewExpression: {
        NewExpression tNode = (NewExpression) node;
        return reducer.reduceNewExpression(
            tNode,
            path,
            this.reduceExpression(tNode.callee, path.cons(StaticBranch.CALLEE)),
            l(this::reduceExpression).apply(tNode.arguments, path.cons(StaticBranch.ARGUMENTS)));
      }
      case PostfixExpression: {
        PostfixExpression tNode = (PostfixExpression) node;
        return reducer.reducePostfixExpression(
            tNode,
            path,
            this.reduceExpression(tNode.operand, path.cons(StaticBranch.OPERAND)));
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
            this.reduceExpression(tNode.operand, path.cons(StaticBranch.OPERAND)));
      }
      case StaticMemberExpression: {
        StaticMemberExpression tNode = (StaticMemberExpression) node;
        return reducer.reduceStaticMemberExpression(
            tNode,
            path,
            this.reduceExpression(tNode.object, path.cons(StaticBranch.OBJECT)),
            this.reduceIdentifier(tNode.property, path.cons(StaticBranch.PROPERTY)));
      }
      case ThisExpression: {
        ThisExpression tNode = (ThisExpression) node;
        return reducer.reduceThisExpression(tNode, path);
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }


  @Nonnull
  public DirectiveState reduceDirective(@Nonnull Directive node, @Nonnull List<Branch> path) {
    if (node instanceof UseStrictDirective) {
      return reducer.reduceUseStrictDirective(((UseStrictDirective) node), path);
    } else {
      return reducer.reduceUnknownDirective(((UnknownDirective) node), path);
    }
  }

  @Nonnull
  public StatementState reduceStatement(@Nonnull Statement node, @Nonnull List<Branch> path) {
    switch (node.type()) {
      case FunctionDeclaration: {
        FunctionDeclaration tNode = (FunctionDeclaration) node;
        return reducer.reduceFunctionDeclaration(
            tNode, path,
            this.reduceIdentifier(tNode.name, path.cons(StaticBranch.NAME)),
            l(this::reduceIdentifier).apply(tNode.parameters, path.cons(StaticBranch.PARAMETERS)),
            this.reduceFunctionBody(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case BlockStatement: {
        BlockStatement tNode = (BlockStatement) node;
        return reducer.reduceBlockStatement(
            tNode,
            path,
            this.reduceBlock(tNode.block, path.cons(StaticBranch.BLOCK)));
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
            this.reduceStatement(tNode.body, path.cons(StaticBranch.BODY)),
            this.reduceExpression(tNode.test, path.cons(StaticBranch.TEST)));
      }
      case EmptyStatement: {
        EmptyStatement tNode = (EmptyStatement) node;
        return reducer.reduceEmptyStatement(tNode, path);
      }
      case ExpressionStatement: {
        ExpressionStatement tNode = (ExpressionStatement) node;
        return reducer.reduceExpressionStatement(
            tNode, path,
            this.reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case ForInStatement: {
        ForInStatement tNode = (ForInStatement) node;
        return reducer.reduceForInStatement(
            tNode, path,
            e(this::reduceVariableDeclaration, this::reduceExpression).apply(tNode.left, path.cons(StaticBranch.LEFT)),
            this.reduceExpression(tNode.right, path.cons(StaticBranch.RIGHT)),
            this.reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case ForStatement: {
        ForStatement tNode = (ForStatement) node;
        return reducer.reduceForStatement(
            tNode, path,
            op(e(this::reduceVariableDeclaration, this::reduceExpression))
                .apply(tNode.init, path.cons(StaticBranch.INIT)),
            this.reduceOptionExpression(tNode.test, path.cons(StaticBranch.TEST)),
            this.reduceOptionExpression(tNode.update, path.cons(StaticBranch.UPDATE)),
            this.reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case IfStatement: {
        IfStatement tNode = (IfStatement) node;
        return reducer.reduceIfStatement(
            tNode, path,
            this.reduceExpression(tNode.test, path.cons(StaticBranch.TEST)),
            this.reduceStatement(tNode.consequent, path.cons(StaticBranch.CONSEQUENT)),
            this.reduceOptionStatement(tNode.alternate, path.cons(StaticBranch.ALTERNATE)));
      }
      case LabeledStatement: {
        LabeledStatement tNode = (LabeledStatement) node;
        return reducer.reduceLabeledStatement(
            tNode, path,
            this.reduceIdentifier(tNode.label, path.cons(StaticBranch.LABEL)),
            this.reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case ReturnStatement: {
        ReturnStatement tNode = (ReturnStatement) node;
        return reducer.reduceReturnStatement(
            tNode, path,
            this.reduceOptionExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case SwitchStatement: {
        SwitchStatement tNode = (SwitchStatement) node;
        return reducer.reduceSwitchStatement(
            tNode, path,
            this.reduceExpression(tNode.discriminant, path.cons(StaticBranch.DISCRIMINANT)),
            l(this::reduceSwitchCase).apply(tNode.cases, path.cons(StaticBranch.CASES)));
      }
      case SwitchStatementWithDefault: {
        SwitchStatementWithDefault tNode = (SwitchStatementWithDefault) node;
        return reducer.reduceSwitchStatementWithDefault(
            tNode, path,
            this.reduceExpression(tNode.discriminant, path.cons(StaticBranch.DISCRIMINANT)),
            l(this::reduceSwitchCase).apply(tNode.preDefaultCases, path.cons(StaticBranch.PREDEFAULTCASES)),
            this.reduceSwitchDefault(tNode.defaultCase, path.cons(StaticBranch.DEFAULTCASE)),
            l(this::reduceSwitchCase).apply(tNode.postDefaultCases, path.cons(StaticBranch.POSTDEFAULTCASES)));
      }
      case ThrowStatement: {
        ThrowStatement tNode = (ThrowStatement) node;
        return reducer.reduceThrowStatement(
            tNode, path,
            this.reduceExpression(tNode.expression, path.cons(StaticBranch.EXPRESSION)));
      }
      case TryCatchStatement: {
        TryCatchStatement tNode = (TryCatchStatement) node;
        return reducer.reduceTryCatchStatement(
            tNode, path,
            this.reduceBlock(tNode.body, path.cons(StaticBranch.BODY)),
            this.reduceCatchClause(tNode.catchClause, path.cons(StaticBranch.CATCHCLAUSE)));
      }
      case TryFinallyStatement: {
        TryFinallyStatement tNode = (TryFinallyStatement) node;
        return reducer.reduceTryFinallyStatement(
            tNode, path,
            this.reduceBlock(tNode.body, path.cons(StaticBranch.BODY)),
            op(this::reduceCatchClause).apply(tNode.catchClause, path.cons(StaticBranch.CATCHCLAUSE)),
            this.reduceBlock(tNode.finalizer, path.cons(StaticBranch.FINALIZER)));
      }
      case VariableDeclarationStatement: {
        VariableDeclarationStatement tNode = (VariableDeclarationStatement) node;
        return reducer.reduceVariableDeclarationStatement(
            tNode, path,
            this.reduceVariableDeclaration(tNode.declaration, path.cons(StaticBranch.DECLARATION)));
      }
      case WhileStatement: {
        WhileStatement tNode = (WhileStatement) node;
        return reducer.reduceWhileStatement(
            tNode, path,
            this.reduceExpression(tNode.test, path.cons(StaticBranch.TEST)),
            this.reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      case WithStatement: {
        WithStatement tNode = (WithStatement) node;
        return reducer.reduceWithStatement(
            tNode, path,
            this.reduceExpression(tNode.object, path.cons(StaticBranch.OBJECT)),
            this.reduceStatement(tNode.body, path.cons(StaticBranch.BODY)));
      }
      default:
        throw new RuntimeException("Not reached");
    }
  }

  @Nonnull
  public BlockState reduceBlock(@Nonnull Block node, @Nonnull List<Branch> path) {
    return reducer.reduceBlock(
        node, path, this.reduceOptionStatement(node.statements, path.cons(StaticBranch.STATEMENTS)));
  }

  @Nonnull
  public DeclaratorState reduceVariableDeclarator(@Nonnull VariableDeclarator node, @Nonnull List<Branch> path) {
    return reducer.reduceVariableDeclarator(
        node,
        path,
        this.reduceIdentifier(node.binding, path.cons(StaticBranch.BINDING)),
        this.reduceOptionExpression(node.init, path.cons(StaticBranch.INIT)));
  }

  @Nonnull
  public DeclarationState reduceVariableDeclaration(@Nonnull VariableDeclaration node, @Nonnull List<Branch> path) {
    return reducer.reduceVariableDeclaration(
        node,
        path,
        nel(this::reduceVariableDeclarator).apply(node.declarators, path.cons(StaticBranch.DECLARATORS)));
  }

  @Nonnull
  public SwitchCaseState reduceSwitchCase(@Nonnull SwitchCase node, @Nonnull List<Branch> path) {
    return reducer.reduceSwitchCase(
        node,
        path,
        this.reduceExpression(node.test, path.cons(StaticBranch.TEST)),
        this.reduceOptionStatement(node.consequent, path.cons(StaticBranch.CONSEQUENT)));
  }

  @Nonnull
  public SwitchDefaultState reduceSwitchDefault(@Nonnull SwitchDefault node, @Nonnull List<Branch> path) {
    return reducer.reduceSwitchDefault(
        node,
        path,
        this.reduceOptionStatement(node.consequent, path.cons(StaticBranch.CONSEQUENT)));
  }

  @Nonnull
  public CatchClauseState reduceCatchClause(@Nonnull CatchClause node, @Nonnull List<Branch> path) {
    return reducer.reduceCatchClause(
        node,
        path,
        this.reduceIdentifier(node.binding, path.cons(StaticBranch.BINDING)),
        this.reduceBlock(node.body, path.cons(StaticBranch.BODY)));
  }
}
