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
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.sun.java.swing.plaf.motif.MotifBorders;
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
            VariableDeclarationBinding left_fis = separateVariableDeclarationBinding(deserializeNode(jsonObject.get("left")));
            Expression right_fis = (Expression)deserializeNode(jsonObject.get("right"));
            Statement body_fis = (Statement)deserializeNode(jsonObject.get("body"));
            return new ForInStatement(left_fis, right_fis, body_fis);
          case "ForOfStatement":
            VariableDeclarationBinding left_fos = separateVariableDeclarationBinding(deserializeNode(jsonObject.get("left")));
            Expression right_fos = (Expression)deserializeNode(jsonObject.get("right"));
            Statement body_fos = (Statement)deserializeNode(jsonObject.get("body"));
            return new ForOfStatement(left_fos, right_fos, body_fos);
          case "ForStatement":
            Maybe<VariableDeclarationExpression> init_fs = deserializeMaybeVariableDeclarationExpression(jsonObject.get("init"));
            Maybe<Expression> test_fs = deserializeMaybeExpression(jsonObject.get("test"));
            Maybe<Expression> update_fs = deserializeMaybeExpression(jsonObject.get("update"));
            Statement body_fs = (Statement)deserializeNode(jsonObject.get("body"));
            return new ForStatement(init_fs, test_fs, update_fs, body_fs);
          case "FormalParameters":
            ImmutableList<BindingBindingWithDefault> items_fp = deserializeList(jsonObject.getAsJsonArray("items"));
            Maybe<BindingIdentifier> rest_fp = deserializeMaybeBindingIdentifier(jsonObject.get("rest"));
            return new FormalParameters(items_fp, rest_fp);
          case "FunctionBody":
            ImmutableList<Directive> directives_fb = deserializeList(jsonObject.getAsJsonArray("directives"));
            ImmutableList<Statement> statements_fb = deserializeList(jsonObject.getAsJsonArray("statements"));
            return new FunctionBody(directives_fb, statements_fb);
          case "FunctionDeclaration":
            BindingIdentifier name_fd = (BindingIdentifier)deserializeNode(jsonObject.get("name"));
            boolean isGenerator_fd = jsonObject.get("isGenerator").getAsBoolean();
            FormalParameters params_fd = (FormalParameters)deserializeNode(jsonObject.get("params"));
            FunctionBody body_fd = (FunctionBody)deserializeNode(jsonObject.get("body"));
            return new FunctionDeclaration(name_fd, isGenerator_fd, params_fd, body_fd);
          case "FunctionExpression":
            Maybe<BindingIdentifier> name_fe = deserializeMaybeBindingIdentifier(jsonObject.get("name"));
            boolean isGenerator_fe = jsonObject.get("isGenerator").getAsBoolean();
            FormalParameters params_fe = (FormalParameters)deserializeNode(jsonObject.get("params"));
            FunctionBody body_fe = (FunctionBody)deserializeNode(jsonObject.get("body"));
            return new FunctionExpression(name_fe, isGenerator_fe, params_fe, body_fe);
          case "Getter":
            FunctionBody body_g = (FunctionBody)deserializeNode(jsonObject.get("body"));;
            PropertyName name_g = (PropertyName)deserializeNode(jsonObject.get("name"));
            return new Getter(body_g, name_g);
          case "IdentifierExpression":
            String name_ie = jsonObject.get("name").getAsString();
            return new IdentifierExpression(name_ie);
          case "IfStatement":
            Expression test_if = (Expression)deserializeNode(jsonObject.get("test"));
            Statement consequent_if = (Statement)deserializeNode(jsonObject.get("consequent"));
            Maybe<Statement> alternate_if = deserializeMaybeStatement(jsonObject.get("alternate"));
            return new IfStatement(test_if, consequent_if, alternate_if);
          case "Import":
            Maybe<BindingIdentifier> defaultBinding_i = deserializeMaybeBindingIdentifier(jsonObject.get("defaultBinding"));
            ImmutableList<ImportSpecifier> namedImports_i = deserializeList(jsonObject.getAsJsonArray("namedImports"));
            String moduleSpecifier_i = jsonObject.get("moduleSpecifier").getAsString();
            return new Import(defaultBinding_i, namedImports_i, moduleSpecifier_i);
          case "ImportNamespace":
            Maybe<BindingIdentifier> defaultBinding_in = deserializeMaybeBindingIdentifier(jsonObject.get("defaultBinding"));
            BindingIdentifier namespaceBinding_in = (BindingIdentifier)deserializeNode(jsonObject.get("namespaceBinding"));
            String moduleSpecifier_in = jsonObject.get("moduleSpecifier").getAsString();;
            return new ImportNamespace(defaultBinding_in, namespaceBinding_in, moduleSpecifier_in);
          case "ImportSpecifier":
            Maybe<String> name_is = deserializeMaybeString(jsonObject.get("name"));
            BindingIdentifier binding_is = (BindingIdentifier)deserializeNode(jsonObject.get("binding"));
            return new ImportSpecifier(name_is, binding_is);
          case "LabeledStatement":
            String label_ls = jsonObject.get("label").getAsString();
            Statement body_ls = (Statement)deserializeNode(jsonObject.get("body"));
            return new LabeledStatement(label_ls, body_ls);
          case "LiteralBooleanExpression":
            boolean value_lbe = jsonObject.get("value").getAsBoolean();
            return new LiteralBooleanExpression(value_lbe);
          case "LiteralInfinityExpression":
            return new LiteralInfinityExpression();
          case "LiteralNullExpression":
            return new LiteralNullExpression();
          case "LiteralNumericExpression":
            double value = jsonObject.get("value").getAsDouble();
            return new LiteralNumericExpression(value);
          case "LiteralRegexExpression":
            String pattern_lre = jsonObject.get("pattern").getAsString();
            String flags_lre = jsonObject.get("flags").getAsString();
            return new LiteralRegExpExpression(pattern_lre, flags_lre);
          case "LiteralStringExpression":
            String value_lse = jsonObject.get("value").getAsString();
            return new LiteralStringExpression(value_lse);
          case "Method":
            boolean isGenerator_m = jsonObject.get("isGenerator").getAsBoolean();
            FormalParameters params_m = (FormalParameters)deserializeNode(jsonObject.get("params"));
            FunctionBody body_m = (FunctionBody)deserializeNode(jsonObject.get("body"));
            PropertyName name_m = (PropertyName)deserializeNode(jsonObject.get("name"));
            return new Method(isGenerator_m, params_m, body_m, name_m);
          case "Module":
            ImmutableList<Directive> directives_m = deserializeList(jsonObject.getAsJsonArray("directives"));
            ImmutableList<ImportDeclarationExportDeclarationStatement> items_m = deserializeList(jsonObject.getAsJsonArray("items"));
            return new Module(directives_m, items_m);
          case "NewExpression":
            Expression callee_ne = (Expression)deserializeNode(jsonObject.get("callee"));
            ImmutableList<SpreadElementExpression> arguments_ne = deserializeList(jsonObject.getAsJsonArray("arguments"));
            return new NewExpression(callee_ne, arguments_ne);
          case "NewTargetExpression":
            return new NewTargetExpression();
          case "ObjectBinding":
            ImmutableList<BindingProperty> properties_ob = deserializeList(jsonObject.getAsJsonArray("properties"));
            return new ObjectBinding(properties_ob);
          case "ObjectExpression":
            ImmutableList<ObjectProperty> properties_oe = deserializeList(jsonObject.getAsJsonArray("properties"));
            return new ObjectExpression(properties_oe);
          case "ReturnStatement":
            Maybe<Expression> expression_rs = deserializeMaybeExpression(jsonObject.get("expression"));
            return new ReturnStatement(expression_rs);
          case "Script":
            ImmutableList<Directive> directives = deserializeList(jsonObject.getAsJsonArray("directives"));
            ImmutableList<Statement> statements = deserializeList(jsonObject.getAsJsonArray("statements"));
            return new Script(directives, statements);
          case "Setter":
            BindingBindingWithDefault params_s = separateBindingBindingWithDefault(deserializeNode(jsonObject.get("params")));
            FunctionBody body_s = (FunctionBody)deserializeNode(jsonObject.get("body"));
            PropertyName name_s = (PropertyName)deserializeNode(jsonObject.get("name"));
            return new Setter(params_s, body_s, name_s);
          case "ShorthandProperty":
            String name_sp = jsonObject.get("name").getAsString();
            return new ShorthandProperty(name_sp);
          case "SpreadElement":
            Expression expression_se = (Expression)deserializeNode(jsonObject.get("expression"));
            return new SpreadElement(expression_se);
          case "StaticMemberExpression":
            String property_sme = jsonObject.get("property").getAsString();
            ExpressionSuper object_sme = separateExpressionSuper(deserializeNode(jsonObject.get("object")));
            return new StaticMemberExpression(property_sme, object_sme);
          case "StaticPropertyName":
            String value_spn = jsonObject.get("value").getAsString();
            return new StaticPropertyName(value_spn);
          case "Super":
            return new Super();
          case "SwitchCase":
            Expression test_sc = (Expression)deserializeNode(jsonObject.get("test"));
            ImmutableList<Statement> consequent_sc = deserializeList(jsonObject.getAsJsonArray("consequent"));
            return new SwitchCase(test_sc, consequent_sc);
          case "SwitchDefault":
            ImmutableList<Statement> consequent_sd = deserializeList(jsonObject.getAsJsonArray("consequent"));
            return new SwitchDefault(consequent_sd);
          case "SwitchStatement":
            Expression discriminant_ss = (Expression)deserializeNode(jsonObject.get("discriminant"));
            ImmutableList<SwitchCase> cases_ss = deserializeList(jsonObject.getAsJsonArray("cases"));
            return new SwitchStatement(discriminant_ss, cases_ss);
          case "SwitchStatementWithDefault":
            Expression discriminant_sswd = (Expression)deserializeNode(jsonObject.get("discriminant"));
            ImmutableList<SwitchCase> preDefaultCases_sswd = deserializeList(jsonObject.getAsJsonArray("preDefaultCases"));
            SwitchDefault defaultCase_sswd = (SwitchDefault)deserializeNode(jsonObject.get("defaultCase"));
            ImmutableList<SwitchCase> postDefaultCases_sswd = deserializeList(jsonObject.getAsJsonArray("postDefaultCases"));
            return new SwitchStatementWithDefault(discriminant_sswd, preDefaultCases_sswd, defaultCase_sswd, postDefaultCases_sswd);
          case "TemplateElement":
            String rawValue_te = jsonObject.get("rawValue").getAsString();
            return new TemplateElement(rawValue_te);
          case "TemplateExpression":
            Maybe<Expression> tag_te = deserializeMaybeExpression(jsonObject.get("tag"));
            ImmutableList<ExpressionTemplateElement> elements_te = deserializeList(jsonObject.getAsJsonArray("elements"));
            return new TemplateExpression(tag_te, elements_te);
          case "ThisExpression":
            return new ThisExpression();
          case "ThrowStatement":
            Expression expression_ts = (Expression)deserializeNode(jsonObject.get("expression"));
            return new ThrowStatement(expression_ts);
          case "TryCatchStatement":
            Block body_tcs = (Block)deserializeNode(jsonObject.get("body"));
            CatchClause catchClause_tcs = (CatchClause)deserializeNode(jsonObject.get("catchClause"));
            return new TryCatchStatement(body_tcs, catchClause_tcs);
          case "TryFinallyStatement":
            Block body_tfs = (Block)deserializeNode(jsonObject.get("body"));
            Maybe<CatchClause> catchClause_tfs = deserializeMaybeCatchClause(jsonObject.get("catchClause"));
            Block finalizer_tfs = (Block)deserializeNode(jsonObject.get("finalizer"));
            return new TryFinallyStatement(body_tfs, catchClause_tfs, finalizer_tfs);
          case "UnaryExpression":
            UnaryOperator operator_ue = deserializeUnaryOperator(jsonObject.get("operator"));
            Expression operand_ue = (Expression)deserializeNode(jsonObject.get("operand"));
            return new UnaryExpression(operator_ue, operand_ue);
          case "UpdateExpression":
            boolean isPrefix_upe = jsonObject.get("isPrefix").getAsBoolean();
            UpdateOperator operator_upe = deserializeUpdateOperator(jsonObject.get("operator"));
            BindingIdentifierMemberExpression operand_upe = separateBindingIdentifierMemberExpression(deserializeNode(jsonObject.get("operand")));
            return new UpdateExpression(isPrefix_upe, operator_upe, operand_upe);
          case "VariableDeclaration":
            VariableDeclarationKind kind_vd = deserializeVariableDeclarationKind(jsonObject.get("kind"));
            ImmutableList<VariableDeclarator> declarators_vd = deserializeList(jsonObject.getAsJsonArray("declarators"));
            return new VariableDeclaration(kind_vd, declarators_vd);
          case "VariableDeclarationStatement":
            VariableDeclaration declaration_vds = (VariableDeclaration)deserializeNode(jsonObject.get("declaration"));
            return new VariableDeclarationStatement(declaration_vds);
          case "VariableDeclarator":
            Binding binding_vd = separateBinding(deserializeNode(jsonObject.get("binding")));
            Maybe<Expression> init_vd = deserializeMaybeExpression(jsonObject.get("init"));
            return new VariableDeclarator(binding_vd, init_vd);
          case "WhileStatement":
            Expression test_ws = (Expression)deserializeNode(jsonObject.get("expression"));
            Statement body_ws = (Statement)deserializeNode(jsonObject.get("body"));
            return new WhileStatement(test_ws, body_ws);
          case "WithStatement":
            Expression object_wis = (Expression)deserializeNode(jsonObject.get("object"));
            Statement body_wis = (Statement)deserializeNode(jsonObject.get("body"));
            return new WithStatement(object_wis, body_wis);
          case "YieldExpression":
            Maybe<Expression> expression_ye = deserializeMaybeExpression(jsonObject.get("expression"));
            return new YieldExpression(expression_ye);
          case "YieldGeneratorExpression":
            Expression expression_yge = (Expression)deserializeNode(jsonObject.get("expression"));
            return new YieldGeneratorExpression(expression_yge);
        }
      }
    }
    return toReturn;
  }

  /**************************
   * PRIVATE HELPER METHODS *
   **************************/

  private Maybe<CatchClause> deserializeMaybeCatchClause(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.getAsString().equals("null")) {
      return Maybe.nothing();
    } else {
      return Maybe.just((CatchClause) deserializeNode(jsonElement));
    }
  }
  private Maybe<Statement> deserializeMaybeStatement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.getAsString().equals("null")) {
      return Maybe.nothing();
    } else {
      return Maybe.just((Statement) deserializeNode(jsonElement));
    }
  }

  private Maybe<VariableDeclarationExpression> deserializeMaybeVariableDeclarationExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.getAsString().equals("null")) {
      return Maybe.nothing();
    } else {
      Node node = deserializeNode(jsonElement);
      return Maybe.just(separateVariableDeclarationExpression(node));
    }
  }

  private VariableDeclarationExpression separateVariableDeclarationExpression(Node node) {
    if (node instanceof VariableDeclaration) {
      return (VariableDeclaration) node;
    } else {
      return (Expression) node;
    }
  }

  private VariableDeclarationBinding separateVariableDeclarationBinding(Node node) {
    if (node instanceof VariableDeclaration) {
      return (VariableDeclaration) node;
    } else {
      return separateBinding(node);
    }
  }
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

  private UnaryOperator deserializeUnaryOperator(JsonElement jsonElement) {
    String operatorString = jsonElement.getAsString();
    switch (operatorString) {
      case "+":
        return UnaryOperator.Plus;
      case "-":
        return UnaryOperator.Minus;
      case "!":
        return UnaryOperator.LogicalNot;
      case "~":
        return UnaryOperator.BitNot;
      case "typeof":
        return UnaryOperator.Typeof;
      case "void":
        return UnaryOperator.Void;
      case "delete":
        return UnaryOperator.Delete;
      default:
        return null;
    }
  }

  private UpdateOperator deserializeUpdateOperator(JsonElement jsonElement) {
    String operatorString = jsonElement.getAsString();
    switch (operatorString) {
      case "++":
        return UpdateOperator.Increment;
      case "":
        return UpdateOperator.Decrement;
      default:
        return null;
    }
  }

  private VariableDeclarationKind deserializeVariableDeclarationKind(JsonElement jsonElement) {
    String kindString = jsonElement.getAsString();
    switch (kindString) {
      case "var":
        return VariableDeclarationKind.Var;
      case "const":
        return VariableDeclarationKind.Const;
      case "let":
        return VariableDeclarationKind.Let;
      default:
        return null;
    }
  }


}
