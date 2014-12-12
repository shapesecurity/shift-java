/**
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * See the License for the specific language governing rights and
 * limitations under the License.
 * 
 * The Original Code is soma.js.
 * 
 * The Initial Developer of the Original Code is Romuald Quantin (original actionscript version).
 * romu@soundstep.com (www.soundstep.com).
 * 
 * Javascript port of the AS3 MVC framework SomaCore (http://www.soundstep.com/blog/downloads/somacore/).
 * The Initial Developer of the port is Henry Schmieder (javascript version).
 *
 * @author Henry Romuald Quantin
 * @author Henry Schmieder
 *
 * Initial Developer are Copyright (C) 2008-2012 Soundstep. All Rights Reserved.
 * All Rights Reserved.
 * 
 */

(function() {

	/** @namespace Global namespace. */
	soma = {};
	/** framework version */
	soma.version = "1.0.1";
	/** framework type */
	soma.type = "native";

	/**
	 * @name bind
	 * @namespace Creates a new function that, when called, itself calls this function in the context of the provided this value, with a given sequence of arguments preceding any provided when the new function was called.
	 * @param arguments
	 * @example
myfunction.bind(this);
	*/
	if (!Function.prototype.bind) {
		Function.prototype.bind = function bind(that) {
			var target = this;
			if (typeof target != "function") {
				throw new Error("Error, you must bind a function.");
			}
			var args = Array.prototype.slice.call(arguments, 1); // for normal call
			var bound = function () {
				if (this instanceof bound) {
					var F = function(){};
					F.prototype = target.prototype;
					var self = new F;
					var result = target.apply(
						self,
						args.concat(Array.prototype.slice.call(arguments))
					);
					if (Object(result) === result) {
						return result;
					}
					return self;
				} else {
					return target.apply(
						that,
						args.concat(Array.prototype.slice.call(arguments))
					);
				}
			};
			return bound;
		};
	}

	/**
	 * @static
	 * @description Apply an object properties on another object
	 * @param {object} target An object to receive properties
	 * @param {object} extension An object holding the properties
	 * @example
// example with objects
var objectTarget = {};
var objectExtension = {
	name: "John",
	say: function() {
		alert("My name is " + this.name);
	}
};
soma.applyProperties(objectTarget, objectExtension);
objectTarget.say();

// example with prototype (no inheritance)
var Person = function(name) {
	this.name = name;
}
Person.prototype = {
	say: function() {
	    alert("My name is " + this.name);
	}
}
var Man = function(name){
	this.name = name
};
soma.applyProperties(Man.prototype, Person.prototype);
var one = new Person("Person");
one.say();
var john = new Man("John");
john.say();

	 */
	soma.applyProperties = function(target, extension) {
		for (var prop in extension) {
			target[prop] = extension[prop];
		}
	};

	/**
	 * @static
	 * @description Apply inheritance to a function
	 * @param {function} target A function to receive inheritance
	 * @param {object} obj An object holding the properties to inherit from
	 * @example
// Person constructor
var Person = function(name){
	this.name = name;
	alert("Person constructor: Person created with name " + this.name);
};
// Person methods
Person.prototype = {
	say: function() {
	    alert("Person method: My name is " + this.name);
	}
}
// Man constructor
var Man = function(name) {
	// call super constructor
	Person.call(this, name);
	alert("Man constructor: Man created with name " + this.name);
}
// Man methods
Man.prototype = {
	say: function() {
	    // call super method "say"
	    Person.prototype.say.call(this);
	    alert("Man method: I'm a man and my name is " + this.name);
	}
}
// Man inheritance from Person
soma.inherit(Person, Man.prototype);
// create Person
var one = new Person("Person");
one.say();
// create Man
var john = new Man("John");
john.say();
	 */
	soma.inherit = function(target, obj) {
		var subclass;
		if (obj && obj.hasOwnProperty('constructor')) {
			// use constructor if defined
			subclass = obj.constructor;
		} else {
			// call the super constructor
			subclass = function(){
				return target.apply(this, arguments);
			};
		}
		// add super properties
		soma.applyProperties(subclass.prototype, target.prototype);
		// set the prototype chain to inherit from the parent without calling parent's constructor
		var chain = function(){};
		chain.prototype = target.prototype;
		subclass.prototype = new chain();
		// add obj properties
		if (obj) soma.applyProperties(subclass.prototype, obj, target.prototype);
		// point constructor to the subclass
		subclass.prototype.constructor = subclass;
		// set super class reference
		subclass.parent = target.prototype;
		// add extend shortcut
		subclass.extend = function(obj) {
			return soma.inherit(subclass, obj);
		};
		return subclass;
	};

	/**
	 * @static
	 * @description Create a function that extends the function it is called from
	 * @param {object} obj Object containing the extension
	 * @example
var Person = soma.extend({
	name: null,
	// Person constructor
	constructor: function(name) {
	    this.name = name;
	    alert("Person constructor: Person created with name " + this.name);
	},
	// Person methods
	say: function() {
	    alert("Person method: My name is " + this.name);
	}
});

var Man = Person.extend({
	// Man constructor
	constructor: function(name) {
	    // call super constructor
	    Person.call(this, name);
	    alert("Man constructor: Man created with name " + this.name);
	},
	// Man methods
	say: function() {
	    // call super method "say"
	    Person.prototype.say.call(this);
	    alert("Man method: I'm a man and my name is " + this.name);
	}
});

// create Person
var one = new Person("Person");
one.say();
// create Man
var john = new Man("John");
john.say();
	 */
	soma.extend = function(obj) {
		return soma.inherit(function(){}, obj);
	};

	/**
	 * @private
	 */
	var SomaSharedCore = soma.extend({
		createPlugin: function() {
			this.instance.createPlugin.apply(this.instance, arguments);
		},
		dispatchEvent: function() {
			this.instance.dispatchEvent.apply(this.instance, arguments);
		},
		addEventListener: function() {
			this.instance.addEventListener.apply(this.instance, arguments);
		},
		removeEventListener: function() {
			this.instance.removeEventListener.apply(this.instance, arguments);
		},
		hasCommand: function(commandName) {
			return this.instance.hasCommand(commandName);
		},
		getCommand: function(commandName) {
			return this.instance.getCommand(commandName);
		},
		getCommands: function() {
			return this.instance.getCommands();
		},
		addCommand: function(commandName, commandClass) {
			this.instance.controller.addCommand(commandName, commandClass);
		},
		removeCommand: function(commandName) {
			this.instance.controller.removeCommand(commandName);
		},
		hasWire: function(wireName) {
			return this.instance.hasWire(wireName);
		},
		getWire: function(wireName) {
			return this.instance.getWire(wireName);
		},
		addWire: function(wireName, wire) {
			return this.instance.addWire(wireName, wire);
		},
		removeWire: function(wireName) {
			this.instance.removeWire(wireName);
		},
		hasModel: function(modelName) {
			return this.instance.hasModel(modelName);
		},
		getModel: function(modelName) {
			return this.instance.getModel(modelName);
		},
		addModel: function(modelName, model) {
			return this.instance.addModel(modelName, model);
		},
		removeModel: function(modelName) {
			this.instance.removeModel(modelName);
		},
		getSequencer: function(event) {
			return !!this.instance.controller ? this.instance.controller.getSequencer(event) : null;
		},
		stopSequencerWithEvent: function(event) {
			return !!this.instance.controller ? this.instance.controller.stopSequencerWithEvent(event) : null;
		},
		stopSequencer: function(event) {
			if (this.instance.controller) {
				return this.instance.controller.stopSequencer(event);
			}
		},
		stopAllSequencers: function() {
			if (this.instance.controller) {
				this.instance.controller.stopAllSequencers();
			}
		},
		isPartOfASequence: function(event) {
			return !!this.instance.controller ? this.instance.controller.isPartOfASequence(event) : false;
		},
		getLastSequencer: function() {
			return !!this.instance.controller ? this.instance.controller.getLastSequencer() : null;
		},
		getRunningSequencers: function() {
			return !!this.instance.controller ? this.instance.controller.getRunningSequencers() : null;
		},
		hasView: function(viewName) {
			return this.instance.hasView(viewName);
		},
		getView: function(viewName) {
			return this.instance.getView(viewName);
		},
		addView: function(viewName, view) {
			return this.instance.addView(viewName, view);
		},
		removeView: function(viewName) {
			this.instance.removeView(viewName);
		}
	});

	/**
	 * Provides the functionality to autobind with implicit need to keep object scope like event listeners and handlers/callbacks ending with *Listener or *Handler.
	 * Wires and Mediators as framework objects are implementing instance scope autobinding upon registration.
	 * @description Creates a new AutoBind class.
	 * @example
// wire not autobound
var MyWire = soma.Wire.extend({
	init: function() {
		this.addEventListener("eventType", this.eventHandler.bind(this));
	},
	eventHandler: function(event) {
		// "this" keyword is this wire.
	}
});

// wire autobound
var MyWire = soma.Wire.extend({
	shouldAutobind: true,
	init: function() {
		this.addEventListener("eventType", this.eventHandler);
	},
	eventHandler: function(event) {
		// "this" keyword is this wire.
	}
});

// custom class autobind
var MyClassToAutobind = soma.extend({
	shouldAutobind: true
});
soma.applyProperties(MyClassToAutobind.prototype, soma.AutoBind);
	 */
	soma.AutoBind = {
		/** @private */
		blackList: ["initialize", "parent", "constructor", "$constructor", "addEventListener", "removeEventListener"],
		autobind: function() {
			if (this.wasAutoBound) {
				return;
			}
			var o = this;
			var ab = o["AutoBindPattern"];
			var coreAb = "([lL]istener|[hH]andler|[cB]allback)$";
			if (!ab) {
				ab = coreAb;
			} else {
				ab = coreAb + "|" + ab;
			}
			for (var k in o) {
				if (typeof o[k] == "function") {
					if (this._autobindIsBlacklisted(k)) {
						continue;
					}
					if (!k.match(ab)) {
						continue;
					}
					o[k] = o[k].bind(o);
				}
			}
		},
		/** @private */
		_autobindIsBlacklisted: function(name) {
			var bl = this.blackList;
			for (var i = 0; i < bl.length; i++) {
				if (bl[i] == name) {
					return true;
				}
			}
			return false;
		}
	};
	/**
	 * @class Class that will be instantiated when a registered event (command) is dispatched, the framework will automatically call the execute method.
	 * @description Creates a new Command, should be instantiated by the framework only.
	 * @borrows soma.Application#createPlugin
	 * @borrows soma.Application#addWire
	 * @borrows soma.Application#getWire
	 * @borrows soma.Application#getWires
	 * @borrows soma.Application#hasWire
	 * @borrows soma.Application#removeWire
	 * @borrows soma.Application#addModel
	 * @borrows soma.Application#getModel
	 * @borrows soma.Application#getModels
	 * @borrows soma.Application#hasModel
	 * @borrows soma.Application#removeModel
	 * @borrows soma.Application#addView
	 * @borrows soma.Application#getView
	 * @borrows soma.Application#getViews
	 * @borrows soma.Application#hasView
	 * @borrows soma.Application#removeView
	 * @borrows soma.Application#addCommand
	 * @borrows soma.Application#getCommand
	 * @borrows soma.Application#getCommands
	 * @borrows soma.Application#hasCommand
	 * @borrows soma.Application#removeCommand
	 * @borrows soma.Application#getSequencer
	 * @borrows soma.Application#stopSequencerWithEvent
	 * @borrows soma.Application#stopSequencer
	 * @borrows soma.Application#stopAllSequencers
	 * @borrows soma.Application#isPartOfASequence
	 * @borrows soma.Application#getLastSequencer
	 * @borrows soma.Application#getRunningSequencers
	 * @borrows soma.EventDispatcher#addEventListener
	 * @borrows soma.EventDispatcher#removeEventListener
	 * @borrows soma.EventDispatcher#hasEventListener
	 * @borrows soma.EventDispatcher#dispatchEvent
	 * @example
// register and dispatch a command
this.addCommand("myEventType", MyCommand);
this.dispatchEvent(new soma.Event("myEventType"));
	 * @example
// create a command class
var MyCommand = soma.Command.extend({
	execute: function(event) {
		// access framework elements examples:
		// alert(this.instance)
		// alert(this.getWire("myWireName"))
		// this.addModel("myModelName", new Model());
	}
});
	 */
	soma.Command = SomaSharedCore.extend(
		/** @lends soma.Command.prototype */
		{
		instance: null,
		registerInstance: function(instance) {
			this.instance = instance;
		},
		/**
		 * Method called by the framework when the command is executed by the framework. All the framework elements are accessible in this method (wires, commands, models, views, instance of the framework and body).
		 * @param {soma.Event} event The event dispatched to triggered the command.
		 * @example
var MyCommand = soma.Command.extend({
	execute: function(event) {
		// alert(event.type)
	}
});
		 */
		execute: function(event) {

		},
		toString: function() {
			return "[soma.Command]";
		}
	});

	/** @private */
	var SequenceCommandProxy = soma.extend({
		event:null,
		sequenceId:null,
		constructor: function(event) {
			this.event = event;
		}
	});

	/**
	 * @class The SequenceCommand class is used to execute a list of commands one after the other. The commands added can be asynchronous or synchronous.
	 * @description Creates a new SequenceCommand, should be instantiated by the framework only.
	 * @param {string} id The id of the sequence.
	 * @extends soma.Command
	 * @borrows soma.Command#execute
	 * @borrows soma.Application#createPlugin
	 * @borrows soma.Application#addWire
	 * @borrows soma.Application#getWire
	 * @borrows soma.Application#getWires
	 * @borrows soma.Application#hasWire
	 * @borrows soma.Application#removeWire
	 * @borrows soma.Application#addModel
	 * @borrows soma.Application#getModel
	 * @borrows soma.Application#getModels
	 * @borrows soma.Application#hasModel
	 * @borrows soma.Application#removeModel
	 * @borrows soma.Application#addView
	 * @borrows soma.Application#getView
	 * @borrows soma.Application#getViews
	 * @borrows soma.Application#hasView
	 * @borrows soma.Application#removeView
	 * @borrows soma.Application#addCommand
	 * @borrows soma.Application#getCommand
	 * @borrows soma.Application#getCommands
	 * @borrows soma.Application#hasCommand
	 * @borrows soma.Application#removeCommand
	 * @borrows soma.Application#getSequencer
	 * @borrows soma.Application#stopSequencerWithEvent
	 * @borrows soma.Application#stopSequencer
	 * @borrows soma.Application#stopAllSequencers
	 * @borrows soma.Application#isPartOfASequence
	 * @borrows soma.Application#getLastSequencer
	 * @borrows soma.Application#getRunningSequencers
	 * @borrows soma.EventDispatcher#addEventListener
	 * @borrows soma.EventDispatcher#removeEventListener
	 * @borrows soma.EventDispatcher#hasEventListener
	 * @borrows soma.EventDispatcher#dispatchEvent
	 * @example
this.addCommand("doSomethingAsync", CommandASyncExample);
this.addCommand("doSomethingElseAsync", CommandASyncExample);
this.addCommand("doSomething", CommandExample);
		 * @example
this.addCommand("excuteMySequence", SequenceCommandExample);
this.dispatchEvent(new MyEvent("excuteMysequence"));
		 * @example
var SequenceTestCommand = soma.SequenceCommand.extend({
	constructor: function() {
		soma.SequenceCommand.call(this, "sequencer.test.id");
	},
	initializeSubCommands: function() {
		 this.addSubCommand(new soma.Event("doSomethingAsync"));
		 this.addSubCommand(new soma.Event("doSomethingElseAsync"));
		 this.addSubCommand(new soma.Event("doSomething"));
	}
});
		 * @example
var CommandExample = soma.Command.extend{{
	execute: function(event) {
		// do something
		if(this.isPartOfASequence(event)) {
			// execute the next command
			this.getSequencer(event).executeNextCommand();
		}
	}
});
	 */
	soma.SequenceCommand = soma.Command.extend(
		/** @lends soma.SequenceCommand.prototype */
		{
		/** {array} List of commands */
		commands: null,
		/** {soma.Event} The current command in progress. */
		currentCommand: null,
		/** {string} The id of the sequence. */
		id:null,
		constructor: function(id) {
			if (id == null) {
				throw new Error("SequenceCommand Children expect an unique id as constructor arg");
			}
			this.commands = [];
			this.id = id;
			soma.Command.call(this);
		},
		registerInstance: function(instance) {
			this.instance = instance;
			this.initializeSubCommands();
		},
			/**
			 * Method that you can overwrite to add commands to the sequence command.
			 * @example
var SequenceTestCommand = soma.SequenceCommand.extend({
	constructor: function() {
		soma.SequenceCommand.call(this, "sequencer.test.id");
	},
	initializeSubCommands: function() {
		 this.addSubCommand(new soma.Event("doSomethingAsync"));
		 this.addSubCommand(new soma.Event("doSomethingElseAsync"));
		 this.addSubCommand(new soma.Event("doSomething"));
	}
});
		 */
		initializeSubCommands: function() {
			throw new Error("Subclasses of SequenceCommand must implement initializeSubCommands()");
		},
		/**
		 * Add a command to the list of commands to execute one after the other.
		 * @param {soma.Event} event The event that will trigger a command.
		 * @example
this.addSubCommand(new soma.Event("eventType"));
		 */
		addSubCommand: function(event) {
			var c = new SequenceCommandProxy(event);
			this.commands.push(c);
			this.instance.controller.registerSequencedCommand(this, c);
		},
		/**
		 * Should not be overridden in a sequence command class.
		 */
		execute: function(event) {
			if (this.commands == null || this.commands.length === 0) {
				return;
			}
			this.currentCommand = this.commands.shift();
			if (this.hasCommand(this.currentCommand.event.type)) {
				this.dispatchEvent(this.currentCommand.event);
			}
		},
			/**
			 * Method used to execute the next command in the list of subcommands. If a command is part of a sequence, you must call the executeNextCommand in the command itself.
			 * @example
var CommandExample = soma.Command.extend({
	execute: function(event) {
		// do something
		if(this.isPartOfASequence(event)) {
			// execute the next command
			this.getSequencer(event).executeNextCommand();
		}
	}
});
		 */
		executeNextCommand: function() {
			if (this.commands == null) {
				return;
			}
			this.instance.controller.unregisterSequencedCommand(this, this.currentCommand.event.type);
			if (this.commands.length > 0) {
				this.execute(this.commands[0].event);
			} else {
				this.commands = null;
				this.currentCommand = null;
			}
		},
		/**
		 * Gets the numbers of commands to be executed.
		 * @return {int}
		 */
		getLength: function() {
			if (this.commands == null) {
				return -1;
			}
			return this.commands.length;
		},
		/**
		 * Stops the current sequence.
		 * @return Boolean
		 */
		stop: function() {
			this.commands = null;
			this.commands = null;
			this.currentCommand = null;
			return this.instance.controller.unregisterSequencer(this);
		},
		/**
		 * Returns the current command in progress.
		 * @return {soma.SequenceCommand}
		 */
		getCurrentCommand: function() {
			return this.currentCommand;
		},
		/**
		 * Gets the list of commands to be executed.
		 * @return {array} An array of commands.
		 */
		getCommands: function() {
			return this.commands;
		},
		toString: function() {
			return "[soma.SequenceCommand]";
		}

	});

	/**
	 * @class The ParallelCommand class is used to execute a list of commands, they will all start at the same time.
	 * @description Creates a new ParallelCommand, should be instantiated by the framework only.
	 * @extends soma.Command
	 * @borrows soma.Command#execute
	 * @borrows soma.Application#createPlugin
	 * @borrows soma.Application#addWire
	 * @borrows soma.Application#getWire
	 * @borrows soma.Application#getWires
	 * @borrows soma.Application#hasWire
	 * @borrows soma.Application#removeWire
	 * @borrows soma.Application#addModel
	 * @borrows soma.Application#getModel
	 * @borrows soma.Application#getModels
	 * @borrows soma.Application#hasModel
	 * @borrows soma.Application#removeModel
	 * @borrows soma.Application#addView
	 * @borrows soma.Application#getView
	 * @borrows soma.Application#getViews
	 * @borrows soma.Application#hasView
	 * @borrows soma.Application#removeView
	 * @borrows soma.Application#addCommand
	 * @borrows soma.Application#getCommand
	 * @borrows soma.Application#getCommands
	 * @borrows soma.Application#hasCommand
	 * @borrows soma.Application#removeCommand
	 * @borrows soma.Application#getSequencer
	 * @borrows soma.Application#stopSequencerWithEvent
	 * @borrows soma.Application#stopSequencer
	 * @borrows soma.Application#stopAllSequencers
	 * @borrows soma.Application#isPartOfASequence
	 * @borrows soma.Application#getLastSequencer
	 * @borrows soma.Application#getRunningSequencers
	 * @borrows soma.EventDispatcher#addEventListener
	 * @borrows soma.EventDispatcher#removeEventListener
	 * @borrows soma.EventDispatcher#hasEventListener
	 * @borrows soma.EventDispatcher#dispatchEvent
	 * @example
this.addCommand("doSomethingAsync", CommandASyncExample);
this.addCommand("doSomethingElseAsync", CommandASyncExample);
this.addCommand("doSomething", CommandExample);
	 * @example
this.addCommand("excuteMyCommands", ParallelCommandExample);
this.dispatchEvent(new MyEvent("excuteMyCommands"));
	 * @example
var ParallelTestCommand = soma.ParallelCommand.extend({
	initializeSubCommands: function(){
		this.addSubCommand(new soma.Event("doSomethingAsync"));
		this.addSubCommand(new soma.Event("doSomethingElseAsync"));
		this.addSubCommand(new soma.Event("doSomething"));
	}
});
	 */
	soma.ParallelCommand = soma.Command.extend(
		/** @lends soma.ParallelCommand.prototype */
		{
		commands:null,
		constructor: function() {
			this.commands = [];
		},
		registerInstance: function(instance) {
			this.instance = instance;
			this.initializeSubCommands();
		},
		/**
		 * Method that you can overwrite to add commands to the parallel command.
		 * @example
var ParallelTestCommand = soma.ParallelCommand.extend({
	initializeSubCommands: function(){
		this.addSubCommand(new soma.Event("doSomethingAsync"));
		this.addSubCommand(new soma.Event("doSomethingElseAsync"));
		this.addSubCommand(new soma.Event("doSomething"));
	}
});
		 */
		initializeSubCommands: function() {
			throw new Error("Subclasses of ParallelCommand must implement initializeSubCommands()");
		},
		/**
		 * Add a command to the list of commands to execute in parallel.
		 * @param {soma.Event} event The event that will trigger a command.
		 * @example
this.addSubCommand(new soma.Event("eventType"));
		 */
		addSubCommand: function(e) {
			this.commands.push(e);
		},
		/**
		 * Should not be overridden in a parallel command class.
		 */
		execute: function() {
			while (this.commands.length > 0) {
				var c = this.commands.shift();
				if (this.hasCommand(c.type)) {
					this.dispatchEvent(c);
				}
			}
			this.commands = null;
		},
		/**
		 * Gets the numbers of commands to be executed.
		 * @return {int}
		 */
		getLength: function() {
			return this.commands != null ? this.commands.length : -1;
		},
		/**
		 * Gets the list of commands to be executed.
		 * @return {array} An array of commands.
		 */
		getCommands: function() {
			return this.commands;
		},
		toString: function() {
			return "[soma.ParallelCommand]";
		}
	});

	/**
	 * @class
	 * A Wire is a class that will hold the logic of the Application.<br/>
	 * Wires can be used in many ways, depending on how you want to manage your views, commands and models. A wire can be used as a manager and handle many models, views or other wires. A wire can also be used in a one-to-one way (as a proxy), a single wire that handles a single view, a single wire that handles a single model, and so on.<br/>
	 * Wires can be flexible or rigid depending on how your build your application.<br/>
	 * A wire has access to everything in the framework: you can create views, add and dispatch commands, create models, access to the framework instance, access to the stage, and so on.<br/>
	 * A wire can also be in control of the commands that are dispatched by listening to them and even stop their execution if needed (see the examples in this page).<br/>
	 * @description Create an instance of a Wire class.
	 * @param {string} name The name of the wire.
	 * @borrows soma.Application#createPlugin
	 * @borrows soma.Application#addWire
	 * @borrows soma.Application#getWire
	 * @borrows soma.Application#getWires
	 * @borrows soma.Application#hasWire
	 * @borrows soma.Application#removeWire
	 * @borrows soma.Application#addModel
	 * @borrows soma.Application#getModel
	 * @borrows soma.Application#getModels
	 * @borrows soma.Application#hasModel
	 * @borrows soma.Application#removeModel
	 * @borrows soma.Application#addView
	 * @borrows soma.Application#getView
	 * @borrows soma.Application#getViews
	 * @borrows soma.Application#hasView
	 * @borrows soma.Application#removeView
	 * @borrows soma.Application#addCommand
	 * @borrows soma.Application#getCommand
	 * @borrows soma.Application#getCommands
	 * @borrows soma.Application#hasCommand
	 * @borrows soma.Application#removeCommand
	 * @borrows soma.Application#getSequencer
	 * @borrows soma.Application#stopSequencerWithEvent
	 * @borrows soma.Application#stopSequencer
	 * @borrows soma.Application#stopAllSequencers
	 * @borrows soma.Application#isPartOfASequence
	 * @borrows soma.Application#getLastSequencer
	 * @borrows soma.Application#getRunningSequencers
	 * @borrows soma.EventDispatcher#addEventListener
	 * @borrows soma.EventDispatcher#removeEventListener
	 * @borrows soma.EventDispatcher#hasEventListener
	 * @borrows soma.EventDispatcher#dispatchEvent
	 * @example
// add a wire to the framework
this.addWire("myWireName", new MyWire());
	 * @example
// remove a wire from the framework
this.removeWire("myWireName");
	 * @example
// retrieve a wire
var wire = this.getWire("myWireName");
	 * @example
// create a wire
var MyWire = soma.Wire.extend({
	init: function() {
		// starting point
	},
	dispose: function() {
		// called when the wire is removed from the framework
	}
});
MyWire.NAME = "Wire::MyWire";
	 * @example
// listening to a command in a wire.
var MyWire = soma.Wire.extend({
	init: function() {
		this.addEventListener("eventType", this.eventHandler);
	},
	eventHandler: function(event) {

	}
});
MyWire.NAME = "Wire::MyWire";
	 * @example
// Stopping the execution of a command in a wire.
// The cancelable property of the event need to be set to true when you dispatch it.
// Any command can be stopped using the native event built-in method: preventDefault.
var MyWire = soma.Wire.extend({
	init: function() {
		this.addEventListener("eventType", this.eventHandler);
	},
	eventHandler: function(event) {
		// stops the execution of the command
		event.preventDefault();
	}
});
MyWire.NAME = "Wire::MyWire";
	 */
	soma.Wire = SomaSharedCore.extend(
		/** @lends soma.Wire.prototype */
		{
		/** {string} The name of the wire */
		name: null,
		instance: null,
		constructor: function(name) {
			this.name = name;
		},
		registerInstance: function(instance) {
			this.instance = instance;
		},
		/**
		 * Method that can you can override, called when the wire has been registered to the framework.
		 */
		init: function() {

		},
		/**
		 * Method that can you can override, called when the wire has been removed from the framework.
		 */
		dispose: function() {

		},
		/**
		 * Retrieves the name of the wire.
		 * @returns {string} The name of the wire.
		 */
		getName: function() {
			return this.name;
		},
		/**
		 * Sets the name of the wire.
		 * @param {string} The name of the wire.
		 */
		setName: function(name) {
			this.name = name;
		},
		toString: function() {
			return "[soma.Wire]";
		}
	});
	soma.applyProperties(soma.Wire.prototype, soma.AutoBind);

	/**
	 * @class soma.IDisposable
	 * @description This interface provides the method that can be called to dispose the elements created inside this instance.
	 * @example
var MyDisposableClass = soma.IDisposable.extend({
	dispose: function() {
	},
});

var MyDisposableClass = soma.extend({
	dispose: function() {
	},
});
soma.applyProperties(MyDisposableClass.prototype, soma.IDisposable.prototype);
	*/
	soma.IDisposable = soma.extend(
		/** @lends soma.IDisposable.prototype **/
		{
		/**
		 * Method will dispose the elements created.
		 * @name dispose
		 */
		dispose: function() {}
	});

	/**
	 * @class soma.SomaController
	 * @description
	 * The SomaController class handles the commands added to the framework and the events dispatched from either a display list or a framework element (instance of the framework, commands or wires).<br/>
	 * All the events dispatched with a property bubbles set to false will be ignored, that is why the event mapped to a command class must have this property set to true.<br/>
	 * You can add commands, remove commands and dispatch commands from: the framework instance, the body, a view, a wire, a command or a model.<br/>
	 * You can create 4 types of commands: synchronous (Command), asynchronous, parallel (ParallelCommand) and sequence (SequenceCommand). See each class for a detailed explanation and examples.<br/>
	 * You can use the properties of a custom event to send parameters and receive them in the commands.<br/>
	 * @borrows soma.Application#addCommand
	 * @borrows soma.Application#getCommand
	 * @borrows soma.Application#getCommands
	 * @borrows soma.Application#hasCommand
	 * @borrows soma.Application#removeCommand
	 * @borrows soma.Application#getSequencer
	 * @borrows soma.Application#stopSequencerWithEvent
	 * @borrows soma.Application#stopSequencer
	 * @borrows soma.Application#stopAllSequencers
	 * @borrows soma.Application#isPartOfASequence
	 * @borrows soma.Application#getLastSequencer
	 * @borrows soma.Application#getRunningSequencers
	 * @example
this.addCommand("eventType", CommandExample);
 this.dispatchEvent(new soma.Event("eventType"));
 this.removeCommand("eventType");
	 */
	soma.SomaController = soma.extend(
		/** @lends soma.SomaController.prototype */
		{
		instance:null,
		constructor: function(instance) {
			this.boundInstance = this.instanceHandler.bind(this);
			this.boundDomtree = this.domTreeHandler.bind(this);
			this.commands = {};
			this.sequencers = {};
			this.sequencersInfo = {};
			this.lastEvent = null;
			this.lastSequencer = null;
			this.instance = instance;
		},
		/** @private */
		addInterceptor: function(commandName) {
			if (!soma) {
				throw new Error("soma package has been overwritten by local variable");
			}
			if (this.instance.body.addEventListener) {
				this.instance.body.addEventListener(commandName, this.boundDomtree, true);
			}
			this.instance.addEventListener(commandName, this.boundInstance, -Number.MAX_VALUE);
		},
		/** @private */
		removeInterceptor: function(commandName) {
			if (this.instance.body.removeEventListener) {
				this.instance.body.removeEventListener(commandName, this.boundDomtree, true);
			}
			this.instance.removeEventListener(commandName, this.boundInstance);
		},
		/** @private */
		executeCommand: function(e) {
			var commandName = e.type;
			if (this.hasCommand(commandName)) {
				var command = new this.commands[commandName]();
				command.registerInstance(this.instance);
				command.execute(e);
			}
		},
		/** @private */
		registerSequencedCommand: function(sequencer, c) {
			if (!( c instanceof SequenceCommandProxy )) {
				throw new Error("capsulate sequence commands in SequenceCommandProxy objects!");
			}
			var s = this.sequencersInfo;
			if (s[sequencer.id] == null || this.sequencers[sequencer.id] == null) {
				this.lastSequencer = sequencer;
				s[sequencer.id] = [];
				this.sequencers[sequencer.id] = sequencer;
			}
			c.sequenceId = sequencer.id;
			s[sequencer.id].push(c);
		},
		/** @private */
		unregisterSequencedCommand: function(sequencer, commandName) {
			if (typeof commandName != "string") {
				throw new Error("Controller::unregisterSequencedCommand() expects commandName to be of type String, given:" + commandName);
			}
			var s = this.sequencersInfo;
			if (s[sequencer.id] != null && s[sequencer.id] != undefined) {
				var len = s[sequencer.id].length;
				for (var i = 0; i < len; i++) {
					if (s[sequencer.id][i].event.type == commandName) {
						s[sequencer.id][i] = null;
						s[sequencer.id].splice(i, 1);
						if (s[sequencer.id].length == 0) {
							s[sequencer.id] = null;
							delete s[sequencer.id];
						}
						break;
					}
				}
			}
		},
		/** @private */
		unregisterSequencer: function(sequencer) {
			var s = this.sequencers;
			if (s[sequencer.id] != null && s[sequencer.id] != undefined) {
				s[sequencer.id] = null;
				delete s[sequencer.id];
				s = this.sequencersInfo;
				if (s[sequencer.id] != null) {
					var len = s[sequencer.id].length;
					for (var i = 0; i < len; i++) {
						s[sequencer.id][i] = null;
					}
					s[sequencer.id] = null;
					delete s[sequencer.id];
					return true;
				}
			}
			return false;
		},
		hasCommand: function(commandName) {
			return this.commands[ commandName ] != null;
		},
		getCommand: function(commandName) {
			if (this.hasCommand(commandName)) {
				return this.commands[commandName];
			}
			return null;
		},
		getCommands: function() {
			var a = [];
			var cmds = this.commands;
			for (var c in cmds) {
				a.push(c);
			}
			return a;
		},
		addCommand: function(commandName, command) {
			if (this.hasCommand(commandName)) {
				throw new Error("Error in " + this + " Command \"" + commandName + "\" already registered.");
			}
			this.commands[ commandName ] = command;
			this.addInterceptor(commandName);
		},
		removeCommand: function(commandName) {
			if (!this.hasCommand(commandName)) {
				return;
			}
			this.commands[commandName] = null;
			delete this.commands[commandName];
			this.removeInterceptor(commandName);
		},
		getSequencer: function(event) {
			var ss = this.sequencersInfo;
			for (var s  in ss) {
				var len = ss[s].length;
				for (var i = 0; i < len; i++) {
					if (ss[s][i] && ss[s][i].event.type === event.type) {
						var seq = this.sequencers[ ss[s][i].sequenceId ];
						return !!seq ? seq : null;
					}
				}
			}
			return null;
		},
		stopSequencerWithEvent: function(event) {
			var ss = this.sequencersInfo;
			for (var s in ss) {
				var len = ss[s].length;
				for (var i = 0; i < len; i++) {
					if (ss[s][i].event.type === event.type) {
						try {
							this.sequencers[ ss[s][i].sequenceId ].stop();
						} catch(e) {
							return false;
						}
						return true;
					}
				}
			}
			return false;
		},
		stopSequencer: function(sequencer) {
			if (sequencer == null) {
				return false;
			}
			sequencer.stop();
			return true;
		},
		stopAllSequencers: function() {
			var ss = this.sequencers;
			var sis = this.sequencersInfo;
			for (var s in ss) {
				if (sis[s] == null) {
					continue;
				}
				var cl = sis[s].length;
				sis[s] = null;
				delete sis[s];
				ss[s].stop();
				ss[s] = null;
				delete ss[s];
			}
		},
		isPartOfASequence: function(event) {
			return ( this.getSequencer(event) != null );
		},
		getRunningSequencers: function() {
			var a = [];
			var ss = this.sequencers;
			for (var s in ss) {
				a.push(ss[s]);
			}
			return a;
		},
		getLastSequencer: function() {
			return this.lastSequencer;
		},
		dispose: function() {
			for (var nameCommand in this.commands) {
				this.removeCommand(nameCommand);
			}
			for (var nameSequencer in this.sequencers) {
				this.sequencers[nameSequencer] = null;
				delete this.sequencers[nameSequencer];
			}
			this.commands = null;
			this.sequencers = null;
			this.lastEvent = null;
			this.lastSequencer = null;
		},
		/** @private */
		domTreeHandler: function(e) {
			if (e.bubbles && this.hasCommand(e.type) && !e.isCloned) {
				if( e.stopPropagation ) {
                    e.stopPropagation();
                }else{
                    e.cancelBubble = true;
                }
				var clonedEvent = e.clone();
				// store a reference of the events not to dispatch it twice
				// in case it is dispatched from the display list
				this.lastEvent = clonedEvent;
				this.instance.dispatchEvent(clonedEvent);
				if (!clonedEvent.isDefaultPrevented()) {
					this.executeCommand(e);
				}
				this.lastEvent = null;
			}
		},
		/** @private */
		instanceHandler: function(e) {
			if (e.bubbles && this.hasCommand(e.type)) {
				// if the event is equal to the lastEvent, this has already been dispatched for execution
				if (this.lastEvent != e) {
					if (!e.isDefaultPrevented()) {
						this.executeCommand(e);
					}
				}
			}
			this.lastEvent = null;
		}
	});

	/**
	 * @class soma.SomaViews
	 * @description The SomaViews class handles the views of the application.
	 * @borrows soma.Application#addView
	 * @borrows soma.Application#getView
	 * @borrows soma.Application#getViews
	 * @borrows soma.Application#hasView
	 * @borrows soma.Application#removeView
	 * @example
this.addView("myViewName", new MyView());
this.removeView("myViewName");
var view = this.getView("myViewName");
	 */
	soma.SomaViews = soma.extend(
		/** @lends soma.SomaViews.prototype */
		{
		/** @private */
		views: null,
		autoBound:false,
		/**
         * {SomaApplication}
         */
		instance:null,
		constructor: function(instance) {
			this.views = {};
			this.instance = instance;
		},
		hasView: function(viewName) {
			return this.views[ viewName ] != null;
		},
		addView: function(viewName, view) {
			if (this.hasView(viewName)) {
				throw new Error("View \"" + viewName + "\" already exists");
			}
			if (document.attachEvent) {
				view.instance = this.instance;
			}
			if (!this.autoBound) {
				soma.applyProperties( soma.View.prototype, soma.AutoBind );
				this.autoBound = true;
			}
			if (view['shouldAutobind']) {
                view.autobind();
            }
			this.views[ viewName ] = view;
			if (view[ "init" ] != null) {
				view.init();
			}
			return view;
		},
		getView: function(viewName) {
			if (this.hasView(viewName)) {
				return this.views[ viewName ];
			}
			return null;
		},
		getViews: function() {
			var clone = {};
			for (var name in this.views) {
				clone[name] = this.views[name];
			}
			return clone;
		},
		removeView: function(viewName) {
			if (!this.hasView(viewName)) {
				return;
			}
			if (this.views[viewName]["dispose"] != null) {
				this.views[viewName].dispose();
			}
			this.views[ viewName ] = null;
			delete this.views[ viewName ];
		},
		dispose: function() {
			for (var name in this.views) {
				this.removeView(name);
			}
			this.views = null;
            this.instance = null;
		}
	});

	/**
	 * @class soma.EventDispatcher
	 * @description Class on which on you can add and remove listeners of an event. A event can be dispatched from it and a notification will be sent to all registered listeners.
	 *
	 * @example
var dispatcher = new soma.EventDispatcher();
dispatcher.addEventListener("eventType", eventHandler);
function eventHandler(event) {
	// alert(event.type);
}
dispatcher.dispatchEvent(new soma.Event("eventType"));
	 */
	soma.EventDispatcher = soma.extend(
		/** @lends soma.EventDispatcher.prototype */
		{
		listeners: null,
		constructor: function() {
			this.listeners = [];
		},
		/**
		 * Registers an event listener with an EventDispatcher object so that the listener receives notification of an event.
		 * @param {string} type The type of event.
		 * @param {function} listener The listener function that processes the event. This function must accept an Event object as its only parameter and must return nothing.
		 * @param {int} priority The priority level of the event listener (default 0). The higher the number, the higher the priority (can take negative number).
		 * @example
dispatcher.addEventListener("eventType", eventHandler);
function eventHandler(event) {
	// alert(event.type)
}
		 */
		addEventListener: function(type, listener, priority) {
			if (!this.listeners || !type || !listener) return;
			if (isNaN(priority)) priority = 0;
			this.listeners.push({type: type, listener: listener, priority: priority,scope:this});
		},
		/**
		 * Removes a listener from the EventDispatcher object. If there is no matching listener registered with the EventDispatcher object, a call to this method has no effect.
		 * @param {string} type The type of event.
		 * @param {function} listener The listener object to remove.
		 * @example
dispatcher.removeEventListener("eventType", eventHandler);
		 */
		removeEventListener: function(type, listener) {
			if (!this.listeners || !type || !listener) return;
			var i = 0;
			var l = this.listeners.length;
			for (i=l-1; i > -1; i--) {
				var eventObj = this.listeners[i];
				if (eventObj.type == type && eventObj.listener == listener) {
					this.listeners.splice(i, 1);
				}
			}
		},
		/**
		 * Checks whether the EventDispatcher object has any listeners registered for a specific type of event.
		 * @param {string} type The type of event.
		 * @return {boolean}
		 * @example
dispatcher.hasEventListener("eventType");
		 */
		hasEventListener: function(type) {
			if (!this.listeners || !type) return false;
			var i = 0;
			var l = this.listeners.length;
			for (; i < l; ++i) {
				var eventObj = this.listeners[i];
				if (eventObj.type == type) {
					return true;
				}
			}
			return false;
		},
		/**
		 * Dispatches an event into the event flow. The event target is the EventDispatcher object upon which the dispatchEvent() method is called.
		 * @param {soma.Event} event The Event object that is dispatched into the event flow. If the event is being redispatched, a clone of the event is created automatically.
		 * @example
dispatcher.dispatchEvent(new soma.Event("eventType"));
		 */
		dispatchEvent: function(event) {
			if (!this.listeners || !event) return;
			var events = [];
			var i;
			for (i = 0; i < this.listeners.length; i++) {
				var eventObj = this.listeners[i];
				if (eventObj.type == event.type) {
					events.push(eventObj);
				}
			}
			events.sort(function(a, b) {
				return b.priority - a.priority;
			});

			for (i = 0; i < events.length; i++) {
                events[i].listener.apply((event.srcElement) ? event.srcElement : event.currentTarget, [event]);
			}
		},
		/**
	     * Returns a copy of the listener array.
	     * @param {Array} listeners
	     */
        getListeners: function()
        {
            return this.listeners.slice();
        },
		toString: function() {
			return "[soma.EventDispatcher]";
		},
		/**
		 * Destroy the elements of the instance. The instance still needs to be nullified.
		 * @example
instance.dispose();
instance = null;
		 */
		dispose: function() {
			this.listeners = null;
		}
	});

	/**
	 * @class
	 * <b>Introduction</b><br/><br/>
	 * soma.js is a javascript model-view-controller (MVC) framework that is meant to help developers to write loosely-coupled applications to increase scalability and maintainability.<br/><br/>
	 * The main idea behind the MVC pattern is to separate the data (model), the user interface (view) and the logic of the application (controller). They must be independent and should not know about each other in order to increase the scalability of the application.<br/><br/>
	 * soma.js is providing tools to make the three parts "talks" to each other, keeping the view and the model free of framework code, using only native events that can be dispatched from either the framework of the DOM itself.<br/><br/>
	 * <b>When to use soma.js?</b><br/><br/>
	 * One of the great things about javascript is that it scales up with the skill of the developers. Javascript is suitable to write small functions to handle some basics in a site, but javascript is also powerful enough to handle more complex applications, this is where soma.js will shine.<br/><br/>
	 * The primary goal of the framework is to help developers to write "decoupled" modules. The amount of code might be greater than what you can achieve with other frameworks because the goals are different.<br/><br/>
	 * For that reason, while you can use the framework for anything, soma.js might not be the best option for small tasks. For larger applications, where it is important to easily refactor and swap out modules, important to work in large team or collaborate with developers, soma.js will be a great tools and is made for that.<br/><br/>
	 * soma.js is suitable to work for both mobile and desktop application without problem.<br/><br/>
	 * <b>Tools at your disposition</b><br/><br/>
	 * The framework makes an heavy use of the Observer pattern using native javascript events. Here is a quote from Addy Osmani about the Observer pattern:<br/><br/>
	 * The motivation behind using the observer pattern is where you need to maintain consistency between related objects without making classes tightly coupled. For example, when an object needs to be able to notify other objects without making assumptions regarding those objects.<br/><br/>
	 * The framework also provides tools to use the Command pattern that will get triggered using native javascript events. Here is a quote about the Command pattern:<br/><br/>
	 * The command pattern aims to encapsulate method invocation, requests or operations into a single object and gives you the ability to both parameterize and pass method calls around that can be executed at your discretion. In addition, it enables you to decouple objects invoking the action from the objects which implement them, giving you a greater degree of overall flexibility in swapping out concrete 'classes'.<br/><br/>
	 * The framework also provides an easy way to use prototypal inheritance. Two different versions can be used, a "javascript native" version and a Mootools version.<br/><br/>
	 * soma.js is a "base" framework and does not provide tools for specific javascript tasks. External libraries can be used with the framework if you wish to, such as jquery or anything you like.<br/><br/>
	 * soma.js can be used for anything, except to include/distribute it in another framework, application, template, component or structure that is meant to build, scaffold or generate source files.<br/><br/>
	 * <b>Few things to know</b><br/>
	 *     - Wires are the glue of the frameworks elements (models, commands, views, wires) and can be used the way you wish, as proxy/mediators or managers.<br/>
	 *     - Wires can manage one class or multiple classes.<br/>
	 *     - Parallel and sequence commands are built-in.<br/>
	 *     - You can access to all the framework elements that you have registered (framework instance, wires, models, views, injector, reflector, mediators and commands) from commands, wires and mediators.<br/>
	 *     - Commands are native events with the bubbles property set to true.<br/>
	 *     - Commands can be executed, monitored, and stopped using native methods (dispatchEvent, addEventListener, removeEventListener, preventDefault, and so on).<br/>
	 * @description Creates a new Application and acts as the facade and main entry point of the application.<br/>
	 * The Application class can be extended to create a framework instance and start the application.
	 * @extends soma.EventDispatcher
	 * @borrows soma.EventDispatcher#addEventListener
	 * @borrows soma.EventDispatcher#removeEventListener
	 * @borrows soma.EventDispatcher#hasEventListener
	 * @borrows soma.EventDispatcher#dispatchEvent
	 * @borrows soma.EventDispatcher#dispose
	 * @example
var SomaApplication = soma.Application.extend({
	init: function() {

	},
	registerModels: function() {

	},
	registerViews: function() {

	},
	registerCommands: function() {

	},
	registerWires: function() {

	},
	start: function() {

	}
});
new SomaApplication();
	 */
	soma.Application = soma.EventDispatcher.extend(
		/** @lends soma.Application.prototype */
		{
		body:null,
		models:null,
		controller:null,
		wires:null,
		views:null,
		constructor: function() {
			soma.EventDispatcher.call(this);
			this.body = document.body;
			if (!this.body) {
				throw new Error("soma requires body of type Element");
			}
			this.controller = new soma.SomaController(this);
			this.models = new soma.SomaModels(this);
			this.wires = new soma.SomaWires(this);
			this.views = new soma.SomaViews(this);
			this.init();
			this.registerModels();
			this.registerViews();
			this.registerCommands();
			this.registerWires();
			this.start();
		},
		/**
		 * Create a plugin instance.
		 * @param {object} object plugin to instantiate
		 * @returns {object} the plugin
		 * @example
var PluginExample1 = soma.extend({
	constructor: function(instance) {
		this.instance = instance;
	}
});

var PluginExample2 = function(instance) {
	this.instance = instance;
}

var PluginExampleWithParams = function(instance, myParam1, myParam2) {
	this.instance = instance;
}

var SomaApplication = soma.Application.extend({
	init: function() {
		var plugin1 = this.createPlugin(PluginExample1);
		var plugin2 = this.createPlugin(PluginExample2);
		var plugin3 = this.createPlugin(PluginExampleWithParams, "param1", "param2");
	}
});
var app = new SomaApplication();
		 */
		createPlugin: function() {
			if (arguments.length == 0 || !arguments[0]) {
				throw new Error("Error creating a plugin, plugin class is missing.");
			}
			var PluginClass = arguments[0];
			arguments[0] = this;
			var args = [null];
			// args.concat([].splice.call(arguments, 0)); // doesn't work on IE7 and IE8
			for (var i=0; i<arguments.length; i++) {
				args.push(arguments[i]);
			}
			return new (Function.prototype.bind.apply(PluginClass, args));
		},
		/**
		 * Indicates whether a command has been registered to the framework.
		 * @param {string} commandName Event type that is used as a command name.
		 * @returns {boolean}
		 * @example
		 * this.hasCommand("eventType");
		 */
		hasCommand: function(commandName) {
			return (!this.controller) ? false : this.controller.hasCommand(commandName);
		},
		/**
		 * Retrieves the command class that has been registered with a command name.
		 * @param {string} commandName Event type that is used as a command name.
		 * @returns {class} A command Class.
		 * @example
		 * var commandClass = this.getCommand("eventType");
		 */
		getCommand: function(commandName) {
			return (!this.controller) ? null : this.controller.getCommand(commandName);
		},
		/**
		 * Retrieves all the command names (event type) that have been registered to the framework.
		 * @returns {array} An array of command names (string).
		 * @example
		 * var commands = this.getCommands();
		 */
		getCommands: function() {
			return (!this.controller) ? null : this.controller.getCommands();
		},
		/**
		 * Registers a command to the framework.
		 * @param {string} commandName Event type that is used as a command name.
		 * @param {class} command Class that will be executed when a command has been dispatched.
		 * @example
		 * this.addCommand("eventType", MyCommand);
		 */
		addCommand: function(commandName, command) {
			this.controller.addCommand(commandName, command);
		},
		/**
		 * Removes a command from the framework.
		 * @param {string} commandName Event type that is used as a command name.
		 * @example
		 * this.removeCommand("eventType");
		 */
		removeCommand: function(commandName) {
			this.controller.removeCommand(commandName);
		},
		/**
		 * Indicates whether a wire has been registered to the framework.
		 * @param {string} wireName The name of the wire.
		 * @returns {boolean}
		 * @example
		 * this.hasWire("myWireName");
		 */
		hasWire: function(wireName) {
			return (!this.wires) ? false : this.wires.hasWire(wireName);
		},
		/**
		 * Retrieves the wire instance that has been registered using its name.
		 * @param {string} wireName The name of the wire.
		 * @returns {soma.Wire} A wire instance.
		 * @example
		 * var myWire = this.getWire("myWireName");
		 */
		getWire: function(wireName) {
			return (!this.wires) ? null : this.wires.getWire(wireName);
		},
		/**
		 * Retrieves an array of the registered wires.
		 * @returns {array} An array of wires.
		 * @example
		 * var wires = this.getWires();
		 */
		getWires: function() {
			return (!this.wires) ? null : this.wires.getWires();
		},
		/**
		 * Registers a wire to the framework.
		 * @param {string} wireName The name of the wire.
		 * @param {soma.Wire} wire A wire instance.
		 * @returns {soma.Wire} The wire instance.
		 * @example
		 * this.addWire("myWireName", new MyWire());
		 */
		addWire: function(wireName, wire) {
			return this.wires.addWire(wireName, wire);
		},
		/**
		 * Removes a wire from the framework and calls the dispose method of this wire.
		 * @param {string} wireName The name of the wire.
		 * @example
		 * this.removeWire("myWireName");
		 */
		removeWire: function(wireName) {
			this.wires.removeWire(wireName);
		},
		/**
		 * Indicates whether a model has been registered to the framework.
		 * @param {string} modelName The name of the model.
		 * @returns {boolean}
		 * @example
		 * this.hasModel("myModelName");
		 */
		hasModel: function(modelName) {
			return (!this.models) ? false : this.models.hasModel(modelName);
		},
		/**
		 * Retrieves the model instance that has been registered using its name.
		 * @param {string} modelName The name of the model.
		 * @returns {soma.Model} A model instance.
		 * @example
		 * var myModel = this.getModel("myModelName");
		 */
		getModel: function(modelName) {
			return (!this.models) ? null : this.models.getModel(modelName);
		},
		/**
		 * Retrieves an array of the registered models.
		 * @returns {array} An array of models.
		 * @example
		 * var models = this.getModels();
		 */
		getModels: function() {
			return (!this.models) ? null : this.models.getModels();
		},
		/**
		 * Registers a model to the framework.
		 * @param {string} modelName The name of the model.
		 * @param {soma.Model} model A model instance.
		 * @returns {soma.Model} The model instance.
		 * @example
		 * this.addModel("myModelName", new MyModel());
		 */
		addModel: function(modelName, model) {
			return this.models.addModel(modelName, model);
		},
		/**
		 * Removes a model from the framework and call the dispose method of this model.
		 * @param {string} modelName The name of the model.
		 * @example
		 * this.removeModel("myModelName");
		 */
		removeModel: function(modelName) {
			this.models.removeModel(modelName);
		},
		/**
		 * Indicates whether a view has been registered to the framework.
		 * @param {string} viewName The name of the view.
		 * @returns {boolean}
		 * @example
		 * this.hasView("myViewName");
		 */
		hasView: function(viewName) {
			return (!this.views) ? false : this.views.hasView(viewName);
		},
		/**
		 * Retrieves the view instance that has been registered using its name.
		 * @param {string} viewName The name of the view.
		 * @returns {soma.View or custom class} A view instance.
		 * @example
		 * var myView = this.getView("myViewName");
		 */
		getView: function(viewName) {
			return (!this.views) ? null : this.views.getView(viewName);
		},
		/**
		 * Retrieves an array of the registered views.
		 * @returns {array} An array of views.
		 * @example
		 * var views = this.getViews();
		 */
		getViews: function() {
			return (!this.views) ? null : this.views.getViews();
		},
		/**
		 * Registers a view to the framework.
		 * @param {string} viewName The name of the view.
		 * @param {soma.View or custom class} view A view instance.
		 * @returns {soma.View or custom class} The view instance.
		 * @example
		 * this.addView("myViewName", new MyView());
		 */
		addView: function(viewName, view) {
			return this.views.addView(viewName, view);
		},
		/**
		 * Removes a view from the framework and call the (optional) dispose method of this view.
		 * @param {string} viewName The name of the view.
		 * @example
		 * this.removeView("myViewName");
		 */
		removeView: function(viewName) {
			this.views.removeView(viewName);
		},
		/**
		 * Retrieves the sequence command instance using an event instance that has been created from this sequence command.
		 * @param {soma.Event} event Event instance.
		 * @returns {soma.SequenceCommand} A sequence command.
		 * @example
		 * var sequencer = this.getSequencer(myEvent);
		 */
		getSequencer: function(event) {
			return !!this.controller ? this.controller.getSequencer(event) : null;
		},
		/**
		 * Indicates whether an event has been instantiated from a ISequenceCommand class.
		 * @param {soma.Event} event Event instance.
		 * @returns {boolean}
		 * @example
		 * var inSequence = this.isPartOfASequence(myEvent);
		 */
		isPartOfASequence: function(event) {
			return ( this.getSequencer(event) != null );
		},
		/**
		 * Stops a sequence command using an event instance that has been created from this sequence command.
		 * @param {soma.Event} event Event instance.
		 * @returns {boolean}
		 * @example
		 * var success = this.stopSequencerWithEvent(myEvent);
		 */
		stopSequencerWithEvent: function(event) {
			return !!this.controller ? this.controller.stopSequencerWithEvent(event) : false;
		},
		/**
		 * Stops a sequence command using the sequence command instance itself.
		 * @param {soma.SequenceCommand} sequencer A sequence command.
		 * @returns {boolean}
		 * @example
		 * var success = this.stopSequencer(mySequenceCommand);
		 */
		stopSequencer: function(sequencer) {
			return !!this.controller ? this.controller.stopSequencer(sequencer) : false;
		},
		/**
		 * Stops all the sequence command instances that are running.
		 * @example
		 * this.stopAllSequencers();
		 */
		stopAllSequencers: function() {
			if (this.controller) {
				this.controller.stopAllSequencers();
			}
		},
		/**
		 * Retrieves all the sequence command instances that are running.
		 * @returns {array} An array of sequence commands.
		 * @example
		 * var sequencers = this.getRunningSequencers();
		 */
		getRunningSequencers: function() {
			return !!this.controller ? this.controller.getRunningSequencers() : null;
		},
		/**
		 * Retrieves the last sequence command that has been instantiated in the framework.
		 * @returns {soma.SequenceCommand} A sequence command.
		 * @example
		 * var lastSequencer = this.getLastSequencer();
		 */
		getLastSequencer: function() {
			return !!this.controller ? this.controller.getLastSequencer() : null;
		},
		dispose: function() {
			soma.EventDispatcher.prototype.dispose.call(this);
			if (this.models) {
				this.models.dispose();
				this.models = null;
			}
			if (this.views) {
				this.views.dispose();
				this.views = null;
			}
			if (this.controller) {
				this.controller.dispose();
				this.controller = null;
			}
			if (this.wires) {
				this.wires.dispose();
				this.wires = null;
			}
			this.body = null;
		},
		toString: function() {
			return "[soma.Application]";
		},
		/** Method that you can optionally overwrite to initialize elements before anything else, this method is the first one called after that the framework is ready (init > registerModels > registerViews > registerCommands > registerWires > start). */
		init: function() {
		},
		/** Method that you can optionally overwrite to register models to the framework (init > registerModels > registerViews > registerCommands > registerWires > start). */
		registerModels: function() {
		},
		/** Method that you can optionally overwrite to register views to the framework (init > registerModels > registerViews > registerCommands > registerWires > start). */
		registerViews: function() {
		},
		/** Method that you can optionally overwrite to register commands (mapping events to command classes) to the framework (init > registerModels > registerViews > registerCommands > registerWires > start). */
		registerCommands: function() {
		},
		/** Method that you can optionally overwrite to register wires to the framework (init > registerModels > registerViews > registerCommands > registerWires > start). */
		registerWires: function() {
		},
		/** Method that you can optionally overwrite, this method is the last one called by the framework and comes after all the registration methods (init > registerModels > registerViews > registerCommands > registerWires > start). */
		start: function() {
		}
	});

	/**
	 * @class soma.SomaModels
	 * @description The SomaModels class handles the models of the application. See the Model class documentation for implementation.
	 * @borrows soma.Application#addModel
	 * @borrows soma.Application#getModel
	 * @borrows soma.Application#getModels
	 * @borrows soma.Application#hasModel
	 * @borrows soma.Application#removeModel
	 * @example
this.addModel("myModelName", new MyModel());
this.removeModel("myModelName");
var model = this.getModel("myModelName");
	 */
	soma.SomaModels = soma.extend(
		/** @lends soma.SomaModels.prototype */
		{
		/** @private */
		models: null,
		/** {SomaApplication} */
		instance:null,
		constructor: function(instance) {
			this.models = {};
			this.instance = instance;
		},
		hasModel: function(modelName) {
			return this.models[ modelName ] != null;
		},
		getModel: function(modelName) {
			if (this.hasModel(modelName)) {
				return this.models[ modelName ];
			}
			return null;
		},
		getModels: function() {
			var clone = {};
			var ms = this.models;
			for (var name in ms) {
				clone[name] = ms[name];
			}
			return clone;
		},
		addModel: function(modelName, model) {
			if (this.hasModel(modelName)) {
				throw new Error("Model \"" + modelName + "\" already exists");
			}
			this.models[ modelName ] = model;
			if (!model.dispatcher) model.dispatcher = this.instance;
			model.init();
			return model;
		},
		removeModel: function(modelName) {
			if (!this.hasModel(modelName)) {
				return;
			}
			this.models[ modelName ].dispose();
			this.models[ modelName ] = null;
			delete this.models[ modelName ];
		},
		dispose: function() {
			for (var name in this.models) {
				this.removeModel(name);
			}
			this.models = null;
            this.instance = null;
		}
	});

	/**
	 * @class
	 * The model is the class used to manage you application's data model.
	 * The data can be XML, local data, data retrieved from a server or anything.
	 * Ideally, the data should be set to the data property of the model instance, but you are free to create specific getters.
	 * @description Create an instance of a Model class.
	 * @param {string} name The name of the wire.
	 * @borrows soma.EventDispatcher#addEventListener
	 * @borrows soma.EventDispatcher#removeEventListener
	 * @borrows soma.EventDispatcher#hasEventListener
	 * @borrows soma.EventDispatcher#dispatchEvent
	 * @example
// add a model to the framework
this.addModel("myModelName", new MyModel());
	 * @example
// remove a model from the framework
this.removeModel("myModelName");
	 * @example
// retrieve a model
var model = this.getModel("myModelName");
	 * @example
var MyModel = soma.Model.extend({
	init: function() {
		this.data = "my data example";
		// the model can be used as a dispatcher (default dispatcher is the framework instance) to dispatch commands, example:
        this.dispatchEvent(new soma.Event("dataReady"));
	},
	dispose: function() {
		this.data = null;
	}

});
MyModel.NAME = "Model::MyModel";
	 */
	soma.Model = soma.extend(
		/** @lends soma.Model.prototype */
		{
		/** Name of the model. */
		name: null,
		/** Variable that can be used to hold data. */
		data: null,
		/** Instance of a EventDispatcher that can be used to dispatch commands. */
		dispatcher:null,
		constructor: function(name, data, dispatcher) {
			this.data = data;
			this.dispatcher = dispatcher;
			if (name != null) {
				this.name = name;
			}
		},
		/** Method that can you can override, called when the model has been registered to the framework. */
		init: function() {

		},
		/** Method that can you can override, called when the model has been removed from the framework. */
		dispose: function() {

		},
		dispatchEvent: function() {
			if (this.dispatcher) {
				this.dispatcher.dispatchEvent.apply(this.dispatcher, arguments);
			}
		},
		addEventListener: function() {
			if (this.dispatcher) {
				this.dispatcher.addEventListener.apply(this.dispatcher, arguments);
			}
		},
		removeEventListener: function() {
			if (this.dispatcher) {
				this.dispatcher.addEventListener.apply(this.dispatcher, arguments);
			}
		},
		/** Retrieves the name of the model. */
		getName: function() {
			return this.name;
		},
		/** Sets the name of the model. */
		setName: function(name) {
			this.name = name;
		},
		toString: function() {
			return "[soma.Model]";
		}
	});

	/**
	 * @class
	 * The View class is not dependant of the framework and is completely optional.
	 * Its purpose is to dispatch commands in an easier way.
	 * Commands can be dispatched from views and will not tight your views to the framework as they are simple native events (with a property bubbles set to true to be considered as command by the framework).
	 * The commmands must be dispatched from a DOM Element in order for the framework to catch them (see the examples).
	 * @description Create an instance of a View class.
	 * @param {DOM Element optional} domElement A DOM Element.
	 * @example
// add a view to the framework
this.addView("myViewName", new MyView());
	 * @example
// remove a view from the framework
this.removeView("myViewName");
	 * @example
// retrieve a view
var view = this.getView("myViewName");
	 * @example
// view that extends soma.View
// the event "eventType" is considered a command in this example.
var MyView = soma.View.extend({
	init: function() {
		this.dispatchEvent(new soma.Event("eventType")); // works in all browsers
		// or
		this.domElement.dispatchEvent(new soma.Event("eventType")); // does not work with IE7 and 8
	},
	dispose: function() {

	}
});
MyView.NAME = "View::MyView";

var view = new MyView(document.getElementById("myDomElement"));
	 * @example
// view that does not extend soma.View and uses a domElement property (as the soma.View).
// the event "eventType" is considered a command in this example.
var MyView = function(domElement) {
	this.domElement = domElement;
};
MyView.prototype = {
	init: function() {
		this.domElement.dispatchEvent(new soma.Event("eventType")); // does not work with IE7 and 8
	},
	dispose: function() {

	}
};
MyView.NAME = "View::MyView";

var view = new MyView(document.getElementById("myDomElement"));
	 * @example
// view that does not extend soma.View and does not use the domElement property.
// the event "eventType" is considered a command in this example.
var MyView = function(){};
MyView.prototype = {
	init: function() {
		var myDomElement = document.getElementById("myDomElement");
		myDomElement.dispatchEvent(new soma.Event("eventType")); // does not work with IE7 and 8
	},
	dispose: function() {

	}
});
MyView.NAME = "View::MyView";
var view = new MyView();

// another example
// the event "eventType" is considered a command in this example.
var MyView = function(){};
MyView.prototype = {
	init: function() {
		var button = document.getElementById("requestMessageButton");
		button.addEventListener("click", function() {
			this.dispatchEvent(new soma.Event("eventType")); // does not work with IE7 and 8
		});
	},
	dispose: function() {

	}
});
MyView.NAME = "View::MyView";
var view = new MyView();
	 */
	soma.View = soma.extend(
		/** @lends soma.View.prototype */
		{
		/** {SomaApplication} (used for IE7 and IE8, creates a framework dependency in these two browsers) */
		instance: null,
		/** {DOM Element} An optional DOM Element. */
		domElement: null,
		constructor: function(domElement) {
			var d;
			if( domElement != undefined ) {
				if( domElement.nodeType ) {
					d = domElement;
				}else{
					throw new Error( "domElement has to be a DOM-ELement");
				}
			}else{
				d = document.body;
			}
			this.domElement = d;
		},
		/**
		 * DOM native method. The soma.Event class can be used as a shortcut to create the event.
		 * If no DOM Element are specified when instantiated, the body is used by default.
		 * (IE7 and IE8 use the framework instance)
		 * @param {event or soma.Event} An event instance.
		 * @example
this.dispatchEvent(new soma.Event("eventType"));
		 */
		dispatchEvent: function(event) {
			if (this.domElement.dispatchEvent) {
				this.domElement.dispatchEvent(event);
			} else if (this.instance) {
				this.instance.dispatchEvent(event);
			}
		},
		/**
		 * DOM native method.
		 * If no DOM Element are specified when instantiated, the body is used by default.
		 * (IE7 and IE8 use the framework instance)
		 * @param {string} type Type of the event.
		 * @param {function} function The listener that will be notified.
		 * @param {boolean} capture Capture phase of the event.
		 * @example
this.addEventListener("eventType", eventHandler, false);
		 */
		addEventListener: function() {
	        if (this.domElement.addEventListener) {
	            this.domElement.addEventListener.apply(this.domElement, arguments);
	        } else if(this.instance) {
	            this.instance.addEventListener.apply(this.instance, arguments);
	        }
		},
		/**
		 * DOM native method.
		 * If no DOM Element are specified when instantiated, the body is used by default.
		 * (IE7 and IE8 use the framework instance)
		 * @param {string} type Type of the event.
		 * @param {function} function The listener that will be notified.
		 * @param {boolean} capture Capture phase of the event.
		 * @example
this.removeEventListener("eventType", eventHandler, false);
		 */
		removeEventListener: function() {
	        if(this.domElement.addEventListener) {
			    this.domElement.removeEventListener.apply(this.domElement, arguments);
	        } else if(this.instance) {
	             this.instance.removeEventListener.apply(this.instance, arguments);
	        }
		},
		/**
		 * Optional method that will be called by the framework (if it exists) when the view is added to the framework.
		 */
		init: function() {

		},
		/**
		 * Optional method that will be called by the framework (if it exists) when the view is removed from the framework.
		 */
		dispose: function() {

		},
		toString: function() {
			return "[soma.View]";
		}
	});

	/**
	 * @class soma.SomaWires
	 * @description The SomaWires class handles the wires of the application. See the Wire class documentation for implementation.
	 * @borrows soma.Application#addWire
	 * @borrows soma.Application#getWire
	 * @borrows soma.Application#getWires
	 * @borrows soma.Application#hasWire
	 * @borrows soma.Application#removeWire
	 * @example
this.addWire("myWireName", new MyWire());
this.removeWire("myWireName");
var wire = this.getWire("myWireName");
	 */
	soma.SomaWires = soma.extend(
		/** @lends soma.SomaWires.prototype */
		{
		/** @private */
		wires: null,
		/**
         * {SomaApplication}
         */
		instance:null,
		constructor: function(instance) {
			this.wires = {};
			this.instance = instance;
		},
		hasWire: function(wireName) {
			return this.wires[ wireName ] != null;
		},
		addWire: function(wireName, wire) {
			if (this.hasWire(wireName)) {
				throw new Error("Wire \"" + wireName + "\" already exists");
			}
			if (wire['shouldAutobind']) wire.autobind();
			this.wires[ wireName ] = wire;
			wire.registerInstance(this.instance);
			wire.init();
			return wire;
		},
		getWire: function(wireName) {
			if (this.hasWire(wireName)) {
				return this.wires[ wireName ];
			}
			return null;
		},
		getWires: function() {
			var clone = {};
			for (var name in this.wires) {
				clone[name] = this.wires[name];
			}
			return clone;
		},
		removeWire: function(wireName) {
			if (!this.hasWire(wireName)) {
				return;
			}
			this.wires[ wireName ].dispose();
			this.wires[ wireName ] = null;
			delete this.wires[ wireName ];
		},
		dispose: function() {
			for (var name in this.wires) {
				this.removeWire(name);
			}
			this.wires = null;
            this.instance = null;
		}
	});

	soma.Mediator = soma.Wire.extend({
		viewComponent: null,
		constructor: function(name) {
			soma.Wire.call(this, name);
			this.viewComponent = viewComponent;
		},
		dispose: function() {
			this.viewComponent = null;
		},
		toString: function() {
			return "[soma.Mediator]";
		}
	});

	/**
     * @class Event wrapper class for a native event.
     * @description Create an instance of an native event.
     * @param {string} type The type of the event.
     * @param {object} params An object for a custom use and that can hold data.
     * @param {boolean} bubbles Indicates whether an event is a bubbling event. If the event can bubble, this value is true; otherwise it is false. The default is true for framework purposes: the commands are mapped with events types, the framework will ignore events that are commands if the bubbles property is set to false.
     * @param {boolean} cancelable Indicates whether the behavior associated with the event can be prevented (using event.preventDefault()). If the behavior can be canceled, this value is true; otherwise it is false.
     * @returns {event} A event instance.
     * @example
// create an event
var event = new soma.Event("eventType");
var event = new soma.Event("eventType", {myData:"my data"}, true, true);
     * @example
// create an event class
var MyEvent = soma.Event.extend({
    constructor: function(type, params, bubbles, cancelable) {
        // alert(params.myData)
        return soma.Event.call(this, type, params, bubbles, cancelable);
    }
});
MyEvent.DO_SOMETHING = "ApplicationEvent.DO_SOMETHING"; // constant use as an event type
var event = new MyEvent(MyEvent.DO_SOMETHING, {myData:"my data"});
      */
	soma.Event = soma.extend(
		/** @lends soma.Event.prototype */
		{
		constructor: function(type, params, bubbles, cancelable) {
			var e = soma.Event.createGenericEvent(type, bubbles, cancelable);
			if (params != null && params != undefined) {
				e.params = params;
			}
		    e.isCloned = false;
		    e.clone = this.clone.bind(e);
		    e.isIE9 = this.isIE9;
	        e.isDefaultPrevented = this.isDefaultPrevented;
		    if (this.isIE9() || !e.preventDefault || (e.getDefaultPrevented == undefined && e.defaultPrevented == undefined ) ) {
			    e.preventDefault = this.preventDefault.bind(e);
		    }
		    if (this.isIE9()) e.IE9PreventDefault = false;
			return e;
		},
		/**
	     * Duplicates an event.
	     * @returns {event} A event instance.
	     */
		clone: function() {
	        var e = soma.Event.createGenericEvent(this.type, this.bubbles, this.cancelable);
			e.params = this.params;
			e.isCloned = true;
			e.clone = this.clone;
	        e.isDefaultPrevented = this.isDefaultPrevented;
		    e.isIE9 = this.isIE9;
		    if (this.isIE9()) e.IE9PreventDefault = this.IE9PreventDefault;
			return e;
		},
		/**
	     * Prevent the default action of an event.
	     */
		preventDefault: function() {
			if (!this.cancelable) return false;
			this.defaultPrevented = true;
			if (this.isIE9()) this.IE9PreventDefault = true;
	        this.returnValue = false;
	        return this;
		},
		/**
	     * Checks whether the preventDefault() method has been called on the event. If the preventDefault() method has been called, returns true; otherwise, returns false.<br/>
	     * This method should be used rather than the native property: event.defaultPrevented, as the latter has different implementations in browsers.
	     * @returns {boolean}
	     */
		isDefaultPrevented: function() {
		    if (!this.cancelable) return false;
		    if (this.isIE9()) {
			    return this.IE9PreventDefault;
		    }
	        if( this.defaultPrevented != undefined ) {
	           return this.defaultPrevented;
	        }else if( this.getDefaultPrevented != undefined ) {
	            return this.getDefaultPrevented();
	        }
	        return false;
		},
		/** @private */
		isIE9: function() {
		    return document.body.style.scrollbar3dLightColor!=undefined && document.body.style.opacity != undefined;
	    },
		toString: function() {
			return "[soma.Event]";
		}
	});

	/**
	 * @static
	 * @param {string} type
	 * @param {boolean} bubbles
	 * @param {boolean} cancelable
	 * @returns {event} a generic event object
	 */
	soma.Event.createGenericEvent = function (type, bubbles, cancelable) {
	    var e;
	    bubbles = bubbles !== undefined ? bubbles : true;
	    if (document.createEvent) {
	        e = document.createEvent("Event");
	        e.initEvent(type, bubbles, !!cancelable);
	    } else {
	        e = document.createEventObject();
	        e.type = type;
	        e.bubbles = !!bubbles;
	        e.cancelable = !!cancelable;
	    }
	    return e;
	};

	/**
	 * @class soma.IResponder
	 * @description This interface provides the contract for any service that needs to respond to remote or asynchronous calls.
	 * @example
var MyResponderClass = soma.IResponder.extend({
	fault: function(info) {
	},
	result: function(data) {
	}
});

var MyResponderClass = soma.extend({
	fault: function(info) {
	},
	result: function(data) {
	}
});
soma.applyProperties(MyResponderClass.prototype, soma.IResponder.prototype);
	 */
	soma.IResponder = soma.extend(
		/** @lends soma.IResponder.prototype **/
		{
		/**
	     * This method is called by a service when an error has been received.
	     * @param {object} info Description of the error.
	     * @name fault
	     */
		fault: function(info) {
		},
		/**
	     * This method is called by a service when the return value has been received.
	     * @param {object} data Object containing the result.
	     * @name result
	     */
		result: function(data) {
		}
	});

})();