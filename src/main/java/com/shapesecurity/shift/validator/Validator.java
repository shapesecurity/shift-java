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

package com.shapesecurity.shift.validator;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.MonoidalReducer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class Validator extends MonoidalReducer<ValidationContext> {

  public Validator() {
    super(ValidationContext.MONOID);
  }

  public static ImmutableList<ValidationError> validate(Script node) {
    return node.reduce(new Validator()).errors.toList();
  }

  @NotNull
  @Override
  public ValidationContext reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull ValidationContext binding,
      @NotNull ValidationContext expression) {
    ValidationContext v = super.reduceAssignmentExpression(node, binding, expression);
    if (node.binding instanceof IdentifierExpression) {
      v = v.checkRestricted((IdentifierExpression) node.binding);
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceBreakStatement(
      @NotNull BreakStatement node) {
    ValidationContext v = super.reduceBreakStatement(node);
    return node.label.maybe(
        v.addFreeBreakStatement(
            new ValidationError(
                node,
                "break must be nested within switch or iteration statement")), v::addFreeJumpTarget);
  }

  @NotNull
  @Override
  public ValidationContext reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull ValidationContext binding,
      @NotNull ValidationContext body) {
    ValidationContext v = super.reduceCatchClause(node, binding, body);
    return v.checkRestricted((IdentifierExpression) node.binding);
  }

  @NotNull
  @Override
  public ValidationContext reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull Maybe<ValidationContext> label) {
    final ValidationContext v = super.reduceContinueStatement(node, label).addFreeContinueStatement(node);
    return node.label.maybe(v, v::addFreeJumpTarget);
  }

  @NotNull
  @Override
  public ValidationContext reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull ValidationContext body,
      @NotNull ValidationContext test) {
    return super.reduceDoWhileStatement(node, body, test).clearFreeContinueStatements()
        .clearFreeBreakStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull Either<ValidationContext, ValidationContext> left,
      @NotNull ValidationContext right,
      @NotNull ValidationContext body) {
    ValidationContext v = super.reduceForInStatement(node, left, right, body).clearFreeBreakStatements()
        .clearFreeContinueStatements();
    if (node.left.isLeft() && !node.left.left().just().declarators.tail().isEmpty()) {
      v = v.addError(
        new ValidationError(
          node.left.left().just(),
          "VariableDeclarationStatement in ForInVarStatement contains more than one VariableDeclarator"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceForStatement(
      @NotNull ForStatement node,
      @NotNull Maybe<Either<ValidationContext, ValidationContext>> init,
      @NotNull Maybe<ValidationContext> test,
      @NotNull Maybe<ValidationContext> update,
      @NotNull ValidationContext body) {
    return super.reduceForStatement(node, init, test, update, body).clearFreeBreakStatements()
        .clearFreeContinueStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull ImmutableList<ValidationContext> directives,
      @NotNull ImmutableList<ValidationContext> statements) {
    ValidationContext v = super.reduceFunctionBody(node, directives, statements).checkFreeJumpTargets();
    if (node.isStrict()) {
      v = v.invalidateStrictErrors();
    }
    return v.invalidateFreeContinueAndBreakErrors();
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull ValidationContext name,
      @NotNull ImmutableList<ValidationContext> params,
      @NotNull ValidationContext programBody) {
    ValidationContext v = super.reduceFunctionDeclaration(node, name, params, programBody).clearUsedLabelNames()
        .clearReturnStatements().clearUsedLabelNames();
    if (!Utils.areUniqueNames(node.parameters)) {
      v = v.addStrictError(new ValidationError(node, "FunctionDeclaration must have unique parameter names"));
    }
    v = node.parameters.foldLeft(ValidationContext::checkRestricted, v.checkRestricted(node.name));
    if (node.body.isStrict()) {
      v = v.invalidateStrictErrors();
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull Maybe<ValidationContext> name,
      @NotNull ImmutableList<ValidationContext> parameters,
      @NotNull ValidationContext programBody) {
    ValidationContext v = super.reduceFunctionExpression(node, name, parameters, programBody)
        .clearReturnStatements();
    if (!Utils.areUniqueNames(node.params)) {
      v = v.addStrictError(new ValidationError(node, "FunctionExpression parameter names must be unique"));
    }
    v = node.parameters.foldLeft(ValidationContext::checkRestricted, node.name.map(v::checkRestricted).orJust(v));
    if (node.body.isStrict()) {
      v = v.invalidateStrictErrors();
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceGetter(@NotNull Getter node,
                                        @NotNull ValidationContext name, @NotNull ValidationContext body) {
    return super.reduceGetter(node, name, body).clearReturnStatements();
  }

//  @NotNull
//  @Override
//  public ValidationContext reduceIdentifier(@NotNull Identifier node) {
//    ValidationContext v = new ValidationContext();
//    if (!Utils.isValidIdentifierName(node.name)) {
//      v = v.addError(new ValidationError(node, "Identifier `name` must be a valid IdentifierName"));
//    }
//    return v;
//  }

  @NotNull
  @Override
  public ValidationContext reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull ValidationContext identifier) {
    return super.reduceIdentifierExpression(node, identifier).checkReserved(node.identifier);
  }

  @NotNull
  @Override
  public ValidationContext reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull ValidationContext label,
      @NotNull ValidationContext body) {
    return super.reduceLabeledStatement(node, label, body).observeLabelName(node.label);
  }

  @NotNull
  @Override
  public ValidationContext reduceLiteralNumericExpression(
      @NotNull LiteralNumericExpression node) {
    ValidationContext v = new ValidationContext();
    if (node.value < 0 || node.value == 0 && 1 / node.value < 0) {
      v = v.addError(new ValidationError(node, "Numeric Literal node must be non-negative"));
    } else if (Double.isNaN(node.value)) {
      v = v.addError(new ValidationError(node, "Numeric Literal node must not be NaN"));
    } else if (Double.isInfinite(node.value)) {
      v = v.addError(new ValidationError(node, "Numeric Literal node must be finite"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceObjectExpression(
      @NotNull final ObjectExpression node,
      @NotNull ImmutableList<ValidationContext> properties) {
    ValidationContext v = super.reduceObjectExpression(node, properties);
    final HashSet<String> setKeys = new HashSet<>();
    final HashSet<String> getKeys = new HashSet<>();
    final HashSet<String> dataKeys = new HashSet<>();
    for (ObjectProperty p : node.properties) {
      String key = p.name.value;
      switch (p.getKind()) {
      case InitProperty:
        if (dataKeys.contains(key)) {
          v = v.addStrictError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have more that one data property with the same name"));
        }
        if (getKeys.contains(key)) {
          v = v.addError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have data and getter properties with same name"));
        }
        if (setKeys.contains(key)) {
          v = v.addError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have data and setter properties with same name"));
        }
        dataKeys.add(key);
        break;
      case GetterProperty:
        if (getKeys.contains(key)) {
          v = v.addError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have multiple getters with the same name"));
        }
        if (dataKeys.contains(key)) {
          v = v.addError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have data and getter properties with the same name"));
        }
        getKeys.add(key);
        break;
      case SetterProperty:
        if (setKeys.contains(key)) {
          v = v.addError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have multiple setters with the same name"));
        }
        if (dataKeys.contains(key)) {
          v = v.addError(
              new ValidationError(
                  node,
                  "ObjectExpression must not have data and setter properties with the same name"));
        }
        setKeys.add(key);
        break;
      default:
        break;
      }
    }
    return v;
  }

//  @NotNull
//  @Override
//  public ValidationContext reducePrefixExpression(
//      @NotNull PrefixExpression node,
//      @NotNull ValidationContext operand) {
//    ValidationContext v = super.reducePrefixExpression(node, path, operand);
//    if (node.operator == PrefixOperator.Delete && node.operand instanceof IdentifierExpression) {
//      return v.addStrictError(
//          new ValidationError(
//              node,
//              "`delete` with unqualified identifier not allowed in strict mode"));
//    }
//    return v;
//  }

  @NotNull
  @Override
  public ValidationContext reducePropertyName(@NotNull PropertyName node, @NotNull ImmutableList<Branch> path) {
    ValidationContext v = super.reducePropertyName(node, path);
    switch (node.kind) {
    case Identifier:
      if (!Utils.isValidIdentifierName(node.value)) {
        return v.addError(
            new ValidationError(node, "PropertyName of kind 'identifier' must be valid identifier name."));
      }
      break;
    case Number:
      if (!Utils.isValidNumber(node.value)) {
        return v.addError(new ValidationError(node, "PropertyName of kind 'number' must be a valid number literal."));
      }
      break;
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull Maybe<ValidationContext> expression) {
    return super.reduceReturnStatement(node, expression).addFreeReturnStatement(node);
  }

  @NotNull
  @Override
  public ValidationContext reduceScript(
      @NotNull Script node,
      @NotNull ValidationContext body) {
    return super.reduceScript(node, body).invalidateFreeReturnErrors();
  }

  @NotNull
  @Override
  public ValidationContext reduceSetter(
      @NotNull Setter node,
      @NotNull ValidationContext name,
      @NotNull ValidationContext param,
      @NotNull ValidationContext body) {
    return super.reduceSetter(node, name, param, body).checkRestricted(node.param).clearReturnStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull ValidationContext object,
      @NotNull ValidationContext property) {
    return super.reduceStaticMemberExpression(node, object, property.clearIdentifierNameError());
  }

  @NotNull
  @Override
  public ValidationContext reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull ValidationContext discriminant,
      @NotNull ImmutableList<ValidationContext> cases) {
    return super.reduceSwitchStatement(node, discriminant, cases).clearFreeBreakStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull ValidationContext discriminant,
      @NotNull ImmutableList<ValidationContext> preDefaultCases,
      @NotNull ValidationContext defaultCase,
      @NotNull ImmutableList<ValidationContext> postDefaultCases) {
    return super.reduceSwitchStatementWithDefault(
      node,
      discriminant,
      preDefaultCases,
      defaultCase,
      postDefaultCases)
        .clearFreeBreakStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull ValidationContext binding,
      @NotNull Maybe<ValidationContext> init) {
    return super.reduceVariableDeclarator(node, binding, init).checkRestricted(node.binding);
  }

  @NotNull
  @Override
  public ValidationContext reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull ValidationContext test,
      @NotNull ValidationContext body) {
    return super.reduceWhileStatement(node, test, body).clearFreeBreakStatements().clearFreeContinueStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull ValidationContext object,
      @NotNull ValidationContext body) {
    return super.reduceWithStatement(node, object, body).addStrictError(
        new ValidationError(node, "WithStatement not allowed in strict mode"));
  }
}
