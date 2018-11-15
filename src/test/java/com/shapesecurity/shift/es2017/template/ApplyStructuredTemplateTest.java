package com.shapesecurity.shift.es2017.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.shift.es2017.ast.IdentifierExpression;
import com.shapesecurity.shift.es2017.ast.Node;
import com.shapesecurity.shift.es2017.ast.Program;
import com.shapesecurity.shift.es2017.ast.Script;
import com.shapesecurity.shift.es2017.parser.JsError;
import com.shapesecurity.shift.es2017.parser.Parser;
import com.shapesecurity.shift.es2017.parser.ParserWithLocation;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.shapesecurity.shift.es2017.template.Template.findNodes;

public class ApplyStructuredTemplateTest extends TestCase {
	public void testNesting() throws JsError {
		String source = "" +
			"f(" +
			"  /*# for each x of xs #*/" +
			"    /*# if x::include #*/" +
			"      /*# for each y of x::ys #*/" +
			"        /*# y::arg #*/ a" +
			");";
		String expected = "f(a_1, a_2, a_5, a_6);";

		HashMap<String, Boolean> conditions = new HashMap<>();
		HashMap<String, List<ReduceStructured.TemplateValues>> lists = new HashMap<String, List<ReduceStructured.TemplateValues>>() {{
			put("xs", Arrays.asList(
				new ReduceStructured.TemplateValues(
					new HashMap<String, Boolean>() {{ put("include", true); }},
					new HashMap<String, List<ReduceStructured.TemplateValues>>() {{
						put("ys", Arrays.asList(
							new ReduceStructured.TemplateValues(
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<String, F<Node, Node>>() {{ put("arg", node -> new IdentifierExpression(((IdentifierExpression) node).name + "_1")); }}
							),
							new ReduceStructured.TemplateValues(
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<String, F<Node, Node>>() {{ put("arg", node -> new IdentifierExpression(((IdentifierExpression) node).name + "_2")); }}
							)
						));
					}},
					new HashMap<>()
				),
				new ReduceStructured.TemplateValues(
					new HashMap<String, Boolean>() {{ put("include", false); }},
					new HashMap<>(),
					new HashMap<>()
				),
				new ReduceStructured.TemplateValues(
					new HashMap<String, Boolean>() {{ put("include", true); }},
					new HashMap<String, List<ReduceStructured.TemplateValues>>() {{
						put("ys", Arrays.asList(
							new ReduceStructured.TemplateValues(
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<String, F<Node, Node>>() {{ put("arg", node -> new IdentifierExpression(((IdentifierExpression) node).name + "_5")); }}
							),
							new ReduceStructured.TemplateValues(
								new HashMap<>(),
								new HashMap<>(),
								new HashMap<String, F<Node, Node>>() {{ put("arg", node -> new IdentifierExpression(((IdentifierExpression) node).name + "_6")); }}
							)
						));
					}},
					new HashMap<>()
				)
			));
		}};
		HashMap<String, F<Node, Node>> replacers = new HashMap<>();

		ReduceStructured.TemplateValues values = new ReduceStructured.TemplateValues(conditions, lists, replacers);



		ParserWithLocation parserWithLocation = new ParserWithLocation();
		Script tree = parserWithLocation.parseScript(source);
		ImmutableList<Template.NodeInfo> namePairs = findNodes(tree, parserWithLocation, parserWithLocation.getComments());

		Program result = Template.applyStruturedTemplate(tree, namePairs, values);
		assertEquals(Parser.parseScript(expected), result);
	}
}
