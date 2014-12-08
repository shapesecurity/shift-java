/**
 * Serenade.js JavaScript Framework v0.5.0
 * Revision: bcda813708
 * http://github.com/elabs/serenade.js
 *
 * Copyright 2011, Jonas Nicklas, Elabs AB
 * Released under the MIT License
 */
(function(root) {
  /* Jison generated parser */
var parser = (function(){
var parser = {trace: function trace() { },
yy: {},
symbols_: {"error":2,"Root":3,"ChildList":4,"ElementIdentifier":5,"AnyIdentifier":6,"#":7,".":8,"Element":9,"[":10,"]":11,"PropertyList":12,"WHITESPACE":13,"Text":14,"INDENT":15,"OUTDENT":16,"TextList":17,"Bound":18,"STRING_LITERAL":19,"Child":20,"TERMINATOR":21,"IfInstruction":22,"Instruction":23,"Helper":24,"Property":25,"=":26,"!":27,":":28,"-":29,"VIEW":30,"COLLECTION":31,"UNLESS":32,"IN":33,"IDENTIFIER":34,"IF":35,"ElseInstruction":36,"ELSE":37,"@":38,"$accept":0,"$end":1},
terminals_: {2:"error",7:"#",8:".",10:"[",11:"]",13:"WHITESPACE",15:"INDENT",16:"OUTDENT",19:"STRING_LITERAL",21:"TERMINATOR",26:"=",27:"!",28:":",29:"-",30:"VIEW",31:"COLLECTION",32:"UNLESS",33:"IN",34:"IDENTIFIER",35:"IF",37:"ELSE",38:"@"},
productions_: [0,[3,0],[3,1],[5,1],[5,3],[5,2],[5,2],[5,3],[9,1],[9,3],[9,4],[9,3],[9,4],[17,1],[17,3],[14,1],[14,1],[4,1],[4,3],[20,1],[20,1],[20,1],[20,1],[20,1],[12,1],[12,3],[25,3],[25,3],[25,4],[25,4],[25,3],[25,3],[23,5],[23,5],[23,5],[23,5],[23,5],[23,4],[24,3],[24,3],[24,4],[22,5],[22,4],[22,2],[36,6],[6,1],[6,1],[6,1],[6,1],[6,1],[6,1],[18,2],[18,1]],
performAction: function anonymous(yytext,yyleng,yylineno,yy,yystate,$$,_$) {

var $0 = $$.length - 1;
switch (yystate) {
case 1:this.$ = null;
break;
case 2:return this.$
break;
case 3:this.$ = {
          name: $$[$0],
          classes: []
        };
break;
case 4:this.$ = {
          name: $$[$0-2],
          id: $$[$0],
          classes: []
        };
break;
case 5:this.$ = {
          name: 'div',
          id: $$[$0],
          classes: []
        };
break;
case 6:this.$ = {
          name: 'div',
          classes: [$$[$0]]
        };
break;
case 7:this.$ = (function () {
        $$[$0-2].classes.push($$[$0]);
        return $$[$0-2];
      }());
break;
case 8:this.$ = {
          name: $$[$0].name,
          id: $$[$0].id,
          classes: $$[$0].classes,
          properties: [],
          children: [],
          type: 'element'
        };
break;
case 9:this.$ = $$[$0-2];
break;
case 10:this.$ = (function () {
        $$[$0-3].properties = $$[$0-1];
        return $$[$0-3];
      }());
break;
case 11:this.$ = (function () {
        $$[$0-2].children = $$[$0-2].children.concat($$[$0]);
        return $$[$0-2];
      }());
break;
case 12:this.$ = (function () {
        $$[$0-3].children = $$[$0-3].children.concat($$[$0-1]);
        return $$[$0-3];
      }());
break;
case 13:this.$ = [$$[$0]];
break;
case 14:this.$ = $$[$0-2].concat($$[$0]);
break;
case 15:this.$ = {
          type: 'text',
          value: $$[$0],
          bound: true
        };
break;
case 16:this.$ = {
          type: 'text',
          value: $$[$0],
          bound: false
        };
break;
case 17:this.$ = [].concat($$[$0]);
break;
case 18:this.$ = $$[$0-2].concat($$[$0]);
break;
case 19:this.$ = $$[$0];
break;
case 20:this.$ = $$[$0];
break;
case 21:this.$ = $$[$0];
break;
case 22:this.$ = $$[$0];
break;
case 23:this.$ = $$[$0];
break;
case 24:this.$ = [$$[$0]];
break;
case 25:this.$ = $$[$0-2].concat($$[$0]);
break;
case 26:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: true,
          scope: 'attribute'
        };
break;
case 27:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: true,
          scope: 'attribute'
        };
break;
case 28:this.$ = {
          name: $$[$0-3],
          value: $$[$0-1],
          bound: true,
          scope: 'attribute',
          preventDefault: true
        };
break;
case 29:this.$ = {
          name: $$[$0-3],
          value: $$[$0-1],
          bound: true,
          scope: 'attribute',
          preventDefault: true
        };
break;
case 30:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: false,
          scope: 'attribute'
        };
break;
case 31:this.$ = (function () {
        $$[$0].scope = $$[$0-2];
        return $$[$0];
      }());
break;
case 32:this.$ = {
          children: [],
          type: 'view',
          argument: $$[$0]
        };
break;
case 33:this.$ = {
          children: [],
          type: 'view',
          argument: $$[$0],
          bound: true
        };
break;
case 34:this.$ = {
          children: [],
          type: 'collection',
          argument: $$[$0]
        };
break;
case 35:this.$ = {
          children: [],
          type: 'unless',
          argument: $$[$0]
        };
break;
case 36:this.$ = {
          children: [],
          type: 'in',
          argument: $$[$0]
        };
break;
case 37:this.$ = (function () {
        $$[$0-3].children = $$[$0-1];
        return $$[$0-3];
      }());
break;
case 38:this.$ = {
          command: $$[$0],
          "arguments": [],
          children: [],
          type: 'helper'
        };
break;
case 39:this.$ = (function () {
        $$[$0-2]["arguments"].push($$[$0]);
        return $$[$0-2];
      }());
break;
case 40:this.$ = (function () {
        $$[$0-3].children = $$[$0-1];
        return $$[$0-3];
      }());
break;
case 41:this.$ = {
          children: [],
          type: 'if',
          argument: $$[$0]
        };
break;
case 42:this.$ = (function () {
        $$[$0-3].children = $$[$0-1];
        return $$[$0-3];
      }());
break;
case 43:this.$ = (function () {
        $$[$0-1]["else"] = $$[$0];
        return $$[$0-1];
      }());
break;
case 44:this.$ = {
          "arguments": [],
          children: $$[$0-1],
          type: 'else'
        };
break;
case 45:this.$ = $$[$0];
break;
case 46:this.$ = $$[$0];
break;
case 47:this.$ = $$[$0];
break;
case 48:this.$ = $$[$0];
break;
case 49:this.$ = $$[$0];
break;
case 50:this.$ = $$[$0];
break;
case 51:this.$ = $$[$0];
break;
case 52:this.$ = (function () {}());
break;
}
},
table: [{1:[2,1],3:1,4:2,5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:3,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{1:[3]},{1:[2,2],21:[1,24]},{1:[2,17],16:[2,17],21:[2,17]},{1:[2,19],10:[1,25],13:[1,26],15:[1,27],16:[2,19],21:[2,19]},{1:[2,20],15:[1,28],16:[2,20],21:[2,20],29:[1,30],36:29},{1:[2,21],15:[1,31],16:[2,21],21:[2,21]},{1:[2,22],13:[1,32],15:[1,33],16:[2,22],21:[2,22]},{1:[2,23],13:[1,34],16:[2,23],21:[2,23]},{1:[2,8],8:[1,35],10:[2,8],13:[2,8],15:[2,8],16:[2,8],21:[2,8]},{13:[1,36]},{1:[2,13],13:[2,13],16:[2,13],21:[2,13]},{1:[2,3],7:[1,37],8:[2,3],10:[2,3],13:[2,3],15:[2,3],16:[2,3],21:[2,3]},{6:38,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{6:39,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{1:[2,15],10:[2,15],13:[2,15],15:[2,15],16:[2,15],21:[2,15]},{1:[2,16],10:[2,16],13:[2,16],15:[2,16],16:[2,16],21:[2,16]},{1:[2,45],7:[2,45],8:[2,45],10:[2,45],11:[2,45],13:[2,45],15:[2,45],16:[2,45],21:[2,45],26:[2,45],27:[2,45],28:[2,45],29:[2,45]},{1:[2,46],7:[2,46],8:[2,46],10:[2,46],11:[2,46],13:[2,46],15:[2,46],16:[2,46],21:[2,46],26:[2,46],27:[2,46],28:[2,46],29:[2,46]},{1:[2,47],7:[2,47],8:[2,47],10:[2,47],11:[2,47],13:[2,47],15:[2,47],16:[2,47],21:[2,47],26:[2,47],27:[2,47],28:[2,47],29:[2,47]},{1:[2,48],7:[2,48],8:[2,48],10:[2,48],11:[2,48],13:[2,48],15:[2,48],16:[2,48],21:[2,48],26:[2,48],27:[2,48],28:[2,48],29:[2,48]},{1:[2,49],7:[2,49],8:[2,49],10:[2,49],11:[2,49],13:[2,49],15:[2,49],16:[2,49],21:[2,49],26:[2,49],27:[2,49],28:[2,49],29:[2,49]},{1:[2,50],7:[2,50],8:[2,50],10:[2,50],11:[2,50],13:[2,50],15:[2,50],16:[2,50],21:[2,50],26:[2,50],27:[2,50],28:[2,50],29:[2,50]},{1:[2,52],6:40,10:[2,52],11:[2,52],13:[2,52],15:[2,52],16:[2,52],21:[2,52],27:[2,52],29:[2,52],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:41,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{6:45,11:[1,42],12:43,25:44,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{14:46,18:15,19:[1,16],38:[1,23]},{4:47,5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:3,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{4:48,5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:3,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{1:[2,43],15:[2,43],16:[2,43],21:[2,43],29:[2,43]},{13:[1,49]},{4:50,5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:3,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{14:51,18:15,19:[1,16],38:[1,23]},{4:52,5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:3,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{14:53,18:15,19:[1,16],38:[1,23]},{6:54,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{30:[1,56],31:[1,57],32:[1,58],33:[1,59],34:[1,60],35:[1,55]},{6:61,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{1:[2,5],8:[2,5],10:[2,5],13:[2,5],15:[2,5],16:[2,5],21:[2,5]},{1:[2,6],8:[2,6],10:[2,6],13:[2,6],15:[2,6],16:[2,6],21:[2,6]},{1:[2,51],10:[2,51],11:[2,51],13:[2,51],15:[2,51],16:[2,51],21:[2,51],27:[2,51],29:[2,51]},{1:[2,18],16:[2,18],21:[2,18]},{1:[2,9],10:[2,9],13:[2,9],15:[2,9],16:[2,9],21:[2,9]},{11:[1,62],13:[1,63]},{11:[2,24],13:[2,24]},{26:[1,64],28:[1,65]},{1:[2,11],10:[2,11],13:[2,11],15:[2,11],16:[2,11],21:[2,11]},{16:[1,66],21:[1,24]},{16:[1,67],21:[1,24]},{37:[1,68]},{16:[1,69],21:[1,24]},{1:[2,39],13:[2,39],15:[2,39],16:[2,39],21:[2,39]},{16:[1,70],21:[1,24]},{1:[2,14],13:[2,14],16:[2,14],21:[2,14]},{1:[2,7],8:[2,7],10:[2,7],13:[2,7],15:[2,7],16:[2,7],21:[2,7]},{13:[1,71]},{13:[1,72]},{13:[1,73]},{13:[1,74]},{13:[1,75]},{1:[2,38],13:[2,38],15:[2,38],16:[2,38],21:[2,38]},{1:[2,4],8:[2,4],10:[2,4],13:[2,4],15:[2,4],16:[2,4],21:[2,4]},{1:[2,10],10:[2,10],13:[2,10],15:[2,10],16:[2,10],21:[2,10]},{6:45,25:76,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{6:77,18:78,19:[1,79],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{6:45,25:80,30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19]},{1:[2,12],10:[2,12],13:[2,12],15:[2,12],16:[2,12],21:[2,12]},{1:[2,42],15:[2,42],16:[2,42],21:[2,42],29:[2,42]},{15:[1,81]},{1:[2,37],15:[2,37],16:[2,37],21:[2,37]},{1:[2,40],13:[2,40],15:[2,40],16:[2,40],21:[2,40]},{18:82,38:[1,23]},{18:84,19:[1,83],38:[1,23]},{18:85,38:[1,23]},{18:86,38:[1,23]},{18:87,38:[1,23]},{11:[2,25],13:[2,25]},{11:[2,26],13:[2,26],27:[1,88]},{11:[2,27],13:[2,27],27:[1,89]},{11:[2,30],13:[2,30]},{11:[2,31],13:[2,31]},{4:90,5:9,6:12,7:[1,13],8:[1,14],9:4,14:11,17:8,18:15,19:[1,16],20:3,22:5,23:6,24:7,29:[1,10],30:[1,17],31:[1,18],32:[1,20],33:[1,21],34:[1,22],35:[1,19],38:[1,23]},{1:[2,41],15:[2,41],16:[2,41],21:[2,41],29:[2,41]},{1:[2,32],15:[2,32],16:[2,32],21:[2,32]},{1:[2,33],15:[2,33],16:[2,33],21:[2,33]},{1:[2,34],15:[2,34],16:[2,34],21:[2,34]},{1:[2,35],15:[2,35],16:[2,35],21:[2,35]},{1:[2,36],15:[2,36],16:[2,36],21:[2,36]},{11:[2,28],13:[2,28]},{11:[2,29],13:[2,29]},{16:[1,91],21:[1,24]},{1:[2,44],15:[2,44],16:[2,44],21:[2,44],29:[2,44]}],
defaultActions: {},
parseError: function parseError(str, hash) {
    throw new Error(str);
},
parse: function parse(input) {
    var self = this, stack = [0], vstack = [null], lstack = [], table = this.table, yytext = "", yylineno = 0, yyleng = 0, recovering = 0, TERROR = 2, EOF = 1;
    this.lexer.setInput(input);
    this.lexer.yy = this.yy;
    this.yy.lexer = this.lexer;
    this.yy.parser = this;
    if (typeof this.lexer.yylloc == "undefined")
        this.lexer.yylloc = {};
    var yyloc = this.lexer.yylloc;
    lstack.push(yyloc);
    var ranges = this.lexer.options && this.lexer.options.ranges;
    if (typeof this.yy.parseError === "function")
        this.parseError = this.yy.parseError;
    function popStack(n) {
        stack.length = stack.length - 2 * n;
        vstack.length = vstack.length - n;
        lstack.length = lstack.length - n;
    }
    function lex() {
        var token;
        token = self.lexer.lex() || 1;
        if (typeof token !== "number") {
            token = self.symbols_[token] || token;
        }
        return token;
    }
    var symbol, preErrorSymbol, state, action, a, r, yyval = {}, p, len, newState, expected;
    while (true) {
        state = stack[stack.length - 1];
        if (this.defaultActions[state]) {
            action = this.defaultActions[state];
        } else {
            if (symbol === null || typeof symbol == "undefined") {
                symbol = lex();
            }
            action = table[state] && table[state][symbol];
        }
        if (typeof action === "undefined" || !action.length || !action[0]) {
            var errStr = "";
        }
        if (action[0] instanceof Array && action.length > 1) {
            throw new Error("Parse Error: multiple actions possible at state: " + state + ", token: " + symbol);
        }
        switch (action[0]) {
        case 1:
            stack.push(symbol);
            vstack.push(this.lexer.yytext);
            lstack.push(this.lexer.yylloc);
            stack.push(action[1]);
            symbol = null;
            if (!preErrorSymbol) {
                yyleng = this.lexer.yyleng;
                yytext = this.lexer.yytext;
                yylineno = this.lexer.yylineno;
                yyloc = this.lexer.yylloc;
                if (recovering > 0)
                    recovering--;
            } else {
                symbol = preErrorSymbol;
                preErrorSymbol = null;
            }
            break;
        case 2:
            len = this.productions_[action[1]][1];
            yyval.$ = vstack[vstack.length - len];
            yyval._$ = {first_line: lstack[lstack.length - (len || 1)].first_line, last_line: lstack[lstack.length - 1].last_line, first_column: lstack[lstack.length - (len || 1)].first_column, last_column: lstack[lstack.length - 1].last_column};
            if (ranges) {
                yyval._$.range = [lstack[lstack.length - (len || 1)].range[0], lstack[lstack.length - 1].range[1]];
            }
            r = this.performAction.call(yyval, yytext, yyleng, yylineno, this.yy, action[1], vstack, lstack);
            if (typeof r !== "undefined") {
                return r;
            }
            if (len) {
                stack = stack.slice(0, -1 * len * 2);
                vstack = vstack.slice(0, -1 * len);
                lstack = lstack.slice(0, -1 * len);
            }
            stack.push(this.productions_[action[1]][0]);
            vstack.push(yyval.$);
            lstack.push(yyval._$);
            newState = table[stack[stack.length - 2]][stack[stack.length - 1]];
            stack.push(newState);
            break;
        case 3:
            return true;
        }
    }
    return true;
}
};
;function Parser () { this.yy = {}; }Parser.prototype = parser;parser.Parser = Parser;
return new Parser;
})();
if (typeof require !== 'undefined' && typeof exports !== 'undefined') {
exports.parser = parser;
exports.Parser = parser.Parser;
exports.parse = function () { return parser.parse.apply(parser, arguments); }
exports.main = function commonjsMain(args) {
    if (!args[1])
        throw new Error('Usage: '+args[0]+' FILE');
    var source, cwd;
    if (typeof process !== 'undefined') {
        source = require('fs').readFileSync(require('path').resolve(args[1]), "utf8");
    } else {
        source = require("file").path(require("file").cwd()).join(args[1]).read({charset: "utf-8"});
    }
    return exports.parser.parse(source);
}
if (typeof module !== 'undefined' && require.main === module) {
  exports.main(typeof process !== 'undefined' ? process.argv.slice(1) : require("system").args);
}
}var AssociationCollection, COMMENT, Cache, Collection, Compile, DynamicNode, Event, IDENTIFIER, KEYWORDS, LITERAL, Lexer, MULTI_DENT, Map, Model, Node, Property, PropertyAccessor, PropertyDefinition, STRING, Serenade, Transform, View, WHITESPACE, assignUnlessEqual, capitalize, compile, def, defineEvent, defineOptions, defineProperty, extend, format, getValue, hash, idCounter, isArray, isArrayIndex, merge, normalize, pairToObject, primitiveTypes, safeDelete, safePush, serializeObject, settings,
  __hasProp = {}.hasOwnProperty,
  __slice = [].slice,
  __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
  __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; },
  __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

settings = {
  async: false
};

def = Object.defineProperty;

primitiveTypes = ["undefined", "boolean", "number", "string"];

defineOptions = function(object, name) {
  return def(object, name, {
    get: function() {
      var options;

      if (!this.hasOwnProperty("_" + name)) {
        options = name in Object.getPrototypeOf(this) ? Object.create(Object.getPrototypeOf(this)[name]) : {};
        def(this, "_" + name, {
          configurable: true,
          writable: true,
          value: options
        });
      }
      return this["_" + name];
    }
  });
};

extend = function(target, source, enumerable) {
  var key, value;

  if (enumerable == null) {
    enumerable = true;
  }
  for (key in source) {
    if (!__hasProp.call(source, key)) continue;
    value = source[key];
    if (enumerable) {
      target[key] = value;
    } else {
      def(target, key, {
        value: value,
        configurable: true
      });
    }
  }
  return target;
};

assignUnlessEqual = function(object, prop, value) {
  if (object[prop] !== value) {
    return object[prop] = value;
  }
};

merge = function(target, source, enumerable) {
  if (enumerable == null) {
    enumerable = true;
  }
  return extend(extend({}, target, enumerable), source, enumerable);
};

format = function(model, key) {
  if (model[key + "_property"]) {
    return model[key + "_property"].format();
  } else {
    return model[key];
  }
};

isArray = function(object) {
  return Object.prototype.toString.call(object) === "[object Array]";
};

pairToObject = function(one, two) {
  var temp;

  temp = {};
  temp[one] = two;
  return temp;
};

serializeObject = function(object) {
  var item, _i, _len, _results;

  if (object && typeof object.toJSON === 'function') {
    return object.toJSON();
  } else if (isArray(object)) {
    _results = [];
    for (_i = 0, _len = object.length; _i < _len; _i++) {
      item = object[_i];
      _results.push(serializeObject(item));
    }
    return _results;
  } else {
    return object;
  }
};

capitalize = function(word) {
  return word.slice(0, 1).toUpperCase() + word.slice(1);
};

hash = function(value) {
  var key;

  key = value instanceof Object ? (!("_s_hash" in value) ? def(value, "_s_hash", {
    value: ++arguments.callee.current
  }) : void 0, value._s_hash) : value;
  return (typeof value) + ' ' + key;
};

hash.current = 0;

safePush = function(object, collection, item) {
  if (!object[collection] || object[collection].indexOf(item) === -1) {
    if (object.hasOwnProperty(collection)) {
      return object[collection].push(item);
    } else if (object[collection]) {
      return def(object, collection, {
        value: [item].concat(object[collection])
      });
    } else {
      return def(object, collection, {
        value: [item]
      });
    }
  }
};

safeDelete = function(object, collection, item) {
  var index;

  if (object[collection] && (index = object[collection].indexOf(item)) !== -1) {
    if (!object.hasOwnProperty(collection)) {
      def(object, collection, {
        value: [].concat(object[collection])
      });
    }
    return object[collection].splice(index, 1);
  }
};

Map = (function() {
  function Map(array) {
    var element, index, _i, _len;

    this.map = {};
    for (index = _i = 0, _len = array.length; _i < _len; index = ++_i) {
      element = array[index];
      this.put(index, element);
    }
  }

  Map.prototype.isMember = function(element) {
    var _ref;

    return ((_ref = this.map[hash(element)]) != null ? _ref[0].length : void 0) > 0;
  };

  Map.prototype.indexOf = function(element) {
    var _ref, _ref1;

    return (_ref = this.map[hash(element)]) != null ? (_ref1 = _ref[0]) != null ? _ref1[0] : void 0 : void 0;
  };

  Map.prototype.put = function(index, element) {
    var existing;

    existing = this.map[hash(element)];
    return this.map[hash(element)] = existing ? [
      existing[0].concat(index).sort(function(a, b) {
        return a - b;
      }), element
    ] : [[index], element];
  };

  Map.prototype.remove = function(element) {
    var _base, _ref;

    return (_ref = this.map[hash(element)]) != null ? typeof (_base = _ref[0]).shift === "function" ? _base.shift() : void 0 : void 0;
  };

  return Map;

})();

Transform = function(from, to) {
  var actual, cleaned, cleanedMap, complete, completeMap, element, index, indexActual, indexWanted, operations, targetMap, wanted, _i, _j, _k, _len, _len1, _len2;

  if (from == null) {
    from = [];
  }
  if (to == null) {
    to = [];
  }
  operations = [];
  to = to.map(function(e) {
    return e;
  });
  targetMap = new Map(to);
  cleaned = [];
  for (_i = 0, _len = from.length; _i < _len; _i++) {
    element = from[_i];
    if (targetMap.isMember(element)) {
      cleaned.push(element);
    } else {
      operations.push({
        type: "remove",
        index: cleaned.length
      });
    }
    targetMap.remove(element);
  }
  complete = [].concat(cleaned);
  cleanedMap = new Map(cleaned);
  for (index = _j = 0, _len1 = to.length; _j < _len1; index = ++_j) {
    element = to[index];
    if (!cleanedMap.isMember(element)) {
      operations.push({
        type: "insert",
        index: index,
        value: element
      });
      complete.splice(index, 0, element);
    }
    cleanedMap.remove(element);
  }
  completeMap = new Map(complete);
  for (indexActual = _k = 0, _len2 = complete.length; _k < _len2; indexActual = ++_k) {
    actual = complete[indexActual];
    wanted = to[indexActual];
    if (actual !== wanted) {
      indexWanted = completeMap.indexOf(wanted);
      completeMap.remove(actual);
      completeMap.remove(wanted);
      completeMap.put(indexWanted, actual);
      complete[indexActual] = wanted;
      complete[indexWanted] = actual;
      operations.push({
        type: "swap",
        index: indexActual,
        "with": indexWanted
      });
    } else {
      completeMap.remove(actual);
    }
  }
  return operations;
};

Event = (function() {
  function Event(object, name, options) {
    this.object = object;
    this.name = name;
    this.options = options;
  }

  def(Event.prototype, "async", {
    get: function() {
      if ("async" in this.options) {
        return this.options.async;
      } else {
        return settings.async;
      }
    }
  });

  Event.prototype.trigger = function() {
    var args, _base,
      _this = this;

    args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    if (this.listeners.length) {
      this.queue.push(args);
      if (this.async) {
        if (this.options.animate) {
          return (_base = this.queue).frame || (_base.frame = requestAnimationFrame((function() {
            return _this.resolve();
          }), this.options.timeout || 0));
        } else {
          if (this.queue.timeout && !this.options.buffer) {
            return;
          }
          clearTimeout(this.queue.timeout);
          return this.queue.timeout = setTimeout((function() {
            return _this.resolve();
          }), this.options.timeout || 0);
        }
      } else {
        return this.resolve();
      }
    }
  };

  Event.prototype.bind = function(fun) {
    if (this.options.bind) {
      this.options.bind.call(this.object, fun);
    }
    return safePush(this.object._s, "listeners_" + this.name, fun);
  };

  Event.prototype.one = function(fun) {
    var unbind,
      _this = this;

    unbind = function(fun) {
      return _this.unbind(fun);
    };
    return this.bind(function() {
      unbind(arguments.callee);
      return fun.apply(this, arguments);
    });
  };

  Event.prototype.unbind = function(fun) {
    safeDelete(this.object._s, "listeners_" + this.name, fun);
    if (this.options.unbind) {
      return this.options.unbind.call(this.object, fun);
    }
  };

  Event.prototype.resolve = function() {
    var args, perform, _i, _len, _ref,
      _this = this;

    if (this.queue.frame) {
      cancelAnimationFrame(this.queue.frame);
    }
    clearTimeout(this.queue.timeout);
    if (this.queue.length) {
      perform = function(args) {
        if (_this.listeners) {
          return ([].concat(_this.listeners)).forEach(function(listener) {
            return listener.apply(_this.object, args);
          });
        }
      };
      if (this.options.optimize) {
        perform(this.options.optimize(this.queue));
      } else {
        _ref = this.queue;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          args = _ref[_i];
          perform(args);
        }
      }
    }
    return this.queue = [];
  };

  def(Event.prototype, "listeners", {
    get: function() {
      return this.object._s["listeners_" + this.name] || [];
    }
  });

  def(Event.prototype, "queue", {
    get: function() {
      if (!this.object._s.hasOwnProperty("queue_" + this.name)) {
        this.queue = [];
      }
      return this.object._s["queue_" + this.name];
    },
    set: function(val) {
      return this.object._s["queue_" + this.name] = val;
    }
  });

  return Event;

})();

defineEvent = function(object, name, options) {
  if (options == null) {
    options = {};
  }
  if (!("_s" in object)) {
    defineOptions(object, "_s");
  }
  return def(object, name, {
    configurable: true,
    get: function() {
      return new Event(this, name, options);
    }
  });
};

Cache = {
  _identityMap: {},
  get: function(ctor, id) {
    var name, _ref;

    name = ctor.uniqueId();
    if (name && id) {
      return (_ref = this._identityMap[name]) != null ? _ref[id] : void 0;
    }
  },
  set: function(ctor, id, obj) {
    var name, _base;

    name = ctor.uniqueId();
    if (name && id) {
      (_base = this._identityMap)[name] || (_base[name] = {});
      return this._identityMap[name][id] = obj;
    }
  },
  unset: function(ctor, id) {
    var name, _base;

    name = ctor.uniqueId();
    if (name && id) {
      (_base = this._identityMap)[name] || (_base[name] = {});
      return delete this._identityMap[name][id];
    }
  }
};

isArrayIndex = function(index) {
  return ("" + index).match(/^\d+$/);
};

Collection = (function() {
  var _this = this;

  defineEvent(Collection.prototype, "change");

  def(Collection.prototype, "first", {
    get: function() {
      return this[0];
    }
  });

  def(Collection.prototype, "last", {
    get: function() {
      return this[this.length - 1];
    }
  });

  function Collection(list) {
    var index, val, _i, _len;

    if (list == null) {
      list = [];
    }
    for (index = _i = 0, _len = list.length; _i < _len; index = ++_i) {
      val = list[index];
      this[index] = val;
    }
    this.length = (list != null ? list.length : void 0) || 0;
  }

  Collection.prototype.get = function(index) {
    return this[index];
  };

  Collection.prototype.set = function(index, value) {
    this[index] = value;
    if (isArrayIndex(index)) {
      this.length = Math.max(this.length, index + 1);
    }
    return value;
  };

  Collection.prototype.update = function(list) {
    var index, val, _, _i, _len;

    for (index in this) {
      _ = this[index];
      if (isArrayIndex(index)) {
        delete this[index];
      }
    }
    for (index = _i = 0, _len = list.length; _i < _len; index = ++_i) {
      val = list[index];
      this[index] = val;
    }
    this.length = (list != null ? list.length : void 0) || 0;
    return list;
  };

  Collection.prototype.sortBy = function(attribute) {
    return this.sort(function(a, b) {
      if (a[attribute] < b[attribute]) {
        return -1;
      } else {
        return 1;
      }
    });
  };

  Collection.prototype.includes = function(item) {
    return this.indexOf(item) >= 0;
  };

  Collection.prototype.find = function(fun) {
    var item, _i, _len;

    for (_i = 0, _len = this.length; _i < _len; _i++) {
      item = this[_i];
      if (fun(item)) {
        return item;
      }
    }
  };

  Collection.prototype.insertAt = function(index, value) {
    Array.prototype.splice.call(this, index, 0, value);
    return value;
  };

  Collection.prototype.deleteAt = function(index) {
    var value;

    value = this[index];
    Array.prototype.splice.call(this, index, 1);
    return value;
  };

  Collection.prototype["delete"] = function(item) {
    var index;

    index = this.indexOf(item);
    if (index !== -1) {
      return this.deleteAt(index);
    }
  };

  Collection.prototype.concat = function() {
    var arg, args, _ref;

    args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    args = (function() {
      var _i, _len, _results;

      _results = [];
      for (_i = 0, _len = args.length; _i < _len; _i++) {
        arg = args[_i];
        if (arg instanceof Collection) {
          _results.push(arg.toArray());
        } else {
          _results.push(arg);
        }
      }
      return _results;
    })();
    return new Collection((_ref = this.toArray()).concat.apply(_ref, args));
  };

  Collection.prototype.toArray = function() {
    var array, index, val;

    array = [];
    for (index in this) {
      val = this[index];
      if (isArrayIndex(index)) {
        array[index] = val;
      }
    }
    return array;
  };

  Collection.prototype.clone = function() {
    return new Collection(this.toArray());
  };

  Collection.prototype.toString = function() {
    return this.toArray().toString();
  };

  Collection.prototype.toLocaleString = function() {
    return this.toArray().toLocaleString();
  };

  Collection.prototype.toJSON = function() {
    return serializeObject(this.toArray());
  };

  Object.getOwnPropertyNames(Array.prototype).forEach(function(fun) {
    var _base;

    return (_base = Collection.prototype)[fun] || (_base[fun] = Array.prototype[fun]);
  });

  ["splice", "map", "filter", "slice"].forEach(function(fun) {
    var original;

    original = Collection.prototype[fun];
    return Collection.prototype[fun] = function() {
      return new Collection(original.apply(this, arguments));
    };
  });

  ["push", "pop", "unshift", "shift", "splice", "sort", "reverse", "update", "set", "insertAt", "deleteAt"].forEach(function(fun) {
    var original;

    original = Collection.prototype[fun];
    return Collection.prototype[fun] = function() {
      var old, val;

      old = this.clone();
      val = original.apply(this, arguments);
      this.change.trigger(old, this);
      return val;
    };
  });

  return Collection;

}).call(this);

AssociationCollection = (function(_super) {
  __extends(AssociationCollection, _super);

  function AssociationCollection(owner, options, list) {
    var _this = this;

    this.owner = owner;
    this.options = options;
    this._convert.apply(this, __slice.call(list).concat([function() {
      var items;

      items = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return AssociationCollection.__super__.constructor.call(_this, items);
    }]));
  }

  AssociationCollection.prototype.set = function(index, item) {
    var _this = this;

    return this._convert(item, function(item) {
      return AssociationCollection.__super__.set.call(_this, index, item);
    });
  };

  AssociationCollection.prototype.push = function(item) {
    var _this = this;

    return this._convert(item, function(item) {
      return AssociationCollection.__super__.push.call(_this, item);
    });
  };

  AssociationCollection.prototype.update = function(list) {
    var _this = this;

    return this._convert.apply(this, __slice.call(list).concat([function() {
      var items;

      items = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return AssociationCollection.__super__.update.call(_this, items);
    }]));
  };

  AssociationCollection.prototype.splice = function() {
    var deleteCount, list, start,
      _this = this;

    start = arguments[0], deleteCount = arguments[1], list = 3 <= arguments.length ? __slice.call(arguments, 2) : [];
    return this._convert.apply(this, __slice.call(list).concat([function() {
      var items;

      items = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return AssociationCollection.__super__.splice.apply(_this, [start, deleteCount].concat(__slice.call(items)));
    }]));
  };

  AssociationCollection.prototype.insertAt = function(index, item) {
    var _this = this;

    return this._convert(item, function(item) {
      return AssociationCollection.__super__.insertAt.call(_this, index, item);
    });
  };

  AssociationCollection.prototype._convert = function() {
    var fn, item, items, returnValue, _i, _j, _len;

    items = 2 <= arguments.length ? __slice.call(arguments, 0, _i = arguments.length - 1) : (_i = 0, []), fn = arguments[_i++];
    items = (function() {
      var _j, _len, _results;

      _results = [];
      for (_j = 0, _len = items.length; _j < _len; _j++) {
        item = items[_j];
        if ((item != null ? item.constructor : void 0) === Object && this.options.as) {
          _results.push(item = new (this.options.as())(item));
        } else {
          _results.push(item);
        }
      }
      return _results;
    }).call(this);
    returnValue = fn.apply(null, items);
    for (_j = 0, _len = items.length; _j < _len; _j++) {
      item = items[_j];
      if (this.options.inverseOf && item[this.options.inverseOf] !== this.owner) {
        item[this.options.inverseOf] = this.owner;
      }
    }
    return returnValue;
  };

  return AssociationCollection;

})(Collection);

PropertyDefinition = (function() {
  function PropertyDefinition(name, options) {
    var _i, _len, _ref;

    this.name = name;
    extend(this, options);
    this.dependencies = [];
    this.localDependencies = [];
    this.globalDependencies = [];
    if (this.dependsOn) {
      _ref = [].concat(this.dependsOn);
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        name = _ref[_i];
        this.addDependency(name);
      }
    }
  }

  def(PropertyDefinition.prototype, "eventOptions", {
    get: function() {
      var name, options;

      name = this.name;
      options = {
        timeout: this.timeout,
        buffer: this.buffer,
        animate: this.animate,
        bind: function() {
          this[name];
          return this[name + "_property"].registerGlobal();
        },
        optimize: function(queue) {
          var _ref, _ref1;

          return [(_ref = queue[0]) != null ? _ref[0] : void 0, (_ref1 = queue[queue.length - 1]) != null ? _ref1[1] : void 0];
        }
      };
      if (this.async != null) {
        options.async = this.async;
      }
      return options;
    }
  });

  PropertyDefinition.prototype.addDependency = function(name) {
    var subname, type, _ref, _ref1;

    if (this.dependencies.indexOf(name) === -1) {
      this.dependencies.push(name);
      if (name.match(/\./)) {
        type = "singular";
        _ref = name.split("."), name = _ref[0], subname = _ref[1];
      } else if (name.match(/:/)) {
        type = "collection";
        _ref1 = name.split(":"), name = _ref1[0], subname = _ref1[1];
      }
      this.localDependencies.push(name);
      if (this.localDependencies.indexOf(name) === -1) {
        this.localDependencies.push(name);
      }
      if (type) {
        return this.globalDependencies.push({
          subname: subname,
          name: name,
          type: type
        });
      }
    }
  };

  return PropertyDefinition;

})();

PropertyAccessor = (function() {
  var _this = this;

  function PropertyAccessor(definition, object) {
    this.definition = definition;
    this.object = object;
    this.trigger = __bind(this.trigger, this);
    this.name = this.definition.name;
    this.valueName = "_" + this.name;
    this.event = new Event(this.object, this.name + "_change", this.definition.eventOptions);
    this._gcQueue = [];
  }

  PropertyAccessor.prototype.set = function(value) {
    if (typeof value === "function") {
      return this.definition.get = value;
    } else {
      if (this.definition.set) {
        this.definition.set.call(this.object, value);
      } else {
        def(this.object, this.valueName, {
          value: value,
          configurable: true
        });
      }
      return this.trigger();
    }
  };

  PropertyAccessor.prototype.get = function() {
    var listener, value,
      _this = this;

    if (this.definition.get && !(this.definition.cache && this.valueName in this.object)) {
      listener = function(name) {
        return _this.definition.addDependency(name);
      };
      if (!("dependsOn" in this.definition)) {
        this.object._s.property_access.bind(listener);
      }
      value = this.definition.get.call(this.object);
      if (!("dependsOn" in this.definition)) {
        this.object._s.property_access.unbind(listener);
      }
      if (this.definition.cache) {
        this.object[this.valueName] = value;
        if (!this._isCached) {
          this._isCached = true;
          this.bind(function() {});
        }
      }
    } else {
      value = this.object[this.valueName];
    }
    this.object._s.property_access.trigger(this.name);
    return value;
  };

  PropertyAccessor.prototype.format = function() {
    if (typeof this.definition.format === "function") {
      return this.definition.format.call(this.object, this.get());
    } else {
      return this.get();
    }
  };

  PropertyAccessor.prototype.registerGlobal = function() {
    var dependency, _i, _len, _ref, _ref1, _results,
      _this = this;

    if (this._isRegistered) {
      return;
    }
    this._isRegistered = true;
    this.definition.globalDependencies.forEach(function(dep) {
      var name, subname, type, updateCollectionBindings, updateItemBinding, updateItemBindings, _ref, _ref1;

      name = dep.name, type = dep.type, subname = dep.subname;
      switch (type) {
        case "singular":
          updateItemBinding = function(before, after) {
            var _ref, _ref1;

            if (before != null) {
              if ((_ref = before[subname + "_property"]) != null) {
                _ref.unbind(_this.trigger);
              }
            }
            return after != null ? (_ref1 = after[subname + "_property"]) != null ? _ref1.bind(_this.trigger) : void 0 : void 0;
          };
          if ((_ref = _this.object[name + "_property"]) != null) {
            _ref.bind(updateItemBinding);
          }
          updateItemBinding(void 0, _this.object[name]);
          return _this._gcQueue.push(function() {
            var _ref1;

            updateItemBinding(_this.object[name], void 0);
            return (_ref1 = _this.object[name + "_property"]) != null ? _ref1.unbind(updateItemBinding) : void 0;
          });
        case "collection":
          updateItemBindings = function(before, after) {
            if (before != null) {
              if (typeof before.forEach === "function") {
                before.forEach(function(item) {
                  var _ref1;

                  return (_ref1 = item[subname + "_property"]) != null ? _ref1.unbind(_this.trigger) : void 0;
                });
              }
            }
            return after != null ? typeof after.forEach === "function" ? after.forEach(function(item) {
              var _ref1;

              return (_ref1 = item[subname + "_property"]) != null ? _ref1.bind(_this.trigger) : void 0;
            }) : void 0 : void 0;
          };
          updateCollectionBindings = function(before, after) {
            var _ref1, _ref2, _ref3, _ref4;

            updateItemBindings(before, after);
            if (before != null) {
              if ((_ref1 = before.change) != null) {
                _ref1.unbind(_this.trigger);
              }
            }
            if (after != null) {
              if ((_ref2 = after.change) != null) {
                _ref2.bind(_this.trigger);
              }
            }
            if (before != null) {
              if ((_ref3 = before.change) != null) {
                _ref3.unbind(updateItemBindings);
              }
            }
            return after != null ? (_ref4 = after.change) != null ? _ref4.bind(updateItemBindings) : void 0 : void 0;
          };
          if ((_ref1 = _this.object[name + "_property"]) != null) {
            _ref1.bind(updateCollectionBindings);
          }
          updateCollectionBindings(void 0, _this.object[name]);
          return _this._gcQueue.push(function() {
            var _ref2;

            updateCollectionBindings(_this.object[name], void 0);
            return (_ref2 = _this.object[name + "_property"]) != null ? _ref2.unbind(updateCollectionBindings) : void 0;
          });
      }
    });
    _ref = this.definition.localDependencies;
    _results = [];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      dependency = _ref[_i];
      _results.push((_ref1 = this.object[dependency + "_property"]) != null ? _ref1.registerGlobal() : void 0);
    }
    return _results;
  };

  PropertyAccessor.prototype.gc = function() {
    var dependency, fn, _i, _j, _len, _len1, _ref, _ref1, _ref2, _results,
      _this = this;

    if (this.listeners.length === 0 && !this.dependentProperties.find(function(prop) {
      return prop.listeners.length !== 0;
    })) {
      _ref = this._gcQueue;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        fn = _ref[_i];
        fn();
      }
      this._isRegistered = false;
    }
    _ref1 = this.definition.localDependencies;
    _results = [];
    for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
      dependency = _ref1[_j];
      _results.push((_ref2 = this.object[dependency + "_property"]) != null ? _ref2.gc() : void 0);
    }
    return _results;
  };

  PropertyAccessor.prototype.trigger = function() {
    var changes, name, newValue, _i, _len, _ref, _ref1;

    this.clearCache();
    if (this.hasChanged()) {
      newValue = this.get();
      this.event.trigger(this._oldValue, newValue);
      changes = {};
      changes[this.name] = newValue;
      if ((_ref = this.object.changed) != null) {
        if (typeof _ref.trigger === "function") {
          _ref.trigger(changes);
        }
      }
      _ref1 = this.dependents;
      for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
        name = _ref1[_i];
        this.object[name + "_property"].trigger();
      }
      return this._oldValue = newValue;
    }
  };

  ["bind", "one", "resolve"].forEach(function(fn) {
    return PropertyAccessor.prototype[fn] = function() {
      var _ref;

      return (_ref = this.event)[fn].apply(_ref, arguments);
    };
  });

  PropertyAccessor.prototype.unbind = function(fn) {
    this.event.unbind(fn);
    return this.gc();
  };

  def(PropertyAccessor.prototype, "dependents", {
    get: function() {
      var deps, findDependencies,
        _this = this;

      deps = [];
      findDependencies = function(name) {
        var property, _i, _len, _ref, _ref1, _results;

        _ref = _this.object._s.properties;
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          property = _ref[_i];
          if ((_ref1 = property.name, __indexOf.call(deps, _ref1) < 0) && __indexOf.call(property.localDependencies, name) >= 0) {
            deps.push(property.name);
            _results.push(findDependencies(property.name));
          } else {
            _results.push(void 0);
          }
        }
        return _results;
      };
      findDependencies(this.name);
      return deps;
    }
  });

  def(PropertyAccessor.prototype, "listeners", {
    get: function() {
      return this.event.listeners;
    }
  });

  PropertyAccessor.prototype.clearCache = function() {
    if (this.definition.cache && this.definition.get) {
      return delete this.object[this.valueName];
    }
  };

  PropertyAccessor.prototype.hasChanged = function() {
    var _ref, _ref1;

    if ((_ref = this.definition.changed) === true || _ref === false) {
      return this.definition.changed;
    } else {
      if ("_oldValue" in this) {
        if (this.definition.changed) {
          return this.definition.changed.call(this.object, this._oldValue, this.get());
        } else {
          if (_ref1 = typeof this._oldValue, __indexOf.call(primitiveTypes, _ref1) >= 0) {
            return this._oldValue !== this.get();
          } else {
            return true;
          }
        }
      } else {
        return true;
      }
    }
  };

  def(PropertyAccessor.prototype, "dependentProperties", {
    get: function() {
      var name;

      return new Serenade.Collection((function() {
        var _i, _len, _ref, _results;

        _ref = this.dependents;
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          name = _ref[_i];
          _results.push(this.object[name + "_property"]);
        }
        return _results;
      }).call(this));
    }
  });

  return PropertyAccessor;

}).call(this);

defineProperty = function(object, name, options) {
  var accessorName, definition;

  if (options == null) {
    options = {};
  }
  definition = new PropertyDefinition(name, options);
  if (!("_s" in object)) {
    defineOptions(object, "_s");
  }
  safePush(object._s, "properties", definition);
  defineEvent(object._s, "property_access");
  def(object, name, {
    get: function() {
      return this[name + "_property"].get();
    },
    set: function(value) {
      return this[name + "_property"].set(value);
    },
    configurable: true,
    enumerable: "enumerable" in options ? options.enumerable : true
  });
  accessorName = name + "_property";
  def(object, accessorName, {
    get: function() {
      if (!this._s.hasOwnProperty(accessorName)) {
        this._s[accessorName] = new PropertyAccessor(definition, this);
      }
      return this._s[accessorName];
    },
    configurable: true
  });
  if (typeof options.serialize === 'string') {
    defineProperty(object, options.serialize, {
      get: function() {
        return this[name];
      },
      set: function(v) {
        return this[name] = v;
      },
      configurable: true
    });
  }
  if ("value" in options) {
    return object[name] = options.value;
  }
};

idCounter = 1;

Model = (function() {
  Model.identityMap = true;

  Model.find = function(id) {
    return Cache.get(this, id) || new this({
      id: id
    });
  };

  Model.extend = function(ctor) {
    var New;

    return New = (function(_super) {
      __extends(New, _super);

      function New() {
        var val;

        val = New.__super__.constructor.apply(this, arguments);
        if (val) {
          return val;
        }
        if (ctor) {
          ctor.apply(this, arguments);
        }
      }

      return New;

    })(this);
  };

  Model.property = function() {
    var name, names, options, _i, _j, _len, _results;

    names = 2 <= arguments.length ? __slice.call(arguments, 0, _i = arguments.length - 1) : (_i = 0, []), options = arguments[_i++];
    if (typeof options === "string") {
      names.push(options);
      options = {};
    }
    _results = [];
    for (_j = 0, _len = names.length; _j < _len; _j++) {
      name = names[_j];
      _results.push(defineProperty(this.prototype, name, options));
    }
    return _results;
  };

  Model.event = function(name, options) {
    return defineEvent(this.prototype, name, options);
  };

  Model.delegate = function() {
    var names, options, to, _i,
      _this = this;

    names = 2 <= arguments.length ? __slice.call(arguments, 0, _i = arguments.length - 1) : (_i = 0, []), options = arguments[_i++];
    to = options.to;
    return names.forEach(function(name) {
      var propName, propOptions;

      propName = name;
      if (options.prefix === true) {
        propName = to + capitalize(name);
      } else if (options.prefix) {
        propName = options.prefix + capitalize(name);
      }
      if (options.suffix === true) {
        propName = propName + capitalize(to);
      } else if (options.suffix) {
        propName = propName + options.suffix;
      }
      propOptions = merge(options, {
        dependsOn: options.dependsOn || ("" + to + "." + name),
        get: function() {
          var _ref;

          return (_ref = this[to]) != null ? _ref[name] : void 0;
        },
        set: function(value) {
          var _ref;

          return (_ref = this[to]) != null ? _ref[name] = value : void 0;
        },
        format: function() {
          if (this[to] != null) {
            return Serenade.format(this[to], name);
          }
        }
      });
      return _this.property(propName, propOptions);
    });
  };

  Model.collection = function(name, options) {
    var propOptions;

    if (options == null) {
      options = {};
    }
    propOptions = merge(options, {
      changed: true,
      get: function() {
        var valueName;

        valueName = "_" + name;
        if (!this[valueName]) {
          this[valueName] = new Collection([]);
          this[valueName].change.bind(this[name + "_property"].trigger);
        }
        return this[valueName];
      },
      set: function(value) {
        return this[name].update(value);
      }
    });
    this.property(name, propOptions);
    return this.property(name + 'Count', {
      get: function() {
        return this[name].length;
      },
      dependsOn: name
    });
  };

  Model.belongsTo = function(name, options) {
    var propOptions;

    if (options == null) {
      options = {};
    }
    propOptions = merge(options, {
      set: function(model) {
        var previous, valueName;

        valueName = "_" + name;
        if (model && model.constructor === Object && options.as) {
          model = new (options.as())(model);
        }
        previous = this[valueName];
        this[valueName] = model;
        if (options.inverseOf && !model[options.inverseOf].includes(this)) {
          if (previous) {
            previous[options.inverseOf]["delete"](this);
          }
          return model[options.inverseOf].push(this);
        }
      }
    });
    this.property(name, propOptions);
    return this.property(name + 'Id', {
      get: function() {
        var _ref;

        return (_ref = this[name]) != null ? _ref.id : void 0;
      },
      set: function(id) {
        if (id != null) {
          return this[name] = options.as().find(id);
        }
      },
      dependsOn: name,
      serialize: options.serializeId
    });
  };

  Model.hasMany = function(name, options) {
    var propOptions;

    if (options == null) {
      options = {};
    }
    propOptions = merge(options, {
      changed: true,
      get: function() {
        var valueName;

        valueName = "_" + name;
        if (!this[valueName]) {
          this[valueName] = new AssociationCollection(this, options, []);
          this[valueName].change.bind(this[name + "_property"].trigger);
        }
        return this[valueName];
      },
      set: function(value) {
        return this[name].update(value);
      }
    });
    this.property(name, propOptions);
    this.property(name + 'Ids', {
      get: function() {
        return new Collection(this[name]).map(function(item) {
          return item != null ? item.id : void 0;
        });
      },
      set: function(ids) {
        var id, objects;

        objects = (function() {
          var _i, _len, _results;

          _results = [];
          for (_i = 0, _len = ids.length; _i < _len; _i++) {
            id = ids[_i];
            _results.push(options.as().find(id));
          }
          return _results;
        })();
        return this[name].update(objects);
      },
      dependsOn: name,
      serialize: options.serializeIds
    });
    return this.property(name + 'Count', {
      get: function() {
        return this[name].length;
      },
      dependsOn: name
    });
  };

  Model.selection = function(name, options) {
    var propOptions;

    if (options == null) {
      options = {};
    }
    propOptions = merge(options, {
      get: function() {
        return this[options.from].filter(function(item) {
          return item[options.filter];
        });
      },
      dependsOn: "" + options.from + ":" + options.filter
    });
    this.property(name, propOptions);
    return this.property(name + 'Count', {
      get: function() {
        return this[name].length;
      },
      dependsOn: name
    });
  };

  Model.uniqueId = function() {
    if (!(this._uniqueId && this._uniqueGen === this)) {
      this._uniqueId = (idCounter += 1);
      this._uniqueGen = this;
    }
    return this._uniqueId;
  };

  Model.property('id', {
    serialize: true,
    set: function(val) {
      Cache.unset(this.constructor, this.id);
      Cache.set(this.constructor, val, this);
      return this._s.val_id = val;
    },
    get: function() {
      return this._s.val_id;
    }
  });

  Model.event("saved");

  Model.event("changed", {
    optimize: function(queue) {
      var item, result, _i, _len;

      result = {};
      for (_i = 0, _len = queue.length; _i < _len; _i++) {
        item = queue[_i];
        extend(result, item[0]);
      }
      return [result];
    }
  });

  function Model(attributes) {
    var fromCache;

    if (this.constructor.identityMap && (attributes != null ? attributes.id : void 0)) {
      fromCache = Cache.get(this.constructor, attributes.id);
      if (fromCache) {
        fromCache.set(attributes);
        return fromCache;
      } else {
        Cache.set(this.constructor, attributes.id, this);
      }
    }
    this.set(attributes);
  }

  Model.prototype.set = function(attributes) {
    var name, value, _results;

    _results = [];
    for (name in attributes) {
      if (!__hasProp.call(attributes, name)) continue;
      value = attributes[name];
      if (!(name in this)) {
        defineProperty(this, name);
      }
      _results.push(this[name] = value);
    }
    return _results;
  };

  Model.prototype.save = function() {
    return this.saved.trigger();
  };

  Model.prototype.toJSON = function() {
    var key, property, serialized, value, _i, _len, _ref, _ref1;

    serialized = {};
    _ref = this._s.properties;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      property = _ref[_i];
      if (typeof property.serialize === 'string') {
        serialized[property.serialize] = serializeObject(this[property.name]);
      } else if (typeof property.serialize === 'function') {
        _ref1 = property.serialize.call(this), key = _ref1[0], value = _ref1[1];
        serialized[key] = serializeObject(value);
      } else if (property.serialize) {
        serialized[property.name] = serializeObject(this[property.name]);
      }
    }
    return serialized;
  };

  Model.prototype.toString = function() {
    return JSON.stringify(this.toJSON());
  };

  return Model;

})();

IDENTIFIER = /^[a-zA-Z][a-zA-Z0-9\-_]*/;

LITERAL = /^[\[\]=\:\-!#\.@]/;

STRING = /^"((?:\\.|[^"])*)"/;

MULTI_DENT = /^(?:\r?\n[^\r\n\S]*)+/;

WHITESPACE = /^[^\r\n\S]+/;

COMMENT = /^\s*\/\/[^\n]*/;

KEYWORDS = ["IF", "ELSE", "COLLECTION", "IN", "VIEW", "UNLESS"];

Lexer = (function() {
  function Lexer() {}

  Lexer.prototype.tokenize = function(code, opts) {
    var tag;

    if (opts == null) {
      opts = {};
    }
    this.code = code.replace(/^\s*/, '').replace(/\s*$/, '');
    this.line = opts.line || 0;
    this.indent = 0;
    this.indents = [];
    this.ends = [];
    this.tokens = [];
    this.i = 0;
    while (this.chunk = this.code.slice(this.i)) {
      this.i += this.identifierToken() || this.commentToken() || this.whitespaceToken() || this.lineToken() || this.stringToken() || this.literalToken();
    }
    while (tag = this.ends.pop()) {
      if (tag === 'OUTDENT') {
        this.token('OUTDENT');
      } else {
        this.error("missing " + tag);
      }
    }
    while (this.tokens[0][0] === "TERMINATOR") {
      this.tokens.shift();
    }
    while (this.tokens[this.tokens.length - 1][0] === "TERMINATOR") {
      this.tokens.pop();
    }
    return this.tokens;
  };

  Lexer.prototype.commentToken = function() {
    var match;

    if (match = COMMENT.exec(this.chunk)) {
      return match[0].length;
    } else {
      return 0;
    }
  };

  Lexer.prototype.whitespaceToken = function() {
    var match;

    if (match = WHITESPACE.exec(this.chunk)) {
      this.token('WHITESPACE', match[0].length);
      return match[0].length;
    } else {
      return 0;
    }
  };

  Lexer.prototype.token = function(tag, value) {
    return this.tokens.push([tag, value, this.line]);
  };

  Lexer.prototype.identifierToken = function() {
    var match, name;

    if (match = IDENTIFIER.exec(this.chunk)) {
      name = match[0].toUpperCase();
      if (name === "ELSE" && this.last(this.tokens, 2)[0] === "TERMINATOR") {
        this.tokens.splice(this.tokens.length - 3, 1);
      }
      if (__indexOf.call(KEYWORDS, name) >= 0) {
        this.token(name, match[0]);
      } else {
        this.token('IDENTIFIER', match[0]);
      }
      return match[0].length;
    } else {
      return 0;
    }
  };

  Lexer.prototype.stringToken = function() {
    var match;

    if (match = STRING.exec(this.chunk)) {
      this.token('STRING_LITERAL', match[1]);
      return match[0].length;
    } else {
      return 0;
    }
  };

  Lexer.prototype.lineToken = function() {
    var diff, indent, match, prev, size;

    if (!(match = MULTI_DENT.exec(this.chunk))) {
      return 0;
    }
    indent = match[0];
    this.line += this.count(indent, '\n');
    prev = this.last(this.tokens, 1);
    size = indent.length - 1 - indent.lastIndexOf('\n');
    diff = size - this.indent;
    if (size === this.indent) {
      this.newlineToken();
    } else if (size > this.indent) {
      this.token('INDENT');
      this.indents.push(diff);
      this.ends.push('OUTDENT');
    } else {
      while (diff < 0) {
        this.ends.pop();
        diff += this.indents.pop();
        this.token('OUTDENT');
      }
      this.token('TERMINATOR', '\n');
    }
    this.indent = size;
    return indent.length;
  };

  Lexer.prototype.literalToken = function() {
    var match;

    if (match = LITERAL.exec(this.chunk)) {
      this.token(match[0]);
      return 1;
    } else {
      return this.error("Unexpected token '" + (this.chunk.charAt(0)) + "'");
    }
  };

  Lexer.prototype.newlineToken = function() {
    if (this.tag() !== 'TERMINATOR') {
      return this.token('TERMINATOR', '\n');
    }
  };

  Lexer.prototype.tag = function(index, tag) {
    var tok;

    return (tok = this.last(this.tokens, index)) && (tag ? tok[0] = tag : tok[0]);
  };

  Lexer.prototype.value = function(index, val) {
    var tok;

    return (tok = this.last(this.tokens, index)) && (val ? tok[1] = val : tok[1]);
  };

  Lexer.prototype.error = function(message) {
    var chunk;

    chunk = this.code.slice(Math.max(0, this.i - 10), Math.min(this.code.length, this.i + 10));
    throw SyntaxError("" + message + " on line " + (this.line + 1) + " near " + (JSON.stringify(chunk)));
  };

  Lexer.prototype.count = function(string, substr) {
    var num, pos;

    num = pos = 0;
    if (!substr.length) {
      return 1 / 0;
    }
    while (pos = 1 + string.indexOf(substr, pos)) {
      num++;
    }
    return num;
  };

  Lexer.prototype.last = function(array, back) {
    return array[array.length - (back || 0) - 1];
  };

  return Lexer;

})();

Node = (function() {
  defineEvent(Node.prototype, "load", {
    async: false
  });

  defineEvent(Node.prototype, "unload", {
    async: false
  });

  function Node(ast, element) {
    this.ast = ast;
    this.element = element;
    this.children = new Collection([]);
    this.boundClasses = new Collection([]);
    this.boundEvents = new Collection([]);
  }

  Node.prototype.append = function(inside) {
    return inside.appendChild(this.element);
  };

  Node.prototype.insertAfter = function(after) {
    return after.parentNode.insertBefore(this.element, after.nextSibling);
  };

  Node.prototype.remove = function() {
    var _ref;

    this.detach();
    return (_ref = this.element.parentNode) != null ? _ref.removeChild(this.element) : void 0;
  };

  def(Node.prototype, "lastElement", {
    configurable: true,
    get: function() {
      return this.element;
    }
  });

  Node.prototype.nodes = function() {
    return this.children;
  };

  Node.prototype.bindEvent = function(event, fun) {
    if (event) {
      this.boundEvents.push({
        event: event,
        fun: fun
      });
      return event.bind(fun);
    }
  };

  Node.prototype.unbindEvent = function(event, fun) {
    if (event) {
      this.boundEvents["delete"](fun);
      return event.unbind(fun);
    }
  };

  Node.prototype.detach = function() {
    var event, fun, node, _i, _j, _len, _len1, _ref, _ref1, _ref2, _results;

    this.unload.trigger();
    _ref = this.nodes();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      node = _ref[_i];
      node.detach();
    }
    if (this.boundEvents) {
      _ref1 = this.boundEvents;
      _results = [];
      for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
        _ref2 = _ref1[_j], event = _ref2.event, fun = _ref2.fun;
        _results.push(event.unbind(fun));
      }
      return _results;
    }
  };

  Node.prototype.updateClass = function() {
    var classes;

    classes = this.ast.classes;
    if (this.attributeClasses) {
      classes = classes.concat(this.attributeClasses);
    }
    if (this.boundClasses.length) {
      classes = classes.concat(this.boundClasses.toArray());
    }
    classes.sort();
    if (classes.length) {
      return assignUnlessEqual(this.element, "className", classes.join(' '));
    } else {
      return this.element.removeAttribute("class");
    }
  };

  return Node;

})();

DynamicNode = (function(_super) {
  __extends(DynamicNode, _super);

  function DynamicNode(ast) {
    this.ast = ast;
    this.anchor = Serenade.document.createTextNode('');
    this.nodeSets = new Collection([]);
    this.boundEvents = new Collection([]);
  }

  DynamicNode.prototype.nodes = function() {
    var node, nodes, set, _i, _j, _len, _len1, _ref;

    nodes = [];
    _ref = this.nodeSets;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      set = _ref[_i];
      for (_j = 0, _len1 = set.length; _j < _len1; _j++) {
        node = set[_j];
        nodes.push(node);
      }
    }
    return nodes;
  };

  DynamicNode.prototype.rebuild = function() {
    var last, node, _i, _len, _ref, _results;

    if (this.anchor.parentNode) {
      last = this.anchor;
      _ref = this.nodes();
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        node = _ref[_i];
        node.insertAfter(last);
        _results.push(last = node.lastElement);
      }
      return _results;
    }
  };

  DynamicNode.prototype.replace = function(sets) {
    var set;

    this.clear();
    this.nodeSets.update((function() {
      var _i, _len, _results;

      _results = [];
      for (_i = 0, _len = sets.length; _i < _len; _i++) {
        set = sets[_i];
        _results.push(new Collection(set));
      }
      return _results;
    })());
    return this.rebuild();
  };

  DynamicNode.prototype.deleteNodeSet = function(index) {
    var node, _i, _len, _ref;

    _ref = this.nodeSets[index];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      node = _ref[_i];
      node.remove();
    }
    return this.nodeSets.deleteAt(index);
  };

  DynamicNode.prototype.insertNodeSet = function(index, nodes) {
    var last, node, _i, _len, _ref, _ref1;

    last = ((_ref = this.nodeSets[index - 1]) != null ? (_ref1 = _ref.last) != null ? _ref1.lastElement : void 0 : void 0) || this.anchor;
    for (_i = 0, _len = nodes.length; _i < _len; _i++) {
      node = nodes[_i];
      if (this.anchor.parentNode) {
        node.insertAfter(last);
      }
      last = node.lastElement;
    }
    return this.nodeSets.insertAt(index, new Collection(nodes));
  };

  DynamicNode.prototype.swapNodeSet = function(fromIndex, toIndex) {
    var last, node, _i, _j, _len, _len1, _ref, _ref1, _ref2, _ref3, _ref4, _ref5, _ref6;

    last = ((_ref = this.nodeSets[fromIndex - 1]) != null ? (_ref1 = _ref.last) != null ? _ref1.lastElement : void 0 : void 0) || this.anchor;
    _ref2 = this.nodeSets[toIndex];
    for (_i = 0, _len = _ref2.length; _i < _len; _i++) {
      node = _ref2[_i];
      if (this.anchor.parentNode) {
        node.insertAfter(last);
      }
      last = node.lastElement;
    }
    last = ((_ref3 = this.nodeSets[toIndex - 1]) != null ? (_ref4 = _ref3.last) != null ? _ref4.lastElement : void 0 : void 0) || this.anchor;
    _ref5 = this.nodeSets[fromIndex];
    for (_j = 0, _len1 = _ref5.length; _j < _len1; _j++) {
      node = _ref5[_j];
      if (this.anchor.parentNode) {
        node.insertAfter(last);
      }
      last = node.lastElement;
    }
    return _ref6 = [this.nodeSets[toIndex], this.nodeSets[fromIndex]], this.nodeSets[fromIndex] = _ref6[0], this.nodeSets[toIndex] = _ref6[1], _ref6;
  };

  DynamicNode.prototype.clear = function() {
    var node, _i, _len, _ref;

    _ref = this.nodes();
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      node = _ref[_i];
      node.remove();
    }
    return this.nodeSets.update([]);
  };

  DynamicNode.prototype.remove = function() {
    this.detach();
    this.clear();
    if (this.anchor.parentNode) {
      return this.anchor.parentNode.removeChild(this.anchor);
    }
  };

  DynamicNode.prototype.append = function(inside) {
    inside.appendChild(this.anchor);
    return this.rebuild();
  };

  DynamicNode.prototype.insertAfter = function(after) {
    after.parentNode.insertBefore(this.anchor, after.nextSibling);
    return this.rebuild();
  };

  def(DynamicNode.prototype, "lastElement", {
    configurable: true,
    get: function() {
      var _ref, _ref1;

      return ((_ref = this.nodeSets.last) != null ? (_ref1 = _ref.last) != null ? _ref1.lastElement : void 0 : void 0) || this.anchor;
    }
  });

  return DynamicNode;

})(Node);

getValue = function(ast, model) {
  if (ast.bound && ast.value) {
    return format(model, ast.value);
  } else if (ast.value != null) {
    return ast.value;
  } else {
    return model;
  }
};

Property = {
  style: function(ast, node, model, controller) {
    var update;

    update = function() {
      return assignUnlessEqual(node.element.style, ast.name, getValue(ast, model));
    };
    update();
    if (ast.bound) {
      return node.bindEvent(model["" + ast.value + "_property"], update);
    }
  },
  event: function(ast, node, model, controller) {
    return node.element.addEventListener(ast.name, function(e) {
      if (ast.preventDefault) {
        e.preventDefault();
      }
      return controller[ast.value](node.element, model, e);
    });
  },
  "class": function(ast, node, model, controller) {
    var update;

    update = function() {
      if (model[ast.value]) {
        if (!node.boundClasses.includes(ast.name)) {
          node.boundClasses.push(ast.name);
        }
      } else {
        node.boundClasses["delete"](ast.name);
      }
      return node.updateClass();
    };
    update();
    return node.bindEvent(model["" + ast.value + "_property"], update);
  },
  binding: function(ast, node, model, controller) {
    var domUpdated, element, handler, modelUpdated, _ref;

    element = node.element;
    ((_ref = node.ast.name) === "input" || _ref === "textarea" || _ref === "select") || (function() {
      throw SyntaxError("invalid node type " + node.ast.name + " for two way binding");
    })();
    ast.value || (function() {
      throw SyntaxError("cannot bind to whole model, please specify an attribute to bind to");
    })();
    domUpdated = function() {
      return model[ast.value] = element.type === "checkbox" ? element.checked : element.type === "radio" ? element.checked ? element.getAttribute("value") : void 0 : element.value;
    };
    modelUpdated = function() {
      var val;

      val = model[ast.value];
      if (element.type === "checkbox") {
        return element.checked = !!val;
      } else if (element.type === "radio") {
        if (val === element.getAttribute("value")) {
          return element.checked = true;
        }
      } else {
        if (val === void 0) {
          val = "";
        }
        return assignUnlessEqual(element, "value", val);
      }
    };
    modelUpdated();
    node.bindEvent(model["" + ast.value + "_property"], modelUpdated);
    if (ast.name === "binding") {
      handler = function(e) {
        if (element.form === (e.target || e.srcElement)) {
          return domUpdated();
        }
      };
      Serenade.document.addEventListener("submit", handler, true);
      return node.unload.bind(function() {
        return Serenade.document.removeEventListener("submit", handler, true);
      });
    } else {
      return element.addEventListener(ast.name, domUpdated);
    }
  },
  attribute: function(ast, node, model, controller) {
    var element, update;

    if (ast.name === "binding") {
      return Property.binding(ast, node, model, controller);
    }
    element = node.element;
    update = function() {
      var value;

      value = getValue(ast, model);
      if (ast.name === 'value') {
        return assignUnlessEqual(element, "value", value || '');
      } else if (node.ast.name === 'input' && ast.name === 'checked') {
        return assignUnlessEqual(element, "checked", !!value);
      } else if (ast.name === 'class') {
        node.attributeClasses = value;
        return node.updateClass();
      } else if (value === void 0) {
        if (element.hasAttribute(ast.name)) {
          return element.removeAttribute(ast.name);
        }
      } else {
        if (value === 0) {
          value = "0";
        }
        if (element.getAttribute(ast.name) !== value) {
          return element.setAttribute(ast.name, value);
        }
      }
    };
    if (ast.bound) {
      node.bindEvent(model["" + ast.value + "_property"], update);
    }
    return update();
  },
  on: function(ast, node, model, controller) {
    var _ref;

    if ((_ref = ast.name) === "load" || _ref === "unload") {
      return node[ast.name].bind(function() {
        return controller[ast.value](node.element, model);
      });
    } else {
      throw new SyntaxError("unkown lifecycle event '" + ast.name + "'");
    }
  }
};

Compile = {
  element: function(ast, model, controller) {
    var action, child, element, node, property, _i, _j, _len, _len1, _ref, _ref1, _ref2;

    element = Serenade.document.createElement(ast.name);
    node = new Node(ast, element);
    if (ast.id) {
      element.setAttribute('id', ast.id);
    }
    if ((_ref = ast.classes) != null ? _ref.length : void 0) {
      element.setAttribute('class', ast.classes.join(' '));
    }
    node.children = compile(ast.children, model, controller);
    _ref1 = node.children;
    for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
      child = _ref1[_i];
      child.append(element);
    }
    _ref2 = ast.properties;
    for (_j = 0, _len1 = _ref2.length; _j < _len1; _j++) {
      property = _ref2[_j];
      action = Property[property.scope];
      if (action) {
        action(property, node, model, controller);
      } else {
        throw SyntaxError("" + property.scope + " is not a valid scope");
      }
    }
    node.load.trigger();
    return node;
  },
  view: function(ast, model, parent) {
    var compileView, controller, skipCallback;

    controller = Serenade.controllers[ast.argument];
    if (!controller) {
      skipCallback = true;
      controller = parent;
    }
    compileView = function(dynamic, before, after) {
      var view;

      view = Serenade.views[after].render(model, controller, parent, skipCallback);
      dynamic.replace([view.nodes]);
      return dynamic;
    };
    if (ast.bound) {
      return this.bound(ast, model, controller, compileView);
    } else {
      return compileView(new DynamicNode(ast), void 0, ast.argument);
    }
  },
  helper: function(ast, model, controller) {
    var argument, context, dynamic, helperFunction, renderBlock, update, _i, _len, _ref;

    dynamic = new DynamicNode(ast);
    renderBlock = function(model, controller) {
      if (model == null) {
        model = model;
      }
      if (controller == null) {
        controller = controller;
      }
      return new View(null, ast.children).render(model, controller);
    };
    helperFunction = Serenade.Helpers[ast.command] || (function() {
      throw SyntaxError("no helper " + ast.command + " defined");
    })();
    context = {
      model: model,
      controller: controller,
      render: renderBlock
    };
    update = function() {
      var args;

      args = ast["arguments"].map(function(a) {
        if (a.bound) {
          return model[a.value];
        } else {
          return a.value;
        }
      });
      return dynamic.replace([normalize(ast, helperFunction.apply(context, args))]);
    };
    _ref = ast["arguments"];
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      argument = _ref[_i];
      if (argument.bound === true) {
        dynamic.bindEvent(model["" + argument.value + "_property"], update);
      }
    }
    update();
    return dynamic;
  },
  text: function(ast, model, controller) {
    var getText, node, textNode;

    getText = function() {
      var value;

      value = getValue(ast, model);
      if (value === 0) {
        value = "0";
      }
      return value || "";
    };
    textNode = Serenade.document.createTextNode(getText());
    node = new Node(ast, textNode);
    if (ast.bound) {
      node.bindEvent(model["" + ast.value + "_property"], function() {
        return assignUnlessEqual(textNode, "nodeValue", getText());
      });
    }
    return node;
  },
  collection: function(ast, model, controller) {
    var compileItem, dynamic, renderedCollection, update, updateCollection;

    dynamic = null;
    compileItem = function(item) {
      return compile(ast.children, item, controller);
    };
    renderedCollection = [];
    updateCollection = function(_, after) {
      var operation, _i, _len, _ref;

      _ref = Transform(renderedCollection, after);
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        operation = _ref[_i];
        switch (operation.type) {
          case "insert":
            dynamic.insertNodeSet(operation.index, compileItem(operation.value));
            break;
          case "remove":
            dynamic.deleteNodeSet(operation.index);
            break;
          case "swap":
            dynamic.swapNodeSet(operation.index, operation["with"]);
        }
      }
      return renderedCollection = (after != null ? after.map(function(a) {
        return a;
      }) : void 0) || [];
    };
    update = function(dyn, before, after) {
      dynamic = dyn;
      dynamic.unbindEvent(before != null ? before.change : void 0, updateCollection);
      dynamic.bindEvent(after != null ? after.change : void 0, updateCollection);
      return updateCollection(before, after);
    };
    return this.bound(ast, model, controller, update);
  },
  "in": function(ast, model, controller) {
    return this.bound(ast, model, controller, function(dynamic, _, value) {
      if (value) {
        return dynamic.replace([compile(ast.children, value, controller)]);
      } else {
        return dynamic.clear();
      }
    });
  },
  "if": function(ast, model, controller) {
    return this.bound(ast, model, controller, function(dynamic, _, value) {
      if (value) {
        return dynamic.replace([compile(ast.children, model, controller)]);
      } else if (ast["else"]) {
        return dynamic.replace([compile(ast["else"].children, model, controller)]);
      } else {
        return dynamic.clear();
      }
    });
  },
  unless: function(ast, model, controller) {
    return this.bound(ast, model, controller, function(dynamic, _, value) {
      var nodes;

      if (value) {
        return dynamic.clear();
      } else {
        nodes = compile(ast.children, model, controller);
        return dynamic.replace([nodes]);
      }
    });
  },
  bound: function(ast, model, controller, callback) {
    var dynamic, update;

    dynamic = new DynamicNode(ast);
    update = function(before, after) {
      if (before !== after) {
        return callback(dynamic, before, after);
      }
    };
    update({}, model[ast.argument]);
    dynamic.bindEvent(model["" + ast.argument + "_property"], update);
    return dynamic;
  }
};

normalize = function(ast, val) {
  var reduction;

  if (!val) {
    return [];
  }
  reduction = function(aggregate, element) {
    var child, div, _i, _j, _len, _len1, _ref, _ref1;

    if (typeof element === "string") {
      div = Serenade.document.createElement("div");
      div.innerHTML = element;
      _ref = div.childNodes;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        child = _ref[_i];
        aggregate.push(new Node(ast, child));
      }
    } else if (element.nodeName === "#document-fragment") {
      if (element.nodes) {
        aggregate = aggregate.concat(element.nodes);
      } else {
        _ref1 = element.childNodes;
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          child = _ref1[_j];
          aggregate.push(new Node(ast, child));
        }
      }
    } else {
      aggregate.push(new Node(ast, element));
    }
    return aggregate;
  };
  return [].concat(val).reduce(reduction, []);
};

compile = function(asts, model, controller) {
  var ast, _i, _len, _results;

  _results = [];
  for (_i = 0, _len = asts.length; _i < _len; _i++) {
    ast = asts[_i];
    _results.push(Compile[ast.type](ast, model, controller));
  }
  return _results;
};

parser.lexer = {
  lex: function() {
    var tag, _ref;

    _ref = this.tokens[this.pos++] || [''], tag = _ref[0], this.yytext = _ref[1], this.yylineno = _ref[2];
    return tag;
  },
  setInput: function(tokens) {
    this.tokens = tokens;
    return this.pos = 0;
  },
  upcomingInput: function() {
    return "";
  }
};

View = (function() {
  function View(name, view) {
    this.name = name;
    this.view = view;
  }

  View.prototype.parse = function() {
    var e;

    if (typeof this.view === 'string') {
      try {
        return this.view = parser.parse(new Lexer().tokenize(this.view));
      } catch (_error) {
        e = _error;
        if (this.name) {
          e.message = "In view '" + this.name + "': " + e.message;
        }
        throw e;
      }
    } else {
      return this.view;
    }
  };

  View.prototype.render = function(model, controller, parent, skipCallback) {
    var fragment, node, nodes, _i, _len;

    controller || (controller = Serenade.controllers[this.name] || {});
    if (typeof controller === "function") {
      controller = new controller(model, parent);
    }
    nodes = compile(this.parse(), model, controller);
    if (!skipCallback) {
      if (typeof controller.loaded === "function") {
        controller.loaded.apply(controller, __slice.call(nodes.map(function(node) {
          return node.element;
        })).concat([model]));
      }
    }
    fragment = Serenade.document.createDocumentFragment();
    for (_i = 0, _len = nodes.length; _i < _len; _i++) {
      node = nodes[_i];
      node.append(fragment);
    }
    fragment.nodes = nodes;
    fragment.remove = function() {
      var _j, _len1, _ref, _results;

      _ref = this.nodes;
      _results = [];
      for (_j = 0, _len1 = _ref.length; _j < _len1; _j++) {
        node = _ref[_j];
        _results.push(node.remove());
      }
      return _results;
    };
    return fragment;
  };

  return View;

})();

Serenade = function(wrapped) {
  var key, object, value;

  object = Object.create(wrapped);
  for (key in wrapped) {
    value = wrapped[key];
    defineProperty(object, key, {
      value: value
    });
  }
  return object;
};

extend(Serenade, {
  VERSION: '0.5.0',
  views: {},
  controllers: {},
  document: typeof window !== "undefined" && window !== null ? window.document : void 0,
  format: format,
  defineProperty: defineProperty,
  defineEvent: defineEvent,
  view: function(nameOrTemplate, template) {
    if (template) {
      return this.views[nameOrTemplate] = new View(nameOrTemplate, template);
    } else {
      return new View(void 0, nameOrTemplate);
    }
  },
  render: function(name, model, controller, parent, skipCallback) {
    return this.views[name].render(model, controller, parent, skipCallback);
  },
  controller: function(name, klass) {
    return this.controllers[name] = klass;
  },
  clearIdentityMap: function() {
    return Cache._identityMap = {};
  },
  clearCache: function() {
    return Serenade.clearIdentityMap();
  },
  unregisterAll: function() {
    Serenade.views = {};
    return Serenade.controllers = {};
  },
  Model: Model,
  Collection: Collection,
  Cache: Cache,
  View: View,
  Helpers: {}
});

def(Serenade, "async", {
  get: function() {
    return settings.async;
  },
  set: function(value) {
    return settings.async = value;
  }
});
;
  root.Serenade = Serenade;
}(this));