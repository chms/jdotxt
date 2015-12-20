package com.todotxt.todotxttouch.task;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @see <a href="https://github.com/ginatrapani/todo.txt-cli/wiki/The-Todo.txt-Format">Todo.txt Format Specification</a>
 */
public class ProjectParserTest {

	private String task;
	private Set<String> expected;
	private Set<String> actual;

	@Before
	public void setUp() {
		task = null;
		expected = new HashSet<String>();
		actual = new HashSet<String>();
	}

	@Test
	public void testNoProject1() {
		task = "Buy flour, eggs and milk";
		expected = Collections.emptySet();
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	public void testNoProject2() {
		task = "Find k such that k+k=2";
		expected = Collections.emptySet();
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	public void testAsciiProjects() {
		task = "Buy flour, eggs and milk +groceries +birthdayCake23";
		expected.addAll(Arrays.asList(
				new String[] { "birthdayCake23", "groceries" }));
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	public void testCyrillicProjects() {
		task = "Купить муку, яйца и молоко +продукты +праздничныйТорт23";
		expected.addAll(Arrays.asList(
				new String[] { "продукты", "праздничныйТорт23" }));
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	// This is not allowed by spec if you read it literally
	public void testProjectComesFirst() {
		task = "+groceries Buy flour, eggs and milk";
		expected.add("groceries");
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	// This is explicitly forbidden by spec but we
	// have a simplistic implementation
	public void testProjectComesBeforePrio() {
		task = "+groceries (A) Buy flour, eggs and milk";
		expected.add("groceries");
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	// This is explicitly forbidden by spec but we
	// have a simplistic implementation
	public void testProjectComesBeforeDate() {
		task = "+groceries 2016-03-05 Buy flour, eggs and milk";
		expected.add("groceries");
		actual.addAll(ProjectParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

}
