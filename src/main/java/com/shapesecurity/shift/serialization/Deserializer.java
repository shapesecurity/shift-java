package com.shapesecurity.shift.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import org.json.JSONException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Deserializer {

  private final String PACKAGE_NAME = "com.shapesecurity.shift.ast.";

  public Deserializer() {
  }

  public Node deserialize(String toDeserialize) throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
    JsonElement json = new JsonParser().parse(toDeserialize);
    return deserializeNode(json);
  }

  private Node deserializeNode(JsonElement jsonElement) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    Node toReturn = null; // TODO: remove the null later
    if (jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if (jsonObject.has("type")) {
        String nodeType = jsonObject.get("type").getAsString();
        System.out.println(nodeType);
        switch (nodeType) {
          case "ArrayBinding":
            ImmutableList<Maybe<BindingBindingWithDefault>> elements_ab = deserializeList(jsonObject.getAsJsonArray("elements"));
            Maybe<Binding> restElement = deserializeMaybeBinding(jsonObject.get("restElement"));
            return new ArrayBinding(elements_ab, restElement);
          case "ArrayExpression":
            ImmutableList<Maybe<SpreadElementExpression>> elements_ae = deserializeList(jsonObject.getAsJsonArray("elements"));
            return new ArrayExpression(elements_ae);
          case "ArrowExpression":
            FormalParameters params = (FormalParameters)deserializeNode(jsonObject.get("params"));
            FunctionBodyExpression body = separateFunctionBodyExpression(deserializeNode(jsonObject.get("body")));
            return new ArrowExpression(params, body);
          case "AssignmentExpression":
            Binding binding_ae = (Binding)deserializeNode(jsonObject.get("binding"));
            Expression expression_ae = (Expression)deserializeNode(jsonObject.get("expression"));
            return new AssignmentExpression(binding_ae, expression_ae);
          case "BinaryExpression":
            BinaryOperator operator = deserializeBinaryOperator(jsonObject.get("operator"));
            Expression left = (Expression)deserializeNode(jsonObject.get("left"));
            Expression right = (Expression)deserializeNode(jsonObject.get("right"));
            return new BinaryExpression(operator, left, right);
          case "BindingIdentifier":
            String name = jsonObject.get("name").getAsString();
            return new BindingIdentifier(name);
          case "BindingPropertyIdentifier":
            BindingIdentifier binding_bpr = (BindingIdentifier)deserializeNode(jsonObject.get("binding"));
            Maybe<Expression> init_bpi = deserializeMaybeExpression(jsonObject.get("init"));
            return new BindingPropertyIdentifier(binding_bpr, init_bpi);
          case "BindingPropertyProperty":
            PropertyName name_bpp = (PropertyName)deserializeNode(jsonObject.get("name"));
            BindingBindingWithDefault binding_bpp = separateBindingBindingWithDefault(deserializeNode(jsonObject.get("binding")));
            return new BindingPropertyProperty(name_bpp, binding_bpp);
          case "BindingWithDefault":
            Binding binding_bwd = separateBinding(deserializeNode(jsonObject.get("binding")));
            Expression init_bwd = (Expression)deserializeNode(jsonObject.get("init"));
            return new BindingWithDefault(binding_bwd, init_bwd);
          case "Block":
            ImmutableList<Statement> statements_b = deserializeList(jsonObject.getAsJsonArray("statements"));
            return new Block(statements_b);
          case "BlockStatement":
            Block block = (Block)deserializeNode(jsonObject.get("block"));
            return new BlockStatement(block);
          case "BreakStatement":
            Maybe<String> label = deserializeMaybeString(jsonObject.get("label"));
            return new BreakStatement(label);
          case "CallExpression":
            ExpressionSuper callee = separateExpressionSuper(deserializeNode(jsonObject.get("callee")));
            ImmutableList<SpreadElementExpression> arguments = deserializeList(jsonObject.getAsJsonArray("arguments"));
            return new CallExpression(callee, arguments);
          case "CatchClause":

          case "DebuggerStatement":
            return new DebuggerStatement();
          case "Directive":
            String rawValue = jsonObject.get("rawValue").getAsString();
            return new Directive(rawValue);
          case "EmptyStatement":
            return new EmptyStatement();
          case "ExpressionStatement":
            Expression expression_es = (Expression)deserializeNode(jsonObject.get("expression"));
            return new ExpressionStatement(expression_es);
          case "LiteralNumericExpression":
            double value = jsonObject.get("value").getAsDouble();
            return new LiteralNumericExpression(value);
          case "Script":
            ImmutableList<Directive> directives = deserializeList(jsonObject.getAsJsonArray("directives"));
            ImmutableList<Statement> statements = deserializeList(jsonObject.getAsJsonArray("statements"));
            return new Script(directives, statements);
        }
      }
    }
    return toReturn;
  }

  /**************************
   * PRIVATE HELPER METHODS *
   **************************/

  private ExpressionSuper separateExpressionSuper(Node node) {
    if (node instanceof Expression) {
      return (Expression) node;
    } else {
      return (Super) node;
    }
  }

  private Maybe<String> deserializeMaybeString(JsonElement jsonElement) {
    if (jsonElement.getAsString().equals("null")) {
      return Maybe.nothing();
    } else {
      return Maybe.just(jsonElement.getAsString());
    }
  }

  private BindingBindingWithDefault separateBindingBindingWithDefault(Node node) {
    if (node instanceof BindingWithDefault) {
      return (BindingWithDefault) node;
    } else {
      return separateBinding(node);
    }
  }

  private FunctionBodyExpression separateFunctionBodyExpression(Node node) {
    if (node instanceof FunctionBody) {
      return (FunctionBody) node;
    } else {
      return (Expression) node;
    }
  }

  private Maybe<Expression> deserializeMaybeExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.getAsString().equals("null")) {
      System.out.println("null expression");
      return Maybe.nothing();
    } else {
      System.out.println("not null expression");
      return Maybe.just((Expression) deserializeNode(jsonElement));
    }
  }

  private ImmutableList deserializeList(JsonArray jsonArray) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonArray.size() == 0) {
      System.out.println("empty array");
      return ImmutableList.nil();
    } else {
      ArrayList deserializedElements = new ArrayList();
      for (JsonElement jsonElement : jsonArray) {
        deserializedElements.add(deserializeNode(jsonElement));
      }
      System.out.println("array of size " + deserializedElements.size());
      return ImmutableList.from(deserializedElements);
    }
  }

  private Maybe<Binding> deserializeMaybeBinding(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.getAsString().equals("null")) {
      System.out.println("null binding");
      return Maybe.nothing();
    }
    System.out.println("not null binding");
    Node node = deserializeNode(jsonElement);
    return Maybe.just(separateBinding(node));
  }

  private Binding separateBinding(Node node) {
    if (node instanceof ArrayBinding) {
      return (ArrayBinding) node;
    } else if (node instanceof BindingIdentifier) {
      return (BindingIdentifier) node;
    } else if (node instanceof BindingPattern) {
      return (BindingPattern) node;
    } else if (node instanceof ComputedMemberExpression) {
      return (ComputedMemberExpression) node;
    } else if (node instanceof ObjectBinding) {
      return (ObjectBinding) node;
    } else if (node instanceof StaticMemberExpression) {
      return (StaticMemberExpression) node;
    } else {
      return null;
    }
  }
  private BinaryOperator deserializeBinaryOperator(JsonElement jsonElement) {
    String operatorString = jsonElement.getAsString();
    switch (operatorString) {
      case ",":
        return BinaryOperator.Sequence;
      case "||":
        return BinaryOperator.LogicalOr;
      case "&&":
        return BinaryOperator.LogicalAnd;
      case "|":
        return BinaryOperator.BitwiseOr;
      case "^":
        return BinaryOperator.BitwiseXor;
      case "&":
        return BinaryOperator.LogicalAnd;
      case "+":
        return BinaryOperator.Plus;
      case "-":
        return BinaryOperator.Minus;
      case "==":
        return BinaryOperator.Equal;
      case "!=":
        return BinaryOperator.NotEqual;
      case "===":
        return BinaryOperator.StrictEqual;
      case "!==":
        return BinaryOperator.StrictNotEqual;
      case "*":
        return BinaryOperator.Mul;
      case "/":
        return BinaryOperator.Div;
      case "%":
        return BinaryOperator.Rem;
      case "<":
        return BinaryOperator.LessThan;
      case "<=":
        return BinaryOperator.LessThanEqual;
      case ">":
        return BinaryOperator.GreaterThan;
      case ">=":
        return BinaryOperator.GreaterThanEqual;
      case "in":
        return BinaryOperator.In;
      case "instanceof":
        return BinaryOperator.Instanceof;
      case "<<":
        return BinaryOperator.Left;
      case ">>":
        return BinaryOperator.Right;
      case ">>>":
        return BinaryOperator.UnsignedRight;
      default:
        return null; // should not get here
    }
  }

}
