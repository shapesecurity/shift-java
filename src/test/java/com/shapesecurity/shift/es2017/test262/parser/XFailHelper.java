package com.shapesecurity.shift.es2017.test262.parser;

import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertTrue;

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

	@Test
	public void ensureTestsExist() {
		for (String f : PassTest.xfail) {
			assertTrue((new File(PassTest.testsDir, f)).exists());
		}

		for (String f : EarlyTest.xfail) {
			assertTrue((new File(EarlyTest.testsDir, f)).exists());
		}

		for (String f : FailTest.xfail) {
			assertTrue((new File(FailTest.testsDir, f)).exists());
		}
	}

}
