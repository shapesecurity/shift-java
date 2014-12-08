/**
 * Serenade.js JavaScript Framework v0.2.0
 * http://github.com/elabs/serenade.js
 *
 * Copyright 2011, Jonas Nicklas, Elabs AB
 * Released under the MIT License
 */
(function(root) {
  var Serenade = function() {
    function require(path){ return require[path]; }
    require['./events'] = new function() {
  var exports = this;
  (function() {
  var __slice = [].slice;

  exports.Events = {
    bind: function(ev, callback) {
      var calls, evs, name, _i, _len;
      evs = ev.split(' ');
      calls = this.hasOwnProperty('_callbacks') && this._callbacks || (this._callbacks = {});
      for (_i = 0, _len = evs.length; _i < _len; _i++) {
        name = evs[_i];
        calls[name] || (calls[name] = []);
        calls[name].push(callback);
      }
      return this;
    },
    one: function(ev, callback) {
      return this.bind(ev, function() {
        this.unbind(ev, arguments.callee);
        return callback.apply(this, arguments);
      });
    },
    trigger: function() {
      var args, callback, ev, list, _i, _len, _ref;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      ev = args.shift();
      list = this.hasOwnProperty('_callbacks') && ((_ref = this._callbacks) != null ? _ref[ev] : void 0);
      if (!list) {
        return false;
      }
      for (_i = 0, _len = list.length; _i < _len; _i++) {
        callback = list[_i];
        callback.apply(this, args);
      }
      return true;
    },
    unbind: function(ev, callback) {
      var cb, i, list, _i, _len, _ref;
      if (!ev) {
        this._callbacks = {};
        return this;
      }
      list = (_ref = this._callbacks) != null ? _ref[ev] : void 0;
      if (!list) {
        return this;
      }
      if (!callback) {
        delete this._callbacks[ev];
        return this;
      }
      for (i = _i = 0, _len = list.length; _i < _len; i = ++_i) {
        cb = list[i];
        if (!(cb === callback)) {
          continue;
        }
        list = list.slice();
        list.splice(i, 1);
        this._callbacks[ev] = list;
        break;
      }
      return this;
    }
  };

}).call(this);

};require['./helpers'] = new function() {
  var exports = this;
  (function() {
  var Helpers,
    __hasProp = {}.hasOwnProperty;

  Helpers = {
    extend: function(target, source) {
      var key, value, _results;
      _results = [];
      for (key in source) {
        if (!__hasProp.call(source, key)) continue;
        value = source[key];
        _results.push(target[key] = value);
      }
      return _results;
    },
    get: function(model, value, format) {
      if (typeof (model != null ? model.get : void 0) === "function") {
        return model.get(value, format);
      } else {
        return model != null ? model[value] : void 0;
      }
    },
    set: function(model, key, value) {
      if (model != null ? model.set : void 0) {
        return model.set(key, value);
      } else {
        return model[key] = value;
      }
    },
    isArray: function(object) {
      return Object.prototype.toString.call(object) === "[object Array]";
    },
    pairToObject: function(one, two) {
      var temp;
      temp = {};
      temp[one] = two;
      return temp;
    },
    serializeObject: function(object) {
      var item, _i, _len, _results;
      if (object && typeof object.serialize === 'function') {
        return object.serialize();
      } else if (Helpers.isArray(object)) {
        _results = [];
        for (_i = 0, _len = object.length; _i < _len; _i++) {
          item = object[_i];
          _results.push(Helpers.serializeObject(item));
        }
        return _results;
      } else {
        return object;
      }
    },
    getFunctionName: function(fun) {
      var name, _ref, _ref1;
      name = fun.modelName;
      name || (name = fun.name);
      name || (name = (_ref = fun.toString().match(/\[object (.+?)\]/)) != null ? _ref[1] : void 0);
      name || (name = (_ref1 = fun.toString().match(/function (.+?)\(\)/)) != null ? _ref1[1] : void 0);
      return name;
    },
    preventDefault: function(event) {
      if (event.preventDefault) {
        return event.preventDefault();
      } else {
        return event.returnValue = false;
      }
    }
  };

  Helpers.extend(exports, Helpers);

}).call(this);

};require['./cache'] = new function() {
  var exports = this;
  (function() {
  var Cache, getFunctionName, serializeObject, _ref;

  _ref = require('./helpers'), serializeObject = _ref.serializeObject, getFunctionName = _ref.getFunctionName;

  Cache = {
    _storage: typeof window !== "undefined" && window !== null ? window.localStorage : void 0,
    _identityMap: {},
    get: function(ctor, id) {
      var name, _ref1;
      name = getFunctionName(ctor);
      if (name && id) {
        return ((_ref1 = this._identityMap[name]) != null ? _ref1[id] : void 0) || this.retrieve(ctor, id);
      }
    },
    set: function(ctor, id, obj) {
      var name, _base;
      name = getFunctionName(ctor);
      if (name && id) {
        (_base = this._identityMap)[name] || (_base[name] = {});
        return this._identityMap[name][id] = obj;
      }
    },
    store: function(ctor, id, obj) {
      var name;
      name = getFunctionName(ctor);
      if (name && id && (typeof JSON !== "undefined" && JSON !== null)) {
        return this._storage.setItem("" + name + "_" + id, JSON.stringify(serializeObject(obj)));
      }
    },
    retrieve: function(ctor, id) {
      var data, name;
      name = getFunctionName(ctor);
      if (name && id && ctor.localStorage && (typeof JSON !== "undefined" && JSON !== null)) {
        data = this._storage.getItem("" + name + "_" + id);
        if (data) {
          return new ctor(JSON.parse(data), true);
        }
      }
    }
  };

  exports.Cache = Cache;

}).call(this);

};require['./collection'] = new function() {
  var exports = this;
  (function() {
  var Events, extend, get, getLength, isArrayIndex, serializeObject, _ref,
    __slice = [].slice;

  Events = require('./events').Events;

  _ref = require('./helpers'), extend = _ref.extend, serializeObject = _ref.serializeObject, get = _ref.get;

  isArrayIndex = function(index) {
    return index.match(/^\d+$/);
  };

  getLength = function(arr) {
    var index, indices, val;
    indices = (function() {
      var _results;
      _results = [];
      for (index in arr) {
        val = arr[index];
        if (isArrayIndex(index)) {
          _results.push(parseInt(index, 10));
        }
      }
      return _results;
    })();
    if (indices.length) {
      return Math.max.apply(Math, indices) + 1;
    } else {
      return 0;
    }
  };

  exports.Collection = (function() {

    extend(Collection.prototype, Events);

    function Collection(list) {
      var index, val, _i, _len;
      for (index = _i = 0, _len = list.length; _i < _len; index = ++_i) {
        val = list[index];
        this[index] = val;
      }
      this.length = getLength(this);
    }

    Collection.prototype.get = function(index) {
      return this[index];
    };

    Collection.prototype.set = function(index, value) {
      this[index] = value;
      this.length = getLength(this);
      this.trigger("change:" + index, value);
      this.trigger("set", index, value);
      this.trigger("change", this);
      return value;
    };

    Collection.prototype.push = function(element) {
      this[this.length] = element;
      this.length = getLength(this);
      this.trigger("add", element);
      this.trigger("change", this);
      return element;
    };

    Collection.prototype.pop = function() {
      return this.deleteAt(this.length - 1);
    };

    Collection.prototype.unshift = function(item) {
      return this.insertAt(0, item);
    };

    Collection.prototype.shift = function() {
      return this.deleteAt(0);
    };

    Collection.prototype.update = function(list) {
      var index, old, val, _, _i, _len;
      old = this.clone();
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
      this.length = getLength(this);
      this.trigger("update", old, this);
      this.trigger("change", this);
      return list;
    };

    Collection.prototype.splice = function() {
      var deleteCount, deleted, list, old, start;
      start = arguments[0], deleteCount = arguments[1], list = 3 <= arguments.length ? __slice.call(arguments, 2) : [];
      old = this.clone();
      deleted = Array.prototype.splice.apply(this, [start, deleteCount].concat(__slice.call(list)));
      this.length = getLength(this);
      this.trigger("update", old, this);
      this.trigger("change", this);
      return new Collection(deleted);
    };

    Collection.prototype.sort = function(fun) {
      var old;
      old = this.clone();
      Array.prototype.sort.call(this, fun);
      this.trigger("update", old, this);
      this.trigger("change", this);
      return this;
    };

    Collection.prototype.sortBy = function(attribute) {
      return this.sort(function(a, b) {
        if (get(a, attribute) < get(b, attribute)) {
          return -1;
        } else {
          return 1;
        }
      });
    };

    Collection.prototype.reverse = function() {
      var old;
      old = this.clone();
      Array.prototype.reverse.call(this);
      this.trigger("update", old, this);
      this.trigger("change", this);
      return this;
    };

    Collection.prototype.forEach = function(fun) {
      if (typeof Array.prototype.forEach === 'function') {
        return Array.prototype.forEach.call(this, fun);
      } else {
        this.map(fun);
        return void 0;
      }
    };

    Collection.prototype.map = function(fun) {
      var element, index;
      if (typeof Array.prototype.map === 'function') {
        return new Collection(Array.prototype.map.call(this, fun));
      } else {
        return new Collection((function() {
          var _i, _len, _results;
          _results = [];
          for (index = _i = 0, _len = this.length; _i < _len; index = ++_i) {
            element = this[index];
            _results.push(fun(element, index));
          }
          return _results;
        }).call(this));
      }
    };

    Collection.prototype.indexOf = function(search) {
      var index, item, _i, _len;
      if (typeof Array.prototype.indexOf === "function") {
        return Array.prototype.indexOf.call(this, search);
      } else {
        for (index = _i = 0, _len = this.length; _i < _len; index = ++_i) {
          item = this[index];
          if (item === search) {
            return index;
          }
        }
        return -1;
      }
    };

    Collection.prototype.lastIndexOf = function(search) {
      var index, item, last;
      if (typeof Array.prototype.lastIndexOf === "function") {
        return Array.prototype.lastIndexOf.call(this, search);
      } else {
        last = ((function() {
          var _i, _len, _results;
          _results = [];
          for (index = _i = 0, _len = this.length; _i < _len; index = ++_i) {
            item = this[index];
            if (item === search) {
              _results.push(index);
            }
          }
          return _results;
        }).call(this)).pop();
        if (last != null) {
          return last;
        } else {
          return -1;
        }
      }
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
      this.length = getLength(this);
      this.trigger("insert", index, value);
      this.trigger("change", this);
      return value;
    };

    Collection.prototype.deleteAt = function(index) {
      var value;
      value = this[index];
      Array.prototype.splice.call(this, index, 1);
      this.length = getLength(this);
      this.trigger("delete", index, value);
      this.trigger("change", this);
      return value;
    };

    Collection.prototype["delete"] = function(item) {
      var index;
      index = this.indexOf(item);
      if (index !== -1) {
        return this.deleteAt(index);
      }
    };

    Collection.prototype.serialize = function() {
      return serializeObject(this.toArray());
    };

    Collection.prototype.filter = function(fun) {
      var item;
      if (typeof Array.prototype.filter === "function") {
        return new Collection(Array.prototype.filter.call(this, fun));
      } else {
        return new Collection((function() {
          var _i, _len, _results;
          _results = [];
          for (_i = 0, _len = this.length; _i < _len; _i++) {
            item = this[_i];
            if (fun(item)) {
              _results.push(item);
            }
          }
          return _results;
        }).call(this));
      }
    };

    Collection.prototype.join = function() {
      var args;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return Array.prototype.join.apply(this, args);
    };

    Collection.prototype.toString = function() {
      return this.toArray().toString();
    };

    Collection.prototype.toLocaleString = function() {
      return this.toArray().toLocaleString();
    };

    Collection.prototype.concat = function() {
      var args, _ref1;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return new Collection((_ref1 = this.toArray()).concat.apply(_ref1, args));
    };

    Collection.prototype.slice = function() {
      var args, _ref1;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return new Collection((_ref1 = this.toArray()).slice.apply(_ref1, args));
    };

    Collection.prototype.every = function(fun) {
      var item, _i, _len;
      if (typeof Array.prototype.every === "function") {
        return Array.prototype.every.call(this, fun);
      } else {
        for (_i = 0, _len = this.length; _i < _len; _i++) {
          item = this[_i];
          if (!fun(item)) {
            return false;
          }
        }
        return true;
      }
    };

    Collection.prototype.some = function(fun) {
      var item, _i, _len;
      if (typeof Array.prototype.some === "function") {
        return Array.prototype.some.call(this, fun);
      } else {
        for (_i = 0, _len = this.length; _i < _len; _i++) {
          item = this[_i];
          if (fun(item)) {
            return true;
          }
        }
        return false;
      }
    };

    Collection.prototype.reduce = function(fun, initial) {
      var carry, index, item, _i, _len;
      if (typeof Array.prototype.reduce === "function") {
        return Array.prototype.reduce.apply(this, arguments);
      } else {
        carry = initial ? initial : this[0];
        for (index = _i = 0, _len = this.length; _i < _len; index = ++_i) {
          item = this[index];
          if (initial || index !== 0) {
            carry = fun(carry, item, index, this);
          }
        }
        return carry;
      }
    };

    Collection.prototype.reduceRight = function(fun, initial) {
      var carry, index, item, traversed, _i, _len;
      if (typeof Array.prototype.reduceRight === "function") {
        return Array.prototype.reduceRight.apply(this, arguments);
      } else {
        traversed = this.toArray().reverse();
        carry = initial ? initial : traversed[0];
        for (index = _i = 0, _len = traversed.length; _i < _len; index = ++_i) {
          item = traversed[index];
          if (initial || index !== 0) {
            carry = fun(carry, item, traversed.length - index - 1, this);
          }
        }
        return carry;
      }
    };

    Collection.prototype.first = function() {
      return this[0];
    };

    Collection.prototype.last = function() {
      return this[this.length - 1];
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

    Collection.prototype.toJSON = function() {
      return this.serialize();
    };

    Collection.prototype.clone = function() {
      return new Collection(this.toArray());
    };

    Collection.prototype._useDefer = true;

    return Collection;

  })();

}).call(this);

};require['./association_collection'] = new function() {
  var exports = this;
  (function() {
  var AssociationCollection, Collection,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  Collection = require('./collection').Collection;

  AssociationCollection = (function(_super) {

    __extends(AssociationCollection, _super);

    function AssociationCollection(ctor, list) {
      var item;
      this.ctor = ctor;
      AssociationCollection.__super__.constructor.call(this, (function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = list.length; _i < _len; _i++) {
          item = list[_i];
          _results.push(this._convert(item));
        }
        return _results;
      }).call(this));
    }

    AssociationCollection.prototype.set = function(index, item) {
      return AssociationCollection.__super__.set.call(this, index, this._convert(item));
    };

    AssociationCollection.prototype.push = function(item) {
      return AssociationCollection.__super__.push.call(this, this._convert(item));
    };

    AssociationCollection.prototype.update = function(list) {
      var item;
      return AssociationCollection.__super__.update.call(this, (function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = list.length; _i < _len; _i++) {
          item = list[_i];
          _results.push(this._convert(item));
        }
        return _results;
      }).call(this));
    };

    AssociationCollection.prototype._convert = function(item) {
      if (item.constructor === Object && this.ctor) {
        return new (this.ctor())(item);
      } else {
        return item;
      }
    };

    return AssociationCollection;

  })(Collection);

  exports.AssociationCollection = AssociationCollection;

}).call(this);

};require['./properties'] = new function() {
  var exports = this;
  (function() {
  var AssociationCollection, Associations, Collection, Events, Properties, addDependencies, addGlobalDependencies, define, exp, extend, get, globalDependencies, pairToObject, prefix, serializeObject, triggerChangesTo, triggerGlobal, _ref,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __hasProp = {}.hasOwnProperty;

  Collection = require('./collection').Collection;

  AssociationCollection = require('./association_collection').AssociationCollection;

  Events = require('./events').Events;

  _ref = require('./helpers'), pairToObject = _ref.pairToObject, serializeObject = _ref.serializeObject, extend = _ref.extend, get = _ref.get;

  prefix = "_prop_";

  exp = /^_prop_/;

  define = Object.defineProperties;

  globalDependencies = {};

  addGlobalDependencies = function(object, dependency, names) {
    var name, subname, type, _i, _len, _ref1, _ref2, _results;
    if (!object["_glb_" + dependency]) {
      object["_glb_" + dependency] = true;
      _results = [];
      for (_i = 0, _len = names.length; _i < _len; _i++) {
        name = names[_i];
        if (name.match(/\./)) {
          type = "singular";
          _ref1 = name.split("."), name = _ref1[0], subname = _ref1[1];
        } else if (name.match(/:/)) {
          type = "collection";
          _ref2 = name.split(":"), name = _ref2[0], subname = _ref2[1];
        }
        if (subname) {
          globalDependencies[subname] || (globalDependencies[subname] = []);
          _results.push(globalDependencies[subname].push({
            object: object,
            dependency: dependency,
            subname: subname,
            name: name,
            type: type
          }));
        } else {
          _results.push(void 0);
        }
      }
      return _results;
    }
  };

  addDependencies = function(object, dependency, names) {
    var name, subname, _i, _len, _name, _ref1, _results;
    names = [].concat(names);
    _results = [];
    for (_i = 0, _len = names.length; _i < _len; _i++) {
      name = names[_i];
      if (name.match(/[:\.]/)) {
        _ref1 = name.split(/[:\.]/), name = _ref1[0], subname = _ref1[1];
      }
      object[_name = "_dep_" + name] || (object[_name] = []);
      if (object["_dep_" + name].indexOf(dependency) === -1) {
        _results.push(object["_dep_" + name].push(dependency));
      } else {
        _results.push(void 0);
      }
    }
    return _results;
  };

  triggerGlobal = function(object, names) {
    var dependency, name, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = names.length; _i < _len; _i++) {
      name = names[_i];
      if (globalDependencies[name]) {
        _results.push((function() {
          var _j, _len1, _ref1, _results1;
          _ref1 = globalDependencies[name];
          _results1 = [];
          for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
            dependency = _ref1[_j];
            if (dependency.type === "singular") {
              if (object === dependency.object.get(dependency.name)) {
                _results1.push(triggerChangesTo(dependency.object, [dependency.dependency]));
              } else {
                _results1.push(void 0);
              }
            } else if (dependency.type === "collection") {
              if (__indexOf.call(dependency.object.get(dependency.name), object) >= 0) {
                _results1.push(triggerChangesTo(dependency.object, [dependency.dependency]));
              } else {
                _results1.push(void 0);
              }
            } else {
              _results1.push(void 0);
            }
          }
          return _results1;
        })());
      } else {
        _results.push(void 0);
      }
    }
    return _results;
  };

  triggerChangesTo = function(object, names) {
    var changes, findDependencies, name, value, _i, _j, _len, _len1, _results;
    findDependencies = function(name) {
      var dependencies, dependency, _i, _len, _results;
      dependencies = object["_dep_" + name];
      if (dependencies) {
        _results = [];
        for (_i = 0, _len = dependencies.length; _i < _len; _i++) {
          dependency = dependencies[_i];
          if (names.indexOf(dependency) === -1) {
            names.push(dependency);
            _results.push(findDependencies(dependency));
          } else {
            _results.push(void 0);
          }
        }
        return _results;
      }
    };
    for (_i = 0, _len = names.length; _i < _len; _i++) {
      name = names[_i];
      findDependencies(name);
    }
    changes = {};
    for (_j = 0, _len1 = names.length; _j < _len1; _j++) {
      name = names[_j];
      changes[name] = object.get(name);
    }
    object.trigger("change", changes);
    triggerGlobal(object, names);
    _results = [];
    for (name in changes) {
      if (!__hasProp.call(changes, name)) continue;
      value = changes[name];
      _results.push(object.trigger("change:" + name, value));
    }
    return _results;
  };

  Properties = {
    property: function(name, options) {
      if (options == null) {
        options = {};
      }
      this[prefix + name] = options;
      this[prefix + name].name = name;
      if (this.hasOwnProperty(name)) {
        this.set(name, this[name]);
      }
      if (options.dependsOn) {
        addDependencies(this, name, options.dependsOn);
      }
      if (define) {
        Object.defineProperty(this, name, {
          get: function() {
            return Properties.get.call(this, name);
          },
          set: function(value) {
            return Properties.set.call(this, name, value);
          },
          configurable: true
        });
      }
      if (typeof options.serialize === 'string') {
        return this.property(options.serialize, {
          get: function() {
            return this.get(name);
          },
          set: function(v) {
            return this.set(name, v);
          },
          configurable: true
        });
      }
    },
    collection: function(name, options) {
      if (options == null) {
        options = {};
      }
      extend(options, {
        get: function() {
          var _this = this;
          if (!this.attributes[name]) {
            this.attributes[name] = new Collection([]);
            this.attributes[name].bind('change', function() {
              return triggerChangesTo(_this, [name]);
            });
          }
          return this.attributes[name];
        },
        set: function(value) {
          return this.get(name).update(value);
        }
      });
      return this.property(name, options);
    },
    set: function(attributes, value) {
      var name, names, _ref1;
      if (typeof attributes === 'string') {
        attributes = pairToObject(attributes, value);
      }
      names = [];
      for (name in attributes) {
        value = attributes[name];
        names.push(name);
        this.attributes || (this.attributes = {});
        if (!this[prefix + name]) {
          Properties.property.call(this, name);
        }
        if ((_ref1 = this[prefix + name]) != null ? _ref1.set : void 0) {
          this[prefix + name].set.call(this, value);
        } else {
          this.attributes[name] = value;
        }
      }
      return triggerChangesTo(this, names);
    },
    get: function(name, format) {
      var formatter, value, _ref1, _ref2, _ref3, _ref4;
      if ((_ref1 = this[prefix + name]) != null ? _ref1.dependsOn : void 0) {
        addGlobalDependencies(this, name, [].concat(this[prefix + name].dependsOn));
      }
      this.attributes || (this.attributes = {});
      value = ((_ref2 = this[prefix + name]) != null ? _ref2.get : void 0) ? this[prefix + name].get.call(this) : ((_ref3 = this[prefix + name]) != null ? _ref3.hasOwnProperty("default") : void 0) && !this.attributes.hasOwnProperty(name) ? this[prefix + name]["default"] : this.attributes[name];
      formatter = (_ref4 = this[prefix + name]) != null ? _ref4.format : void 0;
      if (format && typeof formatter === 'function') {
        return formatter.call(this, value);
      } else {
        return value;
      }
    },
    serialize: function() {
      var key, name, options, serialized, value, _ref1;
      serialized = {};
      for (name in this) {
        options = this[name];
        if (name.match(exp)) {
          if (typeof options.serialize === 'string') {
            serialized[options.serialize] = serializeObject(this.get(options.name));
          } else if (typeof options.serialize === 'function') {
            _ref1 = options.serialize.call(this), key = _ref1[0], value = _ref1[1];
            serialized[key] = serializeObject(value);
          } else if (options.serialize) {
            serialized[options.name] = serializeObject(this.get(options.name));
          }
        }
      }
      return serialized;
    }
  };

  extend(Properties, Events);

  Associations = {
    belongsTo: function(name, attributes) {
      if (attributes == null) {
        attributes = {};
      }
      extend(attributes, {
        set: function(model) {
          if (model.constructor === Object && attributes.as) {
            model = new (attributes.as())(model);
          }
          return this.attributes[name] = model;
        }
      });
      this.property(name, attributes);
      return this.property(name + 'Id', {
        get: function() {
          return get(this.get(name), 'id');
        },
        set: function(id) {
          return this.attributes[name] = attributes.as().find(id);
        },
        dependsOn: name,
        serialize: attributes.serializeId
      });
    },
    hasMany: function(name, attributes) {
      if (attributes == null) {
        attributes = {};
      }
      extend(attributes, {
        get: function() {
          var _this = this;
          if (!this.attributes[name]) {
            this.attributes[name] = new AssociationCollection(attributes.as, []);
            this.attributes[name].bind('change', function() {
              return triggerChangesTo(_this, [name]);
            });
          }
          return this.attributes[name];
        },
        set: function(value) {
          return this.get(name).update(value);
        }
      });
      this.property(name, attributes);
      return this.property(name + 'Ids', {
        get: function() {
          return new Collection(this.get(name)).map(function(item) {
            return get(item, 'id');
          });
        },
        set: function(ids) {
          var id, objects;
          objects = (function() {
            var _i, _len, _results;
            _results = [];
            for (_i = 0, _len = ids.length; _i < _len; _i++) {
              id = ids[_i];
              _results.push(attributes.as().find(id));
            }
            return _results;
          })();
          return this.get(name).update(objects);
        },
        dependsOn: name,
        serialize: attributes.serializeIds
      });
    }
  };

  exports.Properties = Properties;

  exports.Associations = Associations;

  exports.globalDependencies = globalDependencies;

}).call(this);

};require['./model'] = new function() {
  var exports = this;
  (function() {
  var Associations, Cache, Model, Properties, extend, _ref,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  Cache = require('./cache').Cache;

  _ref = require('./properties'), Associations = _ref.Associations, Properties = _ref.Properties;

  extend = require('./helpers').extend;

  Model = (function() {

    extend(Model.prototype, Properties);

    extend(Model.prototype, Associations);

    Model.property = function() {
      var _ref1;
      return (_ref1 = this.prototype).property.apply(_ref1, arguments);
    };

    Model.collection = function() {
      var _ref1;
      return (_ref1 = this.prototype).collection.apply(_ref1, arguments);
    };

    Model.belongsTo = function() {
      var _ref1;
      return (_ref1 = this.prototype).belongsTo.apply(_ref1, arguments);
    };

    Model.hasMany = function() {
      var _ref1;
      return (_ref1 = this.prototype).hasMany.apply(_ref1, arguments);
    };

    Model.find = function(id) {
      return Cache.get(this, id) || new this({
        id: id
      });
    };

    Model.property('id', {
      serialize: true
    });

    Model.extend = function(name, ctor) {
      var New;
      return New = (function(_super) {

        __extends(New, _super);

        New.modelName = name;

        function New() {
          New.__super__.constructor.apply(this, arguments);
          if (ctor) {
            ctor.apply(this, arguments);
          }
        }

        return New;

      })(this);
    };

    function Model(attributes, bypassCache) {
      var fromCache,
        _this = this;
      if (bypassCache == null) {
        bypassCache = false;
      }
      if (!bypassCache) {
        if (attributes != null ? attributes.id : void 0) {
          fromCache = Cache.get(this.constructor, attributes.id);
          if (fromCache) {
            fromCache.set(attributes);
            return fromCache;
          } else {
            Cache.set(this.constructor, attributes.id, this);
          }
        }
      }
      if (this.constructor.localStorage) {
        this.bind('saved', function() {
          return Cache.store(_this.constructor, _this.get('id'), _this);
        });
        if (this.constructor.localStorage !== 'save') {
          this.bind('change', function() {
            return Cache.store(_this.constructor, _this.get('id'), _this);
          });
        }
      }
      this.set(attributes);
    }

    Model.prototype.save = function() {
      return this.trigger('saved');
    };

    return Model;

  })();

  exports.Model = Model;

}).call(this);

};require['./serenade'] = new function() {
  var exports = this;
  (function() {
  var Cache, Properties, Serenade, extend, globalDependencies, _ref;

  Cache = require('./cache').Cache;

  extend = require('./helpers').extend;

  _ref = require("./properties"), Properties = _ref.Properties, globalDependencies = _ref.globalDependencies;

  Serenade = function(attributes) {
    if (this === root) {
      return new Serenade(attributes);
    }
    this.set(attributes);
    return this;
  };

  extend(Serenade.prototype, Properties);

  extend(Serenade, {
    VERSION: '0.2.0',
    _views: {},
    _controllers: {},
    document: typeof window !== "undefined" && window !== null ? window.document : void 0,
    view: function(nameOrTemplate, template) {
      var View;
      View = require('./view').View;
      if (template) {
        return this._views[nameOrTemplate] = new View(nameOrTemplate, template);
      } else {
        return new View(void 0, nameOrTemplate);
      }
    },
    render: function(name, model, controller, parent) {
      return this._views[name].render(model, controller, parent);
    },
    controller: function(name, klass) {
      return this._controllers[name] = klass;
    },
    controllerFor: function(name) {
      return this._controllers[name];
    },
    clearIdentityMap: function() {
      return Cache._identityMap = {};
    },
    clearLocalStorage: function() {
      return Cache._storage.clear();
    },
    clearCache: function() {
      var key, value, _i, _len, _results;
      Serenade.clearIdentityMap();
      Serenade.clearLocalStorage();
      _results = [];
      for (key = _i = 0, _len = globalDependencies.length; _i < _len; key = ++_i) {
        value = globalDependencies[key];
        _results.push(delete globalDependencies[key]);
      }
      return _results;
    },
    unregisterAll: function() {
      Serenade._views = {};
      return Serenade._controllers = {};
    },
    bindEvent: function(element, event, callback) {
      if (typeof element.addEventListener === 'function') {
        return element.addEventListener(event, callback, false);
      } else {
        return element.attachEvent('on' + event, callback);
      }
    },
    useJQuery: function() {
      return this.bindEvent = function(element, event, callback) {
        return jQuery(element).bind(event, callback);
      };
    },
    Events: require('./events').Events,
    Model: require('./model').Model,
    Collection: require('./collection').Collection,
    Helpers: {}
  });

  exports.Serenade = Serenade;

  exports.compile = function() {
    var document, fs, window;
    document = require("jsdom").jsdom(null, null, {});
    fs = require("fs");
    window = document.createWindow();
    Serenade.document = document;
    return function(env) {
      var element, html, model, viewName;
      model = env.model;
      viewName = env.filename.split('/').reverse()[0].replace(/\.serenade$/, '');
      Serenade.view(viewName, fs.readFileSync(env.filename).toString());
      element = Serenade.render(viewName, model, {});
      document.body.appendChild(element);
      html = document.body.innerHTML;
      if (env.doctype !== false) {
        html = "<!DOCTYPE html>\n" + html;
      }
      return html;
    };
  };

}).call(this);

};require['./lexer'] = new function() {
  var exports = this;
  (function() {
  var IDENTIFIER, KEYWORDS, LITERAL, Lexer, MULTI_DENT, STRING, WHITESPACE,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

  IDENTIFIER = /^[a-zA-Z][a-zA-Z0-9\-_]*/;

  LITERAL = /^[\[\]=\:\-!#\.@]/;

  STRING = /^"((?:\\.|[^"])*)"/;

  MULTI_DENT = /^(?:\r?\n[^\r\n\S]*)+/;

  WHITESPACE = /^[^\r\n\S]+/;

  KEYWORDS = ["IF", "COLLECTION", "IN", "VIEW", "UNLESS"];

  Lexer = (function() {

    function Lexer() {}

    Lexer.prototype.tokenize = function(code, opts) {
      var i, tag;
      if (opts == null) {
        opts = {};
      }
      this.code = code.replace(/^\s*/, '').replace(/\s*$/, '');
      this.line = opts.line || 0;
      this.indent = 0;
      this.indents = [];
      this.ends = [];
      this.tokens = [];
      i = 0;
      while (this.chunk = this.code.slice(i)) {
        i += this.identifierToken() || this.whitespaceToken() || this.lineToken() || this.stringToken() || this.literalToken();
      }
      while (tag = this.ends.pop()) {
        if (tag === 'OUTDENT') {
          this.token('OUTDENT');
        } else {
          this.error("missing " + tag);
        }
      }
      return this.tokens;
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
          if (this.last(this.ends) !== 'OUTDENT') {
            this.error('Should be an OUTDENT, yo');
          }
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
        return this.error("WUT??? is '" + (this.chunk.charAt(0)) + "'");
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
      throw SyntaxError("" + message + " on line " + (this.line + 1));
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

  exports.Lexer = Lexer;

}).call(this);

};require['./node'] = new function() {
  var exports = this;
  (function() {
  var Node, Serenade;

  Serenade = require('./serenade').Serenade;

  Node = (function() {

    function Node(ast, element) {
      this.ast = ast;
      this.element = element;
    }

    Node.prototype.append = function(inside) {
      return inside.appendChild(this.element);
    };

    Node.prototype.insertAfter = function(after) {
      return after.parentNode.insertBefore(this.element, after.nextSibling);
    };

    Node.prototype.remove = function() {
      var _ref;
      return (_ref = this.element.parentNode) != null ? _ref.removeChild(this.element) : void 0;
    };

    Node.prototype.lastElement = function() {
      return this.element;
    };

    return Node;

  })();

  exports.Node = Node;

}).call(this);

};require['./dynamic_node'] = new function() {
  var exports = this;
  (function() {
  var Collection, DynamicNode, Serenade;

  Serenade = require('./serenade').Serenade;

  Collection = require('./collection').Collection;

  DynamicNode = (function() {

    function DynamicNode(ast) {
      this.ast = ast;
      this.anchor = Serenade.document.createTextNode('');
      this.nodeSets = new Collection([]);
    }

    DynamicNode.prototype.eachNode = function(fun) {
      var node, set, _i, _len, _ref, _results;
      _ref = this.nodeSets;
      _results = [];
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        set = _ref[_i];
        _results.push((function() {
          var _j, _len1, _results1;
          _results1 = [];
          for (_j = 0, _len1 = set.length; _j < _len1; _j++) {
            node = set[_j];
            _results1.push(fun(node));
          }
          return _results1;
        })());
      }
      return _results;
    };

    DynamicNode.prototype.rebuild = function() {
      var last;
      if (this.anchor.parentNode) {
        last = this.anchor;
        return this.eachNode(function(node) {
          node.insertAfter(last);
          return last = node.lastElement();
        });
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

    DynamicNode.prototype.appendNodeSet = function(nodes) {
      return this.insertNodeSet(this.nodeSets.length, nodes);
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
      last = ((_ref = this.nodeSets[index - 1]) != null ? (_ref1 = _ref.last()) != null ? _ref1.lastElement() : void 0 : void 0) || this.anchor;
      for (_i = 0, _len = nodes.length; _i < _len; _i++) {
        node = nodes[_i];
        node.insertAfter(last);
        last = node.lastElement();
      }
      return this.nodeSets.insertAt(index, new Collection(nodes));
    };

    DynamicNode.prototype.clear = function() {
      return this.eachNode(function(node) {
        return node.remove();
      });
    };

    DynamicNode.prototype.remove = function() {
      this.clear();
      return this.anchor.parentNode.removeChild(this.anchor);
    };

    DynamicNode.prototype.append = function(inside) {
      inside.appendChild(this.anchor);
      return this.rebuild();
    };

    DynamicNode.prototype.insertAfter = function(after) {
      after.parentNode.insertBefore(this.anchor, after.nextSibling);
      return this.rebuild();
    };

    DynamicNode.prototype.lastElement = function() {
      var _ref, _ref1;
      return ((_ref = this.nodeSets.last()) != null ? (_ref1 = _ref.last()) != null ? _ref1.lastElement() : void 0 : void 0) || this.anchor;
    };

    return DynamicNode;

  })();

  exports.DynamicNode = DynamicNode;

}).call(this);

};require['./compile'] = new function() {
  var exports = this;
  (function() {
  var Collection, Compile, DynamicNode, Node, Property, Serenade, compile, get, getValue, preventDefault, set, _ref;

  Serenade = require('./serenade').Serenade;

  Collection = require('./collection').Collection;

  Node = require('./node').Node;

  DynamicNode = require('./dynamic_node').DynamicNode;

  _ref = require('./helpers'), get = _ref.get, set = _ref.set, preventDefault = _ref.preventDefault;

  getValue = function(ast, model) {
    if (ast.bound && ast.value) {
      return get(model, ast.value, true);
    } else if (ast.value) {
      return ast.value;
    } else {
      return model;
    }
  };

  Property = {
    style: function(ast, node, model, controller) {
      var update;
      update = function() {
        return node.element.style[ast.name] = getValue(ast, model);
      };
      update();
      if (ast.bound) {
        return typeof model.bind === "function" ? model.bind("change:" + ast.value, update) : void 0;
      }
    },
    event: function(ast, node, model, controller) {
      var _this = this;
      return Serenade.bindEvent(node.element, ast.name, function(e) {
        if (ast.preventDefault) {
          preventDefault(e);
        }
        return controller[ast.value](model, node.element, e);
      });
    },
    binding: function(ast, node, model, controller) {
      var domUpdated, element, modelUpdated, _ref1,
        _this = this;
      element = node.element;
      ((_ref1 = node.ast.name) === "input" || _ref1 === "textarea" || _ref1 === "select") || (function() {
        throw SyntaxError("invalid node type " + node.ast.name + " for two way binding");
      })();
      ast.value || (function() {
        throw SyntaxError("cannot bind to whole model, please specify an attribute to bind to");
      })();
      domUpdated = function() {
        if (element.type === "checkbox") {
          return set(model, ast.value, element.checked);
        } else if (element.type === "radio") {
          if (element.checked) {
            return set(model, ast.value, element.getAttribute("value"));
          }
        } else {
          return set(model, ast.value, element.value);
        }
      };
      modelUpdated = function() {
        var val;
        if (element.type === "checkbox") {
          val = get(model, ast.value);
          return element.checked = !!val;
        } else if (element.type === "radio") {
          val = get(model, ast.value);
          if (val === element.getAttribute("value")) {
            return element.checked = true;
          }
        } else {
          val = get(model, ast.value);
          if (val === void 0) {
            val = "";
          }
          return element.value = val;
        }
      };
      modelUpdated();
      if (typeof model.bind === "function") {
        model.bind("change:" + ast.value, modelUpdated);
      }
      if (ast.name === "binding") {
        return Serenade.bindEvent(Serenade.document, "submit", function(e) {
          if (element.form === (e.target || e.srcElement)) {
            return domUpdated();
          }
        });
      } else {
        return Serenade.bindEvent(element, ast.name, domUpdated);
      }
    },
    attribute: function(ast, node, model, controller) {
      var element, update;
      if (ast.name === "binding") {
        return Property.binding(ast, node, model, controller);
      }
      element = node.element;
      update = function() {
        var classes, value;
        value = getValue(ast, model);
        if (ast.name === 'value') {
          return element.value = value || '';
        } else if (node.ast.name === 'input' && ast.name === 'checked') {
          return element.checked = !!value;
        } else if (ast.name === 'class') {
          classes = node.ast.classes;
          if (value !== void 0) {
            classes = classes.concat(value);
          }
          if (classes.length) {
            return element.setAttribute(ast.name, classes.join(' '));
          } else {
            return element.removeAttribute(ast.name);
          }
        } else if (value === void 0) {
          return element.removeAttribute(ast.name);
        } else {
          if (value === 0) {
            value = "0";
          }
          return element.setAttribute(ast.name, value);
        }
      };
      if (ast.bound) {
        if (typeof model.bind === "function") {
          model.bind("change:" + ast.value, update);
        }
      }
      return update();
    }
  };

  Compile = {
    element: function(ast, model, controller) {
      var action, child, element, node, property, _i, _j, _len, _len1, _ref1, _ref2, _ref3;
      element = Serenade.document.createElement(ast.name);
      node = new Node(ast, element);
      if (ast.id) {
        element.setAttribute('id', ast.id);
      }
      if ((_ref1 = ast.classes) != null ? _ref1.length : void 0) {
        element.setAttribute('class', ast.classes.join(' '));
      }
      _ref2 = ast.properties;
      for (_i = 0, _len = _ref2.length; _i < _len; _i++) {
        property = _ref2[_i];
        action = Property[property.scope];
        if (action) {
          action(property, node, model, controller);
        } else {
          throw SyntaxError("" + property.scope + " is not a valid scope");
        }
      }
      _ref3 = ast.children;
      for (_j = 0, _len1 = _ref3.length; _j < _len1; _j++) {
        child = _ref3[_j];
        compile(child, model, controller).append(element);
      }
      return node;
    },
    view: function(ast, model, parent) {
      var controller, element;
      controller = Serenade.controllerFor(ast["arguments"][0]) || parent;
      element = Serenade.render(ast["arguments"][0], model, controller, parent);
      return new Node(ast, element);
    },
    helper: function(ast, model, controller) {
      var context, element, helperFunction, render;
      render = function(model, controller) {
        var child, fragment, node, _i, _len, _ref1;
        if (model == null) {
          model = model;
        }
        if (controller == null) {
          controller = controller;
        }
        fragment = Serenade.document.createDocumentFragment();
        _ref1 = ast.children;
        for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
          child = _ref1[_i];
          node = compile(child, model, controller);
          node.append(fragment);
        }
        return fragment;
      };
      helperFunction = Serenade.Helpers[ast.command] || (function() {
        throw SyntaxError("no helper " + ast.command + " defined");
      })();
      context = {
        render: render,
        model: model,
        controller: controller
      };
      element = helperFunction.apply(context, ast["arguments"]);
      return new Node(ast, element);
    },
    text: function(ast, model, controller) {
      var getText, textNode;
      getText = function() {
        var value;
        value = getValue(ast, model);
        if (value === 0) {
          value = "0";
        }
        return value || "";
      };
      textNode = Serenade.document.createTextNode(getText());
      if (ast.bound) {
        if (typeof model.bind === "function") {
          model.bind("change:" + ast.value, function() {
            return textNode.nodeValue = getText();
          });
        }
      }
      return new Node(ast, textNode);
    },
    collection: function(ast, model, controller) {
      var collection, compileItem, dynamic, item,
        _this = this;
      compileItem = function(item) {
        var child, _i, _len, _ref1, _results;
        _ref1 = ast.children;
        _results = [];
        for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
          child = _ref1[_i];
          _results.push(compile(child, item, controller));
        }
        return _results;
      };
      dynamic = new DynamicNode(ast);
      collection = get(model, ast["arguments"][0]);
      if (typeof collection.bind === "function") {
        collection.bind('set', function() {
          var item;
          return dynamic.replace((function() {
            var _i, _len, _results;
            _results = [];
            for (_i = 0, _len = collection.length; _i < _len; _i++) {
              item = collection[_i];
              _results.push(compileItem(item));
            }
            return _results;
          })());
        });
        collection.bind('update', function() {
          var item;
          return dynamic.replace((function() {
            var _i, _len, _results;
            _results = [];
            for (_i = 0, _len = collection.length; _i < _len; _i++) {
              item = collection[_i];
              _results.push(compileItem(item));
            }
            return _results;
          })());
        });
        collection.bind('add', function(item) {
          return dynamic.appendNodeSet(compileItem(item));
        });
        collection.bind('insert', function(index, item) {
          return dynamic.insertNodeSet(index, compileItem(item));
        });
        collection.bind('delete', function(index) {
          return dynamic.deleteNodeSet(index);
        });
      }
      dynamic.replace((function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = collection.length; _i < _len; _i++) {
          item = collection[_i];
          _results.push(compileItem(item));
        }
        return _results;
      })());
      return dynamic;
    },
    "in": function(ast, model, controller) {
      var dynamic, update;
      dynamic = new DynamicNode(ast);
      update = function() {
        var child, nodes, value;
        value = get(model, ast["arguments"][0]);
        if (value) {
          nodes = (function() {
            var _i, _len, _ref1, _results;
            _ref1 = ast.children;
            _results = [];
            for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
              child = _ref1[_i];
              _results.push(compile(child, value, controller));
            }
            return _results;
          })();
          return dynamic.replace([nodes]);
        } else {
          return dynamic.clear();
        }
      };
      update();
      if (typeof model.bind === "function") {
        model.bind("change:" + ast["arguments"][0], update);
      }
      return dynamic;
    },
    "if": function(ast, model, controller) {
      var dynamic, update;
      dynamic = new DynamicNode(ast);
      update = function() {
        var child, nodes, value;
        value = get(model, ast["arguments"][0]);
        if (value) {
          nodes = (function() {
            var _i, _len, _ref1, _results;
            _ref1 = ast.children;
            _results = [];
            for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
              child = _ref1[_i];
              _results.push(compile(child, model, controller));
            }
            return _results;
          })();
          return dynamic.replace([nodes]);
        } else {
          return dynamic.clear();
        }
      };
      update();
      if (typeof model.bind === "function") {
        model.bind("change:" + ast["arguments"][0], update);
      }
      return dynamic;
    },
    unless: function(ast, model, controller) {
      var dynamic, update;
      dynamic = new DynamicNode(ast);
      update = function() {
        var child, nodes, value;
        value = get(model, ast["arguments"][0]);
        if (value) {
          return dynamic.clear();
        } else {
          nodes = (function() {
            var _i, _len, _ref1, _results;
            _ref1 = ast.children;
            _results = [];
            for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
              child = _ref1[_i];
              _results.push(compile(child, model, controller));
            }
            return _results;
          })();
          return dynamic.replace([nodes]);
        }
      };
      update();
      if (typeof model.bind === "function") {
        model.bind("change:" + ast["arguments"][0], update);
      }
      return dynamic;
    }
  };

  compile = function(ast, model, controller) {
    var action;
    action = Compile[ast.type];
    if (action) {
      return action(ast, model, controller);
    } else {
      throw SyntaxError("unknown type '" + ast.type + "'");
    }
  };

  exports.compile = compile;

}).call(this);

};require['./parser'] = new function() {
  var exports = this;
  /* Jison generated parser */
var parser = (function(){
var parser = {trace: function trace() { },
yy: {},
symbols_: {"error":2,"Root":3,"Element":4,"ElementIdentifier":5,"AnyIdentifier":6,"#":7,".":8,"[":9,"]":10,"PropertyList":11,"WHITESPACE":12,"Text":13,"INDENT":14,"ChildList":15,"OUTDENT":16,"TextList":17,"Bound":18,"STRING_LITERAL":19,"Child":20,"TERMINATOR":21,"Instruction":22,"Property":23,"=":24,"!":25,":":26,"-":27,"VIEW":28,"COLLECTION":29,"IF":30,"UNLESS":31,"IN":32,"IDENTIFIER":33,"@":34,"$accept":0,"$end":1},
terminals_: {2:"error",7:"#",8:".",9:"[",10:"]",12:"WHITESPACE",14:"INDENT",16:"OUTDENT",19:"STRING_LITERAL",21:"TERMINATOR",24:"=",25:"!",26:":",27:"-",28:"VIEW",29:"COLLECTION",30:"IF",31:"UNLESS",32:"IN",33:"IDENTIFIER",34:"@"},
productions_: [0,[3,0],[3,1],[5,1],[5,3],[5,2],[5,2],[5,3],[4,1],[4,3],[4,4],[4,3],[4,4],[17,1],[17,3],[13,1],[13,1],[15,1],[15,3],[20,1],[20,1],[20,1],[11,1],[11,3],[23,3],[23,3],[23,4],[23,4],[23,3],[23,3],[22,3],[22,3],[22,3],[22,3],[22,3],[22,3],[22,3],[22,4],[6,1],[6,1],[6,1],[6,1],[6,1],[6,1],[18,2],[18,1]],
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
case 22:this.$ = [$$[$0]];
break;
case 23:this.$ = $$[$0-2].concat($$[$0]);
break;
case 24:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: true,
          scope: 'attribute'
        };
break;
case 25:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: true,
          scope: 'attribute'
        };
break;
case 26:this.$ = {
          name: $$[$0-3],
          value: $$[$0-1],
          bound: true,
          scope: 'attribute',
          preventDefault: true
        };
break;
case 27:this.$ = {
          name: $$[$0-3],
          value: $$[$0-1],
          bound: true,
          scope: 'attribute',
          preventDefault: true
        };
break;
case 28:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: false,
          scope: 'attribute'
        };
break;
case 29:this.$ = (function () {
        $$[$0].scope = $$[$0-2];
        return $$[$0];
      }());
break;
case 30:this.$ = {
          "arguments": [],
          children: [],
          type: 'view'
        };
break;
case 31:this.$ = {
          "arguments": [],
          children: [],
          type: 'collection'
        };
break;
case 32:this.$ = {
          "arguments": [],
          children: [],
          type: 'if'
        };
break;
case 33:this.$ = {
          "arguments": [],
          children: [],
          type: 'unless'
        };
break;
case 34:this.$ = {
          "arguments": [],
          children: [],
          type: 'in'
        };
break;
case 35:this.$ = {
          command: $$[$0],
          "arguments": [],
          children: [],
          type: 'helper'
        };
break;
case 36:this.$ = (function () {
        $$[$0-2]["arguments"].push($$[$0].value);
        return $$[$0-2];
      }());
break;
case 37:this.$ = (function () {
        $$[$0-3].children = $$[$0-1];
        return $$[$0-3];
      }());
break;
case 38:this.$ = $$[$0];
break;
case 39:this.$ = $$[$0];
break;
case 40:this.$ = $$[$0];
break;
case 41:this.$ = $$[$0];
break;
case 42:this.$ = $$[$0];
break;
case 43:this.$ = $$[$0];
break;
case 44:this.$ = $$[$0];
break;
case 45:this.$ = (function () {}());
break;
}
},
table: [{1:[2,1],3:1,4:2,5:3,6:4,7:[1,5],8:[1,6],28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{1:[3]},{1:[2,2],9:[1,13],12:[1,14],14:[1,15]},{1:[2,8],8:[1,16],9:[2,8],12:[2,8],14:[2,8],16:[2,8],21:[2,8]},{1:[2,3],7:[1,17],8:[2,3],9:[2,3],12:[2,3],14:[2,3],16:[2,3],21:[2,3]},{6:18,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{6:19,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{1:[2,38],7:[2,38],8:[2,38],9:[2,38],10:[2,38],12:[2,38],14:[2,38],16:[2,38],21:[2,38],24:[2,38],25:[2,38],26:[2,38]},{1:[2,39],7:[2,39],8:[2,39],9:[2,39],10:[2,39],12:[2,39],14:[2,39],16:[2,39],21:[2,39],24:[2,39],25:[2,39],26:[2,39]},{1:[2,40],7:[2,40],8:[2,40],9:[2,40],10:[2,40],12:[2,40],14:[2,40],16:[2,40],21:[2,40],24:[2,40],25:[2,40],26:[2,40]},{1:[2,41],7:[2,41],8:[2,41],9:[2,41],10:[2,41],12:[2,41],14:[2,41],16:[2,41],21:[2,41],24:[2,41],25:[2,41],26:[2,41]},{1:[2,42],7:[2,42],8:[2,42],9:[2,42],10:[2,42],12:[2,42],14:[2,42],16:[2,42],21:[2,42],24:[2,42],25:[2,42],26:[2,42]},{1:[2,43],7:[2,43],8:[2,43],9:[2,43],10:[2,43],12:[2,43],14:[2,43],16:[2,43],21:[2,43],24:[2,43],25:[2,43],26:[2,43]},{6:23,10:[1,20],11:21,23:22,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{13:24,18:25,19:[1,26],34:[1,27]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:34,15:28,17:32,18:25,19:[1,26],20:29,22:31,27:[1,33],28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12],34:[1,27]},{6:35,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{6:36,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{1:[2,5],8:[2,5],9:[2,5],12:[2,5],14:[2,5],16:[2,5],21:[2,5]},{1:[2,6],8:[2,6],9:[2,6],12:[2,6],14:[2,6],16:[2,6],21:[2,6]},{1:[2,9],9:[2,9],12:[2,9],14:[2,9],16:[2,9],21:[2,9]},{10:[1,37],12:[1,38]},{10:[2,22],12:[2,22]},{24:[1,39],26:[1,40]},{1:[2,11],9:[2,11],12:[2,11],14:[2,11],16:[2,11],21:[2,11]},{1:[2,15],9:[2,15],12:[2,15],14:[2,15],16:[2,15],21:[2,15]},{1:[2,16],9:[2,16],12:[2,16],14:[2,16],16:[2,16],21:[2,16]},{1:[2,45],6:41,9:[2,45],10:[2,45],12:[2,45],14:[2,45],16:[2,45],21:[2,45],25:[2,45],28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{16:[1,42],21:[1,43]},{16:[2,17],21:[2,17]},{9:[1,13],12:[1,14],14:[1,15],16:[2,19],21:[2,19]},{12:[1,44],14:[1,45],16:[2,20],21:[2,20]},{12:[1,46],16:[2,21],21:[2,21]},{12:[1,47]},{12:[2,13],16:[2,13],21:[2,13]},{1:[2,7],8:[2,7],9:[2,7],12:[2,7],14:[2,7],16:[2,7],21:[2,7]},{1:[2,4],8:[2,4],9:[2,4],12:[2,4],14:[2,4],16:[2,4],21:[2,4]},{1:[2,10],9:[2,10],12:[2,10],14:[2,10],16:[2,10],21:[2,10]},{6:23,23:48,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{6:49,18:50,19:[1,51],28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12],34:[1,27]},{6:23,23:52,28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12]},{1:[2,44],9:[2,44],10:[2,44],12:[2,44],14:[2,44],16:[2,44],21:[2,44],25:[2,44]},{1:[2,12],9:[2,12],12:[2,12],14:[2,12],16:[2,12],21:[2,12]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:34,17:32,18:25,19:[1,26],20:53,22:31,27:[1,33],28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12],34:[1,27]},{13:54,18:25,19:[1,26],34:[1,27]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:34,15:55,17:32,18:25,19:[1,26],20:29,22:31,27:[1,33],28:[1,7],29:[1,8],30:[1,9],31:[1,10],32:[1,11],33:[1,12],34:[1,27]},{13:56,18:25,19:[1,26],34:[1,27]},{28:[1,57],29:[1,58],30:[1,59],31:[1,60],32:[1,61],33:[1,62]},{10:[2,23],12:[2,23]},{10:[2,24],12:[2,24],25:[1,63]},{10:[2,25],12:[2,25],25:[1,64]},{10:[2,28],12:[2,28]},{10:[2,29],12:[2,29]},{16:[2,18],21:[2,18]},{12:[2,36],14:[2,36],16:[2,36],21:[2,36]},{16:[1,65],21:[1,43]},{12:[2,14],16:[2,14],21:[2,14]},{12:[2,30],14:[2,30],16:[2,30],21:[2,30]},{12:[2,31],14:[2,31],16:[2,31],21:[2,31]},{12:[2,32],14:[2,32],16:[2,32],21:[2,32]},{12:[2,33],14:[2,33],16:[2,33],21:[2,33]},{12:[2,34],14:[2,34],16:[2,34],21:[2,34]},{12:[2,35],14:[2,35],16:[2,35],21:[2,35]},{10:[2,26],12:[2,26]},{10:[2,27],12:[2,27]},{12:[2,37],14:[2,37],16:[2,37],21:[2,37]}],
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
undefined/* Jison generated lexer */
var lexer = (function(){
var lexer = ({EOF:1,
parseError:function parseError(str, hash) {
        if (this.yy.parser) {
            this.yy.parser.parseError(str, hash);
        } else {
            throw new Error(str);
        }
    },
setInput:function (input) {
        this._input = input;
        this._more = this._less = this.done = false;
        this.yylineno = this.yyleng = 0;
        this.yytext = this.matched = this.match = '';
        this.conditionStack = ['INITIAL'];
        this.yylloc = {first_line:1,first_column:0,last_line:1,last_column:0};
        if (this.options.ranges) this.yylloc.range = [0,0];
        this.offset = 0;
        return this;
    },
input:function () {
        var ch = this._input[0];
        this.yytext += ch;
        this.yyleng++;
        this.offset++;
        this.match += ch;
        this.matched += ch;
        var lines = ch.match(/(?:\r\n?|\n).*/g);
        if (lines) {
            this.yylineno++;
            this.yylloc.last_line++;
        } else {
            this.yylloc.last_column++;
        }
        if (this.options.ranges) this.yylloc.range[1]++;

        this._input = this._input.slice(1);
        return ch;
    },
unput:function (ch) {
        var len = ch.length;
        var lines = ch.split(/(?:\r\n?|\n)/g);

        this._input = ch + this._input;
        this.yytext = this.yytext.substr(0, this.yytext.length-len-1);
        //this.yyleng -= len;
        this.offset -= len;
        var oldLines = this.match.split(/(?:\r\n?|\n)/g);
        this.match = this.match.substr(0, this.match.length-1);
        this.matched = this.matched.substr(0, this.matched.length-1);

        if (lines.length-1) this.yylineno -= lines.length-1;
        var r = this.yylloc.range;

        this.yylloc = {first_line: this.yylloc.first_line,
          last_line: this.yylineno+1,
          first_column: this.yylloc.first_column,
          last_column: lines ?
              (lines.length === oldLines.length ? this.yylloc.first_column : 0) + oldLines[oldLines.length - lines.length].length - lines[0].length:
              this.yylloc.first_column - len
          };

        if (this.options.ranges) {
            this.yylloc.range = [r[0], r[0] + this.yyleng - len];
        }
        return this;
    },
more:function () {
        this._more = true;
        return this;
    },
less:function (n) {
        this.unput(this.match.slice(n));
    },
pastInput:function () {
        var past = this.matched.substr(0, this.matched.length - this.match.length);
        return (past.length > 20 ? '...':'') + past.substr(-20).replace(/\n/g, "");
    },
upcomingInput:function () {
        var next = this.match;
        if (next.length < 20) {
            next += this._input.substr(0, 20-next.length);
        }
        return (next.substr(0,20)+(next.length > 20 ? '...':'')).replace(/\n/g, "");
    },
showPosition:function () {
        var pre = this.pastInput();
        var c = new Array(pre.length + 1).join("-");
        return pre + this.upcomingInput() + "\n" + c+"^";
    },
next:function () {
        if (this.done) {
            return this.EOF;
        }
        if (!this._input) this.done = true;

        var token,
            match,
            tempMatch,
            index,
            col,
            lines;
        if (!this._more) {
            this.yytext = '';
            this.match = '';
        }
        var rules = this._currentRules();
        for (var i=0;i < rules.length; i++) {
            tempMatch = this._input.match(this.rules[rules[i]]);
            if (tempMatch && (!match || tempMatch[0].length > match[0].length)) {
                match = tempMatch;
                index = i;
                if (!this.options.flex) break;
            }
        }
        if (match) {
            lines = match[0].match(/(?:\r\n?|\n).*/g);
            if (lines) this.yylineno += lines.length;
            this.yylloc = {first_line: this.yylloc.last_line,
                           last_line: this.yylineno+1,
                           first_column: this.yylloc.last_column,
                           last_column: lines ? lines[lines.length-1].length-lines[lines.length-1].match(/\r?\n?/)[0].length : this.yylloc.last_column + match[0].length};
            this.yytext += match[0];
            this.match += match[0];
            this.matches = match;
            this.yyleng = this.yytext.length;
            if (this.options.ranges) {
                this.yylloc.range = [this.offset, this.offset += this.yyleng];
            }
            this._more = false;
            this._input = this._input.slice(match[0].length);
            this.matched += match[0];
            token = this.performAction.call(this, this.yy, this, rules[index],this.conditionStack[this.conditionStack.length-1]);
            if (this.done && this._input) this.done = false;
            if (token) return token;
            else return;
        }
        if (this._input === "") {
            return this.EOF;
        } else {
            return this.parseError('Lexical error on line '+(this.yylineno+1)+'. Unrecognized text.\n'+this.showPosition(),
                    {text: "", token: null, line: this.yylineno});
        }
    },
lex:function lex() {
        var r = this.next();
        if (typeof r !== 'undefined') {
            return r;
        } else {
            return this.lex();
        }
    },
begin:function begin(condition) {
        this.conditionStack.push(condition);
    },
popState:function popState() {
        return this.conditionStack.pop();
    },
_currentRules:function _currentRules() {
        return this.conditions[this.conditionStack[this.conditionStack.length-1]].rules;
    },
topState:function () {
        return this.conditionStack[this.conditionStack.length-2];
    },
pushState:function begin(condition) {
        this.begin(condition);
    }});
lexer.options = {};
lexer.performAction = function anonymous(yy,yy_,$avoiding_name_collisions,YY_START) {

var YYSTATE=YY_START
switch($avoiding_name_collisions) {
}
};
lexer.rules = [];
lexer.conditions = {"INITIAL":{"rules":[],"inclusive":true}};
return lexer;})()
parser.lexer = lexer;function Parser () { this.yy = {}; }Parser.prototype = parser;parser.Parser = Parser;
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
}
};require['./view'] = new function() {
  var exports = this;
  (function() {
  var Lexer, Serenade, View, compile, parser;

  parser = require('./parser').parser;

  Lexer = require('./lexer').Lexer;

  compile = require('./compile').compile;

  Serenade = require('./serenade').Serenade;

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
      if (typeof this.view === 'string') {
        return parser.parse(new Lexer().tokenize(this.view));
      } else {
        return this.view;
      }
    };

    View.prototype.render = function(model, controller, parent) {
      var node;
      if (this.name) {
        controller || (controller = Serenade.controllerFor(this.name, model));
      }
      controller || (controller = {});
      if (typeof controller === "function") {
        controller = new controller(model, parent);
      }
      node = compile(this.parse(), model, controller);
      if (typeof controller.loaded === "function") {
        controller.loaded(model, node.element);
      }
      return node.element;
    };

    return View;

  })();

  exports.View = View;

}).call(this);

};
    return require['./serenade'].Serenade
  }();

  if(typeof define === 'function' && define.amd) {
    define(function() { return Serenade });
  } else { root.Serenade = Serenade }
}(this));