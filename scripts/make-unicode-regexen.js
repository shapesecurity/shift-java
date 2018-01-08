'use strict';

const ID_Start = require('unicode-9.0.0/Binary_Property/ID_Start/code-points.js');
const ID_Continue = require('unicode-9.0.0/Binary_Property/ID_Continue/code-points.js');
const Space_Separator = require('unicode-9.0.0/General_Category/Space_Separator/code-points.js');


function asFourHexDigits(cp) {
  return cp.toString(16).padStart(4, '0');
}

function escape(cp) {
  if (cp < 0xFFFF) {
    return '\\u' + asFourHexDigits(cp);
  }
  const cu1 = Math.floor((cp - 0x10000) / 0x400) + 0xD800;
  const cu2 = (cp - 0x10000) % 0x400 + 0xDC00;
  return '\\u' + asFourHexDigits(cu1) + '\\u' + asFourHexDigits(cu2);
}

function toRegex(array) {
  const sorted = [...new Set(array)].sort((a, b) => a - b);
  const classes = [];
  for (let i = 0; i < sorted.length; ++i) {
    const cp = sorted[i];
    if (cp < 128) continue; // exclude ascii
    if (i + 2 < sorted.length && sorted[i + 1] === cp + 1 && sorted[i + 2] === cp + 2) {
      // i.e. we have a range
      i += 2;
      let offset = 2;
      while (i + 1 < sorted.length && sorted[i + 1] === cp + offset + 1) {
        ++i;
        ++offset;
      }
      classes.push(escape(cp) + '-' + escape(sorted[i]));
    } else {
      classes.push(escape(cp));
    }
  }
  return '[' + classes.join('') + ']';
}


console.log('IdentifierStart:\n' + toRegex(ID_Start));
console.log('IdentifierContinue:\n' + toRegex([].concat(ID_Continue, [0x200c, 0x200d])));
console.log('Whitespace:\n' + toRegex([].concat(Space_Separator, [0x00a0, 0xfeff])));
