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


let reducerContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;

import javax.annotation.Nonnull;

public interface Reducer<State> {`;

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type)).map(f => `            @Nonnull ${toJavaType(f.type, 'State')} ${sanitize(f.name)}`);
  if (attrs.length === 0) {
    reducerContent += `
    @Nonnull
    State reduce${typeName}(@Nonnull ${typeName} node`;
  } else {
    reducerContent += `
    @Nonnull
    State reduce${typeName}(
            @Nonnull ${typeName} node,
${attrs.join(',\n')}`;
  }
  reducerContent += ');\n';
}

reducerContent += '}\n';

fs.writeFileSync(outDir + reducerDir + 'Reducer.java', reducerContent, 'utf-8');
