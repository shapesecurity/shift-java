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

package com.shapesecurity.laserbat.js.comparison;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.shapesecurity.laserbat.functional.F;
import com.shapesecurity.laserbat.functional.data.List;
import com.shapesecurity.laserbat.functional.data.Maybe;
import com.shapesecurity.laserbat.js.AstHelper;
import com.shapesecurity.laserbat.js.ast.Block;
import com.shapesecurity.laserbat.js.ast.CatchClause;
import com.shapesecurity.laserbat.js.ast.Expression;
import com.shapesecurity.laserbat.js.ast.FunctionBody;
import com.shapesecurity.laserbat.js.ast.Identifier;
import com.shapesecurity.laserbat.js.ast.Script;
import com.shapesecurity.laserbat.js.ast.Statement;
import com.shapesecurity.laserbat.js.ast.SwitchCase;
import com.shapesecurity.laserbat.js.ast.SwitchDefault;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration;
import com.shapesecurity.laserbat.js.ast.VariableDeclaration.VariableDeclarationKind;
import com.shapesecurity.laserbat.js.ast.VariableDeclarator;
import com.shapesecurity.laserbat.js.ast.directive.UnknownDirective;
import com.shapesecurity.laserbat.js.ast.directive.UseStrictDirective;
import com.shapesecurity.laserbat.js.ast.expression.BinaryExpression;
import com.shapesecurity.laserbat.js.ast.expression.CallExpression;
import com.shapesecurity.laserbat.js.ast.expression.ComputedMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ConditionalExpression;
import com.shapesecurity.laserbat.js.ast.expression.FunctionExpression;
import com.shapesecurity.laserbat.js.ast.expression.IdentifierExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralBooleanExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNullExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralNumericExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralRegExpExpression;
import com.shapesecurity.laserbat.js.ast.expression.LiteralStringExpression;
import com.shapesecurity.laserbat.js.ast.expression.NewExpression;
import com.shapesecurity.laserbat.js.ast.expression.ObjectExpression;
import com.shapesecurity.laserbat.js.ast.expression.PostfixExpression;
import com.shapesecurity.laserbat.js.ast.expression.PrefixExpression;
import com.shapesecurity.laserbat.js.ast.expression.StaticMemberExpression;
import com.shapesecurity.laserbat.js.ast.expression.ThisExpression;
import com.shapesecurity.laserbat.js.ast.operators.BinaryOperator;
import com.shapesecurity.laserbat.js.ast.operators.PostfixOperator;
import com.shapesecurity.laserbat.js.ast.operators.PrefixOperator;
import com.shapesecurity.laserbat.js.ast.property.DataProperty;
import com.shapesecurity.laserbat.js.ast.property.Getter;
import com.shapesecurity.laserbat.js.ast.property.PropertyName;
import com.shapesecurity.laserbat.js.ast.property.Setter;
import com.shapesecurity.laserbat.js.ast.statement.BlockStatement;
import com.shapesecurity.laserbat.js.ast.statement.BreakStatement;
import com.shapesecurity.laserbat.js.ast.statement.ContinueStatement;
import com.shapesecurity.laserbat.js.ast.statement.DebuggerStatement;
import com.shapesecurity.laserbat.js.ast.statement.DoWhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.EmptyStatement;
import com.shapesecurity.laserbat.js.ast.statement.ExpressionStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForInStatement;
import com.shapesecurity.laserbat.js.ast.statement.ForStatement;
import com.shapesecurity.laserbat.js.ast.statement.FunctionDeclaration;
import com.shapesecurity.laserbat.js.ast.statement.IfStatement;
import com.shapesecurity.laserbat.js.ast.statement.LabeledStatement;
import com.shapesecurity.laserbat.js.ast.statement.ReturnStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatement;
import com.shapesecurity.laserbat.js.ast.statement.SwitchStatementWithDefault;
import com.shapesecurity.laserbat.js.ast.statement.ThrowStatement;
import com.shapesecurity.laserbat.js.ast.statement.TryFinallyStatement;
import com.shapesecurity.laserbat.js.ast.statement.WhileStatement;
import com.shapesecurity.laserbat.js.ast.statement.WithStatement;
import com.shapesecurity.laserbat.js.codegen.CodeGen;
import com.shapesecurity.laserbat.js.parser.JsError;
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.visitor.CloneReducer;

import org.junit.Test;

import java.io.IOException;

public class ComparisonTest extends AstHelper {
  @Test
  public void testDirectiveEquality() {
    assertTrue(new UnknownDirective("testing").equals(new UnknownDirective("testing")));
    assertTrue(new UseStrictDirective().equals(new UseStrictDirective()));

    assertFalse(new UnknownDirective("testing1").equals(new UnknownDirective("testing2")));
  }

  @Test
  public void testArrayExpressionEquality() {
    List<LiteralStringExpression> a = List.list(new LiteralStringExpression("a"), new LiteralStringExpression("r"),
        new LiteralStringExpression("r"), new LiteralStringExpression("a"), new LiteralStringExpression("y"));
    List<LiteralStringExpression> a1 = List.list(new LiteralStringExpression("a"), new LiteralStringExpression("r"),
        new LiteralStringExpression("r"), new LiteralStringExpression("a"), new LiteralStringExpression("y"));
    List<LiteralStringExpression> a2 = List.list(new LiteralStringExpression("n"), new LiteralStringExpression("o"),
        new LiteralStringExpression("t"), new LiteralStringExpression("" + ' '), new LiteralStringExpression("a"),
        new LiteralStringExpression("r"), new LiteralStringExpression("r"), new LiteralStringExpression("a"),
        new LiteralStringExpression("y"));
    assertTrue(a.equals(a1));
    assertFalse(a.equals(a2));
  }

  @Test
  public void testBinaryExpressionEquality() {
    BinaryExpression be = new BinaryExpression(BinaryOperator.BITWISE_AND, identExpr("left"), identExpr("right"));
    BinaryExpression be1 = new BinaryExpression(BinaryOperator.BITWISE_AND, identExpr("left"), identExpr("right"));
    BinaryExpression be2 = new BinaryExpression(BinaryOperator.BITWISE_OR, identExpr("left"), identExpr("right"));
    BinaryExpression be3 = new BinaryExpression(BinaryOperator.BITWISE_AND, identExpr("notLeft"), identExpr("right"));
    BinaryExpression be4 = new BinaryExpression(BinaryOperator.BITWISE_AND, identExpr("left"), identExpr("notRight"));

    assertTrue(be.equals(be1));
    assertFalse(be.equals(be2));
    assertFalse(be.equals(be3));
    assertFalse(be.equals(be4));
  }

  @Test
  public void testCallExpressionEquality() {
    @SuppressWarnings("MagicNumber")
    Double[] dubs = {1.0, 2.0, 3.0};
    List<Expression> args = List.from(dubs).map((F<Double, Expression>) LiteralNumericExpression::new);
    CallExpression c = new CallExpression(identExpr("callee"), args);
    CallExpression dupC = new CallExpression(identExpr("callee"), args);

    assertTrue(c.equals(dupC));
    assertFalse(c.equals(new CallExpression(identExpr("notCallee"), args)));
    assertFalse(c.equals(dupC.arguments.maybeLast().just()));
  }

  @Test
  public void testComputedMemberEquality() {
    ComputedMemberExpression cm = new ComputedMemberExpression(new ObjectExpression(List.nil()), identExpr("a"));
    ComputedMemberExpression cm1 = new ComputedMemberExpression(new ObjectExpression(List.nil()), identExpr("a"));
    ComputedMemberExpression cm2 = new ComputedMemberExpression(new ObjectExpression(List.list(new Getter(
        new PropertyName("prop"), new FunctionBody(List.nil(), List.nil())))), identExpr("a"));
    ComputedMemberExpression cm3 = new ComputedMemberExpression(new ObjectExpression(List.nil()), identExpr("b"));

    assertTrue(cm.equals(cm1));
    assertFalse(cm.equals(cm2));
    assertFalse(cm.equals(cm3));
  }

  @Test
  public void testConditionalExpressionEquality() {
    ConditionalExpression ce = new ConditionalExpression(new LiteralBooleanExpression(true),
        new LiteralNullExpression(), new LiteralNullExpression());
    ConditionalExpression ce1 = new ConditionalExpression(new LiteralBooleanExpression(true),
        new LiteralNullExpression(), new LiteralNullExpression());
    ConditionalExpression ce2 = new ConditionalExpression(new LiteralBooleanExpression(false),
        new LiteralNullExpression(), new LiteralNullExpression());
    ConditionalExpression ce3 = new ConditionalExpression(new LiteralBooleanExpression(true),
        new LiteralStringExpression("a"), new LiteralNullExpression());
    ConditionalExpression ce4 = new ConditionalExpression(new LiteralBooleanExpression(true),
        new LiteralNullExpression(), new LiteralStringExpression("a"));

    assertTrue(ce.equals(ce1));
    assertFalse(ce.equals(ce2));
    assertFalse(ce.equals(ce3));
    assertFalse(ce.equals(ce4));
  }

  @Test
  public void testFunctionExpressionEquality() {
    List<Identifier> params = List.list(new Identifier("a"), new Identifier("b"));
    FunctionExpression fe = new FunctionExpression(params, body());
    FunctionExpression fe1 = new FunctionExpression(params, body());
    FunctionExpression fe2 = new FunctionExpression(params.maybeTail().just(), body());
    FunctionExpression fe3 = new FunctionExpression(params, body(new BlockStatement(new Block(
        List.list(new ContinueStatement(Maybe.<Identifier>nothing()))))));
    FunctionExpression fe4 = new FunctionExpression(Maybe.just(new Identifier("a")), params, body());

    assertTrue(fe.equals(fe1));
    assertFalse(fe.equals(fe2));
    assertFalse(fe.equals(fe3));
    assertFalse(fe.equals(fe4));
  }

  @Test
  public void testIdentifierExpression() {
    IdentifierExpression ie = identExpr("ie1");
    IdentifierExpression ie1 = identExpr("ie1");
    IdentifierExpression ie2 = identExpr("different");

    assertTrue(ie.equals(ie1));
    assertFalse(ie.equals(ie2));
  }

  @Test
  public void testLiteralBooleanExpressionEquality() {
    LiteralBooleanExpression bool = new LiteralBooleanExpression(true);
    LiteralBooleanExpression bool1 = new LiteralBooleanExpression(true);
    LiteralBooleanExpression bool2 = new LiteralBooleanExpression(false);

    assertTrue(bool.equals(bool1));
    assertFalse(bool.equals(bool2));
  }

  @Test
  public void testLiteralNullExpressionEquality() {
    assertTrue(new LiteralNullExpression().equals(new LiteralNullExpression()));
  }

  @SuppressWarnings("MagicNumber")
  @Test
  public void testLiteralNumericExpressionEquality() {
    LiteralNumericExpression num = new LiteralNumericExpression(1.0);
    LiteralNumericExpression num1 = new LiteralNumericExpression(1.0);
    LiteralNumericExpression num2 = new LiteralNumericExpression(2.0);

    assertTrue(num.equals(num1));
    assertFalse(num.equals(num2));
  }

  @Test
  public void testLiteralRegexExpressionEquality() {
    LiteralRegExpExpression regex = new LiteralRegExpExpression("/.*/");
    LiteralRegExpExpression regex1 = new LiteralRegExpExpression("/.*/");
    LiteralRegExpExpression regex2 = new LiteralRegExpExpression("/[a-b]*/");

    assertTrue(regex.equals(regex1));
    assertFalse(regex.equals(regex2));
  }

  @Test
  public void testLiteralStringExpressionEquality() {
    LiteralStringExpression s = new LiteralStringExpression("test");
    LiteralStringExpression s1 = new LiteralStringExpression("test");
    LiteralStringExpression s2 = new LiteralStringExpression("notTest");

    assertTrue(s.equals(s1));
    assertFalse(s.equals(s2));
  }

  @Test
  public void testNewExpressionEquality() {
    NewExpression ne = new NewExpression(identExpr("callee"), List.nil());
    NewExpression ne1 = new NewExpression(identExpr("callee"), List.nil());
    NewExpression ne2 = new NewExpression(identExpr("notCallee"), List.nil());
    NewExpression ne3 = new NewExpression(identExpr("callee"), List.list(identExpr(
        "arg")));

    assertTrue(ne.equals(ne1));
    assertFalse(ne.equals(ne2));
    assertFalse(ne.equals(ne3));
  }

  @Test
  public void testObjectExpressionEquality() {
    ObjectExpression obj = new ObjectExpression(List.nil());
    ObjectExpression obj1 = new ObjectExpression(List.nil());
    ObjectExpression obj2 = new ObjectExpression(List.list(new Getter(new PropertyName("prop"), body())));

    assertTrue(obj.equals(obj1));
    assertFalse(obj.equals(obj2));
  }

  @Test
  public void testPostfixExpressionEquality() {
    PostfixExpression pfe = new PostfixExpression(PostfixOperator.Increment, identExpr("a"));
    PostfixExpression pfe1 = new PostfixExpression(PostfixOperator.Increment, identExpr("a"));
    PostfixExpression pfe2 = new PostfixExpression(PostfixOperator.Decrement, identExpr("a"));
    PostfixExpression pfe3 = new PostfixExpression(PostfixOperator.Increment, identExpr("b"));

    assertTrue(pfe.equals(pfe1));
    assertFalse(pfe.equals(pfe2));
    assertFalse(pfe.equals(pfe3));
  }

  @Test
  public void testPrefixExpressionEquality() {
    PrefixExpression pfe = new PrefixExpression(PrefixOperator.Plus, identExpr("a"));
    PrefixExpression pfe1 = new PrefixExpression(PrefixOperator.Plus, identExpr("a"));
    PrefixExpression pfe2 = new PrefixExpression(PrefixOperator.Decrement, identExpr("a"));
    PrefixExpression pfe3 = new PrefixExpression(PrefixOperator.Plus, identExpr("b"));

    assertTrue(pfe.equals(pfe1));
    assertFalse(pfe.equals(pfe2));
    assertFalse(pfe.equals(pfe3));
  }

  @Test
  public void testStaticMemberExpressionEquality() {
    StaticMemberExpression sme = new StaticMemberExpression(new ObjectExpression(List.nil()), new Identifier("prop"));
    StaticMemberExpression sme1 = new StaticMemberExpression(new ObjectExpression(List.nil()), new Identifier("prop"));
    StaticMemberExpression sme2 = new StaticMemberExpression(new ObjectExpression(List.list(new Getter(new PropertyName(
        "get"), body()))), new Identifier("prop"));
    StaticMemberExpression sme3 = new StaticMemberExpression(new ObjectExpression(List.nil()), new Identifier(
        "notProp"));

    assertTrue(sme.equals(sme1));
    assertFalse(sme.equals(sme2));
    assertFalse(sme.equals(sme3));
  }

  @Test
  public void testThisExpressionEquality() {
    ThisExpression te = new ThisExpression();
    ThisExpression te1 = new ThisExpression();

    assertTrue(te.equals(te1));
  }

  @Test
  public void testGetPropertyEquality() {
    Getter gp = new Getter(new PropertyName("name"), body());
    Getter gp1 = new Getter(new PropertyName("name"), body());
    Getter gp2 = new Getter(new PropertyName("notName"), body());
    Getter gp3 = new Getter(new PropertyName("name"), body(new EmptyStatement()));

    assertTrue(gp.equals(gp1));
    assertFalse(gp.equals(gp2));
    assertFalse(gp.equals(gp3));
  }

  @Test
  public void testInitPropertyEquality() {
    DataProperty ip = new DataProperty(new PropertyName("name"), new LiteralStringExpression("init"));
    DataProperty ip1 = new DataProperty(new PropertyName("name"), new LiteralStringExpression("init"));
    DataProperty ip2 = new DataProperty(new PropertyName("notName"), new LiteralStringExpression("init"));
    DataProperty ip3 = new DataProperty(new PropertyName("name"), new LiteralStringExpression("notInit"));

    assertTrue(ip.equals(ip1));
    assertFalse(ip.equals(ip2));
    assertFalse(ip.equals(ip3));
  }

  @Test
  public void testPropertyNameEquality() {
    PropertyName pn = new PropertyName("parameter");
    PropertyName pn1 = new PropertyName("parameter");
    PropertyName pn2 = new PropertyName("notParameter");

    assertTrue(pn.equals(pn1));
    assertFalse(pn.equals(pn2));
  }

  @Test
  public void testSetPropertyEquality() {
    Setter sp = new Setter(new PropertyName("name"), new Identifier("name"), body());
    Setter sp1 = new Setter(new PropertyName("name"), new Identifier("name"), body());
    Setter sp2 = new Setter(new PropertyName("notName"), new Identifier("name"), body());
    Setter sp3 = new Setter(new PropertyName("name"), new Identifier("notId"), body());
    Setter sp4 = new Setter(new PropertyName("name"), new Identifier("name"), body(new EmptyStatement()));

    assertTrue(sp.equals(sp1));
    assertFalse(sp.equals(sp2));
    assertFalse(sp.equals(sp3));
    assertFalse(sp.equals(sp4));
  }

  @Test
  public void testBlockStatementEquality() {
    BlockStatement bs = new BlockStatement(new Block(List.nil()));
    BlockStatement bs1 = new BlockStatement(new Block(List.nil()));
    BlockStatement bs2 = new BlockStatement(new Block(List.list(new EmptyStatement())));

    assertTrue(bs.equals(bs1));
    assertFalse(bs.equals(bs2));
  }

  @Test
  public void testBreakStatementEquality() {
    BreakStatement bs = new BreakStatement();
    BreakStatement bs1 = new BreakStatement();
    BreakStatement bs2 = new BreakStatement(Maybe.just(new Identifier("label")));
    BreakStatement bs3 = new BreakStatement(Maybe.just(new Identifier("label")));
    BreakStatement bs4 = new BreakStatement(Maybe.just(new Identifier("notLabel")));

    assertTrue(bs.equals(bs1));
    assertFalse(bs.equals(bs2));
    assertFalse(bs.equals(bs3));
    assertFalse(bs.equals(bs4));
  }

  @Test
  public void testContinueStatementEquality() {
    ContinueStatement cs = new ContinueStatement();
    ContinueStatement cs1 = new ContinueStatement();
    ContinueStatement cs2 = new ContinueStatement(Maybe.just(new Identifier("label")));
    ContinueStatement cs3 = new ContinueStatement(Maybe.just(new Identifier("label")));
    ContinueStatement cs4 = new ContinueStatement(Maybe.just(new Identifier("notLabel")));

    assertTrue(cs.equals(cs1));
    assertFalse(cs.equals(cs2));
    assertFalse(cs.equals(cs3));
    assertFalse(cs.equals(cs4));
  }

  @Test
  public void testDebuggerStatement() {
    DebuggerStatement ds = new DebuggerStatement();
    DebuggerStatement ds1 = new DebuggerStatement();

    assertTrue(ds.equals(ds1));
  }

  @Test
  public void testDoWhileStatementEquality() {
    DoWhileStatement dws = new DoWhileStatement(new EmptyStatement(), new LiteralBooleanExpression(true));
    DoWhileStatement dws1 = new DoWhileStatement(new EmptyStatement(), new LiteralBooleanExpression(true));
    DoWhileStatement dws2 = new DoWhileStatement(new ContinueStatement(), new LiteralBooleanExpression(true));
    DoWhileStatement dws3 = new DoWhileStatement(new EmptyStatement(), new LiteralBooleanExpression(false));

    assertTrue(dws.equals(dws1));
    assertFalse(dws.equals(dws2));
    assertFalse(dws.equals(dws3));
  }

  @Test
  public void testEmptyStatementEquality() {
    EmptyStatement es = new EmptyStatement();
    EmptyStatement es1 = new EmptyStatement();

    assertTrue(es.equals(es1));
  }

  @Test
  public void testExpressionStatementEquality() {
    ExpressionStatement es = new ExpressionStatement(new LiteralStringExpression("test"));
    ExpressionStatement es1 = new ExpressionStatement(new LiteralStringExpression("test"));
    ExpressionStatement es2 = new ExpressionStatement(new LiteralStringExpression("notTest"));

    assertTrue(es.equals(es1));
    assertFalse(es.equals(es2));
  }

  @Test
  public void testForInStatementEquality() {
    {
      ForInStatement fis = new ForInStatement(identExpr("left"), identExpr("right"), new BreakStatement());
      ForInStatement fis1 = new ForInStatement(identExpr("left"), identExpr("right"), new BreakStatement());
      ForInStatement fis2 = new ForInStatement(identExpr("notLeft"), identExpr("right"), new BreakStatement());
      ForInStatement fis3 = new ForInStatement(identExpr("left"), identExpr("notRight"), new BreakStatement());
      ForInStatement fis4 = new ForInStatement(identExpr("left"), identExpr("right"), new ContinueStatement());

      assertTrue(fis.equals(fis1));
      assertFalse(fis.equals(fis2));
      assertFalse(fis.equals(fis3));
      assertFalse(fis.equals(fis4));
    }

    {
      ForInStatement fivs = new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("declarator")))), identExpr("right"), new BlockStatement(
          new Block(List.nil())));
      ForInStatement fivs1 = new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("declarator")))), identExpr("right"), new BlockStatement(
          new Block(List.nil())));
      ForInStatement fivs2 = new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("notDeclarator")))), identExpr("right"),
          new BlockStatement(new Block(List.nil())));
      ForInStatement fivs3 = new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Let, List.list(
          new VariableDeclarator(new Identifier("declarator")))), identExpr("right"), new BlockStatement(
          new Block(List.nil())));
      ForInStatement fivs4 = new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("declarator")))), identExpr("notRight"),
          new BlockStatement(new Block(List.nil())));
      ForInStatement fivs5 = new ForInStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("declarator")))), identExpr("right"), new BlockStatement(
          new Block(List.list(new EmptyStatement()))));

      assertTrue(fivs.equals(fivs1));
      assertFalse(fivs.equals(fivs2));
      assertFalse(fivs.equals(fivs3));
      assertFalse(fivs.equals(fivs4));
      assertFalse(fivs.equals(fivs5));
    }
  }

  @Test
  public void testForStatementEquality() {
    {
      ForStatement fs = new ForStatement(Maybe.nothing(), Maybe.nothing(), Maybe.nothing(), new BlockStatement(
          new Block(List.nil())));
      ForStatement fs1 = new ForStatement(Maybe.nothing(), Maybe.nothing(), Maybe.nothing(), new BlockStatement(
          new Block(List.nil())));
      ForStatement fs2 = new ForStatement(identExpr("init"), Maybe.nothing(), Maybe.nothing(),
          new BlockStatement(new Block(List.nil())));
      ForStatement fs3 = new ForStatement(Maybe.nothing(), Maybe.just(new LiteralBooleanExpression(true)),
          Maybe.nothing(), new BlockStatement(new Block(List.nil())));
      ForStatement fs4 = new ForStatement(Maybe.nothing(), Maybe.nothing(), Maybe.just(new PostfixExpression(
          PostfixOperator.Increment, identExpr("operand"))), new BlockStatement(new Block(List.nil())));
      ForStatement fs5 = new ForStatement(Maybe.nothing(), Maybe.nothing(), Maybe.nothing(), new BlockStatement(
          new Block(List.list(new EmptyStatement()))));

      assertTrue(fs.equals(fs1));
      assertFalse(fs.equals(fs2));
      assertFalse(fs.equals(fs3));
      assertFalse(fs.equals(fs4));
      assertFalse(fs.equals(fs5));
    }

    {
      ForStatement fs = new ForStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("init")))), Maybe.<Expression>nothing(), Maybe.<Expression>nothing(),
          new BlockStatement(new Block(List.nil())));
      ForStatement fs1 = new ForStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("init")))), Maybe.<Expression>nothing(), Maybe.<Expression>nothing(),
          new BlockStatement(new Block(List.nil())));
      ForStatement fs2 = new ForStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("notInit")))), Maybe.<Expression>nothing(), Maybe.<Expression>nothing(),
          new BlockStatement(new Block(List.nil())));
      ForStatement fs3 = new ForStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("init")))), Maybe.<Expression>just(new LiteralBooleanExpression(true)),
          Maybe.<Expression>nothing(), new BlockStatement(new Block(List.nil())));
      ForStatement fs4 = new ForStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("init")))), Maybe.<Expression>nothing(), Maybe.<Expression>just(
          new PostfixExpression(PostfixOperator.Increment, identExpr("operand"))), new BlockStatement(
          new Block(List.nil())));
      ForStatement fs5 = new ForStatement(new VariableDeclaration(VariableDeclarationKind.Var, List.list(
          new VariableDeclarator(new Identifier("init")))), Maybe.<Expression>nothing(), Maybe.<Expression>nothing(),
          new BlockStatement(new Block(List.list(new EmptyStatement()))));

      assertTrue(fs.equals(fs1));
      assertFalse(fs.equals(fs2));
      assertFalse(fs.equals(fs3));
      assertFalse(fs.equals(fs4));
      assertFalse(fs.equals(fs5));
    }
  }

  @Test
  public void testFunctionDeclarationEquality() {
    FunctionDeclaration fd = new FunctionDeclaration(new Identifier("name"), List.nil(), body());
    FunctionDeclaration fd1 = new FunctionDeclaration(new Identifier("name"), List.nil(), body());
    FunctionDeclaration fd2 = new FunctionDeclaration(new Identifier("notId"), List.nil(), body());
    FunctionDeclaration fd3 = new FunctionDeclaration(new Identifier("name"), List.list(ident("notParams")), body());
    FunctionDeclaration fd4 = new FunctionDeclaration(new Identifier("name"), List.nil(), body(new EmptyStatement()));

    assertTrue(fd.equals(fd1));
    assertFalse(fd.equals(fd2));
    assertFalse(fd.equals(fd3));
    assertFalse(fd.equals(fd4));
  }

  @Test
  public void testIfStatementEquality() {
    IfStatement is = new IfStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(List.nil())),
        Maybe.<Statement>nothing());
    IfStatement is1 = new IfStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(List.nil())),
        Maybe.<Statement>nothing());
    IfStatement is2 = new IfStatement(new LiteralBooleanExpression(false), new BlockStatement(new Block(List.nil())),
        Maybe.<Statement>nothing());
    IfStatement is3 = new IfStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(List.list(
        new EmptyStatement()))), Maybe.<Statement>nothing());
    IfStatement is4 = new IfStatement(new LiteralBooleanExpression(true), new BlockStatement(new Block(List.nil())),
        Maybe.<Statement>just(new EmptyStatement()));

    assertTrue(is.equals(is1));
    assertFalse(is.equals(is2));
    assertFalse(is.equals(is3));
    assertFalse(is.equals(is4));
  }

  @Test
  public void testLabeledStatementEquality() {
    LabeledStatement ls = new LabeledStatement(new Identifier("label"), new BlockStatement(new Block(List.nil())));
    LabeledStatement ls1 = new LabeledStatement(new Identifier("label"), new BlockStatement(new Block(List.nil())));
    LabeledStatement ls2 = new LabeledStatement(new Identifier("notLabel"), new BlockStatement(new Block(List.nil())));
    LabeledStatement ls3 = new LabeledStatement(new Identifier("label"), new BlockStatement(new Block(List.list(
        new EmptyStatement()))));

    assertTrue(ls.equals(ls1));
    assertFalse(ls.equals(ls2));
    assertFalse(ls.equals(ls3));
  }

  @Test
  public void testReturnStatementEquality() {
    ReturnStatement rs = new ReturnStatement(Maybe.<Expression>nothing());
    ReturnStatement rs1 = new ReturnStatement(Maybe.<Expression>nothing());
    ReturnStatement rs2 = new ReturnStatement(Maybe.<Expression>just(new LiteralNullExpression()));

    assertTrue(rs.equals(rs1));
    assertFalse(rs.equals(rs2));
  }

  @Test
  public void testSwitchStatementEquality() {
    SwitchStatement ss = new SwitchStatement(new LiteralStringExpression("discriminant"), List.nil());
    SwitchStatement ss1 = new SwitchStatement(new LiteralStringExpression("discriminant"), List.nil());
    SwitchStatement ss2 = new SwitchStatement(new LiteralStringExpression("notDiscriminant"), List.nil());
    SwitchStatement ss3 = new SwitchStatement(new LiteralStringExpression("discriminant"), List.list(new SwitchCase(
        new LiteralStringExpression("value"), List.list(new BreakStatement()))));

    assertTrue(ss.equals(ss1));
    assertFalse(ss.equals(ss2));
    assertFalse(ss.equals(ss3));
  }

  @Test
  public void testSwitchStatementWithDefaultEquality() {
    SwitchStatementWithDefault sswd = new SwitchStatementWithDefault(new LiteralStringExpression("discriminant"),
        List.nil(), new SwitchDefault(List.list(new EmptyStatement())), List.nil());
    SwitchStatementWithDefault sswd1 = new SwitchStatementWithDefault(new LiteralStringExpression("discriminant"),
        List.nil(), new SwitchDefault(List.list(new EmptyStatement())), List.nil());
    SwitchStatementWithDefault sswd2 = new SwitchStatementWithDefault(new LiteralStringExpression("notDiscriminant"),
        List.nil(), new SwitchDefault(List.list(new EmptyStatement())), List.nil());
    SwitchStatementWithDefault sswd3 = new SwitchStatementWithDefault(new LiteralStringExpression("discriminant"),
        List.list(new SwitchCase(new LiteralStringExpression("test"), List.list(new EmptyStatement()))),
        new SwitchDefault(List.list(new EmptyStatement())), List.nil());
    SwitchStatementWithDefault sswd4 = new SwitchStatementWithDefault(new LiteralStringExpression("discriminant"),
        List.nil(), new SwitchDefault(List.list(new BreakStatement())), List.nil());
    SwitchStatementWithDefault sswd5 = new SwitchStatementWithDefault(new LiteralStringExpression("discriminant"),
        List.nil(), new SwitchDefault(List.list(new EmptyStatement())), List.list(new SwitchCase(
        new LiteralStringExpression("test"), List.list(new EmptyStatement()))));

    assertTrue(sswd.equals(sswd1));
    assertFalse(sswd.equals(sswd2));
    assertFalse(sswd.equals(sswd3));
    assertFalse(sswd.equals(sswd4));
    assertFalse(sswd.equals(sswd5));
  }

  @Test
  public void testThrowStatementEquality() {
    ThrowStatement ts = new ThrowStatement(new LiteralStringExpression("arg"));
    ThrowStatement ts1 = new ThrowStatement(new LiteralStringExpression("arg"));
    ThrowStatement ts2 = new ThrowStatement(new LiteralStringExpression("notArg"));

    assertTrue(ts.equals(ts1));
    assertFalse(ts.equals(ts2));
  }

  @Test
  public void testTryStatementEquality() {
    TryFinallyStatement ts = new TryFinallyStatement(new Block(List.nil()), Maybe.just(new CatchClause(new Identifier(
        "param"), new Block(List.nil()))), new Block(List.nil()));
    TryFinallyStatement ts1 = new TryFinallyStatement(new Block(List.nil()), Maybe.just(new CatchClause(new Identifier(
        "param"), new Block(List.nil()))), new Block(List.nil()));
    TryFinallyStatement ts2 = new TryFinallyStatement(new Block(List.list(new EmptyStatement())), Maybe.just(
        new CatchClause(new Identifier("param"), new Block(List.nil()))), new Block(List.nil()));
    TryFinallyStatement ts3 = new TryFinallyStatement(new Block(List.nil()), Maybe.just(new CatchClause(new Identifier(
        "notParam"), new Block(List.nil()))), new Block(List.nil()));
    TryFinallyStatement ts4 = new TryFinallyStatement(new Block(List.nil()), Maybe.just(new CatchClause(new Identifier(
        "param"), new Block(List.nil()))), new Block(List.list(new EmptyStatement())));

    assertTrue(ts.equals(ts1));
    assertFalse(ts.equals(ts2));
    assertFalse(ts.equals(ts3));
    assertFalse(ts.equals(ts4));
  }

  @Test
  public void testVariableDeclarationStatementEquality() {
    VariableDeclaration vds = new VariableDeclaration(VariableDeclaration.VariableDeclarationKind.Var, List.list(
        new VariableDeclarator(new Identifier("name"))));
    VariableDeclaration vds1 = new VariableDeclaration(VariableDeclaration.VariableDeclarationKind.Var, List.list(
        new VariableDeclarator(new Identifier("name"))));
    VariableDeclaration vds2 = new VariableDeclaration(VariableDeclaration.VariableDeclarationKind.Var, List.list(
        new VariableDeclarator(new Identifier("name")), new VariableDeclarator(new Identifier("id1"))));
    VariableDeclaration vds3 = new VariableDeclaration(VariableDeclaration.VariableDeclarationKind.Let, List.list(
        new VariableDeclarator(new Identifier("name"))));

    assertTrue(vds.equals(vds1));
    assertFalse(vds.equals(vds2));
    assertFalse(vds.equals(vds3));
  }

  @Test
  public void testWhileStatementEquality() {
    WhileStatement ws = new WhileStatement(new LiteralBooleanExpression(true), new BreakStatement());
    WhileStatement ws1 = new WhileStatement(new LiteralBooleanExpression(true), new BreakStatement());
    WhileStatement ws2 = new WhileStatement(new LiteralBooleanExpression(false), new BreakStatement());
    WhileStatement ws3 = new WhileStatement(new LiteralBooleanExpression(true), new ContinueStatement());

    assertTrue(ws.equals(ws1));
    assertFalse(ws.equals(ws2));
    assertFalse(ws.equals(ws3));
  }

  @Test
  public void testWithStatementEquality() {
    WithStatement ws = new WithStatement(new ObjectExpression(List.nil()), new EmptyStatement());
    WithStatement ws1 = new WithStatement(new ObjectExpression(List.nil()), new EmptyStatement());
    WithStatement ws2 = new WithStatement(new ObjectExpression(List.list(new Getter(new PropertyName("prop"),
        body()))), new EmptyStatement());
    WithStatement ws3 = new WithStatement(new ObjectExpression(List.nil()), new BlockStatement(new Block(List.nil())));

    assertTrue(ws.equals(ws1));
    assertFalse(ws.equals(ws2));
    assertFalse(ws.equals(ws3));
  }

  @Test
  public void testCatchClauseEquality() {
    CatchClause cc = new CatchClause(new Identifier("binding"), new Block(List.nil()));
    CatchClause cc1 = new CatchClause(new Identifier("binding"), new Block(List.nil()));
    CatchClause cc2 = new CatchClause(new Identifier("notParam"), new Block(List.nil()));
    CatchClause cc3 = new CatchClause(new Identifier("binding"), new Block(List.list(new EmptyStatement())));

    assertTrue(cc.equals(cc1));
    assertFalse(cc.equals(cc2));
    assertFalse(cc.equals(cc3));
  }

  @Test
  public void testIdentifierEquality() {
    Identifier i = new Identifier("identifier");
    Identifier i1 = new Identifier("identifier");
    Identifier i2 = new Identifier("notIdentifier");

    assertTrue(i.equals(i1));
    assertFalse(i.equals(i2));
  }

  @Test
  public void testProgramEquality() {
    Script p = new Script(body(new EmptyStatement()));
    Script p1 = new Script(body(new EmptyStatement()));
    Script p2 = new Script(body(new EmptyStatement(), new EmptyStatement()));

    assertTrue(p.equals(p1));
    assertFalse(p.equals(p2));
  }

  @Test
  public void testProgramBodyEquality() {
    FunctionBody pb = body(new EmptyStatement());
    FunctionBody pb1 = body(new EmptyStatement());
    FunctionBody pb2 = body(new EmptyStatement(), new EmptyStatement());
    FunctionBody pb3 = new FunctionBody(List.list(new UnknownDirective("directive")), List.list(new EmptyStatement()));

    assertTrue(pb.equals(pb1));
    assertFalse(pb.equals(pb2));
    assertFalse(pb.equals(pb3));
  }

  @Test
  public void testSwitchCaseEquality() {
    SwitchCase sc = new SwitchCase(new LiteralStringExpression("test"), List.list(new EmptyStatement()));
    SwitchCase sc1 = new SwitchCase(new LiteralStringExpression("test"), List.list(new EmptyStatement()));
    SwitchCase sc2 = new SwitchCase(new LiteralStringExpression("notTest"), List.list(new EmptyStatement()));
    SwitchCase sc3 = new SwitchCase(new LiteralStringExpression("test"), List.list(new EmptyStatement(),
        new EmptyStatement()));

    assertTrue(sc.equals(sc1));
    assertFalse(sc.equals(sc2));
    assertFalse(sc.equals(sc3));
  }

  @Test
  public void testSwitchDefaultEquality() {
    SwitchDefault sd = new SwitchDefault(List.list(new EmptyStatement()));
    SwitchDefault sd1 = new SwitchDefault(List.list(new EmptyStatement()));
    SwitchDefault sd2 = new SwitchDefault(List.list(new EmptyStatement(), new EmptyStatement()));

    assertTrue(sd.equals(sd1));
    assertFalse(sd.equals(sd2));
  }

  @Test
  public void testVariableDeclaratorEquality() {
    VariableDeclarator vd = new VariableDeclarator(new Identifier("name"), Maybe.<Expression>nothing());
    VariableDeclarator vd1 = new VariableDeclarator(new Identifier("name"), Maybe.<Expression>nothing());
    VariableDeclarator vd2 = new VariableDeclarator(new Identifier("notId"), Maybe.<Expression>nothing());
    VariableDeclarator vd3 = new VariableDeclarator(new Identifier("name"), Maybe.<Expression>just(
        new LiteralStringExpression("different")));

    assertTrue(vd.equals(vd1));
    assertFalse(vd.equals(vd2));
    assertFalse(vd.equals(vd3));
  }

  @Test
  public void testClonedTree() throws IOException, JsError {
    Script original = Parser.parse(readLibrary("everything.js"));
    Script cloned = original.reduce(CloneReducer.INSTANCE);
    assertEquals(CodeGen.codeGen(original), CodeGen.codeGen(cloned));
    assertEquals(original, cloned);
    assertEquals(original.hashCode(), cloned.hashCode());
    assertFalse(original == cloned);
  }
}
