# Shift Templates

This class provides a templating system for JavaScript programs.

See [the JavaScript project](https://github.com/shapesecurity/shift-template-js) for more description.

## Java-specific notes

The main difference from the JavaScript implementation is the TemplateValues class, which is used to instantiated structured templates.

This class is constructed with three maps, each going from labels to boolean conditions, to lists of TemplateValues objects to be used in loops, and to Function<node, node> replacement functions. For example:

```java
String source = "" +
    "f(" +
    "  /*# for each x of xs #*/" +
    "    /*# x::arg #*/ replaceme," +
    ")";

Map<String, Boolean> conditions = Map.of();
Map<String, List<ReduceStructured.TemplateValues>> lists = Map.of(
    "xs",
    List.of(
        new ReduceStructured.TemplateValues(
            Map.of(),
            Map.of(),
            Map.of("arg", node -> new IdentifierExpression("x_1"))
        ),
        new ReduceStructured.TemplateValues(
            Map.of(),
            Map.of(),
            Map.of("arg", node -> new IdentifierExpression("x_2"))
        )
    )
);
Map<String, F<Node, Node>> replacers = Map.of();

ReduceStructured.TemplateValues values = new ReduceStructured.TemplateValues(conditions, lists, replacers);

Template moduleTemplate = Template.fromModuleSource(source);
Program result = moduleTemplate.applyStructured(values);

assertEquals(Parser.parseModule("f(x_1, x_2)"), result);
```