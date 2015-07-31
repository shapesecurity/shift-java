package com.shapesecurity.shift.serialization;

import com.google.gson.*;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Deserializer {

  public Deserializer() {
  }

  public Node deserialize(String toDeserialize) throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
    JsonElement json = new JsonParser().parse(toDeserialize);
    return deserializeASTNode(json);
  }

  private Node deserializeASTNode(JsonElement jsonElement) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
    if (jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if (jsonObject.has("type")) {
        String nodeType = jsonObject.get("type").getAsString();
        switch (nodeType) {
          case "ArrayBinding":
            ImmutableList<Maybe<BindingBindingWithDefault>> elements_ab = deserializeMaybeList(jsonObject.getAsJsonArray("elements"));
            Maybe<Binding> restElement_ab = deserializeMaybeBinding(jsonObject.get("restElement"));
            return new ArrayBinding(elements_ab, restElement_ab);
          case "ArrayExpression":
            ImmutableList<Maybe<SpreadElementExpression>> elements_ae = deserializeMaybeList(jsonObject.getAsJsonArray("elements"));
            return new ArrayExpression(elements_ae);
          case "ArrowExpression":
            FormalParameters params = (FormalParameters) deserializeASTNode(jsonObject.get("params"));
            FunctionBodyExpression body = separateFunctionBodyExpression(deserializeASTNode(jsonObject.get("body")));
            return new ArrowExpression(params, body);
          case "AssignmentExpression":
            Binding binding_ae = (Binding) deserializeASTNode(jsonObject.get("binding"));
            Expression expression_ae = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new AssignmentExpression(binding_ae, expression_ae);
          case "BinaryExpression":
            BinaryOperator operator = deserializeBinaryOperator(jsonObject.get("operator"));
            Expression left = (Expression) deserializeASTNode(jsonObject.get("left"));
            Expression right = (Expression) deserializeASTNode(jsonObject.get("right"));
            return new BinaryExpression(operator, left, right);
          case "BindingIdentifier":
            String name = jsonObject.get("name").getAsString();
            return new BindingIdentifier(name);
          case "BindingPropertyIdentifier":
            BindingIdentifier binding_bpr = (BindingIdentifier) deserializeASTNode(jsonObject.get("binding"));
            Maybe<Expression> init_bpi = deserializeMaybeExpression(jsonObject.get("init"));
            return new BindingPropertyIdentifier(binding_bpr, init_bpi);
          case "BindingPropertyProperty":
            PropertyName name_bpp = (PropertyName) deserializeASTNode(jsonObject.get("name"));
            BindingBindingWithDefault binding_bpp = separateBindingBindingWithDefault(deserializeASTNode(jsonObject.get("binding")));
            return new BindingPropertyProperty(name_bpp, binding_bpp);
          case "BindingWithDefault":
            Binding binding_bwd = separateBinding(deserializeASTNode(jsonObject.get("binding")));
            Expression init_bwd = (Expression) deserializeASTNode(jsonObject.get("init"));
            return new BindingWithDefault(binding_bwd, init_bwd);
          case "Block":
            ImmutableList<Statement> statements_b = deserializeList(jsonObject.getAsJsonArray("statements"));
            return new Block(statements_b);
          case "BlockStatement":
            Block block = (Block) deserializeASTNode(jsonObject.get("block"));
            return new BlockStatement(block);
          case "BreakStatement":
            Maybe<String> label = deserializeMaybeString(jsonObject.get("label"));
            return new BreakStatement(label);
          case "CallExpression":
            ExpressionSuper callee = separateExpressionSuper(deserializeASTNode(jsonObject.get("callee")));
            ImmutableList<SpreadElementExpression> arguments = deserializeList(jsonObject.getAsJsonArray("arguments"));
            return new CallExpression(callee, arguments);
          case "CatchClause":
            Binding binding_cc = separateBinding(deserializeASTNode(jsonObject.get("binding")));
            Block block_cc = (Block) deserializeASTNode(jsonObject.get("body"));
            return new CatchClause(binding_cc, block_cc);
          case "ClassDeclaration":
            BindingIdentifier name_cd = (BindingIdentifier) deserializeASTNode(jsonObject.get("name"));
            Maybe<Expression> _super_cd = deserializeMaybeExpression(jsonObject.get("super"));
            ImmutableList<ClassElement> elements_cd = deserializeList(jsonObject.getAsJsonArray("elements"));
            return new ClassDeclaration(name_cd, _super_cd, elements_cd);
          case "ClassElement":
            boolean isStatic = jsonObject.get("isStatic").getAsBoolean();
            MethodDefinition methodDefinition = (MethodDefinition) deserializeASTNode(jsonObject.get("method"));
            return new ClassElement(isStatic, methodDefinition);
          case "ClassExpression":
            Maybe<BindingIdentifier> name_ce = deserializeMaybeBindingIdentifier(jsonObject.get("name"));
            Maybe<Expression> super_ce = deserializeMaybeExpression(jsonObject.get("super"));
            ImmutableList<ClassElement> elements_ce = deserializeList(jsonObject.getAsJsonArray("elements"));
            return new ClassExpression(name_ce, super_ce, elements_ce);
          case "CompoundAssignmentExpression":
            CompoundAssignmentOperator operator_cae = deserializeCompoundAssignmentOperator(jsonObject.get("operator"));
            BindingIdentifierMemberExpression binding_cae = separateBindingIdentifierMemberExpression(deserializeASTNode(jsonObject.get("binding")));
            Expression expression_cae = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new CompoundAssignmentExpression(operator_cae, binding_cae, expression_cae);
          case "ComputedMemberExpression":
            Expression expression_cme = (Expression) deserializeASTNode(jsonObject.get("expression"));
            ExpressionSuper object_cme = separateExpressionSuper(deserializeASTNode(jsonObject.get("object")));
            return new ComputedMemberExpression(expression_cme, object_cme);
          case "ComputedPropertyName":
            Expression expression_cpn = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new ComputedPropertyName(expression_cpn);
          case "ConditionalExpression":
            Expression test = (Expression) deserializeASTNode(jsonObject.get("test"));
            Expression consequent = (Expression) deserializeASTNode(jsonObject.get("consequent"));
            Expression alternate = (Expression) deserializeASTNode(jsonObject.get("alternate"));
            return new ConditionalExpression(test, consequent, alternate);
          case "ContinueStatement":
            Maybe<String> label_cs = deserializeMaybeString(jsonObject.get("label"));
            return new ContinueStatement(label_cs);
          case "DataProperty":
            Expression expression_dp = (Expression) deserializeASTNode(jsonObject.get("expression"));
            PropertyName name_dp = (PropertyName) deserializeASTNode(jsonObject.get("name"));
            return new DataProperty(expression_dp, name_dp);
          case "DebuggerStatement":
            return new DebuggerStatement();
          case "Directive":
            String rawValue = jsonObject.get("rawValue").getAsString();
            return new Directive(rawValue);
          case "DoWhileStatement":
            Expression test_dws = (Expression) deserializeASTNode(jsonObject.get("test"));
            Statement body_dws = (Statement) deserializeASTNode(jsonObject.get("body"));
            return new DoWhileStatement(test_dws, body_dws);
          case "EmptyStatement":
            return new EmptyStatement();
          case "Export":
            FunctionDeclarationClassDeclarationVariableDeclaration declaration = separateFunctionDeclarationClassDeclarationVariableDeclaration(deserializeASTNode(jsonObject.get("declaration")));
            return new Export(declaration);
          case "ExportAllFrom":
            String moduleSpecifier = jsonObject.get("moduleSpecifier").getAsString();
            return new ExportAllFrom(moduleSpecifier);
          case "ExportDefault":
            FunctionDeclarationClassDeclarationExpression body_ed = separateFunctionDeclarationClassDeclarationExpression(deserializeASTNode(jsonObject.get("body")));
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
            Expression expression_es = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new ExpressionStatement(expression_es);
          case "ForInStatement":
            VariableDeclarationBinding left_fis = separateVariableDeclarationBinding(deserializeASTNode(jsonObject.get("left")));
            Expression right_fis = (Expression) deserializeASTNode(jsonObject.get("right"));
            Statement body_fis = (Statement) deserializeASTNode(jsonObject.get("body"));
            return new ForInStatement(left_fis, right_fis, body_fis);
          case "ForOfStatement":
            VariableDeclarationBinding left_fos = separateVariableDeclarationBinding(deserializeASTNode(jsonObject.get("left")));
            Expression right_fos = (Expression) deserializeASTNode(jsonObject.get("right"));
            Statement body_fos = (Statement) deserializeASTNode(jsonObject.get("body"));
            return new ForOfStatement(left_fos, right_fos, body_fos);
          case "ForStatement":
            Maybe<VariableDeclarationExpression> init_fs = deserializeMaybeVariableDeclarationExpression(jsonObject.get("init"));
            Maybe<Expression> test_fs = deserializeMaybeExpression(jsonObject.get("test"));
            Maybe<Expression> update_fs = deserializeMaybeExpression(jsonObject.get("update"));
            Statement body_fs = (Statement) deserializeASTNode(jsonObject.get("body"));
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
            BindingIdentifier name_fd = (BindingIdentifier) deserializeASTNode(jsonObject.get("name"));
            boolean isGenerator_fd = jsonObject.get("isGenerator").getAsBoolean();
            FormalParameters params_fd = (FormalParameters) deserializeASTNode(jsonObject.get("params"));
            FunctionBody body_fd = (FunctionBody) deserializeASTNode(jsonObject.get("body"));
            return new FunctionDeclaration(name_fd, isGenerator_fd, params_fd, body_fd);
          case "FunctionExpression":
            Maybe<BindingIdentifier> name_fe = deserializeMaybeBindingIdentifier(jsonObject.get("name"));
            boolean isGenerator_fe = jsonObject.get("isGenerator").getAsBoolean();
            FormalParameters params_fe = (FormalParameters) deserializeASTNode(jsonObject.get("params"));
            FunctionBody body_fe = (FunctionBody) deserializeASTNode(jsonObject.get("body"));
            return new FunctionExpression(name_fe, isGenerator_fe, params_fe, body_fe);
          case "Getter":
            FunctionBody body_g = (FunctionBody) deserializeASTNode(jsonObject.get("body"));;
            PropertyName name_g = (PropertyName) deserializeASTNode(jsonObject.get("name"));
            return new Getter(body_g, name_g);
          case "IdentifierExpression":
            String name_ie = jsonObject.get("name").getAsString();
            return new IdentifierExpression(name_ie);
          case "IfStatement":
            Expression test_if = (Expression) deserializeASTNode(jsonObject.get("test"));
            Statement consequent_if = (Statement) deserializeASTNode(jsonObject.get("consequent"));
            Maybe<Statement> alternate_if = deserializeMaybeStatement(jsonObject.get("alternate"));
            return new IfStatement(test_if, consequent_if, alternate_if);
          case "Import":
            Maybe<BindingIdentifier> defaultBinding_i = deserializeMaybeBindingIdentifier(jsonObject.get("defaultBinding"));
            ImmutableList<ImportSpecifier> namedImports_i = deserializeList(jsonObject.getAsJsonArray("namedImports"));
            String moduleSpecifier_i = jsonObject.get("moduleSpecifier").getAsString();
            return new Import(defaultBinding_i, namedImports_i, moduleSpecifier_i);
          case "ImportNamespace":
            Maybe<BindingIdentifier> defaultBinding_in = deserializeMaybeBindingIdentifier(jsonObject.get("defaultBinding"));
            BindingIdentifier namespaceBinding_in = (BindingIdentifier) deserializeASTNode(jsonObject.get("namespaceBinding"));
            String moduleSpecifier_in = jsonObject.get("moduleSpecifier").getAsString();;
            return new ImportNamespace(defaultBinding_in, namespaceBinding_in, moduleSpecifier_in);
          case "ImportSpecifier":
            Maybe<String> name_is = deserializeMaybeString(jsonObject.get("name"));
            BindingIdentifier binding_is = (BindingIdentifier) deserializeASTNode(jsonObject.get("binding"));
            return new ImportSpecifier(name_is, binding_is);
          case "LabeledStatement":
            String label_ls = jsonObject.get("label").getAsString();
            Statement body_ls = (Statement) deserializeASTNode(jsonObject.get("body"));
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
            FormalParameters params_m = (FormalParameters) deserializeASTNode(jsonObject.get("params"));
            FunctionBody body_m = (FunctionBody) deserializeASTNode(jsonObject.get("body"));
            PropertyName name_m = (PropertyName) deserializeASTNode(jsonObject.get("name"));
            return new Method(isGenerator_m, params_m, body_m, name_m);
          case "Module":
            ImmutableList<Directive> directives_m = deserializeList(jsonObject.getAsJsonArray("directives"));
            ImmutableList<ImportDeclarationExportDeclarationStatement> items_m = deserializeList(jsonObject.getAsJsonArray("items"));
            return new Module(directives_m, items_m);
          case "NewExpression":
            Expression callee_ne = (Expression) deserializeASTNode(jsonObject.get("callee"));
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
            BindingBindingWithDefault param_s = separateBindingBindingWithDefault(deserializeASTNode(jsonObject.get("param")));
            FunctionBody body_s = (FunctionBody) deserializeASTNode(jsonObject.get("body"));
            PropertyName name_s = (PropertyName) deserializeASTNode(jsonObject.get("name"));
            return new Setter(param_s, body_s, name_s);
          case "ShorthandProperty":
            String name_sp = jsonObject.get("name").getAsString();
            return new ShorthandProperty(name_sp);
          case "SpreadElement":
            Expression expression_se = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new SpreadElement(expression_se);
          case "StaticMemberExpression":
            String property_sme = jsonObject.get("property").getAsString();
            ExpressionSuper object_sme = separateExpressionSuper(deserializeASTNode(jsonObject.get("object")));
            return new StaticMemberExpression(property_sme, object_sme);
          case "StaticPropertyName":
            String value_spn = jsonObject.get("value").getAsString();
            return new StaticPropertyName(value_spn);
          case "Super":
            return new Super();
          case "SwitchCase":
            Expression test_sc = (Expression) deserializeASTNode(jsonObject.get("test"));
            ImmutableList<Statement> consequent_sc = deserializeList(jsonObject.getAsJsonArray("consequent"));
            return new SwitchCase(test_sc, consequent_sc);
          case "SwitchDefault":
            ImmutableList<Statement> consequent_sd = deserializeList(jsonObject.getAsJsonArray("consequent"));
            return new SwitchDefault(consequent_sd);
          case "SwitchStatement":
            Expression discriminant_ss = (Expression) deserializeASTNode(jsonObject.get("discriminant"));
            ImmutableList<SwitchCase> cases_ss = deserializeList(jsonObject.getAsJsonArray("cases"));
            return new SwitchStatement(discriminant_ss, cases_ss);
          case "SwitchStatementWithDefault":
            Expression discriminant_sswd = (Expression) deserializeASTNode(jsonObject.get("discriminant"));
            ImmutableList<SwitchCase> preDefaultCases_sswd = deserializeList(jsonObject.getAsJsonArray("preDefaultCases"));
            SwitchDefault defaultCase_sswd = (SwitchDefault) deserializeASTNode(jsonObject.get("defaultCase"));
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
            Expression expression_ts = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new ThrowStatement(expression_ts);
          case "TryCatchStatement":
            Block body_tcs = (Block) deserializeASTNode(jsonObject.get("body"));
            CatchClause catchClause_tcs = (CatchClause) deserializeASTNode(jsonObject.get("catchClause"));
            return new TryCatchStatement(body_tcs, catchClause_tcs);
          case "TryFinallyStatement":
            Block body_tfs = (Block) deserializeASTNode(jsonObject.get("body"));
            Maybe<CatchClause> catchClause_tfs = deserializeMaybeCatchClause(jsonObject.get("catchClause"));
            Block finalizer_tfs = (Block) deserializeASTNode(jsonObject.get("finalizer"));
            return new TryFinallyStatement(body_tfs, catchClause_tfs, finalizer_tfs);
          case "UnaryExpression":
            UnaryOperator operator_ue = deserializeUnaryOperator(jsonObject.get("operator"));
            Expression operand_ue = (Expression) deserializeASTNode(jsonObject.get("operand"));
            return new UnaryExpression(operator_ue, operand_ue);
          case "UpdateExpression":
            boolean isPrefix_upe = jsonObject.get("isPrefix").getAsBoolean();
            UpdateOperator operator_upe = deserializeUpdateOperator(jsonObject.get("operator"));
            BindingIdentifierMemberExpression operand_upe = separateBindingIdentifierMemberExpression(deserializeASTNode(jsonObject.get("operand")));
            return new UpdateExpression(isPrefix_upe, operator_upe, operand_upe);
          case "VariableDeclaration":
            VariableDeclarationKind kind_vd = deserializeVariableDeclarationKind(jsonObject.get("kind"));
            ImmutableList<VariableDeclarator> declarators_vd = deserializeList(jsonObject.getAsJsonArray("declarators"));
            return new VariableDeclaration(kind_vd, declarators_vd);
          case "VariableDeclarationStatement":
            VariableDeclaration declaration_vds = (VariableDeclaration) deserializeASTNode(jsonObject.get("declaration"));
            return new VariableDeclarationStatement(declaration_vds);
          case "VariableDeclarator":
            Binding binding_vd = separateBinding(deserializeASTNode(jsonObject.get("binding")));
            Maybe<Expression> init_vd = deserializeMaybeExpression(jsonObject.get("init"));
            return new VariableDeclarator(binding_vd, init_vd);
          case "WhileStatement":
            Expression test_ws = (Expression) deserializeASTNode(jsonObject.get("test"));
            Statement body_ws = (Statement) deserializeASTNode(jsonObject.get("body"));
            return new WhileStatement(test_ws, body_ws);
          case "WithStatement":
            Expression object_wis = (Expression) deserializeASTNode(jsonObject.get("object"));
            Statement body_wis = (Statement) deserializeASTNode(jsonObject.get("body"));
            return new WithStatement(object_wis, body_wis);
          case "YieldExpression":
            Maybe<Expression> expression_ye = deserializeMaybeExpression(jsonObject.get("expression"));
            return new YieldExpression(expression_ye);
          case "YieldGeneratorExpression":
            Expression expression_yge = (Expression) deserializeASTNode(jsonObject.get("expression"));
            return new YieldGeneratorExpression(expression_yge);
        }
      }
    }
    return null; // should not get here
  }

  /**************************
   * PRIVATE HELPER METHODS *
   **************************/

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
        return null; // should not get here
    }
  }

  private <A> ImmutableList<A> deserializeList(JsonArray jsonArray) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonArray.size() == 0) {
      return ImmutableList.nil();
    } else {
      ArrayList<A> deserializedElements = new ArrayList<>();
      for (JsonElement jsonElement : jsonArray) {
        A deserializedElement = (A) deserializeASTNode(jsonElement);
        deserializedElements.add(deserializedElement);
      }
      return ImmutableList.from(deserializedElements);
    }
  }

  private <A> ImmutableList<Maybe<A>> deserializeMaybeList(JsonArray jsonArray) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonArray.size() == 0) {
      return ImmutableList.nil();
    } else {
      ArrayList<Maybe<A>> deserializedElements = new ArrayList<>();
      for (JsonElement jsonElement : jsonArray) {
        if (jsonElement.isJsonNull()) {
          deserializedElements.add(Maybe.nothing());
        } else {
          deserializedElements.add(Maybe.just((A) deserializeASTNode(jsonElement)));
        }
      }
      return ImmutableList.from(deserializedElements);
    }
  }

  private Maybe<Binding> deserializeMaybeBinding(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    }
    Node node = deserializeASTNode(jsonElement);
    return Maybe.just(separateBinding(node));
  }

  private Maybe<BindingIdentifier> deserializeMaybeBindingIdentifier(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    } else {
      return Maybe.just((BindingIdentifier) deserializeASTNode(jsonElement));
    }
  }

  private Maybe<CatchClause> deserializeMaybeCatchClause(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    } else {
      return Maybe.just((CatchClause) deserializeASTNode(jsonElement));
    }
  }

  private Maybe<Expression> deserializeMaybeExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    } else {
      return Maybe.just((Expression) deserializeASTNode(jsonElement));
    }
  }

  private Maybe<Statement> deserializeMaybeStatement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    } else {
      return Maybe.just((Statement) deserializeASTNode(jsonElement));
    }
  }

  private Maybe<String> deserializeMaybeString(JsonElement jsonElement) {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    } else {
      return Maybe.just(jsonElement.getAsString());
    }
  }

  private Maybe<VariableDeclarationExpression> deserializeMaybeVariableDeclarationExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (jsonElement.isJsonNull()) {
      return Maybe.nothing();
    } else {
      Node node = deserializeASTNode(jsonElement);
      return Maybe.just(separateVariableDeclarationExpression(node));
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
      case "--":
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

  private BindingBindingWithDefault separateBindingBindingWithDefault(Node node) {
    if (node instanceof BindingWithDefault) {
      return (BindingWithDefault) node;
    } else {
      return separateBinding(node);
    }
  }

  private BindingIdentifierMemberExpression separateBindingIdentifierMemberExpression(Node node) {
    if (node instanceof BindingIdentifier) {
      return (BindingIdentifier) node;
    } else {
      return (MemberExpression) node;
    }
  }

  private ExpressionSuper separateExpressionSuper(Node node) {
    if (node instanceof Expression) {
      return (Expression) node;
    } else {
      return (Super) node;
    }
  }

  private FunctionBodyExpression separateFunctionBodyExpression(Node node) {
    if (node instanceof FunctionBody) {
      return (FunctionBody) node;
    } else {
      return (Expression) node;
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

  private VariableDeclarationBinding separateVariableDeclarationBinding(Node node) {
    if (node instanceof VariableDeclaration) {
      return (VariableDeclaration) node;
    } else {
      return separateBinding(node);
    }
  }

  private VariableDeclarationExpression separateVariableDeclarationExpression(Node node) {
    if (node instanceof VariableDeclaration) {
      return (VariableDeclaration) node;
    } else {
      return (Expression) node;
    }
  }

}
