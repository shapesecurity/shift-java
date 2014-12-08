Shift AST Scope Analyzer
===============================

## Description

The `ScopeAnalyzer` class collects variable scoping information into a scope tree.


## Usage
```
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.ast.Program;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import com.shapesecurity.shift.scope.GlobalScope;

String source = "a; b;";
Script program = Parser.parse(source);
GlobalScope global = ScopeAnalyzer.analyze(program);

// look at the top-level variables

// list the implicit globals (global throughs)

```
