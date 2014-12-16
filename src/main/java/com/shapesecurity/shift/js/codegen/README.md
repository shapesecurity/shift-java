Shift AST Constructors for Java
===============================

##Description

The CodeGen class takes a Script object and generates the javascript string representation of it.

## Usage

```java
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.codegen.CodeGen;


String js = "a; b;";
Script p = Parser.parse(js);
String jsPrime = CodeGen.codeGen(p);
```
