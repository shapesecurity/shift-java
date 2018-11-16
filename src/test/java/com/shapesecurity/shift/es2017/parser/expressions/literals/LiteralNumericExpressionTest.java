package com.shapesecurity.shift.es2017.parser.expressions.literals;

import com.shapesecurity.shift.es2017.ast.LiteralNumericExpression;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.ParserTestCase;

import org.junit.Test;

public class LiteralNumericExpressionTest extends ParserTestCase {

    @Test
    public void testLiteralNumericExpression() throws JsError {

        testScript("0", new LiteralNumericExpression(0.0));
        testScript("0;", new LiteralNumericExpression(0.0));
        testScript("3", new LiteralNumericExpression(3.0));
        testScript("5", new LiteralNumericExpression(5.0));
        testScript("0", new LiteralNumericExpression(0.0));
        testScript("\n    0\n\n", new LiteralNumericExpression(0.0));

        testScript(".14", new LiteralNumericExpression(0.14));
        testScript("6.", new LiteralNumericExpression(6.0));
        testScript("0.", new LiteralNumericExpression(0.0));
        testScript("3.14159", new LiteralNumericExpression(3.14159));

        testScript("6.02214179e+23", new LiteralNumericExpression(6.02214179e+23));
        testScript("1.492417830e-10", new LiteralNumericExpression(1.49241783e-10));
        testScript("0e+100 ", new LiteralNumericExpression(0.0));
        testScript("0e+100", new LiteralNumericExpression(0.0));

        testScript("0x0", new LiteralNumericExpression(0.0));
        testScript("0x0;", new LiteralNumericExpression(0.0));
        testScript("0xabc", new LiteralNumericExpression(0xABC * 1.0));
        testScript("0xdef", new LiteralNumericExpression(0xDEF * 1.0));
        testScript("0X1A", new LiteralNumericExpression(0x1A * 1.0));
        testScript("0x10", new LiteralNumericExpression(0x10 * 1.0));
        testScript("0x100", new LiteralNumericExpression(0x100 * 1.0));
        testScript("0X04", new LiteralNumericExpression(0x4 * 1.0));

        // Legacy Octal Integer Literal
        testScript("02", new LiteralNumericExpression(2.0));
        testScript("012", new LiteralNumericExpression(10.0));
        testScript("0012", new LiteralNumericExpression(10.0));
        testScript("\n    0\n\n", new LiteralNumericExpression(0.0));
        testScript("0.", new LiteralNumericExpression(0.0));

        testScriptFailure("'use strict'; 01", 14, "Unexpected legacy octal integer literal");
        testScriptFailure("'use strict'; 0123", 14, "Unexpected legacy octal integer literal");
        testScriptFailure("'use strict'; 00", 14, "Unexpected legacy octal integer literal");
        testScriptFailure("'use strict'; 07", 14, "Unexpected legacy octal integer literal");
        testScriptFailure("'use strict'; 08", 14, "Unexpected noctal integer literal");
        testScriptFailure("'use strict'; 019", 14, "Unexpected noctal integer literal");
        testModuleFailure("01", 0, "Unexpected legacy octal integer literal");

        // Binary Integer Literal
        testScript("0b0", new LiteralNumericExpression(0.0));
        testScript("0b1", new LiteralNumericExpression(1.0));
        testScript("0b10", new LiteralNumericExpression(2.0));
        testScript("0B0", new LiteralNumericExpression(0.0));
        testScript("'use strict'; 0b0", new LiteralNumericExpression(0.0));

        testScriptFailure("0b", 2, "Unexpected end of input");
        testScriptFailure("0b1a", 3, "Unexpected \"a\"");
        testScriptFailure("0b9", 2, "Unexpected \"9\"");
        testScriptFailure("0b18", 3, "Unexpected \"8\"");
        testScriptFailure("0b12", 3, "Unexpected \"2\"");
        testScriptFailure("0B", 2, "Unexpected end of input");
        testScriptFailure("0B1a", 3, "Unexpected \"a\"");
        testScriptFailure("0B9", 2, "Unexpected \"9\"");
        testScriptFailure("0B18", 3, "Unexpected \"8\"");
        testScriptFailure("0B12", 3, "Unexpected \"2\"");

        // Octal Integer Literal
        testScript("0o0", new LiteralNumericExpression(0.0));
        testScript("(0o0)", new LiteralNumericExpression(0.0));
        testScript("0o1", new LiteralNumericExpression(1.0));
        testScript("0o10", new LiteralNumericExpression(8.0));
        testScript("0O0", new LiteralNumericExpression(0.0));
        testScript("018", new LiteralNumericExpression(18.0));
        testScript("'use strict'; 0o0", new LiteralNumericExpression(0.0));
        testScript("09", new LiteralNumericExpression(9.0));
        testScript("09.0", new LiteralNumericExpression(9.0));

        testScriptFailure("0o", 2, "Unexpected end of input");
        testScriptFailure("0o1a", 3, "Unexpected \"a\"");
        testScriptFailure("0o9", 2, "Unexpected \"9\"");
        testScriptFailure("0o18", 3, "Unexpected \"8\"");
        testScriptFailure("0O", 2, "Unexpected end of input");
        testScriptFailure("0O1a", 3, "Unexpected \"a\"");
        testScriptFailure("0O9", 2, "Unexpected \"9\"");
        testScriptFailure("09.x", 3, "Unexpected identifier");
        testScriptFailure("0O18", 3, "Unexpected \"8\"");
    }
}
