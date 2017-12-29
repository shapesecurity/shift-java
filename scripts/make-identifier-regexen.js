'use strict';

const ID_Start = require('unicode-9.0.0/Binary_Property/ID_Start/code-points.js');
const ID_Continue = require('unicode-9.0.0/Binary_Property/ID_Continue/code-points.js');
const Other_ID_Start = require('unicode-9.0.0/Binary_Property/Other_ID_Start/code-points.js');


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
  const sorted = [...array].sort((a, b) => a - b);
  const alternatives = [];
  for (let i = 0; i < array.length; ++i) {
    const cp = array[i];
    if (cp < 128) continue; // exclude ascii
    if (array[i + 1] === cp + 1 && array[i + 2] === cp + 2) {
      // i.e. we have a range
      i += 2;
      let offset = 2;
      while (i < array.length && array[i + 1] === cp + offset + 1) {
        ++i;
        ++offset;
      }
      alternatives.push('[' + escape(cp) + '-' + escape(array[i]) + ']');
    } else {
      alternatives.push(escape(cp));
    }
  }
  return alternatives.join('|');
}


console.log('IdentifierStart:\n' + toRegex(ID_Start));
console.log('IdentifierContinue:\n' + toRegex(Other_ID_Start.concat(ID_Continue, [0x200c, 0x200d])));
