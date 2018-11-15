package com.shapesecurity.shift.es2017.path;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EqualityTest {
	@Test
	public void testBranch() {
		assertEquals(Branch.ClassExpressionName_(), Branch.ClassExpressionName_());
		assertNotEquals(Branch.ClassExpressionName_(), Branch.ClassExpressionSuper_());

		assertEquals(Branch.ClassExpressionElements_(0), Branch.ClassExpressionElements_(0));
		assertNotEquals(Branch.ClassExpressionElements_(0), Branch.ClassExpressionElements_(1));
	}

	@Test
	public void testBranchGetter() {
		assertEquals(new BranchGetter(), new BranchGetter());
		assertEquals(new BranchGetter().d(Branch.ClassExpressionName_()), new BranchGetter().d(Branch.ClassExpressionName_()));
		assertNotEquals(new BranchGetter().d(Branch.ClassExpressionName_()), new BranchGetter().d(Branch.ClassExpressionSuper_()));
		assertNotEquals(new BranchGetter().d(Branch.ClassExpressionName_()), new BranchGetter().d(Branch.ClassExpressionName_()).d(Branch.ClassExpressionSuper_()));
	}
}
