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
const { makeCloneAttribute, makeEquals, cloneReturnTypes } = require('../lib/clone-utilities.js');
const { outDir } = require('../lib/out-dir.js');

const reducerDir = 'reducer/';
ensureDir(outDir + reducerDir);


let cloneContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.reducer;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;
import javax.annotation.Nonnull;

public class LazyReconstructingReducer implements Reducer<Node> {
    public static <A extends Node, B extends Node> boolean listRefEquals(ImmutableList<A> a, ImmutableList<B> b) {
        return a.length == b.length && !a.zipWith((l, r) -> l == r, b).exists(v -> !v); // I would prefer .every instead of !.exists(!), but we don't seem to have that
    }

    public static <A extends Node, B extends Node> boolean maybeRefEquals(Maybe<A> a, Maybe<B> b) {
        return a.isJust() == b.isJust() && a.maybe(true, l -> l == b.fromJust());
    }

    public static <A extends Node, B extends Node> boolean listMaybeRefEquals(ImmutableList<Maybe<A>> a, ImmutableList<Maybe<B>> b) {
        return a.length == b.length && !a.zipWith(LazyReconstructingReducer::maybeRefEquals, b).exists(v -> !v);
    }

`;


for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type));
  if (attrs.length === 0) {
    cloneContent += `
    @Nonnull
    @Override
    public ${cloneReturnTypes.get(typeName)} reduce${typeName}(@Nonnull ${typeName} node) {
        return node;
    }
`;
  } else {
    let attrStrings = attrs.map(f => `            @Nonnull ${toJavaType(f.type, 'Node')} ${sanitize(f.name)}`);
    let equals = attrs.map(makeEquals).join(' && ');
    cloneContent += `
    @Nonnull
    @Override
    public ${cloneReturnTypes.get(typeName)} reduce${typeName}(
            @Nonnull ${typeName} node,
${attrStrings.join(',\n')}) {
        if (${equals}) {
            return node;
        }
        return new ${typeName}(${type.attributes.map(makeCloneAttribute).join(', ')});
    }
`;
  }
}

cloneContent += '}\n';

fs.writeFileSync(outDir + reducerDir + 'LazyReconstructingReducer.java', cloneContent, 'utf-8');
