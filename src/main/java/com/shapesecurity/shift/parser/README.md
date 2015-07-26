Shift AST Parser
================

## Description

The `Parser.parseScript` function parses a String of ECMAScript program text using `Script` as the goal symbol, and either returns a `Script` node or throws a `ParseError`. Similarly, the `Parser.parseModule` function parses a String of ECMAScript program text using `Module` as the goal symbol, and either returns a `Module` node or throws a `ParseError`.

## Usage

```java
import com.shapesecurity.shift.parser.Parser;

String js = "a; b;";
Script p = Parser.parseScript(js);
Module m = Parser.parseModule(js);
```
