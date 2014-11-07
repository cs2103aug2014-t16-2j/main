package parser;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

//@author A0111887Y
//Unit testing for Parser class
public class ParserTest {

	Parser parser = new Parser();
	
	//@author A0111887Y
	
	@Test
	public void testNullInput() {
		assertEquals(null, parser.getAction(null));
	}

	//@author A0111887Y
	
	@Test
	public void testAddCommand() {
		assertEquals(Command.ADD, parser.getAction("add").getCommand());
		assertEquals(Command.ADD, parser.getAction("create").getCommand());
		assertEquals(Command.ADD, parser.getAction("schedule").getCommand());
		assertEquals(Command.ADD, parser.getAction("").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testModifyCommand() {
		assertEquals(Command.MODIFY, parser.getAction("mod").getCommand());
		assertEquals(Command.MODIFY, parser.getAction("modify").getCommand());
		assertEquals(Command.MODIFY, parser.getAction("edit").getCommand());
		assertEquals(Command.MODIFY, parser.getAction("reschedule").getCommand());
		assertEquals(Command.MODIFY, parser.getAction("change").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testDeleteCommand() {
		assertEquals(Command.DELETE, parser.getAction("del").getCommand());
		assertEquals(Command.DELETE, parser.getAction("delete").getCommand());
		assertEquals(Command.DELETE, parser.getAction("clr").getCommand());
		assertEquals(Command.DELETE, parser.getAction("clear").getCommand());
		assertEquals(Command.DELETE, parser.getAction("rm").getCommand());
		assertEquals(Command.DELETE, parser.getAction("remove").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testSearchCommand() {
		assertEquals(Command.SEARCH, parser.getAction("display").getCommand());
		assertEquals(Command.SEARCH, parser.getAction("show").getCommand());
		assertEquals(Command.SEARCH, parser.getAction("find").getCommand());
		assertEquals(Command.SEARCH, parser.getAction("search").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testBlockCommand() {
		assertEquals(Command.BLOCK, parser.getAction("blk ").getCommand());
		assertEquals(Command.BLOCK, parser.getAction("block task").getCommand());
		assertEquals(Command.BLOCK, parser.getAction("reserve task").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testUnblockCommand() {
		assertEquals(Command.UNBLOCK, parser.getAction("unblk").getCommand());
		assertEquals(Command.UNBLOCK, parser.getAction("unblock").getCommand());
		assertEquals(Command.UNBLOCK, parser.getAction("unreserve").getCommand());
		assertEquals(Command.UNBLOCK, parser.getAction("free").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testOtherCommand() {
		assertEquals(Command.UNDO, parser.getAction("undo").getCommand());
		assertEquals(Command.REDO, parser.getAction("redo").getCommand());
		assertEquals(Command.EXIT, parser.getAction("exit").getCommand());
	}

	//@author A0111887Y
	
	@Test
	public void testDateFormat() {
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7/11/2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7\\11\\2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7-11-2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7 november 2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7th november 2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7 nov 2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("7th nov 2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("nov 7th 2014").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7/11").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7\\11").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7-11").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7 november").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7th november").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7 nov").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("7th nov").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 7, 0, 0), parser.getAction("nov 7th").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 7, 0, 0), parser.getAction("2014 nov 7").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 28, 0, 0), parser.getAction("28 11").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 11, 28, 0, 0), parser.getAction("11 28").getTask().getStartDateTime());
		assertEquals(null, parser.getAction("32 32 32").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(2014, 11, 28, 0, 0), parser.getAction("2014 28 11").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), parser.getAction("today").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)), parser.getAction("tomorrow").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.of(0, 0)), parser.getAction("yesterday").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusWeeks(1), LocalTime.of(0, 0)), parser.getAction("next week").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusWeeks(-1), LocalTime.of(0, 0)), parser.getAction("last week").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusMonths(1), LocalTime.of(0, 0)), parser.getAction("next month").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusMonths(-1), LocalTime.of(0, 0)), parser.getAction("last month").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusYears(1), LocalTime.of(0, 0)), parser.getAction("next year").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusYears(-1), LocalTime.of(0, 0)), parser.getAction("last year").getTask().getStartDateTime());
	}

	//@author A0111887Y
	
	@Test
	public void testTimeFormat() {
		assertEquals(null, parser.getAction("12").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), parser.getAction("12am").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)), parser.getAction("12pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), parser.getAction("12 am").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)), parser.getAction("12 pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)), parser.getAction("12.30").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 30)), parser.getAction("12.30am").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)), parser.getAction("12.30pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 30)), parser.getAction("12.30 am").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)), parser.getAction("12.30 pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)), parser.getAction("12:30").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 30)), parser.getAction("12:30am").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)), parser.getAction("12:30pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 30)), parser.getAction("12:30 am").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 30)), parser.getAction("12:30 pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 0)), parser.getAction("morning").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)), parser.getAction("noon").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 0)), parser.getAction("afternoon").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0)), parser.getAction("evening").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 0)), parser.getAction("night").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59)), parser.getAction("midnight").getTask().getStartDateTime());
	}

	//@author A0111887Y
	
	@Test
	public void testCategory() {
		assertEquals(null, parser.getAction("").getTask().getCategory());
		assertEquals(null, parser.getAction("#").getTask().getCategory());
		assertEquals("2103", parser.getAction("#2103").getTask().getCategory());
	}

	//@author A0111887Y
	
	@Test
	public void testPriority() {
		assertEquals(null, parser.getAction("").getTask().getPriority());
		assertEquals("high", parser.getAction("!").getTask().getPriority());
		assertEquals("very high", parser.getAction("!!").getTask().getPriority());
		assertEquals("very high", parser.getAction("!!!").getTask().getPriority());
		assertEquals("normal", parser.getAction("lowest priority").getTask().getPriority());
		assertEquals("normal", parser.getAction("low priority").getTask().getPriority());
		assertEquals("normal", parser.getAction("normal priority").getTask().getPriority());
		assertEquals("high", parser.getAction("high priority").getTask().getPriority());
		assertEquals("very high", parser.getAction("very high priority").getTask().getPriority());
		assertEquals("very high", parser.getAction("top priority").getTask().getPriority());
		assertEquals("normal", parser.getAction("not important").getTask().getPriority());
		assertEquals("high", parser.getAction("important").getTask().getPriority());
		assertEquals("very high", parser.getAction("very important").getTask().getPriority());
		assertEquals("normal", parser.getAction("unimportant").getTask().getPriority());
	}

	//@author A0111887Y
	
	@Test
	public void testMarkDone() {
		assertEquals(Command.MARK, parser.getAction("task done").getCommand());
		assertTrue(parser.getAction("task done").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task not done").getCommand());
		assertFalse(parser.getAction("task not done").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task complete").getCommand());
		assertTrue(parser.getAction("task complete").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task incomplete").getCommand());
		assertFalse(parser.getAction("task incomplete").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task is done").getCommand());
		assertTrue(parser.getAction("task is done").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task is incomplete").getCommand());
		assertFalse(parser.getAction("task is incomplete").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task is not yet done").getCommand());
		assertFalse(parser.getAction("task is not yet done").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task has been completed").getCommand());
		assertTrue(parser.getAction("task has been completed").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task has not yet been completed").getCommand());
		assertFalse(parser.getAction("task has not yet been completed").getTask().isDone());
		assertEquals(Command.MARK, parser.getAction("task is yet to be completed").getCommand());
		assertFalse(parser.getAction("task is yet to be completed").getTask().isDone());
	}

	//@author A0111887Y
	
	@Test
	public void testStartEndDateTime() {
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), parser.getAction("from today to tomorrow").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59)), parser.getAction("from today to tomorrow").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())), parser.getAction("by tomorrow").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59)), parser.getAction("by tomorrow").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.of(0, 0)), parser.getAction("from yesterday to the day after tomorrow").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(23, 59)), parser.getAction("from yesterday to the day after tomorrow").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusWeeks(-1), LocalTime.of(0, 0)), parser.getAction("from from last week to the day before yesterday").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(-2), LocalTime.of(23, 59)), parser.getAction("from last week to the day before yesterday").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())), parser.getAction("by next week").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusWeeks(1), LocalTime.of(23, 59)), parser.getAction("by next week").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), parser.getAction("from today to tomorrow 2pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0)), parser.getAction("from today to tomorrow 2pm").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)), parser.getAction("from today to tomorrow 12pm to 2pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0)), parser.getAction("from today to tomorrow 12pm to 2pm").getTask().getEndDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())), parser.getAction("by 7pm").getTask().getStartDateTime());
		assertEquals(LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 0)), parser.getAction("by 7pm").getTask().getEndDateTime());
	}

	//@author A0111887Y
	
	@Test
	public void testIndex() {
		assertEquals(1, parser.getAction("1").getTask().getIndex());
		assertEquals("1", parser.getAction("1").getTask().getContent());
		assertEquals(0, parser.getAction("-1").getTask().getIndex());
		assertEquals("-1", parser.getAction("-1").getTask().getContent());
		assertEquals(0, parser.getAction("0").getTask().getIndex());
		assertEquals("0", parser.getAction("0").getTask().getContent());
		assertEquals(0, parser.getAction("0").getTask().getIndex());
		assertEquals("1 task", parser.getAction("1 task").getTask().getContent());
	}

	//@author A0111887Y
	
	@Test
	public void testContent() {
		assertEquals("have lunch", parser.getAction("have lunch at noon").getTask().getContent());
		assertEquals("study maths topic 5", parser.getAction("study at 2pm maths topic 5 to 6pm").getTask().getContent());
	}

	//@author A0111887Y
	
	@Test
	public void testRemoveReminder() {
		assertEquals(null, parser.getAction("add [2 nov 3pm]").getTask().getStartDateTime());
		assertEquals(null, parser.getAction("add [2 nov 3pm]").getTask().getContent());
	}

}
