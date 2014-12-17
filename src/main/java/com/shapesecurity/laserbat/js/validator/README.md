Shift AST Constructors for Java
===============================

## Description

The Validator class validates an AST and returns a list of Validation errors.

## Usage

```java
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.ast.Program;
import com.shapesecurity.shift.js.scope.ScopeAnalyzer;
import com.shapesecurity.shift.js.valid.Validator;
import com.shapesecurity.shift.js.valid.ValidationError;

// parse a javascript program
String source = "a; b;";
Script p = new Parser(source).parse();

// validate the AST
List<ValidationError>  errs = Validator.validate(p);
```
