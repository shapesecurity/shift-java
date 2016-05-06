package com.shapesecurity.shift.reducer;

import com.shapesecurity.shift.ast.Module;
import com.shapesecurity.shift.ast.Script;
import com.shapesecurity.shift.parser.JsError;
import com.shapesecurity.shift.parser.Parser;

import static org.junit.Assert.assertEquals;

public abstract class CloneReducerTestCase {
    protected static void cloneTestScript(String sourceText) throws JsError {
        Script script =  Parser.parseScript(sourceText);
        Script clone = (Script) Director.reduceScript(new ReconstructingReducer(), script);
        assertEquals(script, clone);

        // TODO: same thing, but with location this time
    }

    protected static void cloneTestModule(String sourceText) throws JsError {
        Module module =  Parser.parseModule(sourceText);
        Module clone = (Module) Director.reduceModule(new ReconstructingReducer(), module);
        assertEquals(module, clone);

        // TODO: same thing, but with location this time
    }
}
