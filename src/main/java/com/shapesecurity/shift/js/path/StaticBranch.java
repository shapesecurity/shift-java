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

package com.shapesecurity.shift.js.path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.shapesecurity.shift.js.path.TypedBranches.*;

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.js.ast.EitherNode;
import com.shapesecurity.shift.js.ast.MaybeNode;
import com.shapesecurity.shift.js.ast.Node;
import com.shapesecurity.shift.js.ast.types.MaybeType;

@SuppressWarnings("unchecked")
public enum StaticBranch implements Branch {
  ALTERNATE(IfStatement_alternate, ConditionalExpression_alternate),
  ARGUMENTS(CallExpression_arguments, NewExpression_arguments),
  BINDING(AssignmentExpression_binding, CatchClause_binding, VariableDeclarator_binding),
  BLOCK(BlockStatement_block),
  BODY(
      Getter_body,
      Setter_body,
      FunctionDeclaration_body,
      FunctionExpression_body,
      DoWhileStatement_body,
      ForInStatement_body,
      ForStatement_body,
      WhileStatement_body,
      LabeledStatement_body,
      TryCatchStatement_body,
      TryFinallyStatement_body,
      WithStatement_body,
      CatchClause_body,
      Script_body),
  CALLEE(CallExpression_callee, NewExpression_callee),
  CASES(SwitchStatement_cases),
  CATCHCLAUSE(TryCatchStatement_catchClause, TryFinallyStatement_catchClause),
  CONSEQUENT(ConditionalExpression_consequent, IfStatement_consequent, SwitchCase_consequent, SwitchDefault_consequent),
  DECLARATION(VariableDeclarationStatement_declaration),
  DECLARATORS(VariableDeclaration_declarators),
  DEFAULTCASE(SwitchStatementWithDefault_defaultCase),
  DIRECTIVES(FunctionBody_directives),
  DISCRIMINANT(SwitchStatement_discriminant, SwitchStatementWithDefault_discriminant),
  ELEMENTS(ArrayExpression_elements),
  EXPRESSION(
      AssignmentExpression_expression,
      ComputedMemberExpression_expression,
      ExpressionStatement_expression,
      ReturnStatement_expression,
      ThrowStatement_expression),
  FINALIZER(TryFinallyStatement_finalizer),
  IDENTIFIER(IdentifierExpression_identifier),
  INIT(ForStatement_init, VariableDeclarator_init),
  JUST() {
    @Nullable
    @Override
    public Node view(@Nonnull Node parent) {
      if (parent instanceof MaybeNode) {
        MaybeNode typed = ((MaybeNode) parent);
        if (typed.maybe.isJust()) {
          return Branch.wrap(typed.maybe.just(), typed.genType.elementType);
        }
      }
      return null;
    }

    @Nonnull
    @Override
    public Node set(@Nonnull Node parent, @Nonnull Node child) {
      if (parent instanceof MaybeNode) {
        MaybeType type = (MaybeType) parent.genType();
        if (((MaybeNode) parent).maybe.isJust() && type.isAssignableFrom(child.genType())) {
          return new MaybeNode<>(Maybe.just(child), type);
        }
      }
      return parent;
    }
  },
  LABEL(BreakStatement_label, ContinueStatement_label, LabeledStatement_label),
  /**
   * BinaryExpression,
   * ForInStatement,
   */
  LEFT(BinaryExpression_left, ForInStatement_left) {
    @Nullable
    @Override
    public Node view(@Nonnull Node parent) {
      if (parent instanceof EitherNode) {
        if (((EitherNode) parent).either.isLeft()) {
          return Branch.wrap(((EitherNode) parent).either.left().just(), ((EitherNode) parent).genType.leftType);
        } else {
          return null;
        }
      }
      return super.view(parent);
    }

    @Nonnull
    @Override
    public Node set(@Nonnull Node parent, @Nonnull Node child) {
      if (parent instanceof EitherNode) {
        if (((EitherNode) parent).either.isLeft() &&
            ((EitherNode) parent).genType.leftType.isAssignableFrom(child.genType())) {
          return new EitherNode<>(Either.left(child), ((EitherNode) parent).genType);
        } else {
          return parent;
        }
      }
      return super.set(parent, child);
    }
  },
  NAME(DataProperty_name, Getter_name, Setter_name, FunctionDeclaration_name, FunctionExpression_name),
  OBJECT(StaticMemberExpression_object, ComputedMemberExpression_object, WithStatement_object),
  OPERAND(PrefixExpression_operand, PostfixExpression_operand),
  PARAMETER(Setter_parameter),
  PARAMETERS(FunctionDeclaration_parameters, FunctionExpression_parameters),
  POSTDEFAULTCASES(SwitchStatementWithDefault_postDefaultCases),
  PREDEFAULTCASES(SwitchStatementWithDefault_preDefaultCases),
  PROPERTIES(ObjectExpression_properties),
  PROPERTY(StaticMemberExpression_property),
  /**
   * BinaryExpression,
   * ForInStatement,
   */
  RIGHT(BinaryExpression_right, ForInStatement_right) {
    @Nullable
    @Override
    public Node view(@Nonnull Node parent) {
      if (parent instanceof EitherNode) {
        if (((EitherNode) parent).either.isRight()) {
          return Branch.wrap(((EitherNode) parent).either.right().just(), ((EitherNode) parent).genType.rightType);
        } else {
          return null;
        }
      }
      return super.view(parent);
    }

    @Nonnull
    @Override
    public Node set(@Nonnull Node parent, @Nonnull Node child) {
      if (parent instanceof EitherNode) {
        if (((EitherNode) parent).either.isRight() &&
            ((EitherNode) parent).genType.rightType.isAssignableFrom(child.genType())) {
          return new EitherNode<>(Either.right(child), ((EitherNode) parent).genType);
        } else {
          return parent;
        }
      }
      return super.set(parent, child);
    }
  },
  STATEMENTS(FunctionBody_statements, Block_statements),
  TEST(
      ConditionalExpression_test,
      DoWhileStatement_test,
      ForStatement_test,
      IfStatement_test,
      WhileStatement_test,
      SwitchCase_test),
  UPDATE(ForStatement_update),
  VALUE(DataProperty_value);

  @Nonnull
  private final TypedBranch[] typedBranches;

  private StaticBranch(@Nonnull TypedBranch... typedBranches) {
    this.typedBranches = typedBranches;
  }

  @Nullable
  public Node view(@Nonnull Node parent) {
    for (TypedBranch typedBranch : typedBranches) {
      if (typedBranch.isValidParent(parent.genType())) {
        return typedBranch.view(parent);
      }
    }
    return null;
  }

  @Nonnull
  public Node set(@Nonnull Node parent, @Nonnull Node child) {
    for (TypedBranch typedBranch : typedBranches) {
      if (typedBranch.isValidParent(parent.genType()) && typedBranch.isValidChild(parent.genType(), child.genType())) {
        return typedBranch.set(parent, child);
      }
    }
    return parent;
  }
}
