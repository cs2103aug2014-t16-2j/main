import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TextBuddyTest {

	TextBuddy tb;

	@Before
	public void setUp() throws Exception {
		String[] args = {"test.txt"};
		tb = new TextBuddy(args);
	}

	@After
	public void tearDown() throws Exception {
		tb.clear();
	}

	@Test
	public void testEmptySort() {
		tb.sort();
		List<String> list = tb.getData();
		assertTrue(list.isEmpty());
	}

	@Test
	public void testOneSort() {
		tb.add("a");
		tb.sort();
		List<String> list = tb.getData();
		assertEquals("a",list.get(0));
		tb.clear();
	}

	@Test
	public void testTwoOrMoreSort() {
		tb.add("b");
		tb.add("a");
		List<String> list = tb.getData();
		assertEquals("b",list.get(0));
		assertEquals("a",list.get(1));
		tb.sort();
		list = tb.getData();
		assertEquals("a",list.get(0));
		assertEquals("b",list.get(1));
		tb.clear();
	}

	@Test
	public void testSearchNone() {

		assertEquals("Nothing to search",tb.search("haha"));
		assertEquals("Nothing to search",tb.search("testing"));
		tb.clear();
	}
	
	@Test
	public void testSearchOne() {

		tb.add("i want to test this");
		tb.add("go eat your breakfast");
		assertEquals("i want to test this",tb.search("want"));
		assertEquals("go eat your breakfast",tb.search("breakfast"));
		tb.clear();
	}

	@Test
	public void testSearchTwo() {
		
		tb.add("I want you");
		tb.add("I want them");
		tb.add("I hate you");
		assertEquals("I want youI want them",tb.search("want"));
		tb.clear();
	}
	
	@Test
	public void testSearchCannotFind() {
		
		tb.add("I want you");
		tb.add("I want them");
		tb.add("I hate you");
		assertEquals("Content cannot be found",tb.search("haha"));
		tb.clear();
	}
	
}
