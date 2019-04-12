package com.shapesecurity.shift.es2017.scope;

import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class VariableOrderingTest {

	private static void getVariables(Scope scope, ArrayList<Variable> out) {
		out.addAll(scope.variables());
		scope.children.forEach(child -> getVariables(child, out));
	}



	@Test
	public void testVariableOrdering() throws JsError {
		StringBuilder js = new StringBuilder();
		List<String> expectedOrder = new ArrayList<>();
		for (int i = 0; i < 100; ++i) {
			int x = 100 - i;
			js.append("let i").append(x).append(" = 0;");
			js.append("{ let i").append(x).append(" = 0; }");
			js.append("(function(){ let i").append(x).append(" = 0; });");
			expectedOrder.add("i" + x);
		}
		expectedOrder.sort(String::compareTo);
		ParserWithLocation originalParser = new ParserWithLocation();
		Script script = originalParser.parseScript(js.toString());
		GlobalScope scope = ScopeAnalyzer.analyze(script);
		ArrayList<Variable> originalVariableOrder = new ArrayList<>();
		getVariables(scope, originalVariableOrder);

		for (int i = 0; i < 10; ++i) {
			ParserWithLocation newParser = new ParserWithLocation();
			Script newScript = newParser.parseScript(js.toString());
			GlobalScope newScope = ScopeAnalyzer.analyze(newScript);
			ArrayList<Variable> newVariableOrder = new ArrayList<>(originalVariableOrder.size());
			getVariables(newScope, newVariableOrder);

			Assert.assertEquals(originalVariableOrder.size(), newVariableOrder.size());

			for (int j = 0; j < originalVariableOrder.size(); ++j) {
				Variable originalVar = originalVariableOrder.get(j);
				Variable newVar = newVariableOrder.get(j);

				Assert.assertEquals(
					originalVar.declarations.isEmpty(),
					newVar.declarations.isEmpty()
				);

				if (originalVar.declarations.isNotEmpty()) {
					Assert.assertEquals(
						originalParser.getLocation(originalVar.declarations.maybeHead().fromJust().node).fromJust().start.offset,
						newParser.getLocation(newVar.declarations.maybeHead().fromJust().node).fromJust().start.offset
					);
				}
			}

		}
	}

}
