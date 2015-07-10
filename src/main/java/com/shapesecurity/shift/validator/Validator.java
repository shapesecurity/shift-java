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

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Director;
import com.shapesecurity.shift.visitor.MonoidalReducer;
import org.jetbrains.annotations.NotNull;

import java.lang.Class;

public class Validator extends MonoidalReducer<ValidationContext> {

  public Validator() {
    super(ValidationContext.MONOID);
  }

  public static ImmutableList<ValidationError> validate(Script script) {
    return ImmutableList.from(Director.reduceScript(new Validator(), script).errors);
  }

  @NotNull
  @Override
  public ValidationContext reduceFunctionBody(
    @NotNull FunctionBody node,
    @NotNull ImmutableList<ValidationContext> directives,
    @NotNull ImmutableList<ValidationContext> statements
  ) {
    ValidationContext s = super.reduceFunctionBody(node, directives, statements);
    s.clearFreeReturnStatements();
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceReturnStatement(
    @NotNull ReturnStatement node,
    @NotNull Maybe<ValidationContext> expression
  ) {
    ValidationContext s = super.reduceReturnStatement(node, expression);
    s.addFreeReturnStatement(node);
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceScript(
    @NotNull Script node,
    @NotNull ImmutableList<ValidationContext> directives,
    @NotNull ImmutableList<ValidationContext> statements
  ) {
    ValidationContext s = super.reduceScript(node, directives, statements);
    s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, "return statements must be within a function body"));
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceBindingIdentifier(@NotNull BindingIdentifier node) {
    ValidationContext s = super.reduceBindingIdentifier(node);
    if (!checkValidIdentifierName(node.name)) {
      s.addError(new ValidationError(node, "the name field of binding identifier must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull ValidationContext binding) {
    ValidationContext s = super.reduceImportSpecifier(node, binding);
    if (node.name.isJust() && !checkValidIdentifierName(node.name.just())) {
      s.addError(new ValidationError(node, "the name field of import specifier exists and must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceExportSpecifier(@NotNull ExportSpecifier node) {
    ValidationContext s = super.reduceExportSpecifier(node);
    if (node.name.isJust() && !checkValidIdentifierName(node.name.just())) {
      s.addError(new ValidationError(node, "the name field of export specifier exists and must be a valid identifier name"));
    }
    if (!checkValidIdentifierName(node.exportedName)) {
      s.addError(new ValidationError(node, "the exported name field of export specifier must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceSetter(@NotNull Setter node, @NotNull ValidationContext name, @NotNull ValidationContext parameter, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceSetter(node, name, parameter, body);
    if (node.param instanceof Binding) {
      if (node.param instanceof MemberExpression) {
        s.addError(new ValidationError(node, "the param field of setter must not be a member expression"));
      }
    } else if (node.param instanceof BindingWithDefault) {
      if (((BindingWithDefault) node.param).binding instanceof MemberExpression) {
        s.addError(new ValidationError(node, "the binding field of the param field of setter must not be a member expression"));
      }
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceShorthandProperty(@NotNull ShorthandProperty node) {
    ValidationContext s = super.reduceShorthandProperty(node);
    if (!checkValidIdentifierName(node.name)) {
      s.addError(new ValidationError(node, "the name field of shorthand property must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceIdentifierExpression(@NotNull IdentifierExpression node) {
    ValidationContext s = super.reduceIdentifierExpression(node);
    if (!checkValidIdentifierName(node.name)) {
      s.addError(new ValidationError(node, "the name field of identifier expression must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull ValidationContext object) {
    ValidationContext s = super.reduceStaticMemberExpression(node, object);
    if (!checkValidIdentifierName(node.property)) {
      s.addError(new ValidationError(node, "the property field of static member expression must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceBreakStatement(@NotNull BreakStatement node) {
    ValidationContext s = super.reduceBreakStatement(node);
    if (node.label.isJust() && !checkValidIdentifierName(node.label.just())) {
      s.addError(new ValidationError(node, "the label field of break statement exists and must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceContinueStatement(@NotNull ContinueStatement node) {
    ValidationContext s = super.reduceContinueStatement(node);
    if (node.label.isJust() && !checkValidIdentifierName(node.label.just())) {
      s.addError(new ValidationError(node, "the label field of continue statement exists and must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceLabeledStatement(node, body);
    if (!checkValidIdentifierName(node.label)) {
      s.addError(new ValidationError(node, "the label field of labeled statement must be a valid identifier name"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceCatchClause(@NotNull CatchClause node, @NotNull ValidationContext binding, @NotNull ValidationContext body) {
    ValidationContext s = super.reduceCatchClause(node, binding, body);
    if (node.binding instanceof MemberExpression) {
      s.addError(new ValidationError(node, "the binding field of CatchClause must not be a member expression"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<ValidationContext> items, @NotNull Maybe<ValidationContext> rest) {
    ValidationContext s = super.reduceFormalParameters(node, items, rest);
    node.items.map(x -> {
      if (x instanceof Binding) {
        if (x instanceof MemberExpression) {
          s.addError(new ValidationError(node, "the items field of formal parameters must not be member expressions"));
        }
      } else if (x instanceof BindingWithDefault) {
        if (((BindingWithDefault) x).binding instanceof MemberExpression) {
          s.addError(new ValidationError(node, "binding field of the items field of formal parameters must not be a member expression"));
        }
      }
      return x; // todo how to not return things here
    });
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<ValidationContext> declarators) {
    ValidationContext s = super.reduceVariableDeclaration(node, declarators);
    if (node.declarators.length == 0) {
      s.addError(new ValidationError(node, "the declarators field in variable declaration must not be an empty list"));
    }
    return s;
  }

  @NotNull
  @Override
  public ValidationContext reduceVariableDeclarator(@NotNull VariableDeclarator node, @NotNull ValidationContext binding, @NotNull Maybe<ValidationContext> init) {
    ValidationContext s = super.reduceVariableDeclarator(node, binding, init);
    if (node.binding instanceof MemberExpression) {
      s.addError(new ValidationError(node, "the binding field of variable declarator must not be a member expression"));
    }
    return s;
  }

  private static boolean checkValidIdentifierName(String name) {
    return name.length() > 0 && Utils.isIdentifierStart(name.charAt(0)) && name.chars().allMatch(Utils::isIdentifierPart);
  }
}
