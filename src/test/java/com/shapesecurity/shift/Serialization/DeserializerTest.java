package com.shapesecurity.shift.serialization;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.ast.*;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;
import org.json.JSONException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeserializerTest {

  @Test
  public void testDeserializeArrayBinding() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
//    testHelperFromAST(new AssignmentExpression(new ArrayBinding(ImmutableList.list(Maybe.just(new BindingIdentifier("x"))), Maybe.nothing()), new LiteralNumericExpression(0.0)));
  }

  @Test
  public void testDeserializeAssignmentExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("x=0");
    testHelperFromCode("(x)=(0)");

  }

  @Test
  public void testDeserializeBinaryExpression() throws IllegalAccessException, NoSuchMethodException, InstantiationException, JSONException, JsError, InvocationTargetException, ClassNotFoundException {
    testHelperFromCode("1 + 2");
    testHelperFromCode("1 == 2");
    testHelperFromCode("1 * 2");
    testHelperFromCode("1 && 2");
    testHelperFromCode("1 < 2");
    testHelperFromCode("1 >>> 2");
    testHelperFromCode("1 ^ 2");
    // TODO more tests for different expressions
  }

  @Test
  public void testDeserializeScript() throws JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException, JsError {
    testHelperFromAST(new Script(ImmutableList.nil(), ImmutableList.nil()));
    testHelperFromAST(new Script(ImmutableList.list(new Directive("hi"), new Directive("hello")), ImmutableList.nil()));
    testHelperFromAST(new Script(ImmutableList.list(new Directive("hi"), new Directive("hello")), ImmutableList.list(new DebuggerStatement(), new EmptyStatement())));
  }


  /******************
   * HELPER METHODS *
   ******************/

  private void testHelperFromCode(String jsCode) throws JsError, IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
    Script nodeOriginal = Parser.parseScript(jsCode);
    testHelperFromAST(nodeOriginal);
  }

  private void testHelperFromAST(Node node) throws IllegalAccessException, InvocationTargetException, InstantiationException, JSONException, NoSuchMethodException, ClassNotFoundException {
    String nodeSerialized = Serializer.serialize(node);

    Deserializer deserializer = new Deserializer();
    Node nodeDeserialized = deserializer.deserialize(nodeSerialized);
    assertTrue(node.equals(nodeDeserialized));
  }

}
