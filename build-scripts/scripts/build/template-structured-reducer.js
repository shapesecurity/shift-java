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
const { makeEquals } = require('../lib/clone-utilities.js');

const outDir = 'out/';
const templateDir = 'template/';
ensureDir(outDir + templateDir);


function force(type, name) {
  switch (type.kind) {
    case 'nullable':
      return `applyMaybeLabels(node.${name}, ${name}Thunk)`;
    case 'list':
      if (type.argument.kind === 'nullable') {
        return `applyListMaybeLabels(node.${name}, ${name}Thunk)`;
      }
      return `applyListLabels(node.${name}, ${name}Thunk)`;
    case 'node':
      return `(${type.argument}) ${name}Thunk.get()`;
    default:
      throw new Error('Not reached');
  }
}


let content = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.template;

import com.shapesecurity.functional.F;
import com.shapesecurity.functional.Pair;
import com.shapesecurity.functional.data.ConcatList;
import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.reducer.ThunkedReducer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.shapesecurity.shift.es${year}.reducer.LazyReconstructingReducer.listMaybeRefEquals;
import static com.shapesecurity.shift.es${year}.reducer.LazyReconstructingReducer.listRefEquals;
import static com.shapesecurity.shift.es${year}.reducer.LazyReconstructingReducer.maybeRefEquals;


public class ReduceStructured implements ThunkedReducer<Node> {
    public static abstract class Label {}
    public static class Bare extends Label {
        final String name;

        public Bare(String name) {
            this.name = name;
        }
    }
    public static class If extends Label {
        final boolean inverted;
        final String condition;

        public If(boolean inverted, String condition) {
            this.inverted = inverted;
            this.condition = condition;
        }
    }
    public static class Loop extends Label {
        final String variable;
        final String values;

        public Loop(String variable, String values) {
            this.variable = variable;
            this.values = values;
        }
    }

    public static class TemplateValues { // TODO relocate to Template
        public final Map<String, Boolean> conditions;
        public final Map<String, List<TemplateValues>> lists;
        public final Map<String, F<Node, Node>> replacers;

        public TemplateValues(Map<String, Boolean> conditions, Map<String, List<TemplateValues>> lists, Map<String, F<Node, Node>> replacers) {
            this.conditions = conditions;
            this.lists = lists;
            this.replacers = replacers;
        }

        public TemplateValues merge(TemplateValues other, String namespace) {
             // non-destructive
            TemplateValues merged = new TemplateValues(new HashMap<>(this.conditions), new HashMap<>(this.lists), new HashMap<>(this.replacers));
            for (Map.Entry<String, Boolean> entry : other.conditions.entrySet()) {
                String namespaced = namespace + "::" + entry.getKey();
                if (merged.conditions.containsKey(namespaced)) {
                    throw new RuntimeException("Name " + namespaced + " already exists");
                }
                merged.conditions.put(namespaced, entry.getValue());
            }
            for (Map.Entry<String, List<TemplateValues>> entry : other.lists.entrySet()) {
                String namespaced = namespace + "::" + entry.getKey();
                if (merged.lists.containsKey(namespaced)) {
                    throw new RuntimeException("Name " + namespaced + " already exists");
                }
                merged.lists.put(namespaced, entry.getValue());
            }
            for (Map.Entry<String, F<Node, Node>> entry : other.replacers.entrySet()) {
                String namespaced = namespace + "::" + entry.getKey();
                if (merged.replacers.containsKey(namespaced)) {
                    throw new RuntimeException("Name " + namespaced + " already exists");
                }
                merged.replacers.put(namespaced, entry.getValue());
            }
            return merged;
        }
    }

    IdentityHashMap<Node, List<Label>> nodeToLabels;
    TemplateValues values;
    boolean currentNodeMayHaveStructuredLabel;

    public ReduceStructured(IdentityHashMap<Node, List<Label>> nodeToLabels, TemplateValues values) {
        this.nodeToLabels = nodeToLabels;
        this.values = values;
        this.currentNodeMayHaveStructuredLabel = false;
    }

    void enforceNoStrayStructuralLabels(Node node) {
        List<Label> labels = this.nodeToLabels.get(node);
        if (!this.currentNodeMayHaveStructuredLabel && labels != null && labels.stream().anyMatch(l -> !(l instanceof Bare))) {
            Label label = labels.stream().filter(l -> !(l instanceof Bare)).findFirst().get();
            if (label instanceof If) {
                throw new RuntimeException("Node of type " + node.getClass().getSimpleName() + " with condition " + ((If) label).condition + " is not in an omittable position");
            } else if (label instanceof Loop) {
                throw new RuntimeException("Node of type " + node.getClass().getSimpleName() + " iterating over " + ((Loop) label).values + " is not in a loopable position");
            } else {
                throw new RuntimeException("unreachable");
            }
        }
        this.currentNodeMayHaveStructuredLabel = false;
    }

    Node applyReplacer(List<Label> labels, Node transformed) {
        if (labels == null || labels.isEmpty()) {
            return transformed;
        }
        List<Bare> bareLabels = labels.stream().flatMap(l -> l instanceof Bare ? Stream.of((Bare) l) : Stream.empty()).collect(Collectors.toList());
        if (bareLabels.size() > 1) {
            throw new RuntimeException("Node has multiple labels: " + bareLabels.get(0).name + ", " + bareLabels.get(1).name);
        }
        if (bareLabels.isEmpty()) {
            return transformed;
        }
        F<Node, Node> replacer = this.values.replacers.get(bareLabels.get(0).name);
        if (replacer == null) {
            throw new RuntimeException("Replacer " + bareLabels.get(0).name + " not found");
        }
        return replacer.apply(transformed);
    }

    <T extends Node> ImmutableList<T> applyListLabels(ImmutableList<T> originalChildren, ImmutableList<Supplier<Node>> childThunks) {
        return originalChildren.zipWith(Pair::of, childThunks).flatMap(p -> {
            List<Label> childLabels = this.nodeToLabels.get(p.left);
            if (childLabels == null || childLabels.isEmpty()) {
                return ImmutableList.of(p.right.get());
            }
            return this.applyLabels(p.right, ImmutableList.from(childLabels).filter(l -> !(l instanceof Bare))).toList();
        }).map(n -> (T) n);
    }

    <T extends Node> Maybe<T> applyMaybeLabels(Maybe<T> originalChild, Maybe<Supplier<Node>> childThunk) {
        if (originalChild.isNothing()) {
            return Maybe.empty();
        }
        List<Label> childLabels = this.nodeToLabels.get(originalChild.fromJust());
        if (childLabels == null || childLabels.isEmpty()) {
            return Maybe.of((T) childThunk.fromJust().get());
        }
        ImmutableList<Node> result = this.applyLabels(childThunk.fromJust(), ImmutableList.from(childLabels).filter(l -> !(l instanceof Bare))).toList();
        if (result.isEmpty()) {
            return Maybe.empty();
        }
        NonEmptyImmutableList<Node> nonEmptyResult = (NonEmptyImmutableList<Node>) result;
        if (nonEmptyResult.tail.isNotEmpty()) {
            throw new RuntimeException("unreachable");
        }
        return Maybe.of((T) nonEmptyResult.head);
    }

    <T extends Node> ImmutableList<Maybe<T>> applyListMaybeLabels(ImmutableList<Maybe<T>> originalChildren, ImmutableList<Maybe<Supplier<Node>>> childThunks) {
        return originalChildren.zipWith(Pair::of, childThunks).flatMap(p -> {
            if (p.left.isNothing()) {
                return ImmutableList.of(Maybe.empty());
            }
            List<Label> childLabels = this.nodeToLabels.get(p.left.fromJust());
            if (childLabels == null || childLabels.isEmpty()) {
                return ImmutableList.of(Maybe.of(p.right.fromJust().get()));
            }
            return this.applyLabels(p.right.fromJust(), ImmutableList.from(childLabels).filter(l -> !(l instanceof Bare))).toList().map(Maybe::of);
        }).map(m -> m.map(n -> (T) n));
    }

    ConcatList<Node> applyLabels(Supplier<Node> childThunk, ImmutableList<Label> remainingLabels) {
        if (remainingLabels.isEmpty()) {
            this.currentNodeMayHaveStructuredLabel = true;
            return ConcatList.of(childThunk.get());
        }
        NonEmptyImmutableList<Label> nonEmptyLabels = (NonEmptyImmutableList<Label>) remainingLabels;
        Label head = nonEmptyLabels.head;
        ImmutableList<Label> tail = nonEmptyLabels.tail;
        if (head instanceof If) {
            If label = (If) head;
            Boolean condition = this.values.conditions.get(label.condition);
            if (condition == null) {
                throw new RuntimeException("Condition " + label.condition + " not found");
            }
            if (label.inverted == condition) {
                return ConcatList.empty();
            }
            return applyLabels(childThunk, tail);
        }
        if (head instanceof Loop) {
            Loop label = (Loop) head;
            List<TemplateValues> list = this.values.lists.get(label.values);
            if (list == null) {
                throw new RuntimeException("Loop values " + label.values + " not found");
            }
            TemplateValues oldValues = this.values;
            ConcatList<Node> out = ConcatList.empty();
            for (TemplateValues perIterationTemplateValues : list) {
                this.values = oldValues.merge(perIterationTemplateValues, label.variable);
                out = out.append(applyLabels(childThunk, tail));
            }
            this.values = oldValues;
            return out;
        }
        throw new RuntimeException("unreachable");
    }
`;

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let fields = type.attributes.filter(field => isStatefulType(field.type));

  if (fields.length === 0) {
    content += `
    @Override
    @Nonnull
    public Node reduce${typeName}(
        @Nonnull ${typeName} node
    ) {
        enforceNoStrayStructuralLabels(node);
        return applyReplacer(this.nodeToLabels.get(node), node);
    }
`;
  } else {

    let params = fields.map(f => `,\n        @Nonnull ${toJavaType(f.type, 'Supplier<Node>')} ${sanitize(f.name)}Thunk`).join('');

    let newFields = fields.map(f => `        ${toJavaType(f.type)} ${sanitize(f.name)} = ${force(f.type, sanitize(f.name))};`).join('\n');
    let equals = fields.map(makeEquals).join(' && ');
    let args = type.attributes.filter(f => f.name !== 'type').map(f => `${isStatefulType(f.type) ? sanitize(f.name) : `node.${f.name}`}`).join(', ');

    content += `
    @Override
    @Nonnull
    public Node reduce${typeName}(
        @Nonnull ${typeName} node${params}
    ) {
        enforceNoStrayStructuralLabels(node);
${newFields}
        Node newNode = ${equals}
          ? node
          : new ${typeName}(${args});
        return applyReplacer(this.nodeToLabels.get(node), newNode);
    }
`;
  }
}

content += `
}
`;

fs.writeFileSync(outDir + templateDir + 'ReduceStructured.java', content, 'utf8');
