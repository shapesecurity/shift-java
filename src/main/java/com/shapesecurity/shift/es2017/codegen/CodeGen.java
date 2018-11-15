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

package com.shapesecurity.shift.es2017.codegen;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es2017.ast.operators.Precedence;
import com.shapesecurity.shift.es2017.reducer.Director;
import com.shapesecurity.shift.es2017.reducer.Reducer;
import com.shapesecurity.shift.es2017.utils.D2A;
import com.shapesecurity.shift.es2017.utils.Utils;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnqualifiedFieldAccess")
public class CodeGen implements Reducer<CodeRep> {
    public static final CodeGen COMPACT = new CodeGen(new CodeRepFactory());
    public static final CodeGen PRETTY = new CodeGen(new FormattedCodeRepFactory());
    protected final CodeRepFactory factory;

    public CodeGen(@Nonnull CodeRepFactory factory) {
        this.factory = factory;
    }

    @Nonnull
    public static String codeGen(@Nonnull Program program) {
        return codeGen(program, COMPACT);
    }

    protected static String codeGen(@Nonnull Program program, @Nonnull CodeGen codeGen) {
        StringBuilder sb = new StringBuilder();
        TokenStream ts = new TokenStream(sb);
        Director.reduceProgram(codeGen, program).emit(ts, false);
        return sb.toString();
    }

    @Deprecated
    @Nonnull
    public static String codeGen(@Nonnull Script script, boolean pretty) {
        return codeGen(script, pretty ? PRETTY : COMPACT);
    }

    @Deprecated
    @Nonnull
    public static String codeGen(@Nonnull Module module, boolean pretty) {
        return codeGen(module, pretty ? PRETTY : COMPACT);
    }

    private static char decodeUtf16(char lead, char trail) {
        return (char) ((lead - 0xD800) * 0x400 + (trail - 0xDC00) + 0x10000);
    }

    @Nonnull
    private CodeRep getAssignmentExpr(@Nonnull Maybe<CodeRep> state) {
        if (state.isJust()) {
            return state.fromJust().containsGroup() ? factory.paren(state.fromJust()) : state.fromJust();
        } else {
            return factory.empty();
        }
    }

    private boolean isIdentifierNameES6(String id) {
        char ch;
        char lowCh;

        if (id.length() == 0) {
            return false;
        }
        if (!Utils.isIdentifierStart(id.charAt(0))) {
            return false;
        }
        for (int i = 1, iz = id.length(); i < iz; ++i) {
            ch = id.charAt(i);
            if (0xD800 <= ch && ch <= 0xDBFF) {
                ++i;
                if (i >= iz) {
                    return false;
                }
                lowCh = id.charAt(i);
                if (!(0xDC00 <= lowCh && lowCh <= 0xDFFF)) {
                    return false;
                }
                ch = decodeUtf16(ch, lowCh);
            }
            if (!Utils.isIdentifierPart(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isComplexArrowHead(@Nonnull FormalParameters params) {
        return (params.rest.isJust() || params.items.length != 1 || !(params.items.maybeHead().fromJust() instanceof BindingIdentifier));
    }

    protected CodeRep p(Node node, Precedence precedence, CodeRep a) {
        return ((Expression) node).getPrecedence().ordinal() < precedence.ordinal() ? factory.paren(a) : a;
    }

    @Nonnull
    @Override
    public CodeRep reduceArrayAssignmentTarget(@Nonnull ArrayAssignmentTarget node, @Nonnull ImmutableList<Maybe<CodeRep>> elements, @Nonnull Maybe<CodeRep> rest) {
        CodeRep content;
        if (elements.length == 0) {
            content = rest.maybe(factory.empty(), r -> seqVA(factory.token("..."), r));
        } else {
            content = factory.commaSep(elements.map(this::getAssignmentExpr));
            if (elements.length > 0 && elements.maybeLast().fromJust().isNothing() && rest.isNothing()) {
                content = seqVA(content, factory.token(","));
            }
            if (rest.isJust()) {
                content = seqVA(content, factory.token(","), factory.token("..."), rest.fromJust());
            }
        }
        return factory.bracket(content);
    }

    @Nonnull
    @Override
    public CodeRep reduceArrayBinding(@Nonnull ArrayBinding node, @Nonnull ImmutableList<Maybe<CodeRep>> elements, @Nonnull Maybe<CodeRep> rest) {
        CodeRep content;
        if (elements.length == 0) {
            content = rest.maybe(factory.empty(), r -> seqVA(factory.token("..."), r));
        } else {
            content = factory.commaSep(elements.map(this::getAssignmentExpr));
            if (elements.length > 0 && elements.maybeLast().fromJust().isNothing() && rest.isNothing()) {
                content = seqVA(content, factory.token(","));
            }
            if (rest.isJust()) {
                content = seqVA(content, factory.token(","), factory.token("..."), rest.fromJust());
            }
        }
        return factory.bracket(content);
    }

    @Override
    @Nonnull
    public CodeRep reduceArrayExpression(@Nonnull ArrayExpression node, @Nonnull ImmutableList<Maybe<CodeRep>> elements) {
        if (elements.isEmpty()) {
            return factory.bracket(factory.empty());
        }

        CodeRep content = factory.commaSep(elements.map(this::getAssignmentExpr));
        if (elements.length > 0 && elements.maybeLast().fromJust().isNothing()) {
            content = seqVA(content, factory.token(","));
        }
        return factory.bracket(content);
    }

    @Nonnull
    @Override
    public CodeRep reduceArrowExpression(@Nonnull ArrowExpression node, @Nonnull CodeRep params, @Nonnull CodeRep body) {
        if (!isComplexArrowHead(node.params)) {
            // FormalParameters unconditionally include parentheses, but they're not necessary here
            params = this.reduceBindingIdentifier((BindingIdentifier) node.params.items.maybeHead().fromJust());
        }
        if (body.startsWithObjectCurly()) {
            body = factory.paren(body);
        }
        if (node.body instanceof Expression) {
            body = p(node.body, Precedence.ASSIGNMENT, body);
        }
        return seqVA(node.isAsync ? factory.token("async") : factory.empty(), params, factory.token("=>"), body);
    }

    @Override
    @Nonnull
    public CodeRep reduceAssignmentExpression(@Nonnull AssignmentExpression node, @Nonnull CodeRep leftCode, @Nonnull CodeRep expression) {
        CodeRep rightCode = expression;
        boolean containsIn = expression.containsIn();
        boolean startsWithCurly = leftCode.startsWithObjectCurly();
        boolean startsWithLetSquareBracket = leftCode.startsWithLetSquareBracket();
        boolean startsWithFunctionOrClass = leftCode.startsWithFunctionOrClass();
        if (node.expression.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
            rightCode = factory.paren(rightCode);
            containsIn = false;
        }
        CodeRep toReturn = seqVA(leftCode, factory.token("="), rightCode);
        toReturn.setContainsIn(containsIn);
        toReturn.startsWithObjectCurly(startsWithCurly);
        toReturn.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
        toReturn.setStartsWithFunctionOrClass(startsWithFunctionOrClass);
        return toReturn;
    }

    @Nonnull
    @Override
    public CodeRep reduceAssignmentTargetIdentifier(@Nonnull AssignmentTargetIdentifier node) {
        return factory.token(node.name);
    }

    @Nonnull
    @Override
    public CodeRep reduceAssignmentTargetPropertyIdentifier(@Nonnull AssignmentTargetPropertyIdentifier node, @Nonnull CodeRep binding, @Nonnull Maybe<CodeRep> init) {
        return init.maybe(binding, i -> seqVA(binding, factory.token("="), i));
    }

    @Nonnull
    @Override
    public CodeRep reduceAssignmentTargetPropertyProperty(@Nonnull AssignmentTargetPropertyProperty node, @Nonnull CodeRep name, @Nonnull CodeRep binding) {
        return seqVA(name, factory.token(":"), binding);
    }

    @Nonnull
    @Override
    public CodeRep reduceAssignmentTargetWithDefault(@Nonnull AssignmentTargetWithDefault node, @Nonnull CodeRep binding, @Nonnull CodeRep init) {
        return seqVA(binding, factory.token("="), init);
    }

    @Nonnull
    @Override
    public CodeRep reduceAwaitExpression(@Nonnull AwaitExpression node, @Nonnull CodeRep expression) {
        return seqVA(factory.token("await"), expression);
    }

    @Override
    @Nonnull
    public CodeRep reduceBinaryExpression(@Nonnull BinaryExpression node, @Nonnull CodeRep left, @Nonnull CodeRep right) {
        CodeRep leftCode = left;
        boolean startsWithCurly = left.startsWithObjectCurly();
        boolean startsWithLetSquareBracket = left.startsWithLetSquareBracket();
        boolean startsWithFunctionOrClass = left.startsWithFunctionOrClass();
        boolean leftContainsIn = left.containsIn();
        if (node.left.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
            leftCode = factory.paren(leftCode);
            startsWithCurly = false;
            startsWithLetSquareBracket = false;
            startsWithFunctionOrClass = false;
            leftContainsIn = false;
        }
        CodeRep rightCode = right;
        boolean rightContainsIn = right.containsIn();
        if (node.right.getPrecedence().ordinal() <= node.getPrecedence().ordinal()) {
            rightCode = factory.paren(rightCode);
            rightContainsIn = false;
        }
        CodeRep toReturn = seqVA(leftCode, factory.token(node.operator.getName()), rightCode);
        toReturn.setContainsIn(leftContainsIn || rightContainsIn || node.operator.equals(BinaryOperator.In));
        toReturn.setContainsGroup(node.operator.equals(BinaryOperator.Sequence));
        toReturn.startsWithObjectCurly(startsWithCurly);
        toReturn.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
        toReturn.setStartsWithFunctionOrClass(startsWithFunctionOrClass);
        return toReturn;
    }

    @Nonnull
    @Override
    public CodeRep reduceBindingIdentifier(@Nonnull BindingIdentifier node) {
        CodeRep a = factory.token(node.name);
        if (node.name.equals("let")) {
            a.setStartsWithLet(true);
        }
        return a;
    }

    @Nonnull
    @Override
    public CodeRep reduceBindingPropertyIdentifier(@Nonnull BindingPropertyIdentifier node, @Nonnull CodeRep binding, @Nonnull Maybe<CodeRep> init) {
        return init.maybe(binding, i -> seqVA(binding, factory.token("="), i));
    }

    @Nonnull
    @Override
    public CodeRep reduceBindingPropertyProperty(@Nonnull BindingPropertyProperty node, @Nonnull CodeRep name, @Nonnull CodeRep binding) {
        return seqVA(name, factory.token(":"), binding);
    }

    @Nonnull
    @Override
    public CodeRep reduceBindingWithDefault(@Nonnull BindingWithDefault node, @Nonnull CodeRep binding, @Nonnull CodeRep init) {
        return seqVA(binding, factory.token("="), init);
    }

    @Nonnull
    @Override
    public CodeRep reduceBlock(@Nonnull Block node, @Nonnull ImmutableList<CodeRep> statements) {
        return factory.brace(factory.seq(statements));
    }

    @Override
    @Nonnull
    public CodeRep reduceBlockStatement(@Nonnull BlockStatement node, @Nonnull CodeRep block) {
        return block;
    }

    @Nonnull
    @Override
    public CodeRep reduceBreakStatement(@Nonnull BreakStatement node) {
        return seqVA(factory.token("break"), node.label.maybe(factory.empty(), factory::token), factory.semiOp());
    }

    @Override
    @Nonnull
    public CodeRep reduceCallExpression(@Nonnull CallExpression node, @Nonnull CodeRep callee, @Nonnull ImmutableList<CodeRep> arguments) {
        ImmutableList<CodeRep> parenthesizedArgs = arguments.mapWithIndex((i, r) -> {
            SpreadElementExpression arg = node.arguments.index(i).fromJust();
            if (arg instanceof SpreadElement) {
                return r;
            }
            return p(arg, Precedence.ASSIGNMENT, r);
        });
        CodeRep result;
        if (node.callee instanceof Expression) {
            result = seqVA(p(node.callee, node.getPrecedence(), callee), factory.paren(factory.commaSep(parenthesizedArgs)));
        } else {
            result = seqVA(callee, factory.paren(factory.commaSep(parenthesizedArgs)));
        }
        result.startsWithObjectCurly(callee.startsWithObjectCurly());
        result.setStartsWithLetSquareBracket(callee.startsWithLetSquareBracket());
        result.setStartsWithFunctionOrClass(callee.startsWithFunctionOrClass());
        return result;
    }

    @Nonnull
    @Override
    public CodeRep reduceCatchClause(@Nonnull CatchClause node, @Nonnull CodeRep binding, @Nonnull CodeRep body) {
        return seqVA(factory.token("catch"), factory.paren(binding), body);
    }

    @Nonnull
    @Override
    public CodeRep reduceClassDeclaration(@Nonnull ClassDeclaration node, @Nonnull CodeRep name, @Nonnull Maybe<CodeRep> _super, @Nonnull ImmutableList<CodeRep> elements) {
        CodeRep state = seqVA(factory.token("class"), node.name.name.equals("*default*") ? factory.empty() : name);
        if (_super.isJust()) {
            state = seqVA(state, factory.token("extends"), p(node._super.fromJust(), Precedence.NEW, _super.fromJust()));
        }
        state = seqVA(state, factory.token("{"), factory.seq(elements), factory.token("}"));
        return state;
    }

    @Nonnull
    @Override
    public CodeRep reduceClassElement(@Nonnull ClassElement node, @Nonnull CodeRep method) {
        if (!node.isStatic) {
            return method;
        }
        return seqVA(factory.token("static"), method);
    }

    @Nonnull
    @Override
    public CodeRep reduceClassExpression(@Nonnull ClassExpression node, @Nonnull Maybe<CodeRep> name, @Nonnull Maybe<CodeRep> _super, @Nonnull ImmutableList<CodeRep> elements) {
        CodeRep state = factory.token("class");
        if (name.isJust()) {
            state = seqVA(state, name.fromJust());
        }
        if (_super.isJust()) {
            state = seqVA(state, factory.token("extends"), p(node._super.fromJust(), Precedence.NEW, _super.fromJust()));
        }
        state = seqVA(state, factory.token("{"), factory.seq(elements), factory.token("}"));
        state.setStartsWithFunctionOrClass(true);
        return state;
    }

    @Nonnull
    @Override
    public CodeRep reduceCompoundAssignmentExpression(@Nonnull CompoundAssignmentExpression node, @Nonnull CodeRep binding, @Nonnull CodeRep expression) {
        CodeRep rightCode = expression;
        boolean containsIn = expression.containsIn();
        boolean startsWithCurly = binding.startsWithObjectCurly();
        boolean startsWithLetSquareBracket = binding.startsWithLetSquareBracket();
        boolean startsWithFunctionOrClass = binding.startsWithFunctionOrClass();
        if (node.expression.getPrecedence().ordinal() < node.getPrecedence().ordinal()) {
            rightCode = factory.paren(rightCode);
            containsIn = false;
        }
        CodeRep toReturn = seqVA(binding, factory.token(node.operator.getName()), rightCode);
        toReturn.setContainsIn(containsIn);
        toReturn.startsWithObjectCurly(startsWithCurly);
        toReturn.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
        toReturn.setStartsWithFunctionOrClass(startsWithFunctionOrClass);
        return toReturn;
    }

    @Nonnull
    @Override
    public CodeRep reduceComputedMemberAssignmentTarget(@Nonnull ComputedMemberAssignmentTarget node, @Nonnull CodeRep object, @Nonnull CodeRep expression) {
        boolean startsWithLetSquareBracket = object.startsWithLetSquareBracket() || node.object instanceof IdentifierExpression && ((IdentifierExpression) node.object).name.equals("let");
        CodeRep result;
        if (node.object instanceof Expression) {
            result = seqVA(p(node.object, Precedence.MEMBER, object), factory.bracket(expression)); // todo confirm MEMBER is always correct
        } else {
            result = seqVA(object, factory.bracket(expression));
        }
        result.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
        result.setStartsWithLet(object.startsWithLet());
        result.startsWithObjectCurly(object.startsWithObjectCurly());
        result.setStartsWithFunctionOrClass(object.startsWithFunctionOrClass());
        return result;
    }

    @Override
    @Nonnull
    public CodeRep reduceComputedMemberExpression(@Nonnull ComputedMemberExpression node, @Nonnull CodeRep object, @Nonnull CodeRep expression) {
        boolean startsWithLetSquareBracket = object.startsWithLetSquareBracket() || node.object instanceof IdentifierExpression && ((IdentifierExpression) node.object).name.equals("let");
        CodeRep result;
        if (node.object instanceof Expression) {
            result = seqVA(p(node.object, node.getPrecedence(), object), factory.bracket(expression));
        } else {
            result = seqVA(object, factory.bracket(expression));
        }
        result.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
        result.setStartsWithLet(object.startsWithLet());
        result.startsWithObjectCurly(object.startsWithObjectCurly());
        result.setStartsWithFunctionOrClass(object.startsWithFunctionOrClass());
        return result;
    }

    @Nonnull
    @Override
    public CodeRep reduceComputedPropertyName(@Nonnull ComputedPropertyName node, @Nonnull CodeRep expression) {
        return factory.bracket(expression);
    }

    @Override
    @Nonnull
    public CodeRep reduceConditionalExpression(@Nonnull ConditionalExpression node, @Nonnull CodeRep test, @Nonnull CodeRep consequent, @Nonnull CodeRep alternate) {
        boolean containsIn = test.containsIn() || alternate.containsIn();
        boolean startsWithCurly = test.startsWithObjectCurly();
        boolean startsWithLetSquareBracket = test.startsWithLetSquareBracket();
        boolean startsWithFunctionOrClass = test.startsWithFunctionOrClass();

        CodeRep toReturn = seqVA(p(node.test, Precedence.LOGICAL_OR, test), factory.token("?"), p(node.consequent, Precedence.ASSIGNMENT, consequent), factory.token(":"), p(node.alternate, Precedence.ASSIGNMENT, alternate));
        toReturn.setContainsIn(containsIn);
        toReturn.startsWithObjectCurly(startsWithCurly);
        toReturn.setStartsWithLetSquareBracket(startsWithLetSquareBracket);
        toReturn.setStartsWithFunctionOrClass(startsWithFunctionOrClass);
        return toReturn;
    }

    @Nonnull
    @Override
    public CodeRep reduceContinueStatement(@Nonnull ContinueStatement node) {
        return seqVA(factory.token("continue"), node.label.maybe(factory.empty(), factory::token), factory.semiOp());
    }

    @Override
    @Nonnull
    public CodeRep reduceDataProperty(@Nonnull DataProperty node, @Nonnull CodeRep name, @Nonnull CodeRep expression) {
        return seqVA(name, factory.token(":"), getAssignmentExpr(Maybe.of(expression)));
    }

    @Override
    @Nonnull
    public CodeRep reduceDebuggerStatement(@Nonnull DebuggerStatement node) {
        return seqVA(factory.token("debugger"), factory.semiOp());
    }

    @Nonnull
    @Override
    public CodeRep reduceDirective(@Nonnull Directive node) {
        String delim = node.rawValue.matches("^(?:[^\"]|\\\\.)*$") ? "\"" : "\'";
        return seqVA(factory.token(delim + node.rawValue + delim), factory.semiOp());
    }

    @Override
    @Nonnull
    public CodeRep reduceDoWhileStatement(
            @Nonnull DoWhileStatement node, @Nonnull CodeRep body, @Nonnull CodeRep test) {
        return seqVA(
                factory.token("do"), body, factory.token("while"), factory.paren(test), factory.semiOp());
    }

    @Override
    @Nonnull
    public CodeRep reduceEmptyStatement(@Nonnull EmptyStatement node) {
        return factory.semi();
    }

    @Nonnull
    @Override
    public CodeRep reduceExport(@Nonnull Export node, @Nonnull CodeRep declaration) {
        if (node.declaration instanceof VariableDeclaration) {
            declaration = seqVA(declaration, factory.semiOp());
        }
        return seqVA(factory.token("export"), declaration);
    }

    @Nonnull
    @Override
    public CodeRep reduceExportAllFrom(@Nonnull ExportAllFrom node) {
        return seqVA(factory.token("export"), factory.token("*"), factory.token("from"), factory.token(Utils.escapeStringLiteral(node.moduleSpecifier)), factory.semiOp());

    }

    @Nonnull
    @Override
    public CodeRep reduceExportDefault(@Nonnull ExportDefault node, @Nonnull CodeRep body) {
        body = body.startsWithFunctionOrClass() ? factory.paren(body) : body;
        if (node.body instanceof Expression) {
            body = seqVA(body, factory.semiOp());
        }
        return seqVA(factory.token("export default"), body);
    }

    @Nonnull
    @Override
    public CodeRep reduceExportFrom(@Nonnull ExportFrom node, @Nonnull ImmutableList<CodeRep> namedExports) {
        return seqVA(
                factory.token("export"),
                factory.brace(factory.commaSep(namedExports)),
                seqVA(factory.token("from"), factory.token(Utils.escapeStringLiteral(node.moduleSpecifier)), factory.semiOp())
        );
    }

    @Nonnull
    @Override
    public CodeRep reduceExportFromSpecifier(@Nonnull ExportFromSpecifier node) {
        if (node.exportedName.isNothing()) {
            return factory.token(node.name);
        }
        return seqVA(factory.token(node.name), factory.token("as"), factory.token(node.exportedName.fromJust()));
    }

    @Nonnull
    @Override
    public CodeRep reduceExportLocalSpecifier(@Nonnull ExportLocalSpecifier node, @Nonnull CodeRep name) {
        if (node.exportedName.isNothing()) {
            return name;
        }
        return seqVA(name, factory.token("as"), factory.token(node.exportedName.fromJust()));
    }

    @Nonnull
    @Override
    public CodeRep reduceExportLocals(@Nonnull ExportLocals node, @Nonnull ImmutableList<CodeRep> namedExports) {
        return seqVA(
                factory.token("export"),
                factory.brace(factory.commaSep(namedExports)),
                factory.semiOp()
        );
    }

    @Override
    @Nonnull
    public CodeRep reduceExpressionStatement(@Nonnull ExpressionStatement expressionStatement, @Nonnull CodeRep expression) {
        if (expressionStatement.expression instanceof LiteralStringExpression) {
            return factory.markStringLiteralExpressionStatement(expression);
        }
        boolean needsParens = expression.startsWithObjectCurly() || expression.startsWithLetSquareBracket() || expression.startsWithFunctionOrClass();
        return seqVA((needsParens ? factory.paren(expression) : expression), factory.semiOp());
    }

    @Nonnull
    @Override
    public CodeRep reduceForInStatement(@Nonnull ForInStatement node, @Nonnull CodeRep left, @Nonnull CodeRep right, @Nonnull CodeRep body) {
        CodeRep leftP = left;
        if (node.left instanceof VariableDeclaration) {
            leftP = factory.noIn(factory.markContainsIn(left));
        } else if (node.left instanceof AssignmentTargetIdentifier) {
            if (((AssignmentTargetIdentifier) node.left).name.equals("let")) {
                leftP = factory.paren(left);
            }
        }
        CodeRep toReturn = seqVA(factory.token("for"), factory.paren(seqVA(leftP, factory.token("in"), right)), body);
        toReturn.setEndsWithMissingElse(body.endsWithMissingElse());
        return toReturn;
    }

    @Nonnull
    @Override
    public CodeRep reduceForOfStatement(@Nonnull ForOfStatement node, @Nonnull CodeRep left, @Nonnull CodeRep right, @Nonnull CodeRep body) {
        left = node.left instanceof VariableDeclaration ? factory.noIn(factory.markContainsIn(left)) : left;
        CodeRep toReturn = seqVA(factory.token("for"), factory.paren(seqVA(left.startsWithLet() ? factory.paren(left) : left, factory.token("of"), right)), body);
        toReturn.setEndsWithMissingElse(body.endsWithMissingElse());
        return toReturn;
    }

    @Nonnull
    @Override
    public CodeRep reduceForStatement(
            @Nonnull ForStatement node,
            @Nonnull Maybe<CodeRep> init,
            @Nonnull Maybe<CodeRep> test,
            @Nonnull Maybe<CodeRep> update,
            @Nonnull CodeRep body) {
        CodeRep result = seqVA(
                factory.token("for"),
                factory.paren(seqVA(
                        init.maybe(factory.empty(), x -> factory.noIn(factory.testIn(x))),
                        factory.token(";"),
                        test.orJust(factory.empty()),
                        factory.token(";"),
                        update.orJust(factory.empty()))),
                body);
        result.setEndsWithMissingElse(body.endsWithMissingElse());
        return result;
    }

    @Nonnull
    @Override
    public CodeRep reduceFormalParameters(@Nonnull FormalParameters node, @Nonnull ImmutableList<CodeRep> items, @Nonnull Maybe<CodeRep> rest) {
        return factory.paren(factory.commaSep(rest.maybe(items, r -> items.append(ImmutableList.of(seqVA(factory.token("..."), r))))));
    }

    @Override
    @Nonnull
    public CodeRep reduceFunctionBody(
            @Nonnull final FunctionBody node,
            @Nonnull final ImmutableList<CodeRep> directives,
            @Nonnull final ImmutableList<CodeRep> statements) {
        if (statements.isNotEmpty()) {
            ((NonEmptyImmutableList<CodeRep>) statements).head.markIsInDirectivePosition();
        }
        return factory.brace(seqVA(factory.seq(directives), factory.seq(statements)));
    }

    @Override
    @Nonnull
    public CodeRep reduceFunctionDeclaration(@Nonnull FunctionDeclaration node, @Nonnull CodeRep name, @Nonnull CodeRep params, @Nonnull CodeRep body) {
        return seqVA(node.isAsync ? factory.token("async") : factory.empty(), factory.token("function"), node.isGenerator ? factory.token("*") : factory.empty(), node.name.name.equals("*default*") ? factory.empty() : name, params, body);
    }

    @Override
    @Nonnull
    public CodeRep reduceFunctionExpression(@Nonnull FunctionExpression node, @Nonnull Maybe<CodeRep> name, @Nonnull CodeRep params, @Nonnull CodeRep body) {
        CodeRep state = seqVA(node.isAsync ? factory.token("async") : factory.empty(), factory.token("function"), node.isGenerator ? factory.token("*") : factory.empty(), name.isJust() ? name.fromJust() : factory.empty(), params, body);
        state.setStartsWithFunctionOrClass(true);
        return state;
    }

    @Override
    @Nonnull
    public CodeRep reduceGetter(
            @Nonnull Getter node, @Nonnull CodeRep name, @Nonnull CodeRep body) {
        return seqVA(factory.token("get"), name, factory.paren(factory.empty()), body);
    }

    @Nonnull
    @Override
    public CodeRep reduceIdentifierExpression(@Nonnull IdentifierExpression node) {
        CodeRep a = factory.token(node.name);
        if (node.name.equals("let")) {
            a.setStartsWithLet(true);
        }
        return a;
    }

    @Override
    @Nonnull
    public CodeRep reduceIfStatement(
            @Nonnull IfStatement node,
            @Nonnull CodeRep test,
            @Nonnull CodeRep consequent,
            @Nonnull Maybe<CodeRep> alternate) {
        CodeRep consequentCode = consequent;
        if (alternate.isJust() && consequent.endsWithMissingElse()) {
            consequentCode = factory.brace(consequentCode);
        }
        CodeRep result = seqVA(
                factory.token("if"), factory.paren(test), consequentCode, alternate.maybe(
                        factory.empty(), s -> seqVA(
                                factory.token("else"), s)));
        result.setEndsWithMissingElse(alternate.maybe(true, s -> s.endsWithMissingElse()));
        return result;
    }

    @Nonnull
    @Override
    public CodeRep reduceImport(@Nonnull Import node, @Nonnull Maybe<CodeRep> defaultBinding, @Nonnull ImmutableList<CodeRep> namedImports) {
        List<CodeRep> bindings = new ArrayList<>();
        if (defaultBinding.isJust()) {
            bindings.add(defaultBinding.fromJust());
        }
        if (namedImports.length > 0) {
            bindings.add(factory.brace(factory.commaSep(namedImports)));
        }
        if (bindings.size() == 0) {
            return seqVA(factory.token("import"), factory.token(Utils.escapeStringLiteral(node.moduleSpecifier)), factory.semiOp());
        }
        return seqVA(factory.token("import"), factory.commaSep(ImmutableList.from(bindings)), factory.token("from"), factory.token(Utils.escapeStringLiteral(node.moduleSpecifier)), factory.semiOp());
    }

    @Nonnull
    @Override
    public CodeRep reduceImportNamespace(@Nonnull ImportNamespace node, @Nonnull Maybe<CodeRep> defaultBinding, @Nonnull CodeRep namespaceBinding) {
        return seqVA(
                factory.token("import"),
                defaultBinding.maybe(factory.empty(), b -> seqVA(b, factory.token(","))),
                factory.token("*"),
                factory.token("as"),
                namespaceBinding,
                factory.token("from"),
                factory.token(Utils.escapeStringLiteral(node.moduleSpecifier)),
                factory.semiOp()
        );
    }

    @Nonnull
    @Override
    public CodeRep reduceImportSpecifier(@Nonnull ImportSpecifier node, @Nonnull CodeRep binding) {
        return node.name.maybe(binding, n -> seqVA(factory.token(n), factory.token("as"), binding));
    }

    @Override
    @Nonnull
    public CodeRep reduceLabeledStatement(
            @Nonnull LabeledStatement node, @Nonnull CodeRep body) {
        CodeRep result = seqVA(factory.token(node.label), factory.token(":"), body);
        result.setEndsWithMissingElse(body.endsWithMissingElse());
        return result;
    }

    @Override
    @Nonnull
    public CodeRep reduceLiteralBooleanExpression(@Nonnull LiteralBooleanExpression node) {
        return factory.token(Boolean.toString(node.value));
    }

    @Nonnull
    @Override
    public CodeRep reduceLiteralInfinityExpression(@Nonnull LiteralInfinityExpression node) {
        return factory.token("2e308");
    }

    @Override
    @Nonnull
    public CodeRep reduceLiteralNullExpression(@Nonnull LiteralNullExpression node) {
        return factory.token("null");
    }

    @Override
    @Nonnull
    public CodeRep reduceLiteralNumericExpression(@Nonnull LiteralNumericExpression node) {
        return factory.num(node.value);
    }

    @Override
    @Nonnull
    public CodeRep reduceLiteralRegExpExpression(@Nonnull LiteralRegExpExpression node) {
        return factory.token("/" + node.pattern + "/" + buildFlags(node));
    }

    @Nonnull
    protected static String buildFlags(@Nonnull LiteralRegExpExpression node) {
        return (node.global ? "g" : "") + (node.ignoreCase ? "i" : "") + (node.multiLine ? "m" : "") + (node.unicode ? "u" : "") + (node.sticky ? "y" : "");
    }

    @Override
    @Nonnull
    public CodeRep reduceLiteralStringExpression(@Nonnull LiteralStringExpression node) {
        return factory.token(Utils.escapeStringLiteral(node.value));
    }

    @Nonnull
    @Override
    public CodeRep reduceMethod(@Nonnull Method node, @Nonnull CodeRep name, @Nonnull CodeRep params, @Nonnull CodeRep body) {
        return seqVA(node.isAsync ? factory.token("async") : factory.empty(), node.isGenerator ? factory.token("*") : factory.empty(), name, params, body);
    }

    @Nonnull
    @Override
    public CodeRep reduceModule(@Nonnull Module node, @Nonnull ImmutableList<CodeRep> directives, @Nonnull ImmutableList<CodeRep> items) {
        if (items.isNotEmpty()) {
            ((NonEmptyImmutableList<CodeRep>) items).head.markIsInDirectivePosition();
        }
        return seqVA(factory.seq(directives), factory.seq(items));
    }

    @Override
    @Nonnull
    public CodeRep reduceNewExpression(@Nonnull NewExpression node, @Nonnull CodeRep callee, @Nonnull ImmutableList<CodeRep> arguments) {
        CodeRep calleeRep = node.callee.getPrecedence() == Precedence.CALL ? factory.paren(callee) : p(node.callee, node.getPrecedence(), callee);
        return seqVA(factory.token("new"), calleeRep, arguments.length == 0 ? factory.empty() : factory.paren(factory.commaSep(arguments)));
    }

    @Nonnull
    @Override
    public CodeRep reduceNewTargetExpression(@Nonnull NewTargetExpression node) {
        return factory.token("new.target");
    }

    @Nonnull
    @Override
    public CodeRep reduceObjectAssignmentTarget(@Nonnull ObjectAssignmentTarget node, @Nonnull ImmutableList<CodeRep> properties) {
        CodeRep state = factory.brace(factory.commaSep(properties));
        state.startsWithObjectCurly(true);
        return state;
    }

    @Nonnull
    @Override
    public CodeRep reduceObjectBinding(@Nonnull ObjectBinding node, @Nonnull ImmutableList<CodeRep> properties) {
        CodeRep state = factory.brace(factory.commaSep(properties));
        state.startsWithObjectCurly(true);
        return state;
    }

    @Override
    @Nonnull
    public CodeRep reduceObjectExpression(@Nonnull ObjectExpression node, @Nonnull ImmutableList<CodeRep> properties) {
        CodeRep result = factory.brace(factory.commaSep(properties));
        result.startsWithObjectCurly(true);
        return result;
    }

    @Override
    @Nonnull
    public CodeRep reduceReturnStatement(
            @Nonnull ReturnStatement node, @Nonnull Maybe<CodeRep> expression) {
        return seqVA(
                factory.token("return"), seqVA(expression.orJust(factory.empty())), factory.semiOp());
    }

    @Override
    @Nonnull
    public CodeRep reduceScript(@Nonnull Script node, @Nonnull ImmutableList<CodeRep> directives, @Nonnull ImmutableList<CodeRep> statements) {
        if (statements.isNotEmpty()) {
            ((NonEmptyImmutableList<CodeRep>) statements).head.markIsInDirectivePosition();
        }
        return seqVA(factory.seq(directives), factory.seq(statements));
    }

    @Override
    @Nonnull
    public CodeRep reduceSetter(
            @Nonnull Setter node,
            @Nonnull CodeRep name,
            @Nonnull CodeRep param,
            @Nonnull CodeRep body) {
        return (seqVA(factory.token("set"), name, factory.paren(param), body));
    }

    @Nonnull
    @Override
    public CodeRep reduceShorthandProperty(@Nonnull ShorthandProperty node, @Nonnull CodeRep name) {
        return name;
    }

    @Nonnull
    @Override
    public CodeRep reduceSpreadElement(@Nonnull SpreadElement node, @Nonnull CodeRep expression) {
        return seqVA(factory.token("..."), p(node.expression, Precedence.ASSIGNMENT, expression));
    }

    @Nonnull
    @Override
    public CodeRep reduceStaticMemberAssignmentTarget(@Nonnull StaticMemberAssignmentTarget node, @Nonnull CodeRep object) {
        CodeRep state;
        if (node.object instanceof Expression) {
            state = seqVA(p(node.object, Precedence.MEMBER, object), factory.token("."), factory.token(node.property)); // TODO confirm MEMBMER is always correct
        } else {
            // node._object is a Super
            state = seqVA(object, factory.token("."), factory.token(node.property));
        }
        state.setStartsWithLet(object.startsWithLet());
        state.startsWithObjectCurly(object.startsWithObjectCurly());
        state.setStartsWithLetSquareBracket(object.startsWithLetSquareBracket());
        state.setStartsWithFunctionOrClass(object.startsWithFunctionOrClass());
        return state;
    }

    @Override
    @Nonnull
    public CodeRep reduceStaticMemberExpression(@Nonnull StaticMemberExpression node, @Nonnull CodeRep object) {
        CodeRep state;
        if (node.object instanceof Expression) {
            state = seqVA(p(node.object, node.getPrecedence(), object), factory.token("."), factory.token(node.property));
        } else {
            // node._object is a Super
            state = seqVA(object, factory.token("."), factory.token(node.property));
        }
        state.setStartsWithLet(object.startsWithLet());
        state.startsWithObjectCurly(object.startsWithObjectCurly());
        state.setStartsWithLetSquareBracket(object.startsWithLetSquareBracket());
        state.setStartsWithFunctionOrClass(object.startsWithFunctionOrClass());
        return state;
    }

    @Nonnull
    @Override
    public CodeRep reduceStaticPropertyName(@Nonnull StaticPropertyName node) {
        if (isIdentifierNameES6(node.value) && !node.value.equals("Infinity")) {
            return factory.token(node.value);
        } else {
            try {
                double n = Double.parseDouble(node.value);
                if (n >= 0 && D2A.d2a(n).equals(node.value)) {
                    return factory.num(n);
                }
            } catch (NumberFormatException ignored) {}
            return factory.token(Utils.escapeStringLiteral(node.value));
        }
    }

    @Nonnull
    @Override
    public CodeRep reduceSuper(@Nonnull Super node) {
        return factory.token("super");
    }

    @Nonnull
    @Override
    public CodeRep reduceSwitchCase(
            @Nonnull SwitchCase node, @Nonnull CodeRep test, @Nonnull ImmutableList<CodeRep> consequent) {
        return seqVA(factory.token("case"), test, factory.token(":"), factory.seq(consequent));
    }

    @Nonnull
    @Override
    public CodeRep reduceSwitchDefault(
            @Nonnull SwitchDefault node, @Nonnull ImmutableList<CodeRep> consequent) {
        return seqVA(factory.token("default"), factory.token(":"), factory.seq(consequent));
    }

    @Override
    @Nonnull
    public CodeRep reduceSwitchStatement(
            @Nonnull SwitchStatement node,
            @Nonnull CodeRep discriminant,
            @Nonnull ImmutableList<CodeRep> cases) {
        return seqVA(
                factory.token("switch"), factory.paren(discriminant), factory.brace(
                        factory.seq(cases)));
    }

    @Nonnull
    @Override
    public CodeRep reduceSwitchStatementWithDefault(
            @Nonnull SwitchStatementWithDefault node,
            @Nonnull CodeRep discriminant,
            @Nonnull ImmutableList<CodeRep> preDefaultCases,
            @Nonnull CodeRep defaultCase,
            @Nonnull ImmutableList<CodeRep> postDefaultCases) {
        return seqVA(
                factory.token("switch"), factory.paren(discriminant), factory.brace(
                        seqVA(
                                factory.seq(preDefaultCases), defaultCase, factory.seq(postDefaultCases
                                ))));
    }

    @Nonnull
    @Override
    public CodeRep reduceTemplateElement(@Nonnull TemplateElement node) {
        return factory.rawToken(node.rawValue);
    }

    @Nonnull
    @Override
    public CodeRep reduceTemplateExpression(@Nonnull TemplateExpression node, @Nonnull Maybe<CodeRep> tag, @Nonnull ImmutableList<CodeRep> elements) {
        CodeRep state = node.tag.maybe(factory.empty(), t -> p(t, node.getPrecedence(), tag.fromJust()));
        state = seqVA(state, factory.token("`"));
        for (int i = 0, l = node.elements.length; i < l; ++i) {
            if (node.elements.index(i).fromJust() instanceof TemplateElement) {
                state = seqVA(
                        state,
                        i > 0 ? factory.token("}") : factory.empty(),
                        elements.index(i).fromJust(),
                        i < l - 1 ? factory.token("${") : factory.empty()
                );
            } else {
                state = seqVA(state, elements.index(i).fromJust());
            }
        }
        state = seqVA(state, factory.token("`"));
        if (node.tag.isJust()) {
            state.startsWithObjectCurly(tag.fromJust().startsWithObjectCurly());
            state.setStartsWithLetSquareBracket(tag.fromJust().startsWithLetSquareBracket());
            state.setStartsWithFunctionOrClass(tag.fromJust().startsWithFunctionOrClass());
        }
        return state;
    }

    @Override
    @Nonnull
    public CodeRep reduceThisExpression(@Nonnull ThisExpression node) {
        return factory.token("this");
    }

    @Override
    @Nonnull
    public CodeRep reduceThrowStatement(
            @Nonnull ThrowStatement node, @Nonnull CodeRep expression) {
        return seqVA(factory.token("throw"), expression, factory.semiOp());
    }

    @Nonnull
    @Override
    public CodeRep reduceTryCatchStatement(
            @Nonnull TryCatchStatement node,
            @Nonnull CodeRep block,
            @Nonnull CodeRep catchClause) {
        return seqVA(factory.token("try"), block, catchClause);
    }

    @Nonnull
    @Override
    public CodeRep reduceTryFinallyStatement(
            @Nonnull TryFinallyStatement node,
            @Nonnull CodeRep block,
            @Nonnull Maybe<CodeRep> catchClause,
            @Nonnull CodeRep finalizer) {
        return seqVA(
                factory.token("try"), block, catchClause.orJust(factory.empty()), seqVA(
                        factory.token("finally"), finalizer));
    }

    @Nonnull
    @Override
    public CodeRep reduceUnaryExpression(@Nonnull UnaryExpression node, @Nonnull CodeRep operand) {
        return seqVA(factory.token(node.operator.getName()), p(node.operand, node.getPrecedence(), operand));
    }

    @Nonnull
    @Override
    public CodeRep reduceUpdateExpression(@Nonnull UpdateExpression node, @Nonnull CodeRep operand) {
        if (node.isPrefix) {
            return seqVA(factory.token(node.operator.getName()), operand);
        } else {
            CodeRep toReturn = toReturn = seqVA(operand, factory.token(node.operator.getName()));
            toReturn.startsWithObjectCurly(operand.startsWithObjectCurly());
            toReturn.setStartsWithLetSquareBracket(operand.startsWithLetSquareBracket());
            toReturn.setStartsWithFunctionOrClass(operand.startsWithFunctionOrClass());
            return toReturn;
        }
    }

//----------------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    public CodeRep reduceVariableDeclaration(
            @Nonnull VariableDeclaration node, @Nonnull ImmutableList<CodeRep> declarators) {
        return seqVA(factory.token(node.kind.name), factory.commaSep(declarators));
    }

    @Nonnull
    @Override
    public CodeRep reduceVariableDeclarationStatement(
            @Nonnull VariableDeclarationStatement node, @Nonnull CodeRep declaration) {
        return seqVA(declaration, factory.semiOp());
    }

    @Override
    @Nonnull
    public CodeRep reduceVariableDeclarator(
            @Nonnull VariableDeclarator node, @Nonnull CodeRep binding,
            @Nonnull Maybe<CodeRep> init) {
        CodeRep result = factory.init(
                binding, init.map(
                        state -> state.containsGroup() ? factory.paren(state) : factory.testIn(state)));
        result.setContainsIn(init.maybe(false, state -> state.containsIn() && !state.containsGroup()));
        return result;
    }

    @Override
    @Nonnull
    public CodeRep reduceWhileStatement(
            @Nonnull WhileStatement node, @Nonnull CodeRep test, @Nonnull CodeRep body) {
        CodeRep result = seqVA(factory.token("while"), factory.paren(test), body);
        result.setEndsWithMissingElse(body.endsWithMissingElse());
        return result;
    }

    @Override
    @Nonnull
    public CodeRep reduceWithStatement(
            @Nonnull WithStatement node, @Nonnull CodeRep object, @Nonnull CodeRep body) {
        CodeRep result = seqVA(factory.token("with"), factory.paren(object), body);
        result.setEndsWithMissingElse(body.endsWithMissingElse());
        return result;
    }

    @Nonnull
    @Override
    public CodeRep reduceYieldExpression(@Nonnull YieldExpression node, @Nonnull Maybe<CodeRep> expression) {
        if (node.expression.isNothing()) {
            return factory.token("yield");
        }
        return seqVA(factory.token("yield"), p(node.expression.fromJust(), node.getPrecedence(), expression.fromJust()));
    }

    @Nonnull
    @Override
    public CodeRep reduceYieldGeneratorExpression(@Nonnull YieldGeneratorExpression node, @Nonnull CodeRep expression) {
        return seqVA(factory.token("yield"), factory.token("*"), p(node.expression, node.getPrecedence(), expression));
    }

    @Nonnull
    protected CodeRep seqVA(@Nonnull CodeRep... reps) {
        return factory.seq(reps);
    }
}
