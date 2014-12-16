Shift AST Constructors for Java
===========================

## Description

The Minifier class takes a Script object and reduces the AST based on reduction and expansion rules returning a Program object.

## Usage

```java
import com.shapesecurity.shift.js.minifier.Minifier;
import com.shapesecurity.shift.js.parser.Parser;
import com.shapesecurity.shift.js.codegen.CodeGen;


String js = "a; b;";
Script p = Parser.parse(js);
List<ReductionRule> reductionRules = List.list<>(FlattenBlocks.INSTANCE, ReduceNestedIfStatements.INSTANCE, ...);
List<ExpansionRule> expansionRules = List.list<>();
Script pPrime = Minifier.minify(p, reductionRules, expansionRules);
String minified = CodeGen.codeGen(pPrime);

// using the default sets of reduction/expansion rules
Script pPrimePrime = Minifier.minify(p);
```
