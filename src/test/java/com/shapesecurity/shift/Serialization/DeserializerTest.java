package com.shapesecurity.shift.serialization;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeserializerTest {
  @Test
  public void testDeserializer() throws JSONException {
    Deserializer deserializer = new Deserializer();
    Node node = deserializer.deserialize("{\"type\":\"Script\",\"directives\":[],\"statements\":[]}");
    assertEquals(new Script(ImmutableList.nil(), ImmutableList.nil()), node);
  }


}
