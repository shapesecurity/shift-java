LaserBat AST Constructors for Java
===========================

## Description

The Validator class validates an AST and returns a list of Validation errors.

## Usage

```java
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.ast.Program;
import com.shapesecurity.laserbat.js.scope.ScopeAnalyzer;
import com.shapesecurity.laserbat.js.valid.Validator;
import com.shapesecurity.laserbat.js.valid.ValidationError;

String source = "a; b;";
String program = new Parser(source).parse();
List<ValidationError>  errs = Validator.validate(program);
```
