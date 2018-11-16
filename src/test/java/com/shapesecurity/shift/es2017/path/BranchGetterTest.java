package com.shapesecurity.shift.es2017.path;

import com.shapesecurity.shift.es2017.ast.*;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import junit.framework.TestCase;

public class BranchGetterTest extends TestCase {
	public void testSimple() throws JsError {
		String src = "foo + bar('a', 'b');";
		Script tree = Parser.parseScript(src);

		ExpressionStatement statement = (ExpressionStatement) tree.statements.index(0).fromJust();
		BinaryExpression plus = (BinaryExpression) statement.expression;
		CallExpression call = (CallExpression) plus.right;
		LiteralStringExpression bNode = (LiteralStringExpression) call.arguments.index(1).fromJust();

		BranchGetter getter = new BranchGetter()
			.d(Branch.ScriptStatements_(0))
			.d(Branch.ExpressionStatementExpression_())
			.d(Branch.BinaryExpressionRight_())
			.d(Branch.CallExpressionArguments_(1));

		BranchGetter getter2 = BranchGetter.of(
			Branch.ScriptStatements_(0),
			Branch.ExpressionStatementExpression_(),
			Branch.BinaryExpressionRight_(),
			Branch.CallExpressionArguments_(1)
		);

		assertTrue(bNode == getter.apply(tree).fromJust());
		assertTrue(bNode == getter2.apply(tree).fromJust());
	}
}
