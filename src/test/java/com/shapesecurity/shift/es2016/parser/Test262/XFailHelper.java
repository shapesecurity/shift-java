package com.shapesecurity.shift.es2016.parser.Test262;

import java.util.Set;

public class XFailHelper {
	public static void wrap(String name, Set<String> xfail, TestCase test) throws Exception {
		boolean passed = false;
		try {
			test.test(name);
			passed = true;
		} catch (Exception e) {
			if (!xfail.contains(name)) {
				throw e;
			} // else swallow the exception and thus pass
		}
		if (passed && xfail.contains(name)) {
			throw new RuntimeException("Test marked as xfail, but passed");
		}
	}

	@FunctionalInterface
	public interface TestCase {
		void test(String name) throws Exception; // We need our own interface so it can throw
	}
}
