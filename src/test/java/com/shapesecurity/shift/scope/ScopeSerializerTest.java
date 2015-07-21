package com.shapesecurity.shift.scope;

import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import junit.framework.TestCase;
import org.junit.Test;

public class ScopeSerializerTest extends TestCase {

  @Test
  public void testScopeSerializer_variableDeclaration1() throws JsError {
    // get scope tree
    String js = "var v1; var v2 = 'hello';";
    Script script = Parser.parseScript(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    // get serialization of scope tree
    ScopeSerializer serializer = new ScopeSerializer();
    String serialized = serializer.serializeScope(globalScope);

    // check serialization
    assertEquals("{\"node\": \"Script_0\", \"through\": [], \"children\": [], \"type\": \"Global\", \"isDynamic\": true, \"variables\": [{\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier_1\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier_1\", \"kind\": \"Var\"}]}, {\"name\": \"v1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier_2\", \"kind\": \"Var\"}]}]}", serialized);
  }

  @Test
  public void testScopeSerializer_variableDeclaration2() throws JsError {
    // get scope tree
    String js = "var v1, v2 = 'hello';";
    Script script = Parser.parseScript(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    // get serialization of scope tree
    ScopeSerializer serializer = new ScopeSerializer();
    String serialized = serializer.serializeScope(globalScope);

    // check serialization
    assertEquals("{\"node\": \"Script_0\", \"through\": [], \"children\": [], \"type\": \"Global\", \"isDynamic\": true, \"variables\": [{\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier_1\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier_1\", \"kind\": \"Var\"}]}, {\"name\": \"v1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier_2\", \"kind\": \"Var\"}]}]}", serialized);
  }

  @Test
  public void testScopeSerializer_functionDeclaration1() throws JsError {
    // get scope tree
    String js = "function f1(p1, p2) {" +
      "  var v1 = 1;" +
      "  function f2(p1) {" +
      "    var v2 = p1 + v1 + p2;" +
      "    return v2;" +
      "  }" +
      "  return f2;" +
      '}' +
      "var r = f1(2, 3);";
    Script script = Parser.parseScript(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    // get serialization of scope tree
    ScopeSerializer serializer = new ScopeSerializer();
    String serialized = serializer.serializeScope(globalScope);

    // check serialization
    assertEquals("{\"node\": \"Script_0\", \"through\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"through\": [], \"children\": [{\"node\": \"FunctionDeclaration_2\", \"through\": [{\"node\": \"IdentifierExpression_3\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression_4\", \"accessibility\": \"Read\"}], \"children\": [], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier_5\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression_6\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_5\", \"kind\": \"Var\"}]}, {\"name\": \"p1\", \"references\": [{\"node\": \"IdentifierExpression_7\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_8\", \"kind\": \"Param\"}]}, {\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"v1\", \"references\": [{\"node\": \"BindingIdentifier_9\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression_4\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_9\", \"kind\": \"Var\"}]}, {\"name\": \"p2\", \"references\": [{\"node\": \"IdentifierExpression_3\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_10\", \"kind\": \"Param\"}]}, {\"name\": \"p1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier_11\", \"kind\": \"Param\"}]}, {\"name\": \"f2\", \"references\": [{\"node\": \"IdentifierExpression_12\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_13\", \"kind\": \"FunctionName\"}]}, {\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}], \"type\": \"Global\", \"isDynamic\": true, \"variables\": [{\"name\": \"r\", \"references\": [{\"node\": \"BindingIdentifier_14\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier_14\", \"kind\": \"Var\"}]}, {\"name\": \"f1\", \"references\": [{\"node\": \"IdentifierExpression_15\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_16\", \"kind\": \"FunctionName\"}]}]}", serialized);
  }

  @Test
  public void testScopeSerializer_functionExpression1() throws JsError {
    // get scope tree
    String js = "var f2 = function f1() {f1 = 'hello';}; f1(); f2();";
    Script script = Parser.parseScript(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    // get serialization of scope tree
    ScopeSerializer serializer = new ScopeSerializer();
    String serialized = serializer.serializeScope(globalScope);

    // check serialization
    assertEquals("{\"node\": \"Script_0\", \"through\": [{\"node\": \"IdentifierExpression_1\", \"accessibility\": \"Read\"}], \"children\": [{\"node\": \"FunctionExpression_2\", \"through\": [], \"children\": [{\"node\": \"FunctionExpression_2\", \"through\": [{\"node\": \"BindingIdentifier_3\", \"accessibility\": \"Write\"}], \"children\": [], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}], \"type\": \"FunctionName\", \"isDynamic\": false, \"variables\": [{\"name\": \"f1\", \"references\": [{\"node\": \"BindingIdentifier_3\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier_4\", \"kind\": \"FunctionName\"}]}]}], \"type\": \"Global\", \"isDynamic\": true, \"variables\": [{\"name\": \"f2\", \"references\": [{\"node\": \"BindingIdentifier_5\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression_6\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_5\", \"kind\": \"Var\"}]}, {\"name\": \"f1\", \"references\": [{\"node\": \"IdentifierExpression_1\", \"accessibility\": \"Read\"}], \"declarations\": []}]}", serialized);
  }

  @Test
  public void testScopeSerializer_hoistDeclaration1() throws JsError {
    // get scope tree
    String js = "function foo() {" +
      "  function bar() {" +
      "    return 3;" +
      "  }" +
      "  return bar();" +
      "  function bar() {" +
      "    return 'hello';" +
      "  }" +
      '}';
    Script script = Parser.parseScript(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    // get serialization of scope tree
    ScopeSerializer serializer = new ScopeSerializer();
    String serialized = serializer.serializeScope(globalScope);

    // check serialization
    assertEquals("{\"node\": \"Script_0\", \"through\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"through\": [], \"children\": [{\"node\": \"FunctionDeclaration_2\", \"through\": [], \"children\": [], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}, {\"node\": \"FunctionDeclaration_3\", \"through\": [], \"children\": [], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"bar\", \"references\": [{\"node\": \"IdentifierExpression_4\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_5\", \"kind\": \"FunctionName\"}, {\"node\": \"BindingIdentifier_6\", \"kind\": \"FunctionName\"}]}, {\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}], \"type\": \"Global\", \"isDynamic\": true, \"variables\": [{\"name\": \"foo\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier_7\", \"kind\": \"FunctionName\"}]}]}", serialized);
  }

  @Test
  public void testScopeSerializer_closure1() throws JsError {
    // get scope tree
    String js = "(function() {var f1 = 'hello'; alert(f1);})();";
    Script script = Parser.parseScript(js);
    GlobalScope globalScope = ScopeAnalyzer.analyze(script);

    // get serialization of scope tree
    ScopeSerializer serializer = new ScopeSerializer();
    String serialized = serializer.serializeScope(globalScope);

    // check serialization
    assertEquals("{\"node\": \"Script_0\", \"through\": [{\"node\": \"IdentifierExpression_1\", \"accessibility\": \"Read\"}], \"children\": [{\"node\": \"FunctionExpression_2\", \"through\": [{\"node\": \"IdentifierExpression_1\", \"accessibility\": \"Read\"}], \"children\": [], \"type\": \"Function\", \"isDynamic\": false, \"variables\": [{\"name\": \"f1\", \"references\": [{\"node\": \"BindingIdentifier_3\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression_4\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier_3\", \"kind\": \"Var\"}]}, {\"name\": \"arguments\", \"references\": [], \"declarations\": []}]}], \"type\": \"Global\", \"isDynamic\": true, \"variables\": [{\"name\": \"alert\", \"references\": [{\"node\": \"IdentifierExpression_1\", \"accessibility\": \"Read\"}], \"declarations\": []}]}", serialized);
  }
}
