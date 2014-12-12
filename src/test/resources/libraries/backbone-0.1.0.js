//     (c) 2010 Jeremy Ashkenas, DocumentCloud Inc.
//     Backbone may be freely distributed under the MIT license.
//     For all details and documentation:
//     http://documentcloud.github.com/backbone

(function(){

  // Initial Setup
  // -------------

  // The top-level namespace.
  var Backbone = {};

  // Keep the version here in sync with `package.json`.
  Backbone.VERSION = '0.1.0';

  // Export for both CommonJS and the browser.
  (typeof exports !== 'undefined' ? exports : this).Backbone = Backbone;

  // Require Underscore, if we're on the server, and it's not already present.
  var _ = this._;
  if (!_ && (typeof require !== 'undefined')) _ = require("underscore")._;

  // Helper function to correctly set up the prototype chain, for subclasses.
  // Similar to `goog.inherits`, but uses a hash of prototype properties and
  // class properties to be extended.
  var inherits = function(parent, protoProps, classProps) {
    var child = protoProps.hasOwnProperty('constructor') ? protoProps.constructor :
                function(){ return parent.apply(this, arguments); };
    var ctor = function(){};
    ctor.prototype = parent.prototype;
    child.prototype = new ctor();
    _.extend(child.prototype, protoProps);
    if (classProps) _.extend(child, classProps);
    child.prototype.constructor = child;
    return child;
  };

  // Helper function to get a URL from a Model or Collection as a property
  // or as a function.
  var getUrl = function(object) {
    return _.isFunction(object.url) ? object.url() : object.url;
  };

  // Backbone.Events
  // -----------------

  // A module that can be mixed in to *any object* in order to provide it with
  // custom events. You may `bind` or `unbind` a callback function to an event;
  // `trigger`-ing an event fires all callbacks in succession.
  //
  //     var object = {};
  //     _.extend(object, Backbone.Events);
  //     object.bind('expand', function(){ alert('expanded'); });
  //     object.trigger('expand');
  //
  Backbone.Events = {

    // Bind an event, specified by a string name, `ev`, to a `callback` function.
    // Passing `"all"` will bind the callback to all events fired.
    bind : function(ev, callback) {
      var calls = this._callbacks || (this._callbacks = {});
      var list  = this._callbacks[ev] || (this._callbacks[ev] = []);
      list.push(callback);
      return this;
    },

    // Remove one or many callbacks. If `callback` is null, removes all
    // callbacks for the event. If `ev` is null, removes all bound callbacks
    // for all events.
    unbind : function(ev, callback) {
      var calls;
      if (!ev) {
        this._callbacks = {};
      } else if (calls = this._callbacks) {
        if (!callback) {
          calls[ev] = [];
        } else {
          var list = calls[ev];
          if (!list) return this;
          for (var i = 0, l = list.length; i < l; i++) {
            if (callback === list[i]) {
              list.splice(i, 1);
              break;
            }
          }
        }
      }
      return this;
    },

    // Trigger an event, firing all bound callbacks. Callbacks are passed the
    // same arguments as `trigger` is, apart from the event name.
    // Listening for `"all"` passes the true event name as the first argument.
    trigger : function(ev) {
      var list, calls, i, l;
      var calls = this._callbacks;
      if (!(calls = this._callbacks)) return this;
      if (list = calls[ev]) {
        for (i = 0, l = list.length; i < l; i++) {
          list[i].apply(this, _.rest(arguments));
        }
      }
      if (list = calls['all']) {
        for (i = 0, l = list.length; i < l; i++) {
          list[i].apply(this, arguments);
        }
      }
      return this;
    }

  };

  // Backbone.Model
  // --------------

  // Create a new model, with defined attributes. A client id (`cid`)
  // is automatically generated and assigned for you.
  Backbone.Model = function(attributes) {
    this.attributes = {};
    this.cid = _.uniqueId('c');
    this.set(attributes || {}, {silent : true});
    this._previousAttributes = _.clone(this.attributes);
  };

  // Attach all inheritable methods to the Model prototype.
  _.extend(Backbone.Model.prototype, Backbone.Events, {

    // A snapshot of the model's previous attributes, taken immediately
    // after the last `changed` event was fired.
    _previousAttributes : null,

    // Has the item been changed since the last `changed` event?
    _changed : false,

    // Return a copy of the model's `attributes` object.
    toJSON : function() {
      return _.clone(this.attributes);
    },

    // Get the value of an attribute.
    get : function(attr) {
      return this.attributes[attr];
    },

    // Set a hash of model attributes on the object, firing `changed` unless you
    // choose to silence it.
    set : function(attrs, options) {

      // Extract attributes and options.
      options || (options = {});
      if (!attrs) return this;
      attrs = attrs.attributes || attrs;
      var now = this.attributes;

      // Run validation if `validate` is defined.
      if (this.validate) {
        var error = this.validate(attrs);
        if (error) {
          this.trigger('error', this, error);
          return false;
        }
      }

      // Check for changes of `id`.
      if ('id' in attrs) this.id = attrs.id;

      // Update attributes.
      for (var attr in attrs) {
        var val = attrs[attr];
        if (val === '') val = null;
        if (!_.isEqual(now[attr], val)) {
          now[attr] = val;
          if (!options.silent) {
            this._changed = true;
            this.trigger('change:' + attr, this, val);
          }
        }
      }

      // Fire the `change` event, if the model has been changed.
      if (!options.silent && this._changed) this.change();
      return this;
    },

    // Remove an attribute from the model, firing `changed` unless you choose to
    // silence it.
    unset : function(attr, options) {
      options || (options = {});
      var value = this.attributes[attr];
      delete this.attributes[attr];
      if (!options.silent) {
        this._changed = true;
        this.trigger('change:' + attr, this);
        this.change();
      }
      return value;
    },

    // Set a hash of model attributes, and sync the model to the server.
    // If the server returns an attributes hash that differs, the model's
    // state will be `set` again.
    save : function(attrs, options) {
      attrs   || (attrs = {});
      options || (options = {});
      if (!this.set(attrs, options)) return false;
      var model = this;
      var success = function(resp) {
        if (!model.set(resp.model)) return false;
        if (options.success) options.success(model, resp);
      };
      var method = this.isNew() ? 'create' : 'update';
      Backbone.sync(method, this, success, options.error);
      return this;
    },

    // Destroy this model on the server. Upon success, the model is removed
    // from its collection, if it has one.
    destroy : function(options) {
      options || (options = {});
      var model = this;
      var success = function(resp) {
        if (model.collection) model.collection.remove(model);
        if (options.success) options.success(model, resp);
      };
      Backbone.sync('delete', this, success, options.error);
      return this;
    },

    // Default URL for the model's representation on the server -- if you're
    // using Backbone's restful methods, override this to change the endpoint
    // that will be called.
    url : function() {
      var base = getUrl(this.collection);
      if (this.isNew()) return base;
      return base + '/' + this.id;
    },

    // Create a new model with identical attributes to this one.
    clone : function() {
      return new this.constructor(this);
    },

    // A model is new if it has never been saved to the server, and has a negative
    // ID.
    isNew : function() {
      return !this.id;
    },

    // Call this method to fire manually fire a `change` event for this model.
    // Calling this will cause all objects observing the model to update.
    change : function() {
      this.trigger('change', this);
      this._previousAttributes = _.clone(this.attributes);
      this._changed = false;
    },

    // Determine if the model has changed since the last `changed` event.
    // If you specify an attribute name, determine if that attribute has changed.
    hasChanged : function(attr) {
      if (attr) return this._previousAttributes[attr] != this.attributes[attr];
      return this._changed;
    },

    // Return an object containing all the attributes that have changed, or false
    // if there are no changed attributes. Useful for determining what parts of a
    // view need to be updated and/or what attributes need to be persisted to
    // the server.
    changedAttributes : function(now) {
      var old = this._previousAttributes, now = now || this.attributes, changed = false;
      for (var attr in now) {
        if (!_.isEqual(old[attr], now[attr])) {
          changed = changed || {};
          changed[attr] = now[attr];
        }
      }
      return changed;
    },

    // Get the previous value of an attribute, recorded at the time the last
    // `changed` event was fired.
    previous : function(attr) {
      if (!attr || !this._previousAttributes) return null;
      return this._previousAttributes[attr];
    },

    // Get all of the attributes of the model at the time of the previous
    // `changed` event.
    previousAttributes : function() {
      return _.clone(this._previousAttributes);
    }

  });

  // Backbone.Collection
  // -------------------

  // Provides a standard collection class for our sets of models, ordered
  // or unordered. If a `comparator` is specified, the Collection will maintain
  // its models in sort order, as they're added and removed.
  Backbone.Collection = function(models, options) {
    options || (options = {});
    if (options.comparator) {
      this.comparator = options.comparator;
      delete options.comparator;
    }
    this._boundOnModelEvent = _.bind(this._onModelEvent, this);
    this._initialize();
    if (models) this.refresh(models,true);
  };

  // Define the Collection's inheritable methods.
  _.extend(Backbone.Collection.prototype, Backbone.Events, {

    model : Backbone.Model,

    // Add a model, or list of models to the set. Pass **silent** to avoid
    // firing the `added` event for every new model.
    add : function(models, options) {
      if (!_.isArray(models)) return this._add(models, options);
      for (var i=0; i<models.length; i++) this._add(models[i], options);
      return models;
    },

    // Remove a model, or a list of models from the set. Pass silent to avoid
    // firing the `removed` event for every model removed.
    remove : function(models, options) {
      if (!_.isArray(models)) return this._remove(models, options);
      for (var i=0; i<models.length; i++) this._remove(models[i], options);
      return models;
    },

    // Get a model from the set by id.
    get : function(id) {
      return id && this._byId[id.id != null ? id.id : id];
    },

    // Get a model from the set by client id.
    getByCid : function(cid) {
      return cid && this._byCid[cid.cid || cid];
    },

    // Get the model at the given index.
    at: function(index) {
      return this.models[index];
    },

    // Force the collection to re-sort itself. You don't need to call this under normal
    // circumstances, as the set will maintain sort order as each item is added.
    sort : function(options) {
      options || (options = {});
      if (!this.comparator) throw new Error('Cannot sort a set without a comparator');
      this.models = this.sortBy(this.comparator);
      if (!options.silent) this.trigger('refresh', this);
      return this;
    },

    // Pluck an attribute from each model in the collection.
    pluck : function(attr) {
      return _.map(this.models, function(model){ return model.get(attr); });
    },

    // When you have more items than you want to add or remove individually,
    // you can refresh the entire set with a new list of models, without firing
    // any `added` or `removed` events. Fires `refresh` when finished.
    refresh : function(models, options) {
      options || (options = {});
      models = models || [];
      var collection = this;
      if (models[0] && !(models[0] instanceof Backbone.Model)) {
        models = _.map(models, function(attrs, i) {
          return new collection.model(attrs);
        });
      }
      this._initialize();
      this.add(models, {silent: true});
      if (!options.silent) this.trigger('refresh', this);
      return this;
    },

    // Fetch the default set of models for this collection, refreshing the
    // collection when they arrive.
    fetch : function(options) {
      options || (options = {});
      var collection = this;
      var success = function(resp) {
        collection.refresh(resp.models);
        if (options.success) options.success(collection, resp);
      };
      Backbone.sync('read', this, success, options.error);
      return this;
    },

    // Create a new instance of a model in this collection. After the model
    // has been created on the server, it will be added to the collection.
    create : function(model, options) {
      options || (options = {});
      if (!(model instanceof Backbone.Model)) model = new this.model(model);
      model.collection = this;
      var success = function(resp) {
        if (!model.set(resp.model)) return false;
        model.collection.add(model);
        if (options.success) options.success(model, resp);
      };
      return model.save(null, {success : success, error : options.error});
    },

    // Initialize or re-initialize all internal state. Called when the
    // collection is refreshed.
    _initialize : function(options) {
      this.length = 0;
      this.models = [];
      this._byId  = {};
      this._byCid = {};
    },

    // Internal implementation of adding a single model to the set, updating
    // hash indexes for `id` and `cid` lookups.
    _add : function(model, options) {
      options || (options = {});
      var already = this.get(model);
      if (already) throw new Error(["Can't add the same model to a set twice", already.id]);
      this._byId[model.id] = model;
      this._byCid[model.cid] = model;
      model.collection = this;
      var index = this.comparator ? this.sortedIndex(model, this.comparator) : this.length;
      this.models.splice(index, 0, model);
      model.bind('all', this._boundOnModelEvent);
      this.length++;
      if (!options.silent) this.trigger('add', model);
      return model;
    },

    // Internal implementation of removing a single model from the set, updating
    // hash indexes for `id` and `cid` lookups.
    _remove : function(model, options) {
      options || (options = {});
      model = this.get(model);
      if (!model) return null;
      delete this._byId[model.id];
      delete this._byCid[model.cid];
      delete model.collection;
      this.models.splice(this.indexOf(model), 1);
      model.unbind('all', this._boundOnModelEvent);
      this.length--;
      if (!options.silent) this.trigger('remove', model);
      return model;
    },

    // Internal method called every time a model in the set fires an event.
    // Sets need to update their indexes when models change ids.
    _onModelEvent : function(ev, model, error) {
      switch (ev) {
        case 'change':
          if (model.hasChanged('id')) {
            delete this._byId[model.previous('id')];
            this._byId[model.id] = model;
          }
          this.trigger('change', model);
          break;
        case 'error':
          this.trigger('error', model, error);
      }
    }

  });

  // Underscore methods that we want to implement on the Collection.
  var methods = ['forEach', 'each', 'map', 'reduce', 'reduceRight', 'find', 'detect',
    'filter', 'select', 'reject', 'every', 'all', 'some', 'any', 'include',
    'invoke', 'max', 'min', 'sortBy', 'sortedIndex', 'toArray', 'size',
    'first', 'rest', 'last', 'without', 'indexOf', 'lastIndexOf', 'isEmpty'];

  // Mix in each Underscore method as a proxy to `Collection#models`.
  _.each(methods, function(method) {
    Backbone.Collection.prototype[method] = function() {
      return _[method].apply(_, [this.models].concat(_.toArray(arguments)));
    };
  });

  // Backbone.View
  // -------------

  // Creating a Backbone.View creates its intial element outside of the DOM,
  // if an existing element is not provided...
  Backbone.View = function(options) {
    this._initialize(options || {});
    if (this.options.el) {
      this.el = this.options.el;
    } else {
      var attrs = {};
      if (this.id) attrs.id = this.id;
      if (this.className) attrs.className = this.className;
      this.el = this.make(this.tagName, attrs);
    }
    return this;
  };

  // jQuery lookup, scoped to DOM elements within the current view.
  // This should be prefered to global jQuery lookups, if you're dealing with
  // a specific view.
  var jQueryDelegate = function(selector) {
    return $(selector, this.el);
  };

  // Cached regex to split keys for `handleEvents`.
  var eventSplitter = /^(\w+)\s*(.*)$/;

  // Set up all inheritable **Backbone.View** properties and methods.
  _.extend(Backbone.View.prototype, {

    // The default `tagName` of a View's element is `"div"`.
    tagName : 'div',

    // Attach the jQuery function as the `$` and `jQuery` properties.
    $       : jQueryDelegate,
    jQuery  : jQueryDelegate,

    // **render** is the core function that your view should override, in order
    // to populate its element (`this.el`), with the appropriate HTML. The
    // convention is for **render** to always return `this`.
    render : function() {
      return this;
    },

    // For small amounts of DOM Elements, where a full-blown template isn't
    // needed, use **make** to manufacture elements, one at a time.
    //
    //     var el = this.make('li', {'class': 'row'}, this.model.get('title'));
    //
    make : function(tagName, attributes, content) {
      var el = document.createElement(tagName);
      if (attributes) $(el).attr(attributes);
      if (content) $(el).html(content);
      return el;
    },

    // Set callbacks, where `this.callbacks` is a hash of
    //
    // *{"event selector": "callback"}*
    //
    //     {
    //       'mousedown .title':  'edit',
    //       'click .button':     'save'
    //     }
    //
    // pairs. Callbacks will be bound to the view, with `this` set properly.
    // Uses jQuery event delegation for efficiency.
    // Omitting the selector binds the event to `this.el`.
    // `"change"` events are not delegated through the view because IE does not
    // bubble change events at all.
    handleEvents : function(events) {
      $(this.el).unbind();
      if (!(events || (events = this.events))) return this;
      for (key in events) {
        var methodName = events[key];
        var match = key.match(eventSplitter);
        var eventName = match[1], selector = match[2];
        var method = _.bind(this[methodName], this);
        if (selector === '' || eventName == 'change') {
          $(this.el).bind(eventName, method);
        } else {
          $(this.el).delegate(selector, eventName, method);
        }
      }
      return this;
    },

    // Performs the initial configuration of a View with a set of options.
    // Keys with special meaning *(model, collection, id, className)*, are
    // attached directly to the view.
    _initialize : function(options) {
      if (this.options) options = _.extend({}, this.options, options);
      if (options.model)      this.model      = options.model;
      if (options.collection) this.collection = options.collection;
      if (options.id)         this.id         = options.id;
      if (options.className)  this.className  = options.className;
      if (options.tagName)    this.tagName    = options.tagName;
      this.options = options;
    }

  });

  // Set up inheritance for the model, collection, and view.
  var extend = Backbone.Model.extend = Backbone.Collection.extend = Backbone.View.extend = function (protoProps, classProps) {
    var child = inherits(this, protoProps, classProps);
    child.extend = extend;
    return child;
  };

  // Map from CRUD to HTTP for our default `Backbone.sync` implementation.
  var methodMap = {
    'create': 'POST',
    'update': 'PUT',
    'delete': 'DELETE',
    'read'  : 'GET'
  };

  // Override this function to change the manner in which Backbone persists
  // models to the server. You will be passed the type of request, and the
  // model in question. By default, uses jQuery to make a RESTful Ajax request
  // to the model's `url()`. Some possible customizations could be:
  //
  // * Use `setTimeout` to batch rapid-fire updates into a single request.
  // * Send up the models as XML instead of JSON.
  // * Persist models via WebSockets instead of Ajax.
  //
  Backbone.sync = function(method, model, success, error) {
    $.ajax({
      url       : getUrl(model),
      type      : methodMap[method],
      data      : {model : JSON.stringify(model)},
      dataType  : 'json',
      success   : success,
      error     : error
    });
  };

})();