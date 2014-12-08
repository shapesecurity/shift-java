Scope Analysis
--------------


## Imports

```
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.ast.Program;
import com.shapesecurity.laserbat.js.scope.ScopeAnalyzer;
import com.shapesecurity.laserbat.js.scope.GlobalScope;
```


## Method Invocation
```
String source = "...");
Program program = new Parser(source).parse();
GlobalScope global = ScopeAnalyzer.analyze(program);
```
