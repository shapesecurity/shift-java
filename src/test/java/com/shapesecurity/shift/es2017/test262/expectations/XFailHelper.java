package com.shapesecurity.shift.es2017.test262.expectations;

import com.shapesecurity.shift.es2017.test262.Test262Info;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;

public class XFailHelper {


	// Tests with any of these feature flags are expected not to parse, unless they are whitelisted in xpassDespiteFeatures
	private static final HashSet<String> xfailFeatures = new HashSet<>();

	static {
		xfailFeatures.addAll(Arrays.asList(
				"async-iteration",
				"BigInt",
				"regexp-dotall",
				"regexp-unicode-property-escapes",
				"regexp-named-groups",
				"class-fields-public",
				"class-fields-private",
				"class-static-fields-public",
				"class-static-fields-private",
				"class-methods-private",
				"class-static-methods-private",
				"object-rest",
				"object-spread",
				"numeric-separator-literal",
				"json-superset",
				"export-star-as-namespace-from-module",
				"optional-catch-binding",
				"dynamic-import",
				"import.meta"
		));
	}

	private static final HashSet<String> xpassDespiteFeatures = new HashSet<>();

	static {
		xpassDespiteFeatures.addAll(Arrays.asList(
				// arguable feature misclassification https://github.com/tc39/test262/blob/master/test/language/expressions/async-arrow-function/escaped-async-line-terminator.js
				"language/expressions/async-arrow-function/escaped-async-line-terminator.js",

				// json-superset: tests using eval
				"language/literals/string/line-separator-eval.js",
				"language/literals/string/paragraph-separator-eval.js",

				// export-star-as-namespace-from-module: feature used in an import, not the main test
				"language/module-code/instn-star-as-props-dflt-skip.js",
				"language/module-code/instn-star-props-nrml.js",
				"language/module-code/namespace/internals/get-nested-namespace-dflt-skip.js",
				"language/module-code/namespace/internals/get-nested-namespace-props-nrml.js",

				// class-fields-private: tests using eval
				"language/statements/class/privatename-not-valid-eval-earlyerr-1.js",
				"language/statements/class/privatename-not-valid-eval-earlyerr-2.js",
				"language/statements/class/privatename-not-valid-eval-earlyerr-6.js",
				"language/statements/class/privatename-not-valid-eval-earlyerr-7.js",
				"language/statements/class/privatename-not-valid-eval-earlyerr-8.js",

				// regexp-dotall: tests using constructor, or checking properties of functions
				"annexB/built-ins/RegExp/prototype/flags/order-after-compile.js",
				"built-ins/RegExp/duplicate-flags.js",
				"built-ins/RegExp/prototype/dotAll/length.js",
				"built-ins/RegExp/prototype/dotAll/name.js",
				"built-ins/RegExp/prototype/dotAll/prop-desc.js",
				"built-ins/RegExp/prototype/dotAll/this-val-invalid-obj.js",
				"built-ins/RegExp/prototype/dotAll/this-val-non-obj.js",
				"built-ins/RegExp/prototype/dotAll/this-val-regexp-prototype.js",
				"built-ins/RegExp/prototype/flags/coercion-dotall.js",
				"built-ins/RegExp/prototype/flags/get-order.js",
				"built-ins/RegExp/prototype/flags/rethrow.js",
				"built-ins/RegExp/prototype/flags/return-order.js",

				// dynamic-import: tests using eval
				"language/expressions/dynamic-import/usage-from-eval.js",

				// import.meta: tests using eval
				"language/expressions/import.meta/not-accessible-from-direct-eval.js",
				"language/expressions/import.meta/syntax/goal-function-params-or-body.js",
				"language/expressions/import.meta/syntax/goal-generator-params-or-body.js",
				"language/expressions/import.meta/syntax/goal-async-function-params-or-body.js"
		));
		xpassDespiteFeatures.addAll(Arrays.asList(BigIntNoLiterals.xpass));
		xpassDespiteFeatures.addAll(Arrays.asList(Regexp.xpass));
		xpassDespiteFeatures.addAll(Arrays.asList(NumericSeperatorNoLiterals.xpass));
	}

	private static final HashSet<String> xfailFiles = new HashSet<>();

	static {
		xfailFiles.addAll(Arrays.asList(
				// functions with reserved names whose bodies are strict: https://github.com/tc39/ecma262/pull/1158
				"language/expressions/function/name-arguments-strict-body.js",
				"language/expressions/function/name-eval-strict-body.js",
				"language/statements/function/name-arguments-strict-body.js",
				"language/statements/function/name-eval-strict-body.js",

				// ES2018 invalid escapes in template literals: https://github.com/tc39/ecma262/pull/773
				"language/expressions/tagged-template/invalid-escape-sequences.js",

				// ES2017 for-var-in: https://github.com/tc39/ecma262/pull/614
				"annexB/language/statements/for-in/nonstrict-initializer.js",

				// yield bug, shift-java#212
				"language/statements/for-of/dstr-obj-id-identifier-yield-expr.js",
				"language/statements/for-in/dstr-obj-id-identifier-yield-expr.js",
				"language/expressions/assignment/dstr-obj-id-identifier-yield-expr.js",

				// invalid escape bug, shift-java#219
				"language/literals/string/legacy-non-octal-escape-sequence-strict.js"
		));
		xfailFiles.addAll(Arrays.asList(Regexp.xfail));
		xfailFiles.addAll(Arrays.asList(TrailingCommas.xfail));
		xfailFiles.addAll(Arrays.asList(ContextualKeywordEscapes.xfail));
	}

	public static boolean isXFailed(@Nonnull Test262Info info, boolean shouldFail) {
		if (!shouldFail) {
			for (String feature : info.features) {
				if (xfailFeatures.contains(feature) && !xpassDespiteFeatures.contains(info.name)) {
					return true;
				}
			}
		}
		return xfailFiles.contains(info.name);
	}

}
