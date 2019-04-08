package com.shapesecurity.shift.es2017.scope;

import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class VariableOrderingTest {

	@Test
	public void testVariableOrdering() throws JsError {
		StringBuilder js = new StringBuilder();
		List<String> expectedOrder = new ArrayList<>();
		for (int i = 0; i < 100; ++i) {
			int x = 100 - i;
			js.append("let i").append(x).append(" = 0;");
			expectedOrder.add("i" + x);
		}
		expectedOrder.sort(String::compareTo);
		Script script = Parser.parseScript(js.toString());
		GlobalScope scope = ScopeAnalyzer.analyze(script);
		Map<Variable, String> nameMap = new HashMap<>();
		Set<Variable> variableSet = new HashSet<>();
		scope.children.maybeHead().fromJust().variables().forEach(variable -> {
			nameMap.put(variable, variable.name);
			variableSet.add(variable);
		});
		List<Variable> mapVariables = new ArrayList<>(nameMap.keySet());
		mapVariables.sort(Variable::compareTo);
		List<Variable> setVariables = new ArrayList<>(variableSet);
		setVariables.sort(Variable::compareTo);
		assertEquals(expectedOrder, mapVariables.stream().map(variable -> variable.name).collect(Collectors.toList()));
		assertEquals(expectedOrder, setVariables.stream().map(variable -> variable.name).collect(Collectors.toList()));
	}

}
