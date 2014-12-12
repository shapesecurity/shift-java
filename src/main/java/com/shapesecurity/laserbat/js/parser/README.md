LaserBat AST Constructors for Java
===========================

## Description

The Parser class takes an ECMAScript program and generates a LaserBat AST.

## Usage

```java
import com.shapesecurity.laserbat.js.parser.Parser;

String js = "a; b;";
Script p = Parser.parse(js);
```
