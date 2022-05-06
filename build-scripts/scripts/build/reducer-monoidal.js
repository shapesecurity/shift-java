/**
 * Copyright 2018 Shape Security, Inc.
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

const { ensureDir, nodes, makeHeader, isStatefulType, sanitize, toJavaType, year } = require('../lib/utilities.js');
const { outDir } = require('../lib/out-dir.js');

const reducerDir = 'reducer/';
ensureDir(outDir + reducerDir);


let monoidalContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.reducer;

import com.shapesecurity.functional.data.*;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.Module;

import javax.annotation.Nonnull;

public class MonoidalReducer<State> implements Reducer<State> {
    @Nonnull
    protected final Monoid<State> monoidClass;

    public MonoidalReducer(@Nonnull Monoid<State> monoidClass) {
        this.monoidClass = monoidClass;
    }

    protected State identity() {
        return this.monoidClass.identity();
    }

    protected State append(State a, State b) {
        return this.monoidClass.append(a, b);
    }

    protected State append(State a, State b, State c) {
        return append(append(a, b), c);
    }

    protected State append(State a, State b, State c, State d) {
        return append(append(a, b, c), d);
    }

    protected State fold(ImmutableList<State> as) {
        return as.foldLeft(this::append, this.identity());
    }

    protected State fold1(ImmutableList<State> as, State a) {
        return as.foldLeft(this::append, a);
    }

    @Nonnull
    protected State o(@Nonnull Maybe<State> s) {
        return s.orJust(this.identity());
    }
`;


function red(attr) {
  switch (attr.type.kind) {
    case 'nullable':
      return `o(${sanitize(attr.name)})`;
    case 'list':
      if (attr.type.argument.kind === 'nullable') {
        return `fold(Maybe.catMaybes(${sanitize(attr.name)}))`;
      }
      return `fold(${sanitize(attr.name)})`;
    default:
      return sanitize(attr.name);
  }
}

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type));
  let attrStrings = attrs.map(f => `            @Nonnull ${toJavaType(f.type, 'State')} ${sanitize(f.name)}`);
  if (attrStrings.length === 0) {
    monoidalContent += `
    @Nonnull
    @Override
    public State reduce${typeName}(@Nonnull ${typeName} node`;
  } else {
    monoidalContent += `
    @Nonnull
    @Override
    public State reduce${typeName}(
            @Nonnull ${typeName} node,
${attrStrings.join(',\n')}`;
  }
  let rv;
  if (attrs.length === 0) {
    rv = 'this.identity()';
  } else if (attrs.length === 1) {
    rv = red(attrs[0]);
  } else if (attrs.length === 2 && attrs[1].type.kind === 'list' && attrs[0].type.kind !== 'list') {
    rv = `fold1(${sanitize(attrs[1].name)}, ${red(attrs[0])})`;
  } else {
    rv = `append(${attrs.map(red).join(', ')})`;
  }
  monoidalContent += `) {
        return ${rv};
    }
`;
}

monoidalContent += '}\n';

fs.writeFileSync(outDir + reducerDir + 'MonoidalReducer.java', monoidalContent, 'utf-8');
