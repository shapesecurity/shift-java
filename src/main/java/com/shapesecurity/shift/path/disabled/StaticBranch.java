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
//package com.shapesecurity.shift.path.disabled;
//
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ArrayExpression_elements;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.AssignmentExpression_binding;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.AssignmentExpression_expression;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.BinaryExpression_left;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.BinaryExpression_right;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.BlockStatement_block;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Block_statements;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.BreakStatement_label;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.CallExpression_arguments;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.CallExpression_callee;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.CatchClause_binding;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.CatchClause_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ComputedMemberExpression_expression;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ComputedMemberExpression_object;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ConditionalExpression_alternate;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ConditionalExpression_consequent;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ConditionalExpression_test;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ContinueStatement_label;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.DataProperty_name;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.DataProperty_value;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.DoWhileStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.DoWhileStatement_test;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ExpressionStatement_expression;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForInStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForInStatement_left;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForInStatement_right;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForStatement_init;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForStatement_test;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ForStatement_update;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionBody_directives;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionBody_statements;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionDeclaration_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionDeclaration_name;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionDeclaration_parameters;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionExpression_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionExpression_name;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.FunctionExpression_parameters;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Getter_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Getter_name;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.IdentifierExpression_identifier;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.IfStatement_alternate;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.IfStatement_consequent;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.IfStatement_test;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.LabeledStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.LabeledStatement_label;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.NewExpression_arguments;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.NewExpression_callee;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ObjectExpression_properties;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.PostfixExpression_operand;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.PrefixExpression_operand;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ReturnStatement_expression;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Script_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Setter_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Setter_name;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.Setter_parameter;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.StaticMemberExpression_object;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.StaticMemberExpression_property;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchCase_consequent;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchCase_test;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchDefault_consequent;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchStatementWithDefault_defaultCase;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchStatementWithDefault_discriminant;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchStatementWithDefault_postDefaultCases;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchStatementWithDefault_preDefaultCases;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchStatement_cases;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.SwitchStatement_discriminant;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.ThrowStatement_expression;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.TryCatchStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.TryCatchStatement_catchClause;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.TryFinallyStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.TryFinallyStatement_catchClause;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.TryFinallyStatement_finalizer;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.VariableDeclarationStatement_declaration;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.VariableDeclaration_declarators;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.VariableDeclarator_binding;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.VariableDeclarator_init;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.WhileStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.WhileStatement_test;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.WithStatement_body;
//import static com.shapesecurity.shift.path.disabled.TypedBranches.WithStatement_object;
//
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.ast.EitherNode;
//import com.shapesecurity.shift.ast.MaybeNode;
//import com.shapesecurity.shift.ast.Node;
//import com.shapesecurity.shift.ast.types.MaybeType;
//
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//@SuppressWarnings("unchecked")
//public enum StaticBranch implements Branch {
//  ALTERNATE(IfStatement_alternate, ConditionalExpression_alternate),
//  ARGUMENTS(CallExpression_arguments, NewExpression_arguments),
//  BINDING(AssignmentExpression_binding, CatchClause_binding, VariableDeclarator_binding),
//  BLOCK(BlockStatement_block),
//  BODY(
//      Getter_body,
//      Setter_body,
//      FunctionDeclaration_body,
//      FunctionExpression_body,
//      DoWhileStatement_body,
//      ForInStatement_body,
//      ForStatement_body,
//      WhileStatement_body,
//      LabeledStatement_body,
//      TryCatchStatement_body,
//      TryFinallyStatement_body,
//      WithStatement_body,
//      CatchClause_body,
//      Script_body),
//  CALLEE(CallExpression_callee, NewExpression_callee),
//  CASES(SwitchStatement_cases),
//  CATCHCLAUSE(TryCatchStatement_catchClause, TryFinallyStatement_catchClause),
//  CONSEQUENT(ConditionalExpression_consequent, IfStatement_consequent, SwitchCase_consequent, SwitchDefault_consequent),
//  DECLARATION(VariableDeclarationStatement_declaration),
//  DECLARATORS(VariableDeclaration_declarators),
//  DEFAULTCASE(SwitchStatementWithDefault_defaultCase),
//  DIRECTIVES(FunctionBody_directives),
//  DISCRIMINANT(SwitchStatement_discriminant, SwitchStatementWithDefault_discriminant),
//  ELEMENTS(ArrayExpression_elements),
//  EXPRESSION(
//      AssignmentExpression_expression,
//      ComputedMemberExpression_expression,
//      ExpressionStatement_expression,
//      ReturnStatement_expression,
//      ThrowStatement_expression),
//  FINALIZER(TryFinallyStatement_finalizer),
//  IDENTIFIER(IdentifierExpression_identifier),
//  INIT(ForStatement_init, VariableDeclarator_init),
//  JUST() {
//    @Nullable
//    @Override
//    public Node view(@NotNull Node parent) {
//      if (parent instanceof MaybeNode) {
//        MaybeNode typed = ((MaybeNode) parent);
//        if (typed.maybe.isJust()) {
//          return wrap(typed.maybe.fromJust(), typed.genType.elementType);
//        }
//      }
//      return null;
//    }
//
//    @NotNull
//    @Override
//    public Node set(@NotNull Node parent, @NotNull Node child) {
//      if (parent instanceof MaybeNode) {
//        MaybeType type = (MaybeType) parent.genType();
//        if (((MaybeNode) parent).maybe.isJust() && type.isAssignableFrom(child.genType())) {
//          return new MaybeNode<>(Maybe.of(child), type);
//        }
//      }
//      return parent;
//    }
//  },
//  LABEL(BreakStatement_label, ContinueStatement_label, LabeledStatement_label),
//  LEFT(BinaryExpression_left, ForInStatement_left) {
//    @Nullable
//    @Override
//    public Node view(@NotNull Node parent) {
//      if (parent instanceof EitherNode) {
//        if (((EitherNode) parent).either.isLeft()) {
//          return wrap(((EitherNode) parent).either.left().fromJust(), ((EitherNode) parent).genType.leftType);
//        } else {
//          return null;
//        }
//      }
//      return super.view(parent);
//    }
//
//    @NotNull
//    @Override
//    public Node set(@NotNull Node parent, @NotNull Node child) {
//      if (parent instanceof EitherNode) {
//        if (((EitherNode) parent).either.isLeft() &&
//            ((EitherNode) parent).genType.leftType.isAssignableFrom(child.genType())) {
//          return new EitherNode<>(Either.left(child), ((EitherNode) parent).genType);
//        } else {
//          return parent;
//        }
//      }
//      return super.set(parent, child);
//    }
//  },
//  NAME(DataProperty_name, Getter_name, Setter_name, FunctionDeclaration_name, FunctionExpression_name),
//  OBJECT(StaticMemberExpression_object, ComputedMemberExpression_object, WithStatement_object),
//  OPERAND(PrefixExpression_operand, PostfixExpression_operand),
//  PARAMETER(Setter_parameter),
//  PARAMETERS(FunctionDeclaration_parameters, FunctionExpression_parameters),
//  POSTDEFAULTCASES(SwitchStatementWithDefault_postDefaultCases),
//  PREDEFAULTCASES(SwitchStatementWithDefault_preDefaultCases),
//  PROPERTIES(ObjectExpression_properties),
//  PROPERTY(StaticMemberExpression_property),
//  RIGHT(BinaryExpression_right, ForInStatement_right) {
//    @Nullable
//    @Override
//    public Node view(@NotNull Node parent) {
//      if (parent instanceof EitherNode) {
//        if (((EitherNode) parent).either.isRight()) {
//          return wrap(((EitherNode) parent).either.right().fromJust(), ((EitherNode) parent).genType.rightType);
//        } else {
//          return null;
//        }
//      }
//      return super.view(parent);
//    }
//
//    @NotNull
//    @Override
//    public Node set(@NotNull Node parent, @NotNull Node child) {
//      if (parent instanceof EitherNode) {
//        if (((EitherNode) parent).either.isRight() &&
//            ((EitherNode) parent).genType.rightType.isAssignableFrom(child.genType())) {
//          return new EitherNode<>(Either.right(child), ((EitherNode) parent).genType);
//        } else {
//          return parent;
//        }
//      }
//      return super.set(parent, child);
//    }
//  },
//  STATEMENTS(FunctionBody_statements, Block_statements),
//  TEST(
//      ConditionalExpression_test,
//      DoWhileStatement_test,
//      ForStatement_test,
//      IfStatement_test,
//      WhileStatement_test,
//      SwitchCase_test),
//  UPDATE(ForStatement_update),
//  VALUE(DataProperty_value);
//
//  @NotNull
//  private final TypedBranch[] typedBranches;
//
//  private StaticBranch(@NotNull TypedBranch... typedBranches) {
//    this.typedBranches = typedBranches;
//  }
//
//  @Nullable
//  public Node view(@NotNull Node parent) {
//    for (TypedBranch typedBranch : typedBranches) {
//      if (typedBranch.isValidParent(parent.genType())) {
//        return typedBranch.view(parent);
//      }
//    }
//    return null;
//  }
//
//  @NotNull
//  public Node set(@NotNull Node parent, @NotNull Node child) {
//    for (TypedBranch typedBranch : typedBranches) {
//      if (typedBranch.isValidParent(parent.genType()) && typedBranch.isValidChild(parent.genType(), child.genType())) {
//        return typedBranch.set(parent, child);
//      }
//    }
//    return parent;
//  }
//}
