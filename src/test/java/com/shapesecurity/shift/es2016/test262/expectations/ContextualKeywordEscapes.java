package com.shapesecurity.shift.es2016.test262.expectations;

// Tests which fail on escaped contextual keywords, shift-java#220
abstract class ContextualKeywordEscapes {

	private ContextualKeywordEscapes() {

	}

	public static final String[] xfail = new String[] {
			"language/future-reserved-words/let-strict-escaped.js",
			"language/future-reserved-words/yield-strict-escaped.js",
			"language/statements/for-of/escaped-of.js",
			"language/statements/labeled/value-yield-strict-escaped.js",
			"language/statements/let/syntax/escaped-let.js",
			"language/statements/class/syntax/escaped-static.js",
			"language/expressions/object/method-definition/escaped-get.js",
			"language/expressions/object/method-definition/escaped-set.js",
			"language/expressions/new.target/escaped-target.js",
			"language/expressions/new.target/escaped-new.js",
			"language/export/escaped-from.js",
			"language/export/escaped-as-export-specifier.js",
			"language/export/escaped-default.js",
			"language/import/escaped-as-import-specifier.js",
			"language/import/escaped-from.js",
			"language/import/escaped-as-namespace-import.js"
	};

}
