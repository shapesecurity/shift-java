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

const rangeCheckerDir = 'parser/';
ensureDir(outDir + rangeCheckerDir);


let rangeCheckerContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.parser;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.Monoid;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.reducer.MonoidalReducer;
import javax.annotation.Nonnull;

import static org.junit.Assert.assertTrue;

public class RangeCheckerReducer extends MonoidalReducer<RangeCheckerReducer.RangeChecker> {
    private final ParserWithLocation parserWithLocation;

    protected RangeCheckerReducer(ParserWithLocation parserWithLocation) {
        super(RangeChecker.MONOID);
        this.parserWithLocation = parserWithLocation;
    }

    private RangeChecker accept(Node node, RangeChecker innerBounds) {
        Maybe<SourceSpan> span = this.parserWithLocation.getLocation(node);
        assertTrue(span.isJust());
        RangeChecker outerBounds = new RangeChecker(span.just());
        assertTrue(outerBounds.start <= outerBounds.end);

        assertTrue(outerBounds.start <= innerBounds.start);
        assertTrue(innerBounds.end <= outerBounds.end);

        return outerBounds;
    }

    static class RangeChecker {
        public final static Monoid<RangeChecker> MONOID = new Monoid<RangeChecker>() {
            @Nonnull
            @Override
            public RangeChecker identity() {
                return new RangeChecker(Integer.MAX_VALUE, Integer.MIN_VALUE);
            }

            @Nonnull
            @Override
            public RangeChecker append(RangeChecker a, RangeChecker b) {
                assertTrue(a.end <= b.start);
                return new RangeChecker(a.start, b.end);
            }
        };
        public final int start, end;

        private RangeChecker(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public RangeChecker(SourceSpan sourceSpan) {
            this(sourceSpan.start.offset, sourceSpan.end.offset);
        }
    }
`;

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type));
  let attrStrings = attrs.map(f => `, @Nonnull ${toJavaType(f.type, 'RangeChecker')} ${sanitize(f.name)}`);
  rangeCheckerContent += `
    @Nonnull
    @Override
    public RangeChecker reduce${typeName}(@Nonnull ${typeName} node${attrStrings.join('')}) {`;

  rangeCheckerContent += `
      return accept(node, super.reduce${typeName}(node${attrs.map(f => `, ${sanitize(f.name)}`).join('')}));`;
  rangeCheckerContent += `
    }
`;
}

rangeCheckerContent += '}\n';

fs.writeFileSync(outDir + rangeCheckerDir + 'RangeCheckerReducer.java', rangeCheckerContent, 'utf-8');
