package reminder;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import org.junit.Test;

//@author A0117989H

/**
 * This test class tests the output after checking whether there is reminder 
 * in the command typed by the user.
 *
 */

public class ReminderPatternFinderTest {
	
	ReminderPatternFinder finder = new ReminderPatternFinder();
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	private final String ERROR_INVALID_DATE_TIME = "Invalid date and time!\n";
	private final String INFO_NO_COMMAND_FOR_REMINDER = "Command to set reminder is not found!\n";
	
	private final String NO_REMINDER = "shopping on tue";
	
	private final String INVALID_1 = "shopping on tue [34nov 5pm]";
	private final String INVALID_2 = "shopping on tue [11-13-14 5pm]";
	private final String INVALID_3 = "shopping on tue [11-11-14 14pm]";
	
	private final String PATTERN1_1 = "shopping on tue [remind me 2hrs before]";
	private final String PATTERN1_2 = "shopping on tue [30mins]";
	
	private final String PATTERN2_1 = "shopping on tue [11nov 5pm]";
	private final String PATTERN2_2 = "shopping on tue [11 nov 10:30am]";
	private final String PATTERN2_3 = "shopping on tue [11 nov 1030]";
	
	private final String PATTERN3_1 = "shopping on tue [11-11-14 5pm]";
	private final String PATTERN3_2 = "shopping on tue [11/11/2014 12:30am]";
	
	private final String CLR1 = "[clear]";
	private final String CLR2 = "[clear reminder]";
	
	/** test output of no reminder in command - boundary case **/
	@Test
	public void tc1() {
		System.setOut(new PrintStream(out));
		Object result = finder.parse(NO_REMINDER);
		assertNull(result);
		assertEquals(out.toString(), INFO_NO_COMMAND_FOR_REMINDER);
	}
	
	/** test output of invalid reminder in command **/
	@Test
	public void tc2() {
		System.setOut(new PrintStream(out));
		Object result = finder.parse(INVALID_1);
		assertNull(result);
		assertEquals(out.toString(), ERROR_INVALID_DATE_TIME);
		
		result = finder.parse(INVALID_2);
		assertNull(result);
		
		result = finder.parse(INVALID_3);
		assertNull(result);
	}
	
	/** test output of reminder pattern 1 in command **/
	@Test
	public void tc3() {
		Object result = finder.parse(PATTERN1_1);
		assertTrue(result instanceof Integer);
		assertEquals(result, 120);
		
		result = finder.parse(PATTERN1_2);
		assertTrue(result instanceof Integer);
		assertEquals(result, 30);
	}
	
	/** test output of reminder pattern 2 in command **/
	@Test
	public void tc4() {
		Object result = finder.parse(PATTERN2_1);
		assertTrue(result instanceof LocalDateTime);
		assertEquals(result, LocalDateTime.of(2014, 11, 11, 17, 0));
		
		result = finder.parse(PATTERN2_2);
		assertTrue(result instanceof LocalDateTime);
		assertEquals(result, LocalDateTime.of(2014, 11, 11, 10, 30));
		
		result = finder.parse(PATTERN2_3);
		assertTrue(result instanceof LocalDateTime);
		assertEquals(result, LocalDateTime.of(2014, 11, 11, 10, 30));
	}
	
	/** test output of reminder pattern 3 in command **/
	@Test
	public void tc5() {
		Object result = finder.parse(PATTERN3_1);
		assertTrue(result instanceof LocalDateTime);
		assertEquals(result, LocalDateTime.of(2014, 11, 11, 17, 0));
		
		result = finder.parse(PATTERN3_2);
		assertTrue(result instanceof LocalDateTime);
		assertEquals(result, LocalDateTime.of(2014, 11, 11, 00, 30));
	}
	
	/** test output of clear reminder pattern in command **/
	@Test
	public void tc6() {
		Object result = finder.parse(CLR1);
		assertTrue(result instanceof Integer);
		assertEquals(result, -1);
		
		result = finder.parse(CLR2);
		assertTrue(result instanceof Integer);
		assertEquals(result, -1);
	}
	
	/** test output of checking if there's reminder in command **/
	@Test
	public void tc7() {
		boolean hasReminder;
		hasReminder = finder.hasReminderPatternInCommand(NO_REMINDER);
		assertFalse(hasReminder);
		
		hasReminder = finder.hasReminderPatternInCommand(PATTERN3_1);
		assertTrue(hasReminder);
	}
}
