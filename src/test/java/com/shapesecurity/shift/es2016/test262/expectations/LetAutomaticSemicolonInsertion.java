package com.shapesecurity.shift.es2016.test262.expectations;

// Tests which fail on escaped contextual keywords, shift-java#
abstract class LetAutomaticSemicolonInsertion {

	private LetAutomaticSemicolonInsertion() {

	}

	public static final String[] xfail = new String[] {
			"language/statements/with/let-block-with-newline.js",
			"language/statements/with/let-identifier-with-newline.js",
			"language/statements/if/let-block-with-newline.js",
			"language/statements/if/let-identifier-with-newline.js",
			"language/statements/for-of/let-block-with-newline.js",
			"language/statements/for-of/let-identifier-with-newline.js",
			"language/statements/labeled/let-block-with-newline.js",
			"language/statements/labeled/let-identifier-with-newline.js",
			"language/statements/for/let-block-with-newline.js",
			"language/statements/for/let-identifier-with-newline.js",
			"language/statements/for-in/let-block-with-newline.js",
			"language/statements/for-in/let-identifier-with-newline.js",
			"language/statements/while/let-block-with-newline.js",
			"language/statements/while/let-identifier-with-newline.js"
	};

}
