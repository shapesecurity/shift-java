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


package com.shapesecurity.shift.fuzzer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.ast.operators.BinaryOperator;
import com.shapesecurity.shift.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.ast.operators.UnaryOperator;
import com.shapesecurity.shift.ast.operators.UpdateOperator;
import com.shapesecurity.shift.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Fuzzer {
    private static final String identifierStart = "_$abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String identifierPart = identifierStart + "0123456789";
    private static final char[] identifierStartArr = identifierStart.toCharArray();
    private static final char[] identifierPartArr = identifierPart.toCharArray();

    private static final int MANY_BOUND = 5;
    private static final int MAX_IDENT_LENGTH = 15;
    private static final int MAX_STRING_LENGTH = 3;
    private static final double SPECIAL_IDENT_PROBABILITY = 0.5;
    private static final String[] RESERVED_WORDS = new String[]{
            "false",
            "null",
            "true",
            "let",
            "if",
            "in",
            "do",
            "var",
            "for",
            "new",
            "try",
            "this",
            "else",
            "case",
            "void",
            "with",
            "enum",
            "while",
            "break",
            "catch",
            "throw",
            "const",
            "class",
            "super",
            "return",
            "typeof",
            "delete",
            "switch",
            "export",
            "import",
            "default",
            "finally",
            "extends",
            "function",
            "continue",
            "debugger",
            "instanceof",
    };
    private static final String[] STRICT_MODE_RESERVED_WORDS = new String[]{
            "implements",
            "interface",
            "package",
            "private",
            "protected",
            "public",
            "static",
            "yield",
            "false",
            "null",
            "true",
            "let",
            "if",
            "in",
            "do",
            "var",
            "for",
            "new",
            "try",
            "this",
            "else",
            "case",
            "void",
            "with",
            "enum",
            "while",
            "break",
            "catch",
            "throw",
            "const",
            "class",
            "super",
            "return",
            "typeof",
            "delete",
            "switch",
            "export",
            "import",
            "default",
            "finally",
            "extends",
            "function",
            "continue",
            "debugger",
            "instanceof",
    };
    private static final String[] RESTRICTED_WORDS = new String[]{
            "arguments",
            "eval",
    };
    private final static int kGenBreakStatement = 0;
    private final static int kGenContinueStatement = 1;
    private final static int kGenWithStatement = 2;
    private final static int kGenReturnStatement = 3;
    private final static int kGenLabeledStatement = 4;
    private final static Gen<AssignmentTarget>[] assignmentTargetGens = Fuzzer.<Gen<AssignmentTarget>>array(
            Fuzzer::randomArrayAssignmentTarget,
            Fuzzer::randomAssignmentTargetIdentifier,
            Fuzzer::randomComputedMemberAssignmentTarget,
            Fuzzer::randomObjectAssignmentTarget,
            Fuzzer::randomStaticMemberAssignmentTarget
    );
    private final static Gen<Binding>[] bindingGens = Fuzzer.<Gen<Binding>>array(
            Fuzzer::randomArrayBinding,
            Fuzzer::randomBindingIdentifier,
            Fuzzer::randomObjectBinding
    );
    private static final Gen<Expression>[] expressionGens = Fuzzer.<Gen<Expression>>array(
            Fuzzer::randomArrayExpression,
            Fuzzer::randomArrowExpression,
            Fuzzer::randomAssignmentExpression,
            Fuzzer::randomCompoundAssignmentExpression,
            Fuzzer::randomBinaryExpression,
            Fuzzer::randomCallExpression,
            Fuzzer::randomComputedMemberExpression,
            Fuzzer::randomConditionalExpression,
            Fuzzer::randomFunctionExpression,
            Fuzzer::randomIdentifierExpression,
            Fuzzer::randomLiteralBooleanExpression,
            Fuzzer::randomLiteralNullExpression,
            Fuzzer::randomLiteralInfinityExpression,
            Fuzzer::randomLiteralNumericExpression,
            Fuzzer::randomLiteralRegExpExpression,
            Fuzzer::randomLiteralStringExpression,
            Fuzzer::randomNewExpression,
            Fuzzer::randomObjectExpression,
            Fuzzer::randomUpdateExpression,
            Fuzzer::randomStaticMemberExpression,
            Fuzzer::randomThisExpression,
            Fuzzer::randomClassExpression,
            Fuzzer::randomNewTargetExpression,
            Fuzzer::randomTemplateExpression,
            Fuzzer::randomUnaryExpression,
            Fuzzer::randomYieldExpression,
            Fuzzer::randomYieldGeneratorExpression
    );
    private final static Gen<ExportDeclaration>[] exportDeclarationGens = Fuzzer.<Gen<ExportDeclaration>>array(
            Fuzzer::randomExport,
            Fuzzer::randomExportAllFrom,
            Fuzzer::randomExportDefault,
            Fuzzer::randomExportFrom,
            Fuzzer::randomExportLocals
    );
    private final static Gen<Statement>[] nonIterationStatementGens = Fuzzer.<Gen<Statement>>array(
            Fuzzer::randomBlockStatement,
            Fuzzer::randomBreakStatement,
            Fuzzer::randomClassDeclaration,
            Fuzzer::randomContinueStatement,
            Fuzzer::randomDebuggerStatement,
            Fuzzer::randomEmptyStatement,
            Fuzzer::randomExpressionStatement,
            Fuzzer::randomFunctionDeclaration,
            Fuzzer::randomIfStatement,
            Fuzzer::randomLabeledStatement,
            Fuzzer::randomReturnStatement,
            Fuzzer::randomSwitchStatement,
            Fuzzer::randomSwitchStatementWithDefault,
            Fuzzer::randomThrowStatement,
            Fuzzer::randomTryCatchStatement,
            Fuzzer::randomTryFinallyStatement,
            Fuzzer::randomVariableDeclarationStatement,
            Fuzzer::randomWithStatement
    );
    private final static Gen<IterationStatement>[] iterationStatementGens = Fuzzer.<Gen<IterationStatement>>array(
            Fuzzer::randomDoWhileStatement,
            Fuzzer::randomForInStatement,
            Fuzzer::randomForOfStatement,
            Fuzzer::randomForStatement,
            Fuzzer::randomWhileStatement
    );

    private final static int totalStatements = nonIterationStatementGens.length + iterationStatementGens.length + 5;

    @SafeVarargs
    private static <T> T[] array(T... arr) {
        return arr;
    }

    private static <T> Gen<T> choice(T[] arr) {
        return (ctx, depth) -> arr[ctx.random.nextInt(arr.length)];
    }

    @NotNull
    public static Program generate(@NotNull Random random, int depth) {
        return random.nextBoolean() ? randomScript(new GenCtx(random), depth) : randomModule(new GenCtx(random), depth);
    }

    private static boolean isFunctionBodyStrict(FunctionBody body) {
        for (Directive directive : body.directives) {
            if (directive.rawValue.equals("use strict")) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    private static <T> Gen<ImmutableList<T>> many(final int bound, @NotNull Gen<T> gen) {
        return (ctx, depth) -> {
            if (depth <= 0) {
                return ImmutableList.nil();
            }
            int number = ctx.random.nextInt(bound);
            ImmutableList<T> result = ImmutableList.nil();
            for (int i = 0; i < number; i++) {
                result = result.cons(gen.apply(ctx, depth));
            }
            return result;
        };
    }

    @NotNull
    private static <T> Gen<ImmutableList<T>> many(@NotNull Gen<T> gen) {
        return many(MANY_BOUND, gen);
    }

    @NotNull
    private static <T> Gen<NonEmptyImmutableList<T>> many1(@NotNull Gen<T> gen) {
        return (ctx, depth) -> many(MANY_BOUND - 1, gen).apply(ctx, depth).cons(gen.apply(ctx, depth));
    }

    @NotNull
    private static <T> Gen<Maybe<T>> optional(@NotNull Gen<T> gen) {
        return (ctx, depth) -> {
            if (depth <= 0) {
                return Maybe.nothing();
            }
            if (ctx.random.nextBoolean()) {
                return Maybe.nothing();
            }
            return Maybe.just(gen.apply(ctx, depth));
        };
    }

    @NotNull
    private static ArrayBinding randomArrayBinding(@NotNull GenCtx ctx, int depth) {
        return new ArrayBinding(many(optional(Fuzzer::randomBindingBindingWithDefault)).apply(ctx, depth - 1), optional(Fuzzer::randomBinding).apply(ctx, depth - 1));
    }

    @NotNull
    private static ArrayAssignmentTarget randomArrayAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        return new ArrayAssignmentTarget(many(optional(Fuzzer::randomAssignmentTargetAssignmentTargetWithDefault)).apply(ctx, depth - 1), optional(Fuzzer::randomAssignmentTarget).apply(ctx, depth - 1));
    }

    @NotNull
    private static ArrayExpression randomArrayExpression(@NotNull GenCtx ctx, int depth) {
        return new ArrayExpression(many(optional(Fuzzer::randomSpreadElementExpression)).apply(ctx, depth - 1));
    }

    @NotNull
    private static ArrowExpression randomArrowExpression(@NotNull GenCtx ctx, int depth) {
        return new ArrowExpression(randomFormalParameters(ctx, depth - 1), randomFunctionBodyExpression(ctx, depth - 1));
    }

    @NotNull
    private static AssignmentExpression randomAssignmentExpression(@NotNull GenCtx ctx, int depth) {
        return new AssignmentExpression(randomAssignmentTarget(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static AssignmentTarget randomAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        return choice(assignmentTargetGens).apply(ctx, depth).apply(ctx, depth - 1);
    }


    @NotNull
    private static BinaryExpression randomBinaryExpression(@NotNull GenCtx ctx, int depth) {
        return new BinaryExpression(randomExpression(ctx, depth - 1), choice(BinaryOperator.values()).apply(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static Binding randomBinding(@NotNull GenCtx ctx, int depth) {
//        if (ctx.inForInOfStatement) { // todo why was this here?
//            return randomBindingIdentifier(ctx, depth - 1);
//        }
        return choice(bindingGens).apply(ctx, depth).apply(ctx, depth - 1);
    }

    @NotNull
    private static BindingBindingWithDefault randomBindingBindingWithDefault(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomBinding(ctx, depth);
        } else {
            return randomBindingWithDefault(ctx, depth);
        }
    }

    @NotNull
    private static AssignmentTargetAssignmentTargetWithDefault randomAssignmentTargetAssignmentTargetWithDefault(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomAssignmentTarget(ctx, depth);
        } else {
            return randomAssignmentTargetWithDefault(ctx, depth);
        }
    }

    @NotNull
    private static BindingIdentifier randomBindingIdentifier(@NotNull GenCtx ctx, int depth) {
        return new BindingIdentifier(randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static AssignmentTargetIdentifier randomAssignmentTargetIdentifier(@NotNull GenCtx ctx, int depth) {
        return new AssignmentTargetIdentifier(randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static SimpleAssignmentTarget randomSimpleAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomAssignmentTargetIdentifier(ctx, depth);
        } else {
            number = ctx.random.nextInt();
            if (number % 2 == 0) {
                return randomComputedMemberAssignmentTarget(ctx, depth);
            } else {
                return randomStaticMemberAssignmentTarget(ctx, depth);
            }
        }
    }

    @NotNull
    private static BindingProperty randomBindingProperty(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomBindingPropertyIdentifier(ctx, depth);
        } else {
            return randomBindingPropertyProperty(ctx, depth);
        }
    }

    @NotNull
    private static AssignmentTargetProperty randomAssignmentTargetProperty(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomAssignmentTargetPropertyIdentifier(ctx, depth);
        } else {
            return randomAssignmentTargetPropertyProperty(ctx, depth);
        }
    }

    @NotNull
    private static BindingPropertyIdentifier randomBindingPropertyIdentifier(@NotNull GenCtx ctx, int depth) {
        return new BindingPropertyIdentifier(randomBindingIdentifier(ctx, depth - 1), optional(Fuzzer::randomExpression).apply(ctx, depth - 1));
    }

    @NotNull
    private static AssignmentTargetPropertyIdentifier randomAssignmentTargetPropertyIdentifier(@NotNull GenCtx ctx, int depth) {
        return new AssignmentTargetPropertyIdentifier(randomAssignmentTargetIdentifier(ctx, depth - 1), optional(Fuzzer::randomExpression).apply(ctx, depth - 1));
    }

    @NotNull
    private static BindingPropertyProperty randomBindingPropertyProperty(@NotNull GenCtx ctx, int depth) {
        return new BindingPropertyProperty(randomPropertyName(ctx, depth - 1), randomBindingBindingWithDefault(ctx, depth - 1));
    }

    @NotNull
    private static AssignmentTargetPropertyProperty randomAssignmentTargetPropertyProperty(@NotNull GenCtx ctx, int depth) {
        return new AssignmentTargetPropertyProperty(randomPropertyName(ctx, depth - 1), randomAssignmentTargetAssignmentTargetWithDefault(ctx, depth - 1));
    }

    @NotNull
    private static BindingWithDefault randomBindingWithDefault(@NotNull GenCtx ctx, int depth) {
        return new BindingWithDefault(randomBinding(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static AssignmentTargetWithDefault randomAssignmentTargetWithDefault(@NotNull GenCtx ctx, int depth) {
        return new AssignmentTargetWithDefault(randomAssignmentTarget(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static Block randomBlock(@NotNull GenCtx ctx, int depth) {
        if (depth < 1) {
            return new Block(ImmutableList.nil());
        }
        return new Block(many(Fuzzer::randomStatement).apply(ctx.allowMissingElse(), depth - 1));
    }

    @NotNull
    private static BlockStatement randomBlockStatement(@NotNull GenCtx ctx, int depth) {
        return new BlockStatement(randomBlock(ctx.allowMissingElse(), depth - 1));
    }

    @NotNull
    private static BreakStatement randomBreakStatement(@NotNull GenCtx ctx, int depth) {
        return new BreakStatement(optional(Fuzzer::randomIdentifierString).apply(ctx, depth - 1));
    }

    @NotNull
    private static CallExpression randomCallExpression(@NotNull GenCtx ctx, int depth) {
        return new CallExpression(randomExpressionSuper(ctx, depth - 1), many(Fuzzer::randomSpreadElementExpression).apply(ctx, depth - 1));
    }

    @NotNull
    private static CatchClause randomCatchClause(@NotNull GenCtx ctx, int depth) {
        Binding binding = (Binding) randomParameter(ctx, depth - 1);
        return new CatchClause(binding, randomBlock(ctx, depth - 1));
    }

    @NotNull
    private static ClassDeclaration randomClassDeclaration(@NotNull GenCtx ctx, int depth) {
        return new ClassDeclaration(randomBindingIdentifier(ctx, depth - 1), optional(Fuzzer::randomExpression).apply(ctx, depth - 1), many(Fuzzer::randomClassElement).apply(ctx, depth - 1));
    }

    @NotNull
    private static ClassElement randomClassElement(@NotNull GenCtx ctx, int depth) {
        return new ClassElement(false, randomMethodDefinition(ctx, depth - 1));
    }

    @NotNull
    private static ClassExpression randomClassExpression(@NotNull GenCtx ctx, int depth) {
        return new ClassExpression(optional(Fuzzer::randomBindingIdentifier).apply(ctx, depth - 1), optional(Fuzzer::randomExpression).apply(ctx, depth - 1), many(Fuzzer::randomClassElement).apply(ctx, depth - 1));
    }

    @NotNull
    private static CompoundAssignmentExpression randomCompoundAssignmentExpression(@NotNull GenCtx ctx, int depth) {
        SimpleAssignmentTarget lhs;
        do {
            lhs = randomSimpleAssignmentTarget(ctx, depth - 1);
        }
        while (ctx.inStrictMode && lhs instanceof IdentifierExpression && Utils.isRestrictedWord(((IdentifierExpression) lhs).name));
        return new CompoundAssignmentExpression(
                lhs,
                choice(CompoundAssignmentOperator.values()).apply(ctx, depth - 1),
                randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static ComputedMemberExpression randomComputedMemberExpression(@NotNull GenCtx ctx, int depth) {
        return new ComputedMemberExpression(randomExpression(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static ComputedMemberAssignmentTarget randomComputedMemberAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        return new ComputedMemberAssignmentTarget(randomExpression(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static ComputedPropertyName randomComputedPropertyName(@NotNull GenCtx ctx, int depth) {
        return new ComputedPropertyName(randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static ConditionalExpression randomConditionalExpression(@NotNull GenCtx ctx, int depth) {
        return new ConditionalExpression(randomExpression(ctx, depth - 1), randomExpression(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static ContinueStatement randomContinueStatement(@NotNull GenCtx ctx, int depth) {
        return new ContinueStatement(optional(Fuzzer::randomIdentifierString).apply(ctx, depth - 1));
    }

    @NotNull
    private static DataProperty randomDataProperty(@NotNull GenCtx ctx, int depth) {
        return new DataProperty(randomPropertyName(ctx, depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static DebuggerStatement randomDebuggerStatement(@NotNull GenCtx ctx, int depth) {
        return new DebuggerStatement();
    }

    @NotNull
    private static Directive randomDirective(@NotNull GenCtx ctx, int depth) {
        String value = Utils.escapeStringLiteral(randomIdentifierString(ctx, depth - 1));
        return new Directive(value.substring(1, value.length() - 1));
    }

    @NotNull
    private static DoWhileStatement randomDoWhileStatement(@NotNull GenCtx ctx, int depth) {
        return new DoWhileStatement(randomStatement(ctx.enterIteration().allowMissingElse(), depth - 1), randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static EmptyStatement randomEmptyStatement(@NotNull GenCtx ctx, int depth) {
        return new EmptyStatement();
    }

    @NotNull
    private static Export randomExport(@NotNull GenCtx ctx, int depth) {
        return new Export(randomFunctionDeclarationClassDeclarationVariableDeclaration(ctx, depth - 1));
    }

    @NotNull
    private static ExportAllFrom randomExportAllFrom(@NotNull GenCtx ctx, int depth) {
        return new ExportAllFrom(randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static ExportDeclaration randomExportDeclaration(@NotNull GenCtx ctx, int depth) {
        return choice(exportDeclarationGens).apply(ctx, depth - 1).apply(ctx, depth - 1);
    }

    @NotNull
    private static ExportDefault randomExportDefault(@NotNull GenCtx ctx, int depth) {
        return new ExportDefault(randomFunctionDeclarationClassDeclarationExpression(ctx, depth - 1));
    }

    @NotNull
    private static ExportFrom randomExportFrom(@NotNull GenCtx ctx, int depth) {
        return new ExportFrom(many(Fuzzer::randomExportFromSpecifier).apply(ctx, depth - 1), randomIdentifierString(ctx, depth-1));
    }

    @NotNull
    private static ExportFromSpecifier randomExportFromSpecifier(@NotNull GenCtx ctx, int depth) {
        return new ExportFromSpecifier(randomIdentifierString(ctx, depth-1), optional(Fuzzer::randomIdentifierString).apply(ctx, depth - 1));
    }

    @NotNull
    private static ExportLocals randomExportLocals(@NotNull GenCtx ctx, int depth) {
        return new ExportLocals(many(Fuzzer::randomExportLocalSpecifier).apply(ctx, depth - 1));
    }

    @NotNull
    private static ExportLocalSpecifier randomExportLocalSpecifier(@NotNull GenCtx ctx, int depth) {
        return new ExportLocalSpecifier(randomIdentifierExpression(ctx, depth-1), optional(Fuzzer::randomIdentifierString).apply(ctx, depth - 1));
    }

    @NotNull
    private static Expression randomExpression(@NotNull GenCtx ctx, int depth) {
        if (depth < 0) {
            return randomLiteralStringExpression(ctx, depth - 1);
        }
        Expression expression = choice(expressionGens).apply(ctx, depth - 1).apply(ctx, depth - 1);
        if (!ctx.allowYieldExpression) {
            while (expression instanceof YieldExpression || expression instanceof YieldGeneratorExpression) {
                expression = choice(expressionGens).apply(ctx, depth - 1).apply(ctx, depth - 1);
            }
        }
        return expression;
    }

    @NotNull
    private static ExpressionStatement randomExpressionStatement(@NotNull GenCtx ctx, int depth) {
        return new ExpressionStatement(randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static ExpressionSuper randomExpressionSuper(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomExpression(ctx, depth);
        } else {
            return randomSuper();
        }
    }

    @NotNull
    private static ImmutableList<ExpressionTemplateElement> randomAlternatingTemplateElementExpression(@NotNull GenCtx ctx, int depth) {
        return ImmutableList.list(randomTemplateElement(ctx, depth - 1), randomExpression(ctx, depth - 1), randomTemplateElement(ctx, depth - 1));
    }

    @NotNull
    private static ForInStatement randomForInStatement(@NotNull GenCtx ctx, int depth) {
        return new ForInStatement(randomVariableDeclarationAssignmentTarget(ctx.inForInOfStatement(), depth - 1), randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
    }

    @NotNull
    private static ForOfStatement randomForOfStatement(@NotNull GenCtx ctx, int depth) {
        return new ForOfStatement(randomVariableDeclarationAssignmentTarget(ctx.inForInOfStatement(), depth - 1), randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
    }

    @NotNull
    private static ForStatement randomForStatement(@NotNull GenCtx ctx, int depth) {
        return new ForStatement(optional(Fuzzer::randomVariableDeclarationExpression).apply(ctx, depth - 1), optional(Fuzzer::randomExpression).apply(ctx, depth - 1), optional(Fuzzer::randomExpression).apply(ctx, depth - 1), randomStatement(ctx, depth - 1));
    }

    @NotNull
    private static FormalParameters randomFormalParameters(@NotNull GenCtx ctx, int depth) {
        return new FormalParameters(many(Fuzzer::randomParameter).apply(ctx, depth - 1), optional(Fuzzer::randomBinding).apply(ctx, depth - 1));
    }

    @NotNull
    private static Parameter randomParameter(@NotNull GenCtx ctx, int depth) {
        // returns nodes that are Binding, but not Member Expression
        switch (ctx.random.nextInt(3)) {
            case 0:
                return randomArrayBinding(ctx, depth);
            case 1:
                return randomObjectBinding(ctx, depth);
            default:
                return randomBindingIdentifier(ctx, depth);
        }
    }

    @NotNull
    private static FunctionBody randomFunctionBody(@NotNull GenCtx ctx, int depth) {
        ImmutableList<Directive> directives = many(Fuzzer::randomDirective).apply(ctx, depth - 1);
        if (!ctx.inStrictMode && directives.exists(dir -> dir != null)) {
            ctx = ctx.enterStrictMode();
        }
        return new FunctionBody(directives, many(Fuzzer::randomStatement).apply(ctx.enterFunctional(), depth - 1));
    }

    private static FunctionBodyExpression randomFunctionBodyExpression(GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomFunctionBody(ctx, depth);
        } else {
            return randomExpression(ctx, depth);
        }
    }

    @NotNull
    private static FunctionDeclaration randomFunctionDeclaration(@NotNull GenCtx ctx, int depth) {
        FunctionBody body = randomFunctionBody(ctx.enterFunctional().clearLabels(), depth - 1);
        if (isFunctionBodyStrict(body)) {
            ctx = ctx.enterStrictMode();
        }
        boolean isGenerator = ctx.random.nextBoolean();
        if (isGenerator) {
            ctx = ctx.inGeneratorFunction();
        }
        return new FunctionDeclaration(isGenerator, randomBindingIdentifier(ctx, depth - 1), randomFormalParameters(ctx, depth - 1), body);
    }

    @NotNull
    private static FunctionDeclarationClassDeclarationExpression randomFunctionDeclarationClassDeclarationExpression(@NotNull GenCtx ctx, int depth) {
        switch (ctx.random.nextInt(3)) {
            case 0:
                return randomFunctionDeclaration(ctx, depth);
            case 1:
                return randomClassDeclaration(ctx, depth);
            default:
                return randomExpression(ctx, depth);
        }
    }

    @NotNull
    private static FunctionDeclarationClassDeclarationVariableDeclaration randomFunctionDeclarationClassDeclarationVariableDeclaration(@NotNull GenCtx ctx, int depth) {
        switch (ctx.random.nextInt(3)) {
            case 0:
                return randomFunctionDeclaration(ctx, depth);
            case 1:
                return randomClassDeclaration(ctx, depth);
            default:
                return randomVariableDeclaration(ctx, depth);
        }
    }

    @NotNull
    private static FunctionExpression randomFunctionExpression(@NotNull GenCtx ctx, int depth) {
        FunctionBody body = randomFunctionBody(ctx.enterFunctional(), depth - 1);
        if (isFunctionBodyStrict(body)) {
            ctx = ctx.enterStrictMode();
        }
        boolean isGenerator = ctx.random.nextBoolean();
        if (isGenerator) {
            ctx = ctx.inGeneratorFunction();
        }
        return new FunctionExpression(isGenerator, optional(Fuzzer::randomBindingIdentifier).apply(ctx, depth - 1), randomFormalParameters(ctx, depth - 1), body);
    }

    @NotNull
    private static Getter randomGetter(@NotNull GenCtx ctx, int depth) {
        FunctionBody body = randomFunctionBody(ctx.enterFunctional(), depth - 1);
        if (isFunctionBodyStrict(body)) {
            ctx = ctx.enterStrictMode();
        }
        return new Getter(randomPropertyName(ctx, depth - 1), body);
    }

    @NotNull
    private static IdentifierExpression randomIdentifier(@NotNull GenCtx ctx, int depth, boolean allowReserved, boolean allowRestricted) {
        String name;
        boolean genarateSpecial = ctx.random.nextDouble() < SPECIAL_IDENT_PROBABILITY;
        if (genarateSpecial && allowReserved) {
            if (ctx.inStrictMode) {
                name = choice(STRICT_MODE_RESERVED_WORDS).apply(ctx, depth - 1);
            } else {
                name = choice(RESERVED_WORDS).apply(ctx, depth - 1);
            }
        } else if (genarateSpecial && allowRestricted) {
            name = choice(RESTRICTED_WORDS).apply(ctx, depth - 1);
        } else {
            boolean disallow;
            do {
                disallow = false;
                name = randomIdentifierString(ctx, depth - 1);
                for (String reservedWord : (ctx.inStrictMode ? STRICT_MODE_RESERVED_WORDS : RESERVED_WORDS)) {
                    if (reservedWord.equals(name)) {
                        disallow = true;
                    }
                }
            } while (disallow);
        }
        return new IdentifierExpression(name);
    }

    @NotNull
    private static IdentifierExpression randomIdentifierExpression(@NotNull GenCtx ctx, int depth) {
        // restricted word are filtered in assignment expression.
        return randomIdentifier(ctx, depth - 1, false, true);
    }

    @NotNull
    private static String randomIdentifierString(@NotNull GenCtx ctx, int depth) {
        StringBuilder result = new StringBuilder();
        result.append(identifierStartArr[ctx.random.nextInt(identifierStartArr.length)]);
        int length = ctx.random.nextInt(MAX_IDENT_LENGTH);
        for (int i = 0; i < length; i++) {
            result.append(identifierPartArr[ctx.random.nextInt(identifierPartArr.length)]);
        }
        return result.toString();
    }

    @NotNull
    private static IfStatement randomIfStatement(@NotNull GenCtx ctx, int depth) {
        if (ctx.allowMissingElse) {
            boolean missElse = ctx.random.nextBoolean();
            if (missElse) {
                return new IfStatement(randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1), Maybe.nothing());
            }
        }
        return new IfStatement(randomExpression(ctx, depth - 1), randomStatement(ctx.forbidMissingElse(), depth - 1), Maybe.just(randomStatement(ctx, depth - 1)));
    }

    @NotNull
    private static Import randomImport(@NotNull GenCtx ctx, int depth) {
        return new Import(optional(Fuzzer::randomBindingIdentifier).apply(ctx, depth - 1), many(Fuzzer::randomImportSpecifier).apply(ctx, depth - 1), randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static ImportDeclaration randomImportDeclaration(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomImport(ctx, depth);
        } else {
            return randomImportNamespace(ctx, depth);
        }
    }

    @NotNull
    private static ImportDeclarationExportDeclarationStatement randomImportDeclarationExportDeclarationStatement(@NotNull GenCtx ctx, int depth) {
        switch (ctx.random.nextInt(3)) {
            case 0:
                return randomImportDeclaration(ctx, depth);
            case 1:
                return randomExportDeclaration(ctx, depth);
            default:
                return randomStatement(ctx, depth);
        }
    }

    @NotNull
    private static ImportNamespace randomImportNamespace(@NotNull GenCtx ctx, int depth) {
        return new ImportNamespace(optional(Fuzzer::randomBindingIdentifier).apply(ctx, depth - 1), randomBindingIdentifier(ctx, depth - 1), randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static ImportSpecifier randomImportSpecifier(@NotNull GenCtx ctx, int depth) {
        return new ImportSpecifier(optional(Fuzzer::randomIdentifierString).apply(ctx, depth - 1), randomBindingIdentifier(ctx, depth - 1));
    }

    @NotNull
    private static LabeledStatement randomLabeledStatement(@NotNull GenCtx ctx, int depth) {
        return new LabeledStatement(randomIdentifierString(ctx, depth - 1), randomStatement(ctx, depth - 1));
    }

    @NotNull
    private static LiteralBooleanExpression randomLiteralBooleanExpression(@NotNull GenCtx ctx, int depth) {
        return new LiteralBooleanExpression(ctx.random.nextBoolean());
    }

    @NotNull
    private static LiteralInfinityExpression randomLiteralInfinityExpression(@NotNull GenCtx ctx, int depth) {
        return new LiteralInfinityExpression();
    }

    @NotNull
    private static LiteralNullExpression randomLiteralNullExpression(@NotNull GenCtx ctx, int depth) {
        return new LiteralNullExpression();
    }

    @NotNull
    private static LiteralNumericExpression randomLiteralNumericExpression(@NotNull GenCtx ctx, int depth) {
        return new LiteralNumericExpression(randomNumber(ctx));
    }

    @NotNull
    private static LiteralRegExpExpression randomLiteralRegExpExpression(@NotNull GenCtx ctx, int depth) {
        return new LiteralRegExpExpression(randomRegExpString(ctx, depth - 1), ctx.random.nextBoolean(), ctx.random.nextBoolean(), ctx.random.nextBoolean(), ctx.random.nextBoolean(), ctx.random.nextBoolean());
    }

    @NotNull
    private static LiteralStringExpression randomLiteralStringExpression(@NotNull GenCtx ctx, int depth) {
        return new LiteralStringExpression(randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static MemberExpression randomMemberExpression(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomComputedMemberExpression(ctx, depth);
        } else {
            return randomStaticMemberExpression(ctx, depth);
        }
    }

    @NotNull
    private static Method randomMethod(@NotNull GenCtx ctx, int depth) {
        return new Method(false, randomPropertyName(ctx, depth - 1), randomFormalParameters(ctx, depth - 1), randomFunctionBody(ctx, depth - 1));
    }

    @NotNull
    private static MethodDefinition randomMethodDefinition(@NotNull GenCtx ctx, int depth) {
        switch (ctx.random.nextInt(3)) {
            case 0:
                return randomGetter(ctx, depth);
            case 1:
                return randomSetter(ctx, depth);
            default:
                return randomMethod(ctx, depth);
        }
    }

    @NotNull
    private static Module randomModule(@NotNull GenCtx ctx, int depth) {
        return new Module(many(Fuzzer::randomDirective).apply(ctx, depth - 1), many(Fuzzer::randomImportDeclarationExportDeclarationStatement).apply(ctx, depth - 1));
    }

    @NotNull
    private static NewExpression randomNewExpression(@NotNull GenCtx ctx, int depth) {
        return new NewExpression(randomExpression(ctx, depth - 1), many(Fuzzer::randomSpreadElementExpression).apply(ctx, depth - 1));
    }

    @NotNull
    private static NewTargetExpression randomNewTargetExpression(@NotNull GenCtx ctx, int depth) {
        return new NewTargetExpression();
    }

    @NotNull
    private static double randomNumber(@NotNull GenCtx ctx) {
        return Math.exp(ctx.random.nextGaussian());
    }

    @NotNull
    private static ObjectBinding randomObjectBinding(@NotNull GenCtx ctx, int depth) {
        return new ObjectBinding(many(Fuzzer::randomBindingProperty).apply(ctx, depth - 1));
    }

    @NotNull
    private static ObjectAssignmentTarget randomObjectAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        return new ObjectAssignmentTarget(many(Fuzzer::randomAssignmentTargetProperty).apply(ctx, depth - 1));
    }

    @NotNull
    private static ObjectExpression randomObjectExpression(@NotNull GenCtx ctx, int depth) {
        return new ObjectExpression(many(Fuzzer::randomObjectProperty).apply(ctx, depth - 1));
    }

    @NotNull
    private static ObjectProperty randomObjectProperty(@NotNull GenCtx ctx, int depth) {
        switch (ctx.random.nextInt(5)) {
            case 0:
                return randomDataProperty(ctx, depth);
            case 1:
                return randomShorthandProperty(ctx, depth);
            default:
                return randomMethodDefinition(ctx, depth);
        }
    }

    @NotNull
    private static PropertyName randomPropertyName(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomComputedPropertyName(ctx, depth);
        } else {
            return randomStaticPropertyName(ctx, depth);
        }
    }

    @NotNull
    private static String randomRegExpString(@NotNull GenCtx ctx, int depth) {
        return "/" + randomIdentifierString(ctx, depth - 1) + "/";
    }

    @NotNull
    private static Statement randomReturnStatement(@NotNull GenCtx ctx, int depth) {
        if (ctx.allowReturn) {
            return new ReturnStatement(optional(Fuzzer::randomExpression).apply(ctx, depth - 1));
        } else {
            return new EmptyStatement();
        }
    }

    @NotNull
    private static Script randomScript(@NotNull GenCtx ctx, int depth) {
        FunctionBody randomFunctionBody = randomFunctionBody(ctx, depth - 1);
        return new Script(randomFunctionBody.directives, randomFunctionBody.statements);
    }

    @NotNull
    private static Setter randomSetter(@NotNull GenCtx ctx, int depth) {
        FunctionBody body = randomFunctionBody(ctx.enterFunctional(), depth - 1);
        if (isFunctionBodyStrict(body)) {
            ctx = ctx.enterStrictMode();
        }
        return new Setter(randomPropertyName(ctx, depth - 1), randomParameter(ctx, depth - 1), body);
    }

    @NotNull
    private static ShorthandProperty randomShorthandProperty(@NotNull GenCtx ctx, int depth) {
        return new ShorthandProperty(randomIdentifierExpression(ctx, depth - 1));
    }

    @NotNull
    private static SpreadElement randomSpreadElement(@NotNull GenCtx ctx, int depth) {
        return new SpreadElement(randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static SpreadElementExpression randomSpreadElementExpression(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomSpreadElement(ctx, depth);
        } else {
            return randomExpression(ctx, depth);
        }
    }

    @NotNull
    private static Statement randomStatement(@NotNull GenCtx ctx, int depth) {
        return randomStatementGeneric(ctx, depth, true);
    }

    @NotNull
    private static Statement randomStatementGeneric(@NotNull GenCtx ctx, int depth, boolean allowIteration) {
        if (depth <= 0) {
            switch (ctx.random.nextInt(ctx.inIteration ? 4 : 2)) {
                case 0:
                    return randomDebuggerStatement(ctx, depth - 1);
                case 1:
                    return randomEmptyStatement(ctx, depth - 1);
                case 2:
                    return randomBreakStatement(ctx, depth - 1);
                default:
                    return randomContinueStatement(ctx, depth - 1);
            }
        }
        int n;
        int total = totalStatements - (allowIteration ? 0 : iterationStatementGens.length);
        do {
            n = ctx.random.nextInt(total);
            switch (n) {
                case kGenBreakStatement:
                    if (ctx.labelsInFunctionBoundary.length == 0 && !ctx.inSwitch && !ctx.inIteration) {
                        continue;
                    }
                    break;
                case kGenContinueStatement:
                    if (!ctx.inIteration) {
                        continue;
                    }
                    break;
                case kGenWithStatement:
                    if (ctx.inStrictMode) {
                        continue;
                    }
                    break;
                case kGenReturnStatement:
                    if (!ctx.inFunctional) {
                        continue;
                    }
                    break;
            }
            break;
        } while (true);

        switch (n) {
            case kGenBreakStatement:
                if ((!ctx.inSwitch && !ctx.inIteration) || ctx.labelsInFunctionBoundary.length > 0 && ctx.random.nextBoolean()) {
                    // with label
//        Maybe<String> label = ctx.labelsInFunctionBoundary.index(ctx.random.nextInt(ctx.labelsInFunctionBoundary.length));
                    Maybe<String> label = optional(Fuzzer::randomIdentifierString).apply(ctx, depth);
                    return new BreakStatement(label);
                } else {
                    return new BreakStatement(Maybe.nothing());
                }
            case kGenContinueStatement:
                if (ctx.iterationLabelsInFunctionBoundary.length > 0 && ctx.random.nextBoolean()) {
                    // with label
//        Maybe<IdentifierExpression> label = ctx.iterationLabelsInFunctionBoundary.index(
//            ctx.random.nextInt(ctx.iterationLabelsInFunctionBoundary.length));
                    Maybe<String> label = optional(Fuzzer::randomIdentifierString).apply(ctx, depth);
                    return new ContinueStatement(label);
                } else {
                    return new ContinueStatement(Maybe.nothing());
                }
            case kGenWithStatement:
                return new WithStatement(randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
            case kGenReturnStatement:
                if (ctx.allowReturn) {
                    if (ctx.random.nextBoolean()) {
                        return new ReturnStatement(Maybe.just(randomExpression(ctx, depth - 1)));
                    } else {
                        return new ReturnStatement(Maybe.nothing());
                    }
                } else {
                    return new EmptyStatement();
                }
            case kGenLabeledStatement:
                IdentifierExpression label = randomIdentifier(ctx, depth - 1, false, true);
                while (ctx.labels.exists(label::equals)) {
                    label = randomIdentifier(ctx, depth - 1, false, true);
                }
                int bodyN = ctx.random.nextInt(totalStatements);
                if (bodyN < iterationStatementGens.length) {
                    // generate iteration statement
                    IterationStatement body = iterationStatementGens[bodyN].apply(ctx.withIterationLabel(label), depth - 1);
                    return new LabeledStatement(label.name, body);
                } else {
                    Statement body = randomStatementGeneric(ctx.withLabel(label), depth - 1, false);
                    return new LabeledStatement(label.name, body);
                }
            default:
                if (n < nonIterationStatementGens.length + 5) {
                    return nonIterationStatementGens[n - 5].apply(ctx, depth - 1);
                } else {
                    return iterationStatementGens[n - nonIterationStatementGens.length - 5].apply(ctx, depth - 1);
                }
        }
    }

    @NotNull
    private static StaticMemberExpression randomStaticMemberExpression(@NotNull GenCtx ctx, int depth) {
        return new StaticMemberExpression(randomExpressionSuper(ctx, depth - 1), randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static StaticMemberAssignmentTarget randomStaticMemberAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        return new StaticMemberAssignmentTarget(randomExpressionSuper(ctx, depth - 1), randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static StaticPropertyName randomStaticPropertyName(@NotNull GenCtx ctx, int depth) {
        return new StaticPropertyName(randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static String randomString(@NotNull GenCtx ctx, int depth) {
        int length = ctx.random.nextInt(MAX_STRING_LENGTH);
        StringBuilder sb = new StringBuilder();
        ctx.random.ints(length, 20, 127).forEach((i) -> sb.append((char) i));
        return sb.toString();
    }

    @NotNull
    private static Super randomSuper() {
        return new Super();
    }

    @NotNull
    private static SwitchCase randomSwitchCase(@NotNull GenCtx ctx, int depth) {
        return new SwitchCase(randomExpression(ctx, depth - 1), many(Fuzzer::randomStatement).apply(ctx, depth - 1));
    }

    @NotNull
    private static SwitchDefault randomSwitchDefault(@NotNull GenCtx ctx, int depth) {
        return new SwitchDefault(many(Fuzzer::randomStatement).apply(ctx, depth - 1));
    }

    @NotNull
    private static SwitchStatement randomSwitchStatement(@NotNull GenCtx ctx, int depth) {
        ctx = ctx.allowMissingElse().enterSwitch();
        return new SwitchStatement(
                randomExpression(ctx, depth - 1),
                Fuzzer.many(Fuzzer::randomSwitchCase).apply(ctx, depth - 1));
    }

    @NotNull
    private static SwitchStatementWithDefault randomSwitchStatementWithDefault(@NotNull GenCtx ctx, int depth) {
        ctx = ctx.allowMissingElse().enterSwitch();
        return new SwitchStatementWithDefault(
                randomExpression(ctx, depth - 1),
                Fuzzer.many(Fuzzer::randomSwitchCase).apply(ctx, depth - 1),
                randomSwitchDefault(ctx, depth - 1),
                Fuzzer.many(Fuzzer::randomSwitchCase).apply(ctx, depth - 1));
    }

    @NotNull
    private static TemplateElement randomTemplateElement(@NotNull GenCtx ctx, int depth) {
        return new TemplateElement(randomIdentifierString(ctx, depth - 1));
    }

    @NotNull
    private static TemplateExpression randomTemplateExpression(@NotNull GenCtx ctx, int depth) {
        return new TemplateExpression(optional(Fuzzer::randomExpression).apply(ctx, depth - 1), randomAlternatingTemplateElementExpression(ctx, depth - 1));
    }

    @NotNull
    private static ThisExpression randomThisExpression(@NotNull GenCtx ctx, int depth) {
        return new ThisExpression();
    }

    @NotNull
    private static ThrowStatement randomThrowStatement(@NotNull GenCtx ctx, int depth) {
        return new ThrowStatement(randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static TryCatchStatement randomTryCatchStatement(@NotNull GenCtx ctx, int depth) {
        return new TryCatchStatement(randomBlock(ctx, depth - 1), randomCatchClause(ctx, depth - 1));
    }

    @NotNull
    private static TryFinallyStatement randomTryFinallyStatement(@NotNull GenCtx ctx, int depth) {
        return new TryFinallyStatement(
                randomBlock(ctx, depth - 1),
                optional(Fuzzer::randomCatchClause).apply(ctx, depth - 1),
                randomBlock(ctx, depth - 1));
    }

    @NotNull
    private static UnaryExpression randomUnaryExpression(@NotNull GenCtx ctx, int depth) {
        UnaryOperator operator = choice(UnaryOperator.values()).apply(ctx, depth - 1);
        return new UnaryExpression(operator, randomExpression(ctx, depth - 1));
    }

    @NotNull
    private static UpdateExpression randomUpdateExpression(@NotNull GenCtx ctx, int depth) {
        UpdateOperator operator = choice(UpdateOperator.values()).apply(ctx, depth - 1);
        return new UpdateExpression(false, operator, randomSimpleAssignmentTarget(ctx, depth - 1));
    }

    @NotNull
    private static VariableDeclaration randomVariableDeclaration(@NotNull GenCtx ctx, int depth) {
        VariableDeclarationKind kind = choice(VariableDeclarationKind.values()).apply(ctx, depth - 1);
        if (kind.name.equals("const")) {
            ctx = ctx.variableDeclarationKindIsConst();
        }
        return new VariableDeclaration(ctx.inStrictMode ? VariableDeclarationKind.Var : kind, ctx.inForInOfStatement ? ImmutableList.list(randomVariableDeclaratorWithoutInit(ctx, depth - 1)) : many1(Fuzzer::randomVariableDeclarator).apply(ctx, depth - 1));
    }

    @NotNull
    private static VariableDeclarationAssignmentTarget randomVariableDeclarationAssignmentTarget(@NotNull GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomVariableDeclaration(ctx, depth);
//            return randomVariableDeclarationWithoutInit(ctx, depth);
        } else {
            return randomAssignmentTarget(ctx, depth);
        }
    }

    @NotNull
    private static VariableDeclarator randomVariableDeclaratorWithoutInit(@NotNull GenCtx ctx, int depth) {
        Binding binding = (Binding) randomParameter(ctx, depth - 1);
        return new VariableDeclarator(binding, Maybe.nothing());
    }

    @NotNull
    private static VariableDeclarationExpression randomVariableDeclarationExpression(GenCtx ctx, int depth) {
        int number = ctx.random.nextInt();
        if (number % 2 == 0) {
            return randomVariableDeclaration(ctx, depth);
        } else {
            return randomExpression(ctx, depth);
        }
    }

    @NotNull
    private static VariableDeclarationStatement randomVariableDeclarationStatement(@NotNull GenCtx ctx, int depth) {
        return new VariableDeclarationStatement(randomVariableDeclaration(ctx, depth - 1));
    }

    @NotNull
    private static VariableDeclarator randomVariableDeclarator(@NotNull GenCtx ctx, int depth) {
        Binding binding = (Binding) randomParameter(ctx, depth - 1);
        if (binding instanceof BindingIdentifier && !ctx.isVariableDeclarationKindConst) {
            return new VariableDeclarator(binding, optional(Fuzzer::randomExpression).apply(ctx, depth - 1));
        } else {
            return new VariableDeclarator(binding, Maybe.just(randomExpression(ctx, depth - 1)));
        }
    }


    @NotNull
    private static WhileStatement randomWhileStatement(@NotNull GenCtx ctx, int depth) {
        return new WhileStatement(randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
    }

    @NotNull
    private static WithStatement randomWithStatement(@NotNull GenCtx ctx, int depth) {
        return new WithStatement(randomExpression(ctx, depth - 1), randomStatement(ctx, depth - 1));
    }

    @NotNull
    private static YieldExpression randomYieldExpression(@NotNull GenCtx ctx, int depth) {
        return new YieldExpression(optional(Fuzzer::randomExpression).apply(ctx, depth - 1));
    }

    @NotNull
    private static YieldGeneratorExpression randomYieldGeneratorExpression(@NotNull GenCtx ctx, int depth) {
        return new YieldGeneratorExpression(randomExpression(ctx, depth - 1));
    }

    private interface Gen<T> {
        @NotNull
        T apply(@NotNull GenCtx ctx, int depth);
    }
}
