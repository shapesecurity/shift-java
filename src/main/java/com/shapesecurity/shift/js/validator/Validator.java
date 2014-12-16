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

package com.shapesecurity.shift.js.validator;

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.Monoid;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
import com.shapesecurity.shift.js.ast.VariableDeclarator;
import com.shapesecurity.shift.js.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.js.ast.expression.FunctionExpression;
import com.shapesecurity.shift.js.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.js.ast.expression.ObjectExpression;
import com.shapesecurity.shift.js.ast.expression.PrefixExpression;
import com.shapesecurity.shift.js.ast.operators.PrefixOperator;
import com.shapesecurity.shift.js.ast.property.ObjectProperty;
import com.shapesecurity.shift.js.ast.property.Setter;
import com.shapesecurity.shift.js.ast.statement.BreakStatement;
import com.shapesecurity.shift.js.ast.statement.ContinueStatement;
import com.shapesecurity.shift.js.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.js.ast.statement.ForInStatement;
import com.shapesecurity.shift.js.ast.statement.ForStatement;
import com.shapesecurity.shift.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.js.ast.statement.LabeledStatement;
import com.shapesecurity.shift.js.ast.statement.ReturnStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatement;
import com.shapesecurity.shift.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.js.ast.statement.WhileStatement;
import com.shapesecurity.shift.js.ast.statement.WithStatement;
import com.shapesecurity.shift.js.path.Branch;
import com.shapesecurity.shift.js.utils.Utils;
import com.shapesecurity.shift.js.visitor.MonoidalReducer;

import java.util.HashSet;

import javax.annotation.Nonnull;

public class Validator extends MonoidalReducer<ValidationContext, Monoid<ValidationContext>> {
  public Validator() {
    super(ValidationContext.MONOID);
  }

  public static List<ValidationError> validate(Script node) {
    return node.reduce(new Validator(), List.<Branch>nil()).errors.toList();
  }

  @Nonnull
  @Override
  public ValidationContext reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<ValidationContext> label) {
    ValidationContext v = super.reduceBreakStatement(node, path, label);
    return node.label.maybe(v.addFreeBreakStatement(new ValidationError(node,
        "break must be nested within switch or iteration statement")), v::addFreeJumpTarget);
  }

  @Nonnull
  @Override
  public ValidationContext reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext binding,
      @Nonnull ValidationContext expression) {
    ValidationContext v = super.reduceAssignmentExpression(node, path, binding, expression);
    if (node.binding instanceof IdentifierExpression && Utils.isRestrictedWord(
        ((IdentifierExpression) node.binding).identifier.name)) {
      v = v.addStrictError(new ValidationError(node, "IdentifierExpression must not be a restricted word"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext param,
      @Nonnull ValidationContext body) {
    ValidationContext v = super.reduceCatchClause(node, path, param, body);
    if (Utils.isRestrictedWord(node.binding.name)) {
      v = v.addStrictError(new ValidationError(node, "CatchClause binding must not be restricted in strict mode"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<ValidationContext> label) {
    final ValidationContext v = super.reduceContinueStatement(node, path, label).addFreeContinueStatement(
        new ValidationError(node, "Continue statement must be inside a recursive loop"));
    return node.label.maybe(v, v::addFreeJumpTarget);
  }

  @Nonnull
  @Override
  public ValidationContext reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext body,
      @Nonnull ValidationContext test) {
    return super.reduceDoWhileStatement(node, path, body, test).clearFreeContinueStatements()
        .clearFreeBreakStatements();
  }

  @Nonnull
  @Override
  public ValidationContext reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext id,
      @Nonnull List<ValidationContext> params,
      @Nonnull ValidationContext programBody) {
    ValidationContext v = super.reduceFunctionDeclaration(node, path, id, params, programBody).clearUsedLabelNames()
        .clearReturnStatements();
    if (!Utils.areUniqueNames(node.parameters)) {
      v = v.addStrictError(new ValidationError(node, "FunctionDeclaration must have unique parameter names"));
    }

    v = node.parameters.foldLeft((v1, ident) -> {
      if (Utils.isRestrictedWord(ident.name)) {
        return v1.addStrictError(new ValidationError(ident,
            "FunctionExpression parameter name must not be restricted word"));
      }
      return v1;
    }, v);

    if (Utils.isRestrictedWord(node.name.name)) {
      v = v.addStrictError(new ValidationError(node,
          "FunctionDeclaration `name` must not be `eval` or `arguments` in strict mode"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<ValidationContext> id,
      @Nonnull List<ValidationContext> params,
      @Nonnull ValidationContext programBody) {
    ValidationContext v = super.reduceFunctionExpression(node, path, id, params, programBody).clearReturnStatements();
    if (!Utils.areUniqueNames(node.parameters)) {
      v = v.addStrictError(new ValidationError(node, "FunctionExpression parameter names must be unique"));
    }

    for (Identifier ident : node.parameters) {
      if (Utils.isRestrictedWord(ident.name)) {
        v = v.addStrictError(new ValidationError(ident,
            "FunctionExpression parameter name must not be restricted word"));
      }
    }

    if (node.name.maybe(false, ident -> Utils.isRestrictedWord(ident.name))) {
      v = v.addStrictError(new ValidationError(node,
          "FunctionExpression `name` must not be `eval` or `arguments` in strict mode"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    ValidationContext v = new ValidationContext();
    if (!Utils.isValidIdentifierName(node.name)) {
      v = v.addError(new ValidationError(node, "Identifier `name` must be a valid IdentifierName"));
    }
    if (Utils.isReservedWordES5(node.name)) {
      v = v.addError(new ValidationError(node, "Identifier `name` must not be a reserved word"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext identifier) {
    ValidationContext v = super.reduceIdentifierExpression(node, path, identifier);
    if (Utils.isReservedWordES5(node.identifier.name)) {
      v = v.addStrictError(new ValidationError(node, "Reserved word used in IdentifierExpression"));
    } else if (Utils.isStrictModeReservedWordES6(node.identifier.name)) {
      v = v.addStrictError(new ValidationError(node, "Strict mode reserved word used in IdentifierExpression"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<ValidationContext, ValidationContext> left,
      @Nonnull ValidationContext right,
      @Nonnull ValidationContext body) {
    ValidationContext v = super.reduceForInStatement(node, path, left, right, body).clearFreeBreakStatements()
        .clearFreeContinueStatements();
    if (node.left.isLeft() && !node.left.left().just().declarators.tail().isEmpty()) {
      v = v.addError(new ValidationError(node.left.left().just(),
          "VariableDeclarationStatement in ForInVarStatement contains more than one VariableDeclarator"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<ValidationContext, ValidationContext>> init,
      @Nonnull Maybe<ValidationContext> test,
      @Nonnull Maybe<ValidationContext> update,
      @Nonnull ValidationContext body) {
    return super.reduceForStatement(node, path, init, test, update, body).clearFreeBreakStatements()
        .clearFreeContinueStatements();
  }

  @Nonnull
  @Override
  public ValidationContext reduceLabeledStatement(
      @Nonnull LabeledStatement nodeP,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext label,
      @Nonnull ValidationContext body) {
    final LabeledStatement node = nodeP;
    ValidationContext v = super.reduceLabeledStatement(node, path, label, body);
    if (v.usedLabelNames.exists(s -> s.equals(node.label.name))) {
      v = v.addError(new ValidationError(node, "Duplicate label name."));
    }
    return v.observeLabelName(node.label);
  }

  @Nonnull
  @Override
  public ValidationContext reduceLiteralNumericExpression(
      @Nonnull LiteralNumericExpression node,
      @Nonnull List<Branch> path) {
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

  @Nonnull
  @Override
  public ValidationContext reduceObjectExpression(
      @Nonnull final ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<ValidationContext> properties) {
    ValidationContext v = super.reduceObjectExpression(node, path, properties);
    final HashSet<String> setKeys = new HashSet<>();
    final HashSet<String> getKeys = new HashSet<>();
    final HashSet<String> dataKeys = new HashSet<>();
    for (ObjectProperty p : node.properties) {
      String key = p.name.value;
      switch (p.getKind()) {
      case InitProperty:
        if (dataKeys.contains(key)) {
          v = v.addStrictError(new ValidationError(node,
              "ObjectExpression must not have more that one data property with the same name"));
        }
        if (getKeys.contains(key)) {
          v = v.addError(new ValidationError(node,
              "ObjectExpression must not have data and getter properties with same name"));
        }
        if (setKeys.contains(key)) {
          v = v.addError(new ValidationError(node,
              "ObjectExpression must not have data and setter properties with same name"));
        }
        dataKeys.add(key);
        break;
      case GetterProperty:
        if (getKeys.contains(key)) {
          v = v.addError(new ValidationError(node,
              "ObjectExpression must not have multiple getters with the same name"));
        }
        if (dataKeys.contains(key)) {
          v = v.addError(new ValidationError(node,
              "ObjectExpression must not have data and getter properties with the same name"));
        }
        getKeys.add(key);
        break;
      case SetterProperty:
        if (setKeys.contains(key)) {
          v = v.addError(new ValidationError(node,
              "ObjectExpression must not have multiple setters with the same name"));
        }
        if (dataKeys.contains(key)) {
          v = v.addError(new ValidationError(node,
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

  @Nonnull
  @Override
  public ValidationContext reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext operand) {
    ValidationContext v = super.reducePrefixExpression(node, path, operand);
    if (node.operator == PrefixOperator.Delete && node.operand instanceof IdentifierExpression) {
      return v.addStrictError(new ValidationError(node,
          "`delete` with unqualified identifier not allowed in strict mode"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceScript(
      @Nonnull Script node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext body) {
    return super.reduceScript(node, path, body).addErrors(body.freeReturnStatements);
  }

  @Nonnull
  @Override
  public ValidationContext reduceFunctionBody(
      @Nonnull FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<ValidationContext> directives,
      @Nonnull List<ValidationContext> sourceElements) {
    ValidationContext v = super.reduceFunctionBody(node, path, directives, sourceElements);
    if (v.freeJumpTargets.isNotEmpty()) {
      v = v.freeJumpTargets.foldLeft((v1, ident) -> v1.addError(new ValidationError(ident,
          "Unbound break/continue label")), v);
    }
    if (node.isStrict()) {
      v = v.addErrors(v.strictErrors);
    }
    return v.addErrors(v.freeBreakStatements).addErrors(v.freeContinueStatements);
  }

  @Nonnull
  @Override
  public ValidationContext reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<ValidationContext> argument) {
    return super.reduceReturnStatement(node, path, argument).addFreeReturnStatement(new ValidationError(node,
        "Return statement must be inside of a function"));
  }

  @Nonnull
  @Override
  public ValidationContext reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext key,
      @Nonnull ValidationContext param,
      @Nonnull ValidationContext body) {
    ValidationContext v = super.reduceSetter(node, path, key, param, body);
    if (Utils.isRestrictedWord(node.parameter.name)) {
      v = v.addStrictError(new ValidationError(node, "SetterProperty parameter must not be a restricted name"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext discriminant,
      @Nonnull List<ValidationContext> cases) {
    return super.reduceSwitchStatement(node, path, discriminant, cases).clearFreeBreakStatements();
  }

  @Nonnull
  @Override
  public ValidationContext reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext discriminant,
      @Nonnull List<ValidationContext> cases,
      @Nonnull ValidationContext defaultCase,
      @Nonnull List<ValidationContext> postDefaultCases) {
    return super.reduceSwitchStatementWithDefault(node, path, discriminant, cases, defaultCase, postDefaultCases)
        .clearFreeBreakStatements();
  }

  @Nonnull
  @Override
  public ValidationContext reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext id,
      @Nonnull Maybe<ValidationContext> init) {
    ValidationContext v = super.reduceVariableDeclarator(node, path, id, init);
    if (Utils.isRestrictedWord(node.binding.name)) {
      v = v.addStrictError(new ValidationError(node, "VariableDeclarator must not be restricted name"));
    }
    return v;
  }

  @Nonnull
  @Override
  public ValidationContext reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext test,
      @Nonnull ValidationContext body) {
    return super.reduceWhileStatement(node, path, test, body).clearFreeBreakStatements().clearFreeContinueStatements();
  }

  @Nonnull
  @Override
  public ValidationContext reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull ValidationContext object,
      @Nonnull ValidationContext body) {
    return super.reduceWithStatement(node, path, object, body).addStrictError(new ValidationError(node,
        "WithStatement not allowed in strict mode"));
  }
}
