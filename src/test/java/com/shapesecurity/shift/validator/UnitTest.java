///*
// * Copyright 2014 Shape Security, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.shapesecurity.shift.validator;
//
//import com.shapesecurity.functional.data.Either;
//import com.shapesecurity.functional.data.ImmutableList;
//import com.shapesecurity.functional.data.Maybe;
//import com.shapesecurity.shift.AstHelper;
//import com.shapesecurity.shift.ast.FunctionBody;
//import com.shapesecurity.shift.ast.Identifier;
//import com.shapesecurity.shift.ast.Script;
//import com.shapesecurity.shift.ast.Statement;
//import com.shapesecurity.shift.ast.SwitchCase;
//import com.shapesecurity.shift.ast.SwitchDefault;
//import com.shapesecurity.shift.ast.VariableDeclaration.VariableDeclarationKind;
//import com.shapesecurity.shift.ast.expression.FunctionExpression;
//import com.shapesecurity.shift.ast.expression.IdentifierExpression;
//import com.shapesecurity.shift.ast.expression.LiteralNumericExpression;
//import com.shapesecurity.shift.ast.expression.ObjectExpression;
//import com.shapesecurity.shift.ast.expression.StaticMemberExpression;
//import com.shapesecurity.shift.ast.property.DataProperty;
//import com.shapesecurity.shift.ast.property.Getter;
//import com.shapesecurity.shift.ast.property.ObjectProperty;
//import com.shapesecurity.shift.ast.property.PropertyName;
//import com.shapesecurity.shift.ast.property.Setter;
//import com.shapesecurity.shift.ast.statement.BreakStatement;
//import com.shapesecurity.shift.ast.statement.ContinueStatement;
//import com.shapesecurity.shift.ast.statement.DoWhileStatement;
//import com.shapesecurity.shift.ast.statement.ForInStatement;
//import com.shapesecurity.shift.ast.statement.ForStatement;
//import com.shapesecurity.shift.ast.statement.FunctionDeclaration;
//import com.shapesecurity.shift.ast.statement.IfStatement;
//import com.shapesecurity.shift.ast.statement.LabeledStatement;
//import com.shapesecurity.shift.ast.statement.ReturnStatement;
//import com.shapesecurity.shift.ast.statement.SwitchStatementWithDefault;
//import com.shapesecurity.shift.ast.statement.WhileStatement;
//import com.shapesecurity.shift.ast.statement.WithStatement;
//import com.shapesecurity.shift.parser.JsError;
//import com.shapesecurity.shift.parser.Parser;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.junit.Test;
//
//public class UnitTest extends AstHelper {
//  @Test
//  public final void testBreakStatement() {
//    validStmt(label(ID.name, wrapIter(new BreakStatement(Maybe.just(ID)))));
//    invalidStmt(1, wrapIter(new BreakStatement(Maybe.just(ID))));
//    invalidStmt(1, label("aa", wrapIter(new BreakStatement(Maybe.just(ID)))));
//    validStmt(new SwitchStatementWithDefault(EXPR, ImmutableList.<SwitchCase>nil(), new SwitchDefault(
//            ImmutableList.<Statement>list(
//                new BreakStatement(Maybe.<Identifier>nothing()))), ImmutableList.<SwitchCase>nil()));
//    invalidStmt(1, new SwitchStatementWithDefault(EXPR, ImmutableList.<SwitchCase>nil(), new SwitchDefault(
//            ImmutableList.<Statement>list(
//                new BreakStatement(Maybe.just(ID)))), ImmutableList.<SwitchCase>nil()));
//  }
//
//  @Test
//  public final void testContinueStatement() {
//    validStmt(label("a", wrapIter(new ContinueStatement(Maybe.just(ID)))));
//    invalidStmt(1, wrapIter(new ContinueStatement(Maybe.just(ID))));
//    invalidStmt(1, label("aa", wrapIter(new ContinueStatement(Maybe.just(ID)))));
//  }
//
//  @Test
//  public final void testIdentifierNameMemberMustBeAValidIdentifierName() {
//    validExpr(new IdentifierExpression(new Identifier("x")));
//    validExpr(new IdentifierExpression(new Identifier("$")));
//    validExpr(new IdentifierExpression(new Identifier("_")));
//    validExpr(new IdentifierExpression(new Identifier("_$0x")));
//    validExpr(new StaticMemberExpression(EXPR, ID));
//    validExpr(new StaticMemberExpression(EXPR, new Identifier("if")));
//    validExpr(new ObjectExpression(ImmutableList.list(new DataProperty(new PropertyName(new Identifier("if")), EXPR))));
//    invalidExpr(1, new IdentifierExpression(new Identifier("")));
//    invalidExpr(1, new IdentifierExpression(new Identifier("a-b")));
//    invalidExpr(1, new IdentifierExpression(new Identifier("0x0")));
//    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("")));
//    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("0")));
//    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("a-b")));
//  }
//
//  @Test
//  public final void testIdentifierExpressionMemberMustNotBeAReservedWord() {
//    validExpr(new IdentifierExpression(new Identifier("varx")));
//    validExpr(new IdentifierExpression(new Identifier("xvar")));
//    validExpr(new IdentifierExpression(new Identifier("varif")));
//    validExpr(new IdentifierExpression(new Identifier("if_var")));
//    validExpr(new IdentifierExpression(new Identifier("function0")));
//    invalidExpr(1, new IdentifierExpression(new Identifier("if")));
//    invalidExpr(1, new IdentifierExpression(new Identifier("var")));
//    invalidExpr(1, new IdentifierExpression(new Identifier("function")));
//  }
//
//  @Test
//  public final void testFunctionExpressionNameMustNotBeAReservedWord() {
//    validExpr(new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.<Identifier>nil(), EMPTY_BODY));
//    validExpr(new FunctionExpression(Maybe.just(ID), ImmutableList.<Identifier>nil(), EMPTY_BODY));
//    invalidExpr(1, new FunctionExpression(Maybe.just(BAD_ID), ImmutableList.<Identifier>nil(), EMPTY_BODY));
//  }
//
//  @Test
//  public final void testFunctionDeclarationNameMustNotBeAReservedWord() {
//    validStmt(new FunctionDeclaration(ID, ImmutableList.<Identifier>nil(), EMPTY_BODY));
//    invalidStmt(1, new FunctionDeclaration(BAD_ID, ImmutableList.<Identifier>nil(), EMPTY_BODY));
//  }
//
//  @Test
//  public final void testFunctionExpressionParametersMustNotBeAReservedWord() {
//    validExpr(new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.<Identifier>nil(), EMPTY_BODY));
//    validExpr(new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.list(ID), EMPTY_BODY));
//    invalidExpr(1, new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.list(BAD_ID), EMPTY_BODY));
//    invalidExpr(1, new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.list(ID, BAD_ID), EMPTY_BODY));
//    invalidExpr(1, new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.list(ID, ID, BAD_ID), EMPTY_BODY));
//    invalidExpr(1, new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.list(BAD_ID, ID), EMPTY_BODY));
//    invalidExpr(1, new FunctionExpression(Maybe.<Identifier>nothing(), ImmutableList.list(ID, BAD_ID, ID), EMPTY_BODY));
//  }
//
//  @Test
//  public final void testFunctionDeclarationParametersMustNotBeAReservedWord() {
//    validStmt(new FunctionDeclaration(ID, ImmutableList.<Identifier>nil(), EMPTY_BODY));
//    validStmt(new FunctionDeclaration(ID, ImmutableList.list(ID), EMPTY_BODY));
//    invalidStmt(1, new FunctionDeclaration(ID, ImmutableList.list(BAD_ID), EMPTY_BODY));
//    invalidStmt(1, new FunctionDeclaration(ID, ImmutableList.list(ID, BAD_ID), EMPTY_BODY));
//    invalidStmt(1, new FunctionDeclaration(ID, ImmutableList.list(ID, ID, BAD_ID), EMPTY_BODY));
//    invalidStmt(1, new FunctionDeclaration(ID, ImmutableList.list(BAD_ID, ID), EMPTY_BODY));
//    invalidStmt(1, new FunctionDeclaration(ID, ImmutableList.list(ID, BAD_ID, ID), EMPTY_BODY));
//  }
//
//  @Test
//  public final void testSetterParameterMustNotBeAReservedWord() {
//    validExpr(new ObjectExpression(ImmutableList.list(new Setter(new PropertyName(ID), ID, EMPTY_BODY))));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(new Setter(new PropertyName(ID), BAD_ID, EMPTY_BODY))));
//  }
//
//  @Test
//  public final void testIfStatementWithNullAlternateCanBeTheConsequentOfAnIfStatementWithANonNullAlternate() {
//    validStmt(new IfStatement(EXPR, new DoWhileStatement(new IfStatement(EXPR, STMT), EXPR), STMT));
//    validStmt(new IfStatement(EXPR, new IfStatement(EXPR, STMT), STMT));
//    validStmt(new IfStatement(EXPR, new IfStatement(EXPR, STMT, new IfStatement(EXPR, STMT)), STMT));
//    validStmt(new IfStatement(EXPR, new IfStatement(EXPR, new IfStatement(EXPR, STMT)), STMT));
//    validStmt(new IfStatement(EXPR, new LabeledStatement(ID, new IfStatement(EXPR, STMT)), STMT));
//    validStmt(new IfStatement(EXPR, new WhileStatement(EXPR, new IfStatement(EXPR, STMT)), STMT));
//    validStmt(new IfStatement(EXPR, new WithStatement(EXPR, new IfStatement(EXPR, STMT)), STMT));
//    validStmt(new IfStatement(EXPR, new ForStatement(
//        Maybe.just(Either.right(EXPR)),
//        Maybe.just(EXPR),
//        Maybe.just(EXPR),
//        new IfStatement(EXPR, STMT)), STMT));
//    validStmt(new IfStatement(EXPR, new ForInStatement(Either.right(EXPR), EXPR, new IfStatement(EXPR, STMT)), STMT));
//  }
//
//  @Test
//  public final void testLabeledStatementMustNotBeNestedWithinALabeledStatementWithTheSameLabel() {
//    validStmt(label("a", label("b", STMT)));
//    validStmt(label("a", exprStmt(FE(FD(label("a", STMT))))));
//    invalidStmt(1, label("a", label("a", STMT)));
//
//    // These are not allowed according to the spec but browsers usually allow them.
//    invalidStmt(1, label("a", exprStmt(FE(label("a", STMT)))));
//    invalidStmt(1, label("a", exprStmt(obj(
//        new Getter(new PropertyName("a"), new FunctionBody(ImmutableList.nil(), ImmutableList.list(label("a", STMT))))))));
//    invalidStmt(1, label("a", exprStmt(obj(
//        new Setter(new PropertyName("a"), ID, new FunctionBody(ImmutableList.nil(), ImmutableList.list(label("a", STMT))))))));
//  }
//
//  @Test
//  public final void testNumericLiteralNodesMustNotBeNaN() {
//    invalidExpr(1, new LiteralNumericExpression(Double.NaN));
//  }
//
//  @SuppressWarnings("MagicNumber")
//  @Test
//  public final void testNumericLiteralNodesMustBeNonNegative() {
//    validExpr(new LiteralNumericExpression(0.0));
//    invalidExpr(1, new LiteralNumericExpression(-1));
//    invalidExpr(1, new LiteralNumericExpression(-1e308));
//    invalidExpr(1, new LiteralNumericExpression(-1e-308));
//    invalidExpr(1, new LiteralNumericExpression(-0.0));
//  }
//
//  @Test
//  public final void testNumericLiteralNodesMustBeFinite() {
//    invalidExpr(1, new LiteralNumericExpression(Double.POSITIVE_INFINITY));
//    invalidExpr(1, new LiteralNumericExpression(Double.NEGATIVE_INFINITY));
//  }
//
//  @Test
//  public final void testObjectExpressionConflictingInitGetSetProperties() {
//    ObjectProperty init = new DataProperty(new PropertyName(ID), EXPR);
//    ObjectProperty getter = new Getter(new PropertyName(ID), EMPTY_BODY);
//    ObjectProperty setter = new Setter(new PropertyName(ID), ID, EMPTY_BODY);
//
//    validExpr(new ObjectExpression(ImmutableList.list(init, init)));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(init, getter)));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(init, setter)));
//
//    validExpr(new ObjectExpression(ImmutableList.list(getter, setter)));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(getter, init)));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(getter, getter)));
//
//    validExpr(new ObjectExpression(ImmutableList.list(setter, getter)));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(setter, init)));
//    invalidExpr(1, new ObjectExpression(ImmutableList.list(setter, setter)));
//  }
//
//  @Test
//  public final void testReturnStatementMustBeNestedWithinAFunctionExpressionOrFunctionDeclarationNode() {
//    validExpr(FE(new ReturnStatement(Maybe.nothing())));
//    validStmt(FD(new ReturnStatement(Maybe.nothing())));
//    invalidStmt(1, new ReturnStatement(Maybe.nothing()));
//  }
//
//  @Test
//  public final void testVariableDeclarationStatementInForInVarStatementCanOnlyHasOneVariableDeclarator() {
//    validStmt(new ForInStatement(Either.left(vars(VariableDeclarationKind.Var, "a")), EXPR, STMT));
//    invalidStmt(1, new ForInStatement(Either.left(vars(VariableDeclarationKind.Var, "a", "b")), EXPR, STMT));
//  }
//
//  @Test
//  public final void testPropertyNameOfKindIdentifierMustBeValidIdentifier() {
//    validExpr(obj(init(pn(ID), EXPR)));
//    validExpr(obj(init(pn(new Identifier("function")), EXPR)));
//    invalidExpr(1, obj(init(pn(new Identifier("x x")), EXPR)));
//    invalidExpr(1, obj(init(pn(new Identifier(" ")), EXPR)));
//  }
//
//  @Test
//  public final void testPropertyNameOfKindNumberMustBeValidNumber() {
//    validExpr(obj(init(pn(0), EXPR)));
//    validExpr(obj(init(pn(3), EXPR)));
//    validExpr(obj(init(new PropertyName(""), EXPR)));
//    validExpr(obj(init(new PropertyName("not an ident"), EXPR)));
//    invalidExpr(1, obj(init(pn(-1), EXPR)));
//    invalidExpr(1, obj(init(pn(Double.NaN), EXPR)));
//    invalidExpr(1, obj(init(pn(Double.POSITIVE_INFINITY), EXPR)));
//    invalidExpr(1, obj(init(pn(Double.NEGATIVE_INFINITY), EXPR)));
//  }
//
//  private void testLibrary(String fileName) throws IOException, JsError {
//    String source = readLibrary(fileName);
//    Script script = Parser.parse(source);
//    Validator.validate(script);
//  }
//
//  @Test
//  public final void testLibraries() throws IOException, JsError {
//    ImmutableList<String> jsFiles = ImmutableList.nil();
//    setFatal(false); // Collect the failures in an ErrorCollector
//
//    // Get a list of the js files within the resources directory to process
//    File[] files = new File(getPath(".").toString()).listFiles();
//    if (files == null) {
//      System.out.println("Error retrieving list of javascript libraries.");
//      return;
//    }
//    for (File file : files) {
//      if (file.isFile() && file.getName().endsWith(".js")) {
//        jsFiles = ImmutableList.cons(file.getName(), jsFiles);
//      }
//    }
//
//    // Test the hell out of it... ": )
//    long start = System.nanoTime();
//    System.out.println("Testing " + jsFiles.length + " javascript libraries.");
//    for (String jsLib : jsFiles) {
//      System.out.print(".");
//      testLibrary(jsLib);
//    }
//    System.out.println("");
//    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
//    System.out.printf("Library testing time: %.1fsec\n", elapsed);
//    setFatal(true); // Revert back to the default behavior
//  }
//}
