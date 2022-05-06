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

const serializerDir = 'serialization/';
ensureDir(outDir + serializerDir);


let serializerContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.serialization;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.functional.data.NonEmptyImmutableList;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es${year}.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es${year}.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es${year}.ast.operators.UpdateOperator;
import com.shapesecurity.shift.es${year}.utils.Utils;
import com.shapesecurity.shift.es${year}.reducer.Director;
import com.shapesecurity.shift.es${year}.reducer.Reducer;

import javax.annotation.Nonnull;


public class Serializer implements Reducer<StringBuilder> {

    public static final Serializer INSTANCE = new Serializer();

    protected Serializer() {}

    public static String serialize(@Nonnull Program program) {
        return Director.reduceProgram(INSTANCE, program).toString();
    }

    @Nonnull
    private static JsonObjectBuilder b(@Nonnull String type) {
        return new JsonObjectBuilder().add("type", type);
    }

    @Nonnull
    private static StringBuilder list(@Nonnull ImmutableList<StringBuilder> values) {
        if (values.isEmpty()) {
            return new StringBuilder("[]");
        }
        StringBuilder sb = new StringBuilder("[");
        NonEmptyImmutableList<StringBuilder> nel = (NonEmptyImmutableList<StringBuilder>) values;
        sb.append(nel.head);
        nel.tail().forEach(s -> sb.append(",").append(s));
        sb.append("]");
        return sb;
    }

    @Nonnull
    private static StringBuilder o(@Nonnull Maybe<StringBuilder> el) {
        return el.orJust(new StringBuilder("null"));
    }

    private static class JsonObjectBuilder {
        final StringBuilder text = new StringBuilder("{");
        boolean first = true;

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, boolean value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(value);
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull String value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(Utils.escapeStringLiteral(value, '"', false));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull Number value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(value);
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull BinaryOperator value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(Utils.escapeStringLiteral(value.getName(), '"', false));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull CompoundAssignmentOperator value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(Utils.escapeStringLiteral(value.getName(), '"', false));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull UnaryOperator value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(Utils.escapeStringLiteral(value.getName(), '"', false));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull UpdateOperator value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(Utils.escapeStringLiteral(value.getName(), '"', false));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull VariableDeclarationKind value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(Utils.escapeStringLiteral(value.name, '"', false));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull StringBuilder value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(value);
            return this;
        }

        @Nonnull
        JsonObjectBuilder addMaybeString(@Nonnull String property, @Nonnull Maybe<String> value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(value.map(Utils::escapeStringLiteral).orJust("null"));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull Maybe<StringBuilder> value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(o(value));
            return this;
        }

        @Nonnull
        JsonObjectBuilder add(@Nonnull String property, @Nonnull ImmutableList<StringBuilder> value) {
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(list(value));
            return this;
        }

        @Nonnull
        JsonObjectBuilder addListMaybe(@Nonnull String property, @Nonnull ImmutableList<Maybe<StringBuilder>> value) { // because type erasure
            optionalComma();
            this.text.append(Utils.escapeStringLiteral(property, '"', false)).append(":").append(list(value.map(Serializer::o)));
            return this;
        }

        @Nonnull
        StringBuilder done() {
            this.text.append("}");
            return this.text;
        }

        private void optionalComma() {
            if (this.first) {
                this.first = false;
            } else {
                this.text.append(",");
            }
        }
    }
`;

function whichAdd(type) {
  if (type.kind === 'list' && type.argument.kind === 'nullable') {
    return 'addListMaybe';
  } else if (type.kind === 'nullable' && type.argument.kind === 'value' && type.argument.argument === 'string') {
    return 'addMaybeString';
  }
  return 'add';
}

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  serializerContent += `
    @Nonnull
    @Override
    public StringBuilder reduce${typeName}(@Nonnull ${typeName} node`;

  let attrStrings = type.attributes.filter(f => isStatefulType(f.type)).map(f => `, @Nonnull ${toJavaType(f.type, 'StringBuilder')} ${sanitize(f.name)}`);

  serializerContent += attrStrings.join('');

  serializerContent += `) {
        return b("${typeName}")${type.attributes.map(a => `.${whichAdd(a.type)}("${a.name}", ${isStatefulType(a.type) ? sanitize(a.name) : `node.${a.name}`})`).join('')}.done();
    }
`;
}

serializerContent += '}';

fs.writeFileSync(outDir + serializerDir + 'Serializer.java', serializerContent, 'utf-8');
