package com.shapesecurity.shift.es2017.test262.expectations;

abstract class Regexp {

	private Regexp() {

	}

	// Tests should parse but which have a feature flag we don't support, but which we parse anyway because we're too lax
	static final String[] xpass = new String[] {

			// tests using constructor
			"built-ins/RegExp/named-groups/string-replace-get.js",
			"built-ins/RegExp/named-groups/groups-object-subclass-sans.js",
			"built-ins/RegExp/named-groups/string-replace-undefined.js",
			"built-ins/RegExp/named-groups/string-replace-nocaptures.js",
			"built-ins/RegExp/named-groups/groups-object-subclass.js",
			"built-ins/RegExp/named-groups/string-replace-missing.js",
			"built-ins/RegExp/named-groups/string-replace-numbered.js",
			"built-ins/RegExp/named-groups/groups-object-undefined.js",
			"built-ins/RegExp/named-groups/string-replace-escaped.js",
			"built-ins/RegExp/named-groups/functional-replace-global.js",
			"built-ins/RegExp/named-groups/string-replace-unclosed.js",
			"built-ins/RegExp/named-groups/functional-replace-non-global.js",
			"built-ins/RegExp/prototype/dotAll/cross-realm.js",

			// tests using eval
			"language/literals/regexp/named-groups/invalid-lone-surrogate-groupname.js",
	};


	// Tests which are supposed to fail to parse, but which we parse anyway because we're too lax
	public static final String[] xfail = new String[] {

	};
}
