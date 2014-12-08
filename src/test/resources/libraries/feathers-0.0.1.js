/* global define, exports, module, window */
(function (can, undefined) {
  /**
   * AMD module shim
   * @param {Function} fn The callback function
   */
  var def = (typeof define === "undefined") ? function (deps, fn) {
    var res = fn(can, can.Model);
    if (typeof exports === "undefined" && typeof window !== "undefined") {
      can.Feathers = res;
    } else {
      module.exports = res;
    }
  } : define;

  var stripSlashes = function (name) {
    return name.replace(/^\/|\/$/g, '');
  };

  var socketTypes = {
    primus: 'primus/primus.js',
    socketio: 'socket.io/socket.io.js'
  };

  def(['can/util', 'can/model'], function (can, Model) {
    var SocketModel = Model.extend({
      // Here we store locks that prevent disaptching
      // created, updated and removed events multiple times
      _locks: {},
      connect: can.Deferred(),
      setup: function () {
        Model.setup.apply(this, arguments);

        if(this.service) {
          var self = this;
          var service = stripSlashes(this.service);
          // Creates a service event handler function for a given event
          var makeHandler = function(ev) {
            return function(data) {
              var id = data[self.id];
              // Check if this id is locked and contains e.g. a 'created'
              // Which means that we are getting the duplicate event from the
              // Socket remove the lock but don't dispatch the event
              if(self._locks[id] === ev + 'd') {
                delete self._locks[id];
                // If we are currently updating or removing we ignore all
                // other service events for this model instance
              } else if(self._locks[id] !== ev) {
                // Mapping from CanJS to Feathers event name
                var modelEvent = ev === 'remove' ? 'destroyed' : ev + 'd';
                // Trigger the event from the service as a model event
                var model = self.model(data);

                if(model[modelEvent]) {
                  model[modelEvent]();
                }
              }
            }
          }

          this.connect.done(function(socket) {
            socket.on(service + ' created', makeHandler('create'));
            socket.on(service + ' updated', makeHandler('update'));
            socket.on(service + ' removed', makeHandler('remove'));
          });
        }
      },

      send: function () {
        var deferred = can.Deferred();
        var args = can.makeArray(arguments);
        var name = args[0];
        var self = this;

        // Turn something like 'find' into todos::find
        args[0] = stripSlashes(this.service) + '::' + name;

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

      destroy: function (id, params) {
        var ev = this._locks[id] = 'remove';
        return this.send(ev, id, params || {});
      }
    }, {});

    return {
      Model: SocketModel,

      model: function (service) {
        return SocketModel.extend({
          service: service
        }, {});
      },

      connect: function(options) {
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
            dfd.resolve(provider.connect(options.host));
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
  });
})(window.can);
