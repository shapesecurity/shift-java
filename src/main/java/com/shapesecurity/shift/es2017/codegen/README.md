Shift AST Code Generator
========================

##Description

The `CodeGen.codeGen` function takes a `Script` object and generates the ECMAScript string representation of it.

The generated program generally parses into a `Script` object that is identical to the original one with two exceptions:
  * if the `Script` object cannot pass `Validator` test, the behavior of code generation is undefined.
  * if there is an `IfStatement` without `alternate`-clause who is directly followed by an `alternate`-clause of an
  `IfStatement`, the `consequent`-clause of the latter `IfStatement` is wrapped by a `BlockStatement`, resulting in a
  slightly different parse tree.

## Usage

```java
import com.shapesecurity.shift.parser.Parser;
import com.shapesecurity.shift.codegen.CodeGen;


String js = "a; b;";
Script p = Parser.parse(js);
String jsPrime = CodeGen.codeGen(p);
```
