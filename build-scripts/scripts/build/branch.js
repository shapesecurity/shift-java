/**
 * Copyright 2016 Shape Security, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

let fs = require('fs');

const { ensureDir, nodes, makeHeader, isStatefulType, sanitize, year } = require('../lib/utilities.js');

const outDir = 'out/';
const pathDir = 'path/';
ensureDir(outDir + pathDir);


function cap(name) {
  return name[0].toUpperCase() + name.slice(1);
}

let branchContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.path;


import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;

import java.util.Objects;


public abstract class Branch {
	abstract public Maybe<? extends Node> step(Node node);

	abstract public String propertyName();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		return o != null && getClass() == o.getClass();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass());
	}
`;

let classContent = [];
let classes = [];

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type));

  attrs.forEach(a => {
    let isList = a.type.kind === 'list';
    let isMaybe = a.type.kind === 'nullable';// || a.type.kind === 'list' && a.type.argument.kind === 'nullable';
    let isListMaybe = a.type.kind === 'list' && a.type.argument.kind === 'nullable';

    let name = `${typeName}${cap(a.name)}`;
    classContent.push(`
	public static ${name} ${name}_(${isList ? 'int index' : ''}) {
		return new ${name}(${isList ? 'index' : ''});
	}
`);

    let cl = `
class ${name} extends `;
    if (isListMaybe) {
      cl += `IndexedBranch {
	protected ${name}(int index) {
		super(index);
	}

	@Override
	public Maybe<? extends Node> step(Node node) {
		if (!(node instanceof ${typeName})) return Maybe.empty();
		return ((${typeName}) node).${sanitize(a.name)}.index(index).orJust(Maybe.empty());
	}

	public String propertyName() {
		return "${a.name}[" + Integer.toString(index) + "]";
	}
}`;
    } else if (isList) {
      cl += `IndexedBranch {
	protected ${name}(int index) {
		super(index);
	}

	@Override
	public Maybe<? extends Node> step(Node node) {
		if (!(node instanceof ${typeName})) return Maybe.empty();
		return ((${typeName}) node).${sanitize(a.name)}.index(index);
	}

	public String propertyName() {
		return "${a.name}[" + Integer.toString(index) + "]";
	}
}`;
    } else if (isMaybe) {
      cl += `Branch {
	@Override
	public Maybe<? extends Node> step(Node node) {
		if (!(node instanceof ${typeName})) return Maybe.empty();
		return ((${typeName}) node).${sanitize(a.name)};
	}

	public String propertyName() {
		return "${a.name}";
	}
}`;
    } else {
      cl += `Branch {
	@Override
	public Maybe<? extends Node> step(Node node) {
		if (!(node instanceof ${typeName})) return Maybe.empty();
		return Maybe.of(((${typeName}) node).${sanitize(a.name)});
	}

	public String propertyName() {
		return "${a.name}";
	}
}`;
    }

    classes.push(cl);
  });
}

branchContent += `${classContent.join('')}
}

abstract class IndexedBranch extends Branch {
	public final int index;

	protected IndexedBranch(int index) {
		this.index = index;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IndexedBranch that = (IndexedBranch) o;
		return index == that.index;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), index);
	}
}

${classes.join('\n')}
`;

fs.writeFileSync(outDir + pathDir + 'Branch.java', branchContent, 'utf-8');
