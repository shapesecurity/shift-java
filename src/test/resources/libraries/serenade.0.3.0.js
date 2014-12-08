/**
 * Serenade.js JavaScript Framework v0.3.0
 * Revision: 7e7103d7f5
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

  exports.NodeEvents = {
    bindEvent: function(to, name, fun) {
      if (to != null ? to.bind : void 0) {
        this.boundEvents || (this.boundEvents = []);
        this.boundEvents.push({
          to: to,
          name: name,
          fun: fun
        });
        return to.bind(name, fun);
      }
    },
    unbindEvents: function() {
      var fun, name, node, to, _i, _j, _len, _len1, _ref, _ref1, _ref2, _results;
      if (typeof this.trigger === "function") {
        this.trigger("unload");
      }
      _ref = this.nodes();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        node = _ref[_i];
        node.unbindEvents();
      }
      if (this.boundEvents) {
        _ref1 = this.boundEvents;
        _results = [];
        for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
          _ref2 = _ref1[_j], to = _ref2.to, name = _ref2.name, fun = _ref2.fun;
          _results.push(to.unbind(name, fun));
        }
        return _results;
      }
    }
  };

}).call(this);

};require['./helpers'] = new function() {
  var exports = this;
  (function() {
  var Helpers,
    __hasProp = {}.hasOwnProperty;

  Helpers = {
    prefix: "_prop_",
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
    format: function(model, key) {
      var formatter, value, _ref;
      value = model[key];
      formatter = (_ref = model[Helpers.prefix + key]) != null ? _ref.format : void 0;
      if (typeof formatter === 'function') {
        return formatter.call(this, value);
      } else {
        return value;
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
      if (object && typeof object.toJSON === 'function') {
        return object.toJSON();
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
    capitalize: function(word) {
      return word.slice(0, 1).toUpperCase() + word.slice(1);
    }
  };

  Helpers.extend(exports, Helpers);

}).call(this);

};require['./cache'] = new function() {
  var exports = this;
  (function() {
  var Cache, serializeObject;

  serializeObject = require('./helpers').serializeObject;

  Cache = {
    _storage: typeof window !== "undefined" && window !== null ? window.localStorage : void 0,
    _identityMap: {},
    get: function(ctor, id) {
      var name, _ref;
      name = ctor.uniqueId();
      if (name && id) {
        return ((_ref = this._identityMap[name]) != null ? _ref[id] : void 0) || this.retrieve(ctor, id);
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
    },
    store: function(ctor, id, obj) {
      var name;
      name = ctor.uniqueId();
      if (name && id && (typeof JSON !== "undefined" && JSON !== null)) {
        return this._storage.setItem("" + name + "_" + id, JSON.stringify(serializeObject(obj)));
      }
    },
    retrieve: function(ctor, id) {
      var data, name;
      name = ctor.uniqueId();
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
  var Events, extend, isArrayIndex, serializeObject, _ref,
    __slice = [].slice;

  Events = require('./events').Events;

  _ref = require('./helpers'), extend = _ref.extend, serializeObject = _ref.serializeObject;

  isArrayIndex = function(index) {
    return ("" + index).match(/^\d+$/);
  };

  exports.Collection = (function() {
    var fun, _i, _len, _ref1;

    extend(Collection.prototype, Events);

    function Collection(list) {
      var index, val, _i, _len;
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
      this.trigger("change:" + index, value);
      this.trigger("set", index, value);
      this.trigger("change", this);
      return value;
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
      this.length = (list != null ? list.length : void 0) || 0;
      this.trigger("update", old, this);
      this.trigger("change", this);
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
      this.trigger("insert", index, value);
      this.trigger("change", this);
      return value;
    };

    Collection.prototype.deleteAt = function(index) {
      var value;
      value = this[index];
      Array.prototype.splice.call(this, index, 1);
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

    Collection.prototype.clone = function() {
      return new Collection(this.toArray());
    };

    Collection.prototype.push = function(element) {
      this[this.length++] = element;
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

    Collection.prototype.splice = function() {
      var deleteCount, deleted, list, old, start;
      start = arguments[0], deleteCount = arguments[1], list = 3 <= arguments.length ? __slice.call(arguments, 2) : [];
      old = this.clone();
      deleted = Array.prototype.splice.apply(this, [start, deleteCount].concat(__slice.call(list)));
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

    Collection.prototype.reverse = function() {
      var old;
      old = this.clone();
      Array.prototype.reverse.call(this);
      this.trigger("update", old, this);
      this.trigger("change", this);
      return this;
    };

    _ref1 = ["forEach", "indexOf", "lastIndexOf", "join", "every", "some", "reduce", "reduceRight"];
    for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
      fun = _ref1[_i];
      Collection.prototype[fun] = Array.prototype[fun];
    }

    Collection.prototype.map = function() {
      var args;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return new Collection(Array.prototype.map.apply(this, args));
    };

    Collection.prototype.filter = function() {
      var args;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return new Collection(Array.prototype.filter.apply(this, args));
    };

    Collection.prototype.slice = function() {
      var args, _ref2;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return new Collection((_ref2 = this.toArray()).slice.apply(_ref2, args));
    };

    Collection.prototype.concat = function() {
      var arg, args, _ref2;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      args = (function() {
        var _j, _len1, _results;
        _results = [];
        for (_j = 0, _len1 = args.length; _j < _len1; _j++) {
          arg = args[_j];
          if (arg instanceof Collection) {
            _results.push(arg.toArray());
          } else {
            _results.push(arg);
          }
        }
        return _results;
      })();
      return new Collection((_ref2 = this.toArray()).concat.apply(_ref2, args));
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

    return Collection;

  })();

}).call(this);

};require['./association_collection'] = new function() {
  var exports = this;
  (function() {
  var AssociationCollection, Collection,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
    __slice = [].slice;

  Collection = require('./collection').Collection;

  AssociationCollection = (function(_super) {

    __extends(AssociationCollection, _super);

    function AssociationCollection(owner, options, list) {
      var item;
      this.owner = owner;
      this.options = options;
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

    AssociationCollection.prototype.splice = function() {
      var deleteCount, item, list, start;
      start = arguments[0], deleteCount = arguments[1], list = 3 <= arguments.length ? __slice.call(arguments, 2) : [];
      list = (function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = list.length; _i < _len; _i++) {
          item = list[_i];
          _results.push(this._convert(item));
        }
        return _results;
      }).call(this);
      return AssociationCollection.__super__.splice.apply(this, [start, deleteCount].concat(__slice.call(list)));
    };

    AssociationCollection.prototype.insertAt = function(index, item) {
      return AssociationCollection.__super__.insertAt.call(this, index, this._convert(item));
    };

    AssociationCollection.prototype._convert = function(item) {
      if (item.constructor === Object && this.options.as) {
        item = new (this.options.as())(item);
      }
      if (this.options.inverseOf && item[this.options.inverseOf] !== this.owner) {
        item[this.options.inverseOf] = this.owner;
      }
      return item;
    };

    return AssociationCollection;

  })(Collection);

  exports.AssociationCollection = AssociationCollection;

}).call(this);

};require['./properties'] = new function() {
  var exports = this;
  (function() {
  var AssociationCollection, Associations, Collection, Events, Properties, addDependencies, addGlobalDependencies, exp, extend, globalDependencies, pairToObject, prefix, serializeObject, triggerChangesTo, triggerGlobal, _ref,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; },
    __hasProp = {}.hasOwnProperty;

  Collection = require('./collection').Collection;

  AssociationCollection = require('./association_collection').AssociationCollection;

  Events = require('./events').Events;

  _ref = require('./helpers'), prefix = _ref.prefix, pairToObject = _ref.pairToObject, serializeObject = _ref.serializeObject, extend = _ref.extend;

  exp = /^_prop_/;

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
      if (__indexOf.call(object["_dep_" + name], dependency) < 0) {
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
          if (__indexOf.call(names, dependency) < 0) {
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
      Object.defineProperty(this, name, {
        get: function() {
          return Properties.get.call(this, name);
        },
        set: function(value) {
          return Properties.set.call(this, name, value);
        },
        configurable: true
      });
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
    get: function(name) {
      var _ref1, _ref2, _ref3;
      if ((_ref1 = this[prefix + name]) != null ? _ref1.dependsOn : void 0) {
        addGlobalDependencies(this, name, [].concat(this[prefix + name].dependsOn));
      }
      this.attributes || (this.attributes = {});
      if ((_ref2 = this[prefix + name]) != null ? _ref2.get : void 0) {
        return this[prefix + name].get.call(this);
      } else if (((_ref3 = this[prefix + name]) != null ? _ref3.hasOwnProperty("default") : void 0) && !this.attributes.hasOwnProperty(name)) {
        return this[prefix + name]["default"];
      } else {
        return this.attributes[name];
      }
    },
    toJSON: function() {
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
          var previous;
          if (model.constructor === Object && attributes.as) {
            model = new (attributes.as())(model);
          }
          previous = this.attributes[name];
          this.attributes[name] = model;
          if (attributes.inverseOf && !model[attributes.inverseOf].includes(this)) {
            if (previous) {
              previous[attributes.inverseOf]["delete"](this);
            }
            return model[attributes.inverseOf].push(this);
          }
        }
      });
      this.property(name, attributes);
      return this.property(name + 'Id', {
        get: function() {
          var _ref1;
          return (_ref1 = this.get(name)) != null ? _ref1.id : void 0;
        },
        set: function(id) {
          if (id != null) {
            return this.set(name, attributes.as().find(id));
          }
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
            this.attributes[name] = new AssociationCollection(this, attributes, []);
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
  var Associations, Cache, Model, Properties, capitalize, extend, idCounter, _ref, _ref1,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; },
    __slice = [].slice;

  Cache = require('./cache').Cache;

  _ref = require('./properties'), Associations = _ref.Associations, Properties = _ref.Properties;

  _ref1 = require('./helpers'), extend = _ref1.extend, capitalize = _ref1.capitalize;

  idCounter = 1;

  Model = (function() {

    extend(Model.prototype, Properties);

    extend(Model.prototype, Associations);

    Model.property = function() {
      var _ref2;
      return (_ref2 = this.prototype).property.apply(_ref2, arguments);
    };

    Model.collection = function() {
      var _ref2;
      return (_ref2 = this.prototype).collection.apply(_ref2, arguments);
    };

    Model.belongsTo = function() {
      var _ref2;
      return (_ref2 = this.prototype).belongsTo.apply(_ref2, arguments);
    };

    Model.hasMany = function() {
      var _ref2;
      return (_ref2 = this.prototype).hasMany.apply(_ref2, arguments);
    };

    Model.find = function(id) {
      return Cache.get(this, id) || new this({
        id: id
      });
    };

    Model.property('id', {
      serialize: true,
      set: function(val) {
        Cache.unset(this.constructor, this.attributes.id);
        Cache.set(this.constructor, val, this);
        return this.attributes.id = val;
      }
    });

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

    Model.delegate = function() {
      var name, names, options, to, _i, _j, _len, _results,
        _this = this;
      names = 2 <= arguments.length ? __slice.call(arguments, 0, _i = arguments.length - 1) : (_i = 0, []), options = arguments[_i++];
      to = options.to;
      _results = [];
      for (_j = 0, _len = names.length; _j < _len; _j++) {
        name = names[_j];
        _results.push((function(name) {
          var propName;
          propName = name;
          if (options.prefix) {
            propName = to + capitalize(name);
          }
          if (options.suffix) {
            propName = propName + capitalize(to);
          }
          return _this.property(propName, {
            dependsOn: "" + to + "." + name,
            get: function() {
              var _ref2;
              return (_ref2 = this[to]) != null ? _ref2[name] : void 0;
            }
          });
        })(name));
      }
      return _results;
    };

    Model.uniqueId = function() {
      if (!(this._uniqueId && this._uniqueGen === this)) {
        this._uniqueId = (idCounter += 1);
        this._uniqueGen = this;
      }
      return this._uniqueId;
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
  var Cache, Properties, Serenade, extend, format, globalDependencies, _ref, _ref1;

  Cache = require('./cache').Cache;

  _ref = require('./helpers'), extend = _ref.extend, format = _ref.format;

  _ref1 = require("./properties"), Properties = _ref1.Properties, globalDependencies = _ref1.globalDependencies;

  Serenade = function(attributes) {
    if (this === root) {
      return new Serenade(attributes);
    }
    this.set(attributes);
    return this;
  };

  extend(Serenade.prototype, Properties);

  extend(Serenade, {
    VERSION: '0.3.0',
    _views: {},
    _controllers: {},
    document: typeof window !== "undefined" && window !== null ? window.document : void 0,
    format: format,
    view: function(nameOrTemplate, template) {
      var View;
      View = require('./view').View;
      if (template) {
        return this._views[nameOrTemplate] = new View(nameOrTemplate, template);
      } else {
        return new View(void 0, nameOrTemplate);
      }
    },
    render: function(name, model, controller, parent, skipCallback) {
      return this._views[name].render(model, controller, parent, skipCallback);
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
  var COMMENT, IDENTIFIER, KEYWORDS, LITERAL, Lexer, MULTI_DENT, STRING, WHITESPACE,
    __indexOf = [].indexOf || function(item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

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

  exports.Lexer = Lexer;

}).call(this);

};require['./node'] = new function() {
  var exports = this;
  (function() {
  var Collection, Events, Node, NodeEvents, Serenade, extend, _ref;

  Serenade = require('./serenade').Serenade;

  _ref = require('./events'), Events = _ref.Events, NodeEvents = _ref.NodeEvents;

  extend = require('./helpers').extend;

  Collection = require('./collection').Collection;

  Node = (function() {

    extend(Node.prototype, Events);

    extend(Node.prototype, NodeEvents);

    function Node(ast, element) {
      this.ast = ast;
      this.element = element;
      this.children = new Collection([]);
    }

    Node.prototype.append = function(inside) {
      return inside.appendChild(this.element);
    };

    Node.prototype.insertAfter = function(after) {
      return after.parentNode.insertBefore(this.element, after.nextSibling);
    };

    Node.prototype.remove = function() {
      var _ref1;
      this.unbindEvents();
      return (_ref1 = this.element.parentNode) != null ? _ref1.removeChild(this.element) : void 0;
    };

    Node.prototype.lastElement = function() {
      return this.element;
    };

    Node.prototype.nodes = function() {
      return this.children;
    };

    return Node;

  })();

  exports.Node = Node;

}).call(this);

};require['./dynamic_node'] = new function() {
  var exports = this;
  (function() {
  var Collection, DynamicNode, NodeEvents, Serenade, extend;

  Serenade = require('./serenade').Serenade;

  Collection = require('./collection').Collection;

  extend = require('./helpers').extend;

  NodeEvents = require('./events').NodeEvents;

  DynamicNode = (function() {

    extend(DynamicNode.prototype, NodeEvents);

    function DynamicNode(ast) {
      this.ast = ast;
      this.anchor = Serenade.document.createTextNode('');
      this.nodeSets = new Collection([]);
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
          _results.push(last = node.lastElement());
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
      var node, _i, _len, _ref;
      _ref = this.nodes();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        node = _ref[_i];
        node.remove();
      }
      return this.nodeSets.update([]);
    };

    DynamicNode.prototype.remove = function() {
      this.unbindEvents();
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
  var Collection, Compile, DynamicNode, Node, Property, Serenade, compile, compileAll, format, getValue;

  Serenade = require('./serenade').Serenade;

  Collection = require('./collection').Collection;

  Node = require('./node').Node;

  DynamicNode = require('./dynamic_node').DynamicNode;

  format = require('./helpers').format;

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
        return node.element.style[ast.name] = getValue(ast, model);
      };
      update();
      if (ast.bound) {
        return node.bindEvent(model, "change:" + ast.value, update);
      }
    },
    event: function(ast, node, model, controller) {
      return node.element.addEventListener(ast.name, function(e) {
        if (ast.preventDefault) {
          e.preventDefault();
        }
        return controller[ast.value](model, node.element, e);
      });
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
          if (element.value !== val) {
            return element.value = val;
          }
        }
      };
      modelUpdated();
      node.bindEvent(model, "change:" + ast.value, modelUpdated);
      if (ast.name === "binding") {
        handler = function(e) {
          if (element.form === (e.target || e.srcElement)) {
            return domUpdated();
          }
        };
        Serenade.document.addEventListener("submit", handler, true);
        return node.bind("unload", function() {
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
            return element.className = classes.join(' ');
          } else {
            element.className = '';
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
        node.bindEvent(model, "change:" + ast.value, update);
      }
      return update();
    },
    on: function(ast, node, model, controller) {
      var _ref;
      if ((_ref = ast.name) === "load" || _ref === "unload") {
        return node.bind(ast.name, function() {
          return controller[ast.value](model, node.element);
        });
      } else {
        throw new SyntaxError("unkown lifecycle event '" + ast.name + "'");
      }
    }
  };

  Compile = {
    element: function(ast, model, controller) {
      var action, child, childNode, element, node, property, _i, _j, _len, _len1, _ref, _ref1, _ref2;
      element = Serenade.document.createElement(ast.name);
      node = new Node(ast, element);
      if (ast.id) {
        element.setAttribute('id', ast.id);
      }
      if ((_ref = ast.classes) != null ? _ref.length : void 0) {
        element.setAttribute('class', ast.classes.join(' '));
      }
      _ref1 = ast.children;
      for (_i = 0, _len = _ref1.length; _i < _len; _i++) {
        child = _ref1[_i];
        childNode = compile(child, model, controller);
        childNode.append(element);
        node.children.push(childNode);
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
      node.trigger("load");
      return node;
    },
    view: function(ast, model, parent) {
      var controller, skipCallback;
      controller = Serenade.controllerFor(ast["arguments"][0]);
      if (!controller) {
        skipCallback = true;
        controller = parent;
      }
      return Serenade._views[ast["arguments"][0]].node(model, controller, parent, skipCallback);
    },
    helper: function(ast, model, controller) {
      var context, element, helperFunction, render;
      render = function(model, controller) {
        var child, fragment, node, _i, _len, _ref;
        if (model == null) {
          model = model;
        }
        if (controller == null) {
          controller = controller;
        }
        fragment = Serenade.document.createDocumentFragment();
        _ref = ast.children;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          child = _ref[_i];
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
        node.bindEvent(model, "change:" + ast.value, function() {
          return textNode.nodeValue = getText();
        });
      }
      return node;
    },
    collection: function(ast, model, controller) {
      var collection, compileItem, dynamic, item,
        _this = this;
      compileItem = function(item) {
        return compileAll(ast.children, item, controller);
      };
      dynamic = new DynamicNode(ast);
      collection = model[ast["arguments"][0]];
      if (typeof collection.bind === "function") {
        dynamic.bindEvent(collection, 'set', function() {
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
        dynamic.bindEvent(collection, 'update', function() {
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
        dynamic.bindEvent(collection, 'add', function(item) {
          return dynamic.appendNodeSet(compileItem(item));
        });
        dynamic.bindEvent(collection, 'insert', function(index, item) {
          return dynamic.insertNodeSet(index, compileItem(item));
        });
        dynamic.bindEvent(collection, 'delete', function(index) {
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
      return Compile.bound(ast, model, controller, function(dynamic, value) {
        if (value) {
          return dynamic.replace([compileAll(ast.children, value, controller)]);
        } else {
          return dynamic.clear();
        }
      });
    },
    "if": function(ast, model, controller) {
      return Compile.bound(ast, model, controller, function(dynamic, value) {
        if (value) {
          return dynamic.replace([compileAll(ast.children, model, controller)]);
        } else if (ast["else"]) {
          return dynamic.replace([compileAll(ast["else"].children, model, controller)]);
        } else {
          return dynamic.clear();
        }
      });
    },
    unless: function(ast, model, controller) {
      return Compile.bound(ast, model, controller, function(dynamic, value) {
        var child, nodes;
        if (value) {
          return dynamic.clear();
        } else {
          nodes = (function() {
            var _i, _len, _ref, _results;
            _ref = ast.children;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              child = _ref[_i];
              _results.push(compile(child, model, controller));
            }
            return _results;
          })();
          return dynamic.replace([nodes]);
        }
      });
    },
    bound: function(ast, model, controller, callback) {
      var dynamic, update;
      dynamic = new DynamicNode(ast);
      update = function() {
        var value;
        value = model[ast["arguments"][0]];
        return callback(dynamic, value);
      };
      update();
      dynamic.bindEvent(model, "change:" + ast["arguments"][0], update);
      return dynamic;
    }
  };

  compile = function(ast, model, controller) {
    return Compile[ast.type](ast, model, controller);
  };

  compileAll = function(asts, model, controller) {
    var ast, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = asts.length; _i < _len; _i++) {
      ast = asts[_i];
      _results.push(Compile[ast.type](ast, model, controller));
    }
    return _results;
  };

  exports.compile = compile;

}).call(this);

};require['./parser'] = new function() {
  var exports = this;
  /* Jison generated parser */
var parser = (function(){
var parser = {trace: function trace() { },
yy: {},
symbols_: {"error":2,"Root":3,"Element":4,"ElementIdentifier":5,"AnyIdentifier":6,"#":7,".":8,"[":9,"]":10,"PropertyList":11,"WHITESPACE":12,"Text":13,"INDENT":14,"ChildList":15,"OUTDENT":16,"TextList":17,"Bound":18,"STRING_LITERAL":19,"Child":20,"TERMINATOR":21,"IfInstruction":22,"Instruction":23,"Property":24,"=":25,"!":26,":":27,"-":28,"VIEW":29,"COLLECTION":30,"UNLESS":31,"IN":32,"IDENTIFIER":33,"IF":34,"ElseInstruction":35,"ELSE":36,"@":37,"$accept":0,"$end":1},
terminals_: {2:"error",7:"#",8:".",9:"[",10:"]",12:"WHITESPACE",14:"INDENT",16:"OUTDENT",19:"STRING_LITERAL",21:"TERMINATOR",25:"=",26:"!",27:":",28:"-",29:"VIEW",30:"COLLECTION",31:"UNLESS",32:"IN",33:"IDENTIFIER",34:"IF",36:"ELSE",37:"@"},
productions_: [0,[3,0],[3,1],[5,1],[5,3],[5,2],[5,2],[5,3],[4,1],[4,3],[4,4],[4,3],[4,4],[17,1],[17,3],[13,1],[13,1],[15,1],[15,3],[20,1],[20,1],[20,1],[20,1],[11,1],[11,3],[24,3],[24,3],[24,4],[24,4],[24,3],[24,3],[23,3],[23,3],[23,3],[23,3],[23,3],[23,3],[23,4],[22,3],[22,3],[22,4],[22,2],[35,6],[6,1],[6,1],[6,1],[6,1],[6,1],[6,1],[18,2],[18,1]],
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
case 23:this.$ = [$$[$0]];
break;
case 24:this.$ = $$[$0-2].concat($$[$0]);
break;
case 25:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: true,
          scope: 'attribute'
        };
break;
case 26:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: true,
          scope: 'attribute'
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
          name: $$[$0-3],
          value: $$[$0-1],
          bound: true,
          scope: 'attribute',
          preventDefault: true
        };
break;
case 29:this.$ = {
          name: $$[$0-2],
          value: $$[$0],
          bound: false,
          scope: 'attribute'
        };
break;
case 30:this.$ = (function () {
        $$[$0].scope = $$[$0-2];
        return $$[$0];
      }());
break;
case 31:this.$ = {
          "arguments": [],
          children: [],
          type: 'view'
        };
break;
case 32:this.$ = {
          "arguments": [],
          children: [],
          type: 'collection'
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
case 38:this.$ = {
          "arguments": [],
          children: [],
          type: 'if'
        };
break;
case 39:this.$ = (function () {
        $$[$0-2]["arguments"].push($$[$0].value);
        return $$[$0-2];
      }());
break;
case 40:this.$ = (function () {
        $$[$0-3].children = $$[$0-1];
        return $$[$0-3];
      }());
break;
case 41:this.$ = (function () {
        $$[$0-1]["else"] = $$[$0];
        return $$[$0-1];
      }());
break;
case 42:this.$ = {
          "arguments": [],
          children: $$[$0-1],
          type: 'else'
        };
break;
case 43:this.$ = $$[$0];
break;
case 44:this.$ = $$[$0];
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
case 50:this.$ = (function () {}());
break;
}
},
table: [{1:[2,1],3:1,4:2,5:3,6:4,7:[1,5],8:[1,6],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{1:[3]},{1:[2,2],9:[1,13],12:[1,14],14:[1,15]},{1:[2,8],8:[1,16],9:[2,8],12:[2,8],14:[2,8],16:[2,8],21:[2,8]},{1:[2,3],7:[1,17],8:[2,3],9:[2,3],12:[2,3],14:[2,3],16:[2,3],21:[2,3]},{6:18,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{6:19,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{1:[2,43],7:[2,43],8:[2,43],9:[2,43],10:[2,43],12:[2,43],14:[2,43],16:[2,43],21:[2,43],25:[2,43],26:[2,43],27:[2,43],28:[2,43]},{1:[2,44],7:[2,44],8:[2,44],9:[2,44],10:[2,44],12:[2,44],14:[2,44],16:[2,44],21:[2,44],25:[2,44],26:[2,44],27:[2,44],28:[2,44]},{1:[2,45],7:[2,45],8:[2,45],9:[2,45],10:[2,45],12:[2,45],14:[2,45],16:[2,45],21:[2,45],25:[2,45],26:[2,45],27:[2,45],28:[2,45]},{1:[2,46],7:[2,46],8:[2,46],9:[2,46],10:[2,46],12:[2,46],14:[2,46],16:[2,46],21:[2,46],25:[2,46],26:[2,46],27:[2,46],28:[2,46]},{1:[2,47],7:[2,47],8:[2,47],9:[2,47],10:[2,47],12:[2,47],14:[2,47],16:[2,47],21:[2,47],25:[2,47],26:[2,47],27:[2,47],28:[2,47]},{1:[2,48],7:[2,48],8:[2,48],9:[2,48],10:[2,48],12:[2,48],14:[2,48],16:[2,48],21:[2,48],25:[2,48],26:[2,48],27:[2,48],28:[2,48]},{6:23,10:[1,20],11:21,24:22,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{13:24,18:25,19:[1,26],37:[1,27]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:35,15:28,17:33,18:25,19:[1,26],20:29,22:31,23:32,28:[1,34],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9],37:[1,27]},{6:36,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{6:37,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{1:[2,5],8:[2,5],9:[2,5],12:[2,5],14:[2,5],16:[2,5],21:[2,5]},{1:[2,6],8:[2,6],9:[2,6],12:[2,6],14:[2,6],16:[2,6],21:[2,6]},{1:[2,9],9:[2,9],12:[2,9],14:[2,9],16:[2,9],21:[2,9]},{10:[1,38],12:[1,39]},{10:[2,23],12:[2,23]},{25:[1,40],27:[1,41]},{1:[2,11],9:[2,11],12:[2,11],14:[2,11],16:[2,11],21:[2,11]},{1:[2,15],9:[2,15],12:[2,15],14:[2,15],16:[2,15],21:[2,15],28:[2,15]},{1:[2,16],9:[2,16],12:[2,16],14:[2,16],16:[2,16],21:[2,16],28:[2,16]},{1:[2,50],6:42,9:[2,50],10:[2,50],12:[2,50],14:[2,50],16:[2,50],21:[2,50],26:[2,50],28:[2,50],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{16:[1,43],21:[1,44]},{16:[2,17],21:[2,17]},{9:[1,13],12:[1,14],14:[1,15],16:[2,19],21:[2,19]},{12:[1,45],14:[1,46],16:[2,20],21:[2,20],28:[1,48],35:47},{12:[1,49],14:[1,50],16:[2,21],21:[2,21]},{12:[1,51],16:[2,22],21:[2,22]},{12:[1,52]},{12:[2,13],16:[2,13],21:[2,13]},{1:[2,7],8:[2,7],9:[2,7],12:[2,7],14:[2,7],16:[2,7],21:[2,7]},{1:[2,4],8:[2,4],9:[2,4],12:[2,4],14:[2,4],16:[2,4],21:[2,4]},{1:[2,10],9:[2,10],12:[2,10],14:[2,10],16:[2,10],21:[2,10]},{6:23,24:53,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{6:54,18:55,19:[1,56],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9],37:[1,27]},{6:23,24:57,29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9]},{1:[2,49],9:[2,49],10:[2,49],12:[2,49],14:[2,49],16:[2,49],21:[2,49],26:[2,49],28:[2,49]},{1:[2,12],9:[2,12],12:[2,12],14:[2,12],16:[2,12],21:[2,12]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:35,17:33,18:25,19:[1,26],20:58,22:31,23:32,28:[1,34],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9],37:[1,27]},{13:59,18:25,19:[1,26],37:[1,27]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:35,15:60,17:33,18:25,19:[1,26],20:29,22:31,23:32,28:[1,34],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9],37:[1,27]},{12:[2,41],14:[2,41],16:[2,41],21:[2,41],28:[2,41]},{12:[1,61]},{13:62,18:25,19:[1,26],37:[1,27]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:35,15:63,17:33,18:25,19:[1,26],20:29,22:31,23:32,28:[1,34],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9],37:[1,27]},{13:64,18:25,19:[1,26],37:[1,27]},{29:[1,66],30:[1,67],31:[1,68],32:[1,69],33:[1,70],34:[1,65]},{10:[2,24],12:[2,24]},{10:[2,25],12:[2,25],26:[1,71]},{10:[2,26],12:[2,26],26:[1,72]},{10:[2,29],12:[2,29]},{10:[2,30],12:[2,30]},{16:[2,18],21:[2,18]},{12:[2,39],14:[2,39],16:[2,39],21:[2,39],28:[2,39]},{16:[1,73],21:[1,44]},{36:[1,74]},{12:[2,36],14:[2,36],16:[2,36],21:[2,36]},{16:[1,75],21:[1,44]},{12:[2,14],16:[2,14],21:[2,14]},{12:[2,38],14:[2,38],16:[2,38],21:[2,38],28:[2,38]},{12:[2,31],14:[2,31],16:[2,31],21:[2,31]},{12:[2,32],14:[2,32],16:[2,32],21:[2,32]},{12:[2,33],14:[2,33],16:[2,33],21:[2,33]},{12:[2,34],14:[2,34],16:[2,34],21:[2,34]},{12:[2,35],14:[2,35],16:[2,35],21:[2,35]},{10:[2,27],12:[2,27]},{10:[2,28],12:[2,28]},{12:[2,40],14:[2,40],16:[2,40],21:[2,40],28:[2,40]},{14:[1,76]},{12:[2,37],14:[2,37],16:[2,37],21:[2,37]},{4:30,5:3,6:4,7:[1,5],8:[1,6],13:35,15:77,17:33,18:25,19:[1,26],20:29,22:31,23:32,28:[1,34],29:[1,7],30:[1,8],31:[1,10],32:[1,11],33:[1,12],34:[1,9],37:[1,27]},{16:[1,78],21:[1,44]},{12:[2,42],14:[2,42],16:[2,42],21:[2,42],28:[2,42]}],
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
}
};require['./view'] = new function() {
  var exports = this;
  (function() {
  var Lexer, Serenade, View, compile, parser,
    __slice = [].slice;

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
        try {
          return this.view = parser.parse(new Lexer().tokenize(this.view));
        } catch (e) {
          if (this.name) {
            e.message = "In view '" + this.name + "': " + e.message;
          }
          throw e;
        }
      } else {
        return this.view;
      }
    };

    View.prototype.render = function() {
      var args;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return this.node.apply(this, args).element;
    };

    View.prototype.node = function(model, controller, parent, skipCallback) {
      var node;
      if (this.name) {
        controller || (controller = Serenade.controllerFor(this.name, model));
      }
      controller || (controller = {});
      if (typeof controller === "function") {
        controller = new controller(model, parent);
      }
      node = compile(this.parse(), model, controller);
      if (!skipCallback) {
        if (typeof controller.loaded === "function") {
          controller.loaded(model, node.element);
        }
      }
      return node;
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