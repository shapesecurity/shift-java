## Usage

```java
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.codegen.CodeGen;

String js = "whatever";
Program p = Parser.parse(js);
String jsPrime = CodeGen.codeGen(p);
```
