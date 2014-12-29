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
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.VariableDeclarator;
import com.shapesecurity.shift.ast.expression.AssignmentExpression;
import com.shapesecurity.shift.ast.expression.FunctionExpression;
import com.shapesecurity.shift.ast.expression.IdentifierExpression;
import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
import com.shapesecurity.shift.ast.expression.ObjectExpression;
import com.shapesecurity.shift.ast.expression.PrefixExpression;
import com.shapesecurity.shift.ast.operators.PrefixOperator;
import com.shapesecurity.shift.ast.property.ObjectProperty;
import com.shapesecurity.shift.ast.property.Setter;
import com.shapesecurity.shift.ast.statement.BreakStatement;
import com.shapesecurity.shift.ast.statement.ContinueStatement;
import com.shapesecurity.shift.ast.statement.DoWhileStatement;
import com.shapesecurity.shift.ast.statement.ForInStatement;
import com.shapesecurity.shift.ast.statement.ForStatement;
import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
import com.shapesecurity.shift.ast.statement.LabeledStatement;
import com.shapesecurity.shift.ast.statement.ReturnStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatement;
import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.shift.ast.statement.WhileStatement;
import com.shapesecurity.shift.ast.statement.WithStatement;
import com.shapesecurity.shift.path.Branch;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.MonoidalReducer;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

public class Validator extends MonoidalReducer<ValidationContext, Monoid<ValidationContext>> {
  public Validator() {
    super(ValidationContext.MONOID);
  }

  public static List<ValidationError> validate(Script node) {
    return node.reduce(new Validator()).errors.toList();
  }

  @NotNull
  @Override
  public ValidationContext reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<ValidationContext> label) {
    ValidationContext v = super.reduceBreakStatement(node, path, label);
    return node.label.maybe(
        v.addFreeBreakStatement(
            new ValidationError(
                node,
                "break must be nested within switch or iteration statement")), v::addFreeJumpTarget);
  }

  @NotNull
  @Override
  public ValidationContext reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext binding,
      @NotNull ValidationContext expression) {
    ValidationContext v = super.reduceAssignmentExpression(node, path, binding, expression);
    if (node.binding instanceof IdentifierExpression && Utils.isRestrictedWord(
        ((IdentifierExpression) node.binding).identifier.name)) {
      v = v.addStrictError(new ValidationError(node, "IdentifierExpression must not be a restricted word"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext binding,
      @NotNull ValidationContext body) {
    ValidationContext v = super.reduceCatchClause(node, path, binding, body);
    if (Utils.isRestrictedWord(node.binding.name)) {
      v = v.addStrictError(new ValidationError(node, "CatchClause binding must not be restricted in strict mode"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<ValidationContext> label) {
    final ValidationContext v = super.reduceContinueStatement(node, path, label).addFreeContinueStatement(
        new ValidationError(node, "Continue statement must be inside a recursive loop"));
    return node.label.maybe(v, v::addFreeJumpTarget);
  }

  @NotNull
  @Override
  public ValidationContext reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext body,
      @NotNull ValidationContext test) {
    return super.reduceDoWhileStatement(node, path, body, test).clearFreeContinueStatements()
        .clearFreeBreakStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext name,
      @NotNull List<ValidationContext> params,
      @NotNull ValidationContext programBody) {
    ValidationContext v = super.reduceFunctionDeclaration(node, path, name, params, programBody).clearUsedLabelNames()
        .clearReturnStatements();
    if (!Utils.areUniqueNames(node.parameters)) {
      v = v.addStrictError(new ValidationError(node, "FunctionDeclaration must have unique parameter names"));
    }

    v = node.parameters.foldLeft(
        (v1, ident) -> {
          if (Utils.isRestrictedWord(ident.name)) {
            return v1.addStrictError(
                new ValidationError(
                    ident,
                    "FunctionExpression parameter name must not be restricted word"));
          }
          return v1;
        }, v);

    if (Utils.isRestrictedWord(node.name.name)) {
      v = v.addStrictError(
          new ValidationError(
              node,
              "FunctionDeclaration `name` must not be `eval` or `arguments` in strict mode"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<ValidationContext> name,
      @NotNull List<ValidationContext> parameters,
      @NotNull ValidationContext programBody) {
    ValidationContext v = super.reduceFunctionExpression(node, path, name, parameters, programBody)
        .clearReturnStatements();
    if (!Utils.areUniqueNames(node.parameters)) {
      v = v.addStrictError(new ValidationError(node, "FunctionExpression parameter names must be unique"));
    }

    for (Identifier ident : node.parameters) {
      if (Utils.isRestrictedWord(ident.name)) {
        v = v.addStrictError(
            new ValidationError(
                ident,
                "FunctionExpression parameter name must not be restricted word"));
      }
    }

    if (node.name.maybe(false, ident -> Utils.isRestrictedWord(ident.name))) {
      v = v.addStrictError(
          new ValidationError(
              node,
              "FunctionExpression `name` must not be `eval` or `arguments` in strict mode"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    ValidationContext v = new ValidationContext();
    if (!Utils.isValidIdentifierName(node.name)) {
      v = v.addError(new ValidationError(node, "Identifier `name` must be a valid IdentifierName"));
    }
    if (Utils.isReservedWordES5(node.name)) {
      v = v.addError(new ValidationError(node, "Identifier `name` must not be a reserved word"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext identifier) {
    ValidationContext v = super.reduceIdentifierExpression(node, path, identifier);
    if (Utils.isReservedWordES5(node.identifier.name)) {
      v = v.addStrictError(new ValidationError(node, "Reserved word used in IdentifierExpression"));
    } else if (Utils.isStrictModeReservedWordES6(node.identifier.name)) {
      v = v.addStrictError(new ValidationError(node, "Strict mode reserved word used in IdentifierExpression"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<ValidationContext, ValidationContext> left,
      @NotNull ValidationContext right,
      @NotNull ValidationContext body) {
    ValidationContext v = super.reduceForInStatement(node, path, left, right, body).clearFreeBreakStatements()
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
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<ValidationContext, ValidationContext>> init,
      @NotNull Maybe<ValidationContext> test,
      @NotNull Maybe<ValidationContext> update,
      @NotNull ValidationContext body) {
    return super.reduceForStatement(node, path, init, test, update, body).clearFreeBreakStatements()
        .clearFreeContinueStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceLabeledStatement(
      @NotNull LabeledStatement nodeP,
      @NotNull List<Branch> path,
      @NotNull ValidationContext label,
      @NotNull ValidationContext body) {
    final LabeledStatement node = nodeP;
    ValidationContext v = super.reduceLabeledStatement(node, path, label, body);
    if (v.usedLabelNames.exists(s -> s.equals(node.label.name))) {
      v = v.addError(new ValidationError(node, "Duplicate label name."));
    }
    return v.observeLabelName(node.label);
  }

  @NotNull
  @Override
  public ValidationContext reduceLiteralNumericExpression(
      @NotNull LiteralNumericExpression node,
      @NotNull List<Branch> path) {
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
      @NotNull List<Branch> path,
      @NotNull List<ValidationContext> properties) {
    ValidationContext v = super.reduceObjectExpression(node, path, properties);
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

  @NotNull
  @Override
  public ValidationContext reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext operand) {
    ValidationContext v = super.reducePrefixExpression(node, path, operand);
    if (node.operator == PrefixOperator.Delete && node.operand instanceof IdentifierExpression) {
      return v.addStrictError(
          new ValidationError(
              node,
              "`delete` with unqualified identifier not allowed in strict mode"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceScript(
      @NotNull Script node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext body) {
    return super.reduceScript(node, path, body).addErrors(body.freeReturnStatements);
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<ValidationContext> directives,
      @NotNull List<ValidationContext> statements) {
    ValidationContext v = super.reduceFunctionBody(node, path, directives, statements);
    if (v.freeJumpTargets.isNotEmpty()) {
      v = v.freeJumpTargets.foldLeft(
          (v1, ident) -> v1.addError(
              new ValidationError(
                  ident,
                  "Unbound break/continue label")), v);
    }
    if (node.isStrict()) {
      v = v.addErrors(v.strictErrors);
    }
    return v.addErrors(v.freeBreakStatements).addErrors(v.freeContinueStatements);
  }

  @NotNull
  @Override
  public ValidationContext reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<ValidationContext> expression) {
    return super.reduceReturnStatement(node, path, expression).addFreeReturnStatement(
        new ValidationError(
            node,
            "Return statement must be inside of a function"));
  }

  @NotNull
  @Override
  public ValidationContext reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext name,
      @NotNull ValidationContext param,
      @NotNull ValidationContext body) {
    ValidationContext v = super.reduceSetter(node, path, name, param, body);
    if (Utils.isRestrictedWord(node.parameter.name)) {
      v = v.addStrictError(new ValidationError(node, "SetterProperty parameter must not be a restricted name"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext discriminant,
      @NotNull List<ValidationContext> cases) {
    return super.reduceSwitchStatement(node, path, discriminant, cases).clearFreeBreakStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext discriminant,
      @NotNull List<ValidationContext> preDefaultCases,
      @NotNull ValidationContext defaultCase,
      @NotNull List<ValidationContext> postDefaultCases) {
    return super.reduceSwitchStatementWithDefault(node, path, discriminant, preDefaultCases, defaultCase, postDefaultCases)
        .clearFreeBreakStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext binding,
      @NotNull Maybe<ValidationContext> init) {
    ValidationContext v = super.reduceVariableDeclarator(node, path, binding, init);
    if (Utils.isRestrictedWord(node.binding.name)) {
      v = v.addStrictError(new ValidationError(node, "VariableDeclarator must not be restricted name"));
    }
    return v;
  }

  @NotNull
  @Override
  public ValidationContext reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext test,
      @NotNull ValidationContext body) {
    return super.reduceWhileStatement(node, path, test, body).clearFreeBreakStatements().clearFreeContinueStatements();
  }

  @NotNull
  @Override
  public ValidationContext reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull ValidationContext object,
      @NotNull ValidationContext body) {
    return super.reduceWithStatement(node, path, object, body).addStrictError(
        new ValidationError(
            node,
            "WithStatement not allowed in strict mode"));
  }
}
