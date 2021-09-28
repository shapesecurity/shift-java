package com.shapesecurity.shift.es2018.astpath;

import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es2018.ast.BinaryExpression;
import com.shapesecurity.shift.es2018.ast.CallExpression;
import com.shapesecurity.shift.es2018.ast.Expression;
import com.shapesecurity.shift.es2018.ast.ExpressionStatement;
import com.shapesecurity.shift.es2018.ast.LiteralStringExpression;
import com.shapesecurity.shift.es2018.ast.Script;
import com.shapesecurity.shift.es2018.ast.SpreadElementExpression;
import com.shapesecurity.shift.es2018.parser.JsError;
import com.shapesecurity.shift.es2018.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

public class ASTPathTest {
	@Test
	public void testBasic() throws JsError {
		String src = "foo + bar('a', 'b');";
		Script tree = Parser.parseScript(src);

		ExpressionStatement statement = (ExpressionStatement) tree.statements.index(0).fromJust();
		BinaryExpression plus = (BinaryExpression) statement.expression;
		CallExpression call = (CallExpression) plus.right;
		LiteralStringExpression bNode = (LiteralStringExpression) call.arguments.index(1).fromJust();

		ObjectPath<Script, SpreadElementExpression> getter =
			ASTPath.Script_Statements(0)
				.then(ASTPath.ExpressionStatement_Expression)
				.then(ASTPath.BinaryExpression_Right)
				.then(ASTPath.CallExpression_Arguments(1))
				.then(ObjectPath.identity());

		assertSame(bNode, getter.apply(tree).fromJust());

		assertEquals("b", getter.then(ASTPath.LiteralStringExpression_Value).apply(tree).fromJust());

		assertEquals(Maybe.empty(), getter.apply(Parser.parseScript(";")));
	}

	@Test
	public void testEquals() {
		ObjectPath<Script, SpreadElementExpression> getter1 =
			ASTPath.Script_Statements(0)
				.then(ASTPath.ExpressionStatement_Expression)
				.then(ASTPath.BinaryExpression_Right)
				.then(ASTPath.CallExpression_Arguments(1));

		ObjectPath<Script, SpreadElementExpression> getter2 =
			ASTPath.Script_Statements(0)
				.then(ASTPath.ExpressionStatement_Expression)
				.then(
					ASTPath.BinaryExpression_Right
						.then(ASTPath.CallExpression_Arguments(1))
				)
				.then(ObjectPath.identity());

		assertEquals(getter1, getter2);
		assertEquals(getter1.hashCode(), getter2.hashCode());

		ObjectPath<Script, SpreadElementExpression> different =
			ASTPath.Script_Statements(0)
				.then(ASTPath.ExpressionStatement_Expression)
				.then(
					ASTPath.BinaryExpression_Right
						.then(ASTPath.CallExpression_Arguments(2))
				);
		assertNotEquals(getter1, different);
		assertNotEquals(getter1.hashCode(), different.hashCode());

		assertEquals(ObjectPath.identity(), ObjectPath.identity());
		assertEquals(ObjectPath.identity().then(ObjectPath.identity()), ObjectPath.identity());
		assertEquals(ObjectPath.identity(), ObjectPath.identity().then(ObjectPath.identity()));
		assertEquals(ObjectPath.identity().then(ObjectPath.identity()), ObjectPath.identity().then(ObjectPath.identity()));

		ObjectPath<BinaryExpression, Expression> leftRight = ASTPath.BinaryExpression_Left.then(ASTPath.BinaryExpression_Right);
		ObjectPath<BinaryExpression, Expression> rightLeft = ASTPath.BinaryExpression_Right.then(ASTPath.BinaryExpression_Left);

		assertNotEquals(leftRight, rightLeft);
	}
}