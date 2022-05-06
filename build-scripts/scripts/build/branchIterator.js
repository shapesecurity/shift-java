/**
 * Copyright 2017 Shape Security, Inc.
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
const { outDir } = require('../lib/out-dir.js');

const pathDir = 'path/';
ensureDir(outDir + pathDir);


function cap(name) {
  return name[0].toUpperCase() + name.slice(1);
}

function uncap(name) {
  return name[0].toLowerCase() + name.slice(1);
}

let content = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.path;

import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.SpreadElementExpression;
import javax.annotation.Nonnull;

import java.util.Iterator;


/*
 * Gives an iterator over all the paths to nodes in the tree, along with the node at that path.
 */
public class BranchIterator implements Iterable<Pair<BranchGetter, Node>> {
	final Node root;
	public BranchIterator(Node root) {
		this.root = root;
	}

	@Nonnull
	@Override
	public Iterator<Pair<BranchGetter, Node>> iterator() {
		return new Iter(this.root);
	}

	public static class Iter implements Iterator<Pair<BranchGetter, Node>> {
		private ImmutableList<Pair<BranchGetter, Node>> queue; // the branchgetter is the path to the node

		public Iter(Node root) {
			this.queue = ImmutableList.of(Pair.of(new BranchGetter(), root));
		}

		@Override
		public boolean hasNext() {
			return queue.isNotEmpty();
		}

		@Override
		public Pair<BranchGetter, Node> next() {
			if (!this.hasNext()) {
				return null;
			}
			Pair<BranchGetter, Node> head = queue.maybeHead().fromJust();
			ImmutableList<Pair<BranchGetter, Node>> tail = queue.maybeTail().fromJust();
			this.queue = addChildren(head, tail);
			return head;
		}

		private static ImmutableList<Pair<BranchGetter, Node>> addChildren(Pair<BranchGetter, Node> toExpand, ImmutableList<Pair<BranchGetter, Node>> list) {
			// The queue is FILO, so to maintain prefix order, we add later children first.
			BranchGetter path = toExpand.left;
			Node node = toExpand.right;

			`;


let ifClauses = [];

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrs = type.attributes.filter(f => isStatefulType(f.type));

  if (attrs.length === 0) {
    ifClauses.push(`if (node instanceof ${typeName}) {
				// No children; nothing to do.
			}`);
    continue;
  }

  let nodeName = uncap(typeName) + '_';

  let ifClause = `if (node instanceof ${typeName}) {
				${typeName} ${nodeName} = (${typeName}) node;`;
  let createdIndex = false;
  attrs.reverse().forEach(a => { // reverse so prefix order is maintained
    let isList = a.type.kind === 'list';
    let isMaybe = a.type.kind === 'nullable';
    let isListMaybe = a.type.kind === 'list' && a.type.argument.kind === 'nullable';

    let branchName = `${typeName}${cap(a.name)}_`;

    if (isListMaybe) {
      ifClause += `
				${createdIndex ? '' : 'int '}index = ${nodeName}.${sanitize(a.name)}.length - 1;`;
      createdIndex = true;
      ifClause += `
				for (Maybe<${a.type.argument.argument.argument}> child : ${nodeName}.${sanitize(a.name)}.reverse()) {
					if (child.isJust()) {
						list = list.cons(Pair.of(path.d(Branch.${branchName}(index)), child.fromJust()));
					}
					--index;
				}`;
    } else if (isList) {
      ifClause += `
				${createdIndex ? '' : 'int '}index = ${nodeName}.${sanitize(a.name)}.length - 1;`;
      createdIndex = true;
      ifClause += `
				for (${a.type.argument.argument} child : ${nodeName}.${sanitize(a.name)}.reverse()) {
					list = list.cons(Pair.of(path.d(Branch.${branchName}(index)), child));
					--index;
				}`;
    } else if (isMaybe) {
      ifClause += `
				if (${nodeName}.${sanitize(a.name)}.isJust()) {
					list = list.cons(Pair.of(path.d(Branch.${branchName}()), ${nodeName}.${sanitize(a.name)}.fromJust()));
				}`;
    } else {
      ifClause += `
				list = list.cons(Pair.of(path.d(Branch.${branchName}()), ${nodeName}.${sanitize(a.name)}));`;
    }

  });

  ifClause += `
			}`;

  ifClauses.push(ifClause);
}

content += `${ifClauses.join(' else ')} else {
				throw new RuntimeException("Unreachable: unrecognized node type " + node.getClass().getSimpleName());
			}

      return list;
    }
  }
}
`;

fs.writeFileSync(outDir + pathDir + 'BranchIterator.java', content, 'utf-8');
