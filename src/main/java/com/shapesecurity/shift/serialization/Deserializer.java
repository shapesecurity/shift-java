package com.shapesecurity.shift.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Deserializer {

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
            Binding binding_cc = separateBinding(deserializeNode(jsonObject.get("binding")));
            Block block_cc = (Block) deserializeNode(jsonObject.get("body"));
            return new CatchClause(binding_cc, block_cc);
          case "ClassDeclaration":
            BindingIdentifier name_cd = (BindingIdentifier)deserializeNode(jsonObject.get("name"));
            Maybe<Expression> _super_cd = deserializeMaybeExpression(jsonObject.get("super"));
            ImmutableList<ClassElement> elements_cd = deserializeList(jsonObject.getAsJsonArray("elements"));
            return new ClassDeclaration(name_cd, _super_cd, elements_cd);
          case "ClassElement":
            boolean isStatic = jsonObject.get("isStatic").getAsBoolean();
            MethodDefinition methodDefinition = (MethodDefinition)deserializeNode(jsonObject.get("method"));
            return new ClassElement(isStatic, methodDefinition);
          case "ClassExpression":
            Maybe<BindingIdentifier> name_ce = deserializeMaybeBindingIdentifier(jsonObject.get("name"));
            Maybe<Expression> super_ce = deserializeMaybeExpression(jsonObject.get("super"));
            ImmutableList<ClassElement> elements_ce = deserializeList(jsonObject.getAsJsonArray("elements"));
            return new ClassExpression(name_ce, super_ce, elements_ce);
          case "CompoundAssignmentExpression":
            CompoundAssignmentOperator operator_cae = deserializeCompoundAssignmentOperator(jsonObject.get("operator"));
            BindingIdentifierMemberExpression binding_cae = separateBindingIdentifierMemberExpression(deserializeNode(jsonObject.get("binding")));
            Expression expression_cae = (Expression)deserializeNode(jsonObject.get("expression"));
            return new CompoundAssignmentExpression(operator_cae, binding_cae, expression_cae);
          case "ComputedMemberExpression":
            Expression expression_cme = (Expression)deserializeNode(jsonObject.get("expression"));
            ExpressionSuper object_cme = separateExpressionSuper(deserializeNode(jsonObject.get("object")));
            return new ComputedMemberExpression(expression_cme, object_cme);
          case "ComputedPropertyName":
            Expression expression_cpn = (Expression)deserializeNode(jsonObject.get("expression"));
            return new ComputedPropertyName(expression_cpn);
          case "ConditionalExpression":
            Expression test = (Expression)deserializeNode(jsonObject.get("test"));
            Expression consequent = (Expression)deserializeNode(jsonObject.get("consequent"));
            Expression alternate = (Expression)deserializeNode(jsonObject.get("alternate"));
            return new ConditionalExpression(test, consequent, alternate);
          case "ContinueStatement":
            Maybe<String> label_cs = deserializeMaybeString(jsonObject.get("label"));
            return new ContinueStatement(label_cs);
          case "DataProperty":
            Expression expression_dp = (Expression)deserializeNode(jsonObject.get("expression"));
            PropertyName name_dp = (PropertyName)deserializeNode(jsonObject.get("name"));
            return new DataProperty(expression_dp, name_dp);
          case "DebuggerStatement":
            return new DebuggerStatement();
          case "Directive":
            String rawValue = jsonObject.get("rawValue").getAsString();
            return new Directive(rawValue);
          case "DoWhileStatement":
            Expression test_dws = (Expression)deserializeNode(jsonObject.get("test"));
            Statement body_dws = (Statement)deserializeNode(jsonObject.get("body"));
            return new DoWhileStatement(test_dws, body_dws);
          case "EmptyStatement":
            return new EmptyStatement();
          case "Export":
            FunctionDeclarationClassDeclarationVariableDeclaration declaration = separateFunctionDeclarationClassDeclarationVariableDeclaration(deserializeNode(jsonObject.get("declaration")));
            return new Export(declaration);
          case "ExportAllFrom":
            String moduleSpecifier = jsonObject.get("moduleSpecifier").getAsString();
            return new ExportAllFrom(moduleSpecifier);
          case "ExportDefault":
            FunctionDeclarationClassDeclarationExpression body_ed = separateFunctionDeclarationClassDeclarationExpression(deserializeNode(jsonObject.get("body")));
            return new ExportDefault(body_ed);
          case "ExportFrom":
            ImmutableList<ExportSpecifier> namedExports_ef = deserializeList(jsonObject.getAsJsonArray("namedExports"));
            Maybe<String> moduleSpecifier_ef = deserializeMaybeString(jsonObject.get("moduleSpecifier"));
            return new ExportFrom(namedExports_ef, moduleSpecifier_ef);
          case "ExportSpecifier":
            Maybe<String> name_es = deserializeMaybeString(jsonObject.get("name"));
            String exportedName_es = jsonObject.get("exportedName").getAsString();
            return new ExportSpecifier(name_es, exportedName_es);
          case "ExpressionStatement":
            Expression expression_es = (Expression)deserializeNode(jsonObject.get("expression"));
            return new ExpressionStatement(expression_es);
          case "ForInStatement":
          case "ForOfStatement":
          case "ForStatement":
          case "FormalParameters":
          case "FunctionBody":
          case "FunctionDeclaration":
          case "FunctionExpression":
          case "Getter":
          case "IdentifierExpression":
          case "IfStatement":
          case "Import":
          case "ImportNamespace":
          case "ImportSpecifier":
          case "LabeledStatement":
          case "LiteralBooleanExpression":
          case "LiteralInfinityExpression":
          case "LiteralNullExpression":
          case "LiteralNumericExpression":
            double value = jsonObject.get("value").getAsDouble();
            return new LiteralNumericExpression(value);
          case "LiteralRegexExpression":
          case "LiteralStringExpression":
          case "Method":
          case "Module":
          case "NewExpression":
          case "NewTargetExpression":
          case "ObjectBinding":
          case "ObjectExpression":
          case "ReturnStatement":
          case "Script":
            ImmutableList<Directive> directives = deserializeList(jsonObject.getAsJsonArray("directives"));
            ImmutableList<Statement> statements = deserializeList(jsonObject.getAsJsonArray("statements"));
            return new Script(directives, statements);
          case "Setter":
          case "ShorthandProperty":
          case "SpreadElement":
          case "StaticMemberExpression":
          case "StaticPropertyName":
          case "Super":
          case "SwitchCase":
          case "SwitchDefault":
          case "SwitchStatement":
          case "SwitchStatementWithDefault":
          case "TemplateElement":
          case "TemplateExpression":
          case "ThisExpression":
          case "ThrowStatement":
          case "TryCatchStatement":
          case "TryStatement":
          case "UnaryExpression":
          case "UpdateExpression":
          case "VariableDeclaration":
          case "VariableDeclarationStatement":
          case "VariableDeclarator":
          case "WhileStatement":
          case "WithStatement":
          case "YieldExpression":
          case "YieldGeneratorExpression":
        }
      }
    }
    return toReturn;
  }

  /**************************
   * PRIVATE HELPER METHODS *
   **************************/

  private FunctionDeclarationClassDeclarationExpression separateFunctionDeclarationClassDeclarationExpression(Node node) {
    if (node instanceof FunctionDeclaration) {
      return (FunctionDeclaration) node;
    } else if (node instanceof ClassDeclaration) {
      return (ClassDeclaration) node;
    } else /* Expression */ {
      return (Expression) node;
    }
  }
  private FunctionDeclarationClassDeclarationVariableDeclaration separateFunctionDeclarationClassDeclarationVariableDeclaration(Node node) {
    if (node instanceof FunctionDeclaration) {
      return (FunctionDeclaration) node;
    } else if (node instanceof ClassDeclaration) {
      return (ClassDeclaration) node;
    } else /* variable delcaration */ {
      return (VariableDeclaration) node;
    }
  }

  private BindingIdentifierMemberExpression separateBindingIdentifierMemberExpression(Node node) {
    if (node instanceof BindingIdentifier) {
      return (BindingIdentifier) node;
    } else {
      return (MemberExpression) node;
    }
  }

  private Maybe<BindingIdentifier> deserializeMaybeBindingIdentifier(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.getAsString().equals("null")) {
      return Maybe.nothing();
    } else {
      return Maybe.just((BindingIdentifier) deserializeNode(jsonElement));
    }
  }

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
    } else if (node instanceof ObjectBinding) {
      return (ObjectBinding) node;
    } else {
      return (MemberExpression) node;
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

  private CompoundAssignmentOperator deserializeCompoundAssignmentOperator(JsonElement jsonElement) {
    String operatorString = jsonElement.getAsString();
    switch (operatorString) {
      case "+=":
        return CompoundAssignmentOperator.AssignPlus;
      case "-=":
        return CompoundAssignmentOperator.AssignMinus;
      case "*=":
        return CompoundAssignmentOperator.AssignMul;
      case "/=":
        return CompoundAssignmentOperator.AssignDiv;
      case "%=":
        return CompoundAssignmentOperator.AssignRem;
      case "<<=":
        return CompoundAssignmentOperator.AssignLeftShift;
      case ">>=":
        return CompoundAssignmentOperator.AssignRightShift;
      case ">>>=":
        return CompoundAssignmentOperator.AssignUnsignedRightShift;
      case "|=":
        return CompoundAssignmentOperator.AssignBitOr;
      case "^=":
        return CompoundAssignmentOperator.AssignBitXor;
      case "&=":
        return CompoundAssignmentOperator.AssignBitAnd;
      default:
        return null;
    }
  }

}
