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

package com.shapesecurity.shift.js.serialization;

import com.shapesecurity.shift.functional.data.Either;
import com.shapesecurity.shift.functional.data.List;
import com.shapesecurity.shift.functional.data.Maybe;
import com.shapesecurity.shift.functional.data.NonEmptyList;
import com.shapesecurity.shift.js.ast.Block;
import com.shapesecurity.shift.js.ast.CatchClause;
import com.shapesecurity.shift.js.ast.FunctionBody;
import com.shapesecurity.shift.js.ast.Identifier;
import com.shapesecurity.shift.js.ast.Script;
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
import com.shapesecurity.shift.js.visitor.Director;
import com.shapesecurity.shift.js.visitor.Reducer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;

public class Serializer implements Reducer<JsonElement> {
  public static final Serializer INSTANCE = new Serializer();

  protected Serializer() {
  }

  @Nonnull
  public static JsonObject serialize(@Nonnull Script script) {
    return (JsonObject) script.reduce(INSTANCE);
  }

  @Nonnull
  private static JsonArray list(@Nonnull List<JsonElement> values) {
    return values.foldLeft((arr, jsonElement) -> {
      arr.add(jsonElement);
      return arr;
    }, new JsonArray());
  }

  @Nonnull
  private static JsonArray olist(@Nonnull List<Maybe<JsonElement>> values) {
    return values.foldLeft((arr, el) -> {
      arr.add(o(el));
      return arr;
    }, new JsonArray());
  }

  @Nonnull
  private static JsonElement o(@Nonnull Maybe<JsonElement> el) {
    return el.orJust(new JsonNull());
  }

  @Nonnull
  private static JsonElement e(@Nonnull Either<JsonElement, JsonElement> el) {
    JsonArray arr = new JsonArray();
    return el.either(x -> {
      arr.add(x);
      arr.add(new JsonNull());
      return arr;
    }, x -> {
      arr.add(new JsonNull());
      arr.add(x);
      return arr;
    });
  }

  @Nonnull
  private static JsonObjectBuilder b(@Nonnull String type) {
    return new JsonObjectBuilder().add("type", type);
  }

  @Nonnull
  @Override
  public JsonObject reduceScript(@Nonnull Script node, @Nonnull List<Branch> path, @Nonnull JsonElement body) {
    return b("Script").add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceIdentifier(@Nonnull Identifier node, @Nonnull List<Branch> path) {
    return b("Identifier").add("name", node.name).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceIdentifierExpression(
      @Nonnull IdentifierExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement identifier) {
    return b("IdentifierExpression").add("identifier", identifier).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceThisExpression(@Nonnull ThisExpression node, @Nonnull List<Branch> path) {
    return b("ThisExpression").object;
  }

  @Nonnull
  @Override
  public JsonElement reduceLiteralBooleanExpression(
      @Nonnull LiteralBooleanExpression node,
      @Nonnull List<Branch> path) {
    return b("LiteralBooleanExpression").add("value", node.value).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceLiteralStringExpression(@Nonnull LiteralStringExpression node, @Nonnull List<Branch> path) {
    return b("LiteralStringExpression").add("value", node.value).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node, @Nonnull List<Branch> path) {
    return b("LiteralRegexExpression").add("value", node.value).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceLiteralNumericExpression(
      @Nonnull LiteralNumericExpression node,
      @Nonnull List<Branch> path) {
    return b("LiteralNumericExpression").add("value", node.value).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceLiteralNullExpression(@Nonnull LiteralNullExpression node, @Nonnull List<Branch> path) {
    return b("LiteralNullExpression").object;
  }

  @Nonnull
  @Override
  public JsonElement reduceFunctionExpression(
      @Nonnull FunctionExpression node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<JsonElement> name,
      @Nonnull List<JsonElement> parameters,
      @Nonnull JsonElement body) {
    return b("FunctionExpression").add("name", name).add("parameters", list(parameters)).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceStaticMemberExpression(
      @Nonnull StaticMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement object,
      @Nonnull JsonElement property) {
    return b("StaticMemberExpression").add("object", object).add("property", property).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceComputedMemberExpression(
      @Nonnull ComputedMemberExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement object,
      @Nonnull JsonElement expression) {
    return b("ComputedMemberExpression").add("object", object).add("expression", expression).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceObjectExpression(
      @Nonnull ObjectExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<JsonElement> properties) {
    return b("ObjectExpression").add("properties", list(properties)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceBinaryExpression(
      @Nonnull BinaryExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement left,
      @Nonnull JsonElement right) {
    return b("BinaryExpression").add("operator", node.operator.getName()).add("left", left).add("right", right).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceAssignmentExpression(
      @Nonnull AssignmentExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement binding,
      @Nonnull JsonElement expression) {
    return b("AssignmentExpression").add("operator", node.operator.getName()).add("binding", binding).add("expression",
        expression).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceArrayExpression(
      @Nonnull ArrayExpression node,
      @Nonnull List<Branch> path,
      @Nonnull List<Maybe<JsonElement>> elements) {
    return b("ArrayExpression").add("elements", olist(elements)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceNewExpression(
      @Nonnull NewExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement callee,
      @Nonnull List<JsonElement> arguments) {
    return b("NewExpression").add("callee", callee).add("arguments", list(arguments)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceCallExpression(
      @Nonnull CallExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement callee,
      @Nonnull List<JsonElement> arguments) {
    return b("CallExpression").add("callee", callee).add("arguments", list(arguments)).object;
  }

  @Nonnull
  @Override
  public JsonElement reducePostfixExpression(
      @Nonnull PostfixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement operand) {
    return b("PostfixExpression").add("operator", node.operator.getName()).add("operand", operand).object;
  }

  @Nonnull
  @Override
  public JsonElement reducePrefixExpression(
      @Nonnull PrefixExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement operand) {
    return b("PrefixExpression").add("operator", node.operator.getName()).add("operand", operand).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceConditionalExpression(
      @Nonnull ConditionalExpression node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement test,
      @Nonnull JsonElement consequent,
      @Nonnull JsonElement alternate) {
    return b("ConditionalExpression").add("test", test).add("consequent", consequent).add("alternate",
        alternate).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceFunctionDeclaration(
      @Nonnull FunctionDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement id,
      @Nonnull List<JsonElement> params,
      @Nonnull JsonElement body) {
    return b("FunctionDeclaration").add("name", id).add("parameters", list(params)).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceUseStrictDirective(@Nonnull UseStrictDirective node, @Nonnull List<Branch> path) {
    return b("UseStrictDirective").object;
  }

  @Nonnull
  @Override
  public JsonElement reduceUnknownDirective(@Nonnull UnknownDirective node, @Nonnull List<Branch> path) {
    return b("UnknownDirective").add("value", node.value).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceBlockStatement(
      @Nonnull BlockStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement block) {
    return b("BlockStatement").add("block", block).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceBreakStatement(
      @Nonnull BreakStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<JsonElement> label) {
    return b("BreakStatement").add("label", label).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceCatchClause(
      @Nonnull CatchClause node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement param,
      @Nonnull JsonElement body) {
    return b("CatchClause").add("binding", param).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceContinueStatement(
      @Nonnull ContinueStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<JsonElement> label) {
    return b("ContinueStatement").add("label", label).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceDebuggerStatement(@Nonnull DebuggerStatement node, @Nonnull List<Branch> path) {
    return b("DebuggerStatement").object;
  }

  @Nonnull
  @Override
  public JsonElement reduceDoWhileStatement(
      @Nonnull DoWhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement body,
      @Nonnull JsonElement test) {
    return b("DoWhileStatement").add("body", body).add("test", test).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceEmptyStatement(@Nonnull EmptyStatement node, @Nonnull List<Branch> path) {
    return b("EmptyStatement").object;
  }

  @Nonnull
  @Override
  public JsonElement reduceExpressionStatement(
      @Nonnull ExpressionStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement expression) {
    return b("ExpressionStatement").add("expression", expression).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceForInStatement(
      @Nonnull ForInStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Either<JsonElement, JsonElement> left,
      @Nonnull JsonElement right,
      @Nonnull JsonElement body) {
    return b("ForInStatement").add("left", left).add("right", right).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceForStatement(
      @Nonnull ForStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<Either<JsonElement, JsonElement>> init,
      @Nonnull Maybe<JsonElement> test,
      @Nonnull Maybe<JsonElement> update,
      @Nonnull JsonElement body) {
    return b("ForStatement").add("init", init.map(x -> x.either(y -> y, y -> y))).add("test", test).add("update",
        update).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceIfStatement(
      @Nonnull IfStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement test,
      @Nonnull JsonElement consequent,
      @Nonnull Maybe<JsonElement> alternate) {
    return b("IfStatement").add("test", test).add("consequent", consequent).add("alternate", alternate).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceLabeledStatement(
      @Nonnull LabeledStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement label,
      @Nonnull JsonElement body) {
    return b("LabeledStatement").add("label", label).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceReturnStatement(
      @Nonnull ReturnStatement node,
      @Nonnull List<Branch> path,
      @Nonnull Maybe<JsonElement> expression) {
    return b("ReturnStatement").add("expression", expression).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceSwitchCase(
      @Nonnull SwitchCase node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement test,
      @Nonnull List<JsonElement> consequent) {
    return b("SwitchCase").add("test", test).add("consequent", list(consequent)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceSwitchDefault(
      @Nonnull SwitchDefault node,
      @Nonnull List<Branch> path,
      @Nonnull List<JsonElement> consequent) {
    return b("SwitchDefault").add("consequent", list(consequent)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceSwitchStatement(
      @Nonnull SwitchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement discriminant,
      @Nonnull List<JsonElement> cases) {
    return b("SwitchStatement").add("discriminant", discriminant).add("cases", list(cases)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceSwitchStatementWithDefault(
      @Nonnull SwitchStatementWithDefault node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement discriminant,
      @Nonnull List<JsonElement> cases,
      @Nonnull JsonElement defaultCase,
      @Nonnull List<JsonElement> postDefaultCases) {
    return b("SwitchStatementWithDefault").add("discriminant", discriminant).add("preDefaultCases", list(cases)).add(
        "defaultCase", defaultCase).add("postDefaultCases", list(postDefaultCases)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceThrowStatement(
      @Nonnull ThrowStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement expression) {
    return b("ThrowStatement").add("expression", expression).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceTryCatchStatement(
      @Nonnull TryCatchStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement body,
      @Nonnull JsonElement handler) {
    return b("TryStatement").add("body", body).add("handler", handler).add("finalizer", new JsonNull()).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceTryFinallyStatement(
      @Nonnull TryFinallyStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement body,
      @Nonnull Maybe<JsonElement> handler,
      @Nonnull JsonElement finalizer) {
    return b("TryStatement").add("body", body).add("handler", handler).add("finalizer", finalizer).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceVariableDeclarationStatement(
      @Nonnull VariableDeclarationStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement declaration) {
    return b("VariableDeclarationStatement").add("declaration", declaration).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceVariableDeclaration(
      @Nonnull VariableDeclaration node,
      @Nonnull List<Branch> path,
      @Nonnull NonEmptyList<JsonElement> declarators) {
    return b("VariableDeclaration").add("kind", node.kind.name).add("declarators", list(declarators)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceWhileStatement(
      @Nonnull WhileStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement test,
      @Nonnull JsonElement body) {
    return b("WhileStatement").add("test", test).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceWithStatement(
      @Nonnull WithStatement node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement object,
      @Nonnull JsonElement body) {
    return b("WithStatement").add("object", object).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceDataProperty(
      @Nonnull DataProperty node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement name,
      @Nonnull JsonElement value) {
    return b("DataProperty").add("name", name).add("value", value).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceGetter(
      @Nonnull Getter node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement name,
      @Nonnull JsonElement body) {
    return b("Getter").add("name", name).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceSetter(
      @Nonnull Setter node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement name,
      @Nonnull JsonElement parameter,
      @Nonnull JsonElement body) {
    return b("Setter").add("name", name).add("parameter", parameter).add("body", body).object;
  }

  @Nonnull
  @Override
  public JsonElement reducePropertyName(@Nonnull PropertyName node, @Nonnull List<Branch> path) {
    return b("PropertyName").add("value", node.value).add("type", node.kind.name).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceFunctionBody(
      @Nonnull FunctionBody node,
      @Nonnull List<Branch> path,
      @Nonnull List<JsonElement> directives,
      @Nonnull List<JsonElement> sourceElements) {
    return b("FunctionBody").add("directives", list(directives)).add("statements", list(sourceElements)).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceVariableDeclarator(
      @Nonnull VariableDeclarator node,
      @Nonnull List<Branch> path,
      @Nonnull JsonElement binding,
      @Nonnull Maybe<JsonElement> init) {
    return b("VariableDeclarator").add("binding", binding).add("init", init).object;
  }

  @Nonnull
  @Override
  public JsonElement reduceBlock(
      @Nonnull Block node,
      @Nonnull List<Branch> path,
      @Nonnull List<JsonElement> statements) {
    return b("Block").add("statements", list(statements)).object;
  }

  private static class JsonObjectBuilder {
    final JsonObject object = new JsonObject();

    @Nonnull
    JsonObjectBuilder add(@Nonnull String property, boolean value) {
      this.object.addProperty(property, value);
      return this;
    }

    @Nonnull
    JsonObjectBuilder add(@Nonnull String property, @Nonnull String value) {
      this.object.addProperty(property, value);
      return this;
    }

    @Nonnull
    JsonObjectBuilder add(@Nonnull String property, @Nonnull Number value) {
      this.object.addProperty(property, value);
      return this;
    }

    @Nonnull
    JsonObjectBuilder add(@Nonnull String property, @Nonnull JsonElement value) {
      this.object.add(property, value);
      return this;
    }

    @Nonnull
    JsonObjectBuilder add(@Nonnull String property, @Nonnull Maybe<JsonElement> value) {
      this.object.add(property, o(value));
      return this;
    }

    @Nonnull
    JsonObjectBuilder add(@Nonnull String property, @Nonnull Either<JsonElement, JsonElement> value) {
      this.object.add(property, e(value));
      return this;
    }
  }
}
