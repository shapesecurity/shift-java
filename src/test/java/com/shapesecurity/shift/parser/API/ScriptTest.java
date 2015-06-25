package com.shapesecurity.shift.parser.API;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

/**
 * Created by u478 on 6/22/15.
 */
public class ScriptTest extends Assertions {
  @Test
  public void testScript() throws JsError {
    testScript("", new Script(ImmutableList.nil(), ImmutableList.nil()));
    testScript(" ", new Script(ImmutableList.nil(), ImmutableList.nil()));
//    testScriptFailure("/*", 0, "Unexpected end of input");
  }
}
