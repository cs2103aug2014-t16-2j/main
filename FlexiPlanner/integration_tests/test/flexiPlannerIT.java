package test;

import static org.junit.Assert.*;

/**
 * Integration testing for Logic, Parser and Storage Component
 * 
 * Test cases are in serial format tc1 till the last test case. 
 * Therefore, test cases may not work when testing individually. 
 * Breaking any test case will break the serial form resulting to 
 * incorrect test-results. 
 * 
 *  @author Moe Lwin Hein (A0117989H)
 * 
 */

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Logic.Logic;
import Storage.FileStorage;
import Storage.Storage;
import Storage.TaskData;

public class flexiPlannerIT {
	static Logic logic;
	static Storage storage;
	static ArrayList<TaskData> tasks;
	static ArrayList<TaskData> completedTasks;
	
	private final String ERROR = "Error. ";
	
	private final String INFO_SUCCESSFUL = "Successful";
	
	private static final String TASK_FILE = "text.json";
	private static final String COMPLETED_TASK_FILE = "completed.json";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logic = new Logic();
		storage = FileStorage.getInstance();
		storage.setupDatabase(TASK_FILE);
		storage.setupDatabase(COMPLETED_TASK_FILE);
		tasks = new ArrayList<TaskData>();
		completedTasks = new ArrayList<TaskData>();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		FileUtils.write(new File(TASK_FILE), "", false);
		FileUtils.write(new File(COMPLETED_TASK_FILE), "", false);
	}
	
	public boolean equals(ArrayList<TaskData> list1, ArrayList<TaskData> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			if (!list1.get(i).equals(list2.get(i))) {
				return false;
			}
		}
		return true;
	}

	@Before
	public void setUp() throws Exception {
	}
	/**
	 * test empty input
	 */
	@Test
	public void tc1() throws IOException, ParseException {
		String input = "";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test space input
	 */
	@Test
	public void tc2() throws IOException, ParseException {
		String input = " ";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test add with no content
	 */
	@Test
	public void tc3() throws IOException, ParseException {
		String input = "add";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test add a space
	 */
	@Test
	public void tc4() throws IOException, ParseException {
		String input = "add ";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
	}
	
	/**
	 * test add a floating task
	 */
	@Test
	public void tc5() throws IOException, ParseException {
		String input = "add a task";
		tasks.add(new TaskData("a task", null, null, null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task today
	 */
	@Test
	public void tc6() throws IOException, ParseException {
		String input = "add watch webcast today";
		tasks.add(new TaskData("watch webcast", null, null, LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task tomorrow 
	 */
	@Test
	public void tc7() throws IOException, ParseException {
		String input = "add watch webcast tomorrow";
		tasks.add(new TaskData("watch webcast", null, null, LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with date and time
	 */
	@Test
	public void tc8() throws IOException, ParseException {
		String input = "add project version 3 demo on 30/10 4pm to 5pm";
		tasks.add(new TaskData("project version 3 demo", null, null, LocalDateTime.of(2014, 10, 30, 16, 00), LocalDateTime.of(2014, 10, 30, 17, 0)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with date
	 */
	@Test
	public void atc9() throws IOException, ParseException {
		String input = "add project version 3 demo on 30 oct";
		tasks.add(new TaskData("project version 3 demo", null, null, LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 10, 30, 23, 59, 59)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test undo add
	 */
	@Test
	public void tc10() throws IOException, ParseException {
		String input = "undo";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test redo the undo
	 */
	@Test
	public void tc11() throws IOException, ParseException {
		String input = "redo";
		tasks.add(new TaskData("project version 3 demo", null, null, LocalDateTime.of(2014, 10, 30, 0, 0), LocalDateTime.of(2014, 10, 30, 23, 59, 59)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with priority
	 */
	@Test
	public void tc12() throws IOException, ParseException {
		String input = "add please prioritize this !";
		tasks.add(new TaskData("please prioritize this", null, "high", null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with higher priority
	 */
	@Test
	public void tc13() throws IOException, ParseException {
		String input = "add please prioritize the document today !!";
		tasks.add(new TaskData("please prioritize the document", null, "very high", LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task without "add" at the beginning
	 */
	@Test
	public void tc14() throws IOException, ParseException {
		String input = "watch harry potter";
		tasks.add(new TaskData("watch harry potter", null, null, null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add the same task with one of the tasks
	 */
	@Test
	public void tc15() throws IOException, ParseException {
		String input = "add watch webcast tomorrow";
		assertTrue(logic.executeInputCommand(input).equals(ERROR));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with date format dd/mm/yyyy
	 */
	@Test
	public void tc16() throws IOException, ParseException {
		String input = "add go shopping 02/11/2014";
		tasks.add(new TaskData("go shopping", null, null, LocalDateTime.of(2014, 11, 2, 0, 0), LocalDateTime.of(2014, 11, 2, 23, 59, 59)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test add a task with date format dd-mm-yyyy
	 */
	@Test
	public void tc17() throws IOException, ParseException {
		String input = "add meeting 02-11-2014";
		tasks.add(new TaskData("meeting", null, null, LocalDateTime.of(2014, 11, 2, 0, 0), LocalDateTime.of(2014, 11, 2, 23, 59, 59)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test schedule a task (Alias of add)
	 */
	@Test
	public void tc18() throws IOException, ParseException {
		String input = "schedule a meeting";
		tasks.add(new TaskData("a meeting", null, null, null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test create a task (Alias of add)
	 */
	@Test
	public void tc19() throws IOException, ParseException {
		String input = "create a birthday event the day after tomorrow";
		tasks.add(new TaskData("a birthday event", null, null, LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test remember to do a task (Alias of add)
	 */
	@Test
	public void tc20() throws IOException, ParseException {
		String input = "remember to buy balloons for birthday";
		tasks.add(new TaskData("to buy balloons for birthday", null, null, null, null));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test delete followed by index
	 */
	@Test
	public void tc21() throws IOException, ParseException {
		String input = "delete 1";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test delete followed by index
	 */
	@Test
	public void tc22() throws IOException, ParseException {
		String input = "delete 2";
		tasks.remove(tasks.size() - 2);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test delete followed by content
	 */
	@Test
	public void tc23() throws IOException, ParseException {
		String input = "delete a birthday event";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test undo delete
	 */
	@Test
	public void tc24() throws IOException, ParseException {
		String input = "undo";
		tasks.add(new TaskData("a birthday event", null, null, LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test redo the undo
	 */
	@Test
	public void tc25() throws IOException, ParseException {
		String input = "redo";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}

	/**
	 * test clear followed by index (Alias of delete)
	 */
	@Test
	public void tc26() throws IOException, ParseException {
		String input = "clear 1";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test remove followed by index (Alias of delete)
	 */
	@Test
	public void tc27() throws IOException, ParseException {
		String input = "remove 1";
		tasks.remove(tasks.size() - 1);
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test modify followed by content
	 */
	@Test
	public void tc28() throws IOException, ParseException {
		String input = "modify watch harry potter to tomorrow";
		tasks.remove(7);
		tasks.add(7, new TaskData("watch harry potter", null, null, LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test edit followed by content (Alias of modify)
	 */
	@Test
	public void tc29() throws IOException, ParseException {
		String input = "modify watch harry potter to the day after tomorrow";
		tasks.remove(7);
		tasks.add(7, new TaskData("watch harry potter", null, null, LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test change followed by content (Alias of modify)
	 */
	@Test
	public void tc30() throws IOException, ParseException {
		String input = "change watch harry potter to tomorrow";
		tasks.remove(7);
		tasks.add(7, new TaskData("watch harry potter", null, null, LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test reschedule followed by content (Alias of modify)
	 */
	@Test
	public void tc31() throws IOException, ParseException {
		String input = "reschedule watch harry potter to the day after tomorrow";
		tasks.remove(7);
		tasks.add(7, new TaskData("watch harry potter", null, null, LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test undo edit
	 */
	@Test
	public void tc32() throws IOException, ParseException {
		String input = "undo";
		tasks.remove(7);
		tasks.add(7, new TaskData("watch harry potter", null, null, LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test redo the undo
	 */
	@Test
	public void tc33() throws IOException, ParseException {
		String input = "redo";
		tasks.remove(7);
		tasks.add(7, new TaskData("watch harry potter", null, null, LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(0, 0)), LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(23, 59, 59))));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}
	
	/**
	 * test search tasks by dates
	 */
	@Test
	public void tc34() throws IOException, ParseException {
		for (int i = 1; i < 31; i++) {
			String input = "add meeting" + i + " on " + i + " November";
			tasks.add(new TaskData("meeting" + i, null, null, LocalDateTime.of(2014, 11, i, 0, 0), LocalDateTime.of(2014, 11, i, 23, 59, 59)));
			assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
			assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));	
		}
		String input = "search from 1 Nov to 30 Nov";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		
		input = "search 1/11/2014 15/11/2014";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
	}
	
	/**
	 * test search tasks by dates (dd/mm/yyyy)
	 */
	@Test
	public void tc35() throws IOException, ParseException {
		String input = "search 1/11/2014 15/11/2014";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
	}
	
	/**
	 * test display, find, show tasks (Alias of search)
	 */
	@Test
	public void tc36() throws IOException, ParseException {
		String input = "search today";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		
		input = "display today";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		
		input = "find today";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		
		input = "show today";
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
	}
	
	/**
	 * test mark content
	 */
	@Test
	public void tc37() throws IOException, ParseException {
		String input = "mark meeting30";
		completedTasks.add(tasks.remove(tasks.size() - 1));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
		assertTrue(equals(completedTasks, storage.loadTasks(COMPLETED_TASK_FILE)));
		
		input = "mark meeting29";
		completedTasks.add(tasks.remove(tasks.size() - 1));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
		assertTrue(equals(completedTasks, storage.loadTasks(COMPLETED_TASK_FILE)));
	}
	
	/**
	 * test undo mark
	 */
	@Test
	public void tc38() throws IOException, ParseException {
		String input = "undo";
		tasks.add(completedTasks.remove(completedTasks.size() - 1));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
		assertTrue(equals(completedTasks, storage.loadTasks(COMPLETED_TASK_FILE)));
	}
	
	/**
	 * test redo the undo
	 */
	@Test
	public void tc39() throws IOException, ParseException {
		String input = "redo";
		completedTasks.add(tasks.remove(tasks.size() - 1));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
		assertTrue(equals(completedTasks, storage.loadTasks(COMPLETED_TASK_FILE)));
	}
	
	/**
	 * test add category
	 */
	@Test
	public void tc40() throws IOException, ParseException {
		String input = "add cs2103 tutorial today 4pm to 5pm #school !!";
		tasks.add(new TaskData("cs2103 tutorial", "school", "very high", LocalDateTime.of(2014, 10, 30, 16, 0, 0), LocalDateTime.of(2014, 10, 30, 17, 0, 0)));
		assertTrue(logic.executeInputCommand(input).equals(INFO_SUCCESSFUL));
		assertTrue(equals(tasks, storage.loadTasks(TASK_FILE)));
	}			
}
