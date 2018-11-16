package com.shapesecurity.shift.es2017.path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToStringTest {
	@Test
	public void testToString() {
		assertEquals("", new BranchGetter().toString());

		assertEquals(".statements[5].body", new BranchGetter().d(Branch.ScriptStatements_(5)).d(Branch.ForStatementBody_()).toString());
	}
}
