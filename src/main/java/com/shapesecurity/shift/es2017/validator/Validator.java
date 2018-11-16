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

package com.shapesecurity.shift.es2017.validator;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2017.ast.AssignmentTargetIdentifier;
import com.shapesecurity.shift.es2017.ast.BindingIdentifier;
import com.shapesecurity.shift.es2017.ast.BreakStatement;
import com.shapesecurity.shift.es2017.ast.ClassDeclaration;
import com.shapesecurity.shift.es2017.ast.ContinueStatement;
import com.shapesecurity.shift.es2017.ast.Directive;
import com.shapesecurity.shift.es2017.ast.ExportDefault;
import com.shapesecurity.shift.es2017.ast.ExportFromSpecifier;
import com.shapesecurity.shift.es2017.ast.ExportLocalSpecifier;
import com.shapesecurity.shift.es2017.ast.Expression;
import com.shapesecurity.shift.es2017.ast.ForInStatement;
import com.shapesecurity.shift.es2017.ast.ForOfStatement;
import com.shapesecurity.shift.es2017.ast.FunctionBody;
import com.shapesecurity.shift.es2017.ast.FunctionDeclaration;
import com.shapesecurity.shift.es2017.ast.FunctionExpression;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.IfStatement;
import com.shapesecurity.shift.es2017.ast.ImportSpecifier;
import com.shapesecurity.shift.es2017.ast.IterationStatement;
import com.shapesecurity.shift.es2017.ast.LabeledStatement;
import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.ast.LiteralRegExpExpression;
import com.shapesecurity.shift.es2017.ast.Method;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.ast.ReturnStatement;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.ast.Statement;
import com.shapesecurity.shift.es2017.ast.StaticMemberExpression;
import com.shapesecurity.shift.es2017.ast.TemplateElement;
import com.shapesecurity.shift.es2017.ast.TemplateExpression;
import com.shapesecurity.shift.es2017.ast.VariableDeclaration;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationKind;
import com.shapesecurity.shift.es2017.ast.VariableDeclarationStatement;
import com.shapesecurity.shift.es2017.ast.YieldExpression;
import com.shapesecurity.shift.es2017.ast.YieldGeneratorExpression;
import com.shapesecurity.shift.es2017.parser.TokenType;
import com.shapesecurity.shift.es2017.parser.token.StringLiteralToken;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Token;
import com.shapesecurity.shift.es2017.parser.Tokenizer;
import com.shapesecurity.shift.es2017.parser.token.EOFToken;
import com.shapesecurity.shift.es2017.utils.Utils;
import com.shapesecurity.shift.es2017.reducer.MonoidalReducer;

import javax.annotation.Nonnull;

public class Validator extends MonoidalReducer<ValidationContext> {

    public Validator() {
        super(ValidationContext.MONOID);
    }

    private static boolean checkIsLiteralRegExpPattern(String pattern) {
        // copied from tokenizer for getting a regex token
        // TODO this probably needs to be rewritten to be more strict and depend on the flags
        int index = 0;
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
                } else if (ch == '[') {
                    classMarker = true;
                }
                index++;
            }
        }

        return !classMarker;
    }

    public static boolean checkIsValidIdentifierName(String name) {
        return name.length() > 0 && Utils.isIdentifierStart(name.codePointAt(0)) && name.codePoints().skip(1).allMatch(Utils::isIdentifierPart);
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

    private boolean checkIsTemplateElement(String rawValue) {
        try {
            Tokenizer tokenizer = new Tokenizer('`' + rawValue + '`', false);
            Token token = tokenizer.lex();
            return token.type == TokenType.TEMPLATE && tokenizer.lookahead.type == TokenType.EOS;
        } catch(JsError e) {
            return false;
        }
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

    @Nonnull
    @Override
    public ValidationContext reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node) {
        ValidationContext s = super.reduceAssignmentTargetIdentifier(node);
        if (!checkIsValidIdentifierName(node.name)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_ASSIGNMENT_TARGET_IDENTIFIER_NAME));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
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

    @Nonnull
    @Override
    public ValidationContext reduceBreakStatement(@Nonnull BreakStatement node) {
        ValidationContext s = super.reduceBreakStatement(node);
        if (node.label.isJust() && !checkIsValidIdentifierName(node.label.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_BREAK_STATEMENT_LABEL));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceContinueStatement(@Nonnull ContinueStatement node) {
        ValidationContext s = super.reduceContinueStatement(node);
        if (node.label.isJust() && !checkIsValidIdentifierName(node.label.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_CONTINUE_STATEMENT_LABEL));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceDirective(@Nonnull Directive node) {
        ValidationContext s = super.reduceDirective(node);
        if (!checkIsStringLiteral(node.rawValue)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_DIRECTIVE));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceExportDefault(@Nonnull ExportDefault node, @Nonnull ValidationContext body) {
        ValidationContext s = super.reduceExportDefault(node, body);
        if (node.body instanceof FunctionDeclaration || node.body instanceof ClassDeclaration) {
            s.clearBindingIdentifiersCalledDefault();
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceExportLocalSpecifier(@Nonnull ExportLocalSpecifier node, @Nonnull ValidationContext name) {
        ValidationContext s = super.reduceExportLocalSpecifier(node, name);
        if (node.exportedName.isJust() && !checkIsValidIdentifierName(node.exportedName.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_EXPORTED_NAME));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceExportFromSpecifier(@Nonnull ExportFromSpecifier node) {
        ValidationContext s = super.reduceExportFromSpecifier(node);
        if (!checkIsValidIdentifierName(node.name)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_EXPORT_SPECIFIER_NAME));
        }
        if (node.exportedName.isJust() && !checkIsValidIdentifierName(node.exportedName.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_EXPORTED_NAME));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceForInStatement(@Nonnull ForInStatement node, @Nonnull ValidationContext left, @Nonnull ValidationContext right, @Nonnull ValidationContext body) {
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

    @Nonnull
    @Override
    public ValidationContext reduceForOfStatement(@Nonnull ForOfStatement node, @Nonnull ValidationContext left, @Nonnull ValidationContext right, @Nonnull ValidationContext body) {
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

    @Nonnull
    @Override
    public ValidationContext reduceFunctionBody(@Nonnull FunctionBody node, @Nonnull ImmutableList<ValidationContext> directives, @Nonnull ImmutableList<ValidationContext> statements
    ) {
        ValidationContext s = super.reduceFunctionBody(node, directives, statements);
        s.clearFreeReturnStatements();
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull ValidationContext name, @Nonnull ValidationContext params, @Nonnull ValidationContext body) {
        ValidationContext s = super.reduceFunctionDeclaration(node, name, params, body);
        if (node.isGenerator) {
            s.clearYieldExpressionsNotInGeneratorContext();
            s.clearYieldGeneratorExpressionsNotInGeneratorContext();
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<ValidationContext> name, @Nonnull ValidationContext params, @Nonnull ValidationContext body) {
        ValidationContext s = super.reduceFunctionExpression(node, name, params, body);
        if (node.isGenerator) {
            s.clearYieldExpressionsNotInGeneratorContext();
            s.clearYieldGeneratorExpressionsNotInGeneratorContext();
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
        ValidationContext s = super.reduceIdentifierExpression(node);
        if (!checkIsValidIdentifierName(node.name)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_IDENTIFIER_NAME));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceIfStatement(@Nonnull IfStatement node, @Nonnull ValidationContext test, @Nonnull ValidationContext consequent, @Nonnull Maybe<ValidationContext> alternate) {
        ValidationContext s = super.reduceIfStatement(node, test, consequent, alternate);
        if (isProblematicIfStatement(node)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_IF_STATEMENT));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceImportSpecifier(@Nonnull ImportSpecifier node, @Nonnull ValidationContext binding) {
        ValidationContext s = super.reduceImportSpecifier(node, binding);
        if (node.name.isJust() && !checkIsValidIdentifierName(node.name.fromJust())) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_IMPORT_SPECIFIER_NAME));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceLabeledStatement(@Nonnull LabeledStatement node, @Nonnull ValidationContext body) {
        ValidationContext s = super.reduceLabeledStatement(node, body);
        if (!checkIsValidIdentifierName(node.label)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_LABEL));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node) {
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

    @Nonnull
    @Override
    public ValidationContext reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node) {
        ValidationContext s = super.reduceLiteralRegExpExpression(node);
        if (!checkIsLiteralRegExpPattern(node.pattern)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_REG_EX_PATTERN));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceMethod(@Nonnull Method node, @Nonnull ValidationContext params, @Nonnull ValidationContext body, @Nonnull ValidationContext name) {
        ValidationContext s = super.reduceMethod(node, params, body, name);
        if (node.isGenerator) {
            s.clearYieldExpressionsNotInGeneratorContext();
            s.clearYieldGeneratorExpressionsNotInGeneratorContext();
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceModule(@Nonnull Module node, @Nonnull ImmutableList<ValidationContext> directives, @Nonnull ImmutableList<ValidationContext> items
    ) {
        ValidationContext s = super.reduceModule(node, directives, items);
        s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY));
        s.enforceBindingIdentifiersCalledDefault(bindingIdentifier -> new ValidationError(bindingIdentifier, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT));
        s.enforceYieldExpressionsNotInGeneratorContext(yieldExpression -> new ValidationError(yieldExpression, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION));
        s.enforceYieldGeneratorExpressionsNotInGeneratorContext(yieldGeneratorExpression -> new ValidationError(yieldGeneratorExpression, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION));
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceReturnStatement(@Nonnull ReturnStatement node, @Nonnull Maybe<ValidationContext> expression
    ) {
        ValidationContext s = super.reduceReturnStatement(node, expression);
        s.addFreeReturnStatement(node);
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceScript(@Nonnull Script node, @Nonnull ImmutableList<ValidationContext> directives, @Nonnull ImmutableList<ValidationContext> statements
    ) {
        ValidationContext s = super.reduceScript(node, directives, statements);
        s.enforceFreeReturnStatements(returnStatement -> new ValidationError(returnStatement, ValidationErrorMessages.RETURN_STATEMENT_IN_FUNCTION_BODY));
        s.enforceBindingIdentifiersCalledDefault(bindingIdentifier -> new ValidationError(bindingIdentifier, ValidationErrorMessages.BINDING_IDENTIFIERS_CALLED_DEFAULT));
        s.enforceYieldExpressionsNotInGeneratorContext(yieldExpression -> new ValidationError(yieldExpression, ValidationErrorMessages.VALID_YIELD_EXPRESSION_POSITION));
        s.enforceYieldGeneratorExpressionsNotInGeneratorContext(yieldGeneratorExpression -> new ValidationError(yieldGeneratorExpression, ValidationErrorMessages.VALID_YIELD_GENERATOR_EXPRESSION_POSITION));
        return s;
    }


    @Nonnull
    @Override
    public ValidationContext reduceStaticMemberExpression(@Nonnull StaticMemberExpression node, @Nonnull ValidationContext object) {
        ValidationContext s = super.reduceStaticMemberExpression(node, object);
        if (!checkIsValidIdentifierName(node.property)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_STATIC_MEMBER_EXPRESSION_PROPERTY_NAME));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceTemplateElement(@Nonnull TemplateElement node) {
        ValidationContext s = super.reduceTemplateElement(node);
        if (!checkIsTemplateElement(node.rawValue)) {
            s.addError(new ValidationError(node, ValidationErrorMessages.VALID_TEMPLATE_ELEMENT_VALUE));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceTemplateExpression(@Nonnull TemplateExpression node, @Nonnull Maybe<ValidationContext> tag, @Nonnull ImmutableList<ValidationContext> elements) {
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

    @Nonnull
    @Override
    public ValidationContext reduceVariableDeclaration(@Nonnull VariableDeclaration node, @Nonnull ImmutableList<ValidationContext> declarators) {
        ValidationContext s = super.reduceVariableDeclaration(node, declarators);
        if (node.declarators.length == 0) {
            s.addError(new ValidationError(node, ValidationErrorMessages.NOT_EMPTY_VARIABLE_DECLARATORS_LIST));
        }
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceVariableDeclarationStatement(@Nonnull VariableDeclarationStatement node, @Nonnull ValidationContext declaration) {
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

    @Nonnull
    @Override
    public ValidationContext reduceYieldExpression(@Nonnull YieldExpression node, @Nonnull Maybe<ValidationContext> expression) {
        ValidationContext s = super.reduceYieldExpression(node, expression);
        s.addYieldExpressionsNotInGeneratorContext(node);
        return s;
    }

    @Nonnull
    @Override
    public ValidationContext reduceYieldGeneratorExpression(@Nonnull YieldGeneratorExpression node, @Nonnull ValidationContext expression) {
        ValidationContext s = super.reduceYieldGeneratorExpression(node, expression);
        s.addYieldGeneratorExpressionsNotInGeneratorContext(node);
        return s;
    }

    @Nonnull
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
