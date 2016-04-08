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


package com.shapesecurity.shift.visitor;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;

import org.jetbrains.annotations.NotNull;

public final class Director {

    @NotNull
    public static <State>
    State reduceArrayBinding(
            @NotNull Reducer<State> reducer,
            @NotNull ArrayBinding node) {
        return reducer.reduceArrayBinding(node, reduceListMaybeBindingBindingWithDefault(reducer, node.elements), reduceMaybeBinding(reducer, node.rest));
    }

    @NotNull
    public static <State>
    State reduceArrayAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull ArrayAssignmentTarget node) {
        return reducer.reduceArrayAssignmentTarget(node, reduceListMaybeAssignmentTargetAssignmentTargetWithDefault(reducer, node.elements), reduceMaybeAssignmentTarget(reducer, node.rest));
    }

    @NotNull
    public static <State>
    State reduceAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull AssignmentTarget node) {
        if (node instanceof ArrayAssignmentTarget) {
            return reduceArrayAssignmentTarget(reducer, (ArrayAssignmentTarget) node);
        } else if (node instanceof BindingIdentifier) {
            return reduceBindingIdentifier(reducer, (BindingIdentifier) node);
        } else if (node instanceof MemberAssignmentTarget) {
            return reduceMemberAssignmentTarget(reducer, (MemberAssignmentTarget) node);
        } else if (node instanceof ObjectAssignmentTarget) {
            return reduceObjectAssignmentTarget(reducer, (ObjectAssignmentTarget) node);
        } else { // no instances of BindingPattern
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceAssignmentTargetAssignmentTargetWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull AssignmentTargetAssignmentTargetWithDefault node) {
        if (node instanceof AssignmentTarget) {
            return reduceAssignmentTarget(reducer, (AssignmentTarget) node);
        } else {
            return reduceAssignmentTargetWithDefault(reducer, (AssignmentTargetWithDefault) node);
        }
    }

    @NotNull
    public static <State>
    State reduceAssignmentTargetWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull AssignmentTargetWithDefault node) {
        return reducer.reduceAssignmentTargetWithDefault(node, reduceAssignmentTarget(reducer, node.binding), reduceExpression(reducer, node.init));
    }

    @NotNull
    public static <State>
    State reduceAssignmentTargetProperty(
            @NotNull Reducer<State> reducer,
            @NotNull AssignmentTargetProperty node) {
        if (node instanceof AssignmentTargetPropertyIdentifier) {
            AssignmentTargetPropertyIdentifier tNode = (AssignmentTargetPropertyIdentifier) node;
            return reducer.reduceAssignmentTargetPropertyIdentifier(tNode, reduceBindingIdentifier(reducer, tNode.binding), reduceMaybeExpression(reducer, tNode.init));
        } else if (node instanceof AssignmentTargetPropertyProperty) {
            AssignmentTargetPropertyProperty tNode = (AssignmentTargetPropertyProperty) node;
            return reducer.reduceAssignmentTargetPropertyProperty(tNode, reducePropertyName(reducer, tNode.name), reduceAssignmentTargetAssignmentTargetWithDefault(reducer, tNode.binding));
        } else {
            throw new RuntimeException("Not reached");
        }
    }
    @NotNull
    public static <State>
    State reduceBinding(
            @NotNull Reducer<State> reducer,
            @NotNull Binding node) {
        if (node instanceof ArrayBinding) {
            return reduceArrayBinding(reducer, (ArrayBinding) node);
        } else if (node instanceof BindingIdentifier) {
            return reduceBindingIdentifier(reducer, (BindingIdentifier) node);
        } else if (node instanceof MemberExpression) {
            return reduceMemberExpression(reducer, (MemberExpression) node);
        } else if (node instanceof ObjectBinding) {
            return reduceObjectBinding(reducer, (ObjectBinding) node);
        } else { // no instances of BindingPattern
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceBindingBindingWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull BindingBindingWithDefault node) {
        if (node instanceof Binding) {
            return reduceBinding(reducer, (Binding) node);
        } else {
            return reduceBindingWithDefault(reducer, (BindingWithDefault) node);
        }
    }

    @NotNull
    public static <State>
    State reduceBindingIdentifier(
            @NotNull Reducer<State> reducer,
            @NotNull BindingIdentifier node) {
        return reducer.reduceBindingIdentifier(node);
    }

    @NotNull
    public static <State>
    State reduceSimpleAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull SimpleAssignmentTarget node) {
        if (node instanceof BindingIdentifier) {
            return reducer.reduceBindingIdentifier((BindingIdentifier) node);
        } else {
            return reduceMemberAssignmentTarget(reducer, (MemberAssignmentTarget) node);
        }
    }

    @NotNull
    public static <State>
    State reduceBindingProperty(
            @NotNull Reducer<State> reducer,
            @NotNull BindingProperty node) {
        if (node instanceof BindingPropertyIdentifier) {
            BindingPropertyIdentifier tNode = (BindingPropertyIdentifier) node;
            return reducer.reduceBindingPropertyIdentifier(tNode, reduceBindingIdentifier(reducer, tNode.binding), reduceMaybeExpression(reducer, tNode.init));
        } else if (node instanceof BindingPropertyProperty) {
            BindingPropertyProperty tNode = (BindingPropertyProperty) node;
            return reducer.reduceBindingPropertyProperty(tNode, reducePropertyName(reducer, tNode.name), reduceBindingBindingWithDefault(reducer, tNode.binding));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceBindingWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull BindingWithDefault node) {
        return reducer.reduceBindingWithDefault(node, reduceBinding(reducer, node.binding), reduceExpression(reducer, node.init));
    }

    @NotNull
    public static <State>
    State reduceBlock(
            @NotNull Reducer<State> reducer,
            @NotNull Block node) {
        return reducer.reduceBlock(node, reduceListStatement(reducer, node.statements));
    }

    @NotNull
    public static <State>
    State reduceCatchClause(
            @NotNull Reducer<State> reducer,
            @NotNull CatchClause node) {
        return reducer.reduceCatchClause(node, reduceBinding(reducer, node.binding), reduceBlock(reducer, node.body));
    }

    @NotNull
    public static <State>
    State reduceClassElement(
            @NotNull Reducer<State> reducer,
            @NotNull ClassElement node) {
        return reducer.reduceClassElement(node, reduceMethodDefinition(reducer, node.method));
    }

    @NotNull
    public static <State>
    State reduceDirective(
            @NotNull Reducer<State> reducer,
            @NotNull Directive node) {
        return reducer.reduceDirective(node);
    }

    private static <State> State reduceExportDeclaration(Reducer<State> reducer, ExportDeclaration node) {
        if (node instanceof Export) {
            Export tNode = (Export) node;
            return reducer.reduceExport(tNode, reduceFunctionDeclarationClassDeclarationVariableDeclaration(reducer, tNode.declaration));
        } else if (node instanceof ExportAllFrom) {
            return reducer.reduceExportAllFrom((ExportAllFrom) node);
        } else if (node instanceof ExportDefault) {
            ExportDefault tNode = (ExportDefault) node;
            return reducer.reduceExportDefault(tNode, reduceFunctionDeclarationClassDeclarationExpression(reducer, tNode.body));
        } else if (node instanceof ExportFrom) {
            ExportFrom tNode = (ExportFrom) node;
            return reducer.reduceExportFrom(tNode, reduceListExportFromSpecifier(reducer, tNode.namedExports));
        } else if (node instanceof ExportLocals) {
            ExportLocals tNode = (ExportLocals) node;
            return reducer.reduceExportLocals(tNode, reduceListExportLocalSpecifier(reducer, tNode.namedExports));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceExportFromSpecifier(
            @NotNull Reducer<State> reducer,
            @NotNull ExportFromSpecifier node) {
        return reducer.reduceExportFromSpecifier(node);
    }

    @NotNull
    public static <State>
    State reduceExportLocalSpecifier(
            @NotNull Reducer<State> reducer,
            @NotNull ExportLocalSpecifier node) {
        return reducer.reduceExportLocalSpecifier(node, reducer.reduceIdentifierExpression(node.name));
    }

    @NotNull
    public static <State>
    State reduceExpression(
            @NotNull Reducer<State> reducer,
            @NotNull Expression node) {
        if (node instanceof FunctionExpression) {
            FunctionExpression tNode = (FunctionExpression) node;
            return reducer.reduceFunctionExpression(tNode, reduceMaybeBindingIdentifier(reducer, tNode.name), reduceFormalParameters(reducer, tNode.params), reduceFunctionBody(reducer, tNode.body));
        } else if (node instanceof LiteralBooleanExpression) {
            LiteralBooleanExpression tNode = (LiteralBooleanExpression) node;
            return reducer.reduceLiteralBooleanExpression(tNode);
        } else if (node instanceof LiteralNullExpression) {
            LiteralNullExpression tNode = (LiteralNullExpression) node;
            return reducer.reduceLiteralNullExpression(tNode);
        } else if (node instanceof LiteralInfinityExpression) {
            LiteralInfinityExpression tNode = (LiteralInfinityExpression) node;
            return reducer.reduceLiteralInfinityExpression(tNode);
        } else if (node instanceof LiteralNumericExpression) {
            LiteralNumericExpression tNode = (LiteralNumericExpression) node;
            return reducer.reduceLiteralNumericExpression(tNode);
        } else if (node instanceof LiteralRegExpExpression) {
            LiteralRegExpExpression tNode = (LiteralRegExpExpression) node;
            return reducer.reduceLiteralRegExpExpression(tNode);
        } else if (node instanceof LiteralStringExpression) {
            LiteralStringExpression tNode = (LiteralStringExpression) node;
            return reducer.reduceLiteralStringExpression(tNode);
        } else if (node instanceof ArrowExpression) {
            ArrowExpression tNode = (ArrowExpression) node;
            return reducer.reduceArrowExpression(tNode, reduceFormalParameters(reducer, tNode.params), reduceFunctionBodyExpression(reducer, tNode.body));
        } else if (node instanceof ArrayExpression) {
            ArrayExpression tNode = (ArrayExpression) node;
            return reducer.reduceArrayExpression(tNode, reduceListMaybeSpreadElementExpression(reducer, tNode.elements));
        } else if (node instanceof AssignmentExpression) {
            AssignmentExpression tNode = (AssignmentExpression) node;
            return reducer.reduceAssignmentExpression(tNode, reduceAssignmentTarget(reducer, tNode.binding), reduceExpression(reducer, tNode.expression));
        } else if (node instanceof BinaryExpression) {
            BinaryExpression tNode = (BinaryExpression) node;
            return reducer.reduceBinaryExpression(tNode, reduceExpression(reducer, tNode.left), reduceExpression(reducer, tNode.right));
        } else if (node instanceof CallExpression) {
            CallExpression tNode = (CallExpression) node;
            return reducer.reduceCallExpression(tNode, reduceExpressionSuper(reducer, tNode.callee), reduceListSpreadElementExpression(reducer, tNode.arguments));
        } else if (node instanceof ClassExpression) {
            ClassExpression tNode = (ClassExpression) node;
            return reducer.reduceClassExpression(tNode, reduceMaybeBindingIdentifier(reducer, tNode.name), reduceMaybeExpression(reducer, tNode._super), reduceListClassElement(reducer, tNode.elements));
        } else if (node instanceof CompoundAssignmentExpression) {
            CompoundAssignmentExpression tNode = (CompoundAssignmentExpression) node;
            return reducer.reduceCompoundAssignmentExpression(tNode, reduceSimpleAssignmentTarget(reducer, tNode.binding), reduceExpression(reducer, tNode.expression));
        } else if (node instanceof ComputedMemberExpression) {
            ComputedMemberExpression tNode = (ComputedMemberExpression) node;
            return reducer.reduceComputedMemberExpression(tNode, reduceExpressionSuper(reducer, tNode._object), reduceExpression(reducer, tNode.expression));
        } else if (node instanceof ConditionalExpression) {
            ConditionalExpression tNode = (ConditionalExpression) node;
            return reducer.reduceConditionalExpression(tNode, reduceExpression(reducer, tNode.test), reduceExpression(reducer, tNode.consequent), reduceExpression(reducer, tNode.alternate));
        } else if (node instanceof IdentifierExpression) {
            IdentifierExpression tNode = (IdentifierExpression) node;
            return reducer.reduceIdentifierExpression(tNode);
        } else if (node instanceof NewExpression) {
            NewExpression tNode = (NewExpression) node;
            return reducer.reduceNewExpression(tNode, reduceExpression(reducer, tNode.callee), reduceListSpreadElementExpression(reducer, tNode.arguments));
        } else if (node instanceof NewTargetExpression) {
            NewTargetExpression tNode = (NewTargetExpression) node;
            return reducer.reduceNewTargetExpression(tNode);
        } else if (node instanceof ObjectExpression) {
            ObjectExpression tNode = (ObjectExpression) node;
            return reducer.reduceObjectExpression(tNode, reduceListObjectProperty(reducer, tNode.properties));
        } else if (node instanceof StaticMemberExpression) {
            StaticMemberExpression tNode = (StaticMemberExpression) node;
            return reducer.reduceStaticMemberExpression(tNode, reduceExpressionSuper(reducer, tNode._object));
        } else if (node instanceof TemplateExpression) {
            TemplateExpression tNode = (TemplateExpression) node;
            return reducer.reduceTemplateExpression(tNode, reduceMaybeExpression(reducer, tNode.tag), reduceListExpressionTemplateElement(reducer, tNode.elements));
        } else if (node instanceof ThisExpression) {
            ThisExpression tNode = (ThisExpression) node;
            return reducer.reduceThisExpression(tNode);
        } else if (node instanceof UnaryExpression) {
            UnaryExpression tNode = (UnaryExpression) node;
            return reducer.reduceUnaryExpression(tNode, reduceExpression(reducer, tNode.operand));
        } else if (node instanceof UpdateExpression) {
            UpdateExpression tNode = (UpdateExpression) node;
            return reducer.reduceUpdateExpression(tNode, reduceSimpleAssignmentTarget(reducer, tNode.operand));
        } else if (node instanceof YieldExpression) {
            YieldExpression tNode = (YieldExpression) node;
            return reducer.reduceYieldExpression(tNode, reduceMaybeExpression(reducer, tNode.expression));
        } else if (node instanceof YieldGeneratorExpression) {
            YieldGeneratorExpression tNode = (YieldGeneratorExpression) node;
            return reducer.reduceYieldGeneratorExpression(tNode, reduceExpression(reducer, tNode.expression));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceExpressionSuper(
            @NotNull Reducer<State> reducer,
            @NotNull ExpressionSuper node) {
        if (node instanceof Expression) {
            return reduceExpression(reducer, (Expression) node);
        } else {
            return reducer.reduceSuper((Super) node);
        }
    }

    @NotNull
    public static <State>
    State reduceExpressionTemplateElement(
            @NotNull Reducer<State> reducer,
            @NotNull ExpressionTemplateElement node) {
        if (node instanceof Expression) {
            return reduceExpression(reducer, (Expression) node);
        } else if (node instanceof TemplateElement) {
            return reduceTemplateElement(reducer, (TemplateElement) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceFormalParameters(
            @NotNull Reducer<State> reducer,
            @NotNull FormalParameters node) {
        return reducer.reduceFormalParameters(node, reduceListParameter(reducer, node.items), reduceMaybeBinding(reducer, node.rest));
    }

    @NotNull
    public static <State>
    State reduceFunctionBody(
            @NotNull Reducer<State> reducer,
            @NotNull FunctionBody node) {
        return reducer.reduceFunctionBody(node, reduceListDirective(reducer, node.directives), reduceListStatement(reducer, node.statements));
    }

    @NotNull
    public static <State>
    State reduceFunctionBodyExpression(
            @NotNull Reducer<State> reducer,
            @NotNull FunctionBodyExpression node) {
        if (node instanceof FunctionBody) {
            return reduceFunctionBody(reducer, (FunctionBody) node);
        } else if (node instanceof Expression) {
            return reduceExpression(reducer, (Expression) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceFunctionDeclarationClassDeclarationExpression(
            @NotNull Reducer<State> reducer,
            @NotNull FunctionDeclarationClassDeclarationExpression node) {
        if (node instanceof FunctionDeclaration) {
            FunctionDeclaration tNode = (FunctionDeclaration) node;
            return reducer.reduceFunctionDeclaration(tNode, reduceBindingIdentifier(reducer, tNode.name), reduceFormalParameters(reducer, tNode.params), reduceFunctionBody(reducer, tNode.body));
        } else if (node instanceof ClassDeclaration) {
            ClassDeclaration tNode = (ClassDeclaration) node;
            return reducer.reduceClassDeclaration(tNode, reduceBindingIdentifier(reducer, tNode.name), reduceMaybeExpression(reducer, tNode._super), reduceListClassElement(reducer, tNode.elements));
        } else if (node instanceof Expression) {
            return reduceExpression(reducer, (Expression) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceFunctionDeclarationClassDeclarationVariableDeclaration(
            @NotNull Reducer<State> reducer,
            @NotNull FunctionDeclarationClassDeclarationVariableDeclaration node) {
        if (node instanceof FunctionDeclaration) {
            FunctionDeclaration tNode = (FunctionDeclaration) node;
            return reducer.reduceFunctionDeclaration(tNode, reduceBindingIdentifier(reducer, tNode.name), reduceFormalParameters(reducer, tNode.params), reduceFunctionBody(reducer, tNode.body));
        } else if (node instanceof ClassDeclaration) {
            ClassDeclaration tNode = (ClassDeclaration) node;
            return reducer.reduceClassDeclaration(tNode, reduceBindingIdentifier(reducer, tNode.name), reduceMaybeExpression(reducer, tNode._super), reduceListClassElement(reducer, tNode.elements));
        } else if (node instanceof VariableDeclaration) {
            VariableDeclaration tNode = (VariableDeclaration) node;
            return reducer.reduceVariableDeclaration(tNode, reduceListVariableDeclarator(reducer, tNode.declarators));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    private static <State> State reduceImportDeclaration(Reducer<State> reducer, ImportDeclaration node) {
        if (node instanceof Import) {
            Import tNode = (Import) node;
            return reducer.reduceImport(tNode, reduceMaybeBindingIdentifier(reducer, tNode.defaultBinding), reduceListImportSpecifier(reducer, tNode.namedImports));
        } else if (node instanceof ImportNamespace) {
            ImportNamespace tNode = (ImportNamespace) node;
            return reducer.reduceImportNamespace(tNode, reduceMaybeBindingIdentifier(reducer, tNode.defaultBinding), reduceBindingIdentifier(reducer, tNode.namespaceBinding));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceImportDeclarationExportDeclarationStatement(
            @NotNull Reducer<State> reducer,
            @NotNull ImportDeclarationExportDeclarationStatement node) {
        if (node instanceof ImportDeclaration) {
            return reduceImportDeclaration(reducer, (ImportDeclaration) node);
        } else if (node instanceof ExportDeclaration) {
            return reduceExportDeclaration(reducer, (ExportDeclaration) node);
        } else if (node instanceof Statement) {
            return reduceStatement(reducer, (Statement) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceImportSpecifier(
            @NotNull Reducer<State> reducer,
            @NotNull ImportSpecifier node) {
        return reducer.reduceImportSpecifier(node, reduceBindingIdentifier(reducer, node.binding));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListBindingBindingWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<BindingBindingWithDefault> node) {
        return node.map(x -> reduceBindingBindingWithDefault(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListAssignmentTargetProperty(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<AssignmentTargetProperty> node) {
        return node.map(x -> reduceAssignmentTargetProperty(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListBindingProperty(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<BindingProperty> node) {
        return node.map(x -> reduceBindingProperty(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListClassElement(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ClassElement> node) {
        return node.map(x -> reduceClassElement(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListDirective(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<Directive> directives) {
        return directives.mapWithIndex((i, el) -> reduceDirective(reducer, el));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListExportFromSpecifier(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ExportFromSpecifier> node) {
        return node.map(x -> reduceExportFromSpecifier(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListExportLocalSpecifier(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ExportLocalSpecifier> node) {
        return node.map(x -> reduceExportLocalSpecifier(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListExpressionTemplateElement(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ExpressionTemplateElement> node) {
        return node.map(x -> reduceExpressionTemplateElement(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListImportDeclarationExportDeclarationStatement(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ImportDeclarationExportDeclarationStatement> node) {
        return node.map(x -> reduceImportDeclarationExportDeclarationStatement(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListImportSpecifier(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ImportSpecifier> node) {
        return node.map(x -> reduceImportSpecifier(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<Maybe<State>> reduceListMaybeBindingBindingWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<Maybe<BindingBindingWithDefault>> node) {
        return node.map(x -> reduceMaybeBindingBindingWithDefault(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<Maybe<State>> reduceListMaybeAssignmentTargetAssignmentTargetWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<Maybe<AssignmentTargetAssignmentTargetWithDefault>> node) {
        return node.map(x -> reduceMaybeAssignmentTargetAssignmentTargetWithDefault(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<Maybe<State>> reduceListMaybeSpreadElementExpression(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<Maybe<SpreadElementExpression>> node) {
        return node.map(x -> reduceMaybeSpreadElementExpression(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListObjectProperty(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<ObjectProperty> properties) {
        return properties.mapWithIndex((i, el) -> reduceObjectProperty(reducer, el));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListParameter(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<Parameter> parameters) {
        return parameters.map(el -> reduceParameter(reducer, el));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListSpreadElementExpression(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<SpreadElementExpression> node) {
        return node.map(x -> reduceSpreadElementExpression(reducer, x));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListStatement(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<Statement> list) {
        return list.mapWithIndex((i, el) -> reduceStatement(reducer, el));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListSwitchCase(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<SwitchCase> node) {
        return node.mapWithIndex((i, el) -> reduceSwitchCase(reducer, el));
    }

    @NotNull
    public static <State>
    ImmutableList<State> reduceListVariableDeclarator(
            @NotNull Reducer<State> reducer,
            @NotNull ImmutableList<VariableDeclarator> node) {
        return node.map(x -> reduceVariableDeclarator(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<AssignmentTarget> node) {
        return node.map(x -> reduceAssignmentTarget(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeBinding(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<Binding> node) {
        return node.map(x -> reduceBinding(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeBindingBindingWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<BindingBindingWithDefault> node) {
        return node.map(x -> reduceBindingBindingWithDefault(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeAssignmentTargetAssignmentTargetWithDefault(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<AssignmentTargetAssignmentTargetWithDefault> node) {
        return node.map(x -> reduceAssignmentTargetAssignmentTargetWithDefault(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeBindingIdentifier(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<BindingIdentifier> node) {
        return node.map(x -> reduceBindingIdentifier(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeExpression(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<Expression> node) {
        return node.map(x -> reduceExpression(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeSpreadElementExpression(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<SpreadElementExpression> node) {
        return node.map(x -> reduceSpreadElementExpression(reducer, x));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeStatement(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<Statement> node) {
        return node.map(n -> reduceStatement(reducer, n));
    }

    @NotNull
    public static <State>
    Maybe<State> reduceMaybeVariableDeclarationExpression(
            @NotNull Reducer<State> reducer,
            @NotNull Maybe<VariableDeclarationExpression> node) {
        return node.map(x -> reduceVariableDeclarationExpression(reducer, x));
    }

    @NotNull
    public static <State>
    State reduceMemberExpression(
            @NotNull Reducer<State> reducer,
            @NotNull MemberExpression node) {
        if (node instanceof ComputedMemberExpression) {
            ComputedMemberExpression computedMemberExpression = (ComputedMemberExpression) node;
            return reducer.reduceComputedMemberExpression(computedMemberExpression, reduceExpressionSuper(reducer, computedMemberExpression._object), reduceExpression(reducer, computedMemberExpression.expression));
        } else {
            StaticMemberExpression staticMemberExpression = (StaticMemberExpression) node;
            return reducer.reduceStaticMemberExpression(staticMemberExpression, reduceExpressionSuper(reducer, staticMemberExpression._object));
        }
    }

    @NotNull
    public static <State>
    State reduceMemberAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull MemberAssignmentTarget node) {
        if (node instanceof ComputedMemberAssignmentTarget) {
            ComputedMemberAssignmentTarget computedMemberAssignmentTarget = (ComputedMemberAssignmentTarget) node;
            return reducer.reduceComputedMemberAssignmentTarget(computedMemberAssignmentTarget, reduceExpressionSuper(reducer, computedMemberAssignmentTarget._object), reduceExpression(reducer, computedMemberAssignmentTarget.expression));
        } else {
            StaticMemberAssignmentTarget staticMemberAssignmentTarget = (StaticMemberAssignmentTarget) node;
            return reducer.reduceStaticMemberAssignmentTarget(staticMemberAssignmentTarget, reduceExpressionSuper(reducer, staticMemberAssignmentTarget._object));
        }
    }

    @NotNull
    public static <State>
    State reduceMethodDefinition(
            @NotNull Reducer<State> reducer,
            @NotNull MethodDefinition node) {
        if (node instanceof Getter) {
            Getter tNode = (Getter) node;
            return reducer.reduceGetter(tNode, reducePropertyName(reducer, tNode.name), reduceFunctionBody(reducer, tNode.body));
        } else if (node instanceof Setter) {
            Setter tNode = (Setter) node;
            return reducer.reduceSetter(tNode, reducePropertyName(reducer, tNode.name), reduceParameter(reducer, tNode.param), reduceFunctionBody(reducer, tNode.body));
        } else if (node instanceof Method) {
            Method tNode = (Method) node;
            return reducer.reduceMethod(tNode, reducePropertyName(reducer, tNode.name), reduceFormalParameters(reducer, tNode.params), reduceFunctionBody(reducer, tNode.body));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceModule(@NotNull Reducer<State> reducer, @NotNull Module node) {
        return reducer.reduceModule(node, reduceListDirective(reducer, node.directives), reduceListImportDeclarationExportDeclarationStatement(reducer, node.items));
    }

    @NotNull
    public static <State>
    State reduceNamedObjectProperty(
            @NotNull Reducer<State> reducer,
            @NotNull NamedObjectProperty node) {
        if (node instanceof DataProperty) {
            DataProperty tNode = (DataProperty) node;
            return reducer.reduceDataProperty(tNode, reduceExpression(reducer, tNode.expression), reducePropertyName(reducer, tNode.name));
        } else if (node instanceof MethodDefinition) {
            return reduceMethodDefinition(reducer, (MethodDefinition) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceObjectBinding(
            @NotNull Reducer<State> reducer,
            @NotNull ObjectBinding node) {
        return reducer.reduceObjectBinding(node, reduceListBindingProperty(reducer, node.properties));
    }

    @NotNull
    public static <State>
    State reduceObjectAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull ObjectAssignmentTarget node) {
        return reducer.reduceObjectAssignmentTarget(node, reduceListAssignmentTargetProperty(reducer, node.properties));
    }

    @NotNull
    public static <State>
    State reduceParameter(
            @NotNull Reducer<State> reducer,
            @NotNull Parameter node) {
        return reducer.reduceParameter(node, reduceBinding(reducer, node.binding), reduceMaybeExpression(reducer, node.init));
    }

    @NotNull
    public static <State>
    State reduceObjectProperty(
            @NotNull Reducer<State> reducer,
            @NotNull ObjectProperty node) {
        if (node instanceof DataProperty) {
            DataProperty tNode = (DataProperty) node;
            return reducer.reduceDataProperty(tNode, reduceExpression(reducer, tNode.expression), reducePropertyName(reducer, tNode.name));
        } else if (node instanceof MethodDefinition) {
            return reduceMethodDefinition(reducer, (MethodDefinition) node);
        } else if (node instanceof NamedObjectProperty) {
            return reduceNamedObjectProperty(reducer, (NamedObjectProperty) node);
        } else if (node instanceof ShorthandProperty) {
            ShorthandProperty tNode = (ShorthandProperty) node;
            return reducer.reduceShorthandProperty(tNode, reducer.reduceIdentifierExpression(tNode.name));
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reducePropertyName(
            @NotNull Reducer<State> reducer,
            @NotNull PropertyName node) {
        if (node instanceof ComputedPropertyName) {
            ComputedPropertyName tNode = (ComputedPropertyName) node;
            return reducer.reduceComputedPropertyName(tNode, reduceExpression(reducer, tNode.expression));
        } else if (node instanceof StaticPropertyName) {
            StaticPropertyName tNode = (StaticPropertyName) node;
            return reducer.reduceStaticPropertyName(tNode);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceScript(
            @NotNull Reducer<State> reducer,
            @NotNull Script node) {
        return reducer.reduceScript(node, reduceListDirective(reducer, node.directives), reduceListStatement(reducer, node.statements));
    }

    @NotNull
    public static <State>
    State reduceSpreadElementExpression(
            @NotNull Reducer<State> reducer,
            @NotNull SpreadElementExpression node) {
        if (node instanceof SpreadElement) {
            SpreadElement tNode = (SpreadElement) node;
            return reducer.reduceSpreadElement(tNode, reduceExpression(reducer, tNode.expression));
        } else if (node instanceof Expression) {
            return reduceExpression(reducer, (Expression) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceStatement(
            @NotNull Reducer<State> reducer,
            @NotNull Statement node) {
        if (node instanceof FunctionDeclaration) {
            FunctionDeclaration tNode = (FunctionDeclaration) node;
            return reducer.reduceFunctionDeclaration(tNode, reduceBindingIdentifier(reducer, tNode.name), reduceFormalParameters(reducer, tNode.params), reduceFunctionBody(reducer, tNode.body));
        } else if (node instanceof ClassDeclaration) {
            ClassDeclaration tNode = (ClassDeclaration) node;
            return reducer.reduceClassDeclaration(tNode, reduceBindingIdentifier(reducer, tNode.name), reduceMaybeExpression(reducer, tNode._super), reduceListClassElement(reducer, tNode.elements));
        } else if (node instanceof BlockStatement) {
            BlockStatement tNode = (BlockStatement) node;
            return reducer.reduceBlockStatement(tNode, reduceBlock(reducer, tNode.block));
        } else if (node instanceof BreakStatement) {
            BreakStatement tNode = (BreakStatement) node;
            return reducer.reduceBreakStatement(tNode);
        } else if (node instanceof ContinueStatement) {
            ContinueStatement tNode = (ContinueStatement) node;
            return reducer.reduceContinueStatement(tNode);
        } else if (node instanceof DebuggerStatement) {
            DebuggerStatement tNode = (DebuggerStatement) node;
            return reducer.reduceDebuggerStatement(tNode);
        } else if (node instanceof DoWhileStatement) {
            DoWhileStatement tNode = (DoWhileStatement) node;
            return reducer.reduceDoWhileStatement(tNode, reduceStatement(reducer, tNode.body), reduceExpression(reducer, tNode.test));
        } else if (node instanceof EmptyStatement) {
            EmptyStatement tNode = (EmptyStatement) node;
            return reducer.reduceEmptyStatement(tNode);
        } else if (node instanceof ExpressionStatement) {
            ExpressionStatement tNode = (ExpressionStatement) node;
            return reducer.reduceExpressionStatement(tNode, reduceExpression(reducer, tNode.expression));
        } else if (node instanceof ForInStatement) {
            ForInStatement tNode = (ForInStatement) node;
            return reducer.reduceForInStatement(tNode, reduceVariableDeclarationAssignmentTarget(reducer, tNode.left), reduceExpression(reducer, tNode.right), reduceStatement(reducer, tNode.body));
        } else if (node instanceof ForOfStatement) {
            ForOfStatement tNode = (ForOfStatement) node;
            return reducer.reduceForOfStatement(tNode, reduceVariableDeclarationAssignmentTarget(reducer, tNode.left), reduceExpression(reducer, tNode.right), reduceStatement(reducer, tNode.body));
        } else if (node instanceof ForStatement) {
            ForStatement tNode = (ForStatement) node;
            return reducer.reduceForStatement(tNode, reduceMaybeVariableDeclarationExpression(reducer, tNode.init), reduceMaybeExpression(reducer, tNode.test), reduceMaybeExpression(reducer, tNode.update), reduceStatement(reducer, tNode.body));
        } else if (node instanceof IfStatement) {
            IfStatement tNode = (IfStatement) node;
            return reducer.reduceIfStatement(tNode, reduceExpression(reducer, tNode.test), reduceStatement(reducer, tNode.consequent), reduceMaybeStatement(reducer, tNode.alternate));
        } else if (node instanceof LabeledStatement) {
            LabeledStatement tNode = (LabeledStatement) node;
            return reducer.reduceLabeledStatement(tNode, reduceStatement(reducer, tNode.body));
        } else if (node instanceof ReturnStatement) {
            ReturnStatement tNode = (ReturnStatement) node;
            return reducer.reduceReturnStatement(tNode, reduceMaybeExpression(reducer, tNode.expression));
        } else if (node instanceof SwitchStatement) {
            SwitchStatement tNode = (SwitchStatement) node;
            return reducer.reduceSwitchStatement(tNode, reduceExpression(reducer, tNode.discriminant), reduceListSwitchCase(reducer, tNode.cases));
        } else if (node instanceof SwitchStatementWithDefault) {
            SwitchStatementWithDefault tNode = (SwitchStatementWithDefault) node;
            return reducer.reduceSwitchStatementWithDefault(tNode, reduceExpression(reducer, tNode.discriminant), reduceListSwitchCase(reducer, tNode.preDefaultCases), reduceSwitchDefault(reducer, tNode.defaultCase), reduceListSwitchCase(reducer, tNode.postDefaultCases));
        } else if (node instanceof ThrowStatement) {
            ThrowStatement tNode = (ThrowStatement) node;
            return reducer.reduceThrowStatement(tNode, reduceExpression(reducer, tNode.expression));
        } else if (node instanceof TryCatchStatement) {
            TryCatchStatement tNode = (TryCatchStatement) node;
            return reducer.reduceTryCatchStatement(tNode, reduceBlock(reducer, tNode.body), reduceCatchClause(reducer, tNode.catchClause));
        } else if (node instanceof TryFinallyStatement) {
            TryFinallyStatement tNode = (TryFinallyStatement) node;
            return reducer.reduceTryFinallyStatement(tNode, reduceBlock(reducer, tNode.body), tNode.catchClause.map(n -> reduceCatchClause(reducer, n)), reduceBlock(reducer, tNode.finalizer));
        } else if (node instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement tNode = (VariableDeclarationStatement) node;
            return reducer.reduceVariableDeclarationStatement(tNode, reduceVariableDeclaration(reducer, tNode.declaration));
        } else if (node instanceof WhileStatement) {
            WhileStatement tNode = (WhileStatement) node;
            return reducer.reduceWhileStatement(tNode, reduceExpression(reducer, tNode.test), reduceStatement(reducer, tNode.body));
        } else if (node instanceof WithStatement) {
            WithStatement tNode = (WithStatement) node;
            return reducer.reduceWithStatement(tNode, reduceExpression(reducer, tNode._object), reduceStatement(reducer, tNode.body));
        }
        throw new RuntimeException("Not reached");
    }

    @NotNull
    public static <State>
    State reduceSwitchCase(
            @NotNull Reducer<State> reducer,
            @NotNull SwitchCase node) {
        return reducer.reduceSwitchCase(node, reduceExpression(reducer, node.test), reduceListStatement(reducer, node.consequent));
    }

    @NotNull
    public static <State>
    State reduceSwitchDefault(
            @NotNull Reducer<State> reducer,
            @NotNull SwitchDefault node) {
        return reducer.reduceSwitchDefault(node, reduceListStatement(reducer, node.consequent));
    }

    @NotNull
    public static <State>
    State reduceTemplateElement(
            @NotNull Reducer<State> reducer,
            @NotNull TemplateElement node) {
        return reducer.reduceTemplateElement(node);
    }

    @NotNull
    public static <State>
    State reduceVariableDeclaration(
            @NotNull Reducer<State> reducer,
            @NotNull VariableDeclaration node) {
        return reducer.reduceVariableDeclaration(node, node.declarators.map((el) -> reduceVariableDeclarator(reducer, el)));
    }

    @NotNull
    public static <State>
    State reduceVariableDeclarationAssignmentTarget(
            @NotNull Reducer<State> reducer,
            @NotNull VariableDeclarationAssignmentTarget node) {
        if (node instanceof VariableDeclaration) {
            return reduceVariableDeclaration(reducer, (VariableDeclaration) node);
        } else if (node instanceof AssignmentTarget) {
            return reduceAssignmentTarget(reducer, (AssignmentTarget) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceVariableDeclarationExpression(
            @NotNull Reducer<State> reducer,
            @NotNull VariableDeclarationExpression node) {
        if (node instanceof VariableDeclaration) {
            return reduceVariableDeclaration(reducer, (VariableDeclaration) node);
        } else if (node instanceof Expression) {
            return reduceExpression(reducer, (Expression) node);
        } else {
            throw new RuntimeException("Not reached");
        }
    }

    @NotNull
    public static <State>
    State reduceVariableDeclarator(
            @NotNull Reducer<State> reducer,
            @NotNull VariableDeclarator node) {
        return reducer.reduceVariableDeclarator(node, reduceBinding(reducer, node.binding), reduceMaybeExpression(reducer, node.init));
    }
}
