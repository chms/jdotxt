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
public class ContextParserTest {

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
	public void testNoContext1() {
		task = "Buy flour, eggs and milk";
		expected = Collections.emptySet();
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	public void testNoContext2() {
		task = "Poke mike@live.com";
		expected = Collections.emptySet();
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	public void testAsciiContexts() {
		task = "Buy flour, eggs and milk @supermarket12 @inthecity";
		expected.addAll(Arrays.asList(
				new String[] { "inthecity", "supermarket12" }));
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	public void testCyrillicContexts() {
		task = "Купить муку, яйца и молоко @магазин12 @вгороде";
		expected.addAll(Arrays.asList(
				new String[] { "магазин12", "вгороде" }));
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	// This is not allowed by spec if you read it literally
	public void testContextComesFirst() {
		task = "@supermarket Buy flour, eggs and milk";
		expected.add("supermarket");
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	// This is explicitly forbidden by spec but we
	// have a simplistic implementation
	public void testContextComesBeforePrio() {
		task = "@supermarket (A) Buy flour, eggs and milk";
		expected.add("supermarket");
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}

	@Test
	// This is explicitly forbidden by spec but we
	// have a simplistic implementation
	public void testContextComesBeforeDate() {
		task = "@supermarket 2014-03-05 Buy flour, eggs and milk";
		expected.add("supermarket");
		actual.addAll(ContextParser.getInstance().parse(task));
		assertEquals(expected, actual);
	}
}
