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
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.utils.Utils;
import com.shapesecurity.shift.visitor.Reducer;
import org.jetbrains.annotations.NotNull;


public class Serializer implements Reducer<StringBuilder> {
  public static final Serializer INSTANCE = new Serializer();

  protected Serializer() {
  }

  @NotNull
  private static JsonObjectBuilder b(@NotNull String type) {
    return new JsonObjectBuilder().add("type", type);
  }

  @NotNull
  private static CharSequence e(@NotNull Either<StringBuilder, StringBuilder> el) {
    return olist(ImmutableList.list(el.left(), el.right()));
  }

  @NotNull
  private static StringBuilder list(@NotNull ImmutableList<StringBuilder> values) {
    if (values.isEmpty()) {
      return new StringBuilder("[]");
    }
    StringBuilder sb = new StringBuilder("[");
    NonEmptyImmutableList<StringBuilder> nel = (NonEmptyImmutableList<StringBuilder>) values;
    sb.append(nel.head);
    nel.tail().foreach(s -> sb.append(",").append(s));
    sb.append("]");
    return sb;
  }

  @NotNull
  private static StringBuilder o(@NotNull Maybe<StringBuilder> el) {
    return el.orJust(new StringBuilder("null"));
  }

  @NotNull
  private static StringBuilder olist(@NotNull ImmutableList<Maybe<StringBuilder>> values) {
    if (values.isEmpty()) {
      return new StringBuilder("[]");
    }
    StringBuilder sb = new StringBuilder("[");
    NonEmptyImmutableList<Maybe<StringBuilder>> nel = (NonEmptyImmutableList<Maybe<StringBuilder>>) values;
    sb.append(o(nel.head));
    nel.tail().foreach(s -> sb.append(",").append(o(s)));
    sb.append("]");
    return sb;
  }

  @NotNull
  public static String serialize(@NotNull Script script) {
    return script.reduce(INSTANCE).toString();
  }

  @NotNull
  @Override
  public StringBuilder reduceArrayBinding(@NotNull ArrayBinding node, @NotNull ImmutableList<Maybe<StringBuilder>> elements, @NotNull Maybe<StringBuilder> restElement) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceArrayExpression(
      @NotNull ArrayExpression node,
      @NotNull ImmutableList<Maybe<StringBuilder>> elements) {
    return b("ArrayExpression").add("elements", olist(elements)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceArrowExpression(@NotNull ArrowExpression node, @NotNull StringBuilder params, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceAssignmentExpression(@NotNull AssignmentExpression node, @NotNull StringBuilder binding, @NotNull StringBuilder expression) {
    return b("AssignmentExpression").add("operator", "=").add("binding", binding).add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBinaryExpression(@NotNull BinaryExpression node, @NotNull StringBuilder left, @NotNull StringBuilder right) {
    return b("BinaryExpression").add("operator", node.operator.getName()).add("left", left).add("right", right).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBindingIdentifier(@NotNull BindingIdentifier node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceBindingPropertyIdentifier(@NotNull BindingPropertyIdentifier node, @NotNull StringBuilder binding, @NotNull Maybe<StringBuilder> init) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceBindingPropertyProperty(@NotNull BindingPropertyProperty node, @NotNull StringBuilder name, @NotNull StringBuilder binding) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceBindingWithDefault(@NotNull BindingWithDefault node, @NotNull StringBuilder binding, @NotNull StringBuilder init) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceBlock(
      @NotNull Block node,
      @NotNull ImmutableList<StringBuilder> statements) {
    return b("Block").add("statements", list(statements)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBlockStatement(
      @NotNull BlockStatement node,
      @NotNull StringBuilder block) {
    return b("BlockStatement").add("block", block).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceBreakStatement(@NotNull BreakStatement node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceBreakStatement(
      @NotNull BreakStatement node,
      @NotNull Maybe<StringBuilder> label) {
    return b("BreakStatement").add("label", label).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceCallExpression(
      @NotNull CallExpression node,
      @NotNull StringBuilder callee,
      @NotNull ImmutableList<StringBuilder> arguments) {
    return b("CallExpression").add("callee", callee).add("arguments", list(arguments)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceCatchClause(
      @NotNull CatchClause node,
      @NotNull StringBuilder binding,
      @NotNull StringBuilder body) {
    return b("CatchClause").add("binding", binding).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceClassDeclaration(@NotNull ClassDeclaration node, @NotNull StringBuilder name, @NotNull Maybe<StringBuilder> _super, @NotNull ImmutableList<StringBuilder> elements) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceClassElement(@NotNull ClassElement node, @NotNull StringBuilder method) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceClassExpression(@NotNull ClassExpression node, @NotNull Maybe<StringBuilder> name, @NotNull Maybe<StringBuilder> _super, @NotNull ImmutableList<StringBuilder> elements) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceCompoundAssignmentExpression(@NotNull CompoundAssignmentExpression node, @NotNull StringBuilder binding, @NotNull StringBuilder expression) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceComputedMemberExpression(
      @NotNull ComputedMemberExpression node,
      @NotNull StringBuilder object,
      @NotNull StringBuilder expression) {
    return b("ComputedMemberExpression").add("object", object).add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceComputedPropertyName(@NotNull ComputedPropertyName node, @NotNull StringBuilder expression) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceConditionalExpression(
      @NotNull ConditionalExpression node,
      @NotNull StringBuilder test,
      @NotNull StringBuilder consequent,
      @NotNull StringBuilder alternate) {
    return b("ConditionalExpression").add("test", test).add("consequent", consequent).add("alternate",
        alternate).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceContinueStatement(@NotNull ContinueStatement node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceContinueStatement(
      @NotNull ContinueStatement node,
      @NotNull Maybe<StringBuilder> label) {
    return b("ContinueStatement").add("label", label).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceDataProperty(
      @NotNull DataProperty node,
      @NotNull StringBuilder name,
      @NotNull StringBuilder value) {
    return b("DataProperty").add("name", name).add("value", value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceDebuggerStatement(@NotNull DebuggerStatement node) {
    return b("DebuggerStatement").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceDirective(@NotNull Directive node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceDoWhileStatement(
      @NotNull DoWhileStatement node,
      @NotNull StringBuilder body,
      @NotNull StringBuilder test) {
    return b("DoWhileStatement").add("body", body).add("test", test).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceEmptyStatement(@NotNull EmptyStatement node) {
    return b("EmptyStatement").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceExport(@NotNull Export node, @NotNull StringBuilder declaration) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceExportAllFrom(@NotNull ExportAllFrom node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceExportDefault(@NotNull ExportDefault node, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceExportFrom(@NotNull ExportFrom node, @NotNull ImmutableList<StringBuilder> namedExports) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceExportSpecifier(@NotNull ExportSpecifier node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceExpressionStatement(
      @NotNull ExpressionStatement node,
      @NotNull StringBuilder expression) {
    return b("ExpressionStatement").add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceForInStatement(@NotNull ForInStatement node, @NotNull StringBuilder left, @NotNull StringBuilder right, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceForInStatement(
      @NotNull ForInStatement node,
      @NotNull Either<StringBuilder, StringBuilder> left,
      @NotNull StringBuilder right,
      @NotNull StringBuilder body) {
    return b("ForInStatement").add("left", left).add("right", right).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceForOfStatement(@NotNull ForOfStatement node, @NotNull StringBuilder left, @NotNull StringBuilder right, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceForStatement(@NotNull ForStatement node, @NotNull Maybe<StringBuilder> init, @NotNull Maybe<StringBuilder> test, @NotNull Maybe<StringBuilder> update, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceForStatement(
      @NotNull ForStatement node,
      @NotNull Maybe<Either<StringBuilder, StringBuilder>> init,
      @NotNull Maybe<StringBuilder> test,
      @NotNull Maybe<StringBuilder> update,
      @NotNull StringBuilder body) {
    return b("ForStatement").add("init", init.map(x -> x.either(y -> y, y -> y))).add("test", test).add("update",
        update).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFormalParameters(@NotNull FormalParameters node, @NotNull ImmutableList<StringBuilder> items, @NotNull Maybe<StringBuilder> rest) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionBody(
      @NotNull FunctionBody node,
      @NotNull ImmutableList<StringBuilder> directives,
      @NotNull ImmutableList<StringBuilder> statements) {
    return b("FunctionBody").add("directives", list(directives)).add("statements", list(statements)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionDeclaration(
      @NotNull FunctionDeclaration node,
      @NotNull StringBuilder name,
      @NotNull ImmutableList<StringBuilder> params,
      @NotNull StringBuilder body) {
    return b("FunctionDeclaration").add("name", name).add("parameters", list(params)).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull StringBuilder name, @NotNull StringBuilder params, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionExpression(
      @NotNull FunctionExpression node,
      @NotNull Maybe<StringBuilder> name,
      @NotNull ImmutableList<StringBuilder> parameters,
      @NotNull StringBuilder body) {
    return b("FunctionExpression").add("name", name).add("parameters", list(parameters)).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<StringBuilder> name, @NotNull StringBuilder parameters, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceGetter(
      @NotNull Getter node,
      @NotNull StringBuilder name,
      @NotNull StringBuilder body) {
    return b("Getter").add("name", name).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceIdentifier(@NotNull Identifier node) {
    return b("Identifier").add("name", node.name).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceIdentifierExpression(
      @NotNull IdentifierExpression node,
      @NotNull StringBuilder identifier) {
    return b("IdentifierExpression").add("identifier", identifier).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceIdentifierExpression(@NotNull IdentifierExpression node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceIfStatement(
      @NotNull IfStatement node,
      @NotNull StringBuilder test,
      @NotNull StringBuilder consequent,
      @NotNull Maybe<StringBuilder> alternate) {
    return b("IfStatement").add("test", test).add("consequent", consequent).add("alternate", alternate).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceImport(@NotNull Import node, @NotNull Maybe<StringBuilder> defaultBinding, @NotNull ImmutableList<StringBuilder> namedImports) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceImportNamespace(@NotNull ImportNamespace node, @NotNull Maybe<StringBuilder> defaultBinding, @NotNull StringBuilder namespaceBinding) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull StringBuilder binding) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceLabeledStatement(
      @NotNull LabeledStatement node,
      @NotNull StringBuilder label,
      @NotNull StringBuilder body) {
    return b("LabeledStatement").add("label", label).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralBooleanExpression(
      @NotNull LiteralBooleanExpression node) {
    return b("LiteralBooleanExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralInfinityExpression(@NotNull LiteralInfinityExpression node) {
    return b("LiteralInfinityExpression").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralNullExpression(@NotNull LiteralNullExpression node) {
    return b("LiteralNullExpression").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralNumericExpression(
      @NotNull LiteralNumericExpression node) {
    return b("LiteralNumericExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralRegExpExpression(
      @NotNull LiteralRegExpExpression node) {
    return b("LiteralRegexExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceLiteralStringExpression(
      @NotNull LiteralStringExpression node) {
    return b("LiteralStringExpression").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceMethod(@NotNull Method node, @NotNull StringBuilder name, @NotNull StringBuilder params, @NotNull StringBuilder body) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceModule(@NotNull Module node, @NotNull ImmutableList<StringBuilder> directives, @NotNull ImmutableList<StringBuilder> items) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceNewExpression(
      @NotNull NewExpression node,
      @NotNull StringBuilder callee,
      @NotNull ImmutableList<StringBuilder> arguments) {
    return b("NewExpression").add("callee", callee).add("arguments", list(arguments)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceNewTargetExpression(@NotNull NewTargetExpression node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceObjectBinding(@NotNull ObjectBinding node, @NotNull ImmutableList<StringBuilder> properties) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceObjectExpression(
      @NotNull ObjectExpression node,
      @NotNull ImmutableList<StringBuilder> properties) {
    return b("ObjectExpression").add("properties", list(properties)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reducePostfixExpression(
      @NotNull PostfixExpression node,
      @NotNull StringBuilder operand) {
    return b("PostfixExpression").add("operator", node.operator.getName()).add("operand", operand).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reducePrefixExpression(
      @NotNull PrefixExpression node,
      @NotNull StringBuilder operand) {
    return b("PrefixExpression").add("operator", node.operator.getName()).add("operand", operand).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reducePropertyName(@NotNull PropertyName node) {
    return b("PropertyName").add("value", node.value).add("kind", node.kind.name).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceReturnStatement(
      @NotNull ReturnStatement node,
      @NotNull Maybe<StringBuilder> expression) {
    return b("ReturnStatement").add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceScript(@NotNull Script node, @NotNull StringBuilder body) {
    return b("Script").add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceScript(@NotNull Script node, @NotNull ImmutableList<StringBuilder> directives, @NotNull ImmutableList<StringBuilder> statements) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceSetter(
      @NotNull Setter node,
      @NotNull StringBuilder name,
      @NotNull StringBuilder parameter,
      @NotNull StringBuilder body) {
    return b("Setter").add("name", name).add("parameter", parameter).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceShorthandProperty(@NotNull ShorthandProperty node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceSpreadElement(@NotNull SpreadElement node, @NotNull StringBuilder expression) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceStaticMemberExpression(
      @NotNull StaticMemberExpression node,
      @NotNull StringBuilder object,
      @NotNull StringBuilder property) {
    return b("StaticMemberExpression").add("object", object).add("property", property).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull StringBuilder object) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceStaticPropertyName(@NotNull StaticPropertyName node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceSuper(@NotNull Super node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchCase(
      @NotNull SwitchCase node,
      @NotNull StringBuilder test,
      @NotNull ImmutableList<StringBuilder> consequent) {
    return b("SwitchCase").add("test", test).add("consequent", list(consequent)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchDefault(
      @NotNull SwitchDefault node,
      @NotNull ImmutableList<StringBuilder> consequent) {
    return b("SwitchDefault").add("consequent", list(consequent)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchStatement(
      @NotNull SwitchStatement node,
      @NotNull StringBuilder discriminant,
      @NotNull ImmutableList<StringBuilder> cases) {
    return b("SwitchStatement").add("discriminant", discriminant).add("cases", list(cases)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceSwitchStatementWithDefault(
      @NotNull SwitchStatementWithDefault node,
      @NotNull StringBuilder discriminant,
      @NotNull ImmutableList<StringBuilder> preDefaultCases,
      @NotNull StringBuilder defaultCase,
      @NotNull ImmutableList<StringBuilder> postDefaultCases) {
    return b("SwitchStatementWithDefault").add("discriminant", discriminant).add("preDefaultCases", list(
            preDefaultCases)).add(
        "defaultCase", defaultCase).add("postDefaultCases", list(postDefaultCases)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceTemplateElement(@NotNull TemplateElement node) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<StringBuilder> tag, @NotNull ImmutableList<StringBuilder> elements) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceThisExpression(@NotNull ThisExpression node) {
    return b("ThisExpression").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceThrowStatement(
      @NotNull ThrowStatement node,
      @NotNull StringBuilder expression) {
    return b("ThrowStatement").add("expression", expression).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceTryCatchStatement(
      @NotNull TryCatchStatement node,
      @NotNull StringBuilder body,
      @NotNull StringBuilder handler) {
    return b("TryStatement").add("body", body).add("handler", handler).add("finalizer", Maybe.nothing()).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceTryFinallyStatement(
      @NotNull TryFinallyStatement node,
      @NotNull StringBuilder body,
      @NotNull Maybe<StringBuilder> handler,
      @NotNull StringBuilder finalizer) {
    return b("TryStatement").add("body", body).add("handler", handler).add("finalizer", finalizer).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceUnaryExpression(@NotNull UnaryExpression node, @NotNull StringBuilder operand) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceUnknownDirective(@NotNull UnknownDirective node) {
    return b("UnknownDirective").add("value", node.value).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceUpdateExpression(@NotNull UpdateExpression node, @NotNull StringBuilder operand) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceUseStrictDirective(@NotNull UseStrictDirective node) {
    return b("UseStrictDirective").done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<StringBuilder> declarators) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclaration(
      @NotNull VariableDeclaration node,
      @NotNull NonEmptyImmutableList<StringBuilder> declarators) {
    return b("VariableDeclaration").add("kind", node.kind.name).add("declarators", list(declarators)).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclarationStatement(
      @NotNull VariableDeclarationStatement node,
      @NotNull StringBuilder declaration) {
    return b("VariableDeclarationStatement").add("declaration", declaration).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceVariableDeclarator(
      @NotNull VariableDeclarator node,
      @NotNull StringBuilder binding,
      @NotNull Maybe<StringBuilder> init) {
    return b("VariableDeclarator").add("binding", binding).add("init", init).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceWhileStatement(
      @NotNull WhileStatement node,
      @NotNull StringBuilder test,
      @NotNull StringBuilder body) {
    return b("WhileStatement").add("test", test).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceWithStatement(
      @NotNull WithStatement node,
      @NotNull StringBuilder object,
      @NotNull StringBuilder body) {
    return b("WithStatement").add("object", object).add("body", body).done(node);
  }

  @NotNull
  @Override
  public StringBuilder reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<StringBuilder> expression) {
    return null;
  }

  @NotNull
  @Override
  public StringBuilder reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull StringBuilder expression) {
    return null;
  }

  private static class JsonObjectBuilder {
    final StringBuilder text = new StringBuilder("{");
    boolean first = true;

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
            list(
                ImmutableList.list(
                    new StringBuilder(Integer.toString(loc.offset)), new StringBuilder(
                        Integer.toString(loc.offset + loc.source.length())))));
      }
      this.text.append("}");
      return this.text;
    }

    private void optionalComma() {
      if (this.first) {
        this.first = false;
      } else {
        this.text.append(",");
      }
    }
  }
}
