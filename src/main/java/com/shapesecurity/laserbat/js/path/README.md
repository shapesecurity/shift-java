LaserBat AST Tooling for Java
=======================

## Description

Paths in AST are represented as a list of branches. A branch represents a function from a parent node to one of its children.

## Usage

```java
import com.shapesecurity.laserbat.js.parser.Parser;
import com.shapesecurity.laserbat.js.path.Branch;

String js = "a; b;";
Script p = Parser.parse(js);

List<Branch> path;
// TODO: Do something to create the path list
        :  :  :

// Follow path from node p
Node n = p;
while (!path.isEmpty()) {
	Maybe<Node>maybeNext = path.maybeHead().bind(n::branchChild);
	if (maybeNext.isJust()) {
		n = maybeNext.just();	} else {
		// path is not valid
		break;	}
	path = path.maybeTail().just();}

// do something with n
```