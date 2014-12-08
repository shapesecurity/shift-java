(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        define(['can/util/library', 'can/model'], factory);
    } else if (typeof exports === 'object') {
        module.exports = factory(require('can/util/library'), require('can/model'));
    } else if(typeof window !== 'undefined' && root && root.can) {
        factory(root.can, root.can.Model);
    }
}(this, function (can, Model) {
    var stripSlashes = function (name) {
      return name.replace(/^\/|\/$/g, '');
    };

    var socketTypes = {
      primus: 'primus/primus.js',
      socketio: 'socket.io/socket.io.js'
    };

    var SocketModel = Model.extend({
      // Here we store locks that prevent disaptching
      // created, updated and removed events multiple times
      _locks: {},
      connect: can.Deferred(),
      setup: function () {
        Model.setup.apply(this, arguments);

        if(this.resource) {
          var self = this;
          var resource = stripSlashes(this.resource);
          // Creates a resource event handler function for a given event
          var makeHandler = function(ev) {
            return function(data) {
              var id = data[self.id];
              // Check if this id is locked and contains e.g. a 'created'
              // Which means that we are getting the duplicate event from the
              // Socket remove the lock but don't dispatch the event
              if(self._locks[id] === ev + 'd') {
                delete self._locks[id];
                // If we are currently updating or removing we ignore all
                // other resource events for this model instance
              } else if(self._locks[id] !== ev) {
                // Mapping from CanJS to Feathers event name
                var modelEvent = ev === 'remove' ? 'destroyed' : ev + 'd';

                // Don't trigger the create event on the model if it's
                // already in the model store, but still trigger other events.
                if (!(ev =='create' && self.store[id])) {
                  // Trigger the event from the resource as a model event
                  var model = self.model(data);
                  if(model[modelEvent]) {
                    model[modelEvent]();
                  }
                }
              }
            };
          };

          this.connect.done(function(socket) {
            socket.on(resource + ' created', makeHandler('create'));
            socket.on(resource + ' updated', makeHandler('update'));
            socket.on(resource + ' removed', makeHandler('remove'));
          });
        }
      },

      send: function () {
        var deferred = can.Deferred();
        var args = can.makeArray(arguments);
        var name = args[0];
        var self = this;

        // Turn something like 'find' into todos::find
        args[0] = stripSlashes(this.resource) + '::' + name;

        // Add the success callback which just resolves or fails the Deferred
        args.push(function (error, data) {
          if (error) {
            return deferred.reject(error);
          }

          // Set the lock so that we don't dispatch duplicate events
          if(name === 'create' || name === 'remove' || name === 'update') {
            self._locks[data[self.id]] = name + 'd';
          }

          deferred.resolve(data);
        });

        this.connect.done(function(socket) {
          socket.emit.apply(socket, args);
        });

        return deferred;
      },

      findAll: function (params) {
        return this.send('find', params || {});
      },

      findOne: function (params) {
        return this.send('get', params.id, params);
      },

      create: function (attrs, params) {
        return this.send('create', attrs, params || {});
      },

      update: function (id, attrs, params) {
        var ev = this._locks[id] = 'update';
        return this.send(ev, id, attrs, params || {});
      },

      destroy: function (id, attrs, params) {
        var ev = this._locks[id] = 'remove';
        return this.send(ev, id, params || {});
      }
    }, {});

    can.Feathers = {
      Model: SocketModel,

      model: function (resource) {
        return SocketModel.extend({
          resource: resource
        }, {});
      },

      connect: function(options, queryObj) {
        options = options || {};

        // Load the Socket library (SocketIO or Primus) into a Script tag
        var dfd = SocketModel.connect;

        // We should only connect once
        if(dfd.state() !== 'resolved') {
          var script = document.createElement('script');
          // Resolves the Deferred when the script is loaded
          var resolve = function () {
            // Connect to the socket via the library given
            var provider = options.type === 'primus' ? Primus : io;
            // Use the query object, if provided.
            if (queryObj) {
              dfd.resolve(provider.connect(options.host, queryObj));
            } else {
              dfd.resolve(provider.connect(options.host));
            }
          };

          script.setAttribute('type', 'text/javascript');
          script.setAttribute('src', (options.host || '') +
            '/' + socketTypes[options.type || 'socketio']);

          script.onload = resolve;

          script.onreadystatechange = function () {
            if (this.readyState === 'complete') {
              resolve();
            }
          };

          document.body.appendChild(script);
        }

        return dfd;
      }
    };

    return can.Feathers;
}));
