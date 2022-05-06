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

module.exports = function ({ nodes, enums, namedTypes }) {
  function inherits(child, parent) {
    let parents = nodes.get(child).parents;
    if (parents.indexOf(parent) === -1) {
      parents.push(parent);
    }
    nodes.get(parent).children.push(child);
  }


  let newNodes = [];

  namedTypes.forEach((type, name) => {
    if (type.kind === 'union') {
      if (nodes.has(name)) {
        throw `Node named ${name} already exists!`;
      }
      nodes.set(name, { children: [], parents: [], attributes: [] });
      newNodes.push(name);
      type.argument.forEach(t => {
        inherits(t.argument, name);
      });
      namedTypes.set(name, { kind: 'node', argument: name });
    }
  });

  let seen = new Map;
  function addUnions(type) {
    if (seen.has(type)) return seen.get(type);
    let ret;
    switch (type.kind) {
      case 'nullable':
      case 'list':
        type.argument = addUnions(type.argument);
        ret = type;
        break;
      case 'namedType': {
        let child = namedTypes.get(type.argument);
        if (child.kind === 'union') {
          throw 'Not reached';
        }
        ret = addUnions(child);
        break;
      }
      case 'union': {
        let name = type.argument.map(t => t.argument).join('');
        newNodes.push(name);
        nodes.set(name, { children: [], parents: [], attributes: [] });
        type.argument.forEach(t => {
          if (t.kind === 'node' || t.kind === 'namedType') {
            inherits(t.argument, name);
          } else {
            throw `Union of unhandled type ${JSON.stringify(t)}`;
          }
        });
        ret = { kind: 'node', argument: name };
        break;
      }
      default:
        ret = type;
    }
    seen.set(type, ret);
    return ret;
  }

  nodes.forEach(n => {
    n.attributes.forEach((a, i) => {
      n.attributes[i].type = addUnions(a.type);
    });
  });

  // Ensure that our artificial union types inherit, where possible.
  // E.g., the new type T representing Union(A, B), where A < C and B < C,
  // should have T < C.

  function allAnscestors(name) {
    let anscestors = new Set;
    let todo = nodes.get(name).parents.slice(0);
    while (todo.length > 0) {
      let cur = todo.shift();
      if (anscestors.has(cur)) continue;
      anscestors.add(cur);
      todo.push(...nodes.get(cur).parents);
    }
    return anscestors;
  }

  function isDescendent(child, parent) {
    let childNode = nodes.get(child);
    if (childNode.parents.indexOf(parent) !== -1) return true;
    return childNode.parents.some(c => isDescendent(c, parent));
  }

  function minimizeBounds(bounds) {
    bounds = Array.from(bounds);
    let out = bounds.filter(b => bounds.every(p => !isDescendent(p, b)));
    return out;
  }

  function unsortedArrayEquals(a, b) {
    if (a.length !== b.length) return false;
    let x = a.slice(0).sort();
    let y = b.slice(0).sort();
    return x.every((v, i) => y[i] === v);
  }

  function intersection(sets) {
    if (sets.length === 0) return new Set;
    return new Set(Array.from(sets[0]).filter(v => sets.every(s => s.has(v))));
  }

  function subset(a, b) {
    return a.every(x => b.indexOf(x) !== -1);
  }

  let touched = true;
  while (touched) {
    touched = false;
    newNodes.forEach(name => {
      let node = nodes.get(name);
      let parents = new Set(node.parents);
      let childParents = Array.from(intersection(node.children.map(allAnscestors))).filter(a => a !== name && !subset(nodes.get(a).children, node.children));
      parents.add(...childParents);
      let minimizedParents = minimizeBounds(parents);
      if (!unsortedArrayEquals(minimizedParents, node.parents)) {
        // todo strictly speaking also need to check that all attributes required by parents are present on this node, but whatever
        touched = true;
        node.parents = minimizedParents;
      }
    });
  }


  return { nodes, enums };
};
