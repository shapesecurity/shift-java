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

const { ensureDir, makeHeader, sanitize, year } = require('../lib/utilities.js');
const { outDir } = require('../lib/out-dir.js');

const spec = require('shift-spec').default;

const reducerDir = 'reducer/';
ensureDir(outDir + reducerDir);


function isNodeOrUnionOfNodes(type) {
  return type.typeName === 'Union' && type.arguments.every(isNodeOrUnionOfNodes) || spec.hasOwnProperty(type.typeName);
}

function isStatefulType(type) {
  switch (type.typeName) {
    case 'Const':
    case 'Enum':
    case 'String':
    case 'Number':
    case 'Boolean':
      return false;
    case 'Maybe':
    case 'List':
      return isStatefulType(type.argument);
    case 'Union':
      return type.arguments.some(isStatefulType);
    default:
      if (isNodeOrUnionOfNodes(type)) {
        return true;
      }
      throw new Error('unimplemented: type ' + type);
  }
}

function toJavaType(type) {
  switch (type.typeName) {
    case 'Maybe':
      return `Maybe<${toJavaType(type.argument)}>`;
    case 'List':
      return `ImmutableList<${toJavaType(type.argument)}>`;
    default:
      if (isNodeOrUnionOfNodes(type)) {
        return 'Supplier<State>';
      }
      throw new Error('Not reached');
  }
}

function force(type, name) {
  switch (type.typeName) {
    case 'Maybe':
      return `${name}.map(Supplier::get)`;
    case 'List':
      return `${name}.map(${type.argument.typeName === 'Maybe' ? `x -> ${force(type.argument, 'x')}` : 'Supplier::get' })`;
    default:
      if (isNodeOrUnionOfNodes(type)) {
        return `${name}.get()`;
      }
      throw new Error('Not reached');
  }
}


let content = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.Module;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class Thunked <State, T extends Reducer<State>> implements ThunkedReducer<State> {
    private Reducer<State> reducer;
    public Thunked(Reducer<State> reducer) {
        this.reducer = reducer;
    }
`;

for (let typeName of Object.keys(spec)) {
  let type = spec[typeName];
  let fields = type.fields.filter(field => isStatefulType(field.type));
  let params = fields.map(f => `,\n        @Nonnull ${toJavaType(f.type)} ${sanitize(f.name)}`).join('');
  let args = fields.map(f => `, ${force(f.type, sanitize(f.name))}`).join('');
  content += `
    @Override
    @Nonnull
    public State reduce${typeName}(
        @Nonnull ${typeName} node${params}
    ) {
        return reducer.reduce${typeName}(node${args});
    }
`;
}

content += `
}
`;

fs.writeFileSync(outDir + reducerDir + 'Thunked.java', content, 'utf8');
