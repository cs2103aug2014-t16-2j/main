package parsertests;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Parser.Action;
import Parser.Parser;
import Parser.Task;

/**
 * To test parsing of timed tasks.
 * Timed tasks are tasks which has start date/time and end date/time.
 * 
 * @author Moe Lwin Hein (A0117989H)
 *
 */

public class TestParseTimedTasks {
	Parser parser = new Parser();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	/** Adding timed tasks **/
	/************************/
	/************************/
	
	/**
	 * add a task with no content
	 */
	@Test
	public void tc1Add() {
		final String s = "add from 30/10/2014 to 1/11/2014";
		Task t = new Task(null, null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd/mm/yyyy
	 * form 1 (from to)
	 */
	@Test
	public void tc2Add() {
		final String s = "add a task from 30/10/2014 to 1/11/2014";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd/mm/yyyy
	 * form 2 (to)
	 */
	@Test
	public void tc3Add() {
		final String s = "add a task 30/10/2014 to 1/11/2014";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd/mm/yyyy
	 * form 3 (without preposition)
	 */
	@Test
	public void tc4Add() {
		final String s = "add a task 30/10/2014 1/11/2014";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd/mm/yyyy
	 * form 4 (misplaced dates)
	 */
	@Test
	public void tc5Add() {
		final String s = "add a task 1/11/2014 30/10/2014";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd MMM
	 * form 1 (from to)
	 */
	@Test
	public void tc6Add() {
		final String s = "add a task from 30 oct to 1 nov";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd MMM
	 * form 2 (to)
	 */
	@Test
	public void tc7Add() {
		final String s = "add a task 30 oct to 1 nov";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd MMM
	 * form 3 (without prepositions)
	 */
	@Test
	public void tc8Add() {
		final String s = "add a task 30 oct 1 nov";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
	
	/**
	 * add a standard task with start date and end date
	 * date format = dd MMM
	 * form 4 (misplaced dates)
	 */
	@Test
	public void tc9Add() {
		final String s = "add a task 1 nov 30 oct";
		Task t = new Task("a task", null, "normal", LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 11, 1, 0, 0));
		Action action = parser.getAction(s);
		assertTrue(action.getCommand().equals("add"));
		assertTrue(action.getTask().equals(t));
	}
}
