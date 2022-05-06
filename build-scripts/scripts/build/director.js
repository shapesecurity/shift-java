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

const { ensureDir, nodes, makeHeader, isStatefulType, sanitize, toJavaType, year } = require('../lib/utilities.js');

const outDir = 'out/';
const reducerDir = 'reducer/';
ensureDir(outDir + reducerDir);


function methodNameFor(type) {
  switch (type.kind) {
    case 'nullable':
      if (type.argument.kind === 'list') {
        return `reduceMaybeList${type.argument.argument.argument}`;
      }
      return `reduceMaybe${type.argument.argument}`;
    case 'list':
      if (type.argument.kind === 'nullable') {
        return `reduceListMaybe${type.argument.argument.argument}`;
      }
      return `reduceList${type.argument.argument}`;
    case 'node':
      return `reduce${type.argument}`;
    default:
      throw new Error('Not reached');
  }
}

function toArgument(type, name, thunked) {
  return `${thunked && type.kind === 'node' ? '() -> ' : ''}${methodNameFor(type)}(reducer, ${name})`;
}

function nodeReducer(type, methods, thunked) {
  let node = nodes.get(type);
  let attrs = node.attributes.filter(a => isStatefulType(a.type));
  attrs.forEach(a => {
    direct(a.type, methods, thunked);
  });
  let args = 'node' + attrs.map(a => ', ' + toArgument(a.type, 'node.' + sanitize(a.name), thunked)).join('');
  return `reducer.reduce${type}(${args})`;
}

function directNode(name, methods, thunked) {
  direct({ kind: 'node', argument: name }, methods, thunked);
}

function direct(type, methods, thunked) {
  let methodName = methodNameFor(type);
  if (methods.has(methodName)) return;
  methods.set(methodName, null);

  let method;
  switch (type.kind) {
    case 'nullable': {
      direct(type.argument, methods, thunked);
      let innerType = type.argument.kind === 'list'
        ? thunked ? 'ImmutableList<Supplier<State>>' : 'ImmutableList<State>'
        : thunked ? 'Supplier<State>' : 'State';
      method = `
    @Nonnull
    public static <State> Maybe<${innerType}> ${methodName}(
      @Nonnull ${thunked ? 'Thunked' : ''}Reducer<State> reducer,
      @Nonnull ${toJavaType(type)} maybe) {
        return maybe.map(x -> ${toArgument(type.argument, 'x', thunked)});
      }
`;
      break;
    }
    case 'list': {
      direct(type.argument, methods, thunked);
      let innerType = type.argument.kind === 'nullable'
        ? thunked ? 'Maybe<Supplier<State>>' : 'Maybe<State>'
        : thunked ? 'Supplier<State>' : 'State';
      method = `
    @Nonnull
    public static <State> ImmutableList<${innerType}> ${methodName}(
      @Nonnull ${thunked ? 'Thunked' : ''}Reducer<State> reducer,
      @Nonnull ${toJavaType(type)} list) {
        return list.map(x -> ${toArgument(type.argument, 'x', thunked)});
      }
`;
      break;
    }
    case 'node': {
      let node = nodes.get(type.argument);
      method = `
    @Nonnull
    public static <State> State ${methodName}(
      @Nonnull ${thunked ? 'Thunked' : ''}Reducer<State> reducer,
      @Nonnull ${type.argument} node) {
`;
      if (node.children.length > 0) {
        node.children.forEach(child => directNode(child, methods, thunked));
        method += '        ' + node.children.map(child => `if (node instanceof ${child}) {
            return reduce${child}(reducer, (${child}) node);
        }`).join(' else ') + ` else {
            throw new RuntimeException("Not reached");
        }
`;

      } else {
        method += `        return ${nodeReducer(type.argument, methods, thunked)};
`;
      }
      method += '    }';
      break;
    }
    default:
      throw 'Not reached';
  }

  methods.set(methodName, method);
}


let baseMethods = new Map;
directNode('Program', baseMethods, false);
let thunkedMethods = new Map;
directNode('Program', thunkedMethods, true);


let content = thunked => `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;

import javax.annotation.Nonnull;
${thunked ? 'import java.util.function.Supplier;' : ''}

public final class ${thunked ? 'Thunked' : ''}Director {
${Array.from((thunked ? thunkedMethods : baseMethods).keys()).sort().map(methodName => (thunked ? thunkedMethods : baseMethods).get(methodName)).join('\n')}
}
`;

fs.writeFileSync(outDir + reducerDir + 'Director.java', content(false), 'utf8');
fs.writeFileSync(outDir + reducerDir + 'ThunkedDirector.java', content(true), 'utf8');
