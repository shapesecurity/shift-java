Shift AST Constructors for Java
===============================

## Description

The ScopeAnalyzer class collects variable scoping information into a scope tree.


## Usage
```
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.ast.Program;
import com.shapesecurity.shift.js.scope.ScopeAnalyzer;
import com.shapesecurity.shift.js.scope.GlobalScope;

String source = "a; b;";
Script program = Parser.parse(source);
GlobalScope global = ScopeAnalyzer.analyze(program);

// look at the top-level variables

// list the implicit globals (global throughs)
```