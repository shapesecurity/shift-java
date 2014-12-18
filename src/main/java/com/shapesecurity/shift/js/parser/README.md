Shift AST Constructors for Java
===========================

## Description

The Parser class takes an ECMAScript program and generates a Shift AST.

## Usage

```java
import com.shapesecurity.shift.js.parser.Parser;

String js = "a; b;";
Script p = Parser.parse(js);
```