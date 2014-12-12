Scope Analysis
--------------

The ScopeAnalyzer class collects variable scoping information into a scope tree.

## Imports

```
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.ast.Program;
import com.shapesecurity.laserbat.js.scope.ScopeAnalyzer;
import com.shapesecurity.laserbat.js.scope.GlobalScope;
```


## Method Invocation
```
String source = "...";
Script program = Parser.parse(source);
GlobalScope global = ScopeAnalyzer.analyze(program);

// look at the top-level variables

// list the implicit globals (global throughs)
```
