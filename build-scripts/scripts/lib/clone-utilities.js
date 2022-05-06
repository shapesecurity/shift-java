'use strict';

const { nodes, isStatefulType, sanitize } = require('./utilities.js');
const cloneReturnTypes = require('./find-max-super.js').default(nodes);

function makeCloneAttribute(attr) {
  let name = sanitize(attr.name);
  if (!isStatefulType(attr.type)) {
    return `node.${name}`;
  }
  switch (attr.type.kind) {
    case 'nullable':
      if (attr.type.argument.kind === 'node') {
        return `${name}.map(x -> (${attr.type.argument.argument}) x)`;
      }
      throw 'Not reached';
    case 'list':
      if (attr.type.argument.kind === 'nullable') {
        return `${name}.map(x -> x.map(y -> (${attr.type.argument.argument.argument}) y))`;
      } else if (attr.type.argument.kind === 'node') {
        return `${name}.map(x -> (${attr.type.argument.argument}) x)`;
      }
      throw 'Not reached';
    case 'node':
      return `(${attr.type.argument}) ${name}`;
    default:
      throw 'Not reached';
  }
}

function makeEquals(attr) {
  let name = sanitize(attr.name);
  if (!isStatefulType(attr.type)) {
    throw 'Not reached';
  }
  switch (attr.type.kind) {
    case 'nullable':
      if (attr.type.argument.kind === 'node') {
        return `maybeRefEquals(node.${name}, ${name})`;
      }
      throw 'Not reached';
    case 'list':
      if (attr.type.argument.kind === 'nullable') {
        return `listMaybeRefEquals(node.${name}, ${name})`;
      } else if (attr.type.argument.kind === 'node') {
        return `listRefEquals(node.${name}, ${name})`;
      }
      throw 'Not reached';
    case 'node':
      return `node.${name} == ${name}`;
    default:
      throw 'Not reached';
  }
}

module.exports = {
  makeCloneAttribute,
  makeEquals,
  cloneReturnTypes,
};
