'use strict';


let fs = require('fs');

const { ensureDir, nodes, makeHeader, sanitize, year } = require('../lib/utilities.js');
const { outDir } = require('../lib/out-dir.js');

const pathDir = 'astpath/';
ensureDir(outDir + pathDir);


function cap(name) {
  return name[0].toUpperCase() + name.slice(1);
}

let branchContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.astpath;


import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.Module;

import java.util.Objects;


public abstract class ASTPath<S, T> implements ObjectPath<S, T> {
  private ASTPath() {}

  public abstract String propertyName();

  public boolean equals(Object o) {
    return this == o || o != null && getClass() == o.getClass();
  }

  public int hashCode() {
    return Objects.hash(getClass());
  }

`;

let classContent = [];
let classes = [];

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes;

  attrs.forEach(a => {
    let isList = a.type.kind === 'list';
    let isMaybe = a.type.kind === 'nullable';// || a.type.kind === 'list' && a.type.argument.kind === 'nullable';
    let isListMaybe = a.type.kind === 'list' && a.type.argument.kind === 'nullable';

    let name = `${typeName}_${cap(a.name)}`;
    classContent.push(`
  public static final ${name} ${name} = new ${name}();
`);

    let returnType = isListMaybe
      ? a.type.argument.argument.argument
      : isList || isMaybe
        ? a.type.argument.argument
        : a.type.argument;

    // capitalize first letter, for primitives
    returnType = returnType[0].toUpperCase() + returnType.substring(1);

    // enums need to be qualified, for some reason
    if (a.type.kind === 'enum' && returnType.endsWith('Operator')) {
      returnType = `com.shapesecurity.shift.es${year}.ast.operators.${returnType}`;
    }

    if (isListMaybe) {
      returnType = `ImmutableList<Maybe<${returnType}>>`;
    } else if (isList) {
      returnType = `ImmutableList<${returnType}>`;
    } else if (isMaybe) {
      returnType = `Maybe<${returnType}>`;
    }

    let cl = `
  public static class ${name} extends ASTPath<${typeName}, ${returnType}> {
    private ${name}() {}

    @Override
    public Maybe<${returnType}> apply(Object source) {
      if (!(source instanceof ${typeName})) return Maybe.empty();
      return Maybe.of(((${typeName}) source).${sanitize(a.name)});
    }

    public String propertyName() {
      return "${a.name}";
    }
  }`;

    classes.push(cl);
  });
}

branchContent += `${classContent.join('')}

${classes.join('\n')}
}
`;

fs.writeFileSync(outDir + pathDir + 'ASTPath.java', branchContent, 'utf-8');
