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

package com.shapesecurity.shift.serialization;

import com.shapesecurity.functional.data.Either;
import com.shapesecurity.functional.data.List;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyList;
import com.shapesecurity.shift.ast.Block;
import com.shapesecurity.shift.ast.CatchClause;
import com.shapesecurity.shift.ast.FunctionBody;
import com.shapesecurity.shift.ast.Identifier;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.ast.SourceLocation;
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
import com.shapesecurity.shift.ast.expression.LiteralInfinityExpression;
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
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Reducer;

import org.jetbrains.annotations.NotNull;


public class Serializer implements Reducer<StringBuilder> {
  public static final Serializer INSTANCE = new Serializer();

  protected Serializer() {
  }

  @NotNull
  public static String serialize(@NotNull Script script) {
    return script.reduce(INSTANCE).toString();
  }

  @NotNull
  private static StringBuilder list(@NotNull List<StringBuilder> values) {
    if (values.isEmpty()) {
      return new StringBuilder("[]");
    }
    StringBuilder sb = new StringBuilder("[");
    NonEmptyList<StringBuilder> nel = (NonEmptyList<StringBuilder>) values;
    sb.append(nel.head);
    nel.tail().foreach(s -> sb.append(",").append(s));
    sb.append("]");
    return sb;
  }

  @NotNull
  private static StringBuilder olist(@NotNull List<Maybe<StringBuilder>> values) {
    if (values.isEmpty()) {
      return new StringBuilder("[]");
    }
    StringBuilder sb = new StringBuilder("[");
    NonEmptyList<Maybe<StringBuilder>> nel = (NonEmptyList<Maybe<StringBuilder>>) values;
    sb.append(o(nel.head));
    nel.tail().foreach(s -> sb.append(",").append(o(s)));
    sb.append("]");
    return sb;
  }

  @NotNull
  private static StringBuilder o(@NotNull Maybe<StringBuilder> el) {
    return el.orJust(new StringBuilder("null"));
  }

  @NotNull
  private static CharSequence e(@NotNull Either<StringBuilder, StringBuilder> el) {
    return olist(List.list(el.left(), el.right()));
  }

  private static class JsonObjectBuilder {
    boolean first = true;
    final StringBuilder text = new StringBuilder("{");

    private void optionalComma() {
      if (this.first) {
        this.first = false;
      } else {
        this.text.append(",");
      }
    }

    @NotNull
    JsonObjectBuilder add(@NotNull String property, boolean value) {
      optionalComma();
      this.text.append(Utils.escapeStringLiteral(property)).append(":").append(value);
      return this;
    }

    @NotNull
    JsonObjectBuilder add(@NotNull String property, @NotNull String value) {
      optionalComma();
      this.text.append(Utils.escapeStringLiteral(property)).append(":").append(Utils.escapeStringLiteral(value));
      return this;
    }

    @NotNull
    JsonObjectBuilder add(@NotNull String property, @NotNull Number value) {
      optionalComma();
      this.text.append(Utils.escapeStringLiteral(property)).append(":").append(value);
      return this;
    }

    @NotNull
    JsonObjectBuilder add(@NotNull String property, @NotNull StringBuilder value) {
      optionalComma();
      this.text.append(Utils.escapeStringLiteral(property)).append(":").append(value);
      return this;
    }

    @NotNull
    JsonObjectBuilder add(@NotNull String property, @NotNull Maybe<StringBuilder> value) {
      optionalComma();
      this.text.append(Utils.escapeStringLiteral(property)).append(":").append(o(value));
      return this;
    }

    @NotNull
    JsonObjectBuilder add(@NotNull String property, @NotNull Either<StringBuilder, StringBuilder> value) {
      optionalComma();
      this.text.append(Utils.escapeStringLiteral(property)).append(":").append(e(value));
      return this;
    }

    @NotNull
    StringBuilder done(@NotNull Node node) {
      SourceLocation loc = node.getLoc();
      if (loc != null && loc.source != null) {
        this.add("range",
            list(List.list(new StringBuilder(Integer.toString(loc.offset)), new StringBuilder(
                Integer.toString(loc.offset + loc.source.length())))));
      }
      this.text.append("}");
      return this.text;
    }
  }

  @NotNull
  private static JsonObjectBuilder b(@NotNull String type) {
    return new JsonObjectBuilder().add("type", type);
  }

  @NotNull
  @Override
  public StringBuilder reduceScript(@NotNull Script node, @NotNull List<Branch> path, @NotNull StringBuilder body) {
    return b("Script").add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceIdentifier(@NotNull Identifier node, @NotNull List<Branch> path) {
    return b("Identifier").add("name", node.name).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder identifier) {
    return b("IdentifierExpression").add("identifier", identifier).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceThisExpression(@NotNull ThisExpression node, @NotNull List<Branch> path) {
    return b("ThisExpression").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralBooleanExpression(
      @NotNull LiteralBooleanExpression node,
      @NotNull List<Branch> path) {
    return b("LiteralBooleanExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralStringExpression(
      @NotNull LiteralStringExpression node, @NotNull List<Branch> path) {
    return b("LiteralStringExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralRegExpExpression(
      @NotNull LiteralRegExpExpression node, @NotNull List<Branch> path) {
    return b("LiteralRegexExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralNumericExpression(
      @NotNull LiteralNumericExpression node,
      @NotNull List<Branch> path) {
    return b("LiteralNumericExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node,
                                                       @NotNull List<Branch> path) {
    return b("LiteralInfinityExpression").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralNullExpression(@NotNull LiteralNullExpression node, @NotNull List<Branch> path) {
    return b("LiteralNullExpression").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull List<Branch> path,
      @NotNull Maybe<StringBuilder> name,
      @NotNull List<StringBuilder> parameters,
      @NotNull StringBuilder body) {
    return b("FunctionExpression").add("name", name).add("parameters", list(parameters)).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder object,
      @NotNull StringBuilder property) {
    return b("StaticMemberExpression").add("object", object).add("property", property).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder object,
      @NotNull StringBuilder expression) {
    return b("ComputedMemberExpression").add("object", object).add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull List<Branch> path,
      @NotNull List<StringBuilder> properties) {
    return b("ObjectExpression").add("properties", list(properties)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBinaryExpression(
      @NotNull BinaryExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder left,
      @NotNull StringBuilder right) {
    return b("BinaryExpression").add("operator", node.operator.getName()).add("left", left).add("right", right).done(
        node);
  }

  @NotNull
  @Override
  public StringBuilder reduceAssignmentExpression(
      @NotNull AssignmentExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder binding,
      @NotNull StringBuilder expression) {
    return b("AssignmentExpression").add("operator", node.operator.getName()).add("binding", binding).add("expression",
        expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull List<Branch> path,
      @NotNull List<Maybe<StringBuilder>> elements) {
    return b("ArrayExpression").add("elements", olist(elements)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder callee,
      @NotNull List<StringBuilder> arguments) {
    return b("NewExpression").add("callee", callee).add("arguments", list(arguments)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder callee,
      @NotNull List<StringBuilder> arguments) {
    return b("CallExpression").add("callee", callee).add("arguments", list(arguments)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder operand) {
    return b("PostfixExpression").add("operator", node.operator.getName()).add("operand", operand).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder operand) {
    return b("PrefixExpression").add("operator", node.operator.getName()).add("operand", operand).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder test,
      @NotNull StringBuilder consequent,
      @NotNull StringBuilder alternate) {
    return b("ConditionalExpression").add("test", test).add("consequent", consequent).add("alternate",
        alternate).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder name,
      @NotNull List<StringBuilder> params,
      @NotNull StringBuilder body) {
    return b("FunctionDeclaration").add("name", name).add("parameters", list(params)).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceUseStrictDirective(@NotNull UseStrictDirective node, @NotNull List<Branch> path) {
    return b("UseStrictDirective").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceUnknownDirective(@NotNull UnknownDirective node, @NotNull List<Branch> path) {
    return b("UnknownDirective").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder block) {
    return b("BlockStatement").add("block", block).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<StringBuilder> label) {
    return b("BreakStatement").add("label", label).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder binding,
      @NotNull StringBuilder body) {
    return b("CatchClause").add("binding", binding).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<StringBuilder> label) {
    return b("ContinueStatement").add("label", label).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceDebuggerStatement(@NotNull DebuggerStatement node, @NotNull List<Branch> path) {
    return b("DebuggerStatement").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder body,
      @NotNull StringBuilder test) {
    return b("DoWhileStatement").add("body", body).add("test", test).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceEmptyStatement(@NotNull EmptyStatement node, @NotNull List<Branch> path) {
    return b("EmptyStatement").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder expression) {
    return b("ExpressionStatement").add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull List<Branch> path,
      @NotNull Either<StringBuilder, StringBuilder> left,
      @NotNull StringBuilder right,
      @NotNull StringBuilder body) {
    return b("ForInStatement").add("left", left).add("right", right).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceForStatement(
      @NotNull ForStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<Either<StringBuilder, StringBuilder>> init,
      @NotNull Maybe<StringBuilder> test,
      @NotNull Maybe<StringBuilder> update,
      @NotNull StringBuilder body) {
    return b("ForStatement").add("init", init.map(x -> x.either(y -> y, y -> y))).add("test", test).add("update",
        update).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder test,
      @NotNull StringBuilder consequent,
      @NotNull Maybe<StringBuilder> alternate) {
    return b("IfStatement").add("test", test).add("consequent", consequent).add("alternate", alternate).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder label,
      @NotNull StringBuilder body) {
    return b("LabeledStatement").add("label", label).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull List<Branch> path,
      @NotNull Maybe<StringBuilder> expression) {
    return b("ReturnStatement").add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder test,
      @NotNull List<StringBuilder> consequent) {
    return b("SwitchCase").add("test", test).add("consequent", list(consequent)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull List<Branch> path,
      @NotNull List<StringBuilder> consequent) {
    return b("SwitchDefault").add("consequent", list(consequent)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder discriminant,
      @NotNull List<StringBuilder> cases) {
    return b("SwitchStatement").add("discriminant", discriminant).add("cases", list(cases)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder discriminant,
      @NotNull List<StringBuilder> preDefaultCases,
      @NotNull StringBuilder defaultCase,
      @NotNull List<StringBuilder> postDefaultCases) {
    return b("SwitchStatementWithDefault").add("discriminant", discriminant).add("preDefaultCases", list(
            preDefaultCases)).add(
        "defaultCase", defaultCase).add("postDefaultCases", list(postDefaultCases)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder expression) {
    return b("ThrowStatement").add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder body,
      @NotNull StringBuilder handler) {
    return b("TryStatement").add("body", body).add("handler", handler).add("finalizer", Maybe.nothing()).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder body,
      @NotNull Maybe<StringBuilder> handler,
      @NotNull StringBuilder finalizer) {
    return b("TryStatement").add("body", body).add("handler", handler).add("finalizer", finalizer).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder declaration) {
    return b("VariableDeclarationStatement").add("declaration", declaration).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull List<Branch> path,
      @NotNull NonEmptyList<StringBuilder> declarators) {
    return b("VariableDeclaration").add("kind", node.kind.name).add("declarators", list(declarators)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder test,
      @NotNull StringBuilder body) {
    return b("WhileStatement").add("test", test).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder object,
      @NotNull StringBuilder body) {
    return b("WithStatement").add("object", object).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder name,
      @NotNull StringBuilder value) {
    return b("DataProperty").add("name", name).add("value", value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceGetter(
      @NotNull Getter node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder name,
      @NotNull StringBuilder body) {
    return b("Getter").add("name", name).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSetter(
      @NotNull Setter node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder name,
      @NotNull StringBuilder parameter,
      @NotNull StringBuilder body) {
    return b("Setter").add("name", name).add("parameter", parameter).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reducePropertyName(@NotNull PropertyName node, @NotNull List<Branch> path) {
    return b("PropertyName").add("value", node.value).add("kind", node.kind.name).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull List<Branch> path,
      @NotNull List<StringBuilder> directives,
      @NotNull List<StringBuilder> statements) {
    return b("FunctionBody").add("directives", list(directives)).add("statements", list(statements)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull List<Branch> path,
      @NotNull StringBuilder binding,
      @NotNull Maybe<StringBuilder> init) {
    return b("VariableDeclarator").add("binding", binding).add("init", init).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBlock(
      @NotNull Block node,
      @NotNull List<Branch> path,
      @NotNull List<StringBuilder> statements) {
    return b("Block").add("statements", list(statements)).done(node);
  }
}
