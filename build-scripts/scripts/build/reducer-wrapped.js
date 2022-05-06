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


let content = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.reducer;

import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.Module;
import javax.annotation.Nonnull;

public class WrappedReducer<T> implements Reducer<T> {
  @Nonnull
  private final F2<Node, T, T> wrap;

  @Nonnull
  private final Reducer<T> reducer;

  public WrappedReducer(@Nonnull F2<Node, T, T> wrap, @Nonnull Reducer<T> reducer) {
    this.wrap = wrap;
    this.reducer = reducer;
  }
`;


for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type));
  let attrStrings = attrs.map(f => `@Nonnull ${toJavaType(f.type, 'T')} ${sanitize(f.name)}`);
  if (attrStrings.length === 0) {
    content += `
    @Nonnull
    @Override
    public T reduce${typeName}(@Nonnull ${typeName} node`;
  } else {
    content += `
    @Nonnull
    @Override
    public T reduce${typeName}(@Nonnull ${typeName} node, ${attrStrings.join(', ')}`;
  }
  content += `) {
        return wrap.apply(node, reducer.reduce${typeName}(node${attrs.map(f => ', ' + sanitize(f.name)).join('')}));
    }
`;
}

content += '}\n';

fs.writeFileSync(outDir + reducerDir + 'WrappedReducer.java', content, 'utf-8');
