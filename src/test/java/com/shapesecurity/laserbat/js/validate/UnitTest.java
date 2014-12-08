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

package com.shapesecurity.laserbat.js.validate;

import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.js.AstHelper;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.SwitchCase;
import com.shapesecurity.laserbat.js.ast.SwitchDefault;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration.VariableDeclarationKind;
import com.shapesecurity.laserbat.js.ast.expression.IdentifierExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.laserbat.js.ast.expression.ObjectExpression;
import com.shapesecurity.laserbat.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.laserbat.js.ast.property.DataProperty;
import com.shapesecurity.laserbat.js.ast.property.Getter;
import com.shapesecurity.laserbat.js.ast.property.ObjectProperty;
import com.shapesecurity.laserbat.js.ast.property.PropertyName;
import com.shapesecurity.laserbat.js.ast.property.Setter;
import com.shapesecurity.laserbat.js.ast.statement.BreakStatement;
import com.shapesecurity.laserbat.js.ast.statement.ContinueStatement;
import com.shapesecurity.laserbat.js.ast.statement.DoWhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForInStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForStatement;
import com.shapesecurity.laserbat.js.ast.statement.IfStatement;
import com.shapesecurity.laserbat.js.ast.statement.LabeledStatement;
import com.shapesecurity.laserbat.js.ast.statement.ReturnStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.laserbat.js.ast.statement.WhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.WithStatement;
import com.shapesecurity.laserbat.js.parser.JsError;
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.valid.Validator;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class UnitTest extends AstHelper {
  @Test
  public final void testBreakStatement() {
    validStmt(label(ID.name, wrapIter(new BreakStatement(Maybe.just(ID)))));
    invalidStmt(1, wrapIter(new BreakStatement(Maybe.just(ID))));
    invalidStmt(1, label("aa", wrapIter(new BreakStatement(Maybe.just(ID)))));
    validStmt(new SwitchStatementWithDefault(EXPR, List.<SwitchCase>nil(), new SwitchDefault(List.<Statement>list(
        new BreakStatement(Maybe.<Identifier>nothing()))), List.<SwitchCase>nil()));
    invalidStmt(1, new SwitchStatementWithDefault(EXPR, List.<SwitchCase>nil(), new SwitchDefault(List.<Statement>list(
        new BreakStatement(Maybe.just(ID)))), List.<SwitchCase>nil()));
  }

  @Test
  public final void testContinueStatement() {
    validStmt(label("a", wrapIter(new ContinueStatement(Maybe.just(ID)))));
    invalidStmt(1, wrapIter(new ContinueStatement(Maybe.just(ID))));
    invalidStmt(1, label("aa", wrapIter(new ContinueStatement(Maybe.just(ID)))));
  }

  @Test
  public final void testIdentifierNameMemberMustBeAValidIdentifierName() {
    validExpr(new IdentifierExpression(new Identifier("x")));
    validExpr(new IdentifierExpression(new Identifier("$")));
    validExpr(new IdentifierExpression(new Identifier("_")));
    validExpr(new IdentifierExpression(new Identifier("_$0x")));
    invalidExpr(1, new IdentifierExpression(new Identifier("")));
    invalidExpr(1, new IdentifierExpression(new Identifier("a-b")));
    invalidExpr(1, new IdentifierExpression(new Identifier("0x0")));
  }

  @Test
  public final void testIdentifierNameMemberMustNotBeAReservedWord() {
    validExpr(new IdentifierExpression(new Identifier("varx")));
    validExpr(new IdentifierExpression(new Identifier("xvar")));
    validExpr(new IdentifierExpression(new Identifier("varif")));
    validExpr(new IdentifierExpression(new Identifier("if_var")));
    validExpr(new IdentifierExpression(new Identifier("function0")));
    invalidExpr(1, new IdentifierExpression(new Identifier("if")));
    invalidExpr(1, new IdentifierExpression(new Identifier("var")));
    invalidExpr(1, new IdentifierExpression(new Identifier("function")));
  }

  @Test
  public final void testIfStatementWithNullAlternateCanBeTheConsequentOfAnIfStatementWithANonNullAlternate() {
    validStmt(new IfStatement(EXPR, new DoWhileStatement(new IfStatement(EXPR, STMT), EXPR), STMT));
    validStmt(new IfStatement(EXPR, new IfStatement(EXPR, STMT), STMT));
    validStmt(new IfStatement(EXPR, new IfStatement(EXPR, STMT, new IfStatement(EXPR, STMT)), STMT));
    validStmt(new IfStatement(EXPR, new IfStatement(EXPR, new IfStatement(EXPR, STMT)), STMT));
    validStmt(new IfStatement(EXPR, new LabeledStatement(ID, new IfStatement(EXPR, STMT)), STMT));
    validStmt(new IfStatement(EXPR, new WhileStatement(EXPR, new IfStatement(EXPR, STMT)), STMT));
    validStmt(new IfStatement(EXPR, new WithStatement(EXPR, new IfStatement(EXPR, STMT)), STMT));
    validStmt(new IfStatement(EXPR, new ForStatement(EXPR, Maybe.just(EXPR), Maybe.just(EXPR), new IfStatement(
        EXPR, STMT)), STMT));
    validStmt(new IfStatement(EXPR, new ForInStatement(EXPR, EXPR, new IfStatement(EXPR, STMT)), STMT));
  }

  @Test
  public final void testLabeledStatementMustNotBeNestedWithinALabeledStatementWithTheSameLabel() {
    validStmt(label("a", label("b", STMT)));
    validStmt(label("a", exprStmt(FE(FD(label("a", STMT))))));
    invalidStmt(1, label("a", label("a", STMT)));
    invalidStmt(1, label("a", exprStmt(FE(label("a", STMT)))));
  }

  @Test
  public final void testNumericLiteralNodesMustNotBeNaN() {
    invalidExpr(1, new LiteralNumericExpression(Double.NaN));
  }

  @SuppressWarnings("MagicNumber")
  @Test
  public final void testNumericLiteralNodesMustBeNonNegative() {
    validExpr(new LiteralNumericExpression(0.0));
    invalidExpr(1, new LiteralNumericExpression(-1));
    invalidExpr(1, new LiteralNumericExpression(-1e308));
    invalidExpr(1, new LiteralNumericExpression(-1e-308));
    invalidExpr(1, new LiteralNumericExpression(-0.0));
  }

  @Test
  public final void testNumericLiteralNodesMustBeFinite() {
    invalidExpr(1, new LiteralNumericExpression(Double.POSITIVE_INFINITY));
    invalidExpr(1, new LiteralNumericExpression(Double.NEGATIVE_INFINITY));
  }

  @Test
  public final void testStaticMemberExpressionPropertyMemberMustHaveAValidIdentifierNameNameMember() {
    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("var")));
    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("")));
    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("0")));
    invalidExpr(1, new StaticMemberExpression(EXPR, new Identifier("a-b")));
  }

  @Test
  public final void testObjectExpressionConflictingInitGetSetProperties() {
    ObjectProperty init = new DataProperty(new PropertyName(ID), EXPR);
    ObjectProperty getter = new Getter(new PropertyName(ID), EMPTY_BODY);
    ObjectProperty setter = new Setter(new PropertyName(ID), ID, EMPTY_BODY);

    validExpr(new ObjectExpression(List.list(init, init)));
    invalidExpr(1, new ObjectExpression(List.list(init, getter)));
    invalidExpr(1, new ObjectExpression(List.list(init, setter)));

    validExpr(new ObjectExpression(List.list(getter, setter)));
    invalidExpr(1, new ObjectExpression(List.list(getter, init)));
    invalidExpr(1, new ObjectExpression(List.list(getter, getter)));

    validExpr(new ObjectExpression(List.list(setter, getter)));
    invalidExpr(1, new ObjectExpression(List.list(setter, init)));
    invalidExpr(1, new ObjectExpression(List.list(setter, setter)));
  }

  @Test
  public final void testReturnStatementMustBeNestedWithinAFunctionExpressionOrFunctionDeclarationNode() {
    validExpr(FE(new ReturnStatement()));
    validStmt(FD(new ReturnStatement()));
    invalidStmt(1, new ReturnStatement());
  }

  @Test
  public final void testVariableDeclarationStatementInForInVarStatementCanOnlyHasOneVariableDeclarator() {
    validStmt(new ForInStatement(vars(VariableDeclarationKind.Var, "a"), EXPR, STMT));
    invalidStmt(1, new ForInStatement(vars(VariableDeclarationKind.Var, "a", "b"), EXPR, STMT));
  }

  private void testLibrary(String fileName) throws IOException, JsError {
    String source = readLibrary(fileName);
    Script script = Parser.parse(source);
    Validator.validate(script);
  }

  @Test
  public final void testLibraries() throws IOException, JsError {
    List<String> jsFiles = List.nil();
    setFatal(false); // Collect the failures in an ErrorCollector

    // Get a list of the js files within the resources directory to process
    File[] files = new File(getPath(".").toString()).listFiles();
    if (files == null) {
      System.out.println("Error retrieving list of javascript libraries.");
      return;
    }
    for (File file : files) {
      if (file.isFile() && file.getName().endsWith(".js")) {
        jsFiles = List.cons(file.getName(), jsFiles);
      }
    }

    // Test the hell out of it... ": )
    long start = System.nanoTime();
    System.out.println("Testing " + jsFiles.length() + " javascript libraries.");
    for (String jsLib : jsFiles) {
      System.out.print(".");
      testLibrary(jsLib);
    }
    System.out.println("");
    double elapsed = ((System.nanoTime() - start) * NANOS_TO_SECONDS);
    System.out.printf("Library testing time: %.1fsec\n", elapsed);
    setFatal(true); // Revert back to the default behavior
  }
}
