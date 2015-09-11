package com.shapesecurity.shift.scope;

import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;

import junit.framework.TestCase;

import org.junit.Test;

public class ScopeSerializerTest {

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
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier(v2)_1\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v2)_1\", \"kind\": \"Var\"}]}, {\"name\": \"v1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(v1)_2\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(v2)_1\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}", serialized);
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
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier(v2)_1\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v2)_1\", \"kind\": \"Var\"}]}, {\"name\": \"v1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(v1)_2\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(v2)_1\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}", serialized);
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
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"r\", \"references\": [{\"node\": \"BindingIdentifier(r)_1\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(r)_1\", \"kind\": \"Var\"}]}, {\"name\": \"f1\", \"references\": [{\"node\": \"IdentifierExpression(f1)_2\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_3\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f1)_2\", \"accessibility\": \"Read\"}, {\"node\": \"BindingIdentifier(r)_1\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_4\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"v1\", \"references\": [{\"node\": \"BindingIdentifier(v1)_5\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(v1)_6\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v1)_5\", \"kind\": \"Var\"}]}, {\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f2\", \"references\": [{\"node\": \"IdentifierExpression(f2)_7\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f2)_8\", \"kind\": \"FunctionDeclaration\"}]}, {\"name\": \"p2\", \"references\": [{\"node\": \"IdentifierExpression(p2)_9\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(p2)_10\", \"kind\": \"Param\"}]}, {\"name\": \"p1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(p1)_11\", \"kind\": \"Param\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_12\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(p2)_9\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(v1)_6\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier(v2)_13\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(v2)_14\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v2)_13\", \"kind\": \"Var\"}]}, {\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"p1\", \"references\": [{\"node\": \"IdentifierExpression(p1)_15\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(p1)_16\", \"kind\": \"Param\"}]}], \"children\": []}]}]}]}", serialized);
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
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(f1)_1\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"f2\", \"references\": [{\"node\": \"BindingIdentifier(f2)_2\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f2)_3\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f2)_2\", \"kind\": \"Var\"}]}, {\"name\": \"f1\", \"references\": [{\"node\": \"IdentifierExpression(f1)_1\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f1)_1\", \"accessibility\": \"Read\"}, {\"node\": \"BindingIdentifier(f2)_2\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f2)_3\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_4\", \"type\": \"FunctionName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f1\", \"references\": [{\"node\": \"BindingIdentifier(f1)_5\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_6\", \"kind\": \"FunctionExpressionName\"}]}], \"children\": [{\"node\": \"FunctionExpression_4\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(f1)_5\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}", serialized);
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
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"foo\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(foo)_1\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_2\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"bar\", \"references\": [{\"node\": \"IdentifierExpression(bar)_3\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(bar)_4\", \"kind\": \"FunctionDeclaration\"}, {\"node\": \"BindingIdentifier(bar)_5\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_6\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"FunctionDeclaration_7\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}", serialized);
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
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(alert)_1\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"alert\", \"references\": [{\"node\": \"IdentifierExpression(alert)_1\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(alert)_1\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_2\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(alert)_1\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f1\", \"references\": [{\"node\": \"BindingIdentifier(f1)_3\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f1)_4\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_3\", \"kind\": \"Var\"}]}], \"children\": []}]}]}", serialized);
    }
}
