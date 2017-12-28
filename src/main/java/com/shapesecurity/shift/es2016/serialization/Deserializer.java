package com.shapesecurity.shift.es2016.serialization;

import com.google.gson.*;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.*;
import com.shapesecurity.shift.es2016.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2016.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es2016.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es2016.ast.operators.UpdateOperator;

import org.json.JSONException;

import java.util.ArrayList;

public class Deserializer {

    protected Deserializer() {}

    public static Node deserialize(String toDeserialize) throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        JsonElement json = new JsonParser().parse(toDeserialize);
        return new Deserializer().deserializeNode(json);
    }

    protected BinaryOperator deserializeBinaryOperator(JsonElement jsonElement) {
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
                return BinaryOperator.BitwiseAnd;
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
            case "**":
                return BinaryOperator.Exp;
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

    protected CompoundAssignmentOperator deserializeCompoundAssignmentOperator(JsonElement jsonElement) {
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
            case "**=":
                return CompoundAssignmentOperator.AssignExp;
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

    protected UnaryOperator deserializeUnaryOperator(JsonElement jsonElement) {
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

    protected UpdateOperator deserializeUpdateOperator(JsonElement jsonElement) {
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

    protected VariableDeclarationKind deserializeVariableDeclarationKind(JsonElement jsonElement) {
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

    protected ImmutableList<Maybe<AssignmentTargetAssignmentTargetWithDefault>> deserializeListMaybeAssignmentTargetAssignmentTargetWithDefault(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Maybe<AssignmentTargetAssignmentTargetWithDefault>> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                if (ele.isJsonNull()) {
                    deserializedElements.add(Maybe.empty());
                } else {
                    deserializedElements.add(Maybe.of((AssignmentTargetAssignmentTargetWithDefault) deserializeNode(ele)));
                }
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<AssignmentTarget> deserializeMaybeAssignmentTarget(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((AssignmentTarget) deserializeNode(jsonElement));
    }

    protected ImmutableList<Maybe<BindingBindingWithDefault>> deserializeListMaybeBindingBindingWithDefault(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Maybe<BindingBindingWithDefault>> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                if (ele.isJsonNull()) {
                    deserializedElements.add(Maybe.empty());
                } else {
                    deserializedElements.add(Maybe.of((BindingBindingWithDefault) deserializeNode(ele)));
                }
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<Binding> deserializeMaybeBinding(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((Binding) deserializeNode(jsonElement));
    }

    protected ImmutableList<Maybe<SpreadElementExpression>> deserializeListMaybeSpreadElementExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Maybe<SpreadElementExpression>> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                if (ele.isJsonNull()) {
                    deserializedElements.add(Maybe.empty());
                } else {
                    deserializedElements.add(Maybe.of((SpreadElementExpression) deserializeNode(ele)));
                }
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<Expression> deserializeMaybeExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((Expression) deserializeNode(jsonElement));
    }

    protected ImmutableList<Statement> deserializeListStatement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Statement> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                Statement deserializedElement = (Statement) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<String> deserializeMaybeString(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        } else {
            return Maybe.of(jsonElement.getAsString());
        }
    }

    protected ImmutableList<SpreadElementExpression> deserializeListSpreadElementExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<SpreadElementExpression> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                SpreadElementExpression deserializedElement = (SpreadElementExpression) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<ClassElement> deserializeListClassElement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ClassElement> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ClassElement deserializedElement = (ClassElement) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<BindingIdentifier> deserializeMaybeBindingIdentifier(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((BindingIdentifier) deserializeNode(jsonElement));
    }

    protected ImmutableList<ExportFromSpecifier> deserializeListExportFromSpecifier(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ExportFromSpecifier> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ExportFromSpecifier deserializedElement = (ExportFromSpecifier) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<ExportLocalSpecifier> deserializeListExportLocalSpecifier(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ExportLocalSpecifier> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ExportLocalSpecifier deserializedElement = (ExportLocalSpecifier) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<VariableDeclarationExpression> deserializeMaybeVariableDeclarationExpression(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((VariableDeclarationExpression) deserializeNode(jsonElement));
    }

    protected ImmutableList<Parameter> deserializeListParameter(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Parameter> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                Parameter deserializedElement = (Parameter) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<Directive> deserializeListDirective(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Directive> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                Directive deserializedElement = (Directive) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<Statement> deserializeMaybeStatement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((Statement) deserializeNode(jsonElement));
    }

    protected ImmutableList<ImportSpecifier> deserializeListImportSpecifier(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ImportSpecifier> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ImportSpecifier deserializedElement = (ImportSpecifier) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<ImportDeclarationExportDeclarationStatement> deserializeListImportDeclarationExportDeclarationStatement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ImportDeclarationExportDeclarationStatement> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ImportDeclarationExportDeclarationStatement deserializedElement = (ImportDeclarationExportDeclarationStatement) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<AssignmentTargetProperty> deserializeListAssignmentTargetProperty(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<AssignmentTargetProperty> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                AssignmentTargetProperty deserializedElement = (AssignmentTargetProperty) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<BindingProperty> deserializeListBindingProperty(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<BindingProperty> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                BindingProperty deserializedElement = (BindingProperty) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<ObjectProperty> deserializeListObjectProperty(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ObjectProperty> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ObjectProperty deserializedElement = (ObjectProperty) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<SwitchCase> deserializeListSwitchCase(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<SwitchCase> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                SwitchCase deserializedElement = (SwitchCase) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected ImmutableList<ExpressionTemplateElement> deserializeListExpressionTemplateElement(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<ExpressionTemplateElement> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ExpressionTemplateElement deserializedElement = (ExpressionTemplateElement) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }

    protected Maybe<CatchClause> deserializeMaybeCatchClause(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((CatchClause) deserializeNode(jsonElement));
    }

    protected ImmutableList<VariableDeclarator> deserializeListVariableDeclarator(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<VariableDeclarator> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                VariableDeclarator deserializedElement = (VariableDeclarator) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }


    protected Node deserializeNode(JsonElement jsonElement) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("type")) {
                String nodeType = jsonObject.get("type").getAsString();
                switch (nodeType) {
                    case "ArrayAssignmentTarget":
                        return new ArrayAssignmentTarget(deserializeListMaybeAssignmentTargetAssignmentTargetWithDefault(jsonObject.get("elements")), deserializeMaybeAssignmentTarget(jsonObject.get("rest")));
                    case "ArrayBinding":
                        return new ArrayBinding(deserializeListMaybeBindingBindingWithDefault(jsonObject.get("elements")), deserializeMaybeBinding(jsonObject.get("rest")));
                    case "ArrayExpression":
                        return new ArrayExpression(deserializeListMaybeSpreadElementExpression(jsonObject.get("elements")));
                    case "ArrowExpression":
                        return new ArrowExpression((FormalParameters) deserializeNode(jsonObject.get("params")), (FunctionBodyExpression) deserializeNode(jsonObject.get("body")));
                    case "AssignmentExpression":
                        return new AssignmentExpression((AssignmentTarget) deserializeNode(jsonObject.get("binding")), (Expression) deserializeNode(jsonObject.get("expression")));
                    case "AssignmentTargetIdentifier":
                        return new AssignmentTargetIdentifier(jsonObject.get("name").getAsString());
                    case "AssignmentTargetPropertyIdentifier":
                        return new AssignmentTargetPropertyIdentifier((AssignmentTargetIdentifier) deserializeNode(jsonObject.get("binding")), deserializeMaybeExpression(jsonObject.get("init")));
                    case "AssignmentTargetPropertyProperty":
                        return new AssignmentTargetPropertyProperty((PropertyName) deserializeNode(jsonObject.get("name")), (AssignmentTargetAssignmentTargetWithDefault) deserializeNode(jsonObject.get("binding")));
                    case "AssignmentTargetWithDefault":
                        return new AssignmentTargetWithDefault((AssignmentTarget) deserializeNode(jsonObject.get("binding")), (Expression) deserializeNode(jsonObject.get("init")));
                    case "BinaryExpression":
                        return new BinaryExpression((Expression) deserializeNode(jsonObject.get("left")), deserializeBinaryOperator(jsonObject.get("operator")), (Expression) deserializeNode(jsonObject.get("right")));
                    case "BindingIdentifier":
                        return new BindingIdentifier(jsonObject.get("name").getAsString());
                    case "BindingPropertyIdentifier":
                        return new BindingPropertyIdentifier((BindingIdentifier) deserializeNode(jsonObject.get("binding")), deserializeMaybeExpression(jsonObject.get("init")));
                    case "BindingPropertyProperty":
                        return new BindingPropertyProperty((PropertyName) deserializeNode(jsonObject.get("name")), (BindingBindingWithDefault) deserializeNode(jsonObject.get("binding")));
                    case "BindingWithDefault":
                        return new BindingWithDefault((Binding) deserializeNode(jsonObject.get("binding")), (Expression) deserializeNode(jsonObject.get("init")));
                    case "Block":
                        return new Block(deserializeListStatement(jsonObject.get("statements")));
                    case "BlockStatement":
                        return new BlockStatement((Block) deserializeNode(jsonObject.get("block")));
                    case "BreakStatement":
                        return new BreakStatement(deserializeMaybeString(jsonObject.get("label")));
                    case "CallExpression":
                        return new CallExpression((ExpressionSuper) deserializeNode(jsonObject.get("callee")), deserializeListSpreadElementExpression(jsonObject.get("arguments")));
                    case "CatchClause":
                        return new CatchClause((Binding) deserializeNode(jsonObject.get("binding")), (Block) deserializeNode(jsonObject.get("body")));
                    case "ClassDeclaration":
                        return new ClassDeclaration((BindingIdentifier) deserializeNode(jsonObject.get("name")), deserializeMaybeExpression(jsonObject.get("super")), deserializeListClassElement(jsonObject.get("elements")));
                    case "ClassElement":
                        return new ClassElement(jsonObject.get("isStatic").getAsBoolean(), (MethodDefinition) deserializeNode(jsonObject.get("method")));
                    case "ClassExpression":
                        return new ClassExpression(deserializeMaybeBindingIdentifier(jsonObject.get("name")), deserializeMaybeExpression(jsonObject.get("super")), deserializeListClassElement(jsonObject.get("elements")));
                    case "CompoundAssignmentExpression":
                        return new CompoundAssignmentExpression((SimpleAssignmentTarget) deserializeNode(jsonObject.get("binding")), deserializeCompoundAssignmentOperator(jsonObject.get("operator")), (Expression) deserializeNode(jsonObject.get("expression")));
                    case "ComputedMemberAssignmentTarget":
                        return new ComputedMemberAssignmentTarget((ExpressionSuper) deserializeNode(jsonObject.get("object")), (Expression) deserializeNode(jsonObject.get("expression")));
                    case "ComputedMemberExpression":
                        return new ComputedMemberExpression((ExpressionSuper) deserializeNode(jsonObject.get("object")), (Expression) deserializeNode(jsonObject.get("expression")));
                    case "ComputedPropertyName":
                        return new ComputedPropertyName((Expression) deserializeNode(jsonObject.get("expression")));
                    case "ConditionalExpression":
                        return new ConditionalExpression((Expression) deserializeNode(jsonObject.get("test")), (Expression) deserializeNode(jsonObject.get("consequent")), (Expression) deserializeNode(jsonObject.get("alternate")));
                    case "ContinueStatement":
                        return new ContinueStatement(deserializeMaybeString(jsonObject.get("label")));
                    case "DataProperty":
                        return new DataProperty((PropertyName) deserializeNode(jsonObject.get("name")), (Expression) deserializeNode(jsonObject.get("expression")));
                    case "DebuggerStatement":
                        return new DebuggerStatement();
                    case "Directive":
                        return new Directive(jsonObject.get("rawValue").getAsString());
                    case "DoWhileStatement":
                        return new DoWhileStatement((Statement) deserializeNode(jsonObject.get("body")), (Expression) deserializeNode(jsonObject.get("test")));
                    case "EmptyStatement":
                        return new EmptyStatement();
                    case "Export":
                        return new Export((FunctionDeclarationClassDeclarationVariableDeclaration) deserializeNode(jsonObject.get("declaration")));
                    case "ExportAllFrom":
                        return new ExportAllFrom(jsonObject.get("moduleSpecifier").getAsString());
                    case "ExportDefault":
                        return new ExportDefault((FunctionDeclarationClassDeclarationExpression) deserializeNode(jsonObject.get("body")));
                    case "ExportFrom":
                        return new ExportFrom(deserializeListExportFromSpecifier(jsonObject.get("namedExports")), jsonObject.get("moduleSpecifier").getAsString());
                    case "ExportFromSpecifier":
                        return new ExportFromSpecifier(jsonObject.get("name").getAsString(), deserializeMaybeString(jsonObject.get("exportedName")));
                    case "ExportLocalSpecifier":
                        return new ExportLocalSpecifier((IdentifierExpression) deserializeNode(jsonObject.get("name")), deserializeMaybeString(jsonObject.get("exportedName")));
                    case "ExportLocals":
                        return new ExportLocals(deserializeListExportLocalSpecifier(jsonObject.get("namedExports")));
                    case "ExpressionStatement":
                        return new ExpressionStatement((Expression) deserializeNode(jsonObject.get("expression")));
                    case "ForInStatement":
                        return new ForInStatement((VariableDeclarationAssignmentTarget) deserializeNode(jsonObject.get("left")), (Expression) deserializeNode(jsonObject.get("right")), (Statement) deserializeNode(jsonObject.get("body")));
                    case "ForOfStatement":
                        return new ForOfStatement((VariableDeclarationAssignmentTarget) deserializeNode(jsonObject.get("left")), (Expression) deserializeNode(jsonObject.get("right")), (Statement) deserializeNode(jsonObject.get("body")));
                    case "ForStatement":
                        return new ForStatement(deserializeMaybeVariableDeclarationExpression(jsonObject.get("init")), deserializeMaybeExpression(jsonObject.get("test")), deserializeMaybeExpression(jsonObject.get("update")), (Statement) deserializeNode(jsonObject.get("body")));
                    case "FormalParameters":
                        return new FormalParameters(deserializeListParameter(jsonObject.get("items")), deserializeMaybeBinding(jsonObject.get("rest")));
                    case "FunctionBody":
                        return new FunctionBody(deserializeListDirective(jsonObject.get("directives")), deserializeListStatement(jsonObject.get("statements")));
                    case "FunctionDeclaration":
                        return new FunctionDeclaration(jsonObject.get("isGenerator").getAsBoolean(), (BindingIdentifier) deserializeNode(jsonObject.get("name")), (FormalParameters) deserializeNode(jsonObject.get("params")), (FunctionBody) deserializeNode(jsonObject.get("body")));
                    case "FunctionExpression":
                        return new FunctionExpression(jsonObject.get("isGenerator").getAsBoolean(), deserializeMaybeBindingIdentifier(jsonObject.get("name")), (FormalParameters) deserializeNode(jsonObject.get("params")), (FunctionBody) deserializeNode(jsonObject.get("body")));
                    case "Getter":
                        return new Getter((PropertyName) deserializeNode(jsonObject.get("name")), (FunctionBody) deserializeNode(jsonObject.get("body")));
                    case "IdentifierExpression":
                        return new IdentifierExpression(jsonObject.get("name").getAsString());
                    case "IfStatement":
                        return new IfStatement((Expression) deserializeNode(jsonObject.get("test")), (Statement) deserializeNode(jsonObject.get("consequent")), deserializeMaybeStatement(jsonObject.get("alternate")));
                    case "Import":
                        return new Import(deserializeMaybeBindingIdentifier(jsonObject.get("defaultBinding")), deserializeListImportSpecifier(jsonObject.get("namedImports")), jsonObject.get("moduleSpecifier").getAsString());
                    case "ImportNamespace":
                        return new ImportNamespace(deserializeMaybeBindingIdentifier(jsonObject.get("defaultBinding")), (BindingIdentifier) deserializeNode(jsonObject.get("namespaceBinding")), jsonObject.get("moduleSpecifier").getAsString());
                    case "ImportSpecifier":
                        return new ImportSpecifier(deserializeMaybeString(jsonObject.get("name")), (BindingIdentifier) deserializeNode(jsonObject.get("binding")));
                    case "LabeledStatement":
                        return new LabeledStatement(jsonObject.get("label").getAsString(), (Statement) deserializeNode(jsonObject.get("body")));
                    case "LiteralBooleanExpression":
                        return new LiteralBooleanExpression(jsonObject.get("value").getAsBoolean());
                    case "LiteralInfinityExpression":
                        return new LiteralInfinityExpression();
                    case "LiteralNullExpression":
                        return new LiteralNullExpression();
                    case "LiteralNumericExpression":
                        return new LiteralNumericExpression(jsonObject.get("value").getAsDouble());
                    case "LiteralRegExpExpression":
                        return new LiteralRegExpExpression(jsonObject.get("pattern").getAsString(), jsonObject.get("global").getAsBoolean(), jsonObject.get("ignoreCase").getAsBoolean(), jsonObject.get("multiLine").getAsBoolean(), jsonObject.get("sticky").getAsBoolean(), jsonObject.get("unicode").getAsBoolean());
                    case "LiteralStringExpression":
                        return new LiteralStringExpression(jsonObject.get("value").getAsString());
                    case "Method":
                        return new Method(jsonObject.get("isGenerator").getAsBoolean(), (PropertyName) deserializeNode(jsonObject.get("name")), (FormalParameters) deserializeNode(jsonObject.get("params")), (FunctionBody) deserializeNode(jsonObject.get("body")));
                    case "Module":
                        return new Module(deserializeListDirective(jsonObject.get("directives")), deserializeListImportDeclarationExportDeclarationStatement(jsonObject.get("items")));
                    case "NewExpression":
                        return new NewExpression((Expression) deserializeNode(jsonObject.get("callee")), deserializeListSpreadElementExpression(jsonObject.get("arguments")));
                    case "NewTargetExpression":
                        return new NewTargetExpression();
                    case "ObjectAssignmentTarget":
                        return new ObjectAssignmentTarget(deserializeListAssignmentTargetProperty(jsonObject.get("properties")));
                    case "ObjectBinding":
                        return new ObjectBinding(deserializeListBindingProperty(jsonObject.get("properties")));
                    case "ObjectExpression":
                        return new ObjectExpression(deserializeListObjectProperty(jsonObject.get("properties")));
                    case "ReturnStatement":
                        return new ReturnStatement(deserializeMaybeExpression(jsonObject.get("expression")));
                    case "Script":
                        return new Script(deserializeListDirective(jsonObject.get("directives")), deserializeListStatement(jsonObject.get("statements")));
                    case "Setter":
                        return new Setter((PropertyName) deserializeNode(jsonObject.get("name")), (Parameter) deserializeNode(jsonObject.get("param")), (FunctionBody) deserializeNode(jsonObject.get("body")));
                    case "ShorthandProperty":
                        return new ShorthandProperty((IdentifierExpression) deserializeNode(jsonObject.get("name")));
                    case "SpreadElement":
                        return new SpreadElement((Expression) deserializeNode(jsonObject.get("expression")));
                    case "StaticMemberAssignmentTarget":
                        return new StaticMemberAssignmentTarget((ExpressionSuper) deserializeNode(jsonObject.get("object")), jsonObject.get("property").getAsString());
                    case "StaticMemberExpression":
                        return new StaticMemberExpression((ExpressionSuper) deserializeNode(jsonObject.get("object")), jsonObject.get("property").getAsString());
                    case "StaticPropertyName":
                        return new StaticPropertyName(jsonObject.get("value").getAsString());
                    case "Super":
                        return new Super();
                    case "SwitchCase":
                        return new SwitchCase((Expression) deserializeNode(jsonObject.get("test")), deserializeListStatement(jsonObject.get("consequent")));
                    case "SwitchDefault":
                        return new SwitchDefault(deserializeListStatement(jsonObject.get("consequent")));
                    case "SwitchStatement":
                        return new SwitchStatement((Expression) deserializeNode(jsonObject.get("discriminant")), deserializeListSwitchCase(jsonObject.get("cases")));
                    case "SwitchStatementWithDefault":
                        return new SwitchStatementWithDefault((Expression) deserializeNode(jsonObject.get("discriminant")), deserializeListSwitchCase(jsonObject.get("preDefaultCases")), (SwitchDefault) deserializeNode(jsonObject.get("defaultCase")), deserializeListSwitchCase(jsonObject.get("postDefaultCases")));
                    case "TemplateElement":
                        return new TemplateElement(jsonObject.get("rawValue").getAsString());
                    case "TemplateExpression":
                        return new TemplateExpression(deserializeMaybeExpression(jsonObject.get("tag")), deserializeListExpressionTemplateElement(jsonObject.get("elements")));
                    case "ThisExpression":
                        return new ThisExpression();
                    case "ThrowStatement":
                        return new ThrowStatement((Expression) deserializeNode(jsonObject.get("expression")));
                    case "TryCatchStatement":
                        return new TryCatchStatement((Block) deserializeNode(jsonObject.get("body")), (CatchClause) deserializeNode(jsonObject.get("catchClause")));
                    case "TryFinallyStatement":
                        return new TryFinallyStatement((Block) deserializeNode(jsonObject.get("body")), deserializeMaybeCatchClause(jsonObject.get("catchClause")), (Block) deserializeNode(jsonObject.get("finalizer")));
                    case "UnaryExpression":
                        return new UnaryExpression(deserializeUnaryOperator(jsonObject.get("operator")), (Expression) deserializeNode(jsonObject.get("operand")));
                    case "UpdateExpression":
                        return new UpdateExpression(jsonObject.get("isPrefix").getAsBoolean(), deserializeUpdateOperator(jsonObject.get("operator")), (SimpleAssignmentTarget) deserializeNode(jsonObject.get("operand")));
                    case "VariableDeclaration":
                        return new VariableDeclaration(deserializeVariableDeclarationKind(jsonObject.get("kind")), deserializeListVariableDeclarator(jsonObject.get("declarators")));
                    case "VariableDeclarationStatement":
                        return new VariableDeclarationStatement((VariableDeclaration) deserializeNode(jsonObject.get("declaration")));
                    case "VariableDeclarator":
                        return new VariableDeclarator((Binding) deserializeNode(jsonObject.get("binding")), deserializeMaybeExpression(jsonObject.get("init")));
                    case "WhileStatement":
                        return new WhileStatement((Expression) deserializeNode(jsonObject.get("test")), (Statement) deserializeNode(jsonObject.get("body")));
                    case "WithStatement":
                        return new WithStatement((Expression) deserializeNode(jsonObject.get("object")), (Statement) deserializeNode(jsonObject.get("body")));
                    case "YieldExpression":
                        return new YieldExpression(deserializeMaybeExpression(jsonObject.get("expression")));
                    case "YieldGeneratorExpression":
                        return new YieldGeneratorExpression((Expression) deserializeNode(jsonObject.get("expression")));
                }
            }
        }
        return null;
    }
}
