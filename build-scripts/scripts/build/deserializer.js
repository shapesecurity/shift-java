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

const { ensureDir, nodes, makeHeader, toJavaType, year } = require('../lib/utilities.js');
const { outDir } = require('../lib/out-dir.js');

const serializerDir = 'serialization/';
ensureDir(outDir + serializerDir);


let deserializerContent = `${makeHeader(__filename)}

package com.shapesecurity.shift.es${year}.serialization;

import com.google.gson.*;

import com.shapesecurity.functional.data.ImmutableList;
import com.shapesecurity.functional.data.Maybe;
import com.shapesecurity.shift.es${year}.ast.*;
import com.shapesecurity.shift.es${year}.ast.Module;
import com.shapesecurity.shift.es${year}.ast.operators.BinaryOperator;
import com.shapesecurity.shift.es${year}.ast.operators.CompoundAssignmentOperator;
import com.shapesecurity.shift.es${year}.ast.operators.UnaryOperator;
import com.shapesecurity.shift.es${year}.ast.operators.UpdateOperator;

import org.json.JSONException;

import java.util.ArrayList;

public class Deserializer {

    protected Deserializer() {}

    public static Node deserialize(String toDeserialize) throws JSONException, IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        JsonElement json = new JsonParser().parse(toDeserialize);
        return new Deserializer().deserializeNode(json);
    }

    protected BinaryOperator deserializeBinaryOperator(JsonElement jsonElement) {
        String operatorString = jsonElement.getAsString();
        switch (operatorString) {
            case ",":
                return BinaryOperator.Sequence;
            case "||":
                return BinaryOperator.LogicalOr;
            case "&&":
                return BinaryOperator.LogicalAnd;
            case "|":
                return BinaryOperator.BitwiseOr;
            case "^":
                return BinaryOperator.BitwiseXor;
            case "&":
                return BinaryOperator.BitwiseAnd;
            case "+":
                return BinaryOperator.Plus;
            case "-":
                return BinaryOperator.Minus;
            case "==":
                return BinaryOperator.Equal;
            case "!=":
                return BinaryOperator.NotEqual;
            case "===":
                return BinaryOperator.StrictEqual;
            case "!==":
                return BinaryOperator.StrictNotEqual;
            case "*":
                return BinaryOperator.Mul;
            case "/":
                return BinaryOperator.Div;
            case "%":
                return BinaryOperator.Rem;
            case "**":
                return BinaryOperator.Exp;
            case "<":
                return BinaryOperator.LessThan;
            case "<=":
                return BinaryOperator.LessThanEqual;
            case ">":
                return BinaryOperator.GreaterThan;
            case ">=":
                return BinaryOperator.GreaterThanEqual;
            case "in":
                return BinaryOperator.In;
            case "instanceof":
                return BinaryOperator.Instanceof;
            case "<<":
                return BinaryOperator.Left;
            case ">>":
                return BinaryOperator.Right;
            case ">>>":
                return BinaryOperator.UnsignedRight;
            default:
                throw new RuntimeException("unrecognized binary operator");
        }
    }

    protected CompoundAssignmentOperator deserializeCompoundAssignmentOperator(JsonElement jsonElement) {
        String operatorString = jsonElement.getAsString();
        switch (operatorString) {
            case "+=":
                return CompoundAssignmentOperator.AssignPlus;
            case "-=":
                return CompoundAssignmentOperator.AssignMinus;
            case "*=":
                return CompoundAssignmentOperator.AssignMul;
            case "/=":
                return CompoundAssignmentOperator.AssignDiv;
            case "%=":
                return CompoundAssignmentOperator.AssignRem;
            case "**=":
                return CompoundAssignmentOperator.AssignExp;
            case "<<=":
                return CompoundAssignmentOperator.AssignLeftShift;
            case ">>=":
                return CompoundAssignmentOperator.AssignRightShift;
            case ">>>=":
                return CompoundAssignmentOperator.AssignUnsignedRightShift;
            case "|=":
                return CompoundAssignmentOperator.AssignBitOr;
            case "^=":
                return CompoundAssignmentOperator.AssignBitXor;
            case "&=":
                return CompoundAssignmentOperator.AssignBitAnd;
            default:
                throw new RuntimeException("unrecognized compound assignment operator");
        }
    }

    protected UnaryOperator deserializeUnaryOperator(JsonElement jsonElement) {
        String operatorString = jsonElement.getAsString();
        switch (operatorString) {
            case "+":
                return UnaryOperator.Plus;
            case "-":
                return UnaryOperator.Minus;
            case "!":
                return UnaryOperator.LogicalNot;
            case "~":
                return UnaryOperator.BitNot;
            case "typeof":
                return UnaryOperator.Typeof;
            case "void":
                return UnaryOperator.Void;
            case "delete":
                return UnaryOperator.Delete;
            default:
                throw new RuntimeException("unrecognized unary operator");
        }
    }

    protected UpdateOperator deserializeUpdateOperator(JsonElement jsonElement) {
        String operatorString = jsonElement.getAsString();
        switch (operatorString) {
            case "++":
                return UpdateOperator.Increment;
            case "--":
                return UpdateOperator.Decrement;
            default:
                throw new RuntimeException("unrecognized update operator");
        }
    }

    protected VariableDeclarationKind deserializeVariableDeclarationKind(JsonElement jsonElement) {
        String kindString = jsonElement.getAsString();
        switch (kindString) {
            case "var":
                return VariableDeclarationKind.Var;
            case "const":
                return VariableDeclarationKind.Const;
            case "let":
                return VariableDeclarationKind.Let;
            default:
                throw new RuntimeException("unrecognized variable declaration kind");
        }
    }
`;

let innerDeserializerContent = `
    protected Node deserializeNode(JsonElement jsonElement) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has("type")) {
                String nodeType = jsonObject.get("type").getAsString();
                switch (nodeType) {
`;

let deserializers = new Map;

function makeDeserializer(type) { // todo consider generics
  let name, base;
  switch (type.kind) {
    case 'list':
      switch (type.argument.kind) {
        case 'nullable':
          if (type.argument.argument.kind !== 'node') break;
          name = `deserializeListMaybe${type.argument.argument.argument}`;
          if (deserializers.has(name)) return name;
          base = type.argument.argument.argument;
          deserializers.set(name, `
    protected ${toJavaType(type)} ${name}(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<Maybe<${base}>> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                if (ele.isJsonNull()) {
                    deserializedElements.add(Maybe.empty());
                } else {
                    deserializedElements.add(Maybe.of((${base}) deserializeNode(ele)));
                }
            }
            return ImmutableList.from(deserializedElements);
        }
    }
`);
          return name;
        case 'node':
          name = `deserializeList${type.argument.argument}`;
          if (deserializers.has(name)) return name;
          base = type.argument.argument;
          deserializers.set(name, `
    protected ${toJavaType(type)} ${name}(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        if (jsonArray.size() == 0) {
          return ImmutableList.empty();
        } else {
            ArrayList<${base}> deserializedElements = new ArrayList<>();
            for (JsonElement ele : jsonArray) {
                ${base} deserializedElement = (${base}) deserializeNode(ele);
                deserializedElements.add(deserializedElement);
            }
            return ImmutableList.from(deserializedElements);
        }
    }
`);
          return name;
      }
      break;
    case 'nullable':
      switch (type.argument.kind) {
        case 'node':
          name = `deserializeMaybe${type.argument.argument}`;
          if (deserializers.has(name)) return name;
          base = type.argument.argument;
          deserializers.set(name, `
    protected ${toJavaType(type)} ${name}(JsonElement jsonElement) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        }
        return Maybe.of((${base}) deserializeNode(jsonElement));
    }
`);
          return name;
        case 'value':
          base = toJavaType(type.argument);
          name = `deserializeMaybe${base}`;
          if (deserializers.has(name)) return name;
          deserializers.set(name, `
    protected ${toJavaType(type)} ${name}(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return Maybe.empty();
        } else {
            return Maybe.of(jsonElement.getAsString());
        }
    }
`);
          return name;
      }
      break;
  }
  throw 'Unhandled type ' + JSON.stringify(type);
}

// todo consider checking if attributes failed to get created

function deserializer(attr) {
  switch (attr.type.kind) {
    case 'list':
    case 'nullable':
      return `${makeDeserializer(attr.type)}(jsonObject.get("${attr.name}"))`;
    case 'value':
      switch (attr.type.argument) {
        case 'string':
          return `jsonObject.get("${attr.name}").getAsString()`;
        case 'boolean':
          return `jsonObject.get("${attr.name}").getAsBoolean()`;
        case 'double':
          return `jsonObject.get("${attr.name}").getAsDouble()`;
        default:
          throw new Error('unreachable');
      }
    case 'node':
      return `(${attr.type.argument}) deserializeNode(jsonObject.get("${attr.name}"))`;
    case 'enum':
      return `deserialize${attr.type.argument}(jsonObject.get("${attr.name}"))`;
  }
  throw 'Unhandled type ' + JSON.stringify(attr.type);
}

for (let typeName of Array.from(nodes.keys()).sort()) {
  let type = nodes.get(typeName);
  if (type.children.length !== 0) continue;

  let attrStrings = type.attributes.map(deserializer);

  innerDeserializerContent += `                    case "${typeName}":
                        return new ${typeName}(${attrStrings.join(', ')});
`;
}

deserializerContent += `${Array.from(deserializers.values()).join('')}
${innerDeserializerContent}                }
            }
        }
        throw new RuntimeException("node has no type or unrecognized type");
    }
}`;

fs.writeFileSync(outDir + serializerDir + 'Deserializer.java', deserializerContent, 'utf-8');
