Shift AST Validator
===========================

## Description

The `Validator.validate` function validates an AST and returns a list of validation errors.

## Usage

```java
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.ast.Program;
import com.shapesecurity.shift.scope.ScopeAnalyzer;
import com.shapesecurity.shift.js.valid.Validator;
import com.shapesecurity.shift.js.valid.ValidationError;

String source = "a; b;";
Script script = new Parser(source).parse();
List<ValidationError>  errs = Validator.validate(script);
```
