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

package com.shapesecurity.shift.es2016.validator;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2016.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2016.ast.BindingIdentifier;
import com.shapesecurity.shift.es2016.ast.BreakStatement;
import com.shapesecurity.shift.es2016.ast.ClassDeclaration;
import com.shapesecurity.shift.es2016.ast.ContinueStatement;
import com.shapesecurity.shift.es2016.ast.Directive;
import com.shapesecurity.shift.es2016.ast.ExportDefault;
import com.shapesecurity.shift.es2016.ast.ExportFromSpecifier;
import com.shapesecurity.shift.es2016.ast.ExportLocalSpecifier;
import com.shapesecurity.shift.es2016.ast.Expression;
import com.shapesecurity.shift.es2016.ast.ForInStatement;
import com.shapesecurity.shift.es2016.ast.ForOfStatement;
import com.shapesecurity.shift.es2016.ast.FunctionBody;
import com.shapesecurity.shift.es2016.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2016.ast.FunctionExpression;
import com.shapesecurity.shift.es2016.ast.IdentifierExpression;
import com.shapesecurity.shift.es2016.ast.IfStatement;
import com.shapesecurity.shift.es2016.ast.ImportSpecifier;
import com.shapesecurity.shift.es2016.ast.IterationStatement;
import com.shapesecurity.shift.es2016.ast.LabeledStatement;
import com.shapesecurity.shift.es2016.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2016.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2016.ast.Method;
import com.shapesecurity.shift.es2016.ast.Module;
import com.shapesecurity.shift.es2016.ast.Program;
import com.shapesecurity.shift.es2016.ast.ReturnStatement;
import com.shapesecurity.shift.es2016.ast.Script;
import com.shapesecurity.shift.es2016.ast.Statement;
import com.shapesecurity.shift.es2016.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2016.ast.TemplateElement;
import com.shapesecurity.shift.es2016.ast.TemplateExpression;
import com.shapesecurity.shift.es2016.ast.VariableDeclaration;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2016.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2016.ast.YieldExpression;
import com.shapesecurity.shift.es2016.ast.YieldGeneratorExpression;
import com.shapesecurity.shift.es2016.parser.token.StringLiteralToken;
import com.shapesecurity.shift.es2016.reducer.Director;
import com.shapesecurity.shift.es2016.parser.JsError;
import com.shapesecurity.shift.es2016.parser.Token;
import com.shapesecurity.shift.es2016.parser.Tokenizer;
import com.shapesecurity.shift.es2016.parser.token.EOFToken;
import com.shapesecurity.shift.es2016.utils.Utils;
import com.shapesecurity.shift.es2016.reducer.MonoidalReducer;

import org.jetbrains.annotations.NotNull;

public class Validator extends MonoidalReducer<ValidationContext> {

    public Validator() {
        super(ValidationContext.MONOID);
    }

    private static boolean checkIsLiteralRegExpPattern(String pattern) {
        // copied from tokenizer for getting a regex token
        int index = 0;
        boolean terminated = false;
        boolean classMarker = false;
        while (index < pattern.length()) {
            char ch = pattern.charAt(index);
            if (ch == '\\') {
                index++;
                ch = pattern.charAt(index);
                if (Utils.isLineTerminator(ch)) {
                    return false;
                }
                index++;
            } else if (Utils.isLineTerminator(ch)) {
                return false;
            } else {
                if (classMarker) {
                    if (ch == ']') {
                        classMarker = false;
                    }
                } else {
                    if (ch == '/') {
                        terminated = true;
                        index++;
                        break;
                    } else if (ch == '[') {
                        classMarker = true;
                    }
                }
                index++;
            }
        }

        if (!terminated) {
            return false;
        }

        while (index < pattern.length()) {
            char ch = pattern.charAt(index);
            if (ch == '\\') {
                return false;
            }
            if (!Utils.isIdentifierPart(ch)) {
                break;
            }
            index++;
        }
        return true;
    }

    public static boolean checkIsValidIdentifierName(String name) {
        return name.length() > 0 && Utils.isIdentifierStart(name.charAt(0)) && name.chars().allMatch(Utils::isIdentifierPart);
    }

    public static ImmutableList<ValidationError> validate(Program program) {
        return ImmutableList.from(Director.reduceProgram(new Validator(), program).errors);
    }

    private boolean checkIsStringLiteral(String rawValue) {
        Tokenizer tokenizer;
        try {
            tokenizer = new Tokenizer("\'" + rawValue + "\'", false);
            Token token = tokenizer.lookahead;
            if (!(token instanceof StringLiteralToken)) {
                System.out.println("NOT STRING LITERAL");
                return false;
            }
            token = tokenizer.collectToken();
            if (!(token instanceof EOFToken)) {
                return false;
            }
        } catch (JsError jsError) {
            try {
                tokenizer = new Tokenizer("\"" + rawValue + "\"", false);
                Token token = tokenizer.lookahead;
                if (!(token instanceof StringLiteralToken)) {
                    System.out.println("NOT STRING LITERAL");
                    return false;
                }
                token = tokenizer.collectToken();
                if (!(token instanceof EOFToken)) {
                    return false;
                }
            } catch (JsError jsError1) {
                return false;
            }
        }
        return true;
    }

    private boolean isProblematicIfStatement(IfStatement node) {
        if (node.alternate.isNothing()) {
            return false;
        }
        Maybe<Statement> current = Maybe.of(node.consequent);
        do {
            Statement currentStmt = current.fromJust();
            if (currentStmt instanceof IfStatement && ((IfStatement) currentStmt).alternate.isNothing()) {
                return true;
            }
            current = trailingStatement(currentStmt);
        } while (current.isJust());
        return false;
    }

    @NotNull
    @Override
    public ValidationContext reduceAssignmentTargetIdentifier(@NotNull AssignmentTargetIdentifier node) {
        ValidationContext s = super.reduceAssignmentTargetIdentifier(node);
        if (!checkIsValidIdentifierName(node.name)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_ASSIGNMENT_TARGET_IDENTIFIER_NAME));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceBindingIdentifier(@NotNull BindingIdentifier node) {
        ValidationContext s = super.reduceBindingIdentifier(node);
        if (!checkIsValidIdentifierName(node.name)) {
            if (node.name.equals("*default*")) {
                s.addBindingIdentifierCalledDefault(node);
            } else {
                s.addError(new ValidationError(node, ValidationErrorMessages.VALID_BINDING_IDENTIFIER_NAME));
            }
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceBreakStatement(@NotNull BreakStatement node) {
        ValidationContext s = super.reduceBreakStatement(node);
        if (node.label.isJust() && !checkIsValidIdentifierName(node.label.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_BREAK_STATEMENT_LABEL));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceContinueStatement(@NotNull ContinueStatement node) {
        ValidationContext s = super.reduceContinueStatement(node);
        if (node.label.isJust() && !checkIsValidIdentifierName(node.label.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_CONTINUE_STATEMENT_LABEL));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceDirective(@NotNull Directive node) {
        ValidationContext s = super.reduceDirective(node);
        if (!checkIsStringLiteral(node.rawValue)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_DIRECTIVE));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceExportDefault(@NotNull ExportDefault node, @NotNull ValidationContext body) {
        ValidationContext s = super.reduceExportDefault(node, body);
        if (node.body instanceof FunctionDeclaration || node.body instanceof ClassDeclaration) {
            s.clearBindingIdentifiersCalledDefault();
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceExportLocalSpecifier(@NotNull ExportLocalSpecifier node, @NotNull ValidationContext name) {
        ValidationContext s = super.reduceExportLocalSpecifier(node, name);
        if (node.exportedName.isJust() && !checkIsValidIdentifierName(node.exportedName.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_EXPORTED_NAME));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceExportFromSpecifier(@NotNull ExportFromSpecifier node) {
        ValidationContext s = super.reduceExportFromSpecifier(node);
        if (!checkIsValidIdentifierName(node.name)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_EXPORT_SPECIFIER_NAME));
        }
        if (node.exportedName.isJust() && !checkIsValidIdentifierName(node.exportedName.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_EXPORTED_NAME));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceForInStatement(@NotNull ForInStatement node, @NotNull ValidationContext left, @NotNull ValidationContext right, @NotNull ValidationContext body) {
        ValidationContext s = super.reduceForInStatement(node, left, right, body);
        if (node.left instanceof VariableDeclaration) {
            VariableDeclaration varDec = (VariableDeclaration) node.left;
            if (varDec.declarators.length != 1) {
                s.addError(new ValidationError(node, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_IN));
            }
            if (varDec.declarators.maybeHead().fromJust().init.isJust()) {
                s.addError(new ValidationError(node, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_IN));
            }
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceForOfStatement(@NotNull ForOfStatement node, @NotNull ValidationContext left, @NotNull ValidationContext right, @NotNull ValidationContext body) {
        ValidationContext s = super.reduceForOfStatement(node, left, right, body);
        if (node.left instanceof VariableDeclaration) {
            VariableDeclaration varDec = (VariableDeclaration) node.left;
            if (varDec.declarators.length != 1) {
                s.addError(new ValidationError(node, ValidationErrorMessages.ONE_VARIABLE_DECLARATOR_IN_FOR_OF));
            }
            if (varDec.declarators.maybeHead().fromJust().init.isJust()) {
                s.addError(new ValidationError(node, ValidationErrorMessages.NO_INIT_IN_VARIABLE_DECLARATOR_IN_FOR_OF));
            }
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceFunctionBody(@NotNull FunctionBody node, @NotNull ImmutableList<ValidationContext> directives, @NotNull ImmutableList<ValidationContext> statements
    ) {
        ValidationContext s = super.reduceFunctionBody(node, directives, statements);
        s.clearFreeReturnStatements();
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceFunctionDeclaration(@NotNull FunctionDeclaration node, @NotNull ValidationContext name, @NotNull ValidationContext params, @NotNull ValidationContext body) {
        ValidationContext s = super.reduceFunctionDeclaration(node, name, params, body);
        if (node.isGenerator) {
            s.clearYieldExpressionsNotInGeneratorContext();
            s.clearYieldGeneratorExpressionsNotInGeneratorContext();
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceFunctionExpression(@NotNull FunctionExpression node, @NotNull Maybe<ValidationContext> name, @NotNull ValidationContext params, @NotNull ValidationContext body) {
        ValidationContext s = super.reduceFunctionExpression(node, name, params, body);
        if (node.isGenerator) {
            s.clearYieldExpressionsNotInGeneratorContext();
            s.clearYieldGeneratorExpressionsNotInGeneratorContext();
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceIdentifierExpression(@NotNull IdentifierExpression node) {
        ValidationContext s = super.reduceIdentifierExpression(node);
        if (!checkIsValidIdentifierName(node.name)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_IDENTIFIER_NAME));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceIfStatement(@NotNull IfStatement node, @NotNull ValidationContext test, @NotNull ValidationContext consequent, @NotNull Maybe<ValidationContext> alternate) {
        ValidationContext s = super.reduceIfStatement(node, test, consequent, alternate);
        if (isProblematicIfStatement(node)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_IF_STATEMENT));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceImportSpecifier(@NotNull ImportSpecifier node, @NotNull ValidationContext binding) {
        ValidationContext s = super.reduceImportSpecifier(node, binding);
        if (node.name.isJust() && !checkIsValidIdentifierName(node.name.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_IMPORT_SPECIFIER_NAME));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceLabeledStatement(@NotNull LabeledStatement node, @NotNull ValidationContext body) {
        ValidationContext s = super.reduceLabeledStatement(node, body);
        if (!checkIsValidIdentifierName(node.label)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_LABEL));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceLiteralNumericExpression(@NotNull LiteralNumericExpression node) {
        ValidationContext s = super.reduceLiteralNumericExpression(node);
        if (Double.isNaN(node.value)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NAN));
        }
        if (node.value < 0) {
            s.addError(new ValidationError(node, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_NEGATIVE));
        }
        if (Double.isInfinite(node.value)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.LITERAL_NUMERIC_VALUE_NOT_INFINITE));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceLiteralRegExpExpression(@NotNull LiteralRegExpExpression node) {
        ValidationContext s = super.reduceLiteralRegExpExpression(node);
        if (!checkIsLiteralRegExpPattern(node.pattern)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_REG_EX_PATTERN));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceMethod(@NotNull Method node, @NotNull ValidationContext params, @NotNull ValidationContext body, @NotNull ValidationContext name) {
        ValidationContext s = super.reduceMethod(node, params, body, name);
        if (node.isGenerator) {
            s.clearYieldExpressionsNotInGeneratorContext();
            s.clearYieldGeneratorExpressionsNotInGeneratorContext();
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceModule(@NotNull Module node, @NotNull ImmutableList<ValidationContext> directives, @NotNull ImmutableList<ValidationContext> items
    ) {
        ValidationContext s = super.reduceModule(node, directives, items);
        s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY));
        s.enforceBindingIdentifiersCalledDefault(bindingIdentifier -> new ValidationError(bindingIdentifier, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT));
        s.enforceYieldExpressionsNotInGeneratorContext(yieldExpression -> new ValidationError(yieldExpression, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION));
        s.enforceYieldGeneratorExpressionsNotInGeneratorContext(yieldGeneratorExpression -> new ValidationError(yieldGeneratorExpression, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION));
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceReturnStatement(@NotNull ReturnStatement node, @NotNull Maybe<ValidationContext> expression
    ) {
        ValidationContext s = super.reduceReturnStatement(node, expression);
        s.addFreeReturnStatement(node);
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceScript(@NotNull Script node, @NotNull ImmutableList<ValidationContext> directives, @NotNull ImmutableList<ValidationContext> statements
    ) {
        ValidationContext s = super.reduceScript(node, directives, statements);
        s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY));
        s.enforceBindingIdentifiersCalledDefault(bindingIdentifier -> new ValidationError(bindingIdentifier, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT));
        s.enforceYieldExpressionsNotInGeneratorContext(yieldExpression -> new ValidationError(yieldExpression, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION));
        s.enforceYieldGeneratorExpressionsNotInGeneratorContext(yieldGeneratorExpression -> new ValidationError(yieldGeneratorExpression, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION));
        return s;
    }


    @NotNull
    @Override
    public ValidationContext reduceStaticMemberExpression(@NotNull StaticMemberExpression node, @NotNull ValidationContext object) {
        ValidationContext s = super.reduceStaticMemberExpression(node, object);
        if (!checkIsValidIdentifierName(node.property)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_STATIC_MEMBER_EXPRESSION_PROPERTY_NAME));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceTemplateElement(@NotNull TemplateElement node) {
        ValidationContext s = super.reduceTemplateElement(node);
        if (!checkIsStringLiteral(node.rawValue)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceTemplateExpression(@NotNull TemplateExpression node, @NotNull Maybe<ValidationContext> tag, @NotNull ImmutableList<ValidationContext> elements) {
        ValidationContext s = super.reduceTemplateExpression(node, tag, elements);
        if (elements.length > 0) {
            if (node.elements.length % 2 == 0) {
                s.addError(new ValidationError(node, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS));
            } else {
                node.elements.mapWithIndex((i, x) -> {
                    if (i % 2 == 0) {
                        if (!(x instanceof TemplateElement)) {
                            s.addError(new ValidationError(node, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS));
                        }
                    } else {
                        if (!(x instanceof Expression)) {
                            s.addError(new ValidationError(node, ValidationErrorMessages.ALTERNATING_TEMPLATE_EXPRESSION_ELEMENTS));
                        }
                    }
                    return true;
                });
            }
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceVariableDeclaration(@NotNull VariableDeclaration node, @NotNull ImmutableList<ValidationContext> declarators) {
        ValidationContext s = super.reduceVariableDeclaration(node, declarators);
        if (node.declarators.length == 0) {
            s.addError(new ValidationError(node, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST));
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceVariableDeclarationStatement(@NotNull VariableDeclarationStatement node, @NotNull ValidationContext declaration) {
        ValidationContext s = super.reduceVariableDeclarationStatement(node, declaration);
        if (node.declaration.kind.equals(VariableDeclarationKind.Const)) {
            node.declaration.declarators.forEach(x -> {
                if (x.init.isNothing()) {
                    s.addError(new ValidationError(node, ValidationErrorMessages.CONST_VARIABLE_DECLARATION_MUST_HAVE_INIT));
                }
            });
        }
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceYieldExpression(@NotNull YieldExpression node, @NotNull Maybe<ValidationContext> expression) {
        ValidationContext s = super.reduceYieldExpression(node, expression);
        s.addYieldExpressionsNotInGeneratorContext(node);
        return s;
    }

    @NotNull
    @Override
    public ValidationContext reduceYieldGeneratorExpression(@NotNull YieldGeneratorExpression node, @NotNull ValidationContext expression) {
        ValidationContext s = super.reduceYieldGeneratorExpression(node, expression);
        s.addYieldGeneratorExpressionsNotInGeneratorContext(node);
        return s;
    }

    @NotNull
    private Maybe<Statement> trailingStatement(Statement node) {
        if (node instanceof IfStatement) {
            return Maybe.of(((IfStatement) node).alternate.orJust(((IfStatement) node).consequent));
        } else if (node instanceof LabeledStatement) {
            return Maybe.of(((LabeledStatement) node).body);
        } else if (node instanceof IterationStatement) {
            return Maybe.of(((IterationStatement) node).body);
        }
        return Maybe.empty();
    }
}
