package com.shapesecurity.shift.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shapesecurity.shift.ast.Node;
import com.shapesecurity.shift.ast.Script;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Deserializer {

  public Deserializer() {
  }

  public Node deserialize(String toDeserialize) throws JSONException {
//    JSONObject json = new JSONObject(toDeserialize);
//    return deserializeHelper(json);

    Gson gson = new GsonBuilder().create();
    Script script = gson.fromJson(toDeserialize, Script.class);
    return script;
  }

  private Node deserializeHelper(JSONObject jsonObject) {

    Iterator jsonKeysIterator = jsonObject.keys();

    while(jsonKeysIterator.hasNext()) {
      String key = (String)jsonKeysIterator.next();
//      System.out.println(key);
      if (key.equals("type")) {

      }
    }

    return null;
  }


}
