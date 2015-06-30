package com.shapesecurity.shift.parser.API;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.parser.Assertions;
import com.shapesecurity.shift.parser.JsError;
import org.junit.Test;

public class ModuleTest extends Assertions {
  @Test
  public void testModule() throws JsError {
    testModule("", new Module(ImmutableList.nil(), ImmutableList.nil()));
//    testModuleFailure("/*", 0, "Unexpected end of input");
  }
}
