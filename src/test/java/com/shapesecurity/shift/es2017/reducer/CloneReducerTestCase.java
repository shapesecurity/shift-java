package com.shapesecurity.shift.es2017.reducer;

import com.shapesecurity.shift.es2017.ast.Module;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;

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
