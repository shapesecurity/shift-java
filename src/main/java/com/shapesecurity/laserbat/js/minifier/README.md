## Usage

```java
import com.shapesecurity.laserbat.js.minifier.Minifier;
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.codegen.CodeGen;

String js = "whatever";
Program p = Parser.parse(js);
Program pPrime = Minifier.minify(p);
String minified = CodeGen.codeGen(pPrime);
```

```java
import com.shapesecurity.laserbat.js.minifier.Minifier;
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.codegen.CodeGen;

String js = "whatever";
Program p = Parser.parse(js);
List<ReductionRule> reductionRules = List.list<>(FlattenBlocks.INSTANCE, ReduceNestedIfStatements.INSTANCE, ...);
List<ExpansionRule> expansionRules = List.list<>();
Program pPrime = Minifier.minify(p, reductionRules, expansionRules);
String minified = CodeGen.codeGen(pPrime);
```
