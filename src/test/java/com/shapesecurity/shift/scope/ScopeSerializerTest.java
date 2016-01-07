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
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"v1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(v1)_4\", \"kind\": \"Var\"}]}, {\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier(v2)_8\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v2)_8\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(v2)_8\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}", serialized);
    }

    @Test
    public void testScopeSerializer_variableDeclaration2() throws JsError {
        // get scope tree
        String js = "var v1, v2 = 'hello';";
        Script script = Parser.parseScript(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        // get serialization of scope tree
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"v1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(v1)_4\", \"kind\": \"Var\"}]}, {\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier(v2)_6\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v2)_6\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(v2)_6\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": []}]}", serialized);
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
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"f1\", \"references\": [{\"node\": \"IdentifierExpression(f1)_35\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_2\", \"kind\": \"FunctionDeclaration\"}]}, {\"name\": \"r\", \"references\": [{\"node\": \"BindingIdentifier(r)_33\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(r)_33\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f1)_35\", \"accessibility\": \"Read\"}, {\"node\": \"BindingIdentifier(r)_33\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f2\", \"references\": [{\"node\": \"IdentifierExpression(f2)_29\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f2)_13\", \"kind\": \"FunctionDeclaration\"}]}, {\"name\": \"p1\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(p1)_4\", \"kind\": \"Parameter\"}]}, {\"name\": \"p2\", \"references\": [{\"node\": \"IdentifierExpression(p2)_25\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(p2)_5\", \"kind\": \"Parameter\"}]}, {\"name\": \"v1\", \"references\": [{\"node\": \"BindingIdentifier(v1)_10\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(v1)_24\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v1)_10\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_12\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(v1)_24\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(p2)_25\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"p1\", \"references\": [{\"node\": \"IdentifierExpression(p1)_23\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(p1)_15\", \"kind\": \"Parameter\"}]}, {\"name\": \"v2\", \"references\": [{\"node\": \"BindingIdentifier(v2)_20\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(v2)_27\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(v2)_20\", \"kind\": \"Var\"}]}], \"children\": []}]}]}]}", serialized);
    }

    @Test
    public void testScopeSerializer_functionExpression1() throws JsError {
        // get scope tree
        String js = "var f2 = function f1() {f1 = 'hello';}; f1(); f2();";
        Script script = Parser.parseScript(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        // get serialization of scope tree
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(f1)_15\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"f1\", \"references\": [{\"node\": \"IdentifierExpression(f1)_15\", \"accessibility\": \"Read\"}], \"declarations\": []}, {\"name\": \"f2\", \"references\": [{\"node\": \"BindingIdentifier(f2)_4\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f2)_18\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f2)_4\", \"kind\": \"Var\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f1)_15\", \"accessibility\": \"Read\"}, {\"node\": \"IdentifierExpression(f2)_18\", \"accessibility\": \"Read\"}, {\"node\": \"BindingIdentifier(f2)_4\", \"accessibility\": \"Write\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_5\", \"type\": \"FunctionName\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"f1\", \"references\": [{\"node\": \"BindingIdentifier(f1)_11\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_6\", \"kind\": \"FunctionExpressionName\"}]}], \"children\": [{\"node\": \"FunctionExpression_5\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(f1)_11\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}", serialized);
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
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [{\"name\": \"foo\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(foo)_2\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionDeclaration_1\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"bar\", \"references\": [{\"node\": \"IdentifierExpression(bar)_13\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(bar)_6\", \"kind\": \"FunctionDeclaration\"}, {\"node\": \"BindingIdentifier(bar)_15\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_5\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"FunctionDeclaration_14\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}", serialized);
    }

    @Test
    public void testScopeSerializer_closure1() throws JsError {
        // get scope tree
        String js = "(function() {var f1 = 'hello'; alert(f1);})();";
        Script script = Parser.parseScript(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        // get serialization of scope tree
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [{\"node\": \"IdentifierExpression(alert)_13\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"alert\", \"references\": [{\"node\": \"IdentifierExpression(alert)_13\", \"accessibility\": \"Read\"}], \"declarations\": []}], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(alert)_13\", \"accessibility\": \"Read\"}], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(alert)_13\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f1\", \"references\": [{\"node\": \"BindingIdentifier(f1)_9\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f1)_14\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f1)_9\", \"kind\": \"Var\"}]}], \"children\": []}]}]}", serialized);
    }

    @Test
    public void testScopeSerializer_B331() throws JsError {
        // get scope tree
        String js = "(function() {" +
                "function getOuter(){return f;}" +
                " var g;" +
                "{" +
                "   f = 1;" +
                "   function f(){}" +
                "   g = f;" +
                "}" +
                "})();";
        Script script = Parser.parseScript(js);
        GlobalScope globalScope = ScopeAnalyzer.analyze(script);

        // get serialization of scope tree
        String serialized = ScopeSerializer.serialize(globalScope);

        // check serialization
        TestCase.assertEquals("{\"node\": \"Script_0\", \"type\": \"Global\", \"isDynamic\": true, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"Script_0\", \"type\": \"Script\", \"isDynamic\": false, \"through\": [], \"variables\": [], \"children\": [{\"node\": \"FunctionExpression_3\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}, {\"name\": \"f\", \"references\": [{\"node\": \"IdentifierExpression(f)_11\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_23\", \"kind\": \"Var\"}]}, {\"name\": \"g\", \"references\": [{\"node\": \"BindingIdentifier(g)_28\", \"accessibility\": \"Write\"}], \"declarations\": [{\"node\": \"BindingIdentifier(g)_15\", \"kind\": \"Var\"}]}, {\"name\": \"getOuter\", \"references\": [], \"declarations\": [{\"node\": \"BindingIdentifier(getOuter)_7\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_6\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [{\"node\": \"IdentifierExpression(f)_11\", \"accessibility\": \"Read\"}], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}, {\"node\": \"Block_17\", \"type\": \"Block\", \"isDynamic\": false, \"through\": [{\"node\": \"BindingIdentifier(g)_28\", \"accessibility\": \"Write\"}], \"variables\": [{\"name\": \"f\", \"references\": [{\"node\": \"BindingIdentifier(f)_20\", \"accessibility\": \"Write\"}, {\"node\": \"IdentifierExpression(f)_29\", \"accessibility\": \"Read\"}], \"declarations\": [{\"node\": \"BindingIdentifier(f)_23\", \"kind\": \"FunctionDeclaration\"}]}], \"children\": [{\"node\": \"FunctionDeclaration_22\", \"type\": \"Function\", \"isDynamic\": false, \"through\": [], \"variables\": [{\"name\": \"arguments\", \"references\": [], \"declarations\": []}], \"children\": []}]}]}]}]}", serialized);
    }
}
