Shift AST Parser
================

## Description

The `Parser.parse` function takes an ECMAScript program and generates a `Script` object representing a Shift AST.

## Usage

```java
import com.shapesecurity.shift.parser.Parser;

String js = "a; b;";
Script p = Parser.parse(js);
```
